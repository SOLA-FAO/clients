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
package org.sola.clients.geotools.ui;

import org.geotools.factory.Hints;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.referencing.NamedIdentifier;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.swing.tool.CursorTool;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.Layer;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;
import org.sola.clients.geotools.util.Messaging;
import org.sola.clients.geotools.ui.layers.SolaLayer;
import org.sola.clients.geotools.ui.layers.SolaLayerEditor;
import org.sola.clients.geotools.ui.layers.SolaLayerGraphics;
import org.sola.clients.geotools.ui.layers.SolaLayerShapefile;
import org.sola.clients.geotools.ui.layers.SolaLayerWMS;
import org.sola.clients.geotools.ui.mapactions.SolaAction;
import org.sola.clients.geotools.ui.maptools.SolaTool;

/**
 * This is an extension of the swing control {@see JMapPane}. Added functionality includes:
 * - Initialization of the control only by a SRID. It uses the StreamingRenderer for rendering and
 *      DefaultMapContext for the map context
 * - Helper methods to add different kind of layers
 * - Adding OnMouseWheel Zoom in/out handling
 * - Adding panning with by holding the middle button of the mouse pressed
 * - Get pixel resolution for the map control
 * - Helper methods for transforming points map from/to screen
 * - Defining a custom full extent that is not dependent in the full extent of all layers found in the map
 * @author manoku (Date July 2011)
 * 
 */
public class Map extends JMapPane {

    private static CRSAuthorityFactory factory = null;
    private java.awt.Point panePos = null;
    private boolean panning = false;
    private boolean isRendering = false;
    private MapMouseAdapter mapMouseAdapter;
    private MapPaneAdapter mapPaneListener;
    private ReferencedEnvelope fullExtentEnvelope;
    private LinkedHashMap<String, SolaLayer> solaLayers = new LinkedHashMap<String, SolaLayer>();
    private Integer srid = null;
    private ButtonGroup toolsGroup = new ButtonGroup();
    private double pixelResolution = 0;
    private Toc toc;
    private ReferencedEnvelope currentExtent = null;
    private CursorTool activeTool = null;

    /**
     * This constructor is used only for the graphical designer.
     * Use the other constructor for initializing the map control
     */
    public Map() {
        super();
        this.setBackground(Color.WHITE);
        this.createListeners();
    }

    /**
     * It initializes the map control given an SRID.
     * @param srid The SRID
     * @throws Exception 
     */
    public Map(int srid) throws Exception {
        this();
        this.initialize(srid);
    }

    private void initialize(int srid) throws Exception {
        this.srid = srid;
        MapContext context = new DefaultMapContext();
        context.setCoordinateReferenceSystem(
                this.generateCoordinateReferenceSystem(srid));
        if (context.getCoordinateReferenceSystem() == null) {
            throw new Exception(
                    Messaging.Ids.MAPCONTROL_MAPCONTEXT_WITHOUT_SRID_ERROR.toString());
        }
        GTRenderer renderer = new StreamingRenderer();
        this.setRenderer(renderer);
        this.setMapContext(context);
    }

    @Override
    public void setCursorTool(CursorTool tool) {
        if (this.activeTool != null && this.activeTool instanceof SolaTool) {
            ((SolaTool) this.activeTool).onSelectionChanged(false);
        }
        super.setCursorTool(tool);
        this.activeTool = tool;
        if (this.activeTool != null && this.activeTool instanceof SolaTool) {
            ((SolaTool) this.activeTool).onSelectionChanged(true);
        }
        this.setFocusable(true);
    }

