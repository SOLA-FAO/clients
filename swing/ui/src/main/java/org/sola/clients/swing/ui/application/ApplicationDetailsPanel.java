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
import org.sola.clients.swing.common.LafManager;
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
        tabPanels = new javax.swing.JTabbedPane();
        jPanel23 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        labName = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        labPhone = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        labLastName = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        txtFax = new javax.swing.JTextField();
        labFax = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        labPreferredWay = new javax.swing.JLabel();
        txtPreferredWay = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        labEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        labAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        labAgents = new javax.swing.JLabel();
        txtAgent = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        txtStatus = new javax.swing.JTextField();
        labStatus = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        labLodgedDate = new javax.swing.JLabel();
        txtLodgementDate = new javax.swing.JFormattedTextField();
        jPanel13 = new javax.swing.JPanel();
        labLodgedDate1 = new javax.swing.JLabel();
        txtCompleteDate = new javax.swing.JFormattedTextField();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        labAppNr = new javax.swing.JLabel();
        labTxtAppNr = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        scrollFeeDetails1 = new javax.swing.JScrollPane();
        tabFeeDetails1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel24 = new javax.swing.JPanel();
        scrollPropertyDetails = new javax.swing.JScrollPane();
        tabPropertyDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel25 = new javax.swing.JPanel();
        scrollDocuments = new javax.swing.JScrollPane();
        tabDocuments = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel26 = new javax.swing.JPanel();
        scrollFeeDetails = new javax.swing.JScrollPane();
        tabFeeDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel9 = new javax.swing.JPanel();
        formTxtPaid = new javax.swing.JFormattedTextField();
        formTxtFee = new javax.swing.JFormattedTextField();
        formTxtTaxes = new javax.swing.JFormattedTextField();
        formTxtServiceFee = new javax.swing.JFormattedTextField();
        labFixedFee = new javax.swing.JLabel();
        labTotalFee1 = new javax.swing.JLabel();
        labTotalFee = new javax.swing.JLabel();
        labTotalFee2 = new javax.swing.JLabel();
        labTotalFee3 = new javax.swing.JLabel();
        cbxPaid = new javax.swing.JCheckBox();

        setMinimumSize(new java.awt.Dimension(500, 300));
        setName("Form"); // NOI18N

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 3)); // NOI18N
        txtId.setForeground(new java.awt.Color(240, 240, 240));
        txtId.setBorder(null);
        txtId.setCaretColor(new java.awt.Color(240, 240, 240));
        txtId.setDisabledTextColor(new java.awt.Color(240, 240, 240));
        txtId.setEnabled(false);
        txtId.setMinimumSize(new java.awt.Dimension(0, 0));
        txtId.setName("txtId"); // NOI18N
        txtId.setPreferredSize(new java.awt.Dimension(0, 0));

        tabPanels.setName("tabPanels"); // NOI18N

        jPanel23.setName("jPanel23"); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridLayout(3, 2, 15, 0));

        jPanel1.setName("jPanel1"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/application/Bundle"); // NOI18N
        labName.setText(bundle.getString("ApplicationDetailsPanel.labName.text_1")); // NOI18N
        labName.setName("labName"); // NOI18N

        txtFirstName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtFirstName.setEditable(false);
        txtFirstName.setForeground(new java.awt.Color(102, 102, 102));
        txtFirstName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFirstName.setName("txtFirstName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.name}"), txtFirstName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFirstName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFirstName.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(labName)
                .addContainerGap(212, Short.MAX_VALUE))
            .addComponent(txtFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(labName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel1);

        jPanel5.setName("jPanel5"); // NOI18N

        labPhone.setText(bundle.getString("ApplicationDetailsPanel.labPhone.text_1")); // NOI18N
        labPhone.setName("labPhone"); // NOI18N

        txtPhone.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPhone.setEditable(false);
        txtPhone.setForeground(new java.awt.Color(102, 102, 102));
        txtPhone.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPhone.setName("txtPhone"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.phone}"), txtPhone, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtPhone.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtPhone.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addContainerGap(246, Short.MAX_VALUE))
            .addComponent(txtPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel5);

        jPanel3.setName("jPanel3"); // NOI18N

        labLastName.setText(bundle.getString("ApplicationDetailsPanel.labLastName.text_1")); // NOI18N
        labLastName.setName("labLastName"); // NOI18N

        txtLastName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtLastName.setEditable(false);
        txtLastName.setForeground(new java.awt.Color(102, 102, 102));
        txtLastName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLastName.setName("txtLastName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.lastName}"), txtLastName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtLastName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastName.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labLastName)
                .addContainerGap(226, Short.MAX_VALUE))
            .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labLastName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel3);

        jPanel6.setName("jPanel6"); // NOI18N

        txtFax.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtFax.setEditable(false);
        txtFax.setForeground(new java.awt.Color(102, 102, 102));
        txtFax.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFax.setName("txtFax"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.fax}"), txtFax, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFax.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFax.setHorizontalAlignment(JTextField.LEADING);

        labFax.setText(bundle.getString("ApplicationDetailsPanel.labFax.text_1")); // NOI18N
        labFax.setName("labFax"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(labFax)
                .addContainerGap(258, Short.MAX_VALUE))
            .addComponent(txtFax, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(labFax)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel6);

        jPanel4.setName("jPanel4"); // NOI18N

        labPreferredWay.setText(bundle.getString("ApplicationDetailsPanel.labPreferredWay.text_1")); // NOI18N
        labPreferredWay.setName("labPreferredWay"); // NOI18N

        txtPreferredWay.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPreferredWay.setEditable(false);
        txtPreferredWay.setForeground(new java.awt.Color(102, 102, 102));
        txtPreferredWay.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPreferredWay.setName("txtPreferredWay"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.preferredCommunication.displayValue}"), txtPreferredWay, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtPreferredWay.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtPreferredWay.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(labPreferredWay)
                .addContainerGap(121, Short.MAX_VALUE))
            .addComponent(txtPreferredWay, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(labPreferredWay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPreferredWay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel4);

        jPanel2.setName("jPanel2"); // NOI18N

        labEmail.setText(bundle.getString("ApplicationDetailsPanel.labEmail.text_1")); // NOI18N
        labEmail.setName("labEmail"); // NOI18N

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtEmail.setEditable(false);
        txtEmail.setForeground(new java.awt.Color(102, 102, 102));
        txtEmail.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEmail.setName("txtEmail"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.email}"), txtEmail, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtEmail.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtEmail.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(labEmail)
                .addContainerGap(248, Short.MAX_VALUE))
            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(labEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel2);

        jPanel7.setName("jPanel7"); // NOI18N

        labAddress.setText(bundle.getString("ApplicationDetailsPanel.labAddress.text_1")); // NOI18N
        labAddress.setName("labAddress"); // NOI18N

        txtAddress.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAddress.setEditable(false);
        txtAddress.setForeground(new java.awt.Color(102, 102, 102));
        txtAddress.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAddress.setName("txtAddress"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.address.description}"), txtAddress, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAddress.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAddress.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addContainerGap(533, Short.MAX_VALUE))
            .addComponent(txtAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jPanel11.setName("jPanel11"); // NOI18N

        labAgents.setText(bundle.getString("ApplicationDetailsPanel.labAgents.text_1")); // NOI18N
        labAgents.setName("labAgents"); // NOI18N

        txtAgent.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAgent.setEditable(false);
        txtAgent.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAgent.setName("txtAgent"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${agent.name}"), txtAgent, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAgent.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAgent.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtAgent, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
            .addComponent(labAgents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(labAgents)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel11);

        jPanel10.setName("jPanel10"); // NOI18N

        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtStatus.setEditable(false);
        txtStatus.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtStatus.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtStatus.setHorizontalAlignment(JTextField.LEADING);

        labStatus.setText(bundle.getString("ApplicationDetailsPanel.labStatus.text_1")); // NOI18N
        labStatus.setName("labStatus"); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
            .addComponent(labStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel10);

        jPanel12.setName("jPanel12"); // NOI18N

        labLodgedDate.setText(bundle.getString("ApplicationDetailsPanel.labLodgedDate.text")); // NOI18N
        labLodgedDate.setName("labLodgedDate"); // NOI18N

        txtLodgementDate.setEditable(false);
        txtLodgementDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtLodgementDate.setText(bundle.getString("ApplicationDetailsPanel.txtLodgementDate.text")); // NOI18N
        txtLodgementDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLodgementDate.setName(bundle.getString("ApplicationDetailsPanel.txtLodgementDate.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"), txtLodgementDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labLodgedDate, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
            .addComponent(txtLodgementDate)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(labLodgedDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLodgementDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel12);

        jPanel13.setName("jPanel13"); // NOI18N

        labLodgedDate1.setText(bundle.getString("ApplicationDetailsPanel.labLodgedDate1.text")); // NOI18N
        labLodgedDate1.setName("labLodgedDate1"); // NOI18N

        txtCompleteDate.setEditable(false);
        txtCompleteDate.setText(bundle.getString("ApplicationDetailsPanel.txtCompleteDate.text")); // NOI18N
        txtCompleteDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCompleteDate.setName(bundle.getString("ApplicationDetailsPanel.txtCompleteDate.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"), txtCompleteDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtCompleteDate)
            .addComponent(labLodgedDate1, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(labLodgedDate1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCompleteDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel13);

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("ApplicationDetailsPanel.groupPanel1.titleText")); // NOI18N

        labAppNr.setFont(LafManager.getInstance().getLabFontBold());
        labAppNr.setText(bundle.getString("ApplicationDetailsPanel.labAppNr.text")); // NOI18N
        labAppNr.setName("labAppNr"); // NOI18N

        labTxtAppNr.setFont(LafManager.getInstance().getLabFontBold());
        labTxtAppNr.setName("labTxtAppNr"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${nr}"), labTxtAppNr, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(labAppNr)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labTxtAppNr, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(labAppNr)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createSequentialGroup()
                        .addComponent(labTxtAppNr, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPanels.addTab(bundle.getString("ApplicationDetailsPanel.jPanel23.TabConstraints.tabTitle"), jPanel23); // NOI18N

        jPanel22.setName("jPanel22"); // NOI18N

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

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPanels.addTab(bundle.getString("ApplicationDetailsPanel.jPanel22.TabConstraints.tabTitle"), jPanel22); // NOI18N

        jPanel24.setName("jPanel24"); // NOI18N

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

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPanels.addTab(bundle.getString("ApplicationDetailsPanel.jPanel24.TabConstraints.tabTitle"), jPanel24); // NOI18N

        jPanel25.setName("jPanel25"); // NOI18N

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

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPanels.addTab(bundle.getString("ApplicationDetailsPanel.jPanel25.TabConstraints.tabTitle"), jPanel25); // NOI18N

        jPanel26.setName("jPanel26"); // NOI18N

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

        jPanel9.setName("jPanel9"); // NOI18N

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
        labFixedFee.setText(bundle.getString("ApplicationDetailsPanel.labFixedFee.text")); // NOI18N
        labFixedFee.setName("labFixedFee"); // NOI18N

        labTotalFee1.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee1.text")); // NOI18N
        labTotalFee1.setName("labTotalFee1"); // NOI18N

        labTotalFee.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee.text")); // NOI18N
        labTotalFee.setName("labTotalFee"); // NOI18N

        labTotalFee2.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee2.text")); // NOI18N
        labTotalFee2.setName("labTotalFee2"); // NOI18N

        labTotalFee3.setFont(LafManager.getInstance().getLabFontBold());
        labTotalFee3.setText(bundle.getString("ApplicationDetailsPanel.labTotalFee3.text")); // NOI18N
        labTotalFee3.setName("labTotalFee3"); // NOI18N

        cbxPaid.setText(bundle.getString("ApplicationDetailsPanel.cbxPaid.text")); // NOI18N
        cbxPaid.setActionCommand(bundle.getString("ApplicationDetailsPanel.cbxPaid.actionCommand")); // NOI18N
        cbxPaid.setEnabled(false);
        cbxPaid.setMargin(new java.awt.Insets(2, 0, 2, 2));
        cbxPaid.setName("cbxPaid"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${isFeePaid}"), cbxPaid, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labTotalFee1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labTotalFee, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(labTotalFee2))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                            .addComponent(labTotalFee3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbxPaid))
                        .addComponent(formTxtPaid, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(formTxtTaxes, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(formTxtServiceFee, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(labFixedFee, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(formTxtFee, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(labFixedFee)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formTxtServiceFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(labTotalFee1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formTxtTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labTotalFee)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formTxtFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(labTotalFee2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formTxtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labTotalFee3)
                    .addComponent(cbxPaid))
                .addContainerGap(116, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollFeeDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabPanels.addTab(bundle.getString("ApplicationDetailsPanel.jPanel26.TabConstraints.tabTitle"), jPanel26); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanels)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanels)
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
    private javax.swing.JCheckBox cbxPaid;
    private javax.swing.JFormattedTextField formTxtFee;
    private javax.swing.JFormattedTextField formTxtPaid;
    private javax.swing.JFormattedTextField formTxtServiceFee;
    private javax.swing.JFormattedTextField formTxtTaxes;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
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
    private javax.swing.JScrollPane scrollDocuments;
    private javax.swing.JScrollPane scrollFeeDetails;
    private javax.swing.JScrollPane scrollFeeDetails1;
    private javax.swing.JScrollPane scrollPropertyDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabDocuments;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails1;
    private javax.swing.JTabbedPane tabPanels;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabPropertyDetails;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAgent;
    private javax.swing.JFormattedTextField txtCompleteDate;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JFormattedTextField txtLodgementDate;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtPreferredWay;
    private javax.swing.JTextField txtStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
