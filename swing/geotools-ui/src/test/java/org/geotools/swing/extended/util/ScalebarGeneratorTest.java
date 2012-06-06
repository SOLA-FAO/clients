/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.extended.util;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Manoku
 */
public class ScalebarGeneratorTest {
    
    public ScalebarGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    @Test
    public void testGetImageAsFileLocation() throws Exception {
        System.out.println("getImageAsFileLocation");
        double imageWidth = 200.0;
        double scale = 1500.0;
        int dpi = 96;
        ScalebarGenerator instance = new ScalebarGenerator();
        String result = instance.getImageAsFileLocation(scale, imageWidth, dpi);
        System.out.print("Map image generated in:" + result);
    }
}
