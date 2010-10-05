/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.39 2010-10-05 07:40:45 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.38  2010/10/04 23:36:03  lafrasse
 * Added "Interop" menu handling.
 *
 * Revision 1.37  2010/09/25 12:17:10  bourgesl
 * restored imports (SAMP)
 *
 * Revision 1.36  2010/09/24 16:17:13  bourgesl
 * removed SampManager import to let the classloader open this class (JNLP)
 *
 * Revision 1.35  2010/09/24 16:05:51  bourgesl
 * removed imports for astrogrid to let the classloader open this class
 *
 * Revision 1.34  2010/09/24 12:05:15  lafrasse
 * Added preliminary support for the "Interop" menu (only in beta mode for the time being).
 *
 * Revision 1.33  2010/01/14 13:03:04  bourgesl
 * use Logger.isLoggable to avoid a lot of string.concat()
 *
 * Revision 1.32  2009/11/03 10:17:45  lafrasse
 * Code and documentation refinments.
 *
 * Revision 1.31  2009/11/02 16:28:51  lafrasse
 * Added support for RegisteredPreferencedBooleanAction in radio-button menu items.
 *
 * Revision 1.30  2009/11/02 15:03:32  lafrasse
 * Jalopization.
 *
 * Revision 1.29  2009/11/02 15:00:58  lafrasse
 * Added support for radio-button like menu items.
 *
 * Revision 1.28  2009/08/26 07:26:18  mella
 * removed unused imports
 *
 * Revision 1.27  2009/05/13 09:24:25  lafrasse
 * Added a generic "Hot News (RSS Feed)" Help menu item.
 *
 * Revision 1.26  2009/04/16 15:44:51  lafrasse
 * Jalopization.
 *
 * Revision 1.25  2009/04/14 13:12:04  mella
 * Fix code that didn't retrieve icon for non MCSAction
 *
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

import fr.jmmc.mcs.interop.SampCapabilityAction;
import fr.jmmc.mcs.interop.SampManager;
import fr.jmmc.mcs.util.ActionRegistrar;
import fr.jmmc.mcs.util.RegisteredPreferencedBooleanAction;
import fr.jmmc.mcs.util.Urls;

import org.apache.commons.lang.SystemUtils;

import java.awt.Component;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.gui.GuiHubConnector;


/**
 * This class which extends from JMenuBar, generates all menus from the
 * <b>ApplicationData.xml</b> file.
 *
 * In all cases, it generates default menus.
 *
 * To access to the XML informations, this class uses <b>ApplicationDataModel</b>
 * class. It's a class which has got getters in order to do that and which has
 * been written to abstract the way to access to these informations.
 */
public class MainMenuBar extends JMenuBar
{

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    /** Logger */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.gui.MainMenuBar");

    /** Store wether we are running under Mac OS X or not */
    private boolean _isRunningUnderMacOSX = false;

    /** Table where are stocked the menus */
    private Hashtable<String, JMenu> _menusTable = null;

    /** Store a proxy to the shared ActionRegistrar facility */
    private ActionRegistrar _registrar = null;

    /** Store a proxy to the parent frame */
    private JFrame _frame = null;

