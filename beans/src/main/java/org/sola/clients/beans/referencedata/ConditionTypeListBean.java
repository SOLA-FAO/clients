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
 * Holds the list of {@link ConditionTypeBean} objects and used to bound the
 * data in the combobox on the forms.
 */
public class ConditionTypeListBean extends AbstractBindingListBean {
    
    public static final String SELECTED_CONDITION_TYPE_PROPERTY = "selectedConditionType";
    private SolaCodeList<ConditionTypeBean> leaseConditionList;
    private ConditionTypeBean selectedConditionType;
    
    public ConditionTypeListBean(){
        this(false);
    }

    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public ConditionTypeListBean(boolean createDummy) {
        this(createDummy, (String) null);
    }
    
    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public ConditionTypeListBean(boolean createDummy, String ... excludedCodes) {
        super();
        leaseConditionList = new SolaCodeList<ConditionTypeBean>(excludedCodes);
        loadList(createDummy);
    }
    
    /** 
     * Loads list of {@link LeaseConditionBean}.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public final void loadList(boolean createDummy) {
        loadCodeList(ConditionTypeBean.class, leaseConditionList, 
                CacheManager.getConditionTypes(), createDummy);
    }
    
    public ObservableList<ConditionTypeBean> getLeaseConditionList() {
        return leaseConditionList.getFilteredList();
    }

    public void setExcludedCodes(String ... codes){
        leaseConditionList.setExcludedCodes(codes);
    }
    
    public ConditionTypeBean getSelectedConditionType() {
        return selectedConditionType;
    }

    public void setSelectedConditionType(ConditionTypeBean selectedConditionType) {
        this.selectedConditionType = selectedConditionType;
        propertySupport.firePropertyChange(SELECTED_CONDITION_TYPE_PROPERTY, null, this.selectedConditionType);
    }
}
