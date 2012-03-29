/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeTargetBean;
import org.sola.common.messaging.GisMessage;

/**
 * Layer that maintains the collection of nodes that are targeted during the cadastre redefinition.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionNodeLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Modified Nodes";
    private static final String LAYER_STYLE_RESOURCE = "node_modified.xml";

    /**
     * Constructor
     * @throws InitializeLayerException 
     */
    public CadastreRedefinitionNodeLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POINT, LAYER_STYLE_RESOURCE, null);
    }
    
    /**
     * Adds a node target bean to the layer. It transforms it to feature
     * 
     * @param nodeBean
     * @return 
     */
    public SimpleFeature addNodeTarget(CadastreObjectNodeTargetBean nodeBean) {
        return this.addNodeTarget(nodeBean.getNodeId(), nodeBean.getGeom());
    }

    /**
     * Adds a node target from a WKB object
     * @param id
     * @param geom
     * @return 
     */
    public SimpleFeature addNodeTarget(String id, byte[] geom) {
        SimpleFeature featureAdded = null;
        try {
            featureAdded = this.addFeature(id, geom, null);
        } catch (ParseException ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_REDEFINITION_ADD_NODE_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_REDEFINITION_ADD_NODE_ERROR);
        }
        return featureAdded;
    }
    
    /**
     * Gets the list of node targets
     * @return 
     */
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
    
    /**
     * Sets the list of node targets
     * @param targetList 
     */
    public void addNodeTargetList(List<CadastreObjectNodeTargetBean> targetList){
        for(CadastreObjectNodeTargetBean targetBean: targetList){
            this.addNodeTarget(targetBean);
        }
    }
}
