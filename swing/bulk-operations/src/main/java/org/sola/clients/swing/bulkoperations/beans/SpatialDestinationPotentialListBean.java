/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.Set;
import org.reflections.Reflections;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractListBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.common.logging.LogUtility;

/**
 * Bean that generates a list of potential spatial destination types.
 * For generating the list reflection is used where every class that inherits from 
 * SpatialDestinationBean is considered as valid destination type.
 * 
 * @author Elton Manoku
 */
public class SpatialDestinationPotentialListBean extends AbstractListBean{

    public static final String SELECTED_BEAN_PROPERTY = "selectedSpatialDestinationBean";
   // public static final String SELECTED_BEAN_PROPERTY = "selectedBean";

    public SpatialDestinationPotentialListBean(){
        super();
    }
    
    @Override
    protected SolaObservableList initializeBeanList() {
        SolaObservableList<SpatialDestinationBean> list =
                new SolaObservableList<SpatialDestinationBean>();
        String namespaceToScan = SpatialDestinationBean.class.getPackage().getName();
        Reflections reflections = new Reflections(namespaceToScan);
        Set<Class<? extends SpatialDestinationBean>> subTypes =
                reflections.getSubTypesOf(SpatialDestinationBean.class);
        try {
            for (Class<? extends SpatialDestinationBean> foundClass : subTypes) {
                list.add(foundClass.newInstance());
            }
        } catch (InstantiationException ex) {
            LogUtility.log("Error initializing SpatialDestinationPotentialListBean", ex);
        } catch (IllegalAccessException ex) {
            LogUtility.log("Error initializing SpatialDestinationPotentialListBean", ex);
        }
        return list;
    }

    public final SpatialDestinationBean getSelectedSpatialDestinationBean(){
        return (SpatialDestinationBean)super.getSelectedBean();
    }
    

    public final void setSelectedSpatialDestinationBean(SpatialDestinationBean newValue) {
        SpatialDestinationBean old = getSelectedSpatialDestinationBean();
        super.setSelectedBean((AbstractBindingBean)newValue);
        propertySupport.firePropertyChange(SELECTED_BEAN_PROPERTY, old, newValue);
    }
    
}
