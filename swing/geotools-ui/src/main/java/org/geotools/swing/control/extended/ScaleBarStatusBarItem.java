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
package org.geotools.swing.control.extended;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.geotools.swing.control.StatusBarItem;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.extended.util.ScalebarGenerator;

/**
 * A status bar item that uses the {@linkplain ScalebarGenerator} to create an image of a scale bar
 * for display.
 *
 * Note that the screen resolution has a significant impact on the accuracy of the scale bar when
 * displayed on screen. Unfortunately the Java Toolkit is not guaranteed to give an accurate DPI and
 * will usually give a default value of 96.
 *
 * The true DPI for modern monitors can be much greater than 96 (e.g. up to 200). A +%10 fudge
 * factor is used to attempt to correct the scale for display on modern 22 inch wide screen monitors
 * but note that the fudge factor may in fact increase the inaccuracy of the scale bar on larger,
 * smaller or older monitors. For this reason the scale bar should be considered indicative only and
 * should not be used to obtain precise measurements from the screen.
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
    private double scaleBarDpiFudge = 1.10;

    /**
     * Initializes the scale bar for display. See {@linkplain ScaleBarStatusBarItem} for notes about
     * the accuracy of the scale bar.
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
     * Fudge factor used to attempt to improve the accuracy of the scale bar for on screen display.
     * Default value is 1.10. See {@linkplain ScaleBarStatusBarItem} for notes about the accuracy of
     * the scale bar.
     */
    public void setScaleBarDpiFudge(double scaleBarDpiFudge) {
        this.scaleBarDpiFudge = scaleBarDpiFudge;
    }

    /**
     * Obtains the screen resolution (a.k.a. DPI or PPI) using the default Java Toolkit and applies
     * a fudge factor to attempt to improve the accuracy of the scale bar. See {@linkplain ScaleBarStatusBarItem}
     * for notes about the accuracy of the scale bar.
     *
     * This method can be overridden in descendent classes to provide an improved implementation if
     * one is available.
     *
     * @return
     */
    protected double getScreenResolution() {
        return Toolkit.getDefaultToolkit().getScreenResolution() * scaleBarDpiFudge;
    }

    /**
     * Redraws the scale bar image if the scale of the map has changed. See {@linkplain ScaleBarStatusBarItem}
     * for notes about the accuracy of the scale bar.
     *
     * @param theMap The map display
     */
    private void redrawScaleBar(Map theMap) {
        double scale = theMap.getScale();
        if (scale != previousScale) {
            BufferedImage image = scaleBar.getImage(scale, DEFAULT_WIDTH, getScreenResolution());
            if (image != null) {
                lblScaleBar.setText(null);
                lblScaleBar.setIcon(new ImageIcon(image));
            } else {
                lblScaleBar.setIcon(null);
                lblScaleBar.setText(Messaging.getInstance().getMessageText(
                        Messaging.Ids.MIN_DISPLAY_SCALE.toString()));
            }
            previousScale = scale;
        }

    }
}
