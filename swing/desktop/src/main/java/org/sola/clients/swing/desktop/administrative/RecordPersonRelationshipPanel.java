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
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import org.sola.clients.beans.administrative.NotifiablePartyForBaUnitBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.party.*;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.swing.common.laf.LafManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.desktop.source.DocumentForm;
import org.sola.clients.swing.ui.renderers.BooleanCellRenderer;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.clients.swing.ui.source.AddDocumentForm;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing simple ownership right. {@link RrrBean} is used to bind the
 * data on the form.
 */
public class RecordPersonRelationshipPanel extends ContentPanel {

    private AddDocumentForm applicationDocumentsForm;
    public static final String LODGED_ROLE = "notifiablePerson";
    public static final String REMOVE_PARTY_PROPERTY = "removeParty";
    public static final String SELECT_PARTY_PROPERTY = "selectParty";
    public static final String VIEW_PARTY_PROPERTY = "viewParty";
    public static final String VIEW_DOCUMENT = "viewDocument";
    private PartySummaryBean partyTargetSummary = new PartySummaryBean();
    private PartyMemberBean partyMemberTargetBean = new PartyMemberBean();
    private ApplicationBean appBean;
    private PartyBean partyAppBean;
    private PartyBean partyAppBeanForGroup;
    private GroupPartyBean groupAppPartyBean;
    private ApplicationServiceBean appService;
    private Boolean isContactPerson;
    private String targetName;
    public String serviceId;
    public Boolean setRole = true;

    /**
     * Creates documents table to show paper title documents.
     */
    private DocumentsPanel createDocumentsPanel(PartyBean partyBean) {
        DocumentsPanel panel;
        if (partyBean != null) {
            panel = new DocumentsPanel(partyBean.getSourceList());

        } else {
            panel = new DocumentsPanel();
        }
        return panel;
    }

    private PartyBean CreatePartyBean() {

        if (partyAppBean != null) {
            partyBean = partyAppBean;

        } else {
            partyBean = new PartyBean();
        }
        return partyBean;
    }

    private PartyBean CreatePartyBeanForGroup() {

        if (partyAppBeanForGroup != null) {
            partyBeanforGroup = partyAppBeanForGroup;

        } else {
            partyBeanforGroup = new PartyBean();
        }
        return partyBeanforGroup;
    }

    private GroupPartyBean CreateGroupPartyBean() {

        if (groupAppPartyBean != null) {
            groupPartyBean = groupAppPartyBean;

        } else {
            groupPartyBean = new GroupPartyBean();
        }
        return groupPartyBean;
    }

    private ApplicationBean CreateApplicationBean() {

        if (appBean != null) {
            applicationBean = appBean;

        } else {
            applicationBean = new ApplicationBean();
        }
        return applicationBean;
    }

    public RecordPersonRelationshipPanel() {
        initComponents();
    }

