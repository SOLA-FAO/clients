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
import org.sola.clients.beans.security.GroupBean;
import org.sola.clients.beans.security.GroupSummaryBean;
import org.sola.clients.beans.security.GroupSummaryListBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to manage groups.
 */
public class GroupsManagementPanel extends ContentPanel {

    /** Default constructor. */
    public GroupsManagementPanel() {
        initComponents();
        groupSummaryList.loadGroups(false);
        groupSummaryList.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(GroupSummaryListBean.SELECTED_GROUP_PROPERTY)) {
                    customizeGroupButtons((GroupSummaryBean) evt.getNewValue());
                }
            }
        });
        customizeGroupButtons(null);
    }

    /** 
     * Enables or disables group management buttons, depending on selection in 
     * the groups table and user rights. 
     */
    private void customizeGroupButtons(GroupSummaryBean groupSummaryBean) {
        btnRemoveGroup.setEnabled(groupSummaryBean != null);
        btnEditGroup.setEnabled(groupSummaryBean != null);
        menuEdit.setEnabled(btnEditGroup.isEnabled());
        menuRemove.setEnabled(btnRemoveGroup.isEnabled());
    }

    /** Shows group panel. */
    private void showGroup(final GroupBean group) {
        GroupPanelForm panel = new GroupPanelForm(group, true, group != null, false);
        panel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(GroupPanelForm.GROUP_SAVED_PROPERTY)) {
                    if (group == null) {
                        ((GroupPanelForm) evt.getSource()).setGroupBean(null);
                    }
                    groupSummaryList.loadGroups(false);
                }
            }
        });
        getMainContentPanel().addPanel(panel, MainContentPanel.CARD_ADMIN_GROUP, true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupGroups = new javax.swing.JPopupMenu();
        menuAdd = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenuItem();
        menuRemove = new javax.swing.JMenuItem();
        groupSummaryList = new org.sola.clients.beans.security.GroupSummaryListBean();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        pnlGroups = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddGroup = new javax.swing.JButton();
        btnEditGroup = new javax.swing.JButton();
        btnRemoveGroup = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        popupGroups.setName("popupGroups"); // NOI18N

        menuAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        menuAdd.setText(bundle.getString("GroupsManagementPanel.menuAdd.text")); // NOI18N
        menuAdd.setName("menuAdd"); // NOI18N
        menuAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddActionPerformed(evt);
            }
        });
        popupGroups.add(menuAdd);

        menuEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEdit.setText(bundle.getString("GroupsManagementPanel.menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N
        menuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditActionPerformed(evt);
            }
        });
        popupGroups.add(menuEdit);

        menuRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemove.setText(bundle.getString("GroupsManagementPanel.menuRemove.text")); // NOI18N
        menuRemove.setName("menuRemove"); // NOI18N
        menuRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveActionPerformed(evt);
            }
        });
        popupGroups.add(menuRemove);

        setHeaderPanel(pnlHeader);
        setMinimumSize(new java.awt.Dimension(100, 100));

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("GroupsManagementPanel.pnlHeader.titleText")); // NOI18N

        pnlGroups.setName("pnlGroups"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddGroup.setText(bundle.getString("GroupsManagementPanel.btnAddGroup.text")); // NOI18N
        btnAddGroup.setFocusable(false);
        btnAddGroup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddGroup.setName("btnAddGroup"); // NOI18N
        btnAddGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddGroupActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddGroup);

        btnEditGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditGroup.setText(bundle.getString("GroupsManagementPanel.btnEditGroup.text")); // NOI18N
        btnEditGroup.setFocusable(false);
        btnEditGroup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditGroup.setName("btnEditGroup"); // NOI18N
        btnEditGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditGroupActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditGroup);

        btnRemoveGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveGroup.setText(bundle.getString("GroupsManagementPanel.btnRemoveGroup.text")); // NOI18N
        btnRemoveGroup.setFocusable(false);
        btnRemoveGroup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveGroup.setName("btnRemoveGroup"); // NOI18N
        btnRemoveGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveGroupActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveGroup);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTableWithDefaultStyles1.setComponentPopupMenu(popupGroups);
        jTableWithDefaultStyles1.setName("jTableWithDefaultStyles1"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${groupSummaryList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, groupSummaryList, eLProperty, jTableWithDefaultStyles1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, groupSummaryList, org.jdesktop.beansbinding.ELProperty.create("${selectedGroup}"), jTableWithDefaultStyles1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTableWithDefaultStyles1);
        jTableWithDefaultStyles1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("GroupsManagementPanel.jTableWithDefaultStyles1.columnModel.title0_1")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("GroupsManagementPanel.jTableWithDefaultStyles1.columnModel.title1_1")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(1).setCellRenderer(new TableCellTextAreaRenderer());

        javax.swing.GroupLayout pnlGroupsLayout = new javax.swing.GroupLayout(pnlGroups);
        pnlGroups.setLayout(pnlGroupsLayout);
        pnlGroupsLayout.setHorizontalGroup(
            pnlGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
        );
        pnlGroupsLayout.setVerticalGroup(
            pnlGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGroupsLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlGroups, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlGroups, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGroupActionPerformed
        addGroup();
    }//GEN-LAST:event_btnAddGroupActionPerformed

    private void menuAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddActionPerformed
        addGroup();
    }//GEN-LAST:event_menuAddActionPerformed

    private void btnEditGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditGroupActionPerformed
        editGroup();
    }//GEN-LAST:event_btnEditGroupActionPerformed

    private void btnRemoveGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveGroupActionPerformed
        removeGroup();
    }//GEN-LAST:event_btnRemoveGroupActionPerformed

    private void menuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditActionPerformed
        editGroup();
    }//GEN-LAST:event_menuEditActionPerformed

    private void menuRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveActionPerformed
        removeGroup();
    }//GEN-LAST:event_menuRemoveActionPerformed

    private void addGroup() {
        showGroup(null);
    }

    private void editGroup() {
        if (groupSummaryList.getSelectedGroup() != null) {
            showGroup(GroupBean.getGroup(groupSummaryList.getSelectedGroup().getId()));
        }
    }

    private void removeGroup() {
        if (groupSummaryList.getSelectedGroup() != null
                && MessageUtility.displayMessage(ClientMessage.ADMIN_CONFIRM_DELETE_GROUP)
                == MessageUtility.BUTTON_ONE) {
            GroupBean.removeGroup(groupSummaryList.getSelectedGroup().getId());
            groupSummaryList.loadGroups(false);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddGroup;
    private javax.swing.JButton btnEditGroup;
    private javax.swing.JButton btnRemoveGroup;
    private org.sola.clients.beans.security.GroupSummaryListBean groupSummaryList;
    private javax.swing.JScrollPane jScrollPane1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles jTableWithDefaultStyles1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAdd;
    private javax.swing.JMenuItem menuEdit;
    private javax.swing.JMenuItem menuRemove;
    private javax.swing.JPanel pnlGroups;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPopupMenu popupGroups;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
