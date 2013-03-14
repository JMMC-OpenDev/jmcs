
package fr.jmmc.jmcs.data.app.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Compilation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Compilation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="date" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="compiler" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Compilation")
public class Compilation {

    @XmlAttribute(name = "date", required = true)
    protected String date;
    @XmlAttribute(name = "compiler")
    protected String compiler;

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    public boolean isSetDate() {
        return (this.date!= null);
    }

    /**
     * Gets the value of the compiler property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompiler() {
        return compiler;
    }

    /**
     * Sets the value of the compiler property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompiler(String value) {
        this.compiler = value;
    }

    public boolean isSetCompiler() {
        return (this.compiler!= null);
    }

}
