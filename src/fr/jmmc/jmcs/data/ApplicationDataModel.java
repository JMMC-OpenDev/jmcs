/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data;

import fr.jmmc.jmcs.data.model.ApplicationData;
import fr.jmmc.jmcs.data.model.Company;
import fr.jmmc.jmcs.data.model.Compilation;
import fr.jmmc.jmcs.data.model.Dependences;
import fr.jmmc.jmcs.data.model.Menubar;
import fr.jmmc.jmcs.data.model.Program;
import fr.jmmc.jmcs.jaxb.JAXBFactory;
import fr.jmmc.jmcs.jaxb.XmlBindException;
import java.io.BufferedInputStream;
import java.io.IOException;


import java.net.URL;
import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * This class is the link between the application
 * XML file which stocked the application informations like
 * it's name, version, compiler etc... called <b>ApplicationData.xml</b>,
 * which is saved into the application module, and the others classes
 * which use it to access to the informations like <b>AboutBox</b>,
 * <b>SplashScreen</b> etc...
 *
 * This class uses <b>Castor</b> classes to access to these informations
 * and provides the good getters for each field of the XML file.
 * 
 * @author Guillaume MELLA, Brice COLUCCI, Sylvain LAFRASSE, Laurent BOURGES.
 */
public class ApplicationDataModel {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(ApplicationDataModel.class.getName());
    /** package name for JAXB generated code */
    private final static String APP_DATA_MODEL_JAXB_PATH = "fr.jmmc.jmcs.data.model";

    /* members */
    /** internal JAXB Factory */
    private final JAXBFactory jf;
    /** The JAVA class which JAXB has generated with the XSD file */
    private ApplicationData _applicationDataModel = null;
    /** The JAVA class which JAXB has generated with the XSD file */
    private Company _company = null;
    /** Logo file name */
    private String _companyLogoFileName = null;
    /** Main web page URL */
    private String _mainWebPageURL = null;
    /** URL of the PHP script that handles Feedback reports */
    private String _phpScriptURL = null;
    /** feedback report window header message in HTML format */
    private String _feedbackReportHeaderMessage = null;
    /** authors list */
    private String _authors = null;
    /** Used throughout all jMCS GUI */
    private String _shortCompanyName = null;
    /** Used by SAMP */
    private String _legalCompanyName = null;
    /** User Support URL */
    private String _userSupportUrl = null;
    /** RSS URL */
    private String _hotNewsRSSFeedLink = null;
    /** FAQ URL */
    private String _faqLink = null;
    /** Release Notes URL */
    private String _releaseNotesLink = null;

    /** 
     * Public constructor
     * @param dataModelURL location of the file to load
     * @throws IllegalStateException if the given URL can not be loaded
     */
    public ApplicationDataModel(final URL dataModelURL) throws IllegalStateException {
        _logger.debug("Loading Application data model from {}", dataModelURL);

        // Start JAXB
        jf = JAXBFactory.getInstance(APP_DATA_MODEL_JAXB_PATH);

        _logger.debug("JAXBFactory: {}", jf);

        // Load application data
        _applicationDataModel = loadData(dataModelURL);

        final String programName = getProgramName();

        _feedbackReportHeaderMessage = "<html><body>"
                + "<center>"
                + "<big>Welcome to '" + programName + "' Feedback Report</big><br/>"
                + "We are eager to get your feedback, questions or comments !<br/>"
                + "So please do not hesitate to use this form.<br/>"
                + "</center>"
                + "<br/><br/>"
                + "Moreover, we encourage you to provide us with your e-mail address, so we can :"
                + "<ul>"
                + "<li>keep you up to date on the status of your request;</li>"
                + "<li>ask you more information if needed.</li>"
                + "</ul>"
                + "<em>(*) Summary and description must be filled to enable the 'Submit' button.</em>"
                + "</body></html>";


        // Use company meta data (if any)
        if (_applicationDataModel.isSetCompany()) {
            _company = _applicationDataModel.getCompany();

            // Mandatory data
            _shortCompanyName = _company.getShortName();
            _legalCompanyName = _shortCompanyName;
            _companyLogoFileName = _company.getLogoResource();
            _mainWebPageURL = _company.getHomepageUrl();

            // Optionnal data
            if (_company.isSetLegalName()) {
                _legalCompanyName = _company.getLegalName();
            }
            if (_applicationDataModel.isSetAuthors()) {
                _authors = _applicationDataModel.getAuthors();
            }
            if (_company.isSetFeedbackFormUrl()) {
                _phpScriptURL = _company.getFeedbackFormUrl();
            }
            if (_company.isSetUserSupportUrl()) {
                _userSupportUrl = _company.getUserSupportUrl();
            }
            if (_applicationDataModel.isSetFaqlink()) {
                _faqLink = _applicationDataModel.getFaqlink();
            }
            if (_applicationDataModel.isSetRsslink()) {
                _hotNewsRSSFeedLink = _applicationDataModel.getRsslink();
            }
            if (_applicationDataModel.isSetReleasenotes()) {
                _releaseNotesLink = _applicationDataModel.getReleaselink();
            }
        } else { // If no 'company' data, assume we are in the JMMC context
            _companyLogoFileName = "/fr/jmmc/jmcs/resource/logo.png";
            _mainWebPageURL = "http://www.jmmc.fr/";
            _phpScriptURL = "http://jmmc.fr/feedback/feedback.php";
            _userSupportUrl = "http://www.jmmc.fr/support.htm";
            _authors = "The JMMC Team";
            _shortCompanyName = "JMMC";
            _legalCompanyName = "Jean-Marie Mariotti Center";
            _hotNewsRSSFeedLink = getLinkValue() + "/" + programName.toLowerCase() + ".rss";
            _faqLink = getLinkValue() + "/faq/";
            _releaseNotesLink = getLinkValue() + "/releasenotes.htm";
        }

        _logger.debug("Application data model loaded.");
    }

