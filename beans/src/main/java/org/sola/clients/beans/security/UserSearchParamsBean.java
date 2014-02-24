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

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.webservices.transferobjects.search.UserSearchParamsTO;

/**
 * Contains properties used as the parameters to search users.
 * Could be populated from the {@link UserSearchParamsTO} object.<br />
 */
public class UserSearchParamsBean extends AbstractBindingBean {
    
    public static final String GROUP_ID_PROPERTY = "groupId";
    public static final String USER_NAME_PROPERTY = "userName";
    public static final String FIRST_NAME_PROPERTY = "firstName";
    public static final String LAST_NAME_PROPERTY = "lastName";
    public static final String GROUP_BEAN_PROPERTY = "groupBean";
    
    private String groupId;
    private String userName;
    private String firstName;
    private String lastName;
    private GroupSummaryBean groupBean;
    
    public UserSearchParamsBean(){
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        String oldValue = this.firstName;
        this.firstName = firstName;
        propertySupport.firePropertyChange(FIRST_NAME_PROPERTY, oldValue, this.firstName);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        String oldValue = this.groupId;
        this.groupId = groupId;
        propertySupport.firePropertyChange(GROUP_ID_PROPERTY, oldValue, this.groupId);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        String oldValue = this.lastName;
        this.lastName = lastName;
        propertySupport.firePropertyChange(LAST_NAME_PROPERTY, oldValue, this.lastName);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        String oldValue = this.userName;
        this.userName = userName;
        propertySupport.firePropertyChange(USER_NAME_PROPERTY, oldValue, this.userName);
    }

    public GroupSummaryBean getGroupBean() {
        return groupBean;
    }

    public void setGroupBean(GroupSummaryBean groupBean) {
        this.groupBean = groupBean;
        propertySupport.firePropertyChange(GROUP_BEAN_PROPERTY, null, this.groupBean);
        if(groupBean!=null){
            setGroupId(groupBean.getId());
        }else{
            setGroupId(null);
        }
    }
}
