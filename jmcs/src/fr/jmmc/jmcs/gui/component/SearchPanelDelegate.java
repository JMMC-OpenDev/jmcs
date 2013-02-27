/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.gui.component.SearchPanel.SEARCH_DIRECTION;
import java.util.regex.Pattern;

/**
 * SearchPanel delegate interface supporting previous/next.
 * Implement this to use jMCS SearchPanel.
 * @author Sylvain LAFRASSE.
 */
public interface SearchPanelDelegate {

    /**
     * Tries to find the given regexp pattern in the given direction.
     *
     * @param pattern the regexp pattern to match.
     * @param givenDirection either NEXT or PREVIOUS search direction.
     *
     * @return true if something found, false otherwise.
     */
    boolean search(final Pattern pattern, final SEARCH_DIRECTION givenDirection);
}
