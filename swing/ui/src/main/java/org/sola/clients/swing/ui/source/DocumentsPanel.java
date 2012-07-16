/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.ui.source;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JPopupMenu;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.renderers.AttachedDocumentCellRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Displays documents list. This panel could be used on different forms, where
 * documents list is needed to display in the read only mode.<p/>
 * {@link SourceListBean} is used to bind the data on the panel.
 */
public class DocumentsPanel extends javax.swing.JPanel {

    public static final String SELECTED_SOURCE = "selectedSource";

    /**
     * Creates {@link SourceListBean} to bind data on the panel.
     */
    private SourceListBean createSourceListBean() {
        if (sourceListBean == null) {
            sourceListBean = new SourceListBean();
        }
        return sourceListBean;
    }

    /**
     * Default constructor.
     */
    public DocumentsPanel() {
        initComponents();
        postInit();
    }

    /**
     * Constructs panel and loads sources by the list of given Ids.
     *
     * @param sourceIds List of IDs to use for loading sources.
     */
    public DocumentsPanel(List<String> sourceIds) {
        createSourceListBean();
        loadSourcesByIds(sourceIds);
        initComponents();
        postInit();
    }

    /**
     * Constructs panel and binds provided list sources.
     *
     * @param sourceList List of sources to bind on the panel.
     */
    public DocumentsPanel(SolaList<SourceBean> sourceList) {
        createSourceListBean();

        if (sourceList != null) {
            sourceListBean.setSourceBeanList(sourceList);
        }
        initComponents();
        postInit();
    }

    /**
     * Returns popup menu, bounded to the table.
     */
    public JPopupMenu getPopupMenu() {
        return tableDocuments.getComponentPopupMenu();
    }

    /**
     * Sets popup menu to bind on the table.
     */
    public void setPopupMenu(JPopupMenu popup) {
        tableDocuments.setComponentPopupMenu(popup);
    }

    /**
     * Makes post initialization tasks to bind listener on {@link SourceListBean}.
     */
    private void postInit() {
        sourceListBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(SourceListBean.SELECTED_SOURCE_PROPERTY)) {
                    firePropertyChange(SELECTED_SOURCE, null, evt.getNewValue());
                }
            }
        });
    }

    /**
     * Loads sources by the given list of IDs.
     */
    public final void loadSourcesByIds(List<String> sourceIds) {
        sourceListBean.loadSourceByIds(sourceIds);
    }

    /**
     * Returns the list of sources IDs.
     *
     * @param onlyFiltered Indicates whether to return IDs only from the
     * filtered list. If {@code false}, returns all IDs.
     */
    public final List<String> getSourceIds(boolean onlyFiltered) {
        return sourceListBean.getSourceIds(onlyFiltered);
    }

    /**
     * Returns underlying {@link SourceListBean}, displayed in the table.
     */
    public SourceListBean getSourceListBean() {
        return sourceListBean;
    }

    /**
     * Sets underlying {@link SourceListBean}, to be displayed in the table.
     */
    public void setSourceList(List<SourceBean> sourceList) {
        sourceListBean.getSourceBeanList().clear();
        for (SourceBean sourceBean : sourceList) {
            sourceListBean.getSourceBeanList().addAsNew(sourceBean);
        }
    }

    /**
     * Opens attached digital copy of document in the document's list.
     */
    public void openAttachment() {
        if (sourceListBean.getSelectedSource() != null
                && sourceListBean.getSelectedSource().getArchiveDocument() != null) {
            // Try to open attached file
            SolaTask t = new SolaTask<Void, Void>() {

                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_OPENING));
                    DocumentBean.openDocument(sourceListBean.getSelectedSource().getArchiveDocument().getId(),
                            sourceListBean.getSelectedSource().getArchiveDocument().getFileName());
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    /**
     * Removes selected document.
     */
    public void removeSelectedDocument() {
        if (sourceListBean.getSelectedSource() != null) {
            if (MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD)
                    == MessageUtility.BUTTON_ONE) {
                sourceListBean.safeRemoveSelectedSource();
            }
        }
    }

    /**
     * Adds new source into the list.
     */
    public void addDocument(SourceBean document) {
        sourceListBean.getSourceBeanList().addAsNew(document);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        sourceListBean = createSourceListBean();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDocuments = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableDocuments.setName("tableDocuments"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredSourceBeanList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sourceListBean, eLProperty, tableDocuments);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sourceType.displayValue}"));
        columnBinding.setColumnName("Source Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${laNr}"));
        columnBinding.setColumnName("La Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${acceptance}"));
        columnBinding.setColumnName("Acceptance");
        columnBinding.setColumnClass(java.util.Date.class);
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${archiveDocumentId}"));
        columnBinding.setColumnName("Archive Document Id");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sourceListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedSource}"), tableDocuments, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableDocuments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableDocumentsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableDocuments);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        tableDocuments.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("DocumentsPanel.tableDocuments.columnModel.title0_1")); // NOI18N
        tableDocuments.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("DocumentsPanel.tableDocuments.columnModel.title1_1")); // NOI18N
        tableDocuments.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("DocumentsPanel.tableDocuments.columnModel.title2_1")); // NOI18N
        tableDocuments.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("DocumentsPanel.tableDocuments.columnModel.title3_1")); // NOI18N
        tableDocuments.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("DocumentsPanel.tableDocuments.columnModel.title4")); // NOI18N
        tableDocuments.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("DocumentsPanel.tableDocuments.columnModel.title5")); // NOI18N
        tableDocuments.getColumnModel().getColumn(6).setPreferredWidth(30);
        tableDocuments.getColumnModel().getColumn(6).setMaxWidth(30);
        tableDocuments.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("DocumentsPanel.tableDocuments.columnModel.title6")); // NOI18N
        tableDocuments.getColumnModel().getColumn(6).setCellRenderer(new AttachedDocumentCellRenderer());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void tableDocumentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableDocumentsMouseClicked
        if (evt.getClickCount() == 2) {
            openAttachment();
        }
    }//GEN-LAST:event_tableDocumentsMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private org.sola.clients.beans.source.SourceListBean sourceListBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableDocuments;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
