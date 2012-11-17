/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.List;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.swing.gis.beans.SpatialBean;

/**
 *
 * @author Elton Manoku
 */
public abstract class SpatialDestinationBean extends AbstractCodeBean{
    
    public abstract <T extends SpatialBean> List<T> getBeans(SpatialSourceBean fromSource);
}
