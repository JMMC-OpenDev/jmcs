/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.complex.MutableComplex;

/**
 * This class holds several variables used during model computations ...
 *
 * @author bourgesl
 */
public class ModelComputeContext {

    /* members */
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
     */
    protected ModelComputeContext(final int freqCount) {
        this.freqCount = freqCount;

        allocateVis();
    }

    /**
     * Allocate complex visiblity array for outputs
     */
    private final void allocateVis() {
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
    public final int getFreqCount() {
        return freqCount;
    }

    /* outputs */
    /**
     * Return the model complex contribution to visibility
     *
     * @return model complex contribution to visibility
     */
    public final MutableComplex getModelVis() {
        return modelVis;
    }

    /**
     * Return the complex visiblity array
     *
     * @return complex visiblity array
     */
    public final MutableComplex[] resetAndGetVis() {
        // reset visibilities to (0,0):
        for (int i = this.freqCount - 1; i >= 0; i--) {
            vis[i].updateComplex(0d, 0d);
        }
        return vis;
    }
}
