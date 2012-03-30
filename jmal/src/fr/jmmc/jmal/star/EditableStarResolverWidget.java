/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import fr.jmmc.jmal.ALX;
import fr.jmmc.jmcs.gui.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This extended StarResolverWidget allows the user to enter an RA/DEC couple as a Star without any CDS resolution (manually defined star)
 * 
 * @author Sylvain LAFRASSE, Guillaume MELLA, Laurent BOURGES.
 */
public final class EditableStarResolverWidget extends StarResolverWidget {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** double dot character to detect coordinates in the text field */
    private static final String DOUBLE_DOT = ":";
    /** int formatter for HH:MM or DD:MM */
    private final static NumberFormat DF_INT = new DecimalFormat("00");
    /** double formatter for SS.mmm */
    private final static NumberFormat DF_DBL = new DecimalFormat("00.000");
    /* members */
    /** standard StarResolverWidget action */
    private ActionListener standardAction = null;

    /**
     * Creates a new StarResolverWidget object.
     */
    public EditableStarResolverWidget() {
        this(new Star());
    }

    /**
     * Creates a new StarResolverWidget object.
     *
     * @param star star model
     */
    public EditableStarResolverWidget(final Star star) {
        super(star);

        final ActionListener[] prevListeners = getActionListeners();
        if (prevListeners.length > 1) {
            throw new IllegalStateException("The StarResolverWidget has several action listeners !");
        }

        // keep standard StarResolverWidget action :
        this.standardAction = prevListeners[0];

        // remove it from action listeners :
        removeActionListener(this.standardAction);

        // add custom action listener :
        addActionListener(new ActionListener() {

            /**
             * Check if the field contains star coordinates.
             * If true then parse those coordinates to provide a Star with RA/DEC coordinates only.
             * Else resolve the given name (standard StarResolverWidget action)
             * @param ae action event
             */
            @Override
            public void actionPerformed(final ActionEvent ae) {

                final String textValue = ae.getActionCommand().trim();

                if (textValue.length() > 0) {

                    _logger.debug("EditableStarResolverWidget : actionPerformed : {}", textValue);

                    if (textValue.contains(DOUBLE_DOT)) {
                        try {
                            parseCoordinates(textValue);
                        } catch (IllegalArgumentException iae) {

                            MessagePane.showErrorMessage("Invalid format for star coordinates :\n" + iae.getMessage());

                            StatusBar.show("Parsing star coordinates failed.");
                        }
                    } else {
                        // invoke standard StarResolverWidget action (simbad) :
                        standardAction.actionPerformed(ae);
                    }
                }
            }
        });
    }

    /**
     * Parse the text value as RA/DEC coordinates, update the star model and notify the observers
     * @param input text value with optional star name field(trimmed)
     *
     * @throws IllegalArgumentException if the RA/DEC format was wrong
     */
    private void parseCoordinates(final String input) throws IllegalArgumentException {
        // first remove redundant white space :
        final String coords = input.replaceAll("\\s+", " ");

        // Split the input String at the first occurence of the ' ' char :
        final int pos = coords.indexOf(' ');

        if (pos == -1) {
            throw new IllegalArgumentException("wrong RA/DEC format: '" + input + "'  must be of form '+10:00:00.00 +30:00:00.00'");
        }


        final String inputRA = coords.substring(0, pos);
        final String inputDEC;
        // Search if we have one optional star name
        final int namePos = coords.indexOf(' ', pos + 4);
        if (namePos == -1) {
            inputDEC = coords.substring(pos + 1);
        } else {
            inputDEC = coords.substring(pos + 1, namePos);
        }


        // Validate the format of the RA value
        if (!inputRA.matches("[+|-]?[0-9]+[:][0-9]+[:][0-9]+.?[0-9]*")) {
            throw new IllegalArgumentException("wrong RA format: '"
                    + inputRA + "' must be of form +10:00:00.00");
        }

        // Validate the format of the given value
        if (!inputDEC.matches("[+|-]?[0-9]+[:][0-9]+[:][0-9]+.?[0-9]*")) {
            throw new IllegalArgumentException("wrong DEC format: '"
                    + inputDEC + "' must be of form +30:00:00.00");
        }

        // check hour, minute and second values and reformat value :
        final String hmsRa = parseHMS(inputRA);

        // check degree, minute and second values and reformat value :
        final String dmsDec = parseDMS(inputDEC);

        // ra/dec in degrees :
        final double ra = ALX.parseHMS(hmsRa);
        final double dec = ALX.parseDEC(dmsDec);


        // Set name with coordinates or name if given
        final String name;
        if (namePos == -1) {
            // set default name : 'RA DEC' (given coordinates)
            name = hmsRa + " " + dmsDec;
        } else {
            // set given name
            name = coords.substring(namePos + 1);
        }

        /*
         * At this stage parsing went fine, update the internal star model.
         */

        // Then update the internal star model :
        final Star starModel = getStar();

        // No synchronisation needeed as it is already done by EDT :
        starModel.clear();

        // Name :
        _logger.trace("NAME = {}", name);
        starModel.setName(name);

        // Coordinates :
        _logger.trace("RA_d = {}", ra);
        starModel.setPropertyAsDouble(Star.Property.RA_d, ra);

        _logger.trace("DEC_d = {}", dec);
        starModel.setPropertyAsDouble(Star.Property.DEC_d, dec);

        _logger.trace("RA = {}", hmsRa);
        starModel.setPropertyAsString(Star.Property.RA, hmsRa);

        _logger.trace("DEC = {}", dmsDec);
        starModel.setPropertyAsString(Star.Property.DEC, dmsDec);

        // No object type :
        starModel.setPropertyAsString(Star.Property.OTYPELIST, "");

        // Skip fluxes (FLUX_ properties)
        // Skip Proper motion (skip PROPERMOTION_ properties)
        // Skip Parallax (skip PARALLAX and PARALLAX_err properties)

        // No spectral type :
        starModel.setPropertyAsString(Star.Property.SPECTRALTYPES, "");

        // Id :
        starModel.setPropertyAsString(Star.Property.IDS, name);

        // Finally notify all registered observers that the query went fine :
        starModel.fireNotification(Star.Notification.QUERY_COMPLETE);
    }

