/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.geotools.swing.extended.util.MapImageGenerator;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.extended.util.ScalebarGenerator;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForPublicDisplay;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.clients.swing.ui.reports.SaveFormat;
import org.sola.common.StringUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This panel provides the necessary user interface to manage and start the
 * printing of the public display map. Because the functionality is about
 * printing according to a certain layout, some functionality is duplicated from
 * the print functionality.
 *
 * @author Elton Manoku
 */
public class PublicDisplayPrintPanel extends javax.swing.JPanel {

    private static final String RESOURCE_LAYOUT
            = "layouts_public_display.properties";
    //This is the DPI. Normally the printer has a DPI, the monitor has a DPI. 
    //Also Jasper engine should have a DPI, which is 72. With this DPI pixels and points are equal
    private static int DPI = 72;
    private List<PrintLayout> printLayoutList;
    private PrintLayout printLayout;
    private ControlsBundleForPublicDisplay mapBundle;
    private ApplicationServiceBean appService;

    /**
     * Creates new form PublicDisplayPrintPanel
     */
    public PublicDisplayPrintPanel(ControlsBundleForPublicDisplay mapBundle,
            ApplicationServiceBean appService) {
        this.appService = appService;
        initComponents();
        setPrintLayoutList();
        this.mapBundle = mapBundle;
    }
    
    /**
     * Enables or disables the print panel controls based on the readonly value. 
     * @param readOnly 
     */
    public void setReadOnly(boolean readOnly) {
        boolean enabled = !readOnly;
        cmbLayoutList.setEnabled(enabled);
        txtScale.setEnabled(enabled);
        txtArea.setEnabled(enabled);
        txtNotificationPeriod.setEnabled(enabled);
        txtMapCenterLabel.setEnabled(enabled);
        cmdPrint.setEnabled(enabled);
    }

    /**
     * Sets the list of layouts.
     */
    private void setPrintLayoutList() {

        this.printLayoutList = getPrintLayouts();
        this.cmbLayoutList.removeAllItems();
        for (PrintLayout layout : this.printLayoutList) {
            this.cmbLayoutList.addItem(layout);
        }
    }

    /**
     * Gets the list of available print layouts. The print layouts are defined
     * in resources/print/layouts.properties. <br/> If another source of layout
     * has to be defined, this method has to be overridden.
     *
     * @return
     */
    private List<PrintLayout> getPrintLayouts() {
        Properties propertyLayouts = new Properties();
        String resourceLocation = String.format("/%s/%s",
                this.getClass().getPackage().getName().replace('.', '/'),
                RESOURCE_LAYOUT);
        List<PrintLayout> layoutList = new ArrayList<PrintLayout>();

        try {
            propertyLayouts.load(this.getClass().getResourceAsStream(resourceLocation));
            for (String layoutId : propertyLayouts.stringPropertyNames()) {
                PrintLayout layout = new PrintLayout(
                        layoutId, propertyLayouts.getProperty(layoutId));
                layoutList.add(layout);
            }
        } catch (IOException ex) {
            //Not important to catch
        }
        return layoutList;
    }

    /**
     * Gets the map image that will be embedded in the report
     *
     * @param mapImageWidth
     * @param mapImageHeight
     * @param scale
     * @param dpi
     * @return
     * @throws IOException
     */
    private String getMapImage(Double mapImageWidth, Double mapImageHeight,
            double scale, int dpi) throws IOException {

        //Set the filter for the layers of type PojoPublicDisplay
        String imageFormat = "png";
        MapImageGenerator mapImageGenerator = new MapImageGenerator(
                this.mapBundle.getMap().getMapContent());
        if (!StringUtility.isEmpty(this.txtMapCenterLabel.getText())) {
            mapImageGenerator.setTextInTheMapCenter(this.txtMapCenterLabel.getText());
        }
        //This gives back the absolute location of the map image. 
        String mapImageLocation = mapImageGenerator.getImageAsFileLocation(
                mapImageWidth, mapImageHeight, scale, dpi, imageFormat);

        return mapImageLocation;
    }

    /**
     * Additionally to the standard functionality of printing, it supplies the
     * values of user and date to the layout so it can print them as well. Also
     * if the print succeeds it logs against the application the action of
     * printing if the application id is present.
     *
     *
     * @param layout A layout identifier. This is used to distinguish between
     * many layouts the user can choose from and to call the report for that
     * layout
     * @param scale This is the scale of the map for which the print will be
     * done
     */
    private void printPublicDisplayMap(final PrintLayout layout, final double scale) {
        final String[] locations = new String[2];
        SolaTask<Void, Void> t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                try {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_GENERATE_MAP));

                    //This is the image width of the map. It is given in pixels or in points.
                    Double mapImageWidth = Double.valueOf(layout.getMap().getWidth());
                    //This is the image height of the map. It is given in pixels or in points.
                    Double mapImageHeight = Double.valueOf(layout.getMap().getHeight());

                    //This is the image width of the scalebar. It is given in pixels or in points.
                    //The real width might change from the given size because the scalebar dynamicly looks
                    //for the best width. 
                    //So in Jasper the width/height of the scalebar is not restricted 
                    Double scalebarImageWidth = Double.valueOf(layout.getScalebar().getWidth());

