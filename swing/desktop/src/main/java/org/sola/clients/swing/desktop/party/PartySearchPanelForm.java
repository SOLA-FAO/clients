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
package org.sola.clients.swing.desktop.party;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrShareBean;
import org.sola.clients.beans.application.ObjectionBean;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartyListBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.party.PartySearchPanel;
import org.sola.common.StringUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
//import org.sola.clients.swing.ui.party.PartySearchPanel;

/**
 * Holds {@link PartySearchPanel} component.
 */
public class PartySearchPanelForm extends ContentPanel {

    private static final String ELEMENT_TYPE_RRR = "rrrBean";
    private static final String ELEMENT_TYPE_RRR_SHARE = "rrrShareBean";
    private static final String ELEMENT_TYPE_OBJECTION = "objection";
    private static final String ELEMENT_TYPE_PARTY = "party";

    private boolean selectParty = false;
    private RrrBean rrrBean;
    private RrrShareBean rrrShareBean;
    private ObjectionBean objectionBean;
    private PartyListBean partyList;

    /**
     * Default constructor.
     */
    public PartySearchPanelForm() {
        initComponents();
        partySearchPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PartySearchPanel.CREATE_NEW_PARTY_PROPERTY)
                        || evt.getPropertyName().equals(PartySearchPanel.EDIT_PARTY_PROPERTY)
                        || evt.getPropertyName().equals(PartySearchPanel.VIEW_PARTY_PROPERTY)) {
                    handleSearchPanelEvents(evt);
                }
            }
        });
    }

    /**
     * Sets the party chosen by the user as the SelectedParty on the
     * PartyListBean *
     */
    public PartySearchPanelForm(boolean selectParty, PartyListBean partyList) {
        this.partyList = partyList;
        initComponents();
        postInit(selectParty, ELEMENT_TYPE_PARTY);
    }

    public PartySearchPanelForm(boolean selectParty, RrrBean rrrShareBean) {
        this.rrrBean = rrrBean;
        initComponents();
        postInit(selectParty, ELEMENT_TYPE_RRR);
    }

    public PartySearchPanelForm(boolean selectParty, RrrShareBean rrrShareBean) {
        this.rrrShareBean = rrrShareBean;
        initComponents();
        postInit(selectParty, ELEMENT_TYPE_RRR_SHARE);
    }

    public PartySearchPanelForm(boolean selectParty, ObjectionBean objection) {
        this.objectionBean = objection;
        initComponents();
        postInit(selectParty, ELEMENT_TYPE_OBJECTION);
    }

    private void postInit(boolean selectParty, final String elementType) {
        this.selectParty = selectParty;
        partySearchPanel.setShowAddButton(false);
        partySearchPanel.setShowEditButton(false);
        partySearchPanel.setShowRemoveButton(false);
        partySearchPanel.setShowViewButton(false);
        partySearchPanel.setShowSelectButton(selectParty);
        partySearchPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(partySearchPanel.SELECT_PARTY_PROPERTY)) {
                    addElementList(elementType);
                }
            }
        });
    }

    private void addElementList(String bean) {
        if (this.partySearchPanel.partySearchResuls.getSelectedPartySearchResult() != null) {
            PartySummaryBean partySummary = this.partySearchPanel.partySearchResuls.getSelectedPartySearchResult();
            if (bean.contentEquals(ELEMENT_TYPE_RRR)) {
                this.rrrBean.getFilteredRightHolderList().add(partySummary);
            }
            if (bean.contentEquals(ELEMENT_TYPE_RRR_SHARE)) {
                this.rrrShareBean.getFilteredRightHolderList().add(partySummary);
            }
            if (bean.contentEquals(ELEMENT_TYPE_OBJECTION)) {
                this.objectionBean.getFilteredPartyList().add(partySummary);
            }
            if (bean.contentEquals(ELEMENT_TYPE_PARTY)) {
                this.partyList.setSelectedParty(partySummary.getPartyBean());
            }
            this.headerPanel.firePropertyChange(headerPanel.CLOSE_BUTTON_CLICKED, false, true);
        }
    }

    private void handleSearchPanelEvents(final PropertyChangeEvent evt) {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartyPanelForm panel = null;
                if (evt.getPropertyName().equals(PartySearchPanel.CREATE_NEW_PARTY_PROPERTY)) {
                    panel = new PartyPanelForm(true, null, false, false);
                    panel.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals(PartyPanelForm.PARTY_SAVED)) {
                                ((PartyPanelForm) evt.getSource()).setParty(null);
                            }
                        }
                    });
                } else if (evt.getPropertyName().equals(PartySearchPanel.EDIT_PARTY_PROPERTY)) {
                    panel = new PartyPanelForm(true, (PartyBean) evt.getNewValue(), false, true);
                } else if (evt.getPropertyName().equals(PartySearchPanel.VIEW_PARTY_PROPERTY)) {
                    panel = new PartyPanelForm(true, (PartyBean) evt.getNewValue(), true, true);
                }

                if (panel != null) {
                    getMainContentPanel().addPanel(panel, MainContentPanel.CARD_PERSON, true);
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    public void clickFind() {
        partySearchPanel.clickFind();
    }

    @Override
    public void setBreadCrumbTitle(String breadCrumbPath, String panelTitle) {
        // Ignore the BreadCrumbPath
        if (StringUtility.isEmpty(panelTitle)) {
            panelTitle = getBreadCrumbTitle();
        }
        if (getHeaderPanel() != null) {
            getHeaderPanel().setTitleText(panelTitle);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        partySearchPanel = new org.sola.clients.swing.ui.party.PartySearchPanel();

        setHeaderPanel(headerPanel);
        setHelpTopic("party_search"); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/party/Bundle"); // NOI18N
        headerPanel.setTitleText(bundle.getString("PartySearchPanelForm.headerPanel.titleText")); // NOI18N

        partySearchPanel.setName("partySearchPanel"); // NOI18N
        partySearchPanel.setShowSelectButton(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(partySearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(partySearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private org.sola.clients.swing.ui.party.PartySearchPanel partySearchPanel;
    // End of variables declaration//GEN-END:variables
}
