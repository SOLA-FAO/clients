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

import java.io.File;
import java.io.IOException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;

/**
 * This layer adds a shape file based layer in the map control.
 * 
 * @author Elton Manoku
 */
public class ExtendedLayerShapefile extends ExtendedFeatureLayer{

    /**
     * It initiates the shapefile based layer
     * @param name The name of the layer. It must be unique within the layer collection in the map
     * @param pathOfShapefile The shapefile location 
     * @param styleResource the resource name of the style. Has to be found in one of the paths
     * specified in SLD_RESOURCES in {@see ExtendedFeatureLayer}
     * @throws InitializeLayerException
     */
    public ExtendedLayerShapefile(String name, String pathOfShapefile, String styleResource) 
            throws InitializeLayerException{
        try {
            File file = new File(pathOfShapefile);
            if (file == null) {
                throw new InitializeLayerException(
                        Messaging.Ids.SHAPEFILELAYER_FILE_NOT_FOUND_ERROR.toString(), null);
            }
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            this.setFeatureSource(store.getFeatureSource());
            this.initialize(name, store.getFeatureSource(), styleResource);
        } catch (IOException ex) {
            throw new InitializeLayerException(
                    Messaging.Ids.LAYER_NOT_ADDED_ERROR.toString(), ex);
        }        
    }
}
