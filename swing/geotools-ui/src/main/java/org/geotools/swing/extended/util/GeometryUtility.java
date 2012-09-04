/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.geotools.swing.extended.util;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.extended.exception.ReadGeometryException;

/**
 * Provides geometry manipulation functions to support editing of spatial features such as adding or
 * removing coordinates.
 */
public class GeometryUtility {

    private static GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private static WKBReader wkbReader = new WKBReader();
    private static WKBWriter wkbWriter = new WKBWriter();

    /**
     * @return A JTS Geometry Factory that can be used to build the different types of geometry.
     */
    public static GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    /**
     * Removes the target coordinate from the geometry. <p>This method will preserve the type of
     * geometry. E.g. if the geometry type is multi-linestring, the geometry returned will be
     * multi-linestring or null.</p>
     *
     * @param geometry The geometry to remove the coordinate from.
     * @param targetCoordinate The coordinates to remove.
     * @return The geometry with the coordinate removed or the original geometry. May also return
     * null if the coordinate is removed and the resulting geometry is no longer valid (e.g. Point
     * with no coordinate or LineString with only one coordinate).
     * @see #removeCoordinates(com.vividsolutions.jts.geom.Geometry, java.util.List)
     * removeCoordinates
     */
    public static Geometry removeCoordinate(Geometry geometry, Coordinate targetCoordinate) {
        List targetCoordinates = new ArrayList<Coordinate>();
        targetCoordinates.add(targetCoordinate);
        return removeCoordinates(geometry, targetCoordinates);
    }

    /**
     * Removes the list of target coordinates from the geometry. <p>This method will preserve the
     * type of geometry. E.g. if the geometry type is multi-linestring, the geometry returned will
     * be multi-linestring or null.</p>
     *
     * @param geometry The geometry to remove the coordinates from.
     * @param targetCoordinates The list of coordinates to remove.
     * @return The geometry with the coordinates removed or the original geometry. May also return
     * null if the coordinate is removed and the resulting geometry is no longer valid (e.g. Point
     * with no coordinate or LineString with only one coordinate).
     * @see #removeCoordinatesFromRing(com.vividsolutions.jts.geom.LineString, java.util.List)
     * removeCoordinatesFromRing
     */
    public static Geometry removeCoordinates(Geometry geometry, List<Coordinate> targetCoordinates) {
        Geometry result = geometry;
        if (geometry != null && targetCoordinates != null && !targetCoordinates.isEmpty()) {
            CoordinateList geomCoords = new CoordinateList(geometry.getCoordinates(), false);

            // Check if the geometry contains any of the target coordinates
            List<Coordinate> targetCoords = new ArrayList<Coordinate>();
            for (Coordinate targetCoordinate : targetCoordinates) {
                if (geomCoords.contains(targetCoordinate)) {
                    targetCoords.add(targetCoordinate);
                }
            }

            if (!targetCoords.isEmpty()) {
                // The geometry contains this coordinate. Determine the type of geometry and 
                // remove the coordinate using the appropriate method
                switch (Geometries.get(geometry)) {
                    case MULTIPOINT:
                        geomCoords.removeAll(targetCoords);
                        result = geomCoords.isEmpty() ? null // All coordinates have been removed. 
                                : getGeometryFactory().createMultiPoint(geomCoords.toCoordinateArray());
                        break;
                    case LINESTRING:
                        geomCoords.removeAll(targetCoords);
                        result = geomCoords.size() < 2 ? null // Must have 2 coordinates to create a valid Linestring. 
                                : getGeometryFactory().createLineString(geomCoords.toCoordinateArray());
                        break;
                    case MULTILINESTRING:
                        // Use recursion to check each linestring. 
                        List<LineString> lines = new ArrayList<LineString>();
                        for (int i = 1; i <= geometry.getNumGeometries(); i++) {
                            LineString lineN = (LineString) removeCoordinates(
                                    geometry.getGeometryN(i), targetCoordinates);
                            if (lineN != null) {
                                lines.add(lineN);
                            }
                        }
                        result = getGeometryFactory().createMultiLineString(
                                lines.toArray(new LineString[lines.size()]));
                        break;
                    case POLYGON:
                        Polygon poly = (Polygon) geometry;
                        LinearRing exteriorRing = removeCoordinatesFromRing(
                                poly.getExteriorRing(), targetCoordinates);
                        List<LinearRing> interiorRings = new ArrayList<LinearRing>();
                        // Don't bother processing interior rings if the exterior ring is now invalid. 
                        if (exteriorRing != null) {
                            for (int i = 1; i <= poly.getNumInteriorRing(); i++) {
                                LinearRing interiorRing = removeCoordinatesFromRing(
                                        poly.getInteriorRingN(i), targetCoordinates);
                                if (interiorRing != null) {
                                    interiorRings.add(interiorRing);
                                }
                            }
                            result = getGeometryFactory().createPolygon(exteriorRing,
                                    interiorRings.toArray(new LinearRing[interiorRings.size()]));
                        } else {
                            result = null;
                        }
                        break;
                    case MULTIPOLYGON:
                        // Use recursion to check each polygon
                        List<Polygon> polygons = new ArrayList<Polygon>();
                        for (int i = 0; i < geometry.getNumGeometries(); i++) {
                            Polygon polyN = (Polygon) removeCoordinates(
                                    geometry.getGeometryN(i), targetCoordinates);
                            if (polyN != null) {
                                polygons.add(polyN);
                            }
                        }
                        result = getGeometryFactory().createMultiPolygon(
                                polygons.toArray(new Polygon[polygons.size()]));
                        break;
                    case GEOMETRYCOLLECTION:
                        // Use recursion to check each geometry in the collection
                        List<Geometry> geoms = new ArrayList<Geometry>();
                        for (int i = 0; i < geometry.getNumGeometries(); i++) {
                            Geometry geomN = removeCoordinates(
                                    geometry.getGeometryN(i), targetCoordinates);
                            if (geomN != null) {
                                geoms.add(geomN);
                            }
                        }
                        result = getGeometryFactory().createGeometryCollection(
                                geoms.toArray(new Geometry[geoms.size()]));
                        break;
                    default:
                        // The original geometry is a Point or its invalid. 
                        // Either way the result is null. 
                        result = null;
                        break;
                }
            }
        }
        return result;
    }

