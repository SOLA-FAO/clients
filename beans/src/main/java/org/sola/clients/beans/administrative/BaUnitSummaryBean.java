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

import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.webservices.transferobjects.administrative.BaUnitBasicTO;

/** 
 * Represents summary object of the {@link BaUnitBean}. 
 * Could be populated from the {@link BaUnitBasicTO} object.<br />
 * For more information see data dictionary <b>Administrative</b> schema.
 */
public class BaUnitSummaryBean extends AbstractTransactionedBean {
    public static final String BA_UNIT_TYPE_PROPERTY = "baUnitType";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String NAME_PROPERTY = "name";
    public static final String NAME_FIRSTPART_PROPERTY = "nameFirstpart";
    public static final String NAME_LASTPART_PROPERTY = "nameLastpart";
    
    private String name;
    //@NotEmpty(message = ClientMessage.CHECK_NOTNULL_FIRSTPART, payload=Localized.class)
    private String nameFirstpart;
    //@NotEmpty(message = ClientMessage.CHECK_NOTNULL_FIRSTPART, payload=Localized.class)
    private String nameLastpart;
    private BaUnitTypeBean baUnitType;
    
    public BaUnitSummaryBean(){
        super();
    }
    
    public String getTypeCode() {
        if (baUnitType != null) {
            return baUnitType.getCode();
        } else {
            return null;
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        propertySupport.firePropertyChange(NAME_PROPERTY, oldValue, name);
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        String oldValue = this.nameFirstpart;
        this.nameFirstpart = nameFirstpart;
        propertySupport.firePropertyChange(NAME_FIRSTPART_PROPERTY, oldValue, nameFirstpart);
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        String oldValue = this.nameLastpart;
        this.nameLastpart = nameLastpart;
        propertySupport.firePropertyChange(NAME_LASTPART_PROPERTY, oldValue, nameLastpart);
    }
    
    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (baUnitType != null) {
            oldValue = baUnitType.getCode();
        }
        setBaUnitType(CacheManager.getBeanByCode(
                CacheManager.getBaUnitTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public BaUnitTypeBean getBaUnitType() {
        return baUnitType;
    }

    public void setBaUnitType(BaUnitTypeBean baUnitType) {
        if (this.baUnitType == null) {
            this.baUnitType = new BaUnitTypeBean();
        }
        this.setJointRefDataBean(this.baUnitType, baUnitType, BA_UNIT_TYPE_PROPERTY);
    }
}
