/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectListBean;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.ui.control.CadastreObjectListPanel;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Layer that has the features of the new cadastre objects during the cadastre change
 *
 * @author Elton Manoku
 */
public class CadastreChangeNewCadastreObjectLayer
        extends AbstractSpatialObjectLayer implements TargetBoundaryLayer {

    private static final String LAYER_FIELD_FIRST_PART = "nameFirstpart";
    private static final String LAYER_FIELD_LAST_PART = "nameLastpart";
    private static final String LAYER_FIELD_OFFICIAL_AREA = "officialArea";
    private static final String LAYER_FIELD_CALCULATED_AREA = "calculatedArea";
    private static final String LAYER_NAME = "new_cadastre_object";
    private static final String LAYER_STYLE_RESOURCE = "parcel_new.xml";
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:String,%s:String,%s:Double,%s:Double",
            LAYER_FIELD_FIRST_PART, LAYER_FIELD_LAST_PART,
            LAYER_FIELD_OFFICIAL_AREA, LAYER_FIELD_CALCULATED_AREA);
    private Integer firstPartGenerator = 0;
    private static final String LAST_PART_FORMAT = "SP %s";
    private String lastPart = "";
    private CadastreObjectListPanel spatialObjectDisplayPanel;

    /**
     * Constructor for the layer.
     *
     * @param applicationNumber The application number of the service that starts the transaction
     * where the layer is used. This number is used in the definition of new parcel number
     * @throws InitializeLayerException
     */
    public CadastreChangeNewCadastreObjectLayer(String applicationNumber)
            throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POLYGON,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION, CadastreObjectBean.class);
        this.lastPart = String.format(LAST_PART_FORMAT, applicationNumber);
        this.listBean = new CadastreObjectListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
        this.spatialObjectDisplayPanel =
                new CadastreObjectListPanel((CadastreObjectListBean) this.listBean);
        initializeFormHosting(
                MessageUtility.getLocalizedMessageText(
                GisMessage.CADASTRE_CHANGE_FORM_NEW_OBJECTS_TITLE), 
                this.spatialObjectDisplayPanel);
    }

    /**
     * Gets the panel where the data about the cadastre objects are displayed
     *
     * @return
     */
    public CadastreObjectListPanel getSpatialObjectDisplayPanel() {
        return spatialObjectDisplayPanel;
    }

    /**
     * Gets the list of new cadastre objects
     *
     * @return
     */
    @Override
    public List<CadastreObjectBean> getBeanList() {
        return (List<CadastreObjectBean>) super.getBeanList();
    }

    @Override
    public <T extends SpatialBean> void setBeanList(List<T> beanList) {
        super.setBeanList(beanList);
        this.firstPartGenerator= null;
    }

    @Override
    protected HashMap<String, Object> getFieldsWithValuesForNewFeatures(Geometry geom) {
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        fieldsWithValues.put(CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_FIRST_PART,
                this.getNameFirstPart());
        fieldsWithValues.put(CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_LAST_PART,
                this.lastPart);
        fieldsWithValues.put(CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_OFFICIAL_AREA,
                Math.round(geom.getArea()));
        fieldsWithValues.put(CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_CALCULATED_AREA,
                Math.round(geom.getArea()));
        return fieldsWithValues;
    }

    @Override
    public SimpleFeature getFeatureByCadastreObjectId(String id) {
        for (CadastreObjectBean bean : this.getBeanList()) {
            if (bean.getId().equals(id)) {
                return this.getFeatureCollection().getFeature(bean.getRowId());
            }
        }
        return null;
    }

    @Override
    public void notifyEventChanges(String forFeatureOfCadastreObjectId) {
        SimpleFeature feature = this.getFeatureByCadastreObjectId(forFeatureOfCadastreObjectId);
        if (feature != null) {
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
            CadastreObjectBean bean = this.getBean(iterator.next());
            ids.add(bean.getId());
        }
        iterator.close();
        return ids;
    }

    /**
     * Gets a new first part for a new cadastre object. If the generator is not initialized it
     * searches first in the existing cadastre objects for the biggest id and starts generating from
     * that number upwards. <br/> If the generator has to be changed, can be overridden.
     *
     * @return A unique number
     */
    protected String getNameFirstPart() {
        if (this.firstPartGenerator == null) {
            this.firstPartGenerator = 0;
            for (CadastreObjectBean bean : this.getBeanList()) {
                try {
                    int tmpValue = Integer.parseInt(bean.getNameFirstpart());
                    if (tmpValue > this.firstPartGenerator) {
                        this.firstPartGenerator = tmpValue;
                    }
                } catch (NumberFormatException ex) {
                    //Ignore exception
                }
            }
        }
        this.firstPartGenerator++;
        return this.firstPartGenerator.toString();
    }
}
