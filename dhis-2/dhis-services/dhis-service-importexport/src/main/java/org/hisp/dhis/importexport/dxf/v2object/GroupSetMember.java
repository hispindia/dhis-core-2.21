//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.10.30 at 03:44:18 PM GMT 
//


package org.hisp.dhis.importexport.dxf.v2object;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="groupSet" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="organisationUnitGroup" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "groupSet",
    "organisationUnitGroup"
})
@XmlRootElement(name = "groupSetMember")
public class GroupSetMember {

    @XmlElement(required = true)
    protected BigInteger groupSet;
    @XmlElement(required = true)
    protected BigInteger organisationUnitGroup;

    /**
     * Gets the value of the groupSet property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGroupSet() {
        return groupSet;
    }

    /**
     * Sets the value of the groupSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGroupSet(BigInteger value) {
        this.groupSet = value;
    }

    /**
     * Gets the value of the organisationUnitGroup property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOrganisationUnitGroup() {
        return organisationUnitGroup;
    }

    /**
     * Sets the value of the organisationUnitGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOrganisationUnitGroup(BigInteger value) {
        this.organisationUnitGroup = value;
    }

}
