/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.geotools.swing.extended;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JDialog;
import org.junit.Test;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.geotools.map.extended.layer.ExtendedImageLayer;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.geotools.swing.extended.tool.ExtendedDrawLinestring;
import org.geotools.swing.mapaction.extended.Print;
import org.geotools.swing.mapaction.extended.RemoveDirectImage;
import org.geotools.swing.tool.extended.AddDirectImageTool;
import org.geotools.swing.tool.extended.ExtendedDrawPolygon;
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
//        mapCtrl.Setup(32702);
//        double east = 458000, west = 300000, north = 8520000, south = 8440000;
        String wktOfCrs = "PROJCS[\"NZGD2000 / New Zealand Transverse Mercator 2000\",GEOGCS[\"NZGD2000\",DATUM[\"New_Zealand_Geodetic_Datum_2000\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY[\"EPSG\",\"6167\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4167\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",173],PARAMETER[\"scale_factor\",0.9996],PARAMETER[\"false_easting\",1600000],PARAMETER[\"false_northing\",10000000],AUTHORITY[\"EPSG\",\"2193\"],AXIS[\"Easting\",EAST],AXIS[\"Northing\",NORTH]]";
        mapCtrl.Setup(2193, wktOfCrs, true);
        double east = 1795771, west = 1776400, north = 5932259, south = 5919888;
        mapCtrl.getMap().setFullExtent(east, west, north, south);
//            String wmsServerURL =
//                    "http://193.43.36.40:8085/geoserver/wms?service=WMS&request=GetCapabilities";
//            ArrayList<String> wmsLayerNames = new ArrayList<String>();
//            wmsLayerNames.add("samoaWS:road");
//            wmsLayerNames.add("samoaWS:parcels");
//            wmsLayerNames.add("samoaWS:village");
//            mapCtrl.addLayerWMS("From Server", wmsServerURL, wmsLayerNames);
       //  mapCtrl.getMap().getMapContent().addLayer(new MapDecorationsLayer());
        File directory =  new File(".");
        String shapeFile = 
//                String.format("%s\\src\\test\\java\\org\\sola\\clients\\geotools\\ui\\sample\\data\\Samoa_Parcels.shp", 
                String.format("%s\\src\\test\\java\\org\\geotools\\swing\\extended\\sample\\data\\parcels.shp", 
                directory.getAbsolutePath());
        ExtendedLayer layer = mapCtrl.getMap().addLayerShapefile(
                "Shape layer", "Title of shape layer", shapeFile, "polygon.xml");
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
