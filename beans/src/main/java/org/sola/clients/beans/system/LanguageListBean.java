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

import java.util.ArrayList;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;

/**
 * Holds the list of {@link LanguageBean} objects and used to bound the
 * data in the combobox on the forms.
 */
public class LanguageListBean extends AbstractBindingListBean {
    public static final String SELECTED_LANGUAGE_PROPERTY = "selectedLanguage";
    private ObservableList<LanguageBean> languages;
    private LanguageBean selectedLanguage;
    
    public LanguageListBean() {
        this(false);
    }

    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public LanguageListBean(boolean createDummy) {
        this(createDummy, true);
    }
    
    /** 
     * Creates object instance.
     * @param createDummy Indicates whether to add empty object on the list.
     * @param loadInactive Indicates whether to load inactive languages.
     */
    public LanguageListBean(boolean createDummy, boolean loadInactive) {
        super();
        languages = ObservableCollections.observableList(new ArrayList<LanguageBean>());
        loadList(createDummy, loadInactive);
    }
    
    /** 
     * Loads list of {@link LanguageBean}.
     * @param createDummy Indicates whether to add empty object on the list.
     * @param loadInactive Indicates whether to load inactive languages.
     */
    public final void loadList(boolean createDummy, boolean loadInactive) {
        getLanguages().clear();
        if(createDummy){
            getLanguages().add(new LanguageBean());
        }
        
        for(LanguageBean lang : CacheManager.getLanguages()){
            if(lang.isActive() || (!lang.isActive() && loadInactive)){
                getLanguages().add(lang);
            }
        }
    }

    public ObservableList<LanguageBean> getLanguages() {
        return languages;
    }

    public LanguageBean getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(LanguageBean selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
        propertySupport.firePropertyChange(SELECTED_LANGUAGE_PROPERTY, null, this.selectedLanguage);
    }
}
