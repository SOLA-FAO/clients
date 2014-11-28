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
import org.sola.clients.beans.application.ObjectionBean;
import org.sola.clients.beans.application.ObjectionListBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author soladev
 */
public class ObjectionListPanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private boolean readOnly = false;

    /**
     * Creates new form ObjectionListPanel
     */
    public ObjectionListPanel() {
        initComponents();
    }

    public ObjectionListPanel(ApplicationBean applicationBean, ApplicationServiceBean applicationService,
            Boolean readOnly) {
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        this.readOnly = readOnly;
        initComponents();
        customizeForm();
        listBean.loadList(this.applicationService.getId());
        listBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ObjectionListBean.SELECTED_OBJECTION)) {
                    customizeButtons((ObjectionBean) evt.getNewValue());
                }
            }
        });

        saveListBeanState();
    }

    private void customizeForm() {
        // Disable the edit buttons if the form is in read only mode
        btnSave.setEnabled(!readOnly);
        btnAdd.setEnabled(!readOnly);
        customizeButtons(null);
    }

    private void customizeButtons(ObjectionBean item) {
        boolean enable = item != null && !readOnly;
        btnView.setEnabled(item != null);
        btnEdit.setEnabled(enable);
        btnRemove.setEnabled(enable);
    }

    private void openObjection(final ObjectionBean objection,
            final boolean viewItem) {

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                ObjectionBean obj = null;
                if (objection != null) {
                    boolean hasChanges = MainForm.checkBeanState(listBean);
                    // Create a copy of the objection so that if the user decides to cancel thier changes
                    // the data on the original objection is unchanged. 
                    obj = objection.copy();
                    if (!hasChanges) {
                        // The copy will change the state of the listBean, so reset the bean state
                        // if no changes had been made. 
                        saveListBeanState();
                    }
                }
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_OBJECTION));
                ObjectionPanel panel = new ObjectionPanel(obj, applicationBean, applicationService, viewItem);
                panel.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(ObjectionPanel.OBJECTION_SAVED)) {
                            listBean.addOrUpdateItem((ObjectionBean) evt.getNewValue());
                            tblObjections.clearSelection();
                        }
                    }
                });
                getMainContentPanel().addPanel(panel, MainContentPanel.CARD_OBJECTION_PANEL, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void removeObjection(ObjectionBean item) {
        if (item != null) {
            listBean.removeItem(item);
        }
    }

    private boolean saveObjectionList(final boolean showMessage) {
        final boolean[] result = {listBean.validate(true).size() < 1};
        if (result[0]) {
            // Save the checklist items
            SolaTask<Void, Void> t = new SolaTask<Void, Void>() {
                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SAVING));
                    listBean.saveList();
                    return null;
                }

                @Override
                public void taskDone() {
                    saveListBeanState();
                    if (showMessage) {
                        // Only display the Saved message if the user has choosen to explicitly save
                        MessageUtility.displayMessage(ClientMessage.OBJECTION_SUCCESSFULLY_SAVED);
                    }
                }

                @Override
                protected void taskFailed(Throwable e) {
                    // If the fail saves due to a database or connection exception, 
                    // capture the result for the save. 
                    result[0] = false;
                    super.taskFailed(e);
                }
            };
            TaskManager.getInstance().runTask(t);
        }
        return result[0];
    }

    private void saveListBeanState() {
        tblObjections.clearSelection();
        MainForm.saveBeanState(listBean);
    }

    @Override
    protected boolean panelClosing() {
        tblObjections.clearSelection();
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(listBean)) {
            return saveObjectionList(true);
        }
        return true;
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

        listBean = new org.sola.clients.beans.application.ObjectionListBean();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new org.sola.clients.swing.common.buttons.BtnSave();
        btnView = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAdd = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnEdit = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemove = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblObjections = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        setHeaderPanel(headerPanel1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("ObjectionListPanel.headerPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnView);
        jToolBar1.add(jSeparator1);

        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAdd);

        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemove);

        tblObjections.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredObjectionList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, eLProperty, tblObjections);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nr}"));
        columnBinding.setColumnName("Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lodgedDate}"));
        columnBinding.setColumnName("Lodged Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${authority.displayValue}"));
        columnBinding.setColumnName("Authority.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${resolution}"));
        columnBinding.setColumnName("Resolution");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${resolutionDate}"));
        columnBinding.setColumnName("Resolution Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, org.jdesktop.beansbinding.ELProperty.create("${selectedObjection}"), tblObjections, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblObjections.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblObjectionsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblObjections);
        if (tblObjections.getColumnModel().getColumnCount() > 0) {
            tblObjections.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ObjectionListPanel.tblObjections.columnModel.title0")); // NOI18N
            tblObjections.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ObjectionListPanel.tblObjections.columnModel.title1")); // NOI18N
            tblObjections.getColumnModel().getColumn(1).setCellRenderer(new DateTimeRenderer());
            tblObjections.getColumnModel().getColumn(2).setPreferredWidth(250);
            tblObjections.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ObjectionListPanel.tblObjections.columnModel.title2")); // NOI18N
            tblObjections.getColumnModel().getColumn(2).setCellRenderer(new TableCellTextAreaRenderer());
            tblObjections.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ObjectionListPanel.tblObjections.columnModel.title3")); // NOI18N
            tblObjections.getColumnModel().getColumn(4).setPreferredWidth(250);
            tblObjections.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ObjectionListPanel.tblObjections.columnModel.title4")); // NOI18N
            tblObjections.getColumnModel().getColumn(4).setCellRenderer(new TableCellTextAreaRenderer());
            tblObjections.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("ObjectionListPanel.tblObjections.columnModel.title5")); // NOI18N
            tblObjections.getColumnModel().getColumn(5).setCellRenderer(new DateTimeRenderer());
            tblObjections.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("ObjectionListPanel.tblObjections.columnModel.title6")); // NOI18N
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveObjectionList(true);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        if (listBean.getSelectedObjection() != null) {
            openObjection(listBean.getSelectedObjection(), true);
        }
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        openObjection(null, readOnly);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (listBean.getSelectedObjection() != null) {
            openObjection(listBean.getSelectedObjection(), readOnly);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        removeObjection(listBean.getSelectedObjection());
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void tblObjectionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblObjectionsMouseClicked
        if (evt.getClickCount() == 2) {
            if (btnEdit.isEnabled()) {
                openObjection(listBean.getSelectedObjection(), readOnly);
            } else if (btnView.isEnabled()) {
                openObjection(listBean.getSelectedObjection(), true);
            }
        }
    }//GEN-LAST:event_tblObjectionsMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnAdd btnAdd;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEdit;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemove;
    private org.sola.clients.swing.common.buttons.BtnSave btnSave;
    private org.sola.clients.swing.common.buttons.BtnView btnView;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.beans.application.ObjectionListBean listBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblObjections;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
