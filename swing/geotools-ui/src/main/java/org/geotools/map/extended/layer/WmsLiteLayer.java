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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.geotools.data.ows.SimpleHttpClient;
import org.geotools.data.wms.WMS1_0_0;
import org.geotools.data.wms.WMS1_1_0;
import org.geotools.data.wms.WMS1_1_1;
import org.geotools.data.wms.WMS1_3_0;
import org.geotools.data.wms.response.GetMapResponse;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.ows.ServiceException;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;

/**
 * This layer acts as a client to a wms server. It relies in the configuration that the user does.
 * If does not consult the WMS server to check if the layer (s) exist and/or supported formats 
 * exist. Not checking the capability file of the WMS Server makes the layer to work fast.
 * 
 * 
 * @author Elton Manoku
 */
public class WmsLiteLayer extends DirectLayer {

    private ReferencedEnvelope bounds;
    private BufferedImage image;
    private org.geotools.data.wms.request.GetMapRequest getMapRequest;
    private Integer srid;
    private String format = "image/png";

    /**
     * Constructor of the layer.
     * @param wmsServerUrl the url of the server without the query. If it is added with the query,
     * the query after ? will be removed.
     * @param layerNames The list of layernames that will be asked for rendering. The layers has
     * to be checked with other tools if they exist in the server.
     * @param version The WMS Version that will be used.
     * @throws InitializeLayerException 
     */
    public WmsLiteLayer(String wmsServerUrl, List<String> layerNames, String version)
            throws InitializeLayerException {
        try {
            //Based in the version, the appropriate GetMapRequest is initialized.
            if (version.equals("1.0.0")) {
                getMapRequest = new WMS1_0_0.GetMapRequest(new URL(wmsServerUrl));
            } else if (version.equals("1.1.0")) {
                getMapRequest = new WMS1_1_0.GetMapRequest(new URL(wmsServerUrl));
            } else if (version.equals("1.1.1")) {
                getMapRequest = new WMS1_1_1.GetMapRequest(new URL(wmsServerUrl));
            } else if (version.equals("1.3.0")) {
                getMapRequest = new WMS1_3_0.GetMapRequest(new URL(wmsServerUrl));
            } else {
                throw new InitializeLayerException(
                        Messaging.Ids.WMSLAYER_VERSION_MISSING_ERROR.toString(), null);
            }
            for (String layerName : layerNames) {
                getMapRequest.addLayer(layerName, "");
            }
        } catch (MalformedURLException ex) {
            throw new InitializeLayerException(
                    Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString(), ex);
        }
    }

    /**
     * Sets the srid of the SRS that will be used for the requests.
     * @param srid 
     */
    public void setSrid(Integer srid) {
        this.srid = srid;
    }

    /**
     * Sets the format of the output. Potential formats are image/jpeg or image/png
     * @param format 
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * It draws the layer in the map. If the bounds has not changed it reuses the latest image
     * if that is present.
     * @param gd
     * @param mc
     * @param mv 
     */
    @Override
    public void draw(Graphics2D gd, MapContent mc, MapViewport mv) {
        if (this.bounds != null && this.bounds.equals(mv.getBounds()) && this.image != null) {
            gd.drawImage(this.image, 0, 0, null);
        } else {
            this.bounds = mv.getBounds();
            SimpleHttpClient httpClient = new SimpleHttpClient();
            try {
                getMapRequest.setBBox(this.bounds);
                getMapRequest.setDimensions(mv.getScreenArea().getSize());
                getMapRequest.setFormat(this.format);
                getMapRequest.setSRS(String.format("EPSG:%s", this.srid));
                //The transparency will not work if the format does not support transparency
                getMapRequest.setTransparent(true);
                GetMapResponse response =
                        new GetMapResponse(httpClient.get(getMapRequest.getFinalURL()));
                this.image = ImageIO.read(response.getInputStream());
                response.getInputStream().close();
                response.dispose();
                gd.drawImage(this.image, 0, 0, null);
            } catch (IOException ex) {
                this.LOGGER.log(Level.SEVERE,
                        Messaging.Ids.WMSLAYER_LAYER_RENDER_ERROR.toString(), ex);
            } catch (ServiceException ex) {
                this.LOGGER.log(Level.SEVERE,
                        Messaging.Ids.WMSLAYER_LAYER_RENDER_ERROR.toString(), ex);
            }
        }
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return this.bounds;
    }
}
