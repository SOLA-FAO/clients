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
package org.sola.clients.beans.administrative;

import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.BaUnitRelTypeBean;
import org.sola.webservices.transferobjects.administrative.RelatedBaUnitInfoTO;

/** 
 * Contains properties and methods to manage <b>required_relationship_baunit</b> 
 * object of the domain model. Could be populated from the {@link RelatedBaUnitInfoTO} object.
 */
public class RelatedBaUnitInfoBean extends AbstractVersionedBean {
    public static final String RELATION_CODE_PROPERTY = "relationCode";
    public static final String BA_UNIT_REL_TYPE_PROPERTY = "baUnitRelType";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String RELATED_BA_UNIT_ID_PROPERTY = "relatedBaUnitId";
    public static final String RELATED_BA_UNIT_PROPERTY = "relatedBaUnit";
    
    private String baUnitId;
    private String relatedBaUnitId;
    private BaUnitRelTypeBean baUnitRelType;
    private BaUnitSummaryBean relatedBaUnit;
    
    public RelatedBaUnitInfoBean(){
        super();
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        String oldValue = this.baUnitId;
        this.baUnitId = baUnitId;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, oldValue, this.baUnitId);
    }

    public BaUnitSummaryBean getRelatedBaUnit() {
        return relatedBaUnit;
    }

    public void setRelatedBaUnit(BaUnitSummaryBean relatedBaUnit) {
        BaUnitSummaryBean oldValue = this.relatedBaUnit;
        this.relatedBaUnit = relatedBaUnit;
        propertySupport.firePropertyChange(RELATED_BA_UNIT_PROPERTY, oldValue, this.relatedBaUnit);
    }

    public String getRelatedBaUnitId() {
        return relatedBaUnitId;
    }

    public void setRelatedBaUnitId(String relatedBaUnitId) {
        String oldValue = this.relatedBaUnitId;
        this.relatedBaUnitId = relatedBaUnitId;
        propertySupport.firePropertyChange(RELATED_BA_UNIT_ID_PROPERTY, oldValue, this.relatedBaUnitId);
    }

    public String getRelationCode() {
        if (getBaUnitRelType() != null) {
            return getBaUnitRelType().getCode();
        } else {
            return null;
        }
    }
   
    public void setRelationCode(String relationCode) {
        String oldValue = null;
        if (getBaUnitRelType() != null) {
            oldValue = getBaUnitRelType().getCode();
        }
        setBaUnitRelType(CacheManager.getBeanByCode(
                CacheManager.getBaUnitRelTypes(), relationCode));
        propertySupport.firePropertyChange(RELATION_CODE_PROPERTY, oldValue, relationCode);
    }

    public BaUnitRelTypeBean getBaUnitRelType() {
        return baUnitRelType;
    }

    public void setBaUnitRelType(BaUnitRelTypeBean baUnitRelType) {
        this.baUnitRelType = baUnitRelType;
        if (this.baUnitRelType == null) {
            this.baUnitRelType = new BaUnitRelTypeBean();
        }
        this.setJointRefDataBean(this.baUnitRelType, baUnitRelType, BA_UNIT_REL_TYPE_PROPERTY);
    }
}
