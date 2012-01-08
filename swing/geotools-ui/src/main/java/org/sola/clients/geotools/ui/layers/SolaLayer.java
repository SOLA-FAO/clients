/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.geotools.ui.layers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.geotools.map.Layer;
import org.sola.clients.geotools.ui.Map;
import org.sola.clients.geotools.ui.TocSymbol;

/**
 * This is the layer as understood by SOLA. A layer can have multiple geotool layers.
 * @author manoku
 */
public class SolaLayer {
    
    private String layerName = "";
    
    private ArrayList<Layer> mapLayers = new ArrayList<Layer>();
    
    private Map mapControl = null;
    
    private boolean showInToc = true;
   
    public SolaLayer() throws Exception{    
    }

    public int getSrid() {
        int srid = -1;
        if (this.mapControl != null){
            srid = this.mapControl.getSrid();
        }
        return srid;
    }

    /**
     * @return the layerName
     */
    public String getLayerName() {
        return layerName;
    }

    /**
     * @param layerName the layerName to set
     */
    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    /**
     * @return the mapLayers
     */
    public ArrayList<Layer> getMapLayers() {
        return mapLayers;
    }

    /**
     * @return the mapControl
     */
    public Map getMapControl() {
        return mapControl;
    }

    /**
     * @param mapControl the mapControl to set
     */
    public void setMapControl(Map mapControl) {
        this.mapControl = mapControl;
    }

    /**
     * @return the showInToc
     */
    public boolean isShowInToc() {
        return showInToc;
    }

    /**
     * @param showInToc the showInToc to set
     */
    public void setShowInToc(boolean showInToc) {
        this.showInToc = showInToc;
    }

    /**
     * It sets the  visibility of the layer. 
     * If the map must be refreshed, then it will be refreshed.
     * @param visible 
     */
    public void setVisible(boolean visible){
        boolean mapMustBeRefreshed = false;
        for(Layer mapLayer: this.mapLayers){
            if (mapLayer.isVisible()!= visible){
                mapMustBeRefreshed = true;
            }
            mapLayer.setVisible(visible);
        }
        if (mapMustBeRefreshed){
            this.mapControl.refresh();
        }
    }
    
    /**
     * It gets the visibility of the layer.
     * @return 
     */
    public boolean isVisible(){
        for(Layer mapLayer: this.mapLayers){
            return mapLayer.isVisible();
        }
        return false;
    }
    
    /**
     * Has to be overridden by subclasses
     * @return 
     */
    public List<TocSymbol> getLegend(){
        return null;
    }
}
