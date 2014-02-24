/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
import java.util.Collections;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.sorters.LanguageBeanSorter;
import org.sola.clients.beans.sorters.LocalizedValueBeanSorter;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Helps to manage strings, holding multilingual values. Intensively used in reference data beans.
 * @see LocalizedValueBean
 */
public class LocalizedValuesListBean extends AbstractBindingBean {

    
    private SolaObservableList<LocalizedValueBean> localizedValues;
            
    public LocalizedValuesListBean(){
        super();
        localizedValues = new SolaObservableList<LocalizedValueBean>();
    }

    public ObservableList<LocalizedValueBean> getLocalizedValues() {
        return localizedValues;
    }

    // Methods
    
    /** 
     * Parses provided string to display together with appropriate language. 
     * @param str Multilingual string.
     * @see LanguageBean#getLocalizedValue(java.lang.String) 
     */
    public void loadLocalizedValues(String str){
        localizedValues.clear();
        String[] languages = null;
        if(str!=null){
            languages = str.split(LanguageBean.delimiter);
        }
        
        if(WSManager.getInstance()!=null){
            List<LanguageBean> cachedLanguages = new ArrayList<LanguageBean>();
            cachedLanguages.addAll(CacheManager.getLanguages());
            
            Collections.sort(cachedLanguages, new LanguageBeanSorter());
            
            for(LanguageBean language : cachedLanguages){
            
                String localizedValue = "...";
                if(languages!=null && languages.length>=language.getItemOrder()){
                    localizedValue = languages[language.getItemOrder()-1];
                }
                localizedValues.add(new LocalizedValueBean(language, localizedValue));
            }
        }
    }
    
    /** 
     * Returns compiled string of values for different languages. 
     */
    public String buildMultilingualString(){
        String str="";
        Collections.sort(localizedValues, new LocalizedValueBeanSorter());
        int i = 1;
        
        for(LocalizedValueBean localizedValue : localizedValues){
            str = str + localizedValue.getLocalizedValue();
            if(i<localizedValues.size()){
                str = str + LanguageBean.delimiter;
            }
            i+=1;
        }
        return str;
    }
}
