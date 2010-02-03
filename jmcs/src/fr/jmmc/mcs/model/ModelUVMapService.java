/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ModelUVMapService.java,v 1.2 2010-02-03 16:05:46 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/02/03 09:51:46  bourgesl
 * uv map rendering service
 *
 */
package fr.jmmc.mcs.model;

import fr.jmmc.mcs.image.ColorModels;
import fr.jmmc.mcs.image.ImageUtils;
import fr.jmmc.mcs.model.targetmodel.Model;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.math.complex.Complex;

/**
 * This class generates an UV Map Image for given target Models and an UV area
 * @author bourgesl
 */
public class ModelUVMapService {

  /** Class Name */
  private static final String className_ = "fr.jmmc.mcs.model.ModelUVMapService";
  /** Class logger */
  private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
          className_);
  /** default image width / height */
  private final static int DEFAULT_IMAGE_SIZE = 256;

  /**
   * Image modes (amplitude, phase)
   */
  public enum ImageMode {

    /** Amplitude */
    AMP,
    /** Phase */
    PHASE
  }

  /**
   * Forbidden constructor
   */
  private ModelUVMapService() {
    // no-op
  }

  /**
   * Compute the UV Map for the given models and UV ranges
   * @param models list of models to use
   * @param uMin minimum U frequency in rad-1
   * @param uMax maximum U frequency in rad-1
   * @param vMin minimum V frequency in rad-1
   * @param vMax maximum V frequency in rad-1
   * @param mode image mode (amplitude or phase)
   * @return new image
   */
  public static BufferedImage computeUVMap(final List<Model> models,
          final double uMin, final double uMax,
          final double vMin, final double vMax,
          final ImageMode mode) {
    return computeUVMap(models, uMin, uMax, vMin, vMax, mode, DEFAULT_IMAGE_SIZE, ColorModels.getDefaultColorModel());
  }

  /**
   * Compute the UV Map for the given models and UV ranges
   * @param models list of models to use
   * @param uMin minimum U frequency in rad-1
   * @param uMax maximum U frequency in rad-1
   * @param vMin minimum V frequency in rad-1
   * @param vMax maximum V frequency in rad-1
   * @param mode image mode (amplitude or phase)
   * @param imageSize number of pixels for both width and height of the generated image
   * @param colorModel color model to use
   * @return new image
   */
  public static BufferedImage computeUVMap(final List<Model> models,
          final double uMin, final double uMax,
          final double vMin, final double vMax,
          final ImageMode mode,
          final int imageSize,
          final IndexColorModel colorModel) {

    BufferedImage img = null;

    if (models != null && !models.isEmpty()) {

      /** Get the current thread to check if the computation is interrupted */
      final Thread currentThread = Thread.currentThread();

      // Start the computations :
      final long start = System.nanoTime();

      try {
        // square size :
        final int size = imageSize * imageSize;

        // this step indicates when the thread.isInterrupted() is called in the for loop
        final int stepInterrupt = size / 20;

        // 1 - Prepare UFreq and VFreq arrays :
        double[] u = computeFrequencySamples(imageSize, uMin, uMax);
        double[] v = computeFrequencySamples(imageSize, vMin, vMax);

        // fast interrupt :
        if (currentThread.isInterrupted()) {
          return null;
        }

        double[] ufreq = new double[size];
        double[] vfreq = new double[size];

        // Note : the image is produced from an array where 0,0 corresponds to the upper left corner
        // whereas it corresponds in UV to the lower U and Upper V coordinates => inverse the V axis

        for (int j = 0, l = 0; j < imageSize; j++) {
          // inverse the v axis for the image :
          l = imageSize - j - 1;
          for (int i = 0, k = 0; i < imageSize; i++) {
            k = imageSize * j + i;
            ufreq[k] = u[i];
            vfreq[k] = v[l];
          }
        }
        // force GC :
        u = null;
        v = null;

        // fast interrupt :
        if (currentThread.isInterrupted()) {
          return null;
        }

        // 2 - Compute complex visibility for the given models :

        Complex[] vis = ModelManager.getInstance().computeModels(ufreq, vfreq, models);

        // fast interrupt :
        if (currentThread.isInterrupted()) {
          return null;
        }

        // force GC :
        ufreq = null;
        vfreq = null;

        // 3 - Extract the amplitude/phase to get the uv map :
        float[] data = new float[size];

        float val;
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        switch (mode) {
          case AMP:
            for (int i = 0; i < size; i++) {
              // amplitude = complex modulus (abs in commons-math) :
              val = (float) vis[i].abs();
              data[i] = val;

              if (val < min) {
                min = val;
              }
              if (val > max) {
                max = val;
              }
              // fast interrupt :
              if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
                return null;
              }
            }
            if (logger.isLoggable(Level.FINE)) {
              logger.fine("VIS_AMP in [" + min + ", " + max + "]");
            }
            break;
          case PHASE:
            for (int i = 0; i < size; i++) {
              // phase [-PI;PI] = complex phase (argument in commons-math) :
              val = (float) vis[i].getArgument();
              data[i] = val;

              if (val < min) {
                min = val;
              }
              if (val > max) {
                max = val;
              }
              // fast interrupt :
              if (i % stepInterrupt == 0 && currentThread.isInterrupted()) {
                return null;
              }
            }
            if (logger.isLoggable(Level.FINE)) {
              logger.fine("VIS_PHI in [" + min + ", " + max + "]");
            }
            break;
          default:
        }

        // force GC :
        vis = null;

        // fast interrupt :
        if (currentThread.isInterrupted()) {
          return null;
        }

        // 4 - Get the image with the given color model :

        img = ImageUtils.createImage(imageSize, imageSize, data, min, max, colorModel);

        // force gc :
        data = null;

        // fast interrupt :
        if (currentThread.isInterrupted()) {
          return null;
        }

      } catch (RuntimeException re) {
        logger.log(Level.SEVERE, "runtime exception : ", re);
      }

      if (logger.isLoggable(Level.INFO)) {
        logger.info("compute : duration = " + 1e-6d * (System.nanoTime() - start) + " ms.");
      }
    }

    return img;
  }

  /**
   * Return the frequencies in rad-1
   * @param nb number of sampled values
   * @param min minimum frequency value
   * @param max maximum frequency value
   * @return sampled frequencies in rad-1
   */
  private static double[] computeFrequencySamples(final int nbSamples, final double min, final double max) {
    final double[] freq = new double[nbSamples];

    final double step = (max - min) / nbSamples;

    freq[0] = min;
    for (int i = 1; i < nbSamples; i++) {
      freq[i] = freq[i - 1] + step;
    }

    return freq;
  }
}
