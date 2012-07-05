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
package org.sola.clients.swing.desktop.party;

import javax.swing.ImageIcon;
import org.sola.clients.beans.party.PartySearchParamsBean;
import org.sola.clients.beans.party.PartySearchResultBean;
import org.sola.clients.swing.desktop.administrative.MortgagePanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This form is used for quick search of parties.
 */
public class QuickSearchPartyForm extends javax.swing.JDialog {

    public static final String SELECTED_PARTY="selectedParty";
    public static final java.awt.Frame PARENT_FORM = null;
    private MortgagePanel panelForm;
    
    public QuickSearchPartyForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
       this.setIconImage(new ImageIcon(QuickSearchPartyForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        initComponents();
    }

    public QuickSearchPartyForm(MortgagePanel panel, boolean modal) {
        super(PARENT_FORM, modal);
        
        this.panelForm=panel; 
        this.setIconImage(new ImageIcon(QuickSearchPartyForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        initComponents();
    }
    
    public PartySearchParamsBean getSearchParams(){
        return partyQuickSearchControl.getSearchParams();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        partyQuickSearchControl = new org.sola.clients.swing.ui.party.PartyQuickSearch();
        btnSelect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/party/Bundle"); // NOI18N
        setTitle(bundle.getString("QuickSearchPartyForm.title")); // NOI18N
        setName("Form"); // NOI18N

        partyQuickSearchControl.setName("partyQuickSearchControl"); // NOI18N

        btnSelect.setText(bundle.getString("QuickSearchPartyForm.btnSelect.text")); // NOI18N
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(partyQuickSearchControl, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(partyQuickSearchControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelect))
                .addContainerGap(161, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        if (partyQuickSearchControl.getSelectedElement() != null) {
            this.firePropertyChange(SELECTED_PARTY, null,
                    (PartySearchResultBean) partyQuickSearchControl.getSelectedElement());
            if (this.getSearchParams().getRoleTypeCode().contains("bank")){
                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
                String notationText = bundle.getString("MortgagePanel.notationText.text")+" "+partyQuickSearchControl.getSelectedElement();
                this.panelForm.txtNotationText.setText(notationText);
            }
            this.dispose();
        } else {
            MessageUtility.displayMessage(ClientMessage.PARTY_SELECT_PARTY);
        }
    }//GEN-LAST:event_btnSelectActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelect;
    private org.sola.clients.swing.ui.party.PartyQuickSearch partyQuickSearchControl;
    // End of variables declaration//GEN-END:variables
}
