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

import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.BrReportTO;

/**
 * Holds the list of {@link PartyBean} objects.
 */
public class BrListBean extends AbstractBindingBean {
    
    public static final String SELECTED_BR_PROPERTY = "selectedBr";
    public static final String BR_LIST_PROPERTY = "brBeanList";
    private SolaObservableList<BrReportBean> brListBean;
    private BrReportBean selectedBr;
    
    /** Creates new instance of object and initializes {@link BrReportBean} list.*/
    public BrListBean() {
        brListBean = new SolaObservableList<BrReportBean>();
    }

    public ObservableList<BrReportBean> getBrBeanList()
    {
        return brListBean;
    }

    /** Fills {@link ObservableList}&lt;{@link PartySummaryBean}&gt; with the list of agents.*/
    public void FillBrs() {
        List<BrReportTO> lst = WSManager.getInstance().getCaseManagementService().getAllBrs();
        brListBean.clear();
        TypeConverters.TransferObjectListToBeanList(lst, BrReportBean.class, (List)brListBean);
    }

    public BrReportBean getSelectedParty() {
        return selectedBr;
    }

    public void setSelectedParty(BrReportBean value) {
        selectedBr = value;
        propertySupport.firePropertyChange(SELECTED_BR_PROPERTY, null, value);
    }

}
