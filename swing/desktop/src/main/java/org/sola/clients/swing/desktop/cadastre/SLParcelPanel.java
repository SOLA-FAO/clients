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
package org.sola.clients.swing.desktop.cadastre;

import java.util.LinkedList;
import java.util.List;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.cadastre.ParcelPanel;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Panel used to display, add and edit parcel details.
 *
 * @author soladev
 */
public class SLParcelPanel extends ContentPanel {

    public final static String SAVE_PARCEL = "saveParcel";
    private CadastreObjectBean cadastreObject;
    private boolean readOnly = true;
    private BaUnitBean property;

    /**
     * Creates new form SLParcelPanel
     */
    public SLParcelPanel(CadastreObjectBean cadastreObject, BaUnitBean property, boolean readOnly) {
        this.cadastreObject = cadastreObject;
        this.property = property;
        this.readOnly = readOnly;
        initComponents();
        customizeForm();
    }

    private void customizeForm() {

        java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle");
        String title = String.format(resourceBundle.getString("SLParcelPanel.TitleText.ExistingParcel"), cadastreObject.toString());
        if (cadastreObject == null) {
            title = resourceBundle.getString("SLParcelPanel.TitleText.NewParcel");
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        }
        
        this.setBreadCrumbTitle(this.getBreadCrumbPath(), title);

        btnSave.setEnabled(!readOnly);
        btnSecurity.setVisible(!readOnly
                && SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));
    }

    private ParcelPanel createParcelPanel() {
        return new ParcelPanel(cadastreObject, readOnly);
    }

    private void saveParcel() {
        String parcelId = parcelPanel.getCadastreObject().getId();
        String parcelName = parcelPanel.getCadastreObject().getNameFirstpart() + ' ' + parcelPanel.getCadastreObject().getNameLastpart();
        final List<CadastreObjectBean> searchResult = new LinkedList<CadastreObjectBean>();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCadastreService().getCadastreObjectByAllParts(parcelName),
                CadastreObjectBean.class, (List) searchResult);
        for (CadastreObjectBean co : searchResult) {
            if (!co.getId().equals(parcelId)) {
                // Parcel with the same name that is not the current parcel
                MessageUtility.displayMessage(ClientMessage.BAUNIT_PARCEL_EXISTS);
                return;
            }
        }
        if (parcelPanel.getCadastreObject().validate(true).size() <= 0) {
            this.firePropertyChange(SAVE_PARCEL, null, parcelPanel.getCadastreObject());
            close();
        }
    }

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(parcelPanel.getCadastreObject(),
                MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        parcelPanel = createParcelPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new org.sola.clients.swing.common.buttons.BtnSave();
        btnSecurity = new javax.swing.JButton();

        setHeaderPanel(headerPanel1);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle"); // NOI18N
        btnSave.setText(bundle.getString("SLParcelPanel.btnSave.text")); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("SLParcelPanel.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSecurity);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveParcel();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnSecurityActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnSave btnSave;
    private javax.swing.JButton btnSecurity;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.ui.cadastre.ParcelPanel parcelPanel;
    // End of variables declaration//GEN-END:variables
}
