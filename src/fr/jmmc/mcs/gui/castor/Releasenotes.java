/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Releasenotes.java,v 1.2 2011-02-15 17:01:58 mella Exp $
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
 * Class Releasenotes.
 * 
 * @version $Revision: 1.2 $ $Date: 2011-02-15 17:01:58 $
 */
public class Releasenotes implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _releaseList
     */
    private java.util.ArrayList _releaseList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Releasenotes() {
        super();
        _releaseList = new ArrayList();
    } //-- fr.jmmc.mcs.gui.castor.Releasenotes()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addRelease
     * 
     * @param vRelease
     */
    public void addRelease(fr.jmmc.mcs.gui.castor.Release vRelease)
        throws java.lang.IndexOutOfBoundsException
    {
        _releaseList.add(vRelease);
    } //-- void addRelease(fr.jmmc.mcs.gui.castor.Release) 

    /**
     * Method addRelease
     * 
     * @param index
     * @param vRelease
     */
    public void addRelease(int index, fr.jmmc.mcs.gui.castor.Release vRelease)
        throws java.lang.IndexOutOfBoundsException
    {
        _releaseList.add(index, vRelease);
    } //-- void addRelease(int, fr.jmmc.mcs.gui.castor.Release) 

    /**
     * Method clearRelease
     */
    public void clearRelease()
    {
        _releaseList.clear();
    } //-- void clearRelease() 

    /**
     * Method enumerateRelease
     */
    public java.util.Enumeration enumerateRelease()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_releaseList.iterator());
    } //-- java.util.Enumeration enumerateRelease() 

    /**
     * Method getRelease
     * 
     * @param index
     */
    public fr.jmmc.mcs.gui.castor.Release getRelease(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _releaseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mcs.gui.castor.Release) _releaseList.get(index);
    } //-- fr.jmmc.mcs.gui.castor.Release getRelease(int) 

    /**
     * Method getRelease
     */
    public fr.jmmc.mcs.gui.castor.Release[] getRelease()
    {
        int size = _releaseList.size();
        fr.jmmc.mcs.gui.castor.Release[] mArray = new fr.jmmc.mcs.gui.castor.Release[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mcs.gui.castor.Release) _releaseList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mcs.gui.castor.Release[] getRelease() 

    /**
     * Method getReleaseCount
     */
    public int getReleaseCount()
    {
        return _releaseList.size();
    } //-- int getReleaseCount() 

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
     * Method removeRelease
     * 
     * @param vRelease
     */
    public boolean removeRelease(fr.jmmc.mcs.gui.castor.Release vRelease)
    {
        boolean removed = _releaseList.remove(vRelease);
        return removed;
    } //-- boolean removeRelease(fr.jmmc.mcs.gui.castor.Release) 

    /**
     * Method setRelease
     * 
     * @param index
     * @param vRelease
     */
    public void setRelease(int index, fr.jmmc.mcs.gui.castor.Release vRelease)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _releaseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _releaseList.set(index, vRelease);
    } //-- void setRelease(int, fr.jmmc.mcs.gui.castor.Release) 

    /**
     * Method setRelease
     * 
     * @param releaseArray
     */
    public void setRelease(fr.jmmc.mcs.gui.castor.Release[] releaseArray)
    {
        //-- copy array
        _releaseList.clear();
        for (int i = 0; i < releaseArray.length; i++) {
            _releaseList.add(releaseArray[i]);
        }
    } //-- void setRelease(fr.jmmc.mcs.gui.castor.Release) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mcs.gui.castor.Releasenotes unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mcs.gui.castor.Releasenotes) Unmarshaller.unmarshal(fr.jmmc.mcs.gui.castor.Releasenotes.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Releasenotes unmarshal(java.io.Reader) 

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
