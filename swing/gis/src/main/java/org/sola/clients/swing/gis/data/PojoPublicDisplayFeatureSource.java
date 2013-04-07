/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.data;

import java.util.List;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.layer.PojoForPublicDisplayLayer;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.GisMessage;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;
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

    /**
     * Given the extent, modifies the feature collection
     *
     * @param west
     * @param south
     * @param east
     * @param north
     */
    @Override
    protected void ModifyFeatureCollection(double west, double south, double east, double north) {
        if (!this.getLayer().isForceRefresh()) {
            return;
        }
        this.getLayer().setForceRefresh(false);
        ReferencedEnvelope fullMapExtent = this.getLayer().getMapControl().getFullExtent();
        try {
            ResultForNavigationInfo resultInfo = getResultForNavigation(
                    fullMapExtent.getMinX(), fullMapExtent.getMinY(),
                    fullMapExtent.getMaxX(), fullMapExtent.getMaxY());
            List<SimpleFeature> featuresToAdd = this.getFeaturesFromData(resultInfo.getToAdd());
            this.collection.clear();
            this.collection.addAll(featuresToAdd);
        } catch (WebServiceClientException ex) {
            LogUtility.log(
                    String.format(GisMessage.GENERAL_RETRIEVE_FEATURES_ERROR,
                    this.getLayer().getTitle()), ex);
            Messaging.getInstance().show(
                    GisMessage.GENERAL_RETRIEVE_FEATURES_ERROR, this.getLayer().getTitle());
        }
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
