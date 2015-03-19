/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.util.List;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreChangeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreChangeNewCadastreObjectLayer;
import org.sola.clients.swing.gis.layer.CadastreChangeNewSurveyPointLayer;
import org.sola.clients.swing.gis.layer.CadastreChangeTargetCadastreObjectLayer;
import org.sola.clients.swing.gis.mapaction.CadastreChangeNewCadastreObjectListFormShow;
import org.sola.clients.swing.gis.mapaction.CadastreChangePointSurveyListFormShow;
import org.sola.clients.swing.gis.tool.CadastreBoundarySelectTool;
import org.sola.clients.swing.gis.tool.CadastreChangeNewCadastreObjectTool;
import org.sola.clients.swing.gis.tool.CadastreChangeNodeTool;
import org.sola.clients.swing.gis.tool.CadastreChangeSelectCadastreObjectTool;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.sola.clients.swing.gis.layer.PojoLayer;

/**
 * A control bundle that is used for cadastre change process. The necessary tools and layers are
 * added in the bundle.
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForMapExistingParcel extends ControlsBundleForTransaction {
    
    private static final String PARCEL_LAYER_NAME = "parcels";
    private TransactionCadastreChangeBean transactionBean;
    private CadastreChangeTargetCadastreObjectLayer targetParcelsLayer = null;
    private CadastreChangeNewCadastreObjectLayer newCadastreObjectLayer = null;
    private CadastreChangeNewSurveyPointLayer newPointsLayer = null;
    private String lastPartEntry = "";
    private CadastreChangeSelectCadastreObjectTool selectTargetCadastreObjectTool;
    private CadastreChangeNewCadastreObjectTool newCadastreObjectTool;
    private String lastPartTemplate = "SP %s";

   /**
    * Constructor. It sets up the bundle by adding layers and tools that are relevant. Finally, it
     * zooms in the interested zone. The interested zone is defined in the following order: <br/> If
     * bean has survey points it is zoomed there, otherwise if baUnitId is present it is zoomed
     * there else it is zoomed in the application location.
     *
     * @param applicationBean The application where the transaction is started identifiers
     * @param transactionStarterId The id of the starter of the application. This will be the
     * service id.
    * @param baUnitId Id of the property that is defined in the application as a target for this
     * cadastre change.
     */
    public ControlsBundleForMapExistingParcel(
            ApplicationBean applicationBean,
           String transactionStarterId,
            String baUnitId,
            String targetCadastreObjectType) {
       super(applicationBean, transactionStarterId);
        this.lastPartEntry = applicationBean.getNr();
       this.Setup(PojoDataAccess.getInstance());
        this.setTargetCadastreObjectTypeConfiguration(targetCadastreObjectType);
        this.refreshTransactionFromServer();
        this.setTransaction();
        if (!this.transactionIsStarted()) {
            this.setTargetCadastreObjectsOfBaUnit(baUnitId);
        }
        this.zoomToInterestingArea(null, applicationBean.getLocation());
    }

    /**
     * Constructor. It sets up the bundle by adding layers and tools that are relevant. Finally, it
     * zooms in the interested zone. The interested zone is defined in the following order: <br/> If
     * bean has survey points it is zoomed there, otherwise if baUnitId is present it is zoomed
     * there else it is zoomed in the application location.
     *
     * @param applicationBean The application where the transaction is started identifiers
     * @param transactionStarterId The id of the starter of the application. This will be the
     * service id.
     * @param baUnitId Id of the property that is defined in the application as a target for this
     * cadastre change.
     */
    public ControlsBundleForMapExistingParcel(
            TransactionCadastreChangeBean transactionBean,
            String targetCadastreObjectType,
            String lastPartEntry) {
        super(null, null);
        this.transactionBean = transactionBean;
        this.lastPartEntry = lastPartEntry;
        this.lastPartTemplate = "%s";
        this.Setup(PojoDataAccess.getInstance());
        this.setTargetCadastreObjectTypeConfiguration(targetCadastreObjectType);
        this.setTransaction();
        this.zoomToInterestingArea(null, null);
    }

    @Override
    protected boolean transactionIsStarted() {
        return (this.newPointsLayer.getFeatureCollection().size() > 0
                || this.targetParcelsLayer.getFeatureCollection().size() > 0);
    }

    /**
     * It zooms to the interesting area which is the area where the cadastre changes is happening
     *
     * @param interestingArea
     * @param applicationLocation
     */
    @Override
    public void zoomToInterestingArea(
            ReferencedEnvelope interestingArea, byte[] applicationLocation) {
        ReferencedEnvelope boundsToZoom = null;
        if (this.newPointsLayer.getFeatureCollection().size() > 0) {
            boundsToZoom = this.newPointsLayer.getFeatureCollection().getBounds();
        } else if (this.targetParcelsLayer.getFeatureCollection().size() > 0) {
            boundsToZoom = this.targetParcelsLayer.getFeatureCollection().getBounds();
        }
        super.zoomToInterestingArea(boundsToZoom, applicationLocation);
    }

    @Override
    public TransactionCadastreChangeBean getTransactionBean() {
        transactionBean.setCadastreObjectList(
                this.newCadastreObjectLayer.getBeanListForTransaction());
        transactionBean.setSurveyPointList(this.newPointsLayer.getBeanListForTransaction());
        transactionBean.setCadastreObjectTargetList(
                this.targetParcelsLayer.getBeanListForTransaction());
        if (this.getDocumentsPanel() != null){
            transactionBean.setSourceIdList(this.getDocumentsPanel().getSourceIds());
        }
        return transactionBean;
    }

    @Override
    public void refreshTransactionFromServer() {
        if (this.getTransactionStarterId() != null){
            this.transactionBean = PojoDataAccess.getInstance().getTransactionCadastreChange(
                    this.getTransactionStarterId());
        }else if (this.transactionBean != null){
            this.transactionBean = PojoDataAccess.getInstance().getTransactionCadastreChangeById(
                    this.transactionBean.getId());
        }else{
            this.transactionBean = new TransactionCadastreChangeBean();
        }
    }
    
    @Override
    public final void setTransaction() {
        //Reset the lists of beans in the layers
        this.targetParcelsLayer.getBeanList().clear();
        this.newCadastreObjectLayer.getBeanList().clear();
        this.newPointsLayer.getBeanList().clear();
        //Populate the lists of beans from the lists in transaction
        this.targetParcelsLayer.setBeanList(this.transactionBean.getCadastreObjectTargetList());
        this.newCadastreObjectLayer.setBeanList(
                this.transactionBean.getCadastreObjectList());
        this.newPointsLayer.setBeanList(this.transactionBean.getSurveyPointList());
        if (this.getDocumentsPanel() != null){
            this.getDocumentsPanel().setSourceIds(this.transactionBean.getSourceIdList());
        }
    }

    
    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        super.addLayers();
        this.targetParcelsLayer = new CadastreChangeTargetCadastreObjectLayer();
        this.getMap().addLayer(targetParcelsLayer);

        this.newCadastreObjectLayer = new CadastreChangeNewCadastreObjectLayer(
                getLastPart(this.lastPartEntry), true);
        //This flag defines if the client side generator or server side generator
        // will be used for new cadastre object identifier.
        // If it is false the server side generator will be used. Then
        // the database functions: cadastre.get_new_cadastre_object_identifier_last_part
        // and cadastre.get_new_cadastre_object_identifier_first_part has to be updated
        // to reflect the algorithm that is used for generating the identifier.
        this.newCadastreObjectLayer.setUseClientSideIdentifierGenerator(false);
        this.getMap().addLayer(newCadastreObjectLayer);

        this.newPointsLayer = new CadastreChangeNewSurveyPointLayer(this.newCadastreObjectLayer);
        this.getMap().addLayer(newPointsLayer);
    }

    @Override
    protected void addToolsAndCommands() {
        this.selectTargetCadastreObjectTool =
                new CadastreChangeSelectCadastreObjectTool(this.getPojoDataAccess());
        this.selectTargetCadastreObjectTool.setTargetParcelsLayer(targetParcelsLayer);
        this.getMap().addTool(this.selectTargetCadastreObjectTool, this.getToolbar(), true);

        this.getMap().addMapAction(
                new CadastreChangePointSurveyListFormShow(
                this.getMap(), this.newPointsLayer.getHostForm()),
                this.getToolbar(),
                true);

        CadastreChangeNodeTool nodelinkingTool = new CadastreChangeNodeTool(newPointsLayer);
        nodelinkingTool.getTargetSnappingLayers().add(this.targetParcelsLayer);
        if (this.getMap().getSolaLayers().containsKey(PARCEL_LAYER_NAME)) {
            ExtendedLayer snappingTargetLayer = 
                    this.getMap().getSolaLayers().get(PARCEL_LAYER_NAME);
            if (snappingTargetLayer.getClass() == PojoLayer.class) {
                nodelinkingTool.getTargetSnappingLayers().add((ExtendedFeatureLayer) snappingTargetLayer);
            }
        }
        this.getMap().addTool(nodelinkingTool, this.getToolbar(), true);

        this.newCadastreObjectTool =
                new CadastreChangeNewCadastreObjectTool(this.newCadastreObjectLayer);
        this.newCadastreObjectTool.getTargetSnappingLayers().add(newPointsLayer);
        this.getMap().addTool(newCadastreObjectTool, this.getToolbar(), true);

        this.getMap().addMapAction(new CadastreChangeNewCadastreObjectListFormShow(
                this.getMap(), this.newCadastreObjectLayer.getHostForm()),
                this.getToolbar(),
                true);

        CadastreBoundarySelectTool cadastreBoundarySelectTool =
                new CadastreBoundarySelectTool(
                this.cadastreBoundaryPointLayer,
                this.newCadastreObjectLayer,
                this.newCadastreObjectLayer.getVerticesLayer());

        this.getMap().addTool(cadastreBoundarySelectTool, this.getToolbar(), true);
        super.addToolsAndCommands();

        this.cadastreBoundaryEditTool.setTargetLayer(this.newCadastreObjectLayer);
        this.cadastreBoundaryEditTool.getTargetSnappingLayers().add(this.targetParcelsLayer);

    }

    @Override
    protected void setTargetCadastreObjectTypeConfiguration(String targetCadastreObjectType) {
        this.selectTargetCadastreObjectTool.setCadastreObjectType(targetCadastreObjectType);
        this.newCadastreObjectTool.setCadastreObjectType(targetCadastreObjectType);
        this.newCadastreObjectLayer.getSpatialObjectDisplayPanel().setCadastreObjectType(
                targetCadastreObjectType);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.getMap().getMapActionByName(CadastreChangeSelectCadastreObjectTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(
                CadastreChangePointSurveyListFormShow.MAPACTION_NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(CadastreChangeNodeTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(CadastreChangeNewCadastreObjectTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(
                CadastreChangeNewCadastreObjectListFormShow.MAPACTION_NAME).setEnabled(!readOnly);
    }

    /**
     * Sets cadastre objects that are related with the baUnitId
     *
     * @param baUnitId
     */
    private void setTargetCadastreObjectsOfBaUnit(String baUnitId) {
        List<CadastreObjectTO> cadastreObjects =
                this.getPojoDataAccess().getCadastreService().getCadastreObjectsByBaUnit(baUnitId);
        for (CadastreObjectTO cadastreObjectTo : cadastreObjects) {
            CadastreObjectTargetBean bean = new CadastreObjectTargetBean();
            bean.setCadastreObjectId(cadastreObjectTo.getId());
            bean.setGeomPolygonCurrent(cadastreObjectTo.getGeomPolygon());
            this.targetParcelsLayer.getBeanList().add(bean);
        }
    }
    
    private String getLastPart(String lastPartEntry){
        return String.format(lastPartTemplate, lastPartEntry);
    }
    
}   
