/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.utils.ThreadCheckingRepaintManager;
import fr.jmmc.jmcs.util.MCSExceptionHandler;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.ToolTipManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gather swing related properties settings for our applications.
 *
 * This code is called during Bootstrapper initialization (always performed before any application code)
 *
 * @author Laurent BOURGES, Guillaume MELLA.
 */
public final class SwingSettings {

    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(SwingSettings.class.getName());
    /** enable/disable EDT violation detection */
    private final static boolean DEBUG_EDT_VIOLATIONS = false;
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
        // avoid reentrance:
        if (alreadyDone) {
            return;
        }
        alreadyDone = true;

        installJideLAFExtensions();
        setSwingDefaults();

        // Install exception handlers :
        MCSExceptionHandler.installSwingHandler();

        logger.info("Swing settings set.");
    }

    /**
     * Change locale of SWING and ToolTip related.
     */
    private static void setSwingDefaults() {
        // Force Locale for Swing Components :
        JComponent.setDefaultLocale(Locale.getDefault());

        logger.debug("Set Locale.US for JComponents");

        // Let the tooltip stay longer (60s) :
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(60000);

        logger.debug("Make tooltips appear more quickly and stay longer");

        if (DEBUG_EDT_VIOLATIONS) {
            RepaintManager.setCurrentManager(new ThreadCheckingRepaintManager());
        }
    }

    /**
     * Install JIDE Look And Feel extenstions
     * TODO: it has side-effects on date spinner ... maybe enable it only for applications requiring it (System property) ?
     */
    public static void installJideLAFExtensions() {
        // To ensure the use of TriStateCheckBoxes in the Jide CheckBoxTree
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            @Override
            public void run() {
                // Install JIDE extensions (Swing workaround):
                LookAndFeelFactory.installJideExtension();
            }
        });
    }
}
