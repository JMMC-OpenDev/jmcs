/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: ApplicationData.java,v 1.1 2010-06-18 08:32:49 mella Exp $
 */

package fr.jmmc.mcs.gui.castor;

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
 * Class ApplicationData.
 * 
 * @version $Revision: 1.1 $ $Date: 2010-06-18 08:32:49 $
 */
public class ApplicationData implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _link
     */
    private java.lang.String _link;

    /**
     * Field _program
     */
    private fr.jmmc.mcs.gui.castor.Program _program;

    /**
     * Field _compilation
     */
    private fr.jmmc.mcs.gui.castor.Compilation _compilation;

    /**
     * Field _text
     */
    private java.lang.String _text;

    /**
     * Field _dependences
     */
    private fr.jmmc.mcs.gui.castor.Dependences _dependences;

    /**
     * Field _menubar
     */
    private fr.jmmc.mcs.gui.castor.Menubar _menubar;

    /**
     * Field _releasenotes
     */
    private fr.jmmc.mcs.gui.castor.Releasenotes _releasenotes;

    /**
     * Field _acknowledgment
     */
    private java.lang.String _acknowledgment;


      //----------------/
     //- Constructors -/
    //----------------/

    public ApplicationData() {
        super();
    } //-- fr.jmmc.mcs.gui.castor.ApplicationData()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'acknowledgment'.
     * 
     * @return the value of field 'acknowledgment'.
     */
    public java.lang.String getAcknowledgment()
    {
        return this._acknowledgment;
    } //-- java.lang.String getAcknowledgment() 

    /**
     * Returns the value of field 'compilation'.
     * 
     * @return the value of field 'compilation'.
     */
    public fr.jmmc.mcs.gui.castor.Compilation getCompilation()
    {
        return this._compilation;
    } //-- fr.jmmc.mcs.gui.castor.Compilation getCompilation() 

    /**
     * Returns the value of field 'dependences'.
     * 
     * @return the value of field 'dependences'.
     */
    public fr.jmmc.mcs.gui.castor.Dependences getDependences()
    {
        return this._dependences;
    } //-- fr.jmmc.mcs.gui.castor.Dependences getDependences() 

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
     * Returns the value of field 'menubar'.
     * 
     * @return the value of field 'menubar'.
     */
    public fr.jmmc.mcs.gui.castor.Menubar getMenubar()
    {
        return this._menubar;
    } //-- fr.jmmc.mcs.gui.castor.Menubar getMenubar() 

    /**
     * Returns the value of field 'program'.
     * 
     * @return the value of field 'program'.
     */
    public fr.jmmc.mcs.gui.castor.Program getProgram()
    {
        return this._program;
    } //-- fr.jmmc.mcs.gui.castor.Program getProgram() 

    /**
     * Returns the value of field 'releasenotes'.
     * 
     * @return the value of field 'releasenotes'.
     */
    public fr.jmmc.mcs.gui.castor.Releasenotes getReleasenotes()
    {
        return this._releasenotes;
    } //-- fr.jmmc.mcs.gui.castor.Releasenotes getReleasenotes() 

    /**
     * Returns the value of field 'text'.
     * 
     * @return the value of field 'text'.
     */
    public java.lang.String getText()
    {
        return this._text;
    } //-- java.lang.String getText() 

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
     * Sets the value of field 'acknowledgment'.
     * 
     * @param acknowledgment the value of field 'acknowledgment'.
     */
    public void setAcknowledgment(java.lang.String acknowledgment)
    {
        this._acknowledgment = acknowledgment;
    } //-- void setAcknowledgment(java.lang.String) 

    /**
     * Sets the value of field 'compilation'.
     * 
     * @param compilation the value of field 'compilation'.
     */
    public void setCompilation(fr.jmmc.mcs.gui.castor.Compilation compilation)
    {
        this._compilation = compilation;
    } //-- void setCompilation(fr.jmmc.mcs.gui.castor.Compilation) 

    /**
     * Sets the value of field 'dependences'.
     * 
     * @param dependences the value of field 'dependences'.
     */
    public void setDependences(fr.jmmc.mcs.gui.castor.Dependences dependences)
    {
        this._dependences = dependences;
    } //-- void setDependences(fr.jmmc.mcs.gui.castor.Dependences) 

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
     * Sets the value of field 'menubar'.
     * 
     * @param menubar the value of field 'menubar'.
     */
    public void setMenubar(fr.jmmc.mcs.gui.castor.Menubar menubar)
    {
        this._menubar = menubar;
    } //-- void setMenubar(fr.jmmc.mcs.gui.castor.Menubar) 

    /**
     * Sets the value of field 'program'.
     * 
     * @param program the value of field 'program'.
     */
    public void setProgram(fr.jmmc.mcs.gui.castor.Program program)
    {
        this._program = program;
    } //-- void setProgram(fr.jmmc.mcs.gui.castor.Program) 

    /**
     * Sets the value of field 'releasenotes'.
     * 
     * @param releasenotes the value of field 'releasenotes'.
     */
    public void setReleasenotes(fr.jmmc.mcs.gui.castor.Releasenotes releasenotes)
    {
        this._releasenotes = releasenotes;
    } //-- void setReleasenotes(fr.jmmc.mcs.gui.castor.Releasenotes) 

    /**
     * Sets the value of field 'text'.
     * 
     * @param text the value of field 'text'.
     */
    public void setText(java.lang.String text)
    {
        this._text = text;
    } //-- void setText(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static fr.jmmc.mcs.gui.castor.ApplicationData unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.jmmc.mcs.gui.castor.ApplicationData) Unmarshaller.unmarshal(fr.jmmc.mcs.gui.castor.ApplicationData.class, reader);
    } //-- fr.jmmc.mcs.gui.castor.ApplicationData unmarshal(java.io.Reader) 

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
