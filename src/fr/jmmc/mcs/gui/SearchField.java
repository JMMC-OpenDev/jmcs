/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SearchField.java,v 1.2 2009-10-08 08:26:47 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2009/10/07 15:59:17  lafrasse
 * First release.
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro.star;

import org.apache.commons.lang.SystemUtils;

import java.awt.*;
import java.awt.event.*;

import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 * Store informations relative to a star.
 */
public class StarResolverWidget extends SearchField
{
    /** Logger - register on fr.jmmc to collect all logs under this path */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.StarResolverWidget");

    /**
     * Main.
     */
    public static void main(String[] args)
    {
        // Context initialization
        JFrame frame = new JFrame();
        frame.setTitle("StarResolverWidget Demo");

        Container   container   = frame.getContentPane();
        JPanel      panel       = new JPanel();
        SearchField searchField = new SearchField("Simbad");
        searchField.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String starName = e.getActionCommand();

                    if (starName.length() > 0)
                    {
                        System.out.println("Searching for '" + starName + "'.");
                    }
                }
            });
        panel.add(searchField);
        container.add(panel);
        panel.setVisible(true);

        frame.pack();
        frame.setVisible(true);
    }
}


/**
 * A text field for search/filter interfaces. The extra functionality includes
 * a placeholder string (when the user hasn't yet typed anything), and a button
 * to clear the currently-entered text.
 *
 * @author Elliott Hughes
 *
 * @todo : add a menu of recent searches.
 * @todo : make recent searches persistent.
 * @todo : use rounded corners, at least on Mac OS X.
 */
class SearchField extends JTextField
{
    /**
     * DOCUMENT ME!
     */
    private static final Border CANCEL_BORDER = new CancelBorder();

    /**
     * DOCUMENT ME!
     */
    private boolean sendsNotificationForEachKeystroke = false;

    /**
     * DOCUMENT ME!
     */
    private boolean showingPlaceholderText = false;

    /**
     * DOCUMENT ME!
     */
    private boolean armed = false;

    /**
     * Creates a new SearchField object.
     *
     * @param placeholderText DOCUMENT ME!
     */
    public SearchField(String placeholderText)
    {
        super(8);
        addFocusListener(new PlaceholderText(placeholderText));
        initBorder();
        initKeyListener();
    }

    /**
     * Creates a new SearchField object.
     */
    public SearchField()
    {
        this("Search");
    }

