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
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.clients.beans.referencedata.LandUseTypeBean;
import org.sola.clients.beans.referencedata.NotationStatusTypeBean;
import org.sola.clients.beans.referencedata.RegistrationStatusTypeBean;
import org.sola.clients.beans.referencedata.StateLandStatusTypeBean;

/**
 * Represents BA unit search result.
 */
public class BaUnitSearchResultBean extends AbstractBindingBean {

    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String NAME_FIRST_PART_PROPERTY = "nameFirstpart";
    public static final String NAME_LAST_PART_PROPERTY = "nameLastpart";
    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String REGISTRATION_STATUS_PROPERTY = "registrationStatus";
    public static final String RIGHTHOLDERS_PROPERTY = "rightholders";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String LAND_USE_CODE_PROPERTY = "landUseCode";
    public static final String LAND_USE_TYPE_PROPERTY = "landUseType";
    public static final String PARCELS_PROPERTY = "parcels";
    public static final String LOCALITY_PROPERTY = "locality";
    public static final String PROPERTY_MANAGER_PROPERTY = "propertyManager";
    public static final String BA_UNIT_TYPE_PROPERTY = "baUnitType";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String ACTIVE_JOBS_PROPERTY = "activeJobs";
    public static final String ACTION_STATUS_CODE_PROPERTY = "actionStatusCode";
    public static final String ACTION_STATUS_PROPERTY = "actionStatus";
    public static final String NOTATION_TEXT_PROPERTY = "notationText";
    public static final String CHECKED_PROPERTY = "checked";
    public static final String STATE_LAND_STATUS_PROPERTY = "stateLandStatus";
    public static final String STATE_LAND_STATUS_CODE_PROPERTY = "stateLandStatusCode";
    public static final String STATE_LAND_NAME_PROPERTY = "stateLandName";
    public static final String AREA_PROPERTY = "area";

    private String id;
    private String name;
    private String nameFirstpart;
    private String nameLastpart;
    private RegistrationStatusTypeBean registrationStatus;
    private String rightholders;
    private String description;
    private LandUseTypeBean landUseType;
    private String parcels;
    private String locality;
    private BigDecimal area;
    private String propertyManager;
    private BaUnitTypeBean baUnitType;
    private transient String stateLandName;
    private String activeJobs;
    private NotationStatusTypeBean actionStatus;
    private String notationText;
    private int rowVersion;
    private boolean checked;
    private StateLandStatusTypeBean stateLandStatus;

    public BaUnitSearchResultBean() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        String oldValue = this.id;
        this.id = id;
        propertySupport.firePropertyChange(ID_PROPERTY, oldValue, this.id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        propertySupport.firePropertyChange(NAME_PROPERTY, oldValue, this.name);
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstPart) {
        String oldValue = this.nameFirstpart;
        this.nameFirstpart = nameFirstPart;
        propertySupport.firePropertyChange(NAME_FIRST_PART_PROPERTY, oldValue, this.nameFirstpart);
        propertySupport.firePropertyChange(STATE_LAND_NAME_PROPERTY, oldValue, getStateLandName());
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastPart) {
        String oldValue = this.nameLastpart;
        this.nameLastpart = nameLastPart;
        propertySupport.firePropertyChange(NAME_LAST_PART_PROPERTY, oldValue, this.nameLastpart);
        propertySupport.firePropertyChange(STATE_LAND_NAME_PROPERTY, oldValue, getStateLandName());
    }

    public String getRightholders() {
        return rightholders;
    }

    public void setRightholders(String rightholders) {
        String oldValue = this.rightholders;
        this.rightholders = rightholders;
        propertySupport.firePropertyChange(RIGHTHOLDERS_PROPERTY, oldValue, this.rightholders);
    }

    public String getStatusCode() {
        return getRegistrationStatus().getCode();
    }

