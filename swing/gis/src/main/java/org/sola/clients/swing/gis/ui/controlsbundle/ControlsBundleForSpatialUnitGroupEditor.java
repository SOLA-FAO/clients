/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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

import javax.swing.JLabel;
import org.geotools.feature.SchemaException;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.beans.referencedata.HierarchyLevelBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.SpatialUnitGroupLayer;
import org.sola.clients.swing.gis.mapaction.SaveSpatialUnitGroup;
import org.sola.clients.swing.gis.mapaction.SpatialUnitGroupListShow;
import org.sola.clients.swing.gis.tool.SpatialUnitGroupEdit;
import org.sola.clients.swing.gis.tool.SpatialUnitGroupSelect;
import org.sola.clients.swing.gis.ui.control.SpatialUnitGroupOptionControl;

/**
 * A control bundle that is used to manage spatial unit groups.
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForSpatialUnitGroupEditor extends SolaControlsBundle {

    private SpatialUnitGroupLayer layer = null;
    private SaveSpatialUnitGroup saveAction;
    private static java.util.ResourceBundle resource =
            java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/controlsbundle/Bundle");

    /**
     * Constructor.
     */
    public ControlsBundleForSpatialUnitGroupEditor() {
        super();
        this.Setup(PojoDataAccess.getInstance());
    }

    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess);
        JLabel label = new JLabel();
        label.setText(String.format(" %s ", resource.getString(
                "ControlsBundleForSpatialUnitGroupEditor.selectUnitType.text")));
        this.getToolbar().add(label);
        SpatialUnitGroupOptionControl optionControl = new SpatialUnitGroupOptionControl();

        optionControl.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hierarchyLevelChanged(evt);
            }
        });

        this.getToolbar().add(optionControl);
        this.getMap().addTool(new SpatialUnitGroupSelect(layer), this.getToolbar(), true);
        this.getMap().addTool(new SpatialUnitGroupEdit(layer), this.getToolbar(), true);
        this.getMap().addMapAction(new SpatialUnitGroupListShow(
                this.getMap(), this.layer.getHostForm()),
                this.getToolbar(),
                true);
        //Sets the first item in the list as selected
        if (optionControl.getItemCount()>0){
            optionControl.setSelectedIndex(0);
        }
    }

    private void hierarchyLevelChanged(java.awt.event.ActionEvent evt) {
        Integer newValue = getSelectedValue((SpatialUnitGroupOptionControl) evt.getSource());
        setHierarchyLevel(newValue);
    }

    private Integer getSelectedValue(SpatialUnitGroupOptionControl control) {
        return Integer.parseInt(((HierarchyLevelBean) control.getSelectedItem()).getCode());
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.layer.setHierarchyLevel(hierarchyLevel);
    }

    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        super.addLayers();
        layer = new SpatialUnitGroupLayer();
        this.getMap().addLayer(layer);
        this.saveAction.setTargetLayer(layer);
    }

    @Override
    protected void setupToolbar() {
        saveAction = new SaveSpatialUnitGroup(this.getMap());
        this.getMap().addMapAction(saveAction, this.getToolbar(), true);
        super.setupToolbar();
    }
}
