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
package org.sola.clients.swing.gis.ui.controlsbundle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.util.List;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.Messaging;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/**
 * Abstract bundle that defines common functionality for bundles that are supposed to deal with
 * cadastre objects.
 *
 * @author Elton Manoku
 */
public abstract class ControlsBundleForWorkingWithCO extends SolaControlsBundle {

    /**
     * Adds from the list of cadastre objects features in layer.
     *
     * @param inLayer
     * @param cadastreObjects
     */
    protected void addCadastreObjectsInLayer(
            ExtendedLayerGraphics inLayer, List<CadastreObjectTO> cadastreObjects) {
        if (cadastreObjects == null || cadastreObjects.isEmpty()) {
            return;
        }

        try {
            ReferencedEnvelope envelope = null;
            for (CadastreObjectTO cadastreObject : cadastreObjects) {
                SimpleFeature featureAdded =
                        inLayer.addFeature(cadastreObject.getId(),
                        cadastreObject.getGeomPolygon(), null);
                ReferencedEnvelope tmpEnvelope = JTS.toEnvelope(
                        (Geometry) featureAdded.getDefaultGeometry());
                if (envelope == null) {
                    envelope = tmpEnvelope;
                } else {
                    envelope.expandToInclude(tmpEnvelope);
                }
            }
            if (envelope != null) {
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
