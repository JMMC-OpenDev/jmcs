/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import com.jidesoft.plaf.LookAndFeelFactory;
import fr.jmmc.jmcs.util.MCSExceptionHandler;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gather swing related properties settings for our applications.
 *
 * This code is called during App initialization, but you are strongly invited
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
     * Initialize maximum of things to get uniform application running inn the scientific context.
     * Initialization are done only on the first call of this method ( which should be from a main method )
     */
    public static void setup() {

        if (alreadyDone) {
            return;
        }

        setSystemProps();
        setMandatory();
        setSwingDefaults();
        installJideLAFExtensions();

        // Install exception handlers :
        MCSExceptionHandler.installSwingHandler();

        logger.info("Swing settings set.");

        alreadyDone = true;
    }

    /**
     * Initialize default locale and default time zone.
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
     * Change locale of SWING and ToolTip related.
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
     * Defines in code some System.properties to force text anti-aliasing and Mac OS features ...
     * @see MacOSXAdapter
     */
    protected static void setSystemProps() {
        // force anti aliasing :
        if (SystemUtils.IS_JAVA_1_5) {
            System.setProperty("swing.aatext", "true");
        } else if (SystemUtils.IS_JAVA_1_6) {
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

    private static void installJideLAFExtensions() {
        // To ensure the use of TriStateCheckBoxes in the Jide CheckBoxTree
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installJideExtension();
            }
        });
    }
}
