/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Resources.java,v 1.1 2006-07-28 06:36:11 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package jmmc.mcs.util;

import jmmc.mcs.log.MCSLogger;

import org.w3c.dom.*;

import org.xml.sax.*;

import java.io.*;

import java.net.URL;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;

import javax.xml.parsers.*;


/**
 * Class used to get resources informations from one central point (xml file).
 * Applications must start to set the resource file name before
 * any gui construction.
 */
public abstract class Resources
{
    /** resource filename  that must be overloaded by subclasses */
    protected static String _resourceName = "jmmc/mcs/util/Resources";

    /** logger */
    private static Logger _logger = MCSLogger.getLogger();

    /** Properties */
    private static ResourceBundle _resources = null;

    /**
     * Indicates the property file where informations will be exctracted.
     * The property file must end with .properties filename extension. But the
     * given name should omit the extension.
     *
     * @param name Indicates property file to use.
     */
    public static void setResourceName(String name)
    {
        MCSLogger.trace();
        _logger.fine("Application will grab resources from '" + name + "'");
        _resourceName = name;
    }

    /**
     * Get content from resource file.
     *
     * @param resourceName name of resource
     *
     * @return the content of the resource or null indicating error
     */
    public static String getResource(String resourceName)
    {
        MCSLogger.trace();

        if (_resources == null)
        {
            try
            {
                _resources = java.util.ResourceBundle.getBundle(_resourceName);
            }
            catch (Exception e)
            {
                _logger.warning("Resource bundle can't be found :" +
                    e.getMessage());

                return null;
            }
        }

        _logger.fine("getResource for " + resourceName);

        try
        {
            return _resources.getString(resourceName);
        }
        catch (Exception e)
        {
            _logger.warning("Entry not found :" + e.getMessage());
        }

        return null;
    }

    /**
     * Get the text of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated text
     */
    public static String getActionText(String actionName)
    {
        return getResource("actions.action." + actionName + ".text");
    }

    /**
     * Get the description of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated description
     */
    public static String getActionDescription(String actionName)
    {
        return getResource("actions.action." + actionName + ".description");
    }

    /**
     * Get the icon path of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated icon path
     */
    public static String getActionIconPath(String actionName)
    {
        return getResource("actions.action." + actionName + ".icon");
    }

    /**
     * Get the icon  of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated icon
     */
    public static ImageIcon getActionIcon(String actionName)
    {
        String iconPath = getActionIconPath(actionName);

        ImageIcon imageIcon = createImageIcon(iconPath,
                "Icon of action '" + actionName + "'");

        return imageIcon;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path, String description)
    {
        if (path == null)
        {
            return null;
        }

        java.net.URL imgURL = Resources.class.getResource("./" + path);

        if (imgURL != null)
        {
            return new ImageIcon(imgURL, description);
        }
        else
        {
            _logger.warning("Couldn't find file: '" + path + "'");

            return null;
        }
    }

    /**
     * Get the tooltip text of widget related to the common widget group.
     *
     * @param widgetName the widgetInstanceName
     *
     * @return the tooltip text
     */
    public static String getToolTipText(String widgetName)
    {
        return getResource("widgets.widget." + widgetName + ".tooltip");
    }
}
/*___oOo___*/
