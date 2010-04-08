/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarResolver.java,v 1.13 2010-04-08 08:32:17 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2010/04/07 13:10:00  bourgesl
 * hack for a known StringTokenizer bug with the StrictStringTokenizer to return empty lines (empty flux for example)
 * get all simbad identifiers (IDLIST) for later gui improvements
 *
 * Revision 1.11  2010/01/21 10:05:18  bourgesl
 * Define the star name when the query is complete
 * StarResolverWidget can be used in netbeans's component palette
 *
 * Revision 1.10  2010/01/14 12:40:20  bourgesl
 * Fix blanking value with white spaces for proper motion and parallax ' ; '
 * StringBuilder and Logger.isLoggable to avoid string.concat
 *
 * Revision 1.9  2010/01/04 14:06:41  bourgesl
 * close properly the CDS connection
 *
 * Revision 1.8  2009/12/18 14:43:54  bourgesl
 * blanking value support (~) and allow empty values for proper motion and parallax
 *
 * Revision 1.7  2009/12/16 15:53:02  lafrasse
 * Hardened CDS Simbad science star resolution mecanisms while failing.
 * Code, documentation and log refinments.
 *
 * Revision 1.6  2009/12/08 10:14:50  lafrasse
 * Added proper motion, parallax and spectral types storage and retrieval.
 *
 * Revision 1.5  2009/10/23 15:55:06  lafrasse
 * Removed debugging output.
 *
 * Revision 1.4  2009/10/23 15:38:20  lafrasse
 * Added error (querying and parsing) management.
 *
 * Revision 1.3  2009/10/23 12:24:22  lafrasse
 * Endorsed Star observers notification responsability.
 *
 * Revision 1.2  2009/10/13 15:35:50  lafrasse
 * Updated according to StarProperty migration in Star plus typped getters/setters.
 *
 * Revision 1.1  2009/10/06 15:54:18  lafrasse
 * First release.
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.astro.star;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLEncoder;

import java.text.ParseException;

import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Store informations relative to a star.
 */
public class StarResolver
{
    /** Logger - register on the current class to collect local logs */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.StarResolver");

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
    public StarResolver(String name, Star star)
    {
        _starName      = name;
        _starModel     = star;
    }

    /**
     * Asynchroneously query CDS Simbad to retrieve a given star information according to its name.
     */
    public void resolve()
    {
        _logger.entering("StarResolver", "resolve");

        if (_resolveStarThread != null)
        {
            _logger.warning(
                "A star resolution thread is already running, so doing nothing.");

            return;
        }

        // Launch the query in the background in order to keep GUI updated

        _newStarModel.setName(_starName); // Define the star name

        _resolveStarThread = new ResolveStarThread();
        _resolveStarThread.start(); // Launch query
    }

    /**
     * Command-line tool that tries to resolve the star name given as first parameter.
     * @param args first argument is the star name
     */
    public static void main(String[] args)
    {
        // Context initialization
        final String starName = args[0];
        final Star   star     = new Star();
        star.addObserver(new Observer()
            {
                public void update(Observable o, Object arg)
                {
                    // Outpout results
                    System.out.println("Star '" + starName + "' contains:\n" +
                        star);
                }
            });

        // Seek data about the given star name (first arg on command line)
        StarResolver starResolver = new StarResolver(starName, star);
        starResolver.resolve();
    }

    class ResolveStarThread extends Thread
    {
        /** Simbad main URL */
        private static final String _simbadBaseURL = "http://simbad.u-strasbg.fr/simbad/sim-script?script=";

        /** Logger - register on the current class to collect local logs */
        private final Logger _logger = Logger.getLogger(
                "fr.jmmc.mcs.astro.star.StarResolver.ResolveStarThread");

        /** Simbad querying result */
        private String _result = null;

        @Override
        public void run()
        {
            _logger.entering("ResolveStarThread", "run");

            querySimbad();

            parseResult();
        }

