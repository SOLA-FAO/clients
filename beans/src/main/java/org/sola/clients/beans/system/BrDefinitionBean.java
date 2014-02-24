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

import java.util.Calendar;
import java.util.Date;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.admin.BrDefinitionTO;

/** 
 * Represents BR definition object in the domain model. 
 * Could be populated from the {@link BrDefinitionTO} object.<br />
 * For more information see data dictionary <b>System</b> schema.
 */
public class BrDefinitionBean extends AbstractBindingBean {

    //public static final String ID_PROPERTY = "id";
    public static final String BR_ID_PROPERTY = "brId";
    public static final String ACTIVE_FROM_PROPERTY = "activeFrom";
    public static final String ACTIVE_UNTIL_PROPERTY = "activeUntil";
    public static final String BODY_PROPERTY = "body";
    
   // private String id;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_BRID, payload = Localized.class)
    private String brId;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_ACTIVEFROM, payload = Localized.class)
    private Date activeFrom;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_ACTIVEUNTIL, payload = Localized.class)
    private Date activeUntil;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_BODY, payload = Localized.class)
    private String body;

    public BrDefinitionBean() {
        super();
        // Set default values
        Calendar calendar = Calendar.getInstance();
        activeFrom = calendar.getTime();
        calendar.add(Calendar.YEAR, 10);
        activeUntil = calendar.getTime();
        body = "Enter business rule text.";
    }
    
    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Date activeFrom) {
        Date oldValue = this.activeFrom;
        this.activeFrom = activeFrom;
        propertySupport.firePropertyChange(ACTIVE_FROM_PROPERTY, oldValue, this.activeFrom);
    }

    public Date getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Date activeUntil) {
        Date oldValue = this.activeUntil;
        this.activeUntil = activeUntil;
        propertySupport.firePropertyChange(ACTIVE_UNTIL_PROPERTY, oldValue, this.activeUntil);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        String oldValue = this.body;
        this.body = body;
        propertySupport.firePropertyChange(BODY_PROPERTY, oldValue, this.body);
    }

    public String getBrId() {
        return brId;
    }

    public void setBrId(String brId) {
        String oldValue = this.brId;
        this.brId = brId;
        propertySupport.firePropertyChange(BR_ID_PROPERTY, oldValue, this.brId);
    }

    /** Clean all fields. */
    public void clean() {
        setBody(null);
        setActiveFrom(null);
        setActiveUntil(null);
        setBrId(null);
        setEntityAction(null);
    }
    
    @Override
    public boolean equals(Object aThat) {
        if (aThat == null) {
            return false;
        }
        if (this == aThat) {
            return true;
        }

        if (!(BrDefinitionBean.class.isAssignableFrom(aThat.getClass()))) {
            return false;
        }

        BrDefinitionBean that = (BrDefinitionBean) aThat;

        if ((this.getBrId() != null && that.getBrId() != null
                && this.getBrId().equals(that.getBrId()))
                || (this.getBrId() == null && that.getBrId() == null)) {
            // Check date
            if ((this.getActiveFrom() != null && that.getActiveFrom() != null
                    && this.getActiveFrom().compareTo(that.getActiveFrom()) == 0)
                    || (this.getActiveFrom() == null && that.getActiveFrom() == null)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.brId != null ? this.brId.hashCode() : 0);
        hash = 53 * hash + (this.activeFrom != null ? this.activeFrom.hashCode() : 0);
        return hash;
    }
}
