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
package org.sola.clients.geotools.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.geom.Rectangle2D;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.MapContext;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;

/**
 * This Status bar is a modified copy of the status bar found in Geotools library.
 * It differs from the statusbar in geotools library by the fact that the 
 * coordinate reference system name is not present.
 * Information about the statusbar in geotools library you will find in:
 * {@link http://svn.osgeo.org/geotools/tags/2.7.0.1/modules/unsupported/swing/src/main/java/org/geotools/swing/StatusBar.java)
 */
public class SolaStatusbar extends JPanel {
    private static final ResourceBundle stringRes = 
            ResourceBundle.getBundle("org/geotools/swing/Text");

    private JMapPane mapPane;
    private MapContext context;
    private MapMouseListener mouseListener;
    private MapPaneAdapter mapPaneListener;

    private JLabel renderLabel;
    private JLabel coordsLabel;

    private ImageIcon busyIcon;
    private static final String BUSY_ICON_IMAGE = "/org/geotools/swing/icons/busy_16.gif";


    /**
     * Default constructor.
     * {@linkplain #setMapPane} must be
     * called subsequently for the status bar to receive mouse events.
     */
    public SolaStatusbar() {
        this(null);
    }

    /**
     * Constructor. Links the status bar to the specified map pane.
     *
     * @param pane the map pane that will send mouse events to this
     * status bar
     */
    public SolaStatusbar(JMapPane pane) {
        createListeners();
        initComponents();

        if (pane != null) {
            setMapPane(pane);
        }
    }

    /**
     * Register this status bar to receive mouse events from
     * the given map pane
     *
     * @param newPane the map pane
     * @throws IllegalArgumentException if pane is null
     */
    public void setMapPane(final JMapPane newPane) {
        if (newPane == null) {
            throw new IllegalArgumentException(stringRes.getString("arg_null_error"));
        }

        if (mapPane != newPane) {
            if (mapPane != null) {
                mapPane.removeMouseListener(mouseListener);
            }

            newPane.addMouseListener(mouseListener);
            newPane.addMapPaneListener(mapPaneListener);
            context = newPane.getMapContext();
            mapPane = newPane;
        }
    }

    /**
     * Clear the map coordinate display
     */
    public void clearCoords() {
        coordsLabel.setText("");
    }

    /**
     * Format and display the coordinates of the given position
     *
     * @param mapPos mouse cursor position (world coords)
     */
    public void displayCoords(DirectPosition2D mapPos) {
        if (mapPos != null) {
            coordsLabel.setText(String.format("  %.2f %.2f", mapPos.x, mapPos.y));
        }
    }

    /**
     * Creates and sets the layout of components
     */
    private void initComponents() {
        Rectangle2D rect;
        String constraint;

        LayoutManager lm = new MigLayout("insets 0");
        this.setLayout(lm);

        Font font = Font.decode("Courier-12");

        busyIcon = new ImageIcon(SolaStatusbar.class.getResource(BUSY_ICON_IMAGE));
        renderLabel = new JLabel();
        renderLabel.setHorizontalTextPosition(JLabel.LEADING);
        rect = getFontMetrics(font).getStringBounds(
                "rendering", renderLabel.getGraphics());

        constraint = String.format("gapx 5, width %d!, height %d!",
                (int)rect.getWidth() + busyIcon.getIconWidth() + renderLabel.getIconTextGap(),
                (int)Math.max(rect.getHeight(), busyIcon.getIconHeight()) + 6);

        add(renderLabel, constraint);

        coordsLabel = new JLabel();
        Graphics graphics = coordsLabel.getGraphics();
        coordsLabel.setFont(font);

        rect = getFontMetrics(font).getStringBounds(
                "  00000000.000 00000000.000", graphics);

        constraint = String.format("width %d!, height %d!",
                (int)rect.getWidth() + 10, (int)rect.getHeight() + 6);

        add(coordsLabel, constraint);

        rect = getFontMetrics(font).getStringBounds("X", graphics);

        constraint = String.format("height %d!", (int)rect.getHeight() + 6);

    }

    /**
     * Initialize the mouse and map bounds listeners
     */
    private void createListeners() {
        mouseListener = new MapMouseAdapter() {

            @Override
            public void onMouseMoved(MapMouseEvent ev) {
                displayCoords(ev.getMapPosition());
            }

            @Override
            public void onMouseExited(MapMouseEvent ev) {
                clearCoords();
            }
        };

        mapPaneListener = new MapPaneAdapter() {

            @Override
            public void onRenderingStarted(MapPaneEvent ev) {
                renderLabel.setText("rendering");
                renderLabel.setIcon(busyIcon);
            }

            @Override
            public void onRenderingStopped(MapPaneEvent ev) {
                renderLabel.setText("");
                renderLabel.setIcon(null);
            }

            @Override
            public void onRenderingProgress(MapPaneEvent ev) {
                float progress = ((Number) ev.getData()).floatValue();
                System.out.println("render progress: " + progress);
            }

        };
    }

}
