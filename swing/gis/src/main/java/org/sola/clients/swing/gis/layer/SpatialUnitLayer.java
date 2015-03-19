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
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.beans.LevelBean;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.beans.SpatialUnitBean;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupBean;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupListBean;
import org.sola.clients.swing.gis.beans.SpatialUnitListBean;
import org.sola.clients.swing.gis.ui.control.SpatialUnitGroupListPanel;
import org.sola.clients.swing.gis.ui.control.SpatialUnitListPanel;

/**
 * Editing Layer that is used for the transactions around the spatial unit editor.
 * 
 * @author Elton Manoku
 */
public class SpatialUnitLayer extends AbstractSpatialObjectLayer {

    private static String LAYER_NAME = "spatial_unit";
    private static String LAYER_STYLE_RESOURCE = "spatial_unit.xml";
    private static final String LAYER_FIELD_LABEL = "label";
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:\"\"", LAYER_FIELD_LABEL);
    private SpatialUnitListPanel spatialObjectDisplayPanel;
    private LevelBean level;
    private Integer idGenerator = null;

    public SpatialUnitLayer()
            throws InitializeLayerException {
        super(LAYER_NAME, Geometries.GEOMETRY,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION, SpatialUnitBean.class);
        this.listBean = new SpatialUnitListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
        this.spatialObjectDisplayPanel =
                new SpatialUnitListPanel((SpatialUnitListBean) this.listBean);
        initializeFormHosting(this.spatialObjectDisplayPanel.getTitle(), this.spatialObjectDisplayPanel);
    }

    public LevelBean getLevel() {
        return level;
    }

    public final void setLevel(LevelBean level) {
        this.level = level;
        ((SpatialUnitListBean) this.listBean).setLevel(this.level);
        this.setBeanList(new ArrayList<SpatialUnitBean>());
    }

    @Override
    public List<SpatialUnitBean> getBeanList() {
        return (List<SpatialUnitBean>) super.getBeanList();
    }

    @Override
    public <T extends SpatialBean> void setBeanList(List<T> beanList) {
        super.setBeanList(beanList);
        this.idGenerator = null;
    }

    @Override
    protected HashMap<String, Object> getFieldsWithValuesForNewFeatures(Geometry geom) {
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        fieldsWithValues.put(LAYER_FIELD_LABEL, getLabelForNewFeature());
        return fieldsWithValues;
    }

    private String getLabelForNewFeature() {
        if (idGenerator == null) {
            idGenerator = 0;
            for (SpatialUnitBean bean : getBeanList()) {
                try {
                    Integer beanId = Integer.parseInt(bean.getLabel());
                    if (idGenerator < beanId) {
                        idGenerator = beanId;
                    }
                } catch (NumberFormatException ex) {
                    //Ignore exception
                }
            }
        }
        idGenerator++;
        return idGenerator.toString();
    }
}
