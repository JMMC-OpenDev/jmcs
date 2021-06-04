
package fr.jmmc.jmal.model.targetmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import fr.jmmc.jmal.model.CloneableObject;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="parameterRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "parameterLink")
public class ParameterLink
    extends CloneableObject
{

    @XmlAttribute(name = "parameterRef", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Parameter parameterRef;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the parameterRef property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Parameter getParameterRef() {
        return parameterRef;
    }

    /**
     * Sets the value of the parameterRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setParameterRef(Parameter value) {
        this.parameterRef = value;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParameterLink other = (ParameterLink) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if (this.parameterRef != other.parameterRef && (this.parameterRef == null || !this.parameterRef.equals(other.parameterRef))) {
            return false;
        }
        return true;
    }

//--simple--preserve

}
