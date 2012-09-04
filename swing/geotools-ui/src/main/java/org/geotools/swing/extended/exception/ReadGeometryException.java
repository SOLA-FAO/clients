/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.extended.exception;

/**
 *
 * @author Manoku
 */
public class ReadGeometryException extends RuntimeException{

    private final static String MESSAGE =
            "Error while converting geometry from WKB to Geometry.";
    /**
     * Creates an exception
     * 
     * @param ex 
     */
    public ReadGeometryException(Exception ex){
        super(MESSAGE, ex);
    }
}
