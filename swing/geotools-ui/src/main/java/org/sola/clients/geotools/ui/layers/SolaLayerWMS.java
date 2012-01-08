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
package org.sola.clients.geotools.ui.layers;

import java.net.URL;
import java.util.List;
import org.geotools.data.ows.Layer;
import org.geotools.data.wms.WebMapServer;
import org.geotools.map.WMSLayer;
import org.sola.clients.geotools.ui.Map;
import org.sola.clients.geotools.util.Messaging;

/**
 *
 * @author manoku
 */
public class SolaLayerWMS extends SolaLayer {

    private WebMapServer serverWMS;
    private List<org.geotools.data.ows.Layer> layersWMS;
    //private ControlsBundle mapControl;

    //private ArrayList<MapLayer> mapLayers = new ArrayList<MapLayer>();
    /**
     * @return the mapLayers
     */
    //public ArrayList<MapLayer> getMapLayers() {
   //     return mapLayers;
  //  }

    public SolaLayerWMS(String name, Map mapControl, 
            String WMSServerURL, List<String> layerNames)
            throws Exception {
        this.setLayerName(name);
        this.setMapControl(mapControl);
        this.initializeServerWMS(WMSServerURL);
        for (String layerName : layerNames) {
            this.addLayerFromWMS(layerName);
        }
    }

    private void initializeServerWMS(String url) throws Exception {
        try {
            URL capabilitiesURL = new URL(url);
            this.serverWMS = new WebMapServer(capabilitiesURL);
            this.layersWMS = this.serverWMS.getCapabilities().getLayerList();
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString(), ex);
        }
    }

    /**
     * It adds a layer that exists in the wms server into the map control
     * @param name
     * @return
     * @throws Exception 
     */
    public final boolean addLayerFromWMS(String name) throws Exception {
        boolean addedSuccessfully = true;
        if (this.serverWMS == null) {
            throw new Exception(
                    Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString());
        }
        try {
            Layer foundLayer = null;
            for (Layer layer : this.layersWMS) {
                String layerName = layer.getName();
                if (layerName != null && layerName.equalsIgnoreCase(name)) {
                    foundLayer = layer;
                    break;
                }
            }
            if (foundLayer == null) {
                throw new Exception(
                        Messaging.Ids.WMSLAYER_LAYER_NOT_FOUND_ERROR.toString());
            }
            WMSLayer displayLayer = new WMSLayer(this.serverWMS, foundLayer);
            this.getMapControl().getMapContext().addLayer(displayLayer);
            this.getMapLayers().add(displayLayer);
        } catch (Exception ex) {
            throw ex;
        }
        return addedSuccessfully;
    }
}
