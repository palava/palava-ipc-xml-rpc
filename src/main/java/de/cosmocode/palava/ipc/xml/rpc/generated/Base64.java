//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.05.19 at 12:51:38 AM CEST 
//


package de.cosmocode.palava.ipc.xml.rpc.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for base64 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="base64">
 *   &lt;complexContent>
 *     &lt;extension base="{}value">
 *       &lt;sequence>
 *         &lt;element name="base64" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "base64", propOrder = {
    "base64"
})
public class Base64
    extends Value
{

    @XmlElement(required = true)
    protected byte[] base64;

    /**
     * Gets the value of the base64 property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBase64() {
        return base64;
    }

    /**
     * Sets the value of the base64 property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBase64(byte[] value) {
        this.base64 = ((byte[]) value);
    }

}
