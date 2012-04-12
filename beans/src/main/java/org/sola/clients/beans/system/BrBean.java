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
package org.sola.clients.beans.system;

import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.BrTechnicalTypeBean;
import org.sola.clients.beans.validation.CodeBeanNotEmpty;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.admin.BrTO;
import org.sola.webservices.transferobjects.EntityAction;

/** 
 * Represents business rule object in the domain model. 
 * Could be populated from the {@link BrTO} object.<br />
 * For more information see data dictionary <b>System</b> schema.
 */
public class BrBean extends AbstractBindingBean {
    public static final String ID_PROPERTY = "id";
    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public static final String TECHNICAL_TYPE_CODE_PROPERTY = "technicalTypeCode";
    public static final String BR_TECHNICAL_TYPE_PROPERTY = "brTechnicalType";
    public static final String FEEDBACK_PROPERTY = "feedback";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String TECHNICAL_DESCRIPTION_PROPERTY = "technicalDescription";
    public static final String BR_DEFINITION_LIST_PROPERTY = "brDefinitionList";
    public static final String BR_VALIDATION_LIST_PROPERTY = "brValidationList";
    public static final String SELECTED_BR_VALIDATION_PROPERTY = "selectedBrValidation";
    public static final String SELECTED_BR_DEFINITION_PROPERTY = "selectedBrDefinition";
    
    private String id;
    @NotEmpty(message=ClientMessage.CHECK_NOTNULL_DISPLAYNAME, payload=Localized.class)
    private String displayName;
    @CodeBeanNotEmpty(message=ClientMessage.CHECK_BEANNOTEMPTY_BRTECHTYPE, payload=Localized.class)
    private BrTechnicalTypeBean brTechnicalType;
    private String feedback;
    private String description;
    private SolaList<BrDefinitionBean> brDefinitionList;
    private SolaList<BrValidationBean> brValidationList;
    private String technicalDescription;
    private transient BrDefinitionBean selectedBrDefinition;
    private transient BrValidationBean selectedBrValidation;
    
    public BrBean(){
        super();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        String oldValue = this.displayName;
        this.displayName = displayName;
        propertySupport.firePropertyChange(DISPLAY_NAME_PROPERTY, oldValue, this.displayName);
    }

    @Valid
    @Size(min=1, message=ClientMessage.CHECK_SIZE_BRDEFLIST, payload=Localized.class)
    public ObservableList<BrDefinitionBean> getFilteredBrDefinitionList(){
        return getBrDefinitionList().getFilteredList();
    }
    
    public SolaList<BrDefinitionBean> getBrDefinitionList() {
        if(brDefinitionList == null){
            brDefinitionList = new SolaList<BrDefinitionBean>();
        }
        return brDefinitionList;
    }

    public void setBrDefinitionList(SolaList<BrDefinitionBean> brDefinitionList) {
        this.brDefinitionList = brDefinitionList;
        propertySupport.firePropertyChange(BR_DEFINITION_LIST_PROPERTY, null, this.brDefinitionList);
    }

    public BrTechnicalTypeBean getBrTechnicalType() {
        if(brTechnicalType == null){
            brTechnicalType = new BrTechnicalTypeBean();
        }
        return brTechnicalType;
    }

    public void setBrTechnicalType(BrTechnicalTypeBean brTechnicalType) {
        this.setJointRefDataBean(getBrTechnicalType(), brTechnicalType, BR_TECHNICAL_TYPE_PROPERTY);
    }
    
    @Valid
    //@Size(min=1, message=ClientMessage.CHECK_SIZE_BRVALLIST, payload=Localized.class)
    public ObservableList<BrValidationBean> getFilteredBrValidationList(){
        return getBrValidationList().getFilteredList();
    }

    public SolaList<BrValidationBean> getBrValidationList() {
        if(brValidationList == null){
            brValidationList = new SolaList<BrValidationBean>();
        }
        return brValidationList;
    }

