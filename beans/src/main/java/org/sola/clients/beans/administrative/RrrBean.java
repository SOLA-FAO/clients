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
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.administrative.validation.LeaseValidationGroup;
import org.sola.clients.beans.administrative.validation.MortgageValidationGroup;
import org.sola.clients.beans.administrative.validation.OwnershipValidationGroup;
import org.sola.clients.beans.administrative.validation.SimpleOwnershipValidationGroup;
import org.sola.clients.beans.administrative.validation.TotalShareSize;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.ConditionTypeBean;
import org.sola.clients.beans.referencedata.RrrSubTypeBean;
import org.sola.clients.beans.referencedata.MortgageTypeBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.clients.beans.validation.NoDuplicates;
import org.sola.common.StringUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.RrrTO;

/**
 * Contains properties and methods to manage <b>RRR</b> object of the domain
 * model. Could be populated from the {@link RrrTO} object.
 */
public class RrrBean extends AbstractTransactionedBean {

    public enum RRR_ACTION {

        NEW, VARY, CANCEL, EDIT, VIEW;
    }
    public static final String CODE_OWNERSHIP = "ownership";
    public static final String CODE_APARTMENT = "apartment";
    public static final String CODE_STATE_OWNERSHIP = "stateOwnership";
    public static final String CODE_MORTGAGE = "mortgage";
    public static final String CODE_AGRI_ACTIVITY = "agriActivity";
    public static final String CODE_COMMON_OWNERSHIP = "commonOwnership";
    public static final String CODE_CUSTOMARY_TYPE = "customaryType";
    public static final String CODE_FIREWOOD = "firewood";
    public static final String CODE_FISHING = "fishing";
    public static final String CODE_GRAZING = "grazing";
    public static final String CODE_LEASE = "lease";
    public static final String CODE_OCCUPATION = "occupation";
    public static final String CODE_OWNERSHIP_ASSUMED = "ownershipAssumed";
    public static final String CODE_SUPERFICIES = "superficies";
    public static final String CODE_TENANCY = "tenancy";
    public static final String CODE_USUFRUCT = "usufruct";
    public static final String CODE_WATERRIGHTS = "waterrights";
    public static final String CODE_ADMIN_PUBLIC_SERVITUDE = "adminPublicServitude";
    public static final String CODE_MONUMENT = "monument";
    public static final String CODE_LIFE_ESTATE = "lifeEstate";
    public static final String CODE_CAVEAT = "caveat";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    public static final String EXPIRATION_DATE_PROPERTY = "expirationDate";
    public static final String SHARE_PROPERTY = "share";
    public static final String AMOUNT_PROPERTY = "amount";
    public static final String MORTGAGE_INTEREST_RATE_PROPERTY = "mortgageInterestRate";
    public static final String MORTGAGE_RANKING_PROPERTY = "mortgageRanking";
    public static final String MORTGAGE_TYPE_CODE_PROPERTY = "mortgageTypeCode";
    public static final String MORTGAGE_TYPE_PROPERTY = "mortgageType";
    public static final String NOTATION_PROPERTY = "notation";
    public static final String IS_PRIMARY_PROPERTY = "isPrimary";
    public static final String FIRST_RIGHTHOLDER_PROPERTY = "firstRightholder";
    public static final String SELECTED_SHARE_PROPERTY = "selectedShare";
    public static final String SELECTED_PROPERTY = "selected";
    public static final String SELECTED_RIGHTHOLDER_PROPERTY = "selectedRightHolder";
    public static final String DUE_DATE_PROPERTY = "dueDate";
    public static final String SELECTED_CONDITION_PROPERTY = "selectedCondition";
    public static final String RRR_SUB_TYPE_CODE_PROPERTY = "rrrSubType";
    public static final String RRR_SUB_TYPE_PROPERTY = "rrrSubType";

