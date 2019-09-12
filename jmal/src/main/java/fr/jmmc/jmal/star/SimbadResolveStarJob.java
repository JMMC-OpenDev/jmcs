/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import static fr.jmmc.jmal.star.Star.SEPARATOR_COMMA;
import static fr.jmmc.jmal.star.StarResolver.SEPARATOR_SEMI_COLON;
import fr.jmmc.jmal.util.StrictStringTokenizer;
import fr.jmmc.jmcs.data.preference.SessionSettingsPreferences;
import fr.jmmc.jmcs.network.http.Http;
import fr.jmmc.jmcs.util.CollectionUtils;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Star resolver job: launch and handle CDS SIMBAD query
 * note: this implementation is only usable from the jmal.star package (internal use)
 */
final class SimbadResolveStarJob implements Callable<StarResolverResult> {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(SimbadResolveStarJob.class.getName());
    /** Simbad value for the read timeout in milliseconds (15 seconds) */
    public static final int SIMBAD_SOCKET_READ_TIMEOUT = 15 * 1000;
    /** true to enable query caching (development mode) */
    public static final boolean USE_CACHE_DEV = false;

    /** custom entry separator */
    public static final String MARKER_ENTRY = ":entry:";
    /** simbad error block separator */
    public static final String MARKER_ERROR = "::error";
    /** simbad data block separator */
    public static final String MARKER_DATA = "::data";

    /* members */
    /** optional flags associated with the query */
    private final Set<String> _flags;
    /** callback listener with results */
    private final StarResolverProgressListener _listener;
    /** result */
    private final StarResolverResult _result;
    /** running thread name (only defined during the background execution; null otherwise) */
    private String threadName = null;
    /** current star name during parsing */
    private String _currentName = null;
    /** SIMBAD query response */
    private String _response = null;
    /** temporary parsing result */
    private Star _parsedStar = null;

    /**
     * @param flags optional flags associated with the query
     * @param names list of queried identifiers
     * @param listener callback listener with results
     */
    SimbadResolveStarJob(final Set<String> flags, final List<String> names, final StarResolverProgressListener listener) {
        _flags = flags;
        _listener = listener;
        _result = new StarResolverResult(names);
    }

    /**
     * Cancel any http request in progress (close socket)
     * Called by another thread
     */
    void cancel() {
        _logger.debug("SimbadResolveStarJob.cancel");
        if (this.threadName != null) {
            Http.abort(this.threadName);
        }
    }

    @Override
    public StarResolverResult call() {
        _logger.debug("SimbadResolveStarJob.run");

        if (_listener != null) {
            _listener.handleProgressMessage("searching CDS Simbad data for star(s) "
                    + _result.getNames() + " ... (please wait, this may take a while)");
        }
        // define the thread name:
        this.threadName = Thread.currentThread().getName();
        try {
            querySimbad();

            if (Thread.currentThread().isInterrupted()) {
                handleError(StarResolverStatus.ERROR_IO, "CDS Simbad star resolution cancelled.");
            } else {
                parseResult();

                // If everything went fine, set status to OK
                if (!_result.isErrorStatus()) {
                    _result.setStatus(StarResolverStatus.OK);
                    if (_listener != null) {
                        _listener.handleProgressMessage("CDS Simbad star resolution done.");
                    }
                }
            }

        } catch (IOException ioe) {
            handleError(StarResolverStatus.ERROR_IO, ioe.getMessage());
        } catch (IllegalStateException ise) {
            _logger.info("Parsing error on the CDS Simbad response:\n{}", _response);
            handleError(StarResolverStatus.ERROR_PARSING, ise.getMessage());
        } finally {
            // anyway: process result
            if (_listener != null) {
                _listener.handleResult(_result);
            }
        }
        return _result;
    }

    private void handleError(final StarResolverStatus status, final String errorMessage) {
        if (status == StarResolverStatus.ERROR_SERVER) {
            _result.setServerErrorMessage(errorMessage);
        } else {
            _result.setErrorMessage(status, errorMessage);
        }
        if (_listener != null) {
            _listener.handleProgressMessage("CDS Simbad star resolution failed.");
        }
    }

