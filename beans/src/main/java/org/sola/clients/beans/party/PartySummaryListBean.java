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
package org.sola.clients.beans.party;

import java.util.LinkedList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.PartySummaryTO;

/**
 * Holds the list of {@link PartySummaryBean} objects. This bean is used to bind 
 * the data on the {@link ApplicationForm} for the list of agents.
 */
public class PartySummaryListBean extends AbstractBindingBean {

    public static final String SELECTED_PARTYSUMMARY_PROPERTY = "selectedPartySummaryBean";
    private ObservableList<PartySummaryBean> partySummaryListBean;
    private PartySummaryBean selectedPartySummary;

    /** Creates new instance of the object and initializes the list of {@link PartySummaryBean} objects. */
    public PartySummaryListBean() {
        partySummaryListBean = ObservableCollections.observableList(new LinkedList<PartySummaryBean>());
    }
    
    /** 
     * Fills {@link ObservableList}&lt;{@link PartySummaryBean}&gt; with the list of agents.
     * @param createDummyAgent If true, creates empty {@link PartySummaryBean} agent 
     * to display empty option in the list.
     */
    public void FillAgents(boolean createDummyAgent) {
        List<PartySummaryTO> lst = WSManager.getInstance().getCaseManagementService().getAgents();
        partySummaryListBean.clear();
        TypeConverters.TransferObjectListToBeanList(lst, PartySummaryBean.class, (List)partySummaryListBean);
        if(createDummyAgent){
            PartySummaryBean dummyAgent = new PartySummaryBean();
            dummyAgent.setName(" ");
            dummyAgent.setEntityAction(EntityAction.DISASSOCIATE);
            partySummaryListBean.add(0, dummyAgent);
        }
    }

    public ObservableList<PartySummaryBean> getPartySummaryList() {
        return partySummaryListBean;
    }

    public PartySummaryBean getSelectedPartySummaryBean() {
        return selectedPartySummary;
    }

    public void setSelectedPartySummaryBean(PartySummaryBean value) {
        selectedPartySummary = value;
        propertySupport.firePropertyChange(SELECTED_PARTYSUMMARY_PROPERTY, null, value);
    }
}
