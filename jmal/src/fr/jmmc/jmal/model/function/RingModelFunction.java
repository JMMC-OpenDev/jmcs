/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.function;

import fr.jmmc.jmal.model.AbstractModelFunction;
import fr.jmmc.jmal.model.ModelVariant;
import fr.jmmc.jmal.model.function.math.RingFunction;
import fr.jmmc.jmal.model.targetmodel.Model;

/**
 * This ModelFunction implements the ring model
 * 
 * @author Laurent BOURGES.
 */
public final class RingModelFunction extends AbstractModelFunction<RingFunction> {

    /* Model constants */
    /** ring model description */
    private static final String MODEL_RING_DESC =
                                "Returns the Fourier transform of a normalized uniform ring with internal diameter \n"
            + "DIAMETER (milliarcsecond) and external diameter DIAMETER + WIDTH centered at coordinates \n"
            + "(X,Y) (milliarcsecond). \n\n"
            + "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n"
            + "The function returns an error if DIAMETER or WIDTH are negative.";
    /** elongated ring model description */
    private static final String MODEL_ERING_DESC =
                                "Returns the Fourier transform of a normalized uniform elongated ring centered at \n"
            + "coordinates (X,Y) (milliarcsecond). The sizes of the function in two orthogonal directions \n"
            + "are given by the narrowest internal diameter (MINOR_INTERNAL_DIAMETER) and by the ratio \n"
            + "ELONG_RATIO between the widest internal diameter and MINOR_INTERNAL_DIAMETER, \n"
            + "in the same way as for an ellipse (the elongation is along the major_axis) : \n\n"
            + "ELONG_RATIO = MAJOR_INTERNAL_DIAMETER / MINOR_INTERNAL_DIAMETER. \n"
            + "In the direction of MINOR_INTERNAL_DIAMETER, the external diameter is \n"
            + "MINOR_INTERNAL_DIAMETER + WIDTH. In the direction of the widest internal diameter, \n"
            + "the width is magnified by the ratio ELONG_RATIO, so that the external diameter is \n"
            + "the elongated MAJOR_INTERNAL_DIAMETER + WIDTH * ELONG_RATIO. \n"
            + "MAJOR_AXIS_POS_ANGLE is measured in degrees, from the positive vertical semi-axis \n"
            + "(i.e. North direction) towards to the positive horizontal semi-axis (i.e. East direction). \n"
            + "For avoiding degenerescence, the domain of variation of MAJOR_AXIS_POS_ANGLE is 180 \n"
            + "degrees, for ex. from 0 to 180 degrees. \n\n"
            + "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n"
            + "The function returns an error if MINOR_INTERNAL_DIAMETER is negative or if ELONG_RATIO \n"
            + "is smaller than 1.";
    /** flattened ring model description */
    private static final String MODEL_FRING_DESC =
                                "Returns the Fourier transform of a normalized uniform flattened ring centered at \n"
            + "coordinates (X,Y) (milliarcsecond). The sizes of the function in two orthogonal directions \n"
            + "are given by the widest internal diameter (MAJOR_INTERNAL_DIAMETER) and by the ratio \n"
            + "FLATTEN_RATIO between MAJOR_INTERNAL_DIAMETER and the narrowest internal diameter, \n"
            + "in the same way as for an ellipse (the flattening is along the minor axis) : \n\n"
            + "FLATTEN_RATIO = MAJOR_INTERNAL_DIAMETER / MINOR_INTERNAL_DIAMETER. \n"
            + "In the direction of MAJOR_INTERNAL_DIAMETER, the external diameter is \n"
            + "MAJOR_INTERNAL_DIAMETER + WIDTH. In the direction of the narrowest internal diameter, \n"
            + "the width is decreased by the ratio FLATTEN_RATIO, so that the external diameter is \n"
            + "the flattened MINOR_INTERNAL_DIAMETER + WIDTH / FLATTEN_RATIO. \n"
            + "MINOR_AXIS_POS_ANGLE is measured in degrees, from the positive vertical semi-axis \n"
            + "(i.e. North direction) towards to the positive horizontal semi-axis (i.e. East direction). \n"
            + "For avoiding degenerescence, the domain of variation of MINOR_AXIS_POS_ANGLE is 180 \n"
            + "degrees, for ex. from 0 to 180 degrees. \n\n"
            + "FLUX_WEIGHT is the intensity coefficient. FLUX_WEIGHT=1 means total energy is 1. \n\n"
            + "The function returns an error if MAJOR_INTERNAL_DIAMETER is negative or if FLATTEN_RATIO \n"
            + "is smaller than 1.";
    /** Parameter type for the parameter width */
    public final static String PARAM_WIDTH = "width";

