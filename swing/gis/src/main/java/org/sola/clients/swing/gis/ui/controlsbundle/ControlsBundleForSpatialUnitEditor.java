/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.controlsbundle;

import java.util.ArrayList;
import javax.swing.JComboBox;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.beans.LevelBean;
import org.sola.clients.swing.gis.layer.PojoBaseLayer;
import org.sola.clients.swing.gis.layer.SpatialUnitLayer;
import org.sola.clients.swing.gis.mapaction.SaveSpatialUnit;
import org.sola.clients.swing.gis.mapaction.SaveSpatialUnitGeneric;
import org.sola.clients.swing.gis.tool.SpatialUnitSelect;
import org.sola.clients.swing.gis.ui.control.LevelOptionControl;

/**
 * A control bundle that is used to manage spatial unit objects that are not
 * cadastre objects or spatial unit groups.
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForSpatialUnitEditor extends ControlsBundleForSpatialUnitGenericEditor {

    @Override
    protected JComboBox getOptionControl() {
        LevelOptionControl optionControl = new LevelOptionControl();
        optionControl.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelChanged(evt);
            }
        });
        return optionControl;
    }

    @Override
    protected void addSelectTool() {
        this.getMap().addTool(new SpatialUnitSelect((SpatialUnitLayer) getLayer()), this.getToolbar(), true);
    }

    @Override
    protected final SaveSpatialUnitGeneric getNewSaveAction() {
        return new SaveSpatialUnit(getMap());
    }

    private void levelChanged(java.awt.event.ActionEvent evt) {
        setLevel(getSelectedValue((LevelOptionControl) evt.getSource()));
    }

    private LevelBean getSelectedValue(LevelOptionControl control) {
        return (LevelBean) control.getSelectedItem();
    }

    private void setLevel(LevelBean level) {
        ((SpatialUnitLayer) this.getLayer()).setLevel(level);
        this.getEditTool().setGeometryType(Geometries.getForName(level.getStructureCode()));

        ArrayList<PojoBaseLayer> list = new ArrayList<PojoBaseLayer>();
        if (level.getVisualizationLayers() != null) {
            for (String layerName : level.getVisualizationLayers().split("###")) {
                if (this.getMap().getSolaLayers().containsKey(layerName)) {
                    list.add((PojoBaseLayer) this.getMap().getSolaLayers().get(layerName));
                }
            }
        }
        getEditTool().setExtraTargetSnappingLayers(list);
        getSaveAction().getLayersToRefresh().addAll(list);
    }

    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        setLayer(new SpatialUnitLayer());
        super.addLayers();
    }
}
