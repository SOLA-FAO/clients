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
import org.sola.clients.beans.security.RoleBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.security.RolePanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Holds instance of {@link RolePanel}
 */
public class RolePanelForm extends ContentPanel {

    public static final String ROLE_SAVED_PROPERTY = "RoleSaved";
    
    private boolean saveOnAction;
    private boolean closeOnSave;
    private boolean readOnly;
    private RoleBean roleBean;
    private ResourceBundle resourceBundle;
    
    /** Default constructor. */
    public RolePanelForm() {
        initComponents();
    }

    /** 
     * Form constructor. 
     * @param roleBean The role bean instance to show on the panel.
     * @param saveOnAction If <code>true</code>, role will be saved into database. 
     * If <code>false</code>, role will be validated and validation result returned as a value of 
     * {@link RolePanelForm.ROLE_SAVED_PROPERTY} property change event.
     * @param closeOnSave Indicates whether to close the form upon save action takes place.
     * @param readOnly Indicates whether to display provided {@link RoleBean} in read only mode or not.
     */
    public RolePanelForm(RoleBean roleBean, boolean saveOnAction, boolean closeOnSave, boolean readOnly) {
        this.roleBean = roleBean;
        this.saveOnAction = saveOnAction;
        this.readOnly = readOnly;
        this.closeOnSave = closeOnSave;
        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); 
        
        initComponents();
        setRoleBean(this.roleBean);
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

    public RoleBean getRoleBean() {
        return rolePanel.getRole();
    }

    public final void setRoleBean(RoleBean roleBean) {
        this.roleBean = roleBean;
        rolePanel.setRole(roleBean);
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
        rolePanel.lockForm(readOnly);
        
        if(roleBean!=null){
            headerPanel.setTitleText(String.format(resourceBundle
                    .getString("RolePanelForm.pnlHeader.titleText.EditRole"), getRoleBean().getDisplayValue()));
        }else{
            headerPanel.setTitleText(resourceBundle.getString("RolePanelForm.pnlHeader.titleText.NewRole"));
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
        rolePanel = new org.sola.clients.swing.ui.security.RolePanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);

        headerPanel.setName("headerPanel"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        headerPanel.setTitleText(bundle.getString("RolePanelForm.headerPanel.titleText")); // NOI18N

        rolePanel.setName("rolePanel"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("RolePanelForm.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rolePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rolePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean isSaved = false;
        
        if(saveOnAction){
            if(rolePanel.saveRole(true)){
                MessageUtility.displayMessage(ClientMessage.ADMIN_ROLE_SAVED);
                isSaved = true;
            }
        } else {
            if(rolePanel.validateRole(true)){
                isSaved = true;
            }
        }
        
        if(isSaved){
            firePropertyChange(ROLE_SAVED_PROPERTY, false, true);
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
    private org.sola.clients.swing.ui.security.RolePanel rolePanel;
    // End of variables declaration//GEN-END:variables
}
