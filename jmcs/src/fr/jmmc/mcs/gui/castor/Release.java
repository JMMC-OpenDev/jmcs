/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Release.java,v 1.2 2011-02-15 17:01:58 mella Exp $
 */

package fr.jmmc.mcs.gui.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Release.
 * 
 * @version $Revision: 1.2 $ $Date: 2011-02-15 17:01:58 $
 */
public class Release implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _version
     */
    private java.lang.String _version;

    /**
     * Field _tag
     */
    private java.lang.String _tag;

    /**
     * Field _prereleaseList
     */
    private java.util.ArrayList _prereleaseList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Release() {
        super();
        _prereleaseList = new ArrayList();
    } //-- fr.jmmc.mcs.gui.castor.Release()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPrerelease
     * 
     * @param vPrerelease
     */
    public void addPrerelease(fr.jmmc.mcs.gui.castor.Prerelease vPrerelease)
        throws java.lang.IndexOutOfBoundsException
    {
        _prereleaseList.add(vPrerelease);
    } //-- void addPrerelease(fr.jmmc.mcs.gui.castor.Prerelease) 

    /**
     * Method addPrerelease
     * 
     * @param index
     * @param vPrerelease
     */
    public void addPrerelease(int index, fr.jmmc.mcs.gui.castor.Prerelease vPrerelease)
        throws java.lang.IndexOutOfBoundsException
    {
        _prereleaseList.add(index, vPrerelease);
    } //-- void addPrerelease(int, fr.jmmc.mcs.gui.castor.Prerelease) 

    /**
     * Method clearPrerelease
     */
    public void clearPrerelease()
    {
        _prereleaseList.clear();
    } //-- void clearPrerelease() 

    /**
     * Method enumeratePrerelease
     */
    public java.util.Enumeration enumeratePrerelease()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_prereleaseList.iterator());
    } //-- java.util.Enumeration enumeratePrerelease() 

    /**
     * Method getPrerelease
     * 
     * @param index
     */
    public fr.jmmc.mcs.gui.castor.Prerelease getPrerelease(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _prereleaseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mcs.gui.castor.Prerelease) _prereleaseList.get(index);
    } //-- fr.jmmc.mcs.gui.castor.Prerelease getPrerelease(int) 

    /**
     * Method getPrerelease
     */
    public fr.jmmc.mcs.gui.castor.Prerelease[] getPrerelease()
    {
        int size = _prereleaseList.size();
        fr.jmmc.mcs.gui.castor.Prerelease[] mArray = new fr.jmmc.mcs.gui.castor.Prerelease[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mcs.gui.castor.Prerelease) _prereleaseList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mcs.gui.castor.Prerelease[] getPrerelease() 

    /**
     * Method getPrereleaseCount
     */
    public int getPrereleaseCount()
    {
        return _prereleaseList.size();
    } //-- int getPrereleaseCount() 

    /**
     * Returns the value of field 'tag'.
     * 
     * @return the value of field 'tag'.
     */
    public java.lang.String getTag()
    {
        return this._tag;
    } //-- java.lang.String getTag() 

    /**
     * Returns the value of field 'version'.
     * 
     * @return the value of field 'version'.
     */
    public java.lang.String getVersion()
    {
        return this._version;
    } //-- java.lang.String getVersion() 

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
     * Method removePrerelease
     * 
     * @param vPrerelease
     */
    public boolean removePrerelease(fr.jmmc.mcs.gui.castor.Prerelease vPrerelease)
    {
        boolean removed = _prereleaseList.remove(vPrerelease);
        return removed;
    } //-- boolean removePrerelease(fr.jmmc.mcs.gui.castor.Prerelease) 

    /**
     * Method setPrerelease
     * 
     * @param index
     * @param vPrerelease
     */
    public void setPrerelease(int index, fr.jmmc.mcs.gui.castor.Prerelease vPrerelease)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _prereleaseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _prereleaseList.set(index, vPrerelease);
    } //-- void setPrerelease(int, fr.jmmc.mcs.gui.castor.Prerelease) 

    /**
     * Method setPrerelease
     * 
     * @param prereleaseArray
     */
    public void setPrerelease(fr.jmmc.mcs.gui.castor.Prerelease[] prereleaseArray)
    {
        //-- copy array
        _prereleaseList.clear();
        for (int i = 0; i < prereleaseArray.length; i++) {
            _prereleaseList.add(prereleaseArray[i]);
        }
    } //-- void setPrerelease(fr.jmmc.mcs.gui.castor.Prerelease) 

    /**
     * Sets the value of field 'tag'.
     * 
     * @param tag the value of field 'tag'.
     */
    public void setTag(java.lang.String tag)
    {
        this._tag = tag;
    } //-- void setTag(java.lang.String) 

    /**
     * Sets the value of field 'version'.
     * 
     * @param version the value of field 'version'.
     */
    public void setVersion(java.lang.String version)
    {
        this._version = version;
    } //-- void setVersion(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mcs.gui.castor.Release unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mcs.gui.castor.Release) Unmarshaller.unmarshal(fr.jmmc.mcs.gui.castor.Release.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Release unmarshal(java.io.Reader) 

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
