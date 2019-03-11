/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.star;

import ch.qos.logback.classic.Level;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.logging.LoggingService;
import fr.jmmc.jmcs.util.CollectionUtils;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.jmcs.util.concurrent.ThreadExecutors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store informations relative to a star.
 *
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public final class StarResolver {

    /** Logger - register on the current class to collect local logs */
    private static final Logger _logger = LoggerFactory.getLogger(StarResolver.class.getName());
    /** The collection of CDS mirrors (initialized into getSimbadMirrors()) */
    private static final Map<String, String> _simbadMirrors;
    /** SIMBAD selected mirror (selected using setSimbadMirror()) */
    private static String _simbadMirror = null;
    /** semicolon separator (multiple identifier separator) */
    public static final String SEPARATOR_SEMI_COLON = ";";
    /** RegExp expression to match underscore character */
    private final static Pattern PATTERN_UNDERSCORE = Pattern.compile("_");
    /** RegExp expression to match white spaces arround semicolon separator */
    private final static Pattern PATTERN_WHITE_SPACE_ARROUND_SEMI_COLON = Pattern.compile("\\s*;\\s*");

    static {
        _simbadMirrors = new LinkedHashMap<String, String>(4);
        _simbadMirrors.put("SIMBAD Strasbourg, FR", "http://simbad.u-strasbg.fr/simbad/sim-script");
        _simbadMirrors.put("SIMBAD Harvard, US", "http://simbad.harvard.edu/simbad/sim-script");
        _simbadMirrors.put("SIMBAD Strasbourg, FR [IP]", "http://130.79.128.4/simbad/sim-script");
    }

    /**
     * Get the list of available mirrors.
     * @return one set of available mirror names.
     */
    public static Set<String> getSimbadMirrors() {
        return _simbadMirrors.keySet();
    }

    /**
     * Return the current SIMBAD mirror
     * @return SIMBAD mirror name
     */
    public static String getSimbadMirror() {
        if (_simbadMirror == null) {
            setSimbadMirror(getSimbadMirrors().iterator().next());
        }
        return _simbadMirror;
    }

    /**
     * Return the SIMBAD URL from the current mirror or the first one
     * @return SIMBAD URL
     */
    public static String getSimbadUrl() {
        if (_simbadMirror == null) {
            setSimbadMirror(getSimbadMirrors().iterator().next());
        }

        return _simbadMirrors.get(_simbadMirror);
    }

    /**
     * Choose one mirror giving its name chosen from available ones.
     * @param mirrorName value chosen from getSimbadMirrors().
     */
    public static void setSimbadMirror(final String mirrorName) {
        // prevent bad cases for bad mirror names
        if (_simbadMirrors.get(mirrorName) == null) {
            _simbadMirror = getSimbadMirrors().iterator().next();
        } else {
            _simbadMirror = mirrorName;
        }
    }

    /**
     * Return the next SIMBAD Mirror which URL is not in the failed URL Set
     * @param failedUrl failed URL(s)
     * @return next SIMBAD Mirror or null if none is still available
     */
    static String getNextSimbadMirror(final Set<String> failedUrl) {
        for (Map.Entry<String, String> e : _simbadMirrors.entrySet()) {
            if (!failedUrl.contains(e.getValue())) {
                // change mirror:
                setSimbadMirror(e.getKey());
                return _simbadMirror;
            }
        }
        return null;
    }

    /**
     * Wait for the given future to be ready (
     * @param future Future instance to use for synchronous mode (wait for)
     * @return StarResolverResult; null if the future was cancelled or not executed
     */
    public static StarResolverResult waitFor(final Future<StarResolverResult> future) {
        try {
            // Wait for StarResolver task to be done (and listener called) :
            return future.get();
        } catch (InterruptedException ie) {
            _logger.debug("waitFor: interrupted", ie);
        } catch (ExecutionException ee) {
            _logger.info("waitFor: execution error", ee);
        }
        return null;
    }

    /* members */
    /** listener */
    private final StarResolverProgressListener _listener;
    /** Dedicated thread executor (single thread) */
    private final ThreadExecutors _executor = ThreadExecutors.getSingleExecutor("StarResolverThreadPool");

    /**
     * Constructor without listener (synchronous mode)
     */
    public StarResolver() {
        this(null);
    }

    /**
     * Constructor with listener (asynchronous mode)
     *
     * @param listener callback listener with results
     */
    public StarResolver(final StarResolverProgressListener listener) {
        _listener = listener;
    }

    /**
     * Asynchronously query CDS SIMBAD to retrieve a given star information according to its name.
     * @param name the name of the star to resolve.
     * @return Future instance to use for synchronous mode (wait for)
     * @throws IllegalArgumentException if the given name is empty
     */
    public Future<StarResolverResult> resolve(final String name) throws IllegalArgumentException {
        _logger.debug("Searching data for star '{}'.", name);

        if (isMultiple(name)) {
            throw new IllegalArgumentException("Multiple names: use multipleResolve() directly.");
        }

        final String cleanedName = cleanNames(name);

        if (StringUtils.isEmpty(cleanedName)) {
            throw new IllegalArgumentException("Empty star name !");
        }

        return multipleResolve(Arrays.asList(cleanedName));
    }

    /**
     * Asynchronously query CDS SIMBAD to retrieve multiple stars information according to their names.
     * @param names the names of the star to resolve, separated by semi-colons.
     * @return Future instance to use for synchronous mode (wait for)
     * @throws IllegalArgumentException if the given names are empty
     */
    public Future<StarResolverResult> multipleResolve(final String names) throws IllegalArgumentException {
        return multipleResolve(prepareNames(names));
    }

    /**
     * Asynchronously query CDS SIMBAD to retrieve multiple stars information according to their names.
     * @param nameList the names of the star to resolve (clean ie no semicolon separator) nor empty strings
     * @return Future instance to use for synchronous mode (wait for)
     * @throws IllegalArgumentException if the given names are empty
     */
    public Future<StarResolverResult> multipleResolve(final List<String> nameList) throws IllegalArgumentException {
        _logger.debug("Searching data for stars '{}'.", nameList);

        if (CollectionUtils.isEmpty(nameList)) {
            throw new IllegalArgumentException("Empty star names !");
        }

        // Launch the query in the background in order to keep GUI updated
        return submitJob(new SimbadResolveStarJob(nameList, _listener));
    }

    private Future<StarResolverResult> submitJob(final SimbadResolveStarJob resolveStarJob) {
        // Intercept cancel calls to first abort HTTP method:
        final FutureTask<StarResolverResult> task = new FutureTask<StarResolverResult>(resolveStarJob) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                resolveStarJob.cancel();
                return super.cancel(mayInterruptIfRunning);
            }
        };

        // Launch query and return its Future wrapper
        _executor.execute(task);
        return task;
    }

    /**
     * 
     * @param names
     * @return 
     * @throws IllegalArgumentException if the given names are empty
     */
    public static List<String> prepareNames(final String names) {
        _logger.debug("prepareNames '{}'.", names);

        final String cleanedNames = cleanNames(names);

        if (StringUtils.isEmpty(cleanedNames)) {
            throw new IllegalArgumentException("Empty star names !");
        }

        // Split names properly:
        final String[] cleanedNameArray = cleanedNames.split(SEPARATOR_SEMI_COLON);
        final List<String> nameList = new ArrayList<String>(cleanedNameArray.length);

        for (String cleanedName : cleanedNameArray) {
            // Skip empty names
            if (!cleanedName.isEmpty()) {
                nameList.add(cleanedName);
            }
        }
        return nameList;
    }

    /**
     * Return true if the given value contains the semicolon separator (multiple identifier separator)
     @param value
     @return 
     */
    public static boolean isMultiple(final String value) {
        return (value != null) && value.contains(SEPARATOR_SEMI_COLON);
    }

    /**
     * Trim and remove redundant white space characters arround the semicolon separator
     * @param value input value
     * @return string value
     */
    public static String cleanNames(final String value) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.STRING_EMPTY;
        }
        // replace underscore character by space character:
        final String cleanedUnderscore = PATTERN_UNDERSCORE.matcher(value).replaceAll(StringUtils.STRING_SPACE);
        // replace useless white spaces arround the semicolon separator:
        final String cleanedSemiColon = PATTERN_WHITE_SPACE_ARROUND_SEMI_COLON.matcher(cleanedUnderscore).replaceAll(SEPARATOR_SEMI_COLON);
        return StringUtils.cleanWhiteSpaces(cleanedSemiColon);
    }

    /**
     * Command-line tool that tries to resolve the star name given as first parameter.
     * @param args first argument is the star name
     */
    public static void main(String[] args) {

        // invoke Bootstrapper method to initialize logback now:
        Bootstrapper.getState();

        if (false) {
            LoggingService.setLoggerLevel("fr.jmmc.jmal.star", Level.ALL);
        }

        final String names;

        if (args != null && args.length != 0) {
            names = args[0];
        } else {
// single:            
//            names = "  eps aur";
//            names = "car";
//            names = "aasioi";
// multiple:            
//            names = "    l car  ; L car  ;";
//            names = "aasioi;vega;bad; AK_SCO  ";
            names = "GJ876";
        }

        final StarResolverProgressListener asyncListener = new StarResolverProgressListener() {

            @Override
            public void handleProgressMessage(final String message) {
                _logger.info(message);
            }

            @Override
            public void handleResult(final StarResolverResult result) {
                _logger.info("ASYNC star resolver result:\n{}", result);
            }
        };

        // Seek data about the given star name (first arg on command line)
        // Wait for StarResolver task done (and listener calls) :
        final StarResolverResult result = waitFor(new StarResolver(asyncListener).multipleResolve(names));

        _logger.info("SYNC star resolver result:\n{}", result);

        _logger.info("Exit.");
        System.exit(0);
    }

}
/*___oOo___*/
