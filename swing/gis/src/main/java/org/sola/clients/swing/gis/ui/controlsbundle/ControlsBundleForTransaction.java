/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.controlsbundle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.geotools.map.extended.layer.ExtendedImageLayer;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.mapaction.extended.RemoveDirectImage;
import org.geotools.swing.tool.extended.AddDirectImageTool;
import org.geotools.swing.tool.extended.ExtendedDrawToolWithSnapping;
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
 * An abstract bundle that defines common functionality that is used in the
 * cadastre transaction related changes. It defines also abstract methods that
 * need to be overridden by each cadastre transaction.
 *
 * @author Elton Manoku
 */
public abstract class ControlsBundleForTransaction extends SolaControlsBundle {

    protected static final String PARCEL_LAYER_NAME = "parcels";
    protected static final String PARCEL_PENDING_LAYER_NAME = "pending-parcels";

    private PojoLayer pendingLayer = null;
    private ExtendedImageLayer imageLayer = null;
    private static final String IMAGE_LAYER_NAME = "temporary_image";
    protected CadastreBoundaryPointLayer cadastreBoundaryPointLayer = null;
    protected CadastreBoundaryEditTool cadastreBoundaryEditTool;
    private String transactionStarterId;
    private ApplicationBean applicationBean;
    private MapDocumentsPanel documentsPanel;
    private boolean addBoundaryEditLayer = true;

    /**
     * Creates a controls bundle for transaction component.
     *
     * @param applicationBean The application bean of the application from where
     * the transaction starts
     * @param transactionStarterId The id of the starter of the transaction.
     * This will be the service id.
     */
    public ControlsBundleForTransaction(
            ApplicationBean applicationBean,
            String transactionStarterId) {
        super();
        this.applicationBean = applicationBean;
        this.transactionStarterId = transactionStarterId;
    }

    /**
     * Gets an instance of a transaction bundle depending in the type of the
     * request
     *
     * @param requestTypeCode The type of the request for which to create the
     * transaction bundle
     * @param applicationBean The application bean of the application where the
     * transaction is starting
     * @param transactionStarterId The transaction starter id. It will be the id
     * of the service that will start the transaction
     * @param baUnitId The id of the ba unit which will be used to identify the
     * cadastre object being targeted
     * @param targetCadastreObjectType the type of the cadastre object type
     * being targeted
     * @return
     */
    public static ControlsBundleForTransaction getInstance(
            String requestTypeCode,
            ApplicationBean applicationBean,
            String transactionStarterId,
            String baUnitId,
            String targetCadastreObjectType) {
        ControlsBundleForTransaction instance = null;
        if (requestTypeCode.equals(RequestTypeBean.CODE_CADASTRE_CHANGE)) {
            instance = new ControlsBundleForCadastreChange(
                    applicationBean, transactionStarterId, baUnitId, targetCadastreObjectType);
        } else if (requestTypeCode.equals(RequestTypeBean.CODE_CADASTRE_REDEFINITION)) {
            instance = new ControlsBundleForCadastreRedefinition(
                    applicationBean, transactionStarterId, baUnitId, targetCadastreObjectType);
        } else if (requestTypeCode.equals(RequestTypeBean.CODE_MAP_EXISTINGPARCEL)) {
            instance = new ControlsBundleForMapExistingParcel(
                    applicationBean, transactionStarterId, baUnitId, targetCadastreObjectType);
        } else if (requestTypeCode.equals(RequestTypeBean.CODE_CHANGE_STATE_LAND_PARCELS)) {
            instance = new ControlBundleForStateLand(
                    applicationBean, transactionStarterId, baUnitId, targetCadastreObjectType);
        }
        return instance;
    }

