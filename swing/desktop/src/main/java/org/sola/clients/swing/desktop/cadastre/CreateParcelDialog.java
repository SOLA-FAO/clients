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
package org.sola.clients.swing.desktop.cadastre;

import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.swing.ui.cadastre.ParcelPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/** Dialog form to create new parcel or search existing one. */
public class CreateParcelDialog extends javax.swing.JDialog {

    public final static String SELECTED_PARCEL = "selectedParcel";
    private CadastreObjectBean cadastreObject;
    
    private ParcelPanel createParcelPanel(){
        return new ParcelPanel(cadastreObject);
    }
            
    /** Form constructor. */
    public CreateParcelDialog(CadastreObjectBean cadastreObject, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.cadastreObject = cadastreObject;
        initComponents();
        this.setIconImage(new ImageIcon(CreateParcelDialog.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parcelPanel = createParcelPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnCreate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        setTitle(bundle.getString("CreateParcelDialog.title")); // NOI18N
        setName("Form"); // NOI18N

        parcelPanel.setName("parcelPanel"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle"); // NOI18N
        jToolBar1.setName(bundle1.getString("CreateParcelDialog.jToolBar1.name")); // NOI18N

        btnCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnCreate.setText(bundle1.getString("CreateParcelDialog.btnCreate.text")); // NOI18N
        btnCreate.setName("btnCreate"); // NOI18N
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCreate);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(parcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(parcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
    
    String parcelName = parcelPanel.getCadastreObject().getNameFirstpart()+' ' +parcelPanel.getCadastreObject().getNameLastpart();
    final List<CadastreObjectBean> searchResult = new LinkedList<CadastreObjectBean>();
     TypeConverters.TransferObjectListToBeanList(
                        WSManager.getInstance().getCadastreService().getCadastreObjectByAllParts(parcelName),
                        CadastreObjectBean.class, (List) searchResult);
    if (searchResult.size() > 0) {
      MessageUtility.displayMessage(ClientMessage.BAUNIT_PARCEL_EXISTS);
      return;
    }
    if(parcelPanel.getCadastreObject().validate(true).size()<=0){
        parcelPanel.getCadastreObject().setStatusCode(StatusConstants.PENDING);
        this.firePropertyChange(SELECTED_PARCEL, null, parcelPanel.getCadastreObject());
        this.dispose();
    }
}//GEN-LAST:event_btnCreateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.ui.cadastre.ParcelPanel parcelPanel;
    // End of variables declaration//GEN-END:variables
}
