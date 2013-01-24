/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.data.model.Package;
import fr.jmmc.jmcs.gui.component.ResizableTextViewFactory;
import fr.jmmc.jmcs.util.FileUtils;
import java.util.HashMap;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Show the list of jMCS dependencies in a dedicated window.
 * @author Sylvain LAFRASSE
 */
public class DependenciesView {

    /** jMCS license files resource path constant */
    private static final String JMCS_LICENSE_CONTENT_FILE_PATH = "fr/jmmc/jmcs/resource/license/";
    private static final String WINDOW_TITLE = "jMCS Dependencies";
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(DependenciesView.class.getName());
    /** Singleton instance */
    private static DependenciesView _instance = null;
    // Class members
    private static String _windowContent;

    private DependenciesView() {
        _windowContent = generateHtml();
    }

    /** Show the dependencies window */
    public static void display() {
        if (_instance == null) {
            _instance = new DependenciesView();
        }
        ResizableTextViewFactory.createHtmlWindow(_windowContent, WINDOW_TITLE, false);
    }

    private String generateHtml() {

        HashMap<String, String> licenseContent = new HashMap<String, String>();

        // Get jMCS data
        final ApplicationDescription data = ApplicationDescription.getJmcsInstance();
        final String jMcsName = data.getProgramName();
        final String jmmcLogoResourcePath = data.getCompanyLogoResourcePath();
        final String jmmcLogoURL = FileUtils.getResource(jmmcLogoResourcePath).toString();
        final String jmmcUrl = data.getMainWebPageURL();
        final String jmmcName = data.getShortCompanyName();
        final String jmmcLongName = data.getLegalCompanyName();
        final String jMcsUrl = data.getLinkValue();

        // Compose jMCS header
        final StringBuilder generatedHtml = new StringBuilder(65 * 1024);
        generatedHtml.append("<html><body>");
        generatedHtml.append("<center><b>").append(jMcsName).append(" Acknowledgments</b></center><br>");
        generatedHtml.append("<center><a href='").append(jmmcUrl).append("'><img src='").append(jmmcLogoURL).append("'></a></center><br><br>");
        generatedHtml.append("<i>").append(ApplicationDescription.getInstance().getProgramName()).append("</i>");
        generatedHtml.append(" make extensive use of the <a href = '").append(jMcsUrl).append("'>").append(jMcsName).append("</a> provided by the ").append(jmmcLongName).append(" (").append(jmmcName).append(").<br><br>");
        generatedHtml.append(jMcsName).append(" dependencies include:<br>");

        // Compose each package informations
        for (Package dependency : data.getPackages()) {
            final String name = dependency.getName();
            final String link = dependency.getLink();
            final String description = dependency.getDescription();
            final String jars = dependency.getJars();
            final String licenseName = dependency.getLicense().value();

            // Compute default license filename if none provided
            String file = dependency.getFile();
            if (file == null) {
                file = licenseName.replace(' ', '_');
                file += ".txt";
            }

            // Process each license only once (when referenced several times)
            String licenseLabel = null;
            if (!licenseContent.containsKey(file)) {
                licenseLabel = name + " license (" + licenseName + ")";
            } else {
                licenseLabel = licenseName + " license";
            }
            licenseContent.put(file, licenseLabel);

            // Add dependency link only if available
            if (link == null) {
                generatedHtml.append(name);
            } else {
                generatedHtml.append("<a href='").append(link).append("'>").append(name).append("</a>");
            }
            generatedHtml.append(":<br><i>").append(description).append("</i><br>");
            generatedHtml.append("(composed of ").append(jars).append(", distirbuted under <a href='#").append(file).append("'>").append(licenseName).append(" license</a>)<br><br>");
        }

        // Add every license at the bottom of the pane
        for (Entry<String, String> currentLicense : licenseContent.entrySet()) {

            final String licenseName = currentLicense.getValue();
            final String licenseFilename = currentLicense.getKey();

            // Compose license title (with anchor)
            generatedHtml.append("<br><a name='#").append(licenseFilename).append("'></a><hr><center><b>").append(licenseName).append("</b></center><hr>");

            // Try to load license content
            final String licenseResourcePath = JMCS_LICENSE_CONTENT_FILE_PATH + licenseFilename;
            try {
                final String licenseText = FileUtils.readFile(licenseResourcePath);
                generatedHtml.append("<pre>").append(licenseText).append("</pre>");
            } catch (IllegalStateException ise) {
                _logger.error("Could not load '{}' resource.", licenseResourcePath, ise);
                generatedHtml.append("<i>License file unavailable at the moment.</i><br>");
            }
        }
        generatedHtml.append("</body></html>");

        return generatedHtml.toString();
    }
}