    private CoordinateReferenceSystem generateCoordinateReferenceSystem(int srid)
            throws Exception {
        CoordinateReferenceSystem coordSys = null;
        try {
            // Setting the system-wide default at startup time
            System.setProperty("org.geotools.referencing.forceXY", "true");
            Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
            if (factory == null) {
                factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
            }
            coordSys = factory.createCoordinateReferenceSystem(String.format("EPSG:%s", srid));
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.UTILITIES_COORDSYS_COULDNOT_BE_CREATED_ERROR.toString(), ex);
        }
        return coordSys;
    }

    /**
     * Initialize the mouse and map bounds listeners. The map wheel listener are initialized 
     * separately because the ones provided by mapMouseAdapter gives a null location
     */
    private void createListeners() {

        this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {

            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                handleMouseWheelEvent(evt);
            }
        });

        this.mapMouseAdapter = new MapMouseAdapter() {

            @Override
            public void onMouseDragged(MapMouseEvent mme) {
                handleMouseDragged(mme);
            }

            @Override
            public void onMousePressed(MapMouseEvent mme) {
                handleMousePressed(mme);
            }

            @Override
            public void onMouseReleased(MapMouseEvent mme) {
                handleMouseReleased(mme);
            }
        };

        this.mapPaneListener = new MapPaneAdapter() {

            @Override
            public void onRenderingStarted(MapPaneEvent ev) {
                isRendering = true;
            }

            @Override
            public void onRenderingStopped(MapPaneEvent ev) {
                isRendering = false;
            }

            @Override
            public void onDisplayAreaChanged(MapPaneEvent ev) {
                handleOnDisplayAreaChanged(ev);
            }
        };
        this.addMouseListener(this.mapMouseAdapter);
        this.addMapPaneListener(this.mapPaneListener);
    }

    /**
     * It zooms in and out as the user uses the mousewheel. The mouse stays pointing in the 
     * same map coordinates
     * @param ev 
     */
    private void handleMouseWheelEvent(MouseWheelEvent ev) {
        if (this.IsRendering()) {
            return;
        }
        double zoomFactor = 0.5;
        int clicks = ev.getWheelRotation();
        // -ve means wheel moved up, +ve means down
        int sign = (clicks > 0 ? -1 : 1);

        zoomFactor = sign * zoomFactor / 2;

        Point2D mouseMapPointPos = this.getPointInMap(ev.getPoint());

        ReferencedEnvelope env = this.getDisplayArea();

        double width = env.getSpan(0);
        double height = env.getSpan(1);
        double newWidth = width - (width * zoomFactor);
        double newHeight = height - (height * zoomFactor);

        double centerX = env.getMedian(0);
        double centerY = env.getMedian(1);

        double distanceMouseCenterAlongX = mouseMapPointPos.getX() - centerX;
        double distanceMouseCenterAlongY = mouseMapPointPos.getY() - centerY;
        centerX += distanceMouseCenterAlongX * zoomFactor;
        centerY += distanceMouseCenterAlongY * zoomFactor;

        double newMinX = centerX - newWidth / 2;
        double newMinY = centerY - newHeight / 2;
        double newMaxX = centerX + newWidth / 2;
        double newMaxY = centerY + newHeight / 2;
        env = new ReferencedEnvelope(
                newMinX, newMaxX, newMinY, newMaxY, env.getCoordinateReferenceSystem());

        this.setDisplayArea(env);
        this.refresh();
    }

    /**
     * It catches the start of the pan process
     * @param ev 
     */
    public void handleMousePressed(MapMouseEvent ev) {
        if (ev.getButton() == java.awt.event.MouseEvent.BUTTON2) {
            panePos = ev.getPoint();
            panning = true;
        }
    }

    /**
     * Respond to a mouse dragged event. Calls {@link org.geotools.swing.JMapPane#moveImage()}
     * @param ev the mouse event
     */
    public void handleMouseDragged(MapMouseEvent ev) {
        if (this.panning) {
            java.awt.Point pos = ev.getPoint();
            if (!pos.equals(this.panePos)) {
                this.moveImage(pos.x - this.panePos.x, pos.y - this.panePos.y);
                this.panePos = pos;
            }
        }
    }

    /**
     * If this button release is the end of a mouse dragged event, requests the
     * map mapPane to repaint the display
     * @param ev the mouse event
     */
    public void handleMouseReleased(MapMouseEvent ev) {
        if (this.panning) {
            panning = false;
            this.refresh();
        }
    }

    /**
     * It updates the pixel resolution of the map for each change of the DisplayArea.
     * @param ev 
     */
    public void handleOnDisplayAreaChanged(MapPaneEvent ev) {
        ReferencedEnvelope newDisplayArea = this.getDisplayArea();
        double widthInMap = newDisplayArea.getSpan(0);
        double widthInPixels = this.getSize().getWidth();
        this.pixelResolution = widthInMap / widthInPixels;
    }


    /**
     * It maintains also the display area while the control resized.
     * 
     */
    @Override
    protected void onResizingCompleted() {
        if (this.pendingDisplayArea == null){
            this.pendingDisplayArea = this.getDisplayArea();
        }
        super.onResizingCompleted();
    }
    
    /**
     * Gets the current pixel resolution of the map
     * @return 
     */
    public double getPixelResolution() {
        return this.pixelResolution;
    }

    /**
     * Get the point in screen coordinates
     * @param mapPoint The point in map coordinates
     * @return 
     */
    public Point2D getPointInScreen(Point2D mapPoint) {
        Point2D screenPoint = new Point2D.Double(mapPoint.getX(), mapPoint.getY());
        this.getWorldToScreenTransform().transform(screenPoint, screenPoint);
        return screenPoint;
    }

    /**
     * Get the point in map coordinates
     * @param screenPoint The point in screen coordinates
     * @return 
     */
    public Point2D getPointInMap(Point2D screenPoint) {
        Point2D mapPoint = new Point2D.Double(screenPoint.getX(), screenPoint.getY());
        this.getScreenToWorldTransform().transform(mapPoint, mapPoint);
        return mapPoint;
    }

    /**
     * Gets the fact that the map is busy rendering
     * @return the isRendering
     */
    public boolean IsRendering() {
        return isRendering;
    }

    /**
     * It zooms to the full extent. 
     */
    public void zoomToFullExtent() {
        this.setDisplayArea(this.getFullExtent());
    }

    /**
     * It sets the full extent of the map control.
     * @param east
     * @param west
     * @param north
     * @param south 
     */
    public void setFullExtent(double east, double west, double north, double south) {
        this.fullExtentEnvelope = new ReferencedEnvelope(
                west, east, south, north, this.getMapContext().getCoordinateReferenceSystem());
    }

    @Override
    protected void setFullExtent() {
        if (this.getFullExtent() != null) {
            this.fullExtent = this.getFullExtent();
        } else {
            super.setFullExtent();
        }
    }

    /**
     * Gets the full extent.
     * If the full extent is not yet set then the full extent of all layers in the map is used.     * 
     * @return 
     */
    public ReferencedEnvelope getFullExtent() {
        if (this.fullExtentEnvelope == null) {
            this.reset();
            this.fullExtentEnvelope = this.getDisplayArea();
        }
        return this.fullExtentEnvelope;
    }

    /**
     * Gets the srid of the map
     * @return 
     */
    public int getSrid() {
        if (srid == null) {
            srid = -1;
            try {
                Object[] identifiers =
                        this.getMapContext().getCoordinateReferenceSystem().getIdentifiers().toArray();
                srid = Integer.parseInt(((NamedIdentifier) identifiers[0]).getCode());

            } finally {
            }
        }
        return srid;
    }

    /**
     * Gets the list of SolaLayers loaded in the map
     * @return 
     */
    public LinkedHashMap<String, SolaLayer> getSolaLayers() {
        return this.solaLayers;
    }

    /**
     * It adds a SolaLayer
     * @param solaLayer the SolaLayer
     * @return 
     */
    public SolaLayer addLayer(SolaLayer solaLayer) {
        try {
            this.getSolaLayers().put(solaLayer.getLayerName(), solaLayer);
            for (Layer layer : solaLayer.getMapLayers()) {
                this.getMapContext().addLayer(layer);
                //this.getMapContext().addLayer(0, new MapLayer(layer));
            }
            solaLayer.setMapControl(this);
            if (this.getToc() != null && solaLayer.isShowInToc()) {
                this.getToc().addLayer(solaLayer);
            }
        } catch (Exception ex) {
            Messaging.getInstance().show(ex.getMessage());
        }
        return solaLayer;
    }

    /**
     * It adds a layer of type WMS which is actually a WMS Server with a list of layers in it.
     * @param layerName The name
     * @param URL The WMS Capabilities Location
     * @param layerNames The list of layer names
     * @return 
     */
    public boolean addLayerWMS(String layerName, String URL, List<String> layerNames) {
        boolean addedSuccessfully = true;
        try {
            SolaLayerWMS solaLayerWMS = new SolaLayerWMS(layerName, this, URL, layerNames);
            this.getSolaLayers().put(layerName, solaLayerWMS);
        } catch (Exception ex) {
            addedSuccessfully = false;
            Messaging.getInstance().show(ex.getMessage());
        }
        return addedSuccessfully;
    }

    /**
     * It adds a shapefile layer
     * @param layerName the layer name
     * @param pathOfShapefile the path of the .shp file
     * @param styleResource the resource name .sld in the location of resources for layer styles.
     * This resource location is added in {@see Utility} sld resources list. By default there is one
     * /org/sola/clients/geotools/ui/layers/resources/
     * @return 
     */
    public boolean addLayerShapefile(
            String layerName, String pathOfShapefile, String styleResource) {
        boolean addedSuccessfully = true;
        try {
            SolaLayerShapefile solaLayerShapefile =
                    new SolaLayerShapefile(layerName, pathOfShapefile, styleResource);
            this.addLayer(solaLayerShapefile);
        } catch (Exception ex) {
            addedSuccessfully = false;
            Messaging.getInstance().show(ex.getMessage());
        }
        return addedSuccessfully;
    }

    /**
     * It adds a graphics layer in the map
     * @param layerName layer name
     * @param geometryType geometry type for this layer
     * @param styleResource the resource name .sld in the location of resources for layer styles.
     * This resource location is added in {@see Utility} sld resources list. By default there is one
     * /org/sola/clients/geotools/ui/layers/resources/
     * @return 
     */
    public boolean addLayerGraphics(
            String layerName, Geometries geometryType,
            String styleResource) {
        boolean addedSuccessfully = true;
        try {
            SolaLayerGraphics layer = new SolaLayerGraphics(
                    layerName, geometryType, this.getSrid(), styleResource);
            this.addLayer(layer);
        } catch (Exception ex) {
            addedSuccessfully = false;
            Messaging.getInstance().show(ex.getMessage());
        }
        return addedSuccessfully;
    }

    /**
     * It adds a editor layer in the map. 
     * @param layerName layer name
     * @param geometryType geometry type for this layer
     * @param styleResource the resource name .sld in the location of resources for layer styles.
     * This resource location is added in {@see Utility} sld resources list. By default there is one
     * /org/sola/clients/geotools/ui/layers/resources/
     * @return 
     */
    public SolaLayer addLayerEditor(
            String layerName, Geometries geometryType,
            String styleResource, String extraFieldsFormat) {
        SolaLayerEditor layer = null;
        try {
            layer = new SolaLayerEditor(
                    layerName, geometryType, this.getSrid(), styleResource, extraFieldsFormat);
            this.addLayer(layer);
        } catch (Exception ex) {
            Messaging.getInstance().show(ex.getMessage());
        }
        return layer;
    }

    /**
     * It adds an action of a control (usually a button) in the toolbar.
     * @param action the action to be added
     * @param hasTool if the action activates a map tool
     * @param inToolbar the toolbar where the action will be added
     */
    public void addMapAction(AbstractAction action, boolean hasTool, JToolBar inToolbar) {
        AbstractButton btn = null;
        if (hasTool) {
            btn = new SolaToolItem(action);
            this.toolsGroup.add(btn);
        } else {
            btn = new JButton(action);
        }
        inToolbar.add(btn);
    }

    /**
     * It adds a tool in the map. The tool adds also an action in the toolbar which can activate the tool
     * @param tool the tool
     * @param inToolbar the toolbar
     */
    public void addTool(SolaTool tool, JToolBar inToolbar) {
        this.addMapAction(new SolaAction(this, tool), inToolbar);
    }

    /**
     * Add an action of type SolaAction in a toolbar.
     * @param action the action 
     * @param inToolbar the toolbar
     */
    public void addMapAction(SolaAction action, JToolBar inToolbar) {
        this.addMapAction(action, action.getAttachedTool() != null, inToolbar);
    }

    public Toc getToc() {
        return toc;
    }

    public void setToc(Toc toc) {
        this.toc = toc;
    }

    public void refresh() {
        this.setClearLabelCache(true);
        this.repaint();
    }
}
