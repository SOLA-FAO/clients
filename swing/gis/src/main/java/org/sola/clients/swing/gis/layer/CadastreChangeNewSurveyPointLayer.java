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
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.SurveyPointBean;
import org.geotools.map.extended.layer.ExtendedLayerEditor;
import org.geotools.map.extended.layer.VertexInformation;
import org.sola.clients.swing.gis.ui.control.CadastreChangePointSurveyListForm;
import org.sola.common.messaging.GisMessage;

/**
 *
 * @author Manoku
 */
public class CadastreChangeNewSurveyPointLayer extends ExtendedLayerEditor {

    private static final String LAYER_NAME = "New points";
    private static final String LAYER_STYLE_RESOURCE = "cadastrechange_newpoints.xml";
    public static final String LAYER_FIELD_FID = "fid";
    private static final String LAYER_FIELD_LABEL = "label";
    public static final String LAYER_FIELD_ISBOUNDARY = "is_boundary";
    public static final String LAYER_FIELD_ISLINKED = "is_linked";
    public static final String LAYER_FIELD_SHIFT = "shift";
    private static final String LAYER_FIELD_ORIGINAL_GEOMETRY = "original_geometry";
    private static final String LAYER_ATTRIBUTE_DEFINITION = 
            String.format("%s:\"\",%s:0,%s:0,%s:0.0,%s:Point",
            LAYER_FIELD_LABEL, LAYER_FIELD_ISBOUNDARY, LAYER_FIELD_ISLINKED,
            LAYER_FIELD_SHIFT, LAYER_FIELD_ORIGINAL_GEOMETRY);
    private Integer fidGenerator = 1;
    private DefaultTableModel tableModel = null;
    private CadastreChangeNewCadastreObjectLayer newCadastreObjectLayer = null;
    private List<SurveyPointBean> surveyPointList = new ArrayList<SurveyPointBean>();

    private CadastreChangePointSurveyListForm hostForm = null;
            
