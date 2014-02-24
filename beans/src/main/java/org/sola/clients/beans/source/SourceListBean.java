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
package org.sola.clients.beans.source;

import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Holds the list of {@link SourceBean} objects.
 */
public class SourceListBean extends AbstractBindingBean {

    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    public static final String SOURCE_LIST_PROPERTY = "sourceBeanList";
    private SolaList<SourceBean> sourceBeanList;
    private SourceBean selectedSource;

    /** Creates new instance of object and initializes {@link SourceBean} list.*/
    public SourceListBean() {
        super();
        sourceBeanList = new SolaList();
    }

    // Properties
    public ObservableList<SourceBean> getFilteredSourceBeanList() {
        return sourceBeanList.getFilteredList();
    }

    public SolaList<SourceBean> getSourceBeanList() {
        return sourceBeanList;
    }

    public void setSourceBeanList(SolaList<SourceBean> sourceBeanList) {
        this.sourceBeanList = sourceBeanList;
        propertySupport.firePropertyChange(SOURCE_LIST_PROPERTY, null, sourceBeanList);
    }

    public SourceBean getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedSource(SourceBean value) {
        selectedSource = value;
        propertySupport.firePropertyChange(SELECTED_SOURCE_PROPERTY, null, value);
    }

    // Methods
    /** Safely removes selected source. */
    public void safeRemoveSelectedSource() {
        if (selectedSource != null) {
            sourceBeanList.safeRemove(selectedSource, EntityAction.DISASSOCIATE);
        }
    }

    /** Removes selected source. */
    public void removeSelectedSource() {
        if (selectedSource != null) {
            sourceBeanList.remove(selectedSource);
        }
    }

    /** 
     * Loads source, attached to the transaction. 
     * @param serviceId Application service id, bound to transaction.
     */
    public void loadSourceByService(String serviceId) {
        ArrayList<SourceBean> sourceList = new ArrayList<SourceBean>();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getSourcesByServiceId(serviceId),
                SourceBean.class, (List) sourceList);
        sourceBeanList.clear();
        if (sourceList != null) {
            for (SourceBean sourceBean : sourceList) {
                sourceBeanList.addAsNew(sourceBean);
            }
        }
    }
    
    /** 
     * Loads source by IDs. 
     * @param serviceId Application service id, bound to transaction.
     */
    public void loadSourceByIds(List<String> sourcesIds) {
        sourceBeanList.clear();
        
        if(sourcesIds == null || sourcesIds.size()<1){
            return;
        }
        
        ArrayList<SourceBean> sourceList = new ArrayList<SourceBean>();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getSourcesByIds(sourcesIds),
                SourceBean.class, (List) sourceList);
        
        if (sourceList != null) {
            for (SourceBean sourceBean : sourceList) {
                sourceBeanList.addAsNew(sourceBean);
            }
        }
    }
    
    /** 
     * Returns sources IDs. 
     * @param onlyFiltered Indicates whether to return IDs only from the filtered 
     * list. If {@code false}, returns all IDs.
     */
    public List<String> getSourceIds(boolean onlyFiltered) {
        ArrayList<String> sourceIds = new ArrayList<String>();
        if (sourceBeanList != null) {
            List<SourceBean> list;
            if(onlyFiltered){
                list = sourceBeanList.getFilteredList();
            }else{
                list = sourceBeanList;
            }
            for (SourceBean sourceBean : list) {
                sourceIds.add(sourceBean.getId());
            }
        }
        return sourceIds;
    }
}