    /**
     * Parse the input HMS value (HH:MM:SS.mmm) to check its hour, minute and second values.
     * Then format those values in HH:MM:SS.mmm (insert missing zero characters)
     *
     * @param raHms input HMS value
     * @return formatted string
     *
     * @throws IllegalArgumentException if any value is invalid
     */
    private String parseHMS(final String raHms) throws IllegalArgumentException {

        int hh;
        int hm;
        double hs;

        // note : the input string matches the regexp [+|-]?[0-9]+[:][0-9]+[:][0-9]+.?[0-9]*

        // Parse the given string according to the format HH:MM:SS.mmm
        try {
            final String[] tokens = raHms.split(DOUBLE_DOT);
            hh = Integer.parseInt(tokens[0]);

            if (Math.abs(hh) >= 24) {
                throw new IllegalArgumentException("invalid hour value : '" + raHms + "'");
            }

            hm = Integer.parseInt(tokens[1]);

            if (hm >= 60) {
                throw new IllegalArgumentException("invalid minute value: '" + raHms + "'");
            }

            hs = Double.parseDouble(tokens[2]);

            if (hs >= 60d) {
                throw new IllegalArgumentException("invalid second value: '" + raHms + "'");
            }

        } catch (NumberFormatException nfe) {
            _logger.error("format error", nfe);
            throw new IllegalArgumentException("invalid value : '" + raHms + "'");
        } catch (IllegalArgumentException iae) {
            throw iae;
        }

        // Return a string with missing zero characters :

        final StringBuilder sb = new StringBuilder();
        sb.append(DF_INT.format(hh));
        sb.append(DOUBLE_DOT);
        sb.append(DF_INT.format(hm));
        sb.append(DOUBLE_DOT);
        sb.append(DF_DBL.format(hs));

        final String hms = sb.toString();
        _logger.trace("HMS = {}", hms);

        return hms;
    }

    /**
     * Parse the input DMS value (DD:MM:SS.mmm) to check its degree, minute and second values.
     * Then format those values in DD:MM:SS.mmm (insert missing zero characters)
     *
     * @param decDms input DMS value
     * @return formatted string
     * @throws IllegalArgumentException if any value is invalid
     */
    private String parseDMS(final String decDms) throws IllegalArgumentException {

        int dd;
        int dm;
        double ds;

        // note : the input string matches the regexp [+|-]?[0-9]+[:][0-9]+[:][0-9]+.?[0-9]*

        // Parse the given string according to the format DD:MM:SS.mmm
        try {
            final String[] tokens = decDms.split(DOUBLE_DOT);
            dd = Integer.parseInt(tokens[0]);

            if (Math.abs(dd) >= 90) {
                throw new IllegalArgumentException("invalid degree value : '" + decDms + "'");
            }

            dm = Integer.parseInt(tokens[1]);

            if (dm >= 60) {
                throw new IllegalArgumentException("invalid minute value: '" + decDms + "'");
            }

            ds = Double.parseDouble(tokens[2]);

            if (ds >= 60d) {
                throw new IllegalArgumentException("invalid second value: '" + decDms + "'");
            }

        } catch (NumberFormatException nfe) {
            _logger.error("format error", nfe);
            throw new IllegalArgumentException("invalid value : '" + decDms + "'");
        } catch (IllegalArgumentException iae) {
            throw iae;
        }

        // Return a string with missing zero characters :

        final StringBuilder sb = new StringBuilder();
        sb.append(DF_INT.format(dd));
        sb.append(DOUBLE_DOT);
        sb.append(DF_INT.format(dm));
        sb.append(DOUBLE_DOT);
        sb.append(DF_DBL.format(ds));

        final String dms = sb.toString();
        _logger.trace("DMS = {}", dms);

        return sb.toString();
    }

