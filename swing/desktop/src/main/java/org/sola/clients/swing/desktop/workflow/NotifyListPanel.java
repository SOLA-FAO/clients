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
import org.sola.clients.beans.application.NotifyBean;
import org.sola.clients.beans.application.NotifyListBean;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartyListBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.desktop.party.PartySearchPanelForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author soladev
 */
public class NotifyListPanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private boolean readOnly = false;

    /**
     * Creates new form NotifyListPanel
     */
    public NotifyListPanel() {
        initComponents();
    }

    public NotifyListPanel(ApplicationBean applicationBean, ApplicationServiceBean applicationService,
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
                if (evt.getPropertyName().equals(NotifyListBean.SELECTED_NOTIFY)) {
                    customizeButtons((NotifyBean) evt.getNewValue());
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

    private void customizeButtons(NotifyBean item) {
        boolean enable = item != null && !readOnly;
        btnView.setEnabled(item != null);
        btnEdit.setEnabled(enable);
        btnRemove.setEnabled(enable);
    }

    private void openNotifyParty(final NotifyBean notifyParty, final PartyBean party,
            final boolean viewItem) {

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_NOTIFY_PARTY));
                NotifyPanel panel = null;
                if (notifyParty != null) {
                    boolean hasChanges = MainForm.checkBeanState(listBean);
                    // Create a copy of the Notify Party so that if the user decides to cancel thier changes
                    // the data on the original Notify Party is unchanged. 
                    NotifyBean np = notifyParty.copy();                           
                    if (!hasChanges) {
                        // The copy will change the state of the listBean, so reset the bean state
                        // if no changes had been made. 
                        saveListBeanState();
                    }
                    panel = new NotifyPanel(np, applicationBean, applicationService, viewItem);
                } else if (party != null) {
                    panel = new NotifyPanel(party, applicationBean, applicationService, viewItem);
                }

                panel.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(NotifyPanel.NOTIFY_SAVED)) {
                            listBean.addOrUpdateItem((NotifyBean) evt.getNewValue());
                            tblNotifyParties.clearSelection();
                        }
                    }
                });
                getMainContentPanel().addPanel(panel, MainContentPanel.CARD_NOTIFY_PANEL, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void removeNotifyParty(NotifyBean item) {
        if (item != null) {
            listBean.removeItem(item);
        }
    }

    /**
     * Uses the PartySearchPanelForm to allow the user to search for an existing
     * party to add as the Notify Party.
     */
    private void openSearchPartyForm() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartyListBean partyList = new PartyListBean();
                partyList.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(PartyListBean.SELECTED_PARTY_PROPERTY)
                                && evt.getNewValue() != null) {
                            openNotifyParty(null, (PartyBean) evt.getNewValue(), false);
                        }
                    }
                });
                PartySearchPanelForm partySearchForm = new PartySearchPanelForm(true, partyList);
                getMainContentPanel().addPanel(partySearchForm, MainContentPanel.CARD_SEARCH_PERSONS, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens the PartyForm so the user and add details for a new Notify Party
     */
    private void openPartyForm() {

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartyPanelForm partyForm = new PartyPanelForm(true, null, readOnly, true);
                partyForm.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(PartyPanelForm.PARTY_SAVED)) {
                            openNotifyParty(null, ((PartyPanelForm) evt.getSource()).getParty(), false);
                        }
                    }
                });

                getMainContentPanel().addPanel(partyForm, MainContentPanel.CARD_PERSON, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Saves any changes to the listBean
     *
     * @param showMessage
     * @return
     */
    private boolean saveNotifyPartyList(final boolean showMessage) {
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
                        MessageUtility.displayMessage(ClientMessage.NOTIFY_PARTIES_SUCCESSFULLY_SAVED);
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
        tblNotifyParties.clearSelection();
        MainForm.saveBeanState(listBean);
    }

    @Override
    protected boolean panelClosing() {
        tblNotifyParties.clearSelection();
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(listBean)) {
            return saveNotifyPartyList(true);
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

        listBean = new org.sola.clients.beans.application.NotifyListBean();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new org.sola.clients.swing.common.buttons.BtnSave();
        btnView = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAdd = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnEdit = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemove = new org.sola.clients.swing.common.buttons.BtnRemove();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnSearch = new org.sola.clients.swing.common.buttons.BtnSearch();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNotifyParties = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        setHeaderPanel(headerPanel1);
        setHelpTopic("notifications"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("NotifyListPanel.headerPanel1.titleText")); // NOI18N

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
        jToolBar1.add(jSeparator2);

        btnSearch.setText(bundle.getString("NotifyListPanel.btnSearch.text")); // NOI18N
        btnSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSearch);

        tblNotifyParties.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredNotifyList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, eLProperty, tblNotifyParties);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${checked}"));
        columnBinding.setColumnName("Checked");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${party.fullName}"));
        columnBinding.setColumnName("Party.full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relationshipType.displayValue}"));
        columnBinding.setColumnName("Relationship Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${party.address.description}"));
        columnBinding.setColumnName("Party.address.description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, org.jdesktop.beansbinding.ELProperty.create("${selectedNotify}"), tblNotifyParties, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblNotifyParties.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNotifyPartiesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNotifyParties);
        if (tblNotifyParties.getColumnModel().getColumnCount() > 0) {
            tblNotifyParties.getColumnModel().getColumn(0).setMinWidth(25);
            tblNotifyParties.getColumnModel().getColumn(0).setPreferredWidth(25);
            tblNotifyParties.getColumnModel().getColumn(0).setMaxWidth(25);
            tblNotifyParties.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("NotifyListPanel.tblNotifyParties.columnModel.title4_1")); // NOI18N
            tblNotifyParties.getColumnModel().getColumn(1).setPreferredWidth(30);
            tblNotifyParties.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("NotifyListPanel.tblNotifyParties.columnModel.title0")); // NOI18N
            tblNotifyParties.getColumnModel().getColumn(2).setPreferredWidth(30);
            tblNotifyParties.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("NotifyListPanel.jTableWithDefaultStyles1.columnModel.title2")); // NOI18N
            tblNotifyParties.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("NotifyListPanel.jTableWithDefaultStyles1.columnModel.title3")); // NOI18N
            tblNotifyParties.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("NotifyListPanel.jTableWithDefaultStyles1.columnModel.title1")); // NOI18N
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveNotifyPartyList(true);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        if (listBean.getSelectedNotify() != null) {
            openNotifyParty(listBean.getSelectedNotify(), null, true);
        }
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        openPartyForm();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (listBean.getSelectedNotify() != null) {
            openNotifyParty(listBean.getSelectedNotify(), null, readOnly);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        removeNotifyParty(listBean.getSelectedNotify());
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void tblNotifyPartiesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNotifyPartiesMouseClicked
        if (evt.getClickCount() == 2) {
            if (btnEdit.isEnabled()) {
                openNotifyParty(listBean.getSelectedNotify(), null, readOnly);
            } else if (btnView.isEnabled()) {
                openNotifyParty(listBean.getSelectedNotify(), null, true);
            }
        }
    }//GEN-LAST:event_tblNotifyPartiesMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        openSearchPartyForm();
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnAdd btnAdd;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEdit;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemove;
    private org.sola.clients.swing.common.buttons.BtnSave btnSave;
    private org.sola.clients.swing.common.buttons.BtnSearch btnSearch;
    private org.sola.clients.swing.common.buttons.BtnView btnView;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.beans.application.NotifyListBean listBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblNotifyParties;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
