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
package org.sola.clients.beans.system;

import java.util.Locale;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.webservices.admin.LanguageTO;

/**
 * Represents language object of the <b>language</b> table. Could be populated
 * from the {@link LanguageTO} object.<br />
 * For more information see data dictionary
 * <b>System</b> schema.
 */
public class LanguageBean extends AbstractBindingBean {

    public static final String CODE_PROPERTY = "code";
    public static final String ACTIVE_PROPERTY = "active";
    public static final String IS_DEFAULT_PROPERTY = "isDefault";
    public static final String ITEM_ORDER_PROPERTY = "itemOrder";
    public static final String DISPLAY_VALUE_PROPERTY = "displayValue";
    public static final String delimiter = "::::";

    String code;
    String displayValue;
    boolean active;
    boolean isDefault;
    int itemOrder;

    public LanguageBean() {
        super();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        boolean oldValue = this.active;
        this.active = active;
        propertySupport.firePropertyChange(ACTIVE_PROPERTY, oldValue, this.active);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        String oldValue = this.code;
        this.code = code;
        propertySupport.firePropertyChange(CODE_PROPERTY, oldValue, this.code);
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        String oldValue = this.displayValue;
        this.displayValue = displayValue;
        propertySupport.firePropertyChange(DISPLAY_VALUE_PROPERTY, oldValue, this.displayValue);
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        boolean oldValue = this.isDefault;
        this.isDefault = isDefault;
        propertySupport.firePropertyChange(IS_DEFAULT_PROPERTY, oldValue, this.isDefault);
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        int oldValue = this.itemOrder;
        this.itemOrder = itemOrder;
        propertySupport.firePropertyChange(ITEM_ORDER_PROPERTY, oldValue, this.itemOrder);
    }

    // Methods
    /**
     * Returns language code derived from the code value.
     * @return 
     */
    public String getLanguageCode() {
        String langCode = "";
        if (getCode() != null) {
            langCode = getCode();
            if (langCode.indexOf("-") > 0) {
                langCode = langCode.substring(0, langCode.indexOf("-"));
            }
        }
        return langCode;
    }

    /**
     * Extracts appropriate value from the string, holding it in different
     * languages.
     *
     * @param str String containing values for different languages.
     */
    public static String getLocalizedValue(String str) {
        return getLocalizedValue(str, Locale.getDefault().getLanguage());
    }

    /**
     * Extracts appropriate value from the string, holding it in different
     * languages.
     *
     * @param str String containing values for different languages.
     * @param lang Language code to use for extraction.
     */
    public static String getLocalizedValue(String str, String lang) {
        if (str == null || str.length() < 1) {
            return str;
        }

        if (lang == null || lang.length() < 1) {
            lang = Locale.getDefault().getLanguage();
        }

        int langIndex = -1;
        int defaultIndex = 1;

        for (LanguageBean language : CacheManager.getLanguages()) {
            if (language.getCode() != null && language.getCode().equals(lang)) {
                langIndex = language.getItemOrder();
            }
            if (language.isDefault) {
                defaultIndex = language.getItemOrder();
            }
        }

        if (langIndex < 1) {
            langIndex = defaultIndex;
        }

        String[] languages = str.split(delimiter);

        if (languages.length < langIndex) {
            return str;
        } else {
            return languages[langIndex - 1];
        }
    }

    @Override
    public boolean equals(Object aThat) {
        if (aThat == null) {
            return false;
        }
        if (this == aThat) {
            return true;
        }
        if (!(LanguageBean.class.isAssignableFrom(aThat.getClass()))) {
            return false;
        }

        LanguageBean that = (LanguageBean) aThat;

        if (this.getCode() != null && that.getCode() != null
                && this.getCode().equals(that.getCode())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }
}
