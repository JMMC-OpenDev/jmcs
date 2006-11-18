/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: HelpView.java,v 1.1 2006-11-18 23:13:06 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package jmmc.mcs.gui;

import jmmc.mcs.log.*;

import jmmc.mcs.util.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;


/**
 * About window.
 */
public class HelpView extends JFrame
{
    /** Tutorial action */
    public TutorialAction _tutorialAction;

    /** Tutorial action */
    public FAQAction _faqAction;

    /** Tutorial action */
    public HelpAction _helpAction;

    /** Tutorial action */
    public FeedbackAction _feedbackAction;

    // Panel
    /**
     * DOCUMENT ME!
     */
    JPanel _panel;

    /**
     * Constructor.
     */
    public HelpView()
    {
        _tutorialAction     = new TutorialAction();
        _faqAction          = new FAQAction();
        _helpAction         = new HelpAction();
        _feedbackAction     = new FeedbackAction();

        try
        {
            setTitle("Help");

            // Window size
            setSize(480, 360);
            setResizable(false);

            // Window screen position (centered)
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize  = getSize();
            setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

            _panel = new JPanel();
            _panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));

            Container contentPane = getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(_panel);

            // Set the GUI up
            pack();
            setVisible(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Called to show the tutorial.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class TutorialAction extends MCSAction
    {
        public TutorialAction()
        {
            super("tutorial");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _panel.removeAll();

            JLabel label = new JLabel("TutorialAction");
            _panel.add(label);
            setVisible(true);
        }
    }

    /**
     * Called to show the FAQ.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class FAQAction extends MCSAction
    {
        public FAQAction()
        {
            super("FAQ");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _panel.removeAll();

            JLabel label = new JLabel("FAQAction");
            _panel.add(label);
            setVisible(true);
        }
    }

    /**
     * Called to show the help.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class HelpAction extends MCSAction
    {
        public HelpAction()
        {
            super("help");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _panel.removeAll();

            JLabel label = new JLabel("HelpAction");
            _panel.add(label);
            setVisible(true);
        }
    }

    /**
     * Called to show the feedback.
     *
     * @throws java.lang.Exception << TODO a mettre !!!
     */
    protected class FeedbackAction extends MCSAction
    {
        public FeedbackAction()
        {
            super("feedback");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            MCSLogger.trace();

            _panel.removeAll();

            JLabel label = new JLabel("FeedbackAction");
            _panel.add(label);
            setVisible(true);
        }
    }
}
/*___oOo___*/
