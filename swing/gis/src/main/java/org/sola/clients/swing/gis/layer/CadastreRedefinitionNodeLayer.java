/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeTargetBean;
import org.sola.common.messaging.GisMessage;

/**
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionNodeLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Modified Nodes";
    private static final String LAYER_STYLE_RESOURCE = "node_modified.xml";

    public CadastreRedefinitionNodeLayer() throws Exception {
        super(LAYER_NAME, Geometries.POINT, LAYER_STYLE_RESOURCE, null);
    }
    
    public SimpleFeature addNodeTarget(CadastreObjectNodeTargetBean nodeBean) {
        return this.addNodeTarget(nodeBean.getNodeId(), nodeBean.getGeom());
    }

    public SimpleFeature addNodeTarget(String id, byte[] geom) {
        SimpleFeature featureAdded = null;
        try {
            featureAdded = this.addFeature(id, geom, null);
        } catch (Exception ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_REDEFINITION_ADD_NODE_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_REDEFINITION_ADD_NODE_ERROR);
        }
        return featureAdded;
    }
    
    public List<CadastreObjectNodeTargetBean> getNodeTargetList(){
        List<CadastreObjectNodeTargetBean> nodeTargetList =
                new ArrayList<CadastreObjectNodeTargetBean>();
        SimpleFeature nodeFeature = null;
        SimpleFeatureIterator iterator = 
                (SimpleFeatureIterator)this.getFeatureCollection().features();
        while(iterator.hasNext()){
            nodeFeature = iterator.next();
            CadastreObjectNodeTargetBean nodeTargetBean =
                    new CadastreObjectNodeTargetBean();
            nodeTargetBean.setNodeId(nodeFeature.getID());
            nodeTargetBean.setGeom(wkbWriter.write((Geometry)nodeFeature.getDefaultGeometry()));
            nodeTargetList.add(nodeTargetBean);
        }
        iterator.close();
        return nodeTargetList;
    }
    
    public void addNodeTargetList(List<CadastreObjectNodeTargetBean> targetList){
        for(CadastreObjectNodeTargetBean targetBean: targetList){
            this.addNodeTarget(targetBean);
        }
    }
}
