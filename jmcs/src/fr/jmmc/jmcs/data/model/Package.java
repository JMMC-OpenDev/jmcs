
package fr.jmmc.jmcs.data.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Package complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Package">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="link" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="jars" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="license" use="required" type="{}License" />
 *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Package")
public class Package {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description", required = true)
    protected String description;
    @XmlAttribute(name = "link", required = true)
    protected String link;
    @XmlAttribute(name = "jars", required = true)
    protected String jars;
    @XmlAttribute(name = "license", required = true)
    protected License license;
    @XmlAttribute(name = "file")
    protected String file;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    public boolean isSetName() {
        return (this.name!= null);
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public boolean isSetDescription() {
        return (this.description!= null);
    }

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLink(String value) {
        this.link = value;
    }

    public boolean isSetLink() {
        return (this.link!= null);
    }

    /**
     * Gets the value of the jars property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJars() {
        return jars;
    }

    /**
     * Sets the value of the jars property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJars(String value) {
        this.jars = value;
    }

    public boolean isSetJars() {
        return (this.jars!= null);
    }

    /**
     * Gets the value of the license property.
     * 
     * @return
     *     possible object is
     *     {@link License }
     *     
     */
    public License getLicense() {
        return license;
    }

    /**
     * Sets the value of the license property.
     * 
     * @param value
     *     allowed object is
     *     {@link License }
     *     
     */
    public void setLicense(License value) {
        this.license = value;
    }

    public boolean isSetLicense() {
        return (this.license!= null);
    }

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile(String value) {
        this.file = value;
    }

    public boolean isSetFile() {
        return (this.file!= null);
    }

}
