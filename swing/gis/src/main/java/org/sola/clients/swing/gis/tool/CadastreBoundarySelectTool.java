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

import com.vividsolutions.jts.geom.*;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.GeometryUtility;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.tool.extended.ExtendedDrawRectangle;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.clients.swing.gis.layer.TargetBoundaryLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * It is used to select a boundary from the target cadastre objects in the targetLayer.
 * 
 * @author Elton Manoku
 */
public class CadastreBoundarySelectTool extends ExtendedDrawRectangle {

    public final static String MAP_ACTION_NAME = "cadastre-boundary-select";

    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_BOUNDARY_SELECT_TOOL_TOOLTIP).getMessage();
    private TargetBoundaryLayer targetLayer;
    private ExtendedLayerGraphics targetNodeLayer;
    
    protected CadastreBoundaryPointLayer pointLayer;

    protected List<String> targetCadastreObjectIds = new ArrayList<String>();

    /**
     * Constructor
     * @param pointLayer The layer where the target boundary and its start and end points
     * are hold
     * @param targetLayer The target cadastre object layer. In this layer the target cadastre 
     * objects are found
     * @param targetNodeLayer The target node layer. In this layer the boundary nodes are found
     */
    public CadastreBoundarySelectTool(
            CadastreBoundaryPointLayer pointLayer,
            TargetBoundaryLayer targetLayer,
            ExtendedLayerGraphics targetNodeLayer) {
        this.setToolName(MAP_ACTION_NAME);
        this.setToolTip(toolTip);
        this.pointLayer = pointLayer;
        this.targetLayer = targetLayer;
        this.targetNodeLayer = targetNodeLayer;
    }

    /**
     * Called when the tool is selected or deselected. If the tool is selected, depending
     * in the step that is active, gives a hint message.
     * 
     * @param selected 
     */
    @Override
    public void onSelectionChanged(boolean selected) {
        super.onSelectionChanged(selected);
        if (selected) {
            if (this.isFirstStep()) {
                Messaging.getInstance().show(
                        GisMessage.CADASTRE_BOUNDARY_SELECT_FIRST_BOUNDARY_POINT);
            } else {
                Messaging.getInstance().show(
                        GisMessage.CADASTRE_BOUNDARY_SELECT_SECOND_BOUNDARY_POINT);
            }
        }
    }

    /**
     * Gets the target layer
     * @return 
     */
    protected TargetBoundaryLayer getTargetLayer() {
        return this.targetLayer;
    }

    /**
     * This is the map action for this tool. Depending in which step the procedure is, 
     * selects the begin point or the end point of the boundary.
     * Different checks are done for each step.
     * In the end of the second step, the boundary self is being identified.
     * 
     * @param env The rectangle to filter the boundary node
     */
    @Override
    protected void onRectangleFinished(Envelope2D env) {
        if (this.isFirstStep()) {
            //Select starting point
            this.step1(env);
            if (this.pointLayer.getStartPoint() != null) {
                this.getMapControl().refresh();
                Messaging.getInstance().show(
                        GisMessage.CADASTRE_BOUNDARY_SELECT_SECOND_BOUNDARY_POINT);
            }
        } else {
            //Select ending point
            this.step2(env);
            this.getMapControl().refresh();
        }
    }

    /**
     * Gets if the status of the procedure is the first step.
     * Otherwise it is the second step.
     * 
     * @return 
     */
    private boolean isFirstStep() {
        return (this.pointLayer.getStartPoint() == null || this.pointLayer.getEndPoint() != null);
    }

    /**
     * Gets the point in the target node layer. This is a search in the existing node already
     * identified by other means.
     * 
     * @param env
     * @return 
     */
    protected final SimpleFeature getFirstPointFeature(Envelope2D env) {
        return this.targetNodeLayer.getFirstFeatureInRange(new ReferencedEnvelope(env));
    }

    /**
     * This step defines the begin node of the target boundary.
     * It searches in the nodes already present in the layer.
     * If a node is found, it makes a collection with all cadastre objects that share 
     * this node.
     * 
     * @param env 
     */
    protected void step1(Envelope2D env) {
        this.clearSelection();
        SimpleFeature pointFeature = this.getFirstPointFeature(env);
        if (pointFeature != null) {
            this.pointLayer.setStartPoint((Point) pointFeature.getDefaultGeometry());
            this.targetCadastreObjectIds.addAll(
                    getTargetLayer().getCadastreObjectTargetIdsFromNodeFeature(pointFeature));

        }
    }

    /**
     * This step defines the end node of the target boundary.
     * It searches in the nodes that are found in the layer. 
     * If a node is found, it is retrieved the list of cadastre objects that share this node.
     * If the collection of the cadastre objects found in this step, intersects with 1 or 2
     * members the collection of cadastre objects found in the first step, then we can identify a 
     * boundary.
     * If no boundary can be identified, the process is reseted.
     * 
     * @param env
     * @return 
     */
    protected boolean step2(Envelope2D env) {
        SimpleFeature pointFeature = this.getFirstPointFeature(env);
        if (pointFeature == null) {
            return false;
        }
        Point geom = (Point) pointFeature.getDefaultGeometry();
        if (this.pointLayer.getStartPoint().equals(geom)) {
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_START_END_POINT_SAME);
            return true;
        }
        List<String> targetCadastreObjectIdsTmp = new ArrayList<String>();
        List<String> targetIds = 
                getTargetLayer().getCadastreObjectTargetIdsFromNodeFeature(pointFeature);
        for (String id : targetIds) {
            if (!this.targetCadastreObjectIds.contains(id)) {
                continue;
            }
            targetCadastreObjectIdsTmp.add(id);
        }
        this.targetCadastreObjectIds.clear();
        this.targetCadastreObjectIds.addAll(targetCadastreObjectIdsTmp);
        if (this.targetCadastreObjectIds.size() < 1
                || this.targetCadastreObjectIds.size() > 2) {
            this.clearSelection();
        } else {
            this.pointLayer.setEndPoint(geom);
            this.defineTargetBoundary();
        }
        return true;

    }

    /**
     * It identifies the boundary. It is called after it is made sure that there are two nodes
     * defined and there is a boundary between these two nodes.
     * For the boundary it is searched in the clockwise order in the rings of the first cadastre
     * object which contains both nodes.
     * 
     * @return 
     */
    protected final boolean defineTargetBoundary() {
        boolean clockwise = true;
        Coordinate startCoordinate = this.pointLayer.getStartPoint().getCoordinate();
        Coordinate endCoordinate = this.pointLayer.getEndPoint().getCoordinate();
        Polygon targetGeom =
                (Polygon) getTargetLayer().getFeatureByCadastreObjectId(
                this.targetCadastreObjectIds.get(0)).getDefaultGeometry();
        LineString targetBoundaryGeom = this.getTargetBoundaryFromRing(
                targetGeom.getExteriorRing(), clockwise, startCoordinate, endCoordinate);
        if (targetBoundaryGeom == null) {
            for (int ringInd = 0; ringInd < targetGeom.getNumInteriorRing(); ringInd++) {
                targetBoundaryGeom = this.getTargetBoundaryFromRing(
                        targetGeom.getExteriorRing(), clockwise, startCoordinate, endCoordinate);
                if (targetBoundaryGeom != null) {
                    break;
                }
            }
        }
        if (targetBoundaryGeom != null) {
            this.pointLayer.setTargetBoundary(targetBoundaryGeom);
            this.getMapControl().getMapActionByName(CadastreBoundaryEditTool.NAME).setEnabled(true);
        }
        return (targetBoundaryGeom != null);
    }

    /**
     * It resets the procedure of identifying the target boundary
     */
    public final void clearSelection() {
        this.pointLayer.clearSelection();
        for (String cadastreObjectId : this.targetCadastreObjectIds) {
            getTargetLayer().notifyEventChanges(cadastreObjectId);
        }
        this.targetCadastreObjectIds.clear();
        //Disactivate editing of boundary
        this.getMapControl().getMapActionByName(CadastreBoundaryEditTool.NAME).setEnabled(false);
    }

    /**
     * Gets a target boundary from a polygon ring.
     * 
     * @param targetRing The target ring
     * @param clockwise True = Identification of the boundary is done in clockwise order
     * @param startCoordinate The start node coordinate
     * @param endCoordinate The end node coordinate
     * 
     * @return 
     */
    private LineString getTargetBoundaryFromRing(LineString targetRing, boolean clockwise,
            Coordinate startCoordinate, Coordinate endCoordinate) {
        CoordinateList coordList = new CoordinateList(targetRing.getCoordinates());
        if (!coordList.contains(startCoordinate)) {
            return null;
        }
        int startPosition = coordList.indexOf(startCoordinate);
        int pointPosition = startPosition;
        CoordinateList boundaryCoordList = new CoordinateList();
        boundaryCoordList.add(startCoordinate.clone());
        boolean endPointFound = false;
        while (!endPointFound) {
            if (clockwise) {
                if (pointPosition == coordList.size() - 1) {
                    pointPosition = 0;
                } else {
                    pointPosition++;
                }
            } else {
                if (pointPosition == 0) {
                    pointPosition = coordList.size() - 1;
                } else {
                    pointPosition--;
                }

            }
            boundaryCoordList.add(coordList.getCoordinate(pointPosition).clone());
            endPointFound = coordList.getCoordinate(pointPosition).equals2D(endCoordinate);
        }
        if (!endPointFound) {
            return null;
        }

        return GeometryUtility.getGeometryFactory().createLineString(
                boundaryCoordList.toCoordinateArray());
    }

}
