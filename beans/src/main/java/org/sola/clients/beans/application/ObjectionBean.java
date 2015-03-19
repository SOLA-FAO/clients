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

import java.util.Date;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.administrative.BaUnitSummaryBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.AuthorityBean;
import org.sola.clients.beans.referencedata.ObjectionStatusBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.ObjectionTO;

/**
 * Represents objection comment object. Could be populated from the
 * {@link ObjectionTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class ObjectionBean extends AbstractIdBean {

    public static final String SERVICE_ID_PROPERTY = "serviceId";
    public static final String NR_PROPERTY = "nr";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String LODGED_DATE_PROPERTY = "lodgedDate";
    public static final String RESOLUTION_DATE_PROPERTY = "resolutionDate";
    public static final String RESOLUTION_PROPERTY = "resolution";
    public static final String AUTHORITY_PROPERTY = "authority";
    public static final String AUTHORITY_CODE_PROPERTY = "authorityCode";
    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String STATUS_PROPERTY = "status";
    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    public static final String SELECTED_COMMENT_PROPERTY = "selectedComment";
    public static final String SELECTED_PARTY_PROPERTY = "selectedParty";
    public static final String SELECTED_PROPERTY_PROPERTY = "selectedProperty";
    public static final String OBJECTION_BEAN_PROPERTY = "ObjectionBean";
    private String serviceId;
    private String nr;
    private ObjectionStatusBean status;
    private Date lodgedDate;
    private Date resolutionDate;
    private String description;
    private String resolution;
    private AuthorityBean authority;
    private SolaList<ObjectionCommentBean> commentList;
    private ObjectionCommentBean selectedComment;
    private SolaList<SourceBean> sourceList;
    private SourceBean selectedSource;
    private SolaList<PartySummaryBean> partyList;
    private PartySummaryBean selectedParty;
    private SolaList<BaUnitSummaryBean> propertyList;
    private BaUnitSummaryBean selectedProperty;

    public ObjectionBean() {
        super();
        commentList = new SolaList();
        sourceList = new SolaList();
        partyList = new SolaList();
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

    public String getNr() {
        return nr;
    }

    public void setNr(String value) {
        String oldValue = this.nr;
        this.nr = value;
        propertySupport.firePropertyChange(NR_PROPERTY, oldValue, value);
    }

    public String getStatusCode() {
        return status == null ? null : status.getCode();
    }

    public void setStatusCode(String statusCode) {
        String oldValue = null;
        if (status != null) {
            oldValue = status.getCode();
        }
        setStatus(CacheManager.getBeanByCode(
                CacheManager.getObjectionStatusTypes(), statusCode));
        propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, oldValue, statusCode);
    }

    public ObjectionStatusBean getStatus() {
        return status;
    }

    public void setStatus(ObjectionStatusBean status) {
        if (this.status == null) {
            this.status = new ObjectionStatusBean();
        }
        this.setJointRefDataBean(this.status, status, STATUS_PROPERTY);
    }

    public Date getLodgedDate() {
        return lodgedDate;
    }

    public void setLodgedDate(Date lodgedDate) {
        this.lodgedDate = lodgedDate;
    }

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(Date resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {

        String oldValue = this.description;
        this.description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, value);
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String value) {
        String oldValue = this.resolution;
        this.resolution = value;
        propertySupport.firePropertyChange(RESOLUTION_PROPERTY, oldValue, value);
    }

    public String getAuthorityCode() {
        return authority == null ? null : authority.getCode();
    }

    public void setAuthorityCode(String authorityCode) {
        String oldValue = null;
        if (authority != null) {
            oldValue = authority.getCode();
        }
        setAuthority(CacheManager.getBeanByCode(CacheManager.getAuthorityTypes(), authorityCode));
        propertySupport.firePropertyChange(AUTHORITY_CODE_PROPERTY, oldValue, authorityCode);
    }

    public AuthorityBean getAuthority() {
        return authority;
    }

    public void setAuthority(AuthorityBean authority) {
        if (this.authority == null) {
            this.authority = new AuthorityBean();
        }
        this.setJointRefDataBean(this.authority, authority, AUTHORITY_PROPERTY);
    }

    public SolaList<ObjectionCommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(SolaList<ObjectionCommentBean> commentList) {
        this.commentList = commentList;
    }

    public ObjectionCommentBean getSelectedComment() {
        return selectedComment;
    }

    public void setSelectedComment(ObjectionCommentBean value) {
        this.selectedComment = value;
        propertySupport.firePropertyChange(SELECTED_COMMENT_PROPERTY, null, value);
    }

    public ObservableList<ObjectionCommentBean> getFilteredCommentList() {
        return commentList.getFilteredList();
    }

    /**
     * Adds a new comment to the commentList or updates the comment if it
     * already exists in the list
     *
     * @param comment
     */
    public void addOrUpdateComment(ObjectionCommentBean comment) {
        if (comment != null && commentList != null) {
            if (commentList.contains(comment)) {
                commentList.set(commentList.indexOf(comment), comment);
            } else {
                commentList.addAsNew(comment);
            }
        }
    }

    /**
     * Removes selected comment from the list of comments.
     */
    public void removeSelectedComment() {
        if (selectedComment != null && commentList != null) {
            commentList.safeRemove(selectedComment, EntityAction.DISASSOCIATE);
        }
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

    public SolaList<PartySummaryBean> getPartyList() {
        return partyList;
    }

    public void setPartyList(SolaList<PartySummaryBean> partyList) {
        this.partyList = partyList;
    }

    public PartySummaryBean getSelectedParty() {
        return selectedParty;
    }

    public void setSelectedParty(PartySummaryBean value) {
        this.selectedParty = value;
        propertySupport.firePropertyChange(SELECTED_PARTY_PROPERTY, null, value);
    }

    public ObservableList<PartySummaryBean> getFilteredPartyList() {
        return partyList.getFilteredList();
    }

    /**
     * Adds a new party to the partyList or updates the party if it already
     * exists in the list
     *
     * @param party
     */
    public void addOrUpdateParty(PartySummaryBean party) {
        if (party != null && partyList != null) {
            if (partyList.contains(party)) {
                partyList.set(partyList.indexOf(party), party);
            } else {
                partyList.addAsNew(party);
            }
        }
    }

    /**
     * Removes selected party from the list of parties.
     */
    public void removeSelectedParty() {
        if (selectedParty != null && partyList != null) {
            partyList.safeRemove(selectedParty, EntityAction.DISASSOCIATE);
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
