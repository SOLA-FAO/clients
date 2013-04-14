/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.data;

import org.geotools.feature.SchemaException;
import org.sola.clients.swing.gis.layer.PojoForPublicDisplayLayer;
import org.sola.webservices.spatial.ResultForNavigationInfo;

/**
 * The data source handles feature requests for layers of type public display.
 * These layers need to set the name last part as an extra parameter to the
 * query in the server.
 *
 * @author Elton Manoku
 */
public class PojoPublicDisplayFeatureSource extends PojoFeatureSource {

    private String nameLastPart;

    public PojoPublicDisplayFeatureSource(
            PojoDataAccess dataSource, PojoForPublicDisplayLayer layer) throws SchemaException {
        super(dataSource, layer);
    }

    @Override
    protected ResultForNavigationInfo getResultForNavigation(
            double west, double south, double east, double north) {
        return this.getDataSource().GetQueryDataForPublicDisplay(
                this.getSchema().getTypeName(), west, south, east, north,
                this.getLayer().getSrid(), this.getLayer().getMapControl().getPixelResolution(),
                this.getNameLastPart());
    }

    public String getNameLastPart() {
        return nameLastPart;
    }

    public void setNameLastPart(String nameLastPart) {
        this.nameLastPart = nameLastPart;
    }
}
