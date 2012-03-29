/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetRedefinitionBean;
import org.sola.common.messaging.GisMessage;

/**
 * Layer that maintains a list of target objects during the cadastre redefinition.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionObjectLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Modified Parcels";
    private static final String LAYER_STYLE_RESOURCE = "parcel_modified.xml";
    private static final String LAYER_FIELD_ORIGINAL_GEOMETRY = "original_geometry";
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:Polygon", LAYER_FIELD_ORIGINAL_GEOMETRY);

    /**
     * Constructor
     * 
     * @throws InitializeLayerException 
     */
    public CadastreRedefinitionObjectLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.POLYGON, LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });
    }

    /**
     * Adds the cadastre objects to the layer
     * 
     * @param cadastreObjectList 
     */
    public void addCadastreObjects(List<CadastreObjectBean> cadastreObjectList) {
        for (CadastreObjectBean coBean : cadastreObjectList) {
            if (this.getFeatureCollection().getFeature(coBean.getId()) == null) {
                this.addCadastreObjectTarget(coBean.getId(), null, coBean.getGeomPolygon());
            }
        }
    }

    /**
     * Gets the list of target objects
     * @return 
     */
    public List<CadastreObjectTargetRedefinitionBean> getCadastreObjectTargetList() {
        List<CadastreObjectTargetRedefinitionBean> targetList =
                new ArrayList<CadastreObjectTargetRedefinitionBean>();
        SimpleFeature feature = null;
        SimpleFeatureIterator iterator =
                (SimpleFeatureIterator) this.getFeatureCollection().features();
        while (iterator.hasNext()) {
            feature = iterator.next();
            CadastreObjectTargetRedefinitionBean targetBean =
                    new CadastreObjectTargetRedefinitionBean();
            targetBean.setCadastreObjectId(feature.getID());
            targetBean.setGeomPolygon(wkbWriter.write((Geometry) feature.getDefaultGeometry()));
            targetList.add(targetBean);
        }
        iterator.close();
        return targetList;
    }


    /**
     * Adds the list of target cadastre objects. It is called during the read of the transaction
     * from the server.
     * @param targetList 
     */
    public void addCadastreObjectTargetList(List<CadastreObjectTargetRedefinitionBean> targetList) {
        for (CadastreObjectTargetRedefinitionBean targetBean : targetList) {
            this.addCadastreObjectTarget(
                    targetBean.getCadastreObjectId(),
                    targetBean.getGeomPolygon(),
                    targetBean.getGeomPolygonCurrent());
        }
    }

    /**
     * Add a target cadastre object. If a feature with the same fid is found it is not added
     * @param fid
     * @param geometry If this is null, it is cloned by originalGeometry
     * @param originalGeometry This is the original geometry of the cadastre object
     */
    public void addCadastreObjectTarget(String fid, byte[] geometry, byte[] originalGeometry) {
        if (this.getFeatureCollection().getFeature(fid) != null) {
            return;
        }
        if (geometry == null) {
            geometry = originalGeometry.clone();
        }
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        try {
            fieldsWithValues.put(LAYER_FIELD_ORIGINAL_GEOMETRY, wkbReader.read(originalGeometry));
            this.addFeature(fid, geometry, fieldsWithValues);
        } catch (ParseException ex) {
            org.sola.common.logging.LogUtility.log(
                    GisMessage.CADASTRE_REDEFINITION_ADD_TARGET_ERROR, ex);
            Messaging.getInstance().show(GisMessage.CADASTRE_REDEFINITION_ADD_TARGET_ERROR);
        }
    }

    /**
     * Gets the list of features of target cadastre objects that are involved in a node
     * 
     * @param nodeFeature
     * @return 
     */
    public List<SimpleFeature> getCadastreObjectFeatures(SimpleFeature nodeFeature) {
        List<SimpleFeature> cadastreObjectFeatureList =
                new ArrayList<SimpleFeature>();
        ReferencedEnvelope filterBbox = new ReferencedEnvelope(nodeFeature.getBounds());
        filterBbox.expandBy(0.01, 0.01);
        FeatureCollection featureCollection = this.getFeaturesInRange(filterBbox, null);
        SimpleFeatureIterator iterator = (SimpleFeatureIterator) featureCollection.features();
        while (iterator.hasNext()) {
            cadastreObjectFeatureList.add(iterator.next());
        }
        iterator.close();
        return cadastreObjectFeatureList;
    }


    /**
     * It handles the if a feature changes. If a feature changes and its geometry
     * becomes the same as the original geometry, it is removed from the collection.
     * 
     * @param ev 
     */
    private void featureCollectionChanged(CollectionEvent ev) {
        if (ev.getFeatures() == null
                || ev.getEventType() == CollectionEvent.FEATURES_ADDED
                || ev.getEventType() == CollectionEvent.FEATURES_REMOVED) {
            return;
        }
        for (SimpleFeature feature : ev.getFeatures()) {
            Geometry geom = (Geometry) feature.getDefaultGeometry();
            Geometry originalGeometry =
                    (Geometry) feature.getAttribute(LAYER_FIELD_ORIGINAL_GEOMETRY);
            if (geom.equalsTopo(originalGeometry)) {
                this.removeFeature(feature.getID());
            }
        }
    }
}
