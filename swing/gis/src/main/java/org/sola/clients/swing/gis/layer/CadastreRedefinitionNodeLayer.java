/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.extended.VertexInformation;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeTargetBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeTargetListBean;

/**
 * Layer that maintains the collection of nodes that are targeted during the
 * cadastre redefinition.
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionNodeLayer extends AbstractSpatialObjectLayer {

    private static final String LAYER_NAME = "modified_nodes";
    private static final String LAYER_STYLE_RESOURCE = "node_modified.xml";
    private CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer;

    /**
     * Constructor
     *
     * @throws InitializeLayerException
     */
    public CadastreRedefinitionNodeLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POINT,
                LAYER_STYLE_RESOURCE, null, CadastreObjectNodeTargetBean.class);
        this.listBean = new CadastreObjectNodeTargetListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
    }

    public void setCadastreObjectModifiedLayer(CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        this.cadastreObjectModifiedLayer = cadastreObjectModifiedLayer;
    }

    @Override
    public List<CadastreObjectNodeTargetBean> getBeanList() {
        return super.getBeanList();
    }

    /**
     * It adds a target node bean from a node bean
     *
     * @param nodeBean
     */
    public void addNodeTarget(CadastreObjectNodeBean nodeBean) {
        CadastreObjectNodeTargetBean bean = new CadastreObjectNodeTargetBean();
        bean.setGeom(nodeBean.getGeom());
        bean.setNodeId(nodeBean.getId());
        this.getBeanList().add(bean);
    }

    /**
     * Gets a feature searching by its bean nodeId property
     *
     * @param nodeId
     * @return
     */
    public SimpleFeature getFeatureByNodeId(String nodeId) {
        for (CadastreObjectNodeTargetBean bean : this.getBeanList()) {
            if (bean.getNodeId().equals(nodeId)) {
                return this.getFeatureCollection().getFeature(bean.getRowId());
            }
        }
        return null;
    }

    /**
     * When the vertex is changed, the parcels that use the node of the vertex are
     * changed as well.
     * @param vertexInformation
     * @param newPosition
     * @return 
     */
    @Override
    public SimpleFeature changeVertex(
            VertexInformation vertexInformation, DirectPosition2D newPosition) {
        SimpleFeature featureNode = vertexInformation.getFeature();
        modifyNode(featureNode, newPosition.x, newPosition.y);
        return featureNode;
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
        public final boolean modifyNode(
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
            this.getFeatureCollection().notifyListeners(
                    nodeFeature, CollectionEvent.FEATURES_CHANGED);
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
            this.removeFeature(nodeFeature.getID(), false);
            objectsAreRemoved = true;
        }
        return objectsAreRemoved;
    }
}
