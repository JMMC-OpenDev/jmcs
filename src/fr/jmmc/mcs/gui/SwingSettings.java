/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SwingSettings.java,v 1.4 2011-04-07 14:08:27 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2011/04/07 13:53:32  mella
 * Javadoc
 *
 * Revision 1.2  2011/04/07 13:52:31  mella
 * Add locale and timezone inits
 *
 * Revision 1.1  2011/04/01 16:09:44  mella
 * new class to define swing defaults
 *
 *
 */
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.util.MCSExceptionHandler;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author bourges
 */
public class SwingSettings {

    /** logger */
    private final static Logger logger = Logger.getLogger(SwingSettings.class.getName());

    /**
     * Init maximum of things to get uniform application running inn the scientific context.
     */
    public static void setup() {

        setMandatory();
        setSwingDefaults();
        setSystemProps();

        // Install exception handlers :
        MCSExceptionHandler.installSwingHandler();

        /* in the future ?
        initLookAndFeel();

        changeUIDefaults();

        changeUIFont();
         */
    }

    /**
     * Init default locale and default timezone.
     */
    private static void setMandatory() {
        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set Locale.US as default Locale");
        }

        // Set the default timezone to GMT to handle properly the date in UTC :
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set GMT as default TimeZone");
        }
        
    }

    /**
     * Change locale of swing and Tooltip related.
     */
    private static void setSwingDefaults() {

        // Force Locale for Swing Components :
        JComponent.setDefaultLocale(Locale.US);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Set Locale.US for JComponents");
        }

        // Let the tooltip stay longer (60s) :
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(60000);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Make tooltips appear more quickly and stay longer");
        }
        
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
            // TODO application name : (if it is not too early in the startup phases ??
            //System.setProperty("com.apple.mrj.application.apple.menu.about.name", App.xxx);

            // always use screen menuBar :
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "use screen menu bar in look and feel");
            }
        }

    }
    /**
     * 2 - Initialises the swing look and feel (using the Configuration.FORCE_LOOK_AND_FEEL [force.laf])
     * and use GTK for the GUI on linux platform (if the Configuration.LINUX_FORCE_GTK [linux.forceGTK] = true)
     * @param conf global configuration

    private static void initLookAndFeel() {
    if (log.isDebugEnabled()) {
    log.debug("SwingAdapter.initLookAndFeel : enter");
    }
    String laf = conf.getProperty(Configuration.FORCE_LOOK_AND_FEEL);
    if (laf == null) {
    if (conf.isLinux() && conf.getBoolean(Configuration.LINUX_FORCE_GTK)) {
    laf = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    }
    }
    if (laf != null) {
    if (log.isInfoEnabled()) {
    log.info("initLookAndFeel : " + laf);
    }
    try {
    UIManager.setLookAndFeel(laf);
    } catch (Exception e) {
    log.error("initLookAndFeel : unable to set look and feel : " + laf, e);
    }
    }
    if (log.isDebugEnabled()) {
    log.debug("SwingAdapter.initLookAndFeel : exit");
    }
    }

    /**
     * 3 - 
     *
    private static void changeUIDefaults() {
    if (log.isDebugEnabled()) {
    log.debug("SwingAdapter.changeUIDefaults : enter");
    }

    JFrame.setDefaultLookAndFeelDecorated(false);

    ToolTipManager.sharedInstance().setInitialDelay(100);
    ToolTipManager.sharedInstance().setDismissDelay(30000);

    // Mode TEST = developper tests
    SimpleFrame.setTestMode(conf.getBoolean(Configuration.MODE_TEST));

    if (log.isDebugEnabled()) {
    log.debug("SwingAdapter.changeUIDefaults : exit");
    }
    }

    /**
     * 4 - Use small fonts if the Configuration.FONT_SMALL [font.small] = true
     * @param conf global configuration
     *
    private static void changeUIFont() {
    if (log.isDebugEnabled()) {
    log.debug("SwingAdapter.changeUIFont : enter");
    }

    if (conf.getBoolean(Configuration.FONT_SMALL)) {
    final Enumeration e = UIManager.getDefaults().keys();

    String s;
    if (log.isDebugEnabled()) {
    log.debug("UIManager defaults :");
    }
    while (e.hasMoreElements()) {
    s = e.nextElement().toString();
    if (s.matches("[a-zA-z]+.font")) {
    UIManager.put(s, new Font("Bitstream Vera Sans", Font.PLAIN, 10));
    }
    if (log.isDebugEnabled()) {
    log.debug(s + " = " + UIManager.getString(s));
    }
    }
    UIManager.put("ComboBox.font", new Font("Bitstream Sans Mono", Font.PLAIN, 11));
    }
    if (log.isDebugEnabled()) {
    log.debug("SwingAdapter.changeUIFont : exit");
    }
    }

     */
}
