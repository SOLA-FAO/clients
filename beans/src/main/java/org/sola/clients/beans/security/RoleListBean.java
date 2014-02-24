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

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Holds list of {@link RoleBean} objects for binding on the form.
 */
public class RoleListBean extends AbstractBindingBean {
    public static final String SELECTED_ROLE_PROPERTY = "selectedRole";
    
    private SolaList<RoleBean> roleList;
    private RoleBean selectedRole;
    
    public RoleListBean(){
        super();
        roleList = new SolaList<RoleBean>();
    }

    /** Clears cache and loads roles.*/
    public final void loadRoles(){
        CacheManager.remove(CacheManager.ROLES_KEY);
        roleList.clear();
        for(RoleBean role : CacheManager.getRoles()){
            roleList.addAsNew(role);
        }
    }
    
    public ObservableList<RoleBean> getRoleListFiltered() {
        return roleList.getFilteredList();
    }
    
    public SolaList<RoleBean> getRoleList() {
        return roleList;
    }

    public RoleBean getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(RoleBean selectedRole) {
        RoleBean oldValue = this.selectedRole;
        this.selectedRole = selectedRole;
        propertySupport.firePropertyChange(SELECTED_ROLE_PROPERTY, oldValue, this.selectedRole);
    }
    
    public void removeSelectedRole(){
        if(selectedRole!=null){
            selectedRole.setEntityAction(EntityAction.DELETE);
            selectedRole.save();
            loadRoles();
        }
    }
}
