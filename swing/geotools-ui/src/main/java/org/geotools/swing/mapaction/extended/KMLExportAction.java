/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.geotools.swing.mapaction.extended;

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.geotools.data.collection.extended.GraphicsFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTS;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * This tool will export map data from the selection layer as default long lat
 * coordinates into a basic format KML file. That KML file can then be loaded
 * into Google Earth and the object (or objects) styled to match the users
 * needs. Note that the basic KML file will not load successfully into Google
 * Maps or Bing Maps.
 *
 * @author soladev
 */
public class KMLExportAction extends ExtendedAction {

    Map mapControl;
    private final static String KML_FILE_NAME =
            System.getProperty("user.home") + File.separator + "sola"
            + File.separator + "mapExport.kml";

    /**
     * Constructor for the KML Export action.
     *
     * @param mapControl
     */
    public KMLExportAction(Map mapControl) {
        super(mapControl, "kml-export", Messaging.getInstance().getMessageText(
                Messaging.Ids.KML_EXPORT_TOOLTIP.toString()),
                "resources/kml-export.png");
        this.mapControl = mapControl;
    }

    /**
     * Perform the export when the user clicks the tool using a default file
     * name and location
     */
    @Override
    public void onClick() {
        try {
            // Check there are features selected on the map
            if (mapControl.getSelectedFeatureSource() != null) {

                // Create a GeoTools KML Encoder to output the feature to KML
                Encoder encoder = new Encoder(new KMLConfiguration());
                encoder.setIndenting(true);

                // Create a KML file on the file system as an output stream for the encoder
                File file = new File(KML_FILE_NAME);
                OutputStream out = new FileOutputStream(file);

                // Create a transformation to re-project the map feature to Lat Long so that
                // the feature can be loaded into GoogleEarth
                CoordinateReferenceSystem mapCRS = mapControl.getMapContent().getCoordinateReferenceSystem();
                CoordinateReferenceSystem googleCRS = CRS.decode("EPSG:4326");
                MathTransform transform = CRS.findMathTransform(mapCRS, googleCRS, true);
                                    
                // If the map coordinate system is not North oriented (such as the South Oriented coordinate
                // system of Lesotho), it may be necessary to perform a scaling transformation to ensure
                // the geometries are transformed to the correct Lat Long positions. The code below
                // shows a simple scaling transform that can be used in South Oriented coordinate systems. 
                // AffineTransform affineTransform = AffineTransform.getScaleInstance(-1, -1);
                // MathTransform scaleTransfom = new AffineTransform2D(affineTransform);

                // Loop through each selected feature and transform the coordinates to lat long
                GraphicsFeatureCollection newFeatures = new GraphicsFeatureCollection(Geometries.GEOMETRY);
                SimpleFeatureIterator iterator = mapControl.getSelectedFeatureSource().getFeatures().features();
                Integer x = new Integer(0);
                try {
                    while (iterator.hasNext()) {
                        SimpleFeature feature = iterator.next();
                        Geometry geom = (Geometry) feature.getDefaultGeometry();
                        // Use a scale transformation first if required. 
                        // geom = JTS.transform(geom, scaleTransfom);
                        Geometry transformedGeom = JTS.transform(geom, transform);
                        newFeatures.addFeature(x.toString(), transformedGeom, null);
                        x++;
                    }
                } finally {
                    if (iterator != null) {
                        // Make sure the feature iterator is closed properly. 
                        iterator.close();
                    }
                }
                // Output the KML file containing the transformed features
                encoder.encode(newFeatures, KML.kml, out);
                Messaging.getInstance().show(Messaging.Ids.KML_EXPORT_FILE_LOCATION.toString(), KML_FILE_NAME);
            } else {
                Messaging.getInstance().show(Messaging.Ids.KML_EXPORT_NO_FEATURE_SELECTED.toString());
            }
        } catch (Exception ex) {
            Messaging.getInstance().show(Messaging.Ids.KML_EXPORT_ERROR.toString(), ex.getMessage());
        }
    }
}
