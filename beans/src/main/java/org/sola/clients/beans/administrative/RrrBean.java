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
package org.sola.clients.beans.administrative;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.administrative.validation.MortgageValidationGroup;
import org.sola.clients.beans.administrative.validation.OwnershipValidationGroup;
import org.sola.clients.beans.administrative.validation.TotalShareSize;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.MortgageTypeBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.clients.beans.validation.NoDuplicates;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.RrrTO;

/** 
 * Contains properties and methods to manage <b>RRR</b> object of the 
 * domain model. Could be populated from the {@link RrrTO} object.
 */
public class RrrBean extends AbstractTransactionedBean {

    public enum RRR_ACTION {

        NEW, VARY, CANCEL, EDIT, VIEW;
    }
    public static final String CODE_OWNERSHIP = "ownership";
    public static final String CODE_APARTMENT = "apartment";
    public static final String CODE_STATE_OWNERSHIP = "stateOwnership";
    public static final String CODE_MORTGAGE = "mortgage";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    public static final String EXPIRATION_DATE_PROPERTY = "expirationDate";
    public static final String SHARE_PROPERTY = "share";
    public static final String MORTGAGE_AMOUNT_PROPERTY = "mortgageAmount";
    public static final String MORTGAGE_INTEREST_RATE_PROPERTY = "mortgageInterestRate";
    public static final String MORTGAGE_RANKING_PROPERTY = "mortgageRanking";
    public static final String MORTGAGE_TYPE_CODE_PROPERTY = "mortgageTypeCode";
    public static final String MORTGAGE_TYPE_PROPERTY = "mortgageType";
    public static final String NOTATION_PROPERTY = "notation";
    public static final String IS_PRIMARY_PROPERTY = "isPrimary";
    public static final String FIRST_RIGHTHOLDER_PROPERTY = "firstRightholder";
    public static final String SELECTED_SHARE_PROPERTY = "selectedShare";
    public static final String SELECTED_PROPERTY = "selected";
    
    private String baUnitId;
    private String nr;
    @Past(message = ClientMessage.CHECK_REGISTRATION_DATE, payload = Localized.class)
    private Date registrationDate;
    private String transactionId;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class, groups = {MortgageValidationGroup.class})
    @Future(message = ClientMessage.CHECK_FUTURE_EXPIRATION, payload = Localized.class,
    groups = {MortgageValidationGroup.class})
    private Date expirationDate;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_MORTGAGEAMOUNT, payload = Localized.class, groups = {MortgageValidationGroup.class})
    private BigDecimal mortgageAmount;
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
    private transient RrrShareBean selectedShare;
    private transient boolean selected;

    public RrrBean() {
        super();
        registrationDate = Calendar.getInstance().getTime();
        sourceList = new SolaList();
        rrrShareList = new SolaList();
        rightHolderList = new SolaList();
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

    public BigDecimal getMortgageAmount() {
        return mortgageAmount;
    }

    public void setMortgageAmount(BigDecimal mortgageAmount) {
        this.mortgageAmount = mortgageAmount;
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

    @Size(min = 1, message = ClientMessage.CHECK_SIZE_SOURCELIST, payload = Localized.class)
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
        if (selectedShare != null && rrrShareList != null) {
            rrrShareList.safeRemove(selectedShare, EntityAction.DELETE);
        }
    }

    public SolaList<PartySummaryBean> getRightHolderList() {
        return rightHolderList;
    }

    @Size(min = 1, groups = {MortgageValidationGroup.class}, message = ClientMessage.CHECK_SIZE_RIGHTHOLDERLIST, payload = Localized.class)
    public ObservableList<PartySummaryBean> getFilteredRightHolderList() {
        return rightHolderList.getFilteredList();
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
     * @param resetChildren If true, will change ID fields also for child objects.
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
            getNotation().generateId();
            getNotation().resetVersion();
            if (removeBaUnitId) {
                getNotation().setBaUnitId(null);
            }
        }
    }
}
