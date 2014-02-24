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

import java.util.Date;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.webservices.transferobjects.casemanagement.ApplicationSummaryTO;

/**
 * Contains summary properties of the application object. Could be populated 
 * from the {@link ApplicationSummaryTO} object.<br /> 
 * For more information see data dictionary <b>Application</b> schema.
 */
public class ApplicationSummaryBean extends AbstractIdBean {

    public static final String NR_PROPERTY = "nr";
    public static final String EXPECTED_COMPLETION_DATE_PROPERTY = "expectedCompletionDate";
    public static final String ASSIGNED_DATE_TIME_PROPERTY = "assignedDatetime";
    public static final String LODGING_DATE_TIME_PROPERTY = "lodgingDatetime";
    public static final String AGENT_ID_PROPERTY = "agentId";
    public static final String CONTACT_PERSON_ID_PROPERTY = "contactPersonId";
    public static final String FEE_PAID_PROPERTY = "feePaid";
    
    private String nr;
    private Date lodgingDatetime;
    private Date expectedCompletionDate;
    private Date assignedDatetime;
    private String agentId;
    private String contactPersonId;
    private boolean feePaid;

    public ApplicationSummaryBean() {
        super();
    }

    public void setAgentId(String agentId) {
        String old = this.agentId;
        this.agentId = agentId;
        propertySupport.firePropertyChange(AGENT_ID_PROPERTY, old, this.agentId);
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setFeePaid(boolean feePaid) {
        boolean old = this.feePaid;
        this.feePaid = feePaid;
        propertySupport.firePropertyChange(FEE_PAID_PROPERTY, old, this.feePaid);
    }

    public boolean isFeePaid() {
        return feePaid;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public Date getAssignedDatetime() {
        return assignedDatetime;
    }

    public void setAssignedDatetime(Date assignedDatetime) {
        Date old = this.assignedDatetime;
        this.assignedDatetime = assignedDatetime;
        propertySupport.firePropertyChange(ASSIGNED_DATE_TIME_PROPERTY, old, this.assignedDatetime);
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date expectedCompletionDate) {
        Date old = this.expectedCompletionDate;
        this.expectedCompletionDate = expectedCompletionDate;
        propertySupport.firePropertyChange(EXPECTED_COMPLETION_DATE_PROPERTY, old, this.expectedCompletionDate);
    }

    public Date getLodgingDatetime() {
        return lodgingDatetime;
    }

    public void setLodgingDatetime(Date lodgingDatetime) {
        Date old = this.lodgingDatetime;
        this.lodgingDatetime = lodgingDatetime;
        propertySupport.firePropertyChange(LODGING_DATE_TIME_PROPERTY, old, this.lodgingDatetime);
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        String old = this.nr;
        this.nr = nr;
        propertySupport.firePropertyChange(NR_PROPERTY, old, this.nr);
    }
}
