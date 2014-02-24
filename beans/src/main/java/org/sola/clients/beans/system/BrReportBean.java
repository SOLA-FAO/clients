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

import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.referencedata.CommunicationTypeBean;
import org.sola.webservices.transferobjects.casemanagement.PartyTO;

/** 
 * Represents party object in the domain model. 
 * Could be populated from the {@link PartyTO} object.<br />
 * For more information see data dictionary <b>Party</b> schema.
 * <br />This bean is used as a part of {@link ApplicationBean}.
 */
public class BrReportBean extends AbstractIdBean {

    public static final String TYPE_CODE_PROPERTY = "technicalTypeCode";
    public static final String FEEDBACK_PROPERTY = "feedback";
    public static final String BODY_PROPERTY = "body";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String SEVERITYCODE_PROPERTY = "severityCode";
    public static final String MOMENTCODE_PROPERTY = "momentCode";
    public static final String TARGETCODE_PROPERTY = "targetCode";
    public static final String TARGETREQUESTTYPECODE_PROPERTY = "targetRequestTypeCode";
    public static final String TARGETRRRTYPECODE_PROPERTY = "targetRrrTypeCode";
    public static final String ORDEROFEXECUTION_PROPERTY = "orderOfExecution";
     
    //    private String id;
    private String technicalTypeCode;
    private String feedback;
    private String body;
    private String description;
    private String severityCode;
    private String momentCode;
    private String targetCode;
    private String targetRequestTypeCode;
    private String targetRrrTypeCode;
    private Integer orderOfExecution;
    /** 
     * Default constructor to create party bean. Initializes 
     * {@link CommunicationTypeBean} as a part of this bean.
     */
    public BrReportBean() {
        super();
    }
    
     public String getTechnicalTypeCode() {
        return technicalTypeCode;
    }

    public void setTechnicalTypeCode(String value) {
        String oldValue = technicalTypeCode;
        technicalTypeCode = value;
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, value);
    }
  
     public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String value) {
        String oldValue = feedback;
        feedback = value;
        propertySupport.firePropertyChange(FEEDBACK_PROPERTY, oldValue, value);
    }
    
    public String getBody() {
        return body;
    }

    public void setBody(String value) {
        String oldValue = body;
        body = value;
        propertySupport.firePropertyChange(BODY_PROPERTY, oldValue, value);
    }

   
    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        String oldValue = description;
        description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, value);
    }
    
    
    
    public String getSeverityCode() {
        return severityCode;
    }

    public void setSeverityCode(String value) {
        String oldValue = severityCode;
        severityCode = value;
        propertySupport.firePropertyChange(SEVERITYCODE_PROPERTY, oldValue, value);
    }
    
    public String getMomentCode() {
        return momentCode;
    }

    public void setMomentCode(String value) {
        String oldValue = momentCode;
        momentCode = value;
        propertySupport.firePropertyChange(MOMENTCODE_PROPERTY, oldValue, value);
    }
    
     public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String value) {
         String oldValue = targetCode;
        targetCode = value;
        propertySupport.firePropertyChange(TARGETCODE_PROPERTY, oldValue, value);
    }
    
    public String getTargetRequestTypeCode() {
        return targetRequestTypeCode;
    }

    public void setTargetRequestTypeCode(String value) {
         String oldValue = targetRequestTypeCode;
        targetRequestTypeCode = value;
        propertySupport.firePropertyChange(TARGETREQUESTTYPECODE_PROPERTY, oldValue, value);
    }
    public String getTargetRrrTypeCode() {
        return targetRrrTypeCode;
    }

    public void setTargetRrrTypeCode(String value) {
         String oldValue = targetRrrTypeCode;
        targetRrrTypeCode = value;
        propertySupport.firePropertyChange(TARGETRRRTYPECODE_PROPERTY, oldValue, value);
     }
    
    public Integer getOrderOfExecution() {
        return orderOfExecution;
    }

    public void setOrderOfExecution(Integer value) {
         Integer oldValue = orderOfExecution;
        orderOfExecution = value;
        propertySupport.firePropertyChange(ORDEROFEXECUTION_PROPERTY, oldValue, value);
    }
   
     /** 
     * Returns collection of {@link BrReportBean} objects. This method is 
     * used by Jasper report designer to extract properties of application bean 
     * to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
       
        BrReportBean bean = new BrReportBean();
        collection.add(bean);
        return collection;
    }
}


