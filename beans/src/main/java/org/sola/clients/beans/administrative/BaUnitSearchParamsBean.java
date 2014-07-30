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

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.LandUseTypeBean;

/**
 * Represents search criteria for searching BA units.
 */
public class BaUnitSearchParamsBean extends AbstractBindingBean {

    public static final String SEARCH_TYPE_STATE_LAND = "stateLand";
    public static final String SEARCH_TYPE_GENERAL = "property";
    public static final String NAME_FIRST_PART_PROPERTY = "nameFirstPart";
    public static final String NAME_LAST_PART_PROPERTY = "nameLastPart";
    public static final String OWNER_NAME_PROPERTY = "ownerName";
    public static final String SEARCH_TYPE_PROPERTY = "searchType";
    public static final String LOCALITY_PROPERTY = "locality";
    public static final String LAND_USE_TYPE_PROPERTY = "landUseType";
    public static final String LAND_USE_TYPE_CODE_PROPERTY = "landUseTypeCode";
    public static final String DOCUMENT_NUMBER_PROPERTY = "documentNumber";
    public static final String PARCEL_NUMBER_PROPERTY = "parcelNumber";
    public static final String PLAN_NUMBER_PROPERTY = "planNumber";
    public static final String PROPERTY_MANAGER_PROPERTY = "propertyManager";
    public static final String INTEREST_REF_NUM_PROPERTY = "interestRefNum";
    public static final String DESCRIPTION_PROPERTY = "description";

    private String nameFirstPart;
    private String nameLastPart;
    private String ownerName;
    private String searchType;
    private String locality;
    private LandUseTypeBean landUseType;
    private String documentNumber;
    private String parcelNumber;
    private String planNumber;
    private String propertyManager;
    private String interestRefNum;
    private String description;

    public BaUnitSearchParamsBean() {
        super();
        this.searchType = SEARCH_TYPE_GENERAL;
    }

    public String getNameFirstPart() {
        return nameFirstPart;
    }

    public void setNameFirstPart(String nameFirstPart) {
        String oldValue = this.nameFirstPart;
        this.nameFirstPart = nameFirstPart;
        propertySupport.firePropertyChange(NAME_FIRST_PART_PROPERTY, oldValue, this.nameFirstPart);
    }

    public String getNameLastPart() {
        return nameLastPart;
    }

    public void setNameLastPart(String nameLastPart) {
        String oldValue = this.nameLastPart;
        this.nameLastPart = nameLastPart;
        propertySupport.firePropertyChange(NAME_LAST_PART_PROPERTY, oldValue, this.nameLastPart);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        String oldValue = this.ownerName;
        this.ownerName = ownerName;
        propertySupport.firePropertyChange(OWNER_NAME_PROPERTY, oldValue, this.ownerName);
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        String oldValue = this.searchType;
        this.searchType = searchType;
        propertySupport.firePropertyChange(SEARCH_TYPE_PROPERTY, oldValue, this.searchType);
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        String oldValue = this.locality;
        this.locality = locality;
        propertySupport.firePropertyChange(LOCALITY_PROPERTY, oldValue, this.locality);
    }

    public String getLandUseTypeCode() {
        if (landUseType != null) {
            return landUseType.getCode();
        } else {
            return null;
        }
    }

    public void setLandUseTypeCode(String landUseCode) {
        String oldValue = null;
        if (landUseType != null) {
            oldValue = landUseType.getCode();
        }
        setLandUseType(CacheManager.getBeanByCode(
                CacheManager.getLandUseTypes(), landUseCode));
        propertySupport.firePropertyChange(LAND_USE_TYPE_CODE_PROPERTY, oldValue, landUseCode);
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

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        String oldValue = this.documentNumber;
        this.documentNumber = documentNumber;
        propertySupport.firePropertyChange(DOCUMENT_NUMBER_PROPERTY, oldValue, this.documentNumber);
    }

    public String getParcelNumber() {
        return parcelNumber;
    }

    public void setParcelNumber(String parcelNumber) {
        String oldValue = this.parcelNumber;
        this.parcelNumber = parcelNumber;
        propertySupport.firePropertyChange(PARCEL_NUMBER_PROPERTY, oldValue, this.parcelNumber);
    }

    public String getPlanNumber() {
        return planNumber;
    }

    public void setPlanNumber(String planNumber) {
        String oldValue = this.planNumber;
        this.planNumber = planNumber;
        propertySupport.firePropertyChange(PLAN_NUMBER_PROPERTY, oldValue, this.planNumber);
    }

    public String getPropertyManager() {
        return propertyManager;
    }

    public void setPropertyManager(String propertyManager) {
        String oldValue = this.propertyManager;
        this.propertyManager = propertyManager;
        propertySupport.firePropertyChange(PROPERTY_MANAGER_PROPERTY, oldValue, this.propertyManager);
    }

    public String getInterestRefNum() {
        return interestRefNum;
    }

    public void setInterestRefNum(String interestRefNum) {
        String oldValue = this.interestRefNum;
        this.interestRefNum = interestRefNum;
        propertySupport.firePropertyChange(INTEREST_REF_NUM_PROPERTY, oldValue, this.interestRefNum);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, this.description);
    }

    /**
     * Clears the search criteria from the form.
     */
    public void clear() {
        setNameFirstPart(null);
        setNameLastPart(null);
        setOwnerName(null);
        setLocality(null);
        setLandUseType(null);
        setDocumentNumber(null);
        setParcelNumber(null);
        setPlanNumber(null);
        setPropertyManager(null);
        setInterestRefNum(null);
        setDescription(null);
    }
}
