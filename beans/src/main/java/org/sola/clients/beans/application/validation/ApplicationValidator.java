/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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