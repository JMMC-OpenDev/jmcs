/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.apache.commons.lang.SystemUtils;

/**
 * Class used to get resources informations from one central point (xml file).
 * Applications must start to set the resource file name before
 * any gui construction.
 * 
 * @author Guillaume MELLA, Sylvain LAFRASSE, Laurent BOURGES.
 */
public abstract class Resources {

    /** the logger facility */
    protected static final Logger _logger = LoggerFactory.getLogger(Resources.class.getName());
    /** Contains the class nale for logging */
    private static String _loggerClassName = "Resources";
    /** resource filename  that must be overloaded by subclasses */
    protected static String _resourceName = "fr/jmmc/jmcs/resource/Resources";
    /** cached resource bundle */
    private static ResourceBundle _resources = null;
    /** flag to indicate that the resource bundle is resolved */
    private static boolean _resolved = false;
    /** Store whether the execution platform is a Mac or not */
    private static boolean MAC_OS_X = SystemUtils.IS_OS_MAC_OSX;

    /**
     * Indicates the property file where informations will be extracted.
     * The property file must end with .properties filename extension. But the
     * given name should omit the extension.
     *
     * @param name Indicates property file to use.
     */
    public static void setResourceName(final String name) {
        _logger.debug("Application will grab resources from '{}'", name);

        _resourceName = name;
        _resolved = false;
    }

    /**
     * Get content from resource file.
     *
     * @param resourceName name of resource
     *
     * @return the content of the resource or null indicating error
     */
    public static String getResource(final String resourceName) {
        return getResource(resourceName, false);
    }

    /**
     * Get content from resource file.
     *
     * @param resourceKey name of resource
     * @param quietIfNotFound true to not log at warning level i.e. debug level
     *
     * @return the content of the resource or null indicating error
     */
    public static String getResource(final String resourceKey, final boolean quietIfNotFound) {
        if (_resources == null) {

            if (!_resolved) {
                _logger.debug("getResource for '{}'", _resourceName);
                try {
                    // update the resolve flag to avoid redundant calls to getBundle when no bundle is available:
                    _resolved = true;
                    _resources = ResourceBundle.getBundle(_resourceName);
                } catch (MissingResourceException mre) {
                    if (quietIfNotFound) {
                        _logger.debug("Resource bundle can't be found : {}", mre.getMessage());
                    } else {
                        _logger.warn("Resource bundle can't be found : {}", mre.getMessage());
                    }
                }
            }

            if (_resources == null) {
                return null;
            }
        }

        _logger.debug("getResource for '{}'", resourceKey);
        try {
            return _resources.getString(resourceKey);
        } catch (MissingResourceException mre) {
            if (quietIfNotFound) {
                _logger.debug("Entry can't be found : {}", mre.getMessage());
            } else {
                _logger.warn("Entry can't be found : {}", mre.getMessage());
            }
        }

        return null;
    }

    /**
     * Get the text of an action.
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated text
     */
    public static String getActionText(final String actionName) {
        return getResource("actions.action." + actionName + ".text", true);
    }

    /**
     * Get the description of an action.
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated description
     */
    public static String getActionDescription(final String actionName) {
        return getResource("actions.action." + actionName + ".description", true);
    }

    /**
     * Get the tooltip text of widget related to the common widget group.
     *
     * @param widgetName the widgetInstanceName
     *
     * @return the tooltip text
     */
    public static String getToolTipText(final String widgetName) {
        return getResource("widgets.widget." + widgetName + ".tooltip", true);
    }

    /**
     * Get the accelerator (aka. keyboard short cut) of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated accelerator
     */
    public static KeyStroke getActionAccelerator(final String actionName) {
        // Get the accelerator string description from the Resource.properties file
        String keyString = getResource("actions.action." + actionName + ".accelerator", true);

        if (keyString == null) {
            return null;
        }

        // If the execution is on Mac OS X
        if (MAC_OS_X) {
            // The 'command' key (aka Apple key) is used
            keyString = "meta " + keyString;
        } else {
            // The 'control' key ise used elsewhere
            keyString = "ctrl " + keyString;
        }

        // Get and return the KeyStroke from the accelerator string description
        KeyStroke accelerator = KeyStroke.getKeyStroke(keyString);

        if (_logger.isDebugEnabled()) {
            _logger.debug("keyString['{}'] = '{}' -> accelerator = '{}'.",
                    new Object[]{actionName, keyString, accelerator});
        }

        return accelerator;
    }

    /**
     * Get the icon of an action .
     *
     * @param actionName the actionInstanceName
     *
     * @return the associated icon
     */
    public static ImageIcon getActionIcon(final String actionName) {
        // Get back the icon image path
        String iconPath = getResource("actions.action." + actionName + ".icon", true);

        if (iconPath == null) {
            _logger.debug("No icon resource found for action name '{}'.", actionName);
            return null;
        }

        // Get the image from path
        URL imgURL = Resources.class.getResource(iconPath);

        if (imgURL == null) {
            _logger.debug("Could not load icon '{}'.", iconPath);
            return null;
        }

        _logger.debug("Using imgUrl for icon resource  '{}'.", imgURL);

        return new ImageIcon(imgURL);
    }

    /**
     * Private constructor
     */
    private Resources() {
        super();
    }
}
/*___oOo___*/
