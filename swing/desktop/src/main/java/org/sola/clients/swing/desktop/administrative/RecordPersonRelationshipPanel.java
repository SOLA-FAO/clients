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
package org.sola.clients.swing.desktop.administrative;

import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.validation.groups.Default;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.validation.SimpleOwnershipValidationGroup;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.desktop.party.PartySearchPanelForm;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.clients.swing.ui.source.AddDocumentForm;
import org.sola.clients.swing.ui.source.DocumentsManagementPanel;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing simple ownership right. {@link RrrBean} is used to bind the
 * data on the form.
 */
public class RecordPersonRelationshipPanel extends ContentPanel {

//    private ApplicationBean applicationBean;
//    private ApplicationServiceBean appService;
    private RrrBean.RRR_ACTION rrrAction;
    public static final String UPDATED_RRR = "updatedRRR";
    private AddDocumentForm applicationDocumentsForm;
    

   /**
     * Creates documents table to show paper title documents.
     */
    private DocumentsPanel createDocumentsPanel(PartyBean partyBean) {
        DocumentsPanel panel;
        if (this.applicationBean.getContactPerson() != null) {
             panel = new DocumentsPanel(partyBean.getSourceList());
           
        } else {
            panel = new DocumentsPanel();
        }
        return panel;
    }


