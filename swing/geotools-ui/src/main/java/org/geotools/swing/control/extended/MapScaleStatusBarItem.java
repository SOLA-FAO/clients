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
package org.geotools.swing.control.extended;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.control.StatusBarItem;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.util.Messaging;

/**
 * A status bar item that displays the map scale and allows the user to change the map scale. The
 * center point of the map is maintained when the user changes the map scale to zoom the map. The
 * user must press enter to accept the new scale value and zoom the map.
 */
public class MapScaleStatusBarItem extends StatusBarItem {

    private static final String COMPONENT_NAME = "mapScaleItem";
    private final JTextField txtScale;
    private final JLabel lblScale;
    private boolean suppressZoom = false;
    private Map map;

    /**
     * Initializes the Map Scale status bar item
     *
     * @param theMap The map to link the scale to
     */
    public MapScaleStatusBarItem(Map theMap) {
        super(COMPONENT_NAME, false);
        this.map = theMap;
        // Creates a text field for the scale value that the user can update
        txtScale = new JTextField(6);
        txtScale.setHorizontalAlignment(JTextField.RIGHT);
        lblScale = new JLabel(Messaging.getInstance().getMessageText(
                Messaging.Ids.SCALE_LABEL.toString()));
        this.add(lblScale, BorderLayout.LINE_START);
        this.add(txtScale, BorderLayout.LINE_END);

        // Adds a listener to the map to update the scale whenever the map changes display. 
        map.addMapPaneListener(new MapPaneAdapter() {

            @Override
            public void onDisplayAreaChanged(MapPaneEvent ev) {
                setScale((Map) ev.getSource());
            }
        });

        // Adds a listener to the scale text field that is fired when the user presses enter.  
        txtScale.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                zoomMap();
            }
        });

    }

    /**
     * Obtains the new scale from the map, formats it and displays the value in the scale text
     * field.
     *
     * @param theMap The map linked to the scale.
     */
    private void setScale(Map theMap) {
        String scaleText;
        double scale = theMap.getScale();
        if (scale < 0.01) {
            scaleText = Messaging.getInstance().getMessageText(
                    Messaging.Ids.MIN_DISPLAY_SCALE.toString());
        } else {
            DecimalFormat df = new DecimalFormat("#,###,###");
            df.setRoundingMode(RoundingMode.HALF_UP);
            if (scale < 1) {
                df = new DecimalFormat("0.##");
            }
            scaleText = df.format(scale);
        }
        suppressZoom = true;
        txtScale.setText(scaleText);
        suppressZoom = false;
    }

    /**
     * Calculates the new bounding box for the map and zooms to that extent based on the scale value
     * entered by the user. Note that the zoom may not result in the exact scale entered due to the
     * curvature of the earth. For smaller scales (e.g. < 10,000) the zoom should be accurate. For
     * high scales (e.g. > 200,000) the zoom may become increasingly inaccurate.
     */
    private void zoomMap() {
        if (!suppressZoom) {
            String scaleText = txtScale.getText();
            // Remove any non digit characters from the string using reg expression. 
            scaleText = scaleText.replaceAll("[^0-9\\.]", "");
            try {
                double scale = Double.parseDouble(scaleText);
                double zoomRatio = scale / this.map.getScale();
                ReferencedEnvelope env = this.map.getDisplayArea();
                // Use the zoom ratio to calculate the change in height and width required to 
                // get to the required scale. 
                double deltaWidth = (env.getSpan(0) * zoomRatio) - env.getSpan(0);
                double deltaHeight = (env.getSpan(1) * zoomRatio) - env.getSpan(1);
                env.expandBy(deltaWidth * 0.5, deltaHeight * 0.5);
                this.map.setDisplayArea(env);
            } catch (NumberFormatException ex) {
                setScale(this.map);
            }
        }
    }
}
