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
package org.geotools.swing.tool.extended;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.map.extended.layer.ExtendedFeatureLayer;

/**
 * It extends the drawing tool to support also snapping to features in given target layers.
 * 
 * @author Elton Manoku
 */
public abstract class ExtendedDrawToolWithSnapping extends ExtendedDrawTool {

    /**
     * Type of the target object being snapped
     */
    public enum SNAPPED_TARGET_TYPE {

        None,
        Vertex,
        Line
    }
    private List<ExtendedFeatureLayer> targetSnappingLayers = new ArrayList<ExtendedFeatureLayer>();
    private Color stylePointColorNoTargetFound = Color.RED;
    private Color stylePointColorTargetFound = Color.BLUE;
    private int snapDistanceInPixels = 15;
    private double snapDistanceInMeters = 5;
    private com.vividsolutions.jts.geom.Point snappingPoint = null;
    private SNAPPED_TARGET_TYPE snappedTarget = SNAPPED_TARGET_TYPE.None;

    /**
     * Gets features layers to be target for snapping
     * @return 
     */
    public List<ExtendedFeatureLayer> getTargetSnappingLayers() {
        return targetSnappingLayers;
    }

    /**
     * Gets snap distance in pixels
     * @return 
     */
    public int getSnapDistanceInPixels() {
        return snapDistanceInPixels;
    }

    /**
     * Sets snap distance in pixels
     * @param snapDistanceInPixels 
     */
    public void setSnapDistanceInPixels(int snapDistanceInPixels) {
        this.snapDistanceInPixels = snapDistanceInPixels;
    }

    /**
     * Gets snapped target type
     * @return 
     */
    public SNAPPED_TARGET_TYPE getSnappedTarget() {
        return snappedTarget;
    }

    /**
     * Gets snapping point
     * @return 
     */
    public Point getSnappingPoint() {
        return snappingPoint;
    }

    /**
     * Sets the color used to draw the snapping point.
     * 
     * @param stylePointColorNoTargetFound 
     */
    public void setStylePointColorNoTargetFound(Color stylePointColorNoTargetFound) {
        this.stylePointColorNoTargetFound = stylePointColorNoTargetFound;
    }

    /**
     * This parameter is not yet used.
     * @param stylePointColorTargetFound 
     */
    public void setStylePointColorTargetFound(Color stylePointColorTargetFound) {
        this.stylePointColorTargetFound = stylePointColorTargetFound;
    }

    /**
     * It extends the functionality of the super type {@see ExtendedDrawTool}. <br/>
     * Extra functionality:
     * It calculates the snapping distance in meters and also redraws the snapping point.
     */
    @Override
    protected void afterRendering() {
        super.afterRendering();
        double widthInMeters = this.getMapControl().getDisplayArea().getSpan(0);
        int widthInPixels = this.getMapControl().getWidth();
        this.snapDistanceInMeters = widthInMeters * this.snapDistanceInPixels / widthInPixels;
        Graphics2D g2D = this.getGraphicsToDrawSnappingPoint();
        if (this.snappingPoint != null) {
            this.drawSnappingPoint(g2D, this.snappingPoint);
            //Added because otherwise it will be drawn again.
            this.snappingPoint = null;
        }
    }

    /**
     * It extends the functionality of the super type {@see ExtendedDrawTool}. <br/>
     * Extra functionality:
     * If a snapping target is found in range, it draws the snapping point to the target
     * and also keeps a reference to the snapping point. <br/>
     * If not it resets the snapping point to null.
     * @param e 
     */
    @Override
    public void onMouseMoved(MapMouseEvent e) {
        super.onMouseMoved(e);
        com.vividsolutions.jts.geom.Point newSnappingPoint =
                this.getSnappingPoint(JTS.toGeometry(e.getMapPosition()));
        if (newSnappingPoint == null) {
            if (this.snappingPoint != null) {
                Graphics2D g2D = this.getGraphicsToDrawSnappingPoint();
                this.drawSnappingPoint(g2D, this.snappingPoint);
                this.snappingPoint = null;
            }
            return;
        }
        if (newSnappingPoint != this.snappingPoint) {
            Graphics2D g2D = this.getGraphicsToDrawSnappingPoint();
            if (this.snappingPoint != null) {
                this.drawSnappingPoint(g2D, this.snappingPoint);
            }
            this.snappingPoint = newSnappingPoint;
            this.drawSnappingPoint(g2D, this.snappingPoint);
        }

    }

    /**
     * It extends the functionality of the super type {@see SolaTool}. <br/>
     * Extra functionality:
     * It removes the snapping point from the display when the tool is disactivated.
     * 
     * @param selected 
     */
    @Override
    public void onSelectionChanged(boolean selected) {
        super.onSelectionChanged(selected);
        if (!selected && this.snappingPoint != null) {
            Graphics2D g2D = this.getGraphicsToDrawSnappingPoint();
            this.drawSnappingPoint(g2D, this.snappingPoint);
            this.snappingPoint = null;
        }
    }

