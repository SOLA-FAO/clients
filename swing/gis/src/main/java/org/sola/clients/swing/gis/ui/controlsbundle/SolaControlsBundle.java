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

import java.util.ArrayList;
import org.sola.clients.geotools.ui.layers.SolaFeatureLayer;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layers.PojoLayer;
import org.sola.clients.swing.gis.tools.InformationTool;
import org.sola.clients.geotools.ui.ControlsBundle;
import org.sola.webservices.spatial.ConfigMapLayerTO;
import org.sola.webservices.spatial.MapDefinitionTO;

/**
 *
 * @author Manoku
 */
public abstract class SolaControlsBundle extends ControlsBundle {

    private PojoDataAccess pojoDataAccess = null;
    private static boolean gisInitialized = false;
    
    public SolaControlsBundle(){
        super();
        if (!gisInitialized){
            org.sola.clients.geotools.util.Messaging.getInstance().setMessaging(new Messaging());
            SolaFeatureLayer.SLD_RESOURCES = "/org/sola/clients/swing/gis/layers/resources/,"
                    + SolaFeatureLayer.SLD_RESOURCES;
        }
    }
    
    public void Setup(PojoDataAccess pojoDataAccess) {
        try {
            this.pojoDataAccess = pojoDataAccess;
            MapDefinitionTO mapDefinition = pojoDataAccess.getMapDefinition();
            super.Setup(mapDefinition.getSrid(), true);
            InformationTool infoTool = new InformationTool(this.pojoDataAccess);
            this.getMap().addTool(infoTool, this.getToolbar());
            this.getMap().setFullExtent(
                    mapDefinition.getEast(),
                    mapDefinition.getWest(),
                    mapDefinition.getNorth(),
                    mapDefinition.getSouth());

            for (ConfigMapLayerTO configMapLayer : mapDefinition.getLayers()) {
                this.addLayerConfig(configMapLayer, pojoDataAccess);
            }
            this.getMap().zoomToFullExtent();
        } catch (Exception ex) {
            Messaging.getInstance().show("gis.controlbundle.error.setup");
            org.sola.common.logging.LogUtility.log(
                    "gis.controlbundle.error.setup", ex);
        }
    }

    public void addLayerConfig(ConfigMapLayerTO configMapLayer, PojoDataAccess pojoDataAccess)
            throws Exception {
        if (configMapLayer.getTypeCode().equals("wms")) {
            String wmsServerURL = configMapLayer.getWmsUrl();
            ArrayList<String> wmsLayerNames = new ArrayList<String>();
            String[] layerNameList = configMapLayer.getWmsLayers().split(";");
            java.util.Collections.addAll(wmsLayerNames, layerNameList);
            this.getMap().addLayerWMS(
                    configMapLayer.getId(), wmsServerURL, wmsLayerNames);
        } else if (configMapLayer.getTypeCode().equals("shape")) {
            this.getMap().addLayerShapefile(
                    configMapLayer.getId(),
                    configMapLayer.getShapeLocation(),
                    configMapLayer.getStyle());
        } else if (configMapLayer.getTypeCode().equals("pojo")) {
            PojoLayer layer = new PojoLayer(
                    configMapLayer.getId(), pojoDataAccess);
            this.getMap().addLayer(layer);
        }
    }

    public PojoDataAccess getPojoDataAccess() {
        return pojoDataAccess;
    }
    
    public void refresh(boolean force){
        this.getMap().refresh();
    }
}
