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
package org.sola.clients.swing.ui.security;

import org.sola.clients.beans.security.GroupBean;
import org.sola.clients.beans.security.GroupRoleHelperListBean;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;

/**
 * Displays {@link GroupBean} information and allows to edit it.
 */
public class GroupPanel extends javax.swing.JPanel {

    private GroupBean group;
    private GroupRoleHelperListBean groupRoleHelperList;

    /** Default panel constructor. */
    public GroupPanel() {
        this(null);
    }

    /** 
     * Constructs panel with predefined {@link GroupBean} instance. 
     * @param group {@link GroupBean} instance to bind on the form.
     */
    public GroupPanel(GroupBean group) {
        setupGroupBean(group);
        initComponents();
    }
    
    /** Setup {@link GroupBean} object, used to bind data on the form. */
    private void setupGroupBean(GroupBean group) {
        if (group != null) {
            this.group = group;
        } else {
            this.group = new GroupBean();
        }

        if (groupRoleHelperList == null) {
            groupRoleHelperList = new GroupRoleHelperListBean();
        }

        groupRoleHelperList.setGroupRoles(this.group.getGroupRoles());
        firePropertyChange("group", null, this.group);
    }

    /** Returns {@link GroupRoleHelperListBean}, which holds roles for the group. */
    public GroupRoleHelperListBean getGroupRoleHelperList() {
        return groupRoleHelperList;
    }

    /** Returns {@link GroupBean} object, bound on the form. */
    public GroupBean getGroup() {
        return group;
    }

    /** Sets {@link GroupBean} object to bind on the form. */
    public void setGroup(GroupBean group) {
        setupGroupBean(group);
    }

    /** Disables or enables panel components. */
    public void lockForm(boolean isLocked) {
        txtName.setEnabled(!isLocked);
        txtDescription.setEnabled(!isLocked);
        tableRoles.setEnabled(!isLocked);
    }
    
    /** Validates group */
    public boolean validateGroup(boolean showMessage){
        return group.validate(showMessage).size() < 1;
    }
    
    /** Saves group */
    public boolean saveGroup(boolean showMessage){
        if (validateGroup(showMessage)) {
            group.save();
            groupRoleHelperList.setChecks(group.getGroupRoles());
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRoles = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jLabel3 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/security/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("GroupPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtName.setName("txtName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${group.name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel2.setText(bundle.getString("GroupPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtDescription.setName("txtDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${group.description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableRoles.setName("tableRoles"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${groupRoleHelperList.groupRoleHelpers}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableRoles);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${inGroup}"));
        columnBinding.setColumnName("In Group");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${role.displayValue}"));
        columnBinding.setColumnName("Role.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${role.description}"));
        columnBinding.setColumnName("Role.description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tableRoles);
        tableRoles.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableRoles.getColumnModel().getColumn(0).setMaxWidth(40);
        tableRoles.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("GroupPanel.tableRoles.columnModel.title0_1")); // NOI18N
        tableRoles.getColumnModel().getColumn(1).setPreferredWidth(160);
        tableRoles.getColumnModel().getColumn(1).setMaxWidth(160);
        tableRoles.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("GroupPanel.tableRoles.columnModel.title1_1")); // NOI18N
        tableRoles.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("GroupPanel.tableRoles.columnModel.title2_1")); // NOI18N
        tableRoles.getColumnModel().getColumn(2).setCellRenderer(new TableCellTextAreaRenderer());

        jLabel3.setText(bundle.getString("GroupPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 213, Short.MAX_VALUE))
                            .addComponent(txtDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRoles;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
