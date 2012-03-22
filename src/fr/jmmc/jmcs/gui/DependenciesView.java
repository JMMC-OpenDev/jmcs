/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.data.ApplicationDataModel;
import fr.jmmc.jmcs.data.model.Package;
import fr.jmmc.jmcs.network.BrowserLauncher;
import fr.jmmc.jmcs.util.FileUtils;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lafrasse
 */
public class DependenciesView extends javax.swing.JFrame implements HyperlinkListener {

    private static final String JMCS_LICENSE_CONTENT_FILE_PATH = "fr/jmmc/jmcs/resource/license/";
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(DependenciesView.class.getName());
    private JEditorPane _editorPane;
    private JScrollPane _scrollPane;
    private HashMap<String, String> _licenseContent;

    /** Creates new form DependenciesView */
    public DependenciesView() {
        _licenseContent = new HashMap<String, String>();
        initComponents();
        generateContent();
        setupKeyListeners();
        finsihLayout();
    }

    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("jMCS Dependencies");
        setAlwaysOnTop(true);

        _editorPane = new JEditorPane();
        _editorPane.setEditable(false);
        _editorPane.setMargin(new Insets(5, 5, 5, 5));
        _editorPane.setContentType("text/html");
        _editorPane.addHyperlinkListener(this);

        _scrollPane = new JScrollPane();
        _scrollPane.setViewportView(_editorPane);

        // Window layout
        Container contentPane = getContentPane();
        contentPane.add(_scrollPane, BorderLayout.CENTER);
    }

    private void generateContent() {

        // Get jMCS data
        final ApplicationDataModel data = App.getJMcsApplicationDataModel();
        final String jMcsName = data.getProgramName();
        final String jmmcLogoURL = getClass().getResource(data.getCompanyLogoResourcePath()).toString();
        final String jmmcUrl = data.getMainWebPageURL();
        final String jmmcName = data.getShortCompanyName();
        final String jmmcLongName = data.getLegalCompanyName();
        final String jMcsUrl = data.getLinkValue();

        // Compose jMCS header
        final StringBuilder generatedHtml = new StringBuilder(4096);
        generatedHtml.append("<html><head></head><body>");
        generatedHtml.append("<center><b>").append(jMcsName).append(" Acknowledgments</b></center><br/>");
        generatedHtml.append("<center><a href='").append(jmmcUrl).append("'><img src='").append(jmmcLogoURL).append("'/></a></center><br/><br/>");
        generatedHtml.append("<i>").append(App.getSharedApplicationDataModel().getProgramName()).append("</i>");
        generatedHtml.append(" make extensive use of the <a href = '").append(jMcsUrl).append("'>").append(jMcsName).append("</a> provided by the ").append(jmmcLongName).append(" (").append(jmmcName).append(").<br/><br/>");
        generatedHtml.append(jMcsName).append(" dependencies include:<br/>");

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
            if (!_licenseContent.containsKey(file)) {
                licenseLabel = name + " license (" + licenseName + ")";
            } else {
                licenseLabel = licenseName + " license";
            }
            _licenseContent.put(file, licenseLabel);

            // Add dependency link only if available
            if (link == null) {
                generatedHtml.append(name);
            } else {
                generatedHtml.append("<a href='").append(link).append("'>").append(name).append("</a>");
            }
            generatedHtml.append(":<br/><i>").append(description).append("</i><br/>");
            generatedHtml.append("(composed of ").append(jars).append(", distirbuted under <a href='#").append(file).append("'>").append(licenseName).append(" license</a>)<br/><br/>");
        }

        // Add every license at the bottom of the pane
        for (Entry<String, String> currentLicense : _licenseContent.entrySet()) {

            final String licenseName = currentLicense.getValue();
            final String licenseFilename = currentLicense.getKey();

            // Compose license title (with anchor)
            generatedHtml.append("<br/><a name='#").append(licenseFilename).append("'/><hr/><center><b>").append(licenseName).append("</b></center><hr/>");

            // Try to load license content
            final String licenseResourcePath = JMCS_LICENSE_CONTENT_FILE_PATH + licenseFilename;
            try {
                final String licenseContent = FileUtils.readFile(licenseResourcePath);
                generatedHtml.append("<pre>").append(licenseContent).append("</pre>");
            } catch (IllegalStateException ise) {
                _logger.error("Could not load '{}' resource.", licenseResourcePath, ise);
                generatedHtml.append("<i>License file unavailable at the moment.</i><br/>");
            }
        }
        generatedHtml.append("</body></html>");

        _editorPane.setText(generatedHtml.toString());
    }

    private void setupKeyListeners() {
        // Trap Escape key
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        // Trap command-W key
        KeyStroke metaWStroke = KeyStroke.getKeyStroke(MainMenuBar.getSystemCommandKey() + "W");

        // Close window on either strike
        ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        getRootPane().registerKeyboardAction(actionListener, escapeStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(actionListener, metaWStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void finsihLayout() {
        _editorPane.setCaretPosition(0); // Move back focus at the top of the content
        setPreferredSize(new Dimension(700, 600));
        pack();
        WindowCenterer.centerOnMainScreen(this);
        setVisible(true);
    }

    /**
     * Handle URL link clicked in the JEditorPane.
     *
     * @param event the received event.
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        // When a link is clicked
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

            // Get the clicked URL
            final URL url = event.getURL();

            // If it is valid
            if (url != null) {
                // Get it in the good format
                final String clickedURL = url.toExternalForm();
                // Open the url in web browser
                BrowserLauncher.openURL(clickedURL);
            } else { // Assume it was an anchor
                String anchor = event.getDescription();
                _editorPane.scrollToReference(anchor);
            }
        }
    }
}
