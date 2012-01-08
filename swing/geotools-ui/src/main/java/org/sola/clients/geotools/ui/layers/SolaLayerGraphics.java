/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.geotools.ui.layers;

import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.Serializable;
import org.geotools.data.FeatureSource;
import org.geotools.data.collection.CollectionFeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.geotools.data.GraphicsFeatureCollection;
import org.sola.clients.geotools.util.Messaging;

/**
 * This is a map layer that is used to visualize in the map control features added run-time.
 * @contributor E. Manoku @Date july 2011
 */
public class SolaLayerGraphics extends SolaFeatureLayer {

    //FeatureSource featureSource;
    //SimpleFeatureType type;
    private GeometryFactory geometryFactory = null;
    private GraphicsFeatureCollection featureCollection = null;

    public SolaLayerGraphics(
            String name, Geometries geometryType, int srid, String styleResource)
            throws Exception {
        this(name, geometryType, srid, styleResource, null);

//        this.geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//        try {
//            this.featureCollection =
//                    new GraphicsFeatureCollection(geometryType, srid);
//            FeatureSource featureSource = new CollectionFeatureSource(featureCollection);
//            this.initialize(name, featureSource, styleResource);
//        } catch (Exception ex) {
//            throw new Exception(
//                    Messaging.Ids.LAYERGRAPHICS_STARTUP_ERROR.toString(), ex);
//        }
    }

    public SolaLayerGraphics(
            String name, Geometries geometryType, int srid, 
            String styleResource, String extraFieldsFormat)
            throws Exception {

        this.geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        try {
            this.featureCollection =
                    new GraphicsFeatureCollection(geometryType, srid, extraFieldsFormat);
            SimpleFeatureSource featureSource = new CollectionFeatureSource(featureCollection);
            this.initialize(name, featureSource, styleResource);
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.LAYERGRAPHICS_STARTUP_ERROR.toString(), ex);
        }
    }

    public SimpleFeature addFeature(String fid,
            com.vividsolutions.jts.geom.Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {
        return this.getFeatureCollection().addFeature(fid, geom, fieldsWithValues);
    }

    public SimpleFeature addFeature(String fid,
            byte[] geomAsBytes,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {
        com.vividsolutions.jts.geom.Geometry geom = wkbReader.read(geomAsBytes);
        return this.addFeature(fid, geom, fieldsWithValues);
    }

    public SimpleFeature removeFeature(String fid){
        return this.getFeatureCollection().removeFeature(fid);
    }
    
    public void removeFeatures() throws Exception {
        try {
            this.getFeatureCollection().clear();
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.REMOVE_ALL_FEATURES_ERROR.toString(), ex);
        }
    }

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
