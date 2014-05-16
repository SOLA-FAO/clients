/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.imagegenerator;

/**
 * It holds information about the generated map and scalebar image.
 * 
 * @author Elton Manoku
 */
public class MapImageInformation {
   private String mapImageLocation;
   private String scalebarImageLocation;
   private Integer srid;
   private double scale;
   private double area;

    public String getMapImageLocation() {
        return mapImageLocation;
    }

    public void setMapImageLocation(String mapImageLocation) {
        this.mapImageLocation = mapImageLocation;
    }

    public String getScalebarImageLocation() {
        return scalebarImageLocation;
    }

    public void setScalebarImageLocation(String scalebarImageLocation) {
        this.scalebarImageLocation = scalebarImageLocation;
    }

    public Integer getSrid() {
        return srid;
    }

    public void setSrid(Integer srid) {
        this.srid = srid;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return String.format(
                "map:%s\r\nscalebar:%s\r\narea:%s\r\nscale:%s", 
                getMapImageLocation(), getScalebarImageLocation(), 
                getArea(), getScale());
    }
}
