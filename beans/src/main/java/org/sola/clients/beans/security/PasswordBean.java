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
package org.sola.clients.beans.security;

import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.security.validation.PasswordsCheck;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;

/**
 * Used to treat user password.
 */
@PasswordsCheck(message="Passwords are not equal!.")
public class PasswordBean extends AbstractBindingBean {

    public static final String PASSWORD_PROPERTY = "password";
    public static final String PASSWORD_CONFIRMATION_PROPERTY = "passwordConfirmation";

    @NotEmpty(message= ClientMessage.CHECK_NOTNULL_PASSWORD, payload=Localized.class)
    @Size(min=5, message= ClientMessage.CHECK_MIN_PASSWORD, payload=Localized.class)
    private String password;
    @NotEmpty(message= ClientMessage.CHECK_NOTNULL_CONFPASSWORD, payload=Localized.class)
    private String passwordConfirmation;
    
    public PasswordBean() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String oldValue = this.password;
        this.password = password;
        propertySupport.firePropertyChange(PASSWORD_PROPERTY, oldValue, this.password);
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        String oldValue = this.passwordConfirmation;
        this.passwordConfirmation = passwordConfirmation;
        propertySupport.firePropertyChange(PASSWORD_CONFIRMATION_PROPERTY, oldValue, this.passwordConfirmation);
    }
}
