/*
 * ReportDialog.java
 *
 * Created on 6 septembre 2006, 13:52
 */
package jmmc.mcs.gui;

import jmmc.mcs.log.MCSLogger;

import java.awt.Dimension;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.logging.*;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;


/**
 *
 * @author  mella
 */
public class ReportDialog extends javax.swing.JDialog
{
    /**
     * DOCUMENT ME!
     */
    protected static Logger _logger = MCSLogger.getLogger();

    /**
     * DOCUMENT ME!
     */
    protected static String defaultEmail = "";

    /**
     * DOCUMENT ME!
     */
    protected static String defaultComment = "";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton       cancelButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextArea commentTextArea;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextArea detailMsgTextArea;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel detailPanel;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel detailPanel1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JScrollPane detailsScrollPane;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextField emailTextField;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextField errorMsgTextField;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel infoPanel;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton jButton1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel2;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel3;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel4;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel5;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel6;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel7;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel2;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel3;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel4;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel5;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel6;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JScrollPane jScrollPane1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JToggleButton jToggleButton1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JToggleButton jToggleButton2;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JToggleButton jToggleButton3;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JToggleButton jToggleButton4;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JToggleButton jToggleButton5;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel reportPanel;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton sendBugReportButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel subReportPanel;

    /** Creates new form ErrorDialog */
    public ReportDialog(java.awt.Frame parent, boolean modal, String errorMsg,
        String detailMsg)
    {
        super(parent, modal);
        myInitComponents();
        init(errorMsg, detailMsg);
    }

    /** Creates new form ErrorDialog  with exception information */
    public ReportDialog(java.awt.Frame parent, boolean modal, Exception e)
    {
        super(parent, modal);

        myInitComponents();

        // Get stackTrace
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        init(e.getMessage(), sw.toString());
    }

    /**
     * Add some pre and post UI addons
     */
    private void myInitComponents()
    {
        Dimension  minSize  = new Dimension(500, 1);
        Dimension  prefSize = new Dimension(500, 1);
        Dimension  maxSize  = new Dimension(Short.MAX_VALUE, 1);
        Box.Filler filler   = new Box.Filler(minSize, prefSize, maxSize);
        filler.setAlignmentX(0);
        getContentPane().add(filler);
        initComponents();
        getContentPane().add(Box.createVerticalGlue());
    }

    /**
     * DOCUMENT ME!
     *
     * @param errorMsg DOCUMENT ME!
     * @param detailMsg DOCUMENT ME!
     */
    private void init(String errorMsg, String detailMsg)
    {
        MCSLogger.trace();

        _logger.warning(errorMsg);
        errorMsgTextField.setText(errorMsg);
        detailMsgTextArea.setText(detailMsg);
        emailTextField.setText(defaultEmail);
        commentTextArea.setText(defaultComment);
        detailsScrollPane.setVisible(false);
        sendBugReportButton.setVisible(false);
        subReportPanel.setVisible(false);
        this.setLocationRelativeTo(null);
        pack();
    }

    /**
     * DOCUMENT ME!
     */
    public void sendBugReport()
    {
        MCSLogger.trace();

        _logger.info("To Be Implemented: Send Bug report with " + "\nemail=" +
            emailTextField.getText() + "\ncomment=" +
            commentTextArea.getText() + "\nerrorMsg=" +
            errorMsgTextField.getText() + "\nStack=" +
            detailMsgTextArea.getText());
        dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @param email DOCUMENT ME!
     */
    public static void setDefaultEmail(String email)
    {
        MCSLogger.trace();
        System.out.println("email :" + email);
        defaultEmail = email;
    }

    /**
     * DOCUMENT ME!
     *
     * @param comment DOCUMENT ME!
     */
    public static void setDefaultComment(String comment)
    {
        MCSLogger.trace();

        defaultComment = comment;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        infoPanel               = new javax.swing.JPanel();
        jButton1                = new javax.swing.JButton();
        errorMsgTextField       = new javax.swing.JTextField();
        detailPanel             = new javax.swing.JPanel();
        jPanel1                 = new javax.swing.JPanel();
        jToggleButton1          = new javax.swing.JToggleButton();
        jLabel1                 = new javax.swing.JLabel();
        detailsScrollPane       = new javax.swing.JScrollPane();
        detailMsgTextArea       = new javax.swing.JTextArea();
        reportPanel             = new javax.swing.JPanel();
        jPanel5                 = new javax.swing.JPanel();
        jToggleButton2          = new javax.swing.JToggleButton();
        jLabel2                 = new javax.swing.JLabel();
        subReportPanel          = new javax.swing.JPanel();
        jLabel7                 = new javax.swing.JLabel();
        emailTextField          = new javax.swing.JTextField();
        jLabel6                 = new javax.swing.JLabel();
        jScrollPane1            = new javax.swing.JScrollPane();
        commentTextArea         = new javax.swing.JTextArea();
        jPanel6                 = new javax.swing.JPanel();
        cancelButton            = new javax.swing.JButton();
        sendBugReportButton     = new javax.swing.JButton();

        getContentPane()
            .setLayout(new javax.swing.BoxLayout(getContentPane(),
                javax.swing.BoxLayout.Y_AXIS));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Error occured...");
        setResizable(false);
        infoPanel.setLayout(new javax.swing.BoxLayout(infoPanel,
                javax.swing.BoxLayout.X_AXIS));

        infoPanel.setAlignmentX(0.0F);
        jButton1.setIcon(new javax.swing.ImageIcon(getClass()
                                                       .getResource("/jmmc/mcs/gui/bug_error.png")));
        jButton1.setBorder(null);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton1.setPreferredSize(new java.awt.Dimension(32, 32));
        infoPanel.add(jButton1);

        errorMsgTextField.setEditable(false);
        errorMsgTextField.setText("jTextField1");
        errorMsgTextField.setFocusable(false);
        infoPanel.add(errorMsgTextField);

        getContentPane().add(infoPanel);

        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel,
                javax.swing.BoxLayout.Y_AXIS));

