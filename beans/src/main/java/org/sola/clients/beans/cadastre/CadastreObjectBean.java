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
package org.sola.clients.beans.cadastre;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.address.AddressBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.beans.referencedata.LandUseTypeBean;
import org.sola.clients.beans.referencedata.StateLandStatusTypeBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Contains properties and methods to manage <b>Cadastre</b> object of the
 * domain model. Could be populated from the {@link CadastreObjectTO} object.
 */
public class CadastreObjectBean extends AbstractTransactionedBean {

    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String APPROVAL_DATETIME_PROPERTY = "approvalDatetime";
    public static final String HISTORIC_DATETIME_PROPERTY = "historicDatetime";
    public static final String SOURCE_REFERENCE_PROPERTY = "sourceReference";
    public static final String NAME_FIRSTPART_PROPERTY = "nameFirstpart";
    public static final String NAME_LASTPART_PROPERTY = "nameLastpart";
    public static final String CADASTRE_OBJECT_TYPE_PROPERTY = "cadastreObjectType";
    public static final String GEOM_POLYGON_PROPERTY = "geomPolygon";
    public static final String SELECTED_PROPERTY = "selected";
    public static final String PENDING_STATUS = "pending";
    public static final String LAND_USE_TYPE_PROPERTY = "landUseType";
    public static final String LAND_USE_CODE_PROPERTY = "landUseCode";
    public static final String ADDRESS_LIST_PROPERTY = "addressList";
    public static final String SELECTED_ADDRESS_PROPERTY = "selectedAddress";
    public static final String OFFICIAL_AREA_SIZE_PROPERTY = "officialAreaSize";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String STATE_LAND_STATUS_TYPE_PROPERTY = "stateLandStatusType";
    public static final String STATE_LAND_STATUS_CODE_PROPERTY = "stateLandStatusCode";

