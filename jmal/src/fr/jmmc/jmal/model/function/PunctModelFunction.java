/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.function;

import fr.jmmc.jmal.model.AbstractModelFunction;
import fr.jmmc.jmal.model.function.math.PunctFunction;
import fr.jmmc.jmal.model.targetmodel.Model;

/**
 * This ModelFunction implements the punct model
 * 
 * @author Laurent BOURGES.
 */
public final class PunctModelFunction extends AbstractModelFunction<PunctFunction> {

    /* Model constants */
    /** model description */
    private final static String MODEL_DESC =
                                "Returns the Fourier transform of a punctual object (Dirac function) at coordinates (X,Y) \n"
            + "(milliarcsecond). \n\n"
            + "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1.";

    /**
     * Constructor
     */
    public PunctModelFunction() {
        super();
    }

    /**
     * Return the model type
     * @return model type
     */
    @Override
    public String getType() {
        return MODEL_PUNCT;
    }

    /**
     * Return the model description
     * @return model description
     */
    @Override
    public String getDescription() {
        return MODEL_DESC;
    }

    /**
     * Return a new Model instance with its parameters and default values
     * @return new Model instance
     */
    @Override
    public Model newModel() {
        final Model model = super.newModel();

        model.setNameAndType(getType());
        model.setDesc(getDescription());

        return model;
    }

    /**
     * Create the computation function for the given model :
     * Get model parameters to fill the function context
     * @param model model instance
     * @return model function
     */
    @Override
    protected PunctFunction createFunction(final Model model) {
        final PunctFunction function = new PunctFunction();

        // Get parameters to fill the context :
        function.setX(getParameterValue(model, PARAM_X));
        function.setY(getParameterValue(model, PARAM_Y));
        function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

        return function;
    }
}
