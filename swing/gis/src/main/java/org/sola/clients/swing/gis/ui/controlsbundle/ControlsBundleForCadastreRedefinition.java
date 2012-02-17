/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.controlsbundle;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.swing.gis.beans.TransactionCadastreRedefinitionBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.clients.swing.gis.mapaction.CadastreRedefinitionReset;
import org.sola.clients.swing.gis.tool.CadastreBoundarySelectTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionBoundarySelectTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionModifyNodeTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionAddNodeTool;

/**
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForCadastreRedefinition extends ControlsBundleForTransaction {

    private TransactionCadastreRedefinitionBean transactionBean;
    private CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer = null;
    private CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer = null;

    public ControlsBundleForCadastreRedefinition(
            TransactionCadastreRedefinitionBean transactionBean,
            String baUnitId,
            byte[] applicationLocation) {
        super();
        this.transactionBean = transactionBean;
        if (this.transactionBean == null) {
            this.transactionBean = new TransactionCadastreRedefinitionBean();
        }
        this.Setup(PojoDataAccess.getInstance());
        this.zoomToInterestingArea(null, applicationLocation);
    }

    @Override
    protected void zoomToInterestingArea(
            ReferencedEnvelope interestingArea, byte[] applicationLocation) {
        if (this.cadastreObjectModifiedLayer.getFeatureCollection().size() > 0) {
            interestingArea = this.cadastreObjectModifiedLayer.getFeatureCollection().getBounds();
        }
        super.zoomToInterestingArea(interestingArea, applicationLocation);
    }

    @Override
    public TransactionCadastreRedefinitionBean getTransactionBean() {
        this.transactionBean.setCadastreObjectNodeTargetList(
                this.cadastreObjectNodeModifiedLayer.getNodeTargetList());
        this.transactionBean.setCadastreObjectTargetList(
                this.cadastreObjectModifiedLayer.getCadastreObjectTargetList());
        return this.transactionBean;
    }

    @Override
    protected void addLayers() throws Exception {
        this.cadastreObjectModifiedLayer = new CadastreRedefinitionObjectLayer();
        this.getMap().addLayer(this.cadastreObjectModifiedLayer);

        this.cadastreObjectModifiedLayer.addCadastreObjectTargetList(
                this.transactionBean.getCadastreObjectTargetList());

        this.cadastreObjectNodeModifiedLayer = new CadastreRedefinitionNodeLayer();
        this.getMap().addLayer(this.cadastreObjectNodeModifiedLayer);

        this.cadastreObjectNodeModifiedLayer.addNodeTargetList(
                this.transactionBean.getCadastreObjectNodeTargetList());

        super.addLayers();
    }

    @Override
    protected void addToolsAndCommands() {
        this.getMap().addTool(
                new CadastreRedefinitionAddNodeTool(
                this.getPojoDataAccess(),
                this.cadastreObjectNodeModifiedLayer,
                this.cadastreObjectModifiedLayer),
                this.getToolbar(),
                true);
        this.getMap().addTool(
                new CadastreRedefinitionModifyNodeTool(
                this.getPojoDataAccess(),
                this.cadastreObjectNodeModifiedLayer,
                this.cadastreObjectModifiedLayer),
                this.getToolbar(),
                true);

        this.getMap().addMapAction(new CadastreRedefinitionReset(this), this.getToolbar(), true);
        super.addToolsAndCommands();
        this.cadastreBoundaryEditTool.setTargetLayer(cadastreObjectModifiedLayer);

        CadastreBoundarySelectTool cadastreBoundarySelectTool =
                new CadastreRedefinitionBoundarySelectTool(
                this.getPojoDataAccess(),
                this.cadastreBoundaryPointLayer,
                this.cadastreObjectModifiedLayer,
                this.cadastreObjectNodeModifiedLayer);
        this.getMap().addTool(cadastreBoundarySelectTool, this.getToolbar(), true);
    }

    public void reset() throws Exception {
        this.cadastreObjectModifiedLayer.removeFeatures();
        this.cadastreObjectNodeModifiedLayer.removeFeatures();
        ExtendedAction action = this.getMap().getMapActionByName(CadastreBoundarySelectTool.NAME);
        if (action != null) {
            ((CadastreBoundarySelectTool) action.getAttachedTool()).clearSelection();
            this.getMap().refresh();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly){
        super.setReadOnly(readOnly);
        this.getMap().getMapActionByName(
                CadastreRedefinitionAddNodeTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(
                CadastreRedefinitionModifyNodeTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(
                CadastreRedefinitionReset.MAPACTION_NAME).setEnabled(!readOnly);
    }
}
