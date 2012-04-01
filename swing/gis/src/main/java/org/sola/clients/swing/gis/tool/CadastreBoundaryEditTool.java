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

import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import java.util.Collection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.geotools.swing.tool.extended.ExtendedDrawToolWithSnapping;
import org.geotools.swing.tool.extended.ExtendedPan;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * It handles the editing of a selected boundary. The boundary is already defined 
 * by the points found in the pointLayer and also the boundary itself is found there.
 * Do not forget to set the targetLayer as well before using it.
 * 
 * @author Elton Manoku
 */
public class CadastreBoundaryEditTool extends ExtendedDrawToolWithSnapping {

    public final static String NAME = "cadastre-boundary-edit";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_BOUNDARY_EDIT_TOOL_TOOLTIP).getMessage();
    private ExtendedLayerGraphics targetLayer;
    private CadastreBoundaryPointLayer pointLayer;

    /**
     * Constructor
     * @param pointLayer The layer where the boundary to be changed is defined.
     * 
     */
    public CadastreBoundaryEditTool(CadastreBoundaryPointLayer pointLayer) {
        this.setToolName(NAME);
        this.setGeometryType(Geometries.LINESTRING);
        this.setToolTip(toolTip);
        this.pointLayer = pointLayer;
        this.getTargetSnappingLayers().add(this.pointLayer);
    }

    /**
     * Sets the layer where the target cadastre object is found. 
     * The boundary that will be modified is found in an object in this layer.
     * 
     * @param targetLayer 
     */
    public void setTargetLayer(ExtendedLayerGraphics targetLayer) {
        this.targetLayer = targetLayer;
    }

    /**
     * After the finishing of digitizing of the new boundary, it si checked if the new geometry
     * can replace the existing boundary. The condition is that the new geometry should 
     * begin and end where the existing boundary begins and ends.
     * If that is true, then it is replaced the old boundary with the new geometry in all 
     * cadastre objects where the old boundary is found.
     * 
     * @param geometry 
     */
    @Override
    protected void treatFinalizedGeometry(Geometry geometry) {
        LineString targetBoundary = this.pointLayer.getTargetBoundary();
        if (targetBoundary == null) {
            return;
        }
        //Check if the new boundary starts and ends where the target boundary starts and ends
        LineString newBoundary = (LineString) geometry;
        if (!targetBoundary.getStartPoint().equals(newBoundary.getStartPoint())
                || !targetBoundary.getEndPoint().equals(newBoundary.getEndPoint())) {
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_NEW_MUST_START_END_AS_TARGET);
            return;
        }
        SimpleFeatureIterator iterator =
                (SimpleFeatureIterator) this.targetLayer.getFeatureCollection().features();
        while (iterator.hasNext()) {
            SimpleFeature currentFeature = iterator.next();
            Polygon currentGeom = (Polygon) currentFeature.getDefaultGeometry();
            if (!this.cadastralObjectHasTargetBoundary(currentGeom, targetBoundary)) {
                continue;
            }
            currentGeom = this.getPolygonModifiedBoundary(currentGeom, targetBoundary, newBoundary);
            this.targetLayer.replaceFeatureGeometry(currentFeature, currentGeom);
        }
        iterator.close();
        this.getSelectTool().clearSelection();
        this.getMapControl().setActiveTool(ExtendedPan.NAME);
    }

    /**
     * It replaces the old boundary with the new boundary in a given polygon
     * 
     * @param targetPolygon
     * @param targetBoundary
     * @param newBoundary
     * @return 
     */
    private Polygon getPolygonModifiedBoundary(
            Polygon targetPolygon, LineString targetBoundary, LineString newBoundary) {
        LinearRing exteriorRing = this.getModifiedRing(
                targetPolygon.getExteriorRing(), targetBoundary, newBoundary);
        LinearRing[] interiorRings = new LinearRing[targetPolygon.getNumInteriorRing()];
        for (int ringInd = 0; ringInd < targetPolygon.getNumInteriorRing(); ringInd++) {
            interiorRings[ringInd] = this.getModifiedRing(
                    targetPolygon.getInteriorRingN(ringInd), targetBoundary, newBoundary);
        }
        return targetPolygon.getFactory().createPolygon(exteriorRing, interiorRings);
    }

    /**
     * It replaces the old boundary with the new boundary in a given polygon ring
     * 
     * @param ring
     * @param targetBoundary
     * @param newBoundary
     * @return 
     */
    private LinearRing getModifiedRing(
            LineString ring, LineString targetBoundary, LineString newBoundary) {
        if (!ring.intersects(targetBoundary)) {
            return ring.getFactory().createLinearRing(ring.getCoordinateSequence());
        }
        Geometry leftPart = ring.difference(targetBoundary);
        LineMerger lineMerger = new LineMerger();
        lineMerger.add(leftPart);
        lineMerger.add(newBoundary);
        Collection result = lineMerger.getMergedLineStrings();
        CoordinateList coordList = new CoordinateList(ring.getCoordinates());
        for (Object linePart : result.toArray()) {
            coordList = new CoordinateList(((LineString) linePart).getCoordinates());
            coordList.closeRing();
            break;
        }
        return ring.getFactory().createLinearRing(coordList.toCoordinateArray());
    }

    /**
     * It checks if the boundary belongs to a the cadastre object.
     * 
     * @param coGeom The cadastre object geometry
     * @param targetBoundary The boundary
     * @return 
     */
    private boolean cadastralObjectHasTargetBoundary(Polygon coGeom, LineString targetBoundary){
        return coGeom.covers(targetBoundary);
    }
    
    /**
     * The tool that is used to select the target boundary
     * 
     * @return 
     */
    private CadastreBoundarySelectTool getSelectTool() {
        ExtendedAction selectAction =
                this.getMapControl().getMapActionByName(CadastreBoundarySelectTool.NAME);
        return (CadastreBoundarySelectTool) selectAction.getAttachedTool();
    }
}