    /**
     * Creates new form RecordRelationshipPanel
     */
    public RecordPersonRelationshipPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService) {
        this.applicationBean = applicationBean;
        this.appBean = applicationBean;
        this.appService = applicationService;
        this.serviceId = applicationService.getId();

        notifiablePartyForBaUnitBean = NotifiablePartyForBaUnitBean.getNotifiableParty("", "", "", applicationBean.getId(), this.serviceId);

        if (notifiablePartyForBaUnitBean == null) {
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_APPLICANT_DETAILS)
                    == MessageUtility.BUTTON_ONE) {
                isContactPerson = true;
                partyBean = applicationBean.getContactPerson();
                this.partyAppBean = partyBean;


                partyBean.setName(applicationBean.getContactPerson().getName());
                partyBean.setLastName(applicationBean.getContactPerson().getLastName());
                partyBean.setAddress(applicationBean.getContactPerson().getAddress());
                partyBean.setMobile(applicationBean.getContactPerson().getMobile());
                partyBean.setEmail(applicationBean.getContactPerson().getEmail());
                partyBean.setGenderType(applicationBean.getContactPerson().getGenderType());

            } else {
                isContactPerson = false;
                partyBean = new PartyBean();
            }
            this.partyBeanforGroup = new PartyBean();
            this.partyMemberBean = new PartyMemberBean();
            this.groupPartyBean = new GroupPartyBean();
            this.partyTargetSummary = new PartySummaryBean();
            this.partyMemberTargetBean = new PartyMemberBean();
            this.applicationPropertyBean = new ApplicationPropertyBean();
        } else {
            isContactPerson = false;
            partyBean = PartyBean.getParty(notifiablePartyForBaUnitBean.getPartyId());
            this.partyBeanforGroup = new PartyBean();
            this.partyMemberBean = new PartyMemberBean();
            this.groupPartyBean = new GroupPartyBean();
            this.partyTargetSummary = new PartySummaryBean();
            this.partyMemberTargetBean = new PartyMemberBean();
            this.applicationPropertyBean = new ApplicationPropertyBean();
        }


        if (notifiablePartyForBaUnitBean
                == null) {
            notifiablePartyForBaUnitBean = new NotifiablePartyForBaUnitBean();
        }

        if (notifiablePartyForBaUnitBean.getTargetPartyId()
                != null) {
            targetName = PartyBean.getParty(notifiablePartyForBaUnitBean.getTargetPartyId()).getName() + " " + PartyBean.getParty(notifiablePartyForBaUnitBean.getTargetPartyId()).getLastName();

            partyBean = PartyBean.getParty(notifiablePartyForBaUnitBean.getPartyId());
            partyAppBean = partyBean;

            partyBeanforGroup = PartyBean.getParty(notifiablePartyForBaUnitBean.getGroupId());
            partyAppBeanForGroup = partyBeanforGroup;

            groupPartyBean = (GroupPartyBean.getGroupParty(notifiablePartyForBaUnitBean.getGroupId()));

            groupAppPartyBean = groupPartyBean;
        }

        initComponents();

        partySearchResuls.addPropertyChangeListener(
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {


                        if (evt.getPropertyName().equals(PartyPropertySearchResultListBean.SELECTED_PARTY_SEARCH_RESULT)) {
                        }
                    }
                });

        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(VIEW_PARTY_PROPERTY)) {
                    handleSearchPanelEvents(evt);
                }

                if (evt.getPropertyName().equals(VIEW_DOCUMENT)) {
                    final boolean allowEditing = false;
                    final SourceBean source = (SourceBean) evt.getNewValue();

                    SolaTask t = new SolaTask<Void, Void>() {

                        @Override
                        public Void doTask() {
                            setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_FORM_OPENING));
                            DocumentForm form = new DocumentForm(source, allowEditing, false);
                            MainForm.getInstance().getMainContentPanel().addPanel(form, MainContentPanel.CARD_SOURCE, true);
                            return null;
                        }
                    };
                    TaskManager.getInstance().runTask(t);
                }

            }
        });


        customizeForm();
    }

    private void handleSearchPanelEvents(final PropertyChangeEvent evt) {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartyPanelForm panel = null;
                if (evt.getPropertyName().equals(VIEW_PARTY_PROPERTY)) {
                    panel = new PartyPanelForm(true, (PartyBean) evt.getNewValue(), true, true);
                }

                if (panel != null) {
                    getMainContentPanel().addPanel(panel, MainContentPanel.CARD_PERSON, true);
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void customizeForm() {

        if (isContactPerson) {
            partyBean = applicationBean.getContactPerson();


            partyBean.setName(applicationBean.getContactPerson().getName());
            partyBean.setLastName(applicationBean.getContactPerson().getLastName());
            partyBean.setAddress(applicationBean.getContactPerson().getAddress());
            partyBean.setMobile(applicationBean.getContactPerson().getMobile());
            partyBean.setEmail(applicationBean.getContactPerson().getEmail());
            partyBean.setGenderType(applicationBean.getContactPerson().getGenderType());



            this.txtFirstName.setText(applicationBean.getContactPerson().getName());
            this.txtLastName.setText(applicationBean.getContactPerson().getLastName());
            this.txtAddress.setText(applicationBean.getContactPerson().getAddress().getDescription());
            this.txtPhone.setText(applicationBean.getContactPerson().getPhone());
            this.txtEmail.setText(applicationBean.getContactPerson().getEmail());
            this.cbxGender.setSelectedItem(applicationBean.getContactPerson().getGenderType());
            this.txtAddress.setEnabled(false);
            this.txtFirstName.setEnabled(false);
            this.txtLastName.setEnabled(false);
            this.cbxGender.setEnabled(false);
        }

        if (targetName != null) {
            this.txtName.setText(targetName);
            this.jPanel17.setVisible(false);
            this.jLabel5.setVisible(false);
            this.txtName.setEnabled(false);
            this.txtName.setEditable(false);
            partyBean = partyAppBean;
            partySearchResuls.search(partySearchParams, partyBean.getId());
            partyBeanforGroup = partyAppBeanForGroup;

            groupPartyBean = groupAppPartyBean;
            groupPartyTypeListBean1.setSelectedGroupPartyType(groupPartyBean.getGroupType());
            this.cbxGroupType.setSelectedItem(groupPartyBean.getGroupType());
            this.cbxGroupType.setEnabled(false);
            this.txtGroup.setEnabled(false);
            this.txtAddress.setEditable(false);
            this.txtAddress.setEnabled(false);
            this.txtFirstName.setEnabled(false);
            this.txtLastName.setEnabled(false);
            this.cbxGender.setEnabled(false);
        }
    }

    /**
     * set the contact person's role to notifiablePerson
     *
     */
    public boolean setApplicantRole() {
        PartyRoleTypeBean partyRoleType = new PartyRoleTypeBean();
        partyRoleType.setCode(LODGED_ROLE);

        partyBean.addRole(partyRoleType);

        return true;
    }

    @Override
    protected boolean panelClosing() {

        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(partyBean)) {
            return savePersonRelationship();
        }
        return true;
    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        genderTypeListBean = new org.sola.clients.beans.referencedata.GenderTypeListBean();
        partyMemberBean = new org.sola.clients.beans.party.PartyMemberBean();
        groupPartyBean = new org.sola.clients.beans.party.GroupPartyBean();
        groupPartyTypeListBean1 = new org.sola.clients.beans.referencedata.GroupPartyTypeListBean();
        partyBeanforGroup = CreatePartyBeanForGroup();
        partySearchParams = new org.sola.clients.beans.party.PartySearchParamsBean();
        partySearchResuls = new org.sola.clients.beans.party.PartyPropertySearchResultListBean();
        applicationPropertyBean = new org.sola.clients.beans.application.ApplicationPropertyBean();
        applicationBean = CreateApplicationBean();
        partyBean = CreatePartyBean();
        notifiablePartyForBaUnitBean = new org.sola.clients.beans.administrative.NotifiablePartyForBaUnitBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        jPanel19 = new javax.swing.JPanel();
        lblGender = new javax.swing.JLabel();
        cbxGender = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        labPhone = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        labEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        docTableScrollPanel = new javax.swing.JScrollPane();
        documentsPanel1 = createDocumentsPanel(this.partyBean);
        groupPanel3 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnViewPaperTitle = new javax.swing.JButton();
        btnLinkPaperTitle = new javax.swing.JButton();
        btnRemovePaperTitle = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        groupChoicePanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        labGroupType = new javax.swing.JLabel();
        cbxGroupType = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        txtGroup = new javax.swing.JTextField();
        labGroupName = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        pnlSearch = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnSelect = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        btnRemove1 = new org.sola.clients.swing.common.buttons.BtnRemove();
        separator1 = new javax.swing.JToolBar.Separator();
        jLabel5 = new javax.swing.JLabel();
        lblSearchResultNumber = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableSearchResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel14 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        groupPanel4 = new org.sola.clients.swing.ui.GroupPanel();

        setHeaderPanel(headerPanel);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
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

        jPanel6.setLayout(new java.awt.GridLayout(2, 3, 15, 0));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtFirstName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        LafManager.getInstance().setTxtProperties(txtFirstName);

        labName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labName.setText(bundle.getString("RecordPersonRelationshipPanel.labName.text")); // NOI18N
        labName.setIconTextGap(1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtFirstName)
            .addComponent(labName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${lastName}"), txtLastName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtLastName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastName.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtLastName)
            .addComponent(labLastName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${address.description}"), txtAddress, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAddress.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAddress.setHorizontalAlignment(JTextField.LEADING);

        labAddress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labAddress.setText(bundle.getString("RecordPersonRelationshipPanel.labAddress.text")); // NOI18N
        labAddress.setIconTextGap(1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtAddress)
            .addComponent(labAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel7);

        lblGender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        lblGender.setText(bundle.getString("RecordPersonRelationshipPanel.lblGender.text")); // NOI18N

        cbxGender.setBackground(new java.awt.Color(226, 244, 224));
        cbxGender.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${genderTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, genderTypeListBean, eLProperty, cbxGender);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${genderType}"), cbxGender, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbxGender, 0, 201, Short.MAX_VALUE)
            .addComponent(lblGender, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addComponent(lblGender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel19);

        labPhone.setText(bundle.getString("RecordPersonRelationshipPanel.labPhone.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${mobile}"), txtPhone, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

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
            .addComponent(txtPhone, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(labPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel8);

        labEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labEmail.setText(bundle.getString("RecordPersonRelationshipPanel.labEmail.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${email}"), txtEmail, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

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
            .addComponent(txtEmail)
            .addComponent(labEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel10);

        docTableScrollPanel.setViewportView(documentsPanel1);

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

        btnRemovePaperTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document-remove.png"))); // NOI18N
        btnRemovePaperTitle.setText(bundle.getString("RecordPersonRelationshipPanel.btnRemovePaperTitle.text")); // NOI18N
        btnRemovePaperTitle.setFocusable(false);
        btnRemovePaperTitle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemovePaperTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePaperTitleActionPerformed(evt);
            }
        });
        jToolBar4.add(btnRemovePaperTitle);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(docTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
            .addComponent(groupPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(groupPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(docTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("RecordPersonRelationshipPanel.jPanel12.TabConstraints.tabTitle"), jPanel12); // NOI18N

        jPanel1.setPreferredSize(new java.awt.Dimension(659, 800));

        groupChoicePanel.setLayout(new java.awt.GridLayout(1, 3, 15, 0));

        labGroupType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labGroupType.setText(bundle.getString("RecordPersonRelationshipPanel.labGroupType.text")); // NOI18N

        cbxGroupType.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${groupPartyTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, groupPartyTypeListBean1, eLProperty, cbxGroupType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, groupPartyTypeListBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedGroupPartyType}"), cbxGroupType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labGroupType, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(cbxGroupType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(labGroupType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxGroupType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        groupChoicePanel.add(jPanel2);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBeanforGroup, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtGroup, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labGroupName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labGroupName.setText(bundle.getString("RecordPersonRelationshipPanel.labGroupName.text")); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
            .addComponent(txtGroup)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(labGroupName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        groupChoicePanel.add(jPanel9);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        groupChoicePanel.add(jPanel11);

        pnlSearch.setMinimumSize(new java.awt.Dimension(300, 300));

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnSearch.setText(bundle.getString("RecordPersonRelationshipPanel.btnSearch.text")); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSearch);

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/eraser.png"))); // NOI18N
        btnClear.setText(bundle.getString("RecordPersonRelationshipPanel.btnClear.text")); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jToolBar2.add(btnClear);
        jToolBar2.add(jSeparator2);

        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        btnSelect.setText(bundle.getString("RecordPersonRelationshipPanel.btnSelect.text")); // NOI18N
        btnSelect.setFocusable(false);
        btnSelect.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSelect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSelect);

        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnView.setText(bundle.getString("RecordPersonRelationshipPanel.btnView.text")); // NOI18N
        btnView.setFocusable(false);
        btnView.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });
        jToolBar2.add(btnView);

        btnRemove1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemove1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemove1ActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemove1);
        jToolBar2.add(separator1);

        jLabel5.setText(bundle.getString("RecordPersonRelationshipPanel.jLabel5.text")); // NOI18N
        jToolBar2.add(jLabel5);

        lblSearchResultNumber.setFont(LafManager.getInstance().getLabFontBold());
        lblSearchResultNumber.setText(bundle.getString("RecordPersonRelationshipPanel.lblSearchResultNumber.text")); // NOI18N
        jToolBar2.add(lblSearchResultNumber);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partySearchResults}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchResuls, eLProperty, tableSearchResults);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fullName}"));
        columnBinding.setColumnName("Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${properties}"));
        columnBinding.setColumnName("Properties");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selProperties}"));
        columnBinding.setColumnName("Sel Properties");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchResuls, org.jdesktop.beansbinding.ELProperty.create("${selectedPartySearchResult}"), tableSearchResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableSearchResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableSearchResultsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableSearchResults);
        if (tableSearchResults.getColumnModel().getColumnCount() > 0) {
            tableSearchResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("RecordPersonRelationshipPanel.tableSearchResults.columnModel.title0")); // NOI18N
            tableSearchResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("RecordPersonRelationshipPanel.tableSearchResults.columnModel.title1")); // NOI18N
            tableSearchResults.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("RecordPersonRelationshipPanel.tableSearchResults.columnModel.title2")); // NOI18N
            tableSearchResults.getColumnModel().getColumn(2).setCellRenderer(new BooleanCellRenderer());
        }

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
        );

        jPanel14.setLayout(new java.awt.GridLayout(1, 3, 15, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchParams, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel4.setText(bundle.getString("RecordPersonRelationshipPanel.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
            .addComponent(txtName)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel13);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        jPanel14.add(jPanel15);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        jPanel14.add(jPanel17);

        groupPanel4.setTitleText(bundle.getString("RecordPersonRelationshipPanel.groupPanel4.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
            .addComponent(groupPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(groupPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(groupChoicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(groupChoicePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("RecordPersonRelationshipPanel.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        savePersonRelationship();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtPhoneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPhoneFocusLost
        // Verify the phone number is valid
        if (partyBean.getPhone() == null
                || !partyBean.getPhone().equals(txtPhone.getText())) {
            txtPhone.setText(partyBean.getPhone());
        }
    }//GEN-LAST:event_txtPhoneFocusLost

    private void txtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmailFocusLost
        // Verify the email address is valid
        if (partyBean.getEmail() == null
                || !partyBean.getEmail().equals(txtEmail.getText())) {
            txtEmail.setText(partyBean.getEmail());
        }
    }//GEN-LAST:event_txtEmailFocusLost

//    TODO VERIFICARE FUNZIONI TUTTE  DEI DOCUMENTI 
    /**
     * Opens paper title attachment.
     */
    private void viewDocument() {
        firePropertyChange(VIEW_DOCUMENT, null, documentsPanel1.getSourceListBean().getSelectedSource());

//        if (documentsPanel1.getSourceListBean().getSelectedSource() != null) {
//            documentsPanel1.getSourceListBean().getSelectedSource().openDocument();
//        }
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
                    partyBean.createPaperTitle(document);
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

    private void removeDocument() {
        SourceBean document = this.documentsPanel1.getSourceListBean().getSelectedSource();
        partyBean.removePaperTitle(document);
    }

    private void firePartyEvent(String propertyName) {
        if (partySearchResuls.getSelectedPartySearchResult() != null) {
            firePropertyChange(propertyName, null,
                    PartyBean.getParty(partySearchResuls.getSelectedPartySearchResult().getId()));
        }
    }

    public void clickFind() {
        search();
    }

    private NotifiablePartyForBaUnitBean getnotifiablePartyExists() {
        NotifiablePartyForBaUnitBean notifiablePartyExist = NotifiablePartyForBaUnitBean.getNotifiableParty("", "", "", applicationBean.getId(), this.serviceId);
        return notifiablePartyExist;
    }

    private NotifiablePartyForBaUnitBean getnotifiablePartyExists(String partyId, String targetPartyId, String baunitName) {
        NotifiablePartyForBaUnitBean notifiablePartyExist = NotifiablePartyForBaUnitBean.getNotifiableParty(partyId, targetPartyId, baunitName, applicationBean.getId(), this.serviceId);
        return notifiablePartyExist;
    }

    private boolean savePersonRelationship() {

        if ((this.partySearchResuls.getSelectedPartySearchResult() == null && targetName == null)) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
                    new Object[]{bundle.getString("RecordPersonRelationshipPanel.groupPanel4.titleText")});
            return false;
        }


        if (partyTargetSummary.getLastName() == null && jLabel5.isVisible()) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
                    new Object[]{bundle.getString("RecordPersonRelationshipPanel.groupPanel4.titleText")});
            return false;
        }
        if (this.txtGroup.getText().contentEquals("")
                || this.cbxGroupType.getSelectedIndex() == -1) {

            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
                    new Object[]{bundle.getString("RecordPersonRelationshipPanel.groupPanel2.titleText")});
            return false;
        }

        if (this.txtEmail.getText().contentEquals("")
                || this.cbxGroupType.getSelectedIndex() == -1) {

            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
                    new Object[]{bundle.getString("RecordPersonRelationshipPanel.labEmail.text")});
            return false;
        }

        for (Iterator<ApplicationPropertyBean> it = applicationBean.getPropertyList().iterator(); it.hasNext();) {
            ApplicationPropertyBean appProperty = it.next();
            if ((appProperty.getNameFirstpart() + appProperty.getNameLastpart()).equals(applicationPropertyBean.getNameFirstpart() + applicationPropertyBean.getNameLastpart())) {
                MessageUtility.displayMessage(ClientMessage.APPLICATION_PROPERTY_ALREADY_SELECTED);
                return false;
            }
        }

        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                try {

                    try {

                        if (targetName == null) {
                            if (!isContactPerson) {
                                partyBean.setTypeCode("naturalPerson");
                            }



                            for (Iterator<PartyRoleBean> it = partyBean.getRoleList().iterator(); it.hasNext();) {
                                PartyRoleBean approle = it.next();
                                if (approle.getRole().getCode().contains(LODGED_ROLE)) {
                                    setRole = false;
                                }
                            }

                            if (setRole) {
                                setApplicantRole();
                            }
                            partyBean.saveParty();
                        } else {
                            partyBean.saveParty();
                        }

                    } catch (Exception e) {
                        LogUtility.log("Error saving PARTY BEAN 1: "
                                + e);
                        return null;

                    }
                    try {
                        if (targetName == null) {

                            partyBeanforGroup.setTypeCode("naturalPerson");
                            partyBeanforGroup.setGenderCode("na");
                            partyBeanforGroup.saveParty();
                        }
                    } catch (Exception e) {
                        LogUtility.log("Error saving PARTY FOR GROUP 2: "
                                + e);
                        return null;

                    }
                    try {
                        if (targetName == null) {
                            groupPartyBean.setId(partyBeanforGroup.getId());
                            groupPartyBean.setGroupCode(groupPartyTypeListBean1.getSelectedGroupPartyType().getCode());
                            groupPartyBean.saveGroupParty();
                        }
                    } catch (Exception e) {
                        LogUtility.log("Error saving GROUP 3 "
                                + e);
                        return null;
                    }

                    try {
                        if (partySearchResuls.getSelectedPartySearchResult() != null) {
                            if (targetName == null && getnotifiablePartyExists() == null) {
                                partyMemberBean.setGroupId(groupPartyBean.getId());
                                partyMemberBean.setPartyId(partyBean.getId());
                                partyMemberBean.savePartyMember(serviceId);
                                partyMemberTargetBean.setPartyId(partyTargetSummary.getId());
                                partyMemberTargetBean.setGroupId(groupPartyBean.getId());
                                partyMemberTargetBean.savePartyMember(serviceId);
                            }
                            if (targetName != null && getnotifiablePartyExists() == null) {
                                partyMemberBean.setGroupId(groupPartyBean.getId());
                                partyMemberBean.setPartyId(partyBean.getId());
                                partyMemberBean.savePartyMember(serviceId);
                                partyMemberTargetBean.setPartyId(partyTargetSummary.getId());
                                partyMemberTargetBean.setGroupId(groupPartyBean.getId());
                                partyMemberTargetBean.savePartyMember(serviceId);
                            }

                            if (targetName != null && getnotifiablePartyExists(partyBean.getId(), partyTargetSummary.getId(), notifiablePartyForBaUnitBean.getBaunitName()) == null) {
                                partyMemberTargetBean.setPartyId(partyTargetSummary.getId());
                                partyMemberTargetBean.setGroupId(groupPartyBean.getId());
                                partyMemberTargetBean.savePartyMember(serviceId);
                            }
                        }
                    } catch (Exception e) {
                        LogUtility.log("Error saving PARTY MEMBER 4 "
                                + e);
                        return null;
                    }


                    try {
                        if (partySearchResuls.getSelectedPartySearchResult() != null) {
                            if (notifiablePartyForBaUnitBean == null) {
                                notifiablePartyForBaUnitBean = new NotifiablePartyForBaUnitBean();
                            }
                            if (targetName == null || getnotifiablePartyExists() == null) {

                                notifiablePartyForBaUnitBean.setApplicationId(applicationBean.getId());
                                notifiablePartyForBaUnitBean.setServiceId(appService.getId());
                                notifiablePartyForBaUnitBean.setPartyId(partyBean.getId());
                                notifiablePartyForBaUnitBean.setTargetPartyId(partyTargetSummary.getId());
                                notifiablePartyForBaUnitBean.saveNotifiableParty();
                            }
                            if (getnotifiablePartyExists(partyBean.getId(), partyTargetSummary.getId(), notifiablePartyForBaUnitBean.getBaunitName()) == null) {

                                notifiablePartyForBaUnitBean.setApplicationId(applicationBean.getId());
                                notifiablePartyForBaUnitBean.setServiceId(appService.getId());
                                notifiablePartyForBaUnitBean.setPartyId(partyBean.getId());
                                notifiablePartyForBaUnitBean.setTargetPartyId(partyTargetSummary.getId());
                                notifiablePartyForBaUnitBean.saveNotifiableParty();
                            }
                        }
                    } catch (Exception e) {
                        LogUtility.log("Error saving NOTIFIABLE PARTY FOR BAUNIT 6 "
                                + e);
                        return null;
                    }
//   TODO VERIFY IF NOTIFICATION NEEDED IN THIS CASE   
//                    String recepientFullName = partyBean.getName();
//                    String emailAddress = partyBean.getEmail();
//                    String messageText = "SCRIVO BLA BLA BLA";
//                    String messageSubject = "NOTIFIABLE NOTIFICATION";
//                    WSManager.getInstance().getCaseManagementService().sendEmail(recepientFullName, emailAddress, messageText, messageSubject);
////          systemEjb.sendEmail(recepientFullName, emailAddress, messageText, messageSubject) 

                } catch (Exception e) {
                    LogUtility.log("Error saving notifiable party at the end 7: "
                            + e);
                    return null;

                }

                return null;
            }

            @Override
            public void taskDone() {
                close();
            }
        };
        TaskManager.getInstance().runTask(t);

        return true;
    }

    /**
     * Searches parties with given criteria.
     */
    private void search() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_PERSON_SEARCHING));
                partySearchResuls.search(partySearchParams, partyBean.getId());
                return null;
            }

            @Override
            public void taskDone() {
                jLabel5.setVisible(true);
                lblSearchResultNumber.setText(Integer.toString(partySearchResuls.getPartySearchResults().size()));
                if (partySearchResuls.getPartySearchResults().size() < 1) {
                    MessageUtility.displayMessage(ClientMessage.SEARCH_NO_RESULTS);
                }
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void selectParty() {
        if (this.partySearchResuls.getSelectedPartySearchResult() != null) {
            this.partyTargetSummary = this.partySearchResuls.getSelectedPartySearchResult();
            this.txtName.setText(this.partySearchResuls.getSelectedPartySearchResult().getFullName());
            notifiablePartyForBaUnitBean.setBaunitName(this.partySearchResuls.getSelectedPartySearchResult().getNameFirstPart() + "/" + this.partySearchResuls.getSelectedPartySearchResult().getNameLastPart());

            //            TODO QUI VEDERE IN CASO LASCIARE POSSIBILITA SEARCH
            this.txtName.setEditable(false);
            this.txtName.setEnabled(false);
            this.btnSearch.setEnabled(false);
        }
    }

    private void viewParty() {
        firePartyEvent(VIEW_PARTY_PROPERTY);
    }

    private void removeParty() {
        if (partySearchResuls.getSelectedPartySearchResult() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {

            firePropertyChange(REMOVE_PARTY_PROPERTY, false, true);
            NotifiablePartyForBaUnitBean.remove(partyBean.getId(), partySearchResuls.getSelectedPartySearchResult().getId(), partySearchResuls.getSelectedPartySearchResult().getProperties(), applicationBean.getId(), this.serviceId);

            String groupId = groupPartyBean.getId();
            String targetId = partySearchResuls.getSelectedPartySearchResult().getId();
            if (this.partySearchResuls.getSelectedPartySearchResult().getTotal() == 1) {
                PartyMemberBean.remove(targetId, groupId, serviceId);
            }
            search();

            NotifiablePartyForBaUnitBean notifiablePartyExist = NotifiablePartyForBaUnitBean.getNotifiableParty("", "", "", applicationBean.getId(), this.serviceId);

            if (notifiablePartyExist == null) {
                PartyMemberBean.remove(partyBean.getId(), groupId, serviceId);
                txtName.setText(null);
                txtName.setEditable(true);
                txtName.setEnabled(true);
                jPanel17.setVisible(true);
                btnSearch.setEnabled(true);
                btnSearch.setVisible(true);
                btnClear.setEnabled(true);
                btnClear.setVisible(true);
            }

        }
    }

    private void clearForm() {
        txtName.setText(null);
        txtName.setEditable(true);
        txtName.setEnabled(true);
        this.btnSearch.setEnabled(true);
        partyTargetSummary = new PartySummaryBean();
    }

    private void btnViewPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewPaperTitleActionPerformed
        viewDocument();
    }//GEN-LAST:event_btnViewPaperTitleActionPerformed

    private void btnLinkPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLinkPaperTitleActionPerformed
        linkDocument();
//        removeDocument();
    }//GEN-LAST:event_btnLinkPaperTitleActionPerformed

    private void tableSearchResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSearchResultsMouseClicked
        if (evt.getClickCount() > 1 && evt.getButton() == MouseEvent.BUTTON1) {
            viewParty();
        }
    }//GEN-LAST:event_tableSearchResultsMouseClicked

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        viewParty();
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        selectParty();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnRemove1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemove1ActionPerformed
        removeParty();
    }//GEN-LAST:event_btnRemove1ActionPerformed

    private void btnRemovePaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePaperTitleActionPerformed
        removeDocument();
    }//GEN-LAST:event_btnRemovePaperTitleActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationBean applicationBean;
    private org.sola.clients.beans.application.ApplicationPropertyBean applicationPropertyBean;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnLinkPaperTitle;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemove1;
    private javax.swing.JButton btnRemovePaperTitle;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSelect;
    private javax.swing.JButton btnView;
    private javax.swing.JButton btnViewPaperTitle;
    public javax.swing.JComboBox cbxGender;
    private javax.swing.JComboBox cbxGroupType;
    private javax.swing.JScrollPane docTableScrollPanel;
    public org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel1;
    private javax.swing.Box.Filler filler1;
    private org.sola.clients.beans.referencedata.GenderTypeListBean genderTypeListBean;
    private javax.swing.JPanel groupChoicePanel;
    private org.sola.clients.swing.ui.GroupPanel groupPanel3;
    private org.sola.clients.swing.ui.GroupPanel groupPanel4;
    private org.sola.clients.beans.party.GroupPartyBean groupPartyBean;
    private org.sola.clients.beans.referencedata.GroupPartyTypeListBean groupPartyTypeListBean1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
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
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JLabel labAddress;
    private javax.swing.JLabel labEmail;
    private javax.swing.JLabel labGroupName;
    private javax.swing.JLabel labGroupType;
    private javax.swing.JLabel labLastName;
    private javax.swing.JLabel labName;
    private javax.swing.JLabel labPhone;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblSearchResultNumber;
    private org.sola.clients.beans.administrative.NotifiablePartyForBaUnitBean notifiablePartyForBaUnitBean;
    private org.sola.clients.beans.party.PartyBean partyBean;
    private org.sola.clients.beans.party.PartyBean partyBeanforGroup;
    private org.sola.clients.beans.party.PartyMemberBean partyMemberBean;
    private org.sola.clients.beans.party.PartySearchParamsBean partySearchParams;
    private org.sola.clients.beans.party.PartyPropertySearchResultListBean partySearchResuls;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JToolBar.Separator separator1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableSearchResults;
    public javax.swing.JTextField txtAddress;
    public javax.swing.JTextField txtEmail;
    public javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtGroup;
    public javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtName;
    public javax.swing.JTextField txtPhone;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
