/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.8 2008-06-17 11:59:43 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2008/06/17 11:16:04  bcolucci
 * Fix some comments.
 *
 * Revision 1.6  2008/06/13 08:16:10  bcolucci
 * Add possibility to specify icon and tooltip and keep action properties.
 * Improve menus generation and use OSXAdapter.
 *
 * Revision 1.5  2008/06/12 12:34:52  bcolucci
 * Fix the order of menu items thanks to a vector which keep
 * the order from XML file.
 * Begin to implement OSXAdapter.
 *
 * Revision 1.4  2008/06/12 11:34:25  bcolucci
 * Extend the class from JMenuBar and remove static context.
 *
 * Revision 1.3  2008/06/12 09:31:53  bcolucci
 * Truly added support for complete menubar creation from XML file (last commit was about first introspection version).
 *
 * Revision 1.2  2008/06/12 07:40:54  bcolucci
 * Create functions to generate File, Edit, [Other] and Help menus with
 * some variations when we are running the application on a MAC OS X.
 *
 * Revision 1.1  2008/06/10 12:22:37  bcolucci
 * Created.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;


/** This class is used to build menubar
 * according to the OS
 */
public class MainMenuBar extends JMenuBar
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc");

    /** Store whether the execution platform is a Mac or not */
    private static boolean MAC_OS_X = (System.getProperty("os.name")
                                             .toLowerCase()
                                             .startsWith("mac os x"));

    /** Other menu keys */
    private Vector<String> _otherMenuKeys = new Vector<String>();

    /** JMenus */
    private Hashtable<String, Vector<JComponent>> _jMenus = null;

    /** Application frame */
    private JFrame _jFrame = null;

    /**
     *      HASTABLE
     *      Key =>      |File|Edit |...|Help|
     *      Vector=>    |Save|Cut  |
     *                  |Exit|Copy |
     *                   ----|Paste|
     *                        -----
     *
     * A key references a vector of JComponent
     * Ex : File => [Save, Exit]
     *
     * In this way, we can manipulate JMenuItems
     */

    /** Set the JMenuBar */
    public MainMenuBar(JFrame jFrame)
    {
        // Set JFrame
        _jFrame     = jFrame;

        // Instantiate hashtable of menus
        _jMenus     = new Hashtable<String, Vector<JComponent>>();

        // Get application data model
        ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();

        // Check if the application data model is not null
        if (applicationDataModel == null)
        {
            _logger.warning("Cannot get shared application data model");
        }
        else
        {
            // Get menus from ApplicationData.xml
            fr.jmmc.mcs.gui.castor.Menubar menuBar = applicationDataModel.getMenubar();
            if (menuBar != null)
            {
                fr.jmmc.mcs.gui.castor.Menu[]  menus   = menuBar.getMenu();
                if (menus != null)
                {
                    // For each menu
                    for (fr.jmmc.mcs.gui.castor.Menu menu : menus)
                    {
                        // Get label
                        String menuLabel = menu.getLabel();
                        
                        /* In order to sort other menu items later, we have to
                         keep the order from XML file. So we will access to the
                         hashtable with the key in the good order */
                        if (! menuLabel.equals("File") && ! menuLabel.equals("Edit") &&
                            ! menuLabel.equals("Help"))
                        {
                            _otherMenuKeys.add(menuLabel);
                        }
                        
                        // Get menu items from menu
                        Vector<JComponent> currentMenuItems = getMenuItems(menu);
                        
                        // Put the menu with it's menu items
                        _jMenus.put(menuLabel, currentMenuItems);
                    }
                }
            }

            // Create file menu
            createFileMenu();

            // Create edit menu
            createEditMenu();

            // Create other menus
            createOthersMenu();

            // Create help menu
            createHelpMenu();

            // Use OSXAdapter
            macOSXRegistration();
        }
    }

    /** Create file menu */
    private void createFileMenu()
    {
        // Create file menu
        JMenu fileMenu = new JMenu("File");
        _logger.fine("Create file menu");

        // Add file menu items from XML
        Vector<JComponent> fileVector = _jMenus.get("File");

        // Check if there is file menu in XML
        if (fileVector != null)
        {
            // There are items?
            if (fileVector.size() > 0)
            {
                // Add all components
                for (JComponent jComp : fileVector)
                {
                    // Add component to the menu
                    fileMenu.add(jComp);
                    _logger.fine("Add " + jComp);
                }

                // If not running under Mac OS X
                if (MAC_OS_X == false)
                {
                    // Add a mandatory separator
                    fileMenu.add(new JSeparator());
                    _logger.fine("Add mandatory separator");
                }
            }
        }

        // If not running under Mac OS X
        if (MAC_OS_X == false)
        {
            // Add exit menu /!\ Mac OS X
            fileMenu.add(App.exitAction());
            _logger.fine("Add exit action");
        }

        // Add file menu to the menubar
        add(fileMenu);
    }

    /** Create edit menu */
    private void createEditMenu()
    {
        // Create edit menu
        JMenu editMenu = new JMenu("Edit");
        _logger.fine("Create edit menu");

        // The following 3 actions come from the default editor kit.
        // The 'control' key is used on Linux and Windows
        // If the execution is on Mac OS X
        String keyStringPrefix = "ctrl ";

        // If running under Mac OS X
        if (MAC_OS_X == true)
        {
            // The 'command' key (aka Apple key) is used
            keyStringPrefix = "meta ";
        }

        // Cut menu item
        Action cutAction = new DefaultEditorKit.CutAction();
        cutAction.putValue(Action.NAME, "Cut");
        cutAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keyStringPrefix + "X"));
        editMenu.add(cutAction);

        // Copy menu item
        Action copyAction = new DefaultEditorKit.CopyAction();
        copyAction.putValue(Action.NAME, "Copy");
        copyAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keyStringPrefix + "C"));
        editMenu.add(copyAction);

        // Paste menu item
        Action pasteAction = new DefaultEditorKit.PasteAction();
        pasteAction.putValue(Action.NAME, "Paste");
        pasteAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(keyStringPrefix + "V"));
        editMenu.add(pasteAction);

        // Add edit menu items from XML
        Vector<JComponent> editVector = _jMenus.get("Edit");

        // Check if there is edit menu in XML
        if (editVector != null)
        {
            // There are items?
            if (editVector.size() > 0)
            {
                // Add a mandatory separator
                editMenu.add(new JSeparator());
                _logger.fine("Add mandatory separator");

                // Add all components
                for (JComponent jComp : editVector)
                {
                    // Add component to the menu
                    editMenu.add(jComp);
                    _logger.fine("Add " + jComp);
                }
            }
        }

        // Add edit menu to the menubar
        add(editMenu);
    }

    /** Create other menus */
    private void createOthersMenu()
    {
        // Create and add other menus
        // We have the good order thanks to _otherMenuKeys vector
        for (String key : _otherMenuKeys)
        {
            // Create the key menu
            JMenu              menu   = new JMenu(key);

            // Add current key menu items from XML
            Vector<JComponent> vector = _jMenus.get(key);

            // Check if there is key menu in XML
            if (vector != null)
            {
                // There are items?
                if (vector.size() > 0)
                {
                    // Add all components
                    for (JComponent jComp : vector)
                    {
                        // Add component to the menu
                        menu.add(jComp);
                        _logger.fine("Add " + jComp);
                    }
                }
            }

            // Add menu to the menubar
            add(menu);
        }
    }

    /** Create help menu */
    private void createHelpMenu()
    {
        // Create edit menu
        JMenu helpMenu = new JMenu("Help");
        _logger.fine("Create help menu");

        // Feedback menu item
        helpMenu.add(App.feedbackReportAction());

        // Help menu item
        helpMenu.add(App.helpViewAction());

        // Add help menu items from XML
        Vector<JComponent> helpVector = _jMenus.get("Help");

        // Check if there is help menu in XML
        if (helpVector != null)
        {
            // There are items?
            if (helpVector.size() > 0)
            {
                // Add a mandatory separator
                helpMenu.add(new JSeparator());
                _logger.fine("Add mandatory separator");

                // Add all components
                for (JComponent jComp : helpVector)
                {
                    // Add component to the menu
                    helpMenu.add(jComp);
                    _logger.fine("Add " + jComp);
                }
            }
        }

        // If not running under Mac OS X
        if (MAC_OS_X == false)
        {
            // Add a mandatory separator
            helpMenu.add(new JSeparator());
            _logger.fine("Add mandatory separator");

            // About menu item
            helpMenu.add(App.aboutBoxAction());
        }

        // Add help menu to the menubar
        add(helpMenu);
    }

    /**
     * Return the components vector according
     * to the menu given by castor
     *
     * @param menu castor menu
     *
     * @return components vector
     */
    private Vector<JComponent> getMenuItems(fr.jmmc.mcs.gui.castor.Menu menu)
    {
        // Create components vetor of current menu
        Vector<JComponent> currentMenuItems = new Vector<JComponent>();

        // Get menu items of current menu
        fr.jmmc.mcs.gui.castor.Menuitem[] menuItems = menu.getMenuitem();

        // For each menu item
        for (fr.jmmc.mcs.gui.castor.Menuitem menuItem : menuItems)
        {
            // Get label
            String menuLabel = menuItem.getLabel();

            // Get class name where we can find action
            String className = menuItem.getClasspath();

            // Get method name which returns the action
            String actionName = menuItem.getAction();

            // Get accelerator
            String accelerator = menuItem.getAccelerator();

            // Get description
            String description = menuItem.getDescription();

            // Get icon
            String icon = menuItem.getIcon();
            icon = (icon == null) ? "" : icon;

            // The 'control' key is used on Linux and Windows
            // If the execution is on Mac OS X
            String keyStringPrefix = "ctrl ";

            // If running under Mac OS X
            if (MAC_OS_X == true)
            {
                // The 'command' key (aka Apple key) is used
                keyStringPrefix = "meta ";
            }

            // If there is no label, it's a separator
            if (menuLabel == null)
            {
                currentMenuItems.add(new JSeparator());
            }
            else
            {
                // If the method exists in the class
                if (Introspection.isMethodExists(className, actionName))
                {
                    // Get value of the method
                    Object value = Introspection.getMethodValue(className,
                            actionName);

                    // The menu item is a checkbox?
                    if (menuItem.getCheckbox() == null)
                    {
                        // Create the action
                        Action action = (Action) value;

                        // Put the accelerator
                        if (accelerator != null)
                        {
                            // Create the keystroke and link it
                            String keyStroke = keyStringPrefix + accelerator;
                            action.putValue(Action.ACCELERATOR_KEY,
                                KeyStroke.getKeyStroke(keyStroke));
                        }

                        // Get tooltip action
                        String actionTooltip = (String) action.getValue(Action.SHORT_DESCRIPTION);

                        // Check if we have to set the tooltip
                        if (actionTooltip == null)
                        {
                            // Set and link action tooltip
                            String tooltip = (description != null)
                                ? description : null;
                            action.putValue(Action.SHORT_DESCRIPTION, tooltip);
                        }

                        // Get action icon
                        String actionIcon = (String) action.getValue(Action.SMALL_ICON);

                        // Check if we have to set the icon
                        if (actionIcon == null)
                        {
                            // Set action icon
                            action.putValue(Action.SMALL_ICON,
                                new ImageIcon(icon));
                        }

                        // Create the component with the action
                        JMenuItem jComp = new JMenuItem(action);

                        // set the label
                        jComp.setLabel(menuLabel);

                        // Put the component into the vector
                        currentMenuItems.add(jComp);
                    }
                    else
                    {
                        // Create the action
                        Action action = (Action) value;

                        // Put the accelerator
                        if (accelerator != null)
                        {
                            // Create the keystroke and link it
                            String keyStroke = keyStringPrefix + accelerator;
                            action.putValue(Action.ACCELERATOR_KEY,
                                KeyStroke.getKeyStroke(keyStroke));
                        }

                        // Get action tooltip
                        String actionTooltip = (String) action.getValue(Action.SHORT_DESCRIPTION);

                        // Check if we have to set the tooltip
                        if (actionTooltip == null)
                        {
                            // Set and link action tooltip
                            String tooltip = (description != null)
                                ? description : null;
                            action.putValue(Action.SHORT_DESCRIPTION, tooltip);
                        }

                        // Get action icon
                        String actionIcon = (String) action.getValue(Action.SMALL_ICON);

                        // Check if we have to set the icon
                        if (actionIcon == null)
                        {
                            // Set action icon
                            action.putValue(Action.SMALL_ICON,
                                new ImageIcon(icon));
                        }

                        // Create the component with the action
                        JCheckBoxMenuItem jComp = new JCheckBoxMenuItem(action);

                        // set the label
                        jComp.setLabel(menuLabel);

                        // Put the component into the vector
                        currentMenuItems.add(jComp);
                    }
                }
                else
                {
                    currentMenuItems.add(new JMenuItem(menuLabel +
                            " [no action]"));
                }
            }
        }

        // Return the components vector
        return currentMenuItems;
    }

    /**
     * Generic registration with the Mac OS X application menu.
     *
     * Checks the platform, then attempts.
     */
    public void macOSXRegistration()
    {
        // If running under Mac OS X
        if (MAC_OS_X == true)
        {
            // Execute registerMacOSXApplication method
            Introspection.executeMethod("fr.jmmc.mcs.gui.OSXAdapter",
                "registerMacOSXApplication", new Object[] { _jFrame });
        }
    }
}
/*___oOo___*/
