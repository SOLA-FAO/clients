/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.extended.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.geotools.referencing.CRS;
import org.geotools.referencing.NamedIdentifier;
import org.geotools.swing.control.extended.CRSItem;
import org.geotools.swing.extended.exception.InitializeCRSException;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Elton Manoku
 */
public class CRSUtility {
    
    private static CRSUtility instance;
    private Properties sridResource = null;
    private static String SRID_RESOURCE_LOCATION = "resources/srid.properties";
    private static String PROJECTION_SOUTH_ORIENTED = "Transverse Mercator (South Orientated)";
    
    public static CRSUtility getInstance(){
        if (instance == null){
            instance = new CRSUtility();
        }
        return instance;
    }
    
    public CRSUtility(){
        initializeReferenceSystemResource();
    }
    
    private void initializeReferenceSystemResource() throws InitializeCRSException {
        try {
            System.setProperty("org.geotools.referencing.forceXY", "true");
            //Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
            if (sridResource == null) {
                sridResource = new Properties();
                String resourceLocation = String.format("/%s/%s",
                        this.getClass().getPackage().getName().replace('.', '/'),
                        SRID_RESOURCE_LOCATION);
                sridResource.load(this.getClass().getResourceAsStream(resourceLocation));
            }
        } catch (IOException ex) {
            throw new InitializeCRSException("Coordinative system resource not found.", ex);
        }
    }

    /**
     * It generates a CoordinateReferenceSystem based in srid.The WKT definition
     * of the CRS must have been defined in the resource.
     *
     * @param srid
     * @return The coordinative system corresponding to the srid
     * @throws FactoryException
     */
    public CoordinateReferenceSystem getCRS(int srid)
            throws FactoryException {
        if (!sridResource.containsKey(Integer.toString(srid))){
            throw new InitializeCRSException(String.format("srid %s not found.", srid), null);
        }
        String wktOfCrs = sridResource.getProperty(Integer.toString(srid));
        return CRS.parseWKT(wktOfCrs);
    }

    /**
     * It generates a CoordinateReferenceSystem.
     *
     * @param srid
     * @param wkt The WKT definition of the CRS
     * @return The coordinative system corresponding to the srid
     * @throws FactoryException
     */
    public void setCRS(int srid, String wkt) {
         if (!sridResource.contains(Integer.toString(srid))) {
            sridResource.setProperty(Integer.toString(srid), wkt);
         }
    }
    
    /**
     * It empties the list of CRSes.
     */
    public void clearCRSList(){
        sridResource.clear();
    }
    
    public List<CRSItem> getCRSList(){        
        List<CRSItem> items = new ArrayList<CRSItem>();
        String sridAsString = "";
        try{
        for (String key:sridResource.stringPropertyNames()){
            sridAsString = key;
            int srid = Integer.parseInt(key);
            CRSItem newItem = new CRSItem(srid, getCRS(srid));
            items.add(newItem);
        }
        }catch (FactoryException ex){
            throw new InitializeCRSException(String.format("srid: %s", sridAsString), ex);
        }catch (NumberFormatException ex){
            throw new InitializeCRSException(String.format("srid must be an integer: %s", sridAsString), ex);            
        }
        return items;
    }
    
    public String getSrid(CoordinateReferenceSystem crs){
            Object[] identifiers = crs.getIdentifiers().toArray();
            for(Object identifier:identifiers){
                NamedIdentifier namedIdentifier = (NamedIdentifier)identifier;
                if (namedIdentifier.getCodeSpace().equals("EPSG")){
                    return namedIdentifier.getCode();
                }
            }
            return "";
    }

    public boolean crsIsSouthOriented(CoordinateReferenceSystem crs) {
           return crs.toWKT().contains(PROJECTION_SOUTH_ORIENTED);
    }
}
