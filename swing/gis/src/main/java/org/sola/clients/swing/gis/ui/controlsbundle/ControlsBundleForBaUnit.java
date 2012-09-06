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
package org.sola.clients.swing.gis.ui.controlsbundle;

import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/**
 * A control bundle that is used in the property form to show the location of related cadastre
 * objects for the given property.
 *
 * @author Elton Manoku
 */
public final class ControlsBundleForBaUnit extends SolaControlsBundle {

    ExtendedLayerGraphics layerForCadastreObjects;
    private final String CADASTRE_OBJECTS_LAYERNAME = "Current property cadastre objects";
    private final String STYLE_RESOURCE = "parcel_highlighted.xml";

    /**
     * Creates a controls bundle for display in the property form
     */
    public ControlsBundleForBaUnit() {
        super();
        this.Setup(PojoDataAccess.getInstance());
    }

    /**
     * Performs setup actions for the control bundle
     * @param pojoDataAccess 
     */
    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess);
        try {
            layerForCadastreObjects =
                    new ExtendedLayerGraphics(CADASTRE_OBJECTS_LAYERNAME,
                    Geometries.POLYGON, STYLE_RESOURCE);
            this.getMap().addLayer(layerForCadastreObjects);
        } catch (InitializeLayerException ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_OBJBAUNIT_SETUP_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_OBJBAUNIT_SETUP_ERROR);
        }
    }
    
    /**
     * Sets the cadastre objects to be marked in the map.
     * These cadastre objects are retrieved from the current baUnitBean
     * 
     * @param cadastreObjectBeanList 
     */
    public void setCadastreObjects(SolaList<CadastreObjectBean> cadastreObjectBeanList) {
        List<CadastreObjectTO> cadastreObjects = new ArrayList<CadastreObjectTO>();
        for (CadastreObjectBean coBean : cadastreObjectBeanList) {
           cadastreObjects.add(TypeConverters.BeanToTrasferObject(coBean, CadastreObjectTO.class));
        }
        layerForCadastreObjects.removeFeatures(false);
        this.addCadastreObjectsInLayer(layerForCadastreObjects, cadastreObjects);
    }

    /**
     * Adds from the list of cadastre objects features in layer.
     *
     * @param inLayer
     * @param cadastreObjects
     */
    private void addCadastreObjectsInLayer(
            ExtendedLayerGraphics inLayer, List<CadastreObjectTO> cadastreObjects) {
        if (cadastreObjects == null || cadastreObjects.isEmpty()) {
            return;
        }

        try {
            boolean featureAdded = false;
            for (CadastreObjectTO cadastreObject : cadastreObjects) {
                if (cadastreObject.getGeomPolygon() == null){
                    continue;
                }
                inLayer.addFeature(cadastreObject.getId(),
                        cadastreObject.getGeomPolygon(), null, false);
                featureAdded = true;
            }
            if (featureAdded) {
                ReferencedEnvelope envelope = inLayer.getFeatureCollection().getBounds();
                envelope.expandBy(10);
                this.getMap().setDisplayArea(envelope);
            }
        } catch (ParseException ex) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_ADD_CO);
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_CHANGE_ERROR_ADD_CO, ex);
        }
    }
}
