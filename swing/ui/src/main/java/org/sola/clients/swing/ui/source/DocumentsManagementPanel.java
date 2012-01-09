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
package org.sola.clients.swing.ui.source;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.jdesktop.application.Action;
import org.sola.clients.swing.ui.application.ApplicationDocumentsForm;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.swing.common.LafManager;

/** 
 * Displays documents list. This panel could be used on different forms, where
 * documents list should be displayed and managed. <p/> 
 * {@link DoumentsPanel} is used to display list of documents.
 */
public class DocumentsManagementPanel extends javax.swing.JPanel {

    private ApplicationBean applicationBean;
    private ApplicationDocumentsForm applicationDocumentsForm;
    private SolaList<SourceBean> sourceList;
    private boolean allowEdit = true;
    
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
        customizeComponents();
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
        customizeComponents();
        customizeButtons(null);
    }
     
    
      /** Applies customization of component L&F. */
    private void customizeComponents() {
       
//    BUTTONS   
    LafManager.getInstance().setBtnProperties(btnAdd);
    LafManager.getInstance().setBtnProperties(btnRemove);
    LafManager.getInstance().setBtnProperties(btnView);
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
        btnAdd.getAction().setEnabled(allowEdit);
        btnRemove.getAction().setEnabled(false);
        btnView.getAction().setEnabled(false);
        
        if(selectedSource!=null){
            btnRemove.getAction().setEnabled(allowEdit);
            if(selectedSource.getArchiveDocumentId()!=null && selectedSource.getArchiveDocumentId().length()>0){
                btnView.getAction().setEnabled(true);
            }
        }
    }
    
    /** Attach file to the selected source. */
    private void attachDocument(PropertyChangeEvent e) {
        SourceBean document = null;
        if (e.getPropertyName().equals(ApplicationDocumentsForm.SELECTED_SOURCE)
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        documentsTablePopupMenu = new javax.swing.JPopupMenu();
        menuAdd = new javax.swing.JMenuItem();
        menuRemove = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        documentsPanel = createDocumentsPanel();

        documentsTablePopupMenu.setName("documentsTablePopupMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(DocumentsManagementPanel.class, this);
        menuAdd.setAction(actionMap.get("addDocument")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        menuAdd.setText(bundle.getString("DocumentsManagementPanel.menuAdd.text")); // NOI18N
        menuAdd.setName("menuAdd"); // NOI18N
        documentsTablePopupMenu.add(menuAdd);

        menuRemove.setAction(actionMap.get("removeDocument")); // NOI18N
        menuRemove.setText(bundle.getString("DocumentsManagementPanel.menuRemove.text")); // NOI18N
        menuRemove.setName("menuRemove"); // NOI18N
        documentsTablePopupMenu.add(menuRemove);

        menuView.setAction(actionMap.get("viewAttachment")); // NOI18N
        menuView.setText(bundle.getString("DocumentsManagementPanel.menuView.text")); // NOI18N
        menuView.setName("menuView"); // NOI18N
        documentsTablePopupMenu.add(menuView);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAdd.setAction(actionMap.get("addDocument")); // NOI18N
        btnAdd.setText(bundle.getString("DocumentsManagementPanel.btnAdd.text")); // NOI18N
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAdd);

        btnRemove.setAction(actionMap.get("removeDocument")); // NOI18N
        btnRemove.setText(bundle.getString("DocumentsManagementPanel.btnRemove.text")); // NOI18N
        btnRemove.setFocusable(false);
        btnRemove.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnRemove);

        btnView.setAction(actionMap.get("viewAttachment")); // NOI18N
        btnView.setText(bundle.getString("DocumentsManagementPanel.btnView.text")); // NOI18N
        btnView.setFocusable(false);
        btnView.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnView.setName("btnView"); // NOI18N
        btnView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnView);

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnView;
    private org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel;
    private javax.swing.JPopupMenu documentsTablePopupMenu;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAdd;
    private javax.swing.JMenuItem menuRemove;
    private javax.swing.JMenuItem menuView;
    // End of variables declaration//GEN-END:variables

    /** Opens file attached to the selected source.*/
    @Action
    public void viewAttachment() {
        documentsPanel.openAttachment();
    }

    /** Removes selected document. */
    @Action
    public void removeDocument() {
        documentsPanel.removeSelectedDocument();
    }

    /** Adds new source into the list. */
    @Action
    public void addDocument() {
        if (applicationDocumentsForm != null) {
            applicationDocumentsForm.dispose();
        }

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                attachDocument(e);
            }
        };

        applicationDocumentsForm = new ApplicationDocumentsForm(applicationBean, null, true);
        applicationDocumentsForm.setLocationRelativeTo(this);
        applicationDocumentsForm.addPropertyChangeListener(
                SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
        applicationDocumentsForm.setVisible(true);
        applicationDocumentsForm.removePropertyChangeListener(
                SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
    }
}
