/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.cadastre;

import java.util.Date;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/** 
 * Contains properties and methods to manage <b>Cadastre</b> object of the 
 * domain model. Could be populated from the {@link CadastreObjectTO} object.
 */
public class CadastreObjectBean extends AbstractTransactionedBean {

    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String APPROVAL_DATETIME_PROPERTY = "approvalDatetime";
    public static final String HISTORIC_DATETIME_PROPERTY = "historicDatetime";
    public static final String SOURCE_REFERENCE_PROPERTY = "sourceReference";
    public static final String NAME_FIRSTPART_PROPERTY = "nameFirstpart";
    public static final String NAME_LASTPART_PROPERTY = "nameLastpart";
    public static final String CADASTRE_OBJECT_TYPE_PROPERTY = "cadastreObjectType";
    public static final String GEOM_POLYGON_PROPERTY = "geomPolygon";
    public static final String SELECTED_PROPERTY = "selected";
    
    private Date approvalDatetime;
    private Date historicDatetime;
    @Length(max = 100, message =  ClientMessage.CHECK_FIELD_INVALID_LENGTH_SRCREF, payload=Localized.class)
    private String sourceReference;
    @Length(max = 20, message =  ClientMessage.CHECK_FIELD_INVALID_LENGTH_FIRSTPART, payload=Localized.class)
    @NotEmpty(message =  ClientMessage.CHECK_NOTNULL_CADFIRSTPART, payload=Localized.class)
    private String nameFirstpart;
    @Length(max = 50, message =  ClientMessage.CHECK_FIELD_INVALID_LENGTH_LASTPART, payload=Localized.class)
    @NotEmpty(message =  ClientMessage.CHECK_NOTNULL_CADLASTPART, payload=Localized.class)
    private String nameLastpart;
    @NotNull(message =  ClientMessage.CHECK_NOTNULL_CADOBJTYPE, payload=Localized.class)
    private CadastreObjectTypeBean cadastreObjectType;
    private byte[] geomPolygon;
    private transient boolean selected;
    
    public CadastreObjectBean() {
        super();
    }

    public Date getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(Date approvalDatetime) {
        Date oldValue = approvalDatetime;
        this.approvalDatetime = approvalDatetime;
        propertySupport.firePropertyChange(APPROVAL_DATETIME_PROPERTY,
                oldValue, approvalDatetime);
    }

    public Date getHistoricDatetime() {
        return historicDatetime;
    }

    public void setHistoricDatetime(Date historicDatetime) {
        Date oldValue = historicDatetime;
        this.historicDatetime = historicDatetime;
        propertySupport.firePropertyChange(HISTORIC_DATETIME_PROPERTY,
                oldValue, historicDatetime);
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        String oldValue = nameFirstpart;
        this.nameFirstpart = nameFirstpart;
        propertySupport.firePropertyChange(NAME_FIRSTPART_PROPERTY,
                oldValue, nameFirstpart);
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        String oldValue = nameLastpart;
        this.nameLastpart = nameLastpart;
        propertySupport.firePropertyChange(NAME_LASTPART_PROPERTY,
                oldValue, nameLastpart);
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public void setSourceReference(String sourceReference) {
        String oldValue = sourceReference;
        this.sourceReference = sourceReference;
        propertySupport.firePropertyChange(SOURCE_REFERENCE_PROPERTY,
                oldValue, sourceReference);
    }

    public String getTypeCode() {
        if (cadastreObjectType != null) {
            return cadastreObjectType.getCode();
        } else {
            return null;
        }
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (cadastreObjectType != null) {
            oldValue = cadastreObjectType.getCode();
        }
        setCadastreObjectType(CacheManager.getBeanByCode(
                CacheManager.getCadastreObjectTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public CadastreObjectTypeBean getCadastreObjectType() {
        return cadastreObjectType;
    }

    public void setCadastreObjectType(CadastreObjectTypeBean cadastreObjectType) {
        if(this.cadastreObjectType==null){
            this.cadastreObjectType = new CadastreObjectTypeBean();
        }
        this.setJointRefDataBean(this.cadastreObjectType, cadastreObjectType, CADASTRE_OBJECT_TYPE_PROPERTY);
    }

    public byte[] getGeomPolygon() {
        return geomPolygon;
    }

    public void setGeomPolygon(byte[] geomPolygon) {
        byte[] old = this.geomPolygon;
        this.geomPolygon = geomPolygon;
        propertySupport.firePropertyChange(GEOM_POLYGON_PROPERTY, old, this.geomPolygon);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        propertySupport.firePropertyChange(SELECTED_PROPERTY, oldValue, this.selected);
    }
    
    @Override
    public String toString() {
        String result = "";
        if(nameFirstpart!=null){
            result = nameFirstpart;
            if(nameLastpart!=null){
                result += " / " + nameLastpart;
            }
        }
        return result;
    }
    
}