    /**
     * Query SIMBAD using script
     * @throws IllegalStateException if the star name is empty
     * @throws IOException if no Simbad mirror is responding
     */
    private void querySimbad() throws IllegalArgumentException, IOException {
        _logger.trace("SimbadResolveStarJob.querySimbad");

        // Should never receive an empty scence object name
        if (CollectionUtils.isEmpty(_result.getNames())) {
            throw new IllegalStateException("Could not resolve empty star name.");
        }

        // Reset result before proceeding
        _response = "";

        // In development: load cached query results:
        final File cachedFile;
        if (USE_CACHE_DEV) {
            cachedFile = generateCacheFile(_result.getNames());

            if (cachedFile.exists()) {
                try {
                    _response = FileUtils.readFile(cachedFile);

                    _logger.info("using cached result: " + cachedFile.getAbsolutePath());
                    return;
                } catch (IOException ioe) {
                    _logger.info("unable to read cached result: " + cachedFile.getAbsolutePath(), ioe);
                }
            }
        } else {
            cachedFile = null;
        }

        // buffer used for both script and result:
        final StringBuilder sb = new StringBuilder(2048);
        // Forge Simbad script to execute
        sb.append("output console=off script=off\n"); // Just data
        sb.append("format object form1 \""); // Simbad script preambule
        sb.append(MARKER_ENTRY).append("%OBJECT\\n"); // Simbad entry custom marker ':entry:' followed by the queried object name
        sb.append("%MAIN_ID\\n"); // Main identifier (display)
        sb.append("%COO(d;A);%COO(d;D);%COO(A);%COO(D);\\n"); // RA and DEC coordinates as sexagesimal and decimal degree values
        sb.append("%OTYPELIST\\n"); // Object types enumeration
        sb.append("%FLUXLIST(B,V,G,R,I,J,H,K;N=F,)\\n"); // Magnitudes among [B,V,G,R,I,J,H,K], 'Band=Value' format
        sb.append("%PM(A;D)\\n"); // Proper motion with error
        sb.append("%PLX(V;E)\\n"); // Parallax with error
        sb.append("%SP(S)\\n"); // Spectral types enumeration
        sb.append("%RV(V;W)\\n"); // Radial velocity
        sb.append("%IDLIST[%*,]"); // Simbad identifiers
        sb.append("\"\n"); // Simbad script end

        // loop on identifiers to build several 'query id <ID>' lines
        // Note: simbad supports ';' separated values but it produces strange error messages for invalid identifiers !
        for (String id : _result.getNames()) {
            sb.append("query id ").append(id).append('\n'); // Add each object name we are looking for
        }

        final String simbadScript = sb.toString();
        _logger.trace("CDS Simbad script:\n{}", simbadScript);

        // Get the shared HTTP client to send queries to Simbad (multithread support)
        final HttpClient client = Http.getHttpClient();

        /** Get the current thread to check if the query is cancelled */
        final Thread currentThread = Thread.currentThread();

        // Use prefered Simbad mirror
        String simbadMirror = StarResolver.getSimbadMirror();
        String simbadURL;
        final Set<String> failedUrl = new HashSet<String>(4);
        List<String> ioMessages = null;

        // Retry other mirrors if needed
        while ((simbadMirror != null) && (!currentThread.isInterrupted())) {
            // Try to get star data from CDS
            simbadURL = StarResolver.getSimbadUrl();

            _logger.debug("Querying CDS Simbad: {}", simbadURL);
            PostMethod method = null;
            try {
                final long start = System.nanoTime();

                // create the HTTP Post method:
                method = new PostMethod(simbadURL);
                // customize timeouts:
                method.getParams().setSoTimeout(SIMBAD_SOCKET_READ_TIMEOUT);
                // define query script:
                method.addParameter("script", simbadScript);

                // execute query:
                _response = Http.execute(client, method);

                _logger.info("SimbadResolveStarJob.querySimbad: duration = {} ms.", 1e-6d * (System.nanoTime() - start));
                // exit from loop:
                return;
            } catch (IOException ioe) {
                final String eMsg = getExceptionMessage(ioe);
                _logger.info("Simbad connection failed: {}", eMsg);
                if (ioMessages == null) {
                    ioMessages = new ArrayList<String>(5);
                }
                ioMessages.add("[" + simbadMirror + "] " + eMsg);
                failedUrl.add(simbadURL);
                // get another simbad mirror:
                simbadMirror = StarResolver.getNextSimbadMirror(failedUrl);
                if (simbadMirror != null) {
                    _logger.info("Trying another Simbad mirror [{}]", simbadMirror);
                    if (_listener != null) {
                        _listener.handleProgressMessage("Simbad connection failed: trying another mirror [" + simbadMirror + "] ...");
                    }
                } else {
                    // no more mirror to use (probably bad network settings):
                    ioMessages.add("\nPlease check your network connection !");

                    // reset buffer:
                    sb.setLength(0);
                    sb.append("Simbad connection failed:");
                    for (String msg : ioMessages) {
                        sb.append('\n').append(msg);
                    }

                    throw new IOException(sb.toString());
                }
            } finally {
                if ((method != null) && method.isAborted()) {
                    currentThread.interrupt();
                }

                // In development: save cached query results:
                if (USE_CACHE_DEV && _response.length() != 0) {
                    try {
                        FileUtils.writeFile(cachedFile, _response);

                        _logger.info("saving cached result: " + cachedFile.getAbsolutePath());
                    } catch (IOException ioe) {
                        _logger.info("unable to write cached result: " + cachedFile.getAbsolutePath(), ioe);
                    }
                }
            }
        }
    }

