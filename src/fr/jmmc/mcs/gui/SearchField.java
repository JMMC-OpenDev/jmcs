/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchField.java,v 1.5 2010-10-14 12:12:19 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2009/10/08 14:09:11  lafrasse
 * Moved form fr.jmmc.mcs.astro.star.StarResolverWidget .
 * Refined documentation and layout.
 *
 * Revision 1.3  2009/10/08 09:02:22  lafrasse
 * Added magnifying glass icon on the left.
 *
 * Revision 1.2  2009/10/08 08:26:47  lafrasse
 * Refined border color and anti-aliasing.
 *
 * Revision 1.1  2009/10/07 15:59:17  lafrasse
 * First release.
 *
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
 * @author Elliott Hughes
 *
 * @todo : add a menu of recent searches.
 * @todo : make recent searches persistent.
 */
public class SearchField extends JTextField {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** The border that draws the magnifying glass and the cancel cross */
    private static final Border CANCEL_BORDER = new CancelBorder();
    /** Store wether notifications should be sent every time a key is pressed */
    private boolean sendsNotificationForEachKeystroke = false;
    /** Store wether a text should be drawn when nothing else in textfield */
    private boolean showingPlaceholderText = false;
    /** store wether the cnacell cross is currently clicked */
    private boolean armed = false;

    /**
     * Creates a new SearchField object.
     *
     * @param placeholderText the text displayed when nothing in.
     */
    public SearchField(String placeholderText) {
        super(8); // 8 characters wide by default

        addFocusListener(new PlaceholderText(placeholderText));
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
        if (SystemUtils.IS_OS_MAC_OSX == true) {
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

        MouseInputListener mouseInputListener = new CancelListener();
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
    protected void paintComponent(Graphics g) {
        // On anything but Mac OS X
        if (SystemUtils.IS_OS_MAC_OSX == false) {
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
    public void setSendsNotificationForEachKeystroke(boolean eachKeystroke) {
        this.sendsNotificationForEachKeystroke = eachKeystroke;
    }

    /**
     * Draws the cancel button (a gray circle with a white cross) and the magnifying glass icon.
     */
    private final static class CancelBorder extends EmptyBorder {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        private static final Color DISARMED_GRAY = new Color(0.7f, 0.7f, 0.7f);

        CancelBorder() {
            super(0, 0, 0, 8);
        }

        @Override
        public void paintBorder(Component c, Graphics oldGraphics, int x,
                int y, int width, int height) {
            SearchField field = (SearchField) c;
            Color backgroundColor = field.getBackground();
            Graphics2D g = (Graphics2D) oldGraphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

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

            if (field.showingPlaceholderText
                    || (field.getText().length() == 0)) {
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

        private boolean isOverButton(MouseEvent e) {
            // If the button is down, we might be outside the component
            // without having had mouseExited invoked.
            if (contains(e.getPoint()) == false) {
                return false;
            }

            // In lieu of proper hit-testing for the circle, check that
            // the mouse is somewhere in the border.
            Rectangle innerArea = SwingUtilities.calculateInnerArea(SearchField.this,
                    null);

            return (innerArea.contains(e.getPoint()) == false);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            arm(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            arm(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            disarm();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            arm(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (armed) {
                cancel();
            }

            disarm();
        }

        private void arm(MouseEvent e) {
            armed = (isOverButton(e) && SwingUtilities.isLeftMouseButton(e));
            repaint();
        }

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

        private String placeholderText;
        private String previousText = "";
        private Color previousColor;

        PlaceholderText(String placeholderText) {
            this.placeholderText = placeholderText;
            focusLost(null);
        }

        public void focusGained(FocusEvent e) {
            setForeground(previousColor);
            setText(previousText);
            showingPlaceholderText = false;
        }

        public void focusLost(FocusEvent e) {
            previousText = getText();
            previousColor = getForeground();

            if (previousText.length() == 0) {
                showingPlaceholderText = true;
                setForeground(Color.GRAY);
                setText(placeholderText);
            }
        }
    }
}
/*___oOo___*/
