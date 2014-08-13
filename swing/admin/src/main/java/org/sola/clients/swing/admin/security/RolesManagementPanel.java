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
package org.sola.clients.swing.admin.security;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.security.RoleBean;
import org.sola.clients.beans.security.RoleListBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to manage roles.
 */
public class RolesManagementPanel extends ContentPanel {
    
    /** Creates new form RolesManagementPanel */
    public RolesManagementPanel() {
        initComponents();
        roleListBean.loadRoles();
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
    
    /** 
     * Enables or disables roles management buttons, depending on selection in 
     * the roles table and user rights. 
     */
    private void customizeRoleButtons(RoleBean role) {
        btnRemoveRole.setEnabled(role != null);
        btnEditRole.setEnabled(role != null);
        menuEditRole.setEnabled(btnEditRole.isEnabled());
        menuRemoveRole.setEnabled(btnRemoveRole.isEnabled());
    }
    
    /** Shows role panel. */
    private void showRole(final RoleBean role){
        RolePanelForm panel = new RolePanelForm(role, true, role != null, false);
        panel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RolePanelForm.ROLE_SAVED_PROPERTY)) {
                    if (role == null) {
                        ((RolePanelForm) evt.getSource()).setRoleBean(null);
                    }
                   roleListBean.loadRoles();
                }
            }
        });
        getMainContentPanel().addPanel(panel, MainContentPanel.CARD_ADMIN_ROLE, true);
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
        pnlRoles = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRoles = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        toolBarRoles = new javax.swing.JToolBar();
        btnAddRole = new javax.swing.JButton();
        btnEditRole = new javax.swing.JButton();
        btnRemoveRole = new javax.swing.JButton();

        popupRole.setName("popupRole"); // NOI18N

        menuAddRole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        menuAddRole.setText(bundle.getString("RolesManagementPanel.menuAddRole.text")); // NOI18N
        menuAddRole.setName("menuAddRole"); // NOI18N
        menuAddRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddRoleActionPerformed(evt);
            }
        });
        popupRole.add(menuAddRole);

        menuEditRole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditRole.setText(bundle.getString("RolesManagementPanel.menuEditRole.text")); // NOI18N
        menuEditRole.setName("menuEditRole"); // NOI18N
        menuEditRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditRoleActionPerformed(evt);
            }
        });
        popupRole.add(menuEditRole);

        menuRemoveRole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveRole.setText(bundle.getString("RolesManagementPanel.menuRemoveRole.text")); // NOI18N
        menuRemoveRole.setName("menuRemoveRole"); // NOI18N
        menuRemoveRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveRoleActionPerformed(evt);
            }
        });
        popupRole.add(menuRemoveRole);

        setHeaderPanel(pnlHeader);
        setMinimumSize(new java.awt.Dimension(100, 100));
        setRequestFocusEnabled(false);

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("RolesManagementPanel.pnlHeader.titleText")); // NOI18N

        pnlRoles.setName("pnlRoles"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableRoles.setComponentPopupMenu(popupRole);
        tableRoles.setName("tableRoles"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${roleListFiltered}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, roleListBean, eLProperty, tableRoles);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${translatedDisplayValue}"));
        columnBinding.setColumnName("Translated Display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${translatedDescription}"));
        columnBinding.setColumnName("Translated Description");
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
        if (tableRoles.getColumnModel().getColumnCount() > 0) {
            tableRoles.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("RolesManagementPanel.tableRoles.columnModel.title0")); // NOI18N
            tableRoles.getColumnModel().getColumn(1).setPreferredWidth(400);
            tableRoles.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("RolesManagementPanel.tableRoles.columnModel.title1")); // NOI18N
            tableRoles.getColumnModel().getColumn(1).setCellRenderer(new TableCellTextAreaRenderer());
            tableRoles.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("RolesManagementPanel.tableRoles.columnModel.title2")); // NOI18N
        }

        toolBarRoles.setFloatable(false);
        toolBarRoles.setRollover(true);
        toolBarRoles.setName("toolBarRoles"); // NOI18N

        btnAddRole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddRole.setText(bundle.getString("RolesManagementPanel.btnAddRole.text")); // NOI18N
        btnAddRole.setFocusable(false);
        btnAddRole.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddRole.setName("btnAddRole"); // NOI18N
        btnAddRole.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRoleActionPerformed(evt);
            }
        });
        toolBarRoles.add(btnAddRole);

        btnEditRole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditRole.setText(bundle.getString("RolesManagementPanel.btnEditRole.text")); // NOI18N
        btnEditRole.setFocusable(false);
        btnEditRole.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditRole.setName("btnEditRole"); // NOI18N
        btnEditRole.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditRoleActionPerformed(evt);
            }
        });
        toolBarRoles.add(btnEditRole);

        btnRemoveRole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveRole.setText(bundle.getString("RolesManagementPanel.btnRemoveRole.text")); // NOI18N
        btnRemoveRole.setFocusable(false);
        btnRemoveRole.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveRole.setName("btnRemoveRole"); // NOI18N
        btnRemoveRole.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveRoleActionPerformed(evt);
            }
        });
        toolBarRoles.add(btnRemoveRole);

        javax.swing.GroupLayout pnlRolesLayout = new javax.swing.GroupLayout(pnlRoles);
        pnlRoles.setLayout(pnlRolesLayout);
        pnlRolesLayout.setHorizontalGroup(
            pnlRolesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBarRoles, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
        );
        pnlRolesLayout.setVerticalGroup(
            pnlRolesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRolesLayout.createSequentialGroup()
                .addComponent(toolBarRoles, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlRoles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlRoles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRoleActionPerformed
        addRole();
    }//GEN-LAST:event_btnAddRoleActionPerformed

    private void btnEditRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditRoleActionPerformed
        editRole();
    }//GEN-LAST:event_btnEditRoleActionPerformed

    private void btnRemoveRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRoleActionPerformed
        removeRole();
    }//GEN-LAST:event_btnRemoveRoleActionPerformed

    private void menuAddRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddRoleActionPerformed
        addRole();
    }//GEN-LAST:event_menuAddRoleActionPerformed

    private void menuEditRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditRoleActionPerformed
        editRole();
    }//GEN-LAST:event_menuEditRoleActionPerformed

    private void menuRemoveRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveRoleActionPerformed
        removeRole();
    }//GEN-LAST:event_menuRemoveRoleActionPerformed

    private void addRole() {
        showRole(null);
    }

    private void editRole() {
        if (roleListBean.getSelectedRole() != null) {
            showRole(roleListBean.getSelectedRole());
        }
    }

    private void removeRole() {
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
    private javax.swing.JPanel pnlRoles;
    private javax.swing.JPopupMenu popupRole;
    private org.sola.clients.beans.security.RoleListBean roleListBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRoles;
    private javax.swing.JToolBar toolBarRoles;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
