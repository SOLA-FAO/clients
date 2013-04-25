/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.beans.application;

import java.math.BigDecimal;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.casemanagement.ApplicationPropertyTO;

/**
 * Represents application property object. Could be populated from the
 * {@link ApplicationPropertyTO} object.<br /> For more information see data
 * dictionary <b>Application</b> schema.
 */
public class ApplicationPropertyBean extends AbstractIdBean {

    public static final String APPLICATION_ID_PROPERTY = "applicationId";
    public static final String AREA_PROPERTY = "area";
    public static final String NAME_FIRST_PART_PROPERTY = "nameFirstpart";
    public static final String NAME_LAST_PART_PROPERTY = "nameLastpart";
    public static final String TOTAL_VALUE_PROPERTY = "totalValue";
    public static final String IS_VERIFIED_EXISTS_PROPERTY = "verifiedExists";
    public static final String IS_VERIFIED_LOCATIONS_PROPERTY = "verifiedLocation";
    public static final String IS_VERIFIED_APPLICATIONS_PROPERTY = "verifiedHasOwners";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";

    private String applicationId;
    private BigDecimal area;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_FIRSTPART, payload = Localized.class)
    private String nameFirstpart;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_FIRSTPART, payload = Localized.class)
    private String nameLastpart;
    private BigDecimal totalValue;
    private String baUnitId;
    private boolean verifiedExists;
    private boolean verifiedLocation;
    private boolean verifiedApplications;

    public ApplicationPropertyBean() {
        super();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public boolean isVerifiedApplications() {
        return verifiedApplications;
    }

    public void setVerifiedApplications(boolean val) {
        boolean old = verifiedApplications;
        verifiedApplications = val;
        propertySupport.firePropertyChange(IS_VERIFIED_APPLICATIONS_PROPERTY, old, val);
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String val) {
        String old = baUnitId;
        baUnitId = val;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, old, val);
    }

    public boolean isVerifiedExists() {
        return verifiedExists;
    }

    public void setVerifiedExists(boolean val) {
        boolean old = verifiedExists;
        verifiedExists = val;
        propertySupport.firePropertyChange(IS_VERIFIED_EXISTS_PROPERTY, old, val);
    }

    public boolean isVerifiedLocation() {
        return verifiedLocation;
    }

    public void setVerifiedLocation(boolean val) {
        boolean old = verifiedLocation;
        verifiedLocation = val;
        propertySupport.firePropertyChange(IS_VERIFIED_LOCATIONS_PROPERTY, old, val);
    }

    public void setApplicationId(String val) {
        String old = applicationId;
        applicationId = val;
        propertySupport.firePropertyChange(APPLICATION_ID_PROPERTY, old, val);
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal val) {
        BigDecimal old = area;
        area = val;
        propertySupport.firePropertyChange(AREA_PROPERTY, old, val);
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String val) {
        String old = nameFirstpart;
        nameFirstpart = val;
        propertySupport.firePropertyChange(NAME_FIRST_PART_PROPERTY, old, val);
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String val) {
        String old = nameLastpart;
        nameLastpart = val;
        propertySupport.firePropertyChange(NAME_LAST_PART_PROPERTY, old, val);
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal val) {
        BigDecimal old = totalValue;
        totalValue = val;
        propertySupport.firePropertyChange(TOTAL_VALUE_PROPERTY, old, val);
    }
}
