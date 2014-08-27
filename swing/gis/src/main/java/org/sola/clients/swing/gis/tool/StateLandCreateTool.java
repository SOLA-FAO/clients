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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.tool.extended.ExtendedDrawToolWithSnapping;
import org.sola.clients.swing.gis.layer.StateLandEditLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Tool to create new road centerlines. The road centerlines created by this
 * tool can be edited using the {@linkplain StateLandEditTool}. This tool
 * supports snapping.
 */
public class StateLandCreateTool extends ExtendedDrawToolWithSnapping {

    /**
     * Name of the tool - add-road
     */
    public final static String NAME = "state-land-add";
    private String toolTip = MessageUtility.getLocalizedMessage(
    GisMessage.STATE_LAND_ADD_TOOLTIP).getMessage();
    private StateLandEditLayer editLayer;

    /**
     * Constructor for tool.
     *
     * @param editLayer The {@linkplain SpatialUnitEditLayer} to add new
     * features to.
     * @see SpatialUnitEditLayer#mapLayerToLevel(java.lang.String)
     * SpatialUnitEditLayer.mapLayerToLevel
     */
    public StateLandCreateTool(StateLandEditLayer editLayer) {
        super();
        this.editLayer = editLayer;
        this.setGeometryType(Geometries.POLYGON);
        this.setToolName(NAME);
        this.setToolTip(toolTip);
        this.setIconImage("resources/state-land-add.png");
    }

    /**
     * Adds a new road feature to the {@linkplain SpatialUnitEditLayer}
     *
     * @param geometry The geometry to add.
     */
    @Override
    protected void treatFinalizedGeometry(Geometry geometry) {
        HashMap attributes = new HashMap<String, Object>();
        attributes.put(StateLandEditLayer.LAYER_FIELD_FIRST_PART, null);
        attributes.put(StateLandEditLayer.LAYER_FIELD_LAST_PART, null);
        attributes.put(StateLandEditLayer.LAYER_FIELD_OFFICIAL_AREA, new BigDecimal(geometry.getArea()));
        this.editLayer.addFeature(UUID.randomUUID().toString(),
                geometry, attributes, true, true);
    }

    /**
     * Triggered when the user selects/activates the Tool in the Map toolbar.
     *
     * @param selected true if tool selected/activated, false otherwise.
     */
    @Override
    public void onSelectionChanged(boolean selected) {
        if (selected) {
            // If the Tool is selected, force the redraw of 
            // any existing polygon.
            afterRendering();
        }
    }
}
