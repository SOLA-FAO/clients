/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;

/**
 *
 * @author Elton Manoku
 */
public class CadastreObjectNodeModifiedLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Modified Nodes";
    private static final String LAYER_STYLE_RESOURCE = "node_modified.xml";
    private List<CadastreObjectNodeBean> nodeList = new ArrayList<CadastreObjectNodeBean>();

    public CadastreObjectNodeModifiedLayer() throws Exception {
        super(LAYER_NAME, Geometries.POINT, LAYER_STYLE_RESOURCE, null);
    }

    public List<CadastreObjectNodeBean> getNodeList() {
        return nodeList;
    }
    
    public SimpleFeature addNodeObject(CadastreObjectNodeBean nodeBean) {
        SimpleFeature featureAdded = null;
        try {
            featureAdded = this.addFeature(nodeBean.getId(), nodeBean.getGeom(), null);
            this.nodeList.add(nodeBean);
        } catch (Exception ex) {
            Messaging.getInstance().show("Error while adding nodes involved");
        }
        return featureAdded;
    }

    public void removeNodeObject(CadastreObjectNodeBean nodeBean) {
        try {
            this.removeFeature(nodeBean.getId());
            this.nodeList.remove(nodeBean);
        } catch (Exception ex) {
            Messaging.getInstance().show("Error while removing node.");
        }
    }
}
