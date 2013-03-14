/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util.jaxb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class used to perform generic marshalling and unmarshalling operations.
 * @author Launrent BOURGES, Guillaume MELLA.
 */
public class JAXBUtils {

    // Members
    /** Logger */
    private final static Logger _logger = LoggerFactory.getLogger(JAXBUtils.class);

    /** Private constructor for utility class */
    private JAXBUtils() {
    }

    /**
     * Load on object from url.
     * @param inputUrl File to load
     * @param jbf JAXBFactory
     * @return unmarshalled object
     *
     * @throws IOException if an I/O exception occurred
     * @throws IllegalStateException if an unexpected exception occurred
     * @throws XmlBindException if a JAXBException was caught while creating an unmarshaller
     */
    public static Object loadObject(final URL inputUrl, final JAXBFactory jbf) throws IOException, IllegalStateException, XmlBindException {
        Object result = null;

        _logger.debug("JAXBUtils.loadObject() from url : {}", inputUrl);

        try {
            result = jbf.createUnMarshaller().unmarshal(new BufferedInputStream(inputUrl.openStream()));
        } catch (JAXBException ex) {
            handleException("Loading object from " + inputUrl, ex);
        }

        return result;
    }

    /**
     * Protected load method
     * @param inputFile File to load
     * @param jbf jaxb factory instance
     * @return unmarshalled object
     *
     * @throws IOException if an I/O exception occurred
     * @throws IllegalStateException if an unexpected exception occurred
     * @throws XmlBindException if a JAXBException was caught while creating an unmarshaller
     */
    public static Object loadObject(final File inputFile, final JAXBFactory jbf)
            throws IOException, IllegalStateException, XmlBindException {

        _logger.debug("JAXBUtils.loadObject() from file : {}", inputFile);

        Object result = null;
        try {
            final Unmarshaller u = jbf.createUnMarshaller();

            result = u.unmarshal(inputFile);

        } catch (JAXBException je) {
            handleException("Load failure on " + inputFile, je);
        }
        return result;
    }

    /**
     * Protected load method
     * @param reader any reader
     * @param jbf jaxb factory instance
     * @return unmarshalled object
     * 
     * @throws IOException if an I/O exception occurred
     * @throws IllegalStateException if an unexpected exception occurred
     * @throws XmlBindException if a JAXBException was caught while creating an unmarshaller
     */
    protected static Object loadObject(final Reader reader, final JAXBFactory jbf)
            throws IOException, IllegalStateException, XmlBindException {

        Object result = null;
        try {
            final Unmarshaller u = jbf.createUnMarshaller();

            result = u.unmarshal(reader);

        } catch (JAXBException je) {
            handleException("Load failure on " + reader, je);
        }
        return result;
    }

    /**
     * Protected save method
     * @param outputFile File to save
     * @param object to marshall
     * @param jbf jaxb factory instance
     *
     * @throws IOException if an I/O exception occurred
     * @throws IllegalStateException if an unexpected exception occurred
     */
    public static void saveObject(final File outputFile, final Object object, final JAXBFactory jbf)
            throws IOException, IllegalStateException {
        try {
            jbf.createMarshaller().marshal(object, outputFile);

        } catch (JAXBException je) {
            handleException("Save failure on " + outputFile, je);
        }
    }

    /**
     * Public save method
     * @param writer writer to use
     * @param object to marshall
     * @param jbf jaxb factory instance
     *
     * @throws IllegalStateException if an unexpected exception occurred
     */
    public static void saveObject(final Writer writer, final Object object, JAXBFactory jbf)
            throws IllegalStateException {
        try {
            jbf.createMarshaller().marshal(object, writer);
        } catch (JAXBException je) {
            throw new IllegalStateException("Serialization failure", je);
        }
    }

    /**
     * Handle JAXB Exception to extract IO Exception or unexpected exceptions
     * @param message message
     * @param je jaxb exception
     * 
     * @throws IllegalStateException if an unexpected exception occurred
     * @throws IOException if an I/O exception occurred
     */
    protected static void handleException(final String message, final JAXBException je) throws IllegalStateException, IOException {
        final Throwable cause = je.getCause();
        if (cause != null) {
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
        }
        if (je instanceof UnmarshalException) {
            throw new IllegalArgumentException("The loaded file does not correspond to a valid file", je);
        }
        throw new IllegalStateException(message, je);
    }
}
