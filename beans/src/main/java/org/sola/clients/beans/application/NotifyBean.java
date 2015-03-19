/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.application;

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.administrative.BaUnitSummaryBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.referencedata.NotifyRelationshipTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.NotifyTO;

/**
 * Represents objection comment object. Could be populated from the
 * {@link NotifyTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class NotifyBean extends AbstractIdBean {

    public static final String SERVICE_ID_PROPERTY = "serviceId";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String PARTY_PROPERTY = "party";
    public static final String RELATIONSHIP_TYPE_CODE_PROPERTY = "relationshipTypeCode";
    public static final String RELATIONSHIP_TYPE_PROPERTY = "relationshipType";
    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    public static final String SELECTED_PROPERTY_PROPERTY = "selectedProperty";
    public static final String NOTIFY_BEAN_PROPERTY = "NotifyBean";
    public static final String CHECKED_PROPERTY = "checked";
    private String serviceId;
    private String description;
    private PartyBean party;
    private NotifyRelationshipTypeBean relationshipType;
    private SolaList<SourceBean> sourceList;
    private SourceBean selectedSource;
    private SolaList<BaUnitSummaryBean> propertyList;
    private BaUnitSummaryBean selectedProperty;
    private boolean checked;

    public NotifyBean() {
        super();
        sourceList = new SolaList();
        propertyList = new SolaList();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String value) {;
        String oldValue = this.serviceId;
        this.serviceId = value;
        propertySupport.firePropertyChange(SERVICE_ID_PROPERTY, oldValue, value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {

        String oldValue = this.description;
        this.description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, value);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        boolean oldValue = this.checked;
        this.checked = checked;
        propertySupport.firePropertyChange(CHECKED_PROPERTY, oldValue, this.checked);
    }

    public String getRelationshipTypeCode() {
        return relationshipType == null ? null : relationshipType.getCode();
    }

    public void setRelationshipTypeCode(String code) {
        String oldValue = null;
        if (relationshipType != null) {
            oldValue = relationshipType.getCode();
        }
        setRelationshipType(CacheManager.getBeanByCode(CacheManager.getNotifyRelationshipTypes(), code));
        propertySupport.firePropertyChange(RELATIONSHIP_TYPE_CODE_PROPERTY, oldValue, code);
    }

    public NotifyRelationshipTypeBean getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(NotifyRelationshipTypeBean authority) {
        if (this.relationshipType == null) {
            this.relationshipType = new NotifyRelationshipTypeBean();
        }
        this.setJointRefDataBean(this.relationshipType, authority, RELATIONSHIP_TYPE_PROPERTY);
    }

    public String getPartyId() {
        return party == null ? null : party.getId();
    }

    public PartyBean getParty() {
        return party;
    }

    public void setParty(PartyBean value) {
        this.party = value;
        propertySupport.firePropertyChange(PARTY_PROPERTY, null, value);
    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public SourceBean getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedSource(SourceBean value) {
        selectedSource = value;
        propertySupport.firePropertyChange(SELECTED_SOURCE_PROPERTY, null, value);
    }

    public ObservableList<SourceBean> getFilteredSourceList() {
        return sourceList.getFilteredList();
    }

    /**
     * Removes selected document from the list of documents.
     */
    public void removeSelectedSource() {
        if (selectedSource != null && sourceList != null) {
            sourceList.safeRemove(selectedSource, EntityAction.DISASSOCIATE);
        }
    }

    public SolaList<BaUnitSummaryBean> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(SolaList<BaUnitSummaryBean> propertyList) {
        this.propertyList = propertyList;
    }

    public BaUnitSummaryBean getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(BaUnitSummaryBean value) {
        this.selectedProperty = value;
        propertySupport.firePropertyChange(SELECTED_PROPERTY_PROPERTY, null, value);
    }

    public ObservableList<BaUnitSummaryBean> getFilteredPropertyList() {
        return propertyList.getFilteredList();
    }

    /**
     * Adds a new property to the propertyList or updates the property if it
     * already exists in the list
     *
     * @param property
     */
    public void addOrUpdateProperty(BaUnitSummaryBean property) {
        if (property != null && propertyList != null) {
            if (propertyList.contains(property)) {
                propertyList.set(propertyList.indexOf(property), property);
            } else {
                propertyList.addAsNew(property);
            }
        }
    }

    /**
     * Removes selected property from the list of properties.
     */
    public void removeSelectedProperty() {
        if (selectedProperty != null && selectedProperty != null) {
            propertyList.safeRemove(selectedProperty, EntityAction.DISASSOCIATE);
        }
    }

}