    /**
     * Parse SIMBAD response
     * @throws IllegalStateException if parsing error
     */
    private void parseResult() {
        _logger.trace("SimbadResolveStarJob.parseResult");
        _logger.debug("CDS Simbad raw response:\n{}", _response);
        // If the response is null (when simbad server fails)
        if (_response == null) {
            throw new IllegalStateException("No data for star(s) " + _result.getNames() + ", Simbad service may be off or unreachable.");
        }
        // If the response string is empty
        if (_response.length() < 1) {
            throw new IllegalStateException("No data for star(s) " + _result.getNames() + ".");
        }
        String stream = _response;
        // If there was an error during query
        if (stream.startsWith(MARKER_ERROR)) {
            // sample error (name not found):
            /*
             ::error:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

             [3] java.text.ParseException: Unrecogniezd identifier: aasioi
             [5] java.text.ParseException: Unrecogniezd identifier: bad
             [6] Identifier not found in the database : NAME TEST

             ::data::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

             :entry:l car
             */
            // try to get error message:
            int posStart = stream.indexOf('\n');
            if (posStart != -1) {
                posStart++;
            }
            int posEnd = stream.indexOf(MARKER_DATA, posStart);
            final String errorMessage = (posEnd == -1) ? stream.substring(posStart) : stream.substring(posStart, posEnd);
            _logger.debug("CDS error:\n{}", errorMessage);

            handleError(StarResolverStatus.ERROR_SERVER, "Querying script execution failed:" + errorMessage);

            // try to get data block:
            if (posEnd == -1) {
                return;
            }
            posStart = stream.indexOf('\n', posEnd);
            if (posStart == -1) {
                return;
            }
            posStart = stream.indexOf(MARKER_ENTRY, posStart);
            if (posStart == -1) {
                return;
            }
            // fix data stream:
            stream = stream.substring(posStart);
        }
        // Remove any blanking character (~):
        stream = stream.replaceAll("~[ ]*", StringUtils.STRING_EMPTY);
        _logger.debug("CDS Simbad response without blanking values:\n{}", stream);

        // Parsing result line by line:
        // Special line tokenizer because it is possible to have a blank lines (no flux at all):
        final StrictStringTokenizer lineTokenizer = new StrictStringTokenizer(stream, "\n");

        try {
            /** Get the current thread to check if the query is cancelled */
            final Thread currentThread = Thread.currentThread();

            String name;

            // Parsing result entry by entry:
            for (; !currentThread.isInterrupted();) {
                // line 1 should contain ':entry:' followed by the queried object name
                _currentName = name = parseEntry(lineTokenizer.nextToken());
                if (name == null) {
                    break;
                }
                // line 2 should contain the Main identifier (display)
                parseMainIdentifier(lineTokenizer.nextToken());
                // line 3 should contain star coordinates, separated by ';'
                parseCoordinates(lineTokenizer.nextToken());
                // line 4 should contain object types, separated by ','
                parseObjectTypes(lineTokenizer.nextToken());
                // line 5 should contain star fluxes, separated by ','
                parseFluxes(lineTokenizer.nextToken());
                // line 6 should contain star proper motions, separated by ';'
                parseProperMotion(lineTokenizer.nextToken());
                // line 7 should contain star parallax, separated by ';'
                parseParallax(lineTokenizer.nextToken());
                // line 8 should contain star spectral types
                parseSpectralTypes(lineTokenizer.nextToken());
                // line 9 should contain radial velocity, separated by ';'
                parseRadialVelocity(lineTokenizer.nextToken());
                // line 10 should contain SIMBAD identifiers, separated by ','
                parseIdentifiers(lineTokenizer.nextToken());

                // Check coordinates:
                if ((_parsedStar.getPropertyAsDouble(Star.Property.RA_d) != null)
                        && (_parsedStar.getPropertyAsDouble(Star.Property.DEC_d) != null)) {
                    // Add entry into results:
                    _result.addStar(name, _parsedStar);
                } else {
                    _logger.debug("skip entry (no coordinates):\n{}", _parsedStar);
                }
                _parsedStar = null;
            }

        } catch (NumberFormatException nfe) {
            throw new IllegalStateException("Could not parse data for star '" + _currentName + "':\n" + nfe.getMessage());
        } catch (ParseException pe) {
            throw new IllegalStateException("Could not parse data for star '" + _currentName + "':\n" + pe.getMessage());
        }
    }

    /**
     * Parse the simbad entry: marker followed by queried object name
     * @param markerLine first line
     * @return queried object if the marker line is valid; null otherwise
     */
    private String parseEntry(final String markerLine) {
        _logger.debug("Marker line contains '{}'.", markerLine);
        int pos = markerLine.indexOf(MARKER_ENTRY);
        if (pos == -1) {
            return null;
        }
        // Create a new star model:
        _parsedStar = new Star();
        final String object = markerLine.substring(pos + MARKER_ENTRY.length());
        _parsedStar.setName(object);
        return object;
    }

    /**
     * Parse the main identifier
     * @param mainId SIMBAD main identifier
     */
    private void parseMainIdentifier(final String mainId) {
        _logger.debug("MainId contains '{}'.", mainId);
        // remove redundant space characters:
        final String cleanedId = StringUtils.cleanWhiteSpaces(mainId);
        _parsedStar.setPropertyAsString(Star.Property.MAIN_ID, cleanedId);
    }