    /**
     * DOCUMENT ME!
     */
    private void initBorder()
    {
        // On Mac OS X, use specific search textfield widget
        if (SystemUtils.IS_OS_MAC_OSX == true)
        {
            // http://developer.apple.com/mac/library/technotes/tn2007/tn2196.html#//apple_ref/doc/uid/DTS10004439
            putClientProperty("JTextField.variant", "search");
            putClientProperty("JTextField.FindAction",
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        postActionEvent();
                    }
                });
            putClientProperty("JTextField.CancelAction",
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        cancel();
                    }
                });

            return;
        }

        // Add an empty border around us to compensate for
        // the rounded corners.
        setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 14));

        // On other operating systems, build a custom widget
        setBorder(new CompoundBorder(getBorder(), CANCEL_BORDER));

        MouseInputListener mouseInputListener = new CancelListener();
        addMouseListener(mouseInputListener);
        addMouseMotionListener(mouseInputListener);

        // We must be non-opaque since we won't fill all pixels.
        // This will also stop the UI from filling our background.
        setOpaque(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    protected void paintComponent(Graphics g)
    {
        if (SystemUtils.IS_OS_MAC_OSX == false)
        {
            int width  = getWidth();
            int height = getHeight();

            // Paint a rounded rectangle in the background surrounded by a black line.
            g.setColor(Color.LIGHT_GRAY);
            g.fillRoundRect(0, 0, width, height, height, height);

            g.setColor(Color.GRAY);
            g.fillRoundRect(0, -1, width, height, height, height);

            g.setColor(getBackground());
            g.fillRoundRect(1, 1, width - 2, height - 2, height - 2, height -
                2);

            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(10, 1, width - 10, 1);
        }

        // Now call the superclass behavior to paint the foreground.
        super.paintComponent(g);
    }

    /**
     * DOCUMENT ME!
     */
    private void initKeyListener()
    {
        addKeyListener(new KeyAdapter()
            {
                public void keyReleased(KeyEvent e)
                {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    {
                        cancel();
                    }
                    else if (sendsNotificationForEachKeystroke)
                    {
                        maybeNotify();
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void cancel()
    {
        setText("");
        postActionEvent();
    }

    /**
     * DOCUMENT ME!
     */
    private void maybeNotify()
    {
        if (showingPlaceholderText)
        {
            return;
        }

        postActionEvent();
    }

    /**
     * DOCUMENT ME!
     *
     * @param eachKeystroke DOCUMENT ME!
     */
    public void setSendsNotificationForEachKeystroke(boolean eachKeystroke)
    {
        this.sendsNotificationForEachKeystroke = eachKeystroke;
    }

    /**
     * Draws the cancel button as a gray circle with a white cross inside.
     */
    static class CancelBorder extends EmptyBorder
    {
        private static final Color GRAY = new Color(0.7f, 0.7f, 0.7f);

        CancelBorder()
        {
            super(0, 0, 0, 8);
        }

        public void paintBorder(Component c, Graphics oldGraphics, int x,
            int y, int width, int height)
        {
            SearchField field = (SearchField) c;
            Color       color = GRAY;

            if (field.showingPlaceholderText ||
                    (field.getText().length() == 0))
            {
                return;
            }

            Graphics2D g = (Graphics2D) oldGraphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw shaded disk
            final int circleL = 14;
            final int circleX = (x + width) - circleL + 9;
            final int circleY = y + ((height - 1 - circleL) / 2);
            g.setColor(field.armed ? Color.GRAY : color);
            g.fillOval(circleX, circleY, circleL, circleL);

            // Draw white cross
            final int lineL = circleL - 8;
            final int lineX = circleX + 4;
            final int lineY = circleY + 4;
            g.setColor(Color.WHITE);
            g.drawLine(lineX, lineY, lineX + lineL, lineY + lineL);
            g.drawLine(lineX, lineY + lineL, lineX + lineL, lineY);
        }
    }

    /**
     * Handles a click on the cancel button by clearing the text and notifying
     * any ActionListeners.
     */
    class CancelListener extends MouseInputAdapter
    {
        private boolean isOverButton(MouseEvent e)
        {
            // If the button is down, we might be outside the component
            // without having had mouseExited invoked.
            if (contains(e.getPoint()) == false)
            {
                return false;
            }

            // In lieu of proper hit-testing for the circle, check that
            // the mouse is somewhere in the border.
            Rectangle innerArea = SwingUtilities.calculateInnerArea(SearchField.this,
                    null);

            return (innerArea.contains(e.getPoint()) == false);
        }

        public void mouseDragged(MouseEvent e)
        {
            arm(e);
        }

        public void mouseEntered(MouseEvent e)
        {
            arm(e);
        }

        public void mouseExited(MouseEvent e)
        {
            disarm();
        }

        public void mousePressed(MouseEvent e)
        {
            arm(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            if (armed)
            {
                cancel();
            }

            disarm();
        }

        private void arm(MouseEvent e)
        {
            armed = (isOverButton(e) && SwingUtilities.isLeftMouseButton(e));
            repaint();
        }

        private void disarm()
        {
            armed = false;
            repaint();
        }
    }

    /**
     * Replaces the entered text with a gray placeholder string when the
     * search field doesn't have the focus. The entered text returns when
     * we get the focus back.
     */
    class PlaceholderText implements FocusListener
    {
        private String placeholderText;
        private String previousText  = "";
        private Color  previousColor;

        PlaceholderText(String placeholderText)
        {
            this.placeholderText     = placeholderText;
            focusLost(null);
        }

        public void focusGained(FocusEvent e)
        {
            setForeground(previousColor);
            setText(previousText);
            showingPlaceholderText = false;
        }

        public void focusLost(FocusEvent e)
        {
            previousText      = getText();
            previousColor     = getForeground();

            if (previousText.length() == 0)
            {
                showingPlaceholderText = true;
                setForeground(Color.GRAY);
                setText(placeholderText);
            }
        }
    }
}
/*___oOo___*/
