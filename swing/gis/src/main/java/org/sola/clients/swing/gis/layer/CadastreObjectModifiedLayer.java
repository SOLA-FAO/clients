/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerEditor;

/**
 *
 * @author Elton Manoku
 */
public class CadastreObjectModifiedLayer extends ExtendedLayerEditor {
    
    private static final String LAYER_NAME = "Modified Parcels";
    private static final String LAYER_STYLE_RESOURCE = "parcel_modified.sld";

    public CadastreObjectModifiedLayer() throws Exception{
         super(LAYER_NAME, Geometries.POLYGON, LAYER_STYLE_RESOURCE, null);
    }
}
