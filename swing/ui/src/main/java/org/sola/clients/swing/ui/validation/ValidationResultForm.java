/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.ui.validation;

import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.clients.swing.ui.renderers.ViolationCellRenderer;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.swing.common.LafManager;

/**
 * Display result of changing status of application or service.
 * {link ChangeStatusResultBean} is used to bind data on the form.
 */
public class ValidationResultForm extends javax.swing.JDialog {

    private ObservableList<ValidationResultBean> validationResultsList;
    private boolean isSuccess = true;
    
    /** Creates new form ChangeStatusResultForm */
    public ValidationResultForm(java.awt.Frame parent, boolean modal,
            List<ValidationResultBean> validationResultsList, boolean isSuccess, String message) {
        super(parent, modal);
        
        this.isSuccess = isSuccess;
        
        if(validationResultsList!=null){
            this.validationResultsList = ObservableCollections.observableList(validationResultsList);
        }else{
            this.validationResultsList = ObservableCollections.observableList(new ArrayList<ValidationResultBean>());
        }
        
        ObservableList<ValidationResultBean>  validationSorted1List = ObservableCollections.observableList(new ArrayList<ValidationResultBean>());       
          ObservableList<ValidationResultBean>  validationSorted2List = ObservableCollections.observableList(new ArrayList<ValidationResultBean>());       
          ObservableList<ValidationResultBean>  validationSorted3List = ObservableCollections.observableList(new ArrayList<ValidationResultBean>());       
          ObservableList<ValidationResultBean>  validationSorted4List = ObservableCollections.observableList(new ArrayList<ValidationResultBean>());       
          ObservableList<ValidationResultBean>  validationSortedList = ObservableCollections.observableList(new ArrayList<ValidationResultBean>());       
          
          
          for(ValidationResultBean resultBean:this.validationResultsList){
            if ((resultBean.getSeverity().contains("medium"))) {
                validationSorted1List.add(0,resultBean);
            }else {
                validationSorted1List.add(resultBean);
            }
          }
          validationSorted2List=validationSorted1List;
          
          for(ValidationResultBean resultBean:validationSorted2List){
            if ((resultBean.getSeverity().contains("warning"))) {
                validationSorted3List.add(0,resultBean);
            }else {
                validationSorted3List.add(resultBean);
            }
          }
          
          validationSorted4List = validationSorted3List;
          
          for(ValidationResultBean resultBean:validationSorted4List){
            if (resultBean.isSuccessful()) {
                validationSortedList.add(resultBean);
            }else {
                validationSortedList.add(0, resultBean);
            }
          }
          this.validationResultsList = validationSortedList;
          
        
        initComponents();
        lblMessage.setText(message);
        customizeForm();
    }
    
    public ObservableList<ValidationResultBean> getValidationResultsList() {
        
        return validationResultsList;
    }

    public void setValidationResultsList(ObservableList<ValidationResultBean> validationResultsList) {
        this.validationResultsList = validationResultsList;
    }

    private void customizeForm() {
        if (isSuccess) {
            // Show green
            lblMessage.setBackground(new java.awt.Color(153, 255, 153));
            lblMessage.setForeground(new java.awt.Color(0, 102, 0));
            lblMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/ball_green.png")));
            lblMessage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)));
        } else {
            // Show red
            lblMessage.setBackground(new java.awt.Color(251, 162, 162));
            lblMessage.setForeground(new java.awt.Color(153, 0, 0));
            lblMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/ball_red.png"))); // NOI18N
            lblMessage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jScrollPane1 = new javax.swing.JScrollPane();
        tableValidations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        lblMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/validation/Bundle"); // NOI18N
        setTitle(bundle.getString("ValidationResultForm.title")); // NOI18N
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableValidations.setName("tableValidations"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${validationResultsList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableValidations);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${feedback}"));
        columnBinding.setColumnName("Feedback");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${severity}"));
        columnBinding.setColumnName("Severity");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${successful}"));
        columnBinding.setColumnName("Successful");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tableValidations);
        tableValidations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ValidationResultForm.tableValidations.columnModel.title0")); // NOI18N
        tableValidations.getColumnModel().getColumn(0).setCellRenderer(new TableCellTextAreaRenderer());
        tableValidations.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableValidations.getColumnModel().getColumn(1).setMaxWidth(100);
        tableValidations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ValidationResultForm.tableValidations.columnModel.title1")); // NOI18N
        tableValidations.getColumnModel().getColumn(2).setPreferredWidth(45);
        tableValidations.getColumnModel().getColumn(2).setMaxWidth(45);
        tableValidations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ValidationResultForm.tableValidations.columnModel.title2")); // NOI18N
        tableValidations.getColumnModel().getColumn(2).setCellRenderer(new ViolationCellRenderer());

        lblMessage.setBackground(new java.awt.Color(153, 255, 153));
        lblMessage.setFont(LafManager.getInstance().getLabFontBold());
        lblMessage.setForeground(new java.awt.Color(0, 102, 0));
        lblMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/ball_green.png"))); // NOI18N
        lblMessage.setText(bundle.getString("ValidationResultForm.lblMessage.text")); // NOI18N
        lblMessage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)));
        lblMessage.setName("lblMessage"); // NOI18N
        lblMessage.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMessage;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableValidations;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