    public void setStatusCode(String statusCode) {
        String oldValue = getStatusCode();
        setRegistrationStatus(CacheManager.getBeanByCode(CacheManager.getRegistrationStatusTypes(), statusCode));
        propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, oldValue, statusCode);
    }

    public RegistrationStatusTypeBean getRegistrationStatus() {
        if (registrationStatus == null) {
            registrationStatus = new RegistrationStatusTypeBean();
        }
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatusTypeBean registrationStatus) {
        this.setJointRefDataBean(getRegistrationStatus(), registrationStatus, REGISTRATION_STATUS_PROPERTY);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, this.description);
    }

    public String getParcels() {
        return parcels;
    }

    public void setParcels(String parcels) {
        String oldValue = this.parcels;
        this.parcels = parcels;
        propertySupport.firePropertyChange(PARCELS_PROPERTY, oldValue, this.parcels);
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        String oldValue = this.locality;
        this.locality = locality;
        propertySupport.firePropertyChange(LOCALITY_PROPERTY, oldValue, this.locality);
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        BigDecimal oldValue = this.area;
        this.area = area;
        propertySupport.firePropertyChange(AREA_PROPERTY, oldValue, this.area);
    }

    public String getPropertyManager() {
        return propertyManager;
    }

    public void setPropertyManager(String propertyManager) {
        String oldValue = this.propertyManager;
        this.propertyManager = propertyManager;
        propertySupport.firePropertyChange(PROPERTY_MANAGER_PROPERTY, oldValue, this.propertyManager);
    }

    public String getTypeCode() {
        if (baUnitType != null) {
            return baUnitType.getCode();
        } else {
            return null;
        }
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
        propertySupport.firePropertyChange(LAND_USE_TYPE_PROPERTY, oldValue, landUseCode);
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

    public String getActiveJobs() {
        return activeJobs;
    }

    public void setActiveJobs(String activeJobs) {
        this.activeJobs = activeJobs;
    }

    public String getActionStatusCode() {
        return actionStatus == null ? null : actionStatus.getCode();
    }

    public void setActionStatusCode(String actionStatusCode) {
        String oldValue = null;
        if (actionStatus != null) {
            oldValue = actionStatus.getCode();
        }
        setActionStatus(CacheManager.getBeanByCode(
                CacheManager.getNotationStatusTypes(), actionStatusCode));
        propertySupport.firePropertyChange(ACTION_STATUS_PROPERTY, oldValue, actionStatusCode);
    }

    public NotationStatusTypeBean getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(NotationStatusTypeBean actionStatus) {
        if (this.actionStatus == null) {
            this.actionStatus = new NotationStatusTypeBean();
        }
        this.setJointRefDataBean(this.actionStatus, actionStatus, ACTION_STATUS_PROPERTY);
    }

    public String getNotationText() {
        return notationText;
    }

    public void setNotationText(String notationText) {
        String oldValue = this.notationText;
        this.notationText = notationText;
        propertySupport.firePropertyChange(NOTATION_TEXT_PROPERTY, oldValue, this.notationText);
    }

    /**
     * Returns the value to display as the name of the Property. Determines the
     * correct value based on the type of Property (i.e. State Land or general
     * property)
     *
     * @return
     */
    public String getStateLandName() {
        return String.format("%s%s", getNameFirstpart() == null ? "" : getNameFirstpart(),
                getNameLastpart() == null ? "" : getNameLastpart());
    }

    public void setStateLandName(String stateLandName) {
        this.stateLandName = stateLandName;
    }

    public int getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(int rowVersion) {
        this.rowVersion = rowVersion;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        boolean oldValue = this.checked;
        this.checked = checked;
        propertySupport.firePropertyChange(CHECKED_PROPERTY, oldValue, this.checked);
    }

    public String getStateLandStatusCode() {
        return stateLandStatus == null ? null : stateLandStatus.getCode();
    }

    public void setStateLandStatusCode(String statusCode) {
        String oldValue = null;
        if (stateLandStatus != null) {
            oldValue = stateLandStatus.getCode();
        }
        setStateLandStatus(CacheManager.getBeanByCode(
                CacheManager.getStateLandStatusTypes(), statusCode));
        propertySupport.firePropertyChange(STATE_LAND_STATUS_CODE_PROPERTY, oldValue, statusCode);
    }

    public StateLandStatusTypeBean getStateLandStatus() {
        return stateLandStatus;
    }

    public void setStateLandStatus(StateLandStatusTypeBean stateLandStatus) {
        if (this.stateLandStatus == null) {
            this.stateLandStatus = new StateLandStatusTypeBean();
        }
        this.setJointRefDataBean(this.stateLandStatus, stateLandStatus, STATE_LAND_STATUS_PROPERTY);
    }
}
