/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.modjava;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import fr.jmmc.jmcs.gui.component.DismissableMessagePane;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampMessageHandler;
import fr.jmmc.jmcs.util.RecentFilesManager;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.SampUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of your application.
 */
public class Main extends App {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(Main.class.getName());
    /** Button to launch feedback report window */
    private JButton _feedbackReportButton = null;
    /** Button to launch help view window */
    private JButton _helpViewButton = null;
    /** Actions class */
    public RegisteredAction _openAction;
    private Actions _actions = null;
    /** Test button */
    private JButton _testDismissableMessagePane = null;

    /**
     * Constructor.
     * @param args
     */
    public Main(String[] args) {
        super(args);
    }

    /** Initialize application services */
    @Override
    protected void initServices() {
        // Set others preferences
        try {
            Preferences.getInstance().setPreference("MAIN", "main");
        } catch (Exception ex) {
            _logger.error("Failed setting preference.", ex);
        }
    }

    /** Initialize application GUI */
    @Override
    protected void setupGui() {
        _logger.warn("Initialize application objects");

        _actions = new Actions();
        _openAction = openAction();

        // Buttons
        _feedbackReportButton = new JButton(InternalActionFactory.showFeedbackReportAction());
        _helpViewButton = new JButton(openHelpFrame());
        _testDismissableMessagePane = new JButton(dismissableMessagePaneAction());

        final Container framePanel = getFramePanel();

        // Set borderlayout
        framePanel.setLayout(new BorderLayout());

        // Add buttons to panel
        framePanel.add(_feedbackReportButton, BorderLayout.NORTH);
        framePanel.add(_testDismissableMessagePane, BorderLayout.CENTER);
        framePanel.add(new StatusBar(), BorderLayout.SOUTH);

        // Center main window on the screen
        WindowUtils.centerOnMainScreen(getFrame());
    }

    /** Execute application body */
    @Override
    protected void execute() {
        StatusBar.show("Application ready.");
        _logger.info("Execute application body");

        // Show the frame
        getFrame().setVisible(true);
        RecentFilesManager.addFile(new File("/Users/lafrasse/test.scvot"));

        try {
            SampMessageHandler handler = new SampMessageHandler("stuff.do") {
                @Override
                public void processMessage(String senderId, Message msg) {
                    // do stuff
                    _logger.info("Received 'stuff.do' message from '" + senderId + "' : '" + msg + "'.");
                    StatusBar.show("Received 'stuff.do' SAMP message.");

                    String name = (String) msg.getParam("name");
                    Map result = new HashMap();
                    result.put("name", name);
                    result.put("x", SampUtils.encodeFloat(3.141159));
                }
            };
            handler = new SampMessageHandler(SampCapability.LOAD_VO_TABLE) {
                @Override
                public void processMessage(String senderId, Message msg) {
                    // do stuff
                    _logger.info("Received 'LOAD_VO_TABLE' message from '" + senderId + "' : '" + msg + "'.");
                    StatusBar.show("Received 'LOAD_VO_TABLE' SAMP message.");
                }
            };
        } catch (Exception ex) {
            _logger.error("SAMP error", ex);
        }
    }

    /** Execute operations before closing application */
    @Override
    public boolean canBeTerminatedNow() {
        _logger.warn("Execute operations before closing application");

        // Quit application
        return true;
    }

    /** application cleanup */
    @Override
    protected void cleanup() {
        _feedbackReportButton = null;
        _helpViewButton = null;
        _openAction = null;
        _actions = null;
        _testDismissableMessagePane = null;
    }

    /** Open help view action */
    private Action openHelpFrame() {
        return new AbstractAction("Open Help Frame") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                // Instantiate help JFrame
                JFrame helpFrame = new JFrame("- Help frame -");

                // Add buttons to panel
                helpFrame.getContentPane().add(new JButton(InternalActionFactory.showHelpAction()), BorderLayout.NORTH);
                helpFrame.getContentPane().add(new JButton("CENTER"), BorderLayout.CENTER);
                helpFrame.getContentPane().add(new JButton("WEST"), BorderLayout.WEST);
                helpFrame.getContentPane().add(new JButton("SOUTH"), BorderLayout.SOUTH);

                // Set the frame properties
                helpFrame.pack();
                helpFrame.setLocationRelativeTo(null);
                helpFrame.setVisible(true);
            }
        };
    }

    /** action */
    private Action dismissableMessagePaneAction() {
        return new AbstractAction("Show dismissable message pane") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                DismissableMessagePane.show(
                        "Try to show a test message\n which can be deactivated by user!!",
                        Preferences.getInstance(), "msgTest");
            }
        };
    }

    /** Open file action */
    private RegisteredAction openAction() {
        RegisteredAction temp = new RegisteredAction("fr.jmmc.mcs.modjava.Main", "_openAction") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                StatusBar.show("Open action !!!");
                MessagePane.showMessage("test !");
            }
        };
        temp.flagAsOpenAction();
        return temp;
    }

    /**
     * Main
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Bootstrapper.launchApp(new Main(args), false, true);
    }
}
/*___oOo___*/
