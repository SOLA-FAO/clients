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
package org.sola.clients.geotools.data;

import java.io.Serializable;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureEvent;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.geotools.ui.layers.SolaLayerGraphics;
import org.sola.clients.geotools.util.Messaging;

/**
 * This feature collection is used from the {@see SolaLayersGraphics}. 
 * The extra functionality added are: <br/>
 *  the constructor which gets a geometry type and an srid and builds the collection.<br/>
 *  adding of the feature which is used by the feature editor.<br/>
 * 
 * @author: E. Manoku 
 */
public class GraphicsFeatureCollection extends ListFeatureCollection {

    private SimpleFeatureBuilder builder;
    private static final String FIELD_NAME_GEOMETRY = "geom";

    /**
     * Use this constructor for use in {@see SolaLayerGraphics}
     * @param geometryType The geometry type
     * @param srid The srid
     * @throws Exception it is thrown by schema creation utility
     */
    public GraphicsFeatureCollection(Geometries geometryType, int srid) throws Exception {
        this(geometryType, srid, null);
    }

    public GraphicsFeatureCollection(Geometries geometryType, int srid, String fieldsFormat)
            throws Exception {
        super(DataUtilities.createType(geometryType.getSimpleName(),
                String.format("%s:%s:srid=%s"
                + (fieldsFormat == null || fieldsFormat.isEmpty() ? "" : "," + fieldsFormat),
                FIELD_NAME_GEOMETRY, geometryType.getSimpleName(), srid)));
        this.builder = new SimpleFeatureBuilder(this.getSchema());
    }

    /**
     * It adds a feature in the collection. Use this method instead of add method of the collection
     * itself.
     * @param fid A feature id. If is null, it will be generated.
     * @param geom The geometry
     * @param fieldsWithValues A hashmap with Field, Value pairs
     * @throws Exception It is thrown if adding of the feature fails
     */
    public SimpleFeature addFeature(String fid,
            com.vividsolutions.jts.geom.Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {

        geom.normalize();
        this.builder.set(FIELD_NAME_GEOMETRY, geom);
        if (fieldsWithValues != null) {
            for (String fieldName : fieldsWithValues.keySet()) {
                if (this.getSchema().getDescriptor(fieldName) != null) {
                    this.builder.set(fieldName, fieldsWithValues.get(fieldName));
                }
            }
        }
        SimpleFeature feature = this.builder.buildFeature(fid);
        try {
            this.add(feature);
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.ADDING_FEATURE_ERROR.toString(), ex);
        }
        return feature;
    }

    @Override
    public boolean add(SimpleFeature feature){
        boolean result = super.add(feature);
        if (result) {
            this.notifyListeners(feature, CollectionEvent.FEATURES_ADDED);
        }
        return result;        
    }
    
    /**
     * It removes a feature from the collection
     * @param fid
     * @return 
     */
    public SimpleFeature removeFeature(String fid) {
        SimpleFeature feature = this.getFeature(fid);
        if (feature != null) {
            this.remove(feature);
            return feature;
        }
        return null;
    }

    @Override
    public boolean remove(Object feature) {
        boolean result = super.remove(feature);
        if (result) {
            this.notifyListeners((SimpleFeature)feature, CollectionEvent.FEATURES_REMOVED);
        }
        return result;
    }

    public SimpleFeature getFeature(String fid) {
        SimpleFeature feature = null;
        SimpleFeature currentFeature = null;
        SimpleFeatureIterator iterator = this.features();
        while (iterator.hasNext()) {
            currentFeature = iterator.next();
            if (currentFeature.getID().equals(fid)) {
                feature = currentFeature;
                break;
            }
        }
        this.close(iterator);
        return feature;
    }

    /**
     * 
     * @param feature
     * @param actionOnFeature 
     * values are:
     * CollectionEvent.FEATURES_ADDED, 
     * CollectionEvent.FEATURES_REMOVED,
     * CollectionEvent.FEATURES_CHANGED
     */
    public void notifyListeners(SimpleFeature feature, int actionOnFeature) {
        SimpleFeature[] featuresAffected = new SimpleFeature[1];
        featuresAffected[0] = feature;
        this.notifyListeners(featuresAffected, actionOnFeature);
    }

    /**
     * 
     * @param features
     * @param actionOnFeature 
     * values are:
     * CollectionEvent.FEATURES_ADDED, 
     * CollectionEvent.FEATURES_REMOVED,
     * CollectionEvent.FEATURES_CHANGED
     */
    public void notifyListeners(SimpleFeature[] features, int actionOnFeature) {
        for (CollectionListener listener : this.listeners) {
            listener.collectionChanged(
                    new CollectionEvent(this, features, actionOnFeature));
        }

    }
}
