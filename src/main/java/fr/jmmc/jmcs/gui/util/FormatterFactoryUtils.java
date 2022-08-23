/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Note: these formatters are not thread safe (synchronization must be performed by callers)
 */
public final class FormatterFactoryUtils {

    private static final DefaultFormatterFactory INTEGER_FORMATTER_FACTORY = new DefaultFormatterFactory(
            new NumberFormatter(NumberFormat.getIntegerInstance())
    );
    private static final DefaultFormatterFactory DECIMAL_FORMATTER_FACTORY = new DefaultFormatterFactory(
            new ScientificNumberFormatter()
    );

    public static JFormattedTextField.AbstractFormatterFactory getIntegerFormatterFactory() {
        return INTEGER_FORMATTER_FACTORY;
    }

    public static JFormattedTextField.AbstractFormatterFactory getDecimalFormatterFactory() {
        return DECIMAL_FORMATTER_FACTORY;
    }

    private final static class ScientificNumberFormatter extends NumberFormatter {

        /** default serial UID for Serializable interface */
        private static final long serialVersionUID = 1L;

        ScientificNumberFormatter() {
            super(new DecimalFormat("0.0####E00"));
            setValueClass(Double.class);
        }

        private final NumberFormat fmtDef = new DecimalFormat("0.0####");

        @Override
        public Object stringToValue(final String text) throws ParseException {
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return Double.valueOf(text);
            } catch (NumberFormatException nfe) {
                throw new ParseException(text, 0); // makes the field invalid
            }
        }

        @Override
        public String valueToString(final Object value) throws ParseException {
            // See NumberUtils.format()
            if (value instanceof Number) {
                // check value range:
                final double abs = Math.abs(((Number) value).doubleValue());

                if ((abs > 0.0) && ((abs < 1e-3) || (abs > 1e4))) {
                    return super.valueToString(value); // scientific format
                }
                return fmtDef.format(value);
            }
            return "";
        }
    }

    private FormatterFactoryUtils() {
        super();
    }
}
