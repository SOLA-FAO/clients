/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.geotools.feature.extended;

import com.vividsolutions.jts.geom.Coordinate;
import org.opengis.feature.simple.SimpleFeature;

/**
 * It represents a vertex used in {@see SolaLayerEditor}.
 * 
 * @author Elton Manoku
 */
public class VertexInformation {
    private Coordinate vertex;
    private SimpleFeature feature;
    private SimpleFeature vertexFeature;

    /**
     * Initializes a VertexInformation.
     * @param vertex The coordinates of the vertex
     * @param feature The feature where this vertex belongs to
     * @param vertexFeature The feature which is used to visualize the vertex
     */
    public VertexInformation(Coordinate vertex, SimpleFeature feature, SimpleFeature vertexFeature){
        this.vertex = vertex;
        this.feature = feature;
        this.vertexFeature = vertexFeature;
    }

    /**
     * Gets coordinates of vertex
     * @return 
     */
    public Coordinate getVertex() {
        return vertex;
    }
    
    /**
     * Gets feature where the vertex belongs to
     * @return 
     */
    public SimpleFeature getFeature() {
        return feature;
    }

    /**
     * Gets the feature that is used to visualize the vertex
     * @return 
     */
    public SimpleFeature getVertexFeature() {
        return vertexFeature;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VertexInformation)){
            return false;
        }
        VertexInformation otherVertex = (VertexInformation)obj;
        return (this.vertexFeature.getID().equals(otherVertex.getVertexFeature().getID()));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.vertexFeature != null ? this.vertexFeature.hashCode() : 0);
        return hash;
    }

}
