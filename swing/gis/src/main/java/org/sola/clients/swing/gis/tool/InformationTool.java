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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.WKBWriter;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.extended.ExtendedTool;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.ui.control.InformationResultWindow;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.search.QueryForSelect;
import org.sola.webservices.search.ResultForSelectionInfo;
import org.sola.webservices.spatial.ConfigMapLayerTO;

/**
 * The information tool searches where the mouse is clicked for objects. If objects are found, they
 * are shown in a form.
 * 
 * @author Elton Manoku
 */
public class InformationTool extends ExtendedTool {

    private PojoDataAccess dataAccess;
    private WKBWriter wkbWriter = new WKBWriter();
    private int pixelTolerance = 10;
    private InformationResultWindow resultWindow;
    private String toolTip =  MessageUtility.getLocalizedMessage(
                            GisMessage.INFOTOOL_CLICK).getMessage();

    /**
     * Constructor
     * 
     * @param dataAccess The data access that communicates with the services
     */
    public InformationTool(PojoDataAccess dataAccess) {
        this.setToolName("information");
        this.setToolTip(toolTip);
        this.setIconImage("resources/information.png");
        this.dataAccess = dataAccess;
    }

    /**
     * This is the action of this tool. On click the map coordinates are used to make a filter
     * in a shape of an envelope with a certain width.
     * For each layer that has a query for selected defined, a query definition is made and
     * then it is sent to the server to search for features fulfilling the condition.
     * The result is visualized.
     * 
     * @param ev 
     */
    @Override
    public void onMouseClicked(MapMouseEvent ev) {
        DirectPosition2D pos = ev.getWorldPos();
        double envelopeWidth =
                this.getMapControl().getPixelResolution() * this.pixelTolerance / 2;
        Envelope env = new Envelope(
                pos.x - envelopeWidth, pos.x + envelopeWidth,
                pos.y - envelopeWidth, pos.y + envelopeWidth);
        byte[] filteringGeometry = wkbWriter.write(JTS.toGeometry(env));

        List<QueryForSelect> queriesForSelect = new ArrayList<QueryForSelect>();
        for (ExtendedLayer solaLayer : this.getMapControl().getSolaLayers().values()) {
            ConfigMapLayerTO configMapLayer = this.dataAccess.getMapLayerInfoList().get(
                    solaLayer.getLayerName());
            if (configMapLayer == null){
                continue;
            }
            String queryNameForSelect = configMapLayer.getPojoQueryNameForSelect();
            if (queryNameForSelect == null || queryNameForSelect.isEmpty()) {
                continue;
            }
            QueryForSelect queryForSelect = new QueryForSelect();
            queryForSelect.setId(solaLayer.getLayerName());
            queryForSelect.setQueryName(queryNameForSelect);
            queryForSelect.setSrid(solaLayer.getSrid());
            queryForSelect.setFilteringGeometry(filteringGeometry);
            queriesForSelect.add(queryForSelect);

        }
        List<ResultForSelectionInfo> results = this.dataAccess.Select(queriesForSelect);
        this.VisualizeResult(results);
    }

    /**
     * It visualizes the result from the information tool.
     * @param results 
     */
    private void VisualizeResult(List<ResultForSelectionInfo> results) {
        if (this.resultWindow == null) {
            this.resultWindow = new InformationResultWindow();
        }
        this.resultWindow.Show(results);
    }
}
