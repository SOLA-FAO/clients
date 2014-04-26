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
package org.sola.clients.swing.gis.ui.controlsbundle;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.beans.referencedata.HierarchyLevelBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.PojoBaseLayer;
import org.sola.clients.swing.gis.layer.SpatialUnitGroupLayer;
import org.sola.clients.swing.gis.mapaction.SaveSpatialUnitGeneric;
import org.sola.clients.swing.gis.mapaction.SaveSpatialUnitGroup;
import org.sola.clients.swing.gis.tool.SpatialUnitGroupSelect;
import org.sola.clients.swing.gis.ui.control.SpatialUnitGroupOptionControl;

/**
 * A control bundle that is used to manage spatial unit groups.
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForSpatialUnitGroupEditor extends ControlsBundleForSpatialUnitGenericEditor {

    private final static String LAYER_AFFECTED_PREFIX = "su_";

    @Override
    protected JComboBox getOptionControl() {
        SpatialUnitGroupOptionControl optionControl = new SpatialUnitGroupOptionControl();
        optionControl.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hierarchyLevelChanged(evt);
            }
        });        
        return optionControl;
    }

    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess); 
        getEditTool().setGeometryType(Geometries.POLYGON);

        ArrayList<PojoBaseLayer> list = new ArrayList<PojoBaseLayer>();
        for(String layerName:this.getMap().getSolaLayers().keySet()){
            if (layerName.startsWith(LAYER_AFFECTED_PREFIX)){
                list.add( (PojoBaseLayer)
                        this.getMap().getSolaLayers().get(layerName));
            }
        }
        
        getEditTool().setExtraTargetSnappingLayers(list);
        getSaveAction().getLayersToRefresh().addAll(list);
    }
    
    @Override
    protected void addSelectTool() {
        this.getMap().addTool(new SpatialUnitGroupSelect(
                (SpatialUnitGroupLayer)getLayer()), this.getToolbar(), true);        
    }

    @Override
    protected SaveSpatialUnitGeneric getNewSaveAction() {
        return new SaveSpatialUnitGroup(getMap());
    }
    
    private void hierarchyLevelChanged(java.awt.event.ActionEvent evt) {
        Integer newValue = getSelectedValue((SpatialUnitGroupOptionControl) evt.getSource());
        setHierarchyLevel(newValue);
    }

    private Integer getSelectedValue(SpatialUnitGroupOptionControl control) {
        return Integer.parseInt(((HierarchyLevelBean) control.getSelectedItem()).getCode());
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        ((SpatialUnitGroupLayer)this.getLayer()).setHierarchyLevel(hierarchyLevel);
    }

    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        setLayer(new SpatialUnitGroupLayer());
        super.addLayers();
    }
}
