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
import com.vividsolutions.jts.io.ParseException;
import java.awt.Component;
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
import org.geotools.feature.extended.VertexInformation;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.ui.control.CadastreChangePointSurveyListForm;
import org.sola.common.messaging.GisMessage;

/**
 * Layer of the survey points that is used during the cadastre change
 * 
 * @author Elton Manoku
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
    private Component hostForm = null;

    /**
     * Constructor.
     * @param newCadastreObjectLayer The layer of the new cadastre objects. It is needed in order
     * to do topology checks.
     * 
     * @throws InitializeLayerException 
     */
    public CadastreChangeNewSurveyPointLayer(
            CadastreChangeNewCadastreObjectLayer newCadastreObjectLayer)
            throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POINT,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);

        this.setFilterExpressionForSnapping(String.format("%s=1", LAYER_FIELD_ISBOUNDARY));
        this.newCadastreObjectLayer = newCadastreObjectLayer;
        initializeFormHostingAndEvents();
    }

    /**
     * It initializes event handlers and form hosting
     */
    private void initializeFormHostingAndEvents() {
        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });

        this.tableModel = (DefaultTableModel) this.getHostForm().getTable().getModel();
        this.tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                treatTableChange(e);
            }
        });
    }

    /**
     * Gets the form that is responsible with handling other attributes of features
     * @return 
     */
    public CadastreChangePointSurveyListForm getHostForm() {
        if (this.hostForm == null) {
            this.hostForm = new CadastreChangePointSurveyListForm(this);
        }
        return (CadastreChangePointSurveyListForm) this.hostForm;
    }

    /**
     * Gets the field index in the table of a certain field
     * @param fieldName
     * @return 
     */
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

    /**
     * Gets the list of survey points
     * @return 
     */
    public List<SurveyPointBean> getSurveyPointList() {
        return surveyPointList;
    }

    /**
     * Sets the list of survey points. It is called when the transaction is read from the server
     * 
     * @param surveyPointList 
     */
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
            } catch (ParseException ex) {
                Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_ADDINGPOINT_IN_START);
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGPOINT_IN_START, ex);
            }
        }
    }

    /**
     * Gets the mean of the deviations of survey points from their original location.
     * It is calculated only for linked points
     * @return 
     */
    public Double getMean() {
        Double result = 0.0;
        Double deltaX = 0.0, deltaY = 0.0;
        int totalPoints = 0;
        SimpleFeature currentFeature = null;
        SimpleFeatureIterator iterator = this.getFeatureCollection().features();
        while (iterator.hasNext()) {
            currentFeature = iterator.next();
            if (currentFeature.getAttribute(LAYER_FIELD_ISLINKED).equals(0)) {
                continue;
            }
            totalPoints++;
            Point originalGeometry =
                    (Point) currentFeature.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY);
            Point currentGeometry = (Point) currentFeature.getDefaultGeometry();
            deltaX += Math.abs(originalGeometry.getX() - currentGeometry.getX());
            deltaY += Math.abs(originalGeometry.getY() - currentGeometry.getY());
        }
        this.getFeatureCollection().close(iterator);
        result = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) / totalPoints;
        return result;
    }

    /**
     * Gets the standard deviation of survey points from their original location.
     * It is calculated only for linked points
     * @return 
     */
    public Double getStandardDeviation() {
        Double result = 0.0;
        Double mean = this.getMean();
        Double totalShift = 0.0;
        Integer totalPoints = 0;
        SimpleFeature currentFeature = null;
        SimpleFeatureIterator iterator = this.getFeatureCollection().features();
        while (iterator.hasNext()) {
            currentFeature = iterator.next();
            if (currentFeature.getAttribute(LAYER_FIELD_ISLINKED).equals(0)) {
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

    /**
     * It checks if a point is used in a new cadastre object
     * @param feature
     * @return 
     */
    private boolean pointIsUsedInNewCadastreObject(SimpleFeature feature) {
        Coordinate pointCoord = ((Point) feature.getDefaultGeometry()).getCoordinate();
        for (VertexInformation vertexInformation : this.newCadastreObjectLayer.getVertexList()) {
            if (vertexInformation.getVertex().equals2D(pointCoord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It handles changes in the table where the other attributes are displayed. Some changes
     * in some of the columns of this table, need to be reflected in the feature itself.
     * @param e 
     */
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

    /**
     * It adds a new survey point
     * @param x
     * @param y 
     */
    public void addPoint(Double x, Double y) {
        Point pointGeom = this.getGeometryFactory().createPoint(new Coordinate(x, y));
        this.addFeature(null, pointGeom, null);
        this.getMapControl().refresh();
    }

    /**
     * It adds a new feature of survey point type
     * @param fid This is ignored. The fid is always generated to assure uniqueness and to be
     * human friendly.
     * @param geom
     * @param fieldsWithValues
     * @return 
     */
    @Override
    public SimpleFeature addFeature(
            String fid,
            Geometry geom,
            java.util.HashMap<String, Object> fieldsWithValues) {
        fid = fidGenerator.toString();
        fidGenerator++;
        if (fieldsWithValues == null) {
            fieldsWithValues = new HashMap<String, Object>();
            fieldsWithValues.put(LAYER_FIELD_ISBOUNDARY, 1);
            fieldsWithValues.put(LAYER_FIELD_ISLINKED, 0);
            fieldsWithValues.put(LAYER_FIELD_SHIFT, 0);
            fieldsWithValues.put(LAYER_FIELD_ORIGINAL_GEOMETRY, geom.clone());
        }
        fieldsWithValues.put(LAYER_FIELD_LABEL, fid);
        SimpleFeature addedFeature = super.addFeature(fid, geom, fieldsWithValues);
        return addedFeature;
    }

    /**
     * It removes a feature of survey point type. It is first checked if the point is used
     * in a new cadastre object.
     * @param fid
     * @return 
     */
    @Override
    public SimpleFeature removeFeature(String fid) {
        if (this.pointIsUsedInNewCadastreObject(this.getFeatureCollection().getFeature(fid))) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_POINT_FOUND_IN_PARCEL);
            return null;
        }
        return super.removeFeature(fid);
    }

    /**
     * It changes a survey point. It triggers changes in all vertices that has the same coordinates
     * in the new cadastre objects
     * @param vertexInformation
     * @param newPosition
     * @return 
     */
    @Override
    public SimpleFeature changeVertex(
            VertexInformation vertexInformation, DirectPosition2D newPosition) {
        for (VertexInformation vertexCOInformation : this.newCadastreObjectLayer.getVertexList()) {
            if (vertexInformation.getVertex().distance(vertexCOInformation.getVertex()) <= 0.01) {
                if (this.newCadastreObjectLayer.changeVertex(
                        vertexCOInformation, newPosition) == null) {
                    return null;
                }
            }
        }
        return super.changeVertex(vertexInformation, newPosition);
    }

    /**
     * It handles the changes in the collection of features
     * @param ev 
     */
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

    /**
     * Adds a row in the table from a feature
     * @param feature 
     */
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

    /**
     * Remove a row from the table for a given feature
     * @param feature 
     */
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

    /**
     * This changes the table values if the linked feature is changed. 
     * Only the official area can be changed.
     * @param featureObject 
     */
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

    /**
     * Changes an existing bean from its correspondent feature
     * @param targetBean
     * @param feature 
     */
    private void changeBean(SurveyPointBean targetBean, SimpleFeature feature) {
        targetBean.setId(feature.getID());
        targetBean.setBoundary(feature.getAttribute(LAYER_FIELD_ISBOUNDARY).equals(1));
        targetBean.setLinked(feature.getAttribute(LAYER_FIELD_ISLINKED).equals(1));
        targetBean.setOriginalGeom(wkbWriter.write(
                (Geometry) feature.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY)));
        targetBean.setGeom(wkbWriter.write((Geometry) feature.getDefaultGeometry()));
    }

    /**
     * Gets a new bean from a feature
     * @param feature
     * @return 
     */
    private SurveyPointBean newBean(SimpleFeature feature) {
        SurveyPointBean bean = new SurveyPointBean();
        this.changeBean(bean, feature);
        return bean;
    }

    /**
     * Gets the corresponding bean of a feature
     * 
     * @param feature
     * @return 
     */
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

    /**
     * Gets the shift of a point from its original location
     * @param feature
     * @return 
     */
    private double getPointShift(SimpleFeature feature) {
        return ((Point) feature.getDefaultGeometry()).distance(
                (Geometry) feature.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY));
    }
}
