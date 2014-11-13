/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.security;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;

/**
 * Represents summary object of <b>user</b>. Could be populated from the
 * {@link UserSummaryTO} object.<br />
 * For more information on the properties of the <code>user</code> see data
 * dictionary <b>System</b> schema.
 */
public class UserSummaryBean extends AbstractIdBean {

    public final static String DESCRIPTION_PROPERTY = "description";
    public final static String LASTNAME_PROPERTY = "lastName";
    public final static String FIRSTNAME_PROPERTY = "firstName";
    public final static String USERNAME_PROPERTY = "userName";

    @Length(max = 255, message = ClientMessage.CHECK_FIELD_INVALID_LENGTH_DESCRIPTION, payload = Localized.class)
    private String description;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_FIRSTNAME, payload = Localized.class)
    @Length(max = 30, message = ClientMessage.CHECK_FIELD_INVALID_LENGTH_FIRSTNAME, payload = Localized.class)
    private String firstName;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_LASTNAME, payload = Localized.class)
    @Length(max = 30, message = ClientMessage.CHECK_FIELD_INVALID_LENGTH_LASTNAME, payload = Localized.class)
    private String lastName;
    private String userName;

    public UserSummaryBean() {
        super();
    }

    public String getFullUserName() {
        String fullName = "";

        if (getFirstName() != null) {
            fullName = getFirstName();
        }

        if (getLastName() != null) {
            if (!fullName.equals("")) {
                fullName += " " + getLastName();
            } else {
                fullName = getLastName();
            }
        }

        if (fullName == null || fullName.equals("")) {
            fullName = this.userName;
        }
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        String oldValue = firstName;
        firstName = value;
        propertySupport.firePropertyChange(FIRSTNAME_PROPERTY, oldValue, value);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        String oldValue = lastName;
        lastName = value;
        propertySupport.firePropertyChange(LASTNAME_PROPERTY, oldValue, value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        String oldValue = description;
        description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, value);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String value) {
        String oldValue = userName;
        userName = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, value);
    }

    @Override
    public String toString() {
        return getFullUserName();
    }
}
