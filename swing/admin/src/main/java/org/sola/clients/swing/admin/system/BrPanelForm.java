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

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import javax.swing.JFormattedTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.sola.clients.beans.referencedata.ApplicationActionTypeListBean;
import org.sola.clients.beans.referencedata.BrSeverityTypeListBean;
import org.sola.clients.beans.referencedata.BrTechnicalTypeListBean;
import org.sola.clients.beans.referencedata.BrValidationTargetTypeListBean;
import org.sola.clients.beans.referencedata.RegistrationStatusTypeListBean;
import org.sola.clients.beans.referencedata.RequestTypeListBean;
import org.sola.clients.beans.referencedata.RrrTypeListBean;
import org.sola.clients.beans.referencedata.ServiceActionTypeListBean;
import org.sola.clients.beans.security.RoleBean;
import org.sola.clients.beans.system.BrBean;
import org.sola.clients.beans.system.BrDefinitionBean;
import org.sola.clients.beans.system.BrValidationBean;
import org.sola.clients.swing.admin.MainForm;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.utils.BindingTools;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.HeaderPanel;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to create or change Business rule.
 */
public class BrPanelForm extends ContentPanel {

    public static final String BR_SAVED_PROPERTY = "BrSaved";
    private static final String CARD_BR = "cardBr";
    private static final String CARD_BR_DEFINITION = "cardBrDefinition";
    private static final String CARD_BR_VALIDATION = "cardBrValidation";
    private BrDefinitionBean brDefinition;
    private BrValidationBean brValidation;
    private ResourceBundle resourceBundle;
    private boolean closeOnSave;
    private boolean saveOnAction;
    private BrBean br;
    public String cardDisplayed = CARD_BR;

