/*
 * Created on May 6, 2007
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright @2007-2010 the original author or authors.
 */
package fest.common;

import static fest.common.FestSwingCustomJUnitTestCase.getProjectFolderPath;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import static org.fest.swing.core.FocusOwnerFinder.focusOwner;
import static org.fest.swing.edt.GuiActionRunner.execute;
import static org.fest.swing.image.ImageFileExtensions.PNG;
import static org.fest.swing.query.ComponentLocationOnScreenQuery.locationOnScreen;
import static org.fest.swing.query.ComponentSizeQuery.sizeOf;
import static org.fest.util.Strings.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;

import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import org.fest.swing.annotation.RunsInEDT;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.image.ImageException;
import org.fest.swing.image.ImageFileWriter;
import org.fest.swing.image.ScreenshotTaker;
import org.fest.swing.util.RobotFactory;
import org.fest.util.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Understands taking screenshots of the desktop and GUI components.
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public final class CustomScreenshotTaker {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(FestSwingCustomJUnitTestCase.class.getName());

    /* Gnome 3 issue: window location and size are incorrects ! */
    private static final Rectangle MARGINS = new Rectangle();

    private static BufferedImage mousePointer = null;

    protected static BufferedImage getMousePointer() {
        if (mousePointer == null) {
            final String iconFolder = getProjectFolderPath() + "src/test/resources/icons/";
            try {
                final File file = new File(iconFolder, "left_ptr.png");

                logger.info("Loading file: " + file.getAbsolutePath());
                mousePointer = ImageIO.read(file);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return mousePointer;
    }

    /**
     * Adjust margins used to take screenshots of Windows (ie Frame, Dialogs ...)
     * @param       top   the inset from the top.
     * @param       left   the inset from the left.
     * @param       bottom   the inset from the bottom.
     * @param       right   the inset from the right.
     */
    public static void defineWindowMargins(int top, int left, int bottom, int right) {
        final int t = Math.max(0, top);
        final int l = Math.max(0, left);
        final int b = Math.max(0, bottom);
        final int r = Math.max(0, right);
        MARGINS.setBounds(l, t, l + r, t + b);
    }

    private final Robot robot;
    private final ImageFileWriter writer;

    /**
     * Creates a new <code>{@link ScreenshotTaker}</code>.
     * @throws ImageException if a AWT Robot (the responsible for taking screenshots) cannot be instantiated.
     */
    public CustomScreenshotTaker() {
        this(new ImageFileWriter(), new RobotFactory());
    }

    @VisibleForTesting
    CustomScreenshotTaker(ImageFileWriter writer, RobotFactory robotFactory) {
        this.writer = writer;
        try {
            robot = robotFactory.newRobotInPrimaryScreen();
        } catch (AWTException e) {
            throw new ImageException("Unable to create AWT Robot", e);
        }
    }

    /**
     * Takes a screenshot of the desktop and saves it as a PNG file.
     * @param imageFilePath the path of the file to save the screenshot to.
     * @throws ImageException if the given file path is <code>null</code> or empty.
     * @throws ImageException if the given file path does not end with ".png".
     * @throws ImageException if the given file path belongs to a non-empty directory.
     * @throws ImageException if an I/O error prevents the image from being saved as a file.
     */
    public void saveDesktopAsPng(String imageFilePath, final int mouseX, final int mouseY) {
        saveImage(takeDesktopScreenshot(), imageFilePath, mouseX, mouseY);
    }

    /**
     * Takes a screenshot of the desktop.
     * @return the screenshot of the desktop.
     * @throws SecurityException if <code>readDisplayPixels</code> permission is not granted.
     */
    public BufferedImage takeDesktopScreenshot() {
        Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return takeScreenshot(r);
    }

    /**
     * Takes a screenshot of the given <code>{@link java.awt.Component}</code> and saves it as a PNG file.
     * @param c the given component.
     * @param imageFilePath the path of the file to save the screenshot to.
     * @throws ImageException if the given file path is <code>null</code> or empty.
     * @throws ImageException if the given file path does not end with ".png".
     * @throws ImageException if the given file path belongs to a non-empty directory.
     * @throws ImageException if an I/O error prevents the image from being saved as a file.
     */
    public void saveComponentAsPng(Component c, String imageFilePath, int mouseX, int mouseY) {
        final Point locationOnScreen = locationOnScreen(c);
        if ((mouseX >= 0) && (mouseY >= 0)) {
            mouseX -= locationOnScreen.x;
            mouseY -= locationOnScreen.y;
        }
        saveImage(takeScreenshotOf(c, locationOnScreen), imageFilePath, mouseX, mouseY);
    }

    /**
     * Takes a screenshot of the given <code>{@link java.awt.Component}</code>.
     * @param c the given component.
     * @return a screenshot of the given component.
     * @throws SecurityException if <code>readDisplayPixels</code> permission is not granted.
     */
    public BufferedImage takeScreenshotOf(Component c) {
        final Point locationOnScreen = locationOnScreen(c);
        return takeScreenshotOf(c, locationOnScreen);
    }

    /**
     * Takes a screenshot of the given <code>{@link java.awt.Component}</code>.
     * @param c the given component.
     * @return a screenshot of the given component.
     * @throws SecurityException if <code>readDisplayPixels</code> permission is not granted.
     */
    private BufferedImage takeScreenshotOf(Component c, final Point locationOnScreen) {
        final Dimension size = sizeOf(c);

        int mX = 0;
        int mY = 0;
        int wX = 0;
        int wY = 0;

        if (c instanceof Window) {
            mX = MARGINS.x;
            mY = MARGINS.y;
            wX = MARGINS.width;
            wY = MARGINS.height;
        }
        wX = Math.max(0, size.width - wX);
        wY = Math.max(0, size.height - wY);

        final Rectangle r = new Rectangle(locationOnScreen.x + mX, locationOnScreen.y + mY, wX, wY);
        return takeScreenshot(r);
    }

    private BufferedImage takeScreenshot(Rectangle r) {
        JTextComponent textComponent = findFocusOwnerAndHideItsCaret();
        robot.waitForIdle();
        try {
            return takeScreenshot(robot, r);
        } finally {
            showCaretIfPossible(textComponent);
        }
    }

    @RunsInEDT
    private static JTextComponent findFocusOwnerAndHideItsCaret() {
        return execute(new GuiQuery<JTextComponent>() {
            @Override
            protected JTextComponent executeInEDT() {
                Component focusOwner = focusOwner();
                if (!(focusOwner instanceof JTextComponent)) {
                    return null;
                }
                JTextComponent textComponent = (JTextComponent) focusOwner;
                Caret caret = textComponent.getCaret();
                if (caret == null || !caret.isVisible()) {
                    return null;
                }
                caret.setVisible(false);
                return textComponent;
            }
        });
    }

    private static BufferedImage takeScreenshot(final Robot robot, final Rectangle r) {
        return execute(new GuiQuery<BufferedImage>() {
            @Override
            protected BufferedImage executeInEDT() {
                return robot.createScreenCapture(r);
            }
        });
    }

    private void showCaretIfPossible(JTextComponent textComponent) {
        if (textComponent == null) {
            return;
        }
        showCaretOf(textComponent);
        robot.waitForIdle();
    }

    @RunsInEDT
    private static void showCaretOf(final JTextComponent textComponent) {
        execute(new GuiTask() {
            @Override
            protected void executeInEDT() {
                Caret caret = textComponent.getCaret();
                if (caret != null) {
                    caret.setVisible(true);
                }
            }
        });
    }

    /**
     * Save the given image as a PNG file.
     * @param image the image to save.
     * @param filePath the path of the file to save the image to.
     * @throws ImageException if the given file path is <code>null</code> or empty.
     * @throws ImageException if the given file path does not end with ".png".
     * @throws ImageException if the given file path belongs to a non-empty directory.
     * @throws ImageException if an I/O error prevents the image from being saved as a file.
     */
    public void saveImage(BufferedImage image, String filePath, int mouseX, int mouseY) {
        validate(filePath);
        try {
            if ((mouseX >= 0) && (mouseY >= 0)) {
                mouseX -= MARGINS.x;
                mouseY -= MARGINS.y;
                
                // draw mouse pointer image:
                // System.out.println("draw mouse pointer at (" + mouseX + ", " + mouseY + ")");
                image.getGraphics().drawImage(getMousePointer(), mouseX, mouseY, null);
            }
            final File imgFile = new File(filePath);
            if (imgFile.exists()) {
                imgFile.delete();
            }
            writer.writeAsPng(image, filePath);
        } catch (Exception e) {
            throw new ImageException(concat("Unable to save image as ", quote(filePath)), e);
        }
    }

    private void validate(String imageFilePath) {
        if (isEmpty(imageFilePath)) {
            throw new ImageException("The image path cannot be empty");
        }
        if (!imageFilePath.endsWith(PNG)) {
            throw new ImageException(concat("The image file should be a ", PNG.toUpperCase(Locale.getDefault())));
        }
    }
}
