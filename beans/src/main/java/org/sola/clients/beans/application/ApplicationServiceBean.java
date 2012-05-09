/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.ServiceActionTypeBean;
import org.sola.clients.beans.referencedata.ServiceStatusTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.ServiceTO;

/**
 * Represents application service object.
 * Could be populated from the {@link ServiceTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class ApplicationServiceBean extends ApplicationServiceSummaryBean {
    
    public static final String ACTION_CODE_PROPERTY = "actionCode";
    public static final String ACTION_PROPERTY = "action";
    public static final String ACTION_NOTES_PROPERTY = "actionNotes";
    public static final String AREA_FEE_PROPERTY = "areaFee";
    public static final String BASE_FEE_PROPERTY = "baseFee";
    public static final String EXPECTED_COMPLETION_DATE_PROPERTY = "expectedCompletionDate";
    public static final String LODGING_DATE_PROPERTY = "lodgingDatetime";
    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String STATUS_PROPERTY = "status";
    public static final String VALUE_FEE_PROPERTY = "valueFee";
    
    private ServiceActionTypeBean actionBean;
    private ServiceStatusTypeBean statusBean;
    private String actionNotes;
    private BigDecimal areaFee;
    private BigDecimal baseFee;
    private Date expectedCompletionDate;
    private Date lodgingDatetime;
    private BigDecimal valueFee;

    /** 
     * Creates object's instance and initializes the following beans, which are 
     * the parts of this bean: <br />
     * {@link ServiceActionTypeBean}<br />
     * {@link ServiceStatusTypeBean}
     */
    public ApplicationServiceBean() {
        super();
        actionBean = new ServiceActionTypeBean();
        statusBean = new ServiceStatusTypeBean();
    }

    public String getActionCode() {
        return actionBean.getCode();
    }

    /** 
     * Sets service action code and retrieves relevant {@link ServiceActionTypeBean} 
     * from the cache. 
     * @param value Application service action code.
     */
    public void setActionCode(String value) {
        String old = actionBean.getCode();
        setAction(CacheManager.getBeanByCode(
                CacheManager.getAppServiceActionTypes(), value));
        propertySupport.firePropertyChange(ACTION_CODE_PROPERTY, old, value);
    }

    public ServiceActionTypeBean getAction() {
        return actionBean;
    }

    public void setAction(ServiceActionTypeBean actionBean) {
        if (this.actionBean == null) {
            this.actionBean = new ServiceActionTypeBean();
        }
        this.setJointRefDataBean(this.actionBean, actionBean, ACTION_PROPERTY);
    }

    public String getActionNotes() {
        return actionNotes;
    }

    public void setActionNotes(String value) {
        String old = actionNotes;
        actionNotes = value;
        propertySupport.firePropertyChange(ACTION_NOTES_PROPERTY, old, value);
    }

    public BigDecimal getAreaFee() {
        return areaFee;
    }

    public void setAreaFee(BigDecimal value) {
        BigDecimal old = areaFee;
        areaFee = value;
        propertySupport.firePropertyChange(AREA_FEE_PROPERTY, old, value);
    }

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(BigDecimal value) {
        BigDecimal old = baseFee;
        baseFee = value;
        propertySupport.firePropertyChange(BASE_FEE_PROPERTY, old, value);
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date value) {
        Date old = expectedCompletionDate;
        expectedCompletionDate = value;
        propertySupport.firePropertyChange(EXPECTED_COMPLETION_DATE_PROPERTY, old, value);
    }

    public Date getLodgingDatetime() {
        return lodgingDatetime;
    }

    public void setLodgingDatetime(Date value) {
        Date old = lodgingDatetime;
        lodgingDatetime = value;
        propertySupport.firePropertyChange(LODGING_DATE_PROPERTY, old, value);
    }

    public ServiceStatusTypeBean getStatus() {
        return statusBean;
    }

    public void setStatus(ServiceStatusTypeBean statusBean) {
        if (this.statusBean == null) {
            this.statusBean = new ServiceStatusTypeBean();
        }
        this.setJointRefDataBean(this.statusBean, statusBean, STATUS_PROPERTY);
    }

    public String getStatusCode() {
        if (statusBean == null) {
            return null;
        } else {
            return statusBean.getCode();
        }
    }

    /** 
     * Sets service status code and retrieves relevant {@link ServiceStatusTypeBean} 
     * from the cache. 
     * @param value Application service status code.
     */
    public void setStatusCode(String value) {
        String old = statusBean.getCode();
        setStatus(CacheManager.getBeanByCode(
                CacheManager.getAppServiceStatusTypes(), value));
        propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, old, value);
    }

    public BigDecimal getValueFee() {
        return valueFee;
    }

    public void setValueFee(BigDecimal value) {
        BigDecimal old = valueFee;
        valueFee = value;
        propertySupport.firePropertyChange(VALUE_FEE_PROPERTY, old, value);
    }

    /** Cancels service */
    public List<ValidationResultBean> cancel() {
        return TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().serviceActionCancel(this.getId(), this.getRowVersion()),
                ValidationResultBean.class, null);

    }

    /** Set service as completed */
    public List<ValidationResultBean> complete() {
        return TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().serviceActionComplete(this.getId(), this.getRowVersion()),
                ValidationResultBean.class, null);
    }

    /** Revert service back to the pending state */
    public List<ValidationResultBean> revert() {
        return TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().serviceActionRevert(this.getId(), this.getRowVersion()),
                ValidationResultBean.class, null);
    }

    /** Indicates whether service can be managed */
    public boolean isManagementAllowed() {
        String serviceStatus = getStatusCode();
        boolean result = true;

        if (serviceStatus == null) {
            result = false;
        }

        if (result && (serviceStatus.equals(StatusConstants.CANCELED)
                || serviceStatus.equals(StatusConstants.COMPLETED))) {
            result = false;
        }
        return result;
    }

    /**
     * Saves the service in the database. Used only for services of category: informationServices.
     * If another kind of request type is supplied, it will be thrown a server side exception.
     * @return
     * @throws Exception t
     */
    public boolean saveInformationService() {
        ServiceTO service = TypeConverters.BeanToTrasferObject(this, ServiceTO.class);
        service = WSManager.getInstance().getCaseManagementService().saveInformationService(
                service);
        TypeConverters.TransferObjectToBean(service, ApplicationServiceBean.class, this);
        return true;
    }

    /**
     * Creates and saves new Information service service in the database.
     * If another kind of request type is supplied, it will be thrown a server side exception.
     * @param requestTypeCode Request type code to use for creating service.
     */
    public static boolean saveInformationService(String requestTypeCode) {
        if (requestTypeCode == null) {
            return false;
        }
        ApplicationServiceBean serviceBean = new ApplicationServiceBean();
        RequestTypeBean requestType = CacheManager.getBeanByCode(CacheManager.getRequestTypes(), requestTypeCode);
        if (requestType != null) {
            serviceBean.setRequestType(requestType);
            return serviceBean.saveInformationService();
        } else {
            return false;
        }
    }
}
