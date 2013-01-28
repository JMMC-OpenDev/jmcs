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
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampMessageHandler;
import fr.jmmc.jmcs.util.RecentFilesManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.SampUtils;

/**
 * Class for tests
 *
 * @author $author$
 * @version $Revision: 1.11 $
 */
public class Main extends App {

    /** Logger */
    private static final Logger _logger = Logger.getLogger(Main.class.getName());
    /** Button to launch about box window */
    private JButton _aboutBoxButton = null;
    /** Button to launch feedback report window */
    private JButton _feedbackReportButton = null;
    /** Button to launch help view window */
    private JButton _helpViewButton = null;
    /** Actions class */
    public RegisteredAction _openAction;
    Actions _actions = null;
    /** Test button */
    private JButton _testDismissableMessagePane = null;

    /** Constructor
     * @param args
     */
    public Main(String[] args) {
        super(args, false, true);
    }

    @Override
    protected void initServices() {
        // Set others preferences
        try {
            Preferences.getInstance().setPreference("MAIN", "main");
        } catch (Exception ex) {
            _logger.log(Level.WARNING, "Failed setting preference.", ex);
        }
    }

    /** Initialize application objects */
    @Override
    protected void setupGui() {
        _logger.warning("Initialize application objects");

        _actions = new Actions();
        _openAction = openAction();

        // Buttons
        _aboutBoxButton = new JButton(InternalActionFactory.showAboutBoxAction());
        _feedbackReportButton = new JButton(InternalActionFactory.showFeedbackReportAction());
        _helpViewButton = new JButton(openHelpFrame());
        _testDismissableMessagePane = new JButton(dismissableMessagePaneAction());

        // Set borderlayout
        getFramePanel().setLayout(new BorderLayout());

        // Add buttons to panel
        getFramePanel().add(_aboutBoxButton, BorderLayout.NORTH);
        getFramePanel().add(_feedbackReportButton, BorderLayout.CENTER);
        getFramePanel().add(_testDismissableMessagePane, BorderLayout.SOUTH);
    }

    /** Execute application body */
    @Override
    protected void execute() {
        _logger.info("Execute application body");

        // Show the frame
        getFrame().setVisible(true);
        RecentFilesManager.addFile(new File("/Users/lafrasse/test.scvot"));

        try {
            SampMessageHandler handler = new SampMessageHandler("stuff.do") {
                @Override
                public void processMessage(String senderId, Message msg) {
                    // do stuff
                    System.out.println("\tReceived 'stuff.do' message from '" + senderId + "' : '" + msg + "'.");

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
                    System.out.println("\tReceived 'LOAD_VO_TABLE' message from '" + senderId + "' : '" + msg + "'.");
                }
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Execute operations before closing application */
    @Override
    public boolean canBeTerminatedNow() {
        _logger.warning("Execute operations before closing application");

        // Quit application
        return true;
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
                MessagePane.showMessage("test !");
            }
        };
        temp.flagAsOpenAction();
        return temp;
    }

    /**
     * Main
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Bootstrapper.launchApp(new Main(args));
    }

    @Override
    protected void cleanup() {
        _aboutBoxButton = null;
        _feedbackReportButton = null;
        _helpViewButton = null;
        _openAction = null;
        _actions = null;
        _testDismissableMessagePane = null;
    }
}
/*___oOo___*/
