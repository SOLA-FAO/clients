/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 * It define a destination type for the import spatial objects functionality.
 * 
 * @author Elton Manoku
 */
public class SpatialDestinationTypeBean extends AbstractCodeBean {    
    
    private SolaObservableList<SpatialAttributeBean> attributes=
            new SolaObservableList<SpatialAttributeBean>();

    public SolaObservableList<SpatialAttributeBean> getAttributes() {
        return attributes;
    }
    
}
