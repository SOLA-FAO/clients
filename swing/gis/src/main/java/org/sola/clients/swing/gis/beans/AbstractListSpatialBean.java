/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 * This is a bean that manages the Observable Bean lists. It also manages the selected element
 * in a list. It is used mainly in the GIS Component.
 * 
 * @author Elton Manoku
 */
public abstract class AbstractListSpatialBean extends AbstractBindingBean{
    public static final String SELECTED_BEAN_PROPERTY = "selectedBean";
    private SolaObservableList beanList;
    private SpatialBean selectedBean;

    public AbstractListSpatialBean() {
        super();
        beanList = initializeBeanList();
    }
    
    /**
     * It initializes the list bean. For each subclass this needs to be overridden to
     * initialize the list of choice.
     * @return 
     */
    protected abstract SolaObservableList initializeBeanList();

    /**
     * Gets the list of beans.
     * @return 
     */
    public SolaObservableList getBeanList() {
        return beanList;
    }

    /**
     * Gets the selected bean in the list.
     * @param <T>
     * @return 
     */
    public <T extends SpatialBean> T getSelectedBean() {
        return (T)selectedBean;
    }

    /**
     * Sets the selected bean from the list.
     * @param <T>
     * @param newValue 
     */
    public <T extends SpatialBean> void setSelectedBean(T newValue) {
        SpatialBean oldValue = this.selectedBean;
        this.selectedBean = newValue;
        propertySupport.firePropertyChange(SELECTED_BEAN_PROPERTY, oldValue, newValue);
    }
}