    private String baUnitId;
    private String nr;
    @Past(message = ClientMessage.CHECK_REGISTRATION_DATE, payload = Localized.class)
    private Date registrationDate;
    private String transactionId;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class,
            groups = {MortgageValidationGroup.class, LeaseValidationGroup.class})
    @Future(message = ClientMessage.CHECK_FUTURE_EXPIRATION, payload = Localized.class,
            groups = {MortgageValidationGroup.class})
    private Date expirationDate;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_MORTGAGEAMOUNT, payload = Localized.class, groups = {MortgageValidationGroup.class})
    private BigDecimal amount;
    private Date dueDate;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_MORTAGAETYPE, payload = Localized.class, groups = {MortgageValidationGroup.class})
    private MortgageTypeBean mortgageType;
    private BigDecimal mortgageInterestRate;
    private Integer mortgageRanking;
    private Double share;
    private SolaList<SourceBean> sourceList;
    @Valid
    private SolaList<RrrShareBean> rrrShareList;
    private RrrTypeBean rrrType;
    @Valid
    private BaUnitNotationBean notation;
    private boolean primary = false;
    @Valid
    private SolaList<PartySummaryBean> rightHolderList;
    private SolaList<ConditionForRrrBean> conditionsList;
    private transient RrrShareBean selectedShare;
    private transient boolean selected;
    private transient PartySummaryBean selectedRightholder;
    private transient ConditionForRrrBean selectedCondition;
    private String concatenatedName;
    private transient String referenceNum;
    private RrrSubTypeBean rrrSubType;

    public String getConcatenatedName() {
        // Display the Notation text entered by the user or the list of rightholders
        // if no text is supplied. 
        String result = "";
        if (getRrrSubType() != null) {
            result = getRrrSubType().getDisplayValue();
        }
        if (getNotation() != null && !StringUtility.isEmpty(getNotation().getNotationText())) {
            result = result == "" ? getNotation().getNotationText()
                    : result + " - " + getNotation().getNotationText();
        } else {
            if (getFilteredRightHolderList().size() > 0) {
                for (PartySummaryBean rightHolder : getFilteredRightHolderList()) {
                    result = result + rightHolder.getFullName() + ", ";
                }
                result = result.substring(0, result.length() - 2);
            }
        }
        return result;
    }

    public void setConcatenatedName(String concatenatedName) {
        this.concatenatedName = concatenatedName;
    }

    public RrrBean() {
        super();
        registrationDate = Calendar.getInstance().getTime();
        sourceList = new SolaList();
        rrrShareList = new SolaList();
        rightHolderList = new SolaList();
        conditionsList = new SolaList<ConditionForRrrBean>();
        notation = new BaUnitNotationBean();
    }

    public void setFirstRightholder(PartySummaryBean rightholder) {
        if (rightHolderList.size() > 0) {
            rightHolderList.set(0, rightholder);
        } else {
            rightHolderList.add(rightholder);
        }
        propertySupport.firePropertyChange(FIRST_RIGHTHOLDER_PROPERTY, null, rightholder);
    }

    public PartySummaryBean getFirstRightHolder() {
        if (rightHolderList != null && rightHolderList.size() > 0) {
            return rightHolderList.get(0);
        } else {
            return null;
        }
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        boolean oldValue = this.primary;
        this.primary = primary;
        propertySupport.firePropertyChange(IS_PRIMARY_PROPERTY, oldValue, primary);
    }

    public BaUnitNotationBean getNotation() {
        return notation;
    }

    public void setNotation(BaUnitNotationBean notation) {
        this.notation = notation;
        propertySupport.firePropertyChange(NOTATION_PROPERTY, null, notation);
    }

    public String getMortgageTypeCode() {
        if (mortgageType != null) {
            return mortgageType.getCode();
        } else {
            return null;
        }
    }

    public void setMortgageTypeCode(String mortgageTypeCode) {
        String oldValue = null;
        if (mortgageType != null) {
            oldValue = mortgageType.getCode();
        }
        setMortgageType(CacheManager.getBeanByCode(
                CacheManager.getMortgageTypes(), mortgageTypeCode));
        propertySupport.firePropertyChange(MORTGAGE_TYPE_CODE_PROPERTY,
                oldValue, mortgageTypeCode);
    }

    public MortgageTypeBean getMortgageType() {
        return mortgageType;
    }

    public void setMortgageType(MortgageTypeBean mortgageType) {
        if (this.mortgageType == null) {
            this.mortgageType = new MortgageTypeBean();
        }
        this.setJointRefDataBean(this.mortgageType, mortgageType, MORTGAGE_TYPE_PROPERTY);
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        String oldValue = this.baUnitId;
        this.baUnitId = baUnitId;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, oldValue, baUnitId);
    }

    public String getTypeCode() {
        if (rrrType != null) {
            return rrrType.getCode();
        } else {
            return null;
        }
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (rrrType != null) {
            oldValue = rrrType.getCode();
        }
        setRrrType(CacheManager.getBeanByCode(
                CacheManager.getRrrTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public RrrTypeBean getRrrType() {
        return rrrType;
    }

    public void setRrrType(RrrTypeBean rrrType) {
        if (this.rrrType == null) {
            this.rrrType = new RrrTypeBean();
        }
        this.setJointRefDataBean(this.rrrType, rrrType, RRR_TYPE_PROPERTY);
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date nextDueDate) {
        Date oldValue = this.dueDate;
        this.dueDate = nextDueDate;
        propertySupport.firePropertyChange(DUE_DATE_PROPERTY, oldValue, this.dueDate);
    }

    public BigDecimal getMortgageInterestRate() {
        return mortgageInterestRate;
    }

    public void setMortgageInterestRate(BigDecimal mortgageInterestRate) {
        this.mortgageInterestRate = mortgageInterestRate;
    }

    public Integer getMortgageRanking() {
        return mortgageRanking;
    }

    public void setMortgageRanking(Integer mortgageRanking) {
        this.mortgageRanking = mortgageRanking;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Double getShare() {
        return share;
    }

    public void setShare(Double share) {
        this.share = share;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    // @Size(min = 1, message = ClientMessage.CHECK_SIZE_SOURCELIST, payload = Localized.class)
    @NoDuplicates(message = ClientMessage.CHECK_NODUPLICATED_SOURCELIST, payload = Localized.class)
    public ObservableList<SourceBean> getFilteredSourceList() {
        return sourceList.getFilteredList();
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public SolaList<RrrShareBean> getRrrShareList() {
        return rrrShareList;
    }

    @Valid
    @Size(min = 1, message = ClientMessage.CHECK_SIZE_RRRSHARELIST, payload = Localized.class, groups = OwnershipValidationGroup.class)
    @TotalShareSize(message = ClientMessage.CHECK_TOTALSHARE_RRRSHARELIST, payload = Localized.class)
    public ObservableList<RrrShareBean> getFilteredRrrShareList() {
        return rrrShareList.getFilteredList();
    }

    public void setRrrShareList(SolaList<RrrShareBean> rrrShareList) {
        this.rrrShareList = rrrShareList;
    }

    public void removeSelectedRrrShare() {
        // Issue #256 Unlink Party from RRR when removing share. 
        if (selectedShare != null && rrrShareList != null) {
            if (getRightHolderList().size() > 0) {
                ListIterator<PartySummaryBean> it = getRightHolderList().listIterator();
                while (it.hasNext()) {
                    getRightHolderList().safeRemove(it.next(), EntityAction.DISASSOCIATE);
                }
            }
            rrrShareList.safeRemove(selectedShare, EntityAction.DELETE);
        }
    }

    public PartySummaryBean getSelectedRightHolder() {
        return selectedRightholder;
    }

    public void setSelectedRightHolder(PartySummaryBean selectedRightholder) {
        this.selectedRightholder = selectedRightholder;
        propertySupport.firePropertyChange(SELECTED_RIGHTHOLDER_PROPERTY, null, this.selectedRightholder);
    }

    public ConditionForRrrBean getSelectedCondition() {
        return selectedCondition;
    }

    public void setSelectedCondition(ConditionForRrrBean selectedCondition) {
        this.selectedCondition = selectedCondition;
        propertySupport.firePropertyChange(SELECTED_CONDITION_PROPERTY, null, this.selectedCondition);
    }

    public SolaList<PartySummaryBean> getRightHolderList() {
        return rightHolderList;
    }

    @Size(min = 1, groups = {MortgageValidationGroup.class}, message = ClientMessage.CHECK_SIZE_RIGHTHOLDERLIST, payload = Localized.class)
    public ObservableList<PartySummaryBean> getFilteredRightHolderList() {
        return rightHolderList.getFilteredList();
    }

    @Size(min = 1, groups = {SimpleOwnershipValidationGroup.class, LeaseValidationGroup.class},
            message = ClientMessage.CHECK_SIZE_OWNERSLIST, payload = Localized.class)
    private ObservableList<PartySummaryBean> getFilteredOwnersList() {
        return rightHolderList.getFilteredList();
    }

    public SolaList<ConditionForRrrBean> getConditionsList() {
        return conditionsList;
    }

    //  @Size(min = 1, groups = {SimpleOwnershipValidationGroup.class, LeaseValidationGroup.class},
    //          message = ClientMessage.CHECK_SIZE_CONDITIONS_LIST, payload = Localized.class)
    public ObservableList<ConditionForRrrBean> getConditionsFilteredList() {
        return conditionsList.getFilteredList();
    }

    public void setConditionsList(SolaList<ConditionForRrrBean> conditionsList) {
        this.conditionsList = conditionsList;
    }

    public ArrayList<ConditionForRrrBean> getCustomConditions() {
        ArrayList<ConditionForRrrBean> conditions = new ArrayList<ConditionForRrrBean>();
        for (ConditionForRrrBean cond : getConditionsFilteredList()) {
            if (cond.isCustomCondition()) {
                conditions.add(cond);
            }
        }
        return conditions;
    }

    public ArrayList<ConditionForRrrBean> getStandardConditions() {
        ArrayList<ConditionForRrrBean> conditions = new ArrayList<ConditionForRrrBean>();
        for (ConditionForRrrBean cond : getConditionsFilteredList()) {
            if (!cond.isCustomCondition()) {
                conditions.add(cond);
            }
        }
        return conditions;
    }

    public void setRightHolderList(SolaList<PartySummaryBean> rightHolderList) {
        this.rightHolderList = rightHolderList;
    }

    public RrrShareBean getSelectedShare() {
        return selectedShare;
    }

    public void setSelectedShare(RrrShareBean selectedShare) {
        RrrShareBean oldValue = this.selectedShare;
        this.selectedShare = selectedShare;
        propertySupport.firePropertyChange(SELECTED_SHARE_PROPERTY, oldValue, this.selectedShare);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        propertySupport.firePropertyChange(SELECTED_PROPERTY, oldValue, this.selected);
    }

    /**
     * Return a reference number for the RRR. Use the Ref number from the
     * notation as the user can edit this value. Do not allow the user to edit
     * the rrr.nr as this is used by SOLA when updating previous RRR versions.
     */
    public String getReferenceNum() {
        String result = getNr();
        if (getNotation() != null) {
            result = getNotation().getReferenceNr();
        }
        return result;
    }

    public void setReferenceNum(String referenceNum) {
        this.referenceNum = referenceNum;
    }

    public String getRrrSubTypeCode() {
        if (rrrSubType != null) {
            return rrrSubType.getCode();
        } else {
            return null;
        }
    }

    public void setRrrSubTypeCode(String rrrSubTypeCode) {
        String oldValue = null;
        if (rrrSubType != null) {
            oldValue = rrrSubType.getCode();
        }
        setRrrSubType(CacheManager.getBeanByCode(
                CacheManager.getRrrSubTypes(), rrrSubTypeCode));
        propertySupport.firePropertyChange(RRR_SUB_TYPE_CODE_PROPERTY,
                oldValue, rrrSubTypeCode);
    }

    public RrrSubTypeBean getRrrSubType() {
        return rrrSubType;
    }

    public void setRrrSubType(RrrSubTypeBean rrrSubType) {
        if (this.rrrSubType == null) {
            this.rrrSubType = new RrrSubTypeBean();
        }
        this.setJointRefDataBean(this.rrrSubType, rrrSubType, RRR_SUB_TYPE_PROPERTY);
    }

    public void removeSelectedRightHolder() {
        if (selectedRightholder != null) {
            getRightHolderList().safeRemove(selectedRightholder, EntityAction.DISASSOCIATE);
        }
    }

    /**
     * Removes selected condition.
     */
    public void removeSelectedRrrCondition() {
        if (selectedCondition != null) {
            getConditionsList().safeRemove(selectedCondition, EntityAction.DISASSOCIATE);
        }
    }

    /**
     * Adds conditions to the list
     *
     * @param conditions List of {@link ConditionTypeBean} that needs to be
     * added in the list
     */
    public void addConditions(List<ConditionTypeBean> conditions) {
        if (conditions == null || getConditionsList() == null) {
            return;
        }
        for (ConditionTypeBean cond : conditions) {
            addCondition(cond);
        }
    }

    /**
     * Adds condition to the list
     *
     * @param condition {@link LeaseConditionForRrrBean} that needs to be added
     * in the list
     */
    public void addRrrCondition(ConditionForRrrBean condition) {
        if (condition == null || getConditionsList() == null) {
            return;
        }
        if (condition.isCustomCondition()) {
            condition.setConditionType(null);
        }
        getConditionsList().addAsNew(condition);
    }

    /**
     * Adds lease condition in the list
     *
     * @param condition {@link ConditionTypeBean} that needs to be added in the
     * list. New {@link ConditionForRrrBean} will be created and added in the
     * list.
     */
    public void addCondition(ConditionTypeBean condition) {
        if (condition == null || getConditionsList() == null) {
            return;
        }
        for (ConditionForRrrBean conditionForRrr : getConditionsList()) {
            if (conditionForRrr.getConditionCode() != null
                    && conditionForRrr.getConditionCode().equals(condition.getCode())) {
                if (conditionForRrr.getEntityAction() == EntityAction.DELETE || conditionForRrr.getEntityAction() == EntityAction.DISASSOCIATE) {
                    conditionForRrr.setEntityAction(null);
                }
                return;
            }
        }
        ConditionForRrrBean newLeaseForRrr = new ConditionForRrrBean();
        newLeaseForRrr.setConditionType(condition);
        getConditionsList().addAsNew(newLeaseForRrr);
    }

    public void addOrUpdateRightholder(PartySummaryBean rightholder) {
        if (rightholder != null && rightHolderList != null) {
            if (rightHolderList.contains(rightholder)) {
                rightHolderList.set(rightHolderList.indexOf(rightholder), rightholder);
            } else {
                rightHolderList.addAsNew(rightholder);
            }
        }
    }

    public RrrBean makeCopyByAction(RRR_ACTION rrrAction) {
        RrrBean copy = this;

        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            copy.setStatusCode(StatusConstants.PENDING);
        }

        if (rrrAction == RRR_ACTION.VARY || rrrAction == RRR_ACTION.CANCEL) {
            // Make a copy of current bean with new ID
            copy = this.copy();
            copy.resetIdAndVerion(true, false);
        }

        if (rrrAction == RRR_ACTION.EDIT) {
            // Make a copy of current bean preserving all data
            copy = this.copy();
        }

        return copy;
    }

    /**
     * Generates new ID, RowVerion and RowID.
     *
     * @param resetChildren If true, will change ID fields also for child
     * objects.
     * @param removeBaUnitId If true, will set <code>BaUnitId</code> to null.
     */
    public void resetIdAndVerion(boolean resetChildren, boolean removeBaUnitId) {
        generateId();
        resetVersion();
        setTransactionId(null);
        setStatusCode(StatusConstants.PENDING);
        if (removeBaUnitId) {
            setBaUnitId(null);
        }
        if (resetChildren) {
            for (RrrShareBean shareBean : getRrrShareList()) {
                shareBean.resetVersion();
                shareBean.setRrrId(getId());
            }
            for (ConditionForRrrBean leaseCondition : getConditionsList()) {
                leaseCondition.resetVersion();
            }
            if (getNotation() != null) {
                getNotation().generateId();
                getNotation().resetVersion();
                if (removeBaUnitId) {
                    getNotation().setBaUnitId(null);
                }
            }
        }
    }
}
