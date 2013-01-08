/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.List;
import org.sola.clients.beans.AbstractCodeBean;

/**
 * Abstract bean that encapsulates common functionality and properties for a 
 * spatial destination. <br/>
 * If there will be added a new destination type, it has to inherit from this bean.
 * 
 * @author Elton Manoku
 */
public abstract class SpatialDestinationBean extends AbstractCodeBean{
    

    public SpatialDestinationBean(){
        super();
        setStatus("c");        
    }
    
    /**
     * The name of the user interface panel that will be used in user interface
     * when this destination will be chosen.
     * 
     * @return 
     */
    public abstract String getPanelName();
    
    /**
     * The list of spatial beans converted from the source. These beans can be sent
     * to the server for further processing.
     * @param fromSource The spatial source
     * @return 
     */
    public abstract List<SpatialUnitTemporaryBean> getBeans(SpatialSourceBean fromSource);
}
