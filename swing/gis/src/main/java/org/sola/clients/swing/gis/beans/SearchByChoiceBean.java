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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * It is a data bean which defines a search option for the map control.
 * It contains a static method which generates a list of the choices read from 
 * the resource.
 * 
 * @author Elton Manoku
 */
public class SearchByChoiceBean {

    private static List<SearchByChoiceBean> instanceList = null;
    private String value;
    private String label;

    public SearchByChoiceBean(String value, String label) throws IOException{
        this.value = value;
        this.label = label;
    }

    /**
     * The list of choices as defined in the resources. The resource is searchbychoice.properties.
     * 
     * @return
     * @throws IOException 
     */
    public static List<SearchByChoiceBean> getInstanceList() throws IOException {
        if (instanceList == null) {
            Properties valueSource = new Properties();
            valueSource.load(
                    SearchByChoiceBean.class.getResourceAsStream("searchbychoice.properties"));
            instanceList = new ArrayList<SearchByChoiceBean>();
            for(Object key:valueSource.keySet()){
                instanceList.add(new SearchByChoiceBean(
                        key.toString(), valueSource.getProperty(key.toString())));
                
            }
        }
        return instanceList;
    }

    /**
     * Gets the label which is used for the presentation of the choice for the user
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the value. It is the identifier of the choice
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the string presentation of the choice
     */
    @Override
    public String toString() {
        return this.label;
    }
}
