/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.model.Menu;
import fr.jmmc.jmcs.data.model.Menubar;
import fr.jmmc.jmcs.util.Introspection;
import fr.jmmc.jmcs.network.interop.SampCapabilityAction;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.RegisteredPreferencedBooleanAction;
import fr.jmmc.jmcs.util.Urls;

import org.apache.commons.lang.SystemUtils;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.net.URL;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultEditorKit;

/**
 * This class which extends from JMenuBar, generates all menus from the
 * <b>ApplicationData.xml</b> file.
 *
 * In all cases, it generates default menus.
 *
 * To access to the XML informations, this class uses <b>ApplicationDataModel</b>
 * class. It's a class which has got getters in order to do that and which has
 * been written to abstract the way to access to these informations.
 * 
 * @author Brice COLUCCI, Sylvain LAFRASSE, Guillaume MELLA, Laurent BOURGES.
 */
public class MainMenuBar extends JMenuBar {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /**
     * This System property controls the Look & Feel menu (useful for debugging purposes).
     * To show this menu, add "-Djmcs.laf.menu=true" to your JVM options.
     */
    public final static String SYSTEM_PROPERTY_LAF_MENU = "jmcs.laf.menu";
    /** Logger */
    private static final Logger _logger = Logger.getLogger(MainMenuBar.class.getName());
    /** Store whether we are running under Mac OS X or not */
    private final boolean _isRunningUnderMacOSX = SystemUtils.IS_OS_MAC_OSX;
    /** Table where are stocked the menus */
    private final Hashtable<String, JMenu> _menusTable;
    /** Store a proxy to the shared ActionRegistrar facility */
    private final ActionRegistrar _registrar;
    /** Proxy to the application data model */
    private ApplicationDataModel _applicationDataModel = null;

    /**
     * Instantiate all defaults menus, plus application-specific ones.
     */
    public MainMenuBar() {
        // Member initialization
        _menusTable = new Hashtable<String, JMenu>();
        _registrar = ActionRegistrar.getInstance();

        // Get the application data model
        _applicationDataModel = App.getSharedApplicationDataModel();

        // Contains the name of the others menus
        Vector<String> otherMenus = new Vector<String>();

        // If it's null, we exit
        if (_applicationDataModel != null) {
            // Get the menubar element from XML
            Menubar menuBar = _applicationDataModel.getMenubar();

            // If it's null, we exit
            if (menuBar != null) {
                // Get the menu elements from menubar
                final List<Menu> menus = menuBar.getMenus();

                // If it's null, we exit
                if (menus != null) {
                    for (Menu menu : menus) {
                        // Get menu label
                        String currentMenuLabel = menu.getLabel();

                        if (_logger.isLoggable(Level.FINE)) {
                            _logger.fine("Make '" + currentMenuLabel + "' menu.");
                        }

                        // Keep it if it's an other menu
                        if (!currentMenuLabel.equals("File")
                                && !currentMenuLabel.equals("Edit")
                                && !currentMenuLabel.equals("Interop")
                                && !currentMenuLabel.equals("Help")) {

                            otherMenus.add(currentMenuLabel);

                            if (_logger.isLoggable(Level.FINE)) {
                                _logger.fine("Add '" + currentMenuLabel + "' to other menus vector.");
                            }
                        }

                        // Get the component according to the castor menu object
                        JMenu completeMenu = (JMenu) recursiveParser(menu, null, true, null); // It is a JMenu, has no button group

                        // Put it in the menu table
                        _menusTable.put(currentMenuLabel, completeMenu);

                        if (_logger.isLoggable(Level.FINE)) {
                            _logger.fine("Put '" + completeMenu.getName() + "' into the menus table.");
                        }
                    }
                }
            }
        }

        createFileMenu();

        createEditMenu();

        // Create others (application-specifics) menus
        for (String menuLabel : otherMenus) {
            add(_menusTable.get(menuLabel));

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("Add '" + menuLabel + "' menu into the menubar.");
            }
        }

        // Create Interop menu :
        createInteropMenu();

