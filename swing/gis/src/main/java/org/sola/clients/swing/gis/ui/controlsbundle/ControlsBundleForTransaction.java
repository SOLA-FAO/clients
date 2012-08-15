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
import com.vividsolutions.jts.io.ParseException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedImageLayer;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.mapaction.extended.RemoveDirectImage;
import org.geotools.swing.tool.extended.AddDirectImageTool;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.TransactionBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.data.PojoFeatureSource;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.clients.swing.gis.layer.PojoLayer;
import org.sola.clients.swing.gis.mapaction.SaveTransaction;
import org.sola.clients.swing.gis.tool.CadastreBoundaryEditTool;
import org.sola.clients.swing.gis.tool.CadastreBoundarySelectTool;
import org.sola.clients.swing.gis.ui.control.MapDocumentsPanel;
import org.sola.common.messaging.GisMessage;

/**
 * An abstract bundle that defines common functionality that is used in the cadastre transaction
 * related changes. It defines also abstract methods that need to be overridden by each cadastre
 * transaction.
 *
 * @author Elton Manoku
 */
public abstract class ControlsBundleForTransaction extends SolaControlsBundle {

    private PojoLayer pendingLayer = null;
    private ExtendedImageLayer imageLayer = null;
    private static final String IMAGE_LAYER_NAME = "temporary_image";
    protected CadastreBoundaryPointLayer cadastreBoundaryPointLayer = null;
    protected CadastreBoundaryEditTool cadastreBoundaryEditTool;
    private String transactionStarterId;
    private ApplicationBean applicationBean;
    private MapDocumentsPanel documentsPanel;

    
    public ControlsBundleForTransaction(
            ApplicationBean applicationBean, 
            String transactionStarterId){
        super();
        this.applicationBean = applicationBean;
        this.transactionStarterId = transactionStarterId;
    }
    
    public static ControlsBundleForTransaction getInstance(
            String requestTypeCode,
            ApplicationBean applicationBean,
            String transactionStarterId,
            String baUnitId,
            String targetCadastreObjectType){
        ControlsBundleForTransaction instance = null;
        if (requestTypeCode.equals(RequestTypeBean.CODE_CADASTRE_CHANGE)) {
            instance = new ControlsBundleForCadastreChange(
                    applicationBean, transactionStarterId, baUnitId, targetCadastreObjectType);
        } else if (requestTypeCode.equals(RequestTypeBean.CODE_CADASTRE_REDEFINITION)) {
            instance = new ControlsBundleForCadastreRedefinition(
                    applicationBean, transactionStarterId, baUnitId, targetCadastreObjectType);
        }
        return instance;
    }
    
    /**
     * It sets up the bundle. It calls the adding layer method and adding tools method. It also
     * identifies the pending layer which will be refreshed if a transaction is being saved in the
     * database.
     *
     * @param pojoDataAccess
     */
    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess);
        try {
            
            //Adding layers
            this.addLayers();

            //Adding tools and commands
            this.addToolsAndCommands();

            this.addDocumentsPanel();

            for (ExtendedLayer solaLayer : this.getMap().getSolaLayers().values()) {
                if (solaLayer.getClass().equals(PojoLayer.class)) {
                    if (((PojoLayer) solaLayer).getConfig().getId().equals(
                            PojoLayer.CONFIG_PENDING_PARCELS_LAYER_NAME)) {
                        this.pendingLayer = (PojoLayer) solaLayer;
                        break;
                    }
                }
            }

        } catch (InitializeLayerException ex) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
            org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
        }
    }

    @Override
    protected void setupToolbar() {
        this.getMap().addMapAction(new SaveTransaction(this), this.getToolbar(), true);
        super.setupToolbar();
    }

    protected final MapDocumentsPanel getDocumentsPanel() {
        return documentsPanel;
    }

    protected String getTransactionStarterId() {
        return transactionStarterId;
    }
    
    /**
     * It zooms in the map where the transaction is happening
     *
     * @param interestingArea
     * @param applicationLocation
     */
    protected void zoomToInterestingArea(
            ReferencedEnvelope interestingArea,
            byte[] applicationLocation) {
        if (interestingArea == null && applicationLocation != null) {
            try {
                Geometry applicationLocationGeometry =
                        PojoFeatureSource.getWkbReader().read(applicationLocation);
                interestingArea = JTS.toEnvelope(applicationLocationGeometry);
            } catch (ParseException ex) {
                Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
                org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
            }
        }
        if (interestingArea != null) {
            interestingArea.expandBy(20);
            this.getMap().setDisplayArea(interestingArea);
        }
    }

    /**
     * Gets the transaction bean that is sent to the server.
     *
     * @return
     */
    public abstract TransactionBean getTransactionBean();
    
    /**
     * It sets the transaction.
     */
    public abstract void setTransaction();
    
    /**
     * Gets if the transaction is already started before.
     *
     * @return True if the transaction was already started and now is read back for modifications
     */
    protected abstract boolean transactionIsStarted();

    /**
     * Adds layers that are needed for the transaction
     *
     * @throws InitializeLayerException
     */
    protected void addLayers() throws InitializeLayerException {
        this.imageLayer = new ExtendedImageLayer(IMAGE_LAYER_NAME, 
                ((Messaging)Messaging.getInstance()).getLayerTitle(IMAGE_LAYER_NAME));
        this.getMap().addLayer(this.imageLayer);
        this.cadastreBoundaryPointLayer = new CadastreBoundaryPointLayer();
        this.getMap().addLayer(this.cadastreBoundaryPointLayer);
    }

    /**
     * Adds tools and commands that are relevant to the transaction
     *
     */
    protected void addToolsAndCommands() {
        this.cadastreBoundaryEditTool =
                new CadastreBoundaryEditTool(this.cadastreBoundaryPointLayer);
        this.getMap().addTool(this.cadastreBoundaryEditTool, this.getToolbar(), false);
        this.getMap().addTool(new AddDirectImageTool(this.imageLayer), this.getToolbar(), true);
        this.getMap().addMapAction(new RemoveDirectImage(this.getMap()), this.getToolbar(), true);
        this.setApplicationId(this.applicationBean.getId());
    }

    @Override
    public void refresh(boolean force) {
        this.pendingLayer.setForceRefresh(force);
        super.refresh(force);
    }

    /**
     * It disables/enables the changing tools and commands in order to prohibit user changing the
     * transaction.
     *
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        this.getMap().getMapActionByName(SaveTransaction.MAPACTION_NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(CadastreBoundarySelectTool.MAP_ACTION_NAME).setEnabled(!readOnly);
    }
    
    /**
     * It configures the tools to handle the given type of cadastre objects.
     * It must be called after the Setup method because the Setup method initiates the tools.
     * 
     * @param targetCadastreObjectType 
     */
    protected abstract void setTargetCadastreObjectTypeConfiguration(
            String targetCadastreObjectType);

    /**
     * It adds the panel where the documents are managed
     */
    private void addDocumentsPanel() {
        this.documentsPanel = new  MapDocumentsPanel(this, this.applicationBean);
        this.addInLeftPanel(Messaging.getInstance().getMessageText(
                GisMessage.LEFT_PANEL_TAB_DOCUMENTS_TITLE), this.documentsPanel);
    }

}
