/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: Menu.java,v 1.2 2011-02-15 17:01:57 mella Exp $
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
 * Class Menu.
 * 
 * @version $Revision: 1.2 $ $Date: 2011-02-15 17:01:57 $
 */
public class Menu implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _label
     */
    private java.lang.String _label;

    /**
     * Field _classpath
     */
    private java.lang.String _classpath;

    /**
     * Field _action
     */
    private java.lang.String _action;

    /**
     * Field _checkbox
     */
    private java.lang.String _checkbox;

    /**
     * Field _radiogroup
     */
    private java.lang.String _radiogroup;

    /**
     * Field _accelerator
     */
    private java.lang.String _accelerator;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _icon
     */
    private java.lang.String _icon;

    /**
     * Field _menuList
     */
    private java.util.ArrayList _menuList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Menu() {
        super();
        _menuList = new ArrayList();
    } //-- fr.jmmc.mcs.gui.castor.Menu()


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
     * Returns the value of field 'accelerator'.
     * 
     * @return the value of field 'accelerator'.
     */
    public java.lang.String getAccelerator()
    {
        return this._accelerator;
    } //-- java.lang.String getAccelerator() 

    /**
     * Returns the value of field 'action'.
     * 
     * @return the value of field 'action'.
     */
    public java.lang.String getAction()
    {
        return this._action;
    } //-- java.lang.String getAction() 

    /**
     * Returns the value of field 'checkbox'.
     * 
     * @return the value of field 'checkbox'.
     */
    public java.lang.String getCheckbox()
    {
        return this._checkbox;
    } //-- java.lang.String getCheckbox() 

    /**
     * Returns the value of field 'classpath'.
     * 
     * @return the value of field 'classpath'.
     */
    public java.lang.String getClasspath()
    {
        return this._classpath;
    } //-- java.lang.String getClasspath() 

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
     * Returns the value of field 'icon'.
     * 
     * @return the value of field 'icon'.
     */
    public java.lang.String getIcon()
    {
        return this._icon;
    } //-- java.lang.String getIcon() 

    /**
     * Returns the value of field 'label'.
     * 
     * @return the value of field 'label'.
     */
    public java.lang.String getLabel()
    {
        return this._label;
    } //-- java.lang.String getLabel() 

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
     * Returns the value of field 'radiogroup'.
     * 
     * @return the value of field 'radiogroup'.
     */
    public java.lang.String getRadiogroup()
    {
        return this._radiogroup;
    } //-- java.lang.String getRadiogroup() 

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
     * Sets the value of field 'accelerator'.
     * 
     * @param accelerator the value of field 'accelerator'.
     */
    public void setAccelerator(java.lang.String accelerator)
    {
        this._accelerator = accelerator;
    } //-- void setAccelerator(java.lang.String) 

    /**
     * Sets the value of field 'action'.
     * 
     * @param action the value of field 'action'.
     */
    public void setAction(java.lang.String action)
    {
        this._action = action;
    } //-- void setAction(java.lang.String) 

    /**
     * Sets the value of field 'checkbox'.
     * 
     * @param checkbox the value of field 'checkbox'.
     */
    public void setCheckbox(java.lang.String checkbox)
    {
        this._checkbox = checkbox;
    } //-- void setCheckbox(java.lang.String) 

    /**
     * Sets the value of field 'classpath'.
     * 
     * @param classpath the value of field 'classpath'.
     */
    public void setClasspath(java.lang.String classpath)
    {
        this._classpath = classpath;
    } //-- void setClasspath(java.lang.String) 

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
     * Sets the value of field 'icon'.
     * 
     * @param icon the value of field 'icon'.
     */
    public void setIcon(java.lang.String icon)
    {
        this._icon = icon;
    } //-- void setIcon(java.lang.String) 

    /**
     * Sets the value of field 'label'.
     * 
     * @param label the value of field 'label'.
     */
    public void setLabel(java.lang.String label)
    {
        this._label = label;
    } //-- void setLabel(java.lang.String) 

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
     * Sets the value of field 'radiogroup'.
     * 
     * @param radiogroup the value of field 'radiogroup'.
     */
    public void setRadiogroup(java.lang.String radiogroup)
    {
        this._radiogroup = radiogroup;
    } //-- void setRadiogroup(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mcs.gui.castor.Menu unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mcs.gui.castor.Menu) Unmarshaller.unmarshal(fr.jmmc.mcs.gui.castor.Menu.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.Menu unmarshal(java.io.Reader) 

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