        final String lafMenu = System.getProperty(SYSTEM_PROPERTY_LAF_MENU);
        if (lafMenu != null && "true".equals(lafMenu)) {
            createLAFMenu();
        }

        createHelpMenu();
    }

    /** Create the 'File' menu. */
    private void createFileMenu() {
        // Create menu
        JMenu fileMenu = new JMenu("File");

        // There is one menu item at least
        boolean haveMenu = false;

        // Get file menu from table
        JMenu file = _menusTable.get("File");

        if (file != null) {
            Component[] components = file.getMenuComponents();

            if (components.length > 0) {
                haveMenu = true;

                // Add each component
                for (Component currentComponent : components) {
                    fileMenu.add(currentComponent);
                }

                if (!_isRunningUnderMacOSX) {
                    fileMenu.add(new JSeparator());
                }
            }
        }

        if (!_isRunningUnderMacOSX) {
            fileMenu.add(_registrar.getQuitAction());
            haveMenu = true;
        }

        // Add menu to menubar if there is a menuitem at least
        if (haveMenu) {
            add(fileMenu);
            _logger.fine("Add 'File' menu into the menubar.");
        }
    }

    /** Create the 'Edit' menu. */
    private void createEditMenu() {
        // Create menu
        JMenu editMenu = new JMenu("Edit");

        // Add cut action
        Action cutAction = new DefaultEditorKit.CutAction();
        cutAction.putValue(Action.NAME, "Cut");
        cutAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(getSystemCommandKey() + "X"));
        editMenu.add(cutAction);

        // Add copy action
        Action copyAction = new DefaultEditorKit.CopyAction();
        copyAction.putValue(Action.NAME, "Copy");
        copyAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(getSystemCommandKey() + "C"));
        editMenu.add(copyAction);

        // Add paste action
        Action pasteAction = new DefaultEditorKit.PasteAction();
        pasteAction.putValue(Action.NAME, "Paste");
        pasteAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(getSystemCommandKey() + "V"));
        editMenu.add(pasteAction);

        // Get edit menu from table
        JMenu edit = _menusTable.get("Edit");

        if (edit != null) {
            Component[] components = edit.getMenuComponents();

            if (components.length > 0) {
                editMenu.add(new JSeparator());

                // Add each component
                for (Component currentComponent : components) {
                    editMenu.add(currentComponent);
                }
            }
        }

        if (!_isRunningUnderMacOSX) {
            Action preferenceAction = _registrar.getPreferenceAction();

            if (preferenceAction != null) {
                editMenu.add(new JSeparator());

                editMenu.add(preferenceAction);
            }
        }

        // Add menu to menubar
        add(editMenu);
        _logger.fine("Add 'Edit' menu into the menubar.");
    }

    /** Create the 'Interop' menu. */
    private void createInteropMenu() {
        // Create menu
        final JMenu interopMenu = new JMenu("Interop");

        // Add auto-toggling menu entry to regiter/unregister to/from hub
        interopMenu.add(SampManager.createToggleRegisterAction());

        // To visually monitor hub activity
        interopMenu.add(SampManager.createShowMonitorAction());

        // Get interop menu from table
        JMenu interop = _menusTable.get("Interop");

        // Add app-specific menu entries (if any)
        if (interop != null) {
            Component[] components = interop.getMenuComponents();

            if (components.length > 0) {
                interopMenu.add(new JSeparator());

                Action action;
                // Add each component
                for (Component currentComponent : components) {
                    // Get menuitem initialised from ApplicationData
                    JMenuItem menuItem = (JMenuItem) currentComponent;

                    action = menuItem.getAction();

                    if (action instanceof SampCapabilityAction) {
                        // @TODO : cast SAMP-flagged menus only !
                        SampCapabilityAction sampAction = (SampCapabilityAction) action;
                        // get previously created menu by samp action
                        JMenu menu = SampManager.getMenu(sampAction);

                        if (menu != null) {
                            // set text coming from applicationData.xml
                            menu.setText(menuItem.getText());

                            interopMenu.add(menu);
                        }
                    }
                }
            }
        }

        // Add menu to menubar
        add(interopMenu);

        // Keep this menu invisible until (at least) one capability is registered
        SampManager.hookMenu(interopMenu);
        _logger.fine("Add 'Interop' into the menubar.");
    }

    /** Create the 'Look & Feel' menu. */
    private void createLAFMenu() {
        // Create menu
        final JMenu lafMenu = new JMenu("Look & Feel");

        final ActionListener lafActionListener = new ActionListener() {

            /**
             * Invoked when an action occurs.
             */
            @Override
            public void actionPerformed(final ActionEvent ae) {
                final String className = ae.getActionCommand();
                final String currentClassName = UIManager.getLookAndFeel().getClass().getName();

                if (!className.equals(currentClassName)) {
                    try {
                        if (_logger.isLoggable(Level.INFO)) {
                            _logger.info("use Look and Feel : " + className);
                        }

                        final LookAndFeel newLaf = (LookAndFeel) Introspection.getInstance(className);

                        UIManager.setLookAndFeel(newLaf);

                        final Frame mainFrame = App.getFrame();

                        SwingUtilities.updateComponentTreeUI(mainFrame);
                        mainFrame.pack();

                    } catch (UnsupportedLookAndFeelException ulafe) {
                        throw new RuntimeException("Change LAF failed : " + className, ulafe);
                    }
                }
            }
        };

        JMenuItem menuItem;

        for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {

            menuItem = new JMenuItem(lookAndFeelInfo.getName());
            menuItem.setActionCommand(lookAndFeelInfo.getClassName());
            menuItem.addActionListener(lafActionListener);

            lafMenu.add(menuItem);
        }

        add(lafMenu);
    }

    /** Create the 'Help' menu. */
    private void createHelpMenu() {
        // Create menu
        JMenu helpMenu = new JMenu("Help");

        // Add helpview action
        helpMenu.add(App.showHelpAction());

        // Add feedback action (if supported)
        if (_applicationDataModel.getFeedbackReportFormURL() != null) {
            helpMenu.add(new JSeparator());
            helpMenu.add(App.feedbackReportAction());
        }
        // Get help menu from table
        JMenu help = _menusTable.get("Help");

        if (help != null) {
            Component[] components = help.getMenuComponents();

            if (components.length > 0) {
                helpMenu.add(new JSeparator());

                // Add each component
                for (Component currentComponent : components) {
                    helpMenu.add(currentComponent);
                }
            }
        }

        helpMenu.add(new JSeparator());

        // Add acknowledgement action
        helpMenu.add(App.acknowledgmentAction());

        helpMenu.add(new JSeparator());

        boolean shouldAddSeparator = false;

        // Add hot news action
        if (_applicationDataModel.getHotNewsRSSFeedLinkValue() != null) {
            helpMenu.add(App.showHotNewsAction());
            shouldAddSeparator = true;
        }

        // Add release action
        if (_applicationDataModel.getReleaseNotesLinkValue() != null) {
            helpMenu.add(App.showReleaseAction());
            shouldAddSeparator = true;
        }

        // Add FAQ action
        if (_applicationDataModel.getFaqLinkValue() != null) {
            helpMenu.add(App.showFaqAction());
            shouldAddSeparator = true;
        }

        if (shouldAddSeparator) {
            helpMenu.add(new JSeparator());
        }

        if (!_isRunningUnderMacOSX) {
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
     * @param buttonGroup a ButtonGroup in which radio-buttons should be added, null
     * otherwise.
     *
     * @return the instantiated JComponent according to the XML menu hierarchy.
     */
    private JComponent recursiveParser(Menu menu,
            JComponent parent, boolean createMenu, ButtonGroup buttonGroup) {
        // Create the current component
        JComponent component = createComponent(menu, createMenu, buttonGroup);

        // Add it to the parent if any
        if (parent != null) {
            parent.add(component);

            if (_logger.isLoggable(Level.FINE)) {
                _logger.fine("'" + component.getName() + "' linked to '"
                        + parent.getName() + "'.");
            }
        }

        // Get submenus
        final List<Menu> submenus = menu.getMenus();
        ButtonGroup group = null;

        if (submenus != null) {
            if (menu.getRadiogroup() != null) {
                group = new ButtonGroup();
            }

            for (Menu submenu : submenus) {
                // The submenu will be a jmenu?
                boolean isMenu = !submenu.getMenus().isEmpty();

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
    private JComponent createComponent(Menu menu,
            boolean isMenu, ButtonGroup buttonGroup) {
        // Component to create
        JMenuItem item = null;

        // Attributes
        boolean hasClasspath = (menu.getClasspath() != null);
        boolean hasAction = (menu.getAction() != null);
        boolean isCheckbox = (menu.getCheckbox() != null);

        // flag new component as separator or not
        boolean isSeparator = !(isMenu || hasClasspath || hasAction);

        // Is it a separator?
        if (isSeparator) {
            _logger.fine("Component is a separator.");

            return new JSeparator();
        }

        // Get action
        AbstractAction action = null;

        if (hasClasspath && hasAction) {
            action = _registrar.get(menu.getClasspath(), menu.getAction());

            if (action == null) {
                // Open a feeback report if an action is not found:
                throw new IllegalStateException("Action [" + menu.getClasspath() + "," + menu.getAction() + "] not found !");
            }
        }

        // Set attributes
        setAttributes(menu, action);

        // Is it a checkbox ?
        if (isCheckbox) {
            _logger.fine("Component is a JCheckBoxMenuItem.");
            item = new JCheckBoxMenuItem(action);

            if (action instanceof RegisteredPreferencedBooleanAction) {
                _logger.fine("Component is bound to a RegisteredPreferencedBooleanAction.");

                ((RegisteredPreferencedBooleanAction) action).addBoundButton((JCheckBoxMenuItem) item);
            }

            if (isMenu) {
                _logger.warning("The current menuitem is a checkbox AND a sub-menu, which is impossible !!!");

                return null;
            }
        } else if (buttonGroup != null) // Is it a radio-button ?
        {
            _logger.fine("Component is a JRadioButtonMenuItem.");
            item = new JRadioButtonMenuItem(action);

            // Put the radiobutton menu item in a the ButtonGroup to only have a single one selected at any time.
            buttonGroup.add((JRadioButtonMenuItem) item);

            if (action instanceof RegisteredPreferencedBooleanAction) {
                _logger.fine("Component is bound to a RegisteredPreferencedBooleanAction.");
                ((RegisteredPreferencedBooleanAction) action).addBoundButton((JRadioButtonMenuItem) item);
            }

            if (isMenu) {
                _logger.warning("The current menuitem is a radiobutton AND a sub-menu, which is impossible !!!");

                return null;
            }
        } else if (isMenu) // is it a menu containig other menu item ?
        {
            _logger.fine("Component is a JMenu.");
            item = new JMenu(action);
        } else // It is a menu item.
        {
            _logger.fine("Component is a JMenuItem.");
            item = new JMenuItem(action);
        }

        // If the menu object has its own name
        if (menu.getLabel() != null) {
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
    private void setAttributes(Menu menu, Action action) {
        if ((menu == null) || (action == null)) {
            return;
        }

        // Set action accelerator
        String accelerator = menu.getAccelerator();

        if (accelerator != null) {
            String keyStrokeString = getSystemCommandKey() + accelerator;
            action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyStrokeString));
        }

        // Set action tooltip
        String xmlTooltip = menu.getDescription();
        String actionTooltip = (String) action.getValue(Action.SHORT_DESCRIPTION);

        if ((actionTooltip == null) && (xmlTooltip != null)) {
            action.putValue(Action.SHORT_DESCRIPTION, xmlTooltip);
        }

        // Set action icon
        String icon = menu.getIcon();

        if (icon != null) {
            // Open XML file at path
            URL iconURL = getClass().getResource(icon);

            if (iconURL != null) {
                action.putValue(Action.SMALL_ICON, new ImageIcon(Urls.fixJarURL(iconURL)));
            } else {
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
     * Return prefix key for accelerator
     *
     * @return prefix key
     */
    public static String getSystemCommandKey() {
        return (SystemUtils.IS_OS_MAC_OSX) ? "meta " : "ctrl ";
    }
}
/*___oOo___*/
