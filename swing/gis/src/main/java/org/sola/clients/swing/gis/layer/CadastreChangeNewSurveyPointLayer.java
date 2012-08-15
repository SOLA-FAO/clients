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
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.HashMap;
import java.util.List;
import org.geotools.feature.extended.VertexInformation;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.beans.SurveyPointBean;
import org.sola.clients.swing.gis.beans.SurveyPointListBean;
import org.sola.clients.swing.gis.ui.control.SurveyPointListPanel;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Layer of the survey points that is used during the cadastre change
 *
 * @author Elton Manoku
 */
public final class CadastreChangeNewSurveyPointLayer extends AbstractSpatialObjectLayer {

    public static final String LAYER_NAME = "new_survey_points";
    private static final String LAYER_STYLE_RESOURCE = "cadastrechange_newpoints.xml";
    private static final String LAYER_FIELD_ID = "id";
    private static final String LAYER_FIELD_ISBOUNDARY = "boundaryForFeature";
    private static final String LAYER_FIELD_ISLINKED = "linkedForFeature";
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:String,%s:0,%s:0",
            LAYER_FIELD_ID, LAYER_FIELD_ISBOUNDARY, LAYER_FIELD_ISLINKED);
    private Integer idGenerator = null;
    private CadastreChangeNewCadastreObjectLayer newCadastreObjectLayer = null;

    /**
     * Constructor.
     *
     * @param newCadastreObjectLayer The layer of the new cadastre objects. It is needed in order to
     * do topology checks.
     *
     * @throws InitializeLayerException
     */
    public CadastreChangeNewSurveyPointLayer(
            CadastreChangeNewCadastreObjectLayer newCadastreObjectLayer)
            throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POINT,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION, SurveyPointBean.class);

        //Hide the layer of vertices
        this.getVerticesLayer().setVisible(false);

        //Defines filtering for the features from this layer that can be used in snapping.
        //This layer is used as a target layer for snapping from the newCadastreObjectLayer.
        this.setFilterExpressionForSnapping(String.format("%s=1", LAYER_FIELD_ISBOUNDARY));
        this.newCadastreObjectLayer = newCadastreObjectLayer;
        this.listBean = new SurveyPointListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
        SurveyPointListPanel uiComponent =
                new SurveyPointListPanel((SurveyPointListBean) this.listBean);
        initializeFormHosting(
                MessageUtility.getLocalizedMessageText(
                GisMessage.CADASTRE_CHANGE_FORM_SURVEYPOINT_TITLE), 
                uiComponent);
    }

    /**
     * Gets the list of survey points
     *
     * @return
     */
    @Override
    public List<SurveyPointBean> getBeanList() {
        return (List<SurveyPointBean>) super.getBeanList();
    }

    @Override
    public <T extends SpatialBean> void setBeanList(List<T> beanList) {
        super.setBeanList(beanList);
        this.idGenerator = null;
    }

    /**
     * It checks if a point is used in a new cadastre object
     *
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

    @Override
    protected HashMap<String, Object> getFieldsWithValuesForNewFeatures(Geometry geom) {
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        fieldsWithValues.put(LAYER_FIELD_ISBOUNDARY, 1);
        fieldsWithValues.put(LAYER_FIELD_ISLINKED, 0);
        fieldsWithValues.put(LAYER_FIELD_ID, getPointId());
        return fieldsWithValues;
    }

    /**
     * It removes a feature of survey point type. It is first checked if the point is used in a new
     * cadastre object.
     *
     * @param fid
     * @return
     */
    @Override
    public SimpleFeature removeFeature(String fid, boolean refreshMap) {
        if (this.pointIsUsedInNewCadastreObject(this.getFeatureCollection().getFeature(fid))) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_POINT_FOUND_IN_PARCEL);
            return null;
        }
        return super.removeFeature(fid, refreshMap);
    }

    /**
     * It changes a survey point. It triggers changes in all vertices that has the same coordinates
     * in the new cadastre objects
     *
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
     * Gets a new id for a new point. If the generator is not initialized it searches first in the
     * existing points for the biggest id and starts generating from that number upwards. <br/> If
     * the generator has to be changed, can be overridden.
     *
     * @return A unique number for a new point
     */
    protected String getPointId() {
        if (idGenerator == null) {
            idGenerator = 0;
            for (SurveyPointBean bean : getBeanList()) {
                try {
                    Integer beanId = Integer.parseInt(bean.getId());
                    if (idGenerator < beanId) {
                        idGenerator = beanId;
                    }
                } catch (NumberFormatException ex) {
                    //Ignore exception
                }
            }
        }
        idGenerator++;
        return idGenerator.toString();
    }
}