        public void querySimbad()
        {
            _logger.entering("ResolveStarThread", "querySimbad");

            // Should never receive an empty scence object name
            if (_starName.length() == 0)
            {
                _logger.severe("Received an empty star name");
                _starModel.raiseCDSimbadErrorMessage("Could not resolve star '" +
                    _starName + "'.");

                return;
            }

            // Reset result before proceeding
            _result = "";

            // buffer used for both script and result :
            final StringBuilder sb = new StringBuilder(255);

            // Forge Simbad script to execute
            sb.append("output console=off script=off\n"); // Just data
            sb.append("format object form1 \""); // Simbad script preambule
            sb.append("%COO(d;A);%COO(d;D);%COO(A);%COO(D);\\n"); // RA and DEC coordinates as sexagesimal and decimal degree values
            sb.append("%OTYPELIST\\n"); // Object types enumeration
            sb.append("%FLUXLIST(V,I,J,H,K;N=F,)\\n"); // Magnitudes, 'Band=Value' format
            sb.append("%PM(A;D)\\n"); // Proper motion with error
            sb.append("%PLX(V;E)\\n"); // Parallax with error
            sb.append("%SP(S)\\n"); // Spectral types enumeration
            sb.append("%IDLIST[%*|]"); // Simbad identifiers
            sb.append("\"\n"); // Simbad script end
            sb.append("query id ").append(_starName); // Add the object name we are looking for

            // Note : the object type may be useful too like %OTYPE ('Star') or %OTYPELIST ('*,**,X')

            final String simbadScript = sb.toString();

            if (_logger.isLoggable(Level.FINEST)) {
              _logger.finest("CDS Simbad script :\n" + simbadScript);
            }

            // Try to get star data from CDS
            InputStream       inputStream = null;
            try
            {
                // Forge the URL int UTF8 unicode charset
                String encodedScript = URLEncoder.encode(simbadScript, "UTF-8");
                String simbadURL     = _simbadBaseURL + encodedScript;
                if (_logger.isLoggable(Level.FINE)) {
                  _logger.fine("Querying CDS Simbad at " + simbadURL);
                }

                // Launch the network query
                URL               url               = new URL(simbadURL);
                inputStream       = url.openStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader);

                // Read incoming data line by line
                String currentLine = null;

                // reset buffer :
                sb.setLength(0);

                while ((currentLine = bufferedReader.readLine()) != null)
                {
                    if (sb.length() > 0)
                    {
                        sb.append('\n');
                    }

                    sb.append(currentLine);
                }

                _result = sb.toString();

                if (_logger.isLoggable(Level.FINER)) {
                  _logger.finer("CDS Simbad raw result :\n" + _result);
                }
            }
            catch (Exception ex)
            {
                _logger.log(Level.SEVERE, "CDS Connection failed.", ex);

            } finally
            {
              if (inputStream != null) {
                try {
                  inputStream.close();
                } catch (IOException ex) {
                  _logger.log(Level.SEVERE, "CDS Connection closed.", ex);
                }
              }
            }
        }

