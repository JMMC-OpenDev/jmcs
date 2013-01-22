/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.action.internal;

import com.apple.eawt.QuitResponse;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.gui.AboutBox;
import fr.jmmc.jmcs.gui.DependenciesView;
import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.HelpView;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.ResizableTextViewFactory;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.resource.image.ResourceImage;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initiate all actions needed by jMCS.
 * @author Sylvain LAFRASSE
 */
public class InternalActionFactory {

    /** Logger */
    private static final String CLASS_PATH = InternalActionFactory.class.getName();
    private static final Logger _logger = LoggerFactory.getLogger(CLASS_PATH);
    /** Singleton instance */
    private static InternalActionFactory _instance = null;
    // Members
    /** Acknowledgment handling action */
    private static ShowAcknowledgmentAction _showAcknowledgmentAction = null;
    /** Show About... box action */
    private static ShowAboutBoxAction _showAboutBoxAction = null;
    /** Show Feedback Report action */
    private static ShowFeedbackReportAction _showFeedbackReportAction = null;
    /** Show help handling action */
    private static ShowHelpAction _showHelpAction = null;
    /** Show hot news handling action */
    private static ShowHotNewsAction _showHotNewsAction = null;
    /** Show release handling action */
    private static ShowReleaseAction _showReleaseAction = null;
    /** Show FAQ handling action */
    private static ShowFaqAction _showFaqAction = null;
    /** Show Dependencies action */
    private static ShowDependenciesAction _showDependenciesAction = null;
    /** Show log GUI action */
    private static ShowLogGuiAction _showLogGuiAction = null;
    /** default Open handling action */
    private static DefaultOpenAction _defaultOpenAction = null;
    /** Quit handling action */
    private static QuitAction _quitAction = null;

    /** Hidden constructor */
    private InternalActionFactory() {
        _showAcknowledgmentAction = new ShowAcknowledgmentAction(CLASS_PATH, "_showAcknowledgmentAction");
        _showAboutBoxAction = new ShowAboutBoxAction(CLASS_PATH, "_showAboutBoxsAction");
        _showFeedbackReportAction = new ShowFeedbackReportAction(CLASS_PATH, "_showFeedbackReportAction");
        _showHotNewsAction = new ShowHotNewsAction(CLASS_PATH, "_showHotNewsAction");
        _showReleaseAction = new ShowReleaseAction(CLASS_PATH, "_showReleaseAction");
        _showFaqAction = new ShowFaqAction(CLASS_PATH, "_showFaqAction");
        _showHelpAction = new ShowHelpAction(CLASS_PATH, "_showHelpAction");
        _showDependenciesAction = new ShowDependenciesAction(CLASS_PATH, "_showDependenciesAction");
        _showLogGuiAction = new ShowLogGuiAction(CLASS_PATH, "_showLogGuiAction");
        _defaultOpenAction = new DefaultOpenAction(CLASS_PATH, "_defaultOpenAction");
        _quitAction = new QuitAction(CLASS_PATH, "_quitAction");
    }

