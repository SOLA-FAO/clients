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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.geotools.feature.CollectionEvent;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.Geometries;
//import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.geotools.util.Messaging;

/**
 *
 * @author manoku
 */
public class SolaLayerEditor extends SolaLayerGraphics {

    private SolaLayerGraphics verticesLayer = null;
    protected List<VertexInformation> vertexList = new ArrayList<VertexInformation>();

    public SolaLayerEditor(
            String name,
            Geometries geometryType,
            int srid,
            String styleResource,
            String extraFieldsFormat) throws Exception {
        super(name, geometryType, srid, styleResource, extraFieldsFormat);
        this.verticesLayer = new SolaLayerGraphics(
                name + " vertices",
                Geometries.POINT, srid, "editor_vertices.sld");
        this.getMapLayers().addAll(this.verticesLayer.getMapLayers());
    }

    @Override
    public SimpleFeature addFeature(String fid, byte[] geomAsBytes,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {
        return this.addFeature(fid, wkbReader.read(geomAsBytes), fieldsWithValues);
    }

    @Override
    public SimpleFeature addFeature(String fid, com.vividsolutions.jts.geom.Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {

        SimpleFeature featureAdded = super.addFeature(fid, geom, fieldsWithValues);

        com.vividsolutions.jts.geom.CoordinateList coordinates =
                new CoordinateList(geom.getCoordinates(), false);

        Coordinate coordinate;
        for (Object coordinateObj : coordinates) {
            coordinate = (Coordinate) coordinateObj;
            Point vertex = this.verticesLayer.getGeometryFactory().createPoint(coordinate);

            this.vertexList.add(new VertexInformation(
                    coordinate, featureAdded, this.verticesLayer.addFeature(null, vertex, null)));
        }

        this.getMapControl().refresh();
        return featureAdded;
    }

    @Override
    public SimpleFeature removeFeature(String fid) {
        SimpleFeature result = super.removeFeature(fid);
        if (result != null) {
            List<VertexInformation> vertexesToRemove = new ArrayList<VertexInformation>();
            for (VertexInformation vertexInfo : this.vertexList) {
                if (vertexInfo.getFeature().equals(result)) {
                    vertexesToRemove.add(vertexInfo);
                    this.verticesLayer.removeFeature(vertexInfo.getVertexFeature().getID());
                }
            }
            this.vertexList.removeAll(vertexesToRemove);
            this.getMapControl().refresh();
        }
        return result;
    }

    /**
     * It removes all features from the layer
     */
    @Override
    public void removeFeatures() throws Exception {
        super.removeFeatures();
        this.verticesLayer.removeFeatures();
        this.getMapControl().refresh();
    }

    public VertexInformation getFirstVertexWithinDistance(DirectPosition2D mousePos, double distance) {
        Coordinate coordinateToFind = new Coordinate(mousePos.x, mousePos.y);
        for (VertexInformation vertexInformation : this.vertexList) {
            if (vertexInformation.getVertex().distance(coordinateToFind) <= distance) {
                return vertexInformation;
            }
        }
        return null;
    }

    public SimpleFeature changeVertex(VertexInformation vertexInformation, DirectPosition2D newPosition) {
        double currentX = vertexInformation.getVertex().x;
        double currentY = vertexInformation.getVertex().y;
        vertexInformation.getVertex().x = newPosition.x;
        vertexInformation.getVertex().y = newPosition.y;
        SimpleFeature featureChanged = vertexInformation.getFeature();
        if (this.validateGeometry((Geometry) featureChanged.getDefaultGeometry())) {
            ((Geometry) featureChanged.getDefaultGeometry()).geometryChanged();
            this.getFeatureCollection().notifyListeners(
                    vertexInformation.getFeature(), CollectionEvent.FEATURES_CHANGED);
            this.getMapControl().refresh();
        } else {
            featureChanged = null;
            vertexInformation.getVertex().x = currentX;
            vertexInformation.getVertex().y = currentY;
            Messaging.getInstance().show(
                    Messaging.Ids.DRAWINGTOOL_GEOMETRY_NOT_VALID_ERROR.toString());
        }
        return featureChanged;
    }

    private boolean validateGeometry(Geometry geom) {
        boolean result = true;
        result = geom.isSimple() && geom.isValid();
        return result;
    }
}
