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

import com.vividsolutions.jts.io.ParseException;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetBean;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetListBean;
import org.sola.clients.swing.gis.beans.SpatialBean;

/**
 *
 * Layer of the target cadastre objects that is used during the cadastre change. <br/>
 * It maintains a list of cadastre objects being targeted for change.
 *
 * @author Elton Manoku
 */
public class CadastreChangeTargetCadastreObjectLayer extends AbstractSpatialObjectLayer {

    private static final String LAYER_NAME = "target_cadastre_objects";
    private static final String LAYER_STYLE_RESOURCE = "parcel_target.xml";

    /**
     * Constructor
     *
     * @throws InitializeLayerException
     */
    public CadastreChangeTargetCadastreObjectLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POLYGON,
                LAYER_STYLE_RESOURCE, null, CadastreObjectTargetBean.class);
        this.listBean = new CadastreObjectTargetListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
    }

    /**
     * Gets list of cadastre object targets
     *
     * @return
     */
    @Override
    public List<CadastreObjectTargetBean> getBeanList() {
        return (List<CadastreObjectTargetBean>) super.getBeanList();
    }

    /**
     * It overrides the parent class method because for the geometry value for the feature, it
     * is used the geomPolygonCurrentForFeature property of the bean instead of the 
     * default one.
     * 
     * @param bean
     * @return
     * @throws ParseException 
     */
    @Override
    protected SimpleFeature addFeatureFromBean(SpatialBean bean) throws ParseException {
        return this.addFeature(bean.getRowId(), 
                ((CadastreObjectTargetBean)bean).getGeomPolygonCurrentForFeature(),
                bean.getValues(this.getAttributeNames()), false);
    }
}
