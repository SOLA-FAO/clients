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
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.util.HashMap;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.GeometryUtility;
import org.opengis.feature.simple.SimpleFeature;

/**
 * This layer is used to represent the boundary during the operation of
 * irregular boundary definition. In addition it defines an extra layer also for
 * the start and end point of the boundary.
 *
 * @author Elton Manoku
 */
public class CadastreBoundaryPointLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Boundary points";
    private static final String LAYER_STYLE_RESOURCE = "cadastreboundary_points.xml";
    private static final String LAYER_FIELD_LABEL = "label";
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:\"\"", LAYER_FIELD_LABEL);
    private static final String LABEL_START_P0INT = "1";
    private static final String LABEL_END_P0INT = "2";
    private ExtendedLayerGraphics boundaryLayer;
    private static final String LAYER_BOUNDARY_NAME = "Boundary";
    private static final String LAYER_BOUNDARY_STYLE_RESOURCE = "cadastreboundary.xml";
    private static final String BOUNDARY_FEATURE_ID = "1";

    /**
     * Creates the layer.
     *
     * @throws InitializeLayerException
     */
    public CadastreBoundaryPointLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POINT, LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
        this.setShowInToc(false);
        this.boundaryLayer = new ExtendedLayerGraphics(
                LAYER_BOUNDARY_NAME, Geometries.LINESTRING, LAYER_BOUNDARY_STYLE_RESOURCE);
        this.getMapLayers().addAll(this.boundaryLayer.getMapLayers());
    }

    @Override
    public void setMapControl(Map mapControl) {
        super.setMapControl(mapControl);
        this.boundaryLayer.setMapControl(mapControl);
    }
    /**
     * Gets the end point of the boundary
     *
     * @return
     */
    public final Point getEndPoint() {
        SimpleFeature pointFeature = this.getFeatureCollection().getFeature(LABEL_END_P0INT);
        if (pointFeature != null) {
            return (Point) pointFeature.getDefaultGeometry();
        }
        return null;
    }

    /**
     * Sets the end point using a point object
     *
     * @param endPoint
     */
    public final void setEndPoint(Point endPoint) {
        this.setTargetPoint(endPoint, false);
    }

    /**
     * Sets the end point using a WKB format
     *
     * @param endPoint
     */
    public final void setEndPoint(byte[] endPoint) {
        this.setTargetPoint(endPoint, false);
    }

    /**
     * Gets the star point of the boundary
     *
     * @return
     */
    public final Point getStartPoint() {
        SimpleFeature pointFeature = this.getFeatureCollection().getFeature(LABEL_START_P0INT);
        if (pointFeature != null) {
            return (Point) pointFeature.getDefaultGeometry();
        }
        return null;
    }

    /**
     * Sets the start point of the boundary by using a Point object
     *
     * @param startPoint
     */
    public final void setStartPoint(Point startPoint) {
        this.setTargetPoint(startPoint, true);
    }

    /**
     * Sets the start point of the boundary by using WKB format
     *
     * @param startPoint
     */
    public final void setStartPoint(byte[] startPoint) {
        this.setTargetPoint(startPoint, true);
    }

    private void setTargetPoint(byte[] pointGeom, boolean isStart) {
        this.setTargetPoint((Point) GeometryUtility.getGeometryFromWkb(pointGeom), isStart);
    }

    private void setTargetPoint(Point pointGeom, boolean isStart) {
        String labelToUse = isStart ? LABEL_START_P0INT : LABEL_END_P0INT;
        if (isStart) {
            this.clearSelection();
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(LAYER_FIELD_LABEL, labelToUse);
        this.addFeature(labelToUse, pointGeom, params, false);
    }

    /**
     * Reset the selected boundary
     */
    public final void clearSelection() {
        this.removeFeatures(false);
        this.boundaryLayer.removeFeatures(false);
    }

    /**
     * Sets the target boundary
     *
     * @param boundaryGeometry
     */
    public final void setTargetBoundary(LineString boundaryGeometry) {
        this.boundaryLayer.removeFeatures(false);
        this.boundaryLayer.addFeature(BOUNDARY_FEATURE_ID, boundaryGeometry, null, false);
    }

    /**
     * Gets the target boundary geometry
     *
     * @return
     */
    public final LineString getTargetBoundary() {
        Object targetBoundary = this.boundaryLayer.getFeatureCollection().getFeature(
                BOUNDARY_FEATURE_ID).getDefaultGeometry();
        if (targetBoundary == null) {
            return null;
        }
        return (LineString) targetBoundary;
    }
}
