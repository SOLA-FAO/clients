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
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetRedefinitionBean;

/**
 *
 * @author Elton Manoku
 */
public class CadastreObjectModifiedLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Modified Parcels";
    private static final String LAYER_STYLE_RESOURCE = "parcel_modified.xml";
    private static final String LAYER_FIELD_ORIGINAL_GEOMETRY = "original_geometry";
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:Polygon", LAYER_FIELD_ORIGINAL_GEOMETRY);

    public CadastreObjectModifiedLayer() throws Exception {
        super(LAYER_NAME, Geometries.POLYGON, LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
    }

    public void addCadastreObjects(List<CadastreObjectBean> cadastreObjectList) {
        try {
            for (CadastreObjectBean coBean : cadastreObjectList) {
                if (this.getFeatureCollection().getFeature(coBean.getId()) == null) {
                    this.addCadastreObjectTarget(coBean.getId(), null, coBean.getGeomPolygon());
                }
            }
        } catch (Exception ex) {
            Messaging.getInstance().show("Error while adding parcels involved");
        }
    }

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

    public void addCadastreObjectTargetList(List<CadastreObjectTargetRedefinitionBean> targetList)
    throws Exception, ParseException{
        for (CadastreObjectTargetRedefinitionBean targetBean : targetList) {
            this.addCadastreObjectTarget(
                    targetBean.getCadastreObjectId(), 
                    targetBean.getGeomPolygon(),
                    targetBean.getGeomPolygonCurrent());
        }
    }

    public void addCadastreObjectTarget(String fid, byte[] geometry, byte[] originalGeometry) 
    throws ParseException, Exception{
        if (this.getFeatureCollection().getFeature(fid) != null) {
            return;
        }
        if (geometry == null) {
            geometry = originalGeometry.clone();
        }
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        fieldsWithValues.put(LAYER_FIELD_ORIGINAL_GEOMETRY, wkbReader.read(originalGeometry));
        this.addFeature(fid, geometry, fieldsWithValues);
    }
    
    public List<SimpleFeature> getCadastreObjectFeatures(SimpleFeature nodeFeature){
        List<SimpleFeature> cadastreObjectFeatureList =
                new ArrayList<SimpleFeature>();
        ReferencedEnvelope filterBbox = new ReferencedEnvelope(nodeFeature.getBounds());
        filterBbox.expandBy(0.01, 0.01);
        FeatureCollection featureCollection = this.getFeaturesInRange(filterBbox, null);
        SimpleFeatureIterator iterator = (SimpleFeatureIterator)featureCollection.features();
        while(iterator.hasNext()){
            cadastreObjectFeatureList.add(iterator.next());
        }
        iterator.close();
        return cadastreObjectFeatureList;
    }
}
