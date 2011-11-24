/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.data;

import fr.jmmc.jmcs.data.model.ApplicationData;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final Logger _logger = Logger.getLogger(ApplicationDataModel.class.getName());
    /** package name for JAXB generated code */
    private final static String APP_DATA_MODEL_JAXB_PATH = "fr.jmmc.jmcs.data.model";
    /** default namespace for ApplicationDataModel.xsd */
    private final static String APP_DATA_MODEL_NAMESPACE = "http://www.jmmc.fr/jmcs/app/1.0";

    /* members */
    /** internal JAXB Factory */
    private final JAXBFactory jf;
    /** The JAVA class which castor has generated with the XSD file */
    private ApplicationData _applicationDataModel = null;
    /** Logo file name */
    private final String _logoFileName = "/fr/jmmc/jmcs/resource/logo.png";
    /** Main web page URL */
    private final String _mainWebPageURL = "http://www.jmmc.fr/";
    /** URL of the PHP script that handles Feedback reports */
    private static final String _phpScriptURL = "http://jmmc.fr/feedback/feedback.php";
    /** header message in HTML format */
    private static final String _feedbackReportHeaderMessage = "<html><body>"
            + "<center>"
            + "<big>Welcome to the JMMC Feedback Report</big><br/>"
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

    /** 
     * Public constructor
     * @param dataModelURL location of the file to load
     * @throws IllegalStateException if the given URL can not be loaded
     */
    public ApplicationDataModel(final URL dataModelURL) throws IllegalStateException {
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("Loading Application data model from " + dataModelURL);
        }

        // Start JAXB
        jf = JAXBFactory.getInstance(APP_DATA_MODEL_JAXB_PATH);
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("JAXBFactory: " + jf);
        }

        _applicationDataModel = loadData(dataModelURL);
        _logger.fine("Application data model loaded.");
    }

    private ApplicationData loadData(final URL dataModelURL) throws XmlBindException, IllegalArgumentException, IllegalStateException {

        // Note : use input stream to avoid JNLP offline bug with URL (Unknown host exception)
        try {
            final Unmarshaller u = jf.createUnMarshaller();
            /*
            // Create the XMLReader
            final XMLReader reader = XMLReaderFactory.createXMLReader();
            
            // The filter class to set the correct namespace
            final XMLFilterImpl xmlFilter = new XmlNamespaceFilter(APP_DATA_MODEL_NAMESPACE, true);
            xmlFilter.setParent(reader);
            
            final SAXSource source = new SAXSource(xmlFilter, new InputSource(new BufferedInputStream(dataModelURL.openStream())));
            
            fr.jmmc.jmcs.data.model.ApplicationData appData = (fr.jmmc.jmcs.data.model.ApplicationData)u.unmarshal(source);
             */
            return (ApplicationData) u.unmarshal(new BufferedInputStream(dataModelURL.openStream()));
            /*
            } catch (SAXException se) {
            throw new IllegalStateException("Load failure on " + dataModelURL, se);
             */        
        } catch (IOException ioe) {
            throw new IllegalStateException("Load failure on " + dataModelURL, ioe);
        } catch (JAXBException je) {
            throw new IllegalArgumentException("Load failure on " + dataModelURL, je);
        }
    }

    /**
     * Return the value of the field "copyright" from the XML file
     *
     * @return the value of the field copyright from the XML file or null
     */
    public String getAcknowledgment() {
        if (_applicationDataModel.getAcknowledgment() == null) {
            _logger.fine("_applicationDataCastorModel.getAcknowledgment() is null");

            return null;
        }

        return _applicationDataModel.getAcknowledgment();
    }

    /**
     * Return the file name of JMMC logo
     *
     * @return the file name of JMMC logo
     */
    public String getLogoURL() {
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("logoUrl=" + _logoFileName);
        }

        return _logoFileName;
    }

    /**
     * Return the main web page URL
     *
     * @return the main web page URL
     */
    public String getMainWebPageURL() {
        return _mainWebPageURL;
    }

    /**
     * Return the feedback report form URL
     *
     * @return the feedback report form URL
     */
    public String getFeedabackReportFormURL() {
        return _phpScriptURL;
    }

    /**
     * Return the feedback report header message
     *
     * @return the feedback report header message
     */
    public String getFeedabackReportHeaderMessage() {
        return _feedbackReportHeaderMessage;
    }

    /**
     * Return the value of the "program name" from the XML file
     *
     * @return the value of the element program name from the XML file
     */
    public String getProgramName() {
        Program program = null;
        String programName = "Unknown";

        // Get program
        program = _applicationDataModel.getProgram();

        if (program != null) {
            programName = program.getName();
            _logger.fine("Program name has been taken on model");
        }

        return programName;
    }

    /**
     * Return the value of the element "program version" from the XML file
     *
     * @return the value of the element program version from the XML file
     */
    public String getProgramVersion() {
        Program program = null;
        String programVersion = "?.?";

        // Get program
        program = _applicationDataModel.getProgram();

        if (program != null) {
            programVersion = program.getVersion();
            _logger.fine("Program version has been taken on model");
        }

        return programVersion;
    }

    /**
     * Return the value of the field "link" from the XML file
     *
     * @return the value of the field link from the XML file
     */
    public String getLinkValue() {
        String mainWebPageURL = _mainWebPageURL;

        mainWebPageURL = _applicationDataModel.getLink();
        _logger.fine("MainWebPageURL value has been taken on model:"
                + mainWebPageURL);

        return mainWebPageURL;
    }

    /**
     * Return the value of the release notes "link" based onto the XML link
     * element.
     *
     * @return the release notes link
     */
    public String getReleaseNotesLinkValue() {
        String releaseNotesLink = getLinkValue() + "/releasenotes.htm";
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("ReleaseNotesLink value is :" + releaseNotesLink);
        }

        return releaseNotesLink;
    }

    /**
     * Return the value of the FAQ "link" based onto the XML link element.
     *
     * @return the FAQ link
     */
    public String getFaqLinkValue() {
        String faqLink = getLinkValue() + "/faq/";
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("FaqLink value is :" + faqLink);
        }

        return faqLink;
    }

    /**
     * Return the value of the Hot News RSS feed "link" based onto the XML link element.
     *
     * @return the Hot News RSS feed link
     */
    public String getHotNewsRSSFeedLinkValue() {
        String programName = getProgramName();
        programName = programName.toLowerCase();

        String hotNewsRSSFeedLink = getLinkValue() + "/" + programName
                + ".rss";
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("HotNewsRSSFeedLink value is :" + hotNewsRSSFeedLink);
        }

        return hotNewsRSSFeedLink;
    }

    /**
     * Return the value of the element "compilation date" from the XML file
     *
     * @return the value of the element compilation date from the XML file
     */
    public String getCompilationDate() {
        Compilation compilation = null;
        String compilationDate = "Unknown";

        // Get compilation
        compilation = _applicationDataModel.getCompilation();

        if (compilation != null) {
            compilationDate = compilation.getDate();
            _logger.fine("Compilation date has been taken on model");
        }

        return compilationDate;
    }

    /**
     * Return the value of the element "compilator version" from the XML file
     *
     * @return the value of the element compilator version from the XML file
     */
    public String getCompilatorVersion() {
        Compilation compilation = null;
        String compilationCompilator = "Unknown";

        // Get compilation
        compilation = _applicationDataModel.getCompilation();

        if (compilation != null) {
            compilationCompilator = compilation.getCompiler();
            _logger.fine("Compilation compilator has been taken on model");
        }

        return compilationCompilator;
    }

    /**
     * Return the value of the field "text" from the XML file
     *
     * @return the value of the field text from the XML file
     */
    public String getTextValue() {
        String text = "";

        text = _applicationDataModel.getText();
        _logger.fine("Text value has been taken on model");

        return text;
    }

    /**
     * Return the informations about "packages" taken from the XML file
     *
     * @return vector template [name, link, description], [name, link, description]...
     */
    public Vector<String> getPackagesInfo() {
        Vector<String> packagesInfo = new Vector<String>();

        // TODO: API: use objects not Vector<string>
        Dependences dependences = _applicationDataModel.getDependences();

        // For each package
        for (fr.jmmc.jmcs.data.model.Package p : dependences.getPackages()) {
            packagesInfo.add(p.getName());
            packagesInfo.add(p.getLink());
            packagesInfo.add(p.getDescription());
        }

        _logger.fine("Packages informations have been taken and formated");

        return packagesInfo;
    }

    /**
     * Return the value of the field "copyright" from the XML file
     *
     * @return the value of the field copyright from the XML file
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
            _logger.log(Level.WARNING,
                    "Cannot parse date '" + compilationDate + "' will use current year instead.", pe);

            // Otherwise use the current year
            Calendar cal = new GregorianCalendar();
            year = cal.get(Calendar.YEAR);
        }

        return "Copyright \u00A9 1999 - " + year + ", JMMC.";
    }

    /**
     * Return menubar from XML
     *
     * @return menubar
     */
    public Menubar getMenubar() {
        return _applicationDataModel.getMenubar();
    }
}
/*___oOo___*/