    /** Invoke JAXB to load ApplicationData.xml file */
    private ApplicationData loadData(final URL dataModelURL) throws XmlBindException, IllegalArgumentException, IllegalStateException {

        // Note : use input stream to avoid JNLP offline bug with URL (Unknown host exception)
        try {
            final Unmarshaller u = jf.createUnMarshaller();
            return (ApplicationData) u.unmarshal(new BufferedInputStream(dataModelURL.openStream()));
        } catch (IOException ioe) {
            throw new IllegalStateException("Load failure on " + dataModelURL, ioe);
        } catch (JAXBException je) {
            throw new IllegalArgumentException("Load failure on " + dataModelURL, je);
        }
    }

    /**
     * @return the value of the "Acknowledgment" field from the XML file  if any, null otherwise.
     */
    public String getAcknowledgment() {
        if (_applicationDataModel.getAcknowledgment() == null) {
            _logger.debug("_applicationDataCastorModel.getAcknowledgment() is null");

            return null;
        }

        return _applicationDataModel.getAcknowledgment();
    }

    /**
     * @return the company logo resource path
     */
    public String getCompanyLogoResourcePath() {
        _logger.debug("logoUrl: {}", _companyLogoFileName);

        return _companyLogoFileName;
    }

    /**
     * @return the application main web page URL
     */
    public String getMainWebPageURL() {
        return _mainWebPageURL;
    }

    /**
     * @return the feedback report form URL if any, null otherwise.
     */
    public String getFeedbackReportFormURL() {
        return _phpScriptURL;
    }

    /**
     * @return the feedback report window header message
     */
    public String getFeedbackReportHeaderMessage() {
        return _feedbackReportHeaderMessage;
    }

    /**
     * @return the value of the "program" element name from the XML file
     */
    public String getProgramName() {
        Program program = null;
        String programName = "Unknown";

        // Get program
        program = _applicationDataModel.getProgram();

        if (program != null) {
            programName = program.getName();
        }

        _logger.debug("Program name: {}", programName);

        return programName;
    }

    /**
     * @return the value of the "program version" element from the XML file
     */
    public String getProgramVersion() {
        Program program = null;
        String programVersion = "?.?";

        // Get program
        program = _applicationDataModel.getProgram();

        if (program != null) {
            programVersion = program.getVersion();
        }

        _logger.debug("Program version: {}", programVersion);
        return programVersion;
    }

    /**
    
     * @return the application main web page URL from the "link" field in the XML file
     */
    public String getLinkValue() {
        String mainWebPageURL = _mainWebPageURL;

        mainWebPageURL = _applicationDataModel.getLink();
        _logger.debug("MainWebPageURL: {}", mainWebPageURL);

        return mainWebPageURL;
    }

