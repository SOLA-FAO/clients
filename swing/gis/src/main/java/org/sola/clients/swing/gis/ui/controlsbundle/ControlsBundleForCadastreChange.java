/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
import com.vividsolutions.jts.io.WKBReader;
import org.geotools.geometry.jts.JTS;
import org.sola.clients.swing.gis.beans.CadastreChangeBean;
import java.util.List;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.WKTReader2;
import org.sola.clients.geotools.ui.layers.SolaLayer;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.data.PojoFeatureSource;
import org.sola.clients.swing.gis.layers.NewCadastreObjectLayer;
import org.sola.clients.swing.gis.layers.NewSurveyPointLayer;
import org.sola.clients.swing.gis.layers.PojoLayer;
import org.sola.clients.swing.gis.layers.TargetCadastreObjectLayer;
import org.sola.clients.swing.gis.mapactions.NewCadastreObjectListFormShow;
import org.sola.clients.swing.gis.mapactions.PointSurveyListFormShow;
import org.sola.clients.swing.gis.mapactions.TestCadastreChangeBean;
import org.sola.clients.swing.gis.tools.NewParcelTool;
import org.sola.clients.swing.gis.tools.NodeLinkingTool;
import org.sola.clients.swing.gis.tools.SelectParcelTool;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/**
 *
 * @author manoku
 */
public final class ControlsBundleForCadastreChange extends ControlsBundleForWorkingWithCO {

    private CadastreChangeBean cadastreChangeBean;
    private NewParcelTool newParcelTool = null;
    private SelectParcelTool selectParcelTool = null;
    private NodeLinkingTool nodelinkingTool = null;
    private TargetCadastreObjectLayer targetParcelsLayer = null;
    private NewCadastreObjectLayer newCadastreObjectLayer = null;
    private NewSurveyPointLayer newPointsLayer = null;
    private PojoLayer pendingLayer = null;
    private String applicationNumber = "";

    public ControlsBundleForCadastreChange(
            String applicationNumber,
            CadastreChangeBean cadastreChangeBean,
            String baUnitId,
            byte[] applicationLocation) {
        super();
        this.cadastreChangeBean = cadastreChangeBean;
        this.applicationNumber = applicationNumber;
        this.Setup(PojoDataAccess.getInstance());

        ReferencedEnvelope boundsToZoom = null;
        if (this.newPointsLayer.getFeatureCollection().size() > 0) {
            boundsToZoom = this.newPointsLayer.getFeatureCollection().getBounds();
        } else if (baUnitId != null) {
            this.setTargetParcelsByBaUnit(baUnitId);
        } else if (applicationLocation != null) {
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

            if (this.cadastreChangeBean == null) {
                this.cadastreChangeBean = new CadastreChangeBean();
            }

            //Adding layers
            this.addLayers();

            //Adding tools and commands
            this.addToolsAndCommands();

            this.getMap().addMapAction(new TestCadastreChangeBean(this), this.getToolbar());

            for (SolaLayer solaLayer : this.getMap().getSolaLayers().values()) {
                if (solaLayer.getClass().equals(PojoLayer.class)) {
                    if (((PojoLayer) solaLayer).getConfig().getUsedFor().equals(
                            PojoLayer.CONFIG_USER_FOR)) {
                        this.pendingLayer = (PojoLayer) solaLayer;
                    }
                }
            }
        } catch (Exception ex) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
            org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
        }
    }

    public CadastreChangeBean getCadastreChangeBean() {
        cadastreChangeBean.setNewCadastreObjectList(
                this.newCadastreObjectLayer.getCadastreObjectList());
        cadastreChangeBean.setSurveyPointList(this.newPointsLayer.getSurveyPointList());
        cadastreChangeBean.setTargetCadastreObjectIdList(
                this.targetParcelsLayer.getCadastreObjectIdTargetList());
        return cadastreChangeBean;
    }

    private void addLayers() throws Exception {
        this.targetParcelsLayer = new TargetCadastreObjectLayer(this.getMap().getSrid());
        this.getMap().addLayer(targetParcelsLayer);

        this.newCadastreObjectLayer = new NewCadastreObjectLayer(
                this.getMap().getSrid(), this.applicationNumber);
        this.getMap().addLayer(newCadastreObjectLayer);

        this.newPointsLayer = new NewSurveyPointLayer(
                this.getMap().getSrid(), this.newCadastreObjectLayer);
        this.getMap().addLayer(newPointsLayer);

        this.targetParcelsLayer.setCadastreObjectIdTargetList(
                cadastreChangeBean.getTargetCadastreObjectIdList());

        this.newPointsLayer.setSurveyPointList(this.cadastreChangeBean.getSurveyPointList());

        this.newCadastreObjectLayer.setCadastreObjectList(
                this.cadastreChangeBean.getNewCadastreObjectList());
    }

    private void addToolsAndCommands() {
        selectParcelTool = new SelectParcelTool(this.getPojoDataAccess());
        selectParcelTool.setTargetParcelsLayer(targetParcelsLayer);
        this.getMap().addTool(selectParcelTool, this.getToolbar());

        this.getMap().addMapAction(
                new PointSurveyListFormShow(this.getMap(), this.newPointsLayer.getHostForm()),
                this.getToolbar());

        nodelinkingTool = new NodeLinkingTool(newPointsLayer);
        nodelinkingTool.getTargetSnappingLayers().add(this.targetParcelsLayer);
        this.getMap().addTool(nodelinkingTool, this.getToolbar());

        newParcelTool = new NewParcelTool(this.newCadastreObjectLayer);
        newParcelTool.getTargetSnappingLayers().add(newPointsLayer);
        this.getMap().addTool(newParcelTool, this.getToolbar());

        this.getMap().addMapAction(new NewCadastreObjectListFormShow(
                this.getMap(), this.newCadastreObjectLayer.getHostForm()),
                this.getToolbar());
    }

    public void setTargetParcelsByBaUnit(String baUnitId) {
        List<CadastreObjectTO> cadastreObjects =
                this.getPojoDataAccess().getCadastreService().getCadastreObjectsByBaUnit(baUnitId);

        this.addCadastreObjectsInLayer(targetParcelsLayer, cadastreObjects);
    }

    public void setTargetParcelsByService(String serviceId) {
        List<CadastreObjectTO> cadastreObjects =
                this.getPojoDataAccess().getCadastreService().getCadastreObjectsByService(serviceId);

        this.addCadastreObjectsInLayer(targetParcelsLayer, cadastreObjects);
    }

    @Override
    public void refresh(boolean force) {
        this.pendingLayer.setForceRefresh(force);
        super.refresh(force);
    }
}
