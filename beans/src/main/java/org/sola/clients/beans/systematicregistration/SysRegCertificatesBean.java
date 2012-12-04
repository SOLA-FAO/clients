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
import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;

/**
 *
 * @author RizzoM
 */
public class SysRegCertificatesBean extends AbstractIdBean {

    public static final String NAME_FIRSTPART_PROPERTY = "nameFirstpart";
    public static final String NAME_LASTPART_PROPERTY = "nameLastpart";
    public static final String LAND_USECODE_PROPERTY = "landUsecode";
    public static final String SIZE_PROPERTY = "size";
    public static final String BAUNIT_PROPERTY = "baUnitId";
    public static final String CONCATENATED_NAME_PROPERTY = "concatenatedName";
    public static final String NR_PROPERTY = "nr";
    public static final String APP_STATUS_PROPERTY = "application_status";
    public static final String NAME_PROPERTY = "name";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String CREATION_DATE_PROPERTY = "creationDate";

    
    private String nameFirstpart;
    private String nameLastpart;
    private BigDecimal size;
    private String landUsecode;
    private String baUnitId;
    private String concatenatedName;
    private String nr;
    private String application_status;
    private String name;
    private String typeCode;
    private Date creationDate;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getApplication_status() {
        return application_status;
    }

    public void setApplication_status(String application_status) {
        this.application_status = application_status;
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

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }
}
