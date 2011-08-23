/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import org.apache.commons.lang.SystemUtils;

/**
 * A text field for search/filter interfaces. The extra functionality includes
 * a placeholder string (when the user hasn't yet typed anything), and a button
 * to clear the currently-entered text.
 *
 * @origin Elliott Hughes
 *
 * @todo : add a menu of recent searches.
 * @todo : make recent searches persistent.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public class SearchField extends JTextField {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Logger */
    protected static final Logger _logger = Logger.getLogger(SearchField.class.getName());
    /** The border that draws the magnifying glass and the cancel cross */
    private static final Border CANCEL_BORDER = new CancelBorder();
    /** Store wether notifications should be sent every time a key is pressed */
    private boolean sendsNotificationForEachKeystroke = false;
    /** Store wether a text should be drawn when nothing else in textfield */
    private boolean showingPlaceholderText = false;
    /** store wether the cancel cross is currently clicked */
    private boolean armed = false;
    /** the text displayed when nothing in */
    private final String placeholderText;
    /** previous entered text */
    private String previousText = "";

    /**
     * Creates a new SearchField object.
     *
     * @param placeholderText the text displayed when nothing in.
     */
    public SearchField(final String placeholderText) {
        super(8); // 8 characters wide by default

        this.placeholderText = placeholderText;

        addFocusListener(new PlaceholderText());
        initBorder();
        initKeyListener();
    }

    /**
     * Creates a new SearchField object with a default "Search" place holder.
     */
    public SearchField() {
        this("Search");
    }

    /**
     * Draw the custom widget border.
     */
    private void initBorder() {
        // On Mac OS X, simply use the OS specific search textfield widget
        if (SystemUtils.IS_OS_MAC_OSX) {
            // http://developer.apple.com/mac/library/technotes/tn2007/tn2196.html#//apple_ref/doc/uid/DTS10004439
            putClientProperty("JTextField.variant", "search");
            putClientProperty("JTextField.FindAction",
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            postActionEvent();
                        }
                    });
            putClientProperty("JTextField.CancelAction",
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            cancel();
                        }
                    });

            return;
        }

        // Fallback for platforms other than Mac OS X

        // Add an empty border around to compensate for rounded corners
        setBorder(BorderFactory.createEmptyBorder(4, 22, 4, 14));

        setBorder(new CompoundBorder(getBorder(), CANCEL_BORDER));

        final MouseInputListener mouseInputListener = new CancelListener();
        addMouseListener(mouseInputListener);
        addMouseMotionListener(mouseInputListener);

        // We must be non-opaque since we won't fill all pixels.
        // This will also stop the UI from filling our background.
        setOpaque(false);
    }

    /**
     * Draw the dedicated custom rounded textfield.
     *
     * @param g the graphical context to draw in.
     */
    @Override
    protected void paintComponent(final Graphics g) {
        // On anything but Mac OS X
        if (!SystemUtils.IS_OS_MAC_OSX) {
            int width = getWidth();
            int height = getHeight();

            // Paint a rounded rectangle in the background surrounded by a black line.
            g.setColor(Color.LIGHT_GRAY);
            g.fillRoundRect(0, 0, width, height, height, height);

            g.setColor(Color.GRAY);
            g.fillRoundRect(0, -1, width, height, height, height);

            g.setColor(getBackground());
            g.fillRoundRect(1, 1, width - 2, height - 2, height - 2, height
                    - 2);

            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(10, 1, width - 10, 1);
        }

        // Now call the superclass behavior to paint the foreground.
        super.paintComponent(g);
    }

    /**
     * Follow keystrokes to notify listeners accordinaly.
     */
    private void initKeyListener() {
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancel();
                } else if (sendsNotificationForEachKeystroke) {
                    maybeNotify();
                }
            }
        });
    }

    /**
     * Reset SearchField content and notify listeners.
     */
    private void cancel() {
        setText("");
        postActionEvent();
    }

    /**
     * Sets the text of this <code>TextComponent</code>
     * to the specified text.
     *
     * This overrides the default behaviour to tell the placeholder to use this new text value
     *
     * @param txt the new text to be set
     */
    @Override
    public void setText(final String txt) {
        super.setText(txt);

        if (!this.placeholderText.equals(txt)) {
            this.previousText = txt;
        }
    }

    /**
     * Returns the text contained in this <code>TextComponent</code>.
     *
     * If the text corresponds to the placeholder text then it returns "".
     *
     * @return the text, not the placeholder text
     */
    public final String getRealText() {
        final String txt = super.getText();

        if (this.placeholderText.equals(txt)) {
            return "";
        }
        return txt;
    }

    /**
     * Trap notifications when showing place holder.
     */
    private void maybeNotify() {
        if (showingPlaceholderText) {
            return;
        }

        postActionEvent();
    }

    /**
     * Store wether notifications should be sent for each key pressed.
     *
     * @param eachKeystroke true to notify any key pressed, false otherwise.
     */
    public void setSendsNotificationForEachKeystroke(final boolean eachKeystroke) {
        this.sendsNotificationForEachKeystroke = eachKeystroke;
    }

    /**
     * Draws the cancel button (a gray circle with a white cross) and the magnifying glass icon.
     */
    private final static class CancelBorder extends EmptyBorder {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        /** disarm color */
        private static final Color DISARMED_GRAY = new Color(0.7f, 0.7f, 0.7f);

        /**
         * Constructor
         */
        CancelBorder() {
            super(0, 0, 0, 8);
        }

        @Override
        public void paintBorder(final Component c, final Graphics oldGraphics,
                final int x, final int y, final int width, final int height) {
            final SearchField field = (SearchField) c;
            final Color backgroundColor = field.getBackground();
            final Graphics2D g = (Graphics2D) oldGraphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw magnifying glass lens
            final int diskL = 9;
            final int diskX = x - diskL - 6;
            final int diskY = y + ((height - 1 - diskL) / 2);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(diskX, diskY, diskL, diskL);
            g.setColor(backgroundColor);
            g.fillOval(diskX + 2, diskY + 2, diskL - 4, diskL - 4);

            // Draw magnifying glass handle
            final int downX = (diskX + diskL) - 3;
            final int downY = (diskY + diskL) - 3;
            final int upX = downX + 4;
            final int upY = downY + 4;
            g.setColor(Color.DARK_GRAY);
            g.drawLine(downX, downY, upX, upY);
            g.drawLine(downX, downY, upX, upY);
            g.drawLine(downX + 1, downY, upX, upY);

            if (field.showingPlaceholderText || field.getText().length() == 0) {
                // Don't draw the cancel cross
                return;
            }

            // Draw shaded disk
            final int circleL = 14;
            final int circleX = (x + width) - circleL + 9;
            final int circleY = y + ((height - 1 - circleL) / 2);
            g.setColor(field.armed ? Color.GRAY : DISARMED_GRAY);
            g.fillOval(circleX, circleY, circleL, circleL);

            // Draw white cross
            final int lineL = circleL - 8;
            final int lineX = circleX + 4;
            final int lineY = circleY + 4;
            g.setColor(backgroundColor);
            g.drawLine(lineX, lineY, lineX + lineL, lineY + lineL);
            g.drawLine(lineX, lineY + lineL, lineX + lineL, lineY);
        }
    }

    /**
     * Handles a click on the cancel button by clearing the text and notifying
     * any ActionListeners.
     */
    private final class CancelListener extends MouseInputAdapter {

        /**
         * Return true if the mouse is over the cancel button
         * @param me mouse event
         * @return true if the mouse is over the cancel button
         */
        private boolean isOverButton(final MouseEvent me) {
            // If the button is down, we might be outside the component
            // without having had mouseExited invoked.
            if (!contains(me.getPoint())) {
                return false;
            }

            // In lieu of proper hit-testing for the circle, check that
            // the mouse is somewhere in the border.
            final Rectangle innerArea = SwingUtilities.calculateInnerArea(SearchField.this, null);

            return (!innerArea.contains(me.getPoint()));
        }

        @Override
        public void mouseDragged(final MouseEvent me) {
            arm(me);
        }

        @Override
        public void mouseEntered(final MouseEvent me) {
            arm(me);
        }

        @Override
        public void mouseExited(final MouseEvent me) {
            disarm();
        }

        @Override
        public void mousePressed(final MouseEvent me) {
            arm(me);
        }

        @Override
        public void mouseReleased(final MouseEvent me) {
            if (armed) {
                cancel();
            }

            disarm();
        }

        /**
         * Enable the arm flag and repaint
         * @param me mouse event
         */
        private void arm(final MouseEvent me) {
            armed = (isOverButton(me) && SwingUtilities.isLeftMouseButton(me));
            repaint();
        }

        /**
         * Disable the arm flag and repaint
         */
        private void disarm() {
            armed = false;
            repaint();
        }
    }

    /**
     * Replaces the entered text with a gray placeholder string when the
     * search field doesn't have the focus. The entered text returns when
     * we get the focus back.
     */
    private final class PlaceholderText implements FocusListener {

        /** color used when the field has the focus */
        private Color previousColor;

        /**
         * Constructor
         */
        PlaceholderText() {
            // get initial text and colors:
            focusLost(null);
        }

        public void focusGained(final FocusEvent fe) {
            setForeground(this.previousColor);
            setText(previousText);
            showingPlaceholderText = false;
        }

        public void focusLost(final FocusEvent fe) {
            previousText = getRealText();
            this.previousColor = getForeground();

            // if the field is empty :
            if (previousText.length() == 0) {
                showingPlaceholderText = true;
                setForeground(Color.GRAY);
                setText(placeholderText);
            }
        }
    }
}
/*___oOo___*/
