/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.25 2009-04-14 13:12:04 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.24  2008/10/17 10:41:54  lafrasse
 * Added FAQ handling.
 *
 * Revision 1.23  2008/10/16 14:19:34  mella
 * Use new help view handling
 *
 * Revision 1.22  2008/10/16 13:59:19  lafrasse
 * Re-ordered Help menu.
 *
 * Revision 1.21  2008/10/16 07:54:07  mella
 * Clean and improve createComponent method
 *
 * Revision 1.20  2008/10/15 13:49:54  mella
 * Add default release and acknowledgment menu items
 *
 * Revision 1.19  2008/09/22 16:51:42  lafrasse
 * Enforced Icon attribute retrieval.
 *
 * Revision 1.18  2008/09/22 16:16:29  lafrasse
 * Enforced 'Preferences.." menu creation even if no action is registered as the
 * 'Preference' one.
 *
 * Revision 1.17  2008/09/18 20:59:52  lafrasse
 * Added support of RegisteredPreferencedBooleanAction.
 *
 * Revision 1.16  2008/09/05 16:19:59  lafrasse
 * Added preference entry in edit menu while not running under Mac OS X.
 *
 * Revision 1.15  2008/09/04 16:02:12  lafrasse
 * Moved to new ActionRegistrar infrastructure.
 * Code, documentation and log enhancement.
 *
 * Revision 1.14  2008/06/23 07:47:32  bcolucci
 * Use SystemUtils class from apache common lang library in order
 * to know is we are running on a MAC OS X or not instead of
 * use os.name property.
 *
 * Revision 1.13  2008/06/20 08:41:45  bcolucci
 * Remove unused imports and add class comments.
 *
 * Revision 1.12  2008/06/19 14:35:07  bcolucci
 * If you are on MAC OS X and if there is not one menuitem at least for
 * "File", you don't create the "File" menu. On Windows, we always create
 * it because there is always "Exit" menuitem.
 *
 * Revision 1.11  2008/06/19 13:32:33  bcolucci
 * Fix : generate default menus even if there is no menubar element
 * in the ApplicationData.xml
 *
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

import fr.jmmc.mcs.util.*;

import java.net.MalformedURLException;
import java.util.logging.Level;
import org.apache.commons.lang.SystemUtils;

import java.awt.Component;

import java.lang.reflect.Method;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;


/**
 * This class which extends from JMenuBar, generates
 * all menus from the <b>ApplicationData.xml</b> file.
 *
 * In all cases, it generates default menus.
 *
 * To acces to the XML informations, this class uses
 * <b>ApplicationDataModel</b> class. It's a class which has got getters
 * in order to do that and which has been written to abstract the way
 * to acces to these informations.
 */
