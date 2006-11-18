/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Resources.java,v 1.4 2006-11-18 22:58:03 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/10/16 14:29:49  lafrasse
 * Updated to reflect MCSLogger API changes.
 *
 * Revision 1.2  2006/08/03 14:47:24  lafrasse
 * Jalopyzation
 *
 * Revision 1.1  2006/07/28 06:36:11  mella
 * First revision
 *
 *
 ******************************************************************************/
package jmmc.mcs.util;

import jmmc.mcs.log.MCSLogger;

import org.w3c.dom.*;

import org.xml.sax.*;

import java.awt.*;
import java.awt.event.*;

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
        MCSLogger.info("Application will grab resources from '" + name + "'");
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
                MCSLogger.warning("Resource bundle can't be found :" +
                    e.getMessage());

                return null;
            }
        }

        MCSLogger.info("getResource for " + resourceName);

        try
        {
            return _resources.getString(resourceName);
        }
        catch (Exception e)
        {
            MCSLogger.warning("Entry not found :" + e.getMessage());
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
        MCSLogger.trace();

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
        MCSLogger.trace();

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
        MCSLogger.trace();

        return getResource("actions.action." + actionName + ".icon");
    }

    /**
     * Get the icon of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated icon
     */
    public static ImageIcon getActionIcon(String actionName)
    {
        MCSLogger.trace();

        String    iconPath  = getActionIconPath(actionName);

        ImageIcon imageIcon = createImageIcon(iconPath,
                "Icon of action '" + actionName + "'");

        return imageIcon;
    }

    /**
     * Get the accelerator (aka. keyboard short cut) of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated accelerator
     */
    public static KeyStroke getActionAccelerator(String actionName)
    {
        MCSLogger.trace();

        // Get the accelerator string description from the Resource.properties file
        String keyString = getResource("actions.action." + actionName +
                ".accelerator");

        // Get and return the KeyStroke from the accelerator string description
        KeyStroke accelerator = KeyStroke.getKeyStroke(keyString);

        MCSLogger.debug("keyString['" + actionName + "'] = '" + keyString +
            "' -> accelerator = '" + accelerator + "'.");

        return accelerator;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path, String description)
    {
        MCSLogger.trace();

        if (path == null)
        {
            return null;
        }

        URL imgURL = Resources.class.getResource("./" + path);

        if (imgURL != null)
        {
            return new ImageIcon(imgURL, description);
        }
        else
        {
            MCSLogger.warning("Couldn't find file: '" + path + "'");

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
        MCSLogger.trace();

        return getResource("widgets.widget." + widgetName + ".tooltip");
    }
}
/*___oOo___*/
