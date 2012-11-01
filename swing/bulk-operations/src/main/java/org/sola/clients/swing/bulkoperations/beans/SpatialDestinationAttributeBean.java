/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

/**
 *
 * @author Elton Manoku
 */
public class SpatialDestinationAttributeBean extends SpatialAttributeBean{
    private SpatialAttributeBean sourceAttribute;
    private String sourceText;
    
    public SpatialDestinationAttributeBean(){
        super();
    }
    
    public SpatialDestinationAttributeBean(String name){
        this();
        setName(name);
    }

    public SpatialAttributeBean getSourceAttribute() {
        return sourceAttribute;
    }

    public void setSourceAttribute(SpatialAttributeBean sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }
    
    public void setAttributeObjectName(SpatialAttributeBean value){
        String newValue = null;
        if (value != null){
            newValue = value.getName();
        }
        setName(newValue);
    }

    public SpatialAttributeBean getAttributeObjectName(){
        SpatialAttributeBean bean = new SpatialAttributeBean();
        bean.setName(getName());
        return bean;
    }
}
