/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 *
 * @author Elton Manoku
 */
public class SpatialBulkMoveBean extends AbstractBindingBean{
    
    public static final String PROPERTY_SOURCE = "source";
    private SpatialSourceBean source = new SpatialSourceShapefileBean();
    private SpatialDestinationBean destination = new SpatialDestinationBean();

    public SpatialSourceBean getSource() {
        return source;
    }

    public void setSource(SpatialSourceBean source) {
        SpatialSourceBean old = this.source;
        this.source = source;
        propertySupport.firePropertyChange(PROPERTY_SOURCE, old, source);
    }

    public SpatialDestinationBean getDestination() {
        return destination;
    }

    public void setDestination(SpatialDestinationBean destination) {
        this.destination = destination;
    }
        
}
