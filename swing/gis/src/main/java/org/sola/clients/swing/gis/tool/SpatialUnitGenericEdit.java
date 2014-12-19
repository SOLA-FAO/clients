/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.gis.tool;

import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.Map;
import org.geotools.swing.tool.extended.ExtendedEditGeometryTool;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.layer.AbstractSpatialObjectLayer;
import org.sola.clients.swing.gis.layer.PojoBaseLayer;

/**
 * Tool that is used for adding or editing a spatial unit group.
 *
 * @author Elton Manoku
 */
public class SpatialUnitGenericEdit extends ExtendedEditGeometryTool {

    private String toolName = "SpatialUnitGenericEdit";
    private List<PojoBaseLayer> extraTargetSnappingLayers = new ArrayList<PojoBaseLayer>();

    public SpatialUnitGenericEdit(AbstractSpatialObjectLayer layer) {
        this.setToolName(toolName);
        this.setToolTip(((Messaging)Messaging.getInstance()).getMapToolString(
                "SpatialUnitGenericEdit.tooltip"));
        this.layer = layer;
    }

    /**
     * It sets the geometry type that is used for editing. Call this after the tool has been initialized
     * to have effect also the change of the icon.
     * @param geometryType 
     */
    @Override
    public void setGeometryType(Geometries geometryType) {
        super.setGeometryType(geometryType);
        String iconResource = String.format("resources/edit-%s.png", geometryType.toString().toLowerCase());
        this.setIconImage(iconResource);
        if (this.getActionContainer() != null){
        this.getActionContainer().setIcon(this.getClass(), iconResource);                    
        }
    }

    /**
     * It is overridden to define the target snapped layers during editing.
     * @param mapControl 
     */
    @Override
    public void setMapControl(Map mapControl) {
        super.setMapControl(mapControl);
        this.getTargetSnappingLayers().add(layer);
        this.getTargetSnappingLayers().addAll(this.extraTargetSnappingLayers);
    }
    
    /**
     * It sets the list of layers that will be used as snapping target.
     * @param layerNames 
     */
    public void setExtraTargetSnappingLayers(List<PojoBaseLayer> layers){
        this.extraTargetSnappingLayers.addAll(layers);
    }
}
