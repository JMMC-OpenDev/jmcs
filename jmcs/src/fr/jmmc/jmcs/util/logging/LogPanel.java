/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.logging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This panel displays both log content and refresh buttons
 * @author bourgesl
 */
public class LogPanel extends javax.swing.JPanel implements ActionListener, ChangeListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /** default auto refresh period = 1 second */
    private static final int REFRESH_PERIOD = 1000;
    /** Class logger */
    private static final Logger _logger = LoggerFactory.getLogger(LogPanel.class.getName());
    /** double formatter for auto refresh period */
    private final static NumberFormat df1 = new DecimalFormat("0.0");

    /* members */
    /** logger path */
    private final String loggerPath;
    /** log buffer byte count */
    private int logByteCount = 0;
    /* Swing components */
    /** refresh Swing timer */
    private final Timer timerRefresh;

    /** 
     * Creates new form LogPanel for the application log
     */
    public LogPanel() {
        this(ApplicationLogSingleton.JMMC_APP_LOG);
    }

    /** 
     * Creates new form LogPanel for the given logger path
     * @param loggerPath logger path
     */
    public LogPanel(final String loggerPath) {
        this.loggerPath = loggerPath;

        // Create the autoRefresh timer before any Swing component (see button action listeners):
        this.timerRefresh = new Timer(REFRESH_PERIOD, this);
        this.timerRefresh.setInitialDelay(0);

        initComponents();

        postInit();
    }

    /**
     * Initialize the Swing components
     */
    private void postInit() {

        if (SystemUtils.IS_OS_MAC_OSX) {
            this.setOpaque(false);
        }

        // Refresh buttons listener :
        this.jButtonRefreshLogs.addActionListener(this);
        this.jToggleButtonAutoRefresh.addActionListener(this);
        this.jSliderPeriod.addChangeListener(this);

        // set slider to 10 (1s):
        this.jSliderPeriod.setValue(10);

        // start autoRefresh timer by simulating one click:
        this.jToggleButtonAutoRefresh.doClick();
    }

    /**
     * Free any resource (timer) to this instance
     */
    public void onDispose() {
        _logger.debug("onDispose: {}", this);

        // stop anyway timer if started:
        enableAutoRefreshTimer(false);
    }

    /** 
     * Handle the stateChanged event from the slider.
     * @param ce slider change event
     */
    @Override
    public void stateChanged(final ChangeEvent ce) {
        final int milliseconds = 100 * this.jSliderPeriod.getValue();

        if (_logger.isDebugEnabled()) {
            _logger.debug("slider changed to: {} ms", milliseconds);
        }

        // update text value (rounded to 0.1s):
        this.jTextFieldPeriod.setText(df1.format(0.001d * milliseconds) + " s");

        // apply new delay to the timer
        this.timerRefresh.setDelay(milliseconds);
    }

    /**
     * Process any comboBox change event (level) to update Logger's state
     * @param ae action event
     */
    @Override
    public void actionPerformed(final ActionEvent ae) {
        if (ae.getSource() == this.timerRefresh || ae.getSource() == this.jButtonRefreshLogs) {
            updateLog();
        } else if (ae.getSource() == this.jToggleButtonAutoRefresh) {
            final boolean autoRefresh = this.jToggleButtonAutoRefresh.isSelected();

            enableAutoRefreshTimer(autoRefresh);

            this.jButtonRefreshLogs.setEnabled(!autoRefresh);
        }
    }

    /**
     * Start/Stop the internal autoRefresh timer
     * @param enable true to enable it, false otherwise
     */
    private void enableAutoRefreshTimer(final boolean enable) {
        if (enable) {
            if (!this.timerRefresh.isRunning()) {
                _logger.debug("starting timer: {}", this.timerRefresh);
                this.timerRefresh.start();
            }
        } else {
            if (this.timerRefresh.isRunning()) {
                _logger.debug("stopping timer: {}", this.timerRefresh);
                this.timerRefresh.stop();
            }
        }
    }

    /**
     * Update the log
     */
    private void updateLog() {

        final boolean append = (this.logByteCount > 0) ? true : false;

        // Get the partial application log as string starting at the given byteCount
        final LogOutput logOutput = ApplicationLogSingleton.getInstance().getLogOutput(this.loggerPath, this.logByteCount);

        // update byte count:
        this.logByteCount = logOutput.getByteCount();

        final String content = logOutput.getContent();

        if (content.length() > 0) {
            if (append) {
                final Document doc = this.logTextArea.getDocument();
                try {
                    doc.insertString(doc.getLength(), content, null);
                } catch (BadLocationException ble) {
                    _logger.error("bad location: ", ble);
                }
            } else {
                this.logTextArea.setText(content);
            }
            // scroll to end:
            this.logTextArea.setCaretPosition(this.logTextArea.getText().length());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelLogButtons = new javax.swing.JPanel();
        jToggleButtonAutoRefresh = new javax.swing.JToggleButton();
        jSliderPeriod = new javax.swing.JSlider();
        jTextFieldPeriod = new javax.swing.JTextField();
        jButtonRefreshLogs = new javax.swing.JButton();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout(0, 2));

        jPanelLogButtons.setOpaque(false);
        jPanelLogButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        jToggleButtonAutoRefresh.setText("Auto Refresh");
        jPanelLogButtons.add(jToggleButtonAutoRefresh);

        jSliderPeriod.setMajorTickSpacing(10);
        jSliderPeriod.setMinimum(1);
        jSliderPeriod.setToolTipText("auto refresh periodicity (100ms to 10s)");
        jPanelLogButtons.add(jSliderPeriod);

        jTextFieldPeriod.setColumns(6);
        jTextFieldPeriod.setEditable(false);
        jPanelLogButtons.add(jTextFieldPeriod);

        jButtonRefreshLogs.setText("Refresh");
        jPanelLogButtons.add(jButtonRefreshLogs);

        add(jPanelLogButtons, java.awt.BorderLayout.PAGE_START);

        logScrollPane.setOpaque(false);

        logTextArea.setEditable(false);
        logTextArea.setTabSize(4);
        logScrollPane.setViewportView(logTextArea);

        add(logScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRefreshLogs;
    private javax.swing.JPanel jPanelLogButtons;
    private javax.swing.JSlider jSliderPeriod;
    private javax.swing.JTextField jTextFieldPeriod;
    private javax.swing.JToggleButton jToggleButtonAutoRefresh;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    // End of variables declaration//GEN-END:variables

    /**
     * Return the logger path
     * @return logger path
     */
    public String getLoggerPath() {
        return loggerPath;
    }
}
