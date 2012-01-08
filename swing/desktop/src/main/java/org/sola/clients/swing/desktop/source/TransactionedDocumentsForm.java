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
package org.sola.clients.swing.desktop.source;

import org.sola.clients.swing.ui.source.DocumentSeachPanel;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import javax.swing.ImageIcon;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceSearchResultBean;
import org.sola.clients.beans.source.SourceSummaryBean;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This form is used to manage transaction-driven documents. 
 * Document can be attached to transaction or detached from it.
 */
public class TransactionedDocumentsForm extends javax.swing.JDialog {

    private ApplicationBean appBean;
    private ApplicationServiceBean appService;

    /** Creates new form TransactionedDocumentsForm */
    public TransactionedDocumentsForm(java.awt.Frame parent, boolean modal,
            ApplicationBean appBean, ApplicationServiceBean appService) {
        super(parent, modal);
        this.appBean = appBean;
        this.appService = appService;
        initComponents();
        this.setIconImage(new ImageIcon(TransactionedDocumentsForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    
        postInit();
    }

    private void postInit() {
        String serviceName = "";
        String applicationNr = "";
        
        if (appService != null) {
            selectedDocumentsPanel.getSourceListBean().loadSourceByService(appService.getId());
            serviceName = appService.getRequestType().getDisplayValue();
        }
        if (appBean != null) {
            applicationDocumentsPanel.setSourceList(appBean.getSourceList());
            applicationNr = appBean.getNr();
        }
        
        this.setTitle(MessageFormat.format(this.getTitle(), applicationNr, serviceName));
        lblSelectedDocuments.setText(MessageFormat.format(lblSelectedDocuments.getText(), serviceName));
        
        customizeRemoveButton(null);
        customizeAddFromSearchButton(null);
        customizeAddFromApplicationButton(null);
        
        selectedDocumentsPanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(DocumentsPanel.SELECTED_SOURCE)){
                    customizeRemoveButton((SourceBean)evt.getNewValue());
                }
            }
        });
        
        applicationDocumentsPanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(DocumentsPanel.SELECTED_SOURCE)){
                    customizeAddFromApplicationButton((SourceBean)evt.getNewValue());
                }
            }
        });
        
        documentSeachPanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(DocumentSeachPanel.SELECTED_SOURCE)){
                    customizeAddFromSearchButton((SourceSearchResultBean)evt.getNewValue());
                }
            }
        });
    }

    private void customizeRemoveButton(SourceBean source){
        if(source == null){
            btnRemove.setEnabled(false);
        } else {
            btnRemove.setEnabled(SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL));
        }
    }
    
    private void customizeAddFromApplicationButton(SourceBean source){
        if(source == null){
            btnAddDocumentFromApplication.setEnabled(false);
        } else {
            btnAddDocumentFromApplication.setEnabled(SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL));
        }
    }
    
    private void customizeAddFromSearchButton(SourceSearchResultBean source){
        if(source == null){
            btnAddDocumentFromSearch.setEnabled(false);
        } else {
            btnAddDocumentFromSearch.setEnabled(SecurityBean.isInRole(RolesConstants.SOURCE_TRANSACTIONAL));
        }
    }
    
    private void attachToTransaction(SourceSummaryBean sourceToAttach){
        if(sourceToAttach != null && appService !=null){
            SourceBean attachedSource = SourceBean.attachToTransaction(sourceToAttach.getId(), appService.getId());
            if(attachedSource!=null){
                selectedDocumentsPanel.getSourceListBean().getSourceBeanList().add(attachedSource);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        applicationDocumentsPanel = new org.sola.clients.swing.ui.source.DocumentsPanel();
        btnAddDocumentFromApplication = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        documentSeachPanel = new org.sola.clients.swing.ui.source.DocumentSeachPanel();
        btnAddDocumentFromSearch = new javax.swing.JButton();
        selectedDocumentsPanel = new org.sola.clients.swing.ui.source.DocumentsPanel();
        lblSelectedDocuments = new javax.swing.JLabel();
        btnRemove = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.sola.clients.swing.desktop.DesktopApplication.class).getContext().getResourceMap(TransactionedDocumentsForm.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        applicationDocumentsPanel.setName("applicationDocumentsPanel"); // NOI18N

        btnAddDocumentFromApplication.setText(resourceMap.getString("btnAddDocumentFromApplication.text")); // NOI18N
        btnAddDocumentFromApplication.setName("btnAddDocumentFromApplication"); // NOI18N
        btnAddDocumentFromApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDocumentFromApplicationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddDocumentFromApplication, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(applicationDocumentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(applicationDocumentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddDocumentFromApplication)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        documentSeachPanel.setName("documentSeachPanel"); // NOI18N

        btnAddDocumentFromSearch.setText(resourceMap.getString("btnAddDocumentFromSearch.text")); // NOI18N
        btnAddDocumentFromSearch.setName("btnAddDocumentFromSearch"); // NOI18N
        btnAddDocumentFromSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDocumentFromSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentSeachPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(564, Short.MAX_VALUE)
                .addComponent(btnAddDocumentFromSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentSeachPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddDocumentFromSearch)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        selectedDocumentsPanel.setName("selectedDocumentsPanel"); // NOI18N

        lblSelectedDocuments.setFont(resourceMap.getFont("lblSelectedDocuments.font")); // NOI18N
        lblSelectedDocuments.setText(resourceMap.getString("lblSelectedDocuments.text")); // NOI18N
        lblSelectedDocuments.setName("lblSelectedDocuments"); // NOI18N

        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectedDocumentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                    .addComponent(lblSelectedDocuments)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 486, Short.MAX_VALUE)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSelectedDocuments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedDocumentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnRemove)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        SourceBean selectedSource = selectedDocumentsPanel.getSourceListBean().getSelectedSource();
        if(selectedSource != null && MessageUtility.displayMessage(ClientMessage
                .SOURCE_DETACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE){
            if(SourceBean.detachFromTransaction(selectedSource.getId())){
                selectedDocumentsPanel.getSourceListBean().removeSelectedSource();
            }
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddDocumentFromApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDocumentFromApplicationActionPerformed
        if(MessageUtility.displayMessage(ClientMessage
                .SOURCE_ATTACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE){
            attachToTransaction(applicationDocumentsPanel.getSourceListBean().getSelectedSource());
        }
    }//GEN-LAST:event_btnAddDocumentFromApplicationActionPerformed

    private void btnAddDocumentFromSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDocumentFromSearchActionPerformed
        if(MessageUtility.displayMessage(ClientMessage
                .SOURCE_ATTACH_TRANSACTION_WARNING) == MessageUtility.BUTTON_ONE){
            attachToTransaction(documentSeachPanel.getSelectedSource());
        }
    }//GEN-LAST:event_btnAddDocumentFromSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.ui.source.DocumentsPanel applicationDocumentsPanel;
    private javax.swing.JButton btnAddDocumentFromApplication;
    private javax.swing.JButton btnAddDocumentFromSearch;
    private javax.swing.JButton btnRemove;
    private org.sola.clients.swing.ui.source.DocumentSeachPanel documentSeachPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblSelectedDocuments;
    private org.sola.clients.swing.ui.source.DocumentsPanel selectedDocumentsPanel;
    // End of variables declaration//GEN-END:variables
}
