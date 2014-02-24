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
package org.sola.clients.beans.application;

import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.RequestTypeBean;

/**
 * Contains summary properties of application service.
 * Could be populated from the {@link ServiceSummaryTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class ApplicationServiceSummaryBean extends AbstractIdBean {

    public static final String APPLICATION_ID_PROPERTY = "applicationId";
    public static final String REQUEST_TYPE_CODE_PROPERTY = "requestTypeCode";
    public static final String SERVICE_ORDER_PROPERTY = "serviceOrder";
    public static final String REQUEST_TYPE_PROPERTY = "requestType";
    private String applicationId;
    private String requestTypeCode;
    private int serviceOrder;
    private RequestTypeBean requestTypeBean;

    /** 
     * Creates object's instance and initializes the following bean, which is 
     * the part of this bean: <br />
     * {@link RequestTypeBean}
     */
    public ApplicationServiceSummaryBean() {
        super();
        requestTypeBean = new RequestTypeBean();
    }

    public RequestTypeBean getRequestType() {
        return requestTypeBean;
    }

    public void setRequestType(RequestTypeBean requestTypeBean) {
        if(this.requestTypeBean==null){
            this.requestTypeBean = new RequestTypeBean();
        }
        this.setJointRefDataBean(this.requestTypeBean, requestTypeBean, REQUEST_TYPE_PROPERTY);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String value) {
        String old = applicationId;
        applicationId = value;
        propertySupport.firePropertyChange(APPLICATION_ID_PROPERTY, old, value);
    }

    public String getRequestTypeCode() {
        return requestTypeBean.getCode();
    }

    /** 
     * Sets request type code and retrieves relevant {@link RequestTypeBean} 
     * from the cache. 
     * @param value Request type code.
     */
    public void setRequestTypeCode(String value) {
        String old = requestTypeCode;
        requestTypeCode = value;
        setRequestType(CacheManager.getBeanByCode(
                CacheManager.getRequestTypes(), value));
        propertySupport.firePropertyChange(REQUEST_TYPE_CODE_PROPERTY, old, value);
    }

    public int getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(int value) {
        int old = serviceOrder;
        serviceOrder = value;
        propertySupport.firePropertyChange(SERVICE_ORDER_PROPERTY, old, value);
    }
}
