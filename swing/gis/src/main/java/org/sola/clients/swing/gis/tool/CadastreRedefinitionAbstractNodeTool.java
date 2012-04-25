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
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.feature.CollectionEvent;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.geotools.swing.tool.extended.ExtendedDrawRectangle;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.ui.control.CadastreRedefinitionNodeModifyForm;
import org.sola.common.messaging.GisMessage;

/**
 * An abstract tool that is used for node modification tools used in the cadastre redefinition. It
 * contains common functionality for the tools that add or modify a node.
 *
 * @author Elton Manoku
 */
public abstract class CadastreRedefinitionAbstractNodeTool extends ExtendedDrawRectangle {

    private CadastreRedefinitionNodeModifyForm form = null;
    protected PojoDataAccess dataAccess;
    protected CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer;
    protected CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer;

    /**
     * Constructor
     *
     * @param dataAccess The data access that handles communication with the web services
     * @param cadastreObjectNodeModifiedLayer The node layer
     * @param cadastreObjectModifiedLayer The modified cadastre object layer
     */
    public CadastreRedefinitionAbstractNodeTool(
            PojoDataAccess dataAccess,
            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        this.dataAccess = dataAccess;
        this.cadastreObjectModifiedLayer = cadastreObjectModifiedLayer;
        this.cadastreObjectNodeModifiedLayer = cadastreObjectNodeModifiedLayer;
        this.form = new CadastreRedefinitionNodeModifyForm();
    }

    /**
     * If the tool is selected/ made active, then the irregular boundary procedure is reseted.
     *
     * @param selected
     */
    @Override
    public void onSelectionChanged(boolean selected) {
        super.onSelectionChanged(selected);
        if (selected) {
            ExtendedAction action =
                    this.getMapControl().getMapActionByName(CadastreBoundarySelectTool.NAME);
            if (action != null) {
                ((CadastreBoundarySelectTool) action.getAttachedTool()).clearSelection();
                this.getMapControl().refresh();
            }
        }
    }

    /**
     * Gets a node and the cadastre objects that share the node from the server.
     *
     * @param env
     * @return
     */
    protected abstract CadastreObjectNodeBean getNodeFromServer(Envelope2D env);

    /**
     * If a node is found in the server, it adds it to the node layer and also adds the related
     * cadastre objects to the target cadastre object layer.
     *
     * @param env
     * @return
     */
    protected final CadastreObjectNodeBean addNodeFromServer(Envelope2D env) {
        CadastreObjectNodeBean nodeBean = this.getNodeFromServer(env);
        if (nodeBean != null) {
            this.cadastreObjectNodeModifiedLayer.addNodeTarget(
                    nodeBean.getId(), nodeBean.getGeom());
            this.cadastreObjectModifiedLayer.addCadastreObjects(nodeBean.getCadastreObjectList());
            this.getMapControl().refresh();
        }
        return nodeBean;
    }

