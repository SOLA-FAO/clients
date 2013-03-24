/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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