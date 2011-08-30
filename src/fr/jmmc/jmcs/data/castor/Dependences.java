/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Dependences.java,v 1.2 2011-02-15 17:01:57 mella Exp $
 */

package fr.jmmc.jmcs.data.castor;

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
 * Class Dependences.
 * 
 * @version $Revision: 1.2 $ $Date: 2011-02-15 17:01:57 $
 */
public class Dependences implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _packageList
     */
    private java.util.ArrayList _packageList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Dependences() {
        super();
        _packageList = new ArrayList();
    } //-- fr.jmmc.mcs.gui.castor.Dependences()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method add_package
     * 
     * @param v_package
     */
    public void add_package(fr.jmmc.jmcs.data.castor.Package v_package)
        throws java.lang.IndexOutOfBoundsException
    {
        _packageList.add(v_package);
    } //-- void add_package(fr.jmmc.mcs.gui.castor.Package) 

    /**
     * Method add_package
     * 
     * @param index
     * @param v_package
     */
    public void add_package(int index, fr.jmmc.jmcs.data.castor.Package v_package)
        throws java.lang.IndexOutOfBoundsException
    {
        _packageList.add(index, v_package);
    } //-- void add_package(int, fr.jmmc.mcs.gui.castor.Package) 

    /**
     * Method clear_package
     */
    public void clear_package()
    {
        _packageList.clear();
    } //-- void clear_package() 

    /**
     * Method enumerate_package
     */
    public java.util.Enumeration enumerate_package()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_packageList.iterator());
    } //-- java.util.Enumeration enumerate_package() 

    /**
     * Method get_package
     * 
     * @param index
     */
    public fr.jmmc.jmcs.data.castor.Package get_package(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _packageList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.jmcs.data.castor.Package) _packageList.get(index);
    } //-- fr.jmmc.mcs.gui.castor.Package get_package(int) 

    /**
     * Method get_package
     */
    public fr.jmmc.jmcs.data.castor.Package[] get_package()
    {
        int size = _packageList.size();
        fr.jmmc.jmcs.data.castor.Package[] mArray = new fr.jmmc.jmcs.data.castor.Package[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.jmcs.data.castor.Package) _packageList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mcs.gui.castor.Package[] get_package() 

    /**
     * Method get_packageCount
     */
    public int get_packageCount()
    {
        return _packageList.size();
    } //-- int get_packageCount() 

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
     * Method remove_package
     * 
     * @param v_package
     */
    public boolean remove_package(fr.jmmc.jmcs.data.castor.Package v_package)
    {
        boolean removed = _packageList.remove(v_package);
        return removed;
    } //-- boolean remove_package(fr.jmmc.mcs.gui.castor.Package) 

    /**
     * Method set_package
     * 
     * @param index
     * @param v_package
     */
    public void set_package(int index, fr.jmmc.jmcs.data.castor.Package v_package)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _packageList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _packageList.set(index, v_package);
    } //-- void set_package(int, fr.jmmc.mcs.gui.castor.Package) 

    /**
     * Method set_package
     * 
     * @param _packageArray
     */
    public void set_package(fr.jmmc.jmcs.data.castor.Package[] _packageArray)
    {
        //-- copy array
        _packageList.clear();
        for (int i = 0; i < _packageArray.length; i++) {
            _packageList.add(_packageArray[i]);
        }
    } //-- void set_package(fr.jmmc.mcs.gui.castor.Package) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.jmcs.data.castor.Dependences unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.jmcs.data.castor.Dependences) Unmarshaller.unmarshal(fr.jmmc.jmcs.data.castor.Dependences.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Dependences unmarshal(java.io.Reader) 

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
