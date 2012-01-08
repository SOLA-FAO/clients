/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.validator.constraints.Email;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.address.AddressBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.CommunicationTypeBean;
import org.sola.clients.beans.referencedata.GenderTypeBean;
import org.sola.clients.beans.referencedata.PartyRoleTypeBean;
import org.sola.clients.beans.referencedata.IdTypeBean;
import org.sola.clients.beans.referencedata.PartyTypeBean;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.PartyTO;

/** 
 * Represents party object in the domain model. 
 * Could be populated from the {@link PartyTO} object.<br />
 * For more information see data dictionary <b>Party</b> schema.
 * <br />This bean is used as a part of {@link ApplicationBean}.
 */
public class PartyBean extends PartySummaryBean implements Serializable {

    public static final String EMAIL_PROPERTY = "email";
    public static final String PREFERRED_COMMUNICATION_CODE_PROPERTY = "preferredCommunicationCode";
    public static final String PREFERRED_COMMUNICATION_PROPERTY = "preferredCommunication";
    public static final String PHONE_PROPERTY = "phone";
    public static final String MOBILE_PROPERTY = "mobile";
    public static final String GENDER_TYPE_CODE_PROPERTY = "genderTypeCode";
    public static final String ID_TYPE_CODE_PROPERTY = "idTypeCode";
    public static final String SELECTED_ROLE_PROPERTY = "selectedRole";
    public static final String GENDER_TYPE_PROPERTY = "genderType";
    public static final String ID_TYPE_PROPERTY = "idType";
    public static final String IDNUMBER_PROPERTY = "idNumber";
    public static final String FAX_PROPERTY = "fax";
    public static final String FATHERSNAME_PROPERTY = "fathersName";
    public static final String GRANDFATHERSNAME_PROPERTY = "fathersLastName";
    public static final String ALIAS_PROPERTY = "alias";
    @Email(message = "Invalid email format.")
    private String email;
    private String phone;
    private String mobile;
    private String idNumber;
    private String fax;
    private String fathersName;
    private String fathersLastName;
    private String alias;
    private AddressBean addressBean;
    private GenderTypeBean genderTypeBean;
    private IdTypeBean idTypeBean;
    private CommunicationTypeBean communicationTypeBean;
    private SolaList<PartyRoleBean> roleList;
    private PartyRoleBean selectedRole;

    /** 
     * Default constructor to create party bean. Initializes 
     * {@link CommunicationTypeBean} as a part of this bean.
     */
    public PartyBean() {
        super();
        genderTypeBean = new GenderTypeBean();
        idTypeBean = new IdTypeBean();
        communicationTypeBean = new CommunicationTypeBean();
        roleList = new SolaList();
    }

    public void clean() {
        this.setId(UUID.randomUUID().toString());
        this.setEmail(null);
        this.setPhone(null);
        this.setMobile(null);
        this.setIdNumber(null);
        this.setFax(null);
        this.setFathersName(null);
        this.setAlias(null);
        this.setGenderType(new GenderTypeBean());
        this.setIdType(new IdTypeBean());
        this.setPreferredCommunication(new CommunicationTypeBean());
        this.setName(null);
        this.setLastName(null);
        this.setExtId(null);
        this.setType(new PartyTypeBean());
        this.setAddress(new AddressBean());
        roleList.clear();
        this.setSelectedRole(null);
    }

    public AddressBean getAddress() {
        if (addressBean == null) {
            addressBean = new AddressBean();
        }
        return addressBean;
    }

    public void setAddress(AddressBean addressBean) {
        this.addressBean = addressBean;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        String oldValue = email;
        this.email = value;
        propertySupport.firePropertyChange(EMAIL_PROPERTY, oldValue, this.email);
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String value) {
        String oldValue = fax;
        this.fax = value;
        propertySupport.firePropertyChange(FAX_PROPERTY, oldValue, this.fax);
    }

    public String getFathersName() {
        return fathersName;
    }

    public void setFathersName(String value) {
        String oldValue = fathersName;
        fathersName = value;
        propertySupport.firePropertyChange(FATHERSNAME_PROPERTY, oldValue, value);
    }

    public String getFathersLastName() {
        return fathersLastName;
    }

    public void setFathersLastName(String value) {
        String oldValue = fathersLastName;
        fathersLastName = value;
        propertySupport.firePropertyChange(GRANDFATHERSNAME_PROPERTY, oldValue, value);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String value) {
        String oldValue = alias;
        alias = value;
        propertySupport.firePropertyChange(ALIAS_PROPERTY, oldValue, value);
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String value) {
        String oldValue = idNumber;
        idNumber = value;
        propertySupport.firePropertyChange(IDNUMBER_PROPERTY, oldValue, value);
    }

    public GenderTypeBean getGenderType() {
        if (genderTypeBean == null) {
            genderTypeBean = new GenderTypeBean();
        }
        return genderTypeBean;
    }

    public void setGenderType(GenderTypeBean genderTypeBean) {
        if (this.genderTypeBean == null) {
            this.genderTypeBean = new GenderTypeBean();
        }
        this.setJointRefDataBean(this.genderTypeBean, genderTypeBean, GENDER_TYPE_PROPERTY);
    }

    public String getGenderCode() {
        return genderTypeBean.getCode();
    }

