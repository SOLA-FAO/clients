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
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.validation.groups.Default;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.validation.SimpleOwnershipValidationGroup;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.party.*;
import org.sola.clients.beans.referencedata.*;
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

    private AddDocumentForm applicationDocumentsForm;
    public static final String LODGED_ROLE = "notifiablePerson";
    public static final String CREATE_NEW_PARTY_PROPERTY = "createNewParty";
    public static final String EDIT_PARTY_PROPERTY = "editParty";
    public static final String REMOVE_PARTY_PROPERTY = "removeParty";
    public static final String SELECT_PARTY_PROPERTY = "selectParty";
    public static final String VIEW_PARTY_PROPERTY = "viewParty";
    private PartySummaryBean partyTargetSummary = new PartySummaryBean();
    private PartyMemberBean partyMemberTargetBean = new PartyMemberBean();
    private ApplicationBean appBean;
    private PartyBean partyAppBean;
    private ApplicationServiceBean appService;
    private Boolean isContactPerson;
    
   
   /**
     * Creates documents table to show paper title documents.
     */
    private DocumentsPanel createDocumentsPanel(PartyBean partyBean) {
        DocumentsPanel panel;
//        if (this.applicationBean.getContactPerson() != null) {
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
     * Creates new form SimpleOwhershipPanel
     */
    public RecordPersonRelationshipPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService) {
        this.applicationBean = applicationBean;
        this.appBean = applicationBean;
        this.appService = applicationService;
        
        
         if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SAVE_BEFORE_ACTION)
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
            
             System.out.println("APPLICATION BEAN  "+applicationBean.getContactPerson().getFullName());
             System.out.println("PARTY BEAN  "+partyBean.getFullName());
         }
         else {
             isContactPerson = false;
             partyBean = new PartyBean();
         }
        this.partyBeanforGroup = new PartyBean();
        this.partyMemberBean = new PartyMemberBean();
        this.groupPartyBean = new GroupPartyBean();
        this.partyTargetSummary = new PartySummaryBean();
        this.partyMemberTargetBean = new PartyMemberBean();
        this.applicationPropertyBean = new ApplicationPropertyBean();
   
        
       

        initComponents();
        
          partySearchResuls.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                
                
                
                if (evt.getPropertyName().equals(PartyPropertySearchResultListBean.SELECTED_PARTY_SEARCH_RESULT)) {
//                    customizePartyButtons();
                }
            }
        });

        customizeForm();
