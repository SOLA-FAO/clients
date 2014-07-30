/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.party;

import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.PartyRoleTypeBean;
import org.sola.common.StringUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.PartySummaryTO;
import org.sola.webservices.transferobjects.casemanagement.PartyTO;

/**
 * Holds the list of {@link PartySummaryBean} objects. This bean is used to bind
 * the data on the {@link ApplicationForm} for the list of agents.
 */
public class PartySummaryListBean extends AbstractBindingBean {

    public static final String SELECTED_PARTYSUMMARY_PROPERTY = "selectedPartySummaryBean";
    private SolaObservableList<PartySummaryBean> partySummaryListBean;
    private PartySummaryBean selectedPartySummary;

    /**
     * Creates new instance of the object and initializes the list of
     * {@link PartySummaryBean} objects.
     */
    public PartySummaryListBean() {
        partySummaryListBean = new SolaObservableList<PartySummaryBean>();
    }

    /**
     * Fills {@link ObservableList}&lt;{@link PartySummaryBean}&gt; with the
     * list of agents.
     *
     * @param createDummyAgent If true, creates empty {@link PartySummaryBean}
     * agent to display empty option in the list.
     */
    public void FillAgents(boolean createDummyAgent) {
        loadParties(PartyRoleTypeBean.ROLE_LODGING_AGENT, createDummyAgent, null);
    }

    /**
     * Loads the parties associated with the specified role. Can indicate
     * whether to include a dummy value in the list as well as indicate any
     * required party record that must be displayed (i.e. in the case where a
     * party no longer has the specified role, but is linked to an existing
     * object.)
     *
     * @param partyRole The party role to retrieve records for
     * @param createDummy Flag to indicate if a dummy blank row should be added
     * to the top of the list
     * @param requiredPartyId The identifier of a party that must be included in
     * the list. Can be null, but should be used to ensure a selected party is
     * always displayed even if they no longer have the specified role.
     */
    public void loadParties(String partyRole, boolean createDummy, String requiredPartyId) {
        partySummaryListBean.clear();
        partySummaryListBean.addAll(CacheManager.getPartiesByRole(partyRole)); 
        if (createDummy) {
            PartySummaryBean dummyAgent = new PartySummaryBean();
            dummyAgent.setName(" ");
            dummyAgent.setEntityAction(EntityAction.DISASSOCIATE);
            partySummaryListBean.add(0, dummyAgent);
        }

        // Ensure the specified party record is included in the list for display to the user. 
        if (!StringUtility.isEmpty(requiredPartyId)) {
            boolean loadRequiredParty = true;
            for (PartySummaryBean p : partySummaryListBean) {
                if (requiredPartyId.equals(p.getId())) {
                    loadRequiredParty = false;
                    break;
                }
            }
            if (loadRequiredParty) {
                PartyTO party = WSManager.getInstance().getCaseManagementService().getParty(requiredPartyId);
                if (party != null) {
                    PartySummaryBean bean = TypeConverters.TransferObjectToBean(party, PartySummaryBean.class, null);
                    partySummaryListBean.add(bean);
                }
            }
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
