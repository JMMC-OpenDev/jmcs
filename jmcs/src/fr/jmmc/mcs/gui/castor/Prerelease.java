/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Prerelease.java,v 1.2 2011-02-15 17:01:58 mella Exp $
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
 * Class Prerelease.
 * 
 * @version $Revision: 1.2 $ $Date: 2011-02-15 17:01:58 $
 */
public class Prerelease implements java.io.Serializable {


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
     * Field _changeList
     */
    private java.util.ArrayList _changeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Prerelease() {
        super();
        _changeList = new ArrayList();
    } //-- fr.jmmc.mcs.gui.castor.Prerelease()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addChange
     * 
     * @param vChange
     */
    public void addChange(fr.jmmc.mcs.gui.castor.Change vChange)
        throws java.lang.IndexOutOfBoundsException
    {
        _changeList.add(vChange);
    } //-- void addChange(fr.jmmc.mcs.gui.castor.Change) 

    /**
     * Method addChange
     * 
     * @param index
     * @param vChange
     */
    public void addChange(int index, fr.jmmc.mcs.gui.castor.Change vChange)
        throws java.lang.IndexOutOfBoundsException
    {
        _changeList.add(index, vChange);
    } //-- void addChange(int, fr.jmmc.mcs.gui.castor.Change) 

    /**
     * Method clearChange
     */
    public void clearChange()
    {
        _changeList.clear();
    } //-- void clearChange() 

    /**
     * Method enumerateChange
     */
    public java.util.Enumeration enumerateChange()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_changeList.iterator());
    } //-- java.util.Enumeration enumerateChange() 

    /**
     * Method getChange
     * 
     * @param index
     */
    public fr.jmmc.mcs.gui.castor.Change getChange(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _changeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mcs.gui.castor.Change) _changeList.get(index);
    } //-- fr.jmmc.mcs.gui.castor.Change getChange(int) 

    /**
     * Method getChange
     */
    public fr.jmmc.mcs.gui.castor.Change[] getChange()
    {
        int size = _changeList.size();
        fr.jmmc.mcs.gui.castor.Change[] mArray = new fr.jmmc.mcs.gui.castor.Change[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mcs.gui.castor.Change) _changeList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mcs.gui.castor.Change[] getChange() 

    /**
     * Method getChangeCount
     */
    public int getChangeCount()
    {
        return _changeList.size();
    } //-- int getChangeCount() 

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
     * Method removeChange
     * 
     * @param vChange
     */
    public boolean removeChange(fr.jmmc.mcs.gui.castor.Change vChange)
    {
        boolean removed = _changeList.remove(vChange);
        return removed;
    } //-- boolean removeChange(fr.jmmc.mcs.gui.castor.Change) 

    /**
     * Method setChange
     * 
     * @param index
     * @param vChange
     */
    public void setChange(int index, fr.jmmc.mcs.gui.castor.Change vChange)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _changeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _changeList.set(index, vChange);
    } //-- void setChange(int, fr.jmmc.mcs.gui.castor.Change) 

    /**
     * Method setChange
     * 
     * @param changeArray
     */
    public void setChange(fr.jmmc.mcs.gui.castor.Change[] changeArray)
    {
        //-- copy array
        _changeList.clear();
        for (int i = 0; i < changeArray.length; i++) {
            _changeList.add(changeArray[i]);
        }
    } //-- void setChange(fr.jmmc.mcs.gui.castor.Change) 

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
    public static fr.jmmc.mcs.gui.castor.Prerelease unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mcs.gui.castor.Prerelease) Unmarshaller.unmarshal(fr.jmmc.mcs.gui.castor.Prerelease.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Prerelease unmarshal(java.io.Reader) 

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
