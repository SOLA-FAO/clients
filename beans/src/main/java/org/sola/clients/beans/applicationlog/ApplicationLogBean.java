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
package org.sola.clients.beans.applicationlog;

import java.util.Date;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.ApplicationActionTypeBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.ServiceActionTypeBean;

/**
 *
 * @author dounnaah
 */
public class ApplicationLogBean extends AbstractBindingBean {

    public static final String NOTATION_PROPERTY = "notation";
    public static final String ACTION_TYPE_PROPERTY = "actionType";
    public static final String NUMBER_PROPERTY = "number";
    public static final String USER_FULLNAME_PROPERTY = "userFullName";
    public static final String CHANGE_TIME_PROPERTY = "changeTime";
    public static final String RECORD_TYPE_PROPERTY = "recordType";
    public static final String RECORD_ID_PROPERTY = "recordId";
    public static final String RECORD_GROUP_PROPERTY = "recordGroup";
    public static final String RECORD_SEQUENCE_PROPERTY = "recordSequence";
    private ApplicationActionTypeBean actionBean;
    private String recordGroup;
    private String recordType;
    private String recordId;
    private int recordSequence;
    private String number;
    private String notation;
    private Date changeTime;
    private String userFullname;
    private String actionType;

    public ApplicationLogBean() {
        super();
        actionBean = new ApplicationActionTypeBean();

    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        String old = notation;
        this.notation = notation;
        propertySupport.firePropertyChange(NOTATION_PROPERTY, old, notation);
    }

    public String getActionType() {
//        if(actionBean==null || actionBean.getCode()== null) {
//        return null;
//    }
//        return actionBean.getCode(); 
        return actionType;
    }

    public void setActionType(String actionType) {
//        String old = actionBean.getCode();
//        setActionBean(CacheManager.getBeanByCode(
//                CacheManager.getApplicationActionTypes(), actionType));
//        propertySupport.firePropertyChange(ACTION_TYPE_PROPERTY, old, actionType);
        String old = actionType;
        this.actionType = actionType;
        propertySupport.firePropertyChange(ACTION_TYPE_PROPERTY, old, actionType);
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
        propertySupport.firePropertyChange(RECORD_ID_PROPERTY, null, recordId);
    }

    public int getRecordSequence() {
        return recordSequence;
    }

    public void setRecordSequence(int recordSequence) {
        this.recordSequence = recordSequence;
        propertySupport.firePropertyChange(RECORD_SEQUENCE_PROPERTY, null, recordSequence);
    }

    public Date getChangeTime() {
        return changeTime;

    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
        propertySupport.firePropertyChange(CHANGE_TIME_PROPERTY, null, changeTime);

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        propertySupport.firePropertyChange(NUMBER_PROPERTY, null, number);

    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
        propertySupport.firePropertyChange(RECORD_TYPE_PROPERTY, null, recordType);
    }

    public String getRecordGroup() {
        return recordGroup;
    }

    public void setRecordGroup(String recordGroup) {
        this.recordGroup = recordGroup;
        propertySupport.firePropertyChange(RECORD_GROUP_PROPERTY, null, recordGroup);
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
        propertySupport.firePropertyChange(USER_FULLNAME_PROPERTY, null, userFullname);
    }

    public ApplicationActionTypeBean getActionBean() {
        return actionBean;
    }

    public void setActionBean(ApplicationActionTypeBean actionBean) {
        if (this.actionBean == null) {
            this.actionBean = new ApplicationActionTypeBean();
        }
        this.setJointRefDataBean(this.actionBean, actionBean, ACTION_TYPE_PROPERTY);
    }

    //TODO - Fix code to use localized strings and system code values. 
    public String getDescription() {
        String description = "";
        String actType = getActionType();
        if (getRecordGroup().equalsIgnoreCase("application")) {
            ApplicationActionTypeBean appActionTypeBean = CacheManager.getBeanByCode(
                    CacheManager.getApplicationActionTypes(), actType);
            actType = appActionTypeBean == null
                    ? actType.substring(0, 1).toUpperCase() + actType.substring(1)
                    : appActionTypeBean.getDisplayValue();
            description = "Application, Action: " + actType;
        } else if (getRecordGroup().equalsIgnoreCase("service")) {
            ServiceActionTypeBean serActionTypeBean = CacheManager.getBeanByCode(
                    CacheManager.getAppServiceActionTypes(), actType);
            actType = serActionTypeBean == null
                    ? actType.substring(0, 1).toUpperCase() + actType.substring(1)
                    : serActionTypeBean.getDisplayValue();

            String requestType = getRecordType();
            RequestTypeBean requestTypeBean = CacheManager.getBeanByCode(
                    CacheManager.getRequestTypes(), getRecordType());
            requestType = requestTypeBean == null ? requestType
                    : requestTypeBean.getDisplayValue();

            description = requestType + " (" + getNumber()
                    + "), Action: " + actType;
        } else {
            actType = actType.substring(0, 1).toUpperCase() + actType.substring(1);
            description = getRecordType() + " " + getNumber()
                    + ", Action: " + actType;
        }
        return description;
    }
}