    /** Default constructor */
    public BrPanelForm() {
        initComponents();
    }
   @Override
    protected boolean panelClosing() {
       if (cardDisplayed == CARD_BR)  { 
        if (MainForm.checkSaveBeforeClose(br)) {
            save(true);
            return false;
        }
       } 
        
       if (cardDisplayed == CARD_BR_DEFINITION)  { 
        if (MainForm.checkSaveBeforeClose(brDefinition)) {
            saveDefinition();
            return false;
        }
       } 
       if (cardDisplayed == CARD_BR_VALIDATION)  { 
        if (MainForm.checkSaveBeforeClose(brValidation)) {
            saveValidation();
            return false;
        }
       } 
        return true;
    }
    /** 
     * Form constructor. 
     * @param roleBean The role bean instance to show on the panel.
     * @param saveOnAction If <code>true</code>, role will be saved into database. 
     * If <code>false</code>, role will be validated and validation result returned as a value of 
     * {@link RolePanelForm.ROLE_SAVED_PROPERTY} property change event.
     * @param closeOnSave Indicates whether to close the form upon save action takes place.
     * @param readOnly Indicates whether to display provided {@link RoleBean} in read only mode or not.
     */
    public BrPanelForm(BrBean br, boolean saveOnAction, boolean closeOnSave) {
        this.br = br;
        this.saveOnAction = saveOnAction;
        this.closeOnSave = closeOnSave;
        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/system/Bundle");

        initComponents();
        
        PropertyChangeListener headerPanelListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(HeaderPanel.CLOSE_BUTTON_CLICKED)) {
                    if (panelClosing()) {
                        showPanel(CARD_BR);
                    }
                }
            }
        };
         
        headerPanelBrDefinition.addPropertyChangeListener(headerPanelListener);
        headerPanelBrValidation.addPropertyChangeListener(headerPanelListener);

        tableDefinitions.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                customizeBrDefinitionFields();
            }
        });

        tableValidations.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                customizeBrValidationFields();
            }
        });

        customizeBrDefinitionFields();
        customizeBrValidationFields();
        
        setupBrBean(this.br);
    }

    private BrTechnicalTypeListBean createBrTechnicalTypes() {
        if (brTechnicalTypes == null) {
            brTechnicalTypes = new BrTechnicalTypeListBean(true);
        }
        return brTechnicalTypes;
    }

    private BrValidationTargetTypeListBean createBrValidationTargets() {
        if (brValidationTargetTypes == null) {
            brValidationTargetTypes = new BrValidationTargetTypeListBean(true);
        }
        return brValidationTargetTypes;
    }

    private BrSeverityTypeListBean createBrSeverityTypes() {
        if (brSeverityTypes == null) {
            brSeverityTypes = new BrSeverityTypeListBean(true);
        }
        return brSeverityTypes;
    }

    private ApplicationActionTypeListBean createAppActionTypes() {
        if (applicationActionTypes == null) {
            applicationActionTypes = new ApplicationActionTypeListBean(true);
        }
        return applicationActionTypes;
    }

    private ServiceActionTypeListBean createServiceActionTypes() {
        if (serviceActionTypes == null) {
            serviceActionTypes = new ServiceActionTypeListBean(true);
        }
        return serviceActionTypes;
    }

    private RequestTypeListBean createRequestTypes() {
        if (requestTypes == null) {
            requestTypes = new RequestTypeListBean(true);
        }
        return requestTypes;
    }

    private RrrTypeListBean createRrrTypes() {
        if (rrrTypes == null) {
            rrrTypes = new RrrTypeListBean(true);
        }
        return rrrTypes;
    }

    private RegistrationStatusTypeListBean createRegistrationStatusTypes() {
        if (registrationStatusTypes == null) {
            registrationStatusTypes = new RegistrationStatusTypeListBean(true);
        }
        return registrationStatusTypes;
    }

    public BrBean getBr() {
        if (br == null) {
            br = new BrBean();
            firePropertyChange("br", null, this.br);
        }
        return br;
    }

    public void setBr(BrBean br) {
        setupBrBean(br);
    }

    public BrDefinitionBean getBrDefinition() {
        if (brDefinition == null) {
            brDefinition = new BrDefinitionBean();
            firePropertyChange("brDefinition", null, this.brDefinition);
        }
        return brDefinition;
    }

    public void setBrDefinition(BrDefinitionBean brDefinition) {
        this.brDefinition = brDefinition;
        firePropertyChange("brDefinition", null, this.brDefinition);
    }

    public BrValidationBean getBrValidation() {
        if (brValidation == null) {
            brValidation = new BrValidationBean();
            firePropertyChange("brValidation", null, this.brValidation);
        }
        return brValidation;
    }

    public void setBrValidation(BrValidationBean brValidation) {
        this.brValidation = brValidation;
        firePropertyChange("brValidation", null, this.brValidation);
    }

    public boolean isCloseOnSave() {
        return closeOnSave;
    }

    public void setCloseOnSave(boolean closeOnSave) {
        this.closeOnSave = closeOnSave;
        customizePanel();
    }

    /** Shows given panel/card. */
    private void showPanel(String cardName) {
        ((CardLayout) pnlCards.getLayout()).show(pnlCards, cardName);
        this.cardDisplayed = cardName;
    }

    /** Enables or disables BR definition fields. */
    private void customizeBrDefinitionFields() {
        boolean enabled = getBr().getSelectedBrDefinition() != null;
        btnRemoveDefinition.setEnabled(enabled);
        btnEditDefinition.setEnabled(enabled);
        menuEditDefinition.setEnabled(btnEditDefinition.isEnabled());
        menuRemoveDefinition.setEnabled(btnRemoveDefinition.isEnabled());
    }

    /** Enables or disables BR validation fields. */
    private void customizeBrValidationFields() {
        boolean enabled = getBr().getSelectedBrValidation() != null;
        btnRemoveValidation.setEnabled(enabled);
        btnEditValidation.setEnabled(enabled);
        menuRemoveValidation.setEnabled(btnRemoveValidation.isEnabled());
        menuEditValidation.setEnabled(btnEditValidation.isEnabled());
    }

    private void customizePanel() {
        if (getBr().getDisplayName() != null) {
            headerPanel.setTitleText(String.format(resourceBundle.getString("BrPanelForm.headerPanel.titleText"), getBr().getDisplayName()));
        } else {
            headerPanel.setTitleText(resourceBundle.getString("BrPanelForm.headerPanel.titleText.new"));
        }

        if (closeOnSave) {
            btnSaveBr.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
        } else {
            btnSaveBr.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE).getMessage());
        }
    }

    /** Setup BR bean object, used to bind data on the form. */
    private void setupBrBean(BrBean br) {
        showPanel(CARD_BR);
        tabsPanel.setSelectedIndex(0);
        clearValidationFields();
        clearDefinitionFields();

        if (br != null) {
            this.br = br;
        } else {
            this.br = new BrBean();
        }

        localizedFeedback.loadLocalizedValues(this.br.getFeedback());
        customizePanel();
        firePropertyChange("br", null, this.br);
        BindingTools.refreshBinding(bindingGroup, "BrDefinitions");
        BindingTools.refreshBinding(bindingGroup, "BrValidations");
    }

    /** Calls saving procedure of BR data object. */
    public boolean validate(boolean showMessage) {
        return getBr().validate(showMessage).size() < 1;
    }

    /** Calls saving procedure of BR data object. */
    public void save(boolean showMessage) {
        br.setFeedback(localizedFeedback.buildMultilingualString());
        boolean isSaved = false;
        if (validate(showMessage)) {
            if (saveOnAction) {
                br.save();
                MessageUtility.displayMessage(ClientMessage.ADMIN_BR_SAVED);
            }
            isSaved = true;
        }
        
        if (isSaved) {
            firePropertyChange(BR_SAVED_PROPERTY, false, true);
            if (closeOnSave) {
                close();
            } else {
                customizePanel();
            }
        }
    }

    /** Displays pop-up calendar. */
    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    /** Clears BR definition fields. */
    private void clearDefinitionFields() {
        tableDefinitions.clearSelection();
        txtActiveFrom.setValue(null);
        txtActiveUntil.setValue(null);
        txtBody.setText(null);
    }

    /** Clears BR validation fields. */
    private void clearValidationFields() {
        tableValidations.clearSelection();
        txtOrder.setValue(0);
        cbxApplicationActions.setSelectedIndex(0);
        cbxRegistrationTypes.setSelectedIndex(0);
        cbxRequestTypes.setSelectedIndex(0);
        cbxRrrTypes.setSelectedIndex(0);
        cbxServiceActions.setSelectedIndex(0);
        cbxSeverity.setSelectedIndex(0);
        cbxTargetTypes.setSelectedIndex(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        brTechnicalTypes = createBrTechnicalTypes();
        brValidationTargetTypes = createBrValidationTargets();
        brSeverityTypes = createBrSeverityTypes();
        applicationActionTypes = createAppActionTypes();
        serviceActionTypes = createServiceActionTypes();
        requestTypes = createRequestTypes();
        rrrTypes = createRrrTypes();
        registrationStatusTypes = createRegistrationStatusTypes();
        localizedFeedback = new org.sola.clients.beans.system.LocalizedValuesListBean();
        popupValidations = new javax.swing.JPopupMenu();
        menuAddValidation = new javax.swing.JMenuItem();
        menuEditValidation = new javax.swing.JMenuItem();
        menuRemoveValidation = new javax.swing.JMenuItem();
        popupDefinitions = new javax.swing.JPopupMenu();
        menuAddDefinition = new javax.swing.JMenuItem();
        menuEditDefinition = new javax.swing.JMenuItem();
        menuRemoveDefinition = new javax.swing.JMenuItem();
        pnlCards = new javax.swing.JPanel();
        pnlBr = new javax.swing.JPanel();
        tabsPanel = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txtDisplayName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cbxTechnicalType = new javax.swing.JComboBox();
        jPanel10 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtTechnicalDescription = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableDefinitions = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddDefinition = new javax.swing.JButton();
        btnEditDefinition = new javax.swing.JButton();
        btnRemoveDefinition = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnAddValidation = new javax.swing.JButton();
        btnEditValidation = new javax.swing.JButton();
        btnRemoveValidation = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableValidations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnSaveBr = new javax.swing.JButton();
        pnlBrValidation = new javax.swing.JPanel();
        pnlBrValidationFields = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        cbxTargetTypes = new javax.swing.JComboBox();
        jPanel13 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cbxSeverity = new javax.swing.JComboBox();
        jPanel14 = new javax.swing.JPanel();
        txtOrder = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        cbxApplicationActions = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        cbxServiceActions = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        cbxRequestTypes = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        cbxRrrTypes = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        cbxRegistrationTypes = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        headerPanelBrValidation = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnSaveBrValidation = new javax.swing.JButton();
        pnlBrDefinition = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtActiveFrom = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtBody = new javax.swing.JTextArea();
        btnActiveFrom = new javax.swing.JButton();
        txtActiveUntil = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        btnActiveUntil = new javax.swing.JButton();
        headerPanelBrDefinition = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar5 = new javax.swing.JToolBar();
        btnSaveBrDefinition = new javax.swing.JButton();

        popupValidations.setName("popupValidations"); // NOI18N

        menuAddValidation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/system/Bundle"); // NOI18N
        menuAddValidation.setText(bundle.getString("BrPanelForm.menuAddValidation.text")); // NOI18N
        menuAddValidation.setName("menuAddValidation"); // NOI18N
        menuAddValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddValidationActionPerformed(evt);
            }
        });
        popupValidations.add(menuAddValidation);

        menuEditValidation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditValidation.setText(bundle.getString("BrPanelForm.menuEditValidation.text")); // NOI18N
        menuEditValidation.setName("menuEditValidation"); // NOI18N
        menuEditValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditValidationActionPerformed(evt);
            }
        });
        popupValidations.add(menuEditValidation);

        menuRemoveValidation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveValidation.setText(bundle.getString("BrPanelForm.menuRemoveValidation.text")); // NOI18N
        menuRemoveValidation.setName("menuRemoveValidation"); // NOI18N
        menuRemoveValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveValidationActionPerformed(evt);
            }
        });
        popupValidations.add(menuRemoveValidation);

        popupDefinitions.setName("popupDefinitions"); // NOI18N

        menuAddDefinition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAddDefinition.setText(bundle.getString("BrPanelForm.menuAddDefinition.text")); // NOI18N
        menuAddDefinition.setName("menuAddDefinition"); // NOI18N
        menuAddDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddDefinitionActionPerformed(evt);
            }
        });
        popupDefinitions.add(menuAddDefinition);

        menuEditDefinition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditDefinition.setText(bundle.getString("BrPanelForm.menuEditDefinition.text")); // NOI18N
        menuEditDefinition.setName("menuEditDefinition"); // NOI18N
        menuEditDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditDefinitionActionPerformed(evt);
            }
        });
        popupDefinitions.add(menuEditDefinition);

        menuRemoveDefinition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveDefinition.setText(bundle.getString("BrPanelForm.menuRemoveDefinition.text")); // NOI18N
        menuRemoveDefinition.setName("menuRemoveDefinition"); // NOI18N
        menuRemoveDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveDefinitionActionPerformed(evt);
            }
        });
        popupDefinitions.add(menuRemoveDefinition);

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);

        pnlCards.setName("pnlCards"); // NOI18N
        pnlCards.setLayout(new java.awt.CardLayout());

        pnlBr.setName("pnlBr"); // NOI18N

        tabsPanel.setName("tabsPanel"); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel1.setName("jPanel1"); // NOI18N

        txtDisplayName.setName("txtDisplayName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${br.displayName}"), txtDisplayName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel1.setText(bundle.getString("BrPanelForm.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(200, Short.MAX_VALUE))
            .addComponent(txtDisplayName, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDisplayName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel5.setText(bundle.getString("BrPanelForm.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        cbxTechnicalType.setName("cbxTechnicalType"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${brTechnicalTypes}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brTechnicalTypes, eLProperty, cbxTechnicalType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${br.brTechnicalType}"), cbxTechnicalType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addContainerGap(194, Short.MAX_VALUE))
            .addComponent(cbxTechnicalType, 0, 281, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxTechnicalType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel2);

        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridLayout(2, 1, 0, 15));

        jPanel4.setName("jPanel4"); // NOI18N

        jLabel3.setText(bundle.getString("BrPanelForm.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jScrollPane2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDescription.setRows(2);
        txtDescription.setName("txtDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${br.description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(txtDescription);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(520, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel4);

        jPanel5.setName("jPanel5"); // NOI18N

        jLabel4.setText(bundle.getString("BrPanelForm.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane3.setName("jScrollPane3"); // NOI18N

        txtTechnicalDescription.setRows(2);
        txtTechnicalDescription.setTabSize(2);
        txtTechnicalDescription.setName("txtTechnicalDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${br.technicalDescription}"), txtTechnicalDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(txtTechnicalDescription);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap(474, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel5);

        jLabel2.setText(bundle.getString("BrPanelForm.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        jTableWithDefaultStyles1.setName("jTableWithDefaultStyles1"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${localizedValues}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, localizedFeedback, eLProperty, jTableWithDefaultStyles1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${language.displayValue}"));
        columnBinding.setColumnName("Language.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${localizedValue}"));
        columnBinding.setColumnName("Localized Value");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane7.setViewportView(jTableWithDefaultStyles1);
        jTableWithDefaultStyles1.getColumnModel().getColumn(0).setPreferredWidth(120);
        jTableWithDefaultStyles1.getColumnModel().getColumn(0).setMaxWidth(120);
        jTableWithDefaultStyles1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("BrPanelForm.jTableWithDefaultStyles1.columnModel.title0_2")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("BrPanelForm.jTableWithDefaultStyles1.columnModel.title1_2")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(1).setCellRenderer(new TableCellTextAreaRenderer());

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsPanel.addTab(bundle.getString("BrPanelForm.jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tableDefinitions.setComponentPopupMenu(popupDefinitions);
        tableDefinitions.setName("tableDefinitions"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${br.filteredBrDefinitionList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableDefinitions, "BrDefinitions");
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${activeFrom}"));
        columnBinding.setColumnName("Active From");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${activeUntil}"));
        columnBinding.setColumnName("Active Until");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${br.selectedBrDefinition}"), tableDefinitions, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(tableDefinitions);
        tableDefinitions.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("BrPanelForm.tableDefinitions.columnModel.title0_1")); // NOI18N
        tableDefinitions.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("BrPanelForm.tableDefinitions.columnModel.title1_1")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddDefinition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddDefinition.setText(bundle.getString("BrPanelForm.btnAddDefinition.text")); // NOI18N
        btnAddDefinition.setFocusable(false);
        btnAddDefinition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddDefinition.setName("btnAddDefinition"); // NOI18N
        btnAddDefinition.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDefinitionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddDefinition);

        btnEditDefinition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditDefinition.setText(bundle.getString("BrPanelForm.btnEditDefinition.text")); // NOI18N
        btnEditDefinition.setFocusable(false);
        btnEditDefinition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditDefinition.setName("btnEditDefinition"); // NOI18N
        btnEditDefinition.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditDefinitionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditDefinition);

        btnRemoveDefinition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveDefinition.setText(bundle.getString("BrPanelForm.btnRemoveDefinition.text")); // NOI18N
        btnRemoveDefinition.setFocusable(false);
        btnRemoveDefinition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveDefinition.setName("btnRemoveDefinition"); // NOI18N
        btnRemoveDefinition.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveDefinitionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveDefinition);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsPanel.addTab(bundle.getString("BrPanelForm.jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N

        jPanel21.setName("jPanel21"); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnAddValidation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddValidation.setText(bundle.getString("BrPanelForm.btnAddValidation.text")); // NOI18N
        btnAddValidation.setFocusable(false);
        btnAddValidation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddValidation.setName("btnAddValidation"); // NOI18N
        btnAddValidation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddValidationActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAddValidation);

        btnEditValidation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditValidation.setText(bundle.getString("BrPanelForm.btnEditValidation.text")); // NOI18N
        btnEditValidation.setFocusable(false);
        btnEditValidation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditValidation.setName("btnEditValidation"); // NOI18N
        btnEditValidation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditValidationActionPerformed(evt);
            }
        });
        jToolBar2.add(btnEditValidation);

        btnRemoveValidation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveValidation.setText(bundle.getString("BrPanelForm.btnRemoveValidation.text")); // NOI18N
        btnRemoveValidation.setFocusable(false);
        btnRemoveValidation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveValidation.setName("btnRemoveValidation"); // NOI18N
        btnRemoveValidation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveValidationActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemoveValidation);

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tableValidations.setComponentPopupMenu(popupValidations);
        tableValidations.setName("tableValidations"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${br.filteredBrValidationList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableValidations, "BrValidations");
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${brValidationTargetType.displayValue}"));
        columnBinding.setColumnName("Br Validation Target Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${brSeverityType.displayValue}"));
        columnBinding.setColumnName("Br Severity Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${br.selectedBrValidation}"), tableValidations, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane6.setViewportView(tableValidations);
        tableValidations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("BrPanelForm.tableValidations.columnModel.title0_1")); // NOI18N
        tableValidations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("BrPanelForm.tableValidations.columnModel.title1")); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsPanel.addTab(bundle.getString("BrPanelForm.jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("BrPanelForm.headerPanel.titleText")); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        btnSaveBr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSaveBr.setText(bundle.getString("BrPanelForm.btnSaveBr.text")); // NOI18N
        btnSaveBr.setName("btnSaveBr"); // NOI18N
        btnSaveBr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBrActionPerformed(evt);
            }
        });
        jToolBar3.add(btnSaveBr);

        javax.swing.GroupLayout pnlBrLayout = new javax.swing.GroupLayout(pnlBr);
        pnlBr.setLayout(pnlBrLayout);
        pnlBrLayout.setHorizontalGroup(
            pnlBrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlBrLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsPanel)
                .addContainerGap())
        );
        pnlBrLayout.setVerticalGroup(
            pnlBrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBrLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabsPanel)
                .addContainerGap())
        );

        pnlCards.add(pnlBr, "cardBr");

        pnlBrValidation.setName("pnlBrValidation"); // NOI18N

        pnlBrValidationFields.setName("pnlBrValidationFields"); // NOI18N
        pnlBrValidationFields.setLayout(new java.awt.GridLayout(3, 3, 15, 0));

        jPanel12.setName("jPanel12"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel11.setText(bundle.getString("BrPanelForm.jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        cbxTargetTypes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxTargetTypes.setName("cbxTargetTypes"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${brValidationTargetTypes}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brValidationTargetTypes, eLProperty, cbxTargetTypes);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.brValidationTargetType}"), cbxTargetTypes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addContainerGap(106, Short.MAX_VALUE))
            .addComponent(cbxTargetTypes, 0, 190, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxTargetTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel12);

        jPanel13.setName("jPanel13"); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel12.setText(bundle.getString("BrPanelForm.jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        cbxSeverity.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxSeverity.setName("cbxSeverity"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${brSeverityTypes}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, brSeverityTypes, eLProperty, cbxSeverity);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.brSeverityType}"), cbxSeverity, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addContainerGap(128, Short.MAX_VALUE))
            .addComponent(cbxSeverity, 0, 190, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxSeverity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel13);

        jPanel14.setName("jPanel14"); // NOI18N

        txtOrder.setFormatterFactory(FormattersFactory.getInstance().getIntegerFormatterFactory());
        txtOrder.setText(bundle.getString("BrPanelForm.txtOrder.text")); // NOI18N
        txtOrder.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtOrder.setName("txtOrder"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.orderOfExecution}"), txtOrder, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel13.setText(bundle.getString("BrPanelForm.jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addContainerGap(141, Short.MAX_VALUE))
            .addComponent(txtOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel14);

        jPanel18.setName("jPanel18"); // NOI18N

        cbxApplicationActions.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxApplicationActions.setName("cbxApplicationActions"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${applicationActionTypes}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, applicationActionTypes, eLProperty, cbxApplicationActions);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.applicationActionType}"), cbxApplicationActions, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText(bundle.getString("BrPanelForm.jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addContainerGap(77, Short.MAX_VALUE))
            .addComponent(cbxApplicationActions, 0, 190, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxApplicationActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel18);

        jPanel16.setName("jPanel16"); // NOI18N

        cbxServiceActions.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxServiceActions.setName("cbxServiceActions"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceActionTypes}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, serviceActionTypes, eLProperty, cbxServiceActions);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.serviceActionType}"), cbxServiceActions, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText(bundle.getString("BrPanelForm.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addContainerGap(97, Short.MAX_VALUE))
            .addComponent(cbxServiceActions, 0, 190, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxServiceActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel16);

        jPanel19.setName("jPanel19"); // NOI18N

        cbxRequestTypes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxRequestTypes.setName("cbxRequestTypes"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${requestTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, requestTypes, eLProperty, cbxRequestTypes);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.requestType}"), cbxRequestTypes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText(bundle.getString("BrPanelForm.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addContainerGap(112, Short.MAX_VALUE))
            .addComponent(cbxRequestTypes, 0, 190, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRequestTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel19);

        jPanel17.setName("jPanel17"); // NOI18N

        cbxRrrTypes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxRrrTypes.setName("cbxRrrTypes"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrTypeBeanList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrTypes, eLProperty, cbxRrrTypes);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.rrrType}"), cbxRrrTypes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText(bundle.getString("BrPanelForm.jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addContainerGap(142, Short.MAX_VALUE))
            .addComponent(cbxRrrTypes, 0, 190, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRrrTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel17);

        jPanel15.setName("jPanel15"); // NOI18N

        cbxRegistrationTypes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxRegistrationTypes.setName("cbxRegistrationTypes"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${registrationStatusTypes}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, registrationStatusTypes, eLProperty, cbxRegistrationTypes);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brValidation.registrationStatusType}"), cbxRegistrationTypes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText(bundle.getString("BrPanelForm.jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addContainerGap(72, Short.MAX_VALUE))
            .addComponent(cbxRegistrationTypes, 0, 190, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRegistrationTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBrValidationFields.add(jPanel15);

        headerPanelBrValidation.setName("headerPanelBrValidation"); // NOI18N
        headerPanelBrValidation.setTitleText(bundle.getString("BrPanelForm.headerPanelBrValidation.titleText")); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.setName("jToolBar4"); // NOI18N

        btnSaveBrValidation.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSaveBrValidation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSaveBrValidation.setText(bundle.getString("BrPanelForm.btnSaveBrValidation.text")); // NOI18N
        btnSaveBrValidation.setName("btnSaveBrValidation"); // NOI18N
        btnSaveBrValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBrValidationActionPerformed(evt);
            }
        });
        jToolBar4.add(btnSaveBrValidation);

        javax.swing.GroupLayout pnlBrValidationLayout = new javax.swing.GroupLayout(pnlBrValidation);
        pnlBrValidation.setLayout(pnlBrValidationLayout);
        pnlBrValidationLayout.setHorizontalGroup(
            pnlBrValidationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanelBrValidation, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
            .addGroup(pnlBrValidationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlBrValidationFields, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlBrValidationLayout.setVerticalGroup(
            pnlBrValidationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrValidationLayout.createSequentialGroup()
                .addComponent(headerPanelBrValidation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlBrValidationFields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(205, Short.MAX_VALUE))
        );

        pnlCards.add(pnlBrValidation, "cardBrValidation");

        pnlBrDefinition.setName("pnlBrDefinition"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel6.setText(bundle.getString("BrPanelForm.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtActiveFrom.setEditable(false);
        txtActiveFrom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtActiveFrom.setText(bundle.getString("BrPanelForm.txtActiveFrom.text")); // NOI18N
        txtActiveFrom.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtActiveFrom.setName("txtActiveFrom"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brDefinition.activeFrom}"), txtActiveFrom, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel8.setText(bundle.getString("BrPanelForm.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        txtBody.setColumns(20);
        txtBody.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtBody.setRows(5);
        txtBody.setName("txtBody"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brDefinition.body}"), txtBody, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane5.setViewportView(txtBody);

        btnActiveFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnActiveFrom.setText(bundle.getString("BrPanelForm.btnActiveFrom.text")); // NOI18N
        btnActiveFrom.setBorder(null);
        btnActiveFrom.setBorderPainted(false);
        btnActiveFrom.setName("btnActiveFrom"); // NOI18N
        btnActiveFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActiveFromActionPerformed(evt);
            }
        });

        txtActiveUntil.setEditable(false);
        txtActiveUntil.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtActiveUntil.setText(bundle.getString("BrPanelForm.txtActiveUntil.text")); // NOI18N
        txtActiveUntil.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtActiveUntil.setName("txtActiveUntil"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${brDefinition.activeUntil}"), txtActiveUntil, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel7.setText(bundle.getString("BrPanelForm.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        btnActiveUntil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnActiveUntil.setText(bundle.getString("BrPanelForm.btnActiveUntil.text")); // NOI18N
        btnActiveUntil.setBorder(null);
        btnActiveUntil.setBorderPainted(false);
        btnActiveUntil.setName("btnActiveUntil"); // NOI18N
        btnActiveUntil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActiveUntilActionPerformed(evt);
            }
        });

        headerPanelBrDefinition.setName("headerPanelBrDefinition"); // NOI18N
        headerPanelBrDefinition.setTitleText(bundle.getString("BrPanelForm.headerPanelBrDefinition.titleText")); // NOI18N

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.setName("jToolBar5"); // NOI18N

        btnSaveBrDefinition.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSaveBrDefinition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSaveBrDefinition.setText(bundle.getString("BrPanelForm.btnSaveBrDefinition.text")); // NOI18N
        btnSaveBrDefinition.setName("btnSaveBrDefinition"); // NOI18N
        btnSaveBrDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBrDefinitionActionPerformed(evt);
            }
        });
        jToolBar5.add(btnSaveBrDefinition);

        javax.swing.GroupLayout pnlBrDefinitionLayout = new javax.swing.GroupLayout(pnlBrDefinition);
        pnlBrDefinition.setLayout(pnlBrDefinitionLayout);
        pnlBrDefinitionLayout.setHorizontalGroup(
            pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanelBrDefinition, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
            .addComponent(jToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
            .addGroup(pnlBrDefinitionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addGroup(pnlBrDefinitionLayout.createSequentialGroup()
                        .addGroup(pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txtActiveFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActiveFrom)
                        .addGap(18, 18, 18)
                        .addGroup(pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlBrDefinitionLayout.createSequentialGroup()
                                .addComponent(txtActiveUntil, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnActiveUntil))
                            .addComponent(jLabel7)))
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        pnlBrDefinitionLayout.setVerticalGroup(
            pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrDefinitionLayout.createSequentialGroup()
                .addComponent(headerPanelBrDefinition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBrDefinitionLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlBrDefinitionLayout.createSequentialGroup()
                                .addComponent(txtActiveFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8))
                            .addComponent(btnActiveFrom)))
                    .addGroup(pnlBrDefinitionLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlBrDefinitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtActiveUntil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnActiveUntil))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlCards.add(pnlBrDefinition, "cardBrDefinition");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnActiveFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActiveFromActionPerformed
        showCalendar(txtActiveFrom);
    }//GEN-LAST:event_btnActiveFromActionPerformed

    private void btnActiveUntilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActiveUntilActionPerformed
        showCalendar(txtActiveUntil);
    }

    private void addDefinition() {
        setBrDefinition(new BrDefinitionBean());
        getBrDefinition().setBrId(getBr().getId());
        showPanel(CARD_BR_DEFINITION);
    }//GEN-LAST:event_btnActiveUntilActionPerformed

    private void btnSaveBrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBrActionPerformed
        save(true);
    }//GEN-LAST:event_btnSaveBrActionPerformed
    private void saveDefinition() {
         if (getBrDefinition().validate(true).size() > 0) {
            return;
        }
        if (getBr().getBrDefinitionList().contains(getBrDefinition())) {
            int index = getBr().getBrDefinitionList().indexOf(getBrDefinition());
            getBr().getBrDefinitionList().get(index).copyFromObject(getBrDefinition());
        } else {
            getBr().getBrDefinitionList().addAsNew(getBrDefinition());
        }
        showPanel(CARD_BR);
    }
    private void btnSaveBrDefinitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBrDefinitionActionPerformed
       saveDefinition();
    }//GEN-LAST:event_btnSaveBrDefinitionActionPerformed
    private void saveValidation() {
        if (getBrValidation().validate(true).size() > 0) {
            return;
        }
        if (getBr().getBrValidationList().contains(getBrValidation())) {
            int index = getBr().getBrValidationList().indexOf(getBrValidation());
            getBr().getBrValidationList().get(index).copyFromObject(getBrValidation());
        } else {
            getBr().getBrValidationList().addAsNew(getBrValidation());
        }
        showPanel(CARD_BR);
    }
    private void btnSaveBrValidationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBrValidationActionPerformed
        saveValidation();
    }//GEN-LAST:event_btnSaveBrValidationActionPerformed

    private void btnAddDefinitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDefinitionActionPerformed
        addDefinition();
    }//GEN-LAST:event_btnAddDefinitionActionPerformed

    private void btnEditDefinitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditDefinitionActionPerformed
        editDefinition();
    }//GEN-LAST:event_btnEditDefinitionActionPerformed

    private void btnRemoveDefinitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveDefinitionActionPerformed
        removeDefinition();
    }//GEN-LAST:event_btnRemoveDefinitionActionPerformed

    private void menuAddDefinitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddDefinitionActionPerformed
        addDefinition();
    }//GEN-LAST:event_menuAddDefinitionActionPerformed

    private void menuEditDefinitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditDefinitionActionPerformed
        editDefinition();
    }//GEN-LAST:event_menuEditDefinitionActionPerformed

    private void menuRemoveDefinitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveDefinitionActionPerformed
        removeDefinition();
    }//GEN-LAST:event_menuRemoveDefinitionActionPerformed

    private void btnAddValidationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddValidationActionPerformed
        addValidation();
    }//GEN-LAST:event_btnAddValidationActionPerformed

    private void btnEditValidationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditValidationActionPerformed
        editValidation();
    }//GEN-LAST:event_btnEditValidationActionPerformed

    private void btnRemoveValidationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveValidationActionPerformed
        removeValidation();
    }//GEN-LAST:event_btnRemoveValidationActionPerformed

    private void menuAddValidationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddValidationActionPerformed
        addValidation();
    }//GEN-LAST:event_menuAddValidationActionPerformed

    private void menuEditValidationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditValidationActionPerformed
        editValidation();
    }//GEN-LAST:event_menuEditValidationActionPerformed

    private void menuRemoveValidationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveValidationActionPerformed
        removeValidation();
    }//GEN-LAST:event_menuRemoveValidationActionPerformed

    private void removeDefinition() {
        br.removeSelectedBrDefinition();
    }

    private void addValidation() {
        setBrValidation(new BrValidationBean());
        getBrValidation().setBrId(getBr().getId());
        showPanel(CARD_BR_VALIDATION);
    }

    private void removeValidation() {
        br.removeSelectedBrValidation();
    }

    private void editDefinition() {
        if (getBr().getSelectedBrDefinition() != null) {
            setBrDefinition((BrDefinitionBean) getBr().getSelectedBrDefinition().copy());
            showPanel(CARD_BR_DEFINITION);
        }
    }

    private void editValidation() {
        if (getBr().getSelectedBrValidation() != null) {
            setBrValidation((BrValidationBean) getBr().getSelectedBrValidation().copy());
            showPanel(CARD_BR_VALIDATION);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.referencedata.ApplicationActionTypeListBean applicationActionTypes;
    private org.sola.clients.beans.referencedata.BrSeverityTypeListBean brSeverityTypes;
    private org.sola.clients.beans.referencedata.BrTechnicalTypeListBean brTechnicalTypes;
    private org.sola.clients.beans.referencedata.BrValidationTargetTypeListBean brValidationTargetTypes;
    private javax.swing.JButton btnActiveFrom;
    private javax.swing.JButton btnActiveUntil;
    private javax.swing.JButton btnAddDefinition;
    private javax.swing.JButton btnAddValidation;
    private javax.swing.JButton btnEditDefinition;
    private javax.swing.JButton btnEditValidation;
    private javax.swing.JButton btnRemoveDefinition;
    private javax.swing.JButton btnRemoveValidation;
    private javax.swing.JButton btnSaveBr;
    private javax.swing.JButton btnSaveBrDefinition;
    private javax.swing.JButton btnSaveBrValidation;
    private javax.swing.JComboBox cbxApplicationActions;
    private javax.swing.JComboBox cbxRegistrationTypes;
    private javax.swing.JComboBox cbxRequestTypes;
    private javax.swing.JComboBox cbxRrrTypes;
    private javax.swing.JComboBox cbxServiceActions;
    private javax.swing.JComboBox cbxSeverity;
    private javax.swing.JComboBox cbxTargetTypes;
    private javax.swing.JComboBox cbxTechnicalType;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private org.sola.clients.swing.ui.HeaderPanel headerPanelBrDefinition;
    private org.sola.clients.swing.ui.HeaderPanel headerPanelBrValidation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles jTableWithDefaultStyles1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private org.sola.clients.beans.system.LocalizedValuesListBean localizedFeedback;
    private javax.swing.JMenuItem menuAddDefinition;
    private javax.swing.JMenuItem menuAddValidation;
    private javax.swing.JMenuItem menuEditDefinition;
    private javax.swing.JMenuItem menuEditValidation;
    private javax.swing.JMenuItem menuRemoveDefinition;
    private javax.swing.JMenuItem menuRemoveValidation;
    private javax.swing.JPanel pnlBr;
    private javax.swing.JPanel pnlBrDefinition;
    private javax.swing.JPanel pnlBrValidation;
    private javax.swing.JPanel pnlBrValidationFields;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JPopupMenu popupDefinitions;
    private javax.swing.JPopupMenu popupValidations;
    private org.sola.clients.beans.referencedata.RegistrationStatusTypeListBean registrationStatusTypes;
    private org.sola.clients.beans.referencedata.RequestTypeListBean requestTypes;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypes;
    private org.sola.clients.beans.referencedata.ServiceActionTypeListBean serviceActionTypes;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableDefinitions;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableValidations;
    private javax.swing.JTabbedPane tabsPanel;
    private javax.swing.JFormattedTextField txtActiveFrom;
    private javax.swing.JFormattedTextField txtActiveUntil;
    private javax.swing.JTextArea txtBody;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtDisplayName;
    private javax.swing.JFormattedTextField txtOrder;
    private javax.swing.JTextArea txtTechnicalDescription;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
