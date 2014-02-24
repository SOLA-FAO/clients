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

import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.webservices.transferobjects.security.GroupRoleTO;

/** 
 * Holds code of role, related to the user group ({@link GroupBean}). 
 * Could be populated from the {@link GroupRoleTO} object.<br />
 * For more information on the roles, related to group 
 * see in the data dictionary <b>System</b> schema.
 */
public class GroupRoleBean extends AbstractVersionedBean {
    public static final String ROLE_CODE_PROPERTY = "roleCode";
    public static final String GROUP_ID_PROPERTY = "groupId";
    public static final String ROLE_PROPERTY = "role";
    
    private RoleBean role;
    private String groupId;
    
    public GroupRoleBean(){
        super();
    }
    
    public RoleBean getRole() {
        if (role == null) {
            role = new RoleBean();
        }
        return role;
    }

    public void setRole(RoleBean role) {
        if(this.role==null){
            this.role = new RoleBean();
        }
        this.setJointRefDataBean(this.role, role, ROLE_PROPERTY);
    }

    public String getRoleCode() {
        return getRole().getCode();
    }

    public void setRoleCode(String roleCode) {
        String oldValue = getRole().getCode();
        setRole(CacheManager.getBeanByCode(CacheManager.getRoles(), roleCode));
        propertySupport.firePropertyChange(ROLE_CODE_PROPERTY, oldValue, roleCode);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        String oldValue = this.groupId;
        this.groupId = groupId;
        propertySupport.firePropertyChange(GROUP_ID_PROPERTY, oldValue, this.groupId);
    }
}
