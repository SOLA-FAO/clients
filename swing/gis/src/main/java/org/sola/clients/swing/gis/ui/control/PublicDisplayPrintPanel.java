/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.util.MapImageGenerator;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.extended.util.ScalebarGenerator;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.data.PojoPublicDisplayFeatureSource;
import org.sola.clients.swing.gis.layer.PojoForPublicDisplayLayer;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForPublicDisplay;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This panel provides the necessary user interface to manage and start the printing
 * of the public display map.
 * Because the functionality is about printing according to a certain layout,
 * some functionality is duplicated from the print functionality.
 * 
 * @author Elton Manoku
 */
public class PublicDisplayPrintPanel extends javax.swing.JPanel {

    private static final String RESOURCE_LAYOUT =
            "layouts_public_display.properties";
    //This is the DPI. Normally the printer has a DPI, the monitor has a DPI. 
    //Also Jasper engine should have a DPI, which is 72. With this DPI pixels and points are equal
    private static int DPI = 72;
    private List<PrintLayout> printLayoutList;
    private PrintLayout printLayout;
    private ControlsBundleForPublicDisplay mapBundle;
    private String applicationId;

    /**
     * Creates new form PublicDisplayPrintPanel
     */
    public PublicDisplayPrintPanel(ControlsBundleForPublicDisplay mapBundle) {
        initComponents();
        setPrintLayoutList();
        this.mapBundle = mapBundle;
    }

