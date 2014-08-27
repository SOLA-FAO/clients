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
package org.sola.clients.swing.gis.ui.control;

import java.net.URL;
import javax.swing.ImageIcon;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.gis.beans.StateLandParcelBean;
import org.sola.clients.swing.ui.cadastre.ParcelPanel;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;

/**
 *
 * @author soladev
 */
public class StateLandParcelForm extends javax.swing.JDialog {

    public static final String PARCEL_SAVED = "parcelSaved";
    private boolean readOnly;
    private StateLandParcelBean slParcel;

    /**
     * Creates new form StateLandParcelForm
     */
    public StateLandParcelForm(StateLandParcelBean parcel, boolean readOnly,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.readOnly = readOnly;
        this.slParcel = parcel;
        initComponents();
        customizeForm();
    }

    private ParcelPanel createParcelPanel() {
        return new ParcelPanel(slParcel.getClientCadastreObject(), readOnly);
    }

    private void customizeForm() {
        WindowUtility.addEscapeListener(this, false);
        WindowUtility.centerForm(this);
        URL imgURL = this.getClass().getResource("/images/logo_icon.jpg");
        this.setIconImage(new ImageIcon(imgURL).getImage());

        String parcelName = slParcel.isNew() ? "New" : slParcel.toString(); 
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        setTitle(String.format(bundle.getString("StateLandParcelForm.title"), slParcel.toString())); // NOI18N

        btnSave.setEnabled(!readOnly);
        btnSecurity.setVisible(!readOnly
                && SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));
    }

    private void save() {
        StateLandParcelBean old = slParcel;
        slParcel.copyClientCadastreObject(parcelPanel.getCadastreObject());
        this.firePropertyChange(PARCEL_SAVED, old, slParcel);
        this.dispose();
    }

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(parcelPanel.getCadastreObject(),
                WindowUtility.getTopFrame(), true);
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

        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new org.sola.clients.swing.common.buttons.BtnSave();
        btnSecurity = new javax.swing.JButton();
        parcelPanel = createParcelPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        setTitle(bundle.getString("StateLandParcelForm.title")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setText(bundle.getString("StateLandParcelForm.btnSave.text")); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("StateLandParcelForm.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSecurity);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnSecurityActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnSave btnSave;
    private javax.swing.JButton btnSecurity;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.ui.cadastre.ParcelPanel parcelPanel;
    // End of variables declaration//GEN-END:variables
}