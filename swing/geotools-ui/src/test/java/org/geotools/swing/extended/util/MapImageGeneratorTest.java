/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.extended.util;

import java.awt.Toolkit;
import java.io.File;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.junit.*;

/**
 *
 * @author Elton Manoku
 */
public class MapImageGeneratorTest {

    public MapImageGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getImageAsFileLocation method, of class MapImageGenerator.
     */
    @Test
    @Ignore
    public void testGetImageAsFileLocation() throws Exception {
        System.out.println("getImageAsFileLocation");
        double imageWidth = 200.0;
        double imageHeight = 200.0;
        double scale = 1500.0;
        int dpi = 96;
        String imageFormat = "png";
        MapImageGenerator instance = new MapImageGenerator(this.getMap());
        String result = instance.getImageAsFileLocation(
                imageWidth, imageHeight, scale, dpi, imageFormat);
        System.out.print("Map image generated in:" + result);
    }

    private Map getMap() throws InitializeMapException {
        String wktOfCrs = "PROJCS[\"NZGD2000 / New Zealand Transverse Mercator 2000\",GEOGCS[\"NZGD2000\",DATUM[\"New_Zealand_Geodetic_Datum_2000\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY[\"EPSG\",\"6167\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4167\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",173],PARAMETER[\"scale_factor\",0.9996],PARAMETER[\"false_easting\",1600000],PARAMETER[\"false_northing\",10000000],AUTHORITY[\"EPSG\",\"2193\"],AXIS[\"Easting\",EAST],AXIS[\"Northing\",NORTH]]";
        Map map = new Map(2193, wktOfCrs);
        double east = 1795771, west = 1776400, north = 5932259, south = 5919888;
        map.setFullExtent(east, west, north, south);
//            String wmsServerURL =
//                    "http://193.43.36.40:8085/geoserver/wms?service=WMS&request=GetCapabilities";
//            ArrayList<String> wmsLayerNames = new ArrayList<String>();
//            wmsLayerNames.add("samoaWS:road");
//            wmsLayerNames.add("samoaWS:parcels");
//            wmsLayerNames.add("samoaWS:village");
//            mapCtrl.addLayerWMS("From Server", wmsServerURL, wmsLayerNames);
        //  mapCtrl.getMap().getMapContent().addLayer(new MapDecorationsLayer());
        File directory = new File(".");
        String shapeFile =
                //                String.format("%s\\src\\test\\java\\org\\sola\\clients\\geotools\\ui\\sample\\data\\Samoa_Parcels.shp", 
                String.format("%s\\src\\test\\java\\org\\geotools\\swing\\extended\\sample\\data\\parcels.shp",
                directory.getAbsolutePath());
        map.addLayerShapefile(
                "Shape layer", "Title of shape layer", shapeFile, "polygon.xml");
        return map;
    }
}
