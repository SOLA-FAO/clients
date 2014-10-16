/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.desktop.workflow;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.application.ServiceChecklistItemBean;
import org.sola.clients.beans.application.ServiceChecklistItemListBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.StringUtility;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author soladev
 */
public class ChecklistPanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private boolean readOnly = false;

    /**
     * Creates new form ChecklistPanel
     */
    public ChecklistPanel() {
        initComponents();
    }

    public ChecklistPanel(ApplicationBean applicationBean, ApplicationServiceBean applicationService,
            Boolean readOnly) {
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        this.readOnly = readOnly;
        initComponents();
        customizeForm();
        serviceChecklistItemListBean.loadList(this.applicationService.getId());
        serviceChecklistItemListBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ServiceChecklistItemListBean.SELECTED_SERVICE_CHECKLIST_ITEM)) {
                    customizeItemButtons((ServiceChecklistItemBean) evt.getNewValue());
                }
            }
        });
    }

    private void customizeForm() {
        // Disable the edit buttons if the form is in read only mode
        btnSave.setEnabled(!readOnly);
        cbxChecklistGroup.setEnabled(!readOnly);
        btnSelect.setEnabled(!readOnly);
        tblChecklist.setEnabled(!readOnly);
        btnAddItem.setEnabled(!readOnly);
        btnEditItem.setEnabled(false);
        btnRemoveItem.setEnabled(false);
        if (applicationService.getActionNotes() != null) {
            checklistGroupListBean.setSelectedChecklistGroup(CacheManager.getBeanByCode(
                    CacheManager.getChecklistGroups(), applicationService.getActionNotes()));
        }
    }

    private void customizeItemButtons(ServiceChecklistItemBean item) {
        boolean enable = false;
        if (item != null && StringUtility.isEmpty(item.getItemCode())) {
            enable = true && !readOnly;
        }
        btnEditItem.setEnabled(enable);
        btnRemoveItem.setEnabled(enable);
    }

    private void acceptEdits() {
        // Make sure any user edits in the table are accepted
        if (this.tblChecklist.getCellEditor() != null) {
            this.tblChecklist.getCellEditor().stopCellEditing();
        }
    }

    private void saveChecklist(final boolean showMessage) {
        acceptEdits();

        if (serviceChecklistItemListBean.validate(true).size() < 1) {
            // Save the checklist items
            SolaTask<Void, Void> t = new SolaTask<Void, Void>() {
                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SAVING));
                    String checklistGroup = null;
                    if (checklistGroupListBean.getSelectedChecklistGroup() != null) {
                        checklistGroup = checklistGroupListBean.getSelectedChecklistGroup().getCode();
                    }
                    serviceChecklistItemListBean.saveList(checklistGroup);
                    return null;
                }

                @Override
                public void taskDone() {
                    if (showMessage) {
                        // Only display the Saved message if the user has choosen to explicitly save
                        MessageUtility.displayMessage(ClientMessage.APPLICATION_CHECKLIST_SUCCESSFULLY_SAVED);
                    }
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    private void addChecklistItem() {
        acceptEdits();
        ChecklistItemDialog form = new ChecklistItemDialog(null,
                applicationService.getId(), WindowUtility.getTopFrame(), true);
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ChecklistItemDialog.ITEM_SAVED)) {
                    serviceChecklistItemListBean.addItem((ServiceChecklistItemBean) evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }

    private void editChecklistItem() {
        acceptEdits();
        if (serviceChecklistItemListBean.getSelectedServiceChecklistItem() != null) {
            ServiceChecklistItemBean copy = (ServiceChecklistItemBean) serviceChecklistItemListBean.getSelectedServiceChecklistItem().copy();
            ChecklistItemDialog form = new ChecklistItemDialog(copy,
                    applicationService.getId(), WindowUtility.getTopFrame(), true);

            form.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ChecklistItemDialog.ITEM_SAVED)) {
                        ServiceChecklistItemBean item = (ServiceChecklistItemBean) evt.getNewValue();
                        serviceChecklistItemListBean.getSelectedServiceChecklistItem().setItemDisplayValue(item.getItemDisplayValue());
                        serviceChecklistItemListBean.getSelectedServiceChecklistItem().setItemDescription(item.getItemDescription());
                    }
                }
            });
            form.setVisible(true);
        }
    }

    private void removeChecklistItem() {
        acceptEdits();
        if (serviceChecklistItemListBean.getSelectedServiceChecklistItem() != null) {
            serviceChecklistItemListBean.removeItem(serviceChecklistItemListBean.getSelectedServiceChecklistItem());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        checklistGroupListBean = new org.sola.clients.beans.referencedata.ChecklistGroupListBean();
        serviceChecklistItemListBean = new org.sola.clients.beans.application.ServiceChecklistItemListBean();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new org.sola.clients.swing.common.buttons.BtnSave();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        cbxChecklistGroup = new javax.swing.JComboBox();
        btnSelect = new org.sola.clients.swing.common.buttons.BtnSelect();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnAddItem = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnEditItem = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemoveItem = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblChecklist = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        setHeaderPanel(headerPanel1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("ChecklistPanel.headerPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
        jToolBar1.add(jSeparator1);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${checklistGroupList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checklistGroupListBean, eLProperty, cbxChecklistGroup);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checklistGroupListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedChecklistGroup}"), cbxChecklistGroup, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(cbxChecklistGroup);

        btnSelect.setText(bundle.getString("ChecklistPanel.btnSelect.text")); // NOI18N
        btnSelect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSelect);
        jToolBar1.add(jSeparator2);

        btnAddItem.setText(bundle.getString("ChecklistPanel.btnAddItem.text")); // NOI18N
        btnAddItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddItem);

        btnEditItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditItemActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditItem);

        btnRemoveItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveItemActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveItem);

        tblChecklist.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceChecklistItemList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, serviceChecklistItemListBean, eLProperty, tblChecklist);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${itemDisplayValue}"));
        columnBinding.setColumnName("Item Display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${itemDescription}"));
        columnBinding.setColumnName("Item Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${complies}"));
        columnBinding.setColumnName("Complies");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${comment}"));
        columnBinding.setColumnName("Comment");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, serviceChecklistItemListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedServiceChecklistItem}"), tblChecklist, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblChecklist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblChecklistMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblChecklist);
        if (tblChecklist.getColumnModel().getColumnCount() > 0) {
            tblChecklist.getColumnModel().getColumn(0).setPreferredWidth(150);
            tblChecklist.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ChecklistPanel.tblChecklist.columnModel.title1_1")); // NOI18N
            tblChecklist.getColumnModel().getColumn(0).setCellRenderer(new TableCellTextAreaRenderer());
            tblChecklist.getColumnModel().getColumn(1).setPreferredWidth(300);
            tblChecklist.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ChecklistPanel.tblChecklist.columnModel.title0_1")); // NOI18N
            tblChecklist.getColumnModel().getColumn(1).setCellRenderer(new TableCellTextAreaRenderer());
            tblChecklist.getColumnModel().getColumn(2).setMinWidth(70);
            tblChecklist.getColumnModel().getColumn(2).setPreferredWidth(70);
            tblChecklist.getColumnModel().getColumn(2).setMaxWidth(70);
            tblChecklist.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ChecklistPanel.tblChecklist.columnModel.title2_1")); // NOI18N
            tblChecklist.getColumnModel().getColumn(3).setPreferredWidth(300);
            tblChecklist.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ChecklistPanel.tblChecklist.columnModel.title3_1")); // NOI18N
            tblChecklist.getColumnModel().getColumn(3).setCellRenderer(new TableCellTextAreaRenderer());
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveChecklist(true);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        serviceChecklistItemListBean.loadList(checklistGroupListBean.getSelectedChecklistGroup());
        saveChecklist(false);
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        addChecklistItem();
    }//GEN-LAST:event_btnAddItemActionPerformed

    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        removeChecklistItem();
    }//GEN-LAST:event_btnRemoveItemActionPerformed

    private void btnEditItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditItemActionPerformed
        editChecklistItem();
    }//GEN-LAST:event_btnEditItemActionPerformed

    private void tblChecklistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblChecklistMouseClicked
        if (evt.getClickCount() == 2 && btnEditItem.isEnabled()) {
            editChecklistItem();
        }
    }//GEN-LAST:event_tblChecklistMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnAdd btnAddItem;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEditItem;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemoveItem;
    private org.sola.clients.swing.common.buttons.BtnSave btnSave;
    private org.sola.clients.swing.common.buttons.BtnSelect btnSelect;
    private javax.swing.JComboBox cbxChecklistGroup;
    private org.sola.clients.beans.referencedata.ChecklistGroupListBean checklistGroupListBean;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.beans.application.ServiceChecklistItemListBean serviceChecklistItemListBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblChecklist;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
