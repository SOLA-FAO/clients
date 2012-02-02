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
import org.sola.clients.beans.security.UserBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.security.UserPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Holds instance of {@link UserPanel}
 */
public class UserPanelForm extends ContentPanel {

    public static final String USER_SAVED_PROPERTY = "UserSaved";
    
    private boolean saveOnAction;
    private boolean closeOnSave;
    private boolean readOnly;
    private UserBean userBean;
    private ResourceBundle resourceBundle;
    
    /** Default constructor. */
    public UserPanelForm() {
        initComponents();
    }
    
    /** 
     * Form constructor. 
     * @param userBean The user bean instance to show on the panel.
     * @param saveOnAction If <code>true</code>, user will be saved into database. 
     * If <code>false</code>, user will be validated and validation result returned as a value of 
     * {@link UserPanelForm.USER_SAVED_PROPERTY} property change event.
     * @param closeOnSave Indicates whether to close the form upon save action takes place.
     * @param readOnly Indicates whether to display provided {@link UserBean} in read only mode or not.
     */
    public UserPanelForm(UserBean userBean, boolean saveOnAction, boolean closeOnSave, boolean readOnly) {
        this.userBean = userBean;
        this.saveOnAction = saveOnAction;
        this.readOnly = readOnly;
        this.closeOnSave = closeOnSave;
        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); 
        
        initComponents();
        setUserBean(this.userBean);
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

    public UserBean getUserBean() {
        return userPanel.getUser();
    }

    public final void setUserBean(UserBean userBean) {
        this.userBean = userBean;
        userPanel.setUser(userBean);
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
        userPanel.lockForm(readOnly);
        
        if (userBean != null) {
            headerPanel.setTitleText(String.format(
                    resourceBundle.getString("UserPanelForm.headerPanel.titleText.EditUser"), 
                    getUserBean().getUserName()));
        } else {
            headerPanel.setTitleText(resourceBundle.getString("UserPanelForm.headerPanel.titleText"));
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
        userPanel = new org.sola.clients.swing.ui.security.UserPanel();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);

        headerPanel.setName("headerPanel"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        headerPanel.setTitleText(bundle.getString("UserPanelForm.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("UserPanelForm.btnSave.text")); // NOI18N
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

        userPanel.setName("userPanel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
            .addComponent(userPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean isSaved = false;
        
        if(saveOnAction){
            if(userPanel.saveUser(true)){
                MessageUtility.displayMessage(ClientMessage.ADMIN_USER_SAVED);
                isSaved = true;
            }
        } else {
            if(userPanel.validateUser(true)){
                isSaved = true;
            }
        }
        
        if(isSaved){
            firePropertyChange(USER_SAVED_PROPERTY, false, true);
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
    private org.sola.clients.swing.ui.security.UserPanel userPanel;
    // End of variables declaration//GEN-END:variables
}
