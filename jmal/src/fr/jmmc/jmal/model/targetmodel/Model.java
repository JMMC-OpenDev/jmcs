
package fr.jmmc.jmal.model.targetmodel;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import fr.jmmc.jmal.model.CloneableObject;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.jmmc.fr/jmcs/models/0.1}model" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://www.jmmc.fr/jmcs/models/0.1}parameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.jmmc.fr/jmcs/models/0.1}parameterLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.jmmc.fr/jmcs/models/0.1}ModelAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "models",
    "desc",
    "parameters",
    "parameterLinks"
})
@XmlRootElement(name = "model")
public class Model
    extends CloneableObject
{

    @XmlElement(name = "model", namespace = "http://www.jmmc.fr/jmcs/models/0.1")
    protected List<Model> models;
    protected String desc;
    @XmlElement(name = "parameter", namespace = "http://www.jmmc.fr/jmcs/models/0.1")
    protected List<Parameter> parameters;
    @XmlElement(name = "parameterLink", namespace = "http://www.jmmc.fr/jmcs/models/0.1")
    protected List<ParameterLink> parameterLinks;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected String type;

    /**
     * Gets the value of the models property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the models property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModels().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Model }
     * 
     * 
     */
    public List<Model> getModels() {
        if (models == null) {
            models = new ArrayList<Model>();
        }
        return this.models;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Parameter }
     * 
     * 
     */
    public List<Parameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<Parameter>();
        }
        return this.parameters;
    }

    /**
     * Gets the value of the parameterLinks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameterLinks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameterLinks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParameterLink }
     * 
     * 
     */
    public List<ParameterLink> getParameterLinks() {
        if (parameterLinks == null) {
            parameterLinks = new ArrayList<ParameterLink>();
        }
        return this.parameterLinks;
    }

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

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }
    
//--simple--preserve
    /**
     * Define the name and type to the same value
     * @param value value to set
     */
    public void setNameAndType(final String value) {
        this.name = value;
        this.type = value;
    }

    @Override
    public final String toString() {
        return this.name + " [" + this.type + "]";
    }

    /**
     * Return the parameter of the given type
     * @param type type of the parameter
     * @return parameter or null if the parameter type is not present in this model
     */
    public final Parameter getParameter(final String type) {
        for (Parameter p : getParameters()) {
            if (type.equals(p.getType())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Return a deep "copy" of this instance
     * @return deep "copy" of this instance
     */
    @Override
    public final Object clone() {
        final Model copy = (Model) super.clone();

        // Deep copy of models :
        if (copy.models != null) {
          copy.models = CloneableObject.deepCopyList(copy.models);
        }

        // Deep copy of parameters :
        if (copy.parameters != null) {
          copy.parameters = CloneableObject.deepCopyList(copy.parameters);
        }

        // Clear parameter links as it is not supported by Aspro :
        // note : parameter links have a reference to one Parameter instance (idref)
        // so clone() implementation is harder ...
        copy.parameterLinks = null;

        return copy;
    }
//--simple--preserve

}
