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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 * This is a bean that manages the Observable Bean lists. It also manages the selected element
 * in a list. It is used mainly in the GIS Component.
 * 
 * @author Elton Manoku
 */
public abstract class AbstractListSpatialBean extends AbstractBindingBean{
    public static final String SELECTED_BEAN_PROPERTY = "selectedBean";
    private SolaObservableList beanList;
    private SpatialBean selectedBean;

    public AbstractListSpatialBean() {
        super();
        beanList = initializeBeanList();
    }
    
    /**
     * It initializes the list bean. For each subclass this needs to be overridden to
     * initialize the list of choice.
     * @return 
     */
    protected abstract SolaObservableList initializeBeanList();

    /**
     * Gets the list of beans.
     * @return 
     */
    public SolaObservableList getBeanList() {
        return beanList;
    }

    /**
     * Gets the selected bean in the list.
     * @param <T>
     * @return 
     */
    public <T extends SpatialBean> T getSelectedBean() {
        return (T)selectedBean;
    }

    /**
     * Sets the selected bean from the list.
     * @param <T>
     * @param newValue 
     */
    public <T extends SpatialBean> void setSelectedBean(T newValue) {
        SpatialBean oldValue = this.selectedBean;
        this.selectedBean = newValue;
        propertySupport.firePropertyChange(SELECTED_BEAN_PROPERTY, oldValue, newValue);
    }
}
