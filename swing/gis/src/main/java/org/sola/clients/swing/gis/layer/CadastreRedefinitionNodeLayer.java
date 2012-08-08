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

import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeTargetBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeTargetListBean;

/**
 * Layer that maintains the collection of nodes that are targeted during the cadastre redefinition.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionNodeLayer extends AbstractSpatialObjectLayer {

    private static final String LAYER_NAME = "modified_nodes";
    private static final String LAYER_STYLE_RESOURCE = "node_modified.xml";

    /**
     * Constructor
     * @throws InitializeLayerException 
     */
    public CadastreRedefinitionNodeLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POINT,
                LAYER_STYLE_RESOURCE, null, CadastreObjectNodeTargetBean.class);
        this.listBean = new CadastreObjectNodeTargetListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
    }

    @Override
    public List<CadastreObjectNodeTargetBean> getBeanList() {
        return super.getBeanList();
    }
    
    /**
     * It adds a target node bean from a node bean
     * 
     * @param nodeBean 
     */    
    public void addNodeTarget(CadastreObjectNodeBean nodeBean){
        CadastreObjectNodeTargetBean bean = new CadastreObjectNodeTargetBean();
        bean.setGeom(nodeBean.getGeom());
        bean.setNodeId(nodeBean.getId());
        this.getBeanList().add(bean);
    }
    
    /**
     * Gets a feature searching by its bean nodeId property
     * @param nodeId
     * @return 
     */
    public SimpleFeature getFeatureByNodeId(String nodeId){
        for(CadastreObjectNodeTargetBean bean:this.getBeanList()){
            if (bean.getNodeId().equals(nodeId)){
                return this.getFeatureCollection().getFeature(bean.getRowId());
            }
        }
        return null;
    }
}
