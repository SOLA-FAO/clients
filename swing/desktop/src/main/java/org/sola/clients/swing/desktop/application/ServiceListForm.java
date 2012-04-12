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
package org.sola.clients.swing.desktop.application;

import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.referencedata.RequestTypeListBean;

/**
 * Pop-up form with the list of request types. {@link RequestTypeListBean} is used
 * to bind the data on the form.
 */
public class ServiceListForm extends javax.swing.JDialog {

    private ApplicationBean application;

    public ServiceListForm(ApplicationBean application) {
        super((JFrame) null, true);
        this.application = application;

        initComponents();
        this.setIconImage(new ImageIcon(ServiceListForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
        
        btnAddService.setEnabled(false);
        requestTypeList.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(RequestTypeListBean.SELECTED_REQUEST_TYPE_PROPERTY)){
                    btnAddService.setEnabled(requestTypeList.getSelectedRequestType()!=null);
                }
            }
        });
    }

    private void addService(){
                    if (requestTypeList.getSelectedRequestType() != null) {
//                for (Iterator<ApplicationServiceBean> it = application.getServiceList().iterator(); it.hasNext();) {
//                    ApplicationServiceBean appService = it.next();
//                    System.out.println("appService.getRequestTypeCode() " + appService.getRequestTypeCode());
//
//                    if (requestTypeList.getSelectedRequestType().getCode().equals(appService.getRequestTypeCode())) {
//                        MessageUtility.displayMessage(ClientMessage.APPLICATION_ALREADYSELECTED_SERVICE);
//                        return;
//                    }
//                }
                application.addService(requestTypeList.getSelectedRequestType());
            }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        requestTypeList = new org.sola.clients.beans.referencedata.RequestTypeListBean();
        scrollFeeDetails1 = new javax.swing.JScrollPane();
        tabFeeDetails1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddService = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        setTitle(bundle.getString("ServiceListForm.title")); // NOI18N
        setName("Form"); // NOI18N

        scrollFeeDetails1.setName("scrollFeeDetails1"); // NOI18N
        scrollFeeDetails1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabFeeDetails1.setName("tabFeeDetails1"); // NOI18N
        tabFeeDetails1.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${requestTypeList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, requestTypeList, eLProperty, tabFeeDetails1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${displayValue}"));
        columnBinding.setColumnName("Display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nrPropertiesRequired}"));
        columnBinding.setColumnName("Nr Properties Required");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, requestTypeList, org.jdesktop.beansbinding.ELProperty.create("${selectedRequestType}"), tabFeeDetails1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tabFeeDetails1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabFeeDetails1MouseClicked(evt);
            }
        });
        scrollFeeDetails1.setViewportView(tabFeeDetails1);
        tabFeeDetails1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ServiceListForm.tabFeeDetails1.columnModel.title0")); // NOI18N
        tabFeeDetails1.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabFeeDetails1.getColumnModel().getColumn(1).setMaxWidth(150);
        tabFeeDetails1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ServiceListForm.tabFeeDetails1.columnModel.title1")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName(bundle.getString("ServiceListForm.jToolBar1.name")); // NOI18N

        btnAddService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddService.setText(bundle.getString("ServiceListForm.btnAddService.text")); // NOI18N
        btnAddService.setFocusable(false);
        btnAddService.setName(bundle.getString("ServiceListForm.btnAddService.name")); // NOI18N
        btnAddService.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddServiceActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddService);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabFeeDetails1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabFeeDetails1MouseClicked
        if (evt.getClickCount() == 2) {
            addService();
        }
}//GEN-LAST:event_tabFeeDetails1MouseClicked

    private void btnAddServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddServiceActionPerformed
        addService();
    }//GEN-LAST:event_btnAddServiceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddService;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.beans.referencedata.RequestTypeListBean requestTypeList;
    private javax.swing.JScrollPane scrollFeeDetails1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
