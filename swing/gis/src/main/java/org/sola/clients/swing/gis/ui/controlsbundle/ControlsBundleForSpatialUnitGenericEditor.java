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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.geotools.feature.SchemaException;
import org.geotools.map.extended.layer.ExtendedImageLayer;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.mapaction.extended.RemoveDirectImage;
import org.geotools.swing.tool.extended.AddDirectImageTool;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.AbstractSpatialObjectLayer;
import org.sola.clients.swing.gis.mapaction.AttributeFormShow;
import org.sola.clients.swing.gis.mapaction.SaveSpatialUnit;
import org.sola.clients.swing.gis.mapaction.SaveSpatialUnitGeneric;
import org.sola.clients.swing.gis.mapaction.SpatialUnitResetGeneric;
import org.sola.clients.swing.gis.tool.SpatialUnitGenericEdit;

/**
 * A control bundle that is used to manage spatial unit objects or spatial unit groups.
 *
 * @author Elton Manoku
 */
public abstract class ControlsBundleForSpatialUnitGenericEditor extends SolaControlsBundle {

    private AbstractSpatialObjectLayer layer = null;
    private ExtendedImageLayer imageLayer = null;
    private static final String IMAGE_LAYER_NAME = "temporary_image";
    private SaveSpatialUnitGeneric saveAction;
    private SpatialUnitGenericEdit editTool;
    private static java.util.ResourceBundle resource =
            java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/controlsbundle/Bundle");

    /**
     * Constructor.
     */
    public ControlsBundleForSpatialUnitGenericEditor() {
        super();
        this.Setup(PojoDataAccess.getInstance());
    }

    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess);

        this.getMap().addTool(new AddDirectImageTool(this.imageLayer), this.getToolbar(), true);
        this.getMap().addMapAction(new RemoveDirectImage(this.getMap()), this.getToolbar(), true);

        JLabel label = new JLabel();
        label.setText(String.format(" %s ", resource.getString(
                "ControlsBundleForSpatialUnitGenericEditor.selectUnitType.text")));
        this.getToolbar().add(label);
        JComboBox optionControl = this.getOptionControl();
        this.getToolbar().add(optionControl);
        this.getMap().addMapAction(new SpatialUnitResetGeneric(this),
                this.getToolbar(),
                true);
        this.addSelectTool();
        this.editTool = new SpatialUnitGenericEdit(this.getLayer());
        this.getMap().addTool(this.editTool, this.getToolbar(), true);
        this.getMap().addMapAction(new AttributeFormShow(
                this.getMap(), this.getLayer().getHostForm()),
                this.getToolbar(),
                true);
        //Sets the first item in the list as selected
        if (optionControl.getItemCount() > 0) {
            optionControl.setSelectedIndex(0);
        }
    }
    
    public final AbstractSpatialObjectLayer getLayer(){
        return this.layer;
    }

    /**
     * It sets the editing layer.
     * @param layer 
     */
    protected final void setLayer(AbstractSpatialObjectLayer layer) {
        this.layer = layer;
    }

    /**
     * Gets the editing tool.
     * @return 
     */
    protected final SpatialUnitGenericEdit getEditTool() {
        return editTool;
    }
    
    /**
     * It adds the option control in the toolbar.
     * 
     * @return 
     */
    protected abstract JComboBox getOptionControl();
    
    /**
     * It adds the select tool.
     * 
     */
    protected abstract void addSelectTool();
    
    /**
     * It adds the Save action.
     */
    protected abstract SaveSpatialUnitGeneric getNewSaveAction();

    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        super.addLayers();
        this.imageLayer = new ExtendedImageLayer(IMAGE_LAYER_NAME,
                ((Messaging) Messaging.getInstance()).getLayerTitle(IMAGE_LAYER_NAME));
        this.getMap().addLayer(this.imageLayer);
        this.getMap().addLayer(this.getLayer());
        this.saveAction.setTargetLayer(this.getLayer());

    }

    public SaveSpatialUnitGeneric getSaveAction() {
        return saveAction;
    }

    @Override
    protected void setupToolbar() {
        saveAction =  getNewSaveAction();
        this.getMap().addMapAction(saveAction, this.getToolbar(), true);
        super.setupToolbar();
    }
}
