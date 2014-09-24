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
package org.sola.clients.swing.desktop.application;

import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.RequestTypeListBean;
import org.sola.clients.swing.common.controls.TreeTableRowData;
import org.sola.common.WindowUtility;

/**
 * Pop-up form with the list of request types. {@link RequestTypeListBean} is
 * used to bind the data on the form.
 */
public class ServiceListForm extends javax.swing.JDialog {

    private ApplicationBean application;

    public ServiceListForm(ApplicationBean application) {
        super((JFrame) null, true);
        this.application = application;
        initComponents();
        postInit();
    }

    private void postInit() {
        this.setIconImage(new ImageIcon(ServiceListForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
        btnAddService.setEnabled(true);
        this.getRootPane().setDefaultButton(btnAddService);
        WindowUtility.addEscapeListener(this, false);
        configureTreeTable();
    }

    private void configureTreeTable() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle");
        String column1Lbl = bundle.getString("ServiceListForm.tabFeeDetails1.columnModel.title0");
        String column2Lbl = bundle.getString("ServiceListForm.tabFeeDetails1.columnModel.title1");
        RequestTypeTreeTableModel model = new RequestTypeTreeTableModel(
                requestTypeList.sortRequestTypes(requestTypeList.getRequestTypeList()));
        //model.hideColumn2();
        model.setColumnLabels(column1Lbl, column2Lbl);
        treeTable.setTreeTableModel(model);
        treeTable.setRootVisible(false);
        // Allow user to resize the columns, but not reorder them. 
        treeTable.getTableHeader().setReorderingAllowed(false);
        treeTable.getTableHeader().setResizingAllowed(true);
        // Resize the columns based on thier contents using packAll
        treeTable.packAll();
    }

    private void addService() {
        List<TreeTableRowData> selected = treeTable.getSelectedDataRows();
        for (TreeTableRowData row : selected) {
            if (!row.isParent()) {
                application.addService((RequestTypeBean) row.getSource());
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        requestTypeList = new org.sola.clients.beans.referencedata.RequestTypeListBean();
        jToolBar1 = new javax.swing.JToolBar();
        btnClose = new javax.swing.JButton();
        btnAddService = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeTable = new org.sola.clients.swing.common.controls.JTreeTableWithDefaultStyles();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        setTitle(bundle.getString("ServiceListForm.title")); // NOI18N
        setName("Form"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName(bundle.getString("ServiceListForm.jToolBar1.name")); // NOI18N

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png"))); // NOI18N
        btnClose.setText(bundle.getString("ServiceListForm.btnClose.text")); // NOI18N
        btnClose.setFocusable(false);
        btnClose.setName("btnClose"); // NOI18N
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        btnAddService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddService.setText(bundle.getString("ServiceListForm.btnAddService.text")); // NOI18N
        btnAddService.setName(bundle.getString("ServiceListForm.btnAddService.name")); // NOI18N
        btnAddService.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddServiceActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddService);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        treeTable.setName("treeTable"); // NOI18N
        treeTable.getTableHeader().setReorderingAllowed(false);
        treeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(treeTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddServiceActionPerformed
        addService();
    }//GEN-LAST:event_btnAddServiceActionPerformed

    private void treeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeTableMouseClicked
        if (evt.getClickCount() == 2) {
            addService();
        };
    }//GEN-LAST:event_treeTableMouseClicked

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddService;
    private javax.swing.JButton btnClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.beans.referencedata.RequestTypeListBean requestTypeList;
    private org.sola.clients.swing.common.controls.JTreeTableWithDefaultStyles treeTable;
    // End of variables declaration//GEN-END:variables
}