    /** @return the singleton instance */
    private static synchronized InternalActionFactory getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new InternalActionFactory();
        }

        return _instance;

        // DO NOT MODIFY !!!
    }

    /**
     * Create all internal actions.
     */
    public static void populate() {
        getInstance();
    }

    /**
     * Creates the action which open the about box window
     * @return action which open the about box window
     */
    public static Action showAboutBoxAction() {
        return getInstance()._showAboutBoxAction;
    }

    /**
     * Return the action which displays and copy acknowledgment to clipboard
     * @return action which displays and copy acknowledgment to clipboard
     */
    public static Action showAcknowledgmentAction() {
        return getInstance()._showAcknowledgmentAction;
    }

    /**
     * Creates the feedback action which open the feedback window
     * @return feedback action which open the feedback window
     */
    public static Action showFeedbackReportAction() {
        return _showFeedbackReportAction;
    }

    /**
     * Return the action which tries to display the help
     * @return action which tries to display the help
     */
    public static Action showHelpAction() {
        return getInstance()._showHelpAction;
    }

    /**
     * Return the action which tries to display dependencies
     * @return action which tries to display dependencies
     */
    public static Action showDependenciesAction() {
        return getInstance()._showDependenciesAction;
    }

    /**
     * Return the action dedicated to display hot news
     * @return action dedicated to display hot news
     */
    public static Action showHotNewsAction() {
        return getInstance()._showHotNewsAction;
    }

    /**
     * Return the action dedicated to display release
     * @return action dedicated to display release
     */
    public static Action showReleaseAction() {
        return getInstance()._showReleaseAction;
    }

    /**
     * Return the action dedicated to display FAQ
     * @return action dedicated to display FAQ
     */
    public static Action showFaqAction() {
        return getInstance()._showFaqAction;
    }

    /**
     * Return the action dedicated to display log GUI
     * @return action dedicated to display log GUI
     */
    public static Action showLogGuiAction() {
        return getInstance()._showLogGuiAction;
    }

    /**
     * Return the action which tries to quit the application
     * @return action which tries to quit the application
     */
    public static Action quitAction() {
        return getInstance()._quitAction;
    }

    /** Action to show application About... box. */
    protected static class ShowAboutBoxAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        /** Data model */
        final ApplicationDataModel _applicationData;
        /** AboutBox */
        private static AboutBox _aboutBox = null;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowAboutBoxAction(String classPath, String fieldName) {

            super(classPath, fieldName, "Copy Acknowledgement to Clipboard");
            _applicationData = App.getSharedApplicationDataModel();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (_applicationData != null) {
                if (_aboutBox != null) {
                    if (!_aboutBox.isVisible()) {
                        _aboutBox.setVisible(true);
                    } else {
                        _aboutBox.toFront();
                    }
                } else {
                    _aboutBox = new AboutBox();
                }
            }
        }
    }

    /** Action to copy acknowledgment text to the clipboard. */
    protected static class ShowAcknowledgmentAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        /** Data model */
        final ApplicationDataModel _applicationData;
        /** Acknowledgment content */
        private String _acknowledgement = null;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowAcknowledgmentAction(String classPath, String fieldName) {

            super(classPath, fieldName, "Copy Acknowledgement to Clipboard");
            _applicationData = App.getSharedApplicationDataModel();
            _acknowledgement = _applicationData.getAcknowledgment();
            // If the application does not provide an acknowledgement
            if (_acknowledgement == null) {
                // Generate one instead
                final String compagny = _applicationData.getLegalCompanyName();
                final String appName = _applicationData.getProgramName();
                final String appURL = _applicationData.getLinkValue();
                _acknowledgement = "This research has made use of the " + compagny
                        + "\\texttt{" + appName
                        + "} service\n\\footnote{Available at " + appURL + "}";
            }
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            StringSelection ss = new StringSelection(_acknowledgement);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

            final String delimiter = "---------------------------------------------------------------------------\n";
            final String message = "The previous message has already been copied to your clipboard, in order to\n"
                    + "let you conveniently paste it in your related publication.";
            final String windowTitle = _applicationData.getProgramName()
                    + " Acknowledgment Note";
            final String windowContent = delimiter + _acknowledgement + "\n"
                    + delimiter + "\n" + message;

            ResizableTextViewFactory.createTextWindow(windowContent, windowTitle, enabled);
        }
    }

    /** Action to show hot news RSS feed. */
    protected static class ShowFeedbackReportAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;
        /** Data model */
        final ApplicationDataModel _applicationData;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowFeedbackReportAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Report Feedback to " + App.getSharedApplicationDataModel().getShortCompanyName() + "...");
            _applicationData = App.getSharedApplicationDataModel();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (_applicationData != null) {
                // Show the feedback report :
                FeedbackReport.openDialog(null);
            }
        }
    }

    /** Action to show hot news RSS feed. */
    protected static class ShowHotNewsAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowHotNewsAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Hot News (RSS Feed)");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            BrowserLauncher.openURL(App.getSharedApplicationDataModel().getHotNewsRSSFeedLinkValue());
        }
    }

    /** Action to show release. */
    protected static class ShowReleaseAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowReleaseAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Release Notes");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            BrowserLauncher.openURL(App.getSharedApplicationDataModel().getReleaseNotesLinkValue());
        }
    }

    /** Action to show FAQ. */
    protected static class ShowFaqAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowFaqAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Frequently Asked Questions");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            BrowserLauncher.openURL(App.getSharedApplicationDataModel().getFaqLinkValue());
        }
    }

    /** Action to show dependencies. */
    protected static class ShowDependenciesAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowDependenciesAction(String classPath, String fieldName) {
            super(classPath, fieldName, "jMCS Dependencies Copyrights");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            DependenciesView.display();
        }
    }

    /** Action to show help. */
    protected static class ShowHelpAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowHelpAction(String classPath, String fieldName) {
            super(classPath, fieldName, "User Manual");
            setEnabled(HelpView.isAvailable());

            // Set Icon only if not under Mac OS X
            if (!SystemUtils.IS_OS_MAC_OSX) {
                final ImageIcon helpIcon = ResourceImage.HELP_ICON.icon();
                putValue(SMALL_ICON, helpIcon);
            }
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            HelpView.setVisible(true);
        }
    }

    /** Action to show log GUI. */
    protected static class ShowLogGuiAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        ShowLogGuiAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Show Log Console");
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            App.showLogConsole();
        }
    }

    /** Action to correctly handle file opening. */
    protected static class DefaultOpenAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        DefaultOpenAction(String classPath, String fieldName) {
            super(classPath, fieldName);

            // Disabled as this default implementation does nothing
            setEnabled(false);

            flagAsOpenAction();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.warn("No handler for default file opening.");
        }
    }

    /** Action to correctly handle operations before closing application. */
    protected static class QuitAction extends RegisteredAction {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1;

        /**
         * Public constructor
         * @param classPath the path of the class containing the field pointing to
         * the action, in the form returned by 'getClass().getName();'.
         * @param fieldName the name of the field pointing to the action.
         */
        QuitAction(String classPath, String fieldName) {
            super(classPath, fieldName, "Quit", "ctrl Q");

            flagAsQuitAction();
        }

        /**
         * Handle the action event
         * @param evt action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            _logger.debug("Application is about to die, should we proceed ?");

            // Mac OS X Quit action handler:
            final QuitResponse response;
            if (evt != null && evt.getSource() instanceof QuitResponse) {
                response = (QuitResponse) evt.getSource();
            } else {
                response = null;
            }

            // Check if user is OK to kill SAMP hub (if any)
            if (!SampManager.getInstance().allowHubKilling()) {
                _logger.debug("SAMP cancelled application kill.");
                // Otherwise cancel quit

                if (response != null) {
                    response.cancelQuit();
                }
                return;
            }

            // If we are ready to shouldFinish application execution
            final App app = App.getSharedInstance();
            if (app.shouldFinish()) {
                _logger.debug("Application should be killed.");

                // Verify if we are authorized to kill the application or not
                if (app.shouldExitWhenClosed()) {
                    // Exit the application

                    if (response != null) {
                        App.setAvoidSystemExit(true);
                    }
                    App.exit(0);

                    if (response != null) {
                        response.performQuit();
                    }

                } else {
                    _logger.debug("Application left opened as required.");
                }
            } else {
                _logger.debug("Application killing cancelled.");
            }
            if (response != null) {
                response.cancelQuit();
            }
        }
    }
}
