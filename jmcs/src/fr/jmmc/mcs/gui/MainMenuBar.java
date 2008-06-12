/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: MainMenuBar.java,v 1.2 2008-06-12 07:40:54 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/06/10 12:22:37  bcolucci
 * Created.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.lang.reflect.Method;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
   /!\ Introspective searches /!\
   ------------------------------
   The used class name is "MainMenubar.java"
 */

/** Generate menubar from classes */
public class MainMenuBar
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(MainMenuBar.class.getName());

    /** Action delimiter */
    private static final String ACTION_DELIM = "Action";

    /** Menu delimiter */
    private static final String MENU_DELIM = "Menu";

    /** REGEX Patern for valid methods */
    private static final String ACTIONS_REGEX = ".*" + ACTION_DELIM;

    /** Default class for menu "File" */
    private String _defaultFileMenu = null;

    /** Default class for menu "Help" */
    private String _defaultHelpMenu = null;

    /** Menubar generated */
    private JMenuBar _menuBar = new JMenuBar();

    /** MenuNode vector */
    private Vector<MenuNode> _menus = new Vector<MenuNode>();

    /** MethodNode vector */
    private Vector<MethodNode> _classToParseMethods = new Vector<MethodNode>();

    /** Menus generated */
    private Vector<JMenu> _jMenus = new Vector<JMenu>();

    /** Menus' name already generated */
    private Vector<String> _menusNameAdded = new Vector<String>();

    /** Frame to link to the menubar */
    private JFrame _jFrame = null;

    /** Class scanned for actions */
    private Vector<String> _classNames = new Vector<String>();

    /** We have to add default menus? */
    private boolean _insertDefaultMenus = true;

    /**
     * Constructor
     *
     * @param jFrame frame to link to the menubar generated
     */
    public MainMenuBar(JFrame jFrame)
    {
        _jFrame = jFrame;
    }

    /**
     * Insert menus corresponding to actions
     * found in the class passed in argument.
     *
     * @param className class' name
     */
    public void insertActions(String className)
    {
        Vector<String> tmpClassNames = new Vector<String>();
        _jMenus.removeAllElements();

        tmpClassNames.add(className);

        for (String previousClassName : _classNames)
        {
            tmpClassNames.add(previousClassName);
        }

        Object[] classToParseNames       = tmpClassNames.toArray();
        int      nbClassNames            = classToParseNames.length;

        String[] classToParseStringNames = new String[nbClassNames];

        for (int i = 0; i < nbClassNames; i++)
        {
            classToParseStringNames[i] = (String) classToParseNames[i];
        }

        insertActions(_insertDefaultMenus, classToParseStringNames);
    }

    /**
     * Insert menus corresponding to actions
     * found in classes passed in argument and
     * default menus too.
     *
     * @param classToParseNames DOCUMENT ME!
     */
    public void insertActions(String[] classToParseNames)
    {
        insertActions(_insertDefaultMenus, classToParseNames);
    }

    /**
     * DOCUMENT ME!
     *
     * @param location DOCUMENT ME!
     */
    public void setDefaultFileMenuLocation(String location)
    {
        _defaultFileMenu = location;
    }

    /**
     * DOCUMENT ME!
     *
     * @param location DOCUMENT ME!
     */
    public void setDefaultHelpMenuLocation(String location)
    {
        _defaultHelpMenu = location;
    }

    /**
     * Insert menus corresponding to actions
     * found in classes passed in argument.
     *
     * @param insertDefaultMenus if true, insert default menus
     * @param classToParseNames class' names
     */
    public void insertActions(boolean insertDefaultMenus,
        String[] classToParseNames)
    {
        _insertDefaultMenus = insertDefaultMenus;

        if (_insertDefaultMenus)
        {
            _classNames.add(_defaultFileMenu);
        }

        for (String className : classToParseNames)
        {
            _classNames.add(className);
        }

        if (_insertDefaultMenus)
        {
            _classNames.add(_defaultHelpMenu);
        }

        int nbClassNames = _classNames.size();

        for (int i = 0; i < nbClassNames; i++)
        {
            String className = _classNames.get(i);
            parseMethods(getClassFromClassName(className));
        }

        makeMenuBar();
        _jFrame.setJMenuBar(_menuBar);
    }

    /**
     * Return the class corresponding to the
     * name passed in argument.
     *
     * @param className class' name
     *
     * @return class corresponding to the name
     */
    private Class getClassFromClassName(String className)
    {
        Class searchedClass = null;

        try
        {
            searchedClass = Class.forName(className);
        }
        catch (ClassNotFoundException ex)
        {
            _logger.log(Level.SEVERE, "Cannot find " + className + " class", ex);
        }

        System.out.println("I find the class : " + searchedClass.getName());

        return searchedClass;
    }

    /**
     * Parse each method of a class
     * and keep only the methods corresponding
     * to the REGEX Patern.
     *
     * @param classToParse class to parse
     */
    private void parseMethods(Class classToParse)
    {
        Method[] classToParseMethods = classToParse.getMethods();

        for (Method method : classToParseMethods)
        {
            getSplittedMethodNameIfValid(classToParse, method);
        }
    }

    /**
     * Check if the method of a class corresponding
     * to the REGEX Patern.
     *
     * @param classToParse class to parse
     * @param method method to check
     */
    private void getSplittedMethodNameIfValid(Class classToParse, Method method)
    {
        String methodName   = method.getName();
        String menuName     = null;
        String menuItemName = null;

        if (methodName.matches(ACTIONS_REGEX))
        {
            String[] splittedMenu = methodName.split(MENU_DELIM);

            if (splittedMenu.length == 2)
            {
                menuName = checkMaj(splittedMenu[0]);

                String[] splittedAction = splittedMenu[1].split(ACTION_DELIM);

                if (splittedAction.length == 1)
                {
                    menuItemName = splittedAction[0];
                }
            }

            MethodNode methodNode = new MethodNode(classToParse, method);
            _classToParseMethods.add(methodNode);

            insertMenu(menuName, menuItemName);
        }
    }

    /**
     * If the method corresponding to the REGEX Pattern, we verify and
     * put the first letter to uppercase if it's not already done.
     *
     * @param string string to check
     *
     * @return string with first letter in uppercase
     */
    private String checkMaj(String string)
    {
        char[]  stringArray = string.toCharArray();
        char    firstChar   = stringArray[0];
        boolean isMaj       = ((firstChar >= 'A') && (firstChar <= 'Z'));
        int     majMask     = (isMaj ? 0 : 32);
        stringArray[0]      = (char) (firstChar - majMask);

        return (new String(stringArray));
    }

    /**
     * DOCUMENT ME!
     *
     * @param menu DOCUMENT ME!
     */
    private void setMethodAction(MenuNode menu)
    {
        for (MethodNode methodNode : _classToParseMethods)
        {
            String methodName       = methodNode.getMethod().getName();
            String menuMethodName   = menu.getMethodName();
            String methodNameLC     = methodName.toLowerCase();
            String menuMethodNameLC = menuMethodName.toLowerCase();

            if (methodNameLC.equals(menuMethodNameLC))
            {
                try
                {
                    Method methodToAdd = methodNode.getClassEntity()
                                                   .getMethod(methodName,
                            new Class[] {  });

                    if (isMethodReturnTypeValid(methodToAdd.getReturnType()))
                    {
                        Object value = methodToAdd.invoke(methodNode.getClassEntity()
                                                                    .newInstance(),
                                new Object[] {  });

                        menu.setAction((Action) value);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger(MainMenuBar.class.getName())
                          .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param returnType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isMethodReturnTypeValid(Class<?> returnType)
    {
        return (returnType.equals(Action.class) ? true : false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param menuName DOCUMENT ME!
     * @param menuItemName DOCUMENT ME!
     */
    private void insertMenu(String menuName, String menuItemName)
    {
        if (! isMenuExists(menuName))
        {
            MenuNode menu = new MenuNode(menuName, menuItemName);
            setMethodAction(menu);
            _menus.add(menu);
        }
        else
        {
            insertMenuItem(menuName, menuItemName);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param menuName DOCUMENT ME!
     * @param menuItemName DOCUMENT ME!
     */
    private void insertMenuItem(String menuName, String menuItemName)
    {
        if (! isMenuItemExists(menuName, menuItemName))
        {
            MenuNode menu = new MenuNode(menuName, menuItemName);
            setMethodAction(menu);

            _menus.add(menu);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param menuName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isMenuExists(String menuName)
    {
        for (MenuNode menu : _menus)
        {
            if (menu.getMenuName().equals(menuName))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param menuName DOCUMENT ME!
     * @param menuItemName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isMenuItemExists(String menuName, String menuItemName)
    {
        for (MenuNode menu : _menus)
        {
            if (menu.getMenuName().equals(menuName) &&
                    menu.getMenuItemName().equals(menuItemName))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     */
    private void makeMenuBar()
    {
        for (MenuNode menu : _menus)
        {
            String menuName = menu.getMenuName();

            if (isMenuNameAdded(menuName, _menusNameAdded))
            {
                for (JMenu jMenu : _jMenus)
                {
                    String jMenuName = jMenu.getText();

                    if (jMenuName.equals(menuName))
                    {
                        JMenuItem jMenuItem = new JMenuItem(menu.getAction());
                        jMenu.add(jMenuItem);
                    }
                }
            }
            else
            {
                JMenu     jMenu     = new JMenu(menuName);

                JMenuItem jMenuItem = new JMenuItem(menu.getAction());
                jMenu.add(jMenuItem);

                _jMenus.add(jMenu);
                _menusNameAdded.add(menuName);
            }
        }

        for (JMenu jMenu : _jMenus)
        {
            _menuBar.add(jMenu);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param menuName DOCUMENT ME!
     * @param menusName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isMenuNameAdded(String menuName, Vector<String> menusName)
    {
        for (String menuNameSearched : menusName)
        {
            if (menuName.equals(menuNameSearched))
            {
                return true;
            }
        }

        return false;
    }

    public class MenuNode
    {
        private String _menuName     = null;
        private String _menuItemName = null;
        private Action _action       = null;

        public MenuNode(String menuName, String menuItemName)
        {
            _menuName         = menuName;
            _menuItemName     = menuItemName;
        }

        public String getMenuName()
        {
            return _menuName;
        }

        public String getMenuItemName()
        {
            return _menuItemName;
        }

        public Action getAction()
        {
            return _action;
        }

        public void setAction(Action action)
        {
            _action = action;
        }

        public String getMethodName()
        {
            return (_menuName + MENU_DELIM + _menuItemName + ACTION_DELIM);
        }
    }

    public class MethodNode
    {
        private Class  _classEntity = null;
        private Method _method      = null;

        public MethodNode(Class classEntity, Method method)
        {
            _classEntity     = classEntity;
            _method          = method;
        }

        public Class getClassEntity()
        {
            return _classEntity;
        }

        public Method getMethod()
        {
            return _method;
        }
    }
}
/*___oOo___*/
