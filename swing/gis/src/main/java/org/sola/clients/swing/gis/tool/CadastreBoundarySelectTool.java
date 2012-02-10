/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.tool.extended.ExtendedDrawRectangle;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * It is used to select a boundary from the target cadastre objects in the targetLayer.
 * 
 * @author Elton Manoku
 */
public class CadastreBoundarySelectTool extends ExtendedDrawRectangle {

    public final static String NAME = "cadastre-boundary-select";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_BOUNDARY_SELECT_TOOL_TOOLTIP).getMessage();
    //private PojoDataAccess dataAccess;
    protected CadastreBoundaryPointLayer pointLayer;
    private ExtendedLayerGraphics targetLayer;
    private ExtendedLayerGraphics targetNodeLayer;
    private int stepNumber = 1;
    protected List<String> targetCadastreObjectIds = new ArrayList<String>();

    public CadastreBoundarySelectTool(
            //PojoDataAccess dataAccess,
            //            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            //            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer,
            CadastreBoundaryPointLayer pointLayer,
            ExtendedLayerGraphics targetLayer,
            ExtendedLayerGraphics targetNodeLayer) {
        //this.dataAccess = dataAccess;
        this.setToolName(NAME);
        this.setToolTip(toolTip);
        this.pointLayer = pointLayer;
        this.targetLayer = targetLayer;
        this.targetNodeLayer = targetNodeLayer;
    }

    protected ExtendedLayerGraphics getTargetLayer() {
        return this.targetLayer;
    }

    @Override
    protected void onRectangleFinished(Envelope2D env) {
        boolean refreshMap = false;
        if (stepNumber == 1) {
            //Select starting point
            this.step1(env);
            if (this.pointLayer.getStartPoint() != null) {
                stepNumber = 2;
                refreshMap = true;
            }
        } else if (stepNumber == 2) {
            //Select ending point
            this.step2(env);
            if (this.pointLayer.getEndPoint() != null) {
                stepNumber = 1;
            }
            refreshMap = true;
        }
        if (refreshMap) {
            this.getMapControl().refresh();
        }
    }

    protected final SimpleFeature getFirstPointFeature(Envelope2D env) {
        return this.targetNodeLayer.getFirstFeatureInRange(new ReferencedEnvelope(env));
    }

    protected void step1(Envelope2D env) {
        this.clearSelection();
        SimpleFeature pointFeature = this.getFirstPointFeature(env);
        if (pointFeature != null) {
            this.pointLayer.setStartPoint((Point) pointFeature.getDefaultGeometry());
            this.targetCadastreObjectIds.addAll(
                    this.getCadastreObjectTargetIdsFromNodeFeature(pointFeature));

        }
    }

    protected boolean step2(Envelope2D env) {
        SimpleFeature pointFeature = this.getFirstPointFeature(env);
        if (pointFeature == null) {
            return false;
        }
        List<String> targetCadastreObjectIdsTmp = new ArrayList<String>();
        List<String> targetIds = getCadastreObjectTargetIdsFromNodeFeature(pointFeature);
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
            this.pointLayer.setEndPoint((Point) pointFeature.getDefaultGeometry());
            this.defineTargetBoundary();
        }
        return true;

    }

    protected final boolean defineTargetBoundary() {
        boolean clockwise = true;
        Coordinate startCoordinate = this.pointLayer.getStartPoint().getCoordinate();
        Coordinate endCoordinate = this.pointLayer.getEndPoint().getCoordinate();
        Polygon targetGeom =
                (Polygon) this.targetLayer.getFeatureCollection().getFeature(
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

    public final void clearSelection() {
        try {
            this.pointLayer.clearSelection();
            SimpleFeature feature;
            for (String cadastreObjectId : this.targetCadastreObjectIds) {
                feature = this.targetLayer.getFeatureCollection().getFeature(
                        cadastreObjectId);
                if (feature != null) {
                    this.targetLayer.getFeatureCollection().notifyListeners(
                            feature, CollectionEvent.FEATURES_CHANGED);
                }
            }
            this.targetCadastreObjectIds.clear();
            this.stepNumber = 1;
            //Disactivate editing of boundary
            this.getMapControl().getMapActionByName(CadastreBoundaryEditTool.NAME).setEnabled(false);
        } catch (Exception ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_BOUNDARY_CLEAR_SELECTION_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_BOUNDARY_CLEAR_SELECTION_ERROR);
        }
    }

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

        return this.targetLayer.getGeometryFactory().createLineString(
                boundaryCoordList.toCoordinateArray());
    }

    private List<String> getCadastreObjectTargetIdsFromNodeFeature(SimpleFeature nodeFeature) {
        List<String> ids = new ArrayList<String>();
        ReferencedEnvelope filterBbox = new ReferencedEnvelope(nodeFeature.getBounds());
        filterBbox.expandBy(0.01, 0.01);
        FeatureCollection featureCollection =
                this.getTargetLayer().getFeaturesInRange(filterBbox, null);
        SimpleFeatureIterator iterator = (SimpleFeatureIterator) featureCollection.features();
        while (iterator.hasNext()) {
            ids.add(iterator.next().getID());
        }
        iterator.close();
        return ids;
    }
}
