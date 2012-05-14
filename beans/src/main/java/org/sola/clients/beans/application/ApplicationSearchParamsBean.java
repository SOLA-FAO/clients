/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.application;

import java.util.Date;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.webservices.transferobjects.search.ApplicationSearchParamsTO;

/**
 * Contains properties used as the parameters to search applications. Could be populated from the {@link ApplicationSearchParamsTO}
 * object.<br />
 */
public class ApplicationSearchParamsBean extends AbstractBindingBean {

    public static final String NR_PROPERTY = "nr";
    public static final String FROM_DATE_PROPERTY = "fromDate";
    public static final String TO_DATE_PROPERTY = "toDate";
    public static final String AGENT_PROPERTY = "agent";
    public static final String CONTACT_PERSON_PROPERTY = "contactPerson";
    public static final String DOCUMENT_NUM_PROPERTY = "documentNumber";
    public static final String DOCUMENT_REF_PROPERTY = "documentReference";
    private String nr;
    private Date fromDate;
    private Date toDate;
    private String agent;
    private String contactPerson;
    private String documentNumber;
    private String documentReference;

    public ApplicationSearchParamsBean() {
        super();
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String value) {
        String oldValue = contactPerson;
        contactPerson = value;
        propertySupport.firePropertyChange(CONTACT_PERSON_PROPERTY, oldValue, value);
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String value) {
        String oldValue = agent;
        agent = value;
        propertySupport.firePropertyChange(AGENT_PROPERTY, oldValue, value);
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date value) {
        Date oldValue = fromDate;
        fromDate = value;
        propertySupport.firePropertyChange(FROM_DATE_PROPERTY, oldValue, value);
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String value) {
        String oldValue = nr;
        nr = value;
        propertySupport.firePropertyChange(NR_PROPERTY, oldValue, value);
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date value) {
        Date oldValue = toDate;
        toDate = value;
        propertySupport.firePropertyChange(TO_DATE_PROPERTY, oldValue, value);
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String value) {
        String oldValue = documentNumber;
        documentNumber = value;
        propertySupport.firePropertyChange(DOCUMENT_NUM_PROPERTY, oldValue, value);
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String value) {
        String oldValue = documentReference;
        documentReference = value;
        propertySupport.firePropertyChange(DOCUMENT_REF_PROPERTY, oldValue, value);
    }
}
