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
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.GeometryInvalidException;
import org.geotools.swing.extended.util.Messaging;

/**
 * Map tool that implements drawing in the map control. Based in configuration it can draw,
 * point, line, polygon (also with holes). <br/>
 * It supports also zoom in, out, panning while drawing. <br/>
 * The drawing is happening in the graphics of the map control component.
 *
 * @author Elton Manoku
 */
public abstract class ExtendedDrawTool extends ExtendedTool {

    private Point2D startPos1 = null;
    private Point2D startPos2 = null;
    private Point2D movingPos = null;
    private Geometries geometryType = Geometries.LINESTRING;
    private CoordinateList currentCoordinates = new CoordinateList();
    private ArrayList<CoordinateList> currentCoordinatesList = new ArrayList<CoordinateList>();
    private boolean afterReprojection = false;
    private Color styleTemporaryLinesColor = Color.RED;
    //This has to be always an even number (2,4,6, etc)
    private int styleTemporaryLinesStrokeWidth = 2;

    /**
     * Factory used for building the geometry from the drawing
     */
    protected GeometryFactory geometryFactory = null;

    /**
     * It initializes the tool
     */
    public ExtendedDrawTool() {
        super();
        if (this.geometryFactory == null) {
            this.geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        }
    }

    /**
     * @return the geometryType
     */
    public Geometries getGeometryType() {
        return geometryType;
    }

    /**
     * @param geometryType the geometryType to set
     */
    public void setGeometryType(Geometries geometryType) {
        this.geometryType = geometryType;
    }

    /**
     * @param styleTemporaryLinesColor the styleTemporaryLinesColor to set
     */
    public void setStyleTemporaryLinesColor(Color styleTemporaryLinesColor) {
        this.styleTemporaryLinesColor = styleTemporaryLinesColor;
    }

    /**
     * @param styleTemporaryLinesStrokeWidth the styleTemporaryLinesStrokeWidth to set
     */
    public void setStyleTemporaryLinesStrokeWidth(int styleTemporaryLinesStrokeWidth) {
        this.styleTemporaryLinesStrokeWidth = styleTemporaryLinesStrokeWidth;
    }

    /**
     * It sets the map control and initializes listeners
     * @param mapControl 
     */
    @Override
    public void setMapControl(Map mapControl) {
        super.setMapControl(mapControl);
        this.addMapListenerEvents();
    }

    /**
     * It is called when the mouse is moved. It redraws the lines as the mouse moves.
     * @param e Mouse event data
     */
    @Override
    public void onMouseMoved(MapMouseEvent e) {
        if (this.getGeometryType() == Geometries.POINT) {
            return;
        }
        if (this.startPos1 == null) {
            return;
        }

        Graphics2D g2D = this.getGraphicsToDraw();
        if (this.movingPos == null) {
            this.movingPos = this.getPointShape(e.getPoint());
            return;
        }
        if (!this.afterReprojection) {
            this.drawTemporaryLines(g2D);
        } else {
            this.afterReprojection = false;
        }
        this.movingPos = this.getPointShape(e.getPoint());
        this.drawTemporaryLines(g2D);
    }

    /**
     * It is called when the mouse is clicked or double clicked.
     * It signalizes the end of a line or completion of a geometry depending in the 
     * type of geometry. <br/>
     * If right click it resets the drawing.
     * @param ev 
     */
    @Override
    public void onMouseClicked(MapMouseEvent ev) {

        if (ev.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            if (ev.getClickCount() == 2) {
                this.onDoubleClick(ev);
            } else if (ev.getClickCount() == 1) {
                this.onSingleClick(ev);
            }
        } else if (ev.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            this.resetDrawing(false);
        }
    }
    
    /**
     * It is called after the rendering of the map control. It is used to redraw the temporary 
     * lines already drawn but reprojected while the map has been panned or zoomed.
     * 
     */
    protected void afterRendering() {
        this.startPos1 = this.startPos2 = this.movingPos = null;
        this.reprojectTemporaryDrawing();
        this.afterReprojection = true;
    }

