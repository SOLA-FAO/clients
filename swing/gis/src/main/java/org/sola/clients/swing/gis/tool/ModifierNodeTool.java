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
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.tool.extended.ExtendedDrawRectangle;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreObjectModifiedLayer;
import org.sola.clients.swing.gis.layer.CadastreObjectNodeModifiedLayer;
import org.sola.clients.swing.gis.to.CadastreObjectNodeExtraTO;
import org.sola.clients.swing.gis.ui.control.NodeModifyForm;
import org.sola.common.MappingManager;
import org.sola.common.messaging.GisMessage;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;

/**
 * It starts the process of changing a node in the parcel layer. <br/>
 * 
 * @author Elton Manoku
 */
public abstract class ModifierNodeTool extends ExtendedDrawRectangle {

    protected PojoDataAccess dataAccess;
    protected CadastreObjectModifiedLayer cadastreObjectModifiedLayer;
    protected CadastreObjectNodeModifiedLayer cadastreObjectNodeModifiedLayer;
    private NodeModifyForm form = null;

    public ModifierNodeTool(
            PojoDataAccess dataAccess,
            CadastreObjectNodeModifiedLayer cadastreObjectNodeModifiedLayer,
            CadastreObjectModifiedLayer cadastreObjectModifiedLayer) {
        this.dataAccess = dataAccess;
        this.cadastreObjectModifiedLayer = cadastreObjectModifiedLayer;
        this.cadastreObjectNodeModifiedLayer = cadastreObjectNodeModifiedLayer;
        this.form = new NodeModifyForm();
    }

//    protected final List<CadastreObjectNodeBean> getNodeList(){
//        return this.cadastreObjectNodeModifiedLayer.getNodeList();
//    }
    
    protected abstract CadastreObjectNodeTO getNodeFromServer(Envelope2D env);

    protected final void manipulateNode(SimpleFeature nodeFeature) {
        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        this.form.setStatus(NodeModifyForm.Status.DoNothing);
        this.form.setCoordinateX(nodeFeatureGeom.getCoordinate().x);
        this.form.setCoordinateY(nodeFeatureGeom.getCoordinate().y);
        //CadastreObjectNodeBean nodeBean = this.getNode(nodeFeature);
        this.form.setRemoveButtonVisibility(
                this.cadastreObjectModifiedLayer.getCadastreObjectFeatures(nodeFeature)
                .size() < 3);
        this.form.setVisible(true);
        if (this.form.getStatus() == NodeModifyForm.Status.ModifyNode) {
            this.modifyNode(nodeFeature,
                    this.form.getCoordinateX(), this.form.getCoordinateY());
        } else if (this.form.getStatus() == NodeModifyForm.Status.RemoveNode) {
            this.removeNode(nodeFeature);
        }
    }

    protected final SimpleFeature getFirstNodeFeature(Envelope2D env) {
        SimpleFeature nodeFeature = null;
        FeatureCollection nodeFeatureCollection =
                this.cadastreObjectNodeModifiedLayer.getFeaturesInRange(
                new ReferencedEnvelope(env), null);
        if (nodeFeatureCollection != null) {
            SimpleFeatureIterator featureIterator =
                    (SimpleFeatureIterator) nodeFeatureCollection.features();
            while (featureIterator.hasNext()) {
                nodeFeature = featureIterator.next();
                break;
            }
            featureIterator.close();
        }
        return nodeFeature;
    }

    private void modifyNode(
            SimpleFeature nodeFeature,
            Double newCoordinateX, Double newCoordinateY) {
        //CadastreObjectNodeBean nodeBean = this.getNode(nodeFeature);
        //if (nodeBean == null) {
        //    throw new RuntimeException(GisMessage.CADASTRE_REDEFINITION_NODE_NOTFOUND);
        //}
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
        }
        nodeFeatureGeom.getCoordinate().x = newCoordinateX;
        nodeFeatureGeom.getCoordinate().y = newCoordinateY;
        nodeFeatureGeom.geometryChanged();

        this.getMapControl().refresh();
    }

    private void removeNode(SimpleFeature nodeFeature) {
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
            for (
                    int interiorRingIndex = 0; 
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


//    protected CadastreObjectNodeBean getNode(SimpleFeature fromFeature) {
//        CadastreObjectNodeBean nodeBean = null;
//        int nodeBeanIndex = this.getNodeList().indexOf(
//                new CadastreObjectNodeBean(fromFeature.getID()));
//        if (nodeBeanIndex > -1) {
//            nodeBean = this.getNodeList().get(nodeBeanIndex);
//        }
//        return nodeBean;
//    }
}
