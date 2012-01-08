/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.ui.application;

import java.awt.ComponentOrientation;
import java.util.Collections;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.swing.common.converters.DateConverter;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.ui.renderers.AttachedDocumentCellRenderer;
import org.sola.webservices.transferobjects.casemanagement.ApplicationTO;
import org.sola.clients.beans.sorters.ServicesSorterByOrder;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.UIManager;
/** 
 * Displays application details. This panel could be used on different forms, 
 * where application details are needed to display in the read only mode.
 * <br />{@link ApplicationBean} is used to bind the data on the panel.
 */
public class ApplicationDetailsPanel extends javax.swing.JPanel {

    private String applicationID;

    /** 
     * Default constructor is used to be able to put this panel on the form
     * in design mode.
     */
    public ApplicationDetailsPanel() {
        initComponents();
    }

    /** 
     * This constructor should be used to initialize application bean with a given 
     * <code>applicationId</code>.
     */
    public ApplicationDetailsPanel(String applicationId) {
        this.applicationID = applicationId;
        initComponents();
    }

    /** Public property to expose {@link ApplicationBean} used on the panel.*/
    public ApplicationBean getApplicationBean() {
        return appBean;
    }

    /** This method is used by the panel designer to create {@link ApplicationBean}. 
     * It uses <code>applicationId</code> parameter passed to the panel constructor.<br />
     * <code>applicationId</code> should be initialized before 
     * {@link ApplicationDetailsPanel#initComponents} method call.
     */
    private ApplicationBean createApplicationBean() {
        ApplicationBean applicationBean = new ApplicationBean();

        if (applicationID != null && !applicationID.equals("")) {
            ApplicationTO applicationTO = WSManager.getInstance().getCaseManagementService().getApplication(applicationID);
            TypeConverters.TransferObjectToBean(applicationTO, ApplicationBean.class, applicationBean);

            if (applicationBean.getServiceList() != null) {
                Collections.sort(applicationBean.getServiceList(), new ServicesSorterByOrder());
            }
        }

        return applicationBean;
    }

