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
package org.sola.clients.beans.administrative;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import org.hibernate.validator.constraints.NotEmpty;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.ValuationTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.ValuationTO;

/**
 *
 * @author soladev
 */
public class ValuationBean extends AbstractVersionedBean {

    public static final String NR_PROPERTY = "nr";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String AMOUNT_PROPERTY = "amount";
    public static final String VALUATION_DATE_PROPERTY = "valuationDate";
    public static final String TYPE_PROPERTY = "type";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String SOURCE_PROPERTY = "source";
    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    public static final String SELECTED_PROPERTY_PROPERTY = "selectedProperty";
    public static final String VALUATION_BEAN_PROPERTY = "valuationBean";
    public static final String SERVICE_ID_PROPERTY = "serviceId";

    private String id;
    private String nr;
    private String baUnitId;
    private BigDecimal amount;
    private Date valuationDate;
    private ValuationTypeBean type;
    @NotNull
    @NotEmpty
    private SolaList<SourceBean> sourceList;
    private BaUnitSummaryBean baUnitBasic;
    private BaUnitSummaryBean selectedProperty;
    private String source;
    private String description;
    private String transactionId;
    private SourceBean selectedSource;
    private String serviceId;

    ; 

    public ValuationBean() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the nr
     */
    public String getNr() {
        return nr;
    }

    /**
     * @param nr the nr to set
     */
    public void setNr(String nr) {
        String oldValue = this.nr;
        this.nr = nr;
        propertySupport.firePropertyChange(NR_PROPERTY, oldValue, nr);
    }

    /**
     * @return the baUnitId
     */
    public String getBaUnitId() {
        return baUnitId;
    }

    /**
     * @param baUnitId the baUnitId to set
     */
    public void setBaUnitId(String baUnitId) {
        String oldValue = this.baUnitId;
        this.baUnitId = baUnitId;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, oldValue, baUnitId);
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        BigDecimal oldValue = this.amount;
        this.amount = amount;
        propertySupport.firePropertyChange(AMOUNT_PROPERTY, oldValue, amount);
    }

    /**
     * @return the valuationDate
     */
    public Date getValuationDate() {
        return valuationDate;
    }

    /**
     * @param valuationDate the valuationDate to set
     */
    public void setValuationDate(Date valuationDate) {
        Date oldValue = this.valuationDate;
        this.valuationDate = valuationDate;
        propertySupport.firePropertyChange(VALUATION_DATE_PROPERTY, oldValue, valuationDate);
    }

    /**
     * @return the typeCode
     */
    public String getTypeCode() {
        return type == null ? null : type.getCode();
    }

    /**
     * @param typeCode the typeCode to set
     */
    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (type != null) {
            oldValue = type.getCode();
        }

        setType(CacheManager.getBeanByCode(
                CacheManager.getValuationTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public ValuationTypeBean getType() {
        return type;
    }

    public void setType(ValuationTypeBean type) {
        if (this.type == null) {
            this.type = new ValuationTypeBean();
        }
        this.setJointRefDataBean(this.type, type, TYPE_PROPERTY);
    }

    /**
     * @return the sourceList
     */
    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    /**
     * @param sourceList the sourceList to set
     */
    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        String oldValue = this.source;
        this.source = source;
        propertySupport.firePropertyChange(SOURCE_PROPERTY, oldValue, source);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, description);
    }

    /**
     * @return the transcationId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transcationId the transcationId to set
     */
    public void setTransactionId(String transcationId) {
        this.transactionId = transcationId;
    }

    public SourceBean getSelectedSource() {
        return selectedSource;
    }

    public ObservableList<SourceBean> getSourceFilteredList() {
        return sourceList.getFilteredList();
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String value) {
        String oldValue = serviceId;
        serviceId = value;
        propertySupport.firePropertyChange(SERVICE_ID_PROPERTY, oldValue, value);
    }

    public BaUnitSummaryBean getBaUnitBasic() {
        return baUnitBasic;
    }

    public void setBaUnitBasic(BaUnitSummaryBean baUnitBasic) {
        this.baUnitBasic = baUnitBasic;
    }

    

    public BaUnitSummaryBean getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(BaUnitSummaryBean selectedProperty) {
        this.selectedProperty = selectedProperty;
    }
    

    /**
     * Saves the item details to the database
     *
     * @return 
     * @retun true if the item is successfully saved, otherwise a runtime
     * exception.
     */
    public boolean saveItem() {
        ValuationTO item = TypeConverters.BeanToTrasferObject(this, ValuationTO.class);
        item = WSManager.getInstance().getAdministrative().saveValuation(serviceId, item);
        TypeConverters.TransferObjectToBean(item, ValuationBean.class, this);
        propertySupport.firePropertyChange(VALUATION_BEAN_PROPERTY, null, this);
        return true;
    }

}
