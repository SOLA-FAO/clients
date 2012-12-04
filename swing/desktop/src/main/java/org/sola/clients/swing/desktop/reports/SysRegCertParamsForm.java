/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.swing.desktop.reports;

import java.awt.ComponentOrientation;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.ReportViewerForm;
import org.sola.clients.swing.desktop.source.DocumentForm;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.common.FileUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;

/**
 *
 * @author RizzoM
 */
public class SysRegCertParamsForm extends javax.swing.JDialog {

    private String location;
    
    private String title;
    private String nr;
    private String tmpLocation = "";
    private static String cachePath = System.getProperty("user.home") + "/sola/cache/documents/";
    private String reportdate;
    private String reportTogenerate;
    private Date   currentDate; 

    /**
     * Creates new form SysRegCertParamsForm
     */
    public SysRegCertParamsForm(java.awt.Frame parent, boolean modal, String nr, String location) {
        super(parent, modal);
        initComponents();
        this.location = location;
        this.nr = nr;
        if (! nr.equals(null)){
            this.title = "Certificate " + nr;
        }
        if (location != null){
            this.title = this.title + location;
        }
        this.setTitle(this.title);
    }

    /**
     * Creates new form SysRegCertParamsForm
     */
    public SysRegCertParamsForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * Opens {@link ReportViewerForm} to display report.
     */
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setVisible(true);
        form.setAlwaysOnTop(true);
        try {
            postProcessReport(report);
        } catch (Exception ex) {
            Logger.getLogger(SysRegListingParamsForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void postProcessReport(JasperPrint populatedReport) throws Exception {

        System.out.println("Inside postProcessReport");

        System.out.println("start download");
//        Date currentdate = new Date(System.currentTimeMillis());
//        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
//        String reportdate = formatter.format(currentdate);

        Date recDate = this.currentDate;
        String location = this.tmpLocation.replace(" ", "_");
//        String reportTogenerate = this.report + "_" + location + "_" + this.reportdate + ".pdf";

        JRPdfExporter exporterPdf = new JRPdfExporter();

        exporterPdf.setParameter(JRXlsExporterParameter.JASPER_PRINT, populatedReport);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, cachePath + this.reportTogenerate);

        exporterPdf.exportReport();
        FileUtility.saveFileFromStream(null, this.reportTogenerate);

        System.out.println("End download");
        saveDocument(this.reportTogenerate, recDate, this.reportdate);
    }

    private void saveDocument(String fileName, Date recDate, String subDate) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String reportdate = formatter.format(recDate);
        documentPanel.browseAttachment.setText(fileName);
        documentPanel.cbxDocType.setSelectedIndex(12);
        documentPanel.txtDocRefNumber.setText(reportdate);
        documentPanel.txtDocRecordDate.setText(reportdate);
        documentPanel.txtDocRecordDate.setValue(this.currentDate);

        DocumentBean document = new DocumentBean();
        File file = new File(cachePath + fileName);
        document = DocumentBean.createDocumentFromLocalFile(file);
        documentPanel.archiveDocument = document;

        documentPanel.btnOk.doClick();
        documentPanel.saveDocument();

    }

    private void showDocMessage(String fileName) {

        String params = cachePath + fileName;
        MessageUtility.displayMessage(ClientMessage.SOURCE_SYS_REP_GENERATED, new Object[]{params});

    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cadastreObjectBean = new org.sola.clients.beans.cadastre.CadastreObjectBean();
        sysRegCertificatesBean = new org.sola.clients.beans.systematicregistration.SysRegCertificatesBean();
        sysRegCertificatesListBean = new org.sola.clients.beans.systematicregistration.SysRegCertificatesListBean();
        documentPanel = new org.sola.clients.swing.ui.source.DocumentPanel();
        cadastreObjectSearch = new org.sola.clients.swing.ui.cadastre.LocationSearch();
        btnGenCertificate = new javax.swing.JButton();
        labHeader = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/reports/Bundle"); // NOI18N
        cadastreObjectSearch.setText(bundle.getString("SysRegListingParamsForm.cadastreObjectSearch.text")); // NOI18N

        btnGenCertificate.setText(bundle.getString("SysRegCertParamsForm.btnGenCertificate.text")); // NOI18N
        btnGenCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenCertificateActionPerformed(evt);
            }
        });

        labHeader.setBackground(new java.awt.Color(255, 153, 0));
        labHeader.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labHeader.setForeground(new java.awt.Color(255, 255, 255));
        labHeader.setText(bundle.getString("SysRegCertParamsForm.labHeader.text")); // NOI18N
        labHeader.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cadastreObjectSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addComponent(labHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenCertificate)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(labHeader)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cadastreObjectSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenCertificate))
                .addContainerGap(203, Short.MAX_VALUE))
        );

        labHeader.getAccessibleContext().setAccessibleName(bundle.getString("SysRegCertParamsForm.labHeader.text")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents
     /**
     * Returns {@link BaUnitBean} by first and last name part.
     */
    private BaUnitBean getBaUnit(String id) {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().getBaUnitById(id);
        return TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, null);
    }
    
    private void btnGenCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenCertificateActionPerformed
        if (cadastreObjectSearch.getSelectedElement() != null) {
            this.location = cadastreObjectSearch.getSelectedElement().toString();
            tmpLocation = (this.location.substring(this.location.indexOf("/") + 1).trim());
        } else {
            MessageUtility.displayMessage(ClientMessage.CHECK_SELECT_LOCATION);
            return;
        }

        Date currentdate = new Date(System.currentTimeMillis());
        this.currentDate = currentdate;
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
        this.reportdate = formatter.format(currentdate);
        this.reportTogenerate = this.nr + "_" + tmpLocation + "_" + this.reportdate + ".pdf";
        showDocMessage(this.reportTogenerate);

        if (!nr.equals(null)) {
            sysRegCertificatesListBean.passParameterApp(tmpLocation, nr);
        } else {
            sysRegCertificatesListBean.passParameter(tmpLocation);
        }
        
        String baUnitId= sysRegCertificatesListBean.getSysRegCertificates().get(0).getBaUnitId();
        
        showReport(ReportManager.getBaUnitReport(getBaUnit(baUnitId)));
       
        
//        showReport(ReportManager.getSysRegCertificatesReport(sysRegCertificatesListBean, nr, tmpLocation));
        this.dispose();

    }//GEN-LAST:event_btnGenCertificateActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenCertificate;
    private org.sola.clients.beans.cadastre.CadastreObjectBean cadastreObjectBean;
    private org.sola.clients.swing.ui.cadastre.LocationSearch cadastreObjectSearch;
    private org.sola.clients.swing.ui.source.DocumentPanel documentPanel;
    private javax.swing.JLabel labHeader;
    private org.sola.clients.beans.systematicregistration.SysRegCertificatesBean sysRegCertificatesBean;
    private org.sola.clients.beans.systematicregistration.SysRegCertificatesListBean sysRegCertificatesListBean;
    // End of variables declaration//GEN-END:variables
}
