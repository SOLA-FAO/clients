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

import com.vividsolutions.jts.awt.PointShapeFactory.Point;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.MapScaleException;
import org.geotools.swing.extended.exception.PrintLayoutException;
import org.geotools.swing.extended.util.MapImageGenerator;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.extended.util.ScalebarGenerator;
import org.geotools.swing.mapaction.extended.Print;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.geotools.swing.mapaction.extended.print.TextLayout;
import org.geotools.swing.mapaction.extended.ui.IPrintUi;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.gis.ui.control.SolaPrintViewerForm;
/**
 * This map action extends the Print map action that handles the print of the map according to a
 * layout. The user name and date is added in the layout and also it logs against the application
 * (if the application is present) the action of printing.
 *
 * @author Maria Paola Rizzo
 */
public class SolaJasperPrint extends Print {

    private final static String FIELD_USER = "{userName}";
    private final static String FIELD_DATE = "{date}";
    private String mapImageLocation;
    private String scalebarImageLocation;
    private String layoutName;
    private String applicationId;
    private IPrintUi printForm;
    public SolaJasperPrint(Map map) {
        super(map);
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
        
        int pageHeight=layout.getMap().getHeight();
        int pageWidth=layout.getMap().getWidth();
        
        float pageWPoint= com.lowagie.text.Utilities.millimetersToPoints(pageWidth);
        float pageHPoint= com.lowagie.text.Utilities.millimetersToPoints(pageHeight);
        
        System.out.println("pageHeight  "+pageHeight);
        System.out.println("pageWidth  "+pageWidth);
        System.out.println("pageWidthPoints  "+pageWPoint);
        System.out.println("pageHeightPoints  "+pageHPoint);
                
        //This is the image width of the map. It is given in pixels or in points.
        // - 10 for the horizontal margins        
        Double mapImageWidth = (double) pageWPoint-10;
        //This is the image height of the map. It is given in pixels or in points.
        // - 75 for the vertical margins
        Double mapImageHeight = (double) pageHPoint-75;
        
        //This is the DPI. Normally the printer has a DPI, the monitor has a DPI. 
        //Also Jasper engine should have a DPI, which is 72. With this DPI pixels and points are equal
        int dpi = 72;
        //Here it is possible to change the image format for the map image
        String imageFormat = "png";
        
        //This is the image width of the scalebar. It is given in pixels or in points.
        //The real width might change from the given size because the scalebar dynamicly looks
        //for the best width. So it will be good in Jasper not to restrict the width/height of the
        //scalebar.
        Double scalebarImageWidth = 100.0;
        try {
            MapImageGenerator mapImageGenerator = new MapImageGenerator(this.getMapControl());
            //This gives back the absolute location of the map image. 
            String mapImageLocation = mapImageGenerator.getImageAsFileLocation(
                    mapImageWidth, mapImageHeight, scale, dpi, imageFormat);
            
            ScalebarGenerator scalebarGenerator = new ScalebarGenerator();
            //This gives back the absolute location of the scalebar image. 
            String scalebarImageLocation = scalebarGenerator.getImageAsFileLocation(
                    scale, scalebarImageWidth, dpi);
            
            //This is to get the real size of the scalebar image, since it might change            
            float scalebarHpoint =com.lowagie.text.Utilities.millimetersToPoints(scalebarGenerator.getImage(scale, scalebarImageWidth, dpi).getHeight());
            float scalebarWpoint =com.lowagie.text.Utilities.millimetersToPoints(scalebarGenerator.getImage(scale, scalebarImageWidth, dpi).getWidth());
            scalebarImageWidth= (double)scalebarWpoint;
            
            System.out.println("scalebarWidth  "+scalebarImageWidth);
            System.out.println("scalebarRealWidth  "+scalebarGenerator.getImage(scale, scalebarImageWidth, dpi).getWidth());
            System.out.println("scalebarWidthPoints  "+scalebarWpoint);
            System.out.println("scalebarHeightPoints  "+scalebarHpoint);
        
            
            
            this.mapImageLocation=mapImageLocation;
            this.scalebarImageLocation=scalebarImageLocation;
            this.layoutName=layout.getName().toString();
            String  fieldDate = DateFormat.getInstance().format(Calendar.getInstance().getTime());
            ApplicationServiceBean serviceBean = new ApplicationServiceBean(); 
            serviceBean.setRequestTypeCode(RequestTypeBean.CODE_CADASTRE_PRINT);
            if (this.applicationId != null) {
                serviceBean.setApplicationId(this.applicationId);
            }
            serviceBean.saveInformationService();
            
            //   This is to call the report generation         
	    SolaPrintViewerForm form = new SolaPrintViewerForm(
		ReportManager.getSolaPrintReport(serviceBean,this.mapImageLocation, this.scalebarImageLocation, this.layoutName, fieldDate,  
                    mapImageWidth,  mapImageHeight, scalebarImageWidth )
            );
            // this is to visualize the generated report            
	    form.setVisible(true);
	 
         
        } catch (IOException ex) {
        }
    }
}
