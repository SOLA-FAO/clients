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
import org.sola.clients.beans.application.NotifyBean;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.administrative.BaUnitSearchPanel;
import org.sola.clients.swing.desktop.administrative.SLPropertyPanel;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author soladev
 */
public class NotifyPanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private PartyBean party;
    private boolean readOnly = false;
    public static final String NOTIFY_SAVED = "notifySaved";

    /**
     * Creates new form NotifyPanel
     */
    public NotifyPanel() {
        initComponents();
    }

    /**
     * Creates a new Notify Party for an existing party
     *
     * @param party
     * @param appBean
     * @param serviceBean
     * @param readOnly
     */
    public NotifyPanel(PartyBean party, ApplicationBean appBean,
            ApplicationServiceBean serviceBean, boolean readOnly) {
        this.party = party;
        this.applicationBean = appBean;
        this.applicationService = serviceBean;
        this.readOnly = readOnly;
        initComponents();
        postInit();
    }

    /**
     * Allows edit of an existing Notify Party
     *
     * @param notifyParty
     * @param appBean
     * @param serviceBean
     * @param readOnly
     */
    public NotifyPanel(NotifyBean notifyParty, ApplicationBean appBean,
            ApplicationServiceBean serviceBean, boolean readOnly) {
        this.notifyParty = notifyParty;
        this.applicationBean = appBean;
        this.applicationService = serviceBean;
        this.readOnly = readOnly;
        initComponents();
        postInit();
    }

    /**
     * Common steps post initialization of the form components.
     */
    private void postInit() {
        customizeForm();
        this.notifyParty.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(NotifyBean.SELECTED_PROPERTY_PROPERTY)) {
                    customizePropertyButtons((BaUnitSummaryBean) evt.getNewValue());
                }
            }
        });
        saveNotifyPartyState();
    }

    /**
     * Initializes a new NotifyBean with default values. See Custom Creation
     * Code property of notifyParty.
     *
     * @return
     */
    private NotifyBean initNotifyParty() {
        if (notifyParty == null) {
            notifyParty = new NotifyBean();
            notifyParty.setServiceId(applicationService.getId());
            notifyParty.setParty(party);
        }
        return notifyParty;
    }

    private DocumentsManagementExtPanel initDocumentsPanel() {
        notifyParty = initNotifyParty();
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }
        DocumentsManagementExtPanel panel = new DocumentsManagementExtPanel(
                notifyParty.getSourceList(), applicationBean, !readOnly);
        return panel;
    }

    private void customizeForm() {
        this.setBreadCrumbTitle(this.getBreadCrumbPath(), getPanelTitle());
        btnClose.setEnabled(!readOnly);
        customizePropertyButtons(null);

        // Configure Security button - hide if if the user does not 
        // have permission to change security. 
        btnSecurity.setVisible(btnClose.isEnabled()
                && SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));
    }

    private String getPanelTitle() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle");
        String title = bundle.getString("NotifyPanel.headerPanel1.titleText");
        if (notifyParty.isNew()) {
            title = bundle.getString("NotifyPanel.headerPanel1.newTitleText");
        }
        title += " " + notifyParty.getParty().getFullName();
        return title;
    }

    private void customizePropertyButtons(BaUnitSummaryBean bean) {
        boolean canAdd = !readOnly;
        boolean canView = bean != null;
        boolean canEdit = canView && canAdd;
        btnPropertyAdd.setEnabled(canAdd);
        btnPropertyView.setEnabled(canView);
        btnPropertyRemove.setEnabled(canEdit);
    }

    /**
     * Opens the PartyForm so the user and add, edit or view the details of a
     * party linked to the Notify Party.
     *
     * @param partySummaryBean
     * @param isReadOnly
     */
    private void openPartyForm(final PartySummaryBean partySummaryBean, final boolean isReadOnly) {

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartyPanelForm partyForm;
                if (partySummaryBean != null) {
                    partyForm = new PartyPanelForm(true, partySummaryBean, isReadOnly, true);
                } else {
                    partyForm = new PartyPanelForm(true, null, isReadOnly, true);
                }

                partyForm.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt
                    ) {
                        if (evt.getPropertyName().equals(PartyPanelForm.PARTY_SAVED)) {
                            notifyParty.setParty(((PartyPanelForm) evt.getSource()).getParty());
                            setBreadCrumbTitle(getBreadCrumbPath(), getPanelTitle());
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
     * Opens the SLPropertyPanel to view the details of the property.
     *
     * @param property
     */
    private void openPropertyForm(final BaUnitSummaryBean property) {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTY));
                SLPropertyPanel propertyPanel = new SLPropertyPanel(property.getNameFirstpart(), property.getNameLastpart(), true);
                getMainContentPanel().addPanel(propertyPanel, MainContentPanel.CARD_PROPERTY_PANEL, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens Property Search so the user can search or select a State Land or
     * other property to add to the notify party.
     */
    public void addProperty() {

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTYSEARCH));
                BaUnitSearchPanel propSearchPanel = new BaUnitSearchPanel(applicationBean);
                // Configure the panel for display
                propSearchPanel.getSearchPanel().showSelectButtons(true);
                propSearchPanel.getSearchPanel().showOpenButtons(true);
                propSearchPanel.getSearchPanel().setDefaultActionOpen(false);
                propSearchPanel.setCloseOnSelect(false);
                propSearchPanel.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(BaUnitSearchPanel.SELECTED_RESULT_PROPERTY)) {
                            BaUnitSearchResultBean bean = (BaUnitSearchResultBean) evt.getNewValue();
                            if (bean != null) {
                                BaUnitSummaryBean prop = new BaUnitSummaryBean(bean);
                                notifyParty.addOrUpdateProperty(prop);
                                MessageUtility.displayMessage(ClientMessage.BAUNIT_PROPERTY_ADDED,
                                        new String[]{prop.getDisplayName(), getPanelTitle()});
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

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(
                notifyParty, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    /**
     * Validates the NotifyParty before closing the form.
     *
     * @return
     */
    private boolean confirmClose() {
        boolean result = true;
        if (notifyParty.validate(true).size() < 1) {
            saveNotifyPartyState();
            firePropertyChange(NOTIFY_SAVED, null, notifyParty);
            close();
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Saves a hash of the NotifyParty object so that any data changes can be
     * detected on save.
     */
    private void saveNotifyPartyState() {
        tblProperty.clearSelection();
        MainForm.saveBeanState(notifyParty);
    }

    @Override
    protected boolean panelClosing() {
        tblProperty.clearSelection();
        if (btnClose.isEnabled()
                && MainForm.checkSaveBeforeClose(notifyParty)) {
            return confirmClose();
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

        notifyParty = initNotifyParty();
        relationshipTypeListBean = new org.sola.clients.beans.referencedata.NotifyRelationshipTypeListBean();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnClose = new javax.swing.JButton();
        btnSecurity = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPartyView = new org.sola.clients.swing.common.buttons.BtnView();
        btnPartyEdit = new org.sola.clients.swing.common.buttons.BtnEdit();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        documentsManagementExtPanel1 = initDocumentsPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnPropertyView = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPropertyAdd = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnPropertyRemove = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProperty = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlTop = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();

        setHeaderPanel(headerPanel1);
        setHelpTopic("notifications"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("NotifyPanel.headerPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png"))); // NOI18N
        btnClose.setText(bundle.getString("NotifyPanel.btnClose.text")); // NOI18N
        btnClose.setFocusable(false);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("NotifyPanel.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSecurity);
        jToolBar1.add(jSeparator2);

        btnPartyView.setText(bundle.getString("NotifyPanel.btnPartyView.text")); // NOI18N
        btnPartyView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPartyView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartyViewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPartyView);

        btnPartyEdit.setText(bundle.getString("NotifyPanel.btnPartyEdit.text")); // NOI18N
        btnPartyEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPartyEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartyEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPartyEdit);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentsManagementExtPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentsManagementExtPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("NotifyPanel.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        btnPropertyView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPropertyView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyViewActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPropertyView);
        jToolBar2.add(jSeparator1);

        btnPropertyAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPropertyAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyAddActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPropertyAdd);

        btnPropertyRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPropertyRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyRemoveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPropertyRemove);

        tblProperty.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredPropertyList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, notifyParty, eLProperty, tblProperty);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${displayName}"));
        columnBinding.setColumnName("Display Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandStatus.displayValue}"));
        columnBinding.setColumnName("State Land Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, notifyParty, org.jdesktop.beansbinding.ELProperty.create("${selectedProperty}"), tblProperty, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblProperty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPropertyMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProperty);
        if (tblProperty.getColumnModel().getColumnCount() > 0) {
            tblProperty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("NotifyPanel.tblProperty.columnModel.title0")); // NOI18N
            tblProperty.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("NotifyPanel.tblProperty.columnModel.title1")); // NOI18N
            tblProperty.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("NotifyPanel.tblProperty.columnModel.title2")); // NOI18N
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("NotifyPanel.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        pnlTop.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel4.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jLabel2.setText(bundle.getString("NotifyPanel.jLabel2.text")); // NOI18N

        txtName.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, notifyParty, org.jdesktop.beansbinding.ELProperty.create("${party.fullName}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
            .addComponent(txtName)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel5);

        jLabel3.setText(bundle.getString("NotifyPanel.jLabel3.text")); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredNotifyRelationshipTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, relationshipTypeListBean, eLProperty, jComboBox1);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, notifyParty, org.jdesktop.beansbinding.ELProperty.create("${relationshipType}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel7);

        pnlTop.add(jPanel4);

        jLabel1.setText(bundle.getString("NotifyPanel.jLabel1.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, notifyParty, org.jdesktop.beansbinding.ELProperty.create("${description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
            .addComponent(txtDescription)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        confirmClose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnSecurityActionPerformed

    private void btnPropertyViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyViewActionPerformed
        if (notifyParty.getSelectedProperty() != null) {
            openPropertyForm(notifyParty.getSelectedProperty());
        }
    }//GEN-LAST:event_btnPropertyViewActionPerformed

    private void btnPropertyAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyAddActionPerformed
        addProperty();
    }//GEN-LAST:event_btnPropertyAddActionPerformed

    private void btnPropertyRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyRemoveActionPerformed
        if (notifyParty.getSelectedProperty() != null) {
            notifyParty.removeSelectedProperty();
        }
    }//GEN-LAST:event_btnPropertyRemoveActionPerformed

    private void tblPropertyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPropertyMouseClicked
        if (evt.getClickCount() == 2 && notifyParty.getSelectedProperty() != null) {
            openPropertyForm(notifyParty.getSelectedProperty());
        }
    }//GEN-LAST:event_tblPropertyMouseClicked

    private void btnPartyViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartyViewActionPerformed
        openPartyForm(notifyParty.getParty(), true);
    }//GEN-LAST:event_btnPartyViewActionPerformed

    private void btnPartyEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartyEditActionPerformed
        openPartyForm(notifyParty.getParty(), false);
    }//GEN-LAST:event_btnPartyEditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private org.sola.clients.swing.common.buttons.BtnEdit btnPartyEdit;
    private org.sola.clients.swing.common.buttons.BtnView btnPartyView;
    private org.sola.clients.swing.common.buttons.BtnAdd btnPropertyAdd;
    private org.sola.clients.swing.common.buttons.BtnRemove btnPropertyRemove;
    private org.sola.clients.swing.common.buttons.BtnView btnPropertyView;
    private javax.swing.JButton btnSecurity;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsManagementExtPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private org.sola.clients.beans.application.NotifyBean notifyParty;
    private javax.swing.JPanel pnlTop;
    private org.sola.clients.beans.referencedata.NotifyRelationshipTypeListBean relationshipTypeListBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblProperty;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
