/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.geotools.ui.maptools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.geotools.ui.Map;
import org.sola.clients.geotools.ui.layers.SolaLayerEditor;
import org.sola.clients.geotools.ui.layers.VertexInformation;

/**
 *
 * @author manoku
 */
public class SolaEditGeometryTool extends SolaDrawToolWithSnapping {

    /**
     * The kinds of the statuses the tool can have
     */
    private enum TOOL_STATUS {

        Searching,
        New,
        ChangeVertix
    }
    protected SolaLayerEditor layer = null;
    private String layerName = null;
    private String sldResource = null;
    private String extraFieldsFormat = null;
    private TOOL_STATUS toolStatus = TOOL_STATUS.Searching;
    private com.vividsolutions.jts.geom.Point mousePointerPoint = null;
    private VertexInformation foundedVertex = null;
    private double snapVertexDistanceInMeters = 5;
    private int snapVertexDistanceInPixels = 5;
    private Color styleSnappedVertexColor = Color.GREEN;

    public SolaEditGeometryTool() {
        super();
    }

    public SolaEditGeometryTool(SolaLayerEditor targetLayer) {
        this();
        this.layer = targetLayer;
    }

    public void setExtraFieldsFormat(String extraFieldsFormat) {
        this.extraFieldsFormat = extraFieldsFormat;
    }

    public void setSldResource(String sldResource) {
        this.sldResource = sldResource;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    @Override
    public void setMapControl(Map mapControl) {
        super.setMapControl(mapControl);
        this.initializeLayer();
        //this.getTargetSnappingLayers().add(0, layer);
    }

    private void initializeLayer() {
        if (this.layer != null) {
            return;
        }
        if (sldResource == null) {
            sldResource = String.format(
                    "editor_%s.sld", this.getGeometryType().getSimpleName().toLowerCase());
        }
        this.layer = (SolaLayerEditor) this.getMapControl().addLayerEditor(
                this.layerName, this.getGeometryType(), sldResource, extraFieldsFormat);
    }

    private Graphics2D getGraphicsToDrawSnappedVertex() {
        Graphics2D g2D = (Graphics2D) this.getMapControl().getGraphics();
        g2D.setColor(Color.WHITE);
        g2D.setXORMode(this.styleSnappedVertexColor);
        g2D.setStroke(new BasicStroke(1));
        return g2D;
    }

    private void mouseMovedWhileSearching(MapMouseEvent e) {
        VertexInformation vertexFound =
                this.layer.getFirstVertexWithinDistance(e.getMapPosition(), this.snapVertexDistanceInMeters);
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

    @Override
    public void onMouseMoved(MapMouseEvent e) {
        if (this.toolStatus == TOOL_STATUS.Searching) {
            this.mouseMovedWhileSearching(e);
        }
        super.onMouseMoved(e);
    }

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

    @Override
    public void onMousePressed(MapMouseEvent ev) {
        if (this.foundedVertex != null) {
            this.setToolStatus(TOOL_STATUS.ChangeVertix);
            Graphics2D g2D = this.getGraphicsToDrawSnappedVertex();
            this.drawSnappedVertex(g2D, this.foundedVertex.getVertex());
        }
    }

    private void setToolStatus(TOOL_STATUS toolStatus) {
        this.toolStatus = toolStatus;
    }

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
     * This method can be overridden by the subtypes.
     * @param mousePositionInMap 
     */
    protected SimpleFeature treatChangeVertex(DirectPosition2D mousePositionInMap){
        return this.layer.changeVertex(this.foundedVertex, mousePositionInMap);        
    }
    
    @Override
    protected void afterRendering() {
        this.foundedVertex = null;
        super.afterRendering();
        double widthInMeters = this.getMapControl().getDisplayArea().getSpan(0);
        int widthInPixels = this.getMapControl().getWidth();
        this.snapVertexDistanceInMeters = widthInMeters * this.snapVertexDistanceInPixels / widthInPixels;
    }

    @Override
    protected void onSingleClick(MapMouseEvent ev) {
        this.setToolStatus(TOOL_STATUS.New);
        super.onSingleClick(ev);
    }

    @Override
    protected void treatFinalizedGeometry(Geometry geometry) throws Exception {
        this.addFeature(geometry);
    }

    @Override
    protected void resetDrawing(boolean onlyCurrent) {
        super.resetDrawing(onlyCurrent);
        this.foundedVertex = null;
        if (!onlyCurrent) {
            this.setToolStatus(TOOL_STATUS.Searching);
        }
    }

    /**
     * This method can be overridden by subtypes.
     * @param geometry 
     */
    public SimpleFeature addFeature(Geometry geometry) throws Exception {
        return this.layer.addFeature(null, geometry, null);
    }
}
