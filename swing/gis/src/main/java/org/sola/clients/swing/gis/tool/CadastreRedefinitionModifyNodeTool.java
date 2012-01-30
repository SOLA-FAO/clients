/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import java.util.List;
import org.geotools.feature.CollectionEvent;
import org.geotools.geometry.Envelope2D;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;

/**
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionModifyNodeTool extends CadastreRedefinitionAbstractTool {

    private String toolName = "change-node";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_TOOLTIP_CHANGE_NODE).getMessage();

    public CadastreRedefinitionModifyNodeTool(PojoDataAccess dataAccess,
            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        super(dataAccess, cadastreObjectNodeModifiedLayer, cadastreObjectModifiedLayer);
        this.setToolName(toolName);
        this.setToolTip(toolTip);
    }

    @Override
    protected void onRectangleFinished(Envelope2D env) {

        boolean nodeIsNewFromServer = false;
        SimpleFeature nodeFeature = this.getFirstNodeFeature(env);
        if (nodeFeature == null) {
            CadastreObjectNodeBean nodeBean = this.addNodeFromServer(env);
            if (nodeBean == null) {
                return;
            }
            nodeFeature = this.cadastreObjectNodeModifiedLayer.getFeatureCollection().getFeature(
                    nodeBean.getId());
            nodeIsNewFromServer = true;
        }
        if (nodeFeature == null) {
            return;
        }
        if (!this.manipulateNode(nodeFeature) && nodeIsNewFromServer) {
            this.removeNewServerNodesAfterCancellation(nodeFeature);
        }
    }

    @Override
    protected CadastreObjectNodeTO getNodeFromServer(Envelope2D env) {
        return this.dataAccess.getCadastreService().getCadastreObjectNode(
                env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY(),
                this.getMapControl().getSrid());
    }

    private void removeNewServerNodesAfterCancellation(SimpleFeature nodeFeature) {
        List<SimpleFeature> cadastreObjects =
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature);
        for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
            this.cadastreObjectModifiedLayer.getFeatureCollection().notifyListeners(
                    cadastreObjectFeature, CollectionEvent.FEATURES_CHANGED);
            this.cadastreObjectNodeModifiedLayer.removeFeature(nodeFeature.getID());
        }
        this.removeNode(nodeFeature);
    }
}
