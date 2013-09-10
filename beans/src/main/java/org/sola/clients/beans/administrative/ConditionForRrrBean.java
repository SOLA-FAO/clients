/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
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

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.administrative.validation.LeaseConditionValidationGroup;
import org.sola.clients.beans.administrative.validation.LeaseCustomConditionValidationGroup;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.ConditionTypeBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.administrative.ConditionForRrrTO;

/**
 * Contains properties and methods to manage <b>condition_for_rrr</b>
 * object of the domain model. Could be populated from the {@link ConditionForRrrTO}
 * object.
 */
public class ConditionForRrrBean extends AbstractIdBean {

    public static final String CONDITION_TYPE_PROPERTY = "conditionType";
    public static final String CONDITION_CODE_PROPERTY = "conditionCode";
    public static final String CUSTOM_CONDITION_TEXT_PROPERTY = "customConditionText";
    public static final String CONDITION_TEXT_PROPERTY = "conditionText";
    public static final String CONDITION_QUANTITY_PROPERTY = "conditionQuantity";
    public static final String CONDITION_UNIT_PROPERTY = "conditionUnit";
    
    public static final String RRR_ID_PROPERTY = "rrrId";
    
    @NotNull(groups=LeaseConditionValidationGroup.class, 
            message=ClientMessage.BAUNIT_CONDITION_EMPTY, payload=Localized.class)
    private ConditionTypeBean conditionType;
    
    @NotEmpty(groups=LeaseCustomConditionValidationGroup.class, 
            message=ClientMessage.BAUNIT_CUSTOM_CONDITION_EMTY, payload=Localized.class)
    private String customConditionText;
    private String rrrId;
    private int conditionQuantity;
    private String conditionUnit;
    
    public ConditionForRrrBean() {
        super();
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        String oldValue = this.rrrId;
        this.rrrId = rrrId;
        propertySupport.firePropertyChange(RRR_ID_PROPERTY, oldValue, this.rrrId);
    }

    public String getCustomConditionText() {
        return customConditionText;
    }

    public int getConditionQuantity() {
        return conditionQuantity;
    }

    public void setConditionQuantity(int conditionQuantity) {
        int oldValue = this.conditionQuantity;
        this.conditionQuantity = conditionQuantity;
        propertySupport.firePropertyChange(CONDITION_QUANTITY_PROPERTY, oldValue, this.conditionQuantity);
    }

    public String getConditionUnit() {
        return conditionUnit;
    }

    public void setConditionUnit(String conditionUnit) {
        String oldValue = this.conditionUnit;
        this.conditionUnit = conditionUnit;
        propertySupport.firePropertyChange(CONDITION_UNIT_PROPERTY, oldValue, this.conditionUnit);
    }

    public void setCustomConditionText(String customConditionText) {
        String oldValue = this.customConditionText;
        this.customConditionText = customConditionText;
        propertySupport.firePropertyChange(CUSTOM_CONDITION_TEXT_PROPERTY, oldValue, this.customConditionText);
        propertySupport.firePropertyChange(CONDITION_TEXT_PROPERTY, null, getConditionText());
    }

    /** Returns true if lease condition is custom. */
    public boolean isCustomCondition(){
        if(getConditionCode()==null || getConditionCode().isEmpty()){
            return true;
        }
        return false;
    }
    
    /** 
     * Returns either standard or custom condition text. 
     * If condition code is present the description text of {@link ConditionTypeBean} will be returned.
     * If there is no code, custom condition text will be returned instead.
     */
    public String getConditionText(){
        if(isCustomCondition()){
            return getCustomConditionText();
        } else {
            return getConditionType().getDescription();
        }
    }
    
    public String getConditionCode() {
        if (getConditionType() == null) {
            return null;
        }
        return getConditionType().getCode();
    }

    public void setConditionCode(String conditionCode) {
        String oldValue = getConditionCode();
        setConditionType(CacheManager.getBeanByCode(CacheManager.getConditionTypes(), conditionCode));
        propertySupport.firePropertyChange(CONDITION_CODE_PROPERTY, oldValue, conditionCode);
    }

    public ConditionTypeBean getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionTypeBean conditionType) {
        ConditionTypeBean oldValue = this.conditionType;
        this.conditionType = conditionType;
        propertySupport.firePropertyChange(CONDITION_TYPE_PROPERTY, oldValue, this.conditionType);
        propertySupport.firePropertyChange(CONDITION_TEXT_PROPERTY, null, getConditionText());
    }
}