    /**
     * Gets the mouse position in the map
     * @param ev
     * @return 
     */
    protected DirectPosition2D getOnClickPoint(MapMouseEvent ev){
        return ev.getMapPosition();
    }
    
    /**
     * Used when clicked once in the map. When type of geometry to be clicked is Point it means
     * it is the completion of geometry, If line or polygon it means an extra line is added.
     * @param ev 
     */
    protected void onSingleClick(MapMouseEvent ev) {
        
        DirectPosition2D pos = this.getOnClickPoint(ev);
        Point2D newPos = this.getMapControl().getPointInScreen(pos);
        
        if (this.getGeometryType() == Geometries.POINT) {
            com.vividsolutions.jts.geom.Point newGeometry =
                    this.geometryFactory.createPoint(new Coordinate(pos.getX(), pos.getY()));
            this.finalizeGeometry(newGeometry);
        } else {
            this.currentCoordinates.add(new Coordinate(pos.getX(), pos.getY()));
            this.startPos1 = newPos;
            if (this.geometryType == Geometries.POLYGON && this.startPos2 == null) {
                this.startPos2 = new Point2D.Double(this.startPos1.getX(), this.startPos1.getY());
            }
        }
    }

    /**
     * This is called if the geometry is finalized. It can be overridden by subtypes of this class
     * for different purposes.
     * @param geometry 
     */
    protected void treatFinalizedGeometry(Geometry geometry){
    }

    /**
     * It resets the drawing in the graphic component
     * @param onlyCurrent 
     */
    protected void resetDrawing(boolean onlyCurrent) {
        this.startPos1 = this.startPos2 = this.movingPos = null;
        //this.foundedVertex = null;
        this.currentCoordinates.clear();
        if (!onlyCurrent) {
            this.currentCoordinatesList.clear();
            this.getMapControl().refresh();
        }
    }

    /**
     * Called when double clicked. It is used when drawing polylines or polygon. If geometry is 
     * completed then it creates a geometry, validates it and calls the finalize method.
     * @param ev 
     */
    private void onDoubleClick(MapMouseEvent ev) {
        Geometry currentGeometry = null;
        if (this.getGeometryType() == Geometries.LINESTRING) {
            int coordsSize = this.currentCoordinates.size();
            if (coordsSize == 1) {
                Messaging.getInstance().show(
                        Messaging.Ids.DRAWINGTOOL_NOT_ENOUGH_POINTS_INFORMATIVE.toString());
                return;
            }
            currentGeometry = this.geometryFactory.createLineString(
                    this.currentCoordinates.toCoordinateArray());
        } else if (this.getGeometryType() == Geometries.POLYGON) {
            int coordsSize = this.currentCoordinates.size();
            if (coordsSize < 3) {
                Messaging.getInstance().show(
                        Messaging.Ids.DRAWINGTOOL_NOT_ENOUGH_POINTS_INFORMATIVE.toString());
                return;
            }
            CoordinateList tmpCoordinateList =
                    new CoordinateList(this.currentCoordinates.toCoordinateArray());
            tmpCoordinateList.closeRing();
            this.currentCoordinatesList.add(tmpCoordinateList);
            if (ev.isControlDown()) {
                this.resetDrawing(true);
            } else {
                LinearRing outerRing = null;
                ArrayList<LinearRing> innerRings = new ArrayList<LinearRing>();
                for (int ringIndex = 0; ringIndex < this.currentCoordinatesList.size(); ringIndex++) {
                    LinearRing ring = this.geometryFactory.createLinearRing(
                            this.currentCoordinatesList.get(ringIndex).toCoordinateArray());
                    if (ringIndex == 0) {
                        outerRing = ring;
                    } else {
                        innerRings.add(ring);
                    }
                }
                currentGeometry = this.geometryFactory.createPolygon(
                        outerRing, (LinearRing[]) innerRings.toArray(
                        new LinearRing[innerRings.size()]));
            }
        }
        if (currentGeometry != null) {
            this.finalizeGeometry(currentGeometry);
        }
    }

