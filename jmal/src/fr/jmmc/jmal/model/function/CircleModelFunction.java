/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.function;

import fr.jmmc.jmal.model.AbstractModelFunction;
import fr.jmmc.jmal.model.function.math.CircleFunction;
import fr.jmmc.jmal.model.targetmodel.Model;

/**
 * This ModelFunction implements the circle model
 * 
 * @author Laurent BOURGES.
 */
public final class CircleModelFunction extends AbstractModelFunction<CircleFunction> {

    /* Model constants */
    /** model description */
    private static final String MODEL_DESC =
                                "Returns the Fourier transform of a normalized uniform circle of diameter DIAMETER \n"
            + "(milliarcsecond) and centered at coordinates (X,Y) (milliarcsecond). \n\n"
            + "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n"
            + "The function returns an error if DIAMETER is negative.";

    /**
     * Constructor
     */
    public CircleModelFunction() {
        super();
    }

    /**
     * Return the model type
     * @return model type
     */
    @Override
    public String getType() {
        return MODEL_CIRCLE;
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

        addPositiveParameter(model, PARAM_DIAMETER);

        return model;
    }

    /**
     * Create the computation function for the given model :
     * Get model parameters to fill the function context
     * @param model model instance
     * @return model function
     */
    @Override
    protected CircleFunction createFunction(final Model model) {
        final CircleFunction function = new CircleFunction();

        // Get parameters to fill the context :
        function.setX(getParameterValue(model, PARAM_X));
        function.setY(getParameterValue(model, PARAM_Y));
        function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

        function.setDiameter(getParameterValue(model, PARAM_DIAMETER));

        return function;
    }
}
