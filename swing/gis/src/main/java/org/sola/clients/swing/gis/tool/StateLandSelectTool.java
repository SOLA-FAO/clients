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
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.List;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.extended.ExtendedTool;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.layer.StateLandEditLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Used to select Spatial Unit Features from the selection layers and add them
 * to the edit layer. Any geometry type is supported by this tool including
 * point, line-string and polygon.
 */
public class StateLandSelectTool extends ExtendedTool {

    /**
     * Tool Name - selectSpatialUnit
     */
    public final static String NAME = "state-land-select";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.STATE_LAND_SELECT_TOOLTIP).getMessage();
    private StateLandEditLayer editLayer = null;
    private List<ExtendedFeatureLayer> selectionLayers = new ArrayList<ExtendedFeatureLayer>();

    /**
     * Configures the tool with the layers to select features from and the edit
     * layer to add the features to.
     *
     * @param selectLayers List of layers that the tool can select features
     * from.
     * @param editLayer The layer the tool will add the selected features to.
     */
    public StateLandSelectTool(List<ExtendedFeatureLayer> selectLayers, StateLandEditLayer editLayer) {
        this.editLayer = editLayer;
        this.selectionLayers = selectLayers;
        this.setToolName(NAME);
        this.setIconImage("resources/state-land-select.png");
        this.setToolTip(toolTip);
    }

    /**
     * Indicates if the selected spatial unit should be deleted when the
     * transaction is approved.
     *
     * @return Always returns false. This method may be overridden in descended
     * tools to change the behavior of the tool.
     */
    protected boolean getDeleteOnApproval() {
        return false;
    }

    /**
     * Determines the features in the selection layers that are within the
     * specified tolerance of the mouse click and adds these features to the
     * edit layer.
     *
     * @param ev
     */
    @Override
    public void onMouseClicked(MapMouseEvent ev) {
        ReferencedEnvelope envelope = ev.getEnvelopeByPixels(5);
        for (ExtendedFeatureLayer selectionLayer : selectionLayers) {
            if (!selectionLayer.isVisible()) {
                // If the selection layer is turned off, don't try to select 
                // any features from it. 
                continue;
            }
            FeatureCollection selectedFeatures = selectionLayer.getFeaturesInRange(envelope, null);
            if (selectedFeatures != null && selectedFeatures.size() > 0) {
                FeatureIterator i = selectedFeatures.features();
                try {
                    while (i.hasNext()) {
                        SimpleFeature feature = (SimpleFeature) i.next();
                        if (this.editLayer.removeFeature(feature.getID(), false) == null) {
                            this.editLayer.addFeature(feature.getID(),
                                    (Geometry) feature.getDefaultGeometry(), false, true);
                        }
                    }
                    // Don't process any other selection layers. 
                    break; 
                } finally {
                    // Must close the iterator in a finally clause otherwise it may remain open and
                    // cause concurrent modfication errors. 
                    i.close();
                    this.getMapControl().refresh(); 
                }

            }
        }
    }
}