                    //This gives back the absolute location of the map image. 
                    locations[0] = getMapImage(mapImageWidth, mapImageHeight, scale, DPI);

                    ScalebarGenerator scalebarGenerator = new ScalebarGenerator();
                    //This gives back the absolute location of the scalebar image. 
                    locations[1] = scalebarGenerator.getImageAsFileLocation(
                            scale, scalebarImageWidth, DPI);

                    // Capture the area name represented by the Public Display Map on the service
                    // for reference in the Dashboard.
                    if (appService != null) {
                        String areaText = StringUtility.isEmpty(txtArea.getText()) ? "" : txtArea.getText();
                        if (!areaText.equals(appService.getActionNotes())) {
                            appService.setActionNotes(areaText);
                            appService.saveService();
                        }
                    }
                    return null;
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to generate Public Display Map image", ex);
                }

            }

            @Override
            public void taskDone() {
                try {
                    generateAndShowReport(layout.getFileName(), locations[0], locations[1]);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to generate Public Display Map report", ex);
                }
            }
        };
        TaskManager.getInstance().runTask(t);

    }

    /**
     * It calls the generate report functionality and displays it.
     *
     * @param fileName
     * @param mapImageLocation
     * @param scalebarImageLocation
     * @throws IOException
     */
    private void generateAndShowReport(
            String fileName,
            String mapImageLocation,
            String scalebarImageLocation) throws IOException {

        //   This is to call the report generation         
        ReportViewerForm form = new ReportViewerForm(
                ReportManager.getMapPublicDisplayReport(
                        fileName, this.txtArea.getText(),
                        this.txtNotificationPeriod.getText(),
                        mapImageLocation, scalebarImageLocation));
        form.setSaveFormats(SaveFormat.Pdf, SaveFormat.Docx, SaveFormat.Odt, SaveFormat.Html);
        // this is to visualize the generated report            
        form.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        cmbLayoutList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtScale = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtArea = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        labNotificationFrom = new javax.swing.JLabel();
        txtNotificationPeriod = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtMapCenterLabel = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        cmdCenterMap = new javax.swing.JButton();
        cmdPrint = new javax.swing.JButton();

        jPanel1.setLayout(new java.awt.GridLayout(0, 1, 0, 6));

        jPanel6.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        cmbLayoutList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        jLabel2.setText(bundle.getString("PublicDisplayPrintForm.jLabel2.text")); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbLayoutList, 0, 97, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLayoutList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel9);

        jLabel3.setText(bundle.getString("PublicDisplayPrintForm.jLabel3.text")); // NOI18N

        txtScale.setText(bundle.getString("PublicDisplayPrintForm.txtScale.text")); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtScale)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel8);

        jPanel1.add(jPanel6);

        jLabel1.setText(bundle.getString("PublicDisplayPrintForm.jLabel1.text")); // NOI18N

        txtArea.setText(bundle.getString("PublicDisplayPrintForm.txtArea.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
            .addComponent(txtArea)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);

        labNotificationFrom.setText(bundle.getString("PublicDisplayPrintForm.labNotificationFrom.text")); // NOI18N

        txtNotificationPeriod.setText(bundle.getString("PublicDisplayPrintForm.txtNotificationPeriod.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labNotificationFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
            .addComponent(txtNotificationPeriod)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labNotificationFrom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotificationPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3);

        jLabel4.setText(bundle.getString("PublicDisplayPrintPanel.jLabel4.text")); // NOI18N

        txtMapCenterLabel.setText(bundle.getString("PublicDisplayPrintPanel.txtMapCenterLabel.text")); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
            .addComponent(txtMapCenterLabel)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMapCenterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5);

        cmdCenterMap.setText(bundle.getString("PublicDisplayPrintPanel.cmdCenterMap.text")); // NOI18N
        cmdCenterMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCenterMapActionPerformed(evt);
            }
        });

        cmdPrint.setText(bundle.getString("PublicDisplayPrintForm.cmdPrint.text")); // NOI18N
        cmdPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addComponent(cmdCenterMap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdPrint))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmdPrint)
                            .addComponent(cmdCenterMap))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrintActionPerformed
        if (this.cmbLayoutList.getSelectedItem() == null) {
            Messaging.getInstance().show(Messaging.Ids.PRINT_LAYOUT_NOT_SELECTED.toString());
            return;
        }
        this.printLayout = (PrintLayout) this.cmbLayoutList.getSelectedItem();
        try {
            Integer scale = Integer.parseInt(this.txtScale.getText());
            this.printPublicDisplayMap(printLayout, scale);
        } catch (NumberFormatException ex) {
            Messaging.getInstance().show(Messaging.Ids.PRINT_SCALE_NOT_CORRECT.toString());
        }
    }//GEN-LAST:event_cmdPrintActionPerformed

    private void cmdCenterMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCenterMapActionPerformed
        this.mapBundle.zoomToDisplayArea();
    }//GEN-LAST:event_cmdCenterMapActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbLayoutList;
    private javax.swing.JButton cmdCenterMap;
    private javax.swing.JButton cmdPrint;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel labNotificationFrom;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextField txtMapCenterLabel;
    private javax.swing.JTextField txtNotificationPeriod;
    private javax.swing.JTextField txtScale;
    // End of variables declaration//GEN-END:variables
}
