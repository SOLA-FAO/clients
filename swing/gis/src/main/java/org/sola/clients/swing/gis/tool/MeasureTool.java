/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
import java.text.DecimalFormat;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.tool.extended.ExtendedDrawTool;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Map Tool used to measure distance on the map. Pan and Zoom can 
 * also be used while using this tool. 
 * @author soladev
 */
public class MeasureTool extends ExtendedDrawTool {

    public final static String NAME = "measure";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.MEASURE_TOOLTIP).getMessage();

    public MeasureTool() {
        super();
        this.setGeometryType(Geometries.LINESTRING);
        this.setToolName(NAME);
        this.setToolTip(toolTip);
        this.setIconImage("resources/ruler.png");
    }

    /** Triggered when the user double clicks to complete the measure
     * line. Formats the display value as meters or kilometers depending
     * on the length of the measure line and displays a message to inform
     * the user. 
     * @param geometry Geometry representing the measure line
     */
    @Override
    protected void treatFinalizedGeometry(Geometry geometry) {
        Double length = geometry.getLength();
        DecimalFormat formatter = new DecimalFormat("#,###,###.0");
        String messageId = GisMessage.MEASURE_DISTANCE_METERS;
        if (length > new Double(5000)) {
            length = length / 1000;
            messageId = GisMessage.MEASURE_DISTANCE_KILOMETERS;
        }
        Messaging.getInstance().show(messageId, formatter.format(length));
    }

    /**
     * Triggered when the user selects/activates the Measure Tool in the Map
     * toolbar.
     *
     * @param selected true if tool selected/activated, false otherwise.
     */
    @Override
    public void onSelectionChanged(boolean selected) {
        if (selected) {
            // If the MeasureTool is selected, force the redraw of 
            // any existing measure line.
            afterRendering();
        }
    }
}