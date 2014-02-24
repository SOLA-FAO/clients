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
package org.geotools.map.extended.layer;

import java.util.ArrayList;
import java.util.List;
import org.geotools.map.Layer;
import org.geotools.swing.extended.Map;
import org.geotools.swing.control.extended.TocSymbol;

/**
 * This is the layer as understood by the extended map control. 
 * A layer can have multiple geotools map layers.
 * 
 * @author Elton Manoku
 */
public class ExtendedLayer {
    
    private String layerName = "";
    
    private String title = null;
    
    private ArrayList<Layer> mapLayers = new ArrayList<Layer>();
    
    private Map mapControl = null;
    
    private boolean showInToc = true;
   
    public ExtendedLayer(){    
    }

    /**
     * Gets the srid. It uses the srid of the map control where the layer is added.
     * @return 
     */
    public Integer getSrid() {
        Integer srid = null;
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
     * Gets title
     * @return 
     */
    public String getTitle() {
        if (title == null){
            return layerName;
        }
        return title;
    }

    /**
     * Sets title
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The list of geotools map layers that are used from this layer.
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
     * If the visibility changed, then the map will be refreshed.
     * @param visible 
     */
    public void setVisible(boolean visible){
        for(Layer mapLayer: this.mapLayers){
            if (mapLayer.isVisible()!= visible){
                mapLayer.setVisible(visible);
            }
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
     * It returns a set of TocSymbol that represent the legend of a layer.
     * For each subtype of this class this functionality has to be implemented.
     * If not, it means that the subtype does not have a legend.
     * @return 
     */
    public List<TocSymbol> getLegend(){
        return null;
    }
}
