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
package org.sola.clients.beans.systematicregistration;

import java.util.Date;
import javax.validation.constraints.NotNull;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.administrative.SysRegManagementParamsTO;

/**
 * Contains properties used as the parameters to search applications. Could be
 * populated from the {@link ApplicationSearchParamsTO} object.<br />
 */
/**
 *
 * @author RizzoM
 */
public class SysRegManagementParamsBean extends AbstractBindingBean {

    public static final String FROM_DATE_PROPERTY = "fromDate";
    public static final String TO_DATE_PROPERTY = "toDate";
    public static final String NAME_LAST_PART_PROPERTY = "nameLastpart";
// @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class)
    private Date fromDate;
// @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class)
    private Date toDate;
    private String nameLastpart;

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String value) {
        String oldValue = nameLastpart;
        nameLastpart = value;
        propertySupport.firePropertyChange(NAME_LAST_PART_PROPERTY, oldValue, value);
    }

    public SysRegManagementParamsBean() {
        super();
    }

    public Date getFromDate() {
        return fromDate;
    }

// @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class)
    public void setFromDate(Date value) {
        Date oldValue = fromDate;
        fromDate = value;
        propertySupport.firePropertyChange(FROM_DATE_PROPERTY, oldValue, value);
    }

    public Date getToDate() {
        return toDate;
    }

// @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class)
    public void setToDate(Date value) {
        Date oldValue = toDate;
        toDate = value;
        propertySupport.firePropertyChange(TO_DATE_PROPERTY, oldValue, value);
    }
}