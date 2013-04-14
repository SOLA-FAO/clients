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
package org.sola.clients.beans;

import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.system.LanguageBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.AbstractCodeTO;

/**
 * Abstract bean, which is used as a basic class for all beans representing 
 * reference data types.
 * For more information on reference tables, check data dictionary.
 */
public abstract class AbstractCodeBean extends AbstractBindingBean {

    public static final String CODE_PROPERTY = "code";
    public static final String STATUS_PROPERTY = "status";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String DISPLAY_VALUE_PROPERTY = "displayValue";
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_CODE, payload=Localized.class)
    private String code;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_STATUS, payload=Localized.class)
    @Size(max = 1, message = ClientMessage.CHECK_SIZE_STATUS, payload=Localized.class)
    private String status;
    private String description;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_DISPLAYVALUE, payload=Localized.class)
    private String displayValue;
    private String translatedDisplayValue;
    private String translatedDescription;

    public AbstractCodeBean() {
        super();
    }

    @Override
    public String toString() {
        if(displayValue==null){
            return "";
        }
        return displayValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        String old = status;
        status = value;
        propertySupport.firePropertyChange(STATUS_PROPERTY, old, value);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        String old = code;
        code = value;
        propertySupport.firePropertyChange(CODE_PROPERTY, old, value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        String old = description;
        description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, old, value);
    }

    public String getTranslatedDescription() {
        if (translatedDescription == null && description != null) {
            translatedDescription = LanguageBean.getLocalizedValue(description);
        }
        return translatedDescription;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String value) {
        String old = displayValue;
        displayValue = value;
        propertySupport.firePropertyChange(DISPLAY_VALUE_PROPERTY, old, value);
    }

    public String getTranslatedDisplayValue() {
        if (translatedDisplayValue == null && displayValue != null) {
            translatedDisplayValue = LanguageBean.getLocalizedValue(displayValue);
        }
        return translatedDisplayValue;
    }

    /** 
     * Unified save method to save different reference data objects. 
     * @param refDataBean Reference data bean to save.
     * @param toClass Transfer object class to identify type of reference data object for saving on server side.
     */
    public static <T extends AbstractCodeBean, S extends AbstractCodeTO> T saveRefData(T refDataBean, Class<S> toClass) {

        if (refDataBean == null || toClass == null) {
            return refDataBean;
        }

        AbstractCodeTO saverTO = WSManager.getInstance().getReferenceDataService().saveReferenceData(TypeConverters.BeanToTrasferObject(refDataBean, toClass));
        TypeConverters.TransferObjectToBean(saverTO, refDataBean.getClass(), refDataBean);
        return (T) refDataBean;
    }

    @Override
    public boolean equals(Object aThat) {
        if (aThat == null) {
            return false;
        }
        if (this == aThat) {
            return true;
        }

        if (!(AbstractCodeBean.class.isAssignableFrom(aThat.getClass()))) {
            return false;
        }

        AbstractCodeBean that = (AbstractCodeBean) aThat;

        if ((this.getCode() != null && that.getCode() != null
                && this.getCode().equals(that.getCode())) || 
                (this.getCode() == null && that.getCode() == null)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (getDisplayValue() != null ? getDisplayValue().hashCode() : 0);
        hash = 23 * hash + (this.getCode() != null ? this.getCode().hashCode() : 0);
        return hash;
    }
}
