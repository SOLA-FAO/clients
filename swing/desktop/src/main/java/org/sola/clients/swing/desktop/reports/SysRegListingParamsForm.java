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
package org.sola.clients.swing.desktop.reports;

import java.awt.ComponentOrientation;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.clients.swing.ui.validation.ValidationResultForm;
import org.sola.common.FileUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author RizzoM
 */
public class SysRegListingParamsForm extends javax.swing.JDialog {

    private String location;
    private String tmpLocation = "";
    private String report;
    private static String cachePath = System.getProperty("user.home") + "/sola/cache/documents/";
    private String reportdate;
    private String reportTogenerate;

    /**
     * Creates new form SysRegListingParamsForm
     */
    public SysRegListingParamsForm(java.awt.Frame parent, boolean modal, String report) {
        super(parent, modal);
        initComponents();
        this.report = report;
        this.setTitle("Report " + report);
        this.txtToDate.setVisible(false);
        this.labNotificationTo.setVisible(false);
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

        Date recDate = (Date) txtFromDate.getValue();
        Date expDate = (Date) txtToDate.getValue();
        String location = this.tmpLocation.replace(" ", "_");

        JRPdfExporter exporterPdf = new JRPdfExporter();

        exporterPdf.setParameter(JRXlsExporterParameter.JASPER_PRINT, populatedReport);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, cachePath + this.reportTogenerate);

        exporterPdf.exportReport();
        FileUtility.saveFileFromStream(null, this.reportTogenerate);

