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

import java.util.ArrayList;
import java.util.List;
import org.geotools.feature.SchemaException;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.PojoForPublicDisplayLayer;
import org.sola.clients.swing.gis.ui.control.PublicDisplayPrintPanel;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.search.ConfigMapLayerTO;

/**
 *
 * A bundle that is used to print public display maps. It contains the extra map
 * action to initialize the printing of public display maps.
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForPublicDisplay extends SolaControlsBundle {

    private PublicDisplayPrintPanel printPanel = null;
    private List<PojoForPublicDisplayLayer> publicDisplayLayers =
            new ArrayList<PojoForPublicDisplayLayer>();

    /**
     * Creates the controls bundle viewer component.
     *
     */
    public ControlsBundleForPublicDisplay() {
        super();
        this.Setup(PojoDataAccess.getInstance());
        this.addPrintPanel();

    }

    @Override
    public void setApplicationId(String applicationId) {
        super.setApplicationId(applicationId);
        if (this.printPanel != null) {
            this.printPanel.setApplicationId(applicationId);
        }
    }

    /**
     * It adds additional layers of type pojo_public_display.
     * Additionally it turns layers on/off based in their configuration
     * if they can be used for the public display map.
     *
     * @throws InitializeLayerException
     * @throws SchemaException
     */
    @Override
    protected void addLayers() throws InitializeLayerException, SchemaException {
        super.addLayers();
        for (ConfigMapLayerTO configMapLayer :
                this.getPojoDataAccess().getMapDefinition().getLayers()) {
            if (configMapLayer.getTypeCode().equals("pojo_public_display")) {
                PojoForPublicDisplayLayer layer = new PojoForPublicDisplayLayer(
                        configMapLayer.getId(), getPojoDataAccess(),
                        configMapLayer.isVisible());
                publicDisplayLayers.add(layer);
                this.getMap().addLayer(layer);
            }else{
                String layerName = configMapLayer.getId();
                ExtendedLayer layer = this.getMap().getSolaLayers().get(layerName);
                if (layer != null){
                    if (!(layer.isVisible() && configMapLayer.isUseInPublicDisplay())){
                        this.getMap().getToc().changeNodeSwitch(layerName);                        
                    }
                }
            }
        }
    }
    
    /**
     * Gets the list of layers used for public display.
     * 
     * @return 
     */
    public final List<PojoForPublicDisplayLayer> getPublicDisplayLayers(){
        return publicDisplayLayers;
    }

    /**
     * It adds the panel where the public display map printing process can be managed
     * and started.
     */
    private void addPrintPanel() {
        this.printPanel = new PublicDisplayPrintPanel(this);
        this.addInLeftPanel(Messaging.getInstance().getMessageText(
                GisMessage.LEFT_PANEL_TAB_PUBLIC_DISPLAY_MAP_TITLE), printPanel);
    }
}