    public CadastreChangeNewSurveyPointLayer(
            CadastreChangeNewCadastreObjectLayer newCadastreObjectLayer)
            throws Exception {
        super(LAYER_NAME, Geometries.POINT,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);

        this.setFilterExpressionForSnapping(String.format("%s=1", LAYER_FIELD_ISBOUNDARY));
        this.newCadastreObjectLayer = newCadastreObjectLayer;
        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });
        
        this.hostForm = new CadastreChangePointSurveyListForm(this);
        this.tableModel = (DefaultTableModel) this.hostForm.getTable().getModel();
        this.tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                treatTableChange(e);
            }
        });
    }

    public CadastreChangePointSurveyListForm getHostForm(){
        return this.hostForm;
    }
    
    public int getFieldIndex(String fieldName) {
        if (fieldName.equals(LAYER_FIELD_FID)) {
            return 0;
        } else if (fieldName.equals(LAYER_FIELD_LABEL)) {
            return 0;
        } else if (fieldName.equals(LAYER_FIELD_ISBOUNDARY)) {
            return 3;
        } else if (fieldName.equals(LAYER_FIELD_ISLINKED)) {
            return 4;
        } else if (fieldName.equals(LAYER_FIELD_SHIFT)) {
            return 5;
        }
        return -1;
    }

    public List<SurveyPointBean> getSurveyPointList() {
        return surveyPointList;
    }

    public void setSurveyPointList(List<SurveyPointBean> surveyPointList) {
        if (!surveyPointList.isEmpty()) {
            try {
                for (SurveyPointBean surveyPointBean : surveyPointList) {
                    HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
                    fieldsWithValues.put(LAYER_FIELD_LABEL, surveyPointBean.getId());
                    fieldsWithValues.put(LAYER_FIELD_ISBOUNDARY, 
                            surveyPointBean.isBoundary() ? 1 : 0);
                    fieldsWithValues.put(LAYER_FIELD_ISLINKED, 0);
                    fieldsWithValues.put(LAYER_FIELD_SHIFT, 0);
                    fieldsWithValues.put(
                            LAYER_FIELD_ORIGINAL_GEOMETRY,
                            wkbReader.read(surveyPointBean.getOriginalGeom()));
                    SimpleFeature feature = this.addFeature(
                            surveyPointBean.getId(), surveyPointBean.getGeom(), fieldsWithValues);
                    feature.setAttribute(LAYER_FIELD_SHIFT, this.getPointShift(feature));
                }
            } catch (Exception ex) {
                Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_ADDINGPOINT_IN_START);
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGPOINT_IN_START, ex);
            }
        }
    }

    public Double getMean() {
        Double result = 0.0;
        Double deltaX = 0.0, deltaY = 0.0;
        int totalPoints = 0;
        SimpleFeature currentFeature = null;
        SimpleFeatureIterator iterator = this.getFeatureCollection().features();
        while (iterator.hasNext()) {
            currentFeature = iterator.next();
            if (currentFeature.getAttribute(LAYER_FIELD_ISLINKED).equals(0)){
                continue;
            }
            totalPoints++;
            Point originalGeometry = (Point) currentFeature.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY);
            Point currentGeometry = (Point) currentFeature.getDefaultGeometry();
            deltaX += Math.abs(originalGeometry.getX() - currentGeometry.getX());
            deltaY += Math.abs(originalGeometry.getY() - currentGeometry.getY());
        }
        this.getFeatureCollection().close(iterator);
        result = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))/totalPoints;
        return result;
    }

    public Double getStandardDeviation() {
        Double result = 0.0;
        Double mean = this.getMean();
        Double totalShift = 0.0;
        Integer totalPoints = 0;
        SimpleFeature currentFeature = null;
        SimpleFeatureIterator iterator = this.getFeatureCollection().features();
        while (iterator.hasNext()) {
            currentFeature = iterator.next();
            if (currentFeature.getAttribute(LAYER_FIELD_ISLINKED).equals(0)){
                continue;
            }
            Double shift = Double.parseDouble(
                    currentFeature.getAttribute(LAYER_FIELD_SHIFT).toString());
            totalShift += Math.pow(shift - mean, 2);
            totalPoints++;
        }
        this.getFeatureCollection().close(iterator);
        result = Math.sqrt(totalShift / totalPoints);
        return result;
    }

    private boolean pointIsUsedInNewParcel(SimpleFeature feature) {
        Coordinate pointCoord = ((Point) feature.getDefaultGeometry()).getCoordinate();
        for (VertexInformation vertexInformation : this.newCadastreObjectLayer.getVertexList()) {
            if (vertexInformation.getVertex().equals2D(pointCoord)) {
                return true;
            }
        }
        return false;
    }

    private void treatTableChange(TableModelEvent e) {
        int rowIndex = e.getFirstRow();
        int colIndex = e.getColumn();
        if (colIndex == -1) {
            //It means is new row inserted. Column not defined
            return;
        }
        Object data = this.tableModel.getValueAt(rowIndex, colIndex);
        String fid = this.tableModel.getValueAt(
                rowIndex, this.getFieldIndex(LAYER_FIELD_FID)).toString();
        SimpleFeature pointObject = this.getFeatureCollection().getFeature(fid);
        boolean somethingChanged = false;
        if (this.getFieldIndex(LAYER_FIELD_ISBOUNDARY) == colIndex) {
            pointObject.setAttribute(LAYER_FIELD_ISBOUNDARY, data.equals(Boolean.TRUE) ? 1 : 0);
            somethingChanged = true;
        } else if (this.getFieldIndex(LAYER_FIELD_ISLINKED) == colIndex) {
            if (data.equals(Boolean.TRUE)) {
                if (pointObject.getAttribute(LAYER_FIELD_ISLINKED).equals(0)) {
                    this.tableModel.setValueAt(Boolean.FALSE, rowIndex, colIndex);
                }
            } else if (pointObject.getAttribute(LAYER_FIELD_ISLINKED).equals(1)) {
                pointObject.setAttribute(LAYER_FIELD_ISLINKED, 0);
                Point originalGeometry = 
                        (Point) pointObject.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY);
                Point currentGeometry = (Point) pointObject.getDefaultGeometry();
                currentGeometry.getCoordinate().x = originalGeometry.getX();
                currentGeometry.getCoordinate().y = originalGeometry.getY();
                currentGeometry.geometryChanged();
                somethingChanged = true;
            }
        }
        if (somethingChanged) {
            this.getFeatureCollection().notifyListeners(
                    pointObject, CollectionEvent.FEATURES_CHANGED);
            this.getMapControl().refresh();

        }
    }

    public void addPoint(Double x, Double y) {
        Point pointGeom = this.getGeometryFactory().createPoint(new Coordinate(x, y));
        try {
            this.addFeature(null, pointGeom, null);
            this.getMapControl().refresh();
        } catch (Exception ex) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_ADDING_POINT);

            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_CHANGE_ERROR_ADDING_POINT, ex);
        }

    }

    @Override
    public SimpleFeature addFeature(
            String fid,
            Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues) throws Exception {
        fid = fidGenerator.toString();
        fidGenerator++;
        if (fieldsWithValues == null) {
            fieldsWithValues = new HashMap<String, Object>();
            fieldsWithValues.put(LAYER_FIELD_LABEL, fid);
            fieldsWithValues.put(LAYER_FIELD_ISBOUNDARY, 1);
            fieldsWithValues.put(LAYER_FIELD_ISLINKED, 0);
            fieldsWithValues.put(LAYER_FIELD_SHIFT, 0);
            fieldsWithValues.put(LAYER_FIELD_ORIGINAL_GEOMETRY, geom.clone());
        }
        SimpleFeature addedFeature = super.addFeature(fid, geom, fieldsWithValues);
        return addedFeature;
    }

    @Override
    public SimpleFeature removeFeature(String fid) {
        if (this.pointIsUsedInNewParcel(this.getFeatureCollection().getFeature(fid))) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_POINT_FOUND_IN_PARCEL);
            return null;
        }
        return super.removeFeature(fid);
    }

    @Override
    public SimpleFeature changeVertex(VertexInformation vertexInformation, DirectPosition2D newPosition) {
        for (VertexInformation vertexCOInformation : this.newCadastreObjectLayer.getVertexList()) {
            if (vertexInformation.getVertex().distance(vertexCOInformation.getVertex()) <= 0.01) {
                if (this.newCadastreObjectLayer.changeVertex(vertexCOInformation, newPosition) == null) {
                    return null;
                }
            }
        }
        return super.changeVertex(vertexInformation, newPosition);
    }

    private void featureCollectionChanged(CollectionEvent ev) {
        if (ev.getFeatures() == null) {
            return;
        }
        if (ev.getEventType() == CollectionEvent.FEATURES_ADDED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                this.getSurveyPointList().add(this.newBean(feature));
                addInTable(feature);
            }
        } else if (ev.getEventType() == CollectionEvent.FEATURES_REMOVED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                SurveyPointBean found = this.getBean(feature);
                if (found != null) {
                    this.getSurveyPointList().remove(found);
                }
                this.removeInTable(feature);
            }
        } else if (ev.getEventType() == CollectionEvent.FEATURES_CHANGED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                feature.setAttribute(LAYER_FIELD_SHIFT, this.getPointShift(feature));
                changeInTable(feature);
                SurveyPointBean found = this.getBean(feature);
                if (found != null) {
                    this.changeBean(found, feature);
                }
            }
        }
    }

    private void addInTable(SimpleFeature pointObject) {
        if (this.tableModel == null) {
            return;
        }
        Object[] row = new Object[6];
        row[0] = pointObject.getID();
        row[1] = ((Point) pointObject.getDefaultGeometry()).getX();
        row[2] = ((Point) pointObject.getDefaultGeometry()).getY();
        row[3] = pointObject.getAttribute(LAYER_FIELD_ISBOUNDARY).equals(1);
        row[4] = pointObject.getAttribute(LAYER_FIELD_ISLINKED).equals(1);
        row[5] = pointObject.getAttribute(LAYER_FIELD_SHIFT);
        tableModel.addRow(row);
    }

    private void removeInTable(SimpleFeature feature) {
        if (this.tableModel == null) {
            return;
        }
        for (int rowIndex = 0; rowIndex < this.tableModel.getRowCount(); rowIndex++) {
            String fid = this.tableModel.getValueAt(
                    rowIndex, this.getFieldIndex(LAYER_FIELD_FID)).toString();
            if (feature.getID().equals(fid)) {
                this.tableModel.removeRow(rowIndex);
                break;
            }
        }
    }

    private void changeInTable(SimpleFeature featureObject) {
        if (this.tableModel == null) {
            return;
        }
        for (int rowIndex = 0; rowIndex < this.tableModel.getRowCount(); rowIndex++) {
            String fid = this.tableModel.getValueAt(
                    rowIndex, this.getFieldIndex(LAYER_FIELD_FID)).toString();
            if (featureObject.getID().equals(fid)) {
                this.tableModel.setValueAt(
                        featureObject.getAttribute(LAYER_FIELD_ISLINKED).equals(1),
                        rowIndex, this.getFieldIndex(LAYER_FIELD_ISLINKED));
                this.tableModel.setValueAt(
                        featureObject.getAttribute(LAYER_FIELD_SHIFT),
                        rowIndex, this.getFieldIndex(LAYER_FIELD_SHIFT));
                break;
            }
        }
    }

    private void changeBean(SurveyPointBean targetBean, SimpleFeature feature) {
        targetBean.setId(feature.getID());
        targetBean.setBoundary(feature.getAttribute(LAYER_FIELD_ISBOUNDARY).equals(1));
        targetBean.setLinked(feature.getAttribute(LAYER_FIELD_ISLINKED).equals(1));
        targetBean.setOriginalGeom(wkbWriter.write(
                (Geometry) feature.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY)));
        targetBean.setGeom(wkbWriter.write((Geometry) feature.getDefaultGeometry()));
    }

    private SurveyPointBean newBean(SimpleFeature feature) {
        SurveyPointBean bean = new SurveyPointBean();
        this.changeBean(bean, feature);
        return bean;
    }
        private SurveyPointBean getBean(SimpleFeature feature) {
        SurveyPointBean bean = new SurveyPointBean();
        bean.setId(feature.getID());
        int foundIndex = this.getSurveyPointList().indexOf(bean);
        if (foundIndex > -1) {
            bean = this.getSurveyPointList().get(foundIndex);
        } else {
            bean = null;
        }
        return bean;
    }
    
    private double getPointShift(SimpleFeature feature){
        return ((Point) feature.getDefaultGeometry()).distance(
                (Geometry) feature.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY));
    }
}
