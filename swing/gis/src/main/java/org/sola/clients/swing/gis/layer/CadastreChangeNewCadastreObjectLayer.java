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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.SpatialValueAreaBean;
import org.geotools.map.extended.layer.ExtendedLayerEditor;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.ui.control.CadastreChangeNewCadastreObjectListForm;
import org.sola.common.messaging.GisMessage;

/**
 * Layer that has the features of the new cadastre objects during the cadastre change
 * 
 * @author Elton Manoku
 */
public class CadastreChangeNewCadastreObjectLayer extends ExtendedLayerEditor{

    public static final String LAYER_FIELD_FID = "fid";
    public static final String LAYER_FIELD_FIRST_PART = "first_part";
    public static final String LAYER_FIELD_LAST_PART = "last_part";
    public static final String LAYER_FIELD_OFFICIAL_AREA = "official_area";
    private static final String LAYER_NAME = "New Parcels";
    private static final String LAYER_STYLE_RESOURCE = "parcel_new.xml";
    private static final String LAYER_ATTRIBUTE_DEFINITION = String.format("%s:\"\",%s:\"\",%s:0",
            LAYER_FIELD_FIRST_PART, LAYER_FIELD_LAST_PART, LAYER_FIELD_OFFICIAL_AREA);
    private List<CadastreObjectBean> cadastreObjectList = new ArrayList<CadastreObjectBean>();
    private Integer firstPartGenerator = 1;
    private Integer fidGenerator = 1;
    private static final String LAST_PART_FORMAT = "SP %s";
    private String lastPart = "";
    private DefaultTableModel tableModel = null;
    private Component hostForm = null;

    /**
     * Constructor for the layer. 
     * 
     * @param applicationNumber The application number of the service that starts the 
     * transaction where the layer is used. This number is used in the definition of new parcel
     * number
     * @throws InitializeLayerException 
     */
    public CadastreChangeNewCadastreObjectLayer(String applicationNumber) 
            throws InitializeLayerException{
        super(LAYER_NAME, Geometries.POLYGON,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
        this.lastPart = String.format(LAST_PART_FORMAT, applicationNumber);
        initializeFormHostingAndEvents(new CadastreChangeNewCadastreObjectListForm(this));
    }
    
    /**
     * It initializes event handlers and form hosting
     */
    private void initializeFormHostingAndEvents(Component hostForm){
        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });
        
        this.hostForm = (JDialog)hostForm;
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
    public CadastreChangeNewCadastreObjectListForm getHostForm() {
        if (this.hostForm == null){
            this.hostForm = new CadastreChangeNewCadastreObjectListForm(this);
        }
        return (CadastreChangeNewCadastreObjectListForm)this.hostForm;
    }

    /**
     * Gets the field index in the table of a certain field
     * @param fieldName
     * @return 
     */
    public int getFieldIndex(String fieldName) {
        if (fieldName.equals(LAYER_FIELD_FID)) {
            return 0;
        }
        if (fieldName.equals(LAYER_FIELD_FIRST_PART)) {
            return 1;
        } else if (fieldName.equals(LAYER_FIELD_LAST_PART)) {
            return 2;
        } else if (fieldName.equals(LAYER_FIELD_OFFICIAL_AREA)) {
            return 3;
        }
        return -1;
    }

    /**
     * Gets the list of new cadastre objects
     * @return 
     */
    public List<CadastreObjectBean> getCadastreObjectList() {
        return cadastreObjectList;
    }

