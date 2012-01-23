/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model.test;

import fr.jmmc.jmal.complex.MutableComplex;
import fr.jmmc.jmal.image.ImageViewer;
import fr.jmmc.jmal.model.ModelComputeContext;
import fr.jmmc.jmal.model.ModelDefinition;
import fr.jmmc.jmal.model.ModelManager;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmcs.App;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author bourgesl
 */
public class TestModel {

    /** standard visibility amplitude range [0;1] */
    public final static float[] RANGE_AMPLITUDE = new float[]{0f, 1f};
    /** standard visibility phase range [-PI;PI] */
    public final static float[] RANGE_PHASE = new float[]{(float) -Math.PI, (float) Math.PI};

    private TestModel() {
        // no-op
    }

    /**
     * Return the frequencies in rad-1
     *
     * @param width number of values in the range [-max;max]
     * @param max maximum base line length in meter
     * @return sampled frequencies in rad-1
     */
    private static double[] computeFrequencyRange(final int width, final double max) {
        final double[] freq = new double[width];

        final double min = -1e6D * max;
        final double step = 2e6D * max / width;

        freq[0] = min;
        for (int i = 1; i < width; i++) {
            freq[i] = freq[i - 1] + step;
        }
        freq[width - 1] = 1e6D * max;

        return freq;
    }

    private static List<Model> punctModels() {

        final ModelManager mm = ModelManager.getInstance();

        final List<Model> models = new ArrayList<Model>();

        Model model;

        // punct1 :
        model = mm.createModel(ModelDefinition.MODEL_PUNCT);

        ModelManager.setParameterValue(model, ModelDefinition.PARAM_FLUX_WEIGHT, 1.0);
        ModelManager.setParameterValue(model, ModelDefinition.PARAM_X, 0);
        ModelManager.setParameterValue(model, ModelDefinition.PARAM_Y, 0);

        models.add(model);

        // punct2 :
        model = mm.createModel(ModelDefinition.MODEL_PUNCT);

        ModelManager.setParameterValue(model, ModelDefinition.PARAM_FLUX_WEIGHT, 1);
        ModelManager.setParameterValue(model, ModelDefinition.PARAM_X, 1);
        ModelManager.setParameterValue(model, ModelDefinition.PARAM_Y, 0);

        models.add(model);
        /*
         * // punct3 :
         * model = mm.createModel(ModelDefinition.MODEL_PUNCT);
         *
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_FLUX_WEIGHT, 0.4);
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_X, 0);
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_Y, 1);
         *
         * models.add(model);
         *
         * // punct4 :
         * model = mm.createModel(ModelDefinition.MODEL_PUNCT);
         *
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_FLUX_WEIGHT, 0.2);
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_X, 1);
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_Y, 1);
         *
         * models.add(model);
         */
        return models;
    }

    private static List<Model> diskModels() {

        final ModelManager mm = ModelManager.getInstance();

        final List<Model> models = new ArrayList<Model>();

        Model model;

        // disk1 :
        model = mm.createModel(ModelDefinition.MODEL_DISK);

        ModelManager.setParameterValue(model, ModelDefinition.PARAM_FLUX_WEIGHT, 3.0);
        ModelManager.setParameterValue(model, ModelDefinition.PARAM_X, 0);
        ModelManager.setParameterValue(model, ModelDefinition.PARAM_Y, 0);
        ModelManager.setParameterValue(model, ModelDefinition.PARAM_DIAMETER, 30);

        models.add(model);

        // disk2 :
    /*
         * model = mm.createModel(ModelDefinition.MODEL_DISK);
         *
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_FLUX_WEIGHT, 1.0);
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_X, 1);
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_Y, 0);
         * ModelManager.setParameterValue(model, ModelDefinition.PARAM_DIAMETER, 1);
         *
         * models.add(model);
         */

        return models;
    }

    private static void computeImage(final ImageViewer iv, final List<Model> models) {
        // amplitude or phase :
        final boolean doAmp = true;

        // Image :
        final int width = 1024;
        final int height = width;

        double[] ufreq;
        double[] vfreq;

        MutableComplex[] vis;
        final float[] img = new float[width * height];
        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        // Start the computations :
        final long start = System.nanoTime();

        // prepare models once for all:
        final ModelComputeContext context = ModelManager.getInstance().prepareModels(models, width * height);

        // ASPRO = 200m :

        // LITpro = 10m
        final double maxBL = 10D;

        final double[] freq = computeFrequencyRange(width, maxBL);

        ufreq = new double[width * height];
        vfreq = new double[width * height];

        for (int j = 0; j < height; j++) {
            for (int i = 0, k = 0; i < width; i++) {
                k = width * j + i;
                ufreq[k] = freq[i];
                vfreq[k] = freq[j];
            }
        }
        /*
         * System.out.println("computeUV : duration = " + 1e-6d * (System.nanoTime() - start) + " ms.");
         */
        vis = ModelManager.getInstance().computeModels(context, ufreq, vfreq);
        /*
         * System.out.println("computeVIS : duration = " + 1e-6d * (System.nanoTime() - start) + " ms.");
         */
        // Inverse Y axis :

        float val;
        final float[] stdRange;

        if (doAmp) {
            stdRange = RANGE_AMPLITUDE;


            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    val = (float) vis[width * (height - 1 - j) + i].abs();
                    img[width * j + i] = val;
                    if (val < min) {
                        min = val;
                    }
                    if (val > max) {
                        max = val;
                    }
                }
            }
        } else {
            stdRange = RANGE_PHASE;


            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    val = (float) vis[width * (height - 1 - j) + i].getArgument();
                    img[width * j + i] = val;
                    if (val < min) {
                        min = val;
                    }
                    if (val > max) {
                        max = val;
                    }
                }
            }
        }
        /*
         * System.out.println("VIS min = " + min);
         * System.out.println("VIS max = " + max);
         */
        min = Math.min(min, stdRange[0]);
        max = Math.max(max, stdRange[1]);
        /*
         * System.out.println("VIS min (fixed) = " + min);
         * System.out.println("VIS max (fixed) = " + max);
         */

        System.out.println("image : duration = " + 1e-6d * (System.nanoTime() - start) + " ms.");

        iv.getImageCanvas().initImage(width, height, img, min, max);
    }

    /**
     * Test code
     *
     * @param args
     */
    public static void main(String[] args) {

        // invoke App method to initialize logback now:
        App.isReady();

        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);

        final ImageViewer iv = new ImageViewer();
        iv.getImageCanvas().setAntiAliasing(true);
        iv.getImageCanvas().setDrawTicks(false);

        List<Model> models;

        // models = punctModels();

        models = diskModels();

        computeImage(iv, models);

        iv.setPreferredSize(new Dimension(500, 500));
        iv.pack();
        iv.setVisible(true);
    }
}
