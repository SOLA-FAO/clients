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
import com.vividsolutions.jts.io.ParseException;
import java.awt.Component;
import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.text.html.parser.Entity;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerEditor;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.tool.extended.ui.UiUtil;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.AbstractListSpatialBean;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Abstract Layer that maintains a list of beans, which is synchronized with the feature collection.
 * For each feature there is a bean and viceversa. Not all attributes in the bean need to be in the
 * feature and viceversa. <br/> Optionally, it offers the interface for a host form to show
 * information about the beans. Using the form the information about the beans can change.
 *
 * @author Elton Manoku
 */
public abstract class AbstractSpatialObjectLayer extends ExtendedLayerEditor {

    private Component hostForm = null;
    private Class beanClass = null;
    private PropertyChangeListener beanPropertyChangeListener;
    private List<SpatialBean> removedItems = new ArrayList<SpatialBean>();
    protected AbstractListSpatialBean listBean;

    /**
     * Constructor
     *
     * @param layerName The layer name
     * @param geometryType The geometry type
     * @param styleResource The style
     * @param extraFieldsFormat Extra field information formated according to DataUtility of
     * geotools.
     * @param beanClass A class which will be used for initialization of beans.
     *
     * @throws InitializeLayerException
     */
    public AbstractSpatialObjectLayer(
            String layerName, Geometries geometryType,
            String styleResource, String extraFieldsFormat, Class beanClass)
            throws InitializeLayerException {
        super(layerName, geometryType, styleResource, extraFieldsFormat);
        this.setTitle(((Messaging) Messaging.getInstance()).getLayerTitle(layerName));
        this.beanClass = beanClass;
        this.initializeFeatureCollectionEvents();
    }

