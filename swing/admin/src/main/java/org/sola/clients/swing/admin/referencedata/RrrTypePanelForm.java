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

package org.sola.clients.swing.admin.referencedata;

import java.util.ResourceBundle;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.referencedata.RrrTypePanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Holds instance of {@link RrrTypePanel}.
 */
public class RrrTypePanelForm extends ContentPanel {

    private boolean saveOnAction;
    private boolean closeOnSave;
    private RrrTypeBean rrrTypeBean;
    private ResourceBundle resourceBundle;
    
    /** Default constructor. */
    public RrrTypePanelForm() {
        initComponents();
    }

    /** 
     * Form constructor. 
     * @param requestTypeBean The instance of request type object to show on the panel.
     * @param saveOnAction If <code>true</code>, request type object will be saved into the database. 
     * If <code>false</code>, it will be validated and validation result returned as a value of 
     * {@link ReferenceDataPanelForm.REFDATA_SAVED_PROPERTY} property change event.
     * @param closeOnSave Indicates whether to close the form upon save action takes place.
     */
    public RrrTypePanelForm(RrrTypeBean rrrTypeBean, boolean saveOnAction, boolean closeOnSave) {
        this.rrrTypeBean = rrrTypeBean;
        this.saveOnAction = saveOnAction;
        this.closeOnSave = closeOnSave;
        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/referencedata/Bundle"); 
        
        initComponents();
        setRrrTypeBean(this.rrrTypeBean);
    }
    
    public boolean isCloseOnSave() {
        return closeOnSave;
    }

    public void setCloseOnSave(boolean closeOnSave) {
        this.closeOnSave = closeOnSave;
        customizePanel();
    }

    public RrrTypeBean getRrrTypeBean() {
        return rrrTypePanel.getRrrTypeBean();
    }

    public final void setRrrTypeBean(RrrTypeBean rrrTypeBean) {
        this.rrrTypeBean = rrrTypeBean;
        rrrTypePanel.setRrrTypeBean(rrrTypeBean);
        customizePanel();
    }

    public boolean isSaveOnAction() {
        return saveOnAction;
    }

    public void setSaveOnAction(boolean saveOnAction) {
        this.saveOnAction = saveOnAction;
    }
    
    private void customizePanel() {
        if (rrrTypeBean != null) {
            headerPanel.setTitleText(String.format(
                    resourceBundle.getString("RrrTypePanelForm.headerPanel.titleText"),
                    rrrTypeBean.getTranslatedDisplayValue()));
        } else {
            headerPanel.setTitleText(resourceBundle
                    .getString("RrrTypePanelForm.headerPanel.titleText.new"));
        }

        if (closeOnSave) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
        } else {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE).getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        rrrTypePanel = new org.sola.clients.swing.ui.referencedata.RrrTypePanel();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);

        headerPanel.setName("headerPanel"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/referencedata/Bundle"); // NOI18N
        headerPanel.setTitleText(bundle.getString("RrrTypePanelForm.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("RrrTypePanelForm.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        rrrTypePanel.setName("rrrTypePanel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rrrTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rrrTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean isSaved = false;
        
        if(saveOnAction){
            if(rrrTypePanel.save(true)){
                MessageUtility.displayMessage(ClientMessage.ADMIN_REFDATA_SAVED, 
                        new String[]{rrrTypePanel.getRrrTypeBean().getTranslatedDisplayValue()});
                isSaved = true;
            }
        } else {
            if(rrrTypePanel.validateRrrType(true)){
                isSaved = true;
            }
        }
        
        if(isSaved){
            firePropertyChange(ReferenceDataPanelForm.REFDATA_SAVED_PROPERTY, false, true);
            if(closeOnSave){
                close();
            } else {
                customizePanel();
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.ui.referencedata.RrrTypePanel rrrTypePanel;
    // End of variables declaration//GEN-END:variables
}
