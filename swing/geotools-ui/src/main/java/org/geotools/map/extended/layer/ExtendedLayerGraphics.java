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
package org.geotools.map.extended.layer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import org.geotools.data.collection.CollectionFeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.data.collection.extended.GraphicsFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.exception.GeometryTransformException;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.CRSUtility;
import org.geotools.swing.extended.util.GeometryUtility;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * This is a map layer that is used to visualize in the map control features
 * added run-time. <br/> In initialization it is defined also what kind of
 * features will be visualized by defining the geometry type and optionally
 * extra fields.
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
     * @param styleResource the resource name of the style. Has to be found in
     * one of the paths specified in sldResources in {
     * @see ExtendedFeatureLayer}
     * @throws InitializeLayerException
     */
    public ExtendedLayerGraphics(
            String name, Geometries geometryType, String styleResource)
            throws InitializeLayerException {
        this(name, geometryType, styleResource, null);
    }

    /**
     * The same as the other constructor only extra fields can be specified for
     * the features.
     *
     * @param name
     * @param geometryType
     * @param styleResource
     * @param extraFieldsFormat Extra fields can be specified for the features
     * by using the notation as known by {
     * @see org.geotools.data.DataUtilities}
     * @throws InitializeLayerException
     */
    public ExtendedLayerGraphics(
            String name, Geometries geometryType, String styleResource, String extraFieldsFormat)
            throws InitializeLayerException {

        this.geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        try {
            this.featureCollection
                    = new GraphicsFeatureCollection(geometryType, extraFieldsFormat);
            SimpleFeatureSource featureSource = new CollectionFeatureSource(featureCollection);
            this.initialize(name, featureSource, styleResource);
        } catch (SchemaException ex) {
            throw new InitializeLayerException(
                    Messaging.Ids.LAYERGRAPHICS_STARTUP_ERROR.toString(), ex);
        }
    }

    /**
     * It adds a feature in the collection of features associated with this
     * layer.
     *
     * @param fid The feature identifier. If null it will be generated.
     * @param geom The geometry value
     * @param fieldsWithValues Extra field value pairs. The field names has to
     * be the same as in the definition of the fields in the initialization of
     * the layer.
     * @return The added feature.
     */
    public SimpleFeature addFeature(String fid,
            com.vividsolutions.jts.geom.Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues,
            boolean refreshMap) {
        if (geom.getSRID() == 0) {
            geom.setSRID(this.getSrid());
        } else if (geom.getSRID() != this.getSrid()) {
            geom = GeometryUtility.transform(geom, this.getSrid());
        }
        SimpleFeature feature = this.getFeatureCollection().addFeature(fid, geom, fieldsWithValues);
        if (refreshMap) {
            this.getMapControl().refresh();
        }
        return feature;
    }

    /**
     * It calls the same method but with the geometry value in Well Know Binary
     * format.
     *
     * @param fid
     * @param geomAsBytes Geometry in Well Known Binary format
     * @param fieldsWithValues
     * @return
     * @throws ParseException
     */
    public SimpleFeature addFeature(String fid,
            byte[] geomAsBytes,
            java.util.HashMap<String, Object> fieldsWithValues,
            boolean refreshMap) throws ParseException {
        com.vividsolutions.jts.geom.Geometry geom = GeometryUtility.getGeometryFromWkb(geomAsBytes);
        return this.addFeature(fid, geom, fieldsWithValues, refreshMap);
    }

    /**
     * It removes a feature identified by its fid from the layer.
     *
     * @param fid The fid to search for
     * @return The removed feature
     */
    public SimpleFeature removeFeature(String fid, boolean refreshMap) {
        SimpleFeature feature = this.getFeatureCollection().removeFeature(fid);
        if (feature != null && refreshMap) {
            this.getMapControl().refresh();
        }
        return feature;
    }

    /**
     * It removes all features.
     */
    public void removeFeatures(boolean refreshMap) {
        this.getFeatureCollection().clear();
        if (refreshMap) {
            this.getMapControl().refresh();
        }
    }

    /**
     * Gets the feature collection
     *
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

    /**
     * It replaces a geometry of an existing feature. If the replacement is
     * successful, it also fires the change event.
     *
     * @param ofFeature the target feature
     * @param newGeometry the new geometry
     * @return true if the geometry is successfully changed
     */
    public boolean replaceFeatureGeometry(SimpleFeature ofFeature, Geometry newGeometry) {
        return replaceFeatureGeometry(ofFeature, newGeometry, true);
    }

    /**
     * It replaces the geometry of the feature.
     *
     * @param ofFeature The feature
     * @param newGeometry New geometry to be replaced
     * @param fireEvent If the event should be fired or not
     * @return True if the geometry is successfully changed
     */
    public boolean replaceFeatureGeometry(
            SimpleFeature ofFeature, Geometry newGeometry, boolean fireEvent) {
        if (!this.getFeatureCollection().contains(ofFeature)) {
            return false;
        }
        if (!this.validateGeometry(newGeometry)) {
            Messaging.getInstance().show(
                    Messaging.Ids.DRAWINGTOOL_GEOMETRY_NOT_VALID_ERROR.toString());
            return false;
        }
        newGeometry.normalize();
        ofFeature.setDefaultGeometry(newGeometry);
        newGeometry.setSRID(this.getSrid());
        newGeometry.geometryChanged();
        if (fireEvent) {
            this.getFeatureCollection().notifyListeners(
                    ofFeature, CollectionEvent.FEATURES_CHANGED);
        }
        return true;
    }

    /**
     * It checks if the geometry to be used for the feature is valid.
     *
     * @param geom
     * @return
     */
    protected final boolean validateGeometry(Geometry geom) {
        return geom.isSimple() && geom.isValid();
    }

    /**
     * It will transform the geometries of the features to the CRS of the map.
     * It assumes that the geometries have an assigned srid
     */
    public void transformFeatureGeometries() {
        System.out.println(String.format("Layer: %s, SRID %s", this.getLayerName(), this.getMapControl().getSrid()));
        MathTransform transform = null;
        int targetSrid = this.getMapControl().getSrid();
        Geometry geometry = null;
        SimpleFeatureIterator featureIterator
                = (SimpleFeatureIterator) getFeatureCollection().features();
        try {
            SimpleFeature feature;
            while (featureIterator.hasNext()) {
                feature = featureIterator.next();
                geometry = (Geometry) feature.getDefaultGeometry();
                if (transform == null) {
                    transform = CRSUtility.getInstance().getTransform(geometry.getSRID(), targetSrid);
                }
                Geometry geometryTransformed = JTS.transform(geometry, transform);
                replaceFeatureGeometry(feature, geometryTransformed);
            }
        } catch (MismatchedDimensionException ex) {
            throw new GeometryTransformException(
                    String.format("Error transforming geometry %s in srid:%s", geometry.toString(), targetSrid), ex);
        } catch (TransformException ex) {
            throw new GeometryTransformException(
                    String.format("Error transforming geometry %s in srid:%s", geometry.toString(), targetSrid), ex);
        } finally {
            featureIterator.close();
        }
    }

    /**
     * Returns a new ReferencedEnvelope object representing the bounds of the
     * feature collection. Creates a new object to avoid any risk of updating
     * the actual bounds for the layer via the object reference. Note that
     * the method will only work after the layer is populated with features, 
     * i.e. after the map has completed drawing. 
     *
     * @return The Envelope for the layer or null if the layer does not contain
     *         any features. 
     */
    @Override
    public ReferencedEnvelope getLayerEnvelope() {
        ReferencedEnvelope result = null;
        if (getFeatureCollection().size() > 0) {
            result = new ReferencedEnvelope(getFeatureCollection().getBounds());
        }
        return result;
    }
}
