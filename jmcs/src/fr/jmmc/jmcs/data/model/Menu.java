
package fr.jmmc.jmcs.data.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Menu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Menu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="menu" type="{}Menu" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="classpath" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="checkbox" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="radiogroup" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="accelerator" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Menu", propOrder = {
    "menus"
})
public class Menu {

    @XmlElement(name = "menu")
    protected List<Menu> menus;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "classpath", required = true)
    protected String classpath;
    @XmlAttribute(name = "action", required = true)
    protected String action;
    @XmlAttribute(name = "checkbox")
    protected String checkbox;
    @XmlAttribute(name = "radiogroup")
    protected String radiogroup;
    @XmlAttribute(name = "accelerator")
    protected String accelerator;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "icon")
    protected String icon;

    /**
     * Gets the value of the menus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the menus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMenus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Menu }
     * 
     * 
     */
    public List<Menu> getMenus() {
        if (menus == null) {
            menus = new ArrayList<Menu>();
        }
        return this.menus;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the classpath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClasspath() {
        return classpath;
    }

    /**
     * Sets the value of the classpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClasspath(String value) {
        this.classpath = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the checkbox property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckbox() {
        return checkbox;
    }

    /**
     * Sets the value of the checkbox property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckbox(String value) {
        this.checkbox = value;
    }

    /**
     * Gets the value of the radiogroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRadiogroup() {
        return radiogroup;
    }

    /**
     * Sets the value of the radiogroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRadiogroup(String value) {
        this.radiogroup = value;
    }

    /**
     * Gets the value of the accelerator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccelerator() {
        return accelerator;
    }

    /**
     * Sets the value of the accelerator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccelerator(String value) {
        this.accelerator = value;
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

    /**
     * Gets the value of the icon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Sets the value of the icon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcon(String value) {
        this.icon = value;
    }

}
