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

import java.util.ArrayList;
import org.geotools.feature.SchemaException;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.PojoLayer;
import org.sola.clients.swing.gis.tool.InformationTool;
import org.geotools.swing.extended.ControlsBundle;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.sola.clients.swing.gis.mapaction.SolaPrint;
import org.sola.clients.swing.gis.ui.control.SearchPanel;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.spatial.ConfigMapLayerTO;
import org.sola.webservices.spatial.MapDefinitionTO;

/**
 *
 * @author Elton Manoku
 */
public abstract class SolaControlsBundle extends ControlsBundle {

    private static boolean gisInitialized = false;
    private PojoDataAccess pojoDataAccess = null;
    
    private SolaPrint solaPrint = null;

    public SolaControlsBundle() {
        super();
        if (!gisInitialized) {
            org.geotools.swing.extended.util.Messaging.getInstance().setMessaging(new Messaging());
            ExtendedFeatureLayer.sldResources = "/org/sola/clients/swing/gis/layer/resources/,"
                    + ExtendedFeatureLayer.sldResources;
            gisInitialized = true;
        }
    }

    public void Setup(PojoDataAccess pojoDataAccess) {
        try {
            this.pojoDataAccess = pojoDataAccess;
            MapDefinitionTO mapDefinition = pojoDataAccess.getMapDefinition();
            super.Setup(mapDefinition.getSrid(), mapDefinition.getWktOfCrs(), true);
            this.addSearchPanel();
            InformationTool infoTool = new InformationTool(this.pojoDataAccess);
            this.getMap().addTool(infoTool, this.getToolbar(), true);
            this.solaPrint = new SolaPrint(this.getMap());
            this.getMap().addMapAction(this.solaPrint, this.getToolbar(), true);
            
            this.getMap().setFullExtent(
                    mapDefinition.getEast(),
                    mapDefinition.getWest(),
                    mapDefinition.getNorth(),
                    mapDefinition.getSouth());

            for (ConfigMapLayerTO configMapLayer : mapDefinition.getLayers()) {
                this.addLayerConfig(configMapLayer, pojoDataAccess);
            }
            this.getMap().zoomToFullExtent();
        } catch (InitializeLayerException ex) {
            Messaging.getInstance().show(GisMessage.GENERAL_CONTROLBUNDLE_ERROR);
            org.sola.common.logging.LogUtility.log(
                    GisMessage.GENERAL_CONTROLBUNDLE_ERROR, ex);
        } catch (InitializeMapException ex) {
            Messaging.getInstance().show(GisMessage.GENERAL_CONTROLBUNDLE_ERROR);
            org.sola.common.logging.LogUtility.log(
                    GisMessage.GENERAL_CONTROLBUNDLE_ERROR, ex);
        } catch (SchemaException ex) {
            Messaging.getInstance().show(GisMessage.GENERAL_CONTROLBUNDLE_ERROR);
            org.sola.common.logging.LogUtility.log(
                    GisMessage.GENERAL_CONTROLBUNDLE_ERROR, ex);
        }
    }

    public void addLayerConfig(ConfigMapLayerTO configMapLayer, PojoDataAccess pojoDataAccess)
            throws InitializeLayerException, SchemaException {
        ExtendedLayer layer = null;
        if (configMapLayer.getTypeCode().equals("wms")) {
            String wmsServerURL = configMapLayer.getWmsUrl();
            ArrayList<String> wmsLayerNames = new ArrayList<String>();
            String[] layerNameList = configMapLayer.getWmsLayers().split(";");
            java.util.Collections.addAll(wmsLayerNames, layerNameList);
            layer = this.getMap().addLayerWMS(
                    configMapLayer.getId(), configMapLayer.getTitle(), wmsServerURL, wmsLayerNames);
        } else if (configMapLayer.getTypeCode().equals("shape")) {
            layer = this.getMap().addLayerShapefile(
                    configMapLayer.getId(),
                    configMapLayer.getTitle(),
                    configMapLayer.getShapeLocation(),
                    configMapLayer.getStyle());
        } else if (configMapLayer.getTypeCode().equals("pojo")) {
            layer = new PojoLayer(
                    configMapLayer.getId(), pojoDataAccess);
            this.getMap().addLayer(layer);
        }
    }

    public PojoDataAccess getPojoDataAccess() {
        return pojoDataAccess;
    }

    public void refresh(boolean force) {
        this.getMap().refresh();
    }
    
    public void setApplicationId(String applicationId){
        this.solaPrint.setApplicationId(applicationId);
    }
    
    private void addSearchPanel(){
        SearchPanel panel = new SearchPanel(this.getMap());
        this.addInLeftPanel(Messaging.getInstance().getMessageText(
                GisMessage.LEFT_PANEL_TAB_FIND_TITLE), panel);
    }
}