    /**
     * Parse star RA / DEC coordinates
     * @param coordinates SIMBAD RA / DEC coordinates
     * @throws ParseException if parsing SIMBAD RA/DEC failed
     * @throws NumberFormatException if parsing number(s) failed
     */
    private void parseCoordinates(final String coordinates) throws ParseException, NumberFormatException {
        _logger.debug("Coordinates contains '{}'.", coordinates);
        final StringTokenizer coordinatesTokenizer = new StringTokenizer(coordinates, SEPARATOR_SEMI_COLON);
        if (coordinatesTokenizer.countTokens() == 4) {
            try {
                final double ra = Double.parseDouble(coordinatesTokenizer.nextToken());
                if (_logger.isTraceEnabled()) {
                    _logger.trace("RA_d = '{}'.", ra);
                }
                _parsedStar.setPropertyAsDouble(Star.Property.RA_d, ra);
                final double dec = Double.parseDouble(coordinatesTokenizer.nextToken());
                if (_logger.isTraceEnabled()) {
                    _logger.trace("DEC_d = '{}'.", dec);
                }
                _parsedStar.setPropertyAsDouble(Star.Property.DEC_d, dec);
                final String hmsRa = coordinatesTokenizer.nextToken();
                if (_logger.isTraceEnabled()) {
                    _logger.trace("RA = '{}'.", hmsRa);
                }
                _parsedStar.setPropertyAsString(Star.Property.RA, hmsRa);
                final String dmsDec = coordinatesTokenizer.nextToken();
                if (_logger.isTraceEnabled()) {
                    _logger.trace("DEC = '{}'.", dmsDec);
                }
                _parsedStar.setPropertyAsString(Star.Property.DEC, dmsDec);
            } catch (NumberFormatException nfe) {
                // special case: Coordinates contains 'No Coord.;No Coord.;No Coord.;No Coord.;'.
                _logger.debug("Invalid coordinates '{}'", coordinates);
            }
        } else {
            throw new ParseException("Invalid coordinates '" + coordinates + "'", -1);
        }
    }

    /**
     * Parse object types
     * @param objectTypes SIMBAD object types
     */
    private void parseObjectTypes(final String objectTypes) {
        _logger.debug("Object Types contains '{}'.", objectTypes);
        _parsedStar.setPropertyAsString(Star.Property.OTYPELIST, objectTypes);
    }

    /**
     * Parse magnitudes
     * @param fluxes SIMBAD fluxes
     * @throws NumberFormatException if parsing number(s) failed
     */
    private void parseFluxes(final String fluxes) throws NumberFormatException {
        _logger.debug("Fluxes contains '{}'.", fluxes);
        final StringTokenizer fluxesTokenizer = new StringTokenizer(fluxes, SEPARATOR_COMMA);
        while (fluxesTokenizer.hasMoreTokens()) {
            final String token = fluxesTokenizer.nextToken();
            // The first character is the magnitude band letter:
            final String magnitudeBand = "FLUX_" + token.substring(0, 1).toUpperCase();
            // The second character is "=", followed by the magnitude value in double:
            final String value = token.substring(2);
            if (_logger.isTraceEnabled()) {
                _logger.trace("{} = '{}'.", magnitudeBand, value);
            }
            _parsedStar.setPropertyAsDouble(Star.Property.fromString(magnitudeBand), Double.parseDouble(value));
        }
    }

    /**
     * Parse optional proper motion
     * @param properMotion
     * @throws NumberFormatException if parsing number(s) failed
     */
    private void parseProperMotion(final String properMotion) throws NumberFormatException {
        _logger.debug("Proper Motion contains '{}'.", properMotion);
        final StringTokenizer properMotionTokenizer = new StringTokenizer(properMotion, SEPARATOR_SEMI_COLON);
        if (properMotionTokenizer.countTokens() == 2) {
            final double pm_ra = Double.parseDouble(properMotionTokenizer.nextToken());
            if (_logger.isTraceEnabled()) {
                _logger.trace("PROPERMOTION_RA = '{}'.", pm_ra);
            }
            _parsedStar.setPropertyAsDouble(Star.Property.PROPERMOTION_RA, pm_ra);
            final double pm_dec = Double.parseDouble(properMotionTokenizer.nextToken());
            if (_logger.isTraceEnabled()) {
                _logger.trace("PROPERMOTION_DEC = '{}'.", pm_dec);
            }
            _parsedStar.setPropertyAsDouble(Star.Property.PROPERMOTION_DEC, pm_dec);
        } else {
            if (_logger.isTraceEnabled()) {
                _logger.trace("No proper motion data for star '{}'.", _currentName);
            }
        }
    }

    /**
     * Parse optional parallax
     * @param parallax SIMBAD parallax
     * @throws NumberFormatException if parsing number(s) failed
     */
    private void parseParallax(final String parallax) throws NumberFormatException {
        _logger.debug("Parallax contains '{}'.", parallax);
        final StringTokenizer parallaxTokenizer = new StringTokenizer(parallax, SEPARATOR_SEMI_COLON);
        if (parallaxTokenizer.countTokens() == 2) {
            final double plx = Double.parseDouble(parallaxTokenizer.nextToken());
            if (_logger.isTraceEnabled()) {
                _logger.trace("PARALLAX = '{}'.", plx);
            }
            _parsedStar.setPropertyAsDouble(Star.Property.PARALLAX, plx);
            final double plx_err = Double.parseDouble(parallaxTokenizer.nextToken());
            if (_logger.isTraceEnabled()) {
                _logger.trace("PARALLAX_err = '{}'.", plx_err);
            }
            _parsedStar.setPropertyAsDouble(Star.Property.PARALLAX_err, plx_err);
        } else {
            if (_logger.isTraceEnabled()) {
                _logger.trace("No parallax data for star '{}'.", _currentName);
            }
        }
    }

    /**
     * Parse spectral types
     * @param spectralTypes SIMBAD spectral types
     */
    private void parseSpectralTypes(final String spectralTypes) {
        _logger.debug("Spectral Types contains '{}'.", spectralTypes);
        _parsedStar.setPropertyAsString(Star.Property.SPECTRALTYPES, spectralTypes);
    }

