
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
 *     &lt;enumeration value="Apache_v2"/>
 *     &lt;enumeration value="BSD"/>
 *     &lt;enumeration value="Eclipse_v1"/>
 *     &lt;enumeration value="GPL_v2"/>
 *     &lt;enumeration value="LGPL"/>
 *     &lt;enumeration value="LGPL_v2"/>
 *     &lt;enumeration value="LGPL_v2.1"/>
 *     &lt;enumeration value="MIT"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "License")
@XmlEnum
public enum License {

    @XmlEnumValue("Apache_v2")
    APACHE_V_2("Apache_v2"),
    BSD("BSD"),
    @XmlEnumValue("Eclipse_v1")
    ECLIPSE_V_1("Eclipse_v1"),
    @XmlEnumValue("GPL_v2")
    GPL_V_2("GPL_v2"),
    LGPL("LGPL"),
    @XmlEnumValue("LGPL_v2")
    LGPL_V_2("LGPL_v2"),
    @XmlEnumValue("LGPL_v2.1")
    LGPL_V_2_1("LGPL_v2.1"),
    MIT("MIT"),
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
