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
import org.sola.clients.beans.administrative.BaUnitSearchResultBean;
import org.sola.clients.beans.administrative.BaUnitSummaryBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.application.NegotiateBean;
import org.sola.clients.beans.application.NegotiateListBean;
import org.sola.clients.beans.referencedata.NegotiateStatusBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.administrative.BaUnitSearchPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.AreaCellRenderer;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.clients.swing.ui.renderers.MoneyCellRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author soladev
 */
public class NegotiationListPanel extends ContentPanel {
    
    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private boolean readOnly = false;

    /**
     * Creates new form NegotiationListPanel
     */
    public NegotiationListPanel() {
        initComponents();
    }

    /**
     * Creates new form ValuationListPanel
     *
     * @param applicationBean
     * @param applicationService
     * @param readOnly
     */
    public NegotiationListPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            Boolean readOnly) {
        this.readOnly = readOnly;
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        initComponents();
        customizeForm();
        listBean.loadList(this.applicationService.getId());
        listBean.addPropertyChangeListener(new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(NegotiateListBean.SELECTED_NEGOTIATE)) {
                    customizeButtons((NegotiateBean) evt.getNewValue());
                }
            }
        });
        
        saveListBeanState();
    }
    
    private void customizeForm() {
        // Disable the edit buttons if the form is in read only mode
        btnSave1.setEnabled(!readOnly);
        btnAdd1.setEnabled(!readOnly);
        customizeButtons(null);
    }
    
    private void customizeButtons(NegotiateBean item) {
        boolean enable = item != null && !readOnly;
        btnView1.setEnabled(item != null);
        btnEdit1.setEnabled(enable);
        btnRemove1.setEnabled(enable);
    }

    /**
     * Opens the PropertySearch screen so the user can select a property to
     * associate with the new NegotiateBean.
     */
    private void addNegotiation() {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTYSEARCH));
                BaUnitSearchPanel propSearchPanel = new BaUnitSearchPanel(applicationBean);
                // Configure the panel for display
                propSearchPanel.getSearchPanel().showSelectButtons(true);
                propSearchPanel.getSearchPanel().showOpenButtons(true);
                propSearchPanel.getSearchPanel().setDefaultActionOpen(false);
                propSearchPanel.setCloseOnSelect(true);
                propSearchPanel.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(BaUnitSearchPanel.SELECTED_RESULT_PROPERTY)) {
                            BaUnitSearchResultBean bean = (BaUnitSearchResultBean) evt.getNewValue();
                            if (bean != null) {
                                BaUnitSummaryBean prop = new BaUnitSummaryBean(bean);
                                NegotiateBean neg = new NegotiateBean();
                                neg.setBaUnitId(bean.getId());
                                neg.setBaUnit(prop);
                                neg.setStatusCode(NegotiateStatusBean.CODE_PENDING);
                                neg.setServiceId(applicationService.getId());
                                openNegotiation(neg, false);
                            }
                        }
                    }
                });
                getMainContentPanel().addPanel(propSearchPanel, MainContentPanel.CARD_BAUNIT_SEARCH, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens the NegotiatePanel in view or edit mode depending on the viewItem
     * parameter
     *
     * @param negotiation The NegotiateBean to view or edit. Note that
     * addNegotiate should be called if a new NegotiateBean must be created.
     * @param viewItem If true, the NegotiatePanel will open as readOnly.
     */
    private void openNegotiation(final NegotiateBean negotiation, final boolean viewItem) {
        
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                NegotiateBean obj = null;
                if (negotiation != null) {
                    boolean hasChanges = MainForm.checkBeanState(listBean);
                    // Create a copy of the negotiation so that if the user decides to cancel thier changes
                    // the data on the original negotiation is unchanged. 
                    obj = negotiation.copy();
                    if (!hasChanges) {
                        // The copy will change the state of the listBean, so reset the bean state
                        // if no changes had been made. 
                        saveListBeanState();
                    }
                }
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_NEGOTIATION));
                NegotiationPanel panel = new NegotiationPanel(obj, applicationBean, applicationService, viewItem);
                panel.addPropertyChangeListener(new PropertyChangeListener() {
                    
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(NegotiationPanel.NEGOTIATION_SAVED)) {
                            listBean.addOrUpdateItem((NegotiateBean) evt.getNewValue());
                            listBean.addUpdateTotalBean();
                            tblNegotiations.clearSelection();
                        }
                    }
                });
                getMainContentPanel().addPanel(panel, MainContentPanel.CARD_NEGOTIATION_PANEL, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }
    
    private void removeNegotiation(NegotiateBean item) {
        if (item != null) {
            listBean.removeItem(item);
        }
    }

    /**
     * Saves any changes to the listBean
     *
     * @param showMessage
     * @return
     */
    private boolean saveNegotiateList(final boolean showMessage) {
        final boolean[] result = {listBean.validate(true).size() < 1};
        if (result[0]) {
            // Save the items
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
                        MessageUtility.displayMessage(ClientMessage.NEGOTIATIONS_SUCCESSFULLY_SAVED);
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
        tblNegotiations.clearSelection();
        MainForm.saveBeanState(listBean);
    }
    
    @Override
    protected boolean panelClosing() {
        tblNegotiations.clearSelection();
        if (btnSave1.isEnabled() && MainForm.checkSaveBeforeClose(listBean)) {
            return saveNegotiateList(true);
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

        listBean = new org.sola.clients.beans.application.NegotiateListBean();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave1 = new org.sola.clients.swing.common.buttons.BtnSave();
        btnView1 = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAdd1 = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnEdit1 = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemove1 = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNegotiations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        setHeaderPanel(headerPanel1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("NegotiationListPanel.headerPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave1);

        btnView1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnView1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnView1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnView1);
        jToolBar1.add(jSeparator1);

        btnAdd1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAdd1);

        btnEdit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEdit1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit1);

        btnRemove1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemove1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemove1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemove1);

        tblNegotiations.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredNegotiationList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, eLProperty, tblNegotiations);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnit.displayName}"));
        columnBinding.setColumnName("Ba Unit.display Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${type.displayValue}"));
        columnBinding.setColumnName("Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notificationDate}"));
        columnBinding.setColumnName("Notification Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnit.area}"));
        columnBinding.setColumnName("Ba Unit.area");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${initialAmount}"));
        columnBinding.setColumnName("Initial Amount");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${finalAmount}"));
        columnBinding.setColumnName("Final Amount");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, org.jdesktop.beansbinding.ELProperty.create("${selectedNegotiation}"), tblNegotiations, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblNegotiations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNegotiationsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNegotiations);
        if (tblNegotiations.getColumnModel().getColumnCount() > 0) {
            tblNegotiations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("NegotiationListPanel.tblNegotiations.columnModel.title0_1")); // NOI18N
            tblNegotiations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("NegotiationListPanel.tblNegotiations.columnModel.title1_1")); // NOI18N
            tblNegotiations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("NegotiationListPanel.tblNegotiations.columnModel.title3_1")); // NOI18N
            tblNegotiations.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer(false));
            tblNegotiations.getColumnModel().getColumn(3).setPreferredWidth(30);
            tblNegotiations.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("NegotiationListPanel.tblNegotiations.columnModel.title6")); // NOI18N
            tblNegotiations.getColumnModel().getColumn(3).setCellRenderer(new AreaCellRenderer());
            tblNegotiations.getColumnModel().getColumn(4).setPreferredWidth(30);
            tblNegotiations.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("NegotiationListPanel.tblNegotiations.columnModel.title2_1")); // NOI18N
            tblNegotiations.getColumnModel().getColumn(4).setCellRenderer(new MoneyCellRenderer());
            tblNegotiations.getColumnModel().getColumn(5).setPreferredWidth(30);
            tblNegotiations.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("NegotiationListPanel.tblNegotiations.columnModel.title4")); // NOI18N
            tblNegotiations.getColumnModel().getColumn(5).setCellRenderer(new MoneyCellRenderer());
            tblNegotiations.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("NegotiationListPanel.tblNegotiations.columnModel.title5")); // NOI18N
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        saveNegotiateList(true);
    }//GEN-LAST:event_btnSave1ActionPerformed

    private void btnView1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnView1ActionPerformed
        if (listBean.getSelectedNegotiation() != null) {
            openNegotiation(listBean.getSelectedNegotiation(), true);
        }
    }//GEN-LAST:event_btnView1ActionPerformed

    private void btnAdd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd1ActionPerformed
        addNegotiation();
    }//GEN-LAST:event_btnAdd1ActionPerformed

    private void btnEdit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEdit1ActionPerformed
        if (listBean.getSelectedNegotiation() != null) {
            openNegotiation(listBean.getSelectedNegotiation(), readOnly);
        }
    }//GEN-LAST:event_btnEdit1ActionPerformed

    private void btnRemove1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemove1ActionPerformed
        if (listBean.getSelectedNegotiation() != null) {
            removeNegotiation(listBean.getSelectedNegotiation());
        }
    }//GEN-LAST:event_btnRemove1ActionPerformed

    private void tblNegotiationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNegotiationsMouseClicked
        if (evt.getClickCount() == 2) {
            if (btnEdit1.isEnabled()) {
                openNegotiation(listBean.getSelectedNegotiation(), readOnly);
            } else if (btnView1.isEnabled()) {
                openNegotiation(listBean.getSelectedNegotiation(), true);
            }
        }
    }//GEN-LAST:event_tblNegotiationsMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnAdd btnAdd1;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEdit1;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemove1;
    private org.sola.clients.swing.common.buttons.BtnSave btnSave1;
    private org.sola.clients.swing.common.buttons.BtnView btnView1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.beans.application.NegotiateListBean listBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblNegotiations;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
