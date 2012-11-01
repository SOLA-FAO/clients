/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

/**
 * It define a destination type for the import spatial objects functionality.
 * 
 * @author Elton Manoku
 */
public class SpatialDestinationTypeParcelBean extends SpatialDestinationTypeBean {   
    
    public SpatialDestinationTypeParcelBean(){
        super();
        setCode("parcel");
        setDisplayValue("Parcel");
        SpatialAttributeBean attrBean= new SpatialAttributeBean();
        attrBean.setName("nameFirstpart");
        getAttributes().add(attrBean);
        attrBean= new SpatialAttributeBean();
        attrBean.setName("nameLastpart");
        getAttributes().add(attrBean);
    }
}