    /**
     * It initializes feature related events
     */
    private void initializeFeatureCollectionEvents() {
        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });
    }

    /**
     * It initializes the events on the list of the beans and also a change event handler for every
     * bean. It is called after the listBean is initialized which happens in the constructor of the
     * inheriting classes.
     *
     */
    protected void initializeListBeanEvents() {
        ObservableListListener listListener = new ObservableListListener() {

            @Override
            public void listElementsAdded(ObservableList ol, int i, int i1) {
                newBeansAdded(ol, i, i1);
            }

            @Override
            public void listElementsRemoved(ObservableList ol, int i, List list) {
                removeFeatures(list);
            }

            @Override
            public void listElementReplaced(ObservableList ol, int i, Object o) {
            }

            @Override
            public void listElementPropertyChanged(ObservableList ol, int i) {
            }
        };

        ((SolaObservableList) this.getBeanList()).addObservableListListener(listListener);

        this.beanPropertyChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                beanChanged((SpatialBean) pce.getSource(), pce.getPropertyName(),
                        pce.getNewValue(), pce.getOldValue());
            }
        };
    }

    /**
     * It is called after the bean has been changed. If the property of the bean is also in the list
     * of attributes in the feature, the corresponding attribute in the feature is changed as well.
     * If an attribute of the feature is changed, the map is refreshed.
     *
     * @param bean
     * @param propertyName
     * @param newValue
     * @param oldValue
     */
    protected void beanChanged(SpatialBean bean, String propertyName,
            Object newValue, Object oldValue) {
        if (!Arrays.asList(this.getAttributeNames()).contains(propertyName)) {
            return;
        }
        SimpleFeature feature = this.getFeatureCollection().getFeature(bean.getRowId());
        if (feature == null) {
            return;
        }
        if (feature.getAttribute(propertyName).equals(newValue)) {
            return;
        }
        feature.setAttribute(propertyName, newValue);
        this.getMapControl().refresh();
    }

    /**
     * It is called when beans are removed. It removes the corresponding features from the layer. If
     * the beans are not new, they are moved in the removedItems list. They are moved there, because
     * when the full list of affected beans is created it must include also beans that are removed
     * but marked for deletion.
     *
     * @param beanList
     */
    private void removeFeatures(List beanList) {
        boolean featuresAreRemoved = false;
        for (SpatialBean bean : (List<SpatialBean>) beanList) {
            if (this.getFeatureCollection().getFeature(bean.getRowId()) != null) {
                //Check if the feature is found in the feature collection. It can be that the feature
                // has been removed before the bean has been removed.
                //Remove the feature
                SimpleFeature removedFeature = this.removeFeature(bean.getRowId(), false);
                if (removedFeature != null) {
                    //If feature is removed it means it returns the removed feature. Also
                    // map control needs to be refreshed.
                    featuresAreRemoved = true;
                    if (!bean.isNew()) {
                        removedItems.add(bean);
                    }
                } else {
                    //If feature is not removed it means it should be added back again in the 
                    //bean list
                    getBeanList().add(bean);
                }
            }
        }
        if (featuresAreRemoved) {
            this.getMapControl().refresh();
        }
    }

    /**
     * It is the event handler method called when new beans are added
     *
     * @param fromList The list where the beans are added
     * @param startIndex The index where the new beans start
     * @param total The total of beans added
     */
    protected void newBeansAdded(ObservableList fromList, int startIndex, int total) {
        int endIndex = startIndex + total;
        boolean featureIsAdded = false;
        for (int elementIndex = startIndex; elementIndex < endIndex; elementIndex++) {
            SpatialBean bean = (SpatialBean) fromList.get(elementIndex);
            bean.addPropertyChangeListener(this.beanPropertyChangeListener);
            if (this.getFeatureCollection().getFeature(bean.getRowId()) != null) {
                continue;
            }
            try {
                this.addFeatureFromBean(bean);
                featureIsAdded = true;
            } catch (ParseException ex) {
                Messaging.getInstance().show(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGNEWCADASTREOBJECT_IN_START);
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGNEWCADASTREOBJECT_IN_START, ex);
            }
        }
        if (featureIsAdded) {
            this.getMapControl().refresh();
        }
    }

    /**
     * It adds a feature from the bean.
     *
     * @param bean
     * @return
     * @throws ParseException
     */
    protected SimpleFeature addFeatureFromBean(SpatialBean bean) throws ParseException {
        return this.addFeature(bean.getRowId(), bean.getFeatureGeom(),
                bean.getValues(this.getAttributeNames()), false);
    }

    /**
     * It initializes event handlers and form hosting. This is also called from the constructor of
     * the subclass. It is optional.
     */
    protected void initializeFormHosting(String title, JPanel hostPanel) {
        this.setHostForm(UiUtil.getInstance().getDialog(title, hostPanel));
    }

    /**
     * Gets the form that is responsible showing/editing the attributes in the beans
     *
     * @return
     */
    public Component getHostForm() {
        return this.hostForm;
    }

    /**
     * Sets the hosting component that is used to show the editor/table to change the attributes.
     *
     * @param hostForm
     */
    public void setHostForm(Component hostForm) {
        this.hostForm = hostForm;
    }

    /**
     * Gets the current list of Spatial Beans
     *
     * @return
     */
    public List getBeanList() {
        return listBean.getBeanList();
    }

    /**
     * Gets the list of current Spatial Beans combined with the list of Beans that need to be
     * removed. This list is needed when the beans are supposed to be sent to the server for
     * processing.
     *
     * @return
     */
    public final List getBeanListForTransaction() {
        List finalList = new ArrayList(getBeanList());
        for (SpatialBean bean : removedItems) {
            bean.setEntityAction(EntityAction.DELETE);
            finalList.add(bean);
        }
        return finalList;
    }

    /**
     * Sets the list of new cadastre objects. This is used if the transaction is read from the
     * server.
     *
     * @param beanList
     */
    public <T extends SpatialBean> void setBeanList(List<T> beanList) {
        this.listBean.getBeanList().clear();
        this.removedItems.clear();
        this.listBean.getBeanList().addAll((Collection) beanList);
    }

    /**
     * It adds a feature. If fid is missing it is generated from a GUID generator.
     *
     * @param fid
     * @param geom
     * @param fieldsWithValues
     * @return
     */
    @Override
    public SimpleFeature addFeature(
            String fid,
            Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues,
            boolean refreshMap) {
        if (fid == null) {
            fid = java.util.UUID.randomUUID().toString();
        }
        if (fieldsWithValues == null) {
            fieldsWithValues = this.getFieldsWithValuesForNewFeatures(geom);
        }
        SimpleFeature addedFeature = super.addFeature(fid, geom, fieldsWithValues, refreshMap);
        return addedFeature;
    }

    /**
     * Gets the attributes with their values for a new feature. For each layer it is supposed to be
     * overridden.
     *
     * @param geom
     * @return
     */
    protected java.util.HashMap<String, Object> getFieldsWithValuesForNewFeatures(Geometry geom) {
        java.util.HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        return fieldsWithValues;
    }

    /**
     * Gets the corresponding bean of a feature
     *
     * @param feature
     * @return
     */
    public final <T extends SpatialBean> T getBean(SimpleFeature feature) {
        T bean = this.getNewBeanInstance();
        bean.setRowId(feature.getID());
        int foundIndex = this.getBeanList().indexOf(bean);
        if (foundIndex > -1) {
            bean = (T) this.getBeanList().get(foundIndex);
        } else {
            bean = null;
        }
        return bean;
    }

    /**
     * It handles the changes in the collection of features
     *
     * @param ev
     */
    private void featureCollectionChanged(CollectionEvent ev) {
        if (ev.getFeatures() == null) {
            return;
        }
        if (ev.getEventType() == CollectionEvent.FEATURES_ADDED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                //Check if the bean already exist in the list. This is the case when 
                //the bean list is initiated.
                SpatialBean bean = this.getBean(feature);
                //If not found it means it is a new bean just created so it is added to the list
                if (bean == null) {
                    this.getBeanList().add(this.newBean(feature));
                }
            }
        } else if (ev.getEventType() == CollectionEvent.FEATURES_REMOVED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                SpatialBean found = this.getBean(feature);
                if (found != null) {
                    this.getBeanList().remove(found);
                }
            }
        } else if (ev.getEventType() == CollectionEvent.FEATURES_CHANGED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                SpatialBean found = this.getBean(feature);
                this.changeBean(found, feature);
            }
        }
    }

    /**
     * Gets a new bean from a feature
     *
     * @param feature
     * @return
     */
    private <T extends SpatialBean> T newBean(SimpleFeature feature) {
        T bean = this.getNewBeanInstance();
        bean.setRowId(feature.getID());
        this.changeBean(bean, feature);
        return bean;

    }

    /**
     * Initializes a bean.
     *
     * @param <T>
     * @return
     */
    private <T extends SpatialBean> T getNewBeanInstance() {
        T bean = null;
        try {
            bean = (T) this.beanClass.newInstance();
        } catch (InstantiationException ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_CHANGE_ERROR_INITIALIZE_NEW_OBJECT, ex);
        } catch (IllegalAccessException ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_CHANGE_ERROR_INITIALIZE_NEW_OBJECT, ex);
        }
        return bean;

    }

    /**
     * Changes an existing bean from its correspondent feature
     *
     * @param targetBean
     * @param feature
     */
    private <T extends SpatialBean> void changeBean(T targetBean, SimpleFeature feature) {
        HashMap<String, Object> values = new HashMap<String, Object>();
        for (String attrName : this.getAttributeNames()) {
            values.put(attrName, feature.getAttribute(attrName));
        }
        targetBean.setValues(values);
        targetBean.setFeatureGeom((Geometry) feature.getDefaultGeometry());
    }
}
