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

import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetListBean;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetRedefinitionBean;
import org.sola.clients.swing.gis.beans.SpatialBean;

/**
 * Layer that maintains a list of target objects during the cadastre redefinition.
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionObjectLayer
        extends AbstractSpatialObjectLayer implements TargetBoundaryLayer {

    private static final String LAYER_NAME = "modified_cadastre_objects";
    private static final String LAYER_STYLE_RESOURCE = "parcel_modified.xml";

    /**
     * Constructor
     *
     * @throws InitializeLayerException
     */
    public CadastreRedefinitionObjectLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POLYGON,
                LAYER_STYLE_RESOURCE, null, CadastreObjectTargetRedefinitionBean.class);
        this.listBean = new CadastreObjectTargetListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
    }

    @Override
    public List<CadastreObjectTargetRedefinitionBean> getBeanList() {
        return super.getBeanList();
    }

    /**
     * Adds the cadastre objects to the layer
     *
     * @param cadastreObjectList
     */
    public void addCadastreObjects(List<CadastreObjectBean> cadastreObjectList) {
        for (CadastreObjectBean coBean : cadastreObjectList) {
            addCadastreObjectTarget(coBean);
        }
    }

    /**
     * It checks if a bean of a cadastre object is already present.
     *
     * @param id The cadastre object id to check for
     * @return True if it is found
     */
    private boolean cadastreObjectTargetExists(String id) {
        for (CadastreObjectTargetRedefinitionBean bean : this.getBeanList()) {
            if (bean.getCadastreObjectId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It adds a target cadastre object from the cadastre object
     *
     * @param coBean The cadastre object bean
     */
    public void addCadastreObjectTarget(CadastreObjectBean coBean) {
        if (!this.cadastreObjectTargetExists(coBean.getId())) {
            CadastreObjectTargetRedefinitionBean bean =
                    new CadastreObjectTargetRedefinitionBean();
            bean.setCadastreObjectId(coBean.getId());
            bean.setGeomPolygonCurrent(coBean.getGeomPolygon());
            bean.setFeatureGeom(bean.getGeomPolygonCurrentForFeature());
            this.getBeanList().add(bean);
        }

    }

    /**
     * Gets the list of features of target cadastre objects that are involved in a node
     *
     * @param nodeFeature
     * @return
     */
    public List<SimpleFeature> getCadastreObjectFeatures(SimpleFeature nodeFeature) {
        List<SimpleFeature> cadastreObjectFeatureList =
                new ArrayList<SimpleFeature>();
        ReferencedEnvelope filterBbox = new ReferencedEnvelope(nodeFeature.getBounds());
        filterBbox.expandBy(0.01, 0.01);
        FeatureCollection featureCollection = this.getFeaturesInRange(filterBbox, null);
        SimpleFeatureIterator iterator = (SimpleFeatureIterator) featureCollection.features();
        while (iterator.hasNext()) {
            cadastreObjectFeatureList.add(iterator.next());
        }
        iterator.close();
        return cadastreObjectFeatureList;
    }

    /**
     * It overrides the parent class method because if a bean is changing 
     * and the value of geometry attribute is the same with the original geometry,
     * the bean is not needed to be in the list of target cadastre objects. So it is removed.
     * @param bean
     * @param propertyName
     * @param newValue
     * @param oldValue 
     */
    @Override
    protected void beanChanged(
            SpatialBean bean, String propertyName, Object newValue, Object oldValue) {
        super.beanChanged(bean, propertyName, newValue, oldValue);
        if (((CadastreObjectTargetRedefinitionBean) bean).currentAndNewTheSame()) {
            this.getBeanList().remove((CadastreObjectTargetRedefinitionBean) bean);
        }
    }

    @Override
    public SimpleFeature getFeatureByCadastreObjectId(String id) {
        for (CadastreObjectTargetRedefinitionBean bean : this.getBeanList()) {
            if (bean.getCadastreObjectId().equals(id)) {
                return this.getFeatureCollection().getFeature(bean.getRowId());
            }
        }
        return null;
    }

    @Override
    public void notifyEventChanges(String forFeatureOfCadastreObjectId) {
        SimpleFeature feature = this.getFeatureByCadastreObjectId(forFeatureOfCadastreObjectId);
        if (feature != null){
            this.getFeatureCollection().notifyListeners(feature, CollectionEvent.FEATURES_CHANGED);
        }
    }

    @Override
    public List<String> getCadastreObjectTargetIdsFromNodeFeature(SimpleFeature nodeFeature) {
        List<String> ids = new ArrayList<String>();
        ReferencedEnvelope filterBbox = new ReferencedEnvelope(nodeFeature.getBounds());
        filterBbox.expandBy(FILTER_PRECISION, FILTER_PRECISION);
        FeatureCollection featureCollection = this.getFeaturesInRange(filterBbox, null);
        SimpleFeatureIterator iterator = (SimpleFeatureIterator) featureCollection.features();
        while (iterator.hasNext()) {
            CadastreObjectTargetRedefinitionBean bean = this.getBean(iterator.next());
            ids.add(bean.getCadastreObjectId());
        }
        iterator.close();
        return ids;
    }
}
