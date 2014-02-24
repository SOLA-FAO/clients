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
package org.sola.clients.beans.referencedata;

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaCodeList;

/**
 * Holds list of {@link RequestCategoryTypeBean} objects.
 */
public class RequestCategoryTypeListBean extends AbstractBindingListBean {

    public static final String SELECTED_REQUEST_CATEGORY_TYPE_PROPERTY = "selectedRequestCategoryType";
    private SolaCodeList<RequestCategoryTypeBean> requestCategoryTypes;
    private RequestCategoryTypeBean selectedRequestCategoryType;

    /** Default constructor. */
    public RequestCategoryTypeListBean() {
        this(false);
    }

    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public RequestCategoryTypeListBean(boolean createDummy) {
        this(createDummy, (String) null);
    }
    
    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public RequestCategoryTypeListBean(boolean createDummy, String ... excludedCodes) {
        super();
        requestCategoryTypes = new SolaCodeList<RequestCategoryTypeBean>(excludedCodes);
        loadList(createDummy);
    }

    /** 
     * Loads list of {@link RequestCategoryTypeBean}.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public final void loadList(boolean createDummy) {
        loadCodeList(RequestCategoryTypeBean.class, requestCategoryTypes, 
                CacheManager.getRequestCategoryTypes(), createDummy);
    }

    public ObservableList<RequestCategoryTypeBean> getRequestCategoryTypes() {
        return requestCategoryTypes.getFilteredList();
    }

    public void setExcludedCodes(String ... codes){
        requestCategoryTypes.setExcludedCodes(codes);
    }
    
    public RequestCategoryTypeBean getSelectedRequestCategoryType() {
        return selectedRequestCategoryType;
    }

    public void setSelectedRequestCategoryType(RequestCategoryTypeBean selectedRequestCategoryType) {
        RequestCategoryTypeBean oldValue = this.selectedRequestCategoryType;
        this.selectedRequestCategoryType = selectedRequestCategoryType;
        propertySupport.firePropertyChange(SELECTED_REQUEST_CATEGORY_TYPE_PROPERTY, oldValue, this.selectedRequestCategoryType);
    }
}
