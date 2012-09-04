/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.controlsbundle;

import com.vividsolutions.jts.geom.Geometry;
import java.util.List;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.GeometryUtility;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreRedefinitionBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.mapaction.CadastreRedefinitionReset;
import org.sola.clients.swing.gis.tool.CadastreBoundarySelectTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionAddNodeTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionBoundarySelectTool;
import org.sola.clients.swing.gis.tool.CadastreRedefinitionModifyNodeTool;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/**
 * A control bundle that is used for cadastre redefinition process. The necessary tools and layers
 * are added in the bundle.
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForCadastreRedefinition extends ControlsBundleForTransaction {

    private TransactionCadastreRedefinitionBean transactionBean;
    private CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer = null;
    private CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer = null;

    /**
     * Constructor. It sets up the bundle by adding layers and tools that are relevant. Finally, it
     * zooms in the interested zone. The interested zone is defined in the following order: <br/> If
     * bean has modified cadastre objects it is zoomed there, otherwise if baUnitId is present it is
     * zoomed there else it is zoomed in the application location.
     *
     * @param applicationBean The application where the transaction is started identifiers
     * @param transactionStarterId The id of the starter of the application. This will be the
     * service id.
     * @param baUnitId Id of the property that is defined in the application as a target for this
     * cadastre redefinition.
     */
    public ControlsBundleForCadastreRedefinition(
            ApplicationBean applicationBean,
            String transactionStarterId,
            String baUnitId,
            String targetCadastreObjectType) {
        super(applicationBean, transactionStarterId);
        this.Setup(PojoDataAccess.getInstance());
        this.setTargetCadastreObjectTypeConfiguration(targetCadastreObjectType);
        this.setTransaction();
        ReferencedEnvelope interestingArea = null;
        if (!this.transactionIsStarted()) {
            interestingArea = this.getExtentOfCadastreObjectsOfBaUnit(baUnitId);
        }
        this.zoomToInterestingArea(interestingArea, applicationBean.getLocation());
    }

    @Override
    protected boolean transactionIsStarted() {
        return (this.cadastreObjectModifiedLayer.getFeatureCollection().size() > 0);
    }

    @Override
    protected void zoomToInterestingArea(
            ReferencedEnvelope interestingArea, byte[] applicationLocation) {
        if (interestingArea == null 
                && this.cadastreObjectModifiedLayer.getFeatureCollection().size() > 0) {
            interestingArea = this.cadastreObjectModifiedLayer.getFeatureCollection().getBounds();
        }
        super.zoomToInterestingArea(interestingArea, applicationLocation);
    }

    @Override
    public TransactionCadastreRedefinitionBean getTransactionBean() {
        this.transactionBean.setCadastreObjectNodeTargetList(
                this.cadastreObjectNodeModifiedLayer.getBeanListForTransaction());
        this.transactionBean.setCadastreObjectTargetList(
                this.cadastreObjectModifiedLayer.getBeanListForTransaction());
        this.transactionBean.setSourceIdList(this.getDocumentsPanel().getSourceIds());
        return this.transactionBean;
    }

    @Override
    public final void setTransaction() {
        this.transactionBean = PojoDataAccess.getInstance().getTransactionCadastreRedefinition(
                getTransactionStarterId());
        this.cadastreObjectModifiedLayer.setBeanList(
                this.transactionBean.getCadastreObjectTargetList());
        this.cadastreObjectNodeModifiedLayer.setBeanList(
                this.transactionBean.getCadastreObjectNodeTargetList());
        this.getDocumentsPanel().setSourceIds(this.transactionBean.getSourceIdList());
    }

    @Override
    protected void addLayers() throws InitializeLayerException {
        super.addLayers();
        this.cadastreObjectModifiedLayer = new CadastreRedefinitionObjectLayer();
        this.getMap().addLayer(this.cadastreObjectModifiedLayer);

        this.cadastreObjectNodeModifiedLayer = new CadastreRedefinitionNodeLayer();
        this.getMap().addLayer(this.cadastreObjectNodeModifiedLayer);
    }
    private CadastreRedefinitionAddNodeTool addNodeTool;
    private CadastreRedefinitionModifyNodeTool modifyNodeTool;
    private CadastreRedefinitionBoundarySelectTool boundarySelectTool;

    @Override
    protected void addToolsAndCommands() {
        this.addNodeTool = new CadastreRedefinitionAddNodeTool(
                this.getPojoDataAccess(),
                this.cadastreObjectNodeModifiedLayer,
                this.cadastreObjectModifiedLayer);
        this.getMap().addTool(this.addNodeTool, this.getToolbar(), true);
        this.modifyNodeTool = new CadastreRedefinitionModifyNodeTool(
                this.getPojoDataAccess(),
                this.cadastreObjectNodeModifiedLayer,
                this.cadastreObjectModifiedLayer);
        this.getMap().addTool(this.modifyNodeTool, this.getToolbar(), true);

        this.getMap().addMapAction(new CadastreRedefinitionReset(this), this.getToolbar(), true);
        this.boundarySelectTool =
                new CadastreRedefinitionBoundarySelectTool(
                this.getPojoDataAccess(),
                this.cadastreBoundaryPointLayer,
                this.cadastreObjectModifiedLayer,
                this.cadastreObjectNodeModifiedLayer);
        this.getMap().addTool(this.boundarySelectTool, this.getToolbar(), true);
        super.addToolsAndCommands();
        this.cadastreBoundaryEditTool.setTargetLayer(cadastreObjectModifiedLayer);
    }

    @Override
    protected void setTargetCadastreObjectTypeConfiguration(String targetCadastreObjectType) {
        this.addNodeTool.setCadastreObjectType(targetCadastreObjectType);
        this.modifyNodeTool.setCadastreObjectType(targetCadastreObjectType);
        this.boundarySelectTool.setCadastreObjectType(targetCadastreObjectType);
    }

    /**
     * It resets the transaction. All modifications are wiped out.
     */
    public void reset() {
        this.cadastreObjectModifiedLayer.getBeanList().clear();
        this.cadastreObjectNodeModifiedLayer.getBeanList().clear();
        ExtendedAction action = this.getMap().getMapActionByName(
                CadastreBoundarySelectTool.MAP_ACTION_NAME);
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

    /**
     * Gets the extent of the cadastre objects that are related with the baUnitId
     *
     * @param baUnitId
     */
    private ReferencedEnvelope getExtentOfCadastreObjectsOfBaUnit(String baUnitId) {
        List<CadastreObjectTO> cadastreObjects =
                this.getPojoDataAccess().getCadastreService().getCadastreObjectsByBaUnit(baUnitId);
        ReferencedEnvelope envelope = null;
        for (CadastreObjectTO cadastreObject : cadastreObjects) {
            Geometry geom = GeometryUtility.getGeometryFromWkb(cadastreObject.getGeomPolygon());
            ReferencedEnvelope tmpEnvelope = JTS.toEnvelope(geom);
            if (envelope == null) {
                envelope = tmpEnvelope;
            } else {
                envelope.expandToInclude(tmpEnvelope);
            }
        }
        return envelope;
    }
}
