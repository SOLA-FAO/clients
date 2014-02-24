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
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Envelope;
import java.util.List;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.swing.extended.util.GeometryUtility;
import org.geotools.swing.tool.extended.ExtendedDrawRectangle;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.SpatialUnitGroupLayer;
import org.sola.webservices.transferobjects.cadastre.SpatialUnitGroupTO;

/**
 * It is used to select a set of spatial unit groups.
 * 
 * @author Elton Manoku
 */
public class SpatialUnitGroupSelect extends ExtendedDrawRectangle {

    public final static String MAP_ACTION_NAME = "spatial-unit-group-select";
    private static java.util.ResourceBundle resource =
            java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/tool/resources/strings");

    private SpatialUnitGroupLayer targetLayer;

    /**
     */
    public SpatialUnitGroupSelect(SpatialUnitGroupLayer targetLayer) {
        this.setToolName(MAP_ACTION_NAME);
        this.setToolTip(resource.getString(
                String.format("%s.tooltip",this.getClass().getSimpleName())));
        this.targetLayer = targetLayer;
    }

    /**
     * @param env The rectangle to filter
     */
    @Override
    protected void onRectangleFinished(Envelope2D env) {
        byte[] filteringGeometry = GeometryUtility.getWkbFromGeometry(
                JTS.toGeometry(new Envelope(env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY())));
        List<SpatialUnitGroupTO> spatialUnitGroupTOList =
                PojoDataAccess.getInstance().getCadastreService().getSpatialUnitGroups(
                filteringGeometry, this.targetLayer.getHierarchyLevel(), this.getMapControl().getSrid());
        
        List<SpatialUnitGroupBean> spatialUnitGroupBeanList =
            TypeConverters.TransferObjectListToBeanList(spatialUnitGroupTOList, 
                SpatialUnitGroupBean.class, null);
        this.targetLayer.setBeanList(spatialUnitGroupBeanList);
    }

}
