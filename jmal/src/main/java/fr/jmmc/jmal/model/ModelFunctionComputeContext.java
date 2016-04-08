/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.model.function.math.PunctFunction;
import java.util.List;

/**
 * This class holds several variables used during model computations: model functions ...
 *
 * @author bourgesl
 */
public final class ModelFunctionComputeContext extends ModelComputeContext {

    /* members */
    /** list of model functions to compute */
    private final List<PunctFunction> modelFunctions;

    /**
     * Protected constructor
     *
     * @param freqCount uv frequency count used to preallocate arrays
     * @param modelFunctions list of model functions to compute
     */
    ModelFunctionComputeContext(final int freqCount, final List<PunctFunction> modelFunctions) {
        super(freqCount);
        this.modelFunctions = modelFunctions;
    }

    /**
     * Return the list of model functions to compute
     *
     * @return list of model functions to compute
     */
    List<PunctFunction> getModelFunctions() {
        return modelFunctions;
    }
}
