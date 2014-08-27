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
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.extended.VertexInformation;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerEditor;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.extended.util.GeometryUtility;
import org.geotools.swing.tool.extended.ExtendedEditGeometryTool;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows editing of spatial unit features. Features can be reshaped by dragging their coordinate
 * points. New coordinate points can be added using single click and existing coordinate points can
 * be removed using SHIFT single click. <p>This tool supports editing all feature types including
 * points, line-strings and polygons.</p><p>The default behavior of the
 * {@linkplain ExtendedEditGeometryTool} to allow new features to be created using single click has
 * been suppressed to avoid over complicating the tool.</p>
 */
public class StateLandEditTool extends ExtendedEditGeometryTool {

    public final static String NAME = "state-land-edit";
    private String toolTip = MessageUtility.getLocalizedMessage(
    GisMessage.STATE_LAND_EDIT_TOOLTIP).getMessage();

    public StateLandEditTool(ExtendedLayerEditor layer) {
            super(layer);
        this.setToolName(NAME);
        this.setIconImage("resources/state-land-edit.png");
        this.setToolTip(toolTip);
        this.setSnapDistanceInPixels(10);
        // Configure the tool so it snaps to the edit layer
        this.getTargetSnappingLayers().add(layer);
    }

    /**
     * Overrides the default behavior to allow new coordinates to be added into a feature if a line
     * of the feature is within tolerance or to remove a coordinate if the SHIFT key is held down.
     *
     * @param ev
     */
    @Override
    protected void onSingleClick(MapMouseEvent ev) {
        VertexInformation vertexFound = this.layer.getFirstVertexWithinDistance(
                ev.getWorldPos(), 2);

        if (ev.isShiftDown() && vertexFound != null) {
            // Remove the coordinate from the feature
            try {
                SimpleFeature feature = vertexFound.getFeature();
                Geometry originalGeom = (Geometry) feature.getDefaultGeometry();
                Geometry newGeom = GeometryUtility.removeCoordinate(originalGeom, vertexFound.getVertex());
                if (newGeom != null && !originalGeom.equals(newGeom)) {
                    this.layer.replaceFeatureGeometry(feature, newGeom);
                }
            } finally {
                // Ensure the map is redrawn after the geometry is replaced. 
                this.getMapControl().refresh();
            }

        } else if (!ev.isShiftDown()) {
            // Add the new coordinate to the feature 
            ReferencedEnvelope envelope = ev.getEnvelopeByPixels(5);
            FeatureCollection features = this.layer.getFeaturesInRange(envelope, null);
            Point mousePt = JTS.toGeometry(ev.getWorldPos());

            if (features != null && features.size() > 0) {
                FeatureIterator i = features.features();
                try {
                    while (i.hasNext()) {
                        SimpleFeature feature = (SimpleFeature) i.next();
                        Geometry originalGeom = (Geometry) feature.getDefaultGeometry();
                        Geometry newGeom = GeometryUtility.insertCoordinate(originalGeom,
                                mousePt.getCoordinate(), 2);
                        if (newGeom != null && !originalGeom.equals(newGeom)) {
                            this.layer.replaceFeatureGeometry(feature, newGeom);
                        }
                    }
                } finally {
                    i.close();
                    this.getMapControl().refresh();
                }
            }
        }
    }
}
