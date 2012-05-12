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
import javax.validation.groups.Default;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrShareBean;
import org.sola.clients.beans.administrative.validation.OwnershipValidationGroup;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.TableCellListRenderer;
import org.sola.clients.swing.ui.source.DocumentsManagementPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing ownership right. {@link RrrBean} is used to bind the data on the form.
 */
public class OwnershipPanel extends ContentPanel {

    private class ShareFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(SharePanel.UPDATED_RRR_SHARE)
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

    public OwnershipPanel(RrrBean rrrBean, ApplicationBean applicationBean, 
            ApplicationServiceBean applicationService, RrrBean.RRR_ACTION rrrAction) {

        this.applicationBean = applicationBean;
        this.appService = applicationService;
        this.rrrAction = rrrAction;
        prepareRrrBean(rrrBean, rrrAction);
    
        initComponents();

        customizeForm();
        customizeSharesButtons(null);
        saveRrrState();
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

    private void customizeSharesButtons(RrrShareBean rrrShare) {
        boolean isChangesAllowed = false;
        if (rrrAction == RrrBean.RRR_ACTION.VARY || rrrAction == RrrBean.RRR_ACTION.EDIT
                || rrrAction == RrrBean.RRR_ACTION.NEW) {
            isChangesAllowed = true;
        }

        btnAddShare.setEnabled(isChangesAllowed);

        if (rrrShare == null) {
            btnRemoveShare.setEnabled(false);
            btnChangeShare.setEnabled(false);
            btnViewShare.setEnabled(false);
        } else {
            btnRemoveShare.setEnabled(isChangesAllowed);
            btnChangeShare.setEnabled(isChangesAllowed);
            btnViewShare.setEnabled(true);
        }
        
        menuAddShare.setEnabled(btnAddShare.isEnabled());
        menuRemoveShare.setEnabled(btnRemoveShare.isEnabled());
        menuChangeShare.setEnabled(btnChangeShare.isEnabled());
        menuViewShare.setEnabled(btnViewShare.isEnabled());
    }

    private void customizeForm() {
        headerPanel.setTitleText(rrrBean.getRrrType().getDisplayValue());
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                            ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        }
        if (rrrAction == RrrBean.RRR_ACTION.CANCEL) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                            ClientMessage.GENERAL_LABELS_TERMINATE_AND_CLOSE).getMessage());
        }

        if (rrrAction != RrrBean.RRR_ACTION.EDIT && rrrAction != RrrBean.RRR_ACTION.VIEW
                && appService != null) {
            // Set default noation text from the selected application service
            txtNotationText.setText(appService.getRequestType().getNotationTemplate());
        }

        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            btnSave.setEnabled(false);
            txtNotationText.setEnabled(false);
            txtRegDatetime.setEditable(false);
            txtNotationText.setEditable(false);
            cbxIsPrimary.setEnabled(false);
        }
    }

    private void openShareForm(RrrShareBean shareBean, RrrBean.RRR_ACTION rrrAction) {
        SharePanel shareForm = new SharePanel(shareBean, rrrAction);
        ShareFormListener listener = new ShareFormListener();
        shareForm.addPropertyChangeListener(SharePanel.UPDATED_RRR_SHARE, listener);
        getMainContentPanel().addPanel(shareForm, MainContentPanel.CARD_OWNERSHIP_SHARE, true);
    }

    private boolean saveRrr() {
        if (rrrBean.validate(true, Default.class, OwnershipValidationGroup.class).size() < 1) {
            firePropertyChange(UPDATED_RRR, null, rrrBean);
            close();
            return true;
        }
        return false;
    }
    
    private void saveRrrState() {
        MainForm.saveBeanState(rrrBean);
    }

    @Override
    protected boolean panelClosing() {
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(rrrBean)) {
            return saveRrr();
        }
        return true;
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
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtRegDatetime = new javax.swing.JFormattedTextField();
        cbxIsPrimary = new javax.swing.JCheckBox();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtNotationText = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddShare = new javax.swing.JButton();
        btnRemoveShare = new javax.swing.JButton();
        btnChangeShare = new javax.swing.JButton();
        btnViewShare = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableShares = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel1 = new javax.swing.JPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        documentsPanel = createDocumentsPanel();

        popUpShares.setName("popUpShares"); // NOI18N

        menuAddShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddShare.setText(bundle.getString("OwnershipPanel.menuAddShare.text")); // NOI18N
        menuAddShare.setName("menuAddShare"); // NOI18N
        menuAddShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuAddShare);

        menuRemoveShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveShare.setText(bundle.getString("OwnershipPanel.menuRemoveShare.text")); // NOI18N
        menuRemoveShare.setName("menuRemoveShare"); // NOI18N
        menuRemoveShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuRemoveShare);

        menuChangeShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/change-share.png"))); // NOI18N
        menuChangeShare.setText(bundle.getString("OwnershipPanel.menuChangeShare.text")); // NOI18N
        menuChangeShare.setName("menuChangeShare"); // NOI18N
        menuChangeShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuChangeShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuChangeShare);

        menuViewShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewShare.setText(bundle.getString("OwnershipPanel.menuViewShare.text")); // NOI18N
        menuViewShare.setName("menuViewShare"); // NOI18N
        menuViewShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuViewShare);

        setHeaderPanel(headerPanel);
        setHelpTopic(bundle.getString("OwnershipPanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel13.setText(bundle.getString("OwnershipPanel.jLabel13.text")); // NOI18N
        jLabel13.setToolTipText(bundle.getString("OwnershipPanel.jLabel13.toolTipText")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        txtRegDatetime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtRegDatetime.setName("txtRegDatetime"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"), txtRegDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        cbxIsPrimary.setText(bundle.getString("OwnershipPanel.cbxIsPrimary.text")); // NOI18N
        cbxIsPrimary.setToolTipText(bundle.getString("OwnershipPanel.cbxIsPrimary.toolTipText")); // NOI18N
        cbxIsPrimary.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cbxIsPrimary.setName("cbxIsPrimary"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${primary}"), cbxIsPrimary, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cbxIsPrimary, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxIsPrimary))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("OwnershipPanel.headerPanel.titleText")); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("OwnershipPanel.btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSave);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar2.add(jSeparator1);

        filler1.setName("filler1"); // NOI18N
        jToolBar2.add(filler1);

        jLabel1.setText(bundle.getString("OwnershipPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jToolBar2.add(jLabel1);

        lblStatus.setFont(LafManager.getInstance().getLabFontBold());
        lblStatus.setName("lblStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), lblStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(lblStatus);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel15.setText(bundle.getString("OwnershipPanel.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        txtNotationText.setName("txtNotationText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.notationText}"), txtNotationText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(2, 1, 0, 20));

        jPanel2.setName("jPanel2"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddShare.setText(bundle.getString("OwnershipPanel.btnAddShare.text")); // NOI18N
        btnAddShare.setName("btnAddShare"); // NOI18N
        btnAddShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddShare);

        btnRemoveShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveShare.setText(bundle.getString("OwnershipPanel.btnRemoveShare.text")); // NOI18N
        btnRemoveShare.setName("btnRemoveShare"); // NOI18N
        btnRemoveShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveShare);

        btnChangeShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/change-share.png"))); // NOI18N
        btnChangeShare.setText(bundle.getString("OwnershipPanel.btnChangeShare.text")); // NOI18N
        btnChangeShare.setName("btnChangeShare"); // NOI18N
        btnChangeShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnChangeShare);

        btnViewShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewShare.setText(bundle.getString("OwnershipPanel.btnViewShare.text")); // NOI18N
        btnViewShare.setFocusable(false);
        btnViewShare.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewShare.setName("btnViewShare"); // NOI18N
        btnViewShare.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewShare);

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
        tableShares.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("OwnershipPanel.tableShares.columnModel.title0")); // NOI18N
        tableShares.getColumnModel().getColumn(0).setCellRenderer(new TableCellListRenderer("getName", "getLastName"));
        tableShares.getColumnModel().getColumn(1).setMinWidth(150);
        tableShares.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableShares.getColumnModel().getColumn(1).setMaxWidth(150);
        tableShares.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("OwnershipPanel.tableShares.columnModel.title1")); // NOI18N

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("OwnershipPanel.groupPanel1.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel2);

        jPanel1.setName("jPanel1"); // NOI18N

        groupPanel2.setName("groupPanel2"); // NOI18N
        groupPanel2.setTitleText(bundle.getString("OwnershipPanel.groupPanel2.titleText")); // NOI18N

        documentsPanel.setName("documentsPanel"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
            .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNotationText)
                    .addComponent(jLabel15)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void tableSharesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSharesMouseClicked
        if (evt.getClickCount() > 1) {
            viewShare();
        }
    }//GEN-LAST:event_tableSharesMouseClicked

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveRrr();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddShareActionPerformed
        addShare();
    }//GEN-LAST:event_btnAddShareActionPerformed

    private void btnRemoveShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveShareActionPerformed
        removeShare();
    }//GEN-LAST:event_btnRemoveShareActionPerformed

    private void btnChangeShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeShareActionPerformed
        changeShare();
    }//GEN-LAST:event_btnChangeShareActionPerformed

    private void btnViewShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewShareActionPerformed
        viewShare();
    }//GEN-LAST:event_btnViewShareActionPerformed

    private void menuAddShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddShareActionPerformed
        addShare();
    }//GEN-LAST:event_menuAddShareActionPerformed

    private void menuRemoveShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveShareActionPerformed
        removeShare();
    }//GEN-LAST:event_menuRemoveShareActionPerformed

    private void menuChangeShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuChangeShareActionPerformed
        changeShare();
    }//GEN-LAST:event_menuChangeShareActionPerformed

    private void menuViewShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewShareActionPerformed
        viewShare();
    }//GEN-LAST:event_menuViewShareActionPerformed

    private void changeShare() {
        if (rrrBean.getSelectedShare() != null) {
            openShareForm(rrrBean.getSelectedShare(), RrrBean.RRR_ACTION.VARY);
        }
    }

    private void removeShare() {
        if (rrrBean.getSelectedShare() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            rrrBean.removeSelectedRrrShare();
        }
    }

    private void addShare() {
        openShareForm(null, RrrBean.RRR_ACTION.NEW);
    }

    private void viewShare() {
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
    private javax.swing.Box.Filler filler1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JMenuItem menuAddShare;
    private javax.swing.JMenuItem menuChangeShare;
    private javax.swing.JMenuItem menuRemoveShare;
    private javax.swing.JMenuItem menuViewShare;
    private javax.swing.JPopupMenu popUpShares;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableShares;
    private javax.swing.JTextField txtNotationText;
    private javax.swing.JFormattedTextField txtRegDatetime;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
