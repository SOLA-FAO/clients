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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Geometry;
import java.util.List;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.SpatialUnitGroupLayer;
import org.sola.webservices.transferobjects.cadastre.SpatialUnitGroupTO;

/**
 * It is used to select a set of spatial unit groups that belong to a level
 * hierarchy with a bigger number. The selected spatial unit groups can be used
 * to created a new spatial unit group of the selected hierarchy.
 *
 * @author Elton Manoku
 */
public class SpatialUnitGroupSelectSubHierarchy extends SpatialUnitGenericSelect {

    private final static String MAP_ACTION_NAME = "SpatialUnitGroupSelectSubHierarchy";

    /**
     */
    public SpatialUnitGroupSelectSubHierarchy(SpatialUnitGroupLayer targetLayer) {
        super(targetLayer, MAP_ACTION_NAME, true);
    }

    @Override
    protected List getSelectedSpatialBeans(byte[] filteringGeometry) {
        List<SpatialUnitGroupTO> toList =
                PojoDataAccess.getInstance().getCadastreService().getSpatialUnitGroups(
                filteringGeometry, ((SpatialUnitGroupLayer) this.getTargetLayer()).getHierarchyLevel() + 1,
                this.getMapControl().getSrid());
        return TypeConverters.TransferObjectListToBeanList(toList, SpatialUnitGroupBean.class, null);
    }

    /**
     * It merges all spatial units found in the target layer to a single one.
     * It does that only if there are at least 2 spatial units in the layer.
     * the merge happens according to the union function.
     * @param selectedSpatialBeans 
     */
    @Override
    protected void changeTargetLayer(List<SpatialBean> selectedSpatialBeans) {
        boolean mapMustBeRefreshed = false;
        for (SpatialBean spatialBean : selectedSpatialBeans) {
            Geometry geom = spatialBean.getFeatureGeom();
            boolean found = false;
            for(Object objectBean: this.getTargetLayer().getBeanList()){
                if (((SpatialBean)objectBean).getFeatureGeom().contains(geom.buffer(-0.1))){
                    found = true;
                }
            }
            if (!found){
                this.getTargetLayer().addFeature(null, geom, null, false);
                mapMustBeRefreshed = true;
            }
        }
        if (mapMustBeRefreshed){
            this.getMapControl().refresh();
        }
    }
}
