/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.tool.extended.ExtendedDrawRectangle;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * It is used to select a boundary from the target cadastre objects in the targetLayer.
 * 
 * @author Elton Manoku
 */
public class CadastreBoundarySelectTool extends ExtendedDrawRectangle {

    private String toolName = "cadastre-boundary-select";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_BOUNDARY_SELECT_TOOL_TOOLTIP).getMessage();

    private ExtendedLayerGraphics targetLayer;
    private CadastreBoundaryPointLayer pointLayer;

    public CadastreBoundarySelectTool(CadastreBoundaryPointLayer pointLayer) {
        this.setToolName(toolName);
        this.setToolTip(toolTip);
        this.pointLayer= pointLayer;
    }
    
    public void setTargetLayer(ExtendedLayerGraphics targetLayer) {
        this.targetLayer = targetLayer;
    }

    @Override
    protected void onRectangleFinished(Envelope2D env) {
        if (targetLayer == null){
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_TARGET_LAYER_NOT_DEFINED);
            return;
        }
        FeatureCollection featureCollection =
                this.targetLayer.getFeaturesInRange(new ReferencedEnvelope(env), null);
        if (featureCollection != null) {
            Geometry feature1Geom = null;
            Geometry feature2Geom = null;
            SimpleFeatureIterator featureIterator =
                    (SimpleFeatureIterator) featureCollection.features();
            while (featureIterator.hasNext()) {
                if (feature1Geom == null){
                    feature1Geom = (Geometry)featureIterator.next().getDefaultGeometry();
                }else if (feature2Geom == null){
                    feature2Geom = (Geometry)featureIterator.next().getDefaultGeometry();
                }else{
                    break;                
                }
            }
            featureIterator.close();
            if (feature1Geom != null){
                if (feature2Geom != null){
                    Geometry intersection = feature1Geom.intersection(feature2Geom);
                    System.out.println("Intersection is:" + intersection.toText());
                }
            }
        }
    }
    
}
