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
package org.sola.clients.swing.gis.data;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;
import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geotools.data.DataAccess;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureListener;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.sola.clients.swing.gis.layers.PojoLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.spatial.ResultForNavigationInfo;
import org.sola.webservices.spatial.SpatialResult;

/**
 *
 * @author manoku
 */
public class PojoFeatureSource implements SimpleFeatureSource {
    
    PojoFeatureCollection collection = null;
    protected List<FeatureListener> listeners = null;
    private QueryCapabilities capabilities;
    private Set<Key> hints;
    //private SpatialWS spatialWS;
    private PojoDataAccess dataSource;
    private static WKBReader wkbReader = new WKBReader();
    private SimpleFeatureBuilder builder;
    //private String pojoQueryName;
    private double lastWest;
    private double lastSouth;
    private double lastEast;
    private double lastNorth;
    private PojoLayer layer;
    
    public PojoFeatureSource(PojoDataAccess dataSource, PojoLayer layer) throws Exception {
        try {
            this.layer = layer;
            this.dataSource = dataSource;
            SimpleFeatureType type = this.getNewFeatureType(
                    this.layer.getLayerName(),
                    this.dataSource.getMapLayerInfoList().get(
                    this.layer.getLayerName()).getPojoStructure());
            this.collection = new PojoFeatureCollection(type);
            this.builder = new SimpleFeatureBuilder(type);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public static WKBReader getWkbReader(){
        return wkbReader;
    }
    
    private SimpleFeatureType getNewFeatureType(String name, String structure) {
        try {
            return DataUtilities.createType(name, structure);
        } catch (Exception ex) {
            System.out.println("Feature type is not generated. Reason:" + ex.getMessage());
        }
        return null;
    }

    /**
     * Based in CollectionFeatureSource
     * @param listener 
     */
    @Override
    public synchronized void addFeatureListener(FeatureListener listener) {
        if (listeners == null) {
            listeners = Collections.synchronizedList(new ArrayList<FeatureListener>());
        }
        listeners.add(listener);
    }

    /**
     * Based in CollectionFeatureSource
     * @param listener 
     */
    @Override
    public synchronized void removeFeatureListener(FeatureListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }
    
    @Override
    public ReferencedEnvelope getBounds() throws IOException {
        return collection.getBounds();
    }
    
    @Override
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        return getFeatures(query).getBounds();
    }
    
    @Override
    public int getCount(Query query) throws IOException {
        return getFeatures(query).size();
    }
    
    @Override
    public DataAccess<SimpleFeatureType, SimpleFeature> getDataStore() {
        throw new UnsupportedOperationException(GisMessage.GENERAL_EXCEPTION_COLLFEATSOURCE);
    }
    
    @Override
    public ResourceInfo getInfo() {
        throw new UnsupportedOperationException(GisMessage.GENERAL_EXCEPTION_COLLFEATSOURCE);
    }
    
    @Override
    public Name getName() {
        return this.getSchema().getName();
    }
    
    @Override
    public synchronized QueryCapabilities getQueryCapabilities() {
        if (capabilities == null) {
            capabilities = new QueryCapabilities() {

//                @Override
//                public boolean isOffsetSupported() {
//                    return true;
//                }
                @Override
                public boolean isReliableFIDSupported() {
                    return true;
                }
//                @Override
//                public boolean supportsSorting(org.opengis.filter.sort.SortBy[] sortAttributes) {
//                    return true;
//                }
            };
        }
        return capabilities;
    }
    
    @Override
    public synchronized Set<Key> getSupportedHints() {
        if (hints == null) {
            Set<Key> supports = new HashSet<Key>();
            //supports.add( Hints.FEATURE_DETACHED );
            hints = Collections.unmodifiableSet(supports);
        }
        return hints;
    }
    
    @Override
    public SimpleFeatureType getSchema() {
        return this.collection.getSchema();
    }
    
    @Override
    public SimpleFeatureCollection getFeatures() throws IOException {
        return this.collection;
    }
    
    @Override
    public SimpleFeatureCollection getFeatures(Filter filter) throws IOException {
        Query query = new Query(getSchema().getTypeName(), filter);
        return this.getFeatures(query);
    }
    
    @Override
    public PojoFeatureCollection getFeatures(Query query) throws IOException {
        Filter filter = query.getFilter();
        if (filter == null) {
            throw new UnsupportedOperationException(GisMessage.GENERAL_EXCEPTION_FILTER_NOTFOUND);
        }
        if (!(filter instanceof org.opengis.filter.spatial.BBOX)) {
            throw new UnsupportedOperationException(GisMessage.GENERAL_EXCEPTION_TYPE_NOTSUPPORTED);
        }
        org.opengis.filter.spatial.BBOX bboxFilter = (org.opengis.filter.spatial.BBOX) filter;
        org.geotools.filter.LiteralExpressionImpl literalExpression =
                (org.geotools.filter.LiteralExpressionImpl) bboxFilter.getExpression2();
        Geometry filteringGeometry = (Geometry) literalExpression.getValue();
        Envelope boundingBox = (Envelope) filteringGeometry.getEnvelopeInternal();
        double west = boundingBox.getMinX();
        double east = boundingBox.getMaxX();
        double south = boundingBox.getMinY();
        double north = boundingBox.getMaxY();
        this.ModifyFeatureCollection(west, south, east, north);
        return this.collection;
    }
    
    private void ModifyFeatureCollection(double west, double south,
            double east, double north) {
        if (!this.layer.isForceRefresh()) {
            if (this.lastWest == west && this.lastSouth == south
                    && this.lastEast == east && this.lastNorth == north) {
                return;
            }
        } else {
            this.layer.setForceRefresh(false);
        }
        this.lastWest = west;
        this.lastSouth = south;
        this.lastEast = east;
        this.lastNorth = north;
        ResultForNavigationInfo resultInfo = this.dataSource.GetQueryData(
                this.getSchema().getTypeName(), west, south, east, north,
                this.getLayer().getSrid(), this.getLayer().getMapControl().getPixelResolution());
        List<SimpleFeature> featuresToAdd = this.getFeaturesFromData(resultInfo.getToAdd());
        this.collection.clear();
        this.collection.addAll(featuresToAdd);
    }
    
    private List<SimpleFeature> getFeaturesFromData(List<SpatialResult> spatialResultList) {
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        for (SpatialResult spatialResult : spatialResultList) {
            try {
                Geometry geomValue = getWkbReader().read(spatialResult.getTheGeom());
                String fid = spatialResult.getId();
                this.builder.set("theGeom", geomValue);
                this.builder.set("label", spatialResult.getLabel());
                features.add(this.builder.buildFeature(fid));
            } catch (Exception ex) {
                System.out.println("Error converting row to feature");
                System.out.println("Reason:" + ex.getMessage());
            }
        }
        return features;
    }
    
    public PojoLayer getLayer() {
        return layer;
    }
    
    public void setLayer(PojoLayer layer) {
        this.layer = layer;
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("CollectionFeatureSource:");
        buf.append(collection);
        return buf.toString();
    }
}
