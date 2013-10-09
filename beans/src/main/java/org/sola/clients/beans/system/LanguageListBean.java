/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
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
