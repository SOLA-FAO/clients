/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.HashMap;

/**
 * A generic class that is used for the definition of the spatial source objects.
 * @author Elton Manoku
 */
public class SpatialSourceObjectBean {
    private byte[] theGeom;
    private HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();

    /**
     * The list of fields with values.
     * 
     * @return 
     */
    public HashMap<String, Object> getFieldsWithValues() {
        return this.fieldsWithValues;
    }
       
    /**
     * Get the geometry value.
     * 
     * @return 
     */
    public byte[] getTheGeom() {
        return theGeom;
    }

    public void setTheGeom(byte[] theGeom) {
        this.theGeom = theGeom;
    }
    
}
