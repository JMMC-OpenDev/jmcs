/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import fr.jmmc.jmcs.util.MCSExceptionHandler;
import java.util.Locale;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import org.apache.commons.lang.SystemUtils;

/**
 * This class gather swing related properties settings for our applications.
 *
 * This code is called in the App instanciation, but you are strongly invited
 * to place following code at the first lines of the main method in your
 * applications:
 *
 * <code>SwingSettings.defineDefaults</code>
 *
 * Copied and adapted from voparis code.
 *
 * @author Laurent BOURGES, Guillaume MELLA.
 */
public class SwingSettings {

    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(SwingSettings.class.getName());
    /** flag to prevent multiple code execution */
    private static boolean alreadyDone = false;

    /** Hidden constructor */
    private SwingSettings() {
    }

    /**
     * Init maximum of things to get uniform application running inn the scientific context.
     * Init are done only on the first call of this method ( which should be from a main method )
     */
    public static void setup() {

        if (alreadyDone) {
            return;
        }

        setSystemProps();
        setMandatory();
        setSwingDefaults();

        // Install exception handlers :
        MCSExceptionHandler.installSwingHandler();

        logger.info("Swing settings set.");

        alreadyDone = true;
    }

    /**
     * Init default locale and default timezone.
     */
    private static void setMandatory() {
        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);

        logger.debug("Set Locale.US as default Locale");

        // Set the default timezone to GMT to handle properly the date in UTC :
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        logger.debug("Set GMT as default TimeZone");
    }

    /**
     * Change locale of swing and Tooltip related.
     */
    private static void setSwingDefaults() {

        // Force Locale for Swing Components :
        JComponent.setDefaultLocale(Locale.US);

        logger.debug("Set Locale.US for JComponents");

        // Let the tooltip stay longer (60s) :
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(60000);

        logger.debug("Make tooltips appear more quickly and stay longer");
    }

    /**
     * Defines in code some System.properties to force text antialiasing and macOS features ...
     * @see fr.jmmc.mcs.gui.OSXAdapter
     */
    protected static void setSystemProps() {

        // force anti aliasing :
        final String version = System.getProperty("java.version");
        if (version.startsWith("1.5")) {
            System.setProperty("swing.aatext", "true");
        } else if (version.startsWith("1.6")) {
            final String old = System.getProperty("awt.useSystemAAFontSettings");
            if (old == null) {
                System.setProperty("awt.useSystemAAFontSettings", "on");
            }
        }

        if (SystemUtils.IS_OS_MAC_OSX) {
            // always use screen menuBar :
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            logger.debug("use screen menu bar in look and feel");
        }

    }
}
