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

import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/**
 *
 * Layer of the target cadastre objects that is used during the cadastre change.
 * It maintains a list of cadastre objects being targeted for change.
 * 
 * @author Elton Manoku
 */
public class CadastreChangeTargetCadastreObjectLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Target Parcels";
    private static final String LAYER_STYLE_RESOURCE = "parcel_target.xml";
    private List<CadastreObjectTargetBean> cadastreObjectTargetList =
            new ArrayList<CadastreObjectTargetBean>();

    /**
     * Constructor
     * 
     * @throws InitializeLayerException 
     */
    public CadastreChangeTargetCadastreObjectLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POLYGON, LAYER_STYLE_RESOURCE);

        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });
    }

    /**
     * Gets list of cadastre object targets
     * @return 
     */
    public List<CadastreObjectTargetBean> getCadastreObjectTargetList() {
        return cadastreObjectTargetList;
    }

    /**
     * Sets list of cadastre object targets. It is called when the transaction is read from the 
     * server.
     * 
     * @param objectList 
     */
    public void setCadastreObjectTargetList(
            List<CadastreObjectTargetBean> objectList) {

        if (objectList.size() > 0) {
            List<String> ids = new ArrayList<String>();
            for (CadastreObjectTargetBean bean : objectList) {
                ids.add(bean.getCadastreObjectId());
            }
            List<CadastreObjectTO> targetObjectList =
                    this.getDataAccess().getCadastreService().getCadastreObjects(ids);
            try {
                for (CadastreObjectTO targetCOTO : targetObjectList) {
                    this.addFeature(targetCOTO.getId(), targetCOTO.getGeomPolygon(), null);
                }
            } catch (ParseException ex) {
                Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_ADDTARGET_IN_START);
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDTARGET_IN_START, ex);
            }
        }
    }

    /**
     * It handles the changes in the collection of features
     * @param ev 
     */
    private void featureCollectionChanged(CollectionEvent ev) {
        if (ev.getFeatures() == null) {
            return;
        }
        if (ev.getEventType() == CollectionEvent.FEATURES_ADDED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                this.getCadastreObjectTargetList().add(this.newBean(feature));
            }
        } else if (ev.getEventType() == CollectionEvent.FEATURES_REMOVED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                CadastreObjectTargetBean found = this.getBean(feature);
                if (found != null) {
                    this.getCadastreObjectTargetList().remove(found);
                }
            }
        }
    }

    /**
     * Changes an existing bean from its correspondent feature
     * @param targetBean
     * @param feature 
     */
    private void changeBean(CadastreObjectTargetBean targetBean, SimpleFeature feature) {
        targetBean.setCadastreObjectId(feature.getID());
    }

    /**
     * Gets a new bean from a feature
     * @param feature
     * @return 
     */
    private CadastreObjectTargetBean newBean(SimpleFeature feature) {
        CadastreObjectTargetBean bean = new CadastreObjectTargetBean();
        this.changeBean(bean, feature);
        return bean;
    }

    /**
     * Gets the corresponding bean of a feature
     * 
     * @param feature
     * @return 
     */
    private CadastreObjectTargetBean getBean(SimpleFeature feature) {
        CadastreObjectTargetBean bean = new CadastreObjectTargetBean();
        bean.setCadastreObjectId(feature.getID());
        int foundIndex = this.getCadastreObjectTargetList().indexOf(bean);
        if (foundIndex > -1) {
            bean = this.getCadastreObjectTargetList().get(foundIndex);
        } else {
            bean = null;
        }
        return bean;
    }

    /**
     * Gets a reference to the data access
     * @return 
     */
    public PojoDataAccess getDataAccess() {
        return PojoDataAccess.getInstance();
    }
}
