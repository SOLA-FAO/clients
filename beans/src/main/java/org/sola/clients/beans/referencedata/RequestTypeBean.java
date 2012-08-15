/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.referencedata;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.validation.CodeBeanNotEmpty;
import org.sola.webservices.transferobjects.referencedata.RequestTypeTO;

/** 
 * Represents reference data object of the <b>request_type</b> table.  Could be
 * populated from the {
 * <p/>
 * @link RequestTypeTO} object.<br /> For more information see data dictionary
 * <b>Application</b> schema. <br />This bean is used as a part of {
 * @link ApplicationBean}.
 */
public class RequestTypeBean extends AbstractCodeBean {

    public static final String CODE_CADASTRE_PRINT = "cadastrePrint";
    public static final String CODE_CADASTRE_CHANGE = "cadastreChange";
    public static final String CODE_CADASTRE_REDEFINITION = "redefineCadastre";
    public static final String CODE_REG_POWER_OF_ATTORNEY = "regnPowerOfAttorney";
    public static final String CODE_CANCEL_POWER_OF_ATTORNEY = "cnclPowerOfAttorney";
    public static final String CODE_REG_STANDARD_DOCUMENT = "regnStandardDocument";
    public static final String CODE_NEW_APARTMENT = "newApartment";
    public static final String CODE_NEW_FREEHOLD = "newFreehold";
    public static final String CODE_NEW_OWNERSHIP = "newOwnership";
    public static final String CODE_NEW_STATE = "newState";
    public static final String CODE_NEW_DIGITAL_TITLE = "newDigitalTitle";
    public static final String CODE_TITLE_SERACH = "titleSearch";
    public static final String CODE_DOCUMENT_COPY = "documentCopy";
    public static final String CODE_SERVICE_ENQUIRY = "serviceEnquiry";
    
    public static final String NR_DAYS_TO_COMPLETE_PROPERTY = "nrDaysToComplete";
    public static final String NR_PROPERTIES_REQUIRED_PROPERTY = "nrPropertiesRequired";
    public static final String NOTATION_TEMPLATE_PROPERTY = "notationTemplate";
    public static final String TYPE_ACTION_CODE_PROPERTY = "rrrTypeActionCode";
    public static final String RRR_TYPE_CODE_PROPERTY = "rrrTypeCode";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    public static final String TYPE_ACTION_PROPERTY = "rrrTypeAction";
    public static final String REQUEST_CATEGORY_PROPERTY = "requestCategory";
    public static final String REQUEST_CATEGORY_CODE_PROPERTY = "requestCategoryCode";
    public static final String BASE_FEE_PROPERTY = "baseFee";
    public static final String AREA_BASE_FEE_PROPERTY = "areaBaseFee";
    public static final String VALUE_BASE_FEE_PROPERTY = "valueBaseFee";
    
    private int nrDaysToComplete;
    private int nrPropertiesRequired;
    private String notationTemplate;
    private RrrTypeBean rrrType;
    private TypeActionBean typeAction;
    @NotNull(message="Select category type.")
    @CodeBeanNotEmpty(message="Select category type.")
    private RequestCategoryTypeBean requestCategory;
    private SolaList<RequestTypeSourceTypeBean> sourceTypeCodes;
    @NotNull(message="Enter base fee.")
    private BigDecimal baseFee;
    @NotNull(message="Enter area base fee.")
    private BigDecimal areaBaseFee;
    @NotNull(message="Enter value base fee.")
    private BigDecimal valueBaseFee;
            
    public RequestTypeBean() {
        super();
        sourceTypeCodes = new SolaList<RequestTypeSourceTypeBean>();
    }

    public int getNrPropertiesRequired() {
        return nrPropertiesRequired;
    }

    public void setNrPropertiesRequired(int value) {
        int old = nrPropertiesRequired;
        nrPropertiesRequired = value;
        propertySupport.firePropertyChange(NR_PROPERTIES_REQUIRED_PROPERTY, old, value);
    }

    public int getNrDaysToComplete() {
        return nrDaysToComplete;
    }

    public void setNrDaysToComplete(int value) {
        int old = nrDaysToComplete;
        nrDaysToComplete = value;
        propertySupport.firePropertyChange(NR_DAYS_TO_COMPLETE_PROPERTY, old, value);
    }

    public String getNotationTemplate() {
        return notationTemplate;
    }

