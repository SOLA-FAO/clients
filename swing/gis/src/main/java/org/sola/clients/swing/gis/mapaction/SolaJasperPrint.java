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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.mapaction;

import java.io.IOException;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.util.MapImageGenerator;
import org.geotools.swing.extended.util.ScalebarGenerator;
import org.geotools.swing.mapaction.extended.Print;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.clients.swing.ui.reports.SaveFormat;

/**
 * This map action extends the Print map action that handles the print of the map according to a
 * layout. The user name and date is added in the layout and also it logs against the application
 * (if the application is present) the action of printing.
 *
 * @author Maria Paola Rizzo
 */
public class SolaJasperPrint extends Print {

    private String applicationId;
    
    //This is the DPI. Normally the printer has a DPI, the monitor has a DPI. 
    //Also Jasper engine should have a DPI, which is 72. With this DPI pixels and points are equal
    private static int DPI = 72;


    /**
     * Constructor of the jasper based reporting engine print map action.
     *
     * @param map The map control with which the map action will interact
     */
    public SolaJasperPrint(Map map) {
        this(map, "print");
    }

    public SolaJasperPrint(Map map, String actionName) {
        super(map, actionName);
        //the following changes the layout properties file for 
        //the org.geotools.swing.mapaction.extended.Print.java
        // in order to use it for jasper report print map
        this.layoutLocation = "resources/print/layoutsJasper.properties";
        
    }

    /**
     * Sets the application id if the map action is found in a bundle where the application is
     * known. This application id is used to log the action of printing against the application.
     *
     * @param applicationId
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * This method calls the start screen for the print action. If it is needed to completely change
     * the way the print action starts you should rewrite this method. Checkout super.onClick() for
     * an example how you can rewrite it.
     */
    @Override
    public void onClick() {

        getPrintForm().setScale(this.getMapControl().getScale().intValue());
        getPrintForm().setVisibility(true);
        if (getPrintForm().getPrintLayout() == null) {
            return;
        }
        Print(getPrintForm().getPrintLayout(), getPrintForm().getScale());
    }

    /**
     * Gets the map image that will be embedded in the report
     * @param mapImageWidth
     * @param mapImageHeight
     * @param scale
     * @param dpi
     * @return
     * @throws IOException 
     */
    protected String getMapImage(Double mapImageWidth,  Double mapImageHeight, 
            double scale, int dpi) throws IOException{
        
        String imageFormat = "png";
        MapImageGenerator mapImageGenerator = new MapImageGenerator(this.getMapControl().getMapContent());
        //This gives back the absolute location of the map image. 
        String mapImageLocation = mapImageGenerator.getImageAsFileLocation(
                mapImageWidth, mapImageHeight, scale, dpi, imageFormat);
        
        return mapImageLocation;
    }
    
    /**
     * Additionally to the standard functionality of printing, it supplies the values of user and
     * date to the layout so it can print them as well. Also if the print succeeds it logs against
     * the application the action of printing if the application id is present.
     *
     * This method is used to call an alternative print engine which uses Jasper report Tool
     *
     * @param layout A layout identifier. This is used to distinguish between many layouts the user
     * can choose from and to call the report for that layout
     * @param scale This is the scale of the map for which the print will be done
     */
    protected void Print(PrintLayout layout, double scale) {

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
    
    protected void generateAndShowReport(
            String fileName, String mapImageLocation, String scalebarImageLocation )
    throws IOException{
        //This will be the bean containing data for the report. 
        //it is the data source for the report
        //it must be replaced with appropriate bean if needed
        Object dataBean = new Object();

        //   This is to call the report generation         
        ReportViewerForm form = new ReportViewerForm(
                ReportManager.getSolaPrintReport(
                fileName, dataBean, mapImageLocation, scalebarImageLocation));
        form.setSaveFormats(SaveFormat.Pdf, SaveFormat.Docx, SaveFormat.Odt, SaveFormat.Html);
        // this is to visualize the generated report            
        form.setVisible(true);
    }
}
