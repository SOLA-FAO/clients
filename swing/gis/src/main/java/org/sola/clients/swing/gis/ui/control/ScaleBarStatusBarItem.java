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
package org.sola.clients.swing.gis.ui.control;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.geotools.swing.control.StatusBarItem;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.MapScaleException;
import org.geotools.swing.extended.util.ScalebarGenerator;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * A status bar item that uses the {@linkplain ScalebarGenerator} to create an image of a scale bar
 * for display. The scale bar will redraw if the
 */
public class ScaleBarStatusBarItem extends StatusBarItem {

    private static final String COMPONENT_NAME = "scaleBarItem";
    /**
     * Default width for the scale bar. This measurement is in points. There are approx 2.83 points
     * per mm. The default value of 225 is approx 80mm.
     */
    private static final double DEFAULT_WIDTH = 225;
    private static final int LRB_MARGIN = 16;
    private static final int TOP_MARGIN = 2;
    private static final int IMAGE_HEIGHT = 26;
    private final JLabel lblScaleBar;
    private ScalebarGenerator scaleBar;
    private double previousScale = 0;

    /**
     * Initializes the scale bar for display.
     *
     * @param map The map to link the scale bar with.
     */
    public ScaleBarStatusBarItem(Map map) {
        super(COMPONENT_NAME, false);

        // Create a label to contain the scale bar image
        lblScaleBar = new JLabel();
        this.add(lblScaleBar);

        // Create the scale bar generator and configure it for display on the status bar.
        // The margin and height contraints should generate a scale bar of 8 points in height. 
        scaleBar = new ScalebarGenerator();
        scaleBar.setDrawScaleText(false);
        scaleBar.setColorText(Color.BLACK);
        scaleBar.setLrbMargin(LRB_MARGIN);
        scaleBar.setTopMargin(TOP_MARGIN);
        scaleBar.setHeight(IMAGE_HEIGHT);

        // Listen for a change in the map and redraw the scale bar
        map.addMapPaneListener(new MapPaneAdapter() {

            @Override
            public void onDisplayAreaChanged(MapPaneEvent ev) {
                redrawScaleBar((Map) ev.getSource());
            }
        });
    }

    /**
     * Redraws the scale bar image if the scale of the map has changed.
     *
     * Note that the screen resolution has a significant impact on the accuracy of the scale bar
     * when displayed on screen. Unfortunately the Toolkit is not guaranteed to give an accurate DPI
     * and will usually give a default value of 96. This means the scale bar should be considered
     * indicative only.
     *
     * @param theMap The map display
     */
    private void redrawScaleBar(Map theMap) {
        try {
            double scale = theMap.getScale();
            if (scale != previousScale) {
                BufferedImage image = scaleBar.getImage(scale, DEFAULT_WIDTH,
                        Toolkit.getDefaultToolkit().getScreenResolution());
                if (image != null) {
                    lblScaleBar.setText(null);
                    lblScaleBar.setIcon(new ImageIcon(image));
                } else {
                    lblScaleBar.setIcon(null);
                    lblScaleBar.setText(MessageUtility.getLocalizedMessageText(
                            GisMessage.GENERAL_MIN_DISPLAY_SCALE));
                }
                previousScale = scale;
            }

        } catch (MapScaleException ex) {
            lblScaleBar.setIcon(null);
            lblScaleBar.setText(MessageUtility.getLocalizedMessageText(
                    GisMessage.GENERAL_INVALID_SCALE));
        }
    }
}
