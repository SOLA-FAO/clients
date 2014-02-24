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
package org.sola.clients.swing.gis.layer;

import java.util.List;
import org.opengis.feature.simple.SimpleFeature;

/**
 * The interface has to be implemented by layers that will be used in the functionality of
 * irregular boundary change.
 * 
 * @author Elton Manoku
 */
public interface TargetBoundaryLayer {
    
    /**
     * This is the distance that a point must have from a boundary to be considered as being
     * on the boundary.
     */
    static double FILTER_PRECISION = 0.01;
    
    /**
     * Gets the feature that corresponds to the cadastre object.
     * @param id The id of the cadastre object
     * @return The feature or null if nothing is found
     */
    SimpleFeature getFeatureByCadastreObjectId(String id);
    
    /**
     * It notifies the change events for the feature corresponding to the cadastre object
     * 
     * @param forFeatureOfCadastreObjectId The id of the cadastre object
     */
    void notifyEventChanges(String forFeatureOfCadastreObjectId);
    
    /**
     * Gets a list of ids for those cadastre objects where a node is involved
     * 
     * @param nodeFeature
     * @return 
     */
    List<String> getCadastreObjectTargetIdsFromNodeFeature(SimpleFeature nodeFeature);
    
}
