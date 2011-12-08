/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.LoggerFactory;

/**
 * This class provides two major functionnalities:
 * - application log viewer
 * - one simple  log GUI : Logger hierarchy browser / Level editor and displays current logs
 * 
 * This class is dedicated to Slf4j @see http://www.slf4j.org/ (MIT License) using Logback @see http://logback.qos.ch/ (EPL v1.0 / LGPL 2.1).
 * 
 * @author bourgesl
 */
public final class LogbackGui extends javax.swing.JPanel implements TreeSelectionListener, ActionListener, ChangeListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /** default auto refresh period = 5 second */
    private static final int REFRESH_PERIOD = 1000;
    /** undefined level */
    private static final String UNDEFINED_LEVEL = "UNDEFINED";
    /** Root Logger */
    private static final Logger _rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    /** Class logger */
    private static final Logger _logger = (Logger) LoggerFactory.getLogger(LogbackGui.class.getName());

    /* members */
    /** current edited logger */
    private Logger currentLogger = null;
    /** flag to enable / disable the automatic update of the logger when any swing component changes */
    private boolean doAutoUpdateLogger = true;
    /** log buffer byte count */
    private int logByteCount = 0;
    /* Swing components */
    /** refresh Swing timer */
    private final Timer timerRefresh;
    /** double formatter for auto refresh period */
    private final NumberFormat df1 = new DecimalFormat("0.0");

    /**
     * Display the logger editor
     * @param parent parent frame used to center this window (null means center on screen)
     * @param name name of the editor frame
     */
    public static void showEditor(final JFrame parent, final String name) {
        final String frameName = (name != null) ? name : "Log GUI";

        // Create Gui:
        final LogbackGui logGui = new LogbackGui();

        // 1. Create the frame
        final JFrame frame = new JFrame(frameName) {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1L;

            /**
             * Free any resource or reference to this instance :
             * stop the Swing timer if started
             */
            @Override
            public void dispose() {
                // free LogbackGui resources:
                logGui.onDispose();

                // dispose Frame :
                super.dispose();
            }
        };

        final Dimension dim = new Dimension(700, 500);
        frame.setMinimumSize(dim);
        frame.addComponentListener(new ComponentResizeAdapter(dim));

        // 2. Optional: What happens when the frame closes ?
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 3. Create components and put them in the frame
        frame.add(logGui);

        // 4. Size the frame.
        frame.pack();

        // Center it :
        frame.setLocationRelativeTo(parent);

        // 5. Show it and waits until frame is not visible or disposed :
        frame.setVisible(true);
    }

    /** Creates new form LogbackGui */
    private LogbackGui() {
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

        this.generateTree();

        // tree selection listener :
        this.jTreeLoggers.addTreeSelectionListener(this);

        // add property change listener to editable fields :
        // level (combo box) :
        this.jComboBoxLevel.addActionListener(this);

        // display root logger information:
        processLoggerSelection(_rootLogger);

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
    private void onDispose() {
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
    }

    /* Tree related methods */
    /**
     * Return the custom GenericJTree
     * @return GenericJTree
     */
    @SuppressWarnings("unchecked")
    private GenericJTree<Logger> getTreeLoggers() {
        return (GenericJTree<Logger>) this.jTreeLoggers;
    }

    /**
     * Generate the tree from the current edited list of Loggers
     */
    private void generateTree() {

        final GenericJTree<Logger> treeLoggers = getTreeLoggers();

        final LoggerContext loggerContext = _rootLogger.getLoggerContext();

        final DefaultMutableTreeNode rootNode = treeLoggers.getRootNode();

        // remove complete hierarchy:
        rootNode.removeAllChildren();

        // update the root node with the root logger (Logger[ROOT]):
        rootNode.setUserObject(_rootLogger);

        int pos;
        String path;
        DefaultMutableTreeNode parentNode;

        for (Logger logger : loggerContext.getLoggerList()) {

            // skip root logger
            if (logger != _rootLogger) {

                pos = logger.getName().lastIndexOf('.');

                if (pos == -1) {
                    // no path
                    path = null;
                    parentNode = null;

                } else {
                    path = logger.getName().substring(0, pos);

                    parentNode = treeLoggers.findTreeNode(loggerContext.getLogger(path));
                }

                if (parentNode == null) {
                    parentNode = rootNode;
                }

                if (parentNode != null) {
                    treeLoggers.addNode(parentNode, logger);
                }
            }
        }

        // fire node structure changed :
        treeLoggers.fireNodeChanged(rootNode);

        // select root node
        treeLoggers.selectPath(new TreePath(rootNode.getPath()));
    }

    /**
     * Process the tree selection events
     * @param e tree selection event
     */
    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        final DefaultMutableTreeNode currentNode = this.getTreeLoggers().getLastSelectedNode();

        if (currentNode != null) {
            /* retrieve the node that was selected */
            final Object userObject = currentNode.getUserObject();

            if (userObject instanceof Logger) {
                processLoggerSelection((Logger) userObject);
            }
        }
    }

    /**
     * Update the UI when a Logger is selected in the Logger tree
     * @param logger selected Logger
     */
    private void processLoggerSelection(final Logger logger) {

        // update the current Logger :
        this.currentLogger = logger;

        // disable the automatic update Logger :
        final boolean prevAutoUpdateLogger = this.setAutoUpdateLogger(false);
        try {

            // note : setText() / setValue() methods fire a property change event :

            // name :
            this.jTextFieldName.setText(logger.getName());

            // Level :
            this.jComboBoxLevel.setSelectedItem((logger.getLevel() != null) ? logger.getLevel().toString() : UNDEFINED_LEVEL);

            // effective level :
            this.jTextFieldEffectiveLevel.setText(logger.getEffectiveLevel().toString());

            // additivity :
            this.jRadioButtonAdditivityOn.setSelected(logger.isAdditive());

        } finally {
            // restore the automatic update logger :
            this.setAutoUpdateLogger(prevAutoUpdateLogger);
        }
    }

    /**
     * Process any comboBox change event (level) to update Logger's state
     * @param ae action event
     */
    @Override
    public void actionPerformed(final ActionEvent ae) {
        if (ae.getSource() == this.timerRefresh || ae.getSource() == this.jButtonRefreshLogs) {
            updateApplicationLog();
        } else if (ae.getSource() == this.jComboBoxLevel) {
            if (this.doAutoUpdateLogger) {
                if (this.currentLogger != null) {
                    final Level level = Level.toLevel((String) this.jComboBoxLevel.getSelectedItem(), null);

                    this.currentLogger.setLevel(level);

                    _logger.warn("Updated level for Logger [{}] to [{}]", this.currentLogger.getName(), this.currentLogger.getLevel());

                    // update form:
                    processLoggerSelection(this.currentLogger);
                }
            }
        } else if (ae.getSource() == this.jToggleButtonAutoRefresh) {
            final boolean autoRefresh = this.jToggleButtonAutoRefresh.isSelected();

            enableAutoRefreshTimer(autoRefresh);

            this.jButtonRefreshLogs.setEnabled(!autoRefresh);
        }
    }

    /**
     * Dump current state of all loggers using the root logger in warning level
     */
    private void dumpLoggers() {
        if (_logger.isWarnEnabled()) {
            _logger.warn("Logger Hierarchy:");

            for (Logger logger : _rootLogger.getLoggerContext().getLoggerList()) {
                _logger.warn("Logger[{}] level [{}], effective [{}], additivity [{}]", new Object[]{
                            logger.getName(), logger.getLevel(), logger.getEffectiveLevel(), logger.isAdditive()});
            }
        }
    }

    /**
     * Enable / Disable the automatic update of the logger when any swing component changes.
     * Return its previous value.
     *
     * Typical use is as following :
     * // disable the automatic update logger :
     * final boolean prevAutoUpdateLogger = this.setAutoUpdateLogger(false);
     * try {
     *   // operations ...
     *
     * } finally {
     *   // restore the automatic update logger :
     *   this.setAutoUpdateLogger(prevAutoUpdateLogger);
     * }
     *
     * @param value new value
     * @return previous value
     */
    private boolean setAutoUpdateLogger(final boolean value) {
        // first backup the state of the automatic update target :
        final boolean previous = this.doAutoUpdateLogger;

        // then change its state :
        this.doAutoUpdateLogger = value;

        // return previous state :
        return previous;
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupAdditivity = new javax.swing.ButtonGroup();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelLog = new javax.swing.JPanel();
        jPanelLogButtons = new javax.swing.JPanel();
        jToggleButtonAutoRefresh = new javax.swing.JToggleButton();
        jSliderPeriod = new javax.swing.JSlider();
        jTextFieldPeriod = new javax.swing.JTextField();
        jButtonRefreshLogs = new javax.swing.JButton();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jPanelConf = new javax.swing.JPanel();
        jPanelConfButtons = new javax.swing.JPanel();
        jButtonExpand = new javax.swing.JButton();
        jButtonCollapse = new javax.swing.JButton();
        jButtonRefreshLoggers = new javax.swing.JButton();
        jButtonDumpLoggers = new javax.swing.JButton();
        jScrollPaneLoggerTree = new javax.swing.JScrollPane();
        jTreeLoggers = createLoggerJTree();
        jPanelLoggerInfo = new javax.swing.JPanel();
        jLabelInfo = new javax.swing.JLabel();
        jLabelName = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabelLevel = new javax.swing.JLabel();
        jTextFieldEffectiveLevel = new javax.swing.JTextField();
        jLabelEffectiveLevel = new javax.swing.JLabel();
        jComboBoxLevel = new javax.swing.JComboBox();
        jLabelAdditivity = new javax.swing.JLabel();
        jRadioButtonAdditivityOn = new javax.swing.JRadioButton();
        jRadioButtonAdditivityOff = new javax.swing.JRadioButton();

        setLayout(new java.awt.BorderLayout());

        jPanelLog.setLayout(new java.awt.BorderLayout());

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

        jPanelLog.add(jPanelLogButtons, java.awt.BorderLayout.PAGE_START);

        logTextArea.setEditable(false);
        logTextArea.setFont(new java.awt.Font("Monospaced", 1, 12));
        logTextArea.setTabSize(4);
        logScrollPane.setViewportView(logTextArea);

        jPanelLog.add(logScrollPane, java.awt.BorderLayout.CENTER);

        jTabbedPane.addTab("Log content", jPanelLog);

        jPanelConf.setLayout(new java.awt.GridBagLayout());

        jPanelConfButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        jButtonExpand.setText("Expand");
        jButtonExpand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExpandActionPerformed(evt);
            }
        });
        jPanelConfButtons.add(jButtonExpand);

        jButtonCollapse.setText("Collapse");
        jButtonCollapse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCollapseActionPerformed(evt);
            }
        });
        jPanelConfButtons.add(jButtonCollapse);

        jButtonRefreshLoggers.setText("Refresh");
        jButtonRefreshLoggers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshLoggersActionPerformed(evt);
            }
        });
        jPanelConfButtons.add(jButtonRefreshLoggers);

        jButtonDumpLoggers.setText("Dump");
        jButtonDumpLoggers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDumpLoggersActionPerformed(evt);
            }
        });
        jPanelConfButtons.add(jButtonDumpLoggers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        jPanelConf.add(jPanelConfButtons, gridBagConstraints);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Loggers");
        jTreeLoggers.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTreeLoggers.setVisibleRowCount(5);
        jScrollPaneLoggerTree.setViewportView(jTreeLoggers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelConf.add(jScrollPaneLoggerTree, gridBagConstraints);

        jPanelLoggerInfo.setLayout(new java.awt.GridBagLayout());

        jLabelInfo.setText("Logger information:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelLoggerInfo.add(jLabelInfo, gridBagConstraints);

        jLabelName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 4);
        jPanelLoggerInfo.add(jLabelName, gridBagConstraints);

        jTextFieldName.setEditable(false);
        jTextFieldName.setText("name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanelLoggerInfo.add(jTextFieldName, gridBagConstraints);

        jLabelLevel.setText("Level:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 4);
        jPanelLoggerInfo.add(jLabelLevel, gridBagConstraints);

        jTextFieldEffectiveLevel.setColumns(10);
        jTextFieldEffectiveLevel.setEditable(false);
        jTextFieldEffectiveLevel.setText("level");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanelLoggerInfo.add(jTextFieldEffectiveLevel, gridBagConstraints);

        jLabelEffectiveLevel.setText("Effective level:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 4);
        jPanelLoggerInfo.add(jLabelEffectiveLevel, gridBagConstraints);

        jComboBoxLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "UNDEFINED", "OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanelLoggerInfo.add(jComboBoxLevel, gridBagConstraints);

        jLabelAdditivity.setText("Additivity:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 4);
        jPanelLoggerInfo.add(jLabelAdditivity, gridBagConstraints);

        buttonGroupAdditivity.add(jRadioButtonAdditivityOn);
        jRadioButtonAdditivityOn.setText("true");
        jRadioButtonAdditivityOn.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanelLoggerInfo.add(jRadioButtonAdditivityOn, gridBagConstraints);

        buttonGroupAdditivity.add(jRadioButtonAdditivityOff);
        jRadioButtonAdditivityOff.setText("false");
        jRadioButtonAdditivityOff.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanelLoggerInfo.add(jRadioButtonAdditivityOff, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanelConf.add(jPanelLoggerInfo, gridBagConstraints);

        jTabbedPane.addTab("Configuration", jPanelConf);

        add(jTabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRefreshLoggersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshLoggersActionPerformed
        generateTree();
    }//GEN-LAST:event_jButtonRefreshLoggersActionPerformed

    private void jButtonExpandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExpandActionPerformed
        final DefaultMutableTreeNode currentNode = getTreeLoggers().findTreeNode(this.currentLogger);
        getTreeLoggers().expandAll(new TreePath(currentNode.getPath()), true, (currentNode == getTreeLoggers().getRootNode()) ? false : true);
    }//GEN-LAST:event_jButtonExpandActionPerformed

    private void jButtonCollapseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCollapseActionPerformed
        final DefaultMutableTreeNode currentNode = getTreeLoggers().findTreeNode(this.currentLogger);
        getTreeLoggers().expandAll(new TreePath(currentNode.getPath()), false, (currentNode == getTreeLoggers().getRootNode()) ? false : true);
    }//GEN-LAST:event_jButtonCollapseActionPerformed

    private void jButtonDumpLoggersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDumpLoggersActionPerformed
        dumpLoggers();
    }//GEN-LAST:event_jButtonDumpLoggersActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupAdditivity;
    private javax.swing.JButton jButtonCollapse;
    private javax.swing.JButton jButtonDumpLoggers;
    private javax.swing.JButton jButtonExpand;
    private javax.swing.JButton jButtonRefreshLoggers;
    private javax.swing.JButton jButtonRefreshLogs;
    private javax.swing.JComboBox jComboBoxLevel;
    private javax.swing.JLabel jLabelAdditivity;
    private javax.swing.JLabel jLabelEffectiveLevel;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelLevel;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JPanel jPanelConf;
    private javax.swing.JPanel jPanelConfButtons;
    private javax.swing.JPanel jPanelLog;
    private javax.swing.JPanel jPanelLogButtons;
    private javax.swing.JPanel jPanelLoggerInfo;
    private javax.swing.JRadioButton jRadioButtonAdditivityOff;
    private javax.swing.JRadioButton jRadioButtonAdditivityOn;
    private javax.swing.JScrollPane jScrollPaneLoggerTree;
    private javax.swing.JSlider jSliderPeriod;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextField jTextFieldEffectiveLevel;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldPeriod;
    private javax.swing.JToggleButton jToggleButtonAutoRefresh;
    private javax.swing.JTree jTreeLoggers;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    // End of variables declaration//GEN-END:variables

    /**
     * Create a new JTree dedicated to Logger instances
     * @return Jtree instance
     */
    private static JTree createLoggerJTree() {
        return new GenericJTree<Logger>(Logger.class) {

            /** default serial UID for Serializable interface */
            private static final long serialVersionUID = 1;

            /**
             * Convert a non-null value object to string
             * @param userObject user object to convert
             * @return string representation of the user object
             */
            @Override
            protected String convertUserObjectToString(final Logger userObject) {
                final int pos = userObject.getName().lastIndexOf('.');

                if (pos != -1) {
                    return userObject.getName().substring(pos + 1);
                }

                return userObject.getName();
            }
        };
    }

    /**
     * Update the application log
     */
    private void updateApplicationLog() {

        final boolean append = (this.logByteCount > 0) ? true : false;

        // Get the partial application log as string starting at the given byteCount
        final LogOutput logOutput = ApplicationLogSingleton.getInstance().getLogOutput(this.logByteCount);

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

    /**
     * This custom JTree implementation provides several utility methods to manipulate
     * DefaultMutableTreeNode and visual representation of nodes
     * @param <E> type of the user object 
     * 
     * Note: code copied from @see fr.jmmc.jmcs.gui.GenericJTree to let this class be self consistent
     */
    private static abstract class GenericJTree<E> extends JTree {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        /** Class logger */
        protected static final Logger logger = (Logger) LoggerFactory.getLogger(GenericJTree.class.getName());
        /* members */
        /** class corresponding to <E> generic type */
        private final Class<E> classType;

        /**
         * Public constructor changing default values : SINGLE_TREE_SELECTION
         * 
         * @param classType class corresponding to <E> generic type
         */
        protected GenericJTree(final Class<E> classType) {
            super(new DefaultMutableTreeNode("GenericJTree"), false);

            this.classType = classType;

            // single tree selection :
            this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }

        /**
         * Return the tree model
         * @return tree model
         */
        protected final DefaultTreeModel getTreeModel() {
            return ((DefaultTreeModel) this.getModel());
        }

        /**
         * Create a new node using the given user object and add it to the given parent node
         * @param parentNode parent node
         * @param userObject user object to create the new node
         * @return new created node
         */
        protected final DefaultMutableTreeNode addNode(final DefaultMutableTreeNode parentNode, final E userObject) {
            final DefaultMutableTreeNode modelNode = new DefaultMutableTreeNode(userObject);

            parentNode.add(modelNode);

            return modelNode;
        }

        /**
         * Create a new node using the given user object and add it to the given parent node
         * and Fire node structure changed on the parent node
         * @param parentNode parent node
         * @param userObject user object to create the new node
         */
        protected final void addNodeAndRefresh(final DefaultMutableTreeNode parentNode, final E userObject) {
            final DefaultMutableTreeNode newNode = this.addNode(parentNode, userObject);

            // fire node structure changed :
            this.fireNodeChanged(parentNode);

            // Select the new node = model :
            this.selectPath(new TreePath(newNode.getPath()));
        }

        /**
         * Remove the given current node from the parent node
         * and Fire node structure changed on the parent node
         * @param parentNode parent node
         * @param currentNode node to remove
         */
        protected final void removeNodeAndRefresh(final DefaultMutableTreeNode parentNode, final DefaultMutableTreeNode currentNode) {
            this.removeNodeAndRefresh(parentNode, currentNode, true);
        }

        /**
         * Remove the given current node from the parent node
         * and Fire node structure changed on the parent node
         * @param parentNode parent node
         * @param currentNode node to remove
         * @param doSelectParent flag to indicate to select the parent node once the node removed
         */
        protected final void removeNodeAndRefresh(final DefaultMutableTreeNode parentNode, final DefaultMutableTreeNode currentNode, final boolean doSelectParent) {
            parentNode.remove(currentNode);

            // fire node structure changed :
            this.fireNodeChanged(parentNode);

            if (doSelectParent) {
                // Select the parent node = target :
                this.selectPath(new TreePath(parentNode.getPath()));
            }
        }

        /**
         * Fire node structure changed on the given tree node
         * @param node changed tree node
         */
        public final void fireNodeChanged(final TreeNode node) {
            // fire node structure changed :
            this.getTreeModel().nodeStructureChanged(node);
        }

        /**
         * Return the root node
         * @return root node
         */
        protected final DefaultMutableTreeNode getRootNode() {
            return (DefaultMutableTreeNode) getTreeModel().getRoot();
        }

        /**
         * Return the parent node of the given node
         * @param node node to use
         * @return parent node
         */
        protected final DefaultMutableTreeNode getParentNode(final DefaultMutableTreeNode node) {
            return (DefaultMutableTreeNode) node.getParent();
        }

        /**
         * Return the node corresponding to the last selected path in the tree
         * @return node or null
         */
        protected final DefaultMutableTreeNode getLastSelectedNode() {
            return (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        }

        /**
         * Find the first tree node having the given user object
         * @param userObject user object to locate in the tree
         * @return tree node or null
         */
        protected final DefaultMutableTreeNode findTreeNode(final E userObject) {
            return findTreeNode(getRootNode(), userObject);
        }

        /**
         * Find the first tree node having the given user object recursively
         *
         * @param node current node to traverse
         * @param userObject user object to locate in the tree
         * @return tree node or null
         */
        protected static DefaultMutableTreeNode findTreeNode(final DefaultMutableTreeNode node, final Object userObject) {
            if (node.getUserObject() == userObject) {
                return node;
            }

            final int size = node.getChildCount();
            if (size > 0) {
                DefaultMutableTreeNode result = null;

                DefaultMutableTreeNode childNode;
                for (int i = 0; i < size; i++) {
                    childNode = (DefaultMutableTreeNode) node.getChildAt(i);

                    result = findTreeNode(childNode, userObject);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return null;
        }

        /**
         * Select the first child node
         * @param rootNode root node
         */
        protected final void selectFirstChildNode(final DefaultMutableTreeNode rootNode) {
            if (rootNode.isLeaf()) {
                return;
            }

            // first child = target :
            final DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) rootNode.getFirstChild();
            this.selectPath(new TreePath(firstChild.getPath()));

            // expand node if there is at least one child node :
            if (!firstChild.isLeaf()) {
                final DefaultMutableTreeNode secondChild = (DefaultMutableTreeNode) firstChild.getFirstChild();

                this.scrollPathToVisible(new TreePath(secondChild.getPath()));
            }
        }

        /**
         * Change the selected path in the tree
         * This will send a selection event changed that will refresh the UI
         *
         * @param path tree path
         */
        protected final void selectPath(final TreePath path) {
            this.setSelectionPath(path);
            this.scrollPathToVisible(path);
        }

        /**
         * Expand or collapse all nodes in the tree
         * @param expand true to expand all or collapse all
         */
        protected void expandAll(final boolean expand) {
            // Traverse tree from root
            expandAll(new TreePath(getRootNode()), expand, false);
        }

        /**
         * Expand or collapse all nodes starting from the given parent (path)
         * @param parent parent path
         * @param expand true to expand all or collapse all
         * @param process flag to process this parent node (useful for root node)
         */
        protected void expandAll(final TreePath parent, final boolean expand, final boolean process) {
            // Traverse children
            final TreeNode node = (TreeNode) parent.getLastPathComponent();
            if (node.getChildCount() >= 0) {
                TreePath path;
                for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                    path = parent.pathByAddingChild(e.nextElement());

                    // recursive call:
                    expandAll(path, expand, true);
                }
            }

            if (process) {
                // Expansion or collapse must be done bottom-up
                if (expand) {
                    expandPath(parent);
                } else {
                    collapsePath(parent);
                }
            }
        }

        /**
         * Called by the renderers to convert the specified value to
         * text. This implementation returns <code>value.toString</code>, ignoring
         * all other arguments. To control the conversion, subclass this
         * method and use any of the arguments you need.
         *
         * @param value the <code>Object</code> to convert to text
         * @param selected true if the node is selected
         * @param expanded true if the node is expanded
         * @param leaf  true if the node is a leaf node
         * @param row  an integer specifying the node's display row, where 0 is
         *             the first row in the display
         * @param hasFocus true if the node has the focus
         * @return the <code>String</code> representation of the node's value
         */
        @Override
        @SuppressWarnings("unchecked")
        public final String convertValueToText(
                final Object value,
                final boolean selected,
                final boolean expanded, final boolean leaf, final int row,
                final boolean hasFocus) {

            if (value != null) {
                String sValue = null;

                if (value instanceof DefaultMutableTreeNode) {
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    final Object userObject = node.getUserObject();

                    if (userObject != null) {
                        sValue = convertObjectToString(userObject);
                    }

                } else {
                    logger.error("unsupported class type = {}", value.getClass());

                    sValue = value.toString();
                }

                if (sValue != null) {
                    return sValue;
                }

            }
            return "";
        }

        /**
         * Convert a non-null value object to string
         * @param userObject user object to convert
         * @return string representation of the user object
         */
        @SuppressWarnings("unchecked")
        private final String convertObjectToString(final Object userObject) {
            // Check first for string (root node and default tree model):
            if (userObject instanceof String) {
                return userObject.toString();
            }
            // Check if the class type matches (exact class comparison):
            if (this.classType == null || this.classType.isAssignableFrom(userObject.getClass())) {
                return convertUserObjectToString((E) userObject);
            }
            return toString(userObject);
        }

        /**
         * Default toString() conversion
         * @param userObject user object to convert
         * @return string representation of the user object
         */
        protected final String toString(final Object userObject) {
            if (!(userObject instanceof String)) {
                logger.warn("Unsupported class type = {}", userObject.getClass());
            }
            // String representation :
            return userObject.toString();
        }

        /**
         * Convert a non-null value object to string
         * @param userObject user object to convert
         * @return string representation of the user object
         */
        protected abstract String convertUserObjectToString(final E userObject);
    }

    /**
     * Component adapter to force a resizable component to have a minimal dimension
     * 
     * Note: code copied from @see fr.jmmc.jmcs.gui.ComponentResizeAdapter to let this class be self consistent
     */
    private static final class ComponentResizeAdapter extends ComponentAdapter {

        /** minimal dimension to respect */
        private final Dimension dim;

        /**
         * Constructor with a given minimal dimension
         * @param dim minimal dimension
         */
        protected ComponentResizeAdapter(final Dimension dim) {
            this.dim = dim;
        }

        /**
         * Invoked when the component's size changes.
         * This overriden method checks that the new size is greater than the minimal dimension
         * @param e event to process
         */
        @Override
        public void componentResized(final ComponentEvent e) {
            final Component c = e.getComponent();
            final Dimension d = c.getSize();
            int w = d.width;
            if (w < dim.width) {
                w = dim.width;
            }
            int h = d.height;
            if (h < dim.height) {
                h = dim.height;
            }

            c.setSize(w, h);
        }
    }
}
