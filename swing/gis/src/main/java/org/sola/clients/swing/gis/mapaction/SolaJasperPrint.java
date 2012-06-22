/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.mapaction;

import java.io.IOException;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.MapScaleException;
import org.geotools.swing.extended.exception.PrintLayoutException;
import org.geotools.swing.extended.util.MapImageGenerator;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.extended.util.ScalebarGenerator;
import org.geotools.swing.mapaction.extended.Print;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.geotools.swing.mapaction.extended.ui.IPrintUi;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.gis.ui.control.SolaPrintViewerForm;
/**
 * This map action extends the Print map action that handles the print of the map according to a
 * layout. The user name and date is added in the layout and also it logs against the application
 * (if the application is present) the action of printing.
 *
 * @author Maria Paola Rizzo
 */
public class SolaJasperPrint extends Print {

    private String applicationId;
    private IPrintUi printForm;
    
    public SolaJasperPrint(Map map) {
        super(map);
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
     * This method calls the start screen for the print action. 
     * If it is needed to completely change the way the print action starts you should rewrite this
     * method. Checkout super.onClick() for an example how you can rewrite it.
     */
    @Override
    public void onClick() {

            if (this.printForm == null) {
                this.printForm = this.getPrintForm();
            }
            try {
                this.printForm.setPrintLayoutList(this.getPrintLayouts());
            } catch (PrintLayoutException ex) {
                Messaging.getInstance().show(Messaging.Ids.PRINT_LAYOUT_GENERATION_ERROR.toString());
            }

            try {
                this.printForm.setScale(this.getMapControl().getScale().intValue());
                this.printForm.setVisibility(true);
                if (this.printForm.getPrintLayout() == null) {
                    return;
                }
                Print(this.printForm.getPrintLayout(),
                        this.printForm.getScale());

            } catch (MapScaleException ex) {
                Messaging.getInstance().show(Messaging.Ids.PRINT_LAYOUT_GENERATION_ERROR.toString());
            }
    }

    
    
    
    /**
     * Additionally to the standard functionality of printing, it supplies the values of user and
     * date to the layout so it can print them as well. Also if the print succeeds it logs against
     * the application the action of printing if the application id is present.
     *
     * This method is used to call an alternative print engine which uses Jasper report Tool
     *
     * @param layout A layout identifier. This is used to distinguish between many layouts
     * the user can choose from and to call the report for that layout
     * @param scale This is the scale of the map for which the print will be done
     */
    protected void Print(PrintLayout layout, double scale) {
             
        //This is the image width of the map. It is given in pixels or in points.
        Double mapImageWidth = Double.valueOf(layout.getMap().getWidth());
        //This is the image height of the map. It is given in pixels or in points.
        Double mapImageHeight = Double.valueOf(layout.getMap().getHeight());
        
        //This is the DPI. Normally the printer has a DPI, the monitor has a DPI. 
        //Also Jasper engine should have a DPI, which is 72. With this DPI pixels and points are equal
        int dpi = 72;
        //Here it is possible to change the image format for the map image
        String imageFormat = "png";
        
        //This is the image width of the scalebar. It is given in pixels or in points.
        //The real width might change from the given size because the scalebar dynamicly looks
        //for the best width. 
        //So in Jasper the width/height of the scalebar is not restricted 
        Double scalebarImageWidth = Double.valueOf(layout.getScalebar().getWidth());
        try {
            MapImageGenerator mapImageGenerator = new MapImageGenerator(this.getMapControl());
            //This gives back the absolute location of the map image. 
            String mapImageLocation = mapImageGenerator.getImageAsFileLocation(
                    mapImageWidth, mapImageHeight, scale, dpi, imageFormat);
            
            ScalebarGenerator scalebarGenerator = new ScalebarGenerator();
            //This gives back the absolute location of the scalebar image. 
            String scalebarImageLocation = scalebarGenerator.getImageAsFileLocation(
                    scale, scalebarImageWidth, dpi);
                               
            ApplicationServiceBean serviceBean = new ApplicationServiceBean(); 
            serviceBean.setRequestTypeCode(RequestTypeBean.CODE_CADASTRE_PRINT);
            if (this.applicationId != null) {
                serviceBean.setApplicationId(this.applicationId);
            }
            serviceBean.saveInformationService();
            
            //This will be the bean containing data for the report. 
            //it is the data source for the report
            //it must be replaced with appropriate bean if needed
             Object dataBean = new Object();
             
            //   This is to call the report generation         
	    SolaPrintViewerForm form = new SolaPrintViewerForm(
		ReportManager.getSolaPrintReport(
                    layout.getName(), dataBean, mapImageLocation, scalebarImageLocation)
            );
            // this is to visualize the generated report            
	    form.setVisible(true);
	 
         
        } catch (IOException ex) {
        }
    }
}
