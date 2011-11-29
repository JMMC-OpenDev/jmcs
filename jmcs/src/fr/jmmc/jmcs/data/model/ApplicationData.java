
package fr.jmmc.jmcs.data.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 jMCS application meta data
 *             
 * 
 * <p>Java class for ApplicationData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="program" type="{}Program"/>
 *         &lt;element name="compilation" type="{}Compilation"/>
 *         &lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dependences" type="{}Dependences" minOccurs="0"/>
 *         &lt;element name="menubar" type="{}Menubar" minOccurs="0"/>
 *         &lt;element name="releasenotes" type="{}ReleaseNotes"/>
 *         &lt;element name="acknowledgment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="link" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="iconlink" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationData", propOrder = {
    "program",
    "compilation",
    "text",
    "dependences",
    "menubar",
    "releasenotes",
    "acknowledgment"
})
@XmlRootElement(name = "ApplicationData")
public class ApplicationData {

    @XmlElement(required = true)
    protected Program program;
    @XmlElement(required = true)
    protected Compilation compilation;
    protected String text;
    protected Dependences dependences;
    protected Menubar menubar;
    @XmlElement(required = true)
    protected ReleaseNotes releasenotes;
    protected String acknowledgment;
    @XmlAttribute(name = "link", required = true)
    protected String link;
    @XmlAttribute(name = "iconlink", required = true)
    protected String iconlink;

    /**
     * Gets the value of the program property.
     * 
     * @return
     *     possible object is
     *     {@link Program }
     *     
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Sets the value of the program property.
     * 
     * @param value
     *     allowed object is
     *     {@link Program }
     *     
     */
    public void setProgram(Program value) {
        this.program = value;
    }

    /**
     * Gets the value of the compilation property.
     * 
     * @return
     *     possible object is
     *     {@link Compilation }
     *     
     */
    public Compilation getCompilation() {
        return compilation;
    }

    /**
     * Sets the value of the compilation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Compilation }
     *     
     */
    public void setCompilation(Compilation value) {
        this.compilation = value;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the dependences property.
     * 
     * @return
     *     possible object is
     *     {@link Dependences }
     *     
     */
    public Dependences getDependences() {
        return dependences;
    }

    /**
     * Sets the value of the dependences property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dependences }
     *     
     */
    public void setDependences(Dependences value) {
        this.dependences = value;
    }

    /**
     * Gets the value of the menubar property.
     * 
     * @return
     *     possible object is
     *     {@link Menubar }
     *     
     */
    public Menubar getMenubar() {
        return menubar;
    }

    /**
     * Sets the value of the menubar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Menubar }
     *     
     */
    public void setMenubar(Menubar value) {
        this.menubar = value;
    }

    /**
     * Gets the value of the releasenotes property.
     * 
     * @return
     *     possible object is
     *     {@link ReleaseNotes }
     *     
     */
    public ReleaseNotes getReleasenotes() {
        return releasenotes;
    }

    /**
     * Sets the value of the releasenotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReleaseNotes }
     *     
     */
    public void setReleasenotes(ReleaseNotes value) {
        this.releasenotes = value;
    }

    /**
     * Gets the value of the acknowledgment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcknowledgment() {
        return acknowledgment;
    }

    /**
     * Sets the value of the acknowledgment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcknowledgment(String value) {
        this.acknowledgment = value;
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

    /**
     * Gets the value of the iconlink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIconlink() {
        return iconlink;
    }

    /**
     * Sets the value of the iconlink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIconlink(String value) {
        this.iconlink = value;
    }

}
