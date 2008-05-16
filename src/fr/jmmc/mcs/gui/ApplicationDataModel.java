/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ApplicationDataModel.java,v 1.3 2008-05-16 13:08:26 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2008/04/24 15:57:55  mella
 * Grab application data from given url.
 * Remove not used try catch.
 *
 * Revision 1.1  2008/04/16 14:15:27  fgalland
 * Creation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import fr.jmmc.mcs.gui.castor.*;

import java.io.*;

import java.net.URL;

import java.util.logging.*;


/**
 * This class get informations from XML file
 * and returns values of field to the View Class
 */
public class ApplicationDataModel
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger(ApplicationDataModel.class.getName());

    /** The JAVA class which castor has generated with the XSD file */
    private ApplicationData _applicationDataCastorModel = null;

    /** Logo file name */
    private String _logoFileName = "logo.png";

    /** Main web page URL */
    private String _mainWebPageURL = "http://www.jmmc.fr/";

    /** Constructor */
    public ApplicationDataModel(URL dataModelURL)
    {
        if (dataModelURL != null)
        {
            _logger.fine("Loading Application data model from " + dataModelURL);

            InputStreamReader inputStreamReader;

            try
            {
                inputStreamReader               = new InputStreamReader(dataModelURL.openStream());
                _applicationDataCastorModel     = ApplicationData.unmarshal(inputStreamReader);
            }
            catch (Exception ex)
            {
                _logger.log(Level.WARNING,
                    "Cannot unmarshal ApplicationData.xml", ex);
            }

            _logger.fine("Application data model loaded.");
        }
    }

    /**
     * Return the file name of JMMC logo
     *
     * @return the file name of JMMC logo
     */
    public String getLogoURL()
    {
        _logger.fine("logoUrl=" + _logoFileName);

        return _logoFileName;
    }

    /**
     * Return the main web page url
     *
     * @return the main web page url
     */
    public String getMainWebPageURL()
    {
        return _mainWebPageURL;
    }

    /**
     * Return the value of the "program name" from the XML file
     *
     * @return the value of the element program name from the XML file
     */
    public String getProgramName()
    {
        Program program     = null;
        String  programName = null;

        if (_applicationDataCastorModel != null)
        {
            program = _applicationDataCastorModel.getProgram();

            if (program != null)
            {
                programName = program.getName();
                _logger.fine("Program name has been taken on model");
            }
        }

        return programName;
    }

    /**
     * Return the value of the element "program version" from the XML file
     *
     * @return the value of the element program version from the XML file
     */
    public String getProgramVersion()
    {
        Program program        = null;
        String  programVersion = null;

        if (_applicationDataCastorModel != null)
        {
            program = _applicationDataCastorModel.getProgram();

            if (program != null)
            {
                programVersion = program.getVersion();
                _logger.fine("Program version has been taken on model");
            }
        }

        return programVersion;
    }

    /**
     * Return the value of the field "link" from the XML file
     *
     * @return the value of the field link from the XML file
     */
    public String getLinkValue()
    {
        String mainWebPageURL = null;

        if (_applicationDataCastorModel != null)
        {
            mainWebPageURL = _applicationDataCastorModel.getLink();
            _logger.fine("MainWebPageURL value has been taken on model");
        }

        return mainWebPageURL;
    }

    /**
     * Return the value of the element "compilation date" from the XML file
     *
     * @return the value of the element compilation date from the XML file
     */
    public String getCompilationDate()
    {
        Compilation compilation     = null;
        String      compilationDate = "Unknown";

        if (_applicationDataCastorModel != null)
        {
            compilation = _applicationDataCastorModel.getCompilation();

            if (compilation != null)
            {
                compilationDate = compilation.getDate();
                _logger.fine("Compilation date has been taken on model");
            }
        }

        return compilationDate;
    }

    /**
     * Return the value of the element "compilator version" from the XML file
     *
     * @return the value of the element compilator version from the XML file
     */
    public String getCompilatorVersion()
    {
        Compilation compilation           = null;
        String      compilationCompilator = "Unknown";

        if (_applicationDataCastorModel != null)
        {
            compilation = _applicationDataCastorModel.getCompilation();

            if (compilation != null)
            {
                compilationCompilator = compilation.getCompiler();
                _logger.fine("Compilation compilator has been taken on model");
            }
        }

        return compilationCompilator;
    }

    /**
     * Return the value of the field "text" from the XML file
     *
     * @return the value of the field text from the XML file
     */
    public String getTextValue()
    {
        String text = "";

        if (_applicationDataCastorModel != null)
        {
            text = _applicationDataCastorModel.getText();
            _logger.fine("Text value has been taken on model");
        }

        return text;
    }

    /**
     * Return the informations about "packages" taken from the XML file
     *
     * @return the informations about packages taken from the XML file
     */
    public String[] getPackagesInfo()
    {
        String[] packagesInfo = null;

        if (_applicationDataCastorModel != null)
        {
            fr.jmmc.mcs.gui.castor.Dependences dependences = _applicationDataCastorModel.getDependences();
            fr.jmmc.mcs.gui.castor.Package[]   packages    = dependences.get_package();

            packagesInfo                                   = new String[packages.length];

            // For each package found
            for (int i = 0; i < packages.length; i++)
            {
                String link    = packages[i].getLink();
                String pkgInfo = "";

                if (link != null)
                {
                    // Generate a string with format "<a href='{link}'>{name}</a> : {description} <br>"
                    pkgInfo += ("<a href = '" + link + "'>");
                    pkgInfo += (packages[i].getName() + "</a> : ");
                }
                else
                {
                    pkgInfo += (packages[i].getName() + " : ");
                }

                pkgInfo += (packages[i].getDescription());

                if (i < (packages.length - 1))
                {
                    pkgInfo += "<br>";
                }

                packagesInfo[i] = pkgInfo;
            }

            _logger.fine("Packages informations have been taken and formated");
        }

        return packagesInfo;
    }

    /**
     * Return the value of the field "copyright" from the XML file
     *
     * @return the value of the field copyright from the XML file
     */
    public String getCopyrightValue()
    {
        String copyright = "Copyright JMMC";

        if (_applicationDataCastorModel != null)
        {
            copyright = _applicationDataCastorModel.getCopyright();
            _logger.fine("Copyright value has been taken on model");
        }

        return copyright;
    }
}
/*___oOo___*/
