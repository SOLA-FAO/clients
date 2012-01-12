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

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.TransactionCadastreRedefinitionBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.data.PojoFeatureSource;
import org.sola.clients.swing.gis.layer.CadastreObjectModifiedLayer;
import org.sola.clients.swing.gis.layer.PojoLayer;
import org.sola.clients.swing.gis.layer.TargetCadastreObjectLayer;
import org.sola.clients.swing.gis.tool.ModifyNodeTool;
import org.sola.common.messaging.GisMessage;

/**
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForCadastreRedefinition extends ControlsBundleForWorkingWithCO {

    private TransactionCadastreRedefinitionBean transactionBean;
    private TargetCadastreObjectLayer targetParcelsLayer = null;
    private CadastreObjectModifiedLayer cadastreObjectModifiedLayer = null;
    private PojoLayer pendingLayer = null;
    private String applicationNumber = "";

    public ControlsBundleForCadastreRedefinition(
            String applicationNumber,
            TransactionCadastreRedefinitionBean transactionBean,
            byte[] applicationLocation) {
        super();
        this.transactionBean = transactionBean;
        this.applicationNumber = applicationNumber;
        this.Setup(PojoDataAccess.getInstance());

        ReferencedEnvelope boundsToZoom = null;
        if (applicationLocation != null) {
            try {
                Geometry applicationLocationGeometry =
                        PojoFeatureSource.getWkbReader().read(applicationLocation);
                boundsToZoom = JTS.toEnvelope(applicationLocationGeometry);
            } catch (Exception ex) {
                Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
                org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
            }
        }
        if (boundsToZoom != null) {
            boundsToZoom.expandBy(20);
            this.getMap().setDisplayArea(boundsToZoom);
        }
    }

    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess);
        try {

            if (this.transactionBean == null) {
                this.transactionBean = new TransactionCadastreRedefinitionBean();
            }

            //Adding layers
            this.addLayers();

            //Adding tools and commands
            this.addToolsAndCommands();

            //this.getMap().addMapAction(new TestCadastreRequest(this), this.getToolbar());

            for (ExtendedLayer solaLayer : this.getMap().getSolaLayers().values()) {
                if (solaLayer.getClass().equals(PojoLayer.class)) {
                    if (((PojoLayer) solaLayer).getConfig().getId().equals(
                            PojoLayer.CONFIG_PENDING_PARCELS_LAYER_NAME)) {
                        this.pendingLayer = (PojoLayer) solaLayer;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
            org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
        }
    }

    public TransactionCadastreRedefinitionBean getTransactionBean() {
//        this.transactionBean.setCadastreObjectList(
//                this.cadastreObjectModifiedLayer.getCadastreObjectList());
        this.transactionBean.setCadastreObjectTargetList(
                this.targetParcelsLayer.getCadastreObjectTargetList());
        return this.transactionBean;
    }

    private void addLayers() throws Exception {
        this.targetParcelsLayer = new TargetCadastreObjectLayer(this.getMap().getSrid());
        this.getMap().addLayer(targetParcelsLayer);

        this.cadastreObjectModifiedLayer = new CadastreObjectModifiedLayer();
        this.getMap().addLayer(this.cadastreObjectModifiedLayer);

        this.targetParcelsLayer.setCadastreObjectTargetList(
                transactionBean.getCadastreObjectTargetList());

    }

    private void addToolsAndCommands() {
        this.getMap().addTool(new ModifyNodeTool(), this.getToolbar());
    }

    @Override
    public void refresh(boolean force) {
        this.pendingLayer.setForceRefresh(force);
        super.refresh(force);
    }
}
