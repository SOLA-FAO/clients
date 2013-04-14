/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.webservices.search.ConfigMapLayerTO;

/**
 * Base class that is used from the pojo type layers.
 * 
 * @author Elton Manoku
 */
public abstract class PojoBaseLayer extends ExtendedFeatureLayer {

    public static final String CONFIG_PENDING_PARCELS_LAYER_NAME = "pending-parcels";
    private PojoDataAccess dataAccess;
    private boolean forceRefresh = false;

    public PojoDataAccess getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(PojoDataAccess dataAccess) {
        this.dataAccess = dataAccess;
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
