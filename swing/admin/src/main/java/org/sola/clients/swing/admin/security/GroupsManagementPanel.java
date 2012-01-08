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
import org.sola.clients.beans.security.GroupBean;
import org.sola.clients.beans.security.GroupSummaryBean;
import org.sola.clients.beans.security.GroupSummaryListBean;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.clients.swing.ui.security.GroupPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to manage groups.
 */
public class GroupsManagementPanel extends javax.swing.JPanel {

    /** Listens for events of {@link GroupPanel} */
    private class GroupPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(GroupPanel.SAVED_GROUP_PROPERTY) || 
                    evt.getPropertyName().equals(GroupPanel.CREATED_GROUP_PROPERTY)) {
                
                if(evt.getPropertyName().equals(GroupPanel.CREATED_GROUP_PROPERTY)){
                    MessageUtility.displayMessage(ClientMessage.ADMIN_GROUP_CREATED);
                }else{
                    MessageUtility.displayMessage(ClientMessage.ADMIN_GROUP_SAVED);
                }
                
                pnlGroup.setGroup(null);
                showGroups();
                groupSummaryList.loadGroups(false);
            }
            if (evt.getPropertyName().equals(GroupPanel.CANCEL_ACTION_PROPERTY)) {
                showGroups();
            }
        }
    }

    private ResourceBundle resourceBundle;
    
    /** Default constructor. */
    public GroupsManagementPanel() {
        initComponents();

        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); 
        showGroups();
        pnlGroup.addPropertyChangeListener(new GroupPanelListener());

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
        btnRemoveGroup.getAction().setEnabled(groupSummaryBean != null);
        btnEditGroup.getAction().setEnabled(groupSummaryBean != null);
    }
    
    /** Shows list of groups */
    private void showGroups(){
        pnlGroup.setVisible(false);
        pnlGroups.setVisible(true);
        pnlHeader.setTitleText(resourceBundle.getString("GroupsManagementPanel.pnlHeader.titleText"));
    }

    /** Shows group panel. */
    private void showGroup(GroupSummaryBean group){
        pnlGroups.setVisible(false);
        pnlGroup.setVisible(true);
        if(group!=null){
            pnlHeader.setTitleText(String.format(resourceBundle
                    .getString("GroupsManagementPanel.pnlHeader.titleText.EditGroup"), group.getName()));
        }else{
            pnlHeader.setTitleText(resourceBundle.getString("GroupsManagementPanel.pnlHeader.titleText.NewGroup"));
        }
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
        pnlLayout = new javax.swing.JPanel();
        pnlGroups = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddGroup = new javax.swing.JButton();
        btnEditGroup = new javax.swing.JButton();
        btnRemoveGroup = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlGroup = new org.sola.clients.swing.ui.security.GroupPanel();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();

        popupGroups.setName("popupGroups"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(GroupsManagementPanel.class, this);
        menuAdd.setAction(actionMap.get("addGroup")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/security/Bundle"); // NOI18N
        menuAdd.setText(bundle.getString("GroupsManagementPanel.menuAdd.text")); // NOI18N
        menuAdd.setName("menuAdd"); // NOI18N
        popupGroups.add(menuAdd);

        menuEdit.setAction(actionMap.get("editGroup")); // NOI18N
        menuEdit.setText(bundle.getString("GroupsManagementPanel.menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N
        popupGroups.add(menuEdit);

        menuRemove.setAction(actionMap.get("removeGroup")); // NOI18N
        menuRemove.setText(bundle.getString("GroupsManagementPanel.menuRemove.text")); // NOI18N
        menuRemove.setName("menuRemove"); // NOI18N
        popupGroups.add(menuRemove);

        setMinimumSize(new java.awt.Dimension(547, 329));

        pnlLayout.setName("pnlLayout"); // NOI18N
        pnlLayout.setLayout(new java.awt.CardLayout());

        pnlGroups.setName("pnlGroups"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddGroup.setAction(actionMap.get("addGroup")); // NOI18N
        btnAddGroup.setText(bundle.getString("GroupsManagementPanel.btnAddGroup.text")); // NOI18N
        btnAddGroup.setFocusable(false);
        btnAddGroup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddGroup.setName("btnAddGroup"); // NOI18N
        btnAddGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAddGroup);

        btnEditGroup.setAction(actionMap.get("editGroup")); // NOI18N
        btnEditGroup.setText(bundle.getString("GroupsManagementPanel.btnEditGroup.text")); // NOI18N
        btnEditGroup.setFocusable(false);
        btnEditGroup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditGroup.setName("btnEditGroup"); // NOI18N
        btnEditGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnEditGroup);

        btnRemoveGroup.setAction(actionMap.get("removeGroup")); // NOI18N
        btnRemoveGroup.setText(bundle.getString("GroupsManagementPanel.btnRemoveGroup.text")); // NOI18N
        btnRemoveGroup.setFocusable(false);
        btnRemoveGroup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveGroup.setName("btnRemoveGroup"); // NOI18N
        btnRemoveGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
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
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
        );
        pnlGroupsLayout.setVerticalGroup(
            pnlGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGroupsLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
        );

        pnlLayout.add(pnlGroups, "card3");

        pnlGroup.setName("pnlGroup"); // NOI18N
        pnlLayout.add(pnlGroup, "card2");

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("GroupsManagementPanel.pnlHeader.titleText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlLayout, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLayout, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /** Customizes {@link GroupPanel} to create new group. */
    @Action
    public void addGroup() {
        pnlGroup.setGroup(null);
        pnlGroup.setOkButtonText(MessageUtility.getLocalizedMessage(
                        ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        showGroup(null);
    }

    /** Customizes {@link GroupPanel} to edit selected group from the groups table. */
    @Action
    public void editGroup() {
        if (groupSummaryList.getSelectedGroup() != null) {
            pnlGroup.setGroup(GroupBean.getGroup(groupSummaryList.getSelectedGroup().getId()));
            pnlGroup.setOkButtonText(MessageUtility.getLocalizedMessage(
                        ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
            showGroup(groupSummaryList.getSelectedGroup());
        }
    }

    /** Removes selected group from the list. */
    @Action
    public void removeGroup() {
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
    private org.sola.clients.swing.ui.security.GroupPanel pnlGroup;
    private javax.swing.JPanel pnlGroups;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPanel pnlLayout;
    private javax.swing.JPopupMenu popupGroups;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