    public void setBrValidationList(SolaList<BrValidationBean> brValidationList) {
        this.brValidationList = brValidationList;
        propertySupport.firePropertyChange(BR_VALIDATION_LIST_PROPERTY, null, this.brValidationList);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, this.description);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        String oldValue = this.feedback;
        this.feedback = feedback;
        propertySupport.firePropertyChange(FEEDBACK_PROPERTY, oldValue, this.feedback);
    }

    public String getId() {
        if(id==null){
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public void setId(String id) {
        String oldValue = this.id;
        this.id = id;
        propertySupport.firePropertyChange(ID_PROPERTY, oldValue, this.id);
    }

    public String getTechnicalDescription() {
        return technicalDescription;
    }

    public void setTechnicalDescription(String technicalDescription) {
        String oldValue = this.technicalDescription;
        this.technicalDescription = technicalDescription;
        propertySupport.firePropertyChange(TECHNICAL_DESCRIPTION_PROPERTY, oldValue, this.technicalDescription);
    }

    public String getTechnicalTypeCode() {
        return getBrTechnicalType().getCode();
    }

    public void setTechnicalTypeCode(String technicalTypeCode) {
        String oldValue = getBrTechnicalType().getCode();
        setBrTechnicalType(CacheManager.getBeanByCode(CacheManager.getBrTechnicalTypes(), technicalTypeCode));
        propertySupport.firePropertyChange(TECHNICAL_TYPE_CODE_PROPERTY, oldValue, technicalTypeCode);
    }

    public BrDefinitionBean getSelectedBrDefinition() {
        return selectedBrDefinition;
    }

    public void setSelectedBrDefinition(BrDefinitionBean selectedBrDefinition) {
        this.selectedBrDefinition = selectedBrDefinition;
        propertySupport.firePropertyChange(SELECTED_BR_DEFINITION_PROPERTY, null, this.selectedBrDefinition);
    }

    public BrValidationBean getSelectedBrValidation() {
        return selectedBrValidation;
    }

    public void setSelectedBrValidation(BrValidationBean selectedBrValidation) {
        this.selectedBrValidation = selectedBrValidation;
        propertySupport.firePropertyChange(SELECTED_BR_VALIDATION_PROPERTY, null, this.selectedBrValidation);
    }
    
    // METHODS
    
    /** Returns business rule by ID. */
    public static BrBean getBr(String brId){
        if(brId == null || brId.length()<1){
            return null;
        }
        return TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getAdminService().getBr(brId), BrBean.class, null);
    }
    
    /** Returns business rule by ID. */
    public static BrBean getBrNotLocalized(String brId){
        if(brId == null || brId.length()<1){
            return null;
        }
        return TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getAdminService().getBr(brId, null), BrBean.class, null);
    }
    
    public void save(){
        BrTO brTO = TypeConverters.BeanToTrasferObject(this, BrTO.class);
        brTO = WSManager.getInstance().getAdminService().saveBr(brTO);
        TypeConverters.TransferObjectToBean(brTO, BrBean.class, this);
    }
    
    /** Removes current instance from DB. */
    public void remove(){
        this.setEntityAction(EntityAction.DELETE);
        WSManager.getInstance().getAdminService().saveBr(
                TypeConverters.BeanToTrasferObject(this, BrTO.class));
    }
    
    /** Removes business rule. */
    public static void removeBr(String brId){
        if(brId == null || brId.length() < 1){
            return;
        }
        BrTO brTO = WSManager.getInstance().getAdminService().getBr(brId);
        brTO.setEntityAction(EntityAction.DELETE);
        WSManager.getInstance().getAdminService().saveBr(brTO);
    }
    
    /** Removes selected BR definition. */
    public void removeSelectedBrDefinition(){
        if(selectedBrDefinition!=null){
            brDefinitionList.safeRemove(selectedBrDefinition, EntityAction.DELETE);
        }
    }
    
    /** Adds new BR definition to the list. Returns index of inserted element.*/
    public int addNewBrDefinition(){
        BrDefinitionBean brDefinition = new BrDefinitionBean();
        brDefinition.setBrId(getId());
        getBrDefinitionList().addAsNew(brDefinition);
        return getFilteredBrDefinitionList().indexOf(brDefinition);
    }

    /** Removes selected BR validation. */
    public void removeSelectedBrValidation(){
        if(selectedBrValidation!=null){
            brValidationList.safeRemove(selectedBrValidation, EntityAction.DELETE);
        }
    }
    
    /** Adds new BR validation target to the list. Returns index of inserted element.*/
    public int addNewBrValidationTarget(){
        BrValidationBean brValidation = new BrValidationBean();
        brValidation.setBrId(getId());
        getBrValidationList().addAsNew(brValidation);
        return getFilteredBrValidationList().indexOf(brValidation);
    }
}
