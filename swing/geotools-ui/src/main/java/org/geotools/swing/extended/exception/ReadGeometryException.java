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
    
    public ReadGeometryException(Exception ex){
        super("Error while converting geometry from WKB to Geometry.", ex);
    }
}