        public void parseResult()
        {
            _logger.entering("ResolveStarThread", "parseResult");

            try
            {
                // If the result string is empty
                if (_result.length() < 1)
                {
                    _starModel.raiseCDSimbadErrorMessage("No data for star '" +
                        _starName + "'.");
                    throw new IllegalArgumentException("SIMBAD returned an empty result");
                }

                // If there was an error during query
                if (_result.startsWith("::error"))
                {
                    _starModel.raiseCDSimbadErrorMessage(
                        "Querying script execution failed for star '" +
                        _starName + "'.");
                    throw new IllegalArgumentException(
                        "SIMBAD returned a script execution error");
                }

                // Remove any blanking character (~) :
                _result = _result.replaceAll("~[ ]*", "");

                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("CDS Simbad result without blanking values :\n" + _result);
                }

                // Parsing result line by line :

                // Special line tokenizer because it is possible to have a blank lines (no flux at all) :
                final StrictStringTokenizer lineTokenizer = new StrictStringTokenizer(_result, "\n");

                // First line should contain star coordinates, separated by ';'
                String coordinates = lineTokenizer.nextToken();
                parseCoordinates(coordinates);

                // Second line should contain object types, separated by ','
                String objectTypes = lineTokenizer.nextToken();
                parseObjectTypes(objectTypes);

                // Third line should contain star fluxes, separated by ','
                String fluxes = lineTokenizer.nextToken();
                parseFluxes(fluxes);

                // Forth line should contain star proper motions, separated by ';'
                String properMotion = lineTokenizer.nextToken();
                parseProperMotion(properMotion);

                // Fith line should contain star parallax, separated by ';'
                String parallax = lineTokenizer.nextToken();
                parseParallax(parallax);

                // Sixth line should contain star spectral types
                String spectralTypes = lineTokenizer.nextToken();
                parseSpectralTypes(spectralTypes);

                // Seventh line should contain simbad identifiers, separated by '|'
                String identifiers = lineTokenizer.nextToken();
                parseIdentifiers(identifiers);

            }
            catch (IllegalArgumentException iae) {
              if (_logger.isLoggable(Level.FINE)) {
                  _logger.log(Level.FINE, "Invalid CDS Simbad result", iae);
              }
            }
            catch (Exception ex)
            {
                _logger.log(Level.SEVERE, "CDS Simbad result parsing failed", ex);
                _starModel.raiseCDSimbadErrorMessage(
                    "Could not parse received data for star '" + _starName +
                    "'.");

                return;
            }

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
            synchronized(_starModel) {
              _starModel.copy(_newStarModel);

              // Notify all registered observers that the query went fine :
              _starModel.notifyObservers(Star.Notification.QUERY_COMPLETE);
            }
        }

        private void parseCoordinates(String coordinates)
            throws Exception
        {
            if (_logger.isLoggable(Level.FINER)) {
              _logger.finer("Coordinates contains '" + coordinates + "'.");
            }

            StringTokenizer coordinatesTokenizer = new StringTokenizer(coordinates,
                    ";");

            if (coordinatesTokenizer.countTokens() == 4)
            {
                double ra = Double.parseDouble(coordinatesTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("RA_d = '" + ra + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.RA_d, ra);

                double dec = Double.parseDouble(coordinatesTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("DEC_d = '" + dec + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.DEC_d, dec);

                String hmsRa = coordinatesTokenizer.nextToken();
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("RA = '" + hmsRa + "'.");
                }
                _newStarModel.setPropertyAsString(Star.Property.RA, hmsRa);

                String dmsDec = coordinatesTokenizer.nextToken();
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("DEC = '" + dmsDec + "'.");
                }
                _newStarModel.setPropertyAsString(Star.Property.DEC, dmsDec);
            }
            else
            {
                _starModel.raiseCDSimbadErrorMessage(
                    "Could not parse received coordinates for star '" +
                    _starName + "'.");
                throw new ParseException(
                    "Could not parse SIMBAD returned coordinates '" +
                    coordinates + "'", -1);
            }
        }

        private void parseObjectTypes(String objectTypes)
        {
            if (_logger.isLoggable(Level.FINER)) {
              _logger.finer("Object Types contains '" + objectTypes + "'.");
            }

            _newStarModel.setPropertyAsString(Star.Property.OTYPELIST,
                objectTypes);
        }

        private void parseFluxes(String fluxes) throws Exception
        {
            if (_logger.isLoggable(Level.FINER)) {
              _logger.finer("Fluxes contains '" + fluxes + "'.");
            }

            StringTokenizer fluxesTokenizer = new StringTokenizer(fluxes, ",");

            while (fluxesTokenizer.hasMoreTokens())
            {
                String token         = fluxesTokenizer.nextToken();
                String magnitudeBand = "FLUX_" +
                    token.substring(0, 1).toUpperCase(); // The first character is the magnitude band letter
                String value         = token.substring(2); // The second character is "=", followed by the magnitude value in double

                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest(magnitudeBand + " = '" + value + "'.");
                }

                _newStarModel.setPropertyAsDouble(Star.Property.fromString(
                        magnitudeBand), Double.parseDouble(value));
            }
        }

        private void parseProperMotion(String properMotion)
            throws Exception
        {
            if (_logger.isLoggable(Level.FINER)) {
              _logger.finer("Proper Motion contains '" + properMotion + "'.");
            }

            StringTokenizer properMotionTokenizer = new StringTokenizer(properMotion,
                    ";");

            if (properMotionTokenizer.countTokens() == 2)
            {
                double pm_ra = Double.parseDouble(properMotionTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("PROPERMOTION_RA = '" + pm_ra + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PROPERMOTION_RA,
                    pm_ra);

                double pm_dec = Double.parseDouble(properMotionTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("PROPERMOTION_DEC = '" + pm_dec + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PROPERMOTION_DEC,
                    pm_dec);
            }
            else
            {
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("No proper motion data for star '" + _starName + "'.");
                }
            }
        }

        private void parseParallax(String parallax) throws Exception
        {
            if (_logger.isLoggable(Level.FINER)) {
              _logger.finer("Parallax contains '" + parallax + "'.");
            }

            StringTokenizer parallaxTokenizer = new StringTokenizer(parallax,
                    ";");

            if (parallaxTokenizer.countTokens() == 2)
            {
                double plx = Double.parseDouble(parallaxTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("PARALLAX = '" + plx + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PARALLAX, plx);

                double plx_err = Double.parseDouble(parallaxTokenizer.nextToken());
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("PARALLAX_err = '" + plx_err + "'.");
                }
                _newStarModel.setPropertyAsDouble(Star.Property.PARALLAX_err,
                    plx_err);
            }
            else
            {
                if (_logger.isLoggable(Level.FINEST)) {
                  _logger.finest("No parallax data for star '" + _starName + "'.");
                }
            }
        }

        private void parseSpectralTypes(String spectralTypes)
        {
            if (_logger.isLoggable(Level.FINER)) {
              _logger.finer("Spectral Types contains '" + spectralTypes + "'.");
            }

            _newStarModel.setPropertyAsString(Star.Property.SPECTRALTYPES,
                spectralTypes);
        }

        private void parseIdentifiers(String identifiers)
        {
            if (_logger.isLoggable(Level.FINER)) {
              _logger.finer("Identifier contains '" + identifiers + "'.");
            }

            if (identifiers.length() > 0) {
              // remove redundant space characters :
              identifiers = identifiers.replaceAll("[ ]+", " ");
            }

            if (identifiers.length() > 0) {
              // remove last separator :
              identifiers = identifiers.substring(0, identifiers.length() - 1);
            }

            _newStarModel.setPropertyAsString(Star.Property.IDS, identifiers);
        }

    }

    /**
     * StringTokenizer Hack to return empty token if multiple delimiters found
     *
     * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4140850
     */
  private static class StrictStringTokenizer {

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
