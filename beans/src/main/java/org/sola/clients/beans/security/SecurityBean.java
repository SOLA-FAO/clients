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

import java.security.MessageDigest;
import java.util.HashMap;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.common.RolesConstants;
import org.sola.common.SOLAException;
import org.sola.common.StringUtility;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;

/**
 * Provides methods to authenticate user.
 */
public class SecurityBean extends AbstractBindingBean {

    public static final String USER_NAME_PROPERTY = "userName";
    public static final String USER_PASSWORD_PROPERTY = "userPassword";
    public static final Integer WARN_OF_PWORD_EXPIRY = 10;
    private String userName;
    private char[] userPassword;
    private static UserBean currentUser;

    public SecurityBean() {
        super();
    }

    /**
     * Returns currently logged user.
     */
    public static UserBean getCurrentUser() {
        return currentUser;
    }

    /**
     * Short path to check current user roles. This method equivalent to
     * {@code SecurityBean.getCurrentUser().isInRole()}
     *
     * @see UserBean#isInRole(java.lang.String[])
     */
    public static boolean isInRole(String... roles) {
        if (getCurrentUser() == null) {
            return false;
        } else {
            return getCurrentUser().isInRole(roles);
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String value) {
        String oldValue = userName;
        userName = value;
        propertySupport.firePropertyChange(USER_NAME_PROPERTY, oldValue, userName);
    }

    public String getUserPassword() {
        String result = null;
        if (userPassword != null) {
            result = new String(userPassword);
        }
        return result;
    }

    public void setUserPassword(String value) {
        char[] oldValue = userPassword;
        userPassword = value.toCharArray();
        propertySupport.firePropertyChange(USER_PASSWORD_PROPERTY, oldValue, userPassword);
    }

    /**
     * Authenticates user by calling
     * {@link WSManager#initWebServices(String, char[])} and passing user name
     * and password.
     *
     * @param config Configuration settings for the Web-services initialization.
     */
    public boolean authenticate(HashMap<String, String> config) throws WebServiceClientException, SOLAException {
        boolean result = false;
        if (userName != null && userPassword != null && !userName.equals("") && userPassword.length > 0) {
            result = authenticate(userName, userPassword, config);
        } else {
            throw new SOLAException(ClientMessage.CHECK_INVALID_USERNAME_PASSWORD);
        }
        return result;
    }

    /**
     * Authenticates user by calling
     * {@link WSManager#initWebServices(String, char[])} and passing user name
     * and password.
     *
     * @param userName Username to be authenticated.
     * @param password User password.
     * @param config Configuration settings for the Web-services initialization.
     */
    public static boolean authenticate(String userName, char[] password, HashMap<String, String> config)
            throws WebServiceClientException, SOLAException {
        boolean result = false;
        if (userName != null && password != null && !userName.equals("") && password.length > 0) {
            // Initialize web services
            result = WSManager.getInstance().initWebServices(userName, password, config);
            if (result) {
                // Set current user
                UserBean user = TypeConverters.TransferObjectToBean(
                        WSManager.getInstance().getAdminService().getCurrentUser(),
                        UserBean.class, null);
                currentUser = user;
            }
        } else {
            throw new SOLAException(ClientMessage.CHECK_INVALID_USERNAME_PASSWORD);
        }
        return result;
    }

    /**
     * Determines if the users password is due to expire soon or has been set by
     * an administrator and requires change.
     *
     * @param showMessage If true, a message will be displayed to the user
     * requesting they change their password. If false, no message will be
     * displayed.
     * @return true if the user needs to change their password, false otherwise.
     */
    public static boolean isPasswordChangeReqd(boolean showMessage) {
        boolean result = false;
        if (getCurrentUser().getPwordExpiryDays() != null
                && getCurrentUser().getPwordExpiryDays() <= WARN_OF_PWORD_EXPIRY) {
            result = true;
            if (showMessage) {
                if (getCurrentUser().getPwordExpiryDays() <= 0) {
                    // Admin users can login after thier password has expired so display a different
                    // message if thier password is now expired. 
                    MessageUtility.displayMessage(ClientMessage.SECURITY_PASSWORD_EXPIRED);
                } else {
                    // The password will soon expire, so warn the user
                    MessageUtility.displayMessage(ClientMessage.SECURITY_WARN_PWORD_EXPIRY,
                            new Object[]{getCurrentUser().getPwordExpiryDays()});
                }
            }
            LogUtility.log("User password expires in " + getCurrentUser().getPwordExpiryDays() + " days.");
        } else if (!getCurrentUser().getUserName().equals(getCurrentUser().getLastPwordChangeUser())) {
            // The user was not the last person to change their password. It was most likely
            // set by an Administrator. Tell them to change their password so that it remains secure.
            result = true;
            if (showMessage) {
                MessageUtility.displayMessage(ClientMessage.SECURITY_PASSWORD_SET_BY_ADMIN);
            }
        }
        return result;
    }

    /**
     * Returns SHA-256 hash for the password.
     *
     * @param password Password string to hash.
     */
    public static String getPasswordHash(String password) {
        String hashString = null;

        if (password != null && password.length() > 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes("UTF-8"));
                byte[] hash = md.digest();

                // Ticket #410 - Fix password encyption. Ensure 0 is prepended
                // if the hex length is == 1 
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hash.length; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if (hex.length() == 1) {
                        sb.append('0');
                    }
                    sb.append(hex);
                }

                hashString = sb.toString();

            } catch (Exception e) {
                e.printStackTrace(System.err);
                return null;
            }
        }

        return hashString;
    }

    /**
     * Checks the Security Classification to ensure the user has the appropriate
     * clearance to view the record details. The security clearance assigned to
     * a user will grant them access to all records that have the same or lower
     * security classification. Where a specialty classification is being used,
     * the user must have that clearance assigned (as a security role) or have a
     * clearance level of Top Secret.
     *
     * @param classificationCode The security classification to check
     * @return true user has the appropriate security clearance to view the
     * entity, false otherwise.
     */
    public static boolean hasSecurityClearance(String classificationCode) {
        boolean result;
        if (StringUtility.isEmpty(classificationCode)
                || RolesConstants.CLASSIFICATION_UNRESTRICTED.equals(classificationCode)) {
            result = true;
        } else if (RolesConstants.CLASSIFICATION_RESTRICTED.equals(classificationCode)) {
            result = isInRole(RolesConstants.CLASSIFICATION_RESTRICTED,
                    RolesConstants.CLASSIFICATION_CONFIDENTIAL,
                    RolesConstants.CLASSIFICATION_SECRET,
                    RolesConstants.CLASSIFICATION_TOPSECRET);
        } else if (RolesConstants.CLASSIFICATION_CONFIDENTIAL.equals(classificationCode)) {
            result = isInRole(RolesConstants.CLASSIFICATION_CONFIDENTIAL,
                    RolesConstants.CLASSIFICATION_SECRET,
                    RolesConstants.CLASSIFICATION_TOPSECRET);
        } else if (RolesConstants.CLASSIFICATION_SECRET.equals(classificationCode)) {
            result = isInRole(RolesConstants.CLASSIFICATION_SECRET,
                    RolesConstants.CLASSIFICATION_TOPSECRET);
        } else if (RolesConstants.CLASSIFICATION_TOPSECRET.equals(classificationCode)) {
            result = isInRole(RolesConstants.CLASSIFICATION_TOPSECRET);
        } else {
            // Specialty Classification so allow users with that clearance or 
            // TOP SECRET to view it. 
            result = isInRole(classificationCode,
                    RolesConstants.CLASSIFICATION_TOPSECRET);
        }
        return result;
    }
}
