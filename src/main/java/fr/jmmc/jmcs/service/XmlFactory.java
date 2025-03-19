/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2025, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class to parse XML document (DocumentBuilder and SAXParser).
 *
 * @author Laurent BOURGES.
 */
public final class XmlFactory {

    /** encoding used for XML and XSL documents */
    public static final String ENCODING = "UTF-8";

    // Copied from OpenJDK24's jdk.xml.internal.JdkConstants source code:
    /**
     * JDK property indicating whether the parser shall print out entity
     * count information.
     * Value: a string "yes" means print, "no" or any other string means not.
     */
    public static final String JDK_DEBUG_LIMIT = "jdk.xml.getEntityCountInfo";

    //
    // Implementation limits: corresponding System Properties of the above
    // API properties.
    //
    // Note: as of JDK 17, properties and System properties now share the same
    // name with a prefix "jdk.xml.".
    //
    /**
     * JDK entity expansion limit; Note that the existing system property
     * "entityExpansionLimit" with no prefix is still observed
     */
    public static final String SP_ENTITY_EXPANSION_LIMIT = "jdk.xml.entityExpansionLimit";

    /**
     * JDK element attribute limit; Note that the existing system property
     * "elementAttributeLimit" with no prefix is still observed
     */
    public static final String SP_ELEMENT_ATTRIBUTE_LIMIT = "jdk.xml.elementAttributeLimit";

    /**
     * JDK maxOccur limit; Note that the existing system property
     * "maxOccurLimit" with no prefix is still observed
     */
    public static final String SP_MAX_OCCUR_LIMIT = "jdk.xml.maxOccurLimit";

    /**
     * JDK total entity size limit
     */
    public static final String SP_TOTAL_ENTITY_SIZE_LIMIT = "jdk.xml.totalEntitySizeLimit";

    /**
     * JDK maximum general entity size limit
     */
    public static final String SP_GENERAL_ENTITY_SIZE_LIMIT = "jdk.xml.maxGeneralEntitySizeLimit";

    /**
     * JDK node count limit in entities that limits the total number of nodes
     * in all of entity references.
     */
    public static final String SP_ENTITY_REPLACEMENT_LIMIT = "jdk.xml.entityReplacementLimit";

    /**
     * JDK maximum parameter entity size limit
     */
    public static final String SP_PARAMETER_ENTITY_SIZE_LIMIT = "jdk.xml.maxParameterEntitySizeLimit";
    /**
     * JDK maximum XML name limit
     */
    public static final String SP_XML_NAME_LIMIT = "jdk.xml.maxXMLNameLimit";

    /**
     * JDK maxElementDepth limit
     */
    public static final String SP_MAX_ELEMENT_DEPTH = "jdk.xml.maxElementDepth";

    private static final String[] XML_ALL_LIMITS = new String[]{
        JDK_DEBUG_LIMIT,
        SP_ENTITY_EXPANSION_LIMIT,
        SP_ELEMENT_ATTRIBUTE_LIMIT,
        SP_MAX_OCCUR_LIMIT,
        SP_TOTAL_ENTITY_SIZE_LIMIT,
        SP_GENERAL_ENTITY_SIZE_LIMIT,
        SP_ENTITY_REPLACEMENT_LIMIT,
        SP_PARAMETER_ENTITY_SIZE_LIMIT,
        /* SP_XML_NAME_LIMIT, (disabled as buggy) */
        SP_MAX_ELEMENT_DEPTH
    };

    /** Unsecured Non-validating DocumentBuilder factory */
    private static DocumentBuilderFactory _docBuilderNonValidatingFactory = null;
    /** Unsecured Validating DocumentBuilder factory */
    private static DocumentBuilderFactory _docBuilderValidatingFactory = null;
    /** Unsecured Non-validating SAXParserFactory factory */
    private static SAXParserFactory _saxParserFactory = null;

    /** Forbidden constructor */
    @SuppressWarnings("unused")
    private XmlFactory() {
        /* no-op */
    }

    /**
     * Parses a local xml file with JAXP
     *
     * @param f local file
     *
     * @return Document (DOM)
     */
    public Document parse(final File f) {
        String uri = "file:" + f.getAbsolutePath();

        if (File.separatorChar == '\\') {
            uri = uri.replace('\\', '/');
        }
        return parse(new InputSource(uri));
    }

    /**
     * Parses an xml stream with JAXP
     *
     * @param is input stream
     *
     * @return Document (DOM)
     */
    public Document parse(final InputStream is) {
        return parse(new InputSource(is));
    }

