
package fr.jmmc.jmcs.data.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for License.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="License">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AFL v2.1"/>
 *     &lt;enumeration value="Apache v2"/>
 *     &lt;enumeration value="BSD"/>
 *     &lt;enumeration value="Eclipse v1"/>
 *     &lt;enumeration value="GPL v2"/>
 *     &lt;enumeration value="GPL v3"/>
 *     &lt;enumeration value="LGPL"/>
 *     &lt;enumeration value="LGPL v2"/>
 *     &lt;enumeration value="LGPL v2.1"/>
 *     &lt;enumeration value="MIT"/>
 *     &lt;enumeration value="Proprietary"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "License")
@XmlEnum
public enum License {

    @XmlEnumValue("AFL v2.1")
    AFL_V_2_1("AFL v2.1"),
    @XmlEnumValue("Apache v2")
    APACHE_V_2("Apache v2"),
    BSD("BSD"),
    @XmlEnumValue("Eclipse v1")
    ECLIPSE_V_1("Eclipse v1"),
    @XmlEnumValue("GPL v2")
    GPL_V_2("GPL v2"),
    @XmlEnumValue("GPL v3")
    GPL_V_3("GPL v3"),
    LGPL("LGPL"),
    @XmlEnumValue("LGPL v2")
    LGPL_V_2("LGPL v2"),
    @XmlEnumValue("LGPL v2.1")
    LGPL_V_2_1("LGPL v2.1"),
    MIT("MIT"),
    @XmlEnumValue("Proprietary")
    PROPRIETARY("Proprietary"),
    UNKNOWN("UNKNOWN");
    private final String value;

    License(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static License fromValue(String v) {
        for (License c: License.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
