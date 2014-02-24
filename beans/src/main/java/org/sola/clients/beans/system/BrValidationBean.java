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
package org.sola.clients.beans.system;

import java.util.UUID;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.system.validation.BrValidationCheck;
import org.sola.clients.beans.validation.CodeBeanNotEmpty;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.admin.BrValidationTO;

/** 
 * Represents BR definition object in the domain model. 
 * Could be populated from the {@link BrValidationTO} object.<br />
 * For more information see data dictionary <b>System</b> schema.
 */
@BrValidationCheck()
public class BrValidationBean extends AbstractBindingBean {

    public static final String ID_PROPERTY = "id";
    public static final String BR_ID_PROPERTY = "brId";
    public static final String SEVERITY_CODE_PROPERTY = "severityCode";
    public static final String BR_SEVERITY_TYPE_PROPERTY = "brSeverityType";
    public static final String TARGET_CODE_PROPERTY = "targetCode";
    public static final String BR_VALIDATION_TARGET_TYPE_PROPERTY = "brValidationTargetType";
    public static final String TARGET_APPLICATION_MOMENT_PROPERTY = "targetApplicationMoment";
    public static final String APPLICATION_ACTION_TYPE_PROPERTY = "applicationActionType";
    public static final String TARGET_SERVICE_MOMENT_PROPERTY = "targetServiceMoment";
    public static final String SERVICE_ACTION_TYPE_PROPERTY = "serviceActionType";
    public static final String TARGET_REG_MOMENT_PROPERTY = "targetRegMoment";
    public static final String REGISTRATION_STATUS_TYPE_PROPERTY = "registrationStatusType";
    public static final String TARGET_REQUEST_TYPE_CODE_PROPERTY = "targetRequestTypeCode";
    public static final String REQUEST_TYPE_PROPERTY = "requestType";
    public static final String TARGET_RRR_TYPE_CODE_PROPERTY = "targetRrrTypeCode";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    public static final String ORDER_OF_EXECUTION_PROPERTY = "orderOfExecution";
    