    public void setNotationTemplate(String notationTemplate) {
        this.notationTemplate = notationTemplate;
    }

    public String getTypeActionCode() {
        if (getTypeAction() != null) {
            return typeAction.getCode();
        } else {
            return null;
        }
    }

    public void setTypeActionCode(String typeActionCode) {
        String oldValue = null;
        if (typeAction != null) {
            oldValue = typeAction.getCode();
        }
        setTypeAction(CacheManager.getBeanByCode(CacheManager.getTypeActions(), typeActionCode));
        propertySupport.firePropertyChange(TYPE_ACTION_CODE_PROPERTY, oldValue, typeActionCode);
    }

    public TypeActionBean getTypeAction() {
        if(typeAction == null){
            typeAction = new TypeActionBean();
        }
        return typeAction;
    }

    public void setTypeAction(TypeActionBean rrrTypeAction) {
        if (this.typeAction == null) {
            this.typeAction = new TypeActionBean();
        }
        this.setJointRefDataBean(this.typeAction, rrrTypeAction, TYPE_ACTION_PROPERTY);
    }

    public String getRrrTypeCode() {
        if (rrrType != null) {
            return rrrType.getCode();
        } else {
            return null;
        }
    }

    public void setRrrTypeCode(String rrrTypeCode) {
        String oldValue = null;
        if (rrrType != null) {
            oldValue = rrrType.getCode();
        }
        setRrrType(CacheManager.getBeanByCode(CacheManager.getRrrTypes(), rrrTypeCode));
        propertySupport.firePropertyChange(RRR_TYPE_CODE_PROPERTY, oldValue, rrrTypeCode);
    }
    
    public RrrTypeBean getRrrType() {
        if(rrrType == null){
            rrrType = new RrrTypeBean();
        }
        return rrrType;
    }

    public void setRrrType(RrrTypeBean rrrType) {
        if (this.rrrType == null) {
            this.rrrType = new RrrTypeBean();
        }
        this.setJointRefDataBean(this.rrrType, rrrType, RRR_TYPE_PROPERTY);
    }

    public String getRequestCategoryCode() {
        if (requestCategory != null) {
            return requestCategory.getCode();
        } else {
            return null;
        }
    }

    public void setRequestCategoryCode(String requestCategoryCode) {
        String oldValue = null;
        if (requestCategory != null) {
            oldValue = requestCategory.getCode();
        }
        setRequestCategory(CacheManager.getBeanByCode(CacheManager.getRequestCategoryTypes(), requestCategoryCode));
        propertySupport.firePropertyChange(REQUEST_CATEGORY_CODE_PROPERTY, oldValue, requestCategoryCode);
    }

    public RequestCategoryTypeBean getRequestCategory() {
        if(requestCategory == null){
            requestCategory = new RequestCategoryTypeBean();
        }
        return requestCategory;
    }

    public void setRequestCategory(RequestCategoryTypeBean requestCategory) {
        this.requestCategory = requestCategory;
        if (this.requestCategory == null) {
            this.requestCategory = new RequestCategoryTypeBean();
        }
        this.setJointRefDataBean(this.requestCategory, requestCategory, REQUEST_CATEGORY_PROPERTY);
    }

    public SolaList<RequestTypeSourceTypeBean> getSourceTypeCodes() {
        return sourceTypeCodes;
    }

    public BigDecimal getAreaBaseFee() {
        return areaBaseFee;
    }

    public void setAreaBaseFee(BigDecimal areaBaseFee) {
        BigDecimal oldValue = this.areaBaseFee;
        this.areaBaseFee = areaBaseFee;
        propertySupport.firePropertyChange(AREA_BASE_FEE_PROPERTY, oldValue, this.areaBaseFee);
    }

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(BigDecimal baseFee) {
        BigDecimal oldValue = this.baseFee;
        this.baseFee = baseFee;
        propertySupport.firePropertyChange(BASE_FEE_PROPERTY, oldValue, this.baseFee);
    }

    public BigDecimal getValueBaseFee() {
        return valueBaseFee;
    }

    public void setValueBaseFee(BigDecimal valueBaseFee) {
        BigDecimal oldValue = this.valueBaseFee;
        this.valueBaseFee = valueBaseFee;
        propertySupport.firePropertyChange(VALUE_BASE_FEE_PROPERTY, oldValue, this.valueBaseFee);
    }
}