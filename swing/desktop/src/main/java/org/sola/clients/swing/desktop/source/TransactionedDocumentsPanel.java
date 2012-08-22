/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.desktop.source;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.*;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.source.DocumentSearchPanel;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This form is used to manage transaction-driven documents. Document can be
 * attached to transaction or detached from it.
 */
public class TransactionedDocumentsPanel extends ContentPanel {

    private ApplicationBean appBean;
    private ApplicationServiceBean appService;
    private static final String CARD_POWER_OF_ATTORNEY = "cardPowerOfAttorney";
    private static final String CARD_DOCUMENTS = "cardDocuments";

    /**
     * Creates new form TransactionedDocumentsForm
     */
    public TransactionedDocumentsPanel(ApplicationBean appBean, ApplicationServiceBean appService) {
        this.appBean = appBean;
        this.appService = appService;
        initComponents();

        postInit();
    }

    private void postInit() {
        String serviceName = "";
        String applicationNr = "";

        if (appService != null) {
            serviceName = appService.getRequestType().getDisplayValue();
        }
        if (appBean != null) {
            applicationDocumentsPanel.setSourceList(appBean.getSourceList());
            applicationNr = appBean.getNr();
        }

        headerPanel.setTitleText(MessageFormat.format(serviceName, headerPanel.getTitleText(), applicationNr));
        groupSelectedDocuments.setTitleText(MessageFormat.format(groupSelectedDocuments.getTitleText(), serviceName));

        customizeDocumentRemoveButton(null);
        customizeAddFromSearchButton(null);
        customizeAddFromApplicationButton(null);
        customizePowerOfAttorneyRemoveButton(null);
        customizeAddFromPowerOfAttorneySearchButton(null);

        selectedDocumentsPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DocumentsPanel.SELECTED_SOURCE)) {
                    customizeDocumentRemoveButton((SourceBean) evt.getNewValue());
                }
            }
        });

        powerOfAttorneyList.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PowerOfAttorneyListBean.SELECTED_POWER_OF_ATTORNEY_PROPERTY)) {
                    customizePowerOfAttorneyRemoveButton((PowerOfAttorneyBean) evt.getNewValue());
                }
            }
        });

        applicationDocumentsPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DocumentsPanel.SELECTED_SOURCE)) {
                    customizeAddFromApplicationButton((SourceBean) evt.getNewValue());
                }
            }
        });

        documentSearchPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DocumentSearchPanel.SELECTED_SOURCE)) {
                    customizeAddFromSearchButton((SourceSearchResultBean) evt.getNewValue());
                }
            }
        });

        powerOfAttorneySearchPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PowerOfAttorneySearchPanel.SELECTED_POWER_OF_ATTORNEY_SEARCH_RESULT)) {
                    customizeAddFromPowerOfAttorneySearchButton((PowerOfAttorneySearchResultBean) evt.getNewValue());
                }
            }
        });

        if (appService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_REG_POWER_OF_ATTORNEY)) {
            // Power of attorney registration
            ((CardLayout) pnlCards.getLayout()).show(pnlCards, CARD_POWER_OF_ATTORNEY);
            tabsDocumentSelection.removeTabAt(tabsDocumentSelection.indexOfComponent(tabPowerOfAttorneySearch));
            powerOfAttorneyList.loadPowerOfAttorneyByService(appService.getId());
        } else if (appService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_CANCEL_POWER_OF_ATTORNEY)) {
            // Cancelling power of attorney registration
            ((CardLayout) pnlCards.getLayout()).show(pnlCards, CARD_POWER_OF_ATTORNEY);
            tabsDocumentSelection.removeTabAt(tabsDocumentSelection.indexOfComponent(tabApplicationDocuments));
            tabsDocumentSelection.removeTabAt(tabsDocumentSelection.indexOfComponent(tabDocumentSearch));
            powerOfAttorneyList.loadPowerOfAttorneyByService(appService.getId());
        } else {
            // Other documents
            selectedDocumentsPanel.getSourceListBean().loadSourceByService(appService.getId());
            ((CardLayout) pnlCards.getLayout()).show(pnlCards, CARD_DOCUMENTS);
            tabsDocumentSelection.removeTabAt(tabsDocumentSelection.indexOfComponent(tabPowerOfAttorneySearch));
        }
    }

    private boolean isReadOnly() {
        return appService != null && !appService.isManagementAllowed();
    }

    private void customizeDocumentRemoveButton(SourceBean source) {
        if (source == null) {
            btnRemove.setEnabled(false);
        } else {
            btnRemove.setEnabled(
                    SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL) && !isReadOnly());
        }
    }

    private void customizePowerOfAttorneyRemoveButton(PowerOfAttorneyBean powerOfAttorneyBean) {
        if (powerOfAttorneyBean == null) {
            btnRemovePowerOfAttorney.setEnabled(false);
        } else {
            btnRemovePowerOfAttorney.setEnabled(
                    SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL) && !isReadOnly());
        }
    }

    private void customizeAddFromApplicationButton(SourceBean source) {
        if (source == null) {
            btnAddDocumentFromApplication.setEnabled(false);
        } else {
            btnAddDocumentFromApplication.setEnabled(
                    SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL) && !isReadOnly());
        }
    }

    private void customizeAddFromPowerOfAttorneySearchButton(PowerOfAttorneySearchResultBean powerOfAttorney) {
        if (powerOfAttorney == null) {
            btnAttachPowerOfAttorneySearch.setEnabled(false);
        } else {
            btnAttachPowerOfAttorneySearch.setEnabled(
                    SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL) && !isReadOnly());
        }
    }

    private void customizeAddFromSearchButton(SourceSearchResultBean source) {
        if (source == null) {
            btnAddDocumentFromSearch.setEnabled(false);
        } else {
            btnAddDocumentFromSearch.setEnabled(
                    SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL) && !isReadOnly());
        }
    }

    private void registerPowerOfAttorneyCancellation(PowerOfAttorneySearchResultBean powerOfAttorneySearchResult) {
        PowerOfAttorneyBean powerOfAttorney = PowerOfAttorneyBean.getPowerOfAttorney(powerOfAttorneySearchResult.getId());
        powerOfAttorneyList.getPowerOfAttorneyList().addAsNew(PowerOfAttorneyBean.attachToTransaction(powerOfAttorney, appService.getId()));
    }

    private void attachToTransaction(SourceSummaryBean sourceToAttach) {
        if (sourceToAttach != null && appService != null) {
            if (appService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_REG_POWER_OF_ATTORNEY)) {
                // Register Power of Attorney
                SourceBean source;
                if (SourceBean.class.isAssignableFrom(sourceToAttach.getClass())) {
                    source = (SourceBean) sourceToAttach;
                } else {
                    source = SourceBean.getSource(sourceToAttach.getId());
                }

                PowerOfAttorneyForm form = new PowerOfAttorneyForm(null, true, source);
                form.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(PowerOfAttorneyForm.POWER_OF_ATTORNEY_CAHNGED)) {
                            PowerOfAttorneyBean powerOfAttorney = (PowerOfAttorneyBean) evt.getNewValue();
                            powerOfAttorney = PowerOfAttorneyBean.attachToTransaction(powerOfAttorney, appService.getId());
                            if (powerOfAttorney != null) {
                                powerOfAttorneyList.getPowerOfAttorneyList().addAsNew(powerOfAttorney);
                            }
                        }
                    }
                });
                form.setVisible(true);
            } else {
                // Register other documents
                SourceBean attachedSource = SourceBean.attachToTransaction(sourceToAttach.getId(), appService.getId());
                if (attachedSource != null) {
                    selectedDocumentsPanel.getSourceListBean().getSourceBeanList().add(attachedSource);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        powerOfAttorneyList = new org.sola.clients.beans.source.PowerOfAttorneyListBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        tabsDocumentSelection = new javax.swing.JTabbedPane();
        tabApplicationDocuments = new javax.swing.JPanel();
        applicationDocumentsPanel = new org.sola.clients.swing.ui.source.DocumentsPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnAddDocumentFromApplication = new javax.swing.JButton();
        tabDocumentSearch = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnAddDocumentFromSearch = new javax.swing.JButton();
        documentSearchPanel = new org.sola.clients.swing.desktop.source.DocumentSearchPanel();
        tabPowerOfAttorneySearch = new javax.swing.JPanel();
        jToolBar5 = new javax.swing.JToolBar();
        btnAttachPowerOfAttorneySearch = new javax.swing.JButton();
        powerOfAttorneySearchPanel = new org.sola.clients.swing.desktop.source.PowerOfAttorneySearchPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        groupSelectedDocuments = new org.sola.clients.swing.ui.GroupPanel();
        pnlCards = new javax.swing.JPanel();
        cardPowerOfAttorney = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnRemovePowerOfAttorney = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablePowerOfAttorney = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        cardDocuments = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnRemove = new javax.swing.JButton();
        selectedDocumentsPanel = new org.sola.clients.swing.ui.source.DocumentsPanel();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/source/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("TransactionedDocumentsPanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("TransactionedDocumentsPanel.headerPanel.titleText")); // NOI18N

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 596));

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(600, 480));

        tabsDocumentSelection.setName("tabsDocumentSelection"); // NOI18N

        tabApplicationDocuments.setName("tabApplicationDocuments"); // NOI18N

        applicationDocumentsPanel.setName("applicationDocumentsPanel"); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnAddDocumentFromApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddDocumentFromApplication.setText(bundle.getString("TransactionedDocumentsPanel.btnAddDocumentFromApplication.text")); // NOI18N
        btnAddDocumentFromApplication.setName("btnAddDocumentFromApplication"); // NOI18N
        btnAddDocumentFromApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDocumentFromApplicationActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAddDocumentFromApplication);

        javax.swing.GroupLayout tabApplicationDocumentsLayout = new javax.swing.GroupLayout(tabApplicationDocuments);
        tabApplicationDocuments.setLayout(tabApplicationDocumentsLayout);
        tabApplicationDocumentsLayout.setHorizontalGroup(
            tabApplicationDocumentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabApplicationDocumentsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabApplicationDocumentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(applicationDocumentsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabApplicationDocumentsLayout.setVerticalGroup(
            tabApplicationDocumentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabApplicationDocumentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applicationDocumentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsDocumentSelection.addTab(bundle.getString("TransactionedDocumentsPanel.tabApplicationDocuments.TabConstraints.tabTitle"), tabApplicationDocuments); // NOI18N

        tabDocumentSearch.setName("tabDocumentSearch"); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        btnAddDocumentFromSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddDocumentFromSearch.setText(bundle.getString("TransactionedDocumentsPanel.btnAddDocumentFromSearch.text")); // NOI18N
        btnAddDocumentFromSearch.setName("btnAddDocumentFromSearch"); // NOI18N
        btnAddDocumentFromSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDocumentFromSearchActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAddDocumentFromSearch);

        documentSearchPanel.setName(bundle.getString("TransactionedDocumentsPanel.documentSearchPanel.name")); // NOI18N
        documentSearchPanel.setShowAttachButton(false);
        documentSearchPanel.setShowEditButton(false);
        documentSearchPanel.setShowPrintButton(false);
        documentSearchPanel.setShowSelectButton(false);

        javax.swing.GroupLayout tabDocumentSearchLayout = new javax.swing.GroupLayout(tabDocumentSearch);
        tabDocumentSearch.setLayout(tabDocumentSearchLayout);
        tabDocumentSearchLayout.setHorizontalGroup(
            tabDocumentSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDocumentSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabDocumentSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addComponent(documentSearchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabDocumentSearchLayout.setVerticalGroup(
            tabDocumentSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDocumentSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsDocumentSelection.addTab(bundle.getString("TransactionedDocumentsPanel.tabDocumentSearch.TabConstraints.tabTitle"), tabDocumentSearch); // NOI18N

        tabPowerOfAttorneySearch.setName(bundle.getString("TransactionedDocumentsPanel.tabPowerOfAttorneySearch.name")); // NOI18N

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.setName(bundle.getString("TransactionedDocumentsPanel.jToolBar5.name")); // NOI18N

        btnAttachPowerOfAttorneySearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAttachPowerOfAttorneySearch.setText(bundle.getString("TransactionedDocumentsPanel.btnAttachPowerOfAttorneySearch.text")); // NOI18N
        btnAttachPowerOfAttorneySearch.setFocusable(false);
        btnAttachPowerOfAttorneySearch.setName(bundle.getString("TransactionedDocumentsPanel.btnAttachPowerOfAttorneySearch.name")); // NOI18N
        btnAttachPowerOfAttorneySearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAttachPowerOfAttorneySearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttachPowerOfAttorneySearchActionPerformed(evt);
            }
        });
        jToolBar5.add(btnAttachPowerOfAttorneySearch);

        powerOfAttorneySearchPanel.setName(bundle.getString("TransactionedDocumentsPanel.powerOfAttorneySearchPanel.name")); // NOI18N
        powerOfAttorneySearchPanel.setShowSelectButton(false);

        javax.swing.GroupLayout tabPowerOfAttorneySearchLayout = new javax.swing.GroupLayout(tabPowerOfAttorneySearch);
        tabPowerOfAttorneySearch.setLayout(tabPowerOfAttorneySearchLayout);
        tabPowerOfAttorneySearchLayout.setHorizontalGroup(
            tabPowerOfAttorneySearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPowerOfAttorneySearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPowerOfAttorneySearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addComponent(powerOfAttorneySearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabPowerOfAttorneySearchLayout.setVerticalGroup(
            tabPowerOfAttorneySearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPowerOfAttorneySearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(powerOfAttorneySearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsDocumentSelection.addTab(bundle.getString("TransactionedDocumentsPanel.tabPowerOfAttorneySearch.TabConstraints.tabTitle"), tabPowerOfAttorneySearch); // NOI18N

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("TransactionedDocumentsPanel.groupPanel1.titleText")); // NOI18N

        groupSelectedDocuments.setName("groupSelectedDocuments"); // NOI18N
        groupSelectedDocuments.setTitleText(bundle.getString("TransactionedDocumentsPanel.groupSelectedDocuments.titleText")); // NOI18N

        pnlCards.setName(bundle.getString("TransactionedDocumentsPanel.pnlCards.name")); // NOI18N
        pnlCards.setLayout(new java.awt.CardLayout());

        cardPowerOfAttorney.setName(bundle.getString("TransactionedDocumentsPanel.cardPowerOfAttorney.name")); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.setName(bundle.getString("TransactionedDocumentsPanel.jToolBar4.name")); // NOI18N

        btnRemovePowerOfAttorney.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemovePowerOfAttorney.setText(bundle.getString("TransactionedDocumentsPanel.btnRemovePowerOfAttorney.text")); // NOI18N
        btnRemovePowerOfAttorney.setFocusable(false);
        btnRemovePowerOfAttorney.setName(bundle.getString("TransactionedDocumentsPanel.btnRemovePowerOfAttorney.name")); // NOI18N
        btnRemovePowerOfAttorney.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePowerOfAttorneyActionPerformed(evt);
            }
        });
        jToolBar4.add(btnRemovePowerOfAttorney);

        jScrollPane2.setName(bundle.getString("TransactionedDocumentsPanel.jScrollPane2.name")); // NOI18N

        tablePowerOfAttorney.setName(bundle.getString("TransactionedDocumentsPanel.tablePowerOfAttorney.name")); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${powerOfAttorneyList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, powerOfAttorneyList, eLProperty, tablePowerOfAttorney);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${source.laNr}"));
        columnBinding.setColumnName("Source.la Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${source.referenceNr}"));
        columnBinding.setColumnName("Source.reference Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${source.recordation}"));
        columnBinding.setColumnName("Source.recordation");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${source.submission}"));
        columnBinding.setColumnName("Source.submission");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${attorneyName}"));
        columnBinding.setColumnName("Attorney Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${personName}"));
        columnBinding.setColumnName("Person Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, powerOfAttorneyList, org.jdesktop.beansbinding.ELProperty.create("${selectedPowerOfAttorney}"), tablePowerOfAttorney, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(tablePowerOfAttorney);
        tablePowerOfAttorney.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("TransactionedDocumentsPanel.tablePowerOfAttorney.columnModel.title0")); // NOI18N
        tablePowerOfAttorney.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("TransactionedDocumentsPanel.tablePowerOfAttorney.columnModel.title1")); // NOI18N
        tablePowerOfAttorney.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("TransactionedDocumentsPanel.tablePowerOfAttorney.columnModel.title2")); // NOI18N
        tablePowerOfAttorney.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("TransactionedDocumentsPanel.tablePowerOfAttorney.columnModel.title3")); // NOI18N
        tablePowerOfAttorney.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("TransactionedDocumentsPanel.tablePowerOfAttorney.columnModel.title4")); // NOI18N
        tablePowerOfAttorney.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("TransactionedDocumentsPanel.tablePowerOfAttorney.columnModel.title5")); // NOI18N

        javax.swing.GroupLayout cardPowerOfAttorneyLayout = new javax.swing.GroupLayout(cardPowerOfAttorney);
        cardPowerOfAttorney.setLayout(cardPowerOfAttorneyLayout);
        cardPowerOfAttorneyLayout.setHorizontalGroup(
            cardPowerOfAttorneyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
        );
        cardPowerOfAttorneyLayout.setVerticalGroup(
            cardPowerOfAttorneyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPowerOfAttorneyLayout.createSequentialGroup()
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
        );

        pnlCards.add(cardPowerOfAttorney, "cardPowerOfAttorney");

        cardDocuments.setName(bundle.getString("TransactionedDocumentsPanel.cardDocuments.name")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemove.setText(bundle.getString("TransactionedDocumentsPanel.btnRemove.text")); // NOI18N
        btnRemove.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemove);

        selectedDocumentsPanel.setName("selectedDocumentsPanel"); // NOI18N

        javax.swing.GroupLayout cardDocumentsLayout = new javax.swing.GroupLayout(cardDocuments);
        cardDocuments.setLayout(cardDocumentsLayout);
        cardDocumentsLayout.setHorizontalGroup(
            cardDocumentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(selectedDocumentsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardDocumentsLayout.setVerticalGroup(
            cardDocumentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardDocumentsLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedDocumentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
        );

        pnlCards.add(cardDocuments, "cardDocuments");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groupSelectedDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabsDocumentSelection))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(groupSelectedDocuments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCards, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsDocumentSelection)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        SourceBean selectedSource = selectedDocumentsPanel.getSourceListBean().getSelectedSource();
        if (selectedSource != null && MessageUtility.displayMessage(ClientMessage.SOURCE_DETACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE) {
            if (SourceBean.detachFromTransaction(selectedSource.getId())) {
                selectedDocumentsPanel.getSourceListBean().removeSelectedSource();
            }
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddDocumentFromApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDocumentFromApplicationActionPerformed
        if (MessageUtility.displayMessage(ClientMessage.SOURCE_ATTACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE) {
            attachToTransaction(applicationDocumentsPanel.getSourceListBean().getSelectedSource());
        }
    }//GEN-LAST:event_btnAddDocumentFromApplicationActionPerformed

    private void btnAddDocumentFromSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDocumentFromSearchActionPerformed
        if (MessageUtility.displayMessage(ClientMessage.SOURCE_ATTACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE) {
            attachToTransaction(documentSearchPanel.getSelectedSource());
        }
    }//GEN-LAST:event_btnAddDocumentFromSearchActionPerformed

    private void btnRemovePowerOfAttorneyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePowerOfAttorneyActionPerformed
        PowerOfAttorneyBean selectedPowerOfAttorney = powerOfAttorneyList.getSelectedPowerOfAttorney();
        if (selectedPowerOfAttorney != null && MessageUtility.displayMessage(ClientMessage.SOURCE_DETACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE) {
            if (SourceBean.detachFromTransaction(selectedPowerOfAttorney.getId())) {
                powerOfAttorneyList.removeSelectedPowerOfAttorney();
            }
        }
    }//GEN-LAST:event_btnRemovePowerOfAttorneyActionPerformed

    private void btnAttachPowerOfAttorneySearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttachPowerOfAttorneySearchActionPerformed
        if (MessageUtility.displayMessage(ClientMessage.SOURCE_ATTACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE) {
            registerPowerOfAttorneyCancellation(powerOfAttorneySearchPanel.getSelectedPowerOfAttorney());
        }
    }//GEN-LAST:event_btnAttachPowerOfAttorneySearchActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.ui.source.DocumentsPanel applicationDocumentsPanel;
    private javax.swing.JButton btnAddDocumentFromApplication;
    private javax.swing.JButton btnAddDocumentFromSearch;
    private javax.swing.JButton btnAttachPowerOfAttorneySearch;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnRemovePowerOfAttorney;
    private javax.swing.JPanel cardDocuments;
    private javax.swing.JPanel cardPowerOfAttorney;
    private org.sola.clients.swing.desktop.source.DocumentSearchPanel documentSearchPanel;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupSelectedDocuments;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JPanel pnlCards;
    private org.sola.clients.beans.source.PowerOfAttorneyListBean powerOfAttorneyList;
    private org.sola.clients.swing.desktop.source.PowerOfAttorneySearchPanel powerOfAttorneySearchPanel;
    private org.sola.clients.swing.ui.source.DocumentsPanel selectedDocumentsPanel;
    private javax.swing.JPanel tabApplicationDocuments;
    private javax.swing.JPanel tabDocumentSearch;
    private javax.swing.JPanel tabPowerOfAttorneySearch;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tablePowerOfAttorney;
    private javax.swing.JTabbedPane tabsDocumentSelection;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
