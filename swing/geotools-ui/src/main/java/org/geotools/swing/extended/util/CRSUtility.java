/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.extended.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.geotools.referencing.CRS;
import org.geotools.referencing.NamedIdentifier;
import org.geotools.swing.control.extended.CRSItem;
import org.geotools.swing.extended.exception.InitializeCRSException;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Elton Manoku
 */
public class CRSUtility {

    private static CRSUtility instance;
    private Properties sridResource = null;
    private Map<String, MathTransform> cachedMaptransforms = new HashMap<String, MathTransform>();
    private static String SRID_RESOURCE_LOCATION = "resources/srid.properties";
    private static String PROJECTION_SOUTH_ORIENTED = "Transverse Mercator (South Orientated)";

    public static CRSUtility getInstance() {
        if (instance == null) {
            instance = new CRSUtility();
        }
        return instance;
    }

    public CRSUtility() {
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
    public CoordinateReferenceSystem getCRS(int srid) {
        if (!sridResource.containsKey(Integer.toString(srid))) {
            throw new InitializeCRSException(String.format("srid %s not found.", srid), null);
        }
        String wktOfCrs = sridResource.getProperty(Integer.toString(srid));
        try {
            return CRS.parseWKT(wktOfCrs);
        } catch (FactoryException ex) {
            throw new InitializeCRSException(String.format("Error parsing srid %s.", srid), ex);
        }
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
    public void clearCRSList() {
        sridResource.clear();
    }

    public List<CRSItem> getCRSList() {
        List<CRSItem> items = new ArrayList<CRSItem>();
        String sridAsString = "";
        try {
            for (String key : sridResource.stringPropertyNames()) {
                sridAsString = key;
                int srid = Integer.parseInt(key);
                CRSItem newItem = new CRSItem(srid, getCRS(srid));
                items.add(newItem);
            }
        } catch (NumberFormatException ex) {
            throw new InitializeCRSException(String.format("srid must be an integer: %s", sridAsString), ex);
        }
        return items;
    }

    public String getSrid(CoordinateReferenceSystem crs) {
        Object[] identifiers = crs.getIdentifiers().toArray();
        for (Object identifier : identifiers) {
            NamedIdentifier namedIdentifier = (NamedIdentifier) identifier;
            if (namedIdentifier.getCodeSpace().equals("EPSG")) {
                return namedIdentifier.getCode();
            }
        }
        return "";
    }

    public boolean crsIsSouthOriented(CoordinateReferenceSystem crs) {
        return crs.toWKT().contains(PROJECTION_SOUTH_ORIENTED);
    }

    public MathTransform getTransform(int fromSrid, int toSrid) {
        String cachedKey = String.format("%s-%s", fromSrid, toSrid);
        if (!cachedMaptransforms.containsKey(cachedKey)) {
            CoordinateReferenceSystem sourceCRS = this.getCRS(fromSrid);
            CoordinateReferenceSystem targetCRS = this.getCRS(toSrid);
            try {
                cachedMaptransforms.put(cachedKey, CRS.findMathTransform(sourceCRS, targetCRS));
            } catch (FactoryException ex) {
                throw new InitializeCRSException(String.format(
                        "Error finding the transformation between %s and %s", fromSrid, toSrid), ex);                
            }
        }
        return cachedMaptransforms.get(cachedKey);
    }
}
