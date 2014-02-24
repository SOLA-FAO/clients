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
package org.sola.clients.beans.system.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sola.clients.beans.system.BrValidationBean;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Used to make validation for {@link BrValidationRegistrationMoment}.
 */
public class BrValidationBeanValidator implements
        ConstraintValidator<BrValidationCheck, BrValidationBean> {

    @Override
    public void initialize(BrValidationCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(BrValidationBean brValidationBean, ConstraintValidatorContext context) {
        if (brValidationBean == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        
        if (brValidationBean.getTargetCode() != null) {
            // Application moment
            if (brValidationBean.getTargetCode().equals("application")
                    && (brValidationBean.getTargetServiceMoment() != null
                    || brValidationBean.getTargetRegMoment() != null)) {
                context.buildConstraintViolationWithTemplate(MessageUtility
                        .getLocalizedMessage(ClientMessage.BR_APPLICATION_MOMENT_VALIDATION)
                        .getMessage()).addConstraintViolation();
                return false;
            }
            
            // Registration moment
            if ((!brValidationBean.getTargetCode().equals("application")
                    && !brValidationBean.getTargetCode().equals("service"))
                    && (brValidationBean.getTargetServiceMoment() != null
                    || brValidationBean.getTargetApplicationMoment() != null)) {
                context.buildConstraintViolationWithTemplate(MessageUtility
                        .getLocalizedMessage(ClientMessage.BR_REGISTRATION_MOMENT_VALIDATION)
                        .getMessage()).addConstraintViolation();
                return false;
            }
            
            // Rrr type
            if (!brValidationBean.getTargetCode().equals("rrr")
                    && brValidationBean.getTargetRrrTypeCode() != null) {
                context.buildConstraintViolationWithTemplate(MessageUtility
                        .getLocalizedMessage(ClientMessage.BR_RRR_TYPE_VALIDATION)
                        .getMessage()).addConstraintViolation();
                return false;
            }
            
            // Service moment
            if (brValidationBean.getTargetCode().equals("service")
                    && (brValidationBean.getTargetApplicationMoment() != null
                    || brValidationBean.getTargetRegMoment() != null)) {
                context.buildConstraintViolationWithTemplate(MessageUtility
                        .getLocalizedMessage(ClientMessage.BR_SERVICE_MOMENT_VALIDATION)
                        .getMessage()).addConstraintViolation();
                return false;
            }
            
            // Request type moment
            if (brValidationBean.getTargetCode().equals("application")
                    && brValidationBean.getTargetRequestTypeCode() != null) {
                context.buildConstraintViolationWithTemplate(MessageUtility
                        .getLocalizedMessage(ClientMessage.BR_REQUEST_TYPE_VALIDATION)
                        .getMessage()).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
