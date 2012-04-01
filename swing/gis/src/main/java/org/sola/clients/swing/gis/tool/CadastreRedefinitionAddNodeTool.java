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
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.Envelope2D;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.to.CadastreObjectNodeExtraTO;
import org.sola.common.MappingManager;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;

/**
 * Tool that is used to add a new node in an existing boundary during cadastre redefinition process.
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionAddNodeTool extends CadastreRedefinitionAbstractNodeTool {

    public final static String NAME = "add-node";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_TOOLTIP_ADD_NODE).getMessage();

    public CadastreRedefinitionAddNodeTool(PojoDataAccess dataAccess,
            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        super(dataAccess, cadastreObjectNodeModifiedLayer, cadastreObjectModifiedLayer);
        this.setToolName(NAME);
        this.setToolTip(toolTip);
    }

    /**
     * This is the action of this tool. It retrieves a potential node from the server. If a
     * potential node is found, it adds it in the node layer and also adds the cadastre objects that
     * share the potential node. Then a screen is shown to let the user change the coordinates of
     * the potential node or remove it if changed his mind.
     *
     * @param env
     */
    @Override
    protected void onRectangleFinished(Envelope2D env) {

        CadastreObjectNodeBean nodeBean = this.addNodeFromServer(env);
        if (nodeBean == null) {
            return;
        }
        SimpleFeature nodeFeature =
                this.cadastreObjectNodeModifiedLayer.getFeatureCollection().getFeature(
                nodeBean.getId());
        List<String> cadastreObjectTargetIds = new ArrayList<String>();
        for (CadastreObjectBean coBean : nodeBean.getCadastreObjectList()) {
            cadastreObjectTargetIds.add(coBean.getId());
        }
        this.insertNode(nodeFeature, cadastreObjectTargetIds);
        if (!this.manipulateNode(nodeFeature)) {
            this.removeNode(nodeFeature);
        }
    }

    /**
     * Gets the potential node bean from the server.
     *
     * @param env
     * @return The node bean if something is found, otherwise null.
     */
    @Override
    protected CadastreObjectNodeBean getNodeFromServer(Envelope2D env) {
        CadastreObjectNodeTO nodeTO =
                this.dataAccess.getCadastreService().getCadastreObjectNodePotential(
                env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY(),
                this.getMapControl().getSrid());
        if (nodeTO == null) {
            return null;
        }
        CadastreObjectNodeBean nodeBean = MappingManager.getMapper().map(
                new CadastreObjectNodeExtraTO(nodeTO), CadastreObjectNodeBean.class);
        return nodeBean;
    }

    /**
     * It inserts the potential node to the target cadastre objects.
     *
     * @param nodeFeature
     * @param cadastreObjectTargetIds The target cadastre object ids
     */
    private void insertNode(SimpleFeature nodeFeature, List<String> cadastreObjectTargetIds) {
        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        Coordinate coordinate = nodeFeatureGeom.getCoordinate();
        for (String cadastreObjectTargetId : cadastreObjectTargetIds) {
            SimpleFeature cadastreObjectFeature =
                    this.cadastreObjectModifiedLayer.getFeatureCollection().getFeature(
                    cadastreObjectTargetId);
            if (cadastreObjectFeature == null) {
                continue;
            }
            Polygon cadastreObjectGeom = (Polygon) cadastreObjectFeature.getDefaultGeometry();
            LinearRing exteriorRing = this.insertCoordinateInRing(
                    cadastreObjectGeom.getExteriorRing(), coordinate);

            LinearRing[] interiorRings = new LinearRing[cadastreObjectGeom.getNumInteriorRing()];
            for (int interiorRingIndex = 0;
                    interiorRingIndex < interiorRings.length;
                    interiorRingIndex++) {
                interiorRings[interiorRingIndex] = this.insertCoordinateInRing(
                        cadastreObjectGeom.getInteriorRingN(interiorRingIndex), coordinate);
            }

            cadastreObjectGeom =
                    this.cadastreObjectModifiedLayer.getGeometryFactory().createPolygon(
                    exteriorRing, interiorRings);
            cadastreObjectFeature.setDefaultGeometry(cadastreObjectGeom);
            cadastreObjectGeom.geometryChanged();
        }
        this.getMapControl().refresh();
    }

    /**
     * Inserts a coordinate into a ring
     *
     * @param target The target ring
     * @param coordinate The coordinate to insert
     * @return The ring with the new coordinate
     */
    private LinearRing insertCoordinateInRing(
            LineString target, Coordinate coordinate) {
        LocationIndexedLine line = new LocationIndexedLine(target);
        LinearLocation linearLocation = line.project(coordinate);

        int newCoordIndex = linearLocation.getSegmentIndex() + 1;

        com.vividsolutions.jts.geom.CoordinateList coordinates =
                new CoordinateList(target.getCoordinates(), false);

        coordinates.add(newCoordIndex,
                new Coordinate(coordinate.x, coordinate.y), true);

        coordinates.closeRing();

        return this.cadastreObjectModifiedLayer.getGeometryFactory().createLinearRing(
                coordinates.toCoordinateArray());

    }
}
