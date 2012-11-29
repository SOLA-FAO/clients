/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.common.messaging.ClientMessage;

/**
 *
 * @author Elton Manoku
 */
public class SpatialBulkMoveBean extends AbstractBindingBean{
    
    public static final String PROPERTY_SOURCE = "source";
    public static final String PROPERTY_DESTINATION = "destination";
    private SpatialSourceBean source = new SpatialSourceShapefileBean();
    private SpatialDestinationBean destination = new SpatialDestinationCadastreObjectBean();

    @NotNull(message = ClientMessage.CHECK_NOTNULL_FIRSTPART, payload=Localized.class)
    public SpatialSourceBean getSource() {
        return source;
    }

    public void setSource(SpatialSourceBean source) {
        SpatialSourceBean old = this.source;
        this.source = source;
        propertySupport.firePropertyChange(PROPERTY_SOURCE, old, source);
    }

    @NotNull(message = ClientMessage.CHECK_NOTNULL_FIRSTPART, payload=Localized.class)
    public SpatialDestinationBean getDestination() {
        return destination;
    }

    public void setDestination(SpatialDestinationBean value) {
        SpatialDestinationBean old = this.destination;
        this.destination = value;
        propertySupport.firePropertyChange(PROPERTY_DESTINATION, old, value);
    }
    
    public List<SpatialUnitTemporaryBean> getBeans(){
        return getDestination().getBeans(getSource());
    }
        
}