public class MainMenuBar extends JMenuBar
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.gui.MainMenuBar");

    /** Store wether we are running under Mac OS X or not */
    private boolean _isRunningUnderMacOSX = false;

    /** Table where are stocked the menus */
    private Hashtable<String, JMenu> _menusTable = null;

    /** Store a proxy to the shared ActionRegistrar facility */
    private ActionRegistrar _registrar = null;

    /**
     * Creates a new MainMenuBar object
     *
     * @param frame frame where link the menu
     */
    public MainMenuBar(JFrame frame)
    {
        // Get the host operating system type
        _isRunningUnderMacOSX     = SystemUtils.IS_OS_MAC_OSX;

        // Member initilization
        _menusTable               = new Hashtable<String, JMenu>();
        _registrar                = ActionRegistrar.getInstance();

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
                        _logger.fine("Make '" + currentMenuLabel + "' menu.");

                        // Keep it if it's an other menu
                        if ((currentMenuLabel.equals("File") == false) &&
                                (currentMenuLabel.equals("Edit") == false) &&
                                (currentMenuLabel.equals("Help") == false))
                        {
                            otherMenus.add(currentMenuLabel);
                            _logger.fine("Add '" + currentMenuLabel +
                                "' to other menus vector.");
                        }

                        // Get the component according to the castor menu object
                        JMenu completeMenu = (JMenu) recursiveParser(menu,
                                null, true);

                        // Put it in the menu table
                        _menusTable.put(currentMenuLabel, completeMenu);
                        _logger.fine("Put '" + completeMenu.getName() +
                            "' into the menus table.");
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
            _logger.fine("Add '" + menuLabel + "' menu into the menubar.");
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

        // There is one menu item at least
        boolean haveMenu = false;

        // Get file menu from table
        JMenu file = _menusTable.get("File");

        if (file != null)
        {
            Component[] components = file.getMenuComponents();

            if (components.length > 0)
            {
                haveMenu = true;

                // Add each component
                for (Component currentComponent : components)
                {
                    fileMenu.add(currentComponent);
                }

                if (_isRunningUnderMacOSX == false)
                {
                    fileMenu.add(new JSeparator());
                }
            }
        }

        if (_isRunningUnderMacOSX == false)
        {
            fileMenu.add(_registrar.getQuitAction());
            haveMenu = true;
        }

        // Add menu to menubar if there is a menuitem at least
        if (haveMenu)
        {
            add(fileMenu);
            _logger.fine("Add 'File' menu into the menubar.");
        }
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

        if (_isRunningUnderMacOSX == false)
        {
            Action preferenceAction = _registrar.getPreferenceAction();

            if (preferenceAction != null)
            {
                editMenu.add(new JSeparator());

                editMenu.add(preferenceAction);
            }
        }

        // Add menu to menubar
        add(editMenu);
        _logger.fine("Add 'Edit' menu into the menubar.");
    }

    /** Create help menu */
    private void createHelpMenu()
    {
        // Create menu
        JMenu helpMenu = new JMenu("Help");

        // Add helpview action
        helpMenu.add(App.showHelpAction());

        helpMenu.add(new JSeparator());

        // Add feedback action
        helpMenu.add(App.feedbackReportAction());

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

        helpMenu.add(new JSeparator());

        // Add acknowledgement action
        helpMenu.add(App.acknowledgementAction());

        // Add release action
        helpMenu.add(App.showReleaseAction());

        // Add release action
        helpMenu.add(App.showFaqAction());

        if (_isRunningUnderMacOSX == false)
        {
            helpMenu.add(new JSeparator());

            // Add aboutbox action
            helpMenu.add(App.aboutBoxAction());
        }

        // Add menu to menubar
        add(helpMenu);
        _logger.fine("Add 'Help' into the menubar.");
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
        _logger.fine("Component '" + me.getName() + "' created.");

        // Add it to the parent if there is one
        if (parent != null)
        {
            parent.add(me);
            _logger.fine("'" + me.getName() + "' linked to '" +
                parent.getName() + "'.");
        }

        // Get submenus
        fr.jmmc.mcs.gui.castor.Menu[] submenus = menu.getMenu();

        if (submenus != null)
        {
            for (fr.jmmc.mcs.gui.castor.Menu submenu : submenus)
            {
                // The submenu will be a jmenu?
                boolean isJMenu = ((submenu.getMenu()).length > 0);

                // Recursive call on submenu
                recursiveParser(submenu, me, isJMenu);
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
        boolean hasLabel     = (menu.getLabel() != null);
        boolean hasClasspath = (menu.getClasspath() != null);
        boolean hasAction    = (menu.getAction() != null);
        boolean isCheckbox   = (menu.getCheckbox() != null);

        // flag new component as separator or not
        boolean isSeparator = ! (isJMenu || hasClasspath || hasAction);

        // Is it a separator?
        if (isSeparator)
        {
            comp = new JSeparator();
            _logger.fine("Component is a separator.");
        }
        else
        {
            // Get action
            AbstractAction action = _registrar.get(menu.getClasspath(),
                    menu.getAction());

            // Set attributes
            setAttributes(menu, action);

            // If the (xml) menu seems to be a checkbox and a menu container
            // then only a checkbox will be created
            if (isCheckbox == true) // Is it a checkbox?
            {
                _logger.fine("Component is a JCheckBoxMenuItem.");
                comp = new JCheckBoxMenuItem(action);

                if (action instanceof RegisteredPreferencedBooleanAction)
                {
                    _logger.fine(
                        "Component is bound to a RegisteredPreferencedBooleanAction.");
                    ((RegisteredPreferencedBooleanAction) action).addBoundButton((JCheckBoxMenuItem) comp);
                }

                if (isJMenu == true)
                {
                    _logger.warning(
                        "The current menuitem is a checkbox AND a sub-menu, which is impossible !!!");
                }
            }
            else if (isJMenu == true) // What have we to create?
            {
                _logger.fine("Component is a JMenu.");
                comp = new JMenu(action);
            }
            else
            {
                _logger.fine("Component is a JMenuItem.");
                comp = new JMenuItem(action);
            }

            if (menu.getLabel() != null)
            {
                ((JMenuItem) comp).setText(menu.getLabel());
            }
        }

        return comp;
    }

    /**
     * Set menu attributes (all but the label)
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
            String icon = menu.getIcon();
            if (icon != null) {
                // Open XML file at path
                URL iconURL = getClass().getResource(icon);
                if (iconURL != null) {
                    action.putValue(Action.SMALL_ICON, new ImageIcon(Urls.fixJarURL(iconURL)));
                } else {
                    _logger.warning("Can't find iconUrl : " + icon);
                }
            }
            
            _logger.fine("Attributes set on '" + menu.getLabel() + "'.");
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
        if (_isRunningUnderMacOSX == true)
        {
            // Set the menu bar under Mac OS X
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            try
            {
                Class   osxAdapter     = this.getClass().getClassLoader()
                                             .loadClass("fr.jmmc.mcs.gui.OSXAdapter");

                Class[] defArgs        = { JFrame.class };
                Method  registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication",
                        defArgs);

                if (registerMethod != null)
                {
                    Object[] args = { (JFrame) frame };
                    registerMethod.invoke(osxAdapter, args);
                }

                // This is slightly gross.  to reflectively access methods with boolean args, 
                // use "boolean.class", then pass a Boolean object in as the arg, which apparently
                // gets converted for you by the reflection system.
                defArgs[0] = boolean.class;

                Method prefsEnableMethod = osxAdapter.getDeclaredMethod("enablePrefs",
                        defArgs);

                if (prefsEnableMethod != null)
                {
                    Object[] args = { Boolean.TRUE };
                    prefsEnableMethod.invoke(osxAdapter, args);
                }
            }
            catch (NoClassDefFoundError e)
            {
                // This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
                // because OSXAdapter extends ApplicationAdapter in its def
                System.err.println(
                    "This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" +
                    e + ").");
            }
            catch (ClassNotFoundException e)
            {
                // This shouldn't be reached; if there's a problem with the OSXAdapter we should get the 
                // above NoClassDefFoundError first.
                System.err.println(
                    "This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" +
                    e + ").");
            }
            catch (Exception e)
            {
                System.err.println("Exception while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Return prefix key for accelerator
     *
     * @return prefix key
     */
    private String getPrefixKey()
    {
        return (_isRunningUnderMacOSX == true) ? "meta " : "ctrl ";
    }
}
/*___oOo___*/