    private Date approvalDatetime;
    private Date historicDatetime;
    @Length(max = 100, message = ClientMessage.CHECK_FIELD_INVALID_LENGTH_SRCREF, payload = Localized.class)
    private String sourceReference;
    @Length(max = 20, message = ClientMessage.CHECK_FIELD_INVALID_LENGTH_FIRSTPART, payload = Localized.class)
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_CADFIRSTPART, payload = Localized.class)
    private String nameFirstpart;
    @Length(max = 50, message = ClientMessage.CHECK_FIELD_INVALID_LENGTH_LASTPART, payload = Localized.class)
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_CADLASTPART, payload = Localized.class)
    private String nameLastpart;
    //@NotNull(message = ClientMessage.CHECK_NOTNULL_CADOBJTYPE, payload = Localized.class)
    private CadastreObjectTypeBean cadastreObjectType;
    private byte[] geomPolygon;
    private transient boolean selected;
    private LandUseTypeBean landUseType;
    private SolaList<SpatialValueAreaBean> spatialValueAreaList;
    private SolaList<AddressBean> addressList;
    private transient AddressBean selectedAddress;
    private String description;
    private StateLandStatusTypeBean stateLandStatusType;

    public CadastreObjectBean() {
        super();
        addressList = new SolaList<AddressBean>();
        spatialValueAreaList = new SolaList<SpatialValueAreaBean>();
    }
    
    public Date getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(Date approvalDatetime) {
        Date oldValue = this.approvalDatetime;
        this.approvalDatetime = approvalDatetime;
        propertySupport.firePropertyChange(APPROVAL_DATETIME_PROPERTY,
                oldValue, approvalDatetime);
    }

    public Date getHistoricDatetime() {
        return historicDatetime;
    }

    public void setHistoricDatetime(Date historicDatetime) {
        Date oldValue = this.historicDatetime;
        this.historicDatetime = historicDatetime;
        propertySupport.firePropertyChange(HISTORIC_DATETIME_PROPERTY,
                oldValue, historicDatetime);
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        String oldValue = this.nameFirstpart;
        this.nameFirstpart = nameFirstpart;
        propertySupport.firePropertyChange(NAME_FIRSTPART_PROPERTY,
                oldValue, nameFirstpart);
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        String oldValue = this.nameLastpart;
        this.nameLastpart = nameLastpart;
        propertySupport.firePropertyChange(NAME_LASTPART_PROPERTY,
                oldValue, nameLastpart);
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public void setSourceReference(String sourceReference) {
        String oldValue = this.sourceReference;
        this.sourceReference = sourceReference;
        propertySupport.firePropertyChange(SOURCE_REFERENCE_PROPERTY,
                oldValue, sourceReference);
    }

    public String getTypeCode() {
        if (cadastreObjectType != null) {
            return cadastreObjectType.getCode();
        } else {
            return null;
        }
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (cadastreObjectType != null) {
            oldValue = cadastreObjectType.getCode();
        }
        setCadastreObjectType(CacheManager.getBeanByCode(
                CacheManager.getCadastreObjectTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public String getLandUseCode() {
        if (landUseType != null) {
            return landUseType.getCode();
        } else {
            return null;
        }
    }

    public void setLandUseCode(String landUseCode) {
        String oldValue = null;
        if (landUseType != null) {
            oldValue = landUseType.getCode();
        }
        setLandUseType(CacheManager.getBeanByCode(
                CacheManager.getLandUseTypes(), landUseCode));
        propertySupport.firePropertyChange(LAND_USE_CODE_PROPERTY, oldValue, landUseCode);
    }

    public CadastreObjectTypeBean getCadastreObjectType() {
        return cadastreObjectType;
    }

    public void setCadastreObjectType(CadastreObjectTypeBean cadastreObjectType) {
        if (this.cadastreObjectType == null) {
            this.cadastreObjectType = new CadastreObjectTypeBean();
        }
        this.setJointRefDataBean(this.cadastreObjectType, cadastreObjectType, CADASTRE_OBJECT_TYPE_PROPERTY);
    }

    public LandUseTypeBean getLandUseType() {
        return landUseType;
    }

    public void setLandUseType(LandUseTypeBean landUseType) {
        if (this.landUseType == null) {
            this.landUseType = new LandUseTypeBean();
        }
        this.setJointRefDataBean(this.landUseType, landUseType, LAND_USE_TYPE_PROPERTY);
    }

    public byte[] getGeomPolygon() {
        return geomPolygon;
    }

    public void setGeomPolygon(byte[] geomPolygon) { //NOSONAR
        byte[] old = this.geomPolygon;
        this.geomPolygon = geomPolygon; //NOSONAR
        propertySupport.firePropertyChange(GEOM_POLYGON_PROPERTY, old, this.geomPolygon);
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
     * Looks for officialArea code in the list of areas.
     */
    //@NotNull(message = ClientMessage.CHECK_NOTNULL_AREA, payload = Localized.class)
    public BigDecimal getOfficialAreaSize() {
        if (isCopyInProgress() || getSpatialValueAreaFiletredList() == null
                || getSpatialValueAreaFiletredList().size() < 1) {
            return null;
        }
        for (SpatialValueAreaBean areaBean : getSpatialValueAreaFiletredList()) {
            if (SpatialValueAreaBean.CODE_OFFICIAL_AREA.equals(areaBean.getTypeCode())) {
                return areaBean.getSize();
            }
        }
        return null;
    }

    /**
     * Sets officialArea code.
     */
    public void setOfficialAreaSize(BigDecimal area) {
        if (isCopyInProgress()) {
            // Don't modify the areaList if the Bean is being copied otherwise
            // the list may end up with a duplicate area bean. 
            return;
        }
        boolean found = false;
        for (SpatialValueAreaBean areaBean : getSpatialValueAreaList()) {
            if (SpatialValueAreaBean.CODE_OFFICIAL_AREA.equals(areaBean.getTypeCode())) {
                // Delete area if provided value is null
                if (area == null) {
                    areaBean.setEntityAction(EntityAction.DELETE);
                } else {
                    areaBean.setSize(area);
                    areaBean.setEntityAction(null);
                }
                found = true;
                break;
            }
        }

        // Official area not found, add new if provided area not null
        if (area != null && !found) {
            SpatialValueAreaBean areaBean = new SpatialValueAreaBean();
            areaBean.setSize(area);
            areaBean.setTypeCode(SpatialValueAreaBean.CODE_OFFICIAL_AREA);
            areaBean.setSpatialUnitId(this.getId());
            getSpatialValueAreaList().addAsNew(areaBean);
        }
        propertySupport.firePropertyChange(OFFICIAL_AREA_SIZE_PROPERTY, BigDecimal.ZERO, area);
    }

    @Valid
    public ObservableList<AddressBean> getAddressFilteredList() {
        return addressList.getFilteredList();
    }

    public SolaList<AddressBean> getAddressList() {
        return addressList;
    }

    public void setAddressList(SolaList<AddressBean> addressList) {
        this.addressList = addressList;
    }

    /**
     * Returns merged string of addresses.
     */
    public String getAddressString() {
        String address = "";
        if (getAddressFilteredList() != null) {
            for (AddressBean addressBean : getAddressFilteredList()) {
                if (addressBean.getDescription() != null && !addressBean.getDescription().isEmpty()) {
                    if (address.isEmpty()) {
                        address = addressBean.getDescription();
                    } else {
                        address = address + "; " + addressBean.getDescription();
                    }
                }
            }
        }
        return address;
    }

    public AddressBean getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(AddressBean selectedAddress) {
        AddressBean oldValue = this.selectedAddress;
        this.selectedAddress = selectedAddress;
        propertySupport.firePropertyChange(SELECTED_ADDRESS_PROPERTY, oldValue, this.selectedAddress);
    }

    public SolaList<SpatialValueAreaBean> getSpatialValueAreaList() {
        return spatialValueAreaList;
    }

    @Valid
    public ObservableList<SpatialValueAreaBean> getSpatialValueAreaFiletredList() {
        return spatialValueAreaList.getFilteredList();
    }

    public void setSpatialValueAreaList(SolaList<SpatialValueAreaBean> spatialValueAreaList) {
        this.spatialValueAreaList = spatialValueAreaList;
    }

    /**
     * Adds new cadastre object address.
     */
    public void addAddress(AddressBean address) {
        if (address != null) {
            getAddressList().addAsNew(address);
        }
    }

    /**
     * Removes selected address.
     */
    public void removeSelectedAddress() {
        if (selectedAddress != null) {
            if (selectedAddress.isNew()) {
                getAddressList().remove(selectedAddress);
            } else {
                getAddressList().safeRemove(selectedAddress, EntityAction.DELETE);
            }
        }
    }

    /**
     * Updates selected address.
     */
    public void updateSelectedAddress(AddressBean address) {
        if (selectedAddress != null && address != null) {
            selectedAddress.setDescription(address.getDescription());
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, description);
    }

    public String getStateLandStatusCode() {
        if (stateLandStatusType != null) {
            return stateLandStatusType.getCode();
        } else {
            return null;
        }
    }

    public void setStateLandStatusCode(String statusCode) {
        String oldValue = null;
        if (stateLandStatusType != null) {
            oldValue = stateLandStatusType.getCode();
        }
        setStateLandStatusType(CacheManager.getBeanByCode(
                CacheManager.getStateLandStatusTypes(), statusCode));
        propertySupport.firePropertyChange(STATE_LAND_STATUS_CODE_PROPERTY, oldValue, statusCode);
    }

    public StateLandStatusTypeBean getStateLandStatusType() {
        return stateLandStatusType;
    }

    public void setStateLandStatusType(StateLandStatusTypeBean stateLandStatusType) {
        if (this.stateLandStatusType == null) {
            this.stateLandStatusType = new StateLandStatusTypeBean();
        }
        this.setJointRefDataBean(this.stateLandStatusType, stateLandStatusType, STATE_LAND_STATUS_TYPE_PROPERTY);
    }

    @Override
    public String toString() {
        String result = nameFirstpart == null ? "" : nameFirstpart;
        result += nameLastpart == null ? "" : " " + nameLastpart;
        return result;
    }

}
