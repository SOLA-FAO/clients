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
    
    private static String DESTINATION_TYPE_PROPERTY = "destinationType";
    private SpatialSourceBean source = new SpatialSourceBean();
    private SpatialDestinationTypeBean destinationType;
    private SolaObservableList<SpatialDestinationAttributeBean> destinationAttributes
            = new SolaObservableList<SpatialDestinationAttributeBean>();

    public SolaObservableList<SpatialDestinationAttributeBean> getDestinationAttributes() {
        return destinationAttributes;
    }

    public SpatialDestinationTypeBean getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(SpatialDestinationTypeBean destinationType) {
        SpatialDestinationTypeBean old = this.destinationType;
        this.destinationType = destinationType;
        propertySupport.firePropertyChange(DESTINATION_TYPE_PROPERTY, old, this.destinationType);
    }

    public SpatialSourceBean getSource() {
        return source;
    }

    public void setSource(SpatialSourceBean source) {
        this.source = source;
    }
        
}
