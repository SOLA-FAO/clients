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
package org.sola.clients.swing.admin.security;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import org.jdesktop.application.Action;
import org.sola.clients.beans.security.RoleBean;
import org.sola.clients.beans.security.RoleListBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.clients.swing.ui.security.RolePanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to manage roles.
 */
public class RolesManagementPanel extends javax.swing.JPanel {

    /** Listens for events of {@link RolePanel} */
    private class RolePanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            
            if (property.equals(RolePanel.SAVED_ROLE_PROPERTY) ||
                    property.equals(RolePanel.CREATED_ROLE_PROPERTY)) {
                
                if(property.equals(RolePanel.CREATED_ROLE_PROPERTY)){
                    MessageUtility.displayMessage(ClientMessage.ADMIN_ROLE_CREATED);
                }else{
                    MessageUtility.displayMessage(ClientMessage.ADMIN_ROLE_SAVED);
                }
                
                pnlRole.setRole(null);
                showRoles();
                roleListBean.loadRoles();
            }
            if (property.equals(RolePanel.CANCEL_ACTION_PROPERTY)) {
                showRoles();
            }
        }
    }
    
    private ResourceBundle resourceBundle;
    
    /** Creates new form RolesManagementPanel */
    public RolesManagementPanel() {
        initComponents();
        customizeComponents();
        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); 
        roleListBean.loadRoles();
        showRoles();
        pnlRole.addPropertyChangeListener(new RolePanelListener());

        roleListBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RoleListBean.SELECTED_ROLE_PROPERTY)) {
                    customizeRoleButtons((RoleBean) evt.getNewValue());
                }
            }
        });
        customizeRoleButtons(null);
    }
     
    
     /** Applies customization of component L&F. */
    private void customizeComponents() {
    //    BUTTONS   
     LafManager.getInstance().setBtnProperties(btnAddRole);
     LafManager.getInstance().setBtnProperties(btnEditRole);
     LafManager.getInstance().setBtnProperties(btnRemoveRole);
    
    }

    
    /** 
     * Enables or disables roles management buttons, depending on selection in 
     * the roles table and user rights. 
     */
    private void customizeRoleButtons(RoleBean role) {
        btnRemoveRole.getAction().setEnabled(role != null);
        btnEditRole.getAction().setEnabled(role != null);
    }
    
    /** Shows list of roles */
    private void showRoles(){
        pnlRoleContainer.setVisible(false);
        pnlRoles.setVisible(true);
        pnlHeader.setTitleText(resourceBundle.getString("RolesManagementPanel.pnlHeader.titleText"));
    }

    /** Shows role panel. */
    private void showRole(RoleBean role){
        pnlRoles.setVisible(false);
        pnlRoleContainer.setVisible(true);
        if(role!=null){
            pnlHeader.setTitleText(String.format(resourceBundle
                    .getString("RolesManagementPanel.pnlHeader.titleText.EditRole"), role.getDisplayValue()));
        }else{
            pnlHeader.setTitleText(resourceBundle.getString("RolesManagementPanel.pnlHeader.titleText.NewRole"));
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        roleListBean = new org.sola.clients.beans.security.RoleListBean();
        popupRole = new javax.swing.JPopupMenu();
        menuAddRole = new javax.swing.JMenuItem();
        menuEditRole = new javax.swing.JMenuItem();
        menuRemoveRole = new javax.swing.JMenuItem();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        pnlLayout = new javax.swing.JPanel();
        pnlRoles = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRoles = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        toolBarRoles = new javax.swing.JToolBar();
        btnAddRole = new javax.swing.JButton();
        btnEditRole = new javax.swing.JButton();
        btnRemoveRole = new javax.swing.JButton();
        pnlRoleContainer = new javax.swing.JPanel();
        pnlRole = new org.sola.clients.swing.ui.security.RolePanel();

        popupRole.setName("popupRole"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(RolesManagementPanel.class, this);
        menuAddRole.setAction(actionMap.get("addRole")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        menuAddRole.setText(bundle.getString("RolesManagementPanel.menuAddRole.text")); // NOI18N
        menuAddRole.setName("menuAddRole"); // NOI18N
        popupRole.add(menuAddRole);

        menuEditRole.setAction(actionMap.get("editRole")); // NOI18N
        menuEditRole.setText(bundle.getString("RolesManagementPanel.menuEditRole.text")); // NOI18N
        menuEditRole.setName("menuEditRole"); // NOI18N
        popupRole.add(menuEditRole);

        menuRemoveRole.setAction(actionMap.get("removeRole")); // NOI18N
        menuRemoveRole.setText(bundle.getString("RolesManagementPanel.menuRemoveRole.text")); // NOI18N
        menuRemoveRole.setName("menuRemoveRole"); // NOI18N
        popupRole.add(menuRemoveRole);

        setMinimumSize(new java.awt.Dimension(501, 287));
        setRequestFocusEnabled(false);

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("RolesManagementPanel.pnlHeader.titleText")); // NOI18N

        pnlLayout.setName("pnlLayout"); // NOI18N
        pnlLayout.setLayout(new java.awt.CardLayout());

        pnlRoles.setName("pnlRoles"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableRoles.setComponentPopupMenu(popupRole);
        tableRoles.setName("tableRoles"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${roleListFiltered}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, roleListBean, eLProperty, tableRoles);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${displayValue}"));
        columnBinding.setColumnName("Display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status}"));
        columnBinding.setColumnName("Status");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, roleListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedRole}"), tableRoles, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableRoles);
        tableRoles.getColumnModel().getColumn(0).setPreferredWidth(160);
        tableRoles.getColumnModel().getColumn(0).setMaxWidth(160);
        tableRoles.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("RolesManagementPanel.tableRoles.columnModel.title0")); // NOI18N
        tableRoles.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("RolesManagementPanel.tableRoles.columnModel.title1")); // NOI18N
        tableRoles.getColumnModel().getColumn(1).setCellRenderer(new TableCellTextAreaRenderer());
        tableRoles.getColumnModel().getColumn(2).setMaxWidth(90);
        tableRoles.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("RolesManagementPanel.tableRoles.columnModel.title2")); // NOI18N

        toolBarRoles.setFloatable(false);
        toolBarRoles.setRollover(true);
        toolBarRoles.setName("toolBarRoles"); // NOI18N

        btnAddRole.setAction(actionMap.get("addRole")); // NOI18N
        btnAddRole.setText(bundle.getString("RolesManagementPanel.btnAddRole.text")); // NOI18N
        btnAddRole.setFocusable(false);
        btnAddRole.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddRole.setName("btnAddRole"); // NOI18N
        btnAddRole.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarRoles.add(btnAddRole);

        btnEditRole.setAction(actionMap.get("editRole")); // NOI18N
        btnEditRole.setText(bundle.getString("RolesManagementPanel.btnEditRole.text")); // NOI18N
        btnEditRole.setFocusable(false);
        btnEditRole.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditRole.setName("btnEditRole"); // NOI18N
        btnEditRole.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarRoles.add(btnEditRole);

        btnRemoveRole.setAction(actionMap.get("removeRole")); // NOI18N
        btnRemoveRole.setText(bundle.getString("RolesManagementPanel.btnRemoveRole.text")); // NOI18N
        btnRemoveRole.setFocusable(false);
        btnRemoveRole.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveRole.setName("btnRemoveRole"); // NOI18N
        btnRemoveRole.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarRoles.add(btnRemoveRole);

        javax.swing.GroupLayout pnlRolesLayout = new javax.swing.GroupLayout(pnlRoles);
        pnlRoles.setLayout(pnlRolesLayout);
        pnlRolesLayout.setHorizontalGroup(
            pnlRolesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBarRoles, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );
        pnlRolesLayout.setVerticalGroup(
            pnlRolesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRolesLayout.createSequentialGroup()
                .addComponent(toolBarRoles, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
        );

        pnlLayout.add(pnlRoles, "card3");

        pnlRoleContainer.setName("pnlRoleContainer"); // NOI18N

        pnlRole.setName("pnlRole"); // NOI18N

        javax.swing.GroupLayout pnlRoleContainerLayout = new javax.swing.GroupLayout(pnlRoleContainer);
        pnlRoleContainer.setLayout(pnlRoleContainerLayout);
        pnlRoleContainerLayout.setHorizontalGroup(
            pnlRoleContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoleContainerLayout.createSequentialGroup()
                .addComponent(pnlRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlRoleContainerLayout.setVerticalGroup(
            pnlRoleContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoleContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        pnlLayout.add(pnlRoleContainer, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlLayout, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLayout, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /** Customizes {@link RolePanel} to create new role. */
    @Action
    public void addRole() {
        pnlRole.setRole(null);
        pnlRole.setOkButtonText(MessageUtility.getLocalizedMessage(
                        ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        showRole(null);
    }

    /** Customizes {@link RolePanel} to edit selected role from the roles table. */
    @Action
    public void editRole() {
        if (roleListBean.getSelectedRole() != null) {
            pnlRole.setRole(roleListBean.getSelectedRole());
            pnlRole.setOkButtonText(MessageUtility.getLocalizedMessage(
                        ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
            showRole(roleListBean.getSelectedRole());
        }
    }

    /** Removes selected role from the list. */
    @Action
    public void removeRole() {
        if (roleListBean.getSelectedRole() != null
                && MessageUtility.displayMessage(ClientMessage.ADMIN_CONFIRM_DELETE_ROLE)
                == MessageUtility.BUTTON_ONE) {
            roleListBean.removeSelectedRole();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRole;
    private javax.swing.JButton btnEditRole;
    private javax.swing.JButton btnRemoveRole;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menuAddRole;
    private javax.swing.JMenuItem menuEditRole;
    private javax.swing.JMenuItem menuRemoveRole;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPanel pnlLayout;
    private org.sola.clients.swing.ui.security.RolePanel pnlRole;
    private javax.swing.JPanel pnlRoleContainer;
    private javax.swing.JPanel pnlRoles;
    private javax.swing.JPopupMenu popupRole;
    private org.sola.clients.beans.security.RoleListBean roleListBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRoles;
    private javax.swing.JToolBar toolBarRoles;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
