/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.geotools.data.collection.extended;

import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.swing.extended.util.Messaging;

/**
 * This feature collection is used from the 
 * {@see org.sola.clients.geotools.ui.layers.ExtendedLayerGraphics}. 
 * The extra functionality added are: <br/>
 * Check the documentation about the constructors.
 * The geometry field is named "geom".
 * 
 * @author: Elton Manoku 
 */
public class GraphicsFeatureCollection extends ListFeatureCollection {

    private SimpleFeatureBuilder builder;
    private static final String FIELD_NAME_GEOMETRY = "geom";

    /**
     * Use this constructor for use in 
     * {@see org.sola.clients.geotools.ui.layers.ExtendedLayerGraphics}
     * @param geometryType The geometry type
     * @throws SchemaException it is thrown by schema creation utility
     */
    public GraphicsFeatureCollection(Geometries geometryType)throws SchemaException {
        this(geometryType, null);
    }

    /**
     * Alternatively, with this constructor you can also specify a field structure
     * as excepted by the DataUtility library of geotools.
     * @param geometryType type of geometry
     * @param fieldsFormat Extra fields used for the features
     * @throws SchemaException 
     */
    public GraphicsFeatureCollection(Geometries geometryType, String fieldsFormat)
            throws SchemaException {
        super(DataUtilities.createType(geometryType.getSimpleName(),
                String.format("%s:%s%s",
                FIELD_NAME_GEOMETRY, geometryType.getSimpleName(),
                (fieldsFormat == null || fieldsFormat.isEmpty() ? "" : "," + fieldsFormat))));
        this.builder = new SimpleFeatureBuilder(this.getSchema());
    }

    /**
     * It adds a feature in the collection. Use this method instead of add method of the collection
     * itself.
     * @param fid A feature id. If is null, it will be generated.
     * @param geom The geometry
     * @param fieldsWithValues A hashmap with Field, Value pairs
     */
    public SimpleFeature addFeature(String fid,
            com.vividsolutions.jts.geom.Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues){
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
        this.add(feature);
        return feature;
    }

    /**
     * It overrides the add method of the collection itself in order to notify listeners for changes
     * in the collection.
     * @param feature
     * @return 
     */
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


    /**
     * It overrides the remove method of the collection itself in order to notify listeners 
     * for changes in the collection.
     * @param feature
     * @return 
     */
    @Override
    public boolean remove(Object feature) {
        boolean result = super.remove(feature);
        if (result) {
            this.notifyListeners((SimpleFeature)feature, CollectionEvent.FEATURES_REMOVED);
        }
        return result;
    }

    /**
     * Gets a feature searching by the fid.
     * @param fid
     * @return if found returns the feature otherwise returns null.
     */
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
     * It notifies the listeners attached to the collection for events associated with a feature.
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
     * It notifies the listeners attached to the collection for events associated 
     * with a set of features.
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
