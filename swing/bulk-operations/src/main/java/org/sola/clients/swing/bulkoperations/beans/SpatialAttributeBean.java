/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import org.sola.clients.beans.AbstractBindingBean;

/**
 * It represents an attribute during the spatial bulk move.
 * 
 * @author Elton Manoku
 */
public class SpatialAttributeBean extends AbstractBindingBean{

    private static String NAME_PROPERTY = "name"; 
    private String name;
    private String dataType;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        propertySupport.firePropertyChange(NAME_PROPERTY, old, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (!obj.getClass().equals(this.getClass())){
            return false;
        }
        return ((SpatialAttributeBean)obj).getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getName();
    }
    
}
