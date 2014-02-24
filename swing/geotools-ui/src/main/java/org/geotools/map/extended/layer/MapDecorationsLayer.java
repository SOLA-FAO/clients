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
package org.geotools.map.extended.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.swing.extended.util.ScalebarGenerator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * A layer to add decorations in the map like a scalebar. If used this layer should be in the top
 * of all layers.
 * 
 * @author Elton Manoku
 */
public class MapDecorationsLayer extends DirectLayer {

    private ReferencedEnvelope bounds;
    private ScalebarGenerator scalebarGenerator;

    /**
     * It constructs the layer. 
     */
    public MapDecorationsLayer() {
        this.scalebarGenerator = new ScalebarGenerator();
    }

    /**
     * It is called from the map control in every refresh of the map.
     * @param graphics
     * @param map
     * @param viewport 
     */
    @Override
    public void draw(Graphics2D graphics, MapContent map, MapViewport viewport) {
        if (this.bounds == null) {
            this.bounds = map.getMaxBounds();
        }
        double dpi = RendererUtilities.getDpi(null);
        double scale = 0;
        try {
            scale = 1 / RendererUtilities.calculateScale(
                    viewport.getBounds(), (int) viewport.getScreenArea().getWidth(),
                    (int) viewport.getScreenArea().getHeight(), dpi);

        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        } catch (TransformException ex) {
            throw new RuntimeException(ex);
        }
        BufferedImage bi = this.scalebarGenerator.getImage(scale, 200, dpi);
        if (bi != null) {
            graphics.drawImage(bi, 1, 1, null);
        }
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return this.bounds;
    }
}
