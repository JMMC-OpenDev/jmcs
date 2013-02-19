/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.action;

import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.data.model.Change;
import fr.jmmc.jmcs.data.model.Prerelease;
import fr.jmmc.jmcs.data.model.Release;
import fr.jmmc.jmcs.gui.component.ResizableTextViewFactory;
import fr.jmmc.jmcs.util.StringUtils;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action generates release notes for the given 
 * @author bourgesl
 */
public final class ShowHtmlReleaseNotesAction extends RegisteredAction {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Class name. This name is used to register to the ActionRegistrar */
    public final static String className = ShowHtmlReleaseNotesAction.class.getName();
    /** Action name. This name is used to register to the ActionRegistrar */
    public final static String actionName = "showReleaseNotes";
    /** Class logger */
    private final static Logger logger = LoggerFactory.getLogger(className);
    /* members */
    /** version */
    private String _windowTitle;
    /** html content (cached) */
    private String _windowContent;

    /**
     * Public constructor that automatically register the action in RegisteredAction.
     * 
     * @param actionName the name of the action.
     * @param titlePrefix title prefix to use in window title and html content
     * @param applicationDescription application description to use
     */
    public ShowHtmlReleaseNotesAction(final String actionName, final String titlePrefix, final ApplicationDescription applicationDescription) {
        super(className, actionName);

        generateHtml(titlePrefix, applicationDescription);
    }

    /**
     * Handle the action event
     * @param evt action event
     */
    @Override
    public void actionPerformed(final ActionEvent evt) {
        logger.debug("actionPerformed");

        ResizableTextViewFactory.createHtmlWindow(_windowContent, this._windowTitle, false);
    }

    /**
     * Generate Html content
     * @param titlePrefix title prefix to use in window title and html content
     * @param applicationDescription application description to use
     */
    private void generateHtml(final String titlePrefix, final ApplicationDescription applicationDescription) {

        this._windowTitle = titlePrefix + ' ' + applicationDescription.getProgramVersion() + " release notes";

        // Compose jMCS header
        final StringBuilder generatedHtml = new StringBuilder(8 * 1024);
        generatedHtml.append("<html><body>");

        generatedHtml.append("<h1>").append(this._windowTitle).append("</h1>\n");

        // extracted changes per type:
        final List<Change> changeList = new ArrayList<Change>(20);

        for (Release r : applicationDescription.getReleases()) {
            generatedHtml.append("<h2>").append("Version ").append(r.getVersion()).append("</h2>\n");
            generatedHtml.append("<p>");

            if (r.getPubDate() != null) {
                generatedHtml.append(r.getPubDate()).append('\n');
            }

            generatedHtml.append("<ul>\n");

            processChangeType("FEATURE", "Features", r.getPrereleases(), generatedHtml, changeList);
            processChangeType("CHANGE", "Changes", r.getPrereleases(), generatedHtml, changeList);
            processChangeType("BUGFIX", "Bug fixes", r.getPrereleases(), generatedHtml, changeList);
            processChangeType(null, "Other", r.getPrereleases(), generatedHtml, changeList);

            generatedHtml.append("</ul>\n");

            generatedHtml.append("</p>\n");
        }

        generatedHtml.append("</body></html>");

        this._windowContent = generatedHtml.toString();
    }

    /**
     * Generate HTML for the given type
     * @see #findChangeByType(java.lang.String, java.util.List, java.util.List) 
     * @param type type to match or null (matches empty type)
     * @param label label to display for the given type
     * @param prereleaseList list of prerelease 
     * @param generatedHtml html buffer to fill
     * @param changeList temporary list of Change to fill
     */
    private void processChangeType(final String type, final String label, final List<Prerelease> prereleaseList, final StringBuilder generatedHtml, final List<Change> changeList) {
        if (findChangeByType(type, prereleaseList, changeList)) {
            generatedHtml.append("<li>").append(label).append(":</li>\n");
            generatedHtml.append("<ul>\n");

            for (Change c : changeList) {
                generatedHtml.append("<li>").append(c.getValue()).append("</li>\n");
            }
            generatedHtml.append("</ul>\n");
        }
    }

    /**
     * Extract Change instances according to their type
     * @param type type to match or null (matches empty type)
     * @param prereleaseList list of prerelease 
     * @param changeList list of Change to fill
     * @return true if Change instances found for the given type
     */
    private boolean findChangeByType(final String type, final List<Prerelease> prereleaseList, final List<Change> changeList) {
        changeList.clear();

        final boolean noType = StringUtils.isEmpty(type);

        for (Prerelease p : prereleaseList) {
            for (Change c : p.getChanges()) {
                if (noType) {
                    if (StringUtils.isEmpty(c.getType())) {
                        changeList.add(c);
                    }
                } else {
                    if (type.equalsIgnoreCase(c.getType())) {
                        changeList.add(c);
                    }
                }
            }
        }
        return !changeList.isEmpty();
    }
}
