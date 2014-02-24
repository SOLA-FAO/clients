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
import com.vividsolutions.jts.geom.Geometry;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.geotools.feature.extended.VertexInformation;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.extended.layer.ExtendedLayerEditor;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.extended.Map;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author Elton Manoku
 */
public abstract class ExtendedEditGeometryTool extends ExtendedDrawToolWithSnapping {

    /**
     * The kinds of the statuses the tool can have
     */
    private enum TOOL_STATUS {

        Searching,
        New,
        ChangeVertix
    }
    protected ExtendedLayerEditor layer = null;
    private String layerName = null;
    private String sldResource = null;
    private String extraFieldsFormat = null;
    private TOOL_STATUS toolStatus = TOOL_STATUS.Searching;
    private com.vividsolutions.jts.geom.Point mousePointerPoint = null;
    private VertexInformation foundedVertex = null;
    private double snapVertexDistanceInMeters = 5;
    private int snapVertexDistanceInPixels = 5;
    private Color styleSnappedVertexColor = Color.GREEN;

    /**
     * It initializes the tool
     */
    public ExtendedEditGeometryTool() {
        super();
    }

    /**
     * It initializes the tool with a target layer
     * 
     * @param targetLayer 
     */
    public ExtendedEditGeometryTool(ExtendedLayerEditor targetLayer) {
        this();
        this.layer = targetLayer;
    }

    /**
     * Extra fields format added to be considered when the target layer is defined by the tool.
     * It will be used only if there is no target layer defined in the construction.
     * 
     * @param extraFieldsFormat 
     */
    public void setExtraFieldsFormat(String extraFieldsFormat) {
        this.extraFieldsFormat = extraFieldsFormat;
    }

    /**
     * Sets the style resource. The location of the resource is the same as explained in 
     * the {@see org.sola.clients.geotools.ui.layers.SolaFeatureLayer}. <br/>
     * It will be used only if there is no target layer defined in the construction.
     * 
     * @param sldResource 
     */
    public void setSldResource(String sldResource) {
        this.sldResource = sldResource;
    }

    /**
     * Gets the target layer name
     * 
     * @return 
     */
    public String getLayerName() {
        return layerName;
    }

    /**
     * Sets the target layer name. <br/>
     * It will be used only if there is no target layer defined in the construction.
     * 
     * @param layerName 
     */
    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    /**
     * It extends the functionality of the super type {@see SolaDrawTool}. <br/>
     * Extra functionality:
     * It initializes the target layer if that is missing.
     * 
     * @param mapControl 
     */
    @Override
    public void setMapControl(Map mapControl) {
        super.setMapControl(mapControl);
        this.initializeLayer();
    }


    /**
     * It extends the functionality of the super type {@see ExtendedDrawToolWithSnapping}. <br/>
     * Extra functionality:
     * If the tool is in searching mode, it searches also for vertexes in range.
     * @param e 
     */
    @Override
    public void onMouseMoved(MapMouseEvent e) {
        if (this.toolStatus == TOOL_STATUS.Searching) {
            this.mouseMovedWhileSearching(e);
        }
        super.onMouseMoved(e);
    }

    /**
     * It implements the dragging of a found vertex to a new position
     * @param e 
     */
    @Override
    public void onMouseDragged(MapMouseEvent e) {
        super.onMouseMoved(e);
        if (this.toolStatus == TOOL_STATUS.ChangeVertix
                && this.foundedVertex != null) {
            DirectPosition2D pos = e.getMapPosition();
            Graphics2D g2D = this.getGraphicsToDrawSnappedVertex();
            if (this.mousePointerPoint != null) {
                this.drawSnappedVertex(g2D, this.mousePointerPoint.getCoordinate());
            }
            this.mousePointerPoint =
                    this.geometryFactory.createPoint(new Coordinate(pos.getX(), pos.getY()));
            this.drawSnappedVertex(g2D, this.mousePointerPoint.getCoordinate());
        }
    }

    /**
     * If vertex is found it sets the tool mode in ChangeVertex.
     * @param ev 
     */
    @Override
    public void onMousePressed(MapMouseEvent ev) {
        if (this.foundedVertex != null) {
            this.setToolStatus(TOOL_STATUS.ChangeVertix);
            Graphics2D g2D = this.getGraphicsToDrawSnappedVertex();
            this.drawSnappedVertex(g2D, this.foundedVertex.getVertex());
        }
    }

    /**
     * When mouse released and if tool in change vertex mode, it calls the finalizing method 
     * to change the vertex.
     * 
     * @param ev 
     */
    @Override
    public void onMouseReleased(MapMouseEvent ev) {
        if (this.toolStatus == TOOL_STATUS.ChangeVertix) {
            if (this.mousePointerPoint != null) {
                Graphics2D g2D = this.getGraphicsToDrawSnappedVertex();
                this.drawSnappedVertex(g2D, this.mousePointerPoint.getCoordinate());
                this.mousePointerPoint = null;
            }
            if (this.foundedVertex != null) {
                this.treatChangeVertex(this.getOnClickPoint(ev));
                this.foundedVertex = null;
            }
            this.setToolStatus(TOOL_STATUS.Searching);
        }
    }

    /**
     * The finalize method to change the vertex.
     * 
     * @param mousePositionInMap 
     */
    protected SimpleFeature treatChangeVertex(DirectPosition2D mousePositionInMap){
        return this.layer.changeVertex(this.foundedVertex, mousePositionInMap);        
    }
    
    /**
     * It extends the functionality of the super type {@see ExtendedDrawToolWithSnapping}. <br/>
     * Extra functionality:
     * It calculates the range in meters to look for vertexes.
     */
    @Override
    protected void afterRendering() {
        this.foundedVertex = null;
        super.afterRendering();
        double widthInMeters = this.getMapControl().getDisplayArea().getSpan(0);
        int widthInPixels = this.getMapControl().getWidth();
        this.snapVertexDistanceInMeters = 
                widthInMeters * this.snapVertexDistanceInPixels / widthInPixels;
    }

    /**
     * It extends the functionality of the super type {@see ExtendedDrawToolWithSnapping}. <br/>
     * Extra functionality:
     * It changes the status of the tool to New.
     * @param ev 
     */
    @Override
    protected void onSingleClick(MapMouseEvent ev) {
        this.setToolStatus(TOOL_STATUS.New);
        super.onSingleClick(ev);
    }

    /**
     * It calls adding the feature method to the target layer.
     * 
     * @param geometry
     */
    @Override
    protected void treatFinalizedGeometry(Geometry geometry){
        this.addFeature(geometry);
    }

    /**
     * It extends the functionality of the super type {@see ExtendedDrawToolWithSnapping}. <br/>
     * Extra functionality:
     * It sets the status to Searching and resets any found vertex.
     * 
     * @param onlyCurrent 
     */
    @Override
    protected void resetDrawing(boolean onlyCurrent) {
        super.resetDrawing(onlyCurrent);
        this.foundedVertex = null;
        if (!onlyCurrent) {
            this.setToolStatus(TOOL_STATUS.Searching);
        }
    }

    /**
     * It adds a feature in the target layer. It handles only the case if the layer does not expect
     * any extra fields and the fid will be generated automatically.
     * If there are extra fields then this method has to be overridden.
     * @param geometry 
     */
    public SimpleFeature addFeature(Geometry geometry){
        return this.layer.addFeature(null, geometry, null, true);
    }

    /**
     * It initializes the target layer if that is missing.
     */
    private void initializeLayer() {
        if (this.layer != null) {
            return;
        }
        if (sldResource == null) {
            sldResource = String.format(
                    "editor_%s.xml", this.getGeometryType().getSimpleName().toLowerCase());
        }
        this.layer = (ExtendedLayerEditor) this.getMapControl().addLayerEditor(
                this.layerName, this.layerName, 
                this.getGeometryType(), sldResource, extraFieldsFormat);
    }

    /**
     * Gets the graphics to draw the snapped vertex while searching for vertexes. It also gives
     * the style.
     * 
     * @return 
     */
    private Graphics2D getGraphicsToDrawSnappedVertex() {
        Graphics2D g2D = (Graphics2D) this.getMapControl().getGraphics();
        g2D.setColor(Color.WHITE);
        g2D.setXORMode(this.styleSnappedVertexColor);
        g2D.setStroke(new BasicStroke(1));
        return g2D;
    }

    /**
     * It searches for vertexes within range that are in target layer.
     * @param e 
     */
    private void mouseMovedWhileSearching(MapMouseEvent e) {
        VertexInformation vertexFound = this.layer.getFirstVertexWithinDistance(
                e.getMapPosition(), this.snapVertexDistanceInMeters);
        if (vertexFound == null) {
            if (this.foundedVertex != null) {
                Graphics2D g2D = this.getGraphicsToDrawSnappedVertex();
                this.drawSnappedVertex(g2D, this.foundedVertex.getVertex());
                this.foundedVertex = null;
            }
            return;
        }
        if (vertexFound != this.foundedVertex) {
            Graphics2D g2D = this.getGraphicsToDrawSnappedVertex();
            if (this.foundedVertex != null) {
                this.drawSnappedVertex(g2D, this.foundedVertex.getVertex());
            }
            this.foundedVertex = vertexFound;
            this.drawSnappedVertex(g2D, this.foundedVertex.getVertex());
        }
    }

    /**
     * It draws the vertex that is found.
     * 
     * @param graphics2D
     * @param vertex 
     */
    private void drawSnappedVertex(
            Graphics2D graphics2D, Coordinate vertex) {
        Point2D pointShape =
                this.getMapControl().getPointInScreen(new Point2D.Double(vertex.x, vertex.y));
        int rectWidth = this.snapVertexDistanceInPixels * 3;
        Rectangle rect = new Rectangle(
                (int) pointShape.getX() - rectWidth / 2,
                (int) pointShape.getY() - rectWidth / 2,
                rectWidth, rectWidth);
        graphics2D.fill(rect);
    }

    /**
     * It changes the tool status.
     * @param toolStatus 
     */
    private void setToolStatus(TOOL_STATUS toolStatus) {
        this.toolStatus = toolStatus;
    }
}
