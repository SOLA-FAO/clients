/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.control.extended;

import org.geotools.referencing.NamedIdentifier;
import org.geotools.swing.extended.util.CRSUtility;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Elton Manoku
 */
public class CRSItem {
    
    private int srid;
    private CoordinateReferenceSystem crs;
    
    public CRSItem(int srid, CoordinateReferenceSystem crs){
        this.srid = srid;
        this.crs = crs;
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public int getSrid() {
        return srid;
    }

    @Override
    public String toString() {
        return crs.getName().getCode();
        //return CRSUtility.getInstance().getSrid(crs);
    }
}