        detailPanel.setAlignmentX(0.0F);
        detailPanel.setAlignmentY(0.0F);
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1,
                javax.swing.BoxLayout.X_AXIS));

        jPanel1.setAlignmentX(0.0F);
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass()
                                                             .getResource("/jmmc/mcs/gui/rightarrow.png")));
        jToggleButton1.setBorder(null);
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jToggleButton1.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/jmmc/mcs/gui/downarrow.png")));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    jToggleButton1ActionPerformed(evt);
                }
            });

        jPanel1.add(jToggleButton1);

        jLabel1.setText("detailled error stack");
        jLabel1.setAlignmentX(0.5F);
        jPanel1.add(jLabel1);

        detailPanel.add(jPanel1);

        detailsScrollPane.setAlignmentX(0.0F);
        detailsScrollPane.setMaximumSize(new java.awt.Dimension(680, 480));
        detailsScrollPane.setMinimumSize(new java.awt.Dimension(131, 145));
        detailsScrollPane.setOpaque(false);
        detailsScrollPane.setPreferredSize(new java.awt.Dimension(500, 150));
        detailMsgTextArea.setBackground(javax.swing.UIManager.getDefaults()
                                                             .getColor("Button.background"));
        detailMsgTextArea.setColumns(20);
        detailMsgTextArea.setRows(5);
        detailMsgTextArea.setMaximumSize(null);
        detailsScrollPane.setViewportView(detailMsgTextArea);

        detailPanel.add(detailsScrollPane);

        getContentPane().add(detailPanel);

        reportPanel.setLayout(new javax.swing.BoxLayout(reportPanel,
                javax.swing.BoxLayout.Y_AXIS));

        reportPanel.setAlignmentX(0.0F);
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5,
                javax.swing.BoxLayout.X_AXIS));

        jPanel5.setAlignmentX(0.0F);
        jToggleButton2.setIcon(new javax.swing.ImageIcon(getClass()
                                                             .getResource("/jmmc/mcs/gui/rightarrow.png")));
        jToggleButton2.setBorder(null);
        jToggleButton2.setBorderPainted(false);
        jToggleButton2.setContentAreaFilled(false);
        jToggleButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jToggleButton2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jToggleButton2.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/jmmc/mcs/gui/downarrow.png")));
        jToggleButton2.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    jToggleButton2ActionPerformed(evt);
                }
            });

        jPanel5.add(jToggleButton2);

        jLabel2.setText("report form");
        jPanel5.add(jLabel2);

        reportPanel.add(jPanel5);

        subReportPanel.setLayout(new javax.swing.BoxLayout(subReportPanel,
                javax.swing.BoxLayout.Y_AXIS));

        subReportPanel.setAlignmentX(0.0F);
        jLabel7.setText("Email:");
        subReportPanel.add(jLabel7);

        emailTextField.setAlignmentX(0.0F);
        subReportPanel.add(emailTextField);

        jLabel6.setText("Comments:");
        subReportPanel.add(jLabel6);

        jScrollPane1.setAlignmentX(0.0F);
        commentTextArea.setColumns(20);
        commentTextArea.setRows(5);
        jScrollPane1.setViewportView(commentTextArea);

        subReportPanel.add(jScrollPane1);

        reportPanel.add(subReportPanel);

        getContentPane().add(reportPanel);

        jPanel6.setAlignmentX(0.0F);
        cancelButton.setText("Cancel");
        cancelButton.setAlignmentX(0.5F);
        cancelButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    cancelButtonActionPerformed(evt);
                }
            });

        jPanel6.add(cancelButton);

        sendBugReportButton.setText("Send Bug Report");
        sendBugReportButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    sendBugReportButtonActionPerformed(evt);
                }
            });

        jPanel6.add(sendBugReportButton);

        getContentPane().add(jPanel6);
    }

    // </editor-fold>//GEN-END:initComponents
    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void sendBugReportButtonActionPerformed(
        java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_sendBugReportButtonActionPerformed
        sendBugReport();
    } //GEN-LAST:event_sendBugReportButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    } //GEN-LAST:event_cancelButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_jToggleButton2ActionPerformed

        boolean flag = jToggleButton2.isSelected();
        subReportPanel.setVisible(flag);
        sendBugReportButton.setVisible(flag);
        pack();
        pack();
    } //GEN-LAST:event_jToggleButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_jToggleButton1ActionPerformed

        boolean flag = jToggleButton1.isSelected();
        detailsScrollPane.setVisible(flag);
        pack();
        pack();
    } //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    ReportDialog.setDefaultComment(
                        "this really is a good comment");
                    ReportDialog.setDefaultEmail("t@ti");

                    try
                    {
                        java.io.File f = new java.io.File("fdljkfdlhsdqfjh cqs");
                        f.getCanonicalPath();
                    }
                    catch (Exception e)
                    {
                        new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
                    }
                }
            });
    }

    // End of variables declaration//GEN-END:variables
}
