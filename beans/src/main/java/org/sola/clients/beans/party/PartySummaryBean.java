/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.party;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.party.validation.PartyIndividualValidationGroup;
import org.sola.clients.beans.referencedata.PartyTypeBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.PartySummaryTO;
import org.sola.webservices.transferobjects.casemanagement.PartyTO;

/**
 * Represents summary object of the {@link PartyBean}. Could be populated from the {@link PartySummaryTO}
 * object.<br /> For more information see data dictionary <b>Party</b> schema. <br />This bean is
 * used as a part of {@link ApplicationBean}.
 */
public class PartySummaryBean extends AbstractIdBean {

    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String NAME_PROPERTY = "name";
    public static final String LASTNAME_PROPERTY = "lastName";
    public static final String EXTID_PROPERTY = "extId";
    public static final String TYPE_PROPERTY = "type";
    public static final String IS_RIGHTHOLDER_PROPERTY = "rightHolder";
    public static final String ROLE_CODE_PROPERTY = "roleCode";
    
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_NAME, payload = Localized.class)
    @Length(max = 255, message =  ClientMessage.CHECK_FIELD_INVALID_LENGTH_NAME, payload=Localized.class)
    private String name;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_LASTNAME, payload = Localized.class, groups = PartyIndividualValidationGroup.class)
    @Length(max = 50, message =  ClientMessage.CHECK_FIELD_INVALID_LENGTH_LASTNAME, payload=Localized.class)
    private String lastName;
    private String extId;
    private boolean rightHolder;
    private PartyTypeBean typeBean;

    public PartySummaryBean() {
        super();
        typeBean = new PartyTypeBean();
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String value) {
        String oldValue = extId;
        extId = value;
        propertySupport.firePropertyChange(EXTID_PROPERTY, oldValue, value);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        String oldValue = lastName;
        lastName = value;
        propertySupport.firePropertyChange(LASTNAME_PROPERTY, oldValue, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        String oldValue = name;
        name = value;
        propertySupport.firePropertyChange(NAME_PROPERTY, oldValue, value);
    }

    /**
     * @return The full name of the party  being the concatenation of the name and 
     * lastName properties separated by a space.
     */
    public String getFullName() {
        String fullName = getName() == null ? "" : getName();
        fullName = getLastName() == null ? fullName : fullName + " " + getLastName(); 
        return fullName.trim(); 
    }

    public PartyTypeBean getType() {
        return typeBean;
    }

    public void setType(PartyTypeBean typeBean) {
        if (this.typeBean == null) {
            this.typeBean = new PartyTypeBean();
        }
        this.setJointRefDataBean(this.typeBean, typeBean, TYPE_PROPERTY);
    }

    public String getTypeCode() {
        return typeBean.getCode();
    }

    public void setTypeCode(String value) {
        String oldValue = typeBean.getCode();
        setType(CacheManager.getBeanByCode(CacheManager.getPartyTypes(), value));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, value);
    }

    public PartyBean getPartyBean() {
        PartyTO party = WSManager.getInstance().getCaseManagementService().getParty(this.getId());
        return TypeConverters.TransferObjectToBean(party, PartyBean.class, null);
    }

    public boolean isRightHolder() {
        return rightHolder;
    }

    public void setRightHolder(boolean rightHolder) {
        boolean oldValue = this.rightHolder;
        this.rightHolder = rightHolder;
        propertySupport.firePropertyChange(IS_RIGHTHOLDER_PROPERTY, oldValue, this.rightHolder);
    }

    @Override
    public String toString() {
        if ( !(String.format("%s", lastName).isEmpty())&&String.format("%s", lastName)==null&&String.format("%s", lastName)==""){
            return String.format("%s %s", name, lastName);    
        } else {
            return String.format("%s", name);
        }   
    }
}
