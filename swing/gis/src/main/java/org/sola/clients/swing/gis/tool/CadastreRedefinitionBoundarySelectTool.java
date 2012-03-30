/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.to.CadastreObjectNodeExtraTO;
import org.sola.common.MappingManager;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;

/**
 * This tool is used during the cadastre redefinition for selecting an irregular boundary for 
 * replacement.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionBoundarySelectTool extends CadastreBoundarySelectTool {

    private PojoDataAccess dataAccess;

    public CadastreRedefinitionBoundarySelectTool(
            PojoDataAccess dataAccess,
            CadastreBoundaryPointLayer pointLayer,
            ExtendedLayerGraphics targetLayer,
            ExtendedLayerGraphics targetNodeLayer) {
        super(pointLayer, targetLayer, targetNodeLayer);
        this.dataAccess = dataAccess;
    }

    @Override
    protected CadastreRedefinitionObjectLayer getTargetLayer() {
        return (CadastreRedefinitionObjectLayer) super.getTargetLayer();
    }

    @Override
    protected void step1(Envelope2D env) {
        super.step1(env);
        if (this.pointLayer.getStartPoint() == null) {
            CadastreObjectNodeBean nodeBean = this.getNodeFromServer(env);
            if (nodeBean != null) {
                this.pointLayer.setStartPoint(nodeBean.getGeom());
                for (CadastreObjectBean coBean : nodeBean.getCadastreObjectList()) {
                    this.targetCadastreObjectIds.add(coBean.getId());
                }
            }
        }
    }

    @Override
    protected boolean step2(Envelope2D env) {
        boolean step2Finished = super.step2(env);
        if (!step2Finished) {
            CadastreObjectNodeBean nodeBean = this.getNodeFromServer(env);
            if (nodeBean == null) {
                step2Finished = false;
            } else {
                List<String> targetCadastreObjectIdsTmp = new ArrayList<String>();

                for (CadastreObjectBean coBean : nodeBean.getCadastreObjectList()) {
                    if (!this.targetCadastreObjectIds.contains(coBean.getId())) {
                        continue;
                    }
                    targetCadastreObjectIdsTmp.add(coBean.getId());
                }
                this.targetCadastreObjectIds.clear();
                this.targetCadastreObjectIds.addAll(targetCadastreObjectIdsTmp);
                if (this.targetCadastreObjectIds.size() < 1
                        || this.targetCadastreObjectIds.size() > 2) {
                    this.clearSelection();
                } else {
                    for (CadastreObjectBean coBean : nodeBean.getCadastreObjectList()) {
                        if (this.targetCadastreObjectIds.contains(coBean.getId())) {
                            this.getTargetLayer().addCadastreObjectTarget(
                                    coBean.getId(), null, coBean.getGeomPolygon());
                        }
                    }
                    this.pointLayer.setEndPoint(nodeBean.getGeom());
                    this.defineTargetBoundary();
                }
                step2Finished = true;
            }
        }
        return step2Finished;
    }

    private CadastreObjectNodeBean getNodeFromServer(Envelope2D env) {
        CadastreObjectNodeTO nodeTO = this.dataAccess.getCadastreService().getCadastreObjectNode(
                env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY(),
                this.getMapControl().getSrid());
        if (nodeTO == null) {
            return null;
        }
        CadastreObjectNodeBean nodeBean = MappingManager.getMapper().map(
                new CadastreObjectNodeExtraTO(nodeTO), CadastreObjectNodeBean.class);
        return nodeBean;
    }
}
