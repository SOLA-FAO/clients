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
package org.sola.clients.swing.ui.source;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.common.RolesConstants;

/** 
 * Displays documents list. This panel could be used on different forms, where
 * documents list should be displayed and managed. <p/> 
 * {@link DoumentsPanel} is used to display list of documents.
 */
public class DocumentsManagementPanel extends javax.swing.JPanel {

    public static final String VIEW_DOCUMENT = "viewDocument";
    public static final String EDIT_DOCUMENT = "editDocument";
    
    private ApplicationBean applicationBean;
    private AddDocumentForm addDocumentForm;
    private SolaList<SourceBean> sourceList;
    private boolean allowEdit = true;
    private boolean allowAddingOfNewDocuments = true;
    private boolean closeAddDocumentFormOnAdd = false;
    
    /** Creates new instance of {@link DocumentsPanel}. */
    private DocumentsPanel createDocumentsPanel() {
        DocumentsPanel panel;
        if (sourceList != null) {
            panel = new DocumentsPanel(sourceList);
        } else {
            panel = new DocumentsPanel();
        }
        
        panel.getSourceListBean().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(SourceListBean.SELECTED_SOURCE_PROPERTY)){
                    customizeButtons((SourceBean)evt.getNewValue());
                }
            }
        });
        
        return panel;
    }
    
    /** Default constructor */
    public DocumentsManagementPanel() {
        initComponents();
        customizeButtons(null);
    }

    /** 
     * This constructor is called to display predefined list of {@link SourceBean}.
     * @param sourceList The list of {@link SourceBean} to display.
     * @param applicationBean {ApplicationBean} is used to attach documents from
     * the application.
     * @param allowEdit Indicates whether it is allowed to do changes on the list.
     */
    public DocumentsManagementPanel(SolaList<SourceBean> sourceList,
            ApplicationBean applicationBean, boolean allowEdit) {
        this.applicationBean = applicationBean;
        this.sourceList = sourceList;
        this.allowEdit = allowEdit;
        initComponents();
        customizeButtons(null);
    }
    
    /** 
     * This constructor is called to load list of {@link SourceBean} by the 
     * given list of IDs.
     * @param sourceIds The list of source ID to use for loading relative 
     * {@link SourceBean} list.
     * @param applicationBean {ApplicationBean} is used to attach documents from
     * the application.
     * @param allowEdit Indicates whether it is allowed to do changes on the list.
     */
    public DocumentsManagementPanel(List<String> sourceIds,
            ApplicationBean applicationBean, boolean allowEdit) {
        this.applicationBean = applicationBean;
        this.allowEdit = allowEdit;
        initComponents();
        loadSourcesByIds(sourceIds);
        customizeButtons(null);
    }
    
    /** Enables or disables buttons, depending on the selection in the list. */
    private void customizeButtons(SourceBean selectedSource){
        btnAdd.setEnabled(allowEdit);
        btnRemove.setEnabled(false);
        btnViewAttachmanet.setEnabled(false);
        btnEdit.setEnabled(false);
        btnView.setEnabled(false);
        
        if(selectedSource!=null){
            btnRemove.setEnabled(allowEdit);
            btnEdit.setEnabled(allowEdit && SecurityBean.isInRole(RolesConstants.SOURCE_SAVE));
            btnView.setEnabled(true);
            if(selectedSource.getArchiveDocumentId()!=null && selectedSource.getArchiveDocumentId().length()>0){
                btnViewAttachmanet.setEnabled(true);
            }
        }
        
        menuAdd.setEnabled(btnAdd.isEnabled());
        menuRemove.setEnabled(btnRemove.isEnabled());
        menuView.setEnabled(btnView.isEnabled());
        menuEdit.setEnabled(btnEdit.isEnabled());
        menuViewAttachment.setEnabled(btnViewAttachmanet.isEnabled());
    }

    private void customizeSeparators(){
        if((!menuView.isVisible() && !menuViewAttachment.isVisible()) ||
                (!menuAdd.isVisible() && !menuEdit.isVisible() && !menuRemove.isVisible())){
            menuSeparator.setVisible(false);
            toolbarSeparator.setVisible(false);
        } else {
            menuSeparator.setVisible(true);
            toolbarSeparator.setVisible(true);
        }
    }
    
    public boolean isAddButtonVisible(){
        return btnAdd.isVisible();
    }
    
    public void setAddButtonVisible(boolean visible){
        btnAdd.setVisible(visible);
        menuAdd.setVisible(visible);
        customizeSeparators();
    }
    
    public boolean isEditButtonVisible(){
        return btnEdit.isVisible();
    }
    
    public void setEditButtonVisible(boolean visible){
        btnEdit.setVisible(visible);
        menuEdit.setVisible(visible);
        customizeSeparators();
    }
    
    public boolean isRemoveButtonVisible(){
        return btnRemove.isVisible();
    }
    
    public void setRemoveButtonVisible(boolean visible){
        btnRemove.setVisible(visible);
        menuRemove.setVisible(visible);
        customizeSeparators();
    }
    
    public boolean isViewButtonVisible(){
        return btnView.isVisible();
    }
    
    public void setViewButtonVisible(boolean visible){
        btnView.setVisible(visible);
        menuView.setVisible(visible);
        customizeSeparators();
    }
    
    public boolean isOpenAttachmentButtonVisible(){
        return btnViewAttachmanet.isVisible();
    }
    
    public void setOpenAttachmentButtonVisible(boolean visible){
        btnViewAttachmanet.setVisible(visible);
        menuViewAttachment.setVisible(visible);
        customizeSeparators();
    }
    
    /** Indicates whether {@link AddDocumentForm} should be closed upon add document action. */
    public boolean isCloseAddDocumentFormOnAdd() {
        return closeAddDocumentFormOnAdd;
    }

    /** Returns boolean indicating whether {@link AddDocumentForm} should be closed upon add document action. */
    public void setCloseAddDocumentFormOnAdd(boolean closeAddDocumentFormOnAdd) {
        this.closeAddDocumentFormOnAdd = closeAddDocumentFormOnAdd;
    }

    public boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
        customizeButtons(documentsPanel.getSourceListBean().getSelectedSource());
    }
    
    /** Attach file to the selected source. */
    private void attachDocument(PropertyChangeEvent e) {
        SourceBean document = null;
        if (e.getPropertyName().equals(AddDocumentForm.SELECTED_SOURCE)
                && e.getNewValue() != null) {
            document = (SourceBean) e.getNewValue();
            documentsPanel.addDocument(document);
        }
    }

    /** Loads sources by the given list of IDs. */
    public final void loadSourcesByIds(List<String> sourceIds){
        documentsPanel.loadSourcesByIds(sourceIds);
    }
    
    /** 
     * Returns the list of sources IDs. 
     @param onlyFiltered Indicates whether to return IDs only from the filtered 
     * list. If {@code false}, returns all IDs.
     */
    public final List<String> getSourceIds(boolean onlyFiltered){
        return documentsPanel.getSourceIds(onlyFiltered);
    }
    
    /**
     * Gets the source list bean
     * 
     * @return
     */
    public final SourceListBean getSourceListBean(){
        return documentsPanel.getSourceListBean();
    }
    
    /**
     * Sets the property to allow new documents that are not defined in 
     * application to be added in the list.
     * 
     * @param allow 
     */
    public void setAllowAddingOfNewDocuments(boolean allow){
        allowAddingOfNewDocuments = allow;
    }
    
    private void viewDocument(){
        firePropertyChange(VIEW_DOCUMENT, null, documentsPanel.getSourceListBean().getSelectedSource());
    }
    
    private void editDocument(){
        firePropertyChange(EDIT_DOCUMENT, null, documentsPanel.getSourceListBean().getSelectedSource());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        documentsTablePopupMenu = new javax.swing.JPopupMenu();
        menuAdd = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenuItem();
        menuRemove = new javax.swing.JMenuItem();
        menuSeparator = new javax.swing.JPopupMenu.Separator();
        menuView = new javax.swing.JMenuItem();
        menuViewAttachment = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        toolbarSeparator = new javax.swing.JToolBar.Separator();
        btnView = new javax.swing.JButton();
        btnViewAttachmanet = new javax.swing.JButton();
        documentsPanel = createDocumentsPanel();

        documentsTablePopupMenu.setName("documentsTablePopupMenu"); // NOI18N

        menuAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        menuAdd.setText(bundle.getString("DocumentsManagementPanel.menuAdd.text")); // NOI18N
        menuAdd.setName("menuAdd"); // NOI18N
        menuAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddActionPerformed(evt);
            }
        });
        documentsTablePopupMenu.add(menuAdd);

        menuEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEdit.setText(bundle.getString("DocumentsManagementPanel.menuEdit.text")); // NOI18N
        menuEdit.setName(bundle.getString("DocumentsManagementPanel.menuEdit.name")); // NOI18N
        menuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditActionPerformed(evt);
            }
        });
        documentsTablePopupMenu.add(menuEdit);

        menuRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemove.setText(bundle.getString("DocumentsManagementPanel.menuRemove.text")); // NOI18N
        menuRemove.setName("menuRemove"); // NOI18N
        menuRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveActionPerformed(evt);
            }
        });
        documentsTablePopupMenu.add(menuRemove);

        menuSeparator.setName(bundle.getString("DocumentsManagementPanel.menuSeparator.name")); // NOI18N
        documentsTablePopupMenu.add(menuSeparator);

        menuView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuView.setText(bundle.getString("DocumentsManagementPanel.menuView.text")); // NOI18N
        menuView.setName(bundle.getString("DocumentsManagementPanel.menuView.name")); // NOI18N
        menuView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewActionPerformed(evt);
            }
        });
        documentsTablePopupMenu.add(menuView);

        menuViewAttachment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuViewAttachment.setText(bundle.getString("DocumentsManagementPanel.menuViewAttachment.text")); // NOI18N
        menuViewAttachment.setName("menuViewAttachment"); // NOI18N
        menuViewAttachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewAttachmentActionPerformed(evt);
            }
        });
        documentsTablePopupMenu.add(menuViewAttachment);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAdd.setText(bundle.getString("DocumentsManagementPanel.btnAdd.text")); // NOI18N
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAdd);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEdit.setText(bundle.getString("DocumentsManagementPanel.btnEdit.text")); // NOI18N
        btnEdit.setFocusable(false);
        btnEdit.setName(bundle.getString("DocumentsManagementPanel.btnEdit.name")); // NOI18N
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemove.setText(bundle.getString("DocumentsManagementPanel.btnRemove.text")); // NOI18N
        btnRemove.setFocusable(false);
        btnRemove.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemove);

        toolbarSeparator.setName(bundle.getString("DocumentsManagementPanel.toolbarSeparator.name")); // NOI18N
        jToolBar1.add(toolbarSeparator);

        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnView.setText(bundle.getString("DocumentsManagementPanel.btnView.text")); // NOI18N
        btnView.setFocusable(false);
        btnView.setName(bundle.getString("DocumentsManagementPanel.btnView.name")); // NOI18N
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnView);

        btnViewAttachmanet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnViewAttachmanet.setText(bundle.getString("DocumentsManagementPanel.btnViewAttachmanet.text")); // NOI18N
        btnViewAttachmanet.setFocusable(false);
        btnViewAttachmanet.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewAttachmanet.setName("btnViewAttachmanet"); // NOI18N
        btnViewAttachmanet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewAttachmanetActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewAttachmanet);

        documentsPanel.setName("documentsPanel"); // NOI18N
        documentsPanel.setPopupMenu(documentsTablePopupMenu);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
            .add(documentsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(documentsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addDocument();
    }//GEN-LAST:event_btnAddActionPerformed

    private void menuAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddActionPerformed
        addDocument();
    }//GEN-LAST:event_menuAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        removeDocument();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void menuRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveActionPerformed
        removeDocument();
    }//GEN-LAST:event_menuRemoveActionPerformed

    private void btnViewAttachmanetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewAttachmanetActionPerformed
        viewAttachment();
    }//GEN-LAST:event_btnViewAttachmanetActionPerformed

    private void menuViewAttachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewAttachmentActionPerformed
        viewAttachment();
    }//GEN-LAST:event_menuViewAttachmentActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        viewDocument();
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editDocument();
    }//GEN-LAST:event_btnEditActionPerformed

    private void menuViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewActionPerformed
        viewDocument();
    }//GEN-LAST:event_menuViewActionPerformed

    private void menuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditActionPerformed
        editDocument();
    }//GEN-LAST:event_menuEditActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnView;
    private javax.swing.JButton btnViewAttachmanet;
    private org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel;
    private javax.swing.JPopupMenu documentsTablePopupMenu;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAdd;
    private javax.swing.JMenuItem menuEdit;
    private javax.swing.JMenuItem menuRemove;
    private javax.swing.JPopupMenu.Separator menuSeparator;
    private javax.swing.JMenuItem menuView;
    private javax.swing.JMenuItem menuViewAttachment;
    private javax.swing.JToolBar.Separator toolbarSeparator;
    // End of variables declaration//GEN-END:variables

    /** Opens file attached to the selected source.*/
    private void viewAttachment() {
        documentsPanel.viewAttachment();
    }

    /** Removes selected document. */
    private void removeDocument() {
        documentsPanel.removeSelectedDocument();
    }

    /** Adds new source into the list. */
    private void addDocument() {
        if (addDocumentForm != null) {
            addDocumentForm.dispose();
        }

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                attachDocument(e);
            }
        };

        addDocumentForm = new AddDocumentForm(applicationBean, null, true);
        addDocumentForm.setLocationRelativeTo(this);
        addDocumentForm.addPropertyChangeListener(SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
        addDocumentForm.allowAddingOfNewDocuments(allowAddingOfNewDocuments);
        addDocumentForm.setVisible(true);
        addDocumentForm.removePropertyChangeListener(SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
    }
}
