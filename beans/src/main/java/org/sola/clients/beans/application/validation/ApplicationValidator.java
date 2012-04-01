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
package org.sola.clients.beans.application.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Validates {@link ApplicationBean} object for extra logic validation
 */
public class ApplicationValidator implements ConstraintValidator<ApplicationCheck, ApplicationBean> {

    @Override
    public void initialize(ApplicationCheck a) {
    }

    @Override
    public boolean isValid(ApplicationBean appBean, ConstraintValidatorContext constraintContext) {
        if (appBean == null) {
            return true;
        }

        boolean result = true;

        constraintContext.disableDefaultConstraintViolation();

        // Check contact person
        if (appBean.getContactPerson() == null) {
            result = false;
            constraintContext.buildConstraintViolationWithTemplate(
                    MessageUtility.getLocalizedMessageText(
                    ClientMessage.CHECK_APP_CONTACT_PERSON_NULL)).addConstraintViolation();
        } else {
            if (appBean.getContactPerson().getAddress() == null || 
                    appBean.getContactPerson().getAddress().getDescription() == null ||
                    appBean.getContactPerson().getAddress().getDescription().isEmpty()) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_APP_CONTACT_PERSON_ADDRESS)).addConstraintViolation();
            }
            if (appBean.getContactPerson().getName() == null || appBean.getContactPerson().getName().isEmpty()) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_APP_CONTACT_PERSON_NAME)).addConstraintViolation();
            }
            if (appBean.getContactPerson().getLastName() == null || appBean.getContactPerson().getLastName().isEmpty()) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_APP_CONTACT_PERSON_LASTNAME)).addConstraintViolation();
            }
        }

        return result;
    }
}