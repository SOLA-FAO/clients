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
import org.sola.clients.swing.gis.to.CadastreObjectNodeExtraTO;
import org.sola.common.MappingManager;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;

/**
 * Tool that is used during the cadastre redefinition process to modify an existing node.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionModifyNodeTool extends CadastreRedefinitionAbstractNodeTool {

    public final static String NAME = "change-node";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_TOOLTIP_CHANGE_NODE).getMessage();

    public CadastreRedefinitionModifyNodeTool(PojoDataAccess dataAccess,
            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        super(dataAccess, cadastreObjectNodeModifiedLayer, cadastreObjectModifiedLayer);
        this.setToolName(NAME);
        this.setToolTip(toolTip);
    }

    /**
     * This is the action of this tool. It is first searched for a node in the target node layer.
     * If not found in the layer, it is searched in the server.
     * If a node is found then the manipulation node form is called to let the user
     * enter the new coordinates or remove the node. The remove option is shown only if the node
     * is found in max two cadastre objects.
     * 
     * @param env 
     */
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

    /**
     * Gets a node from the server. The node must be already present in the server.
     * 
     * @param env
     * @return The node bean if found otherwise null
     */
    @Override
    protected CadastreObjectNodeBean getNodeFromServer(Envelope2D env) {
        CadastreObjectNodeTO nodeTO =
                this.dataAccess.getCadastreService().getCadastreObjectNode(
                env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY(),
                this.getMapControl().getSrid());
        
        if (nodeTO == null) {
            return null;
        }
        CadastreObjectNodeBean nodeBean = MappingManager.getMapper().map(
                new CadastreObjectNodeExtraTO(nodeTO), CadastreObjectNodeBean.class);
        return nodeBean;
    }

    /**
     * It removes a node that was just retrieved from the server. It is called if the modification
     * process is canceled. It notifies also the cadastre objects that share the node.
     * 
     * @param nodeFeature 
     */
    private void removeNewServerNodesAfterCancellation(SimpleFeature nodeFeature) {
        List<SimpleFeature> cadastreObjects =
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature);
        for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
            this.cadastreObjectModifiedLayer.getFeatureCollection().notifyListeners(
                    cadastreObjectFeature, CollectionEvent.FEATURES_CHANGED);
        }
        this.cadastreObjectNodeModifiedLayer.removeFeature(nodeFeature.getID());
        this.getMapControl().refresh();
    }
}
