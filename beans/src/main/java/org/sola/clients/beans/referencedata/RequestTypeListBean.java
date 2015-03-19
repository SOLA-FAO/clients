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
package org.sola.clients.beans.referencedata;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaCodeList;

/**
 * Holds list of {@link RequestTypeBean} objects.
 */
public class RequestTypeListBean extends AbstractBindingListBean {

    public static final String SELECTED_REQUEST_TYPE_PROPERTY = "selectedRequestType";
    private SolaCodeList<RequestTypeBean> requestTypeListBean;
    private RequestTypeBean selectedRequestTypeBean;

    /**
     * Initializes object's instance and populates {@link ObservableList}&lt;
     * {@link RequestTypeBean} &gt; with values from the cache.
     */
    public RequestTypeListBean() {
        this(false);
    }

    /**
     * Creates object instance.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public RequestTypeListBean(boolean createDummy) {
        this(createDummy, (String) null);
    }

    /**
     * Creates object instance.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public RequestTypeListBean(boolean createDummy, String... excludedCodes) {
        super();
        requestTypeListBean = new SolaCodeList<RequestTypeBean>(excludedCodes);
        loadList(createDummy);
    }

    /**
     * Loads list of {@link RequestTypeBean}.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public final void loadList(boolean createDummy) {
        loadCodeList(RequestTypeBean.class, requestTypeListBean,
                CacheManager.getRequestTypes(), createDummy);
    }

    public ObservableList<RequestTypeBean> getRequestTypeList() {
        return requestTypeListBean.getFilteredList();
    }

    public void setExcludedCodes(String... codes) {
        requestTypeListBean.setExcludedCodes(codes);
    }

    public RequestTypeBean getSelectedRequestType() {
        return selectedRequestTypeBean;
    }

    public void setSelectedRequestType(RequestTypeBean value) {
        selectedRequestTypeBean = value;
        propertySupport.firePropertyChange(SELECTED_REQUEST_TYPE_PROPERTY, null, value);
    }

    /**
     * Adds {@link RequestTypeBean} to the list by code.
     *
     * @param requestTypeCode The code of request type to add.
     */
    public void addRequestType(String requestTypeCode) {
        if (requestTypeListBean != null && requestTypeCode != null && requestTypeCode.equals("") == false) {
            // Check if code already exist
            boolean isExists = false;
            for (Iterator<RequestTypeBean> it = requestTypeListBean.iterator(); it.hasNext();) {
                RequestTypeBean requestTypeBean = it.next();
                if (requestTypeBean.getCode() != null && requestTypeBean.getCode().equals(requestTypeCode)) {
                    isExists = true;
                    break;
                }
            }

            if (!isExists) {
                for (Iterator<RequestTypeBean> it = CacheManager.getRequestTypes().iterator(); it.hasNext();) {
                    RequestTypeBean requestTypeBean = it.next();
                    if (requestTypeBean.getCode() != null && requestTypeBean.getCode().equals(requestTypeCode)) {
                        requestTypeListBean.add(requestTypeBean);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Removes objects from the RequestType list using a given list of
     * {@link ApplicationServiceBean} objects.
     *
     * @param appServices The list of application services, containing request
     * type codes to remove from the RequestType list.
     */
    public void removeRequestTypesByAppServices(List<ApplicationServiceBean> appServices) {
        for (Iterator<ApplicationServiceBean> it = appServices.iterator(); it.hasNext();) {
            ApplicationServiceBean applicationServiceBean = it.next();

            for (Iterator<RequestTypeBean> it1 = requestTypeListBean.iterator(); it1.hasNext();) {
                RequestTypeBean requestTypeBean = it1.next();

                if (requestTypeBean.getCode().equals(applicationServiceBean.getRequestTypeCode())) {
                    requestTypeListBean.remove(requestTypeBean);
                    break;
                }
            }
        }
    }

    /**
     * Removes currently selected {@link RequestTypeBean} from the RequestType
     * list.
     */
    public void removeRequestType() {
        if (selectedRequestTypeBean != null && requestTypeListBean != null) {
            requestTypeListBean.remove(selectedRequestTypeBean);
        }
    }

    /**
     * Sorts a request types list by its Category Display value followed by its
     * display value. Used for display of the request types in the Add Service
     * dialog.
     *
     * @param beanList The request list to sort.
     * @return
     */
    public List<RequestTypeBean> sortRequestTypes(List<RequestTypeBean> beanList) {
        Collections.sort(beanList, new Comparator<RequestTypeBean>() {
            @Override
            public int compare(RequestTypeBean bean1, RequestTypeBean bean2) {
                if (bean1 == null && bean2 == null) {
                    return 0;
                }
                if (bean1 == null && bean2 != null) {
                    return -1;
                }
                if (bean1 != null && bean2 == null) {
                    return 1;
                }
                // If one of the beans has a non zero display order, then use that 
                // to order the beans. 
                if (bean1.getDisplayOrder() != 0 || bean2.getDisplayOrder() != 0) {
                    return Integer.compare(bean1.getDisplayOrder(), bean2.getDisplayOrder());
                }
                // Use the Category Display Value to determine how these beans should be listed. 
                if (bean1.getCategoryDisplayValue() != null
                        && !bean1.getCategoryDisplayValue().equals(bean2.getCategoryDisplayValue())) {
                    return bean1.getCategoryDisplayValue().compareTo(bean2.getCategoryDisplayValue());
                } else {
                    // Sort by the Display Value for the request type. 
                    return bean1.getDisplayValue().compareTo(bean2.getDisplayValue());
                }
            }
        });
        return beanList;
    }

}