    private String getFilterNameLastPart(){
        String filter = "";
        if (txtNameLastPart.getText() != null){
            filter = txtNameLastPart.getText().trim();
        }
        return filter;
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

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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
    private void printPublicDisplayMap(PrintLayout layout, double scale) {

        //This is the image width of the map. It is given in pixels or in points.
        Double mapImageWidth = Double.valueOf(layout.getMap().getWidth());
        //This is the image height of the map. It is given in pixels or in points.
        Double mapImageHeight = Double.valueOf(layout.getMap().getHeight());

        //This is the image width of the scalebar. It is given in pixels or in points.
        //The real width might change from the given size because the scalebar dynamicly looks
        //for the best width. 
        //So in Jasper the width/height of the scalebar is not restricted 
        Double scalebarImageWidth = Double.valueOf(layout.getScalebar().getWidth());
        try {
            //This gives back the absolute location of the map image. 
            String mapImageLocation = getMapImage(mapImageWidth, mapImageHeight, scale, DPI);

            ScalebarGenerator scalebarGenerator = new ScalebarGenerator();
            //This gives back the absolute location of the scalebar image. 
            String scalebarImageLocation = scalebarGenerator.getImageAsFileLocation(
                    scale, scalebarImageWidth, DPI);

            //This will leave a trace in the services that a print has happened
            ApplicationServiceBean serviceBean = new ApplicationServiceBean();
            serviceBean.setRequestTypeCode(RequestTypeBean.CODE_CADASTRE_PRINT);
            if (this.applicationId != null) {
                serviceBean.setApplicationId(this.applicationId);
            }
            serviceBean.saveInformationService();
            generateAndShowReport(layout.getFileName(), mapImageLocation, scalebarImageLocation);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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
        // this is to visualize the generated report            
        form.setVisible(true);
    }

    /**
     * It centers and zooms the map to the special layers of public display.
     * 
     */
    private void centerMap() {
        ReferencedEnvelope envelope = PojoDataAccess.getInstance().getExtentOfPublicDisplay(
                getFilterNameLastPart());        
        if (envelope == null) {
            MessageUtility.displayMessage(GisMessage.PRINT_PUBLIC_DISPLAY_CENTER_LAST_PART_CO_NOT_FOUND);
            return;
        }
        envelope.expandBy(10);
        this.mapBundle.getMap().setDisplayArea(envelope);        
    }

    /**
     * It sets the filter for the public display layers. The filter is the name
     * last part.
     *
     * @return True if the filter is set.
     */
    private boolean setLayerFilter() {
        String nameLastPart = getFilterNameLastPart();
        if ( nameLastPart.isEmpty()) {
            Messaging.getInstance().show(GisMessage.PRINT_PUBLIC_DISPLAY_FILTER_NOT_FOUND);
            return false;
        }
        boolean mapMustBeRefreshed = false;
        for (PojoForPublicDisplayLayer layer : mapBundle.getPublicDisplayLayers()) {
            String previousNameLastPart =
                    ((PojoPublicDisplayFeatureSource) layer.getFeatureSource()).getNameLastPart();
            if (!nameLastPart.equals(previousNameLastPart)) {
                ((PojoPublicDisplayFeatureSource) layer.getFeatureSource()).setNameLastPart(nameLastPart);
                mapMustBeRefreshed = true;
                layer.setForceRefresh(true);
            }
        }
        if (mapMustBeRefreshed) {
            this.mapBundle.refresh(true);
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtNameLastPart = new javax.swing.JTextField();
        labLocation = new javax.swing.JLabel();
        cmbLayoutList = new javax.swing.JComboBox();
        txtNotificationPeriod = new javax.swing.JTextField();
        txtArea = new javax.swing.JTextField();
        cmdPrint = new javax.swing.JButton();
        labNotificationFrom = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtScale = new javax.swing.JTextField();
        cmdCenterMap = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        jLabel2.setText(bundle.getString("PublicDisplayPrintForm.jLabel2.text")); // NOI18N

        jLabel1.setText(bundle.getString("PublicDisplayPrintForm.jLabel1.text")); // NOI18N

        txtNameLastPart.setText(bundle.getString("PublicDisplayPrintForm.txtNameLastPart.text")); // NOI18N

        labLocation.setText(bundle.getString("PublicDisplayPrintForm.labLocation.text")); // NOI18N

        cmbLayoutList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtNotificationPeriod.setText(bundle.getString("PublicDisplayPrintForm.txtNotificationPeriod.text")); // NOI18N

        txtArea.setText(bundle.getString("PublicDisplayPrintForm.txtArea.text")); // NOI18N

        cmdPrint.setText(bundle.getString("PublicDisplayPrintForm.cmdPrint.text")); // NOI18N
        cmdPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPrintActionPerformed(evt);
            }
        });

        labNotificationFrom.setText(bundle.getString("PublicDisplayPrintForm.labNotificationFrom.text")); // NOI18N

        jLabel3.setText(bundle.getString("PublicDisplayPrintForm.jLabel3.text")); // NOI18N

        txtScale.setText(bundle.getString("PublicDisplayPrintForm.txtScale.text")); // NOI18N

        cmdCenterMap.setText(bundle.getString("PublicDisplayPrintPanel.cmdCenterMap.text")); // NOI18N
        cmdCenterMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCenterMapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdCenterMap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdPrint))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2)
                        .addComponent(labNotificationFrom)
                        .addComponent(jLabel1)
                        .addComponent(labLocation)
                        .addComponent(txtArea)
                        .addComponent(txtNotificationPeriod)
                        .addComponent(txtNameLastPart)
                        .addComponent(cmbLayoutList, 0, 179, Short.MAX_VALUE)
                        .addComponent(txtScale, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNameLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labNotificationFrom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotificationPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLayoutList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(txtScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdPrint)
                    .addComponent(cmdCenterMap))
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrintActionPerformed
        if (this.cmbLayoutList.getSelectedItem() == null) {
            Messaging.getInstance().show(Messaging.Ids.PRINT_LAYOUT_NOT_SELECTED.toString());
            return;
        }
        if (!this.setLayerFilter()) {
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
        if (!this.setLayerFilter()) {
            return;
        }
        centerMap();
    }//GEN-LAST:event_cmdCenterMapActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbLayoutList;
    private javax.swing.JButton cmdCenterMap;
    private javax.swing.JButton cmdPrint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labLocation;
    private javax.swing.JLabel labNotificationFrom;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextField txtNameLastPart;
    private javax.swing.JTextField txtNotificationPeriod;
    private javax.swing.JTextField txtScale;
    // End of variables declaration//GEN-END:variables
}