    /**
     * Instantiate all defaults menus, plus application-specific ones.
     *
     * @param frame the JFrame against which the menubar is linked.
     */
    public MainMenuBar(JFrame frame)
    {
        // Get the parent frame
        _frame = frame;

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

                        if (_logger.isLoggable(Level.FINE)) {
                          _logger.fine("Make '" + currentMenuLabel + "' menu.");
                        }

                        // Keep it if it's an other menu
                        if ((currentMenuLabel.equals("File") == false) &&
                                (currentMenuLabel.equals("Edit") == false) &&
                                (currentMenuLabel.equals("Interop") == false) &&
                                (currentMenuLabel.equals("Help") == false))
                        {
                            otherMenus.add(currentMenuLabel);

                            if (_logger.isLoggable(Level.FINE)) {
                              _logger.fine("Add '" + currentMenuLabel +
                                "' to other menus vector.");
                            }
                        }

                        // Get the component according to the castor menu object
                        JMenu completeMenu = (JMenu) recursiveParser(menu,
                                null, true, null); // It is a JMenu, has no button group

                        // Put it in the menu table
                        _menusTable.put(currentMenuLabel, completeMenu);

                        if (_logger.isLoggable(Level.FINE)) {
                          _logger.fine("Put '" + completeMenu.getName() +
                            "' into the menus table.");
                        }
                    }
                }
            }
        }

        createFileMenu();

        createEditMenu();

        // Create others (application-specifics) menus
        for (String menuLabel : otherMenus)
        {
            add(_menusTable.get(menuLabel));

            if (_logger.isLoggable(Level.FINE)) {
              _logger.fine("Add '" + menuLabel + "' menu into the menubar.");
            }
        }

        // Create Interop menu only for beta version (LAURENT) :
        if (App.isBetaVersion())
        {
          createInteropMenu();
        }

        createHelpMenu();

        // Use OSXAdapter on the frame
        macOSXRegistration(_frame);
    }

    /** Create the 'File' menu. */
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

    /** Create the 'Edit' menu. */
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

    private static int count = 0;

    /** Create the 'Interop' menu. */
    private void createInteropMenu()
    {
        // Create menu (invisible by default)
        JMenu interopMenu = new JMenu("Interop");
        interopMenu.setVisible(false);

        // Start SAMP support
        GuiHubConnector hub;
        try {
            hub = SampManager.getGuiHubConnector();
        } catch (SampException ex) {
            Logger.getLogger(MainMenuBar.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Add auto-toggeling menu entry to regiter/unregister to/from hub
        interopMenu.add(hub.createToggleRegisterAction());

        // To visually monitor hub activity
        interopMenu.add(hub.createShowMonitorAction());

        // Get interop menu from table
        JMenu interop = _menusTable.get("Interop");

        // Add app-specific menu entries (if any)
        if (interop != null)
        {
            Component[] components = interop.getMenuComponents();

            if (components.length > 0)
            {
                interopMenu.setVisible(true);

                interopMenu.add(new JSeparator());

                // Add each component
                for (Component currentComponent : components)
                {
                    // @TODO : cast SAMP-flagged menus only !
                    SampCapabilityAction action = (SampCapabilityAction)((JMenuItem)currentComponent).getAction();
                    String title = ((JMenuItem)currentComponent).getText();

                    JMenu menu = new JMenu(title);
                    menu.setEnabled(false);

                    SampManager.addMenu(menu, action);
                    interopMenu.add(menu);
                }
            }
        }

        // Add menu to menubar
        add(interopMenu);

        // Keep this menu invisible until (at least) one capability is registered
        SampManager.hookMenu(interopMenu);
        _logger.fine("Add 'Interop' into the menubar.");
    }


    /** Create the 'Help' menu. */
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

        helpMenu.add(new JSeparator());

        // Add hot news action
        helpMenu.add(App.showHotNewsAction());

        // Add release action
        helpMenu.add(App.showReleaseAction());

        // Add Faq action
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
     * Recursively instantiate each application-specific menu element.
     *
     * @param menu castor Menu object to instantiate.
     * @param parent parent component, null for the root element.
     * @param createMenu create a JMenu if true, specific menu items otherwise.
     * @param group a ButtonGroup in which radio-buttons should be added, null
     * otherwise.
     *
     * @return the instantiated JComponent according to the XML menu hierarchy.
     */
    private JComponent recursiveParser(fr.jmmc.mcs.gui.castor.Menu menu,
        JComponent parent, boolean createMenu, ButtonGroup buttonGroup)
    {
        // Create the current component
        JComponent component = createComponent(menu, createMenu, buttonGroup);

        // Add it to the parent if any
        if (parent != null)
        {
            parent.add(component);

            if (_logger.isLoggable(Level.FINE)) {
              _logger.fine("'" + component.getName() + "' linked to '" +
                parent.getName() + "'.");
            }
        }

        // Get submenus
        fr.jmmc.mcs.gui.castor.Menu[] submenus = menu.getMenu();
        ButtonGroup                   group    = null;

        if (submenus != null)
        {
            if (menu.getRadiogroup() != null)
            {
                group = new ButtonGroup();
            }

            for (fr.jmmc.mcs.gui.castor.Menu submenu : submenus)
            {
                // The submenu will be a jmenu?
                boolean isMenu = ((submenu.getMenu()).length > 0);

                // Recursive call on submenu
                recursiveParser(submenu, component, isMenu, group);
            }
        }

        // Return the hightest component
        return component;
    }

    /**
     * Create the component according to the castor Menu object.
     *
     * @param menu castor Menu object to instantiate.
     * @param isMenu create a JMenu if true, specific menu items otherwise.
     * @param buttonGroup used to only have a single menu item selected at any
     * time, if not null.
     *
     * @return the instantiated JComponent according to the XML description.
     */
    private JComponent createComponent(fr.jmmc.mcs.gui.castor.Menu menu,
        boolean isMenu, ButtonGroup buttonGroup)
    {
        // Component to create
        JMenuItem item = null;

        // Attributes
        boolean hasLabel     = (menu.getLabel() != null);
        boolean hasClasspath = (menu.getClasspath() != null);
        boolean hasAction    = (menu.getAction() != null);
        boolean isCheckbox   = (menu.getCheckbox() != null);

        // flag new component as separator or not
        boolean isSeparator = ! (isMenu || hasClasspath || hasAction);

        // Is it a separator?
        if (isSeparator)
        {
            _logger.fine("Component is a separator.");

            return new JSeparator();
        }

        // Get action
        AbstractAction action = _registrar.get(menu.getClasspath(),
                menu.getAction());

        // Set attributes
        setAttributes(menu, action);

        // Is it a checkbox ?
        if (isCheckbox == true)
        {
            _logger.fine("Component is a JCheckBoxMenuItem.");
            item = new JCheckBoxMenuItem(action);

            if (action instanceof RegisteredPreferencedBooleanAction)
            {
                _logger.fine(
                    "Component is bound to a RegisteredPreferencedBooleanAction.");
                ((RegisteredPreferencedBooleanAction) action).addBoundButton((JCheckBoxMenuItem) item);
            }

            if (isMenu == true)
            {
                _logger.warning(
                    "The current menuitem is a checkbox AND a sub-menu, which is impossible !!!");

                return null;
            }
        }
        else if (buttonGroup != null) // Is it a radio-button ?
        {
            _logger.fine("Component is a JRadioButtonMenuItem.");
            item = new JRadioButtonMenuItem(action);

            // Put the radiobutton menu item in a the ButtonGroup to only have a single one selected at any time.
            buttonGroup.add((JRadioButtonMenuItem) item);

            if (action instanceof RegisteredPreferencedBooleanAction)
            {
                _logger.fine(
                    "Component is bound to a RegisteredPreferencedBooleanAction.");
                ((RegisteredPreferencedBooleanAction) action).addBoundButton((JRadioButtonMenuItem) item);
            }

            if (isMenu == true)
            {
                _logger.warning(
                    "The current menuitem is a radiobutton AND a sub-menu, which is impossible !!!");

                return null;
            }
        }
        else if (isMenu == true) // is it a menu containig other menu item ?
        {
            _logger.fine("Component is a JMenu.");
            item = new JMenu(action);
        }
        else // It is a menu item.
        {
            _logger.fine("Component is a JMenuItem.");
            item = new JMenuItem(action);
        }

        // If the menu object has its own name
        if (menu.getLabel() != null)
        {
            // Superseed the name of the item associated action
            item.setText(menu.getLabel());
        }

        return item;
    }

    /**
     * Set menu attributes (all but the label).
     *
     * @param menu castor Menu object to get data from.
     * @param action Action instance to modify.
     */
    private void setAttributes(fr.jmmc.mcs.gui.castor.Menu menu, Action action)
    {
        if ((menu == null) || (action == null))
        {
            return;
        }

        // Set action accelerator
        String accelerator = menu.getAccelerator();

        if (accelerator != null)
        {
            String keyStrokeString = getPrefixKey() + accelerator;
            action.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(keyStrokeString));
        }

        // Set action tooltip
        String xmlTooltip    = menu.getDescription();
        String actionTooltip = (String) action.getValue(Action.SHORT_DESCRIPTION);

        if ((actionTooltip == null) && (xmlTooltip != null))
        {
            action.putValue(Action.SHORT_DESCRIPTION, xmlTooltip);
        }

        // Set action icon
        String icon = menu.getIcon();

        if (icon != null)
        {
            // Open XML file at path
            URL iconURL = getClass().getResource(icon);

            if (iconURL != null)
            {
                action.putValue(Action.SMALL_ICON,
                    new ImageIcon(Urls.fixJarURL(iconURL)));
            }
            else
            {
                if (_logger.isLoggable(Level.WARNING)) {
                    _logger.warning("Can't find iconUrl : " + icon);
                }
            }
        }

        if (_logger.isLoggable(Level.FINE)) {
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
