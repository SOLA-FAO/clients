/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.gis.data.ExternalFileImporterSurveyPointBeans;
import org.sola.clients.swing.gis.layer.AbstractSpatialObjectLayer;
import org.sola.clients.swing.gis.layer.CadastreChangeNewSurveyPointLayer;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForTransaction;
import org.sola.clients.swing.ui.source.DocumentsManagementPanel;
import org.sola.common.FileUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Panel that is used to manage the documents used during GIS related transactions. This panel
 * offers also the functionality of adding points from an attachment of a document to the map layer
 * of survey points.
 *
 * @author Elton Manoku
 */
public class MapDocumentsPanel extends javax.swing.JPanel {

    private ApplicationBean applicationBean;
    private ControlsBundleForTransaction mapControl;
    private AbstractSpatialObjectLayer layerToImportGeometries;
    private String recognizedExtensionForImportFile = "csv";
    private String selectedDocumentId;
    private String selectedDocumentFileName;

    /**
     * Creates new form MapDocumentsPanel. This is not used. To create the panel, use the other
     * constructor.
     */
    public MapDocumentsPanel() {
        initComponents();
    }

    /**
     * Constructor that is called from code to create the panel. If the map contains the survey
     * point layer, the add point button is made visible.
     *
     * @param mapControl The bundle of map controls where the panel will be embedded
     * @param applicationBean The application bean where the sources are found
     */
    public MapDocumentsPanel(
            ControlsBundleForTransaction mapControl, ApplicationBean applicationBean) {
        this.mapControl = mapControl;
        this.applicationBean = applicationBean;
        initComponents();
        this.layerToImportGeometries =
                (AbstractSpatialObjectLayer) this.mapControl.getMap().getSolaLayers().get(
                CadastreChangeNewSurveyPointLayer.LAYER_NAME);
        cmdAddInMap.setVisible(this.layerToImportGeometries != null);
    }

    private DocumentsManagementPanel createDocumentsPanel() {
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }

        boolean allowEdit = true;
        boolean allowAddingOfNewDocuments = false;

        DocumentsManagementPanel panel = new DocumentsManagementPanel(
                new ArrayList<String>(), applicationBean, allowEdit);
        panel.getSourceListBean().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(SourceListBean.SELECTED_SOURCE_PROPERTY)) {
                    customizeButtons((SourceBean) evt.getNewValue());
                }
            }
        });
        panel.setAllowAddingOfNewDocuments(allowAddingOfNewDocuments);
        panel.setEditButtonVisible(false);
        panel.setViewButtonVisible(false);
        return panel;
    }

    /**
     * It enables the button that starts the import if the selected source has an attachment, the
     * attachment is of recognized extension.
     *
     * @param selectedSource The selected source
     */
    private void customizeButtons(SourceBean selectedSource) {
        cmdAddInMap.setEnabled(false);
        pnlImportPointAvailable.setEnabled(false);
        if (selectedSource == null || selectedSource.getArchiveDocument() == null) {
            return; 
        }
        DocumentBean documentBean = selectedSource.getArchiveDocument();
        if (documentBean == null) {
            //No attachement
            return;
        }
        if (!documentBean.getExtension().equals(this.recognizedExtensionForImportFile)) {
            //Attachement must be of recognized extension
            return;
        }

        this.selectedDocumentId = documentBean.getId();
        this.selectedDocumentFileName = documentBean.getFileName();
        cmdAddInMap.setEnabled(true);
        pnlImportPointAvailable.setEnabled(true);
    }

    /**
     * Sets list of source ids
     *
     * @param ids
     */
    public final void setSourceIds(List<String> ids) {
        documentsPanel.loadSourcesByIds(ids);
    }

    /**
     * Gets list of source ids
     *
     * @return
     */
    public final List getSourceIds() {
        return documentsPanel.getSourceIds(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        documentsPanel = createDocumentsPanel();
        cmdAddInMap = new javax.swing.JButton();
        pnlImportPointAvailable = new javax.swing.JPanel();
        lblFormatFileExplaination = new javax.swing.JLabel();
        txtLineIndexWherePointsStart = new javax.swing.JTextField();
        lblLineNumberOfFirstPoint = new javax.swing.JLabel();

        jScrollPane1.setViewportView(documentsPanel);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        cmdAddInMap.setText(bundle.getString("MapDocumentsPanel.cmdAddInMap.text")); // NOI18N
        cmdAddInMap.setEnabled(false);
        cmdAddInMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddInMapActionPerformed(evt);
            }
        });

        lblFormatFileExplaination.setText("<html>The file is expected to be of the format:<br>\nFirst column: Point id <br>\nSecond column: X coordinate <br>\nThird column: Y coordinate.\n</html>");

        txtLineIndexWherePointsStart.setText("1");

        lblLineNumberOfFirstPoint.setText("The line number of the first point:");

        javax.swing.GroupLayout pnlImportPointAvailableLayout = new javax.swing.GroupLayout(pnlImportPointAvailable);
        pnlImportPointAvailable.setLayout(pnlImportPointAvailableLayout);
        pnlImportPointAvailableLayout.setHorizontalGroup(
            pnlImportPointAvailableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportPointAvailableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlImportPointAvailableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFormatFileExplaination, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .addGroup(pnlImportPointAvailableLayout.createSequentialGroup()
                        .addComponent(lblLineNumberOfFirstPoint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLineIndexWherePointsStart, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlImportPointAvailableLayout.setVerticalGroup(
            pnlImportPointAvailableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportPointAvailableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFormatFileExplaination, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlImportPointAvailableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLineIndexWherePointsStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLineNumberOfFirstPoint)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cmdAddInMap))
                    .addComponent(pnlImportPointAvailable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(pnlImportPointAvailable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmdAddInMap)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAddInMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddInMapActionPerformed
        //Identifies the layer where the points will be added
        final AbstractSpatialObjectLayer pointLayer = this.layerToImportGeometries;

        //The button is enabled only if there is already a selected source which has
        // an attachment of a recognized extension.
        // So there is no need to check for the attachment.

        final String documentId = selectedDocumentId;
        final String documentFileName = selectedDocumentFileName;
        final int lineIndexToStartFrom =
                Integer.parseInt(this.txtLineIndexWherePointsStart.getText());
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(
                        ClientMessage.PROGRESS_MSG_DOCUMENT_OPENING));
                if (!FileUtility.isCached(documentFileName)) {
                    WSManager.getInstance().getDigitalArchive().getDocument(documentId);
                }
                String fileName = FileUtility.sanitizeFileName(documentFileName, true);
                String absoluteFilePath = FileUtility.getCachePath() + File.separator + fileName;
                ExternalFileImporterSurveyPointBeans.getInstance().setLineIndexToStartFrom(
                        lineIndexToStartFrom);
                List pointBeans = ExternalFileImporterSurveyPointBeans.getInstance().getBeans(
                        absoluteFilePath);
                pointLayer.getBeanList().addAll(pointBeans);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);

    }//GEN-LAST:event_cmdAddInMapActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAddInMap;
    private org.sola.clients.swing.ui.source.DocumentsManagementPanel documentsPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFormatFileExplaination;
    private javax.swing.JLabel lblLineNumberOfFirstPoint;
    private javax.swing.JPanel pnlImportPointAvailable;
    private javax.swing.JTextField txtLineIndexWherePointsStart;
    // End of variables declaration//GEN-END:variables
}
