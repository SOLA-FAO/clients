/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.HashMap;

/**
 *
 * @author Elton Manoku
 */
public class SpatialSourceObjectBean {
    private byte[] theGeom;
    private HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();

    public HashMap<String, Object> getFieldsWithValues() {
        return this.fieldsWithValues;
    }

    public byte[] getTheGeom() {
        return theGeom;
    }

    public void setTheGeom(byte[] theGeom) {
        this.theGeom = theGeom;
    }
    
}
