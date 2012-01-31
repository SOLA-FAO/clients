/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
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
    private boolean startingPoint = true;

    public CadastreBoundaryPointLayer() throws Exception {
        super(LAYER_NAME, Geometries.POINT, LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
        this.setShowInToc(false);
    }

    public void setTargetBoundaryPoint(Geometry pointGeom) {
        try {
            String labelToUse = startingPoint ? LABEL_START_P0INT : LABEL_END_P0INT;
            SimpleFeature pointFeature = this.getFeatureCollection().getFeature(labelToUse);
            if (pointFeature == null) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put(LAYER_FIELD_LABEL, labelToUse);
                this.addFeature(labelToUse, pointGeom, params);
            } else {
                pointFeature.setDefaultGeometry(pointGeom);
            }
        } catch (Exception ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_BOUNDARY_ADD_POINT_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_ADD_POINT_ERROR);
        }
    }
}
