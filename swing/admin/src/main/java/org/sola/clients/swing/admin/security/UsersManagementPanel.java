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
package org.sola.clients.swing.admin.security;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import org.jdesktop.application.Action;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.security.UserBean;
import org.sola.clients.beans.security.UserSearchAdvancedResultBean;
import org.sola.clients.beans.security.UserSearchAdvancedResultListBean;
import org.sola.clients.swing.common.config.ConfigurationManager;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.clients.swing.ui.security.UserPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to manage users of the application.
 */
public class UsersManagementPanel extends javax.swing.JPanel {

    /** Listens for events of {@link UserPanel} */
    private class UserPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(UserPanel.SAVED_USER_PROPERTY)
                    || evt.getPropertyName().equals(UserPanel.CREATED_USER_PROPERTY)) {

                if (evt.getPropertyName().equals(UserPanel.CREATED_USER_PROPERTY)) {
                    pnlUser.setUser(null);
                    MessageUtility.displayMessage(ClientMessage.ADMIN_USER_CREATED);
                } else {
                    MessageUtility.displayMessage(ClientMessage.ADMIN_USER_SAVED);
                    pnlUser.setUser(null);
                    showUsers();
                    searchUsers();
                }
            }
            if (evt.getPropertyName().equals(UserPanel.CANCEL_ACTION_PROPERTY)) {
                showUsers();
            }
        }
    }
    private ResourceBundle resourceBundle;

    /** Creates new form UsersManagementPanel */
    public UsersManagementPanel() {
        initComponents();

        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle");
        showUsers();
        groupsList.loadGroups(true);
        comboGroups.setSelectedIndex(0);
        pnlUser.addPropertyChangeListener(new UserPanelListener());

        userSearchResultList.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(UserSearchAdvancedResultListBean.SELECTED_USER_PROPERTY)) {
                    customizeUserButtons((UserSearchAdvancedResultBean) evt.getNewValue());
                }
            }
        });
        customizeUserButtons(null);
    }

    /** 
     * Enables or disables user management buttons, depending on selection in 
     * the groups table and user rights. 
     */
    private void customizeUserButtons(UserSearchAdvancedResultBean userSearchResult) {
        btnEditUser.getAction().setEnabled(userSearchResult != null);
        btnRemoveUser.getAction().setEnabled(userSearchResult != null);
        btnSetPassword.getAction().setEnabled(userSearchResult != null);
    }

    /** Shows list of users. */
    private void showUsers() {
        pnlUser.setVisible(false);
        pnlPasswordManagement.setVisible(false);
        pnlUsers.setVisible(true);
        pnlHeader.setTitleText(resourceBundle.getString("UsersManagementPanel.pnlHeader.titleText"));
    }

    /** Shows user panel. */
    private void showUser(UserSearchAdvancedResultBean userSearchResult) {
        pnlUsers.setVisible(false);
        pnlPasswordManagement.setVisible(false);
        pnlUser.setVisible(true);
        if (userSearchResult != null) {
            pnlHeader.setTitleText(String.format(resourceBundle.getString("UsersManagementPanel.pnlHeader.titleText.EditUser"), userSearchResult.getUserName()));
        } else {
            pnlHeader.setTitleText(resourceBundle.getString("UsersManagementPanel.pnlHeader.titleText.NewUser"));
        }
    }

    /** Shows password panel. */
    private void showPasswordPanel(String userName) {
        pnlUser.setVisible(false);
        pnlUsers.setVisible(false);
        pnlPasswordManagement.setVisible(true);
        pnlPassword.setUserName(userName);
        pnlHeader.setTitleText(String.format(resourceBundle.getString("UsersManagementPanel.pnlHeader.titleText.ChangePassword"), userName));
    }

    /** Searches users with the given criteria. */
    private void searchUsers() {
        userSearchResultList.searchUsers(userSearchParams);
        if (userSearchResultList.getUsersList().size() < 1) {
            MessageUtility.displayMessage(ClientMessage.ADMIN_USERS_NO_FOUND);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupUsers = new javax.swing.JPopupMenu();
        menuAddUser = new javax.swing.JMenuItem();
        menuEditUser = new javax.swing.JMenuItem();
        menuSetPassword = new javax.swing.JMenuItem();
        menuRemoveUser = new javax.swing.JMenuItem();
        groupsList = new org.sola.clients.beans.security.GroupSummaryListBean();
        userSearchParams = new org.sola.clients.beans.security.UserSearchParamsBean();
        userSearchResultList = new org.sola.clients.beans.security.UserSearchAdvancedResultListBean();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        pnlLayout = new javax.swing.JPanel();
        pnlUsers = new javax.swing.JPanel();
        pnlSearchCriteria = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtFirstName = new javax.swing.JTextField();
        txtLastName = new javax.swing.JTextField();
        comboGroups = new javax.swing.JComboBox();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUsers = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        toolbarUsers = new javax.swing.JToolBar();
        btnAddUser = new javax.swing.JButton();
        btnEditUser = new javax.swing.JButton();
        btnSetPassword = new javax.swing.JButton();
        btnRemoveUser = new javax.swing.JButton();
        pnlUser = new org.sola.clients.swing.ui.security.UserPanel();
        pnlPasswordManagement = new javax.swing.JPanel();
        pnlPassword = new org.sola.clients.swing.ui.security.UserPasswordPanel();
        btnChangePassword = new javax.swing.JButton();
        btnClosePasswordPanel = new javax.swing.JButton();

        popupUsers.setName("popupUsers"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(UsersManagementPanel.class, this);
        menuAddUser.setAction(actionMap.get("addUser")); // NOI18N
        menuAddUser.setName("menuAddUser"); // NOI18N
        popupUsers.add(menuAddUser);

        menuEditUser.setAction(actionMap.get("editUser")); // NOI18N
        menuEditUser.setName("menuEditUser"); // NOI18N
        popupUsers.add(menuEditUser);

        menuSetPassword.setAction(actionMap.get("editPassword")); // NOI18N
        menuSetPassword.setName("menuSetPassword"); // NOI18N
        popupUsers.add(menuSetPassword);

        menuRemoveUser.setAction(actionMap.get("removeUser")); // NOI18N
        menuRemoveUser.setName("menuRemoveUser"); // NOI18N
        popupUsers.add(menuRemoveUser);

        setMinimumSize(new java.awt.Dimension(617, 406));

        pnlHeader.setName("pnlHeader"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("UsersManagementPanel.pnlHeader.titleText")); // NOI18N

        pnlLayout.setName("pnlLayout"); // NOI18N
        pnlLayout.setLayout(new java.awt.CardLayout());

        pnlUsers.setName("pnlUsers"); // NOI18N

        pnlSearchCriteria.setName("pnlSearchCriteria"); // NOI18N
        pnlSearchCriteria.setLayout(new java.awt.GridLayout(2, 4, 15, 0));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText(bundle.getString("UsersManagementPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        pnlSearchCriteria.add(jLabel2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText(bundle.getString("UsersManagementPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        pnlSearchCriteria.add(jLabel1);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText(bundle.getString("UsersManagementPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        pnlSearchCriteria.add(jLabel3);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel4.setText(bundle.getString("UsersManagementPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        pnlSearchCriteria.add(jLabel4);

        txtUsername.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtUsername.setName("txtUsername"); // NOI18N
        txtUsername.setNextFocusableComponent(txtFirstName);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, userSearchParams, org.jdesktop.beansbinding.ELProperty.create("${userName}"), txtUsername, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        pnlSearchCriteria.add(txtUsername);

        txtFirstName.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtFirstName.setName("txtFirstName"); // NOI18N
        txtFirstName.setNextFocusableComponent(txtLastName);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, userSearchParams, org.jdesktop.beansbinding.ELProperty.create("${firstName}"), txtFirstName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        pnlSearchCriteria.add(txtFirstName);

        txtLastName.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtLastName.setName("txtLastName"); // NOI18N
        txtLastName.setNextFocusableComponent(comboGroups);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, userSearchParams, org.jdesktop.beansbinding.ELProperty.create("${lastName}"), txtLastName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        pnlSearchCriteria.add(txtLastName);

        comboGroups.setFont(new java.awt.Font("Tahoma", 0, 12));
        comboGroups.setName("comboGroups"); // NOI18N
        comboGroups.setNextFocusableComponent(btnSearch);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${groupSummaryList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, groupsList, eLProperty, comboGroups);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, userSearchParams, org.jdesktop.beansbinding.ELProperty.create("${groupBean}"), comboGroups, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        pnlSearchCriteria.add(comboGroups);

        btnSearch.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnSearch.setText(bundle.getString("UsersManagementPanel.btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableUsers.setComponentPopupMenu(popupUsers);
        tableUsers.setName("tableUsers"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${usersList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, userSearchResultList, eLProperty, tableUsers);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${userName}"));
        columnBinding.setColumnName("Username");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${firstName}"));
        columnBinding.setColumnName("First name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lastName}"));
        columnBinding.setColumnName("Last name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${groupsList}"));
        columnBinding.setColumnName("Groups");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${active}"));
        columnBinding.setColumnName("Active");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, userSearchResultList, org.jdesktop.beansbinding.ELProperty.create("${selectedUser}"), tableUsers, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableUsers);
        tableUsers.getColumnModel().getColumn(3).setCellRenderer(new TableCellTextAreaRenderer());
        tableUsers.getColumnModel().getColumn(4).setCellRenderer(new TableCellTextAreaRenderer());
        tableUsers.getColumnModel().getColumn(5).setMaxWidth(50);

        toolbarUsers.setFloatable(false);
        toolbarUsers.setRollover(true);
        toolbarUsers.setName("toolbarUsers"); // NOI18N

        btnAddUser.setAction(actionMap.get("addUser")); // NOI18N
        btnAddUser.setFocusable(false);
        btnAddUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddUser.setName("btnAddUser"); // NOI18N
        btnAddUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarUsers.add(btnAddUser);

        btnEditUser.setAction(actionMap.get("editUser")); // NOI18N
        btnEditUser.setFocusable(false);
        btnEditUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditUser.setName("btnEditUser"); // NOI18N
        btnEditUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarUsers.add(btnEditUser);

        btnSetPassword.setAction(actionMap.get("editPassword")); // NOI18N
        btnSetPassword.setFocusable(false);
        btnSetPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSetPassword.setName("btnSetPassword"); // NOI18N
        btnSetPassword.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarUsers.add(btnSetPassword);

        btnRemoveUser.setAction(actionMap.get("removeUser")); // NOI18N
        btnRemoveUser.setFocusable(false);
        btnRemoveUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveUser.setName("btnRemoveUser"); // NOI18N
        btnRemoveUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarUsers.add(btnRemoveUser);

        javax.swing.GroupLayout pnlUsersLayout = new javax.swing.GroupLayout(pnlUsers);
        pnlUsers.setLayout(pnlUsersLayout);
        pnlUsersLayout.setHorizontalGroup(
            pnlUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUsersLayout.createSequentialGroup()
                .addGroup(pnlUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(toolbarUsers, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addGroup(pnlUsersLayout.createSequentialGroup()
                        .addComponent(pnlSearchCriteria, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
        );
        pnlUsersLayout.setVerticalGroup(
            pnlUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsersLayout.createSequentialGroup()
                .addGroup(pnlUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSearch)
                    .addComponent(pnlSearchCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toolbarUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
        );

        pnlLayout.add(pnlUsers, "card3");

        pnlUser.setName("pnlUser"); // NOI18N
        pnlLayout.add(pnlUser, "card2");

        pnlPasswordManagement.setName("pnlPasswordManagement"); // NOI18N

        pnlPassword.setName("pnlPassword"); // NOI18N
        pnlPassword.setNextFocusableComponent(btnChangePassword);

        btnChangePassword.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnChangePassword.setText(bundle.getString("UsersManagementPanel.btnChangePassword.text")); // NOI18N
        btnChangePassword.setName("btnChangePassword"); // NOI18N
        btnChangePassword.setNextFocusableComponent(btnClosePasswordPanel);
        btnChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangePasswordActionPerformed(evt);
            }
        });

        btnClosePasswordPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnClosePasswordPanel.setText(bundle.getString("UsersManagementPanel.btnClosePasswordPanel.text")); // NOI18N
        btnClosePasswordPanel.setName("btnClosePasswordPanel"); // NOI18N
        btnClosePasswordPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClosePasswordPanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPasswordManagementLayout = new javax.swing.GroupLayout(pnlPasswordManagement);
        pnlPasswordManagement.setLayout(pnlPasswordManagementLayout);
        pnlPasswordManagementLayout.setHorizontalGroup(
            pnlPasswordManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPasswordManagementLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPasswordManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPasswordManagementLayout.createSequentialGroup()
                        .addComponent(btnClosePasswordPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(311, Short.MAX_VALUE))
        );
        pnlPasswordManagementLayout.setVerticalGroup(
            pnlPasswordManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPasswordManagementLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlPasswordManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChangePassword)
                    .addComponent(btnClosePasswordPanel))
                .addContainerGap(212, Short.MAX_VALUE))
        );

        pnlLayout.add(pnlPasswordManagement, "card4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlLayout, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLayout, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchUsers();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangePasswordActionPerformed
        if (pnlPassword.changePassword()) {
            MessageUtility.displayMessage(ClientMessage.ADMIN_PASSWORD_CHANGED);
            if (SecurityBean.getCurrentUser().getUserName().equals(pnlPassword.getUserName())) {
                SecurityBean.authenticate(pnlPassword.getUserName(),
                        pnlPassword.getPassword().toCharArray(),
                        ConfigurationManager.getWSConfig());
            }
            pnlPassword.cleanPanel();
            showUsers();
        }
    }//GEN-LAST:event_btnChangePasswordActionPerformed

    private void btnClosePasswordPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClosePasswordPanelActionPerformed
        showUsers();
    }//GEN-LAST:event_btnClosePasswordPanelActionPerformed

    /** Customizes {@link UserPanel} to create new user. */
    @Action
    public void addUser() {
        pnlUser.setUser(null);
        pnlUser.setOkButtonText(MessageUtility.getLocalizedMessage(
                ClientMessage.GENERAL_LABELS_CREATE).getMessage());
        showUser(null);
    }

    /** Customizes {@link UserPanel} to edit selected user from the search results table. */
    @Action
    public void editUser() {
        if (userSearchResultList.getSelectedUser() != null) {
            pnlUser.setUser(UserBean.getUser(userSearchResultList.getSelectedUser().getUserName()));
            pnlUser.setOkButtonText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
            showUser(userSearchResultList.getSelectedUser());
        }
    }

    /** Opens panel to change user password. */
    @Action
    public void editPassword() {
        if (userSearchResultList.getSelectedUser() != null) {
            showPasswordPanel(userSearchResultList.getSelectedUser().getUserName());
        }
    }

    /** Removes selected user. */
    @Action
    public void removeUser() {
        if (userSearchResultList.getSelectedUser() != null) {
            if(userSearchResultList.getSelectedUser().getUserName().equals(SecurityBean.getCurrentUser().getUserName())){
                MessageUtility.displayMessage(ClientMessage.ADMIN_CURRENT_USER_DELETE_ERROR);
                return;
            }
            if (MessageUtility.displayMessage(ClientMessage.ADMIN_CONFIRM_DELETE_USER)
                    == MessageUtility.BUTTON_ONE) {
                UserBean.removeUser(userSearchResultList.getSelectedUser().getUserName());
                userSearchResultList.getUsersList().remove(userSearchResultList.getSelectedUser());
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnChangePassword;
    private javax.swing.JButton btnClosePasswordPanel;
    private javax.swing.JButton btnEditUser;
    private javax.swing.JButton btnRemoveUser;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSetPassword;
    private javax.swing.JComboBox comboGroups;
    private org.sola.clients.beans.security.GroupSummaryListBean groupsList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menuAddUser;
    private javax.swing.JMenuItem menuEditUser;
    private javax.swing.JMenuItem menuRemoveUser;
    private javax.swing.JMenuItem menuSetPassword;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPanel pnlLayout;
    private org.sola.clients.swing.ui.security.UserPasswordPanel pnlPassword;
    private javax.swing.JPanel pnlPasswordManagement;
    private javax.swing.JPanel pnlSearchCriteria;
    private org.sola.clients.swing.ui.security.UserPanel pnlUser;
    private javax.swing.JPanel pnlUsers;
    private javax.swing.JPopupMenu popupUsers;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableUsers;
    private javax.swing.JToolBar toolbarUsers;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtUsername;
    private org.sola.clients.beans.security.UserSearchParamsBean userSearchParams;
    private org.sola.clients.beans.security.UserSearchAdvancedResultListBean userSearchResultList;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
