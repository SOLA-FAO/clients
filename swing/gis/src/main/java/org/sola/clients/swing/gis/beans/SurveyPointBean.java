/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.gis.beans;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.swing.extended.util.GeometryUtility;

/**
 * A data bean that represents a survey point which is used during the cadastre change process.
 * 
 * @author Elton Manoku
 */
public class SurveyPointBean extends SpatialBean{

    public static String LINKED_FOR_FEATURE_PROPERTY = "linkedForFeature";
    public static String BOUNDARY_FOR_FEATURE_PROPERTY = "boundaryForFeature";
    public static String SHIFT_DISTANCE_PROPERTY = "shiftDistance";
    private String id;
    private byte[] geom;
    private byte[] originalGeom;
    private boolean boundary = true;
    private boolean linked = false;
    private Double x;
    private Double y;
    private Double shiftDistance;
    
    private Point originalGeometryForFeature;

    /**
     * Gets the shift of a point from its original location
     *
     * @return
     */
    public Double getShiftDistance() {
        return shiftDistance;
    }

    /**
     * Sets the shift distance between the original position of the survey point 
     * and current position.
     * It fires the event of change.
     * 
     * @param shiftDistance 
     */
    public void setShiftDistance(Double shiftDistance) {
        Double oldValue = this.shiftDistance;
        this.shiftDistance = shiftDistance;
        propertySupport.firePropertyChange(SHIFT_DISTANCE_PROPERTY, oldValue, shiftDistance);        
    }

    /**
     * Gets the x coordinate of the point. It is derived from the geometry
     * @return 
     */
    public Double getX() {
        return x;
    }

    /**
     * Sets the x coordinate of the geometry. It is used when the point is created from coordinates
     * @param x 
     */
    public void setX(Double x) {
        this.x = x;
        this.makeGeom();
    }

    /**
     * Gets the y coordinate of the point. It is derived from the geometry
     * @return 
     */
    public Double getY() {
        return y;
    }

    /**
     * Sets the y coordinate of the geometry. It is used when the point is created from coordinates
     * @param y 
     */
    public void setY(Double y) {
        this.y = y;
        this.makeGeom();
    }

    
    public boolean isBoundary() {
        return boundary;
    }

    public void setBoundary(boolean boundary) {
        int oldValueBoundaryForFeature = getBoundaryForFeature();
        this.boundary = boundary;
        propertySupport.firePropertyChange(
                BOUNDARY_FOR_FEATURE_PROPERTY, oldValueBoundaryForFeature, getBoundaryForFeature());
    }

    public int getBoundaryForFeature(){
        return isBoundary()?1:0;
    }

    public void setBoundaryForFeature(int value){
        setBoundary(value ==1);
    }
    
    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geometry) {
        this.geom = geometry.clone();
        if (getFeatureGeom() == null){
            super.setFeatureGeom(GeometryUtility.getGeometryFromWkb(geometry));
            calculateShift();
        }
        setXAndY();
    }
        
    public byte[] getOriginalGeom() {
        return originalGeom;
    }

    public void setOriginalGeom(byte[] originalGeom) {
        this.originalGeom = originalGeom.clone();
        if (this.originalGeometryForFeature == null){
            this.originalGeometryForFeature = 
                    (Point)GeometryUtility.getGeometryFromWkb(originalGeom);
            calculateShift();
        }
    }  

    @Override
    public void setFeatureGeom(Geometry geometryValue){
        super.setFeatureGeom(geometryValue);
        this.setGeom(GeometryUtility.getWkbFromGeometry(geometryValue));
        if (originalGeom == null){
            this.originalGeometryForFeature = (Point) geometryValue.clone();
            setOriginalGeom(getGeom());
        }
        calculateShift();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        int oldValue = getLinkedForFeature();
        this.linked = linked;
        propertySupport.firePropertyChange(
                LINKED_FOR_FEATURE_PROPERTY, oldValue, getLinkedForFeature());
    }

    public int getLinkedForFeature(){
        return isLinked()?1:0;
    }

    public void setLinkedForFeature(int value){
        setLinked(value ==1);
    }

    public Double getDeltaX(){
        return Math.abs(originalGeometryForFeature.getX() - ((Point)this.getFeatureGeom()).getX());        
    }
    
    public Double getDeltaY(){
        return Math.abs(originalGeometryForFeature.getY() - ((Point)this.getFeatureGeom()).getY());        
    }

    /**
     * It makes the point geometry if x and y are not null and the geometry is null.
     * This happen when the bean is created from manual insert or when the points are loaded 
     * from a file.
     */
    private void makeGeom(){
        if (this.x != null && this.y != null && getGeom() == null){
            Geometry pointGeom = GeometryUtility.getGeometryFactory().createPoint(
                    new Coordinate(this.x, this.y));
            this.setFeatureGeom(pointGeom);
        } 
    }
    
    private void calculateShift(){
        if (getFeatureGeom() != null && this.originalGeometryForFeature != null){
            this.setShiftDistance(getFeatureGeom().distance(this.originalGeometryForFeature));
        }
    }
    
    private void setXAndY(){
        if (x == null){
            x = ((Point)getFeatureGeom()).getX();
            y = ((Point)getFeatureGeom()).getY();
        }
    }
}