    /**
     * It starts the manipulation of a node. It starts a popup form where the existing coordinates
     * are shown and that can be changed. If the node is found in less than 3 cadastre objects, also
     * the remove option is available.
     *
     * @param nodeFeature The node to manipulate
     */
    protected final boolean manipulateNode(SimpleFeature nodeFeature) {
        boolean manipulationHappen = true;
        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        this.form.setStatus(CadastreRedefinitionNodeModifyForm.Status.DoNothing);
        this.form.setCoordinateX(nodeFeatureGeom.getCoordinate().x);
        this.form.setCoordinateY(nodeFeatureGeom.getCoordinate().y);
        this.form.setRemoveButtonVisibility(
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature).size() < 3);
        this.form.setVisible(true);
        if (this.form.getStatus() == CadastreRedefinitionNodeModifyForm.Status.ModifyNode) {
            manipulationHappen = this.modifyNode(
                    nodeFeature, this.form.getCoordinateX(), this.form.getCoordinateY());
        } else if (this.form.getStatus() == CadastreRedefinitionNodeModifyForm.Status.RemoveNode) {
            manipulationHappen = this.removeNode(nodeFeature);
        } else {
            manipulationHappen = false;
        }
        return manipulationHappen;
    }

    /**
     * Gets the first found node feature
     *
     * @param env
     * @return
     */
    protected final SimpleFeature getFirstNodeFeature(Envelope2D env) {
        return this.cadastreObjectNodeModifiedLayer.getFirstFeatureInRange(
                new ReferencedEnvelope(env));
    }

    /**
     * It modifies the node by changing its coordinates. This will bring changes to cadastre objects
     * that share this node. If the changing of this node, brings the cadastre objects in the
     * original situation, it will be removed, because nothing is changed.
     *
     * @param nodeFeature The node feature
     * @param newCoordinateX The new coordinate X
     * @param newCoordinateY The new coordinate Y
     * @return True if the change happen
     */
    private boolean modifyNode(
            SimpleFeature nodeFeature,
            Double newCoordinateX, Double newCoordinateY) {
        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        Coordinate existingCoordinate = new Coordinate(
                nodeFeatureGeom.getCoordinate().x, nodeFeatureGeom.getCoordinate().y);

        List<SimpleFeature> cadastreObjects =
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature);

        Map<String, Geometry> backup = new HashMap<String, Geometry>();
        boolean success = true;
        for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
            backup.put(
                    cadastreObjectFeature.getID(),
                    (Geometry) cadastreObjectFeature.getDefaultGeometry());
            Geometry cadastreObjectGeom =
                    (Geometry) ((Geometry) cadastreObjectFeature.getDefaultGeometry()).clone();
            com.vividsolutions.jts.geom.CoordinateList coordinates =
                    new CoordinateList(cadastreObjectGeom.getCoordinates(), false);

            Coordinate coordinate;
            for (Object coordinateObj : coordinates) {
                coordinate = (Coordinate) coordinateObj;
                if (coordinate.equals2D(existingCoordinate)) {
                    coordinate.x = newCoordinateX;
                    coordinate.y = newCoordinateY;
                }
            }
            success = this.cadastreObjectModifiedLayer.replaceFeatureGeometry(
                    cadastreObjectFeature, cadastreObjectGeom);
            if (!success) {
                break;
            }
        }

        if (success) {
            nodeFeatureGeom.getCoordinate().x = newCoordinateX;
            nodeFeatureGeom.getCoordinate().y = newCoordinateY;
            nodeFeatureGeom.geometryChanged();
            this.removeIfNodeNotUsed(nodeFeature);
            this.getMapControl().refresh();
        } else {
            for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
                if (backup.containsKey(cadastreObjectFeature.getID())) {
                    cadastreObjectFeature.setDefaultGeometry(
                            backup.get(cadastreObjectFeature.getID()));
                }
            }
        }
        return success;
    }

    /**
     * It removes a node. Removing the node means that the node will be removed in all cadastre
     * objects that share that node. If the cadastre objects will come to their original shape,
     * those objects will be removed from the list of target cadastre objects.
     *
     * @param nodeFeature
     * @return True if the manipulation happen
     */
    protected final boolean removeNode(SimpleFeature nodeFeature) {
        List<SimpleFeature> cadastreObjects =
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature);

        if (cadastreObjects.size() > 2) {
            throw new RuntimeException(GisMessage.CADASTRE_REDEFINITION_NODE_HAS_MORE_THAN_ONE_CO);
        }

        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        Coordinate coordinate = nodeFeatureGeom.getCoordinate();


        Map<String, Geometry> backup = new HashMap<String, Geometry>();
        boolean success = true;
        for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
            Polygon cadastreObjectGeom = (Polygon) cadastreObjectFeature.getDefaultGeometry();
            backup.put(cadastreObjectFeature.getID(), (Geometry) cadastreObjectGeom.clone());
            LinearRing exteriorRing = this.removeCoordinateFromRing(
                    cadastreObjectGeom.getExteriorRing(), coordinate);

            LinearRing[] interiorRings = new LinearRing[cadastreObjectGeom.getNumInteriorRing()];
            for (int interiorRingIndex = 0;
                    interiorRingIndex < interiorRings.length;
                    interiorRingIndex++) {
                interiorRings[interiorRingIndex] = this.removeCoordinateFromRing(
                        cadastreObjectGeom.getInteriorRingN(interiorRingIndex), coordinate);
            }

            cadastreObjectGeom =
                    this.cadastreObjectModifiedLayer.getGeometryFactory().createPolygon(
                    exteriorRing, interiorRings);
            success = this.cadastreObjectModifiedLayer.replaceFeatureGeometry(
                    cadastreObjectFeature, cadastreObjectGeom);
            if (!success) {
                break;
            }
        }
        if (success) {
            this.cadastreObjectNodeModifiedLayer.removeFeature(nodeFeature.getID());
            this.getMapControl().refresh();
        } else {
            for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
                if (backup.containsKey(cadastreObjectFeature.getID())) {
                    cadastreObjectFeature.setDefaultGeometry(
                            backup.get(cadastreObjectFeature.getID()));
                }
            }
        }

        return success;
    }

    /**
     * Remove a coordinate from a ring
     *
     * @param target The target ring
     * @param coordinate
     * @return The ring without the coordinate
     */
    private LinearRing removeCoordinateFromRing(
            LineString target, Coordinate coordinate) {
        com.vividsolutions.jts.geom.CoordinateList coordinates =
                new CoordinateList(target.getCoordinates(), false);

        while (coordinates.contains(coordinate)) {
            coordinates.remove(coordinate);
        }
        coordinates.closeRing();

        return this.cadastreObjectModifiedLayer.getGeometryFactory().createLinearRing(
                coordinates.toCoordinateArray());
    }

    /**
     * Removes a node if there is no cadastre object connected with it. This situation happen when
     * the cadastre objects are removed from the list of target objects because they did not change
     * from their original shape.
     *
     * @param nodeFeature
     * @return True = if the node is removed
     */
    private boolean removeIfNodeNotUsed(SimpleFeature nodeFeature) {
        boolean objectsAreRemoved = false;
        List<SimpleFeature> cadastreObjects =
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature);

        if (cadastreObjects.isEmpty()) {
            this.cadastreObjectNodeModifiedLayer.removeFeature(nodeFeature.getID());
            objectsAreRemoved = true;
        }
        return objectsAreRemoved;
    }
}
