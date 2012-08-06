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
package org.sola.clients.beans.administrative;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitAreaTO;

/** 
 * Represents application property object. Could be populated from the
 * {@link ApplicationPropertyTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class BaUnitAreaBean extends AbstractIdBean {
    
    public static final String SIZE_PROPERTY = "size";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String CALCULATED_AREA_SIZE_PROPERTY = "calculatedAreaSize";
    
    @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class)
    private BigDecimal size;
    private String baUnitId;
    private String typeCode;
    private BigDecimal calculatedAreaSize;
    
    public BaUnitAreaBean() {
        super();
    }

    
    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String val) {
        String old = baUnitId;
        baUnitId = val;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, old, val);
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal val) {
        BigDecimal old = size;
        size = val;
        propertySupport.firePropertyChange(SIZE_PROPERTY, old, val);
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String val) {
        String old = typeCode;
        typeCode = val;
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, old, val);
    }
    
    public BigDecimal getCalculatedAreaSize() {
        return calculatedAreaSize;
    }

    public void setCalculatedAreaSize(BigDecimal val) {
        BigDecimal old = calculatedAreaSize;
        calculatedAreaSize = val;
        propertySupport.firePropertyChange(CALCULATED_AREA_SIZE_PROPERTY, old, val);
    }
    
     public boolean createBaUnitArea(String baUnitId) {
        
        BaUnitAreaTO baUnitArea = TypeConverters.BeanToTrasferObject(this, BaUnitAreaTO.class);
        baUnitArea = WSManager.getInstance().getAdministrative().createBaUnitArea(baUnitId, baUnitArea);
        TypeConverters.TransferObjectToBean(baUnitArea, BaUnitAreaBean.class, this);
        return true;
    }
}
