/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.11 2008-06-19 13:32:33 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2008/06/19 13:11:47  bcolucci
 * Modify the way to generate the menubar. Use
 * circular references into XSD schema.
 *
 * Revision 1.9  2008/06/17 12:36:04  bcolucci
 * Merge fix about "null-pointer exceptions in case no menu is defined in the XML file".
 *
 * Revision 1.8  2008/06/17 11:59:43  lafrasse
 * Hnadled 2 null-pointer exceptions in case no menu is defined in the XML file.
 *
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
import javax.swing.AbstractButton;
import javax.swing.text.DefaultEditorKit;


/**
 * Create a menubar from ApplicationData.xml
 * and make the default menus
 */
public class MainMenuBar extends JMenuBar
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(MainMenuBar.class.getName());

    /** Store whether the execution platform is a Mac or not */
    private static boolean MAC_OS_X = (System.getProperty("os.name")
                                             .toLowerCase()
                                             .startsWith("mac os x"));

    /** Table where are stocked the menus */
    private Hashtable<String, JMenu> _menusTable = new Hashtable<String, JMenu>();

    /**
     * Creates a new MainMenuBar object
     *
     * @param frame frame where link the menu
     */
    public MainMenuBar(JFrame frame)
    {
        // Get the application data model
        ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();

        // Contains the name of the others menus
        Vector<String>       otherMenus           = new Vector<String>();

        // If it's null, we exit
        if (applicationDataModel != null)
        {
            // Get the menubar element from XML
            fr.jmmc.mcs.gui.castor.Menubar menuBar = applicationDataModel.getMenubar();

            // If it's null, we exit
            if (menuBar != null)
            {
                // Get the menu elements from menubar
                fr.jmmc.mcs.gui.castor.Menu[] menus = menuBar.getMenu();

                // If it's null, we exit
                if (menus != null)
                {
                    for (fr.jmmc.mcs.gui.castor.Menu menu : menus)
                    {
                        // Get menu label
                        String currentMenuLabel = menu.getLabel();
                        _logger.fine("Make " + currentMenuLabel + " menu");

                        // Keep it if it's an other menu
                        if (! currentMenuLabel.equals("File") &&
                                ! currentMenuLabel.equals("Edit") &&
                                ! currentMenuLabel.equals("Help"))
                        {
                            otherMenus.add(currentMenuLabel);
                            _logger.fine("Add " + currentMenuLabel +
                                " to other menus vector");
                        }

                        // Get the component according to the castor menu object
                        JMenu completeMenu = (JMenu) recursiveParser(menu,
                                null, true);

                        // Put it in the menu table
                        _menusTable.put(currentMenuLabel, completeMenu);
                        _logger.fine("Put " + completeMenu.getName() +
                            " into the menus table");
                    }
                }
            }
        }

        // Create file menu
        createFileMenu();

        // Create edit menu
        createEditMenu();

        // Create others menus
        for (String menuLabel : otherMenus)
        {
            add(_menusTable.get(menuLabel));
            _logger.fine("Add " + menuLabel + " into the menubar");
        }

        // Create help menu
        createHelpMenu();

        // Use OSXAdapter on the frame
        macOSXRegistration(frame);
    }

    /** Create file menu */
    private void createFileMenu()
    {
        // Create menu
        JMenu fileMenu = new JMenu("File");

        // Get file menu from table
        JMenu file = _menusTable.get("File");

        if (file != null)
        {
            Component[] components = file.getMenuComponents();

            if (components.length > 0)
            {
                // Add each component
                for (Component currentComponent : components)
                {
                    fileMenu.add(currentComponent);
                }

                if (! MAC_OS_X)
                {
                    fileMenu.add(new JSeparator());
                }
            }
        }

        if (! MAC_OS_X)
        {
            fileMenu.add(App.exitAction());
        }

        // Add menu to menubar
        add(fileMenu);
        _logger.fine("Add file into the menubar");
    }

    /** Create edit menu */
    private void createEditMenu()
    {
        // Create menu
        JMenu editMenu = new JMenu("Edit");

        // Add cut action
        Action cutAction = new DefaultEditorKit.CutAction();
        cutAction.putValue(Action.NAME, "Cut");
        cutAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(getPrefixKey() + "X"));
        editMenu.add(cutAction);

        // Add copy action
        Action copyAction = new DefaultEditorKit.CopyAction();
        copyAction.putValue(Action.NAME, "Copy");
        copyAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(getPrefixKey() + "C"));
        editMenu.add(copyAction);

        // Add paste action
        Action pasteAction = new DefaultEditorKit.PasteAction();
        pasteAction.putValue(Action.NAME, "Paste");
        pasteAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(getPrefixKey() + "V"));
        editMenu.add(pasteAction);

        // Get edit menu from table
        JMenu edit = _menusTable.get("Edit");

        if (edit != null)
        {
            Component[] components = edit.getMenuComponents();

            if (components.length > 0)
            {
                editMenu.add(new JSeparator());

                // Add each component
                for (Component currentComponent : components)
                {
                    editMenu.add(currentComponent);
                }
            }
        }

        // Add menu to menubar
        add(editMenu);
        _logger.fine("Add edit into the menubar");
    }

    /** Create help menu */
    private void createHelpMenu()
    {
        // Create menu
        JMenu helpMenu = new JMenu("Help");

        // Add feedback action
        helpMenu.add(App.feedbackReportAction());

        // Add helpview action
        helpMenu.add(App.helpViewAction());

        // Get help menu from table
        JMenu help = _menusTable.get("Help");

        if (help != null)
        {
            Component[] components = help.getMenuComponents();

            if (components.length > 0)
            {
                helpMenu.add(new JSeparator());

                // Add each component
                for (Component currentComponent : components)
                {
                    helpMenu.add(currentComponent);
                }
            }
        }

        if (! MAC_OS_X)
        {
            helpMenu.add(new JSeparator());

            // Add aboutbox action
            helpMenu.add(App.aboutBoxAction());
        }

        // Add menu to menubar
        add(helpMenu);
        _logger.fine("Add help into the menubar");
    }

    /**
     * Return the hightest component according to the castor
     * menu object. We cast it to JMenu.
     *
     * @param menu castor menu
     * @param parent parent component
     * @param createJMenu if true, we will create a JMenu
     * @param group a group
     */
    private JComponent recursiveParser(fr.jmmc.mcs.gui.castor.Menu menu,
        JComponent parent, boolean createJMenu)
    {
        // Create the current component
        JComponent me = createComponent(menu, createJMenu);
        _logger.fine("Component " + me.getName() + " created");

        // Add it to the parent if there is one
        if (parent != null)
        {
            parent.add(me);
            _logger.fine(me.getName() + " linked to " + parent.getName());
        }

        // Get submenus
        fr.jmmc.mcs.gui.castor.Menu[] submenus = menu.getMenu();

        if (submenus != null)
        {
            for (fr.jmmc.mcs.gui.castor.Menu submenu : submenus)
            {
                // The submenu will be a jmenu?
                boolean willBeJMenu = ((submenu.getMenu()).length > 0);

                // Recursive call on submenu
                recursiveParser(submenu, me, willBeJMenu);
            }
        }

        // Return the hightest component
        return me;
    }

    /**
     * Create the component according to the
     * castor menu object
     *
     * @param menu castor menu
     * @param isJMenu if true, will create a JMenu
     *
     * @return component according to the castor menu
     */
    private JComponent createComponent(fr.jmmc.mcs.gui.castor.Menu menu,
        boolean isJMenu)
    {
        // Component to create
        JComponent comp = null;

        // Attributes
        String  label      = (menu.getLabel() != null) ? menu.getLabel() : "NONE";
        String  className  = (menu.getClasspath() != null)
            ? menu.getClasspath() : "NONE";
        String  actionName = (menu.getAction() != null) ? menu.getAction()
                                                        : "NONE";
        boolean isCheckbox = (menu.getCheckbox() != null);

        // Check that we cannot have a checkbox and a radio in the same time etc..
        boolean notPossible = (isCheckbox && isJMenu);

        // Is it a separator?
        if (label.equals("NONE") || notPossible)
        {
            comp = new JSeparator();
            _logger.fine("Component is a separator");
        }
        else
        {
            // Get action
            Action action = null;

            if (Introspection.isMethodExists(className, actionName))
            {
                action = (Action) Introspection.getMethodValue(className,
                        actionName);
            }

            // Set attributes
            setAttributes(menu, action);

            if (isCheckbox) // Is it a checkbox?
            {
                comp = new JCheckBoxMenuItem(action);
                ((JCheckBoxMenuItem) comp).setLabel(label);
                _logger.fine("Component is a JCheckBoxMenuItem");
            }
            else if (isJMenu) // What have we to create?
            {
                comp = new JMenu(action);
                ((JMenu) comp).setLabel(label);
                _logger.fine("Component is a JMenu");
            }
            else
            {
                comp = new JMenuItem(action);
                ((JMenuItem) comp).setLabel(label);
                _logger.fine("Component is a JMenuItem");
            }
        }

        // Set component name
        comp.setName(label);

        return comp;
    }

    /**
     * Set menu attributes
     *
     * @param menu castor menu
     * @param action action to modify
     */
    private void setAttributes(fr.jmmc.mcs.gui.castor.Menu menu, Action action)
    {
        if ((menu != null) && (action != null))
        {
            // Set accelerator
            String accelerator = (menu.getAccelerator() != null)
                ? menu.getAccelerator() : null;

            if (accelerator != null)
            {
                String keyStroke = getPrefixKey() + accelerator;
                action.putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(keyStroke));
            }

            // Set tooltip
            String description   = (menu.getDescription() != null)
                ? menu.getDescription() : "";

            String actionTooltip = (String) action.getValue(Action.SHORT_DESCRIPTION);

            if ((actionTooltip == null) && (! description.equals("")))
            {
                action.putValue(Action.SHORT_DESCRIPTION, description);
            }

            // Set icon
            String icon       = (menu.getIcon() != null) ? menu.getIcon() : "";

            String actionIcon = (String) action.getValue(Action.SMALL_ICON);

            if (actionIcon == null)
            {
                action.putValue(Action.SMALL_ICON, new ImageIcon(icon));
            }

            _logger.fine("Attributes set on " + menu.getLabel());
        }
    }

    /**
     * Generic registration with the Mac OS X application menu.
     *
     * Checks the platform, then attempts.
     */
    public void macOSXRegistration(JFrame frame)
    {
        // If running under Mac OS X
        if (MAC_OS_X)
        {
            // Execute registerMacOSXApplication method
            Introspection.executeMethod("fr.jmmc.mcs.gui.OSXAdapter",
                "registerMacOSXApplication", new Object[] { frame });
        }
    }

    /**
     * Return prefix key for accelerator
     *
     * @return prefix key
     */
    private String getPrefixKey()
    {
        return (MAC_OS_X) ? "meta " : "ctrl ";
    }
}
/*___oOo___*/
