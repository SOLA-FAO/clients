/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.AbstractListSpatialBean;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.beans.StateLandParcelBean;
import org.sola.clients.swing.gis.beans.StateLandParcelListBean;
import org.sola.clients.swing.gis.ui.control.StateLandParcelForm;
import org.sola.common.StringUtility;
import org.sola.common.WindowUtility;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.GisMessage;

/**
 *
 * @author soladev
 */
public class StateLandEditLayer extends AbstractSpatialObjectLayer {

    public static final String LAYER_FIELD_FIRST_PART = "nameFirstpart";
    public static final String LAYER_FIELD_LAST_PART = "nameLastpart";
    public static final String LAYER_FIELD_OFFICIAL_AREA = "officialAreaSize";
    /*
     * Defines the additional attributes for the layer. The format of this string is <field
     * name>:<data type>. Note that empty string (i.e. "") can be used to indicate the type as
     * string, but specifying the data type is easier to maintain.
     */
    private static final String LAYER_ATTRIBUTE_DEFINITION
            = String.format("%s:String,%s:String,%s:java.math.BigDecimal",
                    LAYER_FIELD_FIRST_PART, LAYER_FIELD_LAST_PART,
                    LAYER_FIELD_OFFICIAL_AREA);
    private static final String LAYER_NAME = "state_land_edit";
    private static final String LAYER_STYLE_RESOURCE = "state_land_edit.xml";
    private static final String LAYER_VERTEX_STYLE_RESOURCE = "state_land_edit_vertices.xml";
    private String applicationNumber = "";

    public StateLandEditLayer(String appNumber) throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POLYGON, LAYER_STYLE_RESOURCE,
                LAYER_ATTRIBUTE_DEFINITION, StateLandParcelBean.class);
        listBean = new StateLandParcelListBean();
        initializeListBeanEvents();
        this.applicationNumber = appNumber;
        // Configure the title for the layer using localized text
        setTitle(Messaging.getInstance().getMessageText(GisMessage.STATE_LAND_EDIT_LAYER_TITLE));

        // Configures a property change listener for the selected property of the listBean.
        this.listBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(
                        AbstractListSpatialBean.SELECTED_BEAN_PROPERTY)) {
                    highlightSelectedBean((SpatialBean) evt.getNewValue());
                }
            }
        });
    }

    /**
     * Highlights the geometry of the bean on the map to help users identify
     * which feature they are editing.
     *
     * @param bean The bean to highlight
     */
    private void highlightSelectedBean(SpatialBean bean) {
        this.getMapControl().clearSelectedFeatures();
        if (bean != null && bean.getFeatureGeom() != null) {
            String fid = bean.getRowId();
            SimpleFeature feature = this.getFeatureCollection().getFeature(fid);
            if (feature != null) {
                this.getMapControl().selectFeature(fid, (Geometry) feature.getDefaultGeometry());
            }
        }
        getMapControl().refresh();
    }

    public SimpleFeature addFeature(String fid, Geometry geom,
            HashMap<String, Object> fieldsWithValues, boolean refreshMap, boolean displayEditForm) {
        if (Geometries.get(geom) == Geometries.MULTIPOLYGON
                || Geometries.get(geom) == Geometries.GEOMETRYCOLLECTION) {
            // Only process the first geometry of the collection as cadastre objects must be POLYGON
            geom = geom.getGeometryN(0);
            if (geom.getNumGeometries() > 1) {
                LogUtility.log("StateLandEditLayer: Only first geometry added of " + geom.getNumGeometries());
            }
        }
        if (StringUtility.isEmpty((String) fieldsWithValues.get(LAYER_FIELD_LAST_PART))) {
            fieldsWithValues.put(LAYER_FIELD_LAST_PART, applicationNumber);
        }
        SimpleFeature result = super.addFeature(fid, geom, fieldsWithValues, refreshMap);
        if (displayEditForm) {
            StateLandParcelBean bean = this.getBean(result);
            highlightSelectedBean(bean);
            StateLandParcelForm form = new StateLandParcelForm(bean, false, 
                    WindowUtility.getTopFrame(), true);
            form.setVisible(true);
            highlightSelectedBean(null);
        }
        return result;
    }

    public StateLandParcelListBean getSLParcelListBean() {
        return (StateLandParcelListBean) listBean;
    }
}
