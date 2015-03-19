/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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

import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.webservices.transferobjects.security.UserGroupTO;

/** 
 * Holds group id of user, related to the user ({@link UserBean}). 
 * Could be populated from the {@link UserGroupTO} object.<br />
 * For more information on the groups, related to user 
 * see in the data dictionary <b>System</b> schema.
 */
public class UserGroupBean extends AbstractVersionedBean {
    public static final String GROUP_ID_PROPERTY = "groupId";
    public static final String USER_ID_PROPERTY = "userId";

    private String groupId;
    private String userId;
    
    public UserGroupBean() {
        super();
    }
    
    public UserGroupBean(String groupId) {
        super();
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        String oldValue = this.groupId;
        this.groupId = groupId;
        propertySupport.firePropertyChange(GROUP_ID_PROPERTY, oldValue, this.groupId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        String oldValue = this.userId;
        this.userId = userId;
        propertySupport.firePropertyChange(USER_ID_PROPERTY, oldValue, this.userId);
    }
}
