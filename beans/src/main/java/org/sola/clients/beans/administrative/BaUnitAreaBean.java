/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.administrative;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitAreaTO;

/** 
 * Represents application property object. Could be populated from the
 * {@link ApplicationPropertyTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class BaUnitAreaBean extends AbstractIdBean {
    
    public static final String SIZE_PROPERTY = "size";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String CALCULATED_AREA_SIZE_PROPERTY = "calculatedAreaSize";
    
    @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class)
    private BigDecimal size;
    private String baUnitId;
    private String typeCode;
    private BigDecimal calculatedAreaSize;
    
    public BaUnitAreaBean() {
        super();
    }

    
    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String val) {
        String old = baUnitId;
        baUnitId = val;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, old, val);
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal val) {
        BigDecimal old = size;
        size = val;
        propertySupport.firePropertyChange(SIZE_PROPERTY, old, val);
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String val) {
        String old = typeCode;
        typeCode = val;
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, old, val);
    }
    
    public BigDecimal getCalculatedAreaSize() {
        return calculatedAreaSize;
    }

    public void setCalculatedAreaSize(BigDecimal val) {
        BigDecimal old = calculatedAreaSize;
        calculatedAreaSize = val;
        propertySupport.firePropertyChange(CALCULATED_AREA_SIZE_PROPERTY, old, val);
    }
    
     public boolean createBaUnitArea(String baUnitId) {
        
        BaUnitAreaTO baUnitArea = TypeConverters.BeanToTrasferObject(this, BaUnitAreaTO.class);
        baUnitArea = WSManager.getInstance().getAdministrative().createBaUnitArea(baUnitId, baUnitArea);
        TypeConverters.TransferObjectToBean(baUnitArea, BaUnitAreaBean.class, this);
        return true;
    }
}
