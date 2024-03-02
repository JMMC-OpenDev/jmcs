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

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.preference.CommonPreferences;
import fr.jmmc.jmcs.util.StringUtils;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicHTML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is dedicated to EDT invoke methods and simplify GUI code
 *
 * @author Laurent BOURGES.
 */
public final class SwingUtils {

    /** logger */
    private final static Logger _logger = LoggerFactory.getLogger(SwingUtils.class.getName());
    /** default Insets for no margins */
    public final static Insets NO_MARGIN = new Insets(0, 0, 0, 0);

    public enum ComponentSizeVariant {
        mini, small, regular, large;

        /**
         * Compares ComponentSizeVariant
         * @param other instance
         * @return true if (this.ordinal() < other.ordinal())
         */
        public boolean isLower(final ComponentSizeVariant other) {
            return (this.ordinal() < other.ordinal());
        }
    }

    /**
     * Forbidden constructor
     */
    private SwingUtils() {
        super();
    }

    /**
     * @param c component
     * @return true if the given component has an HTML view (swing); false otherwise
     */
    public static boolean isHTML(final JComponent c) {
        return c.getClientProperty(BasicHTML.propertyKey) != null;
    }

    /**
     * Compute the width of the string using a font with the specified
     * "metrics" (sizes). It ignores any HTML tag.
     *
     * @param fontMetrics a FontMetrics object to compute with
     * @param text the String to compute
     * @return an int containing the string width
     */
    @SuppressWarnings("StringEquality")
    public static int getTextWidth(final FontMetrics fontMetrics, final String text) {
        if (StringUtils.isEmpty(text)) {
            return 0;
        }
        final String rawText;

        // Get rid of html tags:
        if ((text.charAt(0) == '<') && (text.startsWith("<html>"))) {
            rawText = StringUtils.removeTags(text);

            if (_logger.isDebugEnabled() && (rawText != text)) {
                _logger.debug("text:    [[{}]]", text);
                _logger.debug("rawText: [[{}]]", rawText);
            }
            if (StringUtils.isEmpty(rawText)) {
                return 0;
            }
        } else {
            rawText = text;
        }
        // May be costly:
        final int textWidth = SwingUtilities.computeStringWidth(fontMetrics, rawText);

        if (_logger.isDebugEnabled()) {
            _logger.debug("getTextWidth('{}') = {}", rawText, textWidth);
        }
        return textWidth;
    }

    /**
     * Returns the first <code>JFrame</code> ancestor of <code>c</code>, or
     * App.getFrame() if <code>c</code> is not contained inside a <code>JFrame</code>.
     *
     * @param c <code>Component</code> to get <code>JFrame</code> ancestor of.
     * @return the first <code>JFrame</code> ancestor of <code>c</code>, or
     *         App.getFrame() if <code>c</code> is not contained inside a
     *         <code>JFrame</code>.
     */
    public static JFrame getParentFrame(final JComponent c) {
        final Window w = getParentWindow(c);
        if (w instanceof JFrame) {
            return (JFrame) w;
        }
        // null or JFrame
        return App.getExistingFrame();
    }

    private static Window getParentWindow(final JComponent c) {
        return SwingUtilities.getWindowAncestor(c);
    }

