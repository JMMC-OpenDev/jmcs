/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model.gui;

import fr.jmmc.jmal.model.ModelDefinition;
import fr.jmmc.jmal.model.ModelManager;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmal.model.targetmodel.Parameter;
import fr.jmmc.jmal.util.MathUtils;
import net.jafama.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class overrides the EditableParameter class to define a separation or position angle position parameter where only the value is editable
 *
 * @author Laurent BOURGES.
 */
public final class EditableSepPosAngleParameter implements Editable {

    /** Class logger */
    private static Logger logger = LoggerFactory.getLogger(EditableSepPosAngleParameter.class.getName());

    /**
     * Custom type enum
     */
    public enum SepPosAngleType {

        /** separation parameter */
        SEPARATION("sep", "separation", "mas"),
        /** position angle parameter */
        POS_ANGLE("pos_angle", "position_angle", "deg");

        /**
         * Custom constructor
         *
         * @param name name of the parameter
         * @param type type of the parameter
         * @param units units of the parameter
         */
        private SepPosAngleType(final String name, final String type, final String units) {
            this.name = name;
            this.type = type;
            this.units = units;
        }
        /** parameter name */
        private final String name;
        /** parameter type */
        private final String type;
        /** parameter units */
        private final String units;

        /**
         * Return the parameter name
         * @return parameter name
         */
        public String getName() {
            return name;
        }

        /**
         * Return the parameter type
         * @return parameter type
         */
        public String getType() {
            return type;
        }

        /**
         * Return the parameter units
         * @return parameter units
         */
        public String getUnits() {
            return units;
        }
    }

    /* members */
    /** model of the parameter */
    private final Model model;
    /** type */
    private final SepPosAngleType type;
    /** name */
    private final String name;
    /** parameter X */
    private final Parameter paramX;
    /** parameter Y */
    private final Parameter paramY;

    /**
     * Constructor for separation or position angle editable parameter
     *
     * @param model parent model
     * @param type separation or position angle type
     */
    public EditableSepPosAngleParameter(final Model model, final SepPosAngleType type) {
        this.model = model;
        this.type = type;

        this.name = this.type.getName() + ModelManager.parseModelUniqueIndex(model);

        this.paramX = model.getParameter(ModelDefinition.PARAM_X);
        this.paramY = model.getParameter(ModelDefinition.PARAM_Y);
    }

    /**
     * Return the model
     *
     * @return model
     */
    @Override
    public Model getModel() {
        return model;
    }

    /**
     * Indicates if the parameter is a shared parameter
     *
     * @return true if the parameter is a shared parameter
     */
    @Override
    public boolean isShared() {
        return false;
    }

    /**
     * Indicates if the parameter is a position
     *
     * @return true if the parameter is a position
     */
    @Override
    public boolean isPosition() {
        return true;
    }

    /* Parameter methods */

    /* --- read-only parameter attributes ------------------------------------- */
    /**
     * Gets the value of the name property.
     *
     * @return value of the name property
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Gets the value of the type property.
     *
     * @return value of the type property
     */
    @Override
    public String getType() {
        return this.type.getType();
    }

    /**
     * Gets the value of the units property.
     *
     * @return value of the units property
     */
    @Override
    public String getUnits() {
        return this.type.getUnits();
    }
    /* --- writable parameter attributes -------------------------------------- */

    /**
     * Gets the value of the value property.
     *
     * @return value property
     */
    @Override
    public double getValue() {
        final double x = paramX.getValue();
        final double y = paramY.getValue();

        if (logger.isDebugEnabled()) {
            logger.debug("getValue[{}] [x,y] = [{}, {}]", type, x, y);
        }

        double value = 0d;
        switch (this.type) {
            case SEPARATION:
                value = getSeparation(x, y);
                break;
            case POS_ANGLE:
                value = getPosAngle(x, y);
                break;
            default:
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getValue[{}] = [{}]", type, value);
        }
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value value to set
     */
    @Override
    public void setValue(final double value) {
        if (logger.isDebugEnabled()) {
            logger.debug("setValue[{}] = [{}]", type, value);
        }
        // old values :
        final double x = paramX.getValue();
        final double y = paramY.getValue();

        if (logger.isDebugEnabled()) {
            logger.debug("setValue[{}] [x,y] = [{}, {}]", type, x, y);
        }

        double x2 = 0d;
        double y2 = 0d;

        switch (this.type) {
            case SEPARATION:
                final double posAngle = getPosAngle(x, y);

                x2 = getX(value, posAngle);
                y2 = getY(value, posAngle);
                break;
            case POS_ANGLE:
                final double dist = getSeparation(x, y);

                x2 = getX(dist, value);
                y2 = getY(dist, value);
                break;
            default:
        }
        paramX.setValue(x2);
        paramY.setValue(y2);

        if (logger.isDebugEnabled()) {
            logger.debug("setValue[{}] [x2,y2] = [{}, {}]", type, x2, y2);
        }
    }

    /**
     * Gets the value of the minValue property.
     *
     * @return null
     */
    @Override
    public Double getMinValue() {
        return null;
    }

    /**
     * Gets the value of the maxValue property.
     *
     * @return null
     */
    @Override
    public Double getMaxValue() {
        return null;
    }

    /**
     * Gets the value of the scale property.
     *
     * @return null
     */
    @Override
    public Double getScale() {
        return null;
    }

    /**
     * Gets the value of the hasFixedValue property.
     *
     * @return false
     */
    @Override
    public boolean isHasFixedValue() {
        return false;
    }

    /**
     * Sets the value of the minValue property.
     *
     * @param value value to set
     */
    @Override
    public void setMinValue(final Double value) {
    }

    /**
     * Sets the value of the maxValue property.
     *
     * @param value value to set
     */
    @Override
    public void setMaxValue(final Double value) {
    }

    /**
     * Sets the value of the scale property.
     *
     * @param value value to set
     */
    @Override
    public void setScale(final Double value) {
    }

    /**
     * Sets the value of the hasFixedValue property.
     *
     * @param value value to set
     */
    @Override
    public void setHasFixedValue(final boolean value) {
    }

    /* --- utility methods */
    /**
     * Return the separation
     *
     * @param x coordinate x
     * @param y coordinate y
     * @return separation
     */
    public static double getSeparation(final double x, final double y) {
        return MathUtils.carthesianNorm(x, y);
    }

    /**
     * Return the position angle in [-180;180] as the astronomical position angle, 
     * North equal to 0 or 180 for x=0, and 90 or 270 for y=0.
     *
     * @param x coordinate x
     * @param y coordinate y
     * @return position angle in [-180;180]
     */
    public static double getPosAngle(final double x, final double y) {
        return FastMath.toDegrees(FastMath.atan2(x, y));
    }

    /**
     * Return the coordinate x
     *
     * @param dist distance
     * @param posAngle angle in [-180;180]
     * @return coordinate x
     */
    public static double getX(final double dist, final double posAngle) {
        return dist * FastMath.sin(FastMath.toRadians(posAngle));
    }

    /**
     * Return the coordinate y
     *
     * @param dist distance
     * @param posAngle angle in [-180;180]
     * @return coordinate y
     */
    public static double getY(final double dist, final double posAngle) {
        return dist * FastMath.cos(FastMath.toRadians(posAngle));
    }
}
