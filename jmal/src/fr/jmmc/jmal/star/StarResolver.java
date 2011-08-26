/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import fr.jmmc.mcs.util.FileUtils;
import fr.jmmc.mcs.util.MCSExceptionHandler;
import fr.jmmc.mcs.util.Urls;
import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;


import java.text.ParseException;

import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Store informations relative to a star.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public final class StarResolver {

    /** Logger - register on the current class to collect local logs */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.StarResolver");
    /** Simbad main URL */
    public static final String _simbadBaseURL = "http://simbad.u-strasbg.fr/simbad/sim-script?script=";
    /** comma separator */
    public static final String SEPARATOR_COMMA = ",";
    /** semicolon separator */
    public static final String SEPARATOR_SEMI_COLON = ";";

    /* members */
    /** The seeked star name */
    private final String _starName;
    /** The star data container */
    private final Star _starModel;
    /**
     * The querying result data container, not to overwrite original model with
     * incomplete data in case an error occurs during CDS querying
     */
    private final Star _newStarModel = new Star();
    /** The thread executing the CDS Simbad query and parsing */
    private ResolveStarThread _resolveStarThread = null;

    /**
     * Constructor.
     *
     * @param name the name of the satr to resolve.
     * @param star the star to fulfill.
     */
    public StarResolver(final String name, final Star star) {
        _starName = name;
        _starModel = star;
    }

    /**
     * Asynchroneously query CDS Simbad to retrieve a given star information according to its name.
     */
    public void resolve() {
        _logger.entering("StarResolver", "resolve");

        if (_resolveStarThread != null) {
            _logger.warning("A star resolution thread is already running, so doing nothing.");
            return;
        }

        // Launch the query in the background in order to keep GUI updated

        // Define the star name :
        _newStarModel.setName(_starName);

        _resolveStarThread = new ResolveStarThread();

        // define UncaughtExceptionHandler :
        MCSExceptionHandler.installThreadHandler(_resolveStarThread);

        // Launch query :
        _resolveStarThread.start();
    }

    /**
     * Command-line tool that tries to resolve the star name given as first parameter.
     * @param args first argument is the star name
     */
    public static void main(String[] args) {
        // Context initialization
        final String starName = args[0];
        final Star star = new Star();
        star.addObserver(new Observer() {

            public void update(Observable o, Object arg) {
                // Outpout results
                System.out.println("Star '" + starName + "' contains:\n" + star);
            }
        });

        // Seek data about the given star name (first arg on command line)
        StarResolver starResolver = new StarResolver(starName, star);
        starResolver.resolve();
    }

    /**
     * Star resolver thread : launch and handle CDS SimBad query
     */
    private final class ResolveStarThread extends Thread {

        /** flag to indicate that an error occured */
        private boolean _error = false;
        /** Simbad querying result */
        private String _result = null;

        @Override
        public void run() {
            _logger.entering("ResolveStarThread", "run");

            querySimbad();

            if (!isError()) {
                parseResult();
            }
        }

        /**
         * Set an error message from CDS Simbad query execution, and notify
         * registered observers.
         * @param message the error message to store.
         */
        private void raiseCDSimbadErrorMessage(final String message) {
            raiseCDSimbadErrorMessage(message, null);
        }

        /**
         * Set an error message from CDS Simbad query execution, and notify
         * registered observers.
         * @param message the error message to store.
         * @param exception exception (optional)
         */
        private void raiseCDSimbadErrorMessage(final String message, final Exception exception) {
            _error = true;

            if (exception != null) {
                String msg = message + " : " + exception.getMessage();

                // avoid too long messages:
                if (msg.length() > 128) {
                    msg = msg.substring(0, 128);
                }
                _starModel.raiseCDSimbadErrorMessage(msg);
            } else {
                _starModel.raiseCDSimbadErrorMessage(message);
            }
        }

        /**
         * Return the flag indicating that an error occured
         * @return true if an error occured
         */
        private boolean isError() {
            return _error;
        }

        /**
         * Query Simbad using script
         */
        public void querySimbad() {
            _logger.entering("ResolveStarThread", "querySimbad");

            // Should never receive an empty scence object name
            if (_starName.length() == 0) {
                raiseCDSimbadErrorMessage("Could not resolve empty star name.");
                return;
            }

            // Reset result before proceeding
            _result = "";

            // buffer used for both script and result :
            final StringBuilder sb = new StringBuilder(1024);

            // Forge Simbad script to execute
            sb.append("output console=off script=off\n"); // Just data
            sb.append("format object form1 \""); // Simbad script preambule
            sb.append("%COO(d;A);%COO(d;D);%COO(A);%COO(D);\\n"); // RA and DEC coordinates as sexagesimal and decimal degree values
            sb.append("%OTYPELIST\\n"); // Object types enumeration
            sb.append("%FLUXLIST(V,I,J,H,K;N=F,)\\n"); // Magnitudes, 'Band=Value' format
            sb.append("%PM(A;D)\\n"); // Proper motion with error
            sb.append("%PLX(V;E)\\n"); // Parallax with error
            sb.append("%SP(S)\\n"); // Spectral types enumeration
            sb.append("%RV(V;W)\\n"); // Radial velocity
            sb.append("%IDLIST[%*,]"); // Simbad identifiers
            sb.append("\"\n"); // Simbad script end
            sb.append("query id ").append(_starName); // Add the object name we are looking for

            final String simbadScript = sb.toString();

            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("CDS Simbad script :\n" + simbadScript);
            }

            // Try to get star data from CDS
            BufferedReader bufferedReader = null;
            try {
                // Forge the URL int UTF8 unicode charset
                final String encodedScript = Urls.encode(simbadScript);
                final String simbadURL = _simbadBaseURL + encodedScript;

                if (_logger.isLoggable(Level.FINE)) {
                    _logger.fine("Querying CDS Simbad at " + simbadURL);
                }

                // Launch the network query
                final InputStream inputStream = Urls.parseURL(simbadURL).openStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                // Read incoming data line by line
                String currentLine = null;

                // reset buffer :
                sb.setLength(0);

                while ((currentLine = bufferedReader.readLine()) != null) {
                    sb.append(currentLine).append('\n');
                }

                _result = sb.toString();

                if (_logger.isLoggable(Level.FINER)) {
                    _logger.finer("CDS Simbad raw result :\n" + _result);
                }
            } catch (IOException ioe) {
                raiseCDSimbadErrorMessage("CDS connection failed", ioe);
            } finally {
                FileUtils.closeFile(bufferedReader);
            }
        }

        /**
         * Parse Simbad response
         */
        public void parseResult() {
            _logger.entering("ResolveStarThread", "parseResult");

            // If the result string is empty
            if (_result.length() < 1) {
                raiseCDSimbadErrorMessage("No data for star '" + _starName + "'.");
                return;
            }

            // If there was an error during query
            if (_result.startsWith("::error")) {
                // sample error (name not found):
                /*
                ::error:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

                [3] Identifier not found in the database : NAME TEST
                

                 */
                // try to get error message:
                String errorMessage = null;
                final int pos = _result.indexOf('\n');
                if (pos != -1) {
                    errorMessage = _result.substring(pos + 1).replaceAll("\\s+", " ").trim();
                }

                raiseCDSimbadErrorMessage("Querying script execution failed for star '" + _starName + "' "
                        + ((errorMessage != null) ? ":\n" + errorMessage : "."));
                return;
            }

            // Remove any blanking character (~) :
            _result = _result.replaceAll("~[ ]*", "");

            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest("CDS Simbad result without blanking values :\n" + _result);
            }

            try {
                // Parsing result line by line :

                // Special line tokenizer because it is possible to have a blank lines (no flux at all) :
                final StrictStringTokenizer lineTokenizer = new StrictStringTokenizer(_result, "\n");

                // First line should contain star coordinates, separated by ';'
                final String coordinates = lineTokenizer.nextToken();
                parseCoordinates(coordinates);

                // Second line should contain object types, separated by ','
                final String objectTypes = lineTokenizer.nextToken();
                parseObjectTypes(objectTypes);

                // Third line should contain star fluxes, separated by ','
                final String fluxes = lineTokenizer.nextToken();
                parseFluxes(fluxes);

                // Forth line should contain star proper motions, separated by ';'
                final String properMotion = lineTokenizer.nextToken();
                parseProperMotion(properMotion);

                // Fith line should contain star parallax, separated by ';'
                final String parallax = lineTokenizer.nextToken();
                parseParallax(parallax);

                // Sixth line should contain star spectral types
                final String spectralTypes = lineTokenizer.nextToken();
                parseSpectralTypes(spectralTypes);

                // Seventh line should contain radial velocity, separated by ';'
                final String radialVelocity = lineTokenizer.nextToken();
                parseRadialVelocity(radialVelocity);

                // Eigth line should contain simbad identifiers, separated by ','
                final String identifiers = lineTokenizer.nextToken();
                parseIdentifiers(identifiers);

                /*
                 * At this stage parsing went fine.
                 * So copy back the new CDS Simbad result in the original Star object.
                 *
                 * Done only after all data were fetched and parsed successfully, to
                 * always have consistent data in _starModel.
                 *
                 * If anything went wrong while querying or parsing, previous data
                 * remain unchanged.
                 */

                // Use EDT to ensure only 1 thread (EDT) updates the model and handles the notification :
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        _starModel.copy(_newStarModel);

                        // Notify all registered observers that the query went fine :
                        _starModel.fireNotification(Star.Notification.QUERY_COMPLETE);
                    }
                });

            } catch (NumberFormatException nfe) {
                raiseCDSimbadErrorMessage("Could not parse data for star '" + _starName + "'", nfe);
            } catch (ParseException pe) {
                raiseCDSimbadErrorMessage("Could not parse data for star '" + _starName + "'", pe);
            }
        }

        /**
         * Parse star RA / DEC coordinates
         * @param coordinates simbad RA / DEC coordinates
         * @throws ParseException if parsing simbad RA/DEC failed
         * @throws NumberFormatException if parsing number(s) failed
         */
        private void parseCoordinates(final String coordinates) throws ParseException, NumberFormatException {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Coordinates contains '" + coordinates + "'.");
            }

            final StringTokenizer coordinatesTokenizer = new StringTokenizer(coordinates, SEPARATOR_SEMI_COLON);

            if (coordinatesTokenizer.countTokens() == 4) {
                final double ra = Double.parseDouble(coordinatesTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("RA_d = '" + ra + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.RA_d, ra);

                final double dec = Double.parseDouble(coordinatesTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("DEC_d = '" + dec + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.DEC_d, dec);

                final String hmsRa = coordinatesTokenizer.nextToken();
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("RA = '" + hmsRa + "'.");
                }
                _newStarModel.setPropertyAsString(Star.Property.RA, hmsRa);

                final String dmsDec = coordinatesTokenizer.nextToken();
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("DEC = '" + dmsDec + "'.");
                }
                _newStarModel.setPropertyAsString(Star.Property.DEC, dmsDec);
            } else {
                throw new ParseException("Invalid coordinates '" + coordinates + "'", -1);
            }
        }

        /**
         * Parse object types
         * @param objectTypes simbad object types
         */
        private void parseObjectTypes(final String objectTypes) {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Object Types contains '" + objectTypes + "'.");
            }

            _newStarModel.setPropertyAsString(Star.Property.OTYPELIST, objectTypes);
        }

        /**
         * Parse magnitudes
         * @param fluxes simbad fluxes
         * @throws NumberFormatException if parsing number(s) failed
         */
        private void parseFluxes(final String fluxes) throws NumberFormatException {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Fluxes contains '" + fluxes + "'.");
            }

            final StringTokenizer fluxesTokenizer = new StringTokenizer(fluxes, SEPARATOR_COMMA);

            while (fluxesTokenizer.hasMoreTokens()) {
                final String token = fluxesTokenizer.nextToken();
                // The first character is the magnitude band letter :
                final String magnitudeBand = "FLUX_" + token.substring(0, 1).toUpperCase();
                // The second character is "=", followed by the magnitude value in double :
                final String value = token.substring(2);

                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest(magnitudeBand + " = '" + value + "'.");
                }

                _newStarModel.setPropertyAsDouble(Star.Property.fromString(magnitudeBand),
                        Double.parseDouble(value));
            }
        }

        /**
         * Parse optional proper motion
         * @param properMotion
         * @throws NumberFormatException if parsing number(s) failed
         */
        private void parseProperMotion(final String properMotion) throws NumberFormatException {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Proper Motion contains '" + properMotion + "'.");
            }

            final StringTokenizer properMotionTokenizer = new StringTokenizer(properMotion, SEPARATOR_SEMI_COLON);

            if (properMotionTokenizer.countTokens() == 2) {
                final double pm_ra = Double.parseDouble(properMotionTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("PROPERMOTION_RA = '" + pm_ra + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PROPERMOTION_RA, pm_ra);

                final double pm_dec = Double.parseDouble(properMotionTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("PROPERMOTION_DEC = '" + pm_dec + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PROPERMOTION_DEC, pm_dec);
            } else {
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("No proper motion data for star '" + _starName + "'.");
                }
            }
        }

        /**
         * Parse optional parallax
         * @param parallax simbad parallax
         * @throws NumberFormatException if parsing number(s) failed
         */
        private void parseParallax(final String parallax) throws NumberFormatException {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Parallax contains '" + parallax + "'.");
            }

            final StringTokenizer parallaxTokenizer = new StringTokenizer(parallax, SEPARATOR_SEMI_COLON);

            if (parallaxTokenizer.countTokens() == 2) {
                final double plx = Double.parseDouble(parallaxTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("PARALLAX = '" + plx + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PARALLAX, plx);

                final double plx_err = Double.parseDouble(parallaxTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("PARALLAX_err = '" + plx_err + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PARALLAX_err, plx_err);
            } else {
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("No parallax data for star '" + _starName + "'.");
                }
            }
        }

        /**
         * Parse spectral types
         * @param spectralTypes simbad spectral types
         */
        private void parseSpectralTypes(final String spectralTypes) {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Spectral Types contains '" + spectralTypes + "'.");
            }

            _newStarModel.setPropertyAsString(Star.Property.SPECTRALTYPES, spectralTypes);
        }

        /**
         * Parse optional radial velocity
         * @param radialVelocity Simbad radial velocity
         * @throws NumberFormatException if parsing number(s) failed
         */
        private void parseRadialVelocity(final String radialVelocity) throws NumberFormatException {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Radial velocity contains '" + radialVelocity + "'.");
            }

            final StringTokenizer rvTokenizer = new StringTokenizer(radialVelocity, SEPARATOR_SEMI_COLON);

            if (rvTokenizer.countTokens() > 0) {
                final double rv = Double.parseDouble(rvTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("RV = '" + rv + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.RV, rv);

                if (rvTokenizer.hasMoreTokens()) {
                    final String rv_def = rvTokenizer.nextToken();
                    if (_logger.isLoggable(Level.FINEST)) {
                        _logger.finest("RV_DEF = '" + rv_def + "'.");
                    }
                    _newStarModel.setPropertyAsString(Star.Property.RV_DEF, rv_def);
                }
            } else {
                if (_logger.isLoggable(Level.FINEST)) {
                    _logger.finest("No radial velocity data for star '" + _starName + "'.");
                }
            }
        }

        /**
         * Parse optional identifiers
         * @param identifiers simbad identifiers
         */
        private void parseIdentifiers(final String identifiers) {
            if (_logger.isLoggable(Level.FINER)) {
                _logger.finer("Identifier contains '" + identifiers + "'.");
            }

            String ids = identifiers;

            if (ids.length() > 0) {
                // remove redundant space characters :
                ids = ids.replaceAll("[ ]+", " ");
            }

            if (ids.length() > 0) {
                // remove last separator :
                ids = ids.substring(0, ids.length() - 1);
            }

            _newStarModel.setPropertyAsString(Star.Property.IDS, ids);
        }
    }

    /**
     * StringTokenizer Hack to return empty token if multiple delimiters found
     *
     * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4140850
     */
    private static final class StrictStringTokenizer {

        /** delimiter */
        private String delimiter;
        /** internal string tokenizer returning delimiter too */
        private final StringTokenizer st;
        /** last token reminder */
        private String lastToken;

        /**
         * Special StringTokenizer that returns empty token if multiple delimiters encountered
         * @param input input string
         * @param delimiter delimiter
         */
        public StrictStringTokenizer(final String input, final String delimiter) {
            this.delimiter = delimiter;
            this.st = new StringTokenizer(input, delimiter, true);
            this.lastToken = delimiter;// if first token is separator
        }

        /**
         * Returns the next token from this string tokenizer.
         *
         * @return     the next token from this string tokenizer.
         */
        public String nextToken() {
            String result = null;

            String token;
            while (result == null && this.st.hasMoreTokens()) {
                token = this.st.nextToken();
                if (token.equals(this.delimiter)) {
                    if (this.lastToken.equals(this.delimiter)) {
                        // no value between 2 separators ?
                        result = "";
                    }
                } else {
                    result = token;
                }
                this.lastToken = token;
            } // next token
            if (result == null) {
                result = "";
            }
            return result;
        }
    }
}
/*___oOo___*/
