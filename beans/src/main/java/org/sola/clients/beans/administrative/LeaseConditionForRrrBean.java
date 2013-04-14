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
import org.sola.clients.beans.referencedata.LeaseConditionBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.administrative.LeaseConditionForRrrTO;

/**
 * Contains properties and methods to manage <b>lease_condition_for_rrr</b>
 * object of the domain model. Could be populated from the {@link LeaseConditionForRrrTO}
 * object.
 */
public class LeaseConditionForRrrBean extends AbstractIdBean {

    public static final String LEASE_CONDITION_PROPERTY = "leaseCondition";
    public static final String LEASE_CONDITION_CODE_PROPERTY = "leaseConditionCode";
    public static final String CUSTOM_CONDITION_TEXT_PROPERTY = "customConditionText";
    public static final String CONDITION_TEXT_PROPERTY = "conditionText";
    
    public static final String RRR_ID_PROPERTY = "rrrId";
    
    @NotNull(groups=LeaseConditionValidationGroup.class, 
            message=ClientMessage.BAUNIT_LEASE_CONDITION_EMPTY, payload=Localized.class)
    private LeaseConditionBean leaseCondition;
    
    @NotEmpty(groups=LeaseCustomConditionValidationGroup.class, 
            message=ClientMessage.BAUNIT_CUSTOM_LEASE_CONDITION_EMTY, payload=Localized.class)
    private String customConditionText;
    private String rrrId;
    
    public LeaseConditionForRrrBean() {
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

    public void setCustomConditionText(String customConditionText) {
        String oldValue = this.customConditionText;
        this.customConditionText = customConditionText;
        propertySupport.firePropertyChange(CUSTOM_CONDITION_TEXT_PROPERTY, oldValue, this.customConditionText);
        propertySupport.firePropertyChange(CONDITION_TEXT_PROPERTY, null, getConditionText());
    }

    /** Returns true if lease condition is custom. */
    public boolean isCustomCondition(){
        if(getLeaseConditionCode()==null || getLeaseConditionCode().isEmpty()){
            return true;
        }
        return false;
    }
    
    /** 
     * Returns either standard or custom lease condition text. 
     * If lease condition code is present the description text of {@link LeaseConditionBean} will be returned.
     * If there is no code, custom condition text will be returned instead.
     */
    public String getConditionText(){
        if(isCustomCondition()){
            return getCustomConditionText();
        } else {
            return getLeaseCondition().getDescription();
        }
    }
    
    public String getLeaseConditionCode() {
        if (getLeaseCondition() == null) {
            return null;
        }
        return getLeaseCondition().getCode();
    }

    public void setLeaseConditionCode(String leaseConditionCode) {
        String oldValue = getLeaseConditionCode();
        setLeaseCondition(CacheManager.getBeanByCode(CacheManager.getLeaseConditions(), leaseConditionCode));
        propertySupport.firePropertyChange(LEASE_CONDITION_CODE_PROPERTY, oldValue, leaseConditionCode);
    }

    public LeaseConditionBean getLeaseCondition() {
        return leaseCondition;
    }

    public void setLeaseCondition(LeaseConditionBean leaseCondition) {
        LeaseConditionBean oldValue = this.leaseCondition;
        this.leaseCondition = leaseCondition;
        propertySupport.firePropertyChange(LEASE_CONDITION_PROPERTY, oldValue, this.leaseCondition);
        propertySupport.firePropertyChange(CONDITION_TEXT_PROPERTY, null, getConditionText());
    }
}