    // --- Test Code -------------------------------------------------------------
    /**
     * Main - for EditableStarResolverWidget demonstration and test only.
     * @param args ignored arguments
     */
    public static void main(String[] args) {
        // Resolver initialization
        final Star star = new Star();
        star.addObserver(new Observer() {

            public void update(Observable o, Object arg) {
                final Star.Notification notification = (Star.Notification) arg;

                if (notification == Star.Notification.QUERY_COMPLETE) {
                    _logger.error("Star changed:\n{}", star);
                }
            }
        });

        // GUI initialization (EDT)
        SwingUtils.invokeLaterEDT(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame("EditableStarResolverWidget Demo");

                Container container = frame.getContentPane();
                JPanel panel = new JPanel();
                EditableStarResolverWidget searchField = new EditableStarResolverWidget(star);
                searchField.setColumns(30);
                panel.add(searchField);
                container.add(panel);

                frame.pack();
                frame.setVisible(true);

                // Test cases :

                // 274,2489167 / -19,0759167 : OK
//        test(searchField, " 18:16:59.74  \t  -19:04:33.3  ");

                //  -274,2489167 / 19,0759167 : OK
//        test(searchField, "-18:16:59.74 +19:04:33.3");

                //  -359,9999958 / 89,9999997 : OK
//        test(searchField, "-23:59:59.999 +89:59:59.999");

                // 359,9999958 / -89,9999997 : OK
//        test(searchField, "+23:59:59.999 -89:59:59.999");

                /*
                eta tau
                RA=03 47 29.0765 DEC=+24 06 18.494
                RA_d=56.8711521  DEC_d=24.1051372
                 */
//        test(searchField, "eta tau");

                // 56,8711521 / 24,1051372 : OK
//        test(searchField, "03:47:29.0765 +24:06:18.494");

                /*
                eps aur
                RA=05 01 58.1341 DEC=+43 49 23.910
                RA_d=75.4922254  DEC_d=43.8233083
                 */
//        test(searchField, "eps aur");

                // 75,4922254 / 43,8233083 : OK
//        test(searchField, "05:01:58.1341 +43:49:23.910");

                /*
                uy aur
                RA=04 51 47.38 DEC=+30 47 13.5
                RA_d=72.94742  DEC_d=30.78708
                 */
//        test(searchField, "uy aur");

                // 72,9474167 / 30,7870833 : OK (rounded value)
//        test(searchField, "04:51:47.38 +30:47:13.5");

                /*
                HD 2403
                RA=00 27 44.680 DEC=-19 19 05.38
                RA_d=6.936167   DEC_d=-19.318161
                 */
//        test(searchField, "HD 2403");

                // 6,9361667 / -19,3181611 : OK (rounded value)
//        test(searchField, "00:27:44.680 -19:19:05.38");

                /*
                HIP 117054
                RA=23 43 49.4616 DEC=-15 17 04.202
                RA_d=355.95609   DEC_d=-15.2845006
                 */
//        test(searchField, "HIP 117054");

                // 355,9560900 / -15,2845006 : OK
//        test(searchField, "23:43:49.4616 -15:17:04.202");

                /*
                HIP32349
                RA=06 45 08.9173 DEC=-16 42 58.017
                RA_d=101.2871554 DEC_d=-16.7161158
                 */
//        test(searchField, "HIP32349");

                // 101,2871554 / -16,7161158 : OK
//        test(searchField, "06:45:08.9173 -16:42:58.017");

                // multiple results :

//        test(searchField, "a");
//        test(searchField, "b");
//        test(searchField, "c");

                // Failure Test cases :

//        test(searchField, "eta tau*");

            }
        });

    }

    /**
     * Test method
     * @param searchField editable star resolver field
     * @param text input string
     */
    private static void test(final EditableStarResolverWidget searchField, final String text) {
        SwingUtils.invokeLaterEDT(new Runnable() {

            @Override
            public void run() {
                searchField.setText(text);
                searchField.fireActionPerformed();
            }
        });
    }
}
/*___oOo___*/
