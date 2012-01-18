/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.util.Messaging;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;

/**
 *
 * @author Elton Manoku
 */
public class CadastreObjectModifiedLayer extends ExtendedLayerGraphics {

    private static final String LAYER_NAME = "Modified Parcels";
    private static final String LAYER_STYLE_RESOURCE = "parcel_modified.xml";
    private List<CadastreObjectBean> cadastreObjectList = new ArrayList<CadastreObjectBean>();

    public CadastreObjectModifiedLayer() throws Exception {
        super(LAYER_NAME, Geometries.POLYGON, LAYER_STYLE_RESOURCE, null);
    }

    public List<CadastreObjectBean> getCadastreObjectList() {
        return cadastreObjectList;
    }

    public void addCadastreObjects(List<CadastreObjectBean> cadastreObjectList) {
        try {
            for (CadastreObjectBean coBean : cadastreObjectList) {
                if (this.getFeatureCollection().getFeature(coBean.getId()) == null) {
                    this.addFeature(coBean.getId(), coBean.getGeomPolygon(), null);
                    this.cadastreObjectList.add(coBean);
                }
            }
        } catch (Exception ex) {
            Messaging.getInstance().show("Error while adding parcels involved");
        }
    }
}
