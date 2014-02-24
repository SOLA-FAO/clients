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

import javax.validation.constraints.Size;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.security.GroupTO;

/** 
 * Represents user <b>group</b> object in the domain model. 
 * Could be populated from the {@link GroupTO} object.<br />
 * For more information on the properties of the <code>group</code> 
 * see data dictionary <b>System</b> schema.
 */
public class GroupBean extends GroupSummaryBean {
    
    public static final String GROUP_ROLES_PROPERTY = "groupRoles";
    
    
    @Size(min=1, message= ClientMessage.CHECK_MIN_GROUPROLES, payload=Localized.class)
    private SolaList<GroupRoleBean> groupRoles;
    
    public GroupBean(){
        super();
        groupRoles=new SolaList<GroupRoleBean>();
    }

    public SolaList<GroupRoleBean> getGroupRoles() {
        return groupRoles;
    }

    public void setGroupRoles(SolaList<GroupRoleBean> groupRoles) {
        this.groupRoles = groupRoles;
        propertySupport.firePropertyChange(GROUP_ROLES_PROPERTY, null, this.groupRoles);
    }
    
    /** Saves group. */
    public void save(){
        GroupTO groupTO = TypeConverters.BeanToTrasferObject(this, GroupTO.class);
        groupTO = WSManager.getInstance().getAdminService().saveGroup(groupTO);
        TypeConverters.TransferObjectToBean(groupTO, GroupBean.class, this);
    }
    
    /** Returns group by the given ID */
    public static GroupBean getGroup(String groupId){
        return TypeConverters.TransferObjectToBean(WSManager.getInstance()
                .getAdminService().getGroup(groupId), GroupBean.class, null);
    }
    
    /** Removes group */
    public static void removeGroup(String groupId){
        GroupTO groupTO = WSManager.getInstance().getAdminService().getGroup(groupId);
        groupTO.setEntityAction(EntityAction.DELETE);
        WSManager.getInstance().getAdminService().saveGroup(groupTO);
    }
}