    /**
     * It sets up the bundle. It calls the adding layer method and adding tools
     * method. It also identifies the pending layer which will be refreshed if a
     * transaction is being saved in the database.
     *
     * @param pojoDataAccess
     */
    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        Setup(pojoDataAccess, true, true);
    }

    /**
     * Overloaded version of Setup that can be used to suppress display of the
     * documents tab and/or the Boundary Edit Layer Tool.
     *
     * @param pojoDataAccess
     * @param addDocumentsTab If true, the Document Tab will be added to the Map
     * tab control.
     * @param addBoundaryEditLayer If true, add the cadastre boundary edit layer
     * to the map
     */
    protected void Setup(PojoDataAccess pojoDataAccess, boolean addDocumentsTab,
            boolean addBoundaryEditLayer) {
        this.addBoundaryEditLayer = addBoundaryEditLayer;
        super.Setup(pojoDataAccess);

        //Adding tools and commands
        this.addToolsAndCommands();

        if (addDocumentsTab) {
            this.addDocumentsPanel();
        }

        for (ExtendedLayer solaLayer : this.getMap().getSolaLayers().values()) {
            if (solaLayer.getClass().equals(PojoLayer.class)) {
                if (((PojoLayer) solaLayer).getConfig().getId().equals(
                        PojoLayer.CONFIG_PENDING_PARCELS_LAYER_NAME)) {
                    this.pendingLayer = (PojoLayer) solaLayer;
                    break;
                }
            }
        }
    }

    @Override
    protected void setupToolbar() {
        this.getMap().addMapAction(new SaveTransaction(this), this.getToolbar(), true);
        super.setupToolbar();
    }

    /**
     * Gets the panel where the documents attached to the transaction are
     * managed
     *
     * @return
     */
    protected final MapDocumentsPanel getDocumentsPanel() {
        return documentsPanel;
    }

    /**
     * Gets the transaction starter id
     *
     * @return
     */
    protected String getTransactionStarterId() {
        return transactionStarterId;
    }

    /**
     * It zooms in the map where the transaction is happening
     *
     * @param interestingArea
     * @param applicationLocation
     */
    public void zoomToInterestingArea(
            ReferencedEnvelope interestingArea,
            byte[] applicationLocation) {
        if (interestingArea == null && applicationLocation != null) {
            try {
                Geometry applicationLocationGeometry
                        = PojoFeatureSource.getWkbReader().read(applicationLocation);
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
     * It refreshes the transaction by retrieving its information again from the
     * server.
     */
    public abstract void refreshTransactionFromServer();

    /**
     * Gets if the transaction is already started before.
     *
     * @return True if the transaction was already started and now is read back
     * for modifications
     */
    protected abstract boolean transactionIsStarted();

    /**
     * Adds layers that are needed for the transaction
     *
     * @throws InitializeLayerException
     */
    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        super.addLayers();
        this.imageLayer = new ExtendedImageLayer(IMAGE_LAYER_NAME,
                ((Messaging) Messaging.getInstance()).getLayerTitle(IMAGE_LAYER_NAME));
        this.getMap().addLayer(this.imageLayer);
        if (addBoundaryEditLayer) {
            this.cadastreBoundaryPointLayer = new CadastreBoundaryPointLayer();
            this.getMap().addLayer(this.cadastreBoundaryPointLayer);
        }
    }

    /**
     * Adds tools and commands that are relevant to the transaction
     *
     */
    protected void addToolsAndCommands() {
        if (addBoundaryEditLayer) {
            this.cadastreBoundaryEditTool
                    = new CadastreBoundaryEditTool(this.cadastreBoundaryPointLayer);
            this.getMap().addTool(this.cadastreBoundaryEditTool, this.getToolbar(), false);
        }
        this.getMap().addTool(new AddDirectImageTool(this.imageLayer), this.getToolbar(), true);
        this.getMap().addMapAction(new RemoveDirectImage(this.getMap()), this.getToolbar(), true);
        if (this.applicationBean != null) {
            this.setApplicationId(this.applicationBean.getId());
        }
    }

    /**
     * It refreshes the map control part of the bundle.
     *
     * @param force If true it forces the refresh of the pending layer
     */
    @Override
    public void refresh(boolean force) {
        if (this.pendingLayer != null) {
            this.pendingLayer.setForceRefresh(force);
        }
        super.refresh(force);
    }

    /**
     * It disables/enables the changing tools and commands in order to prohibit
     * user changing the transaction.
     *
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        this.getMap().getMapActionByName(SaveTransaction.MAPACTION_NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(AddDirectImageTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(RemoveDirectImage.NAME).setEnabled(!readOnly);
        if (addBoundaryEditLayer) {
            this.getMap().getMapActionByName(CadastreBoundarySelectTool.MAP_ACTION_NAME).setEnabled(!readOnly);
        }
    }

    /**
     * It configures the tools to handle the given type of cadastre objects. It
     * must be called after the Setup method because the Setup method initiates
     * the tools.
     *
     * @param targetCadastreObjectType
     */
    protected abstract void setTargetCadastreObjectTypeConfiguration(
            String targetCadastreObjectType);

    /**
     * It adds the panel where the documents are managed
     */
    private void addDocumentsPanel() {
        if (this.applicationBean == null) {
            return;
        }
        this.documentsPanel = new MapDocumentsPanel(this, this.applicationBean);
        this.addInLeftPanel(Messaging.getInstance().getMessageText(
                GisMessage.LEFT_PANEL_TAB_DOCUMENTS_TITLE), this.documentsPanel);
    }

    /**
     * It adds a layer in the list of snapping layers for a given tool. It
     * expects that all layers are already added to the map control. The layer
     * will be added in the snapping layers of the tools if it exist and if it
     * is a navigation layer of type ExtendedFeatureLayer.
     *
     * @param forTool The tool that will target features of the given layer for
     * snapping
     * @param targetLayerName The layer name
     */
    protected final void addSnappingLayerToTool(
            ExtendedDrawToolWithSnapping forTool, String targetLayerName) {
        if (this.getMap().getSolaLayers().containsKey(targetLayerName)) {
            ExtendedLayer snappingTargetLayer
                    = this.getMap().getSolaLayers().get(targetLayerName);
            if (snappingTargetLayer instanceof ExtendedFeatureLayer) {
                forTool.getTargetSnappingLayers().add((ExtendedFeatureLayer) snappingTargetLayer);
            }
        }
    }

    protected ApplicationBean getAppBean() {
        return applicationBean;
    }

}
