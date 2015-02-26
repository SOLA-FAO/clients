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
package org.sola.clients.beans.party;

import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.GroupPartyTypeBean;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.GroupPartyTO;

/**
 * Represents party group object in the domain model. Could be populated from
 * the {@link GroupPartyTO} object.<br /> For more information see data
 * dictionary <b>Party</b> schema. <br />This bean is used as a part of {@link PartyBean}.
 */
public class GroupPartyBean extends AbstractIdBean {

    public static final String GROUP_CODE_PROPERTY = "groupCode";
//    public static final String GROUP_PROPERTY = "group";
    public static final String GROUP_TYPE_PROPERTY = "group";
    public static final String SELECTED_PARTY_MEMBER_PROPERTY = "selectedPartyMember";
    private SolaList<PartyMemberBean> partyMemberList;
    private transient PartyMemberBean selectedPartyMember;
    private GroupPartyTypeBean groupType;

    public GroupPartyBean() {
        super();
        partyMemberList = new SolaList();
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

    public GroupPartyTypeBean getGroupType() {
        if (groupType == null) {
            groupType = new GroupPartyTypeBean();
        }
        return groupType;
    }

    public void setGroupType(GroupPartyTypeBean groupType) {
        this.setJointRefDataBean(getGroupType(), groupType, GROUP_TYPE_PROPERTY);
    }

    public String getGroupCode() {
        return getGroupType().getCode();
    }

    public void setGroupCode(String value) {
        String oldValue = getGroupType().getCode();
        setGroupType(CacheManager.getBeanByCode(CacheManager.getGroupPartyTypes(), value));
        propertySupport.firePropertyChange(GROUP_CODE_PROPERTY, oldValue, value);
    }

    /**
     * Saves changes to the Groupparty into the database.
     *
     * @throws Exception
     */
    public boolean saveGroupParty() {

        GroupPartyTO groupParty = TypeConverters.BeanToTrasferObject(this, GroupPartyTO.class);
        groupParty = WSManager.getInstance().getCaseManagementService().saveGroupParty(groupParty);
        TypeConverters.TransferObjectToBean(groupParty, GroupPartyBean.class, this);
        return true;
    }

    /**
     * Returns party by ID.
     */
    public static GroupPartyBean getGroupParty(String partyId) {
        if (partyId == null || partyId.length() < 1) {
            return null;
        }
        GroupPartyTO groupPartyTO = WSManager.getInstance().getCaseManagementService().getGroupParty(partyId);
        return TypeConverters.TransferObjectToBean(groupPartyTO, GroupPartyBean.class, null);
    }
}
