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
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.swing.gis.beans.TransactionCadastreRedefinitionBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.mapaction.CadastreRedefinitionReset;
import org.sola.clients.swing.gis.tool.CadastreBoundarySelectTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionAddNodeTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionBoundarySelectTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionModifyNodeTool;

/**
 * A control bundle that is used for cadastre redefinition process. 
 * The necessary tools and layers are added in the bundle.
 * 
 * @author Elton Manoku
 */
public final class ControlsBundleForCadastreRedefinition extends ControlsBundleForTransaction {

    private TransactionCadastreRedefinitionBean transactionBean;
    private CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer = null;
    private CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer = null;

    /**
     * Constructor.
     * It sets up the bundle by adding layers and tools that are relevant.
     * Finally, it zooms in the interested zone. The interested zone is defined 
     * in the following order: <br/>
     * If bean has modified cadastre objects it is zoomed there, 
     * otherwise if baUnitId is present it is zoomed
     * there else it is zoomed in the application location.
     * 
     * @param transactionBean  The transaction bean. If this is already populated it means 
     * the transaction is being opened again for change.
     * @param baUnitId Id of the property that is defined in the application as a target for 
     * this cadastre change.
     * @param applicationLocation Location of application that starts the cadastre change
     */
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
    protected void addLayers() throws InitializeLayerException {
        super.addLayers();
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
        CadastreBoundarySelectTool cadastreBoundarySelectTool =
                new CadastreRedefinitionBoundarySelectTool(
                this.getPojoDataAccess(),
                this.cadastreBoundaryPointLayer,
                this.cadastreObjectModifiedLayer,
                this.cadastreObjectNodeModifiedLayer);
        this.getMap().addTool(cadastreBoundarySelectTool, this.getToolbar(), true);
        super.addToolsAndCommands();
        this.cadastreBoundaryEditTool.setTargetLayer(cadastreObjectModifiedLayer);
    }

    public void reset() {
        this.cadastreObjectModifiedLayer.removeFeatures();
        this.cadastreObjectNodeModifiedLayer.removeFeatures();
        ExtendedAction action = this.getMap().getMapActionByName(CadastreBoundarySelectTool.NAME);
        if (action != null) {
            ((CadastreBoundarySelectTool) action.getAttachedTool()).clearSelection();
            this.getMap().refresh();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.getMap().getMapActionByName(
                CadastreRedefinitionAddNodeTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(
                CadastreRedefinitionModifyNodeTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(
                CadastreRedefinitionReset.MAPACTION_NAME).setEnabled(!readOnly);
    }
}
