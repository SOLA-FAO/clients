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
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.referencedata.GroupPartyTypeBean;


/** 
 * Represents party role object in the domain model. 
 * Could be populated from the {@link GroupPartyTO} object.<br />
 * For more information see data dictionary <b>Party</b> schema.
 * <br />This bean is used as a part of {@link PartyBean}.
 */
public class GroupPartyBean extends AbstractVersionedBean{
    public static final String GROUP_CODE_PROPERTY = "groupCode";
    public static final String GROUP_PROPERTY = "group";
    public static final String SELECTED_PARTY_MEMBER_PROPERTY = "selectedPartyMember";
    
    private SolaList<PartyMemberBean> partyMemberList;
    private transient PartyMemberBean selectedPartyMember;
    
    
    private GroupPartyTypeBean group;
    
    public GroupPartyBean(){
        super();
    }

    public SolaList<PartyMemberBean> getPartyMemberList() {
        return partyMemberList;
    }

    public void setPartyMemberList(SolaList<PartyMemberBean> partyMemberList) {
        this.partyMemberList = partyMemberList;
    }

    public PartyMemberBean getSelectedPartyMember() {
        return selectedPartyMember;
    }

    public void setSelectedPartyMember(PartyMemberBean selectedPartyMember) {
        this.selectedPartyMember = selectedPartyMember;
        propertySupport.firePropertyChange(SELECTED_PARTY_MEMBER_PROPERTY, null, selectedPartyMember);
    }
    
    public GroupPartyTypeBean getGroup() {
        if (group == null) {
            group = new GroupPartyTypeBean();
        }
        return group;
    }

    public void setGroup(GroupPartyTypeBean role) {
        if(this.group==null){
            this.group = new GroupPartyTypeBean();
        }
        this.setJointRefDataBean(this.group, group, GROUP_PROPERTY);
    }

    public String getGroupCode() {
        return group.getCode();
    }

    public void setGroupCode(String groupCode) {
        String oldValue = getGroup().getCode();
//  TODO
        //        setGroup(CacheManager.getBeanByCode(CacheManager.getPartyGroups(), groupCode));
        propertySupport.firePropertyChange(GROUP_CODE_PROPERTY, oldValue, groupCode);
    }
}
