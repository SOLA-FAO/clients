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
package org.sola.clients.swing.gis.ui.controlsbundle;

import java.util.ArrayList;
import java.util.List;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.tool.extended.AddDirectImageTool;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.swing.gis.beans.StateLandParcelBean;
import org.sola.clients.swing.gis.beans.TransactionBean;
import org.sola.clients.swing.gis.beans.TransactionStateLandBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.StateLandEditLayer;
import org.sola.clients.swing.gis.mapaction.DisplayStateLandParcelListForm;
import org.sola.clients.swing.gis.mapaction.ZoomToTransaction;
import org.sola.clients.swing.gis.tool.StateLandCreateTool;
import org.sola.clients.swing.gis.tool.StateLandEditTool;
import org.sola.clients.swing.gis.tool.StateLandSelectTool;
import org.sola.common.StringUtility;

/**
 *
 * @author soladev
 */
public class ControlBundleForStateLand extends ControlsBundleForTransaction {

    private TransactionStateLandBean transactionBean;
    private String targetParcelType;
    private StateLandEditLayer editLayer;
    private StateLandEditTool editTool;

    public ControlBundleForStateLand(ApplicationBean applicationBean,
            String serviceId,
            String baUnitId,
            String targetParcelType) {
        super(applicationBean, serviceId);
        this.targetParcelType = targetParcelType;
        initializeMap();
    }

    private void initializeMap() {
        this.Setup(PojoDataAccess.getInstance(), false, false);
        this.refreshTransactionFromServer();
        this.setTransaction();
        zoomToInterestingArea(null, null);
    }

    @Override
    public TransactionBean getTransactionBean() {

        transactionBean.getStateLandParcels().clear();
        if (this.editLayer.getBeanListForTransaction() != null
                && this.editLayer.getBeanListForTransaction().size() > 0) {
            List newSLParcels = new ArrayList();
            int i = 1;
            for (StateLandParcelBean b
                    : (List<StateLandParcelBean>) this.editLayer.getBeanListForTransaction()) {
                if (StringUtility.isEmpty(b.getNameFirstpart())) {
                    b.setNameFirstpart("LOT " + i);
                }
                if (StringUtility.isEmpty(b.getNameLastpart())) {
                    b.setNameLastpart("12345");
                }

                newSLParcels.add(b);

                i++;
            }
            transactionBean.getStateLandParcels().addAll(newSLParcels);
        }
        return transactionBean;
    }

    @Override
    public void setTransaction() {
        editLayer.getBeanList().clear();
        editLayer.setBeanList(transactionBean.getStateLandParcels());
    }

    @Override
    public void refreshTransactionFromServer() {
        if (this.getTransactionStarterId() != null) {
            this.transactionBean = PojoDataAccess.getInstance().getStateLandChange(
                    this.getTransactionStarterId());
        } else {
            this.transactionBean = new TransactionStateLandBean();
        }

    }

    /**
     * Determines if the user has started a transaction by checking if any
     * features are defined in the State Land Edit Layer.
     *
     * @return true of the State Land Edit layer contains one or more features.
     */
    @Override
    protected boolean transactionIsStarted() {
        return editLayer.getFeatureCollection().size() > 0;
    }

    @Override
    protected void setTargetCadastreObjectTypeConfiguration(String targetCadastreObjectType) {
        // No action required
    }

    /**
     * Zooms the map to the area containing the state land parcels that are
     * being edited for this application
     *
     * @param interestingArea The area to zoom to (always null as the parameter
     * is passed to the super method)
     * @param applicationLocation The point location of the application.
     */
    @Override
    public void zoomToInterestingArea(
            ReferencedEnvelope interestingArea, byte[] applicationLocation) {
        super.zoomToInterestingArea(this.editLayer.getLayerEnvelope(), applicationLocation);
    }

    @Override
    protected void addToolsAndCommands() {

        //State Land Edit Tools
        this.getMap().addMapAction(new ZoomToTransaction(this), this.getToolbar(), true);
        
        ExtendedFeatureLayer parcelsLayer = (ExtendedFeatureLayer) this.getMap().getSolaLayers().get("parcels");
        ExtendedFeatureLayer roadLayer = (ExtendedFeatureLayer) this.getMap().getSolaLayers().get("roads");
        ExtendedFeatureLayer stateLandParcelsLayer = (ExtendedFeatureLayer) this.getMap().getSolaLayers().get("state-land");
        List<ExtendedFeatureLayer> selectionLayers = new ArrayList<ExtendedFeatureLayer>();
        selectionLayers.add(parcelsLayer);
        selectionLayers.add(roadLayer);
        this.getMap().addTool(new StateLandSelectTool(selectionLayers, this.editLayer),
                this.getToolbar(), true);

        StateLandCreateTool createTool = new StateLandCreateTool(this.editLayer);
        createTool.getTargetSnappingLayers().add(stateLandParcelsLayer);
        createTool.getTargetSnappingLayers().add(parcelsLayer);
        createTool.getTargetSnappingLayers().add(roadLayer);
        this.getMap().addTool(createTool, this.getToolbar(), true);

        editTool = new StateLandEditTool(this.editLayer);
        // Set the snapping layers for the EditSpatialUnitTool
        editTool.getTargetSnappingLayers().add(stateLandParcelsLayer);
        editTool.getTargetSnappingLayers().add(parcelsLayer);
        editTool.getTargetSnappingLayers().add(roadLayer);
        this.getMap().addTool(editTool, this.getToolbar(), true);
        this.getMap().addMapAction(new DisplayStateLandParcelListForm(this.getMap(), this.editLayer), 
                this.getToolbar(), true);

        // Add tools and commands to the end of the state land tools
        super.addToolsAndCommands();
    }

    /**
     * Adds layers that are needed for the transaction
     *
     * @throws InitializeLayerException
     */
    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        super.addLayers();
        editLayer = new StateLandEditLayer(getAppBean().getNr());
        getMap().addLayer(editLayer);
        getMap().moveSelectionLayer();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.getMap().getMapActionByName(StateLandSelectTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(StateLandEditTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(StateLandCreateTool.NAME).setEnabled(!readOnly);
        this.getMap().getMapActionByName(AddDirectImageTool.NAME).setEnabled(!readOnly);
    }

}
