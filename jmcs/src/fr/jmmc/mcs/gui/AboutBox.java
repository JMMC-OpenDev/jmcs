/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: AboutBox.java,v 1.1 2008-04-16 14:15:27 fgalland Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import edu.stanford.ejalbert.BrowserLauncher;

import java.awt.*;
import java.awt.event.*;

import java.net.*;

import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 * Open a new About window with informations
 * from XML file which should be named "ApplicationData.xml" in src folder
 */
public class AboutBox extends JFrame implements HyperlinkListener
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(AboutBox.class.getName());

    /** Model of the about box */
    private ApplicationDataModel _applicationDataModel = null;

    /** Logo label */
    private JLabel _logoLabel = new JLabel();

    /** Copyright label */
    private JLabel _copyrightLabel = new JLabel();

    /** Container of the textarea */
    private JScrollPane _textareaContainer = new JScrollPane();

    /** Textarea (html) */
    private JEditorPane _textarea = new JEditorPane();

    /** Label of compilation info (date and compilator) */
    private JLabel _compilationInfoLabel = new JLabel();

    /** Link label */
    JEditorPane _linkLabel = new JEditorPane();

    /** Label of program info (name and version) */
    private JLabel _programInfoLabel = new JLabel();

    /** Help : jframe design
     *
     *                contentPane()------------------------------------------------------------|
     *                | _logoLabel                                                             |
     *                | _textareaSplit--------------------------------------------------------||
     *                | | _upSpaceSplit------------------------------------------------------|||
     *                | | | space                                                            |||
     *                | | | _downSpaceSplit-------------------------------------------------||||
     *                | | | | _compilationInfoSplit----------------------------------------|||||
     *                | | | | | _linkAndProgramSplit--------------------------------------||||||
     *                | | | | | | _linkLabel                                              ||||||
     *                | | | | | | _programInfoLabel                                       ||||||
     *                | | | | | -----------------------------------------------------------|||||
     *                | | | | | _compilationInfoLabel                                      |||||
     *                | | | | --------------------------------------------------------------||||
     *                | | | | space                                                         ||||
     *                | | | -----------------------------------------------------------------|||
     *                | | --------------------------------------------------------------------||
     *                | | _textareaContainer                                                  ||
     *                | -----------------------------------------------------------------------|
     *                | _copyrightLabel                                                        |
     *                --------------------------------------------------------------------------
     */

    /** Split with _link and program */
    private JSplitPane _linkAndProgramSplit = new JSplitPane();

    /** Split with (link and program split) and compilation */
    private JSplitPane _compilationInfoSplit = new JSplitPane();

    /** Split with ((link and program split) and compilation) and space */
    private JSplitPane _downSpaceSplit = new JSplitPane();

    /** Split with (((link and program split) and compilation) and space) and space */
    private JSplitPane _upSpaceSplit = new JSplitPane();

    /** Split with ((((link and program split) and compilation)
     * and space) and space) and textarea container
     */
    private JSplitPane _textareaSplit = new JSplitPane();

    /** Constructor */
    public AboutBox()
    {
        try
        {
            // Instantiate ApplicationDataModel object
            _applicationDataModel = new ApplicationDataModel();

            // Launch all methods which set properties of components
            setAllTheProperties();
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE, "Cannot instantiate AboutBox object", ex);
        }
    }

    /**
     * Launch all "set properties" methods.
     *
     * So this method should initialize all of the jframe components
     */
    private void setAllTheProperties()
    {
        // Components
        setLogoLabelProperties();
        setProgramInfoLabelProperties();
        setLinkLabelProperties();
        setCompilationInfoLabelProperties();
        setTextareaProperties();
        setCopyrightLabelProperties();

        // JFrame design
        setTextareaSplitProperties();
        setUpSpaceSplitProperties();
        setDownSpaceSplitProperties();
        setCompilationInfoSplitProperties();
        setLinkAndProgramSplitProperties();

        // Main frame
        setFrameProperties();

        _logger.fine("All of the properties of the frame have been initialized");
    }

    /** Sets logo label properties */
    private void setLogoLabelProperties()
    {
        try
        {
            // Center label content
            _logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Create the Icon with the image which should be named logo.jpg in src folder
            String    logoURL = _applicationDataModel.getLogoURL();
            ImageIcon logo    = new ImageIcon(getClass().getResource(logoURL));
            _logoLabel.setIcon(logo);

            _logger.fine(
                "All of the logo label properties have been initialized");

            // Launch the default browser with the given link
            _logoLabel.addMouseListener(new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent evt)
                    {
                        try
                        {
                            BrowserLauncher launcher       = new BrowserLauncher();
                            String          mainWebPageURL = _applicationDataModel.getMainWebPageURL();
                            launcher.openURLinBrowser(mainWebPageURL);
                        }
                        catch (Exception ex)
                        {
                            _logger.log(Level.WARNING,
                                "Cannot launch web browser", ex);
                        }
                    }
                });

            _logger.fine("Mouse click event placed on logo label");

            // Show hand cursor when mouse is moving on logo
            _logoLabel.addMouseMotionListener(new MouseMotionAdapter()
                {
                    public void mouseMoved(MouseEvent evt)
                    {
                        try
                        {
                            _logoLabel.setCursor(Cursor.getPredefinedCursor(
                                    Cursor.HAND_CURSOR));
                        }
                        catch (Exception ex)
                        {
                            _logger.log(Level.WARNING, "Cannot change cursor",
                                ex);
                        }
                    }
                });

            _logger.fine("Mouse move event placed on logo label");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set logo label properties", ex);
        }
    }

    /** Sets copyright label properties */
    private void setCopyrightLabelProperties()
    {
        try
        {
            _copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
            _copyrightLabel.setText(_applicationDataModel.getCopyrightValue());
            _logger.fine(
                "All of the copyright label properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set copyright label properties",
                ex);
        }
    }

    /** Sets textarea (SWING html) properties */
    private void setTextareaProperties()
    {
        try
        {
            _textarea.setEditable(false);
            _textarea.setMargin(new Insets(5, 5, 5, 5));
            _textarea.setContentType("text/html");
            _textarea.addHyperlinkListener(this);

            // The textarea should have the same width than the logo
            Dimension textareaDimension = new Dimension(_logoLabel.getWidth(),
                    170);
            _textarea.setPreferredSize(textareaDimension);
            _logger.fine("All of the textarea properties have been initialized");

            // HTML generation
            String generatedHtml = "<html><head></head><body>" +
                _applicationDataModel.getTextValue() + "<br><br>";

            // Generate a HTML string with each package informations
            String[] packagesInfo = _applicationDataModel.getPackagesInfo();
            String   packageHtml  = "";

            for (int i = 0; i < packagesInfo.length; i++)
            {
                packageHtml += (packagesInfo[i] + "<br>");
            }

            generatedHtml += (packageHtml + "</body></html>");

            _textarea.setText(generatedHtml);
            _logger.fine("The text of textarea has been inserted");

            _textareaContainer.setViewportView(_textarea);
            _logger.fine("textarea imported in textarea container");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set textarea properties", ex);
        }
    }

    /**
     * Handle URL link clicked in the JEditorPane.
     *
     * @param event the received event.
     */
    public void hyperlinkUpdate(HyperlinkEvent event)
    {
        // When a link is clicked
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            String clickedURL = event.getURL().toExternalForm();

            try
            {
                // Launch the URL in the default browser
                _logger.fine("Launch '" + clickedURL +
                    "' in the default browser");

                BrowserLauncher launcher = new BrowserLauncher();
                launcher.openURLinBrowser(clickedURL);
            }
            catch (Exception ex)
            {
                _logger.log(Level.WARNING,
                    "Cannot launch '" + clickedURL + "' web browser", ex);
            }
        }
    }

    /** Sets link label properties */
    private void setLinkLabelProperties()
    {
        try
        {
            _linkLabel.setEditable(false);
            _linkLabel.setOpaque(false);
            _linkLabel.addHyperlinkListener(this);
            _linkLabel.setContentType("text/html");

            String link     = _applicationDataModel.getLinkValue();
            String linkHTML = "<a href='" + link + "'>" + link + "</a>";

            _linkLabel.setText("<html><head></head><body><center>" + linkHTML +
                "</center></body></html>");

            _logger.fine(
                "All of the link label properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set link label properties", ex);
        }
    }

    /** Set link and program split properties */
    private void setLinkAndProgramSplitProperties()
    {
        /*
         * _linkAndProgramSplit-------------------
         * | _linkLabel                          |
         * |-------------------------------------|
         * | _programInfoLabel                   |
         * |-------------------------------------|
         */
        try
        {
            _linkAndProgramSplit.setBorder(null);
            _linkAndProgramSplit.setDividerSize(0);
            _linkAndProgramSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
            _linkAndProgramSplit.setRightComponent(_linkLabel);
            _linkAndProgramSplit.setLeftComponent(_programInfoLabel);

            _logger.fine(
                "All of the link and program split properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot set link and program split properties", ex);
        }
    }

    /** Set compilation info split properties */
    private void setCompilationInfoSplitProperties()
    {
        /*
         * _compilationInfoSplit------------------
         * | _linkAndProgramSplit                |
         * |-------------------------------------|
         * | _compilationInfoLabel               |
         * |-------------------------------------|
         */
        try
        {
            _compilationInfoSplit.setBorder(null);
            _compilationInfoSplit.setDividerSize(0);
            _compilationInfoSplit.setDividerLocation(-1);
            _compilationInfoSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
            _compilationInfoSplit.setTopComponent(_linkAndProgramSplit);
            _compilationInfoSplit.setRightComponent(_compilationInfoLabel);

            _logger.fine(
                "All of the compilation info split properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot set compilation info split properties", ex);
        }
    }

    /** Set down space split properties */
    private void setDownSpaceSplitProperties()
    {
        /*
         * _downSpaceSplit------------------------
         * | _compilationInfoSplit               |
         * |-------------------------------------|
         * | space                               |
         * |-------------------------------------|
         */
        try
        {
            _downSpaceSplit.setBorder(null);
            _downSpaceSplit.setDividerSize(0);
            _downSpaceSplit.setDividerLocation(-1);
            _downSpaceSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
            _downSpaceSplit.setTopComponent(_compilationInfoSplit);
            _downSpaceSplit.setRightComponent(new JLabel(" "));

            _logger.fine(
                "All of the down space split properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot set down space split properties", ex);
        }
    }

    /** Set up space split properties */
    private void setUpSpaceSplitProperties()
    {
        /*
         * _upSpaceSplit--------------------------
         * | space                               |
         * |-------------------------------------|
         * | _downSpaceSplit                     |
         * |-------------------------------------|
         */
        try
        {
            _upSpaceSplit.setBorder(null);
            _upSpaceSplit.setDividerSize(0);
            _upSpaceSplit.setDividerLocation(-1);
            _upSpaceSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
            _upSpaceSplit.setTopComponent(new JLabel(" "));
            _upSpaceSplit.setRightComponent(_downSpaceSplit);

            _logger.fine(
                "All of the up space split properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set up space split properties",
                ex);
        }
    }

    /** Set textarea split properties */
    private void setTextareaSplitProperties()
    {
        /*
         * _textareaSplit-------------------------
         * | _upSpaceSplit                       |
         * |-------------------------------------|
         * | _textareaContainer                  |
         * |-------------------------------------|
         */
        try
        {
            _textareaSplit.setBorder(null);
            _textareaSplit.setDividerSize(0);
            _textareaSplit.setDividerLocation(-1);
            _textareaSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
            _textareaSplit.setTopComponent(_upSpaceSplit);
            _textareaSplit.setRightComponent(_textareaContainer);

            _logger.fine(
                "All of the textarea split properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot set textarea split properties",
                ex);
        }
    }

    /** Sets program info label properties */
    private void setProgramInfoLabelProperties()
    {
        try
        {
            String name    = _applicationDataModel.getProgramName();
            String version = _applicationDataModel.getProgramVersion();
            String pInfo   = name + " v" + version;

            _programInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            _programInfoLabel.setText(pInfo);

            _logger.fine(
                "All of the program info label properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot set program info label properties", ex);
        }
    }

    /** Sets compilation info label properties */
    private void setCompilationInfoLabelProperties()
    {
        try
        {
            String date              = _applicationDataModel.getCompilationDate();
            String compilatorVersion = _applicationDataModel.getCompilatorVersion();
            String cInfo             = "Build the " + date + " with " +
                compilatorVersion;

            _compilationInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            _compilationInfoLabel.setText(cInfo);

            _logger.fine(
                "All of the compilation info label properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot set compilation info label properties", ex);
        }
    }

    /** Sets frame properties and make the window
     * visible
     */
    private void setFrameProperties()
    {
        /*
         * getContentPane()-----------------------
         * | _logoLabel                          |
         * |-------------------------------------|
         * | _textareaSplit                      |
         * |-------------------------------------|
         * | _copyrightLabel                     |
         * |-------------------------------------|
         */
        try
        {
            String programName = _applicationDataModel.getProgramName();
            String title       = "About " + programName + "...";

            getContentPane().add(_logoLabel, BorderLayout.PAGE_START);
            getContentPane().add(_textareaSplit, BorderLayout.CENTER);
            getContentPane().add(_copyrightLabel, BorderLayout.PAGE_END);
            setResizable(false);
            setLocationRelativeTo(null);
            setTitle(title);
            pack();
            WindowCenterer.centerOnMainScreen(this);

            _logger.fine("All of the frame properties have been initialized");
        }
        catch (Exception ex)
        {
            _logger.log(Level.SEVERE, "Cannot set frame properties", ex);
        }
    }
}
/*___oOo___*/
