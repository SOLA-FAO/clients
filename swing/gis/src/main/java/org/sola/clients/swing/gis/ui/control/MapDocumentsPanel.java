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

        jScrollPane1.setViewportView(documentsPanel);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        cmdAddInMap.setText(bundle.getString("MapDocumentsPanel.cmdAddInMap.text")); // NOI18N
        cmdAddInMap.setEnabled(false);
        cmdAddInMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddInMapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 159, Short.MAX_VALUE)
                        .addComponent(cmdAddInMap)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
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
    // End of variables declaration//GEN-END:variables
}