    /* specific parameters for elongated ring */
    /** Parameter type for the parameter minor_internal_diameter */
    public final static String PARAM_MINOR_INTERNAL_DIAMETER = "minor_internal_diameter";

    /* specific parameters for flattened ring */
    /** Parameter type for the parameter major_internal_diameter */
    public final static String PARAM_MAJOR_INTERNAL_DIAMETER = "major_internal_diameter";

    /* members */
    /** model variant */
    private final ModelVariant variant;

    /**
     * Constructor
     */
    public RingModelFunction() {
        this(ModelVariant.Standard);
    }

    /**
     * Constructor for the given variant
     * @param variant the model variant
     */
    public RingModelFunction(final ModelVariant variant) {
        super();
        this.variant = variant;
    }

    /**
     * Return the model type
     * @return model type
     */
    @Override
    public String getType() {
        switch (this.variant) {
            default:
            case Standard:
                return MODEL_RING;
            case Elongated:
                return MODEL_ERING;
            case Flattened:
                return MODEL_FRING;
        }
    }

    /**
     * Return the model description
     * @return model description
     */
    @Override
    public String getDescription() {
        switch (this.variant) {
            default:
            case Standard:
                return MODEL_RING_DESC;
            case Elongated:
                return MODEL_ERING_DESC;
            case Flattened:
                return MODEL_FRING_DESC;
        }
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

        switch (this.variant) {
            default:
            case Standard:
                addPositiveParameter(model, PARAM_DIAMETER);
                addPositiveParameter(model, PARAM_WIDTH);
                break;
            case Elongated:
                addPositiveParameter(model, PARAM_MINOR_INTERNAL_DIAMETER);
                addRatioParameter(model, PARAM_ELONG_RATIO);
                addPositiveParameter(model, PARAM_WIDTH);
                addAngleParameter(model, PARAM_MAJOR_AXIS_ANGLE);
                break;
            case Flattened:
                addPositiveParameter(model, PARAM_MAJOR_INTERNAL_DIAMETER);
                addRatioParameter(model, PARAM_FLATTEN_RATIO);
                addPositiveParameter(model, PARAM_WIDTH);
                addAngleParameter(model, PARAM_MINOR_AXIS_ANGLE);
                break;
        }

        return model;
    }

    /**
     * Create the computation function for the given model :
     * Get model parameters to fill the function context
     * @param model model instance
     * @return model function
     */
    @Override
    protected RingFunction createFunction(final Model model) {
        final RingFunction function = new RingFunction();

        // Get parameters to fill the context :
        function.setX(getParameterValue(model, PARAM_X));
        function.setY(getParameterValue(model, PARAM_Y));
        function.setFluxWeight(getParameterValue(model, PARAM_FLUX_WEIGHT));

        // Variant specific code :
        switch (this.variant) {
            default:
            case Standard:
                function.setDiameter(getParameterValue(model, PARAM_DIAMETER));
                break;
            case Elongated:
                function.setDiameter(getParameterValue(model, PARAM_MINOR_INTERNAL_DIAMETER));
                function.setAxisRatio(getParameterValue(model, PARAM_ELONG_RATIO));
                function.setPositionAngle(getParameterValue(model, PARAM_MAJOR_AXIS_ANGLE));
                break;
            case Flattened:
                function.setDiameter(getParameterValue(model, PARAM_MAJOR_INTERNAL_DIAMETER));
                function.setAxisRatio(1d / getParameterValue(model, PARAM_FLATTEN_RATIO));
                function.setPositionAngle(getParameterValue(model, PARAM_MINOR_AXIS_ANGLE));
                break;
        }
        function.setWidth(getParameterValue(model, PARAM_WIDTH));

        return function;
    }
}
