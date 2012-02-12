/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.desktop.party;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.party.PartySearchPanel;
//import org.sola.clients.swing.ui.party.PartySearchPanel;

/**
 * Holds {@link PartySearchPanel} component.
 */
public class PartySearchPanelForm extends ContentPanel {

    /** Default constructor. */
    public PartySearchPanelForm() {
        initComponents();
        partySearchPanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                handleSearchPanelEvents(evt);
            }
        });
    }
    
    private void handleSearchPanelEvents(PropertyChangeEvent evt){
        PartyPanelForm panel = null; 
        
        if(evt.getPropertyName().equals(PartySearchPanel.CREATE_NEW_PARTY_PROPERTY)){
            panel = new PartyPanelForm(true, null, false, false);
            panel.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if(evt.getPropertyName().equals(PartyPanelForm.PARTY_SAVED)){
                        ((PartyPanelForm)evt.getSource()).setParty(null);
                    }
                }
            });
        } else if(evt.getPropertyName().equals(PartySearchPanel.EDIT_PARTY_PROPERTY)){
            panel = new PartyPanelForm(true, (PartyBean)evt.getNewValue(), false, true);
        } else if(evt.getPropertyName().equals(PartySearchPanel.VIEW_PARTY_PROPERTY)){
            panel = new PartyPanelForm(true, (PartyBean)evt.getNewValue(), true, true);
        }
        
        if(panel!=null){
            getMainContentPanel().addPanel(panel, MainContentPanel.CARD_PERSON, true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        partySearchPanel = new org.sola.clients.swing.ui.party.PartySearchPanel();

        setHeaderPanel(headerPanel);

        headerPanel.setName("headerPanel"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/party/Bundle"); // NOI18N
        headerPanel.setTitleText(bundle.getString("PartySearchPanelForm.headerPanel.titleText")); // NOI18N

        partySearchPanel.setName("partySearchPanel"); // NOI18N

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
