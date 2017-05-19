/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2013, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.utils.ThreadCheckingRepaintManager;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.util.IntrospectionUtils;
import fr.jmmc.jmcs.util.MCSExceptionHandler;
import java.awt.Font;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gather SWING related properties settings for our applications.
 *
 * This code is called during Bootstrapper initialization (always performed before any application code).
 *
 * @author Laurent BOURGES, Guillaume MELLA.
 */
public final class SwingSettings {

    /** logger */
    private final static Logger _logger = LoggerFactory.getLogger(SwingSettings.class.getName());
    /** enable/disable EDT violation detection */
    private final static boolean DEBUG_EDT_VIOLATIONS = false;
    /** flag to prevent multiple code execution */
    private static boolean _alreadyDone = false;
    /** cache for initial font sizes */
    private final static Map<Object, Integer> INITIAL_FONT_SIZES = new HashMap<Object, Integer>(64);

    /** Hidden constructor */
    private SwingSettings() {
    }

    /**
     * Initialize maximum of things to get uniform application running inn the scientific context.
     * Initialization are done only on the first call of this method (which should be from a main method)
     */
    public static void setup() {
        // avoid reentrance:
        if (_alreadyDone) {
            return;
        }
        _alreadyDone = true;

        setSwingDefaults();

        // Fix font defaults:
        fixUIFonts(UIManager.getDefaults());

        // Apply LAF defaults:
        setLAFDefaults();

        setDefaultLookAndFeel();

        if (DEBUG_EDT_VIOLATIONS) {
            RepaintManager.setCurrentManager(new ThreadCheckingRepaintManager());
        }

        // Install exception handlers:
        if (Bootstrapper.isHeadless()) {
            // Use logging exception handler:
            MCSExceptionHandler.installLoggingHandler();
        } else {
            // Use Swing exception handler:
            MCSExceptionHandler.installSwingHandler();
        }

        _logger.info("Swing settings set.");
    }

    private static void setDefaultLookAndFeel() {
        final String className = CommonPreferences.getInstance().getPreference(CommonPreferences.UI_LAF_CLASSNAME);
        _logger.debug("LAF class: {}", className);

        // Note: use the main thread (not EDT) to avoid any deadlock during bootstrapping:
        setLookAndFeel(className);
    }

    public static void setLookAndFeel(final String className) {
        if (className != null
                && !className.isEmpty()
                && !className.equals(UIManager.getLookAndFeel().getClass().getName())) {

            _logger.info("Use Look & Feel: {}", className);
            try {
                final LookAndFeel newLaf = (LookAndFeel) IntrospectionUtils.getInstance(className);
                UIManager.setLookAndFeel(newLaf);

                // Re-apply LAF defaults:
                setLAFDefaults();

                final Frame mainFrame = App.getExistingFrame();
                if (mainFrame != null) {
                    SwingUtilities.updateComponentTreeUI(mainFrame);
                    mainFrame.pack();
                }
            } catch (UnsupportedLookAndFeelException ulafe) {
                throw new RuntimeException(ulafe);
            }
        }
    }

    /**
     * Define Swing defaults: 
     * - Change locale of SWING and ToolTip related.
     */
    private static void setSwingDefaults() {
        // Force Locale for Swing Components :
        JComponent.setDefaultLocale(Locale.getDefault());

        _logger.debug("Set Locale.US for JComponents");

        // Let the tooltip stay longer (60s) :
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(60000);

        _logger.debug("Make tooltips appear more quickly and stay longer");
    }

    /**
     * Define LAF defaults: 
     * - adjust font sizes
     * - install JIDE extensions
     */
    static void setLAFDefaults() {
        // Fix font for the current LAF:
        fixUIFonts(UIManager.getLookAndFeelDefaults());

        if (!Bootstrapper.isHeadless()) {
            installJideLAFExtensions();
        }
    }

    private static synchronized void fixUIFonts(final UIDefaults uidef) {
        final float fontSizeScale = CommonPreferences.getInstance().getUIScale();
        _logger.debug("Font scale: {}", fontSizeScale);

        for (Entry<Object, Object> e : uidef.entrySet()) {
            final Object key = e.getKey();

            if (key instanceof String) {
                final String strKey = ((String) key);

//                _logger.info("{} = {}", strKey, e.getValue());

                if (strKey.contains("font") || strKey.contains("Font")) {
                    Font font = uidef.getFont(key);

                    _logger.debug("default: {} = {}", key, font);

                    if (font != null) {
                        final int size = font.getSize();
                        final Integer fixedSize = INITIAL_FONT_SIZES.get(key);

                        if (fixedSize == null || size == fixedSize.intValue()) {
                            INITIAL_FONT_SIZES.put(key, Integer.valueOf(size));

                            final int newSize = Math.round(fontSizeScale * size);
                            final int newStyle = (fontSizeScale > 1.2f) ? Font.PLAIN : font.getStyle();
                            final String name = font.getName();

                            // Force using Monospaced font for Tree & TextArea:
                            final String newName = ("Tree.font".equals(strKey)
                                    || "TextArea.font".equals(strKey)) ? "Monospaced" : name;

                            // Derive new font:
                            final Font newFont = new Font(newName, newStyle, newSize);
                            _logger.debug("fixed: {} = {}", key, newFont);

                            uidef.put(key, new FontUIResource(newFont));
                        }
                    }
                }
            }
        }
    }

    /**
     * Install JIDE Look And Feel extensions.
     * TODO: it has side-effects on date spinner ... 
     * maybe enable it only for applications requiring it (System property) ?
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
