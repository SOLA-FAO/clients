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

import java.math.BigDecimal;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.administrative.BaUnitSummaryBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.referencedata.NegotiateStatusBean;
import org.sola.clients.beans.referencedata.NegotiateTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.NegotiateTO;

/**
 * Represents Negotiate object. Could be populated from the {@link NegotiateTO}
 * object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class NegotiateBean extends AbstractIdBean {

    public static final String SERVICE_ID_PROPERTY = "serviceId";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String VALUATION_AMOUNT_PROPERTY = "valuationAmount";
    public static final String OFFER_AMOUNT_PROPERTY = "offerAmount";
    public static final String AGREED_AMOUNT_PROPERTY = "agreedAmount";
    public static final String TYPE_PROPERTY = "type";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String STATUS_PROPERTY = "status";
    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    public static final String BA_UNIT_PROPERTY = "baUnit";
    public static final String OBJECTION_BEAN_PROPERTY = "NegotiateBean";
    private String serviceId;
    private String baUnitId;
    private BigDecimal valuationAmount;
    private BigDecimal offerAmount;
    private BigDecimal agreedAmount;
    private String description;
    private NegotiateTypeBean type;
    private NegotiateStatusBean status;
    private BaUnitSummaryBean baUnit;
    private SolaList<SourceBean> sourceList;
    private SourceBean selectedSource;

    public NegotiateBean() {
        super();
        sourceList = new SolaList();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String value) {;
        String oldValue = this.serviceId;
        this.serviceId = value;
        propertySupport.firePropertyChange(SERVICE_ID_PROPERTY, oldValue, value);
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
                CacheManager.getNegotiateStatusTypes(), statusCode));
        propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, oldValue, statusCode);
    }

    public NegotiateStatusBean getStatus() {
        return status;
    }

    public void setStatus(NegotiateStatusBean status) {
        if (this.status == null) {
            this.status = new NegotiateStatusBean();
        }
        this.setJointRefDataBean(this.status, status, STATUS_PROPERTY);
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public BigDecimal getValuationAmount() {
        return valuationAmount;
    }

    public void setValuationAmount(BigDecimal value) {
        BigDecimal oldValue = this.valuationAmount;
        this.valuationAmount = value;
        propertySupport.firePropertyChange(VALUATION_AMOUNT_PROPERTY, oldValue, value);
    }

    public BigDecimal getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(BigDecimal value) {
        BigDecimal oldValue = this.offerAmount;
        this.offerAmount = value;
        propertySupport.firePropertyChange(OFFER_AMOUNT_PROPERTY, oldValue, value);
    }

    public BigDecimal getAgreedAmount() {
        return agreedAmount;
    }

    public void setAgreedAmount(BigDecimal value) {
        BigDecimal oldValue = this.agreedAmount;
        this.agreedAmount = value;
        propertySupport.firePropertyChange(AGREED_AMOUNT_PROPERTY, oldValue, value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        String oldValue = this.description;
        this.description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, value);
    }

    public BaUnitSummaryBean getBaUnit() {
        return baUnit;
    }

    public void setBaUnit(BaUnitSummaryBean value) {
        this.baUnit = value;
        propertySupport.firePropertyChange(BA_UNIT_PROPERTY, null, value);
    }

    public String getTypeCode() {
        return type == null ? null : type.getCode();
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (type != null) {
            oldValue = type.getCode();
        }
        setType(CacheManager.getBeanByCode(CacheManager.getNegotiateTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public NegotiateTypeBean getType() {
        return type;
    }

    public void setType(NegotiateTypeBean type) {
        if (this.type == null) {
            this.type = new NegotiateTypeBean();
        }
        this.setJointRefDataBean(this.type, type, TYPE_PROPERTY);
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
}
