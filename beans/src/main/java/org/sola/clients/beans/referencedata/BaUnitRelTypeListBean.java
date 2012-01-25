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
package org.sola.clients.beans.referencedata;

import java.util.ArrayList;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;

/**
 * Holds list of {@link BaUnitRelTypeBean} objects.
 */
public class BaUnitRelTypeListBean extends AbstractBindingListBean {
    public static final String SELECTED_BA_UNIT_REL_TYPE_PROPERTY = "selectedBaUnitRelType";
    private ObservableList<BaUnitRelTypeBean> baUnitRelTypes;
    private BaUnitRelTypeBean selectedBaUnitRelType;

    /** Default constructor. */
    public BaUnitRelTypeListBean(){
        this(false);
    }
    
    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public BaUnitRelTypeListBean(boolean createDummy) {
        super();
        loadList(createDummy);
    }

    /** 
     * Loads list of {@link BaUnitRelTypeBean}.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public final void loadList(boolean createDummy) {
        if (baUnitRelTypes == null) {
            baUnitRelTypes = ObservableCollections.observableList(new ArrayList<BaUnitRelTypeBean>());
        }
        loadCodeList(BaUnitRelTypeBean.class, baUnitRelTypes, 
                CacheManager.getBaUnitRelTypes(), createDummy);
    }
    
    public ObservableList<BaUnitRelTypeBean> getBaUnitRelTypes() {
        return baUnitRelTypes;
    }

    public BaUnitRelTypeBean getSelectedBaUnitRelType() {
        return selectedBaUnitRelType;
    }

    public void setSelectedBaUnitRelType(BaUnitRelTypeBean selectedBaUnitRelType) {
        BaUnitRelTypeBean oldValue = this.selectedBaUnitRelType;
        this.selectedBaUnitRelType = selectedBaUnitRelType;
        propertySupport.firePropertyChange(SELECTED_BA_UNIT_REL_TYPE_PROPERTY, 
                oldValue, this.selectedBaUnitRelType);
    }
}
