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
package org.geotools.map.extended.layer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.geotools.data.ows.Layer;
import org.geotools.data.wms.WebMapServer;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;

/**
 * It represents a collection of layers from a WMS server. In the map control this is considered
 * as one layer.
 * 
 * @author Elton Manoku
 */
public class ExtendedLayerWMS extends ExtendedLayer {

    private WebMapServer serverWMS;
    private List<org.geotools.data.ows.Layer> layersWMS;

    /**
     * 
     * @param name The name of the layer in the map control.
     * @param mapControl The map control
     * @param WMSServerURL The url of the wms server
     * @param layerNames The list of layer names within the wms server
     * @throws InitializeLayerException 
     */
    public ExtendedLayerWMS(String name, String title, Map mapControl,
            String WMSServerURL, List<String> layerNames)
            throws InitializeLayerException {
        this.setLayerName(name);
        this.setTitle(title);
        this.setMapControl(mapControl);
        this.initializeServerWMS(WMSServerURL);
        for (String layerName : layerNames) {
            this.addLayerFromWMS(layerName);
        }
    }

    private void initializeServerWMS(String url) throws InitializeLayerException {
        try {
            URL capabilitiesURL = new URL(url);
            this.serverWMS = new WebMapServer(capabilitiesURL);
            this.layersWMS = this.serverWMS.getCapabilities().getLayerList();
        } catch (ServiceException ex) {
            throw new InitializeLayerException(
                    Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString(), ex);
        } catch (MalformedURLException ex) {
            throw new InitializeLayerException(
                    Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString(), ex);
        } catch (IOException ex) {
            throw new InitializeLayerException(
                    Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString(), ex);
        }
    }

    /**
     * It adds a layer that exists in the wms server into the map control
     * @param name Name of the layer in the wms server
     * @return
     * @throws InitializeLayerException 
     */
    public final boolean addLayerFromWMS(String name) throws InitializeLayerException {
        boolean addedSuccessfully = true;
        if (this.serverWMS == null) {
            throw new InitializeLayerException(
                    Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString(), null);
        }
        Layer foundLayer = null;
        for (Layer layer : this.layersWMS) {
            String layerName = layer.getName();
            if (layerName != null && layerName.equalsIgnoreCase(name)) {
                foundLayer = layer;
                break;
            }
        }
        if (foundLayer == null) {
            throw new InitializeLayerException(
                    Messaging.Ids.WMSLAYER_LAYER_NOT_FOUND_ERROR.toString(), null);
        }
        WMSLayer displayLayer = new WMSLayer(this.serverWMS, foundLayer);
        this.getMapControl().getMapContent().addLayer(displayLayer);
        this.getMapLayers().add(displayLayer);
        return addedSuccessfully;
    }
}
