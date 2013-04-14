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
package org.sola.clients.beans.referencedata;

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaCodeList;

/**
 * Holds the list of {@link LeaseConditionBean} objects and used to bound the
 * data in the combobox on the forms.
 */
public class LeaseConditionListBean extends AbstractBindingListBean {
    
    public static final String SELECTED_LEASE_CONDITION_PROPERTY = "selectedLeaseCondition";
    private SolaCodeList<LeaseConditionBean> leaseConditionList;
    private LeaseConditionBean selectedLeaseCondition;
    
    public LeaseConditionListBean(){
        this(false);
    }

    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public LeaseConditionListBean(boolean createDummy) {
        this(createDummy, (String) null);
    }
    
    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public LeaseConditionListBean(boolean createDummy, String ... excludedCodes) {
        super();
        leaseConditionList = new SolaCodeList<LeaseConditionBean>(excludedCodes);
        loadList(createDummy);
    }
    
    /** 
     * Loads list of {@link LeaseConditionBean}.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public final void loadList(boolean createDummy) {
        loadCodeList(LeaseConditionBean.class, leaseConditionList, 
                CacheManager.getLeaseConditions(), createDummy);
    }
    
    public ObservableList<LeaseConditionBean> getLeaseConditionList() {
        return leaseConditionList.getFilteredList();
    }

    public void setExcludedCodes(String ... codes){
        leaseConditionList.setExcludedCodes(codes);
    }
    
    public LeaseConditionBean getSelectedLeaseCondition() {
        return selectedLeaseCondition;
    }

    public void setSelectedLeaseCondition(LeaseConditionBean selectedLeaseCondition) {
        this.selectedLeaseCondition = selectedLeaseCondition;
    }
}
