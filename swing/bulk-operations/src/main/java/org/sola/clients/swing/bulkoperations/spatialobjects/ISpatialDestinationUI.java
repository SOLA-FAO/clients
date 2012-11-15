/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.spatialobjects;

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.swing.bulkoperations.beans.SpatialAttributeBean;
import org.sola.clients.swing.bulkoperations.beans.SpatialDestinationBean;

/**
 *
 * @author Elton Manoku
 */
public interface ISpatialDestinationUI {
    
    String getName();
    SpatialDestinationBean getBean();
    ObservableList<SpatialAttributeBean> getSourceAttributes();
}