        System.out.println("End download");
        saveDocument(this.reportTogenerate, recDate, this.reportdate, expDate);
        FileUtility.deleteFileFromCache(this.reportTogenerate);

    }

    private void saveDocument(String fileName, Date recDate, String subDate, Date expDate) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String reportdate = formatter.format(recDate);
        String expiration = formatter.format(expDate);
        documentPanel.browseAttachment.setText(fileName);

        for (int i = 0, n = documentPanel.cbxDocType.getItemCount(); i < n; i++) {
            if (documentPanel.cbxDocType.getItemAt(i).toString().contains("Public Notification for Systematic Registration")) {
                documentPanel.cbxDocType.setSelectedIndex(i);
                break;
            }
        }

        documentPanel.txtDocRefNumber.setText(tmpLocation);
        documentPanel.txtDocRecordDate.setText(reportdate);
        documentPanel.txtDocRecordDate.setValue(txtFromDate.getValue());
        documentPanel.txtExpiration.setText(expiration);
        documentPanel.txtExpiration.setValue(txtToDate.getValue());
        documentPanel.txtDescription.setText(this.reportTogenerate);
        DocumentBean document = new DocumentBean();
        File file = new File(cachePath + fileName);
        document = DocumentBean.createDocumentFromLocalFile(file);
        documentPanel.archiveDocument = document;
        documentPanel.saveDocument();

    }

    private void showDocMessage(String fileName) {

        String params = fileName;
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

        parcelNumberListingListBean = new org.sola.clients.beans.systematicregistration.ParcelNumberListingListBean();
        cadastreObjectBean = new org.sola.clients.beans.cadastre.CadastreObjectBean();
        ownerNameListingListBean = new org.sola.clients.beans.systematicregistration.OwnerNameListingListBean();
        stateLandListingListBean = new org.sola.clients.beans.systematicregistration.StateLandListingListBean();
        sourceBean = new org.sola.clients.beans.source.SourceBean();
        documentPanel = new org.sola.clients.swing.ui.source.DocumentPanel();
        labHeader = new javax.swing.JLabel();
        labNotificationFrom = new javax.swing.JLabel();
        txtFromDate = new javax.swing.JFormattedTextField();
        labNotificationTo = new javax.swing.JLabel();
        txtToDate = new javax.swing.JFormattedTextField();
        viewReport = new javax.swing.JButton();
        labLocation = new javax.swing.JLabel();
        btnShowCalendarFrom = new javax.swing.JButton();
        cadastreObjectSearch = new org.sola.clients.swing.ui.cadastre.LocationSearch();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        labHeader.setBackground(new java.awt.Color(255, 153, 0));
        labHeader.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labHeader.setForeground(new java.awt.Color(255, 255, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/reports/Bundle"); // NOI18N
        labHeader.setText(bundle.getString("SysRegListingParamsForm.labHeader.text")); // NOI18N
        labHeader.setOpaque(true);

        labNotificationFrom.setText(bundle.getString("SysRegListingParamsForm.labNotificationFrom.text")); // NOI18N

        txtFromDate.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtFromDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtFromDate.setToolTipText(bundle.getString("SysRegListingParamsForm.txtFromDate.toolTipText")); // NOI18N
        txtFromDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFromDate.setHorizontalAlignment(JTextField.LEADING);
        txtFromDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtFromDatePropertyChange(evt);
            }
        });

        labNotificationTo.setText(bundle.getString("SysRegListingParamsForm.labNotificationTo.text")); // NOI18N

        txtToDate.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtToDate.setEditable(false);
        txtToDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtToDate.setToolTipText(bundle.getString("SysRegListingParamsForm.txtToDate.toolTipText")); // NOI18N
        txtToDate.setEnabled(false);
        txtToDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtToDate.setHorizontalAlignment(JTextField.LEADING);

        viewReport.setText(bundle.getString("SysRegListingParamsForm.viewReport.text")); // NOI18N
        viewReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewReportActionPerformed(evt);
            }
        });

        labLocation.setText(bundle.getString("SysRegListingParamsForm.labLocation.text")); // NOI18N

        btnShowCalendarFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnShowCalendarFrom.setText(bundle.getString("SysRegListingParamsForm.btnShowCalendarFrom.text")); // NOI18N
        btnShowCalendarFrom.setBorder(null);
        btnShowCalendarFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowCalendarFromActionPerformed(evt);
            }
        });

        cadastreObjectSearch.setText(bundle.getString("SysRegListingParamsForm.cadastreObjectSearch.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(labHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labNotificationFrom)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnShowCalendarFrom)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(21, 21, 21)
                                            .addComponent(labNotificationTo))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(18, 18, 18)
                                            .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(cadastreObjectSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(viewReport)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labHeader)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labNotificationFrom)
                    .addComponent(labNotificationTo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labLocation))
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnShowCalendarFrom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(viewReport)
                    .addComponent(cadastreObjectSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(152, Short.MAX_VALUE))
        );

        labHeader.getAccessibleContext().setAccessibleName(bundle.getString("SysRegListingParamsForm.labHeader.text")); // NOI18N
        labNotificationFrom.getAccessibleContext().setAccessibleName(bundle.getString("SysRegListingParamsForm.labNotificationFrom.text")); // NOI18N
        labNotificationTo.getAccessibleContext().setAccessibleName(bundle.getString("SysRegListingParamsForm.labNotificationTo.text")); // NOI18N
        viewReport.getAccessibleContext().setAccessibleName(bundle.getString("SysRegListingParamsForm.viewReport.text")); // NOI18N
        labLocation.getAccessibleContext().setAccessibleName(bundle.getString("SysRegListingParamsForm.labLocation.text")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewReportActionPerformed
        boolean dateFilled = false;
        Date tmpFrom;
        Date tmpTo = (Date) txtFromDate.getValue();
        String reportRequested = "subTitlePN";
        if (cadastreObjectSearch.getSelectedElement() != null) {
            this.location = cadastreObjectSearch.getSelectedElement().toString();
            tmpLocation = (this.location.substring(this.location.indexOf("/") + 1).trim());
        } else {
            MessageUtility.displayMessage(ClientMessage.CHECK_SELECT_LOCATION);
            return;
        }
        if (txtFromDate.getValue() == null) {
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_DATEFROM);
            dateFilled = false;
            return;
        } else {
            tmpFrom = (Date) txtFromDate.getValue();
            dateFilled = true;
        }


        if (dateFilled) {

            Date currentdate = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
            this.reportdate = formatter.format(currentdate);
            this.reportTogenerate = this.report + "_" + tmpLocation + "_" + this.reportdate + ".pdf";
            showDocMessage(this.reportTogenerate);

            if (this.report.contentEquals("Owners")) {
                ownerNameListingListBean.passParameter(tmpLocation);
                if (ownerNameListingListBean.getOwnerNameListing().isEmpty()) {
                    MessageUtility.displayMessage(ClientMessage.NO_REPORT_GENERATION);
                    dateFilled = false;
                    return;
                } else {
                    List<ValidationResultBean> result = null;
                    result = parcelNumberListingListBean.publicDisplay(tmpLocation);
                    String message = MessageUtility.getLocalizedMessage(
                            ClientMessage.REPORT_GENERATION_SUCCESS,
                            new String[]{ownerNameListingListBean.getOwnerNameListing().get(0).getNameLastpart()}).getMessage();
                    openValidationResultForm(result, true, message);

                    try {
                        tmpTo = formatter.parse(addPubliNotificationDuration(txtFromDate.getValue(), ownerNameListingListBean.getOwnerNameListing().get(0).getPublicNotificationDuration()));
                    } catch (ParseException ex) {
                        Logger.getLogger(SysRegListingParamsForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    txtToDate.setText(tmpTo.toString());
                    txtToDate.setValue(tmpTo);
                    showReport(ReportManager.getSysRegPubDisOwnerNameReport(ownerNameListingListBean, tmpFrom, tmpTo, tmpLocation, reportRequested));
                }
            }
            if (this.report.contentEquals("StateLand")) {
                stateLandListingListBean.passParameter(tmpLocation);
                if (stateLandListingListBean.getStateLandListing().isEmpty()) {
                    MessageUtility.displayMessage(ClientMessage.NO_REPORT_GENERATION);
                    dateFilled = false;
                    return;
                } else {
                    List<ValidationResultBean> result = null;
                    result = parcelNumberListingListBean.publicDisplay(tmpLocation);
                    String message = MessageUtility.getLocalizedMessage(
                            ClientMessage.REPORT_GENERATION_SUCCESS,
                            new String[]{stateLandListingListBean.getStateLandListing().get(0).getNameLastpart()}).getMessage();
                    openValidationResultForm(result, true, message);

                    try {
                        tmpTo = formatter.parse(addPubliNotificationDuration(txtFromDate.getValue(), stateLandListingListBean.getStateLandListing().get(0).getPublicNotificationDuration()));
                    } catch (ParseException ex) {
                        Logger.getLogger(SysRegListingParamsForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    txtToDate.setText(tmpTo.toString());
                    txtToDate.setValue(tmpTo);
                    showReport(ReportManager.getSysRegPubDisStateLandReport(stateLandListingListBean, tmpFrom, tmpTo, tmpLocation, reportRequested));
                }
            }
            if (this.report.contentEquals("ParcelNumber")) {
                parcelNumberListingListBean.passParameter(tmpLocation);


                if (parcelNumberListingListBean.getParcelNumberListing().isEmpty()) {
                    MessageUtility.displayMessage(ClientMessage.NO_REPORT_GENERATION);
                    dateFilled = false;
                    return;
                } else {
                    List<ValidationResultBean> result = null;
                    result = parcelNumberListingListBean.publicDisplay(tmpLocation);
                    String message = MessageUtility.getLocalizedMessage(
                            ClientMessage.REPORT_GENERATION_SUCCESS,
                            new String[]{parcelNumberListingListBean.getParcelNumberListing().get(0).getNameLastpart()}).getMessage();
                    openValidationResultForm(result, true, message);

                    try {
                        tmpTo = formatter.parse(addPubliNotificationDuration(txtFromDate.getValue(), parcelNumberListingListBean.getParcelNumberListing().get(0).getPublicNotificationDuration()));
                    } catch (ParseException ex) {
                        Logger.getLogger(SysRegListingParamsForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    txtToDate.setText(tmpTo.toString());
                    txtToDate.setValue(tmpTo);
                    showReport(ReportManager.getSysRegPubDisParcelNameReport(parcelNumberListingListBean, tmpFrom, tmpTo, tmpLocation, reportRequested));
                }
            }

            this.dispose();
        }
    }//GEN-LAST:event_viewReportActionPerformed

    /**
     * Opens dialog form to display status change result for application or
     * service.
     */
    private void openValidationResultForm(List<ValidationResultBean> validationResultList,
            boolean isSuccess, String message) {
        ValidationResultForm resultForm = new ValidationResultForm(
                null, true, validationResultList, isSuccess, message);
        resultForm.setLocationRelativeTo(this);
        resultForm.setVisible(true);
    }

    private String addPubliNotificationDuration(Object dateFrom, String pubDuration) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String dt = sdf.format(dateFrom);

        Calendar c = Calendar.getInstance();
        int duration = Integer.parseInt(pubDuration);
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException ex) {
            Logger.getLogger(SysRegListingParamsForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        c.add(Calendar.DATE, duration);  // number of days to add
        dt = sdf.format(c.getTime());  // dt is now the new date
        return dt;
    }

    private void btnShowCalendarFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowCalendarFromActionPerformed
        showCalendar(txtFromDate);
    }//GEN-LAST:event_btnShowCalendarFromActionPerformed

    private void txtFromDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtFromDatePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFromDatePropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnShowCalendarFrom;
    private org.sola.clients.beans.cadastre.CadastreObjectBean cadastreObjectBean;
    private org.sola.clients.swing.ui.cadastre.LocationSearch cadastreObjectSearch;
    private org.sola.clients.swing.ui.source.DocumentPanel documentPanel;
    private javax.swing.JLabel labHeader;
    private javax.swing.JLabel labLocation;
    private javax.swing.JLabel labNotificationFrom;
    private javax.swing.JLabel labNotificationTo;
    private org.sola.clients.beans.systematicregistration.OwnerNameListingListBean ownerNameListingListBean;
    private org.sola.clients.beans.systematicregistration.ParcelNumberListingListBean parcelNumberListingListBean;
    private org.sola.clients.beans.source.SourceBean sourceBean;
    private org.sola.clients.beans.systematicregistration.StateLandListingListBean stateLandListingListBean;
    private javax.swing.JFormattedTextField txtFromDate;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JButton viewReport;
    // End of variables declaration//GEN-END:variables
}
