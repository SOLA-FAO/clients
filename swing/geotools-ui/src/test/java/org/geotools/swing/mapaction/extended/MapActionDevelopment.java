/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.geotools.swing.mapaction.extended;

import java.io.File;
import java.util.List;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.geotools.swing.extended.exception.PrintLayoutException;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Manoku
 */
public class MapActionDevelopment {
    
    public MapActionDevelopment() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    @Ignore
    public void testGetPrintLayouts() throws PrintLayoutException {
        System.out.println("getPrintLayouts");
        Print instance = new Print(null);
        List<PrintLayout> printLayouts = instance.getPrintLayouts();
        for(PrintLayout printLayout: printLayouts){
            System.out.println(String.format("Print layout: id=%s", printLayout.getId()));
        }
        System.out.println("getPrintLayouts - Success");
    }
    
    @Test
    public void testPrint() throws InitializeMapException, PrintLayoutException {
        System.out.println("Test Print");
        Print instance = new Print(this.getMap());
        System.out.println("getPrintLayouts");
        double scale = 1000;
        List<PrintLayout> printLayouts = instance.getPrintLayouts();
        for(PrintLayout printLayout: printLayouts){
            System.out.println(String.format("Print layout: id=%s", printLayout.getId()));
        }
        System.out.println("getPrintLayouts - Success");
        System.out.println("PDF location: " + instance.print(printLayouts.get(0), scale, null));
        System.out.println("test Print - Success");
    }  
    
        private Map getMap() throws InitializeMapException {
        String wktOfCrs = "PROJCS[\"NZGD2000 / New Zealand Transverse Mercator 2000\",GEOGCS[\"NZGD2000\",DATUM[\"New_Zealand_Geodetic_Datum_2000\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY[\"EPSG\",\"6167\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4167\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",173],PARAMETER[\"scale_factor\",0.9996],PARAMETER[\"false_easting\",1600000],PARAMETER[\"false_northing\",10000000],AUTHORITY[\"EPSG\",\"2193\"],AXIS[\"Easting\",EAST],AXIS[\"Northing\",NORTH]]";
        Map map = new Map(2193, wktOfCrs);
        double east = 1795771, west = 1776400, north = 5932259, south = 5919888;
        map.setFullExtent(east, west, north, south);
        File directory = new File(".");
        String shapeFile =
                String.format("%s\\src\\test\\java\\org\\geotools\\swing\\extended\\sample\\data\\parcels.shp",
                directory.getAbsolutePath());
        map.addLayerShapefile(
                "Shape layer", "Title of shape layer", shapeFile, "polygon.xml");
        return map;
    }

}
