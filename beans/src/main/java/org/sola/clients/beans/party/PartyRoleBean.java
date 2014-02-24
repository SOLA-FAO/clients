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
package org.sola.clients.beans.party;

import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.PartyRoleTypeBean;

/** 
 * Represents party role object in the domain model. 
 * Could be populated from the {@link PartyRoleTO} object.<br />
 * For more information see data dictionary <b>Party</b> schema.
 * <br />This bean is used as a part of {@link PartyBean}.
 */
public class PartyRoleBean extends AbstractVersionedBean{
    public static final String ROLE_CODE_PROPERTY = "roleCode";
    public static final String ROLE_PROPERTY = "role";
    
    private PartyRoleTypeBean role;
    
    public PartyRoleBean(){
        super();
    }

    public PartyRoleTypeBean getRole() {
        if (role == null) {
            role = new PartyRoleTypeBean();
        }
        return role;
    }

    public void setRole(PartyRoleTypeBean role) {
        if(this.role==null){
            this.role = new PartyRoleTypeBean();
        }
        this.setJointRefDataBean(this.role, role, ROLE_PROPERTY);
    }

    public String getRoleCode() {
        return role.getCode();
    }

    public void setRoleCode(String roleCode) {
        String oldValue = getRole().getCode();
        setRole(CacheManager.getBeanByCode(CacheManager.getPartyRoles(), roleCode));
        propertySupport.firePropertyChange(ROLE_CODE_PROPERTY, oldValue, roleCode);
    }
}
