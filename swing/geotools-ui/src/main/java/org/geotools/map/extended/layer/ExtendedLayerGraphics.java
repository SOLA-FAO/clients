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
package org.geotools.map.extended.layer;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.data.collection.CollectionFeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.data.collection.extended.GraphicsFeatureCollection;
import org.geotools.swing.extended.util.Messaging;

/**
 * This is a map layer that is used to visualize in the map control features added run-time. <br/>
 * In initialization it is defined also what kind of features will be visualized by defining the 
 * geometry type and optionally extra fields.
 * 
 * @author Elton Manoku 
 */
public class ExtendedLayerGraphics extends ExtendedFeatureLayer {

    private GeometryFactory geometryFactory = null;
    private GraphicsFeatureCollection featureCollection = null;

    /**
     * It initializes the layer.
     * 
     * @param name Name of the layer. Has to be unique.
     * @param geometryType The type of geometry
     * @param styleResource the resource name of the style. Has to be found in one of the paths
     * specified in SLD_RESOURCES in {@see ExtendedFeatureLayer}
     * @throws Exception 
     */
    public ExtendedLayerGraphics(
            String name, Geometries geometryType, String styleResource)
            throws Exception {
        this(name, geometryType, styleResource, null);
    }

    /**
     * The same as the other constructor only extra fields can be specified for the features.
     * @param name
     * @param geometryType
     * @param styleResource
     * @param extraFieldsFormat Extra fields can be specified for the features by using the notation
     * as known by {@see org.geotools.data.DataUtilities}
     * @throws Exception 
     */
    public ExtendedLayerGraphics(
            String name, Geometries geometryType, String styleResource, String extraFieldsFormat)
            throws Exception {

        this.geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        try {
            this.featureCollection =
                    new GraphicsFeatureCollection(geometryType, extraFieldsFormat);
            SimpleFeatureSource featureSource = new CollectionFeatureSource(featureCollection);
            this.initialize(name, featureSource, styleResource);
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.LAYERGRAPHICS_STARTUP_ERROR.toString(), ex);
        }
    }

    /**
     * It adds a feature in the collection of features associated with this layer.
     * @param fid The feature identifier. If null it will be generated.
     * @param geom The geometry value
     * @param fieldsWithValues Extra field value pairs. The field names has to be the same 
     * as in the definition of the fields in the initialization of the layer.
     * @return The added feature.
     * @throws Exception 
     */
    public SimpleFeature addFeature(String fid,
            com.vividsolutions.jts.geom.Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {
        return this.getFeatureCollection().addFeature(fid, geom, fieldsWithValues);
    }

    /**
     * It calls the same method but with the geometry value in Well Know Binary format.
     * @param fid
     * @param geomAsBytes Geometry in Well Known Binary format
     * @param fieldsWithValues
     * @return
     * @throws Exception 
     */
    public SimpleFeature addFeature(String fid,
            byte[] geomAsBytes,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {
        com.vividsolutions.jts.geom.Geometry geom = wkbReader.read(geomAsBytes);
        return this.addFeature(fid, geom, fieldsWithValues);
    }

    /**
     * It removes a feature identified by its fid from the layer.
     * @param fid The fid to search for
     * @return The removed feature
     */
    public SimpleFeature removeFeature(String fid){
        return this.getFeatureCollection().removeFeature(fid);
    }
    
    /**
     * It removes all features.
     * @throws Exception 
     */
    public void removeFeatures() throws Exception {
        try {
            this.getFeatureCollection().clear();
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.REMOVE_ALL_FEATURES_ERROR.toString(), ex);
        }
    }

    /**
     * Gets the feature collection
     * @return 
     */
    public GraphicsFeatureCollection getFeatureCollection() {
        return this.featureCollection;
    }

    /**
     * @return the geometryFactory
     */
    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }
}
