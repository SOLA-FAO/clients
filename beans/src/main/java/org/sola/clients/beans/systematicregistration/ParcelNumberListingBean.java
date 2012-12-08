/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.beans.systematicregistration;

import java.math.BigDecimal;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;

/**
 *
 * @author RizzoM
 */
public class ParcelNumberListingBean extends AbstractIdBean {

    public static final String NAME_FIRSTPART_PROPERTY = "nameFirstpart";
    public static final String NAME_LASTPART_PROPERTY = "nameLastpart";
    public static final String LAND_USECODE_PROPERTY = "landUsecode";
    public static final String SIZE_PROPERTY = "size";
    public static final String BAUNIT_PROPERTY = "baUnitId";
    public static final String CONCATENATED_NAME_PROPERTY = "concatenatedName";
    public static final String PUBLIC_NOTIFICATION_DURATION_PROPERTY = "publicNotificationDuration";
    
    
    private String nameFirstpart;
    private String nameLastpart;
    private BigDecimal size;
    private String landUsecode;
    private String baUnitId;
    private String concatenatedName;
    private String publicNotificationDuration;

    public String getPublicNotificationDuration() {
        return publicNotificationDuration;
    }

    public void setPublicNotificationDuration(String publicNotificationDuration) {
        this.publicNotificationDuration = publicNotificationDuration;
    }

    
    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public String getConcatenatedName() {
        return concatenatedName;
    }

    public void setConcatenatedName(String concatenatedName) {
        this.concatenatedName = concatenatedName;
    }
   
    
    public String getLandUsecode() {
        return landUsecode;
    }

    public void setLandUsecode(String landUsecode) {
        this.landUsecode = landUsecode;
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        this.nameFirstpart = nameFirstpart;
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        this.nameLastpart = nameLastpart;
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }
}
