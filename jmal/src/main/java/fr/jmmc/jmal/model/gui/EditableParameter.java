/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model.gui;

import fr.jmmc.jmal.model.ModelDefinition;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmal.model.targetmodel.Parameter;

/**
 * This class represents a parameter in the model parameters table to act as an editor of the parameter
 * 
 * @author Laurent BOURGES.
 */
public final class EditableParameter implements Editable {

    /* members */
    /** model of the parameter */
    private final Model model;
    /** edited parameter */
    private final Parameter parameter;
    /** shared parameter flag */
    private final boolean shared;

    /**
     * Constructor with the given parameter
     * @param model parent model
     * @param parameter parameter to edit
     * @param shared shared parameter flag
     */
    public EditableParameter(final Model model, final Parameter parameter, final boolean shared) {
        this.model = model;
        this.parameter = parameter;
        this.shared = shared;
    }

    /**
     * Return the model
     * @return model
     */
    @Override
    public Model getModel() {
        return model;
    }

    /**
     * Indicates if the parameter is a shared parameter
     * @return true if the parameter is a shared parameter
     */
    @Override
    public boolean isShared() {
        return shared;
    }

    /**
     * Indicates if the parameter is a position
     * @return true if the parameter is a position
     */
    @Override
    public boolean isPosition() {
        final String type = getType();
        return ModelDefinition.PARAM_X.equals(type)
                || ModelDefinition.PARAM_Y.equals(type);
    }

    /* Parameter methods */

    /* --- read-only parameter attributes ------------------------------------- */
    /**
     * Gets the value of the name property.
     * @return value of the name property
     */
    @Override
    public String getName() {
        return this.parameter.getName();
    }

    /**
     * Gets the value of the type property.
     * @return value of the type property
     */
    @Override
    public String getType() {
        return this.parameter.getType();
    }

    /**
     * Gets the value of the units property.
     * @return value of the units property
     */
    @Override
    public String getUnits() {
        return this.parameter.getUnits();
    }

    /* --- writable parameter attributes -------------------------------------- */
    /**
     * Gets the value of the value property.
     * @return value property
     */
    @Override
    public double getValue() {
        return this.parameter.getValue();
    }

    /**
     * Gets the value of the minValue property.
     * @return minValue property
     */
    @Override
    public Double getMinValue() {
        return this.parameter.getMinValue();
    }

    /**
     * Gets the value of the maxValue property.
     * @return maxValue property
     */
    @Override
    public Double getMaxValue() {
        return this.parameter.getMaxValue();
    }

    /**
     * Gets the value of the scale property.
     * @return scale property
     */
    @Override
    public Double getScale() {
        return this.parameter.getScale();
    }

    /**
     * Gets the value of the hasFixedValue property.
     * @return hasFixedValue property
     */
    @Override
    public boolean isHasFixedValue() {
        return this.parameter.isHasFixedValue();
    }

    /**
     * Sets the value of the value property.
     * @param value value to set
     */
    @Override
    public void setValue(final double value) {
        this.parameter.setValue(value);
    }

    /**
     * Sets the value of the minValue property.
     * @param value value to set
     */
    @Override
    public void setMinValue(final Double value) {
        this.parameter.setMinValue(value);
    }

    /**
     * Sets the value of the maxValue property.
     * @param value value to set
     */
    @Override
    public void setMaxValue(final Double value) {
        this.parameter.setMaxValue(value);
    }

    /**
     * Sets the value of the scale property.
     * @param value value to set
     */
    @Override
    public void setScale(final Double value) {
        this.parameter.setScale(value);
    }

    /**
     * Sets the value of the hasFixedValue property.
     * @param value value to set
     */
    @Override
    public void setHasFixedValue(final boolean value) {
        this.parameter.setHasFixedValue(value);
    }
}
