/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.gis.tool;

import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.layer.TargetBoundaryLayer;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;

/**
 * This tool is used during the cadastre redefinition for selecting an irregular boundary for
 * replacement.
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionBoundarySelectTool
        extends CadastreBoundarySelectTool implements TargetCadastreObjectTool {

    private PojoDataAccess dataAccess;
    private String cadastreObjectType;

    /**
     * Creates the tool.
     * 
     * @param dataAccess The data access library that is used to communicate with the server
     * @param pointLayer The same as in {@see CadastreBoundaryEditTool} 
     * @param targetLayer The same as in {@see CadastreBoundaryEditTool} 
     * @param targetNodeLayer The same as in {@see CadastreBoundaryEditTool} 
     */
    public CadastreRedefinitionBoundarySelectTool(
            PojoDataAccess dataAccess,
            CadastreBoundaryPointLayer pointLayer,
            TargetBoundaryLayer targetLayer,
            ExtendedLayerGraphics targetNodeLayer) {
        super(pointLayer, targetLayer, targetNodeLayer);
        this.dataAccess = dataAccess;
    }

    @Override
    protected CadastreRedefinitionObjectLayer getTargetLayer() {
        return (CadastreRedefinitionObjectLayer) super.getTargetLayer();
    }

    @Override
    public void setCadastreObjectType(String cadastreObjectType) {
        this.cadastreObjectType = cadastreObjectType;
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
                            this.getTargetLayer().addCadastreObjectTarget(coBean);
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
                this.getMapControl().getSrid(), this.cadastreObjectType);
        if (nodeTO == null) {
            return null;
        }
       CadastreObjectNodeBean nodeBean = TypeConverters.TransferObjectToBean(nodeTO, 
               CadastreObjectNodeBean.class, null);
        return nodeBean;
    }
}
