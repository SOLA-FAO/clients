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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Holds list of {@link GroupRoleHelperBean} objects for binding on the form.
 */
public class GroupRoleHelperListBean extends AbstractBindingBean {

    private class GroupRoleHelperListener implements PropertyChangeListener, Serializable{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getPropertyName().equals(GroupRoleHelperBean.IS_IN_GROUP_PROPERTY) && 
                    evt.getNewValue() != evt.getOldValue() && triggerGroupRoleUpdates){
                
                if(groupRoles !=null){
                    GroupRoleHelperBean groupRoleHelper = (GroupRoleHelperBean)evt.getSource();
                    boolean isInList = false;
                    
                    for(GroupRoleBean groupRole : groupRoles){
                        if(groupRole.getRoleCode().equals(groupRoleHelper.getRole().getCode())){
                            isInList = true;
                            if(!groupRoleHelper.isInGroup()){
                                groupRoles.safeRemove(groupRole, EntityAction.DELETE);
                            }
                            break;
                        }
                    }
                    
                    if(!isInList && groupRoleHelper.isInGroup()){
                        GroupRoleBean groupRole = new GroupRoleBean();
                        groupRole.setRole(groupRoleHelper.getRole());
                        groupRoles.add(groupRole);
                    }
                }
            }
        }
    }
    
    private SolaObservableList<GroupRoleHelperBean> groupRoleHelpers;
    private SolaList<GroupRoleBean> groupRoles;
    private boolean triggerGroupRoleUpdates = true;
    
    public GroupRoleHelperListBean() {
        super();
        groupRoleHelpers = new SolaObservableList<GroupRoleHelperBean>();
        GroupRoleHelperListener listener = new  GroupRoleHelperListener();
        
        if (CacheManager.getRoles() != null) {
            for (RoleBean role : CacheManager.getRoles()) {
                GroupRoleHelperBean groupRoleHelper = new GroupRoleHelperBean(role, false);
                groupRoleHelper.addPropertyChangeListener(listener);
                groupRoleHelpers.add(groupRoleHelper);
            }
        }
    }

    public ObservableList<GroupRoleHelperBean> getGroupRoleHelpers() {
        return groupRoleHelpers;
    }

    /** Returns list of {@link GroupRoleBean}, if it is set. */
    public SolaList<GroupRoleBean> getGroupRoles() {
        return groupRoles;
    }

    /** 
     * Sets list of {@link GroupRoleBean}, which is managed, based on checks of 
     * the roles in the list of {@link GroupRoleHelperBean}. 
     */
    public void setGroupRoles(SolaList<GroupRoleBean> groupRoles) {
        this.groupRoles = groupRoles;
        setChecks(groupRoles);
    }
    
    /** Sets or removes checks from the roles, based on provided {@link GroupBean} object. */
    public void setChecks(SolaList<GroupRoleBean> groupRoles) {
        triggerGroupRoleUpdates=false;
        for (GroupRoleHelperBean groupRoleHelper : groupRoleHelpers) {

            groupRoleHelper.setInGroup(false);

            if (groupRoles != null) {
                for (GroupRoleBean groupRole : groupRoles) {
                    if (groupRoleHelper.getRole().getCode().equals(groupRole.getRoleCode())) {
                        groupRoleHelper.setInGroup(true);
                    }
                }
            }
        }
        triggerGroupRoleUpdates=true;
    }
}