    public void setGenderCode(String value) {
        String oldValue = genderTypeBean.getCode();
        setGenderType(CacheManager.getBeanByCode(CacheManager.getGenderTypes(), value));
        propertySupport.firePropertyChange(GENDER_TYPE_CODE_PROPERTY, oldValue, value);
    }

    public void setIdType(IdTypeBean idTypeBean) {
        if (this.idTypeBean == null) {
            this.idTypeBean = new IdTypeBean();
        }
        this.setJointRefDataBean(this.idTypeBean, idTypeBean, ID_TYPE_PROPERTY);
    }

    public IdTypeBean getIdType() {
        return this.idTypeBean;
    }

    public String getIdTypeCode() {
        return idTypeBean.getCode();
    }

    public void setIdTypeCode(String value) {
        String oldValue = idTypeBean.getCode();
        setIdType(CacheManager.getBeanByCode(CacheManager.getIdTypes(), value));
        propertySupport.firePropertyChange(ID_TYPE_CODE_PROPERTY, oldValue, value);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String value) {
        String oldValue = mobile;
        mobile = value;
        propertySupport.firePropertyChange(MOBILE_PROPERTY, oldValue, value);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String value) {
        String oldValue = phone;
        this.phone = value;
        propertySupport.firePropertyChange(PHONE_PROPERTY, oldValue, this.phone);
    }

    public PartyRoleBean getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(PartyRoleBean selectedRole) {
        this.selectedRole = selectedRole;
        propertySupport.firePropertyChange(SELECTED_ROLE_PROPERTY, null, selectedRole);
    }

    public CommunicationTypeBean getPreferredCommunication() {
        return communicationTypeBean;
    }

    public void setPreferredCommunication(CommunicationTypeBean communicationTypeBean) {
        if (this.communicationTypeBean == null) {
            this.communicationTypeBean = new CommunicationTypeBean();
        }
        this.setJointRefDataBean(this.communicationTypeBean, communicationTypeBean, PREFERRED_COMMUNICATION_PROPERTY);
    }

    public String getPreferredCommunicationCode() {
        String result = null;
        if (communicationTypeBean != null) {
            result = communicationTypeBean.getCode();
        }
        return result;
    }

    public ObservableList<PartyRoleBean> getFilteredRoleList() {
        return roleList.getFilteredList();
    }

    public SolaList<PartyRoleBean> getRoleList() {
        return roleList;
    }

    public void setRoleList(SolaList<PartyRoleBean> roleList) {
        this.roleList = roleList;
    }

    /** 
     * Sets preferred communication code and retrieves {@link CommunicationTypeBean} 
     * object related to the given code from the cache. 
     * @param value Preferred communication code.
     */
    public void setPreferredCommunicationCode(String value) {
        String oldValue = communicationTypeBean.getCode();
        setPreferredCommunication(CacheManager.getBeanByCode(
                CacheManager.getCommunicationTypes(), value));
        propertySupport.firePropertyChange(PREFERRED_COMMUNICATION_PROPERTY, oldValue, value);
    }

    /** Checks if role already exists in the list. */
    public boolean checkRoleExists(PartyRoleTypeBean partyRoleTypeBean) {
        if (roleList != null && partyRoleTypeBean != null) {
            for (PartyRoleBean roleBean : roleList) {
                if (roleBean.getRoleCode().equals(partyRoleTypeBean.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 
     * Adds new role ({@link PartyRoleTypeBean}) into party.
     * @param partyRoleBean Role.
     */
    public void addRole(PartyRoleTypeBean partyRoleTypeBean) {
        if (roleList != null && partyRoleTypeBean != null) {
            PartyRoleBean newRole = new PartyRoleBean();
            newRole.setEntityAction(EntityAction.INSERT);
            newRole.setRole(partyRoleTypeBean);
            roleList.addAsNew(newRole);
        }
    }

    /** Removes selected role from the list for  party.*/
    public void removeSelectedRole() {
        if (selectedRole != null && roleList != null) {
            roleList.safeRemove(selectedRole, EntityAction.DELETE);
        }
    }

    /** 
     * Creates new party in the database. 
     * @throws Exception
     */
    public boolean createParty() throws Exception {
        PartyTO party = TypeConverters.BeanToTrasferObject(this, PartyTO.class);
        if (getAddress() != null && (getAddress().getDescription() == null
                || getAddress().getDescription().length() < 1)) {
            party.setAddress(null);
        }
        party = WSManager.getInstance().getCaseManagementService().createParty(party);
        TypeConverters.TransferObjectToBean(party, PartyBean.class, this);
        return true;
    }

    /** 
     * Saves changes to the party into the database. 
     * @throws Exception
     */
    public boolean saveParty() throws Exception {
        PartyTO party = TypeConverters.BeanToTrasferObject(this, PartyTO.class);

        if (getAddress() != null && getAddress().isNew() && (getAddress().getDescription() == null
                || getAddress().getDescription().length() < 1)) {
            party.setAddress(null);
        } else if (getAddress() != null && !getAddress().isNew() && (getAddress().getDescription() == null
                || getAddress().getDescription().length() < 1)) {
            party.getAddress().setEntityAction(EntityAction.DISASSOCIATE);
        }
        
        party = WSManager.getInstance().getCaseManagementService().saveParty(party);

        TypeConverters.TransferObjectToBean(party, PartyBean.class, this);



        return true;
    }
}