    /**
     * Parses an xml stream with JAXP
     *
     * @param is input stream
     * @param systemId absolute file or URL reference used to resolve other xml document references
     *
     * @return Document (DOM)
     */
    public Document parse(final InputStream is, final String systemId) {
        final InputSource in = new InputSource(is);
        in.setSystemId(systemId);
        return parse(in);
    }

    /**
     * Parses an xml stream with JAXP
     *
     * @param input xml input source
     *
     * @return Document (DOM)
     */
    public Document parse(final InputSource input) {
        return parse(input, false);
    }

    /**
     * Parses an xml stream with JAXP
     *
     * @param input xml input source
     * @param validating true to enable XML validation
     *
     * @return Document (DOM)
     */
    public Document parse(final InputSource input, final boolean validating) {
        Document document = null;
        try {
            input.setEncoding(ENCODING);
            document = newDocumentBuilder(validating).parse(input);
        } catch (final SAXException se) {
            throw new IllegalStateException("XmlFactory.parse : error", se);
        } catch (final IOException ioe) {
            throw new IllegalStateException("XmlFactory.parse : error", ioe);
        }
        return document;
    }

    /**
     * Create a new DocumentBuilder instance. If validating is true,
     * the contents is validated against the DTD specified in the file.
    
     * @param validating true to enable XML validation
     * @return new DocumentBuilder instance
     *
     * @throws IllegalStateException if DocumentBuilderFactory or newDocumentBuilder initialization failed
     */
    public static DocumentBuilder newDocumentBuilder(final boolean validating) {
        DocumentBuilder builder = null;
        // Get the appropriate (reused) factory:
        final DocumentBuilderFactory factory = getDocumentBuilderFactory(validating);
        if (factory != null) {
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                throw new IllegalStateException("XmlFactory.newDocumentBuilder : failure on newDocumentBuilder initialisation : ", pce);
            }
        }
        return builder;
    }

    /**
     * Get or create a DocumentBuilderFactory instance (singletons)
     * 
     * @param validating true to enable XML validation
     * @return new DocumentBuilderFactory instance
     *
     * @throws IllegalStateException if DocumentBuilderFactory initialization failed
     */
    private static synchronized DocumentBuilderFactory getDocumentBuilderFactory(final boolean validating) {
        DocumentBuilderFactory factory = (validating) ? _docBuilderValidatingFactory : _docBuilderNonValidatingFactory;

        if (factory == null) {
            factory = newDocumentBuilderFactory(validating);

            if (factory != null) {
                if (validating) {
                    _docBuilderValidatingFactory = factory;
                } else {
                    _docBuilderNonValidatingFactory = factory;
                }
            }
        }
        return factory;
    }

    /**
     * Create a DocumentBuilderFactory instance
     * 
     * @param validating true to enable XML validation
     * @return new DocumentBuilderFactory instance
     *
     * @throws IllegalStateException if DocumentBuilderFactory initialization failed
     */
    private static DocumentBuilderFactory newDocumentBuilderFactory(final boolean validating) {
        DocumentBuilderFactory factory = null;
        try {
            // Create a builder factory 
            factory = DocumentBuilderFactory.newInstance();
            // Disable secure processing (no limits):
            factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, false);
            // Set validation:
            factory.setValidating(validating);
        } catch (Exception e) {
            throw new IllegalStateException("XmlFactory.newDocumentBuilderFactory : failure on DocumentBuilderFactory initialisation : ", e);
        }
        return factory;
    }

    /**
     * Get or create a SAXParserFactory instance (singleton)
     * @return new SAXParserFactory instance
     *
     * @throws IllegalStateException if SAXParserFactory initialization failed
     */
    public static synchronized SAXParserFactory getSAXParserFactory() {
        if (_saxParserFactory == null) {
            _saxParserFactory = newSAXParserFactory();
        }
        return _saxParserFactory;
    }

    private static SAXParserFactory newSAXParserFactory() {
        SAXParserFactory factory = null;
        try {
            factory = SAXParserFactory.newInstance();
            // Disable secure processing (no limits):
            factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, false);
            // Set validation:
            factory.setValidating(false);
        } catch (Exception e) {
            throw new IllegalStateException("XmlFactory.newSAXParserFactory(): failure on SAXParserFactory initialisation : ", e);
        }
        return factory;
    }

    /**
     * Disable all JAXP limits (jdk 24+)
     */
    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "DeadBranch"})
    public static void disableJAXPLimitsUsingSystemProperties() {
        for (String key : XML_ALL_LIMITS) {
            System.setProperty(key, "0");
            if (false) {
                System.out.println("[" + key + "]=" + System.getProperty(key));
            }
        }
        // Note: Do not set SP_XML_NAME_LIMIT as 0 do not disable it (bug in jdk8) !
        // System.setProperty(SP_XML_NAME_LIMIT, "1000");
    }

}
