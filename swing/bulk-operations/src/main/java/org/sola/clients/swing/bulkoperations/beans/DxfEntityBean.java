/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.swing.extended.util.GeometryUtility;
import org.kabeja.dxf.DXFAttrib;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFPoint;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFText;
import org.kabeja.dxf.DXFVertex;

/**
 * Represents an entity from a DXF document.
 *
 * @author Elton Manoku
 */
public class DxfEntityBean {

    private Geometry geometry;
    private String label="";

    public DxfEntityBean(DXFEntity entity) {
        if (entity.getType().equals(DXFConstants.ENTITY_TYPE_POLYLINE)) {
            geometry = getGeometryFromEntityPolyline((DXFPolyline) entity);
        }        if (entity.getType().equals(DXFConstants.ENTITY_TYPE_LWPOLYLINE)) {
            geometry = getGeometryFromEntityPolyline((DXFPolyline) entity);
        } else if (entity.getType().equals(DXFConstants.ENTITY_TYPE_POINT)) {
            geometry = getGeometryFromEntityWithPoint(
                    ((DXFPoint) entity).getX(),
                    ((DXFPoint) entity).getY());
        } else if (entity.getType().equals(DXFConstants.ENTITY_TYPE_TEXT)) {
            geometry = getGeometryFromEntityWithPoint(
                    ((DXFText) entity).getInsertPoint().getX(),
                    ((DXFText) entity).getInsertPoint().getY());
            label = ((DXFText) entity).getText();
        } else if (entity.getType().equals(DXFConstants.ENTITY_TYPE_ATTRIB)) {
            getGeometryFromEntityWithPoint(
                    ((DXFAttrib) entity).getInsertPoint().getX(),
                    ((DXFAttrib) entity).getInsertPoint().getY());
            label = ((DXFAttrib) entity).getText();
        }
    }

    private Geometry getGeometryFromEntityPolyline(DXFPolyline polyline) {
        Coordinate[] coordinates = new Coordinate[polyline.getVertexCount()];
        for (int i = 0; i < polyline.getVertexCount(); i++) {
            DXFVertex vertex = polyline.getVertex(i);
            coordinates[i] = new Coordinate(vertex.getX(), vertex.getY());
        }
        Geometry geom;
        if (coordinates[0].equals2D(coordinates[coordinates.length - 1])) {
            //It means it is a polygon
            geom = GeometryUtility.getGeometryFactory().createPolygon(
                    GeometryUtility.getGeometryFactory().createLinearRing(coordinates), null);
        } else {
            geom = GeometryUtility.getGeometryFactory().createLineString(coordinates);
        }
        return geom;
    }

    private Geometry getGeometryFromEntityWithPoint(double x, double y) {
        return GeometryUtility.getGeometryFactory().createPoint(new Coordinate(x, y));
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getGeometryType(){
        return geometry.getGeometryType();
    }
}
