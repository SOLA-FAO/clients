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
package org.sola.clients.swing.desktop.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.validation.groups.Default;
import org.jdesktop.application.Action;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrShareBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.swing.ui.renderers.TableCellListRenderer;
import org.sola.clients.swing.ui.source.DocumentsManagementPanel;
import org.sola.clients.beans.administrative.validation.OwnershipValidationGroup;
import org.sola.clients.swing.common.LafManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing ownership right. {@link RrrBean} is used to bind the data on the form.
 */
public class OwnershipForm extends javax.swing.JDialog {

    private class ShareFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ShareForm.UPDATED_RRR_SHARE)
                    && evt.getNewValue() != null) {
                rrrBean.updateListItem((RrrShareBean) evt.getNewValue(),
                        rrrBean.getRrrShareList(), true);
                tableShares.clearSelection();
            }
        }
    }
    
    private ApplicationBean applicationBean;
    private ApplicationServiceBean appService;
    private RrrBean.RRR_ACTION rrrAction;
    public static final String UPDATED_RRR = "updatedRRR";

    private DocumentsManagementPanel createDocumentsPanel() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }

        boolean allowEdit = true;
        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            allowEdit = false;
        }

        DocumentsManagementPanel panel = new DocumentsManagementPanel(
                rrrBean.getSourceList(), applicationBean, allowEdit);
        return panel;
    }

    private RrrBean CreateRrrBean() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        return rrrBean;
    }

    public OwnershipForm(java.awt.Frame parent, boolean modal, RrrBean rrrBean,
            ApplicationBean applicationBean, ApplicationServiceBean applicationService,
            RrrBean.RRR_ACTION rrrAction) {

        super(parent, modal);
        this.applicationBean = applicationBean;
        this.appService = applicationService;
        this.rrrAction = rrrAction;
        prepareRrrBean(rrrBean, rrrAction);
        this.setIconImage(new ImageIcon(OwnershipForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        initComponents();

        this.setTitle(rrrBean.getRrrType().getDisplayValue());
        customizeComponents();
        customizeForm();
        customizeSharesButtons(null);
    }

    private void prepareRrrBean(RrrBean rrrBean, RrrBean.RRR_ACTION rrrAction) {
        if (rrrBean == null) {
            this.rrrBean = new RrrBean();
            this.rrrBean.setStatusCode(StatusConstants.PENDING);
        } else {
            this.rrrBean = rrrBean.makeCopyByAction(rrrAction);
        }
        this.rrrBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RrrBean.SELECTED_SHARE_PROPERTY)) {
                    customizeSharesButtons((RrrShareBean) evt.getNewValue());
                }
            }
        });
    }
      /** Applies customization of component L&F. */
    private void customizeComponents() {
      
//    BUTTONS   
    LafManager.getInstance().setBtnProperties(btnAddShare);
    LafManager.getInstance().setBtnProperties(btnChangeShare);
    LafManager.getInstance().setBtnProperties(btnRemoveShare);
    LafManager.getInstance().setBtnProperties(btnSave);
    LafManager.getInstance().setBtnProperties(btnViewShare);
    
//     CHECKBOXES
    LafManager.getInstance().setChkProperties(cbxIsPrimary);
    
//    LABELS    
    LafManager.getInstance().setLabProperties(jLabel13);
    LafManager.getInstance().setLabProperties(jLabel14);
    LafManager.getInstance().setLabProperties(jLabel15);
     
//    TXT FIELDS
    LafManager.getInstance().setTxtProperties(txtStatus);
    LafManager.getInstance().setTxtProperties(txtNotationText);
   
//    FORMATTED TXT
    LafManager.getInstance().setFormattedTxtProperties(txtRegDatetime);
   
    }

    private void customizeSharesButtons(RrrShareBean rrrShare) {
        boolean isChangesAllowed = false;
        if (rrrAction == RrrBean.RRR_ACTION.VARY || rrrAction == RrrBean.RRR_ACTION.EDIT
                || rrrAction == RrrBean.RRR_ACTION.NEW) {
            isChangesAllowed = true;
        }

        btnAddShare.getAction().setEnabled(isChangesAllowed);

        if (rrrShare == null) {
            btnRemoveShare.getAction().setEnabled(false);
            btnChangeShare.getAction().setEnabled(false);
            btnViewShare.getAction().setEnabled(false);
        } else {
            btnRemoveShare.getAction().setEnabled(isChangesAllowed);
            btnChangeShare.getAction().setEnabled(isChangesAllowed);
            btnViewShare.getAction().setEnabled(true);
        }
    }

    private void customizeForm() {
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText("Create");
        }
        if (rrrAction == RrrBean.RRR_ACTION.CANCEL) {
            btnSave.setText("Terminate");
        }

        if (rrrAction != RrrBean.RRR_ACTION.EDIT && rrrAction != RrrBean.RRR_ACTION.VIEW
                && appService != null) {
            // Set default noation text from the selected application service
            txtNotationText.setText(appService.getRequestType().getNotationTemplate());
        }

        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            btnSave.setEnabled(false);
            pnlNotation.setEnabled(false);
            txtRegDatetime.setEditable(false);
            txtNotationText.setEditable(false);
            cbxIsPrimary.setEnabled(false);
        }
    }

    private void openShareForm(RrrShareBean shareBean, RrrBean.RRR_ACTION rrrAction) {
        ShareForm shareForm = new ShareForm(null, true, shareBean, rrrAction);
        ShareFormListener listener = new ShareFormListener();
        shareForm.addPropertyChangeListener(ShareForm.UPDATED_RRR_SHARE, listener);
        shareForm.setLocationRelativeTo(this);
        shareForm.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rrrBean = CreateRrrBean();
        popUpShares = new javax.swing.JPopupMenu();
        menuAddShare = new javax.swing.JMenuItem();
        menuRemoveShare = new javax.swing.JMenuItem();
        menuChangeShare = new javax.swing.JMenuItem();
        menuViewShare = new javax.swing.JMenuItem();
        txtRegDatetime = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        documentsPanel = createDocumentsPanel();
        txtStatus = new javax.swing.JTextField();
        pnlNotation = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtNotationText = new javax.swing.JTextField();
        cbxIsPrimary = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableShares = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddShare = new javax.swing.JButton();
        btnRemoveShare = new javax.swing.JButton();
        btnChangeShare = new javax.swing.JButton();
        btnViewShare = new javax.swing.JButton();

        popUpShares.setName("popUpShares"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(OwnershipForm.class, this);
        menuAddShare.setAction(actionMap.get("addShare")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(OwnershipForm.class);
        menuAddShare.setText(resourceMap.getString("menuAddShare.text")); // NOI18N
        menuAddShare.setName("menuAddShare"); // NOI18N
        popUpShares.add(menuAddShare);

        menuRemoveShare.setAction(actionMap.get("removeShare")); // NOI18N
        menuRemoveShare.setText(resourceMap.getString("menuRemoveShare.text")); // NOI18N
        menuRemoveShare.setName("menuRemoveShare"); // NOI18N
        popUpShares.add(menuRemoveShare);

        menuChangeShare.setAction(actionMap.get("changeShare")); // NOI18N
        menuChangeShare.setText(resourceMap.getString("menuChangeShare.text")); // NOI18N
        menuChangeShare.setName("menuChangeShare"); // NOI18N
        popUpShares.add(menuChangeShare);

        menuViewShare.setAction(actionMap.get("viewShare")); // NOI18N
        menuViewShare.setText(resourceMap.getString("menuViewShare.text")); // NOI18N
        menuViewShare.setName("menuViewShare"); // NOI18N
        popUpShares.add(menuViewShare);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        txtRegDatetime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtRegDatetime.setName("txtRegDatetime"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"), txtRegDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setToolTipText(resourceMap.getString("jLabel13.toolTipText")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel1.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel2.border.titleFont"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        documentsPanel.setName("documentsPanel"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtStatus.setEditable(false);
        txtStatus.setEnabled(false);
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        pnlNotation.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("pnlNotation.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("pnlNotation.border.titleFont"))); // NOI18N
        pnlNotation.setName("pnlNotation"); // NOI18N

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setIcon(resourceMap.getIcon("jLabel15.icon")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        txtNotationText.setName("txtNotationText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.notationText}"), txtNotationText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout pnlNotationLayout = new javax.swing.GroupLayout(pnlNotation);
        pnlNotation.setLayout(pnlNotationLayout);
        pnlNotationLayout.setHorizontalGroup(
            pnlNotationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNotationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNotationText, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                    .addComponent(jLabel15))
                .addContainerGap())
        );
        pnlNotationLayout.setVerticalGroup(
            pnlNotationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        cbxIsPrimary.setText(resourceMap.getString("cbxIsPrimary.text")); // NOI18N
        cbxIsPrimary.setToolTipText(resourceMap.getString("cbxIsPrimary.toolTipText")); // NOI18N
        cbxIsPrimary.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cbxIsPrimary.setName("cbxIsPrimary"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${primary}"), cbxIsPrimary, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Shares", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel2.border.titleFont"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableShares.setComponentPopupMenu(popUpShares);
        tableShares.setName("tableShares"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredRrrShareList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, eLProperty, tableShares);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rightHolderList}"));
        columnBinding.setColumnName("Right Holder List");
        columnBinding.setColumnClass(org.jdesktop.observablecollections.ObservableList.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${share}"));
        columnBinding.setColumnName("Share");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${selectedShare}"), tableShares, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableShares.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableSharesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableShares);
        tableShares.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tableShares.columnModel.title0")); // NOI18N
        tableShares.getColumnModel().getColumn(0).setCellRenderer(new TableCellListRenderer("getName", "getLastName"));
        tableShares.getColumnModel().getColumn(1).setMinWidth(150);
        tableShares.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableShares.getColumnModel().getColumn(1).setMaxWidth(150);
        tableShares.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tableShares.columnModel.title1")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddShare.setAction(actionMap.get("addShare")); // NOI18N
        btnAddShare.setName("btnAddShare"); // NOI18N
        jToolBar1.add(btnAddShare);

        btnRemoveShare.setAction(actionMap.get("removeShare")); // NOI18N
        btnRemoveShare.setName("btnRemoveShare"); // NOI18N
        jToolBar1.add(btnRemoveShare);

        btnChangeShare.setAction(actionMap.get("changeShare")); // NOI18N
        btnChangeShare.setName("btnChangeShare"); // NOI18N
        jToolBar1.add(btnChangeShare);

        btnViewShare.setAction(actionMap.get("viewShare")); // NOI18N
        btnViewShare.setText(resourceMap.getString("btnViewShare.text")); // NOI18N
        btnViewShare.setFocusable(false);
        btnViewShare.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewShare.setName("btnViewShare"); // NOI18N
        btnViewShare.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnViewShare);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlNotation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(cbxIsPrimary, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(cbxIsPrimary)
                    .addComponent(jLabel14)
                    .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlNotation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableSharesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSharesMouseClicked
        if (evt.getClickCount() > 1) {
            viewShare();
        }
    }//GEN-LAST:event_tableSharesMouseClicked

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (rrrBean.validate(true, Default.class, OwnershipValidationGroup.class).size() < 1) {
            firePropertyChange(UPDATED_RRR, null, rrrBean);
            this.dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    @Action
    public void changeShare() {
        if (rrrBean.getSelectedShare() != null) {
            openShareForm(rrrBean.getSelectedShare(), RrrBean.RRR_ACTION.VARY);
        }
    }

    @Action
    public void removeShare() {
        if (rrrBean.getSelectedShare() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            rrrBean.removeSelectedRrrShare();
        }
    }

    @Action
    public void addShare() {
        openShareForm(null, RrrBean.RRR_ACTION.NEW);
    }

    @Action
    public void viewShare() {
        if (rrrBean.getSelectedShare() != null) {
            openShareForm(rrrBean.getSelectedShare(), RrrBean.RRR_ACTION.VIEW);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddShare;
    private javax.swing.JButton btnChangeShare;
    private javax.swing.JButton btnRemoveShare;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnViewShare;
    private javax.swing.JCheckBox cbxIsPrimary;
    private org.sola.clients.swing.ui.source.DocumentsManagementPanel documentsPanel;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAddShare;
    private javax.swing.JMenuItem menuChangeShare;
    private javax.swing.JMenuItem menuRemoveShare;
    private javax.swing.JMenuItem menuViewShare;
    private javax.swing.JPanel pnlNotation;
    private javax.swing.JPopupMenu popUpShares;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableShares;
    private javax.swing.JTextField txtNotationText;
    private javax.swing.JFormattedTextField txtRegDatetime;
    private javax.swing.JTextField txtStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