    /**
     * Parse optional radial velocity
     * @param radialVelocity SIMBAD radial velocity
     * @throws NumberFormatException if parsing number(s) failed
     */
    private void parseRadialVelocity(final String radialVelocity) throws NumberFormatException {
        _logger.debug("Radial velocity contains '{}'.", radialVelocity);
        final StringTokenizer rvTokenizer = new StringTokenizer(radialVelocity, SEPARATOR_SEMI_COLON);
        if (rvTokenizer.countTokens() > 0) {
            final double rv = Double.parseDouble(rvTokenizer.nextToken());
            if (_logger.isTraceEnabled()) {
                _logger.trace("RV = '{}'.", rv);
            }
            _parsedStar.setPropertyAsDouble(Star.Property.RV, rv);
            if (rvTokenizer.hasMoreTokens()) {
                final String rv_def = rvTokenizer.nextToken();
                if (_logger.isTraceEnabled()) {
                    _logger.trace("RV_DEF = '{}'.", rv_def);
                }
                _parsedStar.setPropertyAsString(Star.Property.RV_DEF, rv_def);
            }
        } else {
            if (_logger.isTraceEnabled()) {
                _logger.trace("No radial velocity data for star '{}'.", _currentName);
            }
        }
    }

    /**
     * Parse optional identifiers
     * @param identifiers SIMBAD identifiers
     */
    private void parseIdentifiers(final String identifiers) {
        _logger.debug("Identifiers contain '{}'.", identifiers);
        // remove redundant space characters:
        // note: simbad does trim and do not gives white spaces arround the comma separator:
        String cleanedIds = StringUtils.cleanWhiteSpaces(identifiers);
        if (cleanedIds.length() > 0) {
            // remove last separator:
            cleanedIds = cleanedIds.substring(0, cleanedIds.length() - 1);
        }
        _parsedStar.setPropertyAsString(Star.Property.IDS, cleanedIds);

        if (_flags == null || !_flags.contains(StarResolver.FLAG_SKIP_FIX_NAME)) {
            // Compare the star name with identifiers to find one proper name (case sensitive):
            final String name = _parsedStar.getName(); // should be white space cleaned
            final String[] ids = cleanedIds.split(SEPARATOR_COMMA);
            final int len = ids.length;

            // name 'hd 1234' matches 'HD 1234' => 'HD 1234'
            for (int i = 0; i < len; i++) {
                final String id = ids[i];
                if (id.equalsIgnoreCase(name)) {
                    _logger.debug("found ID: '{}' for name '{}'.", id, name);
                    _parsedStar.setName(id);
                    return;
                }
            }

            // remove all white spaces and convert to lower case:
            final String nameLowerClean = StringUtils.removeWhiteSpaces(name).toLowerCase();

            // Find the name into possible identifiers (case & white space ignored)
            // name 'eps aur' matches '* eps Aur' => 'eps Aur'
            // name 'hd1234' matches 'HD 1234' => 'HD 1234'
            // name 'MWC297' matches 'EM* MWC297' => 'MWC297'
            for (int i = 0; i < len; i++) {
                final String id = ids[i];
                final int pos = indexOfIgnoreCaseAndWhiteSpace(id, nameLowerClean);
                if (pos != -1) {
                    final String idPart = id.substring(pos);
                    _logger.debug("found ID: '{}' for name '{}'.", idPart, name);
                    _parsedStar.setName(idPart);
                    return;
                }
            }
            _logger.debug("CHECK: no ID found for name '{}' among '{}'.", name, cleanedIds);
        }
    }

