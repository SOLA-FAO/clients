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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.swing.ui.source.DocumentPanel;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.swing.ui.source.DocumentSearchPanel;
import org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows select application document.
 */
public class ApplicationDocumentsForm extends javax.swing.JDialog {

    public static final String SELECTED_SOURCE = "selectedSource";
    private SolaList<SourceBean> sourceList;

    private DocumentsPanel createDocumentsPanel() {
        DocumentsPanel panel;
        if (sourceList != null) {
            panel = new DocumentsPanel(sourceList);
        } else {
            panel = new DocumentsPanel();
        }
        return panel;
    }

    /**
     * Creates new instance of form.
     * @param applicationBean {@ApplicationBean} to use on the form to display
     * list of documents.
     */
    public ApplicationDocumentsForm(ApplicationBean applicationBean,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.sourceList = applicationBean.getSourceList();

        initComponents();
        tabs.setTitleAt(0, String.format("Application #%s", applicationBean.getNr()));

        documentPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DocumentPanel.UPDATED_SOURCE) && evt.getNewValue() != null) {
                    fireUpdatedSourceEvent((SourceBean) evt.getNewValue());
                }
            }
        });
        
        documentSearchPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(DocumentSearchPanel.SELECT_SOURCE)){
                    addDocument(SourceBean.getSource(documentSearchPanel.getSelectedSource().getId()));
                }
            }
        });
        
        powerOfAttorneySearchPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(PowerOfAttorneySearchPanel.SELECT_POWER_OF_ATTORNEY)){
                    addDocument(SourceBean.getSource(powerOfAttorneySearchPanel.getSelectedPowerOfAttorney().getId()));
                }
            }
        });
    }

    private void fireUpdatedSourceEvent(SourceBean source) {
        this.firePropertyChange(SELECTED_SOURCE, null, source);
        this.dispose();
    }

    public void addDocument(SourceBean source) {
        if (source == null) {
            MessageUtility.displayMessage(ClientMessage.GENERAL_SELECT_DOCUMENT);
        } else {
            fireUpdatedSourceEvent(source);
        }
    }
    
    /**
     * It disables the tab where a new document can be added.
     * 
     * @param allow 
     */
    public void allowAddingOfNewDocuments(boolean allow){
        tabs.setEnabledAt(1, allow);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        panelApplicationDocs = new javax.swing.JPanel();
        documentsPanel = createDocumentsPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSelect = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        documentPanel = new org.sola.clients.swing.ui.source.DocumentPanel();
        jPanel1 = new javax.swing.JPanel();
        documentSearchPanel = new org.sola.clients.swing.ui.source.DocumentSearchPanel();
        jPanel3 = new javax.swing.JPanel();
        powerOfAttorneySearchPanel = new org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/application/Bundle"); // NOI18N
        setTitle(bundle.getString("ApplicationDocumentsForm.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        tabs.setName("tabs"); // NOI18N

        panelApplicationDocs.setName("panelApplicationDocs"); // NOI18N

        documentsPanel.setName("documentsPanel"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName(bundle.getString("ApplicationDocumentsForm.jToolBar1.name")); // NOI18N

        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        btnSelect.setText(bundle.getString("ApplicationDocumentsForm.btnSelect.text")); // NOI18N
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSelect);

        org.jdesktop.layout.GroupLayout panelApplicationDocsLayout = new org.jdesktop.layout.GroupLayout(panelApplicationDocs);
        panelApplicationDocs.setLayout(panelApplicationDocsLayout);
        panelApplicationDocsLayout.setHorizontalGroup(
            panelApplicationDocsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelApplicationDocsLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelApplicationDocsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, documentsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelApplicationDocsLayout.setVerticalGroup(
            panelApplicationDocsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelApplicationDocsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(documentsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab(bundle.getString("ApplicationDocumentsForm.panelApplicationDocs.TabConstraints.tabTitle"), panelApplicationDocs); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        documentPanel.setName("documentPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(documentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(23, 23, 23)
                .add(documentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(221, Short.MAX_VALUE))
        );

        tabs.addTab(bundle.getString("ApplicationDocumentsForm.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel1.setName(bundle.getString("ApplicationDocumentsForm.jPanel1.name")); // NOI18N

        documentSearchPanel.setName(bundle.getString("ApplicationDocumentsForm.documentSearchPanel.name")); // NOI18N
        documentSearchPanel.setShowAttachButton(false);
        documentSearchPanel.setShowEditButton(false);
        documentSearchPanel.setShowOpenApplicationButton(false);
        documentSearchPanel.setShowPrintButton(false);
        documentSearchPanel.setShowViewButton(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(documentSearchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(documentSearchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab(bundle.getString("ApplicationDocumentsForm.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel3.setName(bundle.getString("ApplicationDocumentsForm.jPanel3.name")); // NOI18N

        powerOfAttorneySearchPanel.setName(bundle.getString("ApplicationDocumentsForm.powerOfAttorneySearchPanel.name")); // NOI18N
        powerOfAttorneySearchPanel.setShowOpenApplicationButton(false);
        powerOfAttorneySearchPanel.setShowViewButton(false);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(powerOfAttorneySearchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(powerOfAttorneySearchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab(bundle.getString("ApplicationDocumentsForm.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tabs)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tabs)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        if(documentsPanel.getSourceListBean().getSelectedSource()==null){
            MessageUtility.displayMessage(ClientMessage.GENERAL_SELECT_DOCUMENT);
        } else{
            addDocument((SourceBean)documentsPanel.getSourceListBean().getSelectedSource().copy());
        }
    }//GEN-LAST:event_btnSelectActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelect;
    private org.sola.clients.swing.ui.source.DocumentPanel documentPanel;
    private org.sola.clients.swing.ui.source.DocumentSearchPanel documentSearchPanel;
    private org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panelApplicationDocs;
    private org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel powerOfAttorneySearchPanel;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