    /**
     * Removes the list of coordinates from a LineString representing the exterior or interior ring
     * of a polygon.
     *
     * @param ring A LineString representing the exterior or interior ring of a polygon.
     * @param targetCoordinates The list of coordinates to remove from the ring.
     * @return A LinearRing that can be used to rebuild the original polygon or null if the ring
     * becomes invalid after the list of coordinates are removed.
     * @see #removeCoordinates(com.vividsolutions.jts.geom.Geometry, java.util.List)
     * removeCoordinates
     * @see com.vividsolutions.jts.geom.Polygon#getExteriorRing() Polygon.getExteriorRing
     * @see com.vividsolutions.jts.geom.Polygon#getInteriorRingN(int) Polygon.getInteriorRingN
     */
    public static LinearRing removeCoordinatesFromRing(LineString ring, List<Coordinate> targetCoordinates) {
        LinearRing result = null;
        if (ring != null && targetCoordinates != null && !targetCoordinates.isEmpty()) {
            CoordinateList ringCoords = new CoordinateList(ring.getCoordinates(), false);

            // Filter the coordinates to find the ones that are in the geometry
            List<Coordinate> targetCoords = new ArrayList<Coordinate>();
            for (Coordinate targetCoordinate : targetCoordinates) {
                if (ringCoords.contains(targetCoordinate)) {
                    targetCoords.add(targetCoordinate);
                }
            }

            if (!targetCoords.isEmpty()) {
                ringCoords.removeAll(targetCoords);
                if (ringCoords.size() > 2) {
                    ringCoords.closeRing();
                    result = getGeometryFactory().createLinearRing(ringCoords.toCoordinateArray());
                } else {
                    // Cannot create a ring from 2 or less coordinates. 
                    result = null;
                }
            } else {
                // Return the original ring intact. 
                result = getGeometryFactory().createLinearRing(ringCoords.toCoordinateArray());
            }
        }
        return result;
    }

