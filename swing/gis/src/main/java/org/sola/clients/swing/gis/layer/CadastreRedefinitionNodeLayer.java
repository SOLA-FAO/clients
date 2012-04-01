/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeTargetBean;
import org.sola.common.messaging.GisMessage;

/**
 * Layer that maintains the collection of nodes that are targeted during the cadastre redefinition.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionNodeLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Modified Nodes";
    private static final String LAYER_STYLE_RESOURCE = "node_modified.xml";

    /**
     * Constructor
     * @throws InitializeLayerException 
     */
    public CadastreRedefinitionNodeLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POINT, LAYER_STYLE_RESOURCE, null);
    }
    
    /**
     * Adds a node target bean to the layer. It transforms it to feature
     * 
     * @param nodeBean
     * @return 
     */
    public SimpleFeature addNodeTarget(CadastreObjectNodeTargetBean nodeBean) {
        return this.addNodeTarget(nodeBean.getNodeId(), nodeBean.getGeom());
    }

    /**
     * Adds a node target from a WKB object
     * @param id
     * @param geom
     * @return 
     */
    public SimpleFeature addNodeTarget(String id, byte[] geom) {
        SimpleFeature featureAdded = null;
        try {
            featureAdded = this.addFeature(id, geom, null);
        } catch (ParseException ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_REDEFINITION_ADD_NODE_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_REDEFINITION_ADD_NODE_ERROR);
        }
        return featureAdded;
    }
    
    /**
     * Gets the list of node targets
     * @return 
     */
    public List<CadastreObjectNodeTargetBean> getNodeTargetList(){
        List<CadastreObjectNodeTargetBean> nodeTargetList =
                new ArrayList<CadastreObjectNodeTargetBean>();
        SimpleFeature nodeFeature = null;
        SimpleFeatureIterator iterator = 
                (SimpleFeatureIterator)this.getFeatureCollection().features();
        while(iterator.hasNext()){
            nodeFeature = iterator.next();
            CadastreObjectNodeTargetBean nodeTargetBean =
                    new CadastreObjectNodeTargetBean();
            nodeTargetBean.setNodeId(nodeFeature.getID());
            nodeTargetBean.setGeom(wkbWriter.write((Geometry)nodeFeature.getDefaultGeometry()));
            nodeTargetList.add(nodeTargetBean);
        }
        iterator.close();
        return nodeTargetList;
    }
    
    /**
     * Sets the list of node targets
     * @param targetList 
     */
    public void addNodeTargetList(List<CadastreObjectNodeTargetBean> targetList){
        for(CadastreObjectNodeTargetBean targetBean: targetList){
            this.addNodeTarget(targetBean);
        }
    }
}
