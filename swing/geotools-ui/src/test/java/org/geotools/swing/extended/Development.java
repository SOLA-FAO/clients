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
package org.geotools.swing.extended;

import org.geotools.data.wms.request.GetMapRequest;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.SimpleHttpClient;
import org.geotools.data.wms.WMS1_1_0;
import org.geotools.data.wms.response.GetMapResponse;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.extended.layer.*;
import org.geotools.map.extended.layer.ExtendedWmsLiteLayer;
import org.junit.Ignore;
import org.junit.Test;
import org.geotools.swing.extended.tool.ExtendedDrawLinestring;
import org.geotools.swing.mapaction.extended.Print;
import org.geotools.swing.mapaction.extended.RemoveDirectImage;
import org.geotools.swing.tool.extended.AddDirectImageTool;
import org.geotools.swing.extended.tool.ExtendedDrawPolygon;
import org.geotools.swing.tool.extended.ExtendedDrawToolWithSnapping;

/**
 *
 * @author Manoku
 */
public class Development {

    /**
     * Test of Visual ControlsBundle
     */
    //@Ignore
    @Test
    public void testMapControl() throws Exception {
        System.out.println("MapControl");

        ControlsBundle mapCtrl = new ControlsBundle();
        String wktOfCrs = "PROJCS[\"NZGD2000 / New Zealand Transverse Mercator 2000\",GEOGCS[\"NZGD2000\",DATUM[\"New_Zealand_Geodetic_Datum_2000\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY[\"EPSG\",\"6167\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4167\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",173],PARAMETER[\"scale_factor\",0.9996],PARAMETER[\"false_easting\",1600000],PARAMETER[\"false_northing\",10000000],AUTHORITY[\"EPSG\",\"2193\"],AXIS[\"Easting\",EAST],AXIS[\"Northing\",NORTH]]";
        mapCtrl.Setup(2193, wktOfCrs, true);
        double east = 1795771, west = 1776400, north = 5932259, south = 5919888;
        mapCtrl.getMap().setFullExtent(east, west, north, south);
        String wmsServerURL = "http://localhost:8085/geoserver/sola/wms";
        ArrayList<String> wmsLayerNames = new ArrayList<String>();
        wmsLayerNames.add("sola:nz_orthophoto");
        //ExtendedWmsLiteLayer wmsLayer = new ExtendedWmsLiteLayer(
        //        "wmsLayer", "WMS Layer", wmsServerURL, wmsLayerNames, 2193);
        //mapCtrl.getMap().addLayer(wmsLayer);
        File directory = new File(".");
        String shapeFile =
                //                String.format("%s\\src\\test\\java\\org\\sola\\clients\\geotools\\ui\\sample\\data\\Samoa_Parcels.shp", 
                String.format("%s\\src\\test\\java\\org\\geotools\\swing\\extended\\sample\\data\\parcels.shp",
                directory.getAbsolutePath());
        ExtendedFeatureLayer shapeLayer =  mapCtrl.getMap().addLayerShapefile(
                "Shape layer", "Title of shape layer", shapeFile, "polygon.xml");
        //mapCtrl.getMap().addLayerWms("wmsLayer", "WMS Layer", 
        //        wmsServerURL, wmsLayerNames, true,  "1.1.0", "image/png");
        ExtendedImageLayer imageLayer = new ExtendedImageLayer("image", "Image");
        //imageLayer.setRasterFile(new File("C:\\dev\\projects\\sola\\docs\\test\\test.jpg"));
        //imageLayer.setMinX(1785170);imageLayer.setMinY(5927351);
        //imageLayer.setMaxX(1786311);imageLayer.setMaxY(5928372);
        mapCtrl.getMap().addLayer(imageLayer);
//            mapCtrl.addLayerShapefile("Shape layer", "C:\\dev\\projects\\sola\\data\\Samoa_Parcels.shp", "parcel.sld");
//            mapCtrl.addTool(new SolaInfoTool());
        //mapCtrl.addTool(new SolaSplitParcelTool());
        mapCtrl.getMap().addMapAction(new Print(mapCtrl.getMap()), mapCtrl.getToolbar(), true);
        ExtendedDrawPolygon solaDrawTool = new ExtendedDrawPolygon();
        mapCtrl.getMap().addTool(solaDrawTool, mapCtrl.getToolbar(), true);
        mapCtrl.getMap().addTool(new ExtendedDrawLinestring(), mapCtrl.getToolbar(), true);

//        ExtendedDrawToolWithSnapping snapTool = new ExtendedDrawToolWithSnapping();
//        snapTool.getTargetSnappingLayers().add(
//                (ExtendedFeatureLayer)mapCtrl.getMap().getSolaLayers().get("Shape layer"));
//        snapTool.getTargetSnappingLayers().add(
//                (ExtendedFeatureLayer)mapCtrl.getMap().getSolaLayers().get(solaDrawTool.getLayerName()));

        //  mapCtrl.getMap().addTool(snapTool, mapCtrl.getToolbar(), true);
        mapCtrl.getMap().addTool(new AddDirectImageTool(imageLayer), mapCtrl.getToolbar(), true);
        mapCtrl.getMap().addMapAction(
                new RemoveDirectImage(mapCtrl.getMap()), mapCtrl.getToolbar(), true);
        // mapCtrl.getToc().afterNodesAdded();
//            mapCtrl.addTool(new SolaDrawLinestring());
//            // mapCtrl.addLayerGraphics("Point layer");
        mapCtrl.setPreferredSize(new Dimension(500, 400));
        this.displayControlsBundleForm(mapCtrl);

    }

    @Ignore
    @Test
    public void test() throws Exception {
        System.out.println("Test");
        double east = 1795771, west = 1776400, north = 5932259, south = 5919888;
        SimpleHttpClient httpClient = new SimpleHttpClient();
        GetMapRequest getMapRequest = new WMS1_1_0.GetMapRequest(
                new URL("http://localhost:8080/geoserver/nz_orthophoto/wms"));
        getMapRequest.addLayer("nz_orthophoto:nz_full_photo", "");
        getMapRequest.setBBox(
                new Envelope2D(new DirectPosition2D(east, south),
                new DirectPosition2D(west, north)));
        getMapRequest.setDimensions(200, 300);
        getMapRequest.setFormat("image/jpeg");
        getMapRequest.setSRS("EPSG:2193");
        URL url = getMapRequest.getFinalURL();
        System.out.println("url:" + url.toString());
        try {
            GetMapResponse response = new GetMapResponse(httpClient.get(getMapRequest.getFinalURL()));
            BufferedImage image = ImageIO.read(response.getInputStream());
        } catch (IOException ex) {
        }

    }

    private void displayControlsBundleForm(Component ctrl) {
        JDialog controlContainer = new JDialog();
        //controlContainer.setAlwaysOnTop(true);
        controlContainer.setModal(true);
        ctrl.setPreferredSize(new Dimension(600, 600));
        controlContainer.getContentPane().add(ctrl);
        controlContainer.pack();
        controlContainer.setVisible(true);
    }
}