//        this.txtFirstName.setText(applicationBean.getContactPerson().getName());
//        this.txtLastName.setText(applicationBean.getContactPerson().getLastName());
//        this.txtAddress.setText(applicationBean.getContactPerson().getAddress().getDescription());
//        this.txtPhone.setText(applicationBean.getContactPerson().getPhone());
//        this.txtEmail.setText(applicationBean.getContactPerson().getEmail());
//        this.cbxGender.setSelectedItem(applicationBean.getContactPerson().getGenderType());
        
    }
    
    
    private void customizeForm() {
// TODO THE HEADER PANEL TEXT       headerPanel.setTitleText(rrrBean.getRrrType().getDisplayValue());
          
        if (isContactPerson) {
             partyBean = applicationBean.getContactPerson();
             
             
            partyBean.setName(applicationBean.getContactPerson().getName());
            partyBean.setLastName(applicationBean.getContactPerson().getLastName());
            partyBean.setAddress(applicationBean.getContactPerson().getAddress());
            partyBean.setMobile(applicationBean.getContactPerson().getMobile());
            partyBean.setEmail(applicationBean.getContactPerson().getEmail());
            partyBean.setGenderType(applicationBean.getContactPerson().getGenderType());
            
            
            System.out.println("APPLICATION BEAN  CUSTOMIZE FORM "+applicationBean.getContactPerson().getFullName());
             System.out.println("PARTY BEAN   CUSTOMIZE FORM "+partyBean.getFullName());
            
            
            this.txtFirstName.setText(applicationBean.getContactPerson().getName());
            this.txtLastName.setText(applicationBean.getContactPerson().getLastName());
            this.txtAddress.setText(applicationBean.getContactPerson().getAddress().getDescription());
            this.txtPhone.setText(applicationBean.getContactPerson().getPhone());
            this.txtEmail.setText(applicationBean.getContactPerson().getEmail());
            this.cbxGender.setSelectedItem(applicationBean.getContactPerson().getGenderType());
            this.txtAddress.setEditable(false);
            this.txtAddress.setEnabled(false);
            this.txtPhone.setEditable(false);
            this.txtPhone.setEnabled(false);
            this.txtEmail.setEditable(false);
            this.txtEmail.setEnabled(false);
            this.txtFirstName.setEditable(false);
            this.txtFirstName.setEnabled(false);
            this.txtLastName.setEditable(false);
            this.txtLastName.setEnabled(false);
            this.cbxGender.setEditable(false);
            this.cbxGender.setEnabled(false);
        }
        
        
        
//        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
//            btnSave.setText(MessageUtility.getLocalizedMessage(
//                    ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
//        }
//        if (rrrAction == RrrBean.RRR_ACTION.CANCEL) {
//            btnSave.setText(MessageUtility.getLocalizedMessage(
//                    ClientMessage.GENERAL_LABELS_TERMINATE_AND_CLOSE).getMessage());
//        }
//
//        if (rrrAction != RrrBean.RRR_ACTION.EDIT && rrrAction != RrrBean.RRR_ACTION.VIEW
//                && appService != null) {
//            // Set default noation text from the selected application service
//            txtNotationText.setText(appService.getRequestType().getNotationTemplate());
//        }
//
//        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
//            btnSave.setEnabled(false);
//            txtNotationText.setEnabled(false);
//            txtRegDatetime.setEditable(false);
//            btnRegDate.setEnabled(false);
//            txtNotationText.setEditable(false);
//            cbxIsPrimary.setEnabled(true);
//        }
    }

    
      /** set the contact person's role to notifiablePerson
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
//        TODO VERIFY partyBean
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

        popUpOwners = new javax.swing.JPopupMenu();
        menuAddOwner = new javax.swing.JMenuItem();
        menuEditOwner = new javax.swing.JMenuItem();
        menuRemoveOwner = new javax.swing.JMenuItem();
        menuViewOwner = new javax.swing.JMenuItem();
        genderTypeListBean = new org.sola.clients.beans.referencedata.GenderTypeListBean();
        partyMemberBean = new org.sola.clients.beans.party.PartyMemberBean();
        groupPartyBean = new org.sola.clients.beans.party.GroupPartyBean();
        groupPartyTypeListBean1 = new org.sola.clients.beans.referencedata.GroupPartyTypeListBean();
        partyBeanforGroup = new org.sola.clients.beans.party.PartyBean();
        popupParties = new javax.swing.JPopupMenu();
        menuView = new javax.swing.JMenuItem();
        menuSelect = new javax.swing.JMenuItem();
        menuAdd = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenuItem();
        menuRemove = new javax.swing.JMenuItem();
        partySearchParams = new org.sola.clients.beans.party.PartySearchParamsBean();
        partySearchResuls = new org.sola.clients.beans.party.PartyPropertySearchResultListBean();
        applicationPropertyBean = new org.sola.clients.beans.application.ApplicationPropertyBean();
        applicationBean = CreateApplicationBean();
        partyBean = CreatePartyBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        pnlSearch = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnView = new javax.swing.JButton();
        btnSelect = new javax.swing.JButton();
        separator1 = new javax.swing.JToolBar.Separator();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel5 = new javax.swing.JLabel();
        lblSearchResultNumber = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableSearchResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel17 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        groupPanel4 = new org.sola.clients.swing.ui.GroupPanel();
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
        groupChoicePanel = new javax.swing.JPanel();
        cbxGroupType = new javax.swing.JComboBox();
        txtGroup = new javax.swing.JTextField();
        labGroupType = new javax.swing.JLabel();
        labGroupName = new javax.swing.JLabel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel3 = new javax.swing.JPanel();
        groupPanel3 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnViewPaperTitle = new javax.swing.JButton();
        btnLinkPaperTitle = new javax.swing.JButton();
        docTableScrollPanel = new javax.swing.JScrollPane();
        documentsPanel1 = createDocumentsPanel(this.applicationBean.getContactPerson());

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

        menuView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuView.setText(bundle.getString("RecordPersonRelationshipPanel.menuView.text")); // NOI18N
        menuView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewActionPerformed(evt);
            }
        });
        popupParties.add(menuView);

        menuSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        menuSelect.setText(bundle.getString("RecordPersonRelationshipPanel.menuSelect.text")); // NOI18N
        menuSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSelectActionPerformed(evt);
            }
        });
        popupParties.add(menuSelect);

        menuAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAdd.setText(bundle.getString("RecordPersonRelationshipPanel.menuAdd.text")); // NOI18N
        menuAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddActionPerformed(evt);
            }
        });
        popupParties.add(menuAdd);

        menuEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEdit.setText(bundle.getString("RecordPersonRelationshipPanel.menuEdit.text")); // NOI18N
        menuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditActionPerformed(evt);
            }
        });
        popupParties.add(menuEdit);

        menuRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemove.setText(bundle.getString("RecordPersonRelationshipPanel.menuRemove.text")); // NOI18N
        menuRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveActionPerformed(evt);
            }
        });
        popupParties.add(menuRemove);

        setHeaderPanel(headerPanel);
        setPreferredSize(new java.awt.Dimension(679, 800));

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

        jPanel1.setPreferredSize(new java.awt.Dimension(659, 800));

        jLabel4.setText(bundle.getString("RecordPersonRelationshipPanel.jLabel4.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchParams, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        pnlSearch.setMinimumSize(new java.awt.Dimension(300, 300));

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

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
        jToolBar2.add(separator1);
        jToolBar2.add(jSeparator2);

        jLabel5.setText(bundle.getString("RecordPersonRelationshipPanel.jLabel5.text")); // NOI18N
        jToolBar2.add(jLabel5);

        lblSearchResultNumber.setFont(LafManager.getInstance().getLabFontBold());
        lblSearchResultNumber.setText(bundle.getString("RecordPersonRelationshipPanel.lblSearchResultNumber.text")); // NOI18N
        jToolBar2.add(lblSearchResultNumber);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partySearchResults}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchResuls, eLProperty, tableSearchResults);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fullName}"));
        columnBinding.setColumnName("Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${properties}"));
        columnBinding.setColumnName("Properties");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchResuls, org.jdesktop.beansbinding.ELProperty.create("${selectedPartySearchResult}"), tableSearchResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableSearchResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableSearchResultsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableSearchResults);
        tableSearchResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("RecordPersonRelationshipPanel.tableSearchResults.columnModel.title0")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("RecordPersonRelationshipPanel.tableSearchResults.columnModel.title1")); // NOI18N

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 1315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap())
            .addComponent(txtName)
            .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 295, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel6.setText(bundle.getString("RecordPersonRelationshipPanel.jLabel6.text")); // NOI18N

        btnSearch.setText(bundle.getString("RecordPersonRelationshipPanel.btnSearch.text")); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnClear.setText(bundle.getString("RecordPersonRelationshipPanel.btnClear.text")); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(btnClear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        groupPanel4.setTitleText(bundle.getString("RecordPersonRelationshipPanel.groupPanel4.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(groupPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(groupPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtFirstName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

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
                .addContainerGap(383, Short.MAX_VALUE))
            .addComponent(txtFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
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
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(labLastName)
                .addContainerGap(434, Short.MAX_VALUE))
            .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
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
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addContainerGap(444, Short.MAX_VALUE))
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

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${genderTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, genderTypeListBean, eLProperty, cbxGender);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBean, org.jdesktop.beansbinding.ELProperty.create("${genderType}"), cbxGender, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(lblGender, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 685, Short.MAX_VALUE))
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
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addContainerGap(719, Short.MAX_VALUE))
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
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(609, Short.MAX_VALUE))
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

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${groupPartyTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, groupPartyTypeListBean1, eLProperty, cbxGroupType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, groupPartyTypeListBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedGroupPartyType}"), cbxGroupType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyBeanforGroup, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtGroup, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labGroupType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labGroupType.setText(bundle.getString("RecordPersonRelationshipPanel.labGroupType.text")); // NOI18N

        labGroupName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labGroupName.setText(bundle.getString("RecordPersonRelationshipPanel.labGroupName.text")); // NOI18N

        groupPanel2.setTitleText(bundle.getString("RecordPersonRelationshipPanel.groupPanel2.titleText")); // NOI18N

        javax.swing.GroupLayout groupChoicePanelLayout = new javax.swing.GroupLayout(groupChoicePanel);
        groupChoicePanel.setLayout(groupChoicePanelLayout);
        groupChoicePanelLayout.setHorizontalGroup(
            groupChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupChoicePanelLayout.createSequentialGroup()
                .addGroup(groupChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxGroupType, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labGroupType, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(groupChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(groupChoicePanelLayout.createSequentialGroup()
                        .addComponent(labGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(txtGroup)))
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        groupChoicePanelLayout.setVerticalGroup(
            groupChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupChoicePanelLayout.createSequentialGroup()
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(groupChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labGroupType)
                    .addComponent(labGroupName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxGroupType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(groupChoicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(groupChoicePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );

        groupPanel1.setTitleText(bundle.getString("RecordPersonRelationshipPanel.groupPanel1.titleText")); // NOI18N

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
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(groupPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addComponent(docTableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1339, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(groupPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(docTableScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(227, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1513, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void menuAddOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddOwnerActionPerformed
//        addOwner();
    }//GEN-LAST:event_menuAddOwnerActionPerformed

    private void menuEditOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditOwnerActionPerformed
//        editOwner();
    }//GEN-LAST:event_menuEditOwnerActionPerformed

    private void menuRemoveOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveOwnerActionPerformed
//        removeOwner();
    }//GEN-LAST:event_menuRemoveOwnerActionPerformed

    private void menuViewOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewOwnerActionPerformed
//        viewOwner();
    }//GEN-LAST:event_menuViewOwnerActionPerformed

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
                System.out.println("PROPERTY CHANGE GET NEW VALUE  "+e.getNewValue());
                if (e.getPropertyName().equals(AddDocumentForm.SELECTED_SOURCE)
                        && e.getNewValue() != null) {
                    document = (SourceBean) e.getNewValue();
                    System.out.println("QUI DOCUMENT IN PARTY  ");
                    System.out.println(""+document.getId());
//                    applicationBean.getContactPerson().createPaperTitle(document);
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
     private void firePartyEvent(String propertyName) {
        if (partySearchResuls.getSelectedPartySearchResult() != null) {
            firePropertyChange(propertyName, null,
                    PartyBean.getParty(partySearchResuls.getSelectedPartySearchResult().getId()));
        }
    }
       public void clickFind() {
        search();
    }
       
       
    private boolean savePersonRelationship() {
     
        if (this.partySearchResuls.getSelectedPartySearchResult()== null) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
                    new Object[]{bundle.getString("RecordPersonRelationshipPanel.groupPanel4.titleText")});
            return false;
        }
        
        if (this.txtGroup.getText().contentEquals("")||
            groupPartyTypeListBean1.getSelectedGroupPartyType().equals("")) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS,
                    new Object[]{bundle.getString("RecordPersonRelationshipPanel.groupPanel2.titleText")});
            return false;
        }
        
       for (Iterator<ApplicationPropertyBean> it = applicationBean.getPropertyList().iterator(); it.hasNext();) {
            ApplicationPropertyBean appProperty = it.next();
                if ((appProperty.getNameFirstpart()+appProperty.getNameLastpart()).equals(applicationPropertyBean.getNameFirstpart()+applicationPropertyBean.getNameLastpart())) {
                      MessageUtility.displayMessage(ClientMessage.APPLICATION_PROPERTY_ALREADY_SELECTED);
//                    break;
                    return false;
                }
       } 
               
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
            if (!isContactPerson){
                partyBean.setTypeCode("naturalPerson");
                setApplicantRole();
                partyBean.saveParty();
            }
            
                partyBeanforGroup.setTypeCode("naturalPerson");
                partyBeanforGroup.setGenderCode("na");
                partyBeanforGroup.saveParty();

                groupPartyBean.setId(partyBeanforGroup.getId());  
                groupPartyBean.setGroupCode(groupPartyTypeListBean1.getSelectedGroupPartyType().getCode());
                groupPartyBean.saveGroupParty();

                partyMemberBean.setGroupId(groupPartyBean.getId());
                partyMemberBean.setPartyId(partyBean.getId());
                partyMemberBean.savePartyMember();

                partyMemberTargetBean.setPartyId(partyTargetSummary.getId());
                partyMemberTargetBean.setGroupId(groupPartyBean.getId());
                partyMemberTargetBean.savePartyMember();         

                applicationBean.addProperty(applicationPropertyBean.getNameFirstpart(), applicationPropertyBean.getNameLastpart(),null,null);
                applicationBean.getContactPerson().setTypeCode("naturalPerson");
                applicationBean.saveApplication();
                
                
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
                partySearchResuls.search(partySearchParams);
                return null;
            }

            @Override
            public void taskDone() {
                lblSearchResultNumber.setText(Integer.toString(partySearchResuls.getPartySearchResults().size()));
                if (partySearchResuls.getPartySearchResults().size() > 100) {
                    MessageUtility.displayMessage(ClientMessage.SEARCH_TOO_MANY_RESULTS, new String[]{"100"});
                } else if (partySearchResuls.getPartySearchResults().size() < 1) {
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
            applicationPropertyBean.setBaUnitId (this.partySearchResuls.getSelectedPartySearchResult().getPropertyId());
            applicationPropertyBean.setNameFirstpart(this.partySearchResuls.getSelectedPartySearchResult().getNameFirstPart());
            applicationPropertyBean.setNameLastpart(this.partySearchResuls.getSelectedPartySearchResult().getNameLastPart());
            
            
            this.txtName.setEditable(false);        
            this.txtName.setEnabled(false);        
        } 
    }

    private void viewParty() {
        firePartyEvent(VIEW_PARTY_PROPERTY);
    }
     private void addParty() {
        firePropertyChange(CREATE_NEW_PARTY_PROPERTY, false, true);
    }

    private void editParty() {
        firePartyEvent(EDIT_PARTY_PROPERTY);
    }

    private void removeParty() {
        if (partySearchResuls.getSelectedPartySearchResult() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            firePropertyChange(REMOVE_PARTY_PROPERTY, false, true);
            PartyBean.remove(partySearchResuls.getSelectedPartySearchResult().getId());
            search();
        }
    }
    
     private void clearForm() {
//        cbxPartyTypes.setSelectedIndex(-1);
//        cbxRoles.setSelectedIndex(-1);
        txtName.setText(null);
        txtName.setEditable(true);        
        txtName.setEnabled(true); 
        partyTargetSummary = new PartySummaryBean();
    }
    
    private void btnViewPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewPaperTitleActionPerformed
        viewDocument();
    }//GEN-LAST:event_btnViewPaperTitleActionPerformed

    private void btnLinkPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLinkPaperTitleActionPerformed
        linkDocument();
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

    private void menuViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewActionPerformed
        viewParty();
    }//GEN-LAST:event_menuViewActionPerformed

    private void menuSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSelectActionPerformed
        selectParty();
    }//GEN-LAST:event_menuSelectActionPerformed

    private void menuAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddActionPerformed
        addParty();
    }//GEN-LAST:event_menuAddActionPerformed

    private void menuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditActionPerformed
        editParty();
    }//GEN-LAST:event_menuEditActionPerformed

    private void menuRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveActionPerformed
        removeParty();
    }//GEN-LAST:event_menuRemoveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationBean applicationBean;
    private org.sola.clients.beans.application.ApplicationPropertyBean applicationPropertyBean;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnLinkPaperTitle;
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
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.GroupPanel groupPanel3;
    private org.sola.clients.swing.ui.GroupPanel groupPanel4;
    private org.sola.clients.beans.party.GroupPartyBean groupPartyBean;
    private org.sola.clients.beans.referencedata.GroupPartyTypeListBean groupPartyTypeListBean1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
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
    private javax.swing.JLabel lblStatus;
    private javax.swing.JMenuItem menuAdd;
    private javax.swing.JMenuItem menuAddOwner;
    private javax.swing.JMenuItem menuEdit;
    private javax.swing.JMenuItem menuEditOwner;
    private javax.swing.JMenuItem menuRemove;
    private javax.swing.JMenuItem menuRemoveOwner;
    private javax.swing.JMenuItem menuSelect;
    private javax.swing.JMenuItem menuView;
    private javax.swing.JMenuItem menuViewOwner;
    private org.sola.clients.beans.party.PartyBean partyBean;
    private org.sola.clients.beans.party.PartyBean partyBeanforGroup;
    private org.sola.clients.beans.party.PartyMemberBean partyMemberBean;
    private org.sola.clients.beans.party.PartySearchParamsBean partySearchParams;
    private org.sola.clients.beans.party.PartyPropertySearchResultListBean partySearchResuls;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPopupMenu popUpOwners;
    private javax.swing.JPopupMenu popupParties;
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
