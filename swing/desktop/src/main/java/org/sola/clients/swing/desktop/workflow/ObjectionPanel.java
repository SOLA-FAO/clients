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
import javax.swing.JFormattedTextField;
import org.sola.clients.beans.administrative.BaUnitSearchResultBean;
import org.sola.clients.beans.administrative.BaUnitSummaryBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.application.ObjectionBean;
import org.sola.clients.beans.application.ObjectionCommentBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.ObjectionStatusBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.administrative.BaUnitSearchPanel;
import org.sola.clients.swing.desktop.administrative.SLPropertyPanel;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.desktop.party.PartySearchPanelForm;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.common.DateUtility;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author soladev
 */
public class ObjectionPanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private boolean readOnly = false;
    public static final String OBJECTION_SAVED = "objectionSaved";

    /**
     * Creates new form ObjectionPanel, Used by Netbeans Designer
     */
    public ObjectionPanel() {
        initComponents();
    }

    public ObjectionPanel(ObjectionBean objection, ApplicationBean appBean,
            ApplicationServiceBean serviceBean, boolean readOnly) {
        this.objection = objection;
        this.applicationBean = appBean;
        this.applicationService = serviceBean;
        this.readOnly = readOnly;
        initComponents();
        postInit();
    }

    private void postInit() {
        customizeForm();
        this.objection.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ObjectionBean.SELECTED_COMMENT_PROPERTY)) {
                    customizeCommentButtons((ObjectionCommentBean) evt.getNewValue());
                }
            }
        });
        this.objection.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ObjectionBean.SELECTED_PARTY_PROPERTY)) {
                    customizePartyButtons((PartySummaryBean) evt.getNewValue());
                }
            }
        });
        this.objection.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ObjectionBean.SELECTED_PROPERTY_PROPERTY)) {
                    customizePropertyButtons((BaUnitSummaryBean) evt.getNewValue());
                }
            }
        });
        saveObjectionState();
    }

    /**
     * Initializes a new ObjectionBean with default values. See Custom Creation
     * Code property of objection.
     *
     * @return
     */
    private ObjectionBean initObjection() {
        if (objection == null) {
            objection = new ObjectionBean();
            objection.setServiceId(applicationService.getId());
            objection.setLodgedDate(DateUtility.now());
            objection.setStatusCode(ObjectionStatusBean.CODE_LODGED);
        }
        return objection;
    }

    private DocumentsManagementExtPanel initDocumentsPanel() {
        objection = initObjection();
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }
        DocumentsManagementExtPanel panel = new DocumentsManagementExtPanel(
                objection.getSourceList(), applicationBean, !readOnly);
        return panel;
    }

    private void customizeForm() {

        this.setBreadCrumbTitle(this.getBreadCrumbPath(), getPanelTitle());

        btnClose.setEnabled(!readOnly);
        txtObjectionNr.setEnabled(!readOnly);
        txtLodgedDate.setEnabled(!readOnly);
        btnLodgedDate.setEnabled(!readOnly);
        cbxAuthority.setEnabled(!readOnly);
        cbxStatus.setEnabled(!readOnly);
        txtResolutionDate.setEnabled(!readOnly);
        btnResolutionDate.setEnabled(!readOnly);
        txtDescription.setEnabled(!readOnly);
        txtResolution.setEnabled(!readOnly);
        customizeCommentButtons(null);
        customizePartyButtons(null);
        customizePropertyButtons(null);

        // Configure Security button - hide if if the user does not 
        // have permission to change security. 
        btnSecurity.setVisible(btnClose.isEnabled()
                && SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));
    }

    private void customizeCommentButtons(ObjectionCommentBean bean) {
        boolean canAdd = !readOnly;
        boolean canView = bean != null;
        // Only allow the user to edit or remove their own comments unless they have the
        // role to edit and remove all comments. 
        boolean canEdit = canView && canAdd
                && (SecurityBean.getCurrentUser().getUserName().equals(bean.getUserName())
                || SecurityBean.isInRole(RolesConstants.WORKFLOW_EDIT_OBJECTION_COMMENT));
        btnCommentAdd.setEnabled(canAdd);
        btnCommentView.setEnabled(canView);
        btnCommentEdit.setEnabled(canEdit);
        btnCommentRemove.setEnabled(canEdit);
    }

    private void customizePartyButtons(PartySummaryBean bean) {
        boolean canAdd = !readOnly;
        boolean canView = bean != null;
        boolean canEdit = canView && canAdd;
        btnPartyAdd.setEnabled(canAdd);
        btnPartyView.setEnabled(canView);
        btnPartyEdit.setEnabled(canEdit);
        btnPartyRemove.setEnabled(canEdit);
        btnPartySearch.setEnabled(canAdd);
    }

    private void customizePropertyButtons(BaUnitSummaryBean bean) {
        boolean canAdd = !readOnly;
        boolean canView = bean != null;
        boolean canEdit = canView && canAdd;
        btnPropertyAdd.setEnabled(canAdd);
        btnPropertyView.setEnabled(canView);
        btnPropertyRemove.setEnabled(canEdit);
    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    private String getPanelTitle() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle");
        String title = bundle.getString("ObjectionPanel.headerPanel1.titleText");
        if (objection.isNew()) {
            title = bundle.getString("ObjectionPanel.headerPanel1.newTitleText");
        } else {
            title += " " + objection.getNr();
        }
        return title;
    }

    /**
     * Opens the Comment dialog so the user can add a new comment for the
     * objection.
     */
    private void addComment() {
        ObjectionCommentDialog form = new ObjectionCommentDialog(null, false, null, true);
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ObjectionCommentDialog.COMMENT_SAVED)) {
                    objection.getCommentList().addAsNew((ObjectionCommentBean) evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }

    /**
     * Opens the comment dialog so the user can edit or view their comment.
     *
     * @param comment
     * @param viewOnly
     */
    private void openComment(final ObjectionCommentBean comment, final boolean viewOnly) {
        ObjectionCommentBean copy = (ObjectionCommentBean) comment.copy();
        ObjectionCommentDialog form = new ObjectionCommentDialog(copy, viewOnly, null, true);

        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ObjectionCommentDialog.COMMENT_SAVED)) {
                    ObjectionCommentBean cmt = (ObjectionCommentBean) evt.getNewValue();
                    comment.setComment(cmt.getComment());
                }
            }
        });
        form.setVisible(true);
    }

    /**
     * Opens the PartyForm so the user and add, edit or view the details of a
     * party linked to the objection.
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
                            objection.addOrUpdateParty((PartySummaryBean) ((PartyPanelForm) evt.getSource()).getParty());

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
     * Uses the PartySearchPanelForm to allow the user to search for an existing
     * party to add to the objection.
     */
    private void openSearchPartyForm() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartySearchPanelForm partySearchForm = new PartySearchPanelForm(true, objection);
                getMainContentPanel().addPanel(partySearchForm, MainContentPanel.CARD_SEARCH_PERSONS, true);
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
     * Opens Property Search so the user can search or select a State Land
     * property to add to the objection.
     */
    public void addProperty() {

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTYSEARCH));
                BaUnitSearchPanel propSearchPanel = new BaUnitSearchPanel(applicationBean);
                // Configure the panel for display
                propSearchPanel.getSearchPanel().hideTabs(org.sola.clients.swing.ui.administrative.BaUnitSearchPanel.TAB_PROPERTY);
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
                                objection.addOrUpdateProperty(new BaUnitSummaryBean(bean));
                                MessageUtility.displayMessage(ClientMessage.BAUNIT_PROPERTY_ADDED,
                                        new String[]{prop.getName(), getPanelTitle()});
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
                objection, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    /**
     * Validates the objection before closing the form.
     *
     * @return
     */
    private boolean confirmClose() {
        boolean result = true;
        if (objection.validate(true).size() < 1) {
            saveObjectionState();
            firePropertyChange(OBJECTION_SAVED, null, objection);
            close();
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Saves a hash of the objection object so that any data changes can be
     * detected on save.
     */
    private void saveObjectionState() {
        tblParty.clearSelection();
        tblProperty.clearSelection();
        tblComments.clearSelection();
        MainForm.saveBeanState(objection);
    }

    @Override
    protected boolean panelClosing() {
        tblParty.clearSelection();
        tblProperty.clearSelection();
        tblComments.clearSelection();
        if (btnClose.isEnabled()
                && MainForm.checkSaveBeforeClose(objection)) {
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

        objection = initObjection();
        authorityListBean1 = new org.sola.clients.beans.referencedata.AuthorityListBean();
        objectionStatusListBean1 = new org.sola.clients.beans.referencedata.ObjectionStatusListBean();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnClose = new javax.swing.JButton();
        btnSecurity = new javax.swing.JButton();
        pnlTop = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtObjectionNr = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtLodgedDate = new javax.swing.JFormattedTextField();
        btnLodgedDate = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbxAuthority = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cbxStatus = new javax.swing.JComboBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtResolutionDate = new javax.swing.JFormattedTextField();
        btnResolutionDate = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtResolution = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnCommentView = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnCommentAdd = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnCommentEdit = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnCommentRemove = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblComments = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel3 = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnPartyView = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnPartyAdd = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnPartyEdit = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnPartyRemove = new org.sola.clients.swing.common.buttons.BtnRemove();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnPartySearch = new org.sola.clients.swing.common.buttons.BtnSearch();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblParty = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel4 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnPropertyView = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPropertyAdd = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnPropertyRemove = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProperty = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel5 = new javax.swing.JPanel();
        documentsManagementExtPanel1 = initDocumentsPanel();

        setHeaderPanel(headerPanel1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/workflow/Bundle"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("ObjectionPanel.headerPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png"))); // NOI18N
        btnClose.setText(bundle.getString("ObjectionPanel.btnClose.text")); // NOI18N
        btnClose.setFocusable(false);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("ObjectionPanel.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSecurity);

        pnlTop.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jLabel1.setText(bundle.getString("ObjectionPanel.jLabel1.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${nr}"), txtObjectionNr, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
            .addComponent(txtObjectionNr)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtObjectionNr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel1);

        jLabel2.setText(bundle.getString("ObjectionPanel.jLabel2.text")); // NOI18N

        txtLodgedDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtLodgedDate.setText(bundle.getString("ObjectionPanel.txtLodgedDate.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${lodgedDate}"), txtLodgedDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnLodgedDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnLodgedDate.setText(bundle.getString("ObjectionPanel.btnLodgedDate.text")); // NOI18N
        btnLodgedDate.setBorder(null);
        btnLodgedDate.setFocusable(false);
        btnLodgedDate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLodgedDate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLodgedDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLodgedDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(txtLodgedDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLodgedDate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLodgedDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLodgedDate))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel6);

        jLabel3.setText(bundle.getString("ObjectionPanel.jLabel3.text")); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredAuthorityList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, authorityListBean1, eLProperty, cbxAuthority);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${authority}"), cbxAuthority, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
            .addComponent(cbxAuthority, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxAuthority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel7);

        jLabel4.setText(bundle.getString("ObjectionPanel.jLabel4.text")); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredObjectionStatusList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objectionStatusListBean1, eLProperty, cbxStatus);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${status}"), cbxStatus, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
            .addComponent(cbxStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel8);

        jPanel9.setLayout(new java.awt.GridLayout(2, 1, 0, 6));

        txtDescription.setColumns(20);
        txtDescription.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtDescription);

        groupPanel1.setTitleText(bundle.getString("ObjectionPanel.groupPanel1.titleText")); // NOI18N

        groupPanel2.setTitleText(bundle.getString("ObjectionPanel.groupPanel2.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.add(jPanel11);

        jPanel13.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jLabel5.setText(bundle.getString("ObjectionPanel.jLabel5.text")); // NOI18N

        txtResolutionDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtResolutionDate.setText(bundle.getString("ObjectionPanel.txtResolutionDate.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${resolutionDate}"), txtResolutionDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnResolutionDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnResolutionDate.setText(bundle.getString("ObjectionPanel.btnResolutionDate.text")); // NOI18N
        btnResolutionDate.setBorder(null);
        btnResolutionDate.setFocusable(false);
        btnResolutionDate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnResolutionDate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnResolutionDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResolutionDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(txtResolutionDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnResolutionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtResolutionDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResolutionDate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel14);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        jPanel13.add(jPanel15);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel13.add(jPanel16);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel13.add(jPanel17);

        txtResolution.setColumns(20);
        txtResolution.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${resolution}"), txtResolution, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(txtResolution);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel12);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("ObjectionPanel.jPanel10.TabConstraints.tabTitle"), jPanel10); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        btnCommentView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommentViewActionPerformed(evt);
            }
        });
        jToolBar2.add(btnCommentView);
        jToolBar2.add(jSeparator1);

        btnCommentAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommentAddActionPerformed(evt);
            }
        });
        jToolBar2.add(btnCommentAdd);

        btnCommentEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommentEditActionPerformed(evt);
            }
        });
        jToolBar2.add(btnCommentEdit);

        btnCommentRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommentRemoveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnCommentRemove);

        tblComments.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredCommentList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, eLProperty, tblComments);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${commentDate}"));
        columnBinding.setColumnName("Comment Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${commentBy}"));
        columnBinding.setColumnName("Comment By");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${comment}"));
        columnBinding.setColumnName("Comment");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${selectedComment}"), tblComments, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblComments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCommentsMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblComments);
        if (tblComments.getColumnModel().getColumnCount() > 0) {
            tblComments.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ObjectionPanel.tblComments.columnModel.title0")); // NOI18N
            tblComments.getColumnModel().getColumn(0).setCellRenderer(new DateTimeRenderer(true));
            tblComments.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ObjectionPanel.tblComments.columnModel.title1")); // NOI18N
            tblComments.getColumnModel().getColumn(2).setPreferredWidth(350);
            tblComments.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ObjectionPanel.tblComments.columnModel.title2")); // NOI18N
            tblComments.getColumnModel().getColumn(2).setCellRenderer(new TableCellTextAreaRenderer());
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("ObjectionPanel.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        btnPartyView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartyViewActionPerformed(evt);
            }
        });
        jToolBar4.add(btnPartyView);
        jToolBar4.add(jSeparator3);

        btnPartyAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartyAddActionPerformed(evt);
            }
        });
        jToolBar4.add(btnPartyAdd);

        btnPartyEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartyEditActionPerformed(evt);
            }
        });
        jToolBar4.add(btnPartyEdit);

        btnPartyRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartyRemoveActionPerformed(evt);
            }
        });
        jToolBar4.add(btnPartyRemove);
        jToolBar4.add(jSeparator4);

        btnPartySearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPartySearchActionPerformed(evt);
            }
        });
        jToolBar4.add(btnPartySearch);

        tblParty.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredPartyList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, eLProperty, tblParty);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fullName}"));
        columnBinding.setColumnName("Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${selectedParty}"), tblParty, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblParty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPartyMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblParty);
        if (tblParty.getColumnModel().getColumnCount() > 0) {
            tblParty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ObjectionPanel.tblParty.columnModel.title0")); // NOI18N
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("ObjectionPanel.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        btnPropertyView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyViewActionPerformed(evt);
            }
        });
        jToolBar3.add(btnPropertyView);
        jToolBar3.add(jSeparator2);

        btnPropertyAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyAddActionPerformed(evt);
            }
        });
        jToolBar3.add(btnPropertyAdd);

        btnPropertyRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyRemoveActionPerformed(evt);
            }
        });
        jToolBar3.add(btnPropertyRemove);

        tblProperty.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredPropertyList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, eLProperty, tblProperty);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${displayName}"));
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
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, objection, org.jdesktop.beansbinding.ELProperty.create("${selectedProperty}"), tblProperty, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblProperty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPropertyMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblProperty);
        if (tblProperty.getColumnModel().getColumnCount() > 0) {
            tblProperty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ObjectionPanel.tblProperty.columnModel.title0")); // NOI18N
            tblProperty.getColumnModel().getColumn(1).setPreferredWidth(300);
            tblProperty.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ObjectionPanel.tblProperty.columnModel.title1")); // NOI18N
            tblProperty.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ObjectionPanel.tblProperty.columnModel.title2")); // NOI18N
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("ObjectionPanel.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentsManagementExtPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentsManagementExtPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("ObjectionPanel.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
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

    private void btnLodgedDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLodgedDateActionPerformed
        showCalendar(txtLodgedDate);
    }//GEN-LAST:event_btnLodgedDateActionPerformed

    private void btnResolutionDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResolutionDateActionPerformed
        showCalendar(txtResolutionDate);
    }//GEN-LAST:event_btnResolutionDateActionPerformed

    private void btnCommentViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommentViewActionPerformed
        if (objection.getSelectedComment() != null) {
            openComment(objection.getSelectedComment(), true);
        }
    }//GEN-LAST:event_btnCommentViewActionPerformed

    private void btnCommentAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommentAddActionPerformed
        addComment();
    }//GEN-LAST:event_btnCommentAddActionPerformed

    private void btnCommentEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommentEditActionPerformed
        if (objection.getSelectedComment() != null) {
            openComment(objection.getSelectedComment(), false);
        }
    }//GEN-LAST:event_btnCommentEditActionPerformed

    private void btnCommentRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommentRemoveActionPerformed
        if (objection.getSelectedComment() != null) {
            objection.removeSelectedComment();
        }
    }//GEN-LAST:event_btnCommentRemoveActionPerformed

    private void tblCommentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCommentsMouseClicked
        if (evt.getClickCount() == 2 && objection.getSelectedComment() != null) {
            openComment(objection.getSelectedComment(), btnCommentEdit.isEnabled());
        }
    }//GEN-LAST:event_tblCommentsMouseClicked

    private void btnPartyViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartyViewActionPerformed
        if (objection.getSelectedParty() != null) {
            openPartyForm(objection.getSelectedParty(), true);
        }
    }//GEN-LAST:event_btnPartyViewActionPerformed

    private void btnPartyAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartyAddActionPerformed
        openPartyForm(null, false);
    }//GEN-LAST:event_btnPartyAddActionPerformed

    private void btnPartyEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartyEditActionPerformed
        if (objection.getSelectedParty() != null) {
            openPartyForm(objection.getSelectedParty(), false);
        }
    }//GEN-LAST:event_btnPartyEditActionPerformed

    private void btnPartyRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartyRemoveActionPerformed
        if (objection.getSelectedParty() != null) {
            objection.removeSelectedParty();
        }
    }//GEN-LAST:event_btnPartyRemoveActionPerformed

    private void btnPartySearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartySearchActionPerformed
        openSearchPartyForm();
    }//GEN-LAST:event_btnPartySearchActionPerformed

    private void tblPartyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPartyMouseClicked
        if (evt.getClickCount() == 2 && objection.getSelectedParty() != null) {
            openPartyForm(objection.getSelectedParty(), btnPartyEdit.isEnabled());
        }
    }//GEN-LAST:event_tblPartyMouseClicked

    private void btnPropertyViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyViewActionPerformed
        if (objection.getSelectedProperty() != null) {
            openPropertyForm(objection.getSelectedProperty());
        }
    }//GEN-LAST:event_btnPropertyViewActionPerformed

    private void btnPropertyAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyAddActionPerformed
        addProperty();
    }//GEN-LAST:event_btnPropertyAddActionPerformed

    private void btnPropertyRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyRemoveActionPerformed
        if (objection.getSelectedProperty() != null) {
            objection.removeSelectedProperty();
        }
    }//GEN-LAST:event_btnPropertyRemoveActionPerformed

    private void tblPropertyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPropertyMouseClicked
        if (evt.getClickCount() == 2 && objection.getSelectedProperty() != null) {
            openPropertyForm(objection.getSelectedProperty());
        }
    }//GEN-LAST:event_tblPropertyMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.referencedata.AuthorityListBean authorityListBean1;
    private javax.swing.JButton btnClose;
    private org.sola.clients.swing.common.buttons.BtnAdd btnCommentAdd;
    private org.sola.clients.swing.common.buttons.BtnEdit btnCommentEdit;
    private org.sola.clients.swing.common.buttons.BtnRemove btnCommentRemove;
    private org.sola.clients.swing.common.buttons.BtnView btnCommentView;
    private javax.swing.JButton btnLodgedDate;
    private org.sola.clients.swing.common.buttons.BtnAdd btnPartyAdd;
    private org.sola.clients.swing.common.buttons.BtnEdit btnPartyEdit;
    private org.sola.clients.swing.common.buttons.BtnRemove btnPartyRemove;
    private org.sola.clients.swing.common.buttons.BtnSearch btnPartySearch;
    private org.sola.clients.swing.common.buttons.BtnView btnPartyView;
    private org.sola.clients.swing.common.buttons.BtnAdd btnPropertyAdd;
    private org.sola.clients.swing.common.buttons.BtnRemove btnPropertyRemove;
    private org.sola.clients.swing.common.buttons.BtnView btnPropertyView;
    private javax.swing.JButton btnResolutionDate;
    private javax.swing.JButton btnSecurity;
    private javax.swing.JComboBox cbxAuthority;
    private javax.swing.JComboBox cbxStatus;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsManagementExtPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private org.sola.clients.beans.application.ObjectionBean objection;
    private org.sola.clients.beans.referencedata.ObjectionStatusListBean objectionStatusListBean1;
    private javax.swing.JPanel pnlTop;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblComments;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblParty;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblProperty;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JFormattedTextField txtLodgedDate;
    private javax.swing.JTextField txtObjectionNr;
    private javax.swing.JTextArea txtResolution;
    private javax.swing.JFormattedTextField txtResolutionDate;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
