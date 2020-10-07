/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import fr.jmmc.jmcs.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gathers the results of one StarResolver query: 
 * - queried names
 * - status (+ optional error message)
 * - results as Map&lt;identifier, List&lt;Star&gt;&gt;
 * @author bourgesl
 */
public final class StarResolverResult {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(StarResolverResult.class.getName());

    /* members */
    /** list of queried identifiers */
    private final List<String> _names;
    /** status */
    private StarResolverStatus _status = StarResolverStatus.IN_PROGRESS;
    /** (optional) server error message */
    private String _serverErrorMessage = null;
    /** (optional) error message */
    private String _errorMessage = null;
    /** map of Star */
    private Map<String, List<Star>> _starMap = null;
    /** flag indicating that a queried identifier got multiple matches */
    private boolean _multipleMatches = false;
    /** list of queried identifier(s) having multiple matches */
    private List<String> _multNames = null;

    /**
     * Protected Constructor
     * @param names list of queried identifiers
     */
    StarResolverResult(final List<String> names) {
        _names = Collections.unmodifiableList(names);
    }

    /**
     * @return list of queried identifiers (read only)
     */
    public List<String> getNames() {
        return _names;
    }

    /**
     * @return true if any error occured
     */
    public boolean isErrorStatus() {
        return (_status != StarResolverStatus.IN_PROGRESS) && (_status != StarResolverStatus.OK);
    }

    /**
     * @return status
     */
    public StarResolverStatus getStatus() {
        return _status;
    }

    /**
     * Define the status (but do not override any error status)
     * @param status status
     */
    void setStatus(final StarResolverStatus status) {
        if (!isErrorStatus()) {
            this._status = status;
        }
    }

    /**
     * @return (optional) server error message
     */
    public String getServerErrorMessage() {
        return _serverErrorMessage;
    }

    /**
     * Set the status to ERROR_SERVER and the server error message
     * @param serverErrorMessage server error message
     */
    public void setServerErrorMessage(String serverErrorMessage) {
        setStatus(StarResolverStatus.ERROR_SERVER);
        this._serverErrorMessage = serverErrorMessage;
    }

    /**
     * @return (optional) error message
     */
    public String getErrorMessage() {
        return _errorMessage;
    }

    /**
     * Set the status to the given status (error expected) and the error message
     * @param status any status (error expected)
     * @param errorMessage error message
     */
    void setErrorMessage(final StarResolverStatus status, final String errorMessage) {
        setStatus(status);
        this._errorMessage = errorMessage;
    }

    /**
     * @return true if no result
     */
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(_starMap);
    }

    /**
     * @return the single star corresponding to the single queried name (single match); null otherwise
     */
    public Star getSingleStar() {
        if (_names != null && _names.size() == 1) {
            return getSingleStar(_names.get(0));
        }
        return null;
    }

    /**
     * @param name queried identifier present in getNames()
     * @return the single star corresponding to the given name (single match); null otherwise
     */
    public Star getSingleStar(final String name) {
        final List<Star> starList = getStars(name);
        // Check for a single match: bad identifier = 0 match; multiple matches > 1
        if ((starList != null) && starList.size() == 1) {
            return starList.get(0);
        }
        return null;
    }

    /**
     * @param name queried identifier present in getNames()
     * @return the list of stars corresponding to the given name (multiple matches)
     */
    public List<Star> getStars(final String name) {
        if (_starMap == null) {
            return Collections.emptyList();
        }
        return _starMap.get(name);
    }

    /**
     * Add a Star instance corresponding to the queried name 
     * @param name queried identifier present in getNames()
     * @param star Star instance
     */
    void addStar(final String name, final Star star) {
        if (_starMap == null) {
            _starMap = new HashMap<String, List<Star>>(_names.size());
        }
        List<Star> starList = _starMap.get(name);
        if (starList == null) {
            starList = new ArrayList<Star>(2);
            _starMap.put(name, starList);
        } else {
            _multipleMatches = true;
        }
        _logger.debug("adding star for name='{}':\n{}", name, star);
        starList.add(star);
    }

    /**
     * @return flag indicating that queried identifier(s) have multiple matches
     */
    public boolean isMultipleMatches() {
        return _multipleMatches;
    }

    /**
     * @return list of queried identifier(s) having multiple matches
     */
    public List<String> getNamesForMultipleMatches() {
        List<String> multNames = _multNames;
        if (multNames != null) {
            return multNames;
        }
        if (!_multipleMatches) {
            multNames = Collections.emptyList();
        } else {
            multNames = new ArrayList<String>(_names.size());
            for (String name : _names) {
                List<Star> starList = _starMap.get(name);

                if (starList != null && starList.size() > 1) {
                    multNames.add(name);
                }
            }
        }
        _multNames = multNames;
        return multNames;
    }

    @Override
    public String toString() {
        return "status: " + _status
                + ((_serverErrorMessage != null) ? (", server error: \"" + _serverErrorMessage + "\"") : "")
                + ((_errorMessage != null) ? (", error: \"" + _errorMessage + "\"") : "")
                + ", ids: " + CollectionUtils.toString(_names)
                + ", star map: " + CollectionUtils.toString(_starMap)
                + ", isMultipleMatches: " + _multipleMatches;
    }
}
