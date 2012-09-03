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
package org.sola.clients.swing.desktop;

import java.math.BigInteger;
import java.security.MessageDigest;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.security.UserBean;
import org.sola.clients.swing.common.config.ConfigurationManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.security.UserPasswordPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Holds {@link UserPasswordPanel} instance.
 */
public class UserProfileForm extends ContentPanel {
    
  private UserBean user;
  public static final String LOGIN_RESULT = "loginResult";
       
    /** 
     * Default constructor. 
     * @param userName Username for which password is going to be reset.
     */
    public UserProfileForm(String userName) {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        initComponents();
        userPasswordPanel.setUserName(userName);
        userPasswordPanel.jLabel1.setText(bundle.getString("UserProfileForm.newpassword.text"));
        userPasswordPanel.jLabel2.setText(bundle.getString("UserProfileForm.confirmnewpassword.text"));
        headerPanel.setTitleText(String.format(headerPanel.getTitleText(), userName));
        this.user = SecurityBean.getCurrentUser();
        txtFirstName.setText(this.user.getFirstName());
        txtLastName.setText(this.user.getLastName());
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        securityBean = new org.sola.clients.beans.security.SecurityBean();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        txtLastName = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        btnSaveUserDetails = new javax.swing.JButton();
        userPasswordPanel = new org.sola.clients.swing.ui.security.UserPasswordPanel();
        jLabel1 = new javax.swing.JLabel();
        txtOldPassword = new javax.swing.JPasswordField();
        jToolBar2 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        jPanel1.setName(bundle.getString("UserProfileForm.jPanel1.name")); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(2, 2, 2, 2));

        jLabel2.setText(bundle.getString("UserProfileForm.jLabel2.text")); // NOI18N
        jLabel2.setName(bundle.getString("UserProfileForm.jLabel2.name")); // NOI18N
        jPanel1.add(jLabel2);

        jLabel3.setText(bundle.getString("UserProfileForm.jLabel3.text")); // NOI18N
        jLabel3.setName(bundle.getString("UserProfileForm.jLabel3.name")); // NOI18N
        jPanel1.add(jLabel3);

        txtFirstName.setName(bundle.getString("UserProfileForm.txtFirstName.name")); // NOI18N
        jPanel1.add(txtFirstName);

        txtLastName.setName(bundle.getString("UserProfileForm.txtLastName.name")); // NOI18N
        jPanel1.add(txtLastName);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSaveUserDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSaveUserDetails.setText(bundle.getString("UserProfileForm.btnSaveUserDetails.text")); // NOI18N
        btnSaveUserDetails.setFocusable(false);
        btnSaveUserDetails.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSaveUserDetails.setName(bundle.getString("UserProfileForm.btnSaveUserDetails.name")); // NOI18N
        btnSaveUserDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveUserDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveUserDetailsActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSaveUserDetails);

        userPasswordPanel.setName("userPasswordPanel"); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel1.setText(bundle.getString("UserProfileForm.jLabel1.text")); // NOI18N
        jLabel1.setName(bundle.getString("UserProfileForm.jLabel1.name")); // NOI18N

        txtOldPassword.setName(bundle.getString("UserProfileForm.txtOldPassword.name")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, securityBean, org.jdesktop.beansbinding.ELProperty.create("${userPassword}"), txtOldPassword, org.jdesktop.beansbinding.BeanProperty.create("text"), "");
        bindingGroup.addBinding(binding);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName(bundle.getString("UserProfileForm.jToolBar2.name")); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/key.png"))); // NOI18N
        btnSave.setText(bundle.getString("UserProfileForm.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName(bundle.getString("UserProfileForm.btnSave.name")); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSave);

        groupPanel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        groupPanel1.setName(bundle.getString("UserProfileForm.groupPanel1.name")); // NOI18N
        groupPanel1.setTitleText(bundle.getString("UserProfileForm.groupPanel1.titleText")); // NOI18N

        headerPanel.setName(bundle.getString("UserProfileForm.headerPanel.name")); // NOI18N
        headerPanel.setTitleText(bundle.getString("UserProfileForm.headerPanel.titleText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userPasswordPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userPasswordPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    
    
    private void btnSaveUserDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveUserDetailsActionPerformed
         
            user.setFirstName(this.txtFirstName.getText());
            user.setLastName(this.txtLastName.getText());
            user.save();
            MessageUtility.displayMessage(ClientMessage.ADMIN_USER_SAVED);
    }//GEN-LAST:event_btnSaveUserDetailsActionPerformed

    /**
     * Returns SHA-256 hash for the password.
     *
     * @param password Password string to hash.
     */
    private String getPasswordHash(String password) {
        String hashString = null;

        if (password != null && password.length() > 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes("UTF-8"));
                byte[] hash = md.digest();

                BigInteger bigInt = new BigInteger(1, hash);
                hashString = bigInt.toString(16);

            } catch (Exception e) {
                e.printStackTrace(System.err);
                return null;
            }
        }

        return hashString;
    }
    
    
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        String oldPassword = new String(txtOldPassword.getPassword()); 
        
        if (oldPassword.isEmpty()||oldPassword==null||oldPassword=="") {
             MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
                     new Object[]{bundle.getString("UserProfileForm.jLabel1.text")});  
          return;
        }
        
//        Verify that the old password is correct encrypting it and matching it with the one retrieved by the db
        oldPassword = getPasswordHash(oldPassword); 
        String dbPassword =  new String(SecurityBean.getCurrentUser().getPassword().toCharArray()); 
        if (!oldPassword.equals(dbPassword)) {
          MessageUtility.displayMessage(ClientMessage.CHECK_CURRENTPSSWD_NOTMATCH);
          return;
         }
    
//        Ask for confirmation since the system will be closed for getting new password set
        if (MessageUtility.displayMessage(ClientMessage.CONFIRM_SYSTEM_WILL_BE_CLOSED) != MessageUtility.BUTTON_ONE) {
                return;
        } 
           
            if (userPasswordPanel.changePassword()) {
                MessageUtility.displayMessage(ClientMessage.ADMIN_PASSWORD_CHANGED);
                    System.exit(0);
            }
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveUserDetails;
    public org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private org.sola.clients.beans.security.SecurityBean securityBean;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JPasswordField txtOldPassword;
    private org.sola.clients.swing.ui.security.UserPasswordPanel userPasswordPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
