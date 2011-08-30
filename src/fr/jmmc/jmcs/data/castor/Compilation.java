/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Compilation.java,v 1.2 2011-02-15 17:01:57 mella Exp $
 */

package fr.jmmc.jmcs.data.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Compilation.
 * 
 * @version $Revision: 1.2 $ $Date: 2011-02-15 17:01:57 $
 */
public class Compilation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _date
     */
    private java.lang.String _date;

    /**
     * Field _compiler
     */
    private java.lang.String _compiler;


      //----------------/
     //- Constructors -/
    //----------------/

    public Compilation() {
        super();
    } //-- fr.jmmc.mcs.gui.castor.Compilation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'compiler'.
     * 
     * @return the value of field 'compiler'.
     */
    public java.lang.String getCompiler()
    {
        return this._compiler;
    } //-- java.lang.String getCompiler() 

    /**
     * Returns the value of field 'date'.
     * 
     * @return the value of field 'date'.
     */
    public java.lang.String getDate()
    {
        return this._date;
    } //-- java.lang.String getDate() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'compiler'.
     * 
     * @param compiler the value of field 'compiler'.
     */
    public void setCompiler(java.lang.String compiler)
    {
        this._compiler = compiler;
    } //-- void setCompiler(java.lang.String) 

    /**
     * Sets the value of field 'date'.
     * 
     * @param date the value of field 'date'.
     */
    public void setDate(java.lang.String date)
    {
        this._date = date;
    } //-- void setDate(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.jmcs.data.castor.Compilation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.jmcs.data.castor.Compilation) Unmarshaller.unmarshal(fr.jmmc.jmcs.data.castor.Compilation.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Compilation unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