    /**
     * Sets the list of new cadastre objects. This is used if the transaction is read from
     * the server.
     * 
     * @param cadastreObjectList 
     */
    public void setCadastreObjectList(List<CadastreObjectBean> cadastreObjectList) {
        if (!cadastreObjectList.isEmpty()) {
            try {
                for (CadastreObjectBean cadastreObjectBean : cadastreObjectList) {
                    HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
                    fieldsWithValues.put(
                            LAYER_FIELD_FIRST_PART,cadastreObjectBean.getNameFirstpart());
                    fieldsWithValues.put(
                            LAYER_FIELD_LAST_PART, cadastreObjectBean.getNameLastpart());
                    if (cadastreObjectBean.getSpatialValueAreaList().size() > 0) {
                        for (SpatialValueAreaBean areaValueBean :
                                cadastreObjectBean.getSpatialValueAreaList()) {
                            if (areaValueBean.getTypeCode().equals(
                                    SpatialValueAreaBean.TYPE_OFFICIAL)) {
                                fieldsWithValues.put(
                                        LAYER_FIELD_OFFICIAL_AREA, 
                                        areaValueBean.getSize().intValue());
                                break;
                            }
                        }
                    }
                    this.addFeature(null, cadastreObjectBean.getGeomPolygon(), fieldsWithValues);
                }
            } catch (ParseException ex) {
                Messaging.getInstance().show(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGNEWCADASTREOBJECT_IN_START);
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGNEWCADASTREOBJECT_IN_START, ex);
            }
        }
    }

    /**
     * It adds a feature of the cadastre object.
     * The fid is not taken into account. It is always regenerated in the ordinal number.
     * This is to assure the uniqueness and to have it also user friendly.
     * @param fid
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
            fieldsWithValues.put(CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_FIRST_PART,
                    firstPartGenerator.toString());
            fieldsWithValues.put(CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_LAST_PART, 
                    this.lastPart);
            fieldsWithValues.put(CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_OFFICIAL_AREA, 
                    Math.round(geom.getArea()));
        }
        SimpleFeature addedFeature = super.addFeature(fid, geom, fieldsWithValues);
        firstPartGenerator++;
        return addedFeature;
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
            //It means is new row inserted / deleted. Column not defined
            return;
        }
        Object data = this.tableModel.getValueAt(rowIndex, colIndex);
        String fid = this.tableModel.getValueAt(
                rowIndex, this.getFieldIndex(LAYER_FIELD_FID)).toString();
        SimpleFeature feature = this.getFeatureCollection().getFeature(fid);
        boolean somethingChanged = false;
        if (this.getFieldIndex(LAYER_FIELD_FIRST_PART) == colIndex
                && !feature.getAttribute(LAYER_FIELD_FIRST_PART).equals(data)) {
            feature.setAttribute(LAYER_FIELD_FIRST_PART, data);
            somethingChanged = true;
        } else if (this.getFieldIndex(LAYER_FIELD_OFFICIAL_AREA) == colIndex
                && !feature.getAttribute(LAYER_FIELD_OFFICIAL_AREA).equals(data)) {
            feature.setAttribute(LAYER_FIELD_OFFICIAL_AREA, Double.parseDouble(data.toString()));
            somethingChanged = true;
        }
        if (somethingChanged) {
            this.getFeatureCollection().notifyListeners(feature, CollectionEvent.FEATURES_CHANGED);
            this.getMapControl().refresh();
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
                        featureObject.getAttribute(LAYER_FIELD_OFFICIAL_AREA),
                        rowIndex, this.getFieldIndex(LAYER_FIELD_OFFICIAL_AREA));
                break;
            }
        }
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
                this.getCadastreObjectList().add(this.newBean(feature));
                this.addInTable(feature);
            }
        } else if (ev.getEventType() == CollectionEvent.FEATURES_REMOVED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                CadastreObjectBean found = this.getBean(feature);
                if (found != null) {
                    this.getCadastreObjectList().remove(found);
                }
                this.removeInTable(feature);
            }
        } else if (ev.getEventType() == CollectionEvent.FEATURES_CHANGED) {
            for (SimpleFeature feature : ev.getFeatures()) {
                CadastreObjectBean found = this.getBean(feature);
                if (found != null) {
                    BigDecimal oldCalculatedArea = this.getSpatialValueBean(found, false).getSize();
                    BigDecimal newCalculatedArea = 
                            BigDecimal.valueOf(((Geometry) feature.getDefaultGeometry()).getArea());
                    
                    if (oldCalculatedArea.compareTo(newCalculatedArea) != 0){
                        feature.setAttribute(LAYER_FIELD_OFFICIAL_AREA, 
                                newCalculatedArea.longValue());
                        this.changeInTable(feature);
                    }
                    this.changeBean(found, feature);
                }
            }
        }
    }

    /**
     * Gets a new bean from a feature
     * @param feature
     * @return 
     */
    private CadastreObjectBean newBean(SimpleFeature feature) {
        CadastreObjectBean coBean = new CadastreObjectBean();
        this.changeBean(coBean, feature);
        return coBean;

    }

    /**
     * Changes an existing bean from its correspondent feature
     * @param targetBean
     * @param feature 
     */
    private void changeBean(CadastreObjectBean targetBean, SimpleFeature feature) {
        targetBean.setId(feature.getID());
        targetBean.setNameFirstpart(feature.getAttribute(
                CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_FIRST_PART).toString());
        targetBean.setNameLastpart(feature.getAttribute(
                CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_LAST_PART).toString());
        SpatialValueAreaBean spatialValueBean;
        SpatialValueAreaBean calculatedAreaBean;
        if (targetBean.getSpatialValueAreaList().isEmpty()) {
            spatialValueBean = new SpatialValueAreaBean();
            targetBean.getSpatialValueAreaList().add(spatialValueBean);
            calculatedAreaBean = new SpatialValueAreaBean();
            calculatedAreaBean.setTypeCode(SpatialValueAreaBean.TYPE_CALCULATED);
            targetBean.getSpatialValueAreaList().add(calculatedAreaBean);
        } else {
            spatialValueBean = this.getSpatialValueBean(targetBean, true);
            calculatedAreaBean = this.getSpatialValueBean(targetBean, false);
        }
        spatialValueBean.setSize(BigDecimal.valueOf(Double.parseDouble(feature.getAttribute(
                CadastreChangeNewCadastreObjectLayer.LAYER_FIELD_OFFICIAL_AREA).toString())));
        calculatedAreaBean.setSize(BigDecimal.valueOf(
                ((Polygon) feature.getDefaultGeometry()).getArea()));
        targetBean.setGeomPolygon(wkbWriter.write((Geometry) feature.getDefaultGeometry()));
    }
    
    /**
     * Gets the spatial value bean of a cadastre object bean
     * @param coBean Cadastre object bean
     * @param officialValue The type of spatial value. True = official, False = calculated
     * @return 
     */
    private SpatialValueAreaBean getSpatialValueBean(
            CadastreObjectBean coBean, boolean officialValue){
        if (officialValue){
            return coBean.getSpatialValueAreaList().get(0);
        }
        return coBean.getSpatialValueAreaList().get(1);
    }

    /**
     * Gets the corresponding bean of a feature
     * 
     * @param feature
     * @return 
     */
    private CadastreObjectBean getBean(SimpleFeature feature) {
        CadastreObjectBean coBean = new CadastreObjectBean();
        coBean.setId(feature.getID());
        int foundIndex = this.getCadastreObjectList().indexOf(coBean);
        if (foundIndex > -1) {
            coBean = this.getCadastreObjectList().get(foundIndex);
        } else {
            coBean = null;
        }
        return coBean;
    }

    /**
     * Adds a row in the table from a feature
     * @param feature 
     */
    private void addInTable(SimpleFeature feature) {
        if (this.tableModel == null) {
            return;
        }
        Object[] row = new Object[4];
        row[0] = feature.getID();
        row[1] = feature.getAttribute(LAYER_FIELD_FIRST_PART);
        row[2] = feature.getAttribute(LAYER_FIELD_LAST_PART);
        row[3] = feature.getAttribute(LAYER_FIELD_OFFICIAL_AREA);
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
}
