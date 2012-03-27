/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
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
 *
 * @author Elton Manoku
 */
public abstract class CadastreRedefinitionAbstractNodeTool extends ExtendedDrawRectangle {

    private CadastreRedefinitionNodeModifyForm form = null;
    protected PojoDataAccess dataAccess;
    protected CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer;
    protected CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer;

    public CadastreRedefinitionAbstractNodeTool(
            PojoDataAccess dataAccess,
            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        this.dataAccess = dataAccess;
        this.cadastreObjectModifiedLayer = cadastreObjectModifiedLayer;
        this.cadastreObjectNodeModifiedLayer = cadastreObjectNodeModifiedLayer;
        this.form = new CadastreRedefinitionNodeModifyForm();
    }

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

    protected abstract CadastreObjectNodeBean getNodeFromServer(Envelope2D env);

    protected final CadastreObjectNodeBean addNodeFromServer(Envelope2D env) {
        CadastreObjectNodeBean nodeBean = this.getNodeFromServer(env);
        if (nodeBean != null){
            this.cadastreObjectNodeModifiedLayer.addNodeTarget(
                    nodeBean.getId(), nodeBean.getGeom());
            this.cadastreObjectModifiedLayer.addCadastreObjects(nodeBean.getCadastreObjectList());
            this.getMapControl().refresh();
        }
        return nodeBean;
    }

    /**
     * It starts the manipulation of a node. It starts a popup form where the existing coordinates
     * are shown and that can be changed.
     * If the node is found in less than 3 cadastre objects, also the remove option is available.
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
            this.modifyNode(nodeFeature, this.form.getCoordinateX(), this.form.getCoordinateY());
        } else if (this.form.getStatus() == CadastreRedefinitionNodeModifyForm.Status.RemoveNode) {
            this.removeNode(nodeFeature);
        } else {
            manipulationHappen = false;
        }
        return manipulationHappen;
    }

    protected final SimpleFeature getFirstNodeFeature(Envelope2D env) {
        return this.cadastreObjectNodeModifiedLayer.getFirstFeatureInRange(
                new ReferencedEnvelope(env));
    }

    private void modifyNode(
            SimpleFeature nodeFeature,
            Double newCoordinateX, Double newCoordinateY) {
        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        Coordinate existingCoordinate = new Coordinate(
                nodeFeatureGeom.getCoordinate().x, nodeFeatureGeom.getCoordinate().y);

        List<SimpleFeature> cadastreObjects =
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature);

        for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
            Geometry cadastreObjectGeom = (Geometry) cadastreObjectFeature.getDefaultGeometry();
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
            cadastreObjectGeom.geometryChanged();
            this.cadastreObjectModifiedLayer.getFeatureCollection().notifyListeners(
                    cadastreObjectFeature, CollectionEvent.FEATURES_CHANGED);
        }


        nodeFeatureGeom.getCoordinate().x = newCoordinateX;
        nodeFeatureGeom.getCoordinate().y = newCoordinateY;
        nodeFeatureGeom.geometryChanged();

        this.removeIfNodeNotUsed(nodeFeature);
        this.getMapControl().refresh();
    }

    protected final void removeNode(SimpleFeature nodeFeature) {
        List<SimpleFeature> cadastreObjects =
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature);

        if (cadastreObjects.size() > 2) {
            throw new RuntimeException(GisMessage.CADASTRE_REDEFINITION_NODE_HAS_MORE_THAN_ONE_CO);
        }

        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        Coordinate coordinate = nodeFeatureGeom.getCoordinate();

        for (SimpleFeature cadastreObjectFeature : cadastreObjects) {
            Polygon cadastreObjectGeom = (Polygon) cadastreObjectFeature.getDefaultGeometry();
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
            cadastreObjectFeature.setDefaultGeometry(cadastreObjectGeom);
            cadastreObjectGeom.geometryChanged();
            this.cadastreObjectModifiedLayer.getFeatureCollection().notifyListeners(
                    cadastreObjectFeature, CollectionEvent.FEATURES_CHANGED);
        }
        this.cadastreObjectNodeModifiedLayer.removeFeature(nodeFeature.getID());
        this.getMapControl().refresh();
    }

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
