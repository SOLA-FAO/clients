/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

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
 *
 * @author Elton Manoku
 */
public class ModifyExistingNodeTool extends ModifierNodeTool {

    private String toolName = "change-node";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_TOOLTIP_CHANGE_NODE).getMessage();

    public ModifyExistingNodeTool(PojoDataAccess dataAccess,
            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        super(dataAccess, cadastreObjectNodeModifiedLayer, cadastreObjectModifiedLayer);
        this.setToolName(toolName);
        this.setToolTip(toolTip);
    }

    @Override
    protected void onRectangleFinished(Envelope2D env) {

        SimpleFeature nodeFeature = this.getFirstNodeFeature(env);
        if (nodeFeature == null) {
            CadastreObjectNodeTO nodeTO = this.getNodeFromServer(env);
            if (nodeTO == null) {
                return;
            }
            CadastreObjectNodeBean nodeBean = MappingManager.getMapper().map(
                    new CadastreObjectNodeExtraTO(nodeTO), CadastreObjectNodeBean.class);
            nodeFeature = this.cadastreObjectNodeModifiedLayer.addNodeTarget(
                    nodeBean.getId(), nodeBean.getGeom());
            this.cadastreObjectModifiedLayer.addCadastreObjects(nodeBean.getCadastreObjectList());
            this.getMapControl().refresh();
            //this.getNodeList().add(nodeBean);
            //nodeFeature = this.getFirstNodeFeature(env);
        }
        if (nodeFeature == null) {
            return;
        }
        this.manipulateNode(nodeFeature);
    }

    @Override
    protected CadastreObjectNodeTO getNodeFromServer(Envelope2D env) {
        return this.dataAccess.getCadastreService().getCadastreObjectNode(
                env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY(),
                this.getMapControl().getSrid());
    }
}