    /**
     * It extends the functionality of the super type {@see ExtendedDrawTool}. <br/>
     * Extra functionality:
     * If there is a snappedTarget then the snappingPoint is used to get the clicked point
     * instead of the point clicked.
     * 
     * @param ev
     * @return 
     */
    @Override
    protected DirectPosition2D getOnClickPoint(MapMouseEvent ev) {
        DirectPosition2D result;
        if (this.snappedTarget != SNAPPED_TARGET_TYPE.None) {
            result = new DirectPosition2D(this.snappingPoint.getX(), this.snappingPoint.getY());
        } else {
            result = super.getOnClickPoint(ev);
        }
        return result;
    }

    /**
     * It searches for points within the range of the mouse to snap to. 
     * Priority is given to vertexes and then lines.
     * 
     * @param mousePosition
     * @return 
     */
    private Point getSnappingPoint(Point mousePosition) {

        this.snappedTarget = SNAPPED_TARGET_TYPE.None;
        Point resultPoint = mousePosition;
        Coordinate mouseCoordinate = mousePosition.getCoordinate();
        Envelope search = new Envelope(mouseCoordinate);
        search.expandBy(this.snapDistanceInMeters);
        double distance = this.snapDistanceInMeters;
        Coordinate coordinate = null;
        ReferencedEnvelope bbox = new ReferencedEnvelope(search, null);
        for (ExtendedFeatureLayer targetLayer : this.targetSnappingLayers) {
            // Only search the layer if it is displayed onscreen and a 
            // coordinate has not been found
            if (!targetLayer.isVisible() || coordinate != null) {
                continue;
            }
            FeatureCollection featuresFound = targetLayer.getFeaturesInRange(
                    bbox, targetLayer.getFilterExpressionForSnapping());
            SimpleFeatureIterator iterator = (SimpleFeatureIterator) featuresFound.features();
            while (iterator.hasNext()) {
                SimpleFeature currentFeature = iterator.next();
                Geometry geom = (Geometry) currentFeature.getDefaultGeometry();
                if (geom == null) {
                    continue;
                }
                //Check first points
                for (Coordinate currentCoordinate : geom.getCoordinates()) {
                    double currentDistance = currentCoordinate.distance(mouseCoordinate);
                    if (currentDistance < distance) {
                        distance = currentDistance;
                        coordinate = currentCoordinate;
                    }
                }
                if (coordinate != null) {
                    this.snappedTarget = SNAPPED_TARGET_TYPE.Vertex;
                    break;
                }
                //Check linestrings
                for (LineString lineString : this.getLines(geom)) {
                    LocationIndexedLine line = new LocationIndexedLine(lineString);
                    LinearLocation linearLocation = line.project(mouseCoordinate);
                    Coordinate currentCoordinate = line.extractPoint(linearLocation);
                    double currentDistance = currentCoordinate.distance(mouseCoordinate);
                    if (currentDistance < distance) {
                        distance = currentDistance;
                        coordinate = currentCoordinate;
                    }
                }
            }
            iterator.close();
        }
        if (coordinate != null) {
            if (this.snappedTarget == SNAPPED_TARGET_TYPE.None) {
                this.snappedTarget = SNAPPED_TARGET_TYPE.Line;
            }
            resultPoint = JTS.toGeometry(new DirectPosition2D(coordinate.x, coordinate.y));
        }
        return resultPoint;
    }

    /**
     * Gives a list of lines composing a geometry
     * 
     * @param geom
     * @return 
     */
    private List<LineString> getLines(Geometry geom) {
        List<LineString> result = new ArrayList<LineString>();
        if (geom.getGeometryType().contains("Multi")) {
            for (int geomIndex = 0; geomIndex < geom.getNumGeometries(); geomIndex++) {
                result.addAll(this.getLines(geom.getGeometryN(geomIndex)));
            }
        } else if (geom.getGeometryType().equals("LineString")) {
            result.add((LineString) geom);
        } else if (geom.getGeometryType().equals("Polygon")) {
            Polygon pol = (Polygon) geom;
            result.add(pol.getExteriorRing());
            for (int geomIndex = 0; geomIndex < pol.getNumInteriorRing(); geomIndex++) {
                result.add(pol.getInteriorRingN(geomIndex));
            }
        }
        return result;
    }

    /**
     * Gets graphic component with style to draw the snapping point
     * @return 
     */
    private Graphics2D getGraphicsToDrawSnappingPoint() {
        Graphics2D g2D = (Graphics2D) this.getMapControl().getGraphics();
        g2D.setColor(Color.WHITE);
        g2D.setXORMode(this.stylePointColorNoTargetFound);
        g2D.setStroke(new BasicStroke(2));
        return g2D;
    }

    /**
     * It draws the snapping point
     * 
     * @param graphics2D
     * @param vertex 
     */
    protected void drawSnappingPoint(
            Graphics2D graphics2D, com.vividsolutions.jts.geom.Point vertex) {
        Point2D pointShape =
                this.getMapControl().getPointInScreen(
                new Point2D.Double(vertex.getX(), vertex.getY()));
        int rectWidth = 12;        
        graphics2D.drawLine( (int)pointShape.getX(), (int) pointShape.getY() - rectWidth/2, 
                (int) pointShape.getX(), (int) pointShape.getY() + rectWidth/2);
        graphics2D.drawLine( (int)pointShape.getX() - rectWidth/2, (int) pointShape.getY(), 
                (int) pointShape.getX()  + rectWidth/2, (int) pointShape.getY());
    }
}
