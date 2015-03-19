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

import java.util.ArrayList;
import java.util.List;
import org.geotools.swing.extended.Map;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.PojoBaseLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.transferobjects.cadastre.SpatialUnitGroupTO;

/**
 * Map action that commits the spatial unit group changes.
 *
 * @author Elton Manoku
 */
public class SaveSpatialUnitGroup extends SaveSpatialUnitGeneric {

    public final static String MAPACTION_NAME = "save-spatial-unit-group";

    /**
     * Constructor of the map action that will initialize the saving process of the transaction.
     * 
     * @param transactionControlsBundle The controls bundle that encapsulates all map related
     * controls used during the transaction.
     */
    public SaveSpatialUnitGroup(Map map) {
        super(map, MAPACTION_NAME);
    }

    /**
     * After it saves the transaction, if there is no critical violation, it reads it from database
     * and refreshes the gui.
     *
     */
    @Override
    public void onClick() {
        List<SpatialUnitGroupTO> toList = new ArrayList<SpatialUnitGroupTO>();
                
        TypeConverters.BeanListToTransferObjectList(
                getTargetLayer().getBeanListForTransaction(),
                toList, SpatialUnitGroupTO.class);

        PojoDataAccess.getInstance().getCadastreService().saveSpatialUnitGroups(toList);
        
        Messaging.getInstance().show(GisMessage.SPATIAL_UNIT_GENERIC_SAVED_SUCCESS);
        
        refreshAffectedLayers();
        
        getTargetLayer().setBeanList(new ArrayList<SpatialUnitGroupBean>());
    }
}