    /**
     * Commit pending edition in any text fields present in parent frame of the given component.
     * @param c <code>Component</code> to get <code>JFrame</code> ancestor of.
     */
    public static void commitChanges(final JComponent c) {
        final Window window = SwingUtils.getParentWindow(c);
        if (window != null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("commitChangesAndInvokeLaterEDT: Parent Window: {}", window);
            }
            final Component com = window.getFocusOwner();
            if (com != null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("commitChangesAndInvokeLaterEDT: Focus owner: {}", com);
                }
                if (com instanceof JFormattedTextField) {
                    final JFormattedTextField jTextField = (JFormattedTextField) com;
                    try {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("commitChangesAndInvokeLaterEDT: JFormattedTextField.commitEdit on: {}", com);
                        }
                        // Convert and commit changes:
                        jTextField.commitEdit();
                    } catch (ParseException pe) {
                        _logger.info("commitChangesAndInvokeLaterEDT: Could not handle input: {}", jTextField.getText(), pe);
                    }
                }
            }
        }
    }

    /**
     * Returns true if the current thread is the Event Dispatcher Thread (EDT)
     *
     * @return true if the current thread is the Event Dispatcher Thread (EDT)
     */
    public static boolean isEDT() {
        return SwingUtilities.isEventDispatchThread();
    }

    /**
     * Execute the given runnable code dedicated to Swing using the Event Dispatcher Thread (EDT)
     * @param runnable runnable code dedicated to Swing
     */
    public static void invokeEDT(final Runnable runnable) {
        if (isEDT()) {
            // current Thread is EDT, simply execute runnable:
            runnable.run();
        } else {
            invokeLaterEDT(runnable);
        }
    }

    /**
     * Execute LATER the given runnable code dedicated to Swing using the Event Dispatcher Thread (EDT)
     * @param runnable runnable code dedicated to Swing
     */
    public static void invokeLaterEDT(final Runnable runnable) {
        // current Thread is NOT EDT, simply invoke later runnable using EDT:
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Commit pending edition in any text fields present in parent frame of the given component.
     * Execute LATER the given runnable code dedicated to Swing using the Event Dispatcher Thread (EDT)
     * @param c <code>Component</code> to get <code>JFrame</code> ancestor of.
     * @param runnable runnable code dedicated to Swing
     */
    public static void commitChangesAndInvokeLaterEDT(final JComponent c, final Runnable runnable) {
        commitChanges(c);
        // current Thread is NOT EDT, simply invoke later runnable using EDT:
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Execute the given runnable code dedicated to Swing using the Event Dispatcher Thread (EDT)
     * And waits for completion
     * @param runnable runnable code dedicated to Swing
     * @throws IllegalStateException if any exception occurs while the given runnable code executes using EDT
     */
    public static void invokeAndWaitEDT(final Runnable runnable) throws IllegalStateException {
        if (isEDT()) {
            // current Thread is EDT, simply execute runnable:
            runnable.run();
        } else {
            // If the current thread is interrupted, then use invoke later EDT (i.e. do not wait):
            if (Thread.currentThread().isInterrupted()) {
                invokeLaterEDT(runnable);
            } else {
                try {
                    // Using invokeAndWait to be in sync with the calling thread:
                    SwingUtilities.invokeAndWait(runnable);

                } catch (InterruptedException ie) {
                    // propagate the exception because it should never happen:
                    throw new IllegalStateException("SwingUtils.invokeAndWaitEDT : interrupted while running " + runnable, ie);
                } catch (InvocationTargetException ite) {
                    // propagate the internal exception :
                    throw new IllegalStateException("SwingUtils.invokeAndWaitEDT : an exception occured while running " + runnable, ite.getCause());
                }
            }
        }
    }

    /**
     * Adjust the row height of the given JTable according to the UI scale
     * @param jTable table to adjust
     */
    public static void adjustRowHeight(final JTable jTable) {
        // note: the given table is just created to avoid reentrance issues:
        final int initialRowHeight = SwingSettings.setAndGetInitialRowHeight(jTable.getRowHeight());
        adjustRowHeight(jTable, initialRowHeight);
    }

    /**
     * Adjust the row height of the given JTable according to the UI scale
     * @param jTable table to adjust
     * @param initialRowHeight initial row height to use in computations
     */
    public static void adjustRowHeight(final JTable jTable, final int initialRowHeight) {
        jTable.setRowHeight(adjustUISize(initialRowHeight));
    }

    /**
     * Adjust the row height of the given JTable according to the UI scale
     * @param jTree tree to adjust
     * @param initialRowHeight initial row height to use in computations
     */
    public static void adjustRowHeight(final JTree jTree, final int initialRowHeight) {
        jTree.setRowHeight(adjustUISize(initialRowHeight));
    }

    /**
     * Adjust the given size according to the UI scale
     * @param size to adjust
     * @return rounded integer value of the scaled input size
     */
    public static int adjustUISize(final int size) {
        return Math.round(adjustUISize((float) size));
    }

    /**
     * Adjust the given size according to the UI scale (if greater than 1.0)
     * @param size to adjust
     * @return rounded integer value of the scaled input size (>= given size)
     */
    public static int adjustUISizeCeil(final int size) {
        return Math.max(size, (int) (adjustUISize((float) size)));
    }

    /**
     * Adjust the given size according to the UI scale
     * @param size to adjust
     * @return float value of the scaled input size
     */
    public static float adjustUISize(final float size) {
        return size * CommonPreferences.getInstance().getUIScale();
    }

    /**
     * Adjust the given size according to the UI scale
     * @param size to adjust
     * @return float value of the scaled input size
     */
    public static double adjustUISize(final double size) {
        return size * CommonPreferences.getInstance().getUIScale();
    }

    public static void adjustSize(final JComponent com, final ComponentSizeVariant variant) {
        if (variant != null) {
            com.putClientProperty("JComponent.sizeVariant", variant.name());

            if (com instanceof AbstractButton && variant.isLower(ComponentSizeVariant.regular)) {
                // ensure no margin
                ((AbstractButton) com).setMargin(NO_MARGIN);

                // or use "square" button with smaller margin on macos:
                com.putClientProperty("JButton.buttonType", "square");
            }
        }
    }
}
