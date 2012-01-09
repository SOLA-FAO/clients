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

import org.geotools.feature.CollectionEvent;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.layer.NewSurveyPointLayer;
import org.geotools.swing.tool.extended.ExtendedEditGeometryTool;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author rizzom
 */
public class NodeLinkingTool extends ExtendedEditGeometryTool {

    private String toolName = "nodelinking";
    private String toolTip = MessageUtility.getLocalizedMessage(
                            GisMessage.CADASTRE_TOOLTIP_NEW_SURVEYPOINT).getMessage();

    public NodeLinkingTool(NewSurveyPointLayer targetLayer) {
        this.setToolName(toolName);
        this.setGeometryType(Geometries.POINT);
        this.setIconImage("resources/node-linking.png");
        this.setToolTip(toolTip);
        this.layer = targetLayer;
    }

    @Override
    protected SimpleFeature treatChangeVertex(DirectPosition2D mousePositionInMap) {
        SimpleFeature featureChanged = super.treatChangeVertex(mousePositionInMap);
        if (featureChanged != null && featureChanged.getAttribute(
                NewSurveyPointLayer.LAYER_FIELD_ISBOUNDARY).equals(1)) {
            featureChanged.setAttribute(NewSurveyPointLayer.LAYER_FIELD_ISLINKED,
                    ((this.getSnappedTarget() == SNAPPED_TARGET_TYPE.Vertex) ? 1 : 0));
            this.layer.getFeatureCollection().notifyListeners(featureChanged,
                    CollectionEvent.FEATURES_CHANGED);
        }
        return featureChanged;
    }
}