    /**
     * It adds Map listeners to the tool. It catches the moment after the rendering stops.
     */
    private void addMapListenerEvents() {
        MapPaneAdapter mapPaneListener = new MapPaneAdapter() {

            @Override
            public void onRenderingStopped(MapPaneEvent ev) {
                afterRendering();
            }
        };
        this.getMapControl().addMapPaneListener(mapPaneListener);
    }

    /**
     * Gets the graphic component where the drawing is happening
     * @return 
     */
    private Graphics2D getGraphicsToDraw() {
        Graphics2D g2D = (Graphics2D) this.getMapControl().getGraphics();
        this.setStyleForTemporaryLines(g2D);
        return g2D;
    }

    /**
     * If there are already lines drawing it can redraw them to the map control. Used after the 
     * display area of the map is changed while panning or zooming.
     */
    private void reprojectTemporaryDrawing() {
        Graphics2D g2D = this.getGraphicsToDraw();
        this.reprojectTemporaryDrawing(g2D, this.currentCoordinates, true);
        for (CoordinateList coordinateList : this.currentCoordinatesList) {
            this.reprojectTemporaryDrawing(g2D, coordinateList, false);
        }
    }

    /**
     * Reproject a polyline defined by the coordinate list
     * @param g2D The drawing component
     * @param coordinates The list of coordinates
     * @param setStartPos If the start position has to be to the first coordinate or not if relevant.
     * It is relevant when it is a non finished polygon.
     * 
     */
    private void reprojectTemporaryDrawing(
            Graphics2D g2D, CoordinateList coordinates, boolean setStartPos) {

        Point2D startFrom = null;
        for (Coordinate coordinate : coordinates.toCoordinateArray()) {
            Point2D currentPoint = this.getMapControl().getPointInScreen(
                    new Point2D.Double(coordinate.x, coordinate.y));
            if (startFrom == null) {
                startFrom = currentPoint;
                if (this.geometryType == Geometries.POLYGON) {
                    if (setStartPos) {
                        this.startPos2 = currentPoint;
                    }
                }
            } else {
                Line2D line = new Line2D.Double(startFrom, currentPoint);
                g2D.draw(line);
                startFrom = currentPoint;
            }
        }
        if (setStartPos && startFrom != null) {
            this.startPos1 = startFrom;
        }
    }

    /**
     * Draws the temporary line while the mouse is moving.
     * @param g2D 
     */
    private void drawTemporaryLines(Graphics2D g2D) {
        Line2D line;
        if (this.startPos1 != null && this.movingPos != null) {
            line = new Line2D.Double(this.startPos1, this.movingPos);
            g2D.draw(line);
        }
        if (this.startPos2 != null && this.startPos1 != this.startPos2) {
            line = new Line2D.Double(this.startPos2, this.movingPos);
            g2D.draw(line);
        }

    }

    /**
     * It finalizes the geometry created by drawing. It normalizes it, it checks if it is valid
     * and then calls the treating method.
     * @param geometry 
     */
    private void finalizeGeometry(Geometry geometry) {
        try {
            geometry.normalize();
            if (!geometry.isValid()) {
                throw new GeometryInvalidException(null);
            }
            this.treatFinalizedGeometry(geometry);
            this.resetDrawing(false);
        } catch (GeometryInvalidException ex) {
            Messaging.getInstance().show(ex.getMessage());
        }
    }

    /**
     * Sets the color of the temporary lines
     * @param graphics2D The graphic component where the lines will be drawn
     */
    private void setStyleForTemporaryLines(Graphics2D graphics2D) {
        graphics2D.setColor(Color.WHITE);
        graphics2D.setXORMode(this.styleTemporaryLinesColor);
        graphics2D.setStroke(new BasicStroke(this.styleTemporaryLinesStrokeWidth));
    }

    /**
     * Gets a clone of the point
     * @param point
     * @return 
     */
    private Point2D getPointShape(Point2D point) {
        return (Point2D) point.clone();
    }
}