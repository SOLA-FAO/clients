/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.data.PojoPublicDisplayFeatureSource;

/**
 * Layer that is used only during the public display map printing process.
 *
 * @author Elton Mmanoku
 */
public class PojoForPublicDisplayLayer extends PojoBaseLayer{
    
    /**
     * Constructor.
     *
     * @param name layer name
     * @param dataAccess the data access that is used to get the features from the server
     * @param name indicates if the layer should be visible by default
     * @throws InitializeLayerException
     * @throws SchemaException
     */
    public PojoForPublicDisplayLayer(
            String name,
            PojoDataAccess dataAccess,
            boolean visible) throws InitializeLayerException, SchemaException {
        this.setDataAccess(dataAccess);
        this.setLayerName(name);
        this.setTitle(this.getConfig().getTitle());
        String styleResource = this.getConfig().getStyle();
        SimpleFeatureSource featureSource = new PojoPublicDisplayFeatureSource(
                this.getDataAccess(), this);
        this.initialize(name, featureSource, styleResource);
        this.setVisible(visible);
    }    
}