    /**
    
     * @return the application release notes URL if any, null otherwise.
     */
    public String getReleaseNotesLinkValue() {
        _logger.debug("ReleaseNotesLink: {}", _releaseNotesLink);

        return _releaseNotesLink;
    }

    /**
     * @return the application FAQ URL if any, null otherwise.
     */
    public String getFaqLinkValue() {
        _logger.debug("FaqLink value is: {}", _faqLink);

        return _faqLink;
    }

    /**
     * @return the application Hot News RSS feed URL if any, null otherwise.
     */
    public String getHotNewsRSSFeedLinkValue() {
        _logger.debug("HotNewsRSSFeedLink: {}", _hotNewsRSSFeedLink);

        return _hotNewsRSSFeedLink;
    }

    /**
     * @return the value of the element compilation date from the XML file
     */
    public String getCompilationDate() {
        Compilation compilation = null;
        String compilationDate = "Unknown";

        // Get compilation
        compilation = _applicationDataModel.getCompilation();

        if (compilation != null) {
            compilationDate = compilation.getDate();
        }

        _logger.debug("Compilation date: {}", compilationDate);
        return compilationDate;
    }

    /**
     * @return the value of the element compiler version from the XML file
     */
    public String getCompilatorVersion() {
        Compilation compilation = null;
        String compilationCompilator = "Unknown";

        // Get compilation
        compilation = _applicationDataModel.getCompilation();

        if (compilation != null) {
            compilationCompilator = compilation.getCompiler();
        }

        _logger.debug("Compilation compilator: {}", compilationCompilator);

        return compilationCompilator;
    }

    /**
     * @return the application description used in the AboutBox
     */
    public String getTextValue() {
        String text = _applicationDataModel.getText();
        _logger.debug("Text value: {}", text);

        return text;
    }

    /**
     * @return the value of the "authors" field from the XML file if any, null otherwise.
     */
    public String getAuthors() {
        return _authors;
    }

    /**
     * Return the informations about "packages" taken from the XML file
     * @return vector template [name, link, description], [name, link, description]...
     */
    public List<String> getPackagesInfo() {

        // TODO: API: use objects not Vector<string>
        Dependences dependences = _applicationDataModel.getDependences();

        final List<String> packagesInfo = new ArrayList<String>();

        // For each package
        for (fr.jmmc.jmcs.data.model.Package p : dependences.getPackages()) {
            packagesInfo.add(p.getName());
            packagesInfo.add(p.getLink());
            packagesInfo.add(p.getDescription());
        }

        _logger.debug("Packages informations: ", packagesInfo);

        return packagesInfo;
    }

    /**
     * @return Forge the "copyright" text used in the AboutBox
     */
    public String getCopyrightValue() {
        int year = 0;
        String compilationDate = getCompilationDate();

        try {
            // Try to get the year from the compilation date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formatter.parse(compilationDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            year = cal.get(Calendar.YEAR);
        } catch (ParseException pe) {
            _logger.warn("Cannot parse date '{}' will use current year instead.", compilationDate, pe);

            // Otherwise use the current year
            Calendar cal = new GregorianCalendar();
            year = cal.get(Calendar.YEAR);
        }

        // \u00A9 means (c)
        return "Copyright \u00A9 " + year + ", " + _shortCompanyName + ".";
    }

    /**
     * @return menu bar from XML description
     */
    public Menubar getMenubar() {
        return _applicationDataModel.getMenubar();
    }

    /**
     * @return company short name
     */
    public String getShortCompanyName() {
        return _shortCompanyName;
    }

    /**
     * @return company legal name if any, short name otherwise.
     */
    public String getLegalCompanyName() {
        return _legalCompanyName;
    }

    /**
     * @return User Support URL if any, null otherwise.
     */
    public String getUserSupportURL() {
        return _userSupportUrl;
    }

    /**
     * @return SAMP description if any, null otherwise.
     */
    public String getSampDescription() {
        return _applicationDataModel.getSampdescription();
    }

    /**
     * @return Application documentation URL if any, null otherwise.
     */
    public String getDocumetationUrl() {
        return _applicationDataModel.getDocumentationlink();
    }

    public String getJnlpUrl() {
        return _applicationDataModel.getJnlp();
    }
}
/*___oOo___*/