    /**
     * Inserts the new coordinate into the geometry. This method will not insert the coordinate if
     * it already exists in the geometry. <p>The behavior of this method depends on the type of
     * geometry as follows: <ul><li>POINT - The geometry will be replaced by the new coordinate</li>
     * <li>MULTIPOINT - The new coordinate will be added to the Multi-point</li> <li>LINESTRING -
     * The new coordinate will be projected onto the closest segment of the line-string and added at
     * that location if the distance to project the new coordinate is less than
     * maxProjectionDistance</li> <li>MULTILINESTRING - Each line-string of the geometry is treated
     * as per LINESTRING</li> <li>POLYGON - Each ring of the Polygon is converted to line-string and
     * treated as per LINESTRING</li> <li>MULTIPOLYGON - Each polygon of the geometry is treated as
     * per POLYGON.</li><li>GEOMETRYCOLLECTION - Recursion is used to process each geometry in the
     * collection. </li></ul> </p>
     *
     * @param geometry The geometry to add the new coordinate into.
     * @param newCoordinate The coordinate to insert.
     * @param maxProjectionDistance The maximum distance (in meters) to project the coordinate onto
     * a line string. Recommended value is 2.
     * @return The geometry with the new coordinate inserted.
     */
    public static Geometry insertCoordinate(Geometry geometry, Coordinate newCoordinate,
            double maxProjectionDistance) {
        Geometry result = geometry;
        if (geometry != null && newCoordinate != null) {
            CoordinateList geomCoords = new CoordinateList(geometry.getCoordinates(), false);
            switch (Geometries.get(geometry)) {
                case POINT:
                    // Replace the original point with the new location i.e treat it as if the
                    // point was moved. 
                    result = getGeometryFactory().createPoint(newCoordinate);
                    break;
                case MULTIPOINT:
                    // Add the new point into the multi-point
                    geomCoords.add(newCoordinate, false);
                    result = getGeometryFactory().createMultiPoint(geomCoords.toCoordinateArray());
                    break;
                case LINESTRING:
                    // Find the closest point on the linestring to the new coordinate and create
                    // the coordinate there. Only add the new coordinate to the line string if the
                    // projected coordinate is within maxProjectionDistance. This prevents the 
                    // new coordinate from being projected onto a line that is not in the immediate
                    // vicinity of the mouse click. 
                    LineString line = (LineString) geometry;
                    LocationIndexedLine indexLine = new LocationIndexedLine(line);
                    LinearLocation linearLocation = indexLine.project(newCoordinate);
                    Coordinate projectedCoordinate = indexLine.extractPoint(linearLocation);
                    if (projectedCoordinate.distance(newCoordinate) < maxProjectionDistance) {
                        int newCoordIndex = linearLocation.getSegmentIndex() + 1;
                        geomCoords.add(newCoordIndex, projectedCoordinate, false);
                        result = getGeometryFactory().createLineString(geomCoords.toCoordinateArray());
                    }
                    break;
                case MULTILINESTRING:
                    // Use recursion to check each linestring. 
                    List<LineString> lines = new ArrayList<LineString>();
                    for (int i = 1; i <= geometry.getNumGeometries(); i++) {
                        LineString lineN = (LineString) insertCoordinate(
                                geometry.getGeometryN(i), newCoordinate, maxProjectionDistance);
                        if (lineN != null) {
                            lines.add(lineN);
                        }
                    }
                    result = getGeometryFactory().createMultiLineString(
                            lines.toArray(new LineString[lines.size()]));
                    break;
                case POLYGON:
                    // Process each ring of the polygon as line strings. 
                    CoordinateList ringCoords;
                    Polygon poly = (Polygon) geometry;
                    LineString exteriorRing = (LineString) insertCoordinate(
                            poly.getExteriorRing(), newCoordinate, maxProjectionDistance);
                    List<LinearRing> interiorRings = new ArrayList<LinearRing>();
                    if (exteriorRing != null) {
                        for (int i = 1; i <= poly.getNumInteriorRing(); i++) {
                            LineString interiorRing = (LineString) insertCoordinate(
                                    poly.getInteriorRingN(i), newCoordinate, maxProjectionDistance);
                            if (interiorRing != null) {
                                ringCoords = new CoordinateList(interiorRing.getCoordinates(), false);
                                ringCoords.closeRing();
                                interiorRings.add(getGeometryFactory().createLinearRing(ringCoords.toCoordinateArray()));
                            }
                        }
                        ringCoords = new CoordinateList(exteriorRing.getCoordinates(), false);
                        ringCoords.closeRing();
                        result = getGeometryFactory().createPolygon(
                                getGeometryFactory().createLinearRing(ringCoords.toCoordinateArray()),
                                interiorRings.toArray(new LinearRing[interiorRings.size()]));
                    } else {
                        result = null;
                    }
                    break;
                case MULTIPOLYGON:
                    // Use recursion to check each polygon
                    List<Polygon> polygons = new ArrayList<Polygon>();
                    for (int i = 0; i < geometry.getNumGeometries(); i++) {
                        Polygon nPoly = (Polygon) insertCoordinate(
                                geometry.getGeometryN(i), newCoordinate, maxProjectionDistance);
                        if (nPoly != null) {
                            polygons.add(nPoly);
                        }
                    }
                    result = getGeometryFactory().createMultiPolygon(
                            polygons.toArray(new Polygon[polygons.size()]));
                    break;
                case GEOMETRYCOLLECTION:
                    // Use recursion to check each geometry in the collection
                    List<Geometry> geoms = new ArrayList<Geometry>();
                    for (int i = 0; i < geometry.getNumGeometries(); i++) {
                        Geometry geomN = insertCoordinate(
                                geometry.getGeometryN(i), newCoordinate, maxProjectionDistance);
                        if (geomN != null) {
                            geoms.add(geomN);
                        }
                    }
                    result = getGeometryFactory().createGeometryCollection(
                            geoms.toArray(new Geometry[geoms.size()]));
                    break;
                default:
                    // Return the original geometry. 
                    break;
            }
        }
        return result;
    }

    /**
     * Gets a geometry from a well known binary presentation
     * @param geometry
     * @return 
     * @throws ReadGeometryException
     */
    public static Geometry getGeometryFromWkb(byte[] geometry) {
        try {
            return wkbReader.read(geometry);
        } catch (ParseException ex) {
            throw new ReadGeometryException(ex);
        }
    }

    /**
     * Gets a well known binary presentation from the geometry
     * @param geometry
     * @return 
     */
    public static byte[] getWkbFromGeometry(Geometry geometry) {
        return wkbWriter.write(geometry);
    }
}
