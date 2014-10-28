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
package org.sola.clients.beans.application;

import java.util.Date;
import javax.validation.constraints.NotNull;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.PublicDisplayItemStatusBean;
import org.sola.clients.beans.referencedata.PublicDisplayItemTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.PublicDisplayItemTO;

/**
 *
 * @author soladev
 */
public class PublicDisplayItemBean extends AbstractVersionedBean {

    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String TYPE_PROPERTY = "type";
    public static final String REFERENCE_NR_PROPERTY = "referenceNr";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String DISPLAY_FROM_PROPERTY = "displayFrom";
    public static final String DISPLAY_TO_PROPERTY = "displayTo";
    public static final String SERVICE_ID_PROPERTY = "serviceId";
    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String STATUS_PROPERTY = "status";
    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    public static final String PUBLIC_DISPLAY_ITEM_BEAN_PROPERTY = "publicDisplayItemBean";
    private String id;
    private String serviceId;
    @NotNull(message = ClientMessage.CHECK_FIELD_DISPLAY_ITEM_TYPE, payload = Localized.class)
    private PublicDisplayItemTypeBean type;
    private String referenceNr;
    private String description;
    @NotNull(message = ClientMessage.CHECK_FIELD_DISPLAY_ITEM_STATUS, payload = Localized.class)
    private PublicDisplayItemStatusBean status;
    private Date displayFrom;
    private Date displayTo;
    private SolaList<SourceBean> sourceList;
    private SourceBean selectedSource;

    public PublicDisplayItemBean() {
        super();
        sourceList = new SolaList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String value) {
        String oldValue = serviceId;
        serviceId = value;
        propertySupport.firePropertyChange(SERVICE_ID_PROPERTY, oldValue, value);
    }

    public String getTypeCode() {
        return type == null ? null : type.getCode();
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (type != null) {
            oldValue = type.getCode();
        }
        setType(CacheManager.getBeanByCode(
                CacheManager.getPublicDisplayTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public PublicDisplayItemTypeBean getType() {
        return type;
    }

    public void setType(PublicDisplayItemTypeBean type) {
        if (this.type == null) {
            this.type = new PublicDisplayItemTypeBean();
        }
        this.setJointRefDataBean(this.type, type, TYPE_PROPERTY);
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
                CacheManager.getPublicDisplayStatusTypes(), statusCode));
        propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, oldValue, statusCode);
    }

    public PublicDisplayItemStatusBean getStatus() {
        return status;
    }

    public void setStatus(PublicDisplayItemStatusBean status) {
        if (this.status == null) {
            this.status = new PublicDisplayItemStatusBean();
        }
        this.setJointRefDataBean(this.status, status, STATUS_PROPERTY);
    }

    public String getReferenceNr() {
        return referenceNr;
    }

    public void setReferenceNr(String value) {
        String oldValue = referenceNr;
        referenceNr = value;
        propertySupport.firePropertyChange(REFERENCE_NR_PROPERTY, oldValue, value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        String oldValue = description;
        description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, value);
    }

    public Date getDisplayFrom() {
        return displayFrom;
    }

    public void setDisplayFrom(Date value) {
        Date oldValue = displayFrom;
        displayFrom = value;
        propertySupport.firePropertyChange(DISPLAY_FROM_PROPERTY, oldValue, value);
    }

    public Date getDisplayTo() {
        return displayTo;
    }

    public void setDisplayTo(Date value) {
        Date oldValue = displayTo;
        displayTo = value;
        propertySupport.firePropertyChange(DISPLAY_TO_PROPERTY, oldValue, value);
    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public ObservableList<SourceBean> getSourceFilteredList() {
        return sourceList.getFilteredList();
    }

    public SourceBean getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedSource(SourceBean value) {
        selectedSource = value;
        propertySupport.firePropertyChange(SELECTED_SOURCE_PROPERTY, null, value);
    }

    /**
     * Removes selected document from the list of documents.
     */
    public void removeSelectedSource() {
        if (selectedSource != null && sourceList != null) {
            sourceList.safeRemove(selectedSource, EntityAction.DISASSOCIATE);
        }
    }

    /**
     * Saves the item details to the database
     *
     * @retun true if the item is successfully saved, otherwise a runtime
     * exception.
     */
    public boolean saveItem() {
        PublicDisplayItemTO item = TypeConverters.BeanToTrasferObject(this, PublicDisplayItemTO.class);
        item = WSManager.getInstance().getCaseManagementService().savePublicDisplayItem(item);
        TypeConverters.TransferObjectToBean(item, PublicDisplayItemBean.class, this);
        propertySupport.firePropertyChange(PUBLIC_DISPLAY_ITEM_BEAN_PROPERTY, null, this);
        return true;
    }
}