    private String id;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_BRVALID, payload = Localized.class)
    private String brId;
    @CodeBeanNotEmpty(message = ClientMessage.CHECK_BEANNOTNULL_BRSEVTYPE, payload = Localized.class)
    private BrSeverityTypeBean brSeverityType;
    @CodeBeanNotEmpty(message = ClientMessage.CHECK_BEANNOTNULL_BRVALTARGETTYPE, payload = Localized.class)
    private BrValidationTargetTypeBean brValidationTargetType;
    private ApplicationActionTypeBean applicationActionType;
    private ServiceActionTypeBean serviceActionType;
    private RegistrationStatusTypeBean registrationStatusType;
    private RequestTypeBean requestType;
    private RrrTypeBean rrrType;
    private int orderOfExecution;

    public BrValidationBean() {
        super();
    }

    public ApplicationActionTypeBean getApplicationActionType() {
        if (applicationActionType == null) {
            applicationActionType = new ApplicationActionTypeBean();
        }
        return applicationActionType;
    }

    public void setApplicationActionType(ApplicationActionTypeBean applicationActionType) {
        this.setJointRefDataBean(getApplicationActionType(), applicationActionType, APPLICATION_ACTION_TYPE_PROPERTY);
    }

    public String getBrId() {
        return brId;
    }

    public void setBrId(String brId) {
        String oldValue = this.brId;
        this.brId = brId;
        propertySupport.firePropertyChange(BR_ID_PROPERTY, oldValue, this.brId);
    }

    public BrSeverityTypeBean getBrSeverityType() {
        if (brSeverityType == null) {
            brSeverityType = new BrSeverityTypeBean();
        }
        return brSeverityType;
    }

    public void setBrSeverityType(BrSeverityTypeBean brSeverityType) {
        this.setJointRefDataBean(getBrSeverityType(), brSeverityType, BR_SEVERITY_TYPE_PROPERTY);
    }

    public BrValidationTargetTypeBean getBrValidationTargetType() {
        if (brValidationTargetType == null) {
            brValidationTargetType = new BrValidationTargetTypeBean();
        }
        return brValidationTargetType;
    }

    public void setBrValidationTargetType(BrValidationTargetTypeBean brValidationTargetType) {
        this.setJointRefDataBean(getBrValidationTargetType(), brValidationTargetType, BR_VALIDATION_TARGET_TYPE_PROPERTY);
    }

    public String getId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public void setId(String id) {
        String oldValue = this.id;
        this.id = id;
        propertySupport.firePropertyChange(ID_PROPERTY, oldValue, this.id);
    }

    public int getOrderOfExecution() {
        return orderOfExecution;
    }

    public void setOrderOfExecution(int orderOfExecution) {
        int oldValue = this.orderOfExecution;
        this.orderOfExecution = orderOfExecution;
        propertySupport.firePropertyChange(ORDER_OF_EXECUTION_PROPERTY, oldValue, this.orderOfExecution);
    }

    public RegistrationStatusTypeBean getRegistrationStatusType() {
        if (registrationStatusType == null) {
            registrationStatusType = new RegistrationStatusTypeBean();
        }
        return registrationStatusType;
    }

    public void setRegistrationStatusType(RegistrationStatusTypeBean registrationStatusType) {
        this.setJointRefDataBean(getRegistrationStatusType(), registrationStatusType, REGISTRATION_STATUS_TYPE_PROPERTY);
    }

    public RequestTypeBean getRequestType() {
        if (requestType == null) {
            requestType = new RequestTypeBean();
        }
        return requestType;
    }

    public void setRequestType(RequestTypeBean requestType) {
        this.setJointRefDataBean(getRequestType(), requestType, REQUEST_TYPE_PROPERTY);
    }

    public RrrTypeBean getRrrType() {
        if (rrrType == null) {
            rrrType = new RrrTypeBean();
        }
        return rrrType;
    }

    public void setRrrType(RrrTypeBean rrrType) {
        this.setJointRefDataBean(getRrrType(), rrrType, RRR_TYPE_PROPERTY);
    }

    public ServiceActionTypeBean getServiceActionType() {
        if (serviceActionType == null) {
            serviceActionType = new ServiceActionTypeBean();
        }
        return serviceActionType;
    }

    public void setServiceActionType(ServiceActionTypeBean serviceActionType) {
        this.setJointRefDataBean(getServiceActionType(), serviceActionType, SERVICE_ACTION_TYPE_PROPERTY);
    }

    public String getSeverityCode() {
        return getBrSeverityType().getCode();
    }

    public void setSeverityCode(String severityCode) {
        String oldValue = getBrSeverityType().getCode();
        setBrSeverityType(CacheManager.getBeanByCode(CacheManager.getBrSeverityTypes(), severityCode));
        propertySupport.firePropertyChange(SEVERITY_CODE_PROPERTY, oldValue, severityCode);
    }

    public String getTargetApplicationMoment() {
        return getApplicationActionType().getCode();
    }

    public void setTargetApplicationMoment(String targetApplicationMoment) {
        String oldValue = getApplicationActionType().getCode();
        setApplicationActionType(CacheManager.getBeanByCode(CacheManager.getApplicationActionTypes(), targetApplicationMoment));
        propertySupport.firePropertyChange(TARGET_APPLICATION_MOMENT_PROPERTY, oldValue, targetApplicationMoment);
    }

    public String getTargetCode() {
        return getBrValidationTargetType().getCode();
    }

    public void setTargetCode(String targetCode) {
        String oldValue = getBrValidationTargetType().getCode();
        setBrValidationTargetType(CacheManager.getBeanByCode(CacheManager.getBrValidationTargetTypes(), targetCode));
        propertySupport.firePropertyChange(TARGET_CODE_PROPERTY, oldValue, targetCode);
    }

    public String getTargetRegMoment() {
        return getRegistrationStatusType().getCode();
    }

    public void setTargetRegMoment(String targetRegMoment) {
        String oldValue = getRegistrationStatusType().getCode();
        setRegistrationStatusType(CacheManager.getBeanByCode(CacheManager.getRegistrationStatusTypes(), targetRegMoment));
        propertySupport.firePropertyChange(TARGET_REG_MOMENT_PROPERTY, oldValue, targetRegMoment);
    }

    public String getTargetRequestTypeCode() {
        return getRequestType().getCode();
    }

    public void setTargetRequestTypeCode(String targetRequestTypeCode) {
        String oldValue = getRequestType().getCode();
        setRequestType(CacheManager.getBeanByCode(CacheManager.getRequestTypes(), targetRequestTypeCode));
        propertySupport.firePropertyChange(TARGET_REQUEST_TYPE_CODE_PROPERTY, oldValue, targetRequestTypeCode);
    }

    public String getTargetRrrTypeCode() {
        return getRrrType().getCode();
    }

    public void setTargetRrrTypeCode(String targetRrrTypeCode) {
        String oldValue = getRrrType().getCode();
        setRrrType(CacheManager.getBeanByCode(CacheManager.getRrrTypes(), targetRrrTypeCode));
        propertySupport.firePropertyChange(TARGET_RRR_TYPE_CODE_PROPERTY, oldValue, targetRrrTypeCode);
    }

    public String getTargetServiceMoment() {
        return getServiceActionType().getCode();
    }

    public void setTargetServiceMoment(String targetServiceMoment) {
        String oldValue = getServiceActionType().getCode();
        setServiceActionType(CacheManager.getBeanByCode(CacheManager.getAppServiceActionTypes(), targetServiceMoment));
        propertySupport.firePropertyChange(TARGET_SERVICE_MOMENT_PROPERTY, oldValue, targetServiceMoment);
    }

    @Override
    public boolean equals(Object aThat) {
        if (aThat == null) {
            return false;
        }
        if (this == aThat) {
            return true;
        }
        if (!(BrValidationBean.class.isAssignableFrom(aThat.getClass()))) {
            return false;
        }

        BrValidationBean that = (BrValidationBean) aThat;

        if (this.getId() != null && that.getId() != null
                && this.getId().equals(that.getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
