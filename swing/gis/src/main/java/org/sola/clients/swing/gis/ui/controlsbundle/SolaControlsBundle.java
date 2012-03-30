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
import org.geotools.swing.extended.ControlsBundle;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.PojoLayer;
import org.sola.clients.swing.gis.mapaction.SolaPrint;
import org.sola.clients.swing.gis.tool.InformationTool;
import org.sola.clients.swing.gis.ui.control.SearchPanel;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.spatial.ConfigMapLayerTO;
import org.sola.webservices.spatial.MapDefinitionTO;

/**
 * This is the basic abstract bundle used in Sola. It sets up the map control with common layers
 * from the layer definitions found in database. 
 * Also using the map definition the full extent and srid are defined.
 * It defines also extra resources for the SLDs.
 * 
 * @author Elton Manoku
 */
public abstract class SolaControlsBundle extends ControlsBundle {

    private static String extraSldResources = "/org/sola/clients/swing/gis/layer/resources/";
    private static boolean gisInitialized = false;
    private PojoDataAccess pojoDataAccess = null;
    
    private SolaPrint solaPrint = null;

    public SolaControlsBundle() {
        super();
        if (!gisInitialized) {
            org.geotools.swing.extended.util.Messaging.getInstance().setMessaging(new Messaging());
            ExtendedFeatureLayer.setExtraSldResources(extraSldResources);
            gisInitialized = true;
        }
    }

    /**
     * Sets up the bundle.
     * @param pojoDataAccess The data access library used to communicate with the server
     * from where the map definitions are retrieved.
     * 
     */
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
                this.addLayerConfig(configMapLayer);
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

    /**
     * It adds a layer in the map using map definition as retrieved by server
     * @param configMapLayer The map layer definition
     * @throws InitializeLayerException
     * @throws SchemaException 
     */
    public void addLayerConfig(ConfigMapLayerTO configMapLayer)
            throws InitializeLayerException, SchemaException {
        if (configMapLayer.getTypeCode().equals("wms")) {
            String wmsServerURL = configMapLayer.getWmsUrl();
            ArrayList<String> wmsLayerNames = new ArrayList<String>();
            String[] layerNameList = configMapLayer.getWmsLayers().split(";");
            java.util.Collections.addAll(wmsLayerNames, layerNameList);
            this.getMap().addLayerWMS(
                    configMapLayer.getId(), configMapLayer.getTitle(), wmsServerURL, wmsLayerNames);
        } else if (configMapLayer.getTypeCode().equals("shape")) {
            this.getMap().addLayerShapefile(
                    configMapLayer.getId(),
                    configMapLayer.getTitle(),
                    configMapLayer.getShapeLocation(),
                    configMapLayer.getStyle());
        } else if (configMapLayer.getTypeCode().equals("pojo")) {
            ExtendedLayer layer = new PojoLayer(configMapLayer.getId(), this.pojoDataAccess);
            this.getMap().addLayer(layer);
        }
    }

    /**
     * Gets the Data access that is used to communicate with the server
     * @return 
     */
    public PojoDataAccess getPojoDataAccess() {
        return pojoDataAccess;
    }

    /**
     * Refreshes the map
     * @param force True = If a layer can be marked to be refreshed always, it will be refreshed
     * even if the extent of the map is not changed. It is used when overridden by sub-classes.
     */
    public void refresh(boolean force) {
        this.getMap().refresh();
    }
    
    /**
     * Sets the application id if the bundle is used within an application
     * @param applicationId 
     */
    public void setApplicationId(String applicationId){
        this.solaPrint.setApplicationId(applicationId);
    }
    
    /**
     * It adds the search panel to the left panel of the bundle
     */
    private void addSearchPanel(){
        SearchPanel panel = new SearchPanel(this.getMap());
        this.addInLeftPanel(Messaging.getInstance().getMessageText(
                GisMessage.LEFT_PANEL_TAB_FIND_TITLE), panel);
    }
}
