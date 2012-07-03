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

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
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
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle");
        
        
        // Check contact person
        if (appBean.getContactPerson() == null) {
            result = false;
            constraintContext.buildConstraintViolationWithTemplate(
                    MessageUtility.getLocalizedMessageText(
                    ClientMessage.CHECK_APP_CONTACT_PERSON_NULL)).addConstraintViolation();
        } else {
            // address validation
            if (appBean.getContactPerson().getAddress() == null || 
                    appBean.getContactPerson().getAddress().getDescription() == null ||
                    appBean.getContactPerson().getAddress().getDescription().isEmpty()) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_APP_CONTACT_PERSON_ADDRESS)).addConstraintViolation();
            }
            if (appBean.getContactPerson().getAddress().getDescription() != null && !appBean.getContactPerson().getAddress().getDescription().isEmpty()) {
                if (appBean.getContactPerson().getAddress().getDescription().length()>255){
                        result = false;
                        constraintContext.buildConstraintViolationWithTemplate(
                        (MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_FIELD_INVALID_LENGTH))+" "+bundle.getString("ApplicationPanel.labAddress.text")).addConstraintViolation();
                }
            }
            // name validation
            if (appBean.getContactPerson().getName() == null || appBean.getContactPerson().getName().isEmpty()) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_APP_CONTACT_PERSON_NAME)).addConstraintViolation();
            }
            
            if (appBean.getContactPerson().getName() != null && !appBean.getContactPerson().getName().isEmpty()) {
                if (appBean.getContactPerson().getName().length()>255){
                        result = false;
                        constraintContext.buildConstraintViolationWithTemplate(
                        (MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_FIELD_INVALID_LENGTH))+" "+bundle.getString("ApplicationPanel.labName.text")).addConstraintViolation();
                }
            }
            // LastName validation
            if (appBean.getContactPerson().getLastName() == null || appBean.getContactPerson().getLastName().isEmpty()) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_APP_CONTACT_PERSON_LASTNAME)).addConstraintViolation();
            }
           
            if (appBean.getContactPerson().getLastName() != null && !appBean.getContactPerson().getLastName().isEmpty()) {
                if (appBean.getContactPerson().getLastName().length()>50){
                        result = false;
                        constraintContext.buildConstraintViolationWithTemplate(
                        (MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_FIELD_INVALID_LENGTH))+" "+bundle.getString("ApplicationPanel.labLastName.text")).addConstraintViolation();
                }
            }
            
            // phone validation
            if (appBean.getContactPerson().getPhone() != null && !appBean.getContactPerson().getPhone().isEmpty()) {
              if (! isPhoneNumberValid(appBean.getContactPerson().getPhone())) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_INVALID_PHONE)).addConstraintViolation();
              }
              if (appBean.getContactPerson().getPhone().length()>15){
                        result = false;
                        constraintContext.buildConstraintViolationWithTemplate(
                        (MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_FIELD_INVALID_LENGTH))+" "+bundle.getString("ApplicationPanel.labPhone.text")).addConstraintViolation();
              }
            }
            // fax validation
            if (appBean.getContactPerson().getFax() != null && !appBean.getContactPerson().getFax().isEmpty()) {
              if (! isPhoneNumberValid(appBean.getContactPerson().getFax())) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_INVALID_FAX)).addConstraintViolation();
              }
              if (appBean.getContactPerson().getFax().length()>15){
                        result = false;
                        constraintContext.buildConstraintViolationWithTemplate(
                        (MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_FIELD_INVALID_LENGTH))+" "+bundle.getString("ApplicationPanel.labFax.text")).addConstraintViolation();
              }
            }  
            // email validation
            if (appBean.getContactPerson().getEmail() != null && !appBean.getContactPerson().getEmail().isEmpty()) {
              if (! isEmailValid(appBean.getContactPerson().getEmail())) {
                result = false;
                constraintContext.buildConstraintViolationWithTemplate(
                        MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_INVALID_EMAIL)).addConstraintViolation();
              }
              if (appBean.getContactPerson().getEmail().length()>50){
                        result = false;
                        constraintContext.buildConstraintViolationWithTemplate(
                       (MessageUtility.getLocalizedMessageText(
                        ClientMessage.CHECK_FIELD_INVALID_LENGTH))+" "+bundle.getString("ApplicationPanel.labEmail.text")).addConstraintViolation();
              }
            }
        }
        return result;
    }
    
    
    

/** isPhoneNumberValid: Validate phone number using Java reg ex.
* This method checks if the input string is a valid phone number.
* @param phoneNumber String. Phone number to validate
* @return boolean: true if phone number is valid, false otherwise.
*/
public static boolean isPhoneNumberValid(String phoneNumber){
boolean isValid = false;

//Initialize reg ex for phone number. 
String expression =  "[0-9\\s]*+$";  
CharSequence inputStr = phoneNumber;
Pattern pattern = Pattern.compile(expression);
Matcher matcher = pattern.matcher(inputStr);
if(matcher.matches()){
isValid = true;
}
return isValid;
}


   /** isEmailValid: Validate phone number using Java reg ex.
* This method checks if the input string is a valid email.
* @param email String. 
* @return boolean: true if phone number is valid, false otherwise.
*/
public static boolean isEmailValid(String email){
boolean isValid = false;

//Initialize reg ex for email.  
String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";  
CharSequence inputStr = email;  
//Make the comparison case-insensitive.  
Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
Matcher matcher = pattern.matcher(inputStr);  
if(matcher.matches()){  
isValid = true;  
}  
return isValid;   
}
    
}