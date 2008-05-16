/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: FeedbackReport.java,v 1.3 2008-05-16 13:01:34 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2008/04/24 15:55:57  mella
 * Added applicationDataModel to constructor.
 *
 * Revision 1.1  2008/04/22 09:15:56  bcolucci
 * Created FeedbackReport.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.awt.*;
import java.awt.event.*;

import java.lang.Thread;

import java.net.URL;

import java.util.logging.*;

import javax.swing.*;


/** View of feedback report box */
public class FeedbackReport extends JFrame
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(FeedbackReport.class.getName());

    /** Load bar */
    private static JProgressBar _loadBar = new JProgressBar();

    /** Kind of error/bug */
    private static JComboBox _typeComboBox = new JComboBox();

    /** User bug description */
    private static JTextArea _description = new JTextArea();

    /** User mail */
    private static JTextField _mail = new JTextField();

    /** Cancel button */
    private static JButton _cancelButton = new JButton();

    /** Submit button */
    private static JButton _submitButton = new JButton();

    /** Model of the feedback report box */
    private FeedbackReportModel _feedbackReportModel = null;

    /** Mail and description split */
    private JSplitPane _mailAndDescriptionSplit = new JSplitPane();

    /** Mail and space split */
    private JSplitPane _mailAndSpaceSplit = new JSplitPane();

    /** Mail and type split */
    private JSplitPane _mailAndTypeSplit = new JSplitPane();

    /** Mail label */
    private JLabel _mailLabel = new JLabel();

    /** Mail split */
    private JSplitPane _mailSplit = new JSplitPane();

    /** Space and mail split */
    private JSplitPane _spaceAndMailSplit = new JSplitPane();

    /** Type label */
    private JLabel _typeLabel = new JLabel();

    /** Type panel */
    private JPanel _typePanel = new JPanel();

    /** Type split */
    private JSplitPane _typeSplit = new JSplitPane();

    /** Description and buttons split */
    private JSplitPane _descriptionAndButtonsSplit = new JSplitPane();

    /** Description label */
    private JLabel _descriptionLabel = new JLabel();

    /** Description pane */
    private JScrollPane _descriptionPane = new JScrollPane();

    /** Description split */
    private JSplitPane _descriptionSplit = new JSplitPane();

    /** Cancel and Submit buttons panel */
    private JPanel _buttonsPanel = new JPanel();

    /** Constructor */
    public FeedbackReport()
    {
        ApplicationDataModel applicationDataModel = App.getSharedApplicationDataModel();

        if (applicationDataModel != null)
        {
            _feedbackReportModel = new FeedbackReportModel(applicationDataModel,
                    this);
            _feedbackReportModel.start();

            // Draw the widgets
            setSplitsProperties();
            setMailProperties();
            setTypeProperties();
            setDescriptionProperties();
            setButtonsProperties();
            setFrameProperties();
            _logger.fine("All feedback report properties have been set");
        }
    }

    /** Set frame properties */
    private void setFrameProperties()
    {
        // Finish window configuration and draw it
        getContentPane().add(_mailAndTypeSplit, BorderLayout.CENTER);
        setTitle("Feedback Report");
        setResizable(false);
        pack();
        setVisible(true);
        WindowCenterer.centerOnMainScreen(this);
        _logger.fine("Frame properties have been set");
    }

    /** Draw mail-related widgets */
    private void setMailProperties()
    {
        _mailSplit.setBorder(null);
        _mailSplit.setDividerLocation(50);
        _mailSplit.setDividerSize(0);

        _mailLabel.setText("Mail :");
        _mailSplit.setLeftComponent(_mailLabel);
        _mailSplit.setRightComponent(_mail);
        _logger.fine("Mail properties have been set");
    }

    /** Draw report type-related widgets */
    private void setTypeProperties()
    {
        _typePanel.setLayout(new BorderLayout());

        _typeComboBox.setModel(_feedbackReportModel.getTypeDataModel());
        _typePanel.add(_typeComboBox, BorderLayout.LINE_START);

        _typeSplit.setBorder(null);
        _typeSplit.setDividerLocation(50);
        _typeSplit.setDividerSize(0);
        _typeSplit.setEnabled(false);

        _typeLabel.setText("Type :");
        _typeSplit.setLeftComponent(_typeLabel);

        _typeSplit.setRightComponent(_typePanel);
        _logger.fine("Type properties have been set");
    }

    /** Draw description-related widgets */
    private void setDescriptionProperties()
    {
        _description.setColumns(40);
        _description.setLineWrap(true);
        _description.setRows(15);
        _descriptionPane.setViewportView(_description);
        _descriptionSplit.setBorder(null);
        _descriptionSplit.setDividerSize(0);
        _descriptionSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
        _descriptionSplit.setEnabled(false);

        _descriptionLabel.setText("Description :");
        _descriptionSplit.setRightComponent(_descriptionLabel);

        _descriptionSplit.setTopComponent(new JLabel(" "));
        _logger.fine("Description properties have been set");
    }

    /** Set Cancel and Submit buttons properties and actions */
    private void setButtonsProperties()
    {
        _buttonsPanel.setLayout(new BorderLayout());

        _cancelButton.setText("Cancel");
        // Actions performed when there was a click on "Cancel" button
        _cancelButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    _feedbackReportModel.stop();
                    dispose();
                }
            });
        _buttonsPanel.add(_cancelButton, BorderLayout.LINE_START);

        _submitButton.setText("Submit");
        // Actions performed when there was a click on "Submit" button
        _submitButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    activateLoadBarProperties();

                    // Sends report
                    _feedbackReportModel.setReadyToSend(true);

                    _submitButton.setEnabled(false);
                }
            });
        _buttonsPanel.add(_submitButton, BorderLayout.LINE_END);
    }

    /** Set split properties */
    private void setSplitsProperties()
    {
        _mailAndTypeSplit.setBorder(null);
        _mailAndTypeSplit.setDividerSize(0);
        _mailAndTypeSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _descriptionAndButtonsSplit.setBorder(null);
        _descriptionAndButtonsSplit.setDividerSize(0);
        _descriptionAndButtonsSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _descriptionAndButtonsSplit.setLeftComponent(_descriptionPane);
        _descriptionAndButtonsSplit.setRightComponent(_buttonsPanel);

        _mailAndTypeSplit.setBottomComponent(_descriptionAndButtonsSplit);

        _mailAndDescriptionSplit.setBorder(null);
        _mailAndDescriptionSplit.setDividerSize(0);
        _mailAndDescriptionSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _spaceAndMailSplit.setBorder(null);
        _spaceAndMailSplit.setDividerSize(0);
        _spaceAndMailSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _spaceAndMailSplit.setBottomComponent(_typeSplit);

        _mailAndSpaceSplit.setBorder(null);
        _mailAndSpaceSplit.setDividerSize(0);
        _mailAndSpaceSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

        _mailAndSpaceSplit.setTopComponent(new JLabel(" "));
        _mailAndSpaceSplit.setRightComponent(_mailSplit);

        _spaceAndMailSplit.setLeftComponent(_mailAndSpaceSplit);

        _mailAndDescriptionSplit.setTopComponent(_spaceAndMailSplit);
        _mailAndDescriptionSplit.setRightComponent(_descriptionSplit);

        _mailAndTypeSplit.setLeftComponent(_mailAndDescriptionSplit);
        _logger.fine("Splits properties have been set");
    }

    /** Set load bar properties */
    private void activateLoadBarProperties()
    {
        _submitButton.setText("Submit");
        _loadBar.setStringPainted(true);
        _loadBar.setIndeterminate(true);
        _loadBar.setString("Sending report...");

        _buttonsPanel.add(_loadBar, BorderLayout.CENTER);
        _buttonsPanel.revalidate();
    }

    /** Update progress bar according to report sending completion state */
    public void setReportSend(boolean succes)
    {
        _cancelButton.setText("Close");
        _loadBar.setIndeterminate(false);

        if (succes == true)
        {
            _loadBar.setString("Thank you for your feedback.");
        }
        else
        {
            _loadBar.setString("Error during report sending.");
            _submitButton.setEnabled(true);
        }
    }

    /**
     * Return the mail value
     *
     * @return mail value
     */
    public static String getMail()
    {
        return _mail.getText();
    }

    /**
     * Return the default combo box model
     *
     * @return default combo box model
     */
    public static DefaultComboBoxModel getDefaultComboBoxModel()
    {
        return (DefaultComboBoxModel) _typeComboBox.getModel();
    }

    /**
     * Return the description value
     *
     * @return description value
     */
    public static String getDescription()
    {
        return _description.getText();
    }
}
/*___oOo___*/
