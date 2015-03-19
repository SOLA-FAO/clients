/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.system;

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.BrTechnicalTypeBean;
import org.sola.clients.beans.referencedata.BrValidationTargetTypeBean;

public class BrSearchParamsBean extends AbstractBindingBean {
    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public static final String TECHNICAL_TYPE_CODE_PROPERTY = "technicalTypeCode";
    public static final String BR_TECHNICAL_TYPE_PROPERTY = "brTechnicalType";
    public static final String TARGET_CODE_PROPERTY = "targetCode";
    public static final String BR_VALIDATION_TARGET_TYPE_PROPERTY = "brValidationTargetType";
    
    private String displayName;
    private BrTechnicalTypeBean brTechnicalType;
    private BrValidationTargetTypeBean brValidationTargetType;
    
    public BrSearchParamsBean(){
        super();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        String oldValue = this.displayName;
        this.displayName = displayName;
        propertySupport.firePropertyChange(DISPLAY_NAME_PROPERTY, oldValue, this.displayName);
    }

    public String getTargetCode() {
        return getBrValidationTargetType().getCode();
    }

    public void setTargetCode(String targetCode) {
        String oldValue = getBrValidationTargetType().getCode();
        setBrValidationTargetType(CacheManager.getBeanByCode(CacheManager.getBrValidationTargetTypes(), targetCode));
        propertySupport.firePropertyChange(TARGET_CODE_PROPERTY, oldValue, targetCode);
    }

    public BrValidationTargetTypeBean getBrValidationTargetType() {
        if(brValidationTargetType == null){
            brValidationTargetType = new BrValidationTargetTypeBean();
        }
        return brValidationTargetType;
    }

    public void setBrValidationTargetType(BrValidationTargetTypeBean brValidationTargetType) {
        this.setJointRefDataBean(getBrValidationTargetType(), brValidationTargetType, BR_VALIDATION_TARGET_TYPE_PROPERTY);
    }
    
    public BrTechnicalTypeBean getBrTechnicalType() {
        if(brTechnicalType == null){
            brTechnicalType = new BrTechnicalTypeBean();
        }
        return brTechnicalType;
    }

    public void setBrTechnicalType(BrTechnicalTypeBean brTechnicalType) {
        this.setJointRefDataBean(getBrTechnicalType(), brTechnicalType, BR_TECHNICAL_TYPE_PROPERTY);
    }
    
    public String getTechnicalTypeCode() {
        return getBrTechnicalType().getCode();
    }

    public void setTechnicalTypeCode(String technicalTypeCode) {
        String oldValue = getBrTechnicalType().getCode();
        setBrTechnicalType(CacheManager.getBeanByCode(CacheManager.getBrTechnicalTypes(), technicalTypeCode));
        propertySupport.firePropertyChange(TECHNICAL_TYPE_CODE_PROPERTY, oldValue, technicalTypeCode);
    }
}
