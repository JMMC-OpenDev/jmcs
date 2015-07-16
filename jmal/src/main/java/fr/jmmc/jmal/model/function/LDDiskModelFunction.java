/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.function;

import fr.jmmc.jmal.model.AbstractModelFunction;
import fr.jmmc.jmal.model.function.math.LDDiskFunction;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmal.model.targetmodel.Parameter;

/**
 * This ModelFunction implements the limb darkened disk model (quadratic)
 * 
 * @author Laurent BOURGES.
 */
public final class LDDiskModelFunction extends AbstractModelFunction<LDDiskFunction> {

    /* Model constants */
    /** model description */
    private static final String MODEL_DESC =
                                "Returns the Fourier transform of a center-to-limb darkened disk of diameter DIAMETER \n"
            + "(milliarcsecond) centered at coordinates (X,Y) (milliarcsecond). \n\n"
            + "The brightness distribution o, if expressed versus mu, the cosine of the azimuth of \n"
            + "a surface element of the star, follows a quadratic law of coefficients \n"
            + "A1_COEFF, A2_COEFF ([-1,1]), and is normalized for mu = 1 (center of the star). \n"
            + "o(mu) = 1 -A1_COEFF(1-mu) - A2_COEFF(1-mu)^2. \n\n"
            + "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n"
            + "The function returns an error if DIAMETER is negative or if A1_COEFF or A2_coeff is \n"
            + "outside bounds [-1,1]";
    /** Parameter type for the parameter a1_coeff */
    public final static String PARAM_A1 = "a1_coeff";
    /** Parameter type for the parameter a2_coeff */
    public final static String PARAM_A2 = "a2_coeff";

    /**
     * Constructor
     */
    public LDDiskModelFunction() {
        super();
    }

    /**
     * Return the model type
     * @return model type
     */
    @Override
    public String getType() {
        return MODEL_LDDISK;
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

        Parameter param;

        param = new Parameter();
        param.setNameAndType(PARAM_A1);
        param.setMinValue(-1D);
        param.setValue(0D);
        param.setMaxValue(1D);
        model.getParameters().add(param);

        param = new Parameter();
        param.setNameAndType(PARAM_A2);
        param.setMinValue(-1D);
        param.setValue(0D);
        param.setMaxValue(1D);
        model.getParameters().add(param);

        return model;
    }

    /**
     * Create the computation function for the given model :
     * Get model parameters to fill the function context
     * @param model model instance
     * @return model function
     */
    @Override
    protected LDDiskFunction createFunction(final Model model) {
        final LDDiskFunction function = new LDDiskFunction();

        // Get parameters to fill the context :
        function.setX(getParameterValue(model, PARAM_X));
        function.setY(getParameterValue(model, PARAM_Y));
        function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

        function.setDiameter(getParameterValue(model, PARAM_DIAMETER));
        function.setA1(getParameterValue(model, PARAM_A1));
        function.setA2(getParameterValue(model, PARAM_A2));

        return function;
    }
}
