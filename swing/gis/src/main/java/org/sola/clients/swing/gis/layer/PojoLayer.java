/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.data.PojoFeatureSource;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.webservices.spatial.ConfigMapLayerTO;

/**
 * The layers of this type are used to draw the features from the server. These layers are mainly
 * meant for visualization purposes only.
 *
 * @author Elton Manoku
 */
public class PojoLayer extends ExtendedFeatureLayer {

    public static final String CONFIG_PENDING_PARCELS_LAYER_NAME = "pending-parcels";
    private PojoDataAccess dataAccess;
    private boolean forceRefresh = false;

    /**
     * Constructor. Sets the Pojo Layer to visible by default.
     *
     * @param name layer name
     * @param dataAccess the data access that is used to get the features from the server
     * @throws InitializeLayerException
     * @throws SchemaException
     */
    public PojoLayer(
            String name,
            PojoDataAccess dataAccess) throws InitializeLayerException, SchemaException {
        this(name, dataAccess, true);
    }

    /**
     * Constructor.
     *
     * @param name layer name
     * @param dataAccess the data access that is used to get the features from the server
     * @param name indicates if the layer should be visible by default
     * @throws InitializeLayerException
     * @throws SchemaException
     */
    public PojoLayer(
            String name,
            PojoDataAccess dataAccess,
            boolean visible) throws InitializeLayerException, SchemaException {
        this.dataAccess = dataAccess;
        this.setLayerName(name);
        this.setTitle(this.getConfig().getTitle());
        String styleResource = this.getConfig().getStyle();
        SimpleFeatureSource featureSource = new PojoFeatureSource(this.dataAccess, this);
        this.initialize(name, featureSource, styleResource);
        this.setVisible(visible);
    }

    /**
     * Gets if the layer must be refreshed even the extent of the map is not changed
     *
     * @return
     */
    public boolean isForceRefresh() {
        return forceRefresh;
    }

    /**
     * Sets if the layer must be refreshed even the extent of the map is not changed
     *
     * @param forceRefresh
     */
    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    /**
     * Gets the configuration of the layer
     *
     * @return
     */
    public final ConfigMapLayerTO getConfig() {
        return this.dataAccess.getMapLayerInfoList().get(this.getLayerName());
    }
}
