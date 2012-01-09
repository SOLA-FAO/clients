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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.geotools.map.extended.layer.VertexInformation;
import org.sola.clients.swing.gis.ui.control.NewCadastreObjectListForm;
import org.sola.common.messaging.GisMessage;

/**
 *
 * @author manoku
 */
public class NewCadastreObjectLayer extends ExtendedLayerEditor{

    public static final String LAYER_FIELD_FID = "fid";
    public static final String LAYER_FIELD_FIRST_PART = "first_part";
    public static final String LAYER_FIELD_LAST_PART = "last_part";
    public static final String LAYER_FIELD_OFFICIAL_AREA = "official_area";
    private static final String LAYER_NAME = "New Parcels";
    private static final String LAYER_STYLE_RESOURCE = "cadastrechange_parcel_new.sld";
    private static final String LAYER_ATTRIBUTE_DEFINITION = String.format("%s:\"\",%s:\"\",%s:0",
            LAYER_FIELD_FIRST_PART, LAYER_FIELD_LAST_PART, LAYER_FIELD_OFFICIAL_AREA);
    private List<CadastreObjectBean> cadastreObjectList = new ArrayList<CadastreObjectBean>();
    private Integer firstPartGenerator = 1;
    private Integer fidGenerator = 1;
    private static final String LAST_PART_FORMAT = "SP %s";
    private String lastPart = "";
    private DefaultTableModel tableModel = null;
    private NewCadastreObjectListForm hostForm = null;

    public NewCadastreObjectLayer(int srid, String applicationNumber) throws Exception {
        super(LAYER_NAME, Geometries.POLYGON,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
        this.lastPart = String.format(LAST_PART_FORMAT, applicationNumber);
        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });
        
        this.hostForm = new NewCadastreObjectListForm(this);
        this.tableModel = (DefaultTableModel) this.hostForm.getTable().getModel();
        this.tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                treatTableChange(e);
            }
        });
    }

    public NewCadastreObjectListForm getHostForm() {
        return hostForm;
    }

    public List<CadastreObjectBean> getCadastreObjectList() {
        return cadastreObjectList;
    }

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
            } catch (Exception ex) {
                Messaging.getInstance().show(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGNEWCADASTREOBJECT_IN_START);
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_ADDINGNEWCADASTREOBJECT_IN_START, ex);
            }
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
            fieldsWithValues.put(NewCadastreObjectLayer.LAYER_FIELD_FIRST_PART,
                    firstPartGenerator.toString());
            fieldsWithValues.put(NewCadastreObjectLayer.LAYER_FIELD_LAST_PART, this.lastPart);
            fieldsWithValues.put(NewCadastreObjectLayer.LAYER_FIELD_OFFICIAL_AREA, 
                    Math.round(geom.getArea()));
        }
        SimpleFeature addedFeature = super.addFeature(fid, geom, fieldsWithValues);
        firstPartGenerator++;
        return addedFeature;
    }

    public List<VertexInformation> getVertexList() {
        return this.vertexList;
    }

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
                    this.changeBean(found, feature);
                }
            }
        }
    }

    private CadastreObjectBean newBean(SimpleFeature feature) {
        CadastreObjectBean coBean = new CadastreObjectBean();
        this.changeBean(coBean, feature);
        return coBean;

    }

    private void changeBean(CadastreObjectBean targetBean, SimpleFeature feature) {
        targetBean.setId(feature.getID());
        targetBean.setNameFirstpart(feature.getAttribute(
                NewCadastreObjectLayer.LAYER_FIELD_FIRST_PART).toString());
        targetBean.setNameLastpart(feature.getAttribute(
                NewCadastreObjectLayer.LAYER_FIELD_LAST_PART).toString());
        SpatialValueAreaBean spatialValueBean;
        SpatialValueAreaBean calculatedAreaBean;
        if (targetBean.getSpatialValueAreaList().isEmpty()) {
            spatialValueBean = new SpatialValueAreaBean();
            targetBean.getSpatialValueAreaList().add(spatialValueBean);
            calculatedAreaBean = new SpatialValueAreaBean();
            calculatedAreaBean.setTypeCode(SpatialValueAreaBean.TYPE_CALCULATED);
            targetBean.getSpatialValueAreaList().add(calculatedAreaBean);
        } else {
            spatialValueBean = targetBean.getSpatialValueAreaList().get(0);
            calculatedAreaBean = targetBean.getSpatialValueAreaList().get(1);
        }
        spatialValueBean.setSize(BigDecimal.valueOf(Double.parseDouble(feature.getAttribute(
                NewCadastreObjectLayer.LAYER_FIELD_OFFICIAL_AREA).toString())));
        calculatedAreaBean.setSize(BigDecimal.valueOf(
                ((Polygon) feature.getDefaultGeometry()).getArea()));
        targetBean.setGeomPolygon(wkbWriter.write((Geometry) feature.getDefaultGeometry()));
    }

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
