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
package org.sola.clients.beans.administrative;

import java.util.Date;
import org.hibernate.validator.constraints.Length;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.AbstractTransactionedBean;
import static org.sola.clients.beans.AbstractTransactionedBean.STATUS_CODE_PROPERTY;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.NotationStatusTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.clients.beans.validation.NoDuplicates;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitNotationTO;

/**
 * Contains properties and methods to manage <b>Notation</b> object of the
 * domain model. Could be populated from the {@link BaUnitNotationTO} object.
 */
public class BaUnitNotationBean extends AbstractTransactionedBean {

    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String TRANSACTION_ID_PROPERTY = "transactionId";
    public static final String NOTATION_TEXT_PROPERTY = "notationText";
    public static final String REFERENCE_NR_PROPERTY = "referenceNr";
    public static final String CHANGE_TIME_PROPERTY = "changeTime";
    public static final String CHANGE_USER_PROPERTY = "changeUser";
    public static final String NOTATION_DATE_PROPERTY = "notationDate";

    private String baUnitId;
    private String transactionId;

    // @Length(max = 1000, message = ClientMessage.CHECK_FIELD_INVALID_LENGTH_NOTATION, payload = Localized.class)
    private String notationText;
    private String referenceNr;
    private Date changeTime;
    private SolaList<SourceBean> sourceList;
    private String changeUser;
    private Date notationDate;
    private NotationStatusTypeBean status;

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        Date oldValue = this.changeTime;
        this.changeTime = changeTime;
        propertySupport.firePropertyChange(CHANGE_TIME_PROPERTY, oldValue, changeTime);
    }

    public BaUnitNotationBean() {
        super();
        sourceList = new SolaList();
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        String oldValue = this.baUnitId;
        this.baUnitId = baUnitId;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, oldValue, baUnitId);
    }

    public String getNotationText() {
        return notationText;
    }

    public void setNotationText(String notationText) {
        String oldValue = this.notationText;
        this.notationText = notationText;
        propertySupport.firePropertyChange(NOTATION_TEXT_PROPERTY, oldValue, notationText);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        String oldValue = this.transactionId;
        this.transactionId = transactionId;
        propertySupport.firePropertyChange(TRANSACTION_ID_PROPERTY, oldValue, transactionId);
    }

    public String getReferenceNr() {
        return referenceNr;
    }

    public void setReferenceNr(String referenceNr) {
        String oldValue = this.referenceNr;
        this.referenceNr = referenceNr;
        propertySupport.firePropertyChange(REFERENCE_NR_PROPERTY, oldValue, referenceNr);
    }

    public Date getNotationDate() {
        return notationDate;
    }

    public void setNotationDate(Date notationDate) {
        Date oldValue = this.notationDate;
        this.notationDate = notationDate;
        propertySupport.firePropertyChange(NOTATION_DATE_PROPERTY, oldValue, notationDate);
    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    @NoDuplicates(message = ClientMessage.CHECK_NODUPLICATED_SOURCELIST, payload = Localized.class)
    public ObservableList<SourceBean> getFilteredSourceList() {
        return sourceList.getFilteredList();
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public String getChangeUser() {
        return changeUser;
    }

    public void setChangeUser(String changeUser) {
        String oldValue = this.changeUser;
        this.changeUser = changeUser;
        propertySupport.firePropertyChange(CHANGE_USER_PROPERTY, oldValue, changeUser);
    }

    @Override
    public String getStatusCode() {
        if (status != null) {
            return status.getCode();
        } else {
            return null;
        }
    }

    @Override
    public void setStatusCode(String statusCode) {
        String oldValue = null;
        if (status != null) {
            oldValue = status.getCode();
        }
        if (WSManager.getInstance().getReferenceDataService() != null) {
            setStatus(CacheManager.getBeanByCode(
                    CacheManager.getNotationStatusTypes(), statusCode));
            propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, oldValue, statusCode);
        }
    }

    @Override
    public AbstractCodeBean getStatus() {
        return status;
    }

    @Override
    public void setStatus(AbstractCodeBean status) {
        if (this.status == null) {
            this.status = new NotationStatusTypeBean();
        }
        this.setJointRefDataBean(this.status, status, STATUS_PROPERTY);
    }

    /**
     * Saves the notation details to the database.
     */
    public void save() {
        BaUnitNotationTO notation = TypeConverters.BeanToTrasferObject(this, BaUnitNotationTO.class);
        notation = WSManager.getInstance().getAdministrative().saveNotation(notation);
        TypeConverters.TransferObjectToBean(notation, BaUnitNotationBean.class, this);
    }

}
