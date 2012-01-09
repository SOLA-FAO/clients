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
package org.sola.clients.swing.admin.system;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.jdesktop.application.Action;
import org.sola.clients.beans.referencedata.BrTechnicalTypeListBean;
import org.sola.clients.beans.referencedata.BrValidationTargetTypeListBean;
import org.sola.clients.beans.system.BrBean;
import org.sola.clients.beans.system.BrSearchResultBean;
import org.sola.clients.beans.system.BrSearchResultListBean;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.clients.swing.ui.system.BrPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to search and manage business rules.
 */
public class BrManagementPanel extends javax.swing.JPanel {

    private class BrPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(BrPanel.CREATED_PROPERTY)) {
                MessageUtility.displayMessage(ClientMessage.ADMIN_BR_CREATED);
            } else if (evt.getPropertyName().equals(BrPanel.SAVED_PROPERTY)) {
                MessageUtility.displayMessage(ClientMessage.ADMIN_BR_SAVED);
                searchBr();
                showBrSearch();
            }
            if (evt.getPropertyName().equals(BrPanel.CANCEL_ACTION_PROPERTY)) {
                showBrSearch();
                searchBr();
            }
        }
    }
    
    private ResourceBundle resourceBundle;
    private String headerTitle;
    
    /** Default panel constructor. */
    public BrManagementPanel() {
        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/system/Bundle");
        initComponents();
        
        headerTitle = pnlHeader.getTitleText();
        BrPanelListener listener = new BrPanelListener();
        pnlBr.addPropertyChangeListener(listener);
        brSearchResults.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BrSearchResultListBean.SELECTED_BR_SEARCH_RESULT_PROPERTY)) {
                    customizeBrButtons(brSearchResults.getSelectedBrSearchResult());
                }
            }
        });
        customizeBrButtons(null);
        showBrSearch();
    }

    private BrTechnicalTypeListBean createBrTechnicalTypes(){
        if(brTechnicalTypes == null){
            brTechnicalTypes = new BrTechnicalTypeListBean(true);
        }
        return brTechnicalTypes;
    }

    private BrValidationTargetTypeListBean createBrValidationTargetTypes(){
        if(brValidationTargetTypes == null){
            brValidationTargetTypes = new BrValidationTargetTypeListBean(true);
        }
        return brValidationTargetTypes;
    }
    
    /** Enables/disables BR managements buttons. */
    private void customizeBrButtons(BrSearchResultBean brSearchResult) {
        btnEdit.getAction().setEnabled(brSearchResult != null);
        btnRemove.getAction().setEnabled(brSearchResult != null);
    }

    /** Shows {@link BrPanel} */
    private void showBrPanel(BrBean br) {
        pnlSearch.setVisible(false);
        pnlBr.setVisible(true);
        pnlBr.setBr(br);

        if (br != null) {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    br.getDisplayName()));
        } else {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    resourceBundle.getString("BrManagementPanel.NewBr")));
        }
    }

    /** Shows BR search panel. */
    private void showBrSearch(){
        pnlBr.setVisible(false);
        pnlSearch.setVisible(true);
        pnlHeader.setTitleText(headerTitle);
    }
    
    /** Searches business rules with given criteria.*/
    private void searchBr() {
        brSearchResults.search(brSearchParams);
        if (brSearchResults.getBrSearchResults().size() < 1) {
            MessageUtility.displayMessage(ClientMessage.ADMIN_BR_NO_FOUND);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        brSearchResults = new org.sola.clients.beans.system.BrSearchResultListBean();
        brSearchParams = new org.sola.clients.beans.system.BrSearchParamsBean();
        brTechnicalTypes = createBrTechnicalTypes();
        brValidationTargetTypes = createBrValidationTargetTypes();
        popupBrs = new javax.swing.JPopupMenu();
        menuAdd = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenuItem();
        menuRemove = new javax.swing.JMenuItem();
        pnlMain = new javax.swing.JPanel();
        pnlBr = new org.sola.clients.swing.ui.system.BrPanel();
        pnlSearch = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableSearchResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel7 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtDisplayName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbxTechnicalTypes = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        cbxValidationTargets = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnSearch = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();

        popupBrs.setName("popupBrs"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.sola.clients.swing.admin.AdminApplication.class).getContext().getActionMap(BrManagementPanel.class, this);
        menuAdd.setAction(actionMap.get("addBr")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/system/Bundle"); // NOI18N
        menuAdd.setText(bundle.getString("BrManagementPanel.menuAdd.text")); // NOI18N
        menuAdd.setName("menuAdd"); // NOI18N
        popupBrs.add(menuAdd);

        menuEdit.setAction(actionMap.get("editBr")); // NOI18N
        menuEdit.setText(bundle.getString("BrManagementPanel.menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N
        popupBrs.add(menuEdit);

        menuRemove.setAction(actionMap.get("removeBr")); // NOI18N
        menuRemove.setText(bundle.getString("BrManagementPanel.menuRemove.text")); // NOI18N
        menuRemove.setName("menuRemove"); // NOI18N
        popupBrs.add(menuRemove);

        pnlMain.setName("pnlMain"); // NOI18N
        pnlMain.setLayout(new java.awt.CardLayout());

        pnlBr.setName("pnlBr"); // NOI18N
        pnlMain.add(pnlBr, "card3");

        pnlSearch.setName("pnlSearch"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableSearchResults.setComponentPopupMenu(popupBrs);
        tableSearchResults.setName("tableSearchResults"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${brSearchResults}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brSearchResults, eLProperty, tableSearchResults);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${displayName}"));
        columnBinding.setColumnName("Display Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${brTechnicalType.displayValue}"));
        columnBinding.setColumnName("Br Technical Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${feedback}"));
        columnBinding.setColumnName("Feedback");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brSearchResults, org.jdesktop.beansbinding.ELProperty.create("${selectedBrSearchResult}"), tableSearchResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableSearchResults);
        tableSearchResults.getColumnModel().getColumn(0).setPreferredWidth(200);
        tableSearchResults.getColumnModel().getColumn(0).setMaxWidth(250);
        tableSearchResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("BrManagementPanel.tableSearchResults.columnModel.title0")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(1).setPreferredWidth(110);
        tableSearchResults.getColumnModel().getColumn(1).setMaxWidth(110);
        tableSearchResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("BrManagementPanel.tableSearchResults.columnModel.title1")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("BrManagementPanel.tableSearchResults.columnModel.title2")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(2).setCellRenderer(new TableCellTextAreaRenderer());

        jPanel7.setName("jPanel7"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridLayout(1, 3, 15, 0));

        jPanel2.setName("jPanel2"); // NOI18N

        txtDisplayName.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtDisplayName.setName("txtDisplayName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brSearchParams, org.jdesktop.beansbinding.ELProperty.create("${displayName}"), txtDisplayName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText(bundle.getString("BrManagementPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(50, Short.MAX_VALUE))
            .addComponent(txtDisplayName, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDisplayName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel2);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText(bundle.getString("BrManagementPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        cbxTechnicalTypes.setFont(new java.awt.Font("Tahoma", 0, 12));
        cbxTechnicalTypes.setName("cbxTechnicalTypes"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${brTechnicalTypes}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brTechnicalTypes, eLProperty, cbxTechnicalTypes);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brSearchParams, org.jdesktop.beansbinding.ELProperty.create("${brTechnicalType}"), cbxTechnicalTypes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(40, Short.MAX_VALUE))
            .addComponent(cbxTechnicalTypes, 0, 124, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxTechnicalTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel3);

        jPanel4.setName("jPanel4"); // NOI18N

        cbxValidationTargets.setFont(new java.awt.Font("Tahoma", 0, 12));
        cbxValidationTargets.setName("cbxValidationTargets"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${brValidationTargetTypes}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brValidationTargetTypes, eLProperty, cbxValidationTargets);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brSearchParams, org.jdesktop.beansbinding.ELProperty.create("${brValidationTargetType}"), cbxValidationTargets, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText(bundle.getString("BrManagementPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(30, Short.MAX_VALUE))
            .addComponent(cbxValidationTargets, 0, 124, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxValidationTargets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel4);

        jPanel6.setName("jPanel6"); // NOI18N

        btnSearch.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnSearch.setText(bundle.getString("BrManagementPanel.btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(btnSearch)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAdd.setAction(actionMap.get("addBr")); // NOI18N
        btnAdd.setText(bundle.getString("BrManagementPanel.btnAdd.text")); // NOI18N
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAdd);

        btnEdit.setAction(actionMap.get("editBr")); // NOI18N
        btnEdit.setText(bundle.getString("BrManagementPanel.btnEdit.text")); // NOI18N
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnEdit);

        btnRemove.setAction(actionMap.get("removeBr")); // NOI18N
        btnRemove.setText(bundle.getString("BrManagementPanel.btnRemove.text")); // NOI18N
        btnRemove.setFocusable(false);
        btnRemove.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnRemove);

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
        );

        pnlMain.add(pnlSearch, "card2");

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("BrManagementPanel.pnlHeader.titleText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchBr();
    }

    @Action
    public void addBr() {
        showBrPanel(null);
    }//GEN-LAST:event_btnSearchActionPerformed

    @Action
    public void editBr() {
        if(brSearchResults.getSelectedBrSearchResult()!=null){
            showBrPanel(brSearchResults.getBrNotLocalized());
        }
    }

    @Action
    public void removeBr() {
        if (brSearchResults.getSelectedBrSearchResult() != null && MessageUtility.displayMessage(
                ClientMessage.ADMIN_CONFIRM_DELETE_BR,
                new String[]{brSearchResults.getSelectedBrSearchResult().getDisplayName()}) == 
                MessageUtility.BUTTON_ONE) {
            BrBean brBean = BrBean.getBr(brSearchResults.getSelectedBrSearchResult().getId());
            if(brBean.getFilteredBrValidationList().size()>0){
                MessageUtility.displayMessage(ClientMessage.ADMIN_BR_REMOVE_VALIDATIONS);
                return;
            }else{
                brBean.remove();
            }
            searchBr();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.system.BrSearchParamsBean brSearchParams;
    private org.sola.clients.beans.system.BrSearchResultListBean brSearchResults;
    private org.sola.clients.beans.referencedata.BrTechnicalTypeListBean brTechnicalTypes;
    private org.sola.clients.beans.referencedata.BrValidationTargetTypeListBean brValidationTargetTypes;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cbxTechnicalTypes;
    private javax.swing.JComboBox cbxValidationTargets;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAdd;
    private javax.swing.JMenuItem menuEdit;
    private javax.swing.JMenuItem menuRemove;
    private org.sola.clients.swing.ui.system.BrPanel pnlBr;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPopupMenu popupBrs;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableSearchResults;
    private javax.swing.JTextField txtDisplayName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
