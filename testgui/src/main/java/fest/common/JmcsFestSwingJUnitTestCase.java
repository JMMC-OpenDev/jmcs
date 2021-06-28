/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fest.common;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.App.ApplicationState;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.gui.component.ResizableTextViewFactory;
import fr.jmmc.jmcs.util.MCSExceptionHandler;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import org.fest.swing.core.ComponentFoundCondition;
import org.fest.swing.core.EmergencyAbortListener;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import static org.fest.swing.launcher.ApplicationLauncher.*;
import org.fest.swing.timing.Condition;
import static org.fest.swing.timing.Pause.*;
import org.junit.AfterClass;

/**
 * This class extends FestSwingCustomJUnitTestCase to start / stop one jMCS application
 * @author bourgesl
 */
public class JmcsFestSwingJUnitTestCase extends FestSwingCustomJUnitTestCase {

    /** absolute path to test folder to load test resources */
    protected final static String TEST_FOLDER = getProjectFolderPath() + "src/test/resources/";

    /* members */
    /** application window fixture */
    protected FrameFixture window;
    /** emergency abort listener associated to 'Ctrl + Shift + A' key combination */
    private EmergencyAbortListener listener;

    /**
     * Public constructor required by JUnit
     */
    public JmcsFestSwingJUnitTestCase() {
        super();
    }

    /**
     * Start the given Jmcs application (Swing)
     * This method is called <strong>after</strong> executing <code>{@link FestSwingCustomJUnitTestCase#setUpOnce()}</code>.
     * @param appClass App subclass
     * @param args main method arguments
     */
    public static void startApplication(final Class<? extends App> appClass, final String... args) {
        // disable SAMP '[cN]' suffix in SAMP action names:
        System.setProperty("SampCapabilityAction.showClientId", "false");

        String message = "<HTML><BODY>";
        message += "<FONT COLOR='RED'>Please do not touch the mouse while FEST tests are running !</FONT><BR><BR>";
        message += "Emergency abort = 'Ctrl + Shift + A'";
        message += "</BODY></HTML>";

        ResizableTextViewFactory.createHtmlWindow(message, "Running FEST tests", true, 5000);

        // Use main thread to start Jmcs application using subclass.main() :
        if (App.getInstance() == null) {

            // disable use of System.exit()
            Bootstrapper.disableSystemExit(true);

            if (appClass != null) {
                logger.info("onSetUpOnce : starting application : {}", appClass);

                // disable dev LAF menu :
                System.setProperty("jmcs.laf.menu", "false");

                if (args != null) {
                    if (logger.isInfoEnabled()) {
                        logger.info("onSetUpOnce : using arguments      : {}", Arrays.toString(args));
                    }

                    application(appClass).withArgs(args).start();

                } else {

                    application(appClass).start();
                }

                // Waits for the splash screen to auto-hide ...
                pause(new Condition("AppReady") {
                    /**
                     * Checks if the condition has been satisfied.
                     * @return <code>true</code> if the condition has been satisfied, otherwise <code>false</code>.
                     */
                    @Override
                    public boolean test() {
                        return (Bootstrapper.isInState(ApplicationState.APP_READY));
                    }
                });

                pauseMedium();

                // To be sure that application frame is available:
                App.getFrame();

                // Disable the SwingExceptionHandler (conflict with EmergencyAbortListener):
                // Switch to logging (then exit) exception handler:
                MCSExceptionHandler.installExitExceptionHandler();
            }

            if (logger.isInfoEnabled()) {
                logger.info("onSetUpOnce : started application = {}", App.getInstance());
            }

            if (App.getInstance() == null) {
                throw new RuntimeException("unable to start application : " + appClass);
            }
        }
    }

    /**
     * Free other resources like the Swing application.
     * This method is called <strong>after</strong> executing <code>{@link #tearDownOnce()}</code>.
     */
    @AfterClass
    public static void onTearDownOnce() {
        final App app = App.getInstance();
        if (app != null) {
            if (logger.isInfoEnabled()) {
                logger.info("onTearDownOnce : exit application = {}", app);
            }

            Bootstrapper.stopApp(1);

            pauseLong();
        }
    }

