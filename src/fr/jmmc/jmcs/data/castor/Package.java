/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Package.java,v 1.2 2011-02-15 17:01:57 mella Exp $
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
 * Class Package.
 * 
 * @version $Revision: 1.2 $ $Date: 2011-02-15 17:01:57 $
 */
public class Package implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _link
     */
    private java.lang.String _link;

    /**
     * Field _jars
     */
    private java.lang.String _jars;

    /**
     * Field _license
     */
    private java.lang.String _license;


      //----------------/
     //- Constructors -/
    //----------------/

    public Package() {
        super();
    } //-- fr.jmmc.mcs.gui.castor.Package()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'jars'.
     * 
     * @return the value of field 'jars'.
     */
    public java.lang.String getJars()
    {
        return this._jars;
    } //-- java.lang.String getJars() 

    /**
     * Returns the value of field 'license'.
     * 
     * @return the value of field 'license'.
     */
    public java.lang.String getLicense()
    {
        return this._license;
    } //-- java.lang.String getLicense() 

    /**
     * Returns the value of field 'link'.
     * 
     * @return the value of field 'link'.
     */
    public java.lang.String getLink()
    {
        return this._link;
    } //-- java.lang.String getLink() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

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
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'jars'.
     * 
     * @param jars the value of field 'jars'.
     */
    public void setJars(java.lang.String jars)
    {
        this._jars = jars;
    } //-- void setJars(java.lang.String) 

    /**
     * Sets the value of field 'license'.
     * 
     * @param license the value of field 'license'.
     */
    public void setLicense(java.lang.String license)
    {
        this._license = license;
    } //-- void setLicense(java.lang.String) 

    /**
     * Sets the value of field 'link'.
     * 
     * @param link the value of field 'link'.
     */
    public void setLink(java.lang.String link)
    {
        this._link = link;
    } //-- void setLink(java.lang.String) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.jmcs.data.castor.Package unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.jmcs.data.castor.Package) Unmarshaller.unmarshal(fr.jmmc.jmcs.data.castor.Package.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Package unmarshal(java.io.Reader) 

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
