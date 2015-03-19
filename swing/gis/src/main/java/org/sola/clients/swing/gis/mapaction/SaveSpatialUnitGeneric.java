/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.gis.mapaction;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.geotools.swing.extended.Map;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.swing.common.DefaultExceptionHandler;
import org.sola.clients.swing.gis.layer.AbstractSpatialObjectLayer;
import org.sola.clients.swing.gis.layer.PojoBaseLayer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Map action that commits the spatial unit group changes.
 *
 * @author Elton Manoku
 */
public abstract class SaveSpatialUnitGeneric extends ExtendedAction {

    private final static String ICON_RESOURCE = "resources/save.png";
    private AbstractSpatialObjectLayer targetLayer;
    private List<PojoBaseLayer> layersToRefresh = new ArrayList<PojoBaseLayer>();

    /**
     * Constructor of the map action that will initialize the saving process of the transaction.
     * 
     * @param transactionControlsBundle The controls bundle that encapsulates all map related
     * controls used during the transaction.
     */
    public SaveSpatialUnitGeneric(Map map, String actionName) {
        super(map, actionName,
                MessageUtility.getLocalizedMessage(
                GisMessage.CADASTRE_CHANGE_TRANSACTION_SAVE).getMessage(), ICON_RESOURCE);
        this.putValue(Action.NAME,
                MessageUtility.getLocalizedMessage(ClientMessage.GENERAL_LABELS_SAVE).getMessage());
    }
     /**
     * Calls {@link AbstractBindingBean#saveStateHash()} method to make a hash
     * of object's state
     */
    public static void saveBeanState(AbstractBindingBean bean) {
        try {
            bean.saveStateHash();
        } catch (IOException ex) {
            DefaultExceptionHandler.handleException(ex);
        } catch (NoSuchAlgorithmException ex) {
            DefaultExceptionHandler.handleException(ex);
        }
    }

    public AbstractSpatialObjectLayer getTargetLayer() {
        return targetLayer;
    }

    /**
     * Sets the target layer for the save operation
     * @param targetLayer 
     */
    public void setTargetLayer(AbstractSpatialObjectLayer targetLayer) {
        this.targetLayer = targetLayer;
    }
    
    /**
     * Get layers to refresh after the Save is done successfully.
     * 
     * @return 
     */
    public final List<PojoBaseLayer> getLayersToRefresh(){
        return layersToRefresh;
    }
    
    protected final void refreshAffectedLayers(){        
        for(PojoBaseLayer layer: layersToRefresh){
            layer.setForceRefresh(true);
        }
    }
    
    /**
     * After it saves the transaction, if there is no critical violation, it reads it from database
     * and refreshes the gui.
     *
     */
    @Override
    public void onClick() {
        // This has to be overriden for each implementation
    }
    
}
