/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