    /**
     * Define the window fixture before each JUnit Test method.
     */
    @Override
    protected void onSetUp() {
        if (App.getInstance() == null) {
            throw new RuntimeException("No application started !");
        }

        listener = EmergencyAbortListener.registerInToolkit();
        window = getFrame(App.getFrame());
    }

    /**
     * Clean up resources of the window fixture after each JUnit Test method.
     */
    @Override
    protected void onTearDown() {
        if (listener != null) {
            listener.unregister();
        }

        // robot is already cleaned up in FestSwingCustomTestCaseTemplate
    }

    /*
     --- Utility methods  ---------------------------------------------------------
     */
    /**
     * Return a frame fixture given its title
     * @param title frame title
     * @return frame fixture
     */
    protected final FrameFixture getFrame(final String title) {

        // IMPORTANT: note the call to 'robot()'
        // we must use the Robot from FestSwingCustomTestCaseTemplate
        final FrameMatcher matcher = FrameMatcher.withTitle(title).andShowing();

        final String description = "frame to be found using matcher " + matcher;

        final ComponentFoundCondition condition = new ComponentFoundCondition(description, robot().finder(), matcher);

        pause(condition, LONG_DELAY);

        final FrameFixture frameFixture = new FrameFixture(robot(), (Frame) condition.found());

        // shows the frame to test on top:
        frameFixture.moveToBack();
        frameFixture.moveToFront();

        return frameFixture;
    }

    /**
     * Return a frame fixture given its name
     * @param frame frame to hook
     * @return frame fixture
     */
    protected final FrameFixture getFrame(final Frame frame) {
        // IMPORTANT: note the call to 'robot()'
        // we must use the Robot from FestSwingCustomTestCaseTemplate
        final FrameFixture frameFixture = new FrameFixture(robot(), frame);

        // shows the frame to test
        frameFixture.show();
        frameFixture.moveToFront();

        return frameFixture;
    }

    /**
     * Close File overwrite confirm dialog clicking on "Replace" button
     */
    protected final void confirmDialogFileOverwrite() {
        try {
            // if file already exists, a confirm message appears :
            final JOptionPaneFixture optionPane = window.optionPane();

            if (optionPane != null) {
                // confirm file overwrite :
                optionPane.buttonWithText("Replace").click();
            }

        } catch (RuntimeException re) {
            // happens when the confirm message does not occur :
            logger.debug("lookup failure : ", re);
        }
    }

    /**
     * Close Save confirm dialog clicking on "Don't Save" button
     */
    protected final void confirmDialogDontSave() {
        try {
            // if a message appears :
            final JOptionPaneFixture optionPane = window.optionPane();

            if (optionPane != null) {
                // close confirm dialog :
                optionPane.buttonWithText("Don't Save").click();
            }

        } catch (RuntimeException re) {
            // happens when the confirm message does not occur :
            logger.debug("lookup failure : ", re);
        }
    }

    /**
     * Close any option pane
     * @return true if a message was closed
     */
    protected final boolean closeMessage() {
        try {
            // if a message appears :
            final JOptionPaneFixture optionPane = window.optionPane();

            if (optionPane != null) {
                // click OK :
                optionPane.okButton().click();

                return true;
            }

        } catch (RuntimeException re) {
            // happens when the confirm message does not occur :
            logger.debug("lookup failure : ", re);
        }
        return false;
    }

    /**
     * Capture a screenshot of the application window, crop it and save it using the given file name
     * @param fileName the file name (including the png extension)
     * @param x the X coordinate of the upper-left corner of the
     *          specified rectangular region
     * @param y the Y coordinate of the upper-left corner of the
     *          specified rectangular region
     * @param w the width of the specified rectangular region (<=0 indicates to use the width of screenshot image)
     * @param h the height of the specified rectangular region (<=0 indicates to use the height of screenshot image)
     */
    protected final void saveCroppedScreenshotOf(final String fileName, final int x, final int y, final int w, final int h) {
        final BufferedImage image = takeScreenshotOf(window);

        final int width = (w <= 0) ? image.getWidth() : w;
        final int height = (h <= 0) ? image.getHeight() : h;

        final BufferedImage croppedImage = image.getSubimage(x, y, width, height);

        saveImage(croppedImage, fileName);
    }
}
