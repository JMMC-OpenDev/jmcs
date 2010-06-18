/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Menubar.java,v 1.1 2010-06-18 08:32:50 mella Exp $
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
 * Class Menubar.
 * 
 * @version $Revision: 1.1 $ $Date: 2010-06-18 08:32:50 $
 */
public class Menubar implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _menuList
     */
    private java.util.ArrayList _menuList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Menubar() {
        super();
        _menuList = new ArrayList();
    } //-- fr.jmmc.mcs.gui.castor.Menubar()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addMenu
     * 
     * @param vMenu
     */
    public void addMenu(fr.jmmc.mcs.gui.castor.Menu vMenu)
        throws java.lang.IndexOutOfBoundsException
    {
        _menuList.add(vMenu);
    } //-- void addMenu(fr.jmmc.mcs.gui.castor.Menu) 

    /**
     * Method addMenu
     * 
     * @param index
     * @param vMenu
     */
    public void addMenu(int index, fr.jmmc.mcs.gui.castor.Menu vMenu)
        throws java.lang.IndexOutOfBoundsException
    {
        _menuList.add(index, vMenu);
    } //-- void addMenu(int, fr.jmmc.mcs.gui.castor.Menu) 

    /**
     * Method clearMenu
     */
    public void clearMenu()
    {
        _menuList.clear();
    } //-- void clearMenu() 

    /**
     * Method enumerateMenu
     */
    public java.util.Enumeration enumerateMenu()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_menuList.iterator());
    } //-- java.util.Enumeration enumerateMenu() 

    /**
     * Method getMenu
     * 
     * @param index
     */
    public fr.jmmc.mcs.gui.castor.Menu getMenu(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _menuList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (fr.jmmc.mcs.gui.castor.Menu) _menuList.get(index);
    } //-- fr.jmmc.mcs.gui.castor.Menu getMenu(int) 

    /**
     * Method getMenu
     */
    public fr.jmmc.mcs.gui.castor.Menu[] getMenu()
    {
        int size = _menuList.size();
        fr.jmmc.mcs.gui.castor.Menu[] mArray = new fr.jmmc.mcs.gui.castor.Menu[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.jmmc.mcs.gui.castor.Menu) _menuList.get(index);
        }
        return mArray;
    } //-- fr.jmmc.mcs.gui.castor.Menu[] getMenu() 

    /**
     * Method getMenuCount
     */
    public int getMenuCount()
    {
        return _menuList.size();
    } //-- int getMenuCount() 

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
     * Method removeMenu
     * 
     * @param vMenu
     */
    public boolean removeMenu(fr.jmmc.mcs.gui.castor.Menu vMenu)
    {
        boolean removed = _menuList.remove(vMenu);
        return removed;
    } //-- boolean removeMenu(fr.jmmc.mcs.gui.castor.Menu) 

    /**
     * Method setMenu
     * 
     * @param index
     * @param vMenu
     */
    public void setMenu(int index, fr.jmmc.mcs.gui.castor.Menu vMenu)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _menuList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _menuList.set(index, vMenu);
    } //-- void setMenu(int, fr.jmmc.mcs.gui.castor.Menu) 

    /**
     * Method setMenu
     * 
     * @param menuArray
     */
    public void setMenu(fr.jmmc.mcs.gui.castor.Menu[] menuArray)
    {
        //-- copy array
        _menuList.clear();
        for (int i = 0; i < menuArray.length; i++) {
            _menuList.add(menuArray[i]);
        }
    } //-- void setMenu(fr.jmmc.mcs.gui.castor.Menu) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mcs.gui.castor.Menubar unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mcs.gui.castor.Menubar) Unmarshaller.unmarshal(fr.jmmc.mcs.gui.castor.Menubar.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Menubar unmarshal(java.io.Reader) 

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
