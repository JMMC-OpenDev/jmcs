/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import fr.jmmc.jmal.image.ColorModels;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;

/**
 *
 * @author bourgesl
 */
public class ColorModelsTest {

    @Test
    public void test() {
        for (String name : ColorModels.getColorModelNames()) {
            saveImage(name, ColorModels.getColorModelImage(name));
        }
    }

    private static void saveImage(final String name, final BufferedImage image) {
        try {
            final File file = new File("ColorModelsTest-" + name + ".png");

            System.out.println("Writing file: " + file.getAbsolutePath());;
            ImageIO.write(image, "PNG", file);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
