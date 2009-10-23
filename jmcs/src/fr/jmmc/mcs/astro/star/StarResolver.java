/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: StarResolver.java,v 1.4 2009-10-23 15:38:20 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLEncoder;

import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.logging.*;


/**
 * Store informations relative to a star.
 */
public class StarResolver
{
    /** Logger - register on the current class to collect local logs */
    private static final Logger _logger = Logger.getLogger(
            "fr.jmmc.mcs.astro.star.StarResolver");

    /** The seeked star name */
    String _starName = null;

    /** The star data container */
    Star _starModel = null;

    /** The thread executing the CDS Simbad query and parsing */
    ResolveStarThread _resolveStarThread = null;

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

        // Launch the query in the background in order to keed GUI updated
        _starModel.clear();
        _resolveStarThread = new ResolveStarThread();
        _resolveStarThread.start();
    }

    /**
     * Main.
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

        public void run()
        {
            _logger.entering("ResolveStarThread", "run");

            querySimbad();

            _logger.fine("CDS Simbad raw result :\n" + _result);

            parseResult();
        }

        public void querySimbad()
        {
            _logger.entering("ResolveStarThread", "querySimbad");

            // Re-initializing the _result
            _result = "";

            if (_starName.length() != 0)
            {
                // The script to execute
                String simbadScript = "output console=off script=off\n"; // Just data
                simbadScript += "format object form1 \"";
                simbadScript += "%COO(d;A);%COO(d;D);%COO(A);%COO(D);\\n%OTYPELIST\\n%FLUXLIST(V,I,J,H,K;N=F,)";
                simbadScript += "\"\n";
                simbadScript += ("query id " + _starName); // Add the object name we are looking for

                // Getting the result
                try
                {
                    // Forging the URL int UTF8 unicode charset
                    String simbadURL = _simbadBaseURL +
                        URLEncoder.encode(simbadScript, "UTF-8");
                    URL    url       = new URL(simbadURL);

                    _logger.finer("CDS Simbad raw result :\n" + simbadURL);

                    // Launching the query
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(
                                url.openStream()));

                    // Reading the result line by line
                    String currentLine;

                    while ((currentLine = rdr.readLine()) != null)
                    {
                        if (_result.length() > 0)
                        {
                            _result += "\n";
                        }

                        _result += currentLine;
                    }
                }
                catch (Exception ex)
                {
                    _logger.log(Level.SEVERE, "CDS Connection failed.", ex);

                    return;
                }
            }
            else
            {
                //@TODO : Assertion - should never receive an empty scence object name
                _logger.severe("Received an empty star name");
                _starModel.raiseCDSimbadErrorMessage(
                    "Could not resolve star with '" + _starName + "'.");
            }
        }

        public void parseResult()
        {
            _logger.entering("ResolveStarThread", "parseResult");

            System.out.println("_result = " + _result);

            try
            {
                // If the result srting is empty
                if (_result.length() < 1)
                {
                    _starModel.raiseCDSimbadErrorMessage("No data received.");
                    throw new Exception("SIMBAD returned an empty result");
                }

                // If there was an error during query
                if (_result.startsWith("::error"))
                {
                    _starModel.raiseCDSimbadErrorMessage(
                        "Querying script execution failed.");
                    throw new Exception(
                        "SIMBAD returned a script execution error");
                }

                // Parsing result line by line
                StringTokenizer lineTokenizer = new StringTokenizer(_result,
                        "\n");

                // First line should contain star coordinates, separated by ';'
                String          coordinates          = lineTokenizer.nextToken();
                StringTokenizer coordinatesTokenizer = new StringTokenizer(coordinates,
                        ";");
                _logger.finer("Coordinates contain '" + coordinates + "'.");

                if (coordinatesTokenizer.countTokens() == 4)
                {
                    double ra = Double.parseDouble(coordinatesTokenizer.nextToken());
                    _logger.finest("RA_d = '" + ra + "'.");
                    _starModel.setPropertyAsDouble(Star.Property.RA_d, ra);

                    double dec = Double.parseDouble(coordinatesTokenizer.nextToken());
                    _logger.finest("DEC_d = '" + dec + "'.");
                    _starModel.setPropertyAsDouble(Star.Property.DEC_d, dec);

                    String hmsRa = coordinatesTokenizer.nextToken();
                    _logger.finest("RA = '" + hmsRa + "'.");
                    _starModel.setPropertyAsString(Star.Property.RA, hmsRa);

                    String dmsDec = coordinatesTokenizer.nextToken();
                    _logger.finest("DEC = '" + dmsDec + "'.");
                    _starModel.setPropertyAsString(Star.Property.DEC, dmsDec);
                }
                else
                {
                    _starModel.raiseCDSimbadErrorMessage(
                        "Could not parse received data.");
                    throw new Exception(
                        "Could not parse SIMBAD returned coordinates");
                }

                // Second line should contain object types, separated by ','
                String objectTypes = lineTokenizer.nextToken();
                _starModel.setPropertyAsString(Star.Property.OTYPELIST,
                    objectTypes);
                _logger.finer("OTYPELIST = '" + objectTypes + "'.");

                // Third line should contain star fluxes, separated by ','
                String          fluxes          = lineTokenizer.nextToken();
                StringTokenizer fluxesTokenizer = new StringTokenizer(fluxes,
                        ",");
                _logger.finer("Fluxes contain '" + fluxes + "'.");

                while (fluxesTokenizer.hasMoreTokens())
                {
                    String token         = fluxesTokenizer.nextToken();
                    String magnitudeBand = "FLUX_" +
                        token.substring(0, 1).toUpperCase(); // The first character is the magnutude band letter
                    String value         = token.substring(2); // The second character is "=", followed by the magnitude value in double

                    _logger.finest(magnitudeBand + " = '" + value + "'.");

                    _starModel.setPropertyAsDouble(Star.Property.fromString(
                            magnitudeBand), Double.parseDouble(value));
                }
            }
            catch (Exception ex)
            {
                _logger.log(Level.SEVERE, "CDS Simbad result parsing failed", ex);
                _starModel.raiseCDSimbadErrorMessage(
                    "Could not parse received data.");

                return;
            }

            _starModel.notifyObservers();
        }
    }
}
/*___oOo___*/
