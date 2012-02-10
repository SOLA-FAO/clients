/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.util.HashMap;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.common.messaging.GisMessage;

/**
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

    public CadastreBoundaryPointLayer() throws Exception {
        super(LAYER_NAME, Geometries.POINT, LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
        this.setShowInToc(false);
        this.boundaryLayer = new ExtendedLayerGraphics(
                LAYER_BOUNDARY_NAME, Geometries.LINESTRING, LAYER_BOUNDARY_STYLE_RESOURCE);
        this.getMapLayers().addAll(this.boundaryLayer.getMapLayers());
    }

    public Point getEndPoint() {
        SimpleFeature pointFeature = this.getFeatureCollection().getFeature(LABEL_END_P0INT);
        if (pointFeature != null) {
            return (Point) pointFeature.getDefaultGeometry();
        }
        return null;
    }

    public void setEndPoint(Point endPoint) {
        this.setTargetPoint(endPoint, false);
    }

    public void setEndPoint(byte[] endPoint) {
        this.setTargetPoint(endPoint, false);
    }

    public Point getStartPoint() {
        SimpleFeature pointFeature = this.getFeatureCollection().getFeature(LABEL_START_P0INT);
        if (pointFeature != null) {
            return (Point) pointFeature.getDefaultGeometry();
        }
        return null;
    }

    public void setStartPoint(Point startPoint) {
        this.setTargetPoint(startPoint, true);
    }

    public void setStartPoint(byte[] startPoint) {
        this.setTargetPoint(startPoint, true);
    }

    private void setTargetPoint(byte[] pointGeom, boolean isStart) {
        try {
            this.setTargetPoint((Point) wkbReader.read(pointGeom), isStart);
        } catch (Exception ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_BOUNDARY_ADD_POINT_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_ADD_POINT_ERROR);
        }

    }

    private void setTargetPoint(Point pointGeom, boolean isStart) {
        try {
            String labelToUse = isStart ? LABEL_START_P0INT : LABEL_END_P0INT;
            if (isStart) {
                this.clearSelection();
            }
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(LAYER_FIELD_LABEL, labelToUse);
            this.addFeature(labelToUse, pointGeom, params);
        } catch (Exception ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_BOUNDARY_ADD_POINT_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_ADD_POINT_ERROR);
        }
    }

    public void clearSelection() throws Exception {
        this.removeFeatures();
        this.boundaryLayer.removeFeatures();
    }

    public void setTargetBoundary(LineString boundaryGeometry) {
        try {
            this.boundaryLayer.removeFeatures();
            this.boundaryLayer.addFeature(BOUNDARY_FEATURE_ID, boundaryGeometry, null);
        } catch (Exception ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_BOUNDARY_ADD_TARGET_BOUNDARY_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_ADD_TARGET_BOUNDARY_ERROR);
        }
    }

    public LineString getTargetBoundary() {
        Object targetBoundary = this.boundaryLayer.getFeatureCollection().getFeature(
                BOUNDARY_FEATURE_ID).getDefaultGeometry();
        if (targetBoundary == null) {
            return null;
        }
        return (LineString) targetBoundary;
    }
}
