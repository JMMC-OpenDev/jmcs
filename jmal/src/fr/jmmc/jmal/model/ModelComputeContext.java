/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.complex.MutableComplex;
import fr.jmmc.jmal.model.function.math.PunctFunction;
import java.util.List;

/**
 * This class holds several variables used during model computations: model functions ...
 *
 * @author bourgesl
 */
public final class ModelComputeContext {

    /* members */
    /** list of model functions to compute */
    private final List<PunctFunction> modelFunctions;
    /** uv frequency count used to preallocate arrays */
    private final int freqCount;
    /* output */
    /** model complex contribution to visibility */
    private final MutableComplex modelVis = new MutableComplex();
    /** complex visiblity array */
    private MutableComplex[] vis = null;

    /**
     * Protected constructor
     *
     * @param freqCount uv frequency count used to preallocate arrays
     * @param modelFunctions list of model functions to compute
     */
    ModelComputeContext(final int freqCount, final List<PunctFunction> modelFunctions) {
        this.freqCount = freqCount;
        this.modelFunctions = modelFunctions;

        allocateVis();
    }

    /**
     * Allocate complex visiblity array for outputs
     */
    private void allocateVis() {
        this.vis = new MutableComplex[this.freqCount];

        // initialize visibilities to (0,0):
        for (int i = 0, len = this.freqCount; i < len; i++) {
            this.vis[i] = new MutableComplex(0d, 0d);
        }
    }

    /**
     * Return the uv frequency count
     *
     * @return uv frequency count
     */
    int getFreqCount() {
        return freqCount;
    }

    /**
     * Return the list of model functions to compute
     *
     * @return list of model functions to compute
     */
    List<PunctFunction> getModelFunctions() {
        return modelFunctions;
    }

    /* outputs */
    /**
     * Return the model complex contribution to visibility
     *
     * @return model complex contribution to visibility
     */
    MutableComplex getModelVis() {
        return modelVis;
    }

    /**
     * Return the complex visiblity array
     *
     * @return complex visiblity array
     */
    public MutableComplex[] resetAndGetVis() {
        // reset visibilities to (0,0):
        for (int i = 0, len = this.freqCount; i < len; i++) {
            vis[i].updateComplex(0d, 0d);
        }
        return vis;
    }
}