    /** Designer generated code */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        appBean = createApplicationBean();
        txtId = new javax.swing.JTextField();
        tabbedContactandService = new javax.swing.JTabbedPane();
        contactPanel = new javax.swing.JPanel();
        labName = new javax.swing.JLabel();
        labLastName = new javax.swing.JLabel();
        labAddress = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        txtLastName = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        labEmail = new javax.swing.JLabel();
        labPhone = new javax.swing.JLabel();
        labFax = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        labPreferredWay = new javax.swing.JLabel();
        txtPreferredWay = new javax.swing.JTextField();
        servicesPanel = new javax.swing.JPanel();
        scrollFeeDetails1 = new javax.swing.JScrollPane();
        tabFeeDetails1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        propertyContainerPanel = new javax.swing.JPanel();
        scrollPropertyDetails = new javax.swing.JScrollPane();
        tabPropertyDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        documentContainerPanel = new javax.swing.JPanel();
        scrollDocuments = new javax.swing.JScrollPane();
        tabDocuments = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        feePanel = new javax.swing.JPanel();
        scrollFeeDetails = new javax.swing.JScrollPane();
        tabFeeDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        cbxPaid = new javax.swing.JCheckBox();
        formTxtPaid = new javax.swing.JFormattedTextField();
        formTxtFee = new javax.swing.JFormattedTextField();
        formTxtTaxes = new javax.swing.JFormattedTextField();
        formTxtServiceFee = new javax.swing.JFormattedTextField();
        labFixedFee = new javax.swing.JLabel();
        labTotalFee1 = new javax.swing.JLabel();
        labTotalFee = new javax.swing.JLabel();
        labTotalFee2 = new javax.swing.JLabel();
        labTotalFee3 = new javax.swing.JLabel();
        appNumberPanel = new javax.swing.JPanel();
        labAppNr = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        txtAgent = new javax.swing.JTextField();
        labAgents = new javax.swing.JLabel();
        labLodgedDate = new javax.swing.JLabel();
        txtCompleteDate = new javax.swing.JTextField();
        labLodgedDate1 = new javax.swing.JLabel();
        txtLodgementDate = new javax.swing.JTextField();
        labStatus = new javax.swing.JLabel();
        labTxtAppNr = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(500, 300));
        setName("Form"); // NOI18N

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 3));
        txtId.setForeground(new java.awt.Color(240, 240, 240));
        txtId.setBorder(null);
        txtId.setCaretColor(new java.awt.Color(240, 240, 240));
        txtId.setDisabledTextColor(new java.awt.Color(240, 240, 240));
        txtId.setEnabled(false);
        txtId.setMinimumSize(new java.awt.Dimension(0, 0));
        txtId.setName("txtId"); // NOI18N
        txtId.setPreferredSize(new java.awt.Dimension(0, 0));

        tabbedContactandService.setBackground(new java.awt.Color(255, 255, 255));
        tabbedContactandService.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedContactandService.setFont(new java.awt.Font("Tahoma", 0, 12));
        tabbedContactandService.setName("tabbedContactandService"); // NOI18N
        tabbedContactandService.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        contactPanel.setBackground(new java.awt.Color(255, 255, 255));
        contactPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        contactPanel.setName("contactPanel"); // NOI18N
        contactPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        labName.setFont(new java.awt.Font("Tahoma", 0, 12));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/application/Bundle"); // NOI18N
        labName.setText(bundle.getString("ApplicationDetailsPanel.labName.text_1")); // NOI18N
        labName.setName("labName"); // NOI18N

        labLastName.setFont(new java.awt.Font("Tahoma", 0, 12));
        labLastName.setText(bundle.getString("ApplicationDetailsPanel.labLastName.text_1")); // NOI18N
        labLastName.setName("labLastName"); // NOI18N

        labAddress.setFont(new java.awt.Font("Tahoma", 0, 12));
        labAddress.setText(bundle.getString("ApplicationDetailsPanel.labAddress.text_1")); // NOI18N
        labAddress.setName("labAddress"); // NOI18N

        txtFirstName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtFirstName.setEditable(false);
        txtFirstName.setForeground(new java.awt.Color(102, 102, 102));
        txtFirstName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFirstName.setEnabled(false);
        txtFirstName.setName("txtFirstName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.name}"), txtFirstName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFirstName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFirstName.setHorizontalAlignment(JTextField.LEADING);

        txtLastName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtLastName.setEditable(false);
        txtLastName.setForeground(new java.awt.Color(102, 102, 102));
        txtLastName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLastName.setEnabled(false);
        txtLastName.setName("txtLastName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.lastName}"), txtLastName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtLastName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastName.setHorizontalAlignment(JTextField.LEADING);

        txtAddress.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAddress.setEditable(false);
        txtAddress.setForeground(new java.awt.Color(102, 102, 102));
        txtAddress.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAddress.setEnabled(false);
        txtAddress.setName("txtAddress"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.address.description}"), txtAddress, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAddress.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAddress.setHorizontalAlignment(JTextField.LEADING);

        labEmail.setFont(new java.awt.Font("Tahoma", 0, 12));
        labEmail.setText(bundle.getString("ApplicationDetailsPanel.labEmail.text_1")); // NOI18N
        labEmail.setName("labEmail"); // NOI18N

        labPhone.setFont(new java.awt.Font("Tahoma", 0, 12));
        labPhone.setText(bundle.getString("ApplicationDetailsPanel.labPhone.text_1")); // NOI18N
        labPhone.setName("labPhone"); // NOI18N

        labFax.setFont(new java.awt.Font("Tahoma", 0, 12));
        labFax.setText(bundle.getString("ApplicationDetailsPanel.labFax.text_1")); // NOI18N
        labFax.setName("labFax"); // NOI18N

        txtFax.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtFax.setEditable(false);
        txtFax.setForeground(new java.awt.Color(102, 102, 102));
        txtFax.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFax.setEnabled(false);
        txtFax.setName("txtFax"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.fax}"), txtFax, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFax.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFax.setHorizontalAlignment(JTextField.LEADING);

        txtPhone.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPhone.setEditable(false);
        txtPhone.setForeground(new java.awt.Color(102, 102, 102));
        txtPhone.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPhone.setEnabled(false);
        txtPhone.setName("txtPhone"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.phone}"), txtPhone, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtPhone.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtPhone.setHorizontalAlignment(JTextField.LEADING);

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtEmail.setEditable(false);
        txtEmail.setForeground(new java.awt.Color(102, 102, 102));
        txtEmail.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEmail.setEnabled(false);
        txtEmail.setName("txtEmail"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.email}"), txtEmail, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtEmail.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtEmail.setHorizontalAlignment(JTextField.LEADING);

        labPreferredWay.setFont(new java.awt.Font("Tahoma", 0, 12));
        labPreferredWay.setText(bundle.getString("ApplicationDetailsPanel.labPreferredWay.text_1")); // NOI18N
        labPreferredWay.setName("labPreferredWay"); // NOI18N

        txtPreferredWay.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPreferredWay.setEditable(false);
        txtPreferredWay.setForeground(new java.awt.Color(102, 102, 102));
        txtPreferredWay.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPreferredWay.setEnabled(false);
        txtPreferredWay.setName("txtPreferredWay"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.preferredCommunication.displayValue}"), txtPreferredWay, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtPreferredWay.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtPreferredWay.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contactPanelLayout.createSequentialGroup()
                        .addComponent(labAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 641, Short.MAX_VALUE))
                    .addComponent(txtAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contactPanelLayout.createSequentialGroup()
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labName)
                                .addComponent(labLastName)
                                .addComponent(txtFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                                .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                            .addComponent(labPreferredWay)
                            .addComponent(txtPreferredWay, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(49, 49, 49)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                .addComponent(labFax)
                                .addGap(145, 145, 145))
                            .addComponent(labPhone)
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                .addComponent(labEmail)
                                .addGap(132, 132, 132))
                            .addComponent(txtFax, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(txtPhone)
                            .addComponent(txtEmail))))
                .addGap(192, 192, 192))
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(contactPanelLayout.createSequentialGroup()
                        .addComponent(labName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labLastName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labPreferredWay)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPreferredWay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(contactPanelLayout.createSequentialGroup()
                        .addComponent(labEmail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labPhone)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labFax)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addComponent(labAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        tabbedContactandService.addTab(bundle.getString("ApplicationDetailsPanel.contactPanel.TabConstraints.tabTitle"), contactPanel); // NOI18N

        servicesPanel.setBackground(new java.awt.Color(255, 255, 255));
        servicesPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        servicesPanel.setName("servicesPanel"); // NOI18N
        servicesPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        scrollFeeDetails1.setBackground(new java.awt.Color(255, 255, 255));
        scrollFeeDetails1.setName("scrollFeeDetails1"); // NOI18N
        scrollFeeDetails1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabFeeDetails1.setName("tabFeeDetails1"); // NOI18N
        tabFeeDetails1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabFeeDetails1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceOrder}"));
        columnBinding.setColumnName("Service Order");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${requestType.displayValue}"));
        columnBinding.setColumnName("Request Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        scrollFeeDetails1.setViewportView(tabFeeDetails1);
        tabFeeDetails1.getColumnModel().getColumn(0).setMinWidth(50);
        tabFeeDetails1.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabFeeDetails1.getColumnModel().getColumn(0).setMaxWidth(50);
        tabFeeDetails1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails1.columnModel.title0")); // NOI18N
        tabFeeDetails1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails1.columnModel.title1")); // NOI18N
        tabFeeDetails1.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails1.columnModel.title2")); // NOI18N

        javax.swing.GroupLayout servicesPanelLayout = new javax.swing.GroupLayout(servicesPanel);
        servicesPanel.setLayout(servicesPanelLayout);
        servicesPanelLayout.setHorizontalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                .addContainerGap())
        );
        servicesPanelLayout.setVerticalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedContactandService.addTab(bundle.getString("ApplicationDetailsPanel.servicesPanel.TabConstraints.tabTitle"), servicesPanel); // NOI18N

        propertyContainerPanel.setBackground(new java.awt.Color(255, 255, 255));
        propertyContainerPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        propertyContainerPanel.setName("propertyContainerPanel"); // NOI18N
        propertyContainerPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        scrollPropertyDetails.setName("scrollPropertyDetails"); // NOI18N
        scrollPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabPropertyDetails.setName("tabPropertyDetails"); // NOI18N
        tabPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${propertyList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabPropertyDetails);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${area}"));
        columnBinding.setColumnName("Area");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalValue}"));
        columnBinding.setColumnName("Total Value");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${isVerifiedExists}"));
        columnBinding.setColumnName("Is Verified Exists");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${isVerifiedHasOwners}"));
        columnBinding.setColumnName("Is Verified Has Owners");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${isVerifiedLocation}"));
        columnBinding.setColumnName("Is Verified Location");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        scrollPropertyDetails.setViewportView(tabPropertyDetails);
        tabPropertyDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabPropertyDetails.columnModel.title0_1")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabPropertyDetails.columnModel.title1_1")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabPropertyDetails.columnModel.title2_1")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabPropertyDetails.columnModel.title3_1")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabPropertyDetails.columnModel.title4")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabPropertyDetails.columnModel.title5")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabPropertyDetails.columnModel.title6")); // NOI18N

        javax.swing.GroupLayout propertyContainerPanelLayout = new javax.swing.GroupLayout(propertyContainerPanel);
        propertyContainerPanel.setLayout(propertyContainerPanelLayout);
        propertyContainerPanelLayout.setHorizontalGroup(
            propertyContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertyContainerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                .addContainerGap())
        );
        propertyContainerPanelLayout.setVerticalGroup(
            propertyContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertyContainerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedContactandService.addTab(bundle.getString("ApplicationDetailsPanel.propertyContainerPanel.TabConstraints.tabTitle"), propertyContainerPanel); // NOI18N

        documentContainerPanel.setBackground(new java.awt.Color(255, 255, 255));
        documentContainerPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        documentContainerPanel.setName("documentContainerPanel"); // NOI18N
        documentContainerPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        scrollDocuments.setName("scrollDocuments"); // NOI18N
        scrollDocuments.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabDocuments.setName("tabDocuments"); // NOI18N
        tabDocuments.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${sourceList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabDocuments);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sourceType.displayValue}"));
        columnBinding.setColumnName("Source Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${acceptance}"));
        columnBinding.setColumnName("Acceptance");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${laNr}"));
        columnBinding.setColumnName("La Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNr}"));
        columnBinding.setColumnName("Reference Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${recordation}"));
        columnBinding.setColumnName("Recordation");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${submission}"));
        columnBinding.setColumnName("Submission");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${archiveDocument}"));
        columnBinding.setColumnName("Archive Document");
        columnBinding.setColumnClass(org.sola.clients.beans.digitalarchive.DocumentBean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedSource}"), tabDocuments, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tabDocuments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabDocumentsMouseClicked(evt);
            }
        });
        scrollDocuments.setViewportView(tabDocuments);
        tabDocuments.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabDocuments.columnModel.title0")); // NOI18N
        tabDocuments.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabDocuments.columnModel.title1_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabDocuments.columnModel.title2_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationDetail.tabDocuments.columnModel.title5_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationDetail.tabDocuments.columnModel.title3_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("ApplicationDetail.tabDocuments.columnModel.title4_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabDocuments.columnModel.title6")); // NOI18N
        tabDocuments.getColumnModel().getColumn(6).setCellRenderer(new AttachedDocumentCellRenderer());

        javax.swing.GroupLayout documentContainerPanelLayout = new javax.swing.GroupLayout(documentContainerPanel);
        documentContainerPanel.setLayout(documentContainerPanelLayout);
        documentContainerPanelLayout.setHorizontalGroup(
            documentContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentContainerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                .addContainerGap())
        );
        documentContainerPanelLayout.setVerticalGroup(
            documentContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, documentContainerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedContactandService.addTab(bundle.getString("ApplicationDetailsPanel.documentContainerPanel.TabConstraints.tabTitle"), documentContainerPanel); // NOI18N

        feePanel.setBackground(new java.awt.Color(255, 255, 255));
        feePanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        feePanel.setName("feePanel"); // NOI18N
        feePanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        scrollFeeDetails.setName("scrollFeeDetails"); // NOI18N
        scrollFeeDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabFeeDetails.setName("tabFeeDetails"); // NOI18N
        tabFeeDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabFeeDetails);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceName}"));
        columnBinding.setColumnName("Service Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baseFee}"));
        columnBinding.setColumnName("Base Fee");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${areaFee}"));
        columnBinding.setColumnName("Area Fee");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${valueFee}"));
        columnBinding.setColumnName("Value Fee");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"));
        columnBinding.setColumnName("Expected Completion Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        scrollFeeDetails.setViewportView(tabFeeDetails);
        tabFeeDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails.columnModel.title0")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails.columnModel.title1")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails.columnModel.title2")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails.columnModel.title3")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails.columnModel.title4")); // NOI18N

        cbxPaid.setBackground(new java.awt.Color(255, 255, 255));
        cbxPaid.setText(bundle.getString("ApplicationDetailsPanel.cbxPaid.text")); // NOI18N
        cbxPaid.setActionCommand(bundle.getString("ApplicationDetailsPanel.cbxPaid.actionCommand")); // NOI18N
        cbxPaid.setEnabled(false);
        cbxPaid.setMargin(new java.awt.Insets(2, 0, 2, 2));
        cbxPaid.setName("cbxPaid"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${isFeePaid}"), cbxPaid, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        formTxtPaid.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        formTxtPaid.setEditable(false);
        formTxtPaid.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtPaid.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        formTxtPaid.setName("formTxtPaid"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${totalAmountPaid}"), formTxtPaid, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtPaid.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtPaid.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtFee.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        formTxtFee.setEditable(false);
        formTxtFee.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtFee.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        formTxtFee.setName("formTxtFee"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${totalFee}"), formTxtFee, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtFee.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtFee.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtTaxes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        formTxtTaxes.setEditable(false);
        formTxtTaxes.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtTaxes.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        formTxtTaxes.setName("formTxtTaxes"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${tax}"), formTxtTaxes, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtTaxes.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtTaxes.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtServiceFee.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        formTxtServiceFee.setEditable(false);
        formTxtServiceFee.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtServiceFee.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        formTxtServiceFee.setName("formTxtServiceFee"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${servicesFee}"), formTxtServiceFee, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtServiceFee.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtServiceFee.setHorizontalAlignment(JFormattedTextField.LEADING);

        labFixedFee.setBackground(new java.awt.Color(255, 255, 255));
        labFixedFee.setFont(new java.awt.Font("Tahoma", 0, 12));
        labFixedFee.setText(bundle.getString("ApplicationDetailsPanel.labFixedFee.text")); // NOI18N
        labFixedFee.setName("labFixedFee"); // NOI18N

        labTotalFee1.setFont(new java.awt.Font("Tahoma", 0, 12));
        labTotalFee1.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee1.text")); // NOI18N
        labTotalFee1.setName("labTotalFee1"); // NOI18N
        labTotalFee1.setPreferredSize(null);

        labTotalFee.setFont(new java.awt.Font("Tahoma", 0, 12));
        labTotalFee.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee.text")); // NOI18N
        labTotalFee.setName("labTotalFee"); // NOI18N

        labTotalFee2.setFont(new java.awt.Font("Tahoma", 0, 12));
        labTotalFee2.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee2.text")); // NOI18N
        labTotalFee2.setName("labTotalFee2"); // NOI18N

        labTotalFee3.setFont(new java.awt.Font("Tahoma", 1, 12));
        labTotalFee3.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee3.text")); // NOI18N
        labTotalFee3.setName("labTotalFee3"); // NOI18N

        javax.swing.GroupLayout feePanelLayout = new javax.swing.GroupLayout(feePanel);
        feePanel.setLayout(feePanelLayout);
        feePanelLayout.setHorizontalGroup(
            feePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(feePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(feePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(feePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(feePanelLayout.createSequentialGroup()
                            .addComponent(labTotalFee3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbxPaid))
                        .addComponent(formTxtPaid, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labTotalFee2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(feePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(formTxtFee, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(formTxtTaxes, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(formTxtServiceFee, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(labFixedFee, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(labTotalFee1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labTotalFee, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        feePanelLayout.setVerticalGroup(
            feePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(feePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(feePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollFeeDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(feePanelLayout.createSequentialGroup()
                        .addComponent(labFixedFee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formTxtServiceFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(labTotalFee1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formTxtTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labTotalFee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formTxtFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(labTotalFee2)
                        .addGap(6, 6, 6)
                        .addComponent(formTxtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(feePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labTotalFee3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbxPaid))))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        tabbedContactandService.addTab(bundle.getString("ApplicationDetailsPanel.feePanel.TabConstraints.tabTitle"), feePanel); // NOI18N

        appNumberPanel.setBackground(new java.awt.Color(255, 255, 255));
        appNumberPanel.setName("appNumberPanel"); // NOI18N
        appNumberPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        labAppNr.setFont(new java.awt.Font("Tahoma", 1, 12));
        labAppNr.setText(bundle.getString("ApplicationDetailsPanel.labAppNr.text")); // NOI18N
        labAppNr.setName("labAppNr"); // NOI18N

        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtStatus.setEditable(false);
        txtStatus.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtStatus.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtStatus.setHorizontalAlignment(JTextField.LEADING);

        txtAgent.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAgent.setEditable(false);
        txtAgent.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAgent.setName("txtAgent"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${agent.name}"), txtAgent, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAgent.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAgent.setHorizontalAlignment(JTextField.LEADING);

        labAgents.setFont(new java.awt.Font("Tahoma", 0, 12));
        labAgents.setText(bundle.getString("ApplicationDetailsPanel.labAgents.text_1")); // NOI18N
        labAgents.setName("labAgents"); // NOI18N

        labLodgedDate.setFont(new java.awt.Font("Tahoma", 0, 12));
        labLodgedDate.setText(bundle.getString("ApplicationDetailsPanel.labLodgedDate.text")); // NOI18N
        labLodgedDate.setName("labLodgedDate"); // NOI18N

        txtCompleteDate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtCompleteDate.setEditable(false);
        txtCompleteDate.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCompleteDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCompleteDate.setName("txtCompleteDate"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"), txtCompleteDate, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new DateConverter());
        bindingGroup.addBinding(binding);

        txtCompleteDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtCompleteDate.setHorizontalAlignment(JTextField.LEADING);

        labLodgedDate1.setFont(new java.awt.Font("Tahoma", 0, 12));
        labLodgedDate1.setText(bundle.getString("ApplicationDetailsPanel.labLodgedDate1.text")); // NOI18N
        labLodgedDate1.setName("labLodgedDate1"); // NOI18N

        txtLodgementDate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtLodgementDate.setEditable(false);
        txtLodgementDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLodgementDate.setName("txtLodgementDate"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"), txtLodgementDate, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new DateConverter());
        bindingGroup.addBinding(binding);

        txtLodgementDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLodgementDate.setHorizontalAlignment(JTextField.LEADING);

        labStatus.setFont(new java.awt.Font("Tahoma", 0, 12));
        labStatus.setText(bundle.getString("ApplicationDetailsPanel.labStatus.text_1")); // NOI18N
        labStatus.setName("labStatus"); // NOI18N

        labTxtAppNr.setFont(new java.awt.Font("Tahoma", 1, 14));
        labTxtAppNr.setName("labTxtAppNr"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${nr}"), labTxtAppNr, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout appNumberPanelLayout = new javax.swing.GroupLayout(appNumberPanel);
        appNumberPanel.setLayout(appNumberPanelLayout);
        appNumberPanelLayout.setHorizontalGroup(
            appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appNumberPanelLayout.createSequentialGroup()
                .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appNumberPanelLayout.createSequentialGroup()
                        .addComponent(labAppNr, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labTxtAppNr, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(appNumberPanelLayout.createSequentialGroup()
                        .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(appNumberPanelLayout.createSequentialGroup()
                                .addComponent(labStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtStatus))
                            .addGroup(appNumberPanelLayout.createSequentialGroup()
                                .addComponent(labAgents)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtAgent, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(43, 43, 43)
                        .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(appNumberPanelLayout.createSequentialGroup()
                                .addComponent(labLodgedDate, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtLodgementDate, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(appNumberPanelLayout.createSequentialGroup()
                                .addComponent(labLodgedDate1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                                .addComponent(txtCompleteDate, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        appNumberPanelLayout.setVerticalGroup(
            appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appNumberPanelLayout.createSequentialGroup()
                .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labAppNr)
                    .addComponent(labTxtAppNr, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labStatus)
                        .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(appNumberPanelLayout.createSequentialGroup()
                        .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labLodgedDate)
                            .addComponent(txtLodgementDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(appNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labLodgedDate1)
                                .addComponent(labAgents)
                                .addComponent(txtAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCompleteDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabbedContactandService, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                    .addComponent(appNumberPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appNumberPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedContactandService, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void tabDocumentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabDocumentsMouseClicked
        if (evt.getClickCount() == 2) {
            openAttachment();
        }
}//GEN-LAST:event_tabDocumentsMouseClicked

    /** Opens attached digital copy of document in the document's list.*/
    private void openAttachment() {
        if (appBean.getSelectedSource() != null
                && appBean.getSelectedSource().getArchiveDocument() != null) {
            // Try to open attached file
            DocumentBean.openDocument(appBean.getSelectedSource().getArchiveDocument().getId());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationBean appBean;
    private javax.swing.JPanel appNumberPanel;
    private javax.swing.JCheckBox cbxPaid;
    private javax.swing.JPanel contactPanel;
    private javax.swing.JPanel documentContainerPanel;
    private javax.swing.JPanel feePanel;
    private javax.swing.JFormattedTextField formTxtFee;
    private javax.swing.JFormattedTextField formTxtPaid;
    private javax.swing.JFormattedTextField formTxtServiceFee;
    private javax.swing.JFormattedTextField formTxtTaxes;
    private javax.swing.JLabel labAddress;
    private javax.swing.JLabel labAgents;
    private javax.swing.JLabel labAppNr;
    private javax.swing.JLabel labEmail;
    private javax.swing.JLabel labFax;
    private javax.swing.JLabel labFixedFee;
    private javax.swing.JLabel labLastName;
    private javax.swing.JLabel labLodgedDate;
    private javax.swing.JLabel labLodgedDate1;
    private javax.swing.JLabel labName;
    private javax.swing.JLabel labPhone;
    private javax.swing.JLabel labPreferredWay;
    private javax.swing.JLabel labStatus;
    private javax.swing.JLabel labTotalFee;
    private javax.swing.JLabel labTotalFee1;
    private javax.swing.JLabel labTotalFee2;
    private javax.swing.JLabel labTotalFee3;
    private javax.swing.JLabel labTxtAppNr;
    private javax.swing.JPanel propertyContainerPanel;
    private javax.swing.JScrollPane scrollDocuments;
    private javax.swing.JScrollPane scrollFeeDetails;
    private javax.swing.JScrollPane scrollFeeDetails1;
    private javax.swing.JScrollPane scrollPropertyDetails;
    private javax.swing.JPanel servicesPanel;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabDocuments;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabPropertyDetails;
    private javax.swing.JTabbedPane tabbedContactandService;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAgent;
    private javax.swing.JTextField txtCompleteDate;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtLodgementDate;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtPreferredWay;
    private javax.swing.JTextField txtStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