    /**
     * Special indexOf() implementation ignoring case and white spaces in the source string.
     * The target string must be first converted to lower case and must not have any space character
     *
     * @param   source       the characters being searched.
     * @param   target       the characters being searched for. (lower case & no space character)
     * @return position or -1 if not found
     */
    static int indexOfIgnoreCaseAndWhiteSpace(final String source, final String target) {
        final int sourceCount = source.length();
        final int targetCount = target.length();

        char first = target.charAt(0); // lower case & no space character
        int max = (sourceCount - targetCount);

        for (int i = 0; i <= max; i++) {
            /* Look for first character (ignore case). */
            if (Character.toLowerCase(source.charAt(i)) != first) {
                while (++i <= max && Character.toLowerCase(source.charAt(i)) != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int count = targetCount - 1;
                for (int k = 1; j < sourceCount;) {
                    char sj = Character.toLowerCase(source.charAt(j));
                    if (sj == ' ') {
                        j++;
                        continue;
                    }
                    char tk = target.charAt(k); // lower case & no space character
                    // sj & tk are not ' ':
                    if (sj != tk) {
                        break;
                    }
                    count--;
                    if (count == 0) {
                        /* Found whole string. */
                        return i;
                    }
                    j++;
                    k++;
                }
            }
        }
        return -1;
    }

    /**
     * Format specific exception messages
     * @param e exception
     * @return user message for the given exception
     */
    private String getExceptionMessage(final Exception e) {
        if (e instanceof UnknownHostException) {
            return "Unknown host [" + e.getMessage() + "]";
        }
        return e.getMessage();
    }

    private static File generateCacheFile(final List<String> nameList) {
        final String parentPath = SessionSettingsPreferences.getApplicationFileStorage();
        // assert that parent directory exist
        new File(parentPath).mkdirs();

        final int nIds = nameList.size();
        final String[] names = nameList.toArray(new String[nIds]);

        // Copy and sort ids to have an hashcode independent from ordering:
        Arrays.sort(names);

        final StringBuilder sb = new StringBuilder(2048);

        // loop on identifiers to build cached key: '<ID>,'
        for (String id : names) {
            sb.append(id).append(','); // Add each object name we are looking for
        }
        final int hash_ids = sb.toString().hashCode();

        // Form file name:
        sb.setLength(0);
        sb.append("Simbad_").append(nIds).append('_');
        sb.append(names[0]).append('-').append(names[nIds - 1]).append('_');;
        sb.append(hash_ids).append(".dat");

        final String fileName = sb.toString();

        return new File(parentPath, fileName);
    }

    public static void main(String[] args) {
        final SimbadResolveStarJob job = new SimbadResolveStarJob(null, Arrays.asList("TEST"), null);

        if (false) {
            job._response = ":entry:eps aur\n"
                    + "* eps Aur\n"
                    + "075.49221855;+43.82330720;05 01 58.13245;+43 49 23.9059;\n"
                    + "**,Al*,SB*,*,Em*,V*,IR,UV\n"
                    + "V=2.99,I=2.02,J=1.83,H=1.702,K=1.48,\n"
                    + "-0.86 ;-2.66 \n"
                    + "1.53 ;1.29\n"
                    + "A8Iab:\n"
                    + "-10.40;~\n"
                    + "2MASS J05015812+4349241,PLX 1122,SBC9 291,* eps Aur,*   7 Aur,AAVSO 0454+43,ADS  3605 A,AG+43  552,ALS  8131,BD+43  1166,CCDM J05020+4350A,CSI+43  1166  1,EM* CDS  456,FK5  183,GC  6123,GCRV  2970,GEN# +1.00031964J,GSC 02907-01275,HD  31964,HIC  23416,HIP  23416,HR  1605,IDS 04548+4341 A,IRAS 04583+4345,IRC +40109,JP11   959,LF  7 +43   70,LS   V +43   23,N30 1068,PMC 90-93   131,PPM  47627,RAFGL  670S,RAFGL  670,ROT   705,SAO  39955,SBC7   200,SKY#  7879,TD1  3824,TYC 2907-1275-1,UBV    4807,UBV M  10528,V* eps Aur,[KW97] 20-37,uvby98 100031964 ABV,UCAC3 268-74264,WDS J05020+4349A,\n"
                    + "";
            job.parseResult();
            _logger.info("star result:\n{}", job._result);
        }
        if (false) {
            job._response = ":entry:car\n"
                    + "NAME CAR ARM\n"
                    + "150.0;-60.0;10 00;-60.0;\n"
                    + "PoG\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR ARM,NAME CARINA ARM,NAME CARINA SPIRAL ARM,\n"
                    + ":entry:car\n"
                    + "NAME CAR I\n"
                    + "160.883;-59.583;10 43 32;-59 35.0;\n"
                    + "HII\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR I,NAME CARINA I,\n"
                    + ":entry:car\n"
                    + "NAME CAR II\n"
                    + "161.196;-59.647;10 44 47;-59 38.8;\n"
                    + "HII\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR II,NAME CARINA II,NAME CARINA 2,\n"
                    + ":entry:car\n"
                    + "NGC  3372\n"
                    + "161.0792;-59.8892;10 44 19.0;-59 53 21;\n"
                    + "ISM,HII,Rad,X\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME KEYHOLE,1E 1044.0-5940,BRAN 316A,GUM 33,MHR 11,MSH 10-5-07,NAME CAR NEBULA,NAME CARINA NEBULA,NAME KEYHOLE NEBULA,NAME ETA CAR NEBULA,NGC  3372,RCW  53,\n"
                    + ":entry:car\n"
                    + "NAME CAR OB2 HI SHELL\n"
                    + "166.5;-60.0;11 06;-60.0;\n"
                    + "sh\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR OB2 HI SHELL,NAME CARINA OB2 HI SHELL,\n"
                    + ":entry:car\n"
                    + "NAME CARINA dSph\n"
                    + "100.4029;-50.9661;06 41 36.7;-50 57 58;\n"
                    + "G,G\n"
                    + "V=11.0,\n"
                    + "0.220 ;0.150 \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "222.9;~\n"
                    + "NAME CARINA dSph,EQ 0640-509,ESO 206-20A,ESO-LV 206-2200,LEDA   19441,NAME CAR DWARF GALAXY,NAME CARINA DWARF GALAXY,NAME CARINA GALAXY,NAME CAR dSph,SGC 064024-5055.0,[FG85] 193,[VDD93]  55,AM 0640-505,\n"
                    + ":entry:car\n"
                    + "NAME CAR-CEN REGION\n"
                    + "No Coord.;No Coord.;No Coord.;No Coord.;\n"
                    + "reg\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR-CEN REGION,\n"
                    + ":entry:car\n"
                    + "NAME CAR-SGE\n"
                    + "No Coord.;No Coord.;No Coord.;No Coord.;\n"
                    + "\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR-SGE,\n"
                    + ":entry:car\n"
                    + "NAME CAR-SGR\n"
                    + "No Coord.;No Coord.;No Coord.;No Coord.;\n"
                    + "\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR-SGR,\n"
                    + ":entry:car\n"
                    + "NAME CAR-SGR ARM\n"
                    + "225.0;-40.0;15 00;-40.0;\n"
                    + "reg,PoG\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR-SGR ARM,NAME SGR-CAR ARM,NAME SGR-CARINA ARM,NAME SAGITTAIRE-CARENE,NAME SAGITTARIUS-CARINA ARM,NAME CARINA-SAGITTARIUS ARM,\n"
                    + ":entry:car\n"
                    + "NAME CAR-VEL REGION\n"
                    + "No Coord.;No Coord.;No Coord.;No Coord.;\n"
                    + "reg\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CAR-VEL REGION,NAME CARINA-VELA,\n"
                    + ":entry:car\n"
                    + "NAME CARINA ASSOCIATION\n"
                    + "No Coord.;No Coord.;No Coord.;No Coord.;\n"
                    + "As*\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME CARINA ASSOCIATION,NAME Car Association,\n"
                    + ":entry:car\n"
                    + "NAME Car 291.6-01.9\n"
                    + "167.8;-62.5;11 11;-62.5;\n"
                    + "MoC\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME Car 291.6-01.9,\n"
                    + ":entry:car\n"
                    + "NAME Car 291.6-01.9 outflow\n"
                    + "167.6875;-62.4875;11 10 45.0;-62 29 15;\n"
                    + "Y*?,out\n"
                    + "\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "~\n"
                    + "~;~\n"
                    + "NAME Car 291.6-01.9 outflow,\n"
                    + "";
            job.parseResult();
            _logger.info("star result:\n{}", job._result);
        }
        if (false) {
            job._response = ":entry:l car\n"
                    + "* l Car\n"
                    + "146.31171343;-62.50790330;09 45 14.81122;-62 30 28.4519;\n"
                    + "*,V*,cC*,IR,UV\n"
                    + "V=3.40,J=1.684,H=1.188,K=1.048,\n"
                    + "-12.88 ;8.19 \n"
                    + "2.09 ;0.29\n"
                    + "G3Ib\n"
                    + "3.30;~\n"
                    + "PLX 2318,* l Car,CD-61  2349,CPC 20.1  2583,CPD-61  1333,FK5 1254,GC 13462,GCRV  6282,GEN# +1.00084810,HD  84810,HIC  47854,HIP  47854,HR  3884,IRAS 09438-6216,JP11  1867,N30 2339,PPM 357533,SAO 250683,SKY# 18842,TD1 14281,V* l Car,uvby98 100084810 V,2MASS J09451481-6230284,PLX 2318.00,TYC 8946-3219-1,\n"
                    + ":entry:L car\n"
                    + "* L Car\n"
                    + "155.74227525;-66.90149731;10 22 58.14606;-66 54 05.3903;\n"
                    + "*,IR,UV,SB*,Ce*\n"
                    + "V=4.99,J=5.204,H=5.292,K=5.314,\n"
                    + "-22.39 ;11.48 \n"
                    + "8.12 ;0.18\n"
                    + "B8V\n"
                    + "12.00;~\n"
                    + "Renson 25960,CPC 21  2112,CPD-66  1243,FK5 2834,GC 14283,GCRV  6536,GEN# +1.00090264,GSC 08968-01393,HD  90264,HIC  50847,HIP  50847,HR  4089,N30 2467,PPM 357955,ROT  1565,SAO 250940,SKY# 19938,TD1 14823,TYC 8968-1393-1,UBV    9630,UBV M  16200,uvby98 100090264,* L Car,2MASS J10225812-6654053,\n"
                    + "";
            job.parseResult();
            _logger.info("star result:\n{}", job._result);
        }
        if (false) {
            job._response = "::error:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n"
                    + "\n"
                    + "[3] java.text.ParseException: Unrecogniezd identifier: aasioi\n"
                    + "[5] java.text.ParseException: Unrecogniezd identifier: bad\n"
                    + "[6] Identifier not found in the database : NAME TEST\n"
                    + "\n"
                    + "::data::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n"
                    + "\n"
                    + ":entry:l car\n"
                    + "* l Car\n"
                    + "146.31171343;-62.50790330;09 45 14.81122;-62 30 28.4519;\n"
                    + "*,V*,cC*,IR,UV\n"
                    + "V=3.40,J=1.684,H=1.188,K=1.048,\n"
                    + "-12.88 ;8.19 \n"
                    + "2.09 ;0.29\n"
                    + "G3Ib\n"
                    + "3.30;~\n"
                    + "PLX 2318,* l Car,CD-61  2349,CPC 20.1  2583,CPD-61  1333,FK5 1254,GC 13462,GCRV  6282,GEN# +1.00084810,HD  84810,HIC  47854,HIP  47854,HR  3884,IRAS 09438-6216,JP11  1867,N30 2339,PPM 357533,SAO 250683,SKY# 18842,TD1 14281,V* l Car,uvby98 100084810 V,2MASS J09451481-6230284,PLX 2318.00,TYC 8946-3219-1,\n"
                    + ":entry:L car\n"
                    + "* L Car\n"
                    + "155.74227525;-66.90149731;10 22 58.14606;-66 54 05.3903;\n"
                    + "*,IR,UV,SB*,Ce*\n"
                    + "V=4.99,J=5.204,H=5.292,K=5.314,\n"
                    + "-22.39 ;11.48 \n"
                    + "8.12 ;0.18\n"
                    + "B8V\n"
                    + "12.00;~\n"
                    + "Renson 25960,CPC 21  2112,CPD-66  1243,FK5 2834,GC 14283,GCRV  6536,GEN# +1.00090264,GSC 08968-01393,HD  90264,HIC  50847,HIP  50847,HR  4089,N30 2467,PPM 357955,ROT  1565,SAO 250940,SKY# 19938,TD1 14823,TYC 8968-1393-1,UBV    9630,UBV M  16200,uvby98 100090264,* L Car,2MASS J10225812-6654053,\n"
                    + "";
            job.parseResult();
            _logger.info("star result:\n{}", job._result);
        }
        if (false) {
            job._response = "::error:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n"
                    + "\n"
                    + "[3] java.text.ParseException: Unrecogniezd identifier: aasioi\n"
                    + "";
            job.parseResult();
            _logger.info("star result:\n{}", job._result);
        }
        if (false) {
            job._response = "::error:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n"
                    + "\n"
                    + "[3] ':entry:%OBJECT\n"
                    + "%MAIN_ID\n"
                    + "%COO(d;A);%COO(d;D);%COO(A);%COO(D);\n"
                    + "%OTYPELIST\n"
                    + "%FLUXLIST(V,I,J,H,K;N=F,)\n"
                    + "%PM(A;D)\n"
                    + "%PLX(V;E)\n"
                    + "%SP(S)\n"
                    + "%RV(V;W)\n"
                    + "%IDLIST[%*,]' incorrect field in format: The field OBJECT is not defined\n"
                    + "[3] ':entry:%OBJECT\n"
                    + "%MAIN_ID\n"
                    + "%COO(d;A);%COO(d;D);%COO(A);%COO(D);\n"
                    + "%OTYPELIST\n"
                    + "%FLUXLIST(V,I,J,H,K;N=F,)\n"
                    + "%PM(A;D)\n"
                    + "%PLX(V;E)\n"
                    + "%SP(S)\n"
                    + "%RV(V;W)\n"
                    + "%IDLIST[%*,]' incorrect field in format: The field OBJECT is not defined\n"
                    + "\n"
                    + "::data::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n"
                    + "\n"
                    + ":entry:vega\n"
                    + "* alf Lyr\n"
                    + "279.23473479;+38.78368896;18 36 56.33635;+38 47 01.2802;\n"
                    + "**,*,PM*,V*,dS*,smm,IR,UV,X\n"
                    + "V=0.03,I=0.10,J=-0.18,H=-0.03,K=0.13,\n"
                    + "200.94 ;286.23 \n"
                    + "130.23 ;0.36\n"
                    + "A0Va\n"
                    + "-20.60;~\n"
                    + "WISE J183656.49+384703.9,AKARI-IRC-V1 J1836564+384703,IRAS F18352+3844,JCMTSE J183656.4+384709,JCMTSF J183656.4+384709,EQ 183456.7+384615.4,PLX 4293,LSPM J1836+3847,ASCC  507896,2MASS J18365633+3847012,USNO-B1.0 1287-00305764,* alf Lyr,*   3 Lyr,8pc 128.93,ADS 11510 A,AG+38 1711,BD+38  3238,CCDM J18369+3847A,CEL   4636,CSI+38  3238  1,CSV 101745,FK5  699,GC 25466,GCRV 11085,GEN# +1.00172167,GJ   721,HD 172167,HGAM    706,HIC  91262,HIP  91262,HR  7001,IDS 18336+3841 A,IRAS 18352+3844,IRC +40322,JP11  2999,LTT 15486,N30 4138,NAME VEGA,NLTT 46746,NSV 11128,PMC 90-93   496,PPM  81558,RAFGL 2208,ROT  2633,SAO  67174,SKY# 34103,TD1 22883,UBV   15842,UBV M  23118,USNO 882,V* alf Lyr,Zkh 277,[HFE83] 1223,uvby98 100172167 V,PLX 4293.00,1E 183515+3844.3,EUVE J1836+38.7,WDS J18369+3846A,TYC 3105-2070-1,GJ   721.0,GAT 1285,\n"
                    + "";
            job.parseResult();
            _logger.info("star result:\n{}", job._result);
        }

        if (false) {
            job._response = ":entry:MWC297\n"
                    + "V* NZ Ser\n"
                    + "276.914696;-03.831125;18 27 39.527;-03 49 52.05;\n"
                    + "**,*,Em*,V*,Or*,IR,X\n"
                    + "V=12.31,J=6.127,H=4.39,K=3.04,\n"
                    + "~ ;~ \n"
                    + "~ ;~\n"
                    + "B0\n"
                    + "~;~\n"
                    + "UCAC2  30568322,EM* MWC  297,FMC 38,GEN# +6.10120297,GSC 05107-00494,IRAS 18250-0351,JP11  5212,PDS 518,RAFGL 2165,SS73 164,V* NZ Ser,[M82] 10,ALS 19652,WDS J00026+5942D,2MASS J18273952-0349520,[DMS2006] 12,[VOE2005] 1,\n"
                    + "\n"
                    + "\n"
                    + "";
            job.parseResult();
            _logger.info("star result:\n{}", job._result);
        }
    }
}
