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
package org.sola.clients.swing.admin.security;

import java.util.ResourceBundle;
import org.sola.clients.beans.security.GroupBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.GroupPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Holds {@link GroupPanel} instance.
 */
public class GroupPanelForm extends ContentPanel {

    public static final String GROUP_SAVED_PROPERTY = "GroupSaved";
    
    private boolean saveOnAction;
    private boolean closeOnSave;
    private boolean readOnly;
    private GroupBean groupBean;
    private ResourceBundle resourceBundle;
    
    /** Default constructor. */
    public GroupPanelForm() {
        initComponents();
    }
    
    /** 
     * Form constructor. 
     * @param groupBean The group bean instance to show on the panel.
     * @param saveOnAction If <code>true</code>, group will be saved into database. 
     * If <code>false</code>, group will be validated and validation result returned as a value of 
     * {@link GroupPanelForm.GROUP_SAVED_PROPERTY} property change event.
     * @param closeOnSave Indicates whether to close the form upon save action takes place.
     * @param readOnly Indicates whether to display provided {@link GroupBean} in read only mode or not.
     */
    public GroupPanelForm(GroupBean groupBean, boolean saveOnAction, boolean closeOnSave, boolean readOnly) {
        this.groupBean = groupBean;
        this.saveOnAction = saveOnAction;
        this.readOnly = readOnly;
        this.closeOnSave = closeOnSave;
        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); 
        
        initComponents();
        setGroupBean(this.groupBean);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        customizePanel();
    }
    
    public boolean isCloseOnSave() {
        return closeOnSave;
    }

    public void setCloseOnSave(boolean closeOnSave) {
        this.closeOnSave = closeOnSave;
        customizePanel();
    }

    public GroupBean getGroupBean() {
        return groupPanel.getGroup();
    }

    public final void setGroupBean(GroupBean groupBean) {
        this.groupBean = groupBean;
        groupPanel.setGroup(groupBean);
        customizePanel();
    }

    public boolean isSaveOnAction() {
        return saveOnAction;
    }

    public void setSaveOnAction(boolean saveOnAction) {
        this.saveOnAction = saveOnAction;
    }
    
    private void customizePanel() {
        btnSave.setEnabled(!readOnly);
        groupPanel.lockForm(readOnly);
        
        if (groupBean != null) {
            headerPanel.setTitleText(String.format(resourceBundle
                    .getString("GroupPanelForm.headerPanel.titleText.EditGroup"), getGroupBean().getName()));
        } else {
            headerPanel.setTitleText(resourceBundle.getString("GroupPanelForm.headerPanel.titleText.NewGroup"));
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
        groupPanel = new org.sola.clients.swing.ui.security.GroupPanel();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);

        headerPanel.setName("headerPanel"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        headerPanel.setTitleText(bundle.getString("GroupPanelForm.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("GroupPanelForm.btnSave.text")); // NOI18N
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

        groupPanel.setName("groupPanel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(groupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(groupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean isSaved = false;
        
        if(saveOnAction){
            if(groupPanel.saveGroup(true)){
                MessageUtility.displayMessage(ClientMessage.ADMIN_GROUP_SAVED);
                isSaved = true;
            }
        } else {
            if(groupPanel.validateGroup(true)){
                isSaved = true;
            }
        }
        
        if(isSaved){
            firePropertyChange(GROUP_SAVED_PROPERTY, false, true);
            if(closeOnSave){
                close();
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private org.sola.clients.swing.ui.security.GroupPanel groupPanel;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
