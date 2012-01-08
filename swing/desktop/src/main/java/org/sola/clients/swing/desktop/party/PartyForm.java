/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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

import org.sola.clients.swing.ui.party.PartyPanel;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartySummaryBean;

/**
 * Used to create or edit party object.
 */
public class PartyForm extends javax.swing.JDialog {

    private boolean savePartyOnAction;
    private boolean isReadOnly = false;
    private PartyBean partyBean;

    public PartyForm(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setIconImage(new ImageIcon(PartyForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        postInit();
    }

    public PartyForm(Frame parent, boolean modal, boolean savePartyOnAction) {
        super(parent, modal);
        this.savePartyOnAction = savePartyOnAction;
        initComponents();
        this.setIconImage(new ImageIcon(PartyForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        postInit();
    }

    public PartyForm(Frame parent, boolean modal, boolean savePartyOnAction,
            PartyBean partyBean, boolean isReadOnly) {
        super(parent, modal);
        
        this.isReadOnly = isReadOnly;
        this.partyBean = partyBean;
        this.savePartyOnAction = savePartyOnAction;
        
        initComponents();
        this.setIconImage(new ImageIcon(PartyForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        postInit();
    }

    public PartyForm(Frame parent, boolean modal, boolean savePartyOnAction,
            PartySummaryBean partySummaryBean, boolean isReadOnly) {
        super(parent, modal);
        
        if(partySummaryBean!=null){
            this.partyBean = partySummaryBean.getPartyBean();
        }
        
        this.isReadOnly = isReadOnly;
        this.savePartyOnAction = savePartyOnAction;
        
        initComponents();
    
        this.setIconImage(new ImageIcon(PartyForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        postInit();
        
    }
    
    private PartyPanel createPartyPanel() {
        PartyPanel panel;
        if (partyBean != null) {
            panel = new PartyPanel(savePartyOnAction, partyBean, isReadOnly);
            panel.setOkButtonText("Save");
        } else {
            panel = new PartyPanel(savePartyOnAction);
            panel.setOkButtonText("Create");
        }
        return panel;
    }

    private void postInit() {
        if(partyBean!=null){
            this.setTitle(String.format("Edit Party Details for %s %s",
                partyBean.getName(), partyBean.getLastName()));
        }
        
        partyPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                okButtonClicked(evt);
            }
        });
    }

    private void okButtonClicked(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PartyPanel.SELECTED_PARTY)
                && evt.getNewValue() != null) {
            firePropertyChange(PartyPanel.SELECTED_PARTY, null, evt.getNewValue());
            this.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        partyPanel = createPartyPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/party/Bundle"); // NOI18N
        setTitle(bundle.getString("PartyForm.title")); // NOI18N
        setName("Form"); // NOI18N

        partyPanel.setName("partyPanel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(partyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(partyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.ui.party.PartyPanel partyPanel;
    // End of variables declaration//GEN-END:variables
}
