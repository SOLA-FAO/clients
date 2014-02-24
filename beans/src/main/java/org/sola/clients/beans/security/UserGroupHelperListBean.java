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
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Holds list of {@link UserGroupHelperBean} objects for binding on the form.
 */
public class UserGroupHelperListBean extends AbstractBindingBean {
    
    private class UserGroupHelperListener implements PropertyChangeListener, Serializable{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getPropertyName().equals(UserGroupHelperBean.IS_IN_USER_GROUPS_PROPERTY) && 
                    evt.getNewValue() != evt.getOldValue() && triggerUserGroupUpdates){
                
                if(userGroups !=null){
                    UserGroupHelperBean userGroupHelper = (UserGroupHelperBean)evt.getSource();
                    boolean isInList = false;
                    
                    for(UserGroupBean userGroup : userGroups){
                        if(userGroup.getGroupId().equals(userGroupHelper.getGroupSummary().getId())){
                            isInList = true;
                            if(!userGroupHelper.isInUserGroups()){
                                userGroups.safeRemove(userGroup, EntityAction.DELETE);
                            }
                            break;
                        }
                    }
                    
                    if(!isInList && userGroupHelper.isInUserGroups()){
                        UserGroupBean userGroup = new UserGroupBean(userGroupHelper.getGroupSummary().getId());
                        userGroups.add(userGroup);
                    }
                }
            }
        }
    }
    
    private SolaObservableList<UserGroupHelperBean> userGroupHelpers;
    private SolaList<UserGroupBean> userGroups;
    private boolean triggerUserGroupUpdates = true;
    
    public UserGroupHelperListBean() {
        super();
        userGroupHelpers = new SolaObservableList<UserGroupHelperBean>();
        UserGroupHelperListener listener = new  UserGroupHelperListener();
        
        GroupSummaryListBean groupsSummaryList = new GroupSummaryListBean();
        groupsSummaryList.loadGroups(false);
        
        for(GroupSummaryBean groupSummary : groupsSummaryList.getGroupSummaryList()){
            UserGroupHelperBean userGroupHelper = new UserGroupHelperBean(false, groupSummary);
            userGroupHelper.addPropertyChangeListener(listener);
            userGroupHelpers.add(userGroupHelper);
        }
    }

    public ObservableList<UserGroupHelperBean> getUserGroupHelpers() {
        return userGroupHelpers;
    }

    /** Returns list of {@link UserGroupBean}, if it is set. */
    public SolaList<UserGroupBean> getUserGroups() {
        return userGroups;
    }

    /** 
     * Sets list of {@link UserGroupBean}, which is managed, based on checks of 
     * the groups in the list of {@link UserGroupHelperBean}. 
     */
    public void setUserGroups(SolaList<UserGroupBean> userGroups) {
        this.userGroups = userGroups;
        setChecks(userGroups);
    }
    
    /** Sets or removes checks from the groups, based on provided {@link UserGroupBean} object. */
    public void setChecks(SolaList<UserGroupBean> userGroups) {
        triggerUserGroupUpdates=false;
        for (UserGroupHelperBean userGroupHelper : userGroupHelpers) {

            userGroupHelper.setInUserGroups(false);

            if (userGroups != null) {
                for (UserGroupBean userGroup : userGroups) {
                    if (userGroupHelper.getGroupSummary().getId()
                            .equals(userGroup.getGroupId())) {
                        userGroupHelper.setInUserGroups(true);
                    }
                }
            }
        }
        triggerUserGroupUpdates=true;
    }
}