    private RrrBean CreateRrrBean() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        return rrrBean;
    }

    public RecordPersonRelationshipPanel(RrrBean rrrBean, RrrBean.RRR_ACTION rrrAction) {
        this(rrrBean, null, null, rrrAction);
    }

    /**
     * Creates new form SimpleOwhershipPanel
     */
    public RecordPersonRelationshipPanel(RrrBean rrrBean, ApplicationBean applicationBean,
            ApplicationServiceBean applicationService, RrrBean.RRR_ACTION rrrAction) {
        this.applicationBean = applicationBean;
        this.appService = applicationService;
        this.rrrAction = rrrAction;
        prepareRrrBean(rrrBean, rrrAction);

        initComponents();

        customizeForm();
        customizeOwnerButtons(null);
        saveRrrState();
    }
    
      /**
     * Creates new form SimpleOwhershipPanel
     */
    public RecordPersonRelationshipPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService) {
        this.applicationBean = applicationBean;
        this.appService = applicationService;
//        this.rrrAction = rrrAction;
//        prepareRrrBean(rrrBean, rrrAction);

        initComponents();

//        customizeForm();
        this.cbxIsPrimary.setEnabled(false);
        this.cbxIsPrimary.setSelected(true);
        this.txtFirstName.setText(applicationBean.getContactPerson().getName());
        this.txtLastName.setText(applicationBean.getContactPerson().getLastName());
        this.txtAddress.setText(applicationBean.getContactPerson().getAddress().getDescription());
        this.txtPhone.setText(applicationBean.getContactPerson().getPhone());
        this.txtEmail.setText(applicationBean.getContactPerson().getEmail());
        this.cbxGender.setSelectedItem(applicationBean.getContactPerson().getGenderType());
        
    
        customizeOwnerButtons(null);
//        saveRrrState();
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
            btnRegDate.setEnabled(false);
            txtNotationText.setEditable(false);
            cbxIsPrimary.setEnabled(true);
        }
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
                if (evt.getPropertyName().equals(RrrBean.SELECTED_RIGHTHOLDER_PROPERTY)) {
                    customizeOwnerButtons((PartySummaryBean) evt.getNewValue());
                }
            }
        });
    }

    private void customizeOwnerButtons(PartySummaryBean owner) {
        boolean isChangesAllowed = false;
        if (rrrAction == RrrBean.RRR_ACTION.VARY || rrrAction == RrrBean.RRR_ACTION.EDIT
                || rrrAction == RrrBean.RRR_ACTION.NEW) {
            isChangesAllowed = true;
        }

//        btnAddOwner.setEnabled(isChangesAllowed);
//
//        if (owner == null) {
//            btnRemoveOwner.setEnabled(false);
//            btnEditOwner.setEnabled(false);
//            btnViewOwner.setEnabled(false);
//        } else {
//            btnRemoveOwner.setEnabled(isChangesAllowed);
//            btnEditOwner.setEnabled(isChangesAllowed);
//            btnViewOwner.setEnabled(true);
//        }
//
//        menuAddOwner.setEnabled(btnAddOwner.isEnabled());
//        menuRemoveOwner.setEnabled(btnRemoveOwner.isEnabled());
//        menuEditOwner.setEnabled(btnEditOwner.isEnabled());
//        menuViewOwner.setEnabled(btnViewOwner.isEnabled());
    }

    private boolean savePersonRelationship() {

//        if (rrrBean.getFilteredRightHolderList().size() < 1) {
//            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
//            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
//                    new Object[]{bundle.getString("SimpleOwhershipPanel.groupPanel1.titleText")});
//            return false;
//        }
//
//        if (rrrBean.validate(true, Default.class, SimpleOwnershipValidationGroup.class).size() < 1) {
//            firePropertyChange(UPDATED_RRR, null, rrrBean);
            applicationBean.getContactPerson().saveParty();
            close();
            return true;
//        }
//        return false;
    }

    private void saveRrrState() {
        MainForm.saveBeanState(rrrBean);
    }

    @Override
    protected boolean panelClosing() {
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(rrrBean)) {
            return savePersonRelationship();
        }
        return true;
    }

    private void viewOwner() {
        if (rrrBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrBean.getSelectedRightHolder(), true);
        }
    }

    private void removeOwner() {
        if (rrrBean.getSelectedRightHolder() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            rrrBean.removeSelectedRightHolder();
        }
    }

    private void addOwner() {
        openRightHolderForm(null, false);
    }

    private void editOwner() {
        if (rrrBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrBean.getSelectedRightHolder(), false);
        }
    }

    private class RightHolderFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PartyPanelForm.PARTY_SAVED)) {
                rrrBean.addOrUpdateRightholder((PartySummaryBean) ((PartyPanelForm) evt.getSource()).getParty());
//                tableOwners.clearSelection();
            }
        }
    }

    private void openRightHolderForm(final PartySummaryBean partySummaryBean, final boolean isReadOnly) {
        final RightHolderFormListener listener = new RightHolderFormListener();

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
                partyForm.addPropertyChangeListener(listener);
                getMainContentPanel().addPanel(partyForm, MainContentPanel.CARD_PERSON, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

//    private void openSelectRightHolderForm() {
//        final RightHolderFormListener listener = new RightHolderFormListener();
//
//        SolaTask t = new SolaTask<Void, Void>() {
//
//            @Override
//            public Void doTask() {
//                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
//                PartySearchPanelForm partySearchForm = null;
//
//                partySearchForm = initializePartySearchForm(partySearchForm);
//
//                partySearchForm.addPropertyChangeListener(listener);
//                getMainContentPanel().addPanel(partySearchForm, MainContentPanel.CARD_SEARCH_PERSONS, true);
//                return null;
//            }
//        };
//        TaskManager.getInstance().runTask(t);
//    }

//    private PartySearchPanelForm initializePartySearchForm(PartySearchPanelForm partySearchForm) {
//        partySearchForm = new PartySearchPanelForm(true, this.rrrBean);
//        return partySearchForm;
//
//    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rrrBean = CreateRrrBean();
        popUpOwners = new javax.swing.JPopupMenu();
        menuAddOwner = new javax.swing.JMenuItem();
        menuEditOwner = new javax.swing.JMenuItem();
        menuRemoveOwner = new javax.swing.JMenuItem();
        menuViewOwner = new javax.swing.JMenuItem();
        applicationBean = new org.sola.clients.beans.application.ApplicationBean();
        appService = new org.sola.clients.beans.application.ApplicationServiceBean();
        genderTypeListBean = new org.sola.clients.beans.referencedata.GenderTypeListBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbxIsPrimary = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        txtNotationText = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        txtFirstName = new javax.swing.JTextField();
        labName = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        labLastName = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        txtAddress = new javax.swing.JTextField();
        labAddress = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        lblGender = new javax.swing.JLabel();
        cbxGender = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        labPhone = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        labEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel3 = new javax.swing.JPanel();
        groupPanel3 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnViewPaperTitle = new javax.swing.JButton();
        btnLinkPaperTitle = new javax.swing.JButton();
        docTableScrollPanel = new javax.swing.JScrollPane();
        documentsPanel1 = createDocumentsPanel(this.applicationBean.getContactPerson());
        txtRegDatetime = new org.sola.clients.swing.common.controls.WatermarkDate();
        btnRegDate = new javax.swing.JButton();

        menuAddOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddOwner.setText(bundle.getString("SimpleOwhershipPanel.menuAddOwner.text")); // NOI18N
        menuAddOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuAddOwner);

        menuEditOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditOwner.setText(bundle.getString("SimpleOwhershipPanel.menuEditOwner.text")); // NOI18N
        menuEditOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuEditOwner);

        menuRemoveOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveOwner.setText(bundle.getString("SimpleOwhershipPanel.menuRemoveOwner.text")); // NOI18N
        menuRemoveOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuRemoveOwner);

        menuViewOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewOwner.setText(bundle.getString("SimpleOwhershipPanel.menuViewOwner.text")); // NOI18N
        menuViewOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuViewOwner);

        setHeaderPanel(headerPanel);

        headerPanel.setTitleText(bundle.getString("RecordPersonRelationshipPanel.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("SimpleOwhershipPanel.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(filler1);

        jLabel1.setText(bundle.getString("SimpleOwhershipPanel.jLabel1.text")); // NOI18N
        jToolBar1.add(jLabel1);

        lblStatus.setFont(LafManager.getInstance().getLabFontBold());
        lblStatus.setText(bundle.getString("SimpleOwhershipPanel.lblStatus.text")); // NOI18N
        jToolBar1.add(lblStatus);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel2.setText(bundle.getString("SimpleOwhershipPanel.jLabel2.text")); // NOI18N

        cbxIsPrimary.setText(bundle.getString("SimpleOwhershipPanel.cbxIsPrimary.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${primary}"), cbxIsPrimary, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel3.setText(bundle.getString("SimpleOwhershipPanel.jLabel3.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.notationText}"), txtNotationText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 0, 20));

        jPanel6.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        LafManager.getInstance().setTxtProperties(txtFirstName);

        labName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labName.setText(bundle.getString("RecordPersonRelationshipPanel.labName.text")); // NOI18N
        labName.setIconTextGap(1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(labName, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
            .addComponent(txtFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(labName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel4);

        labLastName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labLastName.setText(bundle.getString("RecordPersonRelationshipPanel.labLastName.text")); // NOI18N
        labLastName.setIconTextGap(1);

        txtLastName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastName.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(labLastName)
                .addContainerGap(139, Short.MAX_VALUE))
            .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(labLastName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel5);

        txtAddress.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAddress.setHorizontalAlignment(JTextField.LEADING);

        labAddress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labAddress.setText(bundle.getString("RecordPersonRelationshipPanel.labAddress.text")); // NOI18N
        labAddress.setIconTextGap(1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addContainerGap(149, Short.MAX_VALUE))
            .addComponent(txtAddress)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addGap(4, 4, 4)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel7);

        jPanel11.setLayout(new java.awt.GridLayout(2, 3, 15, 5));

        lblGender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        lblGender.setText(bundle.getString("RecordPersonRelationshipPanel.lblGender.text")); // NOI18N

        cbxGender.setBackground(new java.awt.Color(226, 244, 224));
        cbxGender.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${genderTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, genderTypeListBean, eLProperty, cbxGender);
        bindingGroup.addBinding(jComboBoxBinding);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(lblGender, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 243, Short.MAX_VALUE))
            .addComponent(cbxGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addComponent(lblGender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel19);

        labPhone.setText(bundle.getString("RecordPersonRelationshipPanel.labPhone.text")); // NOI18N

        txtPhone.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtPhone.setHorizontalAlignment(JTextField.LEADING);
        txtPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPhoneFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addContainerGap(277, Short.MAX_VALUE))
            .addComponent(txtPhone, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel8);

        labEmail.setText(bundle.getString("RecordPersonRelationshipPanel.labEmail.text")); // NOI18N

        txtEmail.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtEmail.setHorizontalAlignment(JTextField.LEADING);
        txtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEmailFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
            .addComponent(txtEmail)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel10);

        groupPanel1.setTitleText(bundle.getString("RecordPersonRelationshipPanel.groupPanel1.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(groupPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 649, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 217, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel1.add(jPanel2);

        groupPanel3.setTitleText(bundle.getString("RecordPersonRelationshipPanel.groupPanel3.titleText")); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        btnViewPaperTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewPaperTitle.setText(bundle.getString("RecordPersonRelationshipPanel.btnViewPaperTitle.text")); // NOI18N
        btnViewPaperTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewPaperTitleActionPerformed(evt);
            }
        });
        jToolBar4.add(btnViewPaperTitle);

        btnLinkPaperTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document-link.png"))); // NOI18N
        btnLinkPaperTitle.setText(bundle.getString("RecordPersonRelationshipPanel.btnLinkPaperTitle.text")); // NOI18N
        btnLinkPaperTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLinkPaperTitleActionPerformed(evt);
            }
        });
        jToolBar4.add(btnLinkPaperTitle);

        docTableScrollPanel.setViewportView(documentsPanel1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 649, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(groupPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                        .addComponent(docTableScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 217, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(groupPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(docTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jPanel1.add(jPanel3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"), txtRegDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnRegDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnRegDate.setText(bundle.getString("RecordPersonRelationshipPanel.btnRegDate.text")); // NOI18N
        btnRegDate.setBorder(null);
        btnRegDate.setFocusable(false);
        btnRegDate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRegDate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRegDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNotationText)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRegDate)
                                .addGap(27, 27, 27)
                                .addComponent(cbxIsPrimary)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addComponent(jLabel3))
                    .addComponent(btnRegDate)
                    .addComponent(cbxIsPrimary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void menuAddOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddOwnerActionPerformed
        addOwner();
    }//GEN-LAST:event_menuAddOwnerActionPerformed

    private void menuEditOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditOwnerActionPerformed
        editOwner();
    }//GEN-LAST:event_menuEditOwnerActionPerformed

    private void menuRemoveOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveOwnerActionPerformed
        removeOwner();
    }//GEN-LAST:event_menuRemoveOwnerActionPerformed

    private void menuViewOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewOwnerActionPerformed
        viewOwner();
    }//GEN-LAST:event_menuViewOwnerActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        savePersonRelationship();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRegDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegDateActionPerformed
        showCalendar(txtRegDatetime);
    }//GEN-LAST:event_btnRegDateActionPerformed

    private void txtPhoneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPhoneFocusLost
        // Verify the phone number is valid
        if (this.applicationBean.getContactPerson().getPhone() == null
                || !applicationBean.getContactPerson().getPhone().equals(txtPhone.getText())) {
            txtPhone.setText(applicationBean.getContactPerson().getPhone());
        }
    }//GEN-LAST:event_txtPhoneFocusLost

    private void txtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmailFocusLost
        // Verify the email address is valid
        if (applicationBean.getContactPerson().getEmail() == null
                || !applicationBean.getContactPerson().getEmail().equals(txtEmail.getText())) {
            txtEmail.setText(applicationBean.getContactPerson().getEmail());
        }
    }//GEN-LAST:event_txtEmailFocusLost
       /**
     * Opens paper title attachment.
     */
    private void viewDocument() {
        
        if (documentsPanel1.getSourceListBean().getSelectedSource() != null) {
            documentsPanel1.getSourceListBean().getSelectedSource().openDocument();
        }
    }
    
      /**
     * Links document as a paper title on the BaUnit object.
     */
    private void linkDocument() {
        openDocumentsForm();
    }
    
     /**
     * Opens form to select or create document to be used as a paper title
     * document.
     */
    private void openDocumentsForm() {
        if (applicationDocumentsForm != null) {
            applicationDocumentsForm.dispose();
        }
        
        PropertyChangeListener listener = new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                SourceBean document = null;
                if (e.getPropertyName().equals(AddDocumentForm.SELECTED_SOURCE)
                        && e.getNewValue() != null) {
                    document = (SourceBean) e.getNewValue();
                    applicationBean.getContactPerson().createPaperTitle(document);
                }
            }
        };
        
        applicationDocumentsForm = new AddDocumentForm(applicationBean, null, true);
        applicationDocumentsForm.setLocationRelativeTo(this);
        applicationDocumentsForm.addPropertyChangeListener(
                SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
        applicationDocumentsForm.setVisible(true);
        applicationDocumentsForm.removePropertyChangeListener(
                SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
    }

    
    private void btnViewPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewPaperTitleActionPerformed
        viewDocument();
    }//GEN-LAST:event_btnViewPaperTitleActionPerformed

    private void btnLinkPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLinkPaperTitleActionPerformed
        linkDocument();
    }//GEN-LAST:event_btnLinkPaperTitleActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationServiceBean appService;
    private org.sola.clients.beans.application.ApplicationBean applicationBean;
    private javax.swing.JButton btnLinkPaperTitle;
    private javax.swing.JButton btnRegDate;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnViewPaperTitle;
    public javax.swing.JComboBox cbxGender;
    private javax.swing.JCheckBox cbxIsPrimary;
    private javax.swing.JScrollPane docTableScrollPanel;
    public org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel1;
    private javax.swing.Box.Filler filler1;
    private org.sola.clients.beans.referencedata.GenderTypeListBean genderTypeListBean;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel3;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JLabel labAddress;
    private javax.swing.JLabel labEmail;
    private javax.swing.JLabel labLastName;
    private javax.swing.JLabel labName;
    private javax.swing.JLabel labPhone;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JMenuItem menuAddOwner;
    private javax.swing.JMenuItem menuEditOwner;
    private javax.swing.JMenuItem menuRemoveOwner;
    private javax.swing.JMenuItem menuViewOwner;
    private javax.swing.JPopupMenu popUpOwners;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    public javax.swing.JTextField txtAddress;
    public javax.swing.JTextField txtEmail;
    public javax.swing.JTextField txtFirstName;
    public javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtNotationText;
    public javax.swing.JTextField txtPhone;
    private org.sola.clients.swing.common.controls.WatermarkDate txtRegDatetime;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
