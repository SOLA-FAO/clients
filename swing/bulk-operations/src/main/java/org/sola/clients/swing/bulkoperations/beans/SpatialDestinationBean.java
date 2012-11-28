/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.List;
import org.sola.clients.beans.AbstractCodeBean;

/**
 *
 * @author Elton Manoku
 */
public abstract class SpatialDestinationBean extends AbstractCodeBean{
    
    public abstract String getPanelName();
    
    public abstract List<SpatialUnitTemporaryBean> getBeans(SpatialSourceBean fromSource);
}
