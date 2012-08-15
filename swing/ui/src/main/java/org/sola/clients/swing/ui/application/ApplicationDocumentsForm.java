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
    }

    private void fireUpdatedSourceEvent(SourceBean source) {
        this.firePropertyChange(SELECTED_SOURCE, null, source);
        this.dispose();
    }

    public void addDocument() {
        if (documentsPanel.getSourceListBean().getSelectedSource() == null) {
            MessageUtility.displayMessage(ClientMessage.GENERAL_SELECT_DOCUMENT);
        } else {
            fireUpdatedSourceEvent((SourceBean) documentsPanel.getSourceListBean().getSelectedSource().copy());
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
        btnAdd = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        documentPanel = new org.sola.clients.swing.ui.source.DocumentPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/application/Bundle"); // NOI18N
        setTitle(bundle.getString("ApplicationDocumentsForm.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        tabs.setName("tabs"); // NOI18N

        panelApplicationDocs.setName("panelApplicationDocs"); // NOI18N

        documentsPanel.setName("documentsPanel"); // NOI18N

        btnAdd.setText(bundle.getString("ApplicationDocumentsForm.btnAdd.text")); // NOI18N
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout panelApplicationDocsLayout = new org.jdesktop.layout.GroupLayout(panelApplicationDocs);
        panelApplicationDocs.setLayout(panelApplicationDocsLayout);
        panelApplicationDocsLayout.setHorizontalGroup(
            panelApplicationDocsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelApplicationDocsLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelApplicationDocsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(documentsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 529, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        panelApplicationDocsLayout.setVerticalGroup(
            panelApplicationDocsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelApplicationDocsLayout.createSequentialGroup()
                .addContainerGap()
                .add(documentsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnAdd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
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
                .add(documentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(23, 23, 23)
                .add(documentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        tabs.addTab(bundle.getString("ApplicationDocumentsForm.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tabs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 557, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tabs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addDocument();
    }//GEN-LAST:event_btnAddActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private org.sola.clients.swing.ui.source.DocumentPanel documentPanel;
    private org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel panelApplicationDocs;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
