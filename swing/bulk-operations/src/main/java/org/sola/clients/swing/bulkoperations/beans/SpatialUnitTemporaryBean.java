/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.math.BigDecimal;
import org.sola.clients.beans.AbstractIdBean;

/**
 * A class that defines the spatial unit after being converted from features from the source.
 * 
 * @author Elton Manoku
 */
public class SpatialUnitTemporaryBean extends AbstractIdBean{

    private String typeCode;
    private String cadastreObjectTypeCode;
    private String nameFirstpart;
    private String nameLastpart;
    private String transactionId;
    private byte[] geom;
    private BigDecimal officialArea;
    private String label;
    
    public SpatialUnitTemporaryBean(){
        super();
    }

    public String getCadastreObjectTypeCode() {
        return cadastreObjectTypeCode;
    }

    public void setCadastreObjectTypeCode(String cadastreObjectTypeCode) {
        this.cadastreObjectTypeCode = cadastreObjectTypeCode;
    }

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        this.nameFirstpart = nameFirstpart;
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        this.nameLastpart = nameLastpart;
    }

    public BigDecimal getOfficialArea() {
        return officialArea;
    }

    public void setOfficialArea(BigDecimal officialArea) {
        this.officialArea = officialArea;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
    
    
    
}
