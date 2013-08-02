/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupBean;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupListBean;
import org.sola.clients.swing.gis.ui.control.SpatialUnitGroupListPanel;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author Elton Manoku
 */
public class SpatialUnitGroupLayer extends AbstractSpatialObjectLayer {

    private static String LAYER_NAME = "spatial_unit_group";
    private static String LAYER_STYLE_RESOURCE = "spatial_unit_group.xml";
    private static final String LAYER_FIELD_LABEL = "label";
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:\"\"", LAYER_FIELD_LABEL);
    private Integer hierarchyLevel = null;
    private SpatialUnitGroupListPanel spatialObjectDisplayPanel;
    private Integer idGenerator = null;

    public SpatialUnitGroupLayer()
            throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POLYGON,
                LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION, SpatialUnitGroupBean.class);
        this.listBean = new SpatialUnitGroupListBean();
        //This is called after the listBean is initialized
        initializeListBeanEvents();
        this.spatialObjectDisplayPanel =
                new SpatialUnitGroupListPanel((SpatialUnitGroupListBean) this.listBean);
        initializeFormHosting(this.spatialObjectDisplayPanel.getTitle(), this.spatialObjectDisplayPanel);
    }

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public final void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
        ((SpatialUnitGroupListBean) this.listBean).setHierarchyLevel(hierarchyLevel);
        this.setBeanList(new ArrayList<SpatialUnitGroupBean>());
    }

    @Override
    public List<SpatialUnitGroupBean> getBeanList() {
        return (List<SpatialUnitGroupBean>) super.getBeanList();
    }

    @Override
    public <T extends SpatialBean> void setBeanList(List<T> beanList) {
        super.setBeanList(beanList);
        this.idGenerator = null;
    }

    @Override
    protected HashMap<String, Object> getFieldsWithValuesForNewFeatures(Geometry geom) {
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        fieldsWithValues.put(LAYER_FIELD_LABEL, getLabelForNewFeature());
        return fieldsWithValues;
    }

    private String getLabelForNewFeature() {
        if (idGenerator == null) {
            idGenerator = 0;
            for (SpatialUnitGroupBean bean : getBeanList()) {
                try {
                    Integer beanId = Integer.parseInt(bean.getLabel());
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
