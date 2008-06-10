/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ApplicationDataModel.java,v 1.6 2008-06-10 09:16:05 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2008/05/20 08:45:51  bcolucci
 * Changed way to get packages informations.
 *
 * Revision 1.4  2008/05/19 14:45:30  lafrasse
 * Added default values.
 * Changed copyright text generation to reflect current year if not available from
 * XML file.
 *
 * Revision 1.3  2008/05/16 13:08:26  bcolucci
 * Removed unecessary try/catch, and added argument checks.
 * Changed logo.
 *
 * Revision 1.2  2008/04/24 15:57:55  mella
 * Grab application data from given url.
 * Remove not used try catch.
 *
 * Revision 1.1  2008/04/16 14:15:27  fgalland
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.gui.castor.*;

import java.io.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.*;
import java.util.Vector;
import java.util.logging.*;

import javax.swing.*;


/**
 * This class get informations from XML file
 * and returns values of field to the View Class
 */
public class ApplicationDataModel
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(ApplicationDataModel.class.getName());

    /** The JAVA class which castor has generated with the XSD file */
    private ApplicationData _applicationDataCastorModel = null;

    /** Logo file name */
    private String _logoFileName = "logo.png";

    /** Main web page URL */
    private String _mainWebPageURL = "http://www.jmmc.fr/";

    /** Constructor */
    public ApplicationDataModel(URL dataModelURL) throws Exception
    {
        _logger.fine("Loading Application data model from " + dataModelURL);

        InputStreamReader inputStreamReader;

        inputStreamReader               = new InputStreamReader(dataModelURL.openStream());
        _applicationDataCastorModel     = ApplicationData.unmarshal(inputStreamReader);

        _logger.fine("Application data model loaded.");
    }

    /**
     * Return the file name of JMMC logo
     *
     * @return the file name of JMMC logo
     */
    public String getLogoURL()
    {
        _logger.fine("logoUrl=" + _logoFileName);

        return _logoFileName;
    }

    /**
     * Return the main web page url
     *
     * @return the main web page url
     */
    public String getMainWebPageURL()
    {
        return _mainWebPageURL;
    }

    /**
     * Return the value of the "program name" from the XML file
     *
     * @return the value of the element program name from the XML file
     */
    public String getProgramName()
    {
        Program program     = null;
        String  programName = "Unknown";

        program             = _applicationDataCastorModel.getProgram();

        if (program != null)
        {
            programName = program.getName();
            _logger.fine("Program name has been taken on model");
        }

        return programName;
    }

    /**
     * Return the value of the element "program version" from the XML file
     *
     * @return the value of the element program version from the XML file
     */
    public String getProgramVersion()
    {
        Program program        = null;
        String  programVersion = "?.?";

        program                = _applicationDataCastorModel.getProgram();

        if (program != null)
        {
            programVersion = program.getVersion();
            _logger.fine("Program version has been taken on model");
        }

        return programVersion;
    }

    /**
     * Return the value of the field "link" from the XML file
     *
     * @return the value of the field link from the XML file
     */
    public String getLinkValue()
    {
        String mainWebPageURL = _mainWebPageURL;

        mainWebPageURL = _applicationDataCastorModel.getLink();
        _logger.fine("MainWebPageURL value has been taken on model");

        return mainWebPageURL;
    }

    /**
     * Return the value of the element "compilation date" from the XML file
     *
     * @return the value of the element compilation date from the XML file
     */
    public String getCompilationDate()
    {
        Compilation compilation     = null;
        String      compilationDate = "Unknown";

        compilation                 = _applicationDataCastorModel.getCompilation();

        if (compilation != null)
        {
            compilationDate = compilation.getDate();
            _logger.fine("Compilation date has been taken on model");
        }

        return compilationDate;
    }

    /**
     * Return the value of the element "compilator version" from the XML file
     *
     * @return the value of the element compilator version from the XML file
     */
    public String getCompilatorVersion()
    {
        Compilation compilation           = null;
        String      compilationCompilator = "Unknown";

        compilation                       = _applicationDataCastorModel.getCompilation();

        if (compilation != null)
        {
            compilationCompilator = compilation.getCompiler();
            _logger.fine("Compilation compilator has been taken on model");
        }

        return compilationCompilator;
    }

    /**
     * Return the value of the field "text" from the XML file
     *
     * @return the value of the field text from the XML file
     */
    public String getTextValue()
    {
        String text = "";

        text = _applicationDataCastorModel.getText();
        _logger.fine("Text value has been taken on model");

        return text;
    }

    /**
     * Return the informations about "packages" taken from the XML file
     *
     * @return vector template [name, link, description], [name, link, description]...
     */
    public Vector<String> getPackagesInfo()
    {
        Vector<String>                     packagesInfo = new Vector<String>();

        fr.jmmc.mcs.gui.castor.Dependences dependences  = _applicationDataCastorModel.getDependences();
        fr.jmmc.mcs.gui.castor.Package[]   packages     = dependences.get_package();

        // For each package
        for (int i = 0; i < packages.length; i++)
        {
            packagesInfo.add(packages[i].getName());
            packagesInfo.add(packages[i].getLink());
            packagesInfo.add(packages[i].getDescription());
        }

        _logger.fine("Packages informations have been taken and formated");

        return packagesInfo;
    }

    /**
     * Return the value of the field "copyright" from the XML file
     *
     * @return the value of the field copyright from the XML file
     */
    public String getCopyrightValue()
    {
        int    year            = 0;
        String compilationDate = getCompilationDate();

        try
        {
            // Try to get the year from the compilation date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date             date      = formatter.parse(compilationDate);
            Calendar         cal       = Calendar.getInstance();
            cal.setTime(date);
            year = cal.get(Calendar.YEAR);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot parse date '" + compilationDate +
                "' will use current year instead.", ex);

            // Otherwise use the current year
            Calendar cal = new GregorianCalendar();
            year = cal.get(Calendar.YEAR);
        }

        // return _applicationDataCastorModel.getCopyright();
        return "Copyright \u00A9 1999 - " + year + ", JMMC.";
    }

    /**
     * Returns the menu components from
     * ApplicationData.xml
     *
     * @return jcomponents of XML menu
     */
    public JComponent[] getMenusComponents()
    {
        String actionTypeName = "javax.swing.Action";

        // Take menus from ApplicationData.xml
        Menu[] castorMenus = _applicationDataCastorModel.getMenu();
        int    nbMenus     = castorMenus.length;

        _logger.fine("Get " + nbMenus + " menu(s) from XML");

        JComponent[] menuComponents = new JComponent[nbMenus];

        // For each menu taken
        for (int i = 0; i < nbMenus; i++)
        {
            Menu castorMenu = castorMenus[i];

            // Verify if the label is not null
            String menuLabel = castorMenu.getLabel();

            if (menuLabel != null)
            {
                // Create the SWING menu
                JMenu jMenu = new JMenu(menuLabel);
                jMenu.setName(menuLabel);
                _logger.fine("Menu " + menuLabel + " created");

                // Take menu's submenus
                Submenu[] subMenus   = castorMenu.getSubmenu();
                int       nbSubMenus = subMenus.length;

                for (int j = 0; j < nbSubMenus; j++)
                {
                    // Get classpath and action string value from XML
                    String subMenuClasspath = subMenus[j].getClasspath();
                    String subMenuAction    = subMenus[j].getAction();

                    /* Check if this two fields are not empty because
                       they are used together */
                    if ((subMenuClasspath != null) && (subMenuAction != null))
                    {
                        Class actionClass = null;

                        _logger.fine("Trying to configure (" +
                            subMenuClasspath + ", " + subMenuAction + ")");

                        try
                        {
                            // Try to get the class according to XML classpath
                            actionClass = Class.forName(subMenuClasspath);
                        }
                        catch (Exception ex)
                        {
                            _logger.info("Cannot find " + subMenuClasspath +
                                " class");
                        }

                        // If we find the class
                        if (actionClass != null)
                        {
                            /* Get class methods and class fields in order
                               to find the action link */
                            Method[] actionClassMethods    = actionClass.getMethods();
                            Field[]  actionClassAttributes = actionClass.getFields();

                            /* Action class method and
                               field (that we are looking for) */
                            Method actionClassMethod = null;
                            Field  actionClassField  = null;

                            // In order to check what we found
                            boolean findAMethod = false;
                            boolean findAField  = false;

                            // Search in the class methods
                            for (Method method : actionClassMethods)
                            {
                                String methodName = method.getName();

                                if (methodName.equals(subMenuAction))
                                {
                                    _logger.fine("Method " + methodName +
                                        " was found");
                                    actionClassMethod     = method;
                                    findAMethod           = true;
                                }
                            }

                            /* Search in the class fields only if we
                               didn't find a method */
                            if (! findAMethod)
                            {
                                for (Field field : actionClassAttributes)
                                {
                                    String fieldName = field.getName();

                                    if (fieldName.equals(subMenuAction))
                                    {
                                        _logger.fine("Field " + fieldName +
                                            " was found");
                                        actionClassField     = field;
                                        findAField           = true;
                                    }
                                }
                            }

                            /* If we find a method or a field corresponding
                               to the XML action name */
                            if (findAMethod && (actionClassMethod != null))
                            {
                                // Check if the method returns an Action
                                Class returnType = actionClassMethod.getReturnType();

                                if (returnType.getName().equals(actionTypeName))
                                {
                                    try
                                    {
                                        /* Get the value of the method
                                           invoked from the action class */
                                        Object returnedObject = actionClassMethod.invoke(actionClass.newInstance(),
                                                new Object[] {  });

                                        // Cast returned object
                                        Action    action    = (Action) returnedObject;

                                        JMenuItem jMenuItem = new JMenuItem(action);
                                        jMenuItem.setName(subMenuAction);
                                        _logger.fine("Create menu item '" +
                                            jMenuItem.getName() + "'");

                                        jMenu.add(jMenuItem);
                                        _logger.fine(
                                            "Action method linked to (" +
                                            subMenuClasspath + ", " +
                                            subMenuAction + ")");
                                    }
                                    catch (Exception ex)
                                    {
                                        _logger.log(Level.WARNING,
                                            "Cannot invoke " +
                                            actionClassMethod.getName(), ex);
                                    }
                                }
                            }
                            else if (findAField)
                            {
                                // Check if the return type is an action
                                Class returnType = actionClassField.getType();

                                if (returnType.getName().equals(actionTypeName))
                                {
                                    try
                                    {
                                        Object fieldValue = actionClassField.get(actionClass.newInstance());

                                        // Cast returned object
                                        Action    action    = (Action) fieldValue;

                                        JMenuItem jMenuItem = new JMenuItem(action);
                                        jMenuItem.setName(subMenuAction);
                                        _logger.fine("Create menu item '" +
                                            jMenuItem.getName() + "'");

                                        jMenu.add(jMenuItem);
                                        _logger.fine("Action field linked to (" +
                                            subMenuClasspath + ", " +
                                            subMenuAction + ")");
                                    }
                                    catch (Exception ex)
                                    {
                                        _logger.log(Level.WARNING,
                                            "Cannot get " +
                                            actionClassField.getName() +
                                            " value", ex);
                                    }
                                }
                            }
                            else
                            {
                                _logger.fine(
                                    "Cannot find a method/field according to " +
                                    subMenuAction);
                            }
                        }
                    }
                    else // It's a separator
                    {
                        JSeparator jSeparator = new JSeparator();
                        jSeparator.setName("separator");

                        jMenu.add(jSeparator);
                        _logger.fine("Create menu item '" +
                            jSeparator.getName() + "'");
                        _logger.fine(
                            "Put a separator in the menu components array");
                    }

                    // Add SWING menu to the tab
                    menuComponents[i] = jMenu;
                }
            }
        }

        return menuComponents;
    }
}
/*___oOo___*/
