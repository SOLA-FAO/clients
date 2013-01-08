/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.spatialobjects;

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.swing.bulkoperations.beans.SpatialAttributeBean;
import org.sola.clients.swing.bulkoperations.beans.SpatialDestinationBean;

/**
 * Interface that every spatial destination GUI control must implement.
 * 
 * @author Elton Manoku
 */
public interface ISpatialDestinationUI {
   
    /**
     * Gets the name of the GUI Control.
     * 
     * @return 
     */
    String getName();
    
    /**
     * Gets the spatial destination bean that is binded to the GUI.
     * 
     * @return 
     */
    SpatialDestinationBean getBean();
    
    /**
     * Gets the list of spatial source attributes that will eventually be used
     * in the destination.
     * 
     * @return 
     */
    ObservableList<SpatialAttributeBean> getSourceAttributes();
}
