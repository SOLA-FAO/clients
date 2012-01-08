/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.util.LinkedList;
import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.source.SourceBean;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;

/** 
 * Contains properties and methods to manage <b>BA Unit</b> object of the 
 * domain model. Could be populated from the {@link BaUnitTO} object.
 */
public class BaUnitBean extends AbstractTransactionedBean {

    private class RrrListListener implements ObservableListListener {

        @Override
        public void listElementsAdded(ObservableList ol, int i, int i1) {
            setEstateType();
            RrrBean rrrBean = (RrrBean) ol.get(i);
            for (RrrShareBean shareBean : getShares(rrrBean)) {
                rrrSharesList.add(createShareWithStatus(shareBean, rrrBean));
            }
        }

        @Override
        public void listElementsRemoved(ObservableList ol, int i, List list) {
            setEstateType();
            for (Object bean : list) {
                RrrBean rrrBean = (RrrBean) bean;
                for (RrrShareBean shareBean : getShares(rrrBean)) {
                    for (RrrShareWithStatus shareWithStatusBean : rrrSharesList) {
                        if (shareWithStatusBean.getRrrShare().equals(shareBean)) {
                            rrrSharesList.remove(shareWithStatusBean);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void listElementReplaced(ObservableList ol, int i, Object o) {
            setEstateType();
            refreshSharesList();
        }

        @Override
        public void listElementPropertyChanged(ObservableList ol, int i) {
            setEstateType();
        }

        private void refreshSharesList() {
            rrrSharesList.clear();
            for (RrrBean rrrBean : rrrList.getFilteredList()) {
                for (RrrShareBean shareBean : getShares(rrrBean)) {
                    rrrSharesList.add(createShareWithStatus(shareBean, rrrBean));
                }
            }
        }

        private RrrShareWithStatus createShareWithStatus(RrrShareBean shareBean, RrrBean rrrBean) {
            RrrShareWithStatus shareWithStatusBean = new RrrShareWithStatus();
            shareWithStatusBean.setRrrShare(shareBean);
            if (rrrBean.getStatus() != null) {
                shareWithStatusBean.setStatus(rrrBean.getStatus().getDisplayValue());
            }
            return shareWithStatusBean;
        }

        private List<RrrShareBean> getShares(RrrBean rrrBean) {
            List<RrrShareBean> result = new LinkedList<RrrShareBean>();
            if (rrrBean.getTypeCode().toLowerCase().contains("ownership")) {
                result = rrrBean.getFilteredRrrShareList();
            }
            return result;
        }
    }

    private class AllBaUnitNotationsListUpdater implements ObservableListListener {

        @Override
        public void listElementsAdded(ObservableList ol, int i, int i1) {
            BaUnitNotationBean notationBean = getNotation(ol.get(i));
            if (notationBean != null) {
                allBaUnitNotationList.add(notationBean);
            }
        }

        @Override
        public void listElementsRemoved(ObservableList ol, int i, List list) {
            for (Object bean : list) {
                allBaUnitNotationList.remove(getNotation(bean));
            }
        }

        @Override
        public void listElementReplaced(ObservableList ol, int i, Object o) {
            int index = allBaUnitNotationList.indexOf(getNotation(o));
            if (index > -1) {
                allBaUnitNotationList.set(index, getNotation(ol.get(i)));
            }
        }

        @Override
        public void listElementPropertyChanged(ObservableList ol, int i) {
        }

        private BaUnitNotationBean getNotation(Object bean) {
            BaUnitNotationBean notationBean = null;

            if (bean instanceof RrrBean) {
                notationBean = (BaUnitNotationBean) ((RrrBean) bean).getNotation();
            } else if (bean instanceof BaUnitNotationBean) {
                notationBean = (BaUnitNotationBean) bean;
            }

            return notationBean;
        }
    }
    public static final String BA_UNIT_TYPE_PROPERTY = "baUnitType";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String NAME_PROPERTY = "name";
    public static final String NAME_FIRSTPART_PROPERTY = "nameFirstpart";
    public static final String NAME_LASTPART_PROPERTY = "nameLastpart";
    public static final String SELECTED_PARCEL_PROPERTY = "selectedParcel";
    public static final String SELECTED_RIGHT_PROPERTY = "selectedRight";
    public static final String SELECTED_BA_UNIT_NOTATION_PROPERTY = "selectedBaUnitNotation";
    public static final String ESTATE_TYPE_PROPERTY = "estateType";
    private String name;
    @NotEmpty(message = "First part shouldn't be empty")
    private String nameFirstpart;
    @NotEmpty(message = "Last part shouldn't be empty.")
    private String nameLastpart;
    private SolaList<RrrBean> rrrList;
    private SolaList<BaUnitNotationBean> baUnitNotationList;
    private SolaList<CadastreObjectBean> cadastreObjectList;
    private ObservableList<BaUnitNotationBean> allBaUnitNotationList;
    private SolaList<SourceBean> sourceList;
    private BaUnitTypeBean baUnitType;
    private CadastreObjectBean selectedParcel;
    private RrrBean selectedRight;
    private BaUnitNotationBean selectedBaUnitNotation;
    private ObservableList<RrrShareWithStatus> rrrSharesList;
    private String estateType;

    public BaUnitBean() {
        super();
        rrrList = new SolaList();
        baUnitNotationList = new SolaList();
        cadastreObjectList = new SolaList();
        sourceList = new SolaList();
        //sourceList.setIncludedStatuses(new String[]{StatusConstants.CURRENT});
        sourceList.setExcludedStatuses(new String[]{StatusConstants.HISTORIC});
        allBaUnitNotationList = ObservableCollections.observableList(new LinkedList<BaUnitNotationBean>());
        rrrSharesList = ObservableCollections.observableList(new LinkedList<RrrShareWithStatus>());
        rrrList.getFilteredList().addObservableListListener(new RrrListListener());
        AllBaUnitNotationsListUpdater allBaUnitNotationsListener = new AllBaUnitNotationsListUpdater();
        rrrList.getFilteredList().addObservableListListener(allBaUnitNotationsListener);
        baUnitNotationList.getFilteredList().addObservableListListener(allBaUnitNotationsListener);
    }

    public void createPaperTitle(SourceBean source) {
        if (source != null) {
            for (SourceBean sourceBean : sourceList) {
                sourceBean.setEntityAction(EntityAction.DISASSOCIATE);
            }
            sourceList.addAsNew(source);
            sourceList.filter();
        }
    }

    // Checks for pending RRRs by RRR type and transaction.
    public boolean isPendingRrrExists(String rrrTypeCode) {
        if (getRrrFilteredList() != null && rrrTypeCode != null) {
            for (RrrBean bean : getRrrFilteredList()) {
                if (bean.getTypeCode() != null && bean.getTypeCode().equals(rrrTypeCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Checks for pending RRRs by provided RRR object.
    public boolean isPendingRrrExists(RrrBean rrrBean) {
        if (getRrrFilteredList() != null && rrrBean != null) {
            for (RrrBean bean : getRrrFilteredList()) {
                if (bean.getNr() != null && rrrBean.getNr() != null
                        && bean.getNr().equals(rrrBean.getNr())
                        && !bean.getId().equals(rrrBean.getId())
                        && (bean.getStatusCode() == null || bean.getStatusCode().equalsIgnoreCase(StatusConstants.PENDING))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeSelectedParcel() {
        if (selectedParcel != null && cadastreObjectList != null) {
            cadastreObjectList.safeRemove(selectedParcel, EntityAction.DISASSOCIATE);
        }
    }

    public void removeSelectedRight() {
        if (selectedRight != null && rrrList != null) {
            rrrList.safeRemove(selectedRight, EntityAction.DELETE);
        }
    }

    public boolean addBaUnitNotation(String notationText) {
        BaUnitNotationBean notation = new BaUnitNotationBean();
        notation.setBaUnitId(this.getId());
        notation.setNotationText(notationText);
        notation.setStatusCode(StatusConstants.PENDING);

        if (notation.validate(true).size() < 1) {
            baUnitNotationList.addAsNew(notation);
            return true;
        }
        return false;
    }

    public void removeSelectedBaUnitNotation() {
        if (selectedBaUnitNotation != null && baUnitNotationList.size() > 0
                && selectedBaUnitNotation.getStatusCode().equalsIgnoreCase(StatusConstants.PENDING)
                && selectedBaUnitNotation.getBaUnitId().equals(this.getId())) {
            baUnitNotationList.safeRemove(selectedBaUnitNotation, EntityAction.DELETE);
        }
    }

    public ObservableList<BaUnitNotationBean> getAllBaUnitNotationList() {
        return allBaUnitNotationList;
    }

    public String getTypeCode() {
        if (baUnitType != null) {
            return baUnitType.getCode();
        } else {
            return null;
        }
    }

    public BaUnitNotationBean getSelectedBaUnitNotation() {
        return selectedBaUnitNotation;
    }

    public void setSelectedBaUnitNotation(BaUnitNotationBean selectedBaUnitNotation) {
        this.selectedBaUnitNotation = selectedBaUnitNotation;
        propertySupport.firePropertyChange(SELECTED_BA_UNIT_NOTATION_PROPERTY,
                null, selectedBaUnitNotation);
    }

    public CadastreObjectBean getSelectedParcel() {
        return selectedParcel;
    }

    public void setSelectedParcel(CadastreObjectBean selectedParcel) {
        this.selectedParcel = selectedParcel;
        propertySupport.firePropertyChange(SELECTED_PARCEL_PROPERTY,
                null, selectedParcel);
    }

    public RrrBean getSelectedRight() {
        return selectedRight;
    }

    public void setSelectedRight(RrrBean selectedRight) {
        this.selectedRight = selectedRight;
        propertySupport.firePropertyChange(SELECTED_RIGHT_PROPERTY,
                null, selectedRight);
    }

    public SolaList<BaUnitNotationBean> getBaUnitNotationList() {
        return baUnitNotationList;
    }

    public ObservableList<BaUnitNotationBean> getBaUnitFilteredNotationList() {
        return baUnitNotationList.getFilteredList();
    }

    public SolaList<CadastreObjectBean> getCadastreObjectList() {
        return cadastreObjectList;
    }

    public ObservableList<CadastreObjectBean> getCadastreObjectFilteredList() {
        return cadastreObjectList.getFilteredList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        propertySupport.firePropertyChange(NAME_PROPERTY, oldValue, name);
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        String oldValue = this.nameFirstpart;
        this.nameFirstpart = nameFirstpart;
        propertySupport.firePropertyChange(NAME_FIRSTPART_PROPERTY, oldValue, nameFirstpart);
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        String oldValue = this.nameLastpart;
        this.nameLastpart = nameLastpart;
        propertySupport.firePropertyChange(NAME_LASTPART_PROPERTY, oldValue, nameLastpart);
    }

    public SolaList<RrrBean> getRrrList() {
        return rrrList;
    }

    public ObservableList<RrrBean> getRrrFilteredList() {
        return rrrList.getFilteredList();
    }

    public void addRrr(RrrBean rrrBean) {
        if (!this.updateListItem(rrrBean, rrrList, false)) {
            int i = 0;
            // Search by number
            i = 0;
            for (RrrBean bean : rrrList.getFilteredList()) {
                if (bean.getNr() != null && rrrBean.getNr() != null
                        && bean.getNr().equals(rrrBean.getNr())) {
                    rrrList.getFilteredList().add(i + 1, rrrBean);
                    return;
                }
                i += 1;
            }

            // If RRR is new
            rrrList.add(rrrBean);
        }
    }

    public ObservableList<RrrShareWithStatus> getRrrSharesList() {
        return rrrSharesList;
    }

    public String getEstateType() {
        return estateType;
    }

    public void setEstateType() {
        String oldValue = estateType;
        estateType = "";

        for (RrrBean rrrBean : rrrList.getFilteredList()) {
            if (rrrBean.isIsPrimary()) {
                estateType = rrrBean.getRrrType().getDisplayValue();
                break;
            }
        }
        propertySupport.firePropertyChange(ESTATE_TYPE_PROPERTY, oldValue, estateType);

    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public ObservableList<SourceBean> getFilteredSourceList() {
        return sourceList.getFilteredList();
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (baUnitType != null) {
            oldValue = baUnitType.getCode();
        }
        setBaUnitType(CacheManager.getBeanByCode(
                CacheManager.getBaUnitTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public BaUnitTypeBean getBaUnitType() {
        return baUnitType;
    }

    public void setBaUnitType(BaUnitTypeBean baUnitType) {
        if (this.baUnitType == null) {
            this.baUnitType = new BaUnitTypeBean();
        }
        this.setJointRefDataBean(this.baUnitType, baUnitType, BA_UNIT_TYPE_PROPERTY);
    }

    public boolean createBaUnit(String serviceId) {
        BaUnitTO baUnit = TypeConverters.BeanToTrasferObject(this, BaUnitTO.class);
        baUnit = WSManager.getInstance().getAdministrative().CreateBaUnit(serviceId, baUnit);
        TypeConverters.TransferObjectToBean(baUnit, BaUnitBean.class, this);
        return true;
    }

    public boolean saveBaUnit(String serviceId) {
        BaUnitTO baUnit = TypeConverters.BeanToTrasferObject(this, BaUnitTO.class);
        baUnit = WSManager.getInstance().getAdministrative().SaveBaUnit(serviceId, baUnit);
        TypeConverters.TransferObjectToBean(baUnit, BaUnitBean.class, this);
        return true;
    }
}
