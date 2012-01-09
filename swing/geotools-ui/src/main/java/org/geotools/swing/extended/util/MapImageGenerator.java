/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.geotools.swing.extended.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.extended.Map;

/**
 *
 * @author Elton Manoku
 */
public class MapImageGenerator {

    private Map map;
    private Color textColor = Color.RED;
    private Font textFont = new Font(Font.SANS_SERIF, Font.BOLD, 10);

    public MapImageGenerator(Map map) {
        this.map = map;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Font getTextFont() {
        return textFont;
    }

    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    public BufferedImage getImage(double imageWidth, double imageHeight, double scale, int dpi) {
        ReferencedEnvelope currentExtent = this.map.getDisplayArea();
        double centerX = currentExtent.getMedian(0);
        double centerY = currentExtent.getMedian(1);
        double dotPerCm = dpi / 2.54;
        double extentWidth = imageWidth * scale / (dotPerCm * 100);
        double extentHeight = imageHeight * scale / (dotPerCm * 100);
        ReferencedEnvelope extent = new ReferencedEnvelope(
                centerX - extentWidth / 2, centerX + extentWidth / 2,
                centerY - extentHeight / 2, centerY + extentHeight / 2,
                currentExtent.getCoordinateReferenceSystem());
        return this.getImage((int) Math.round(imageWidth), extent);
    }

    public BufferedImage getImage(int imageWidth, ReferencedEnvelope extent) {
        int imageHeight = (int) (imageWidth * (extent.getHeight() / extent.getWidth()));
        BufferedImage bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        StreamingRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(this.map.getMapContent());
        Rectangle rectangle = new Rectangle(imageWidth, imageHeight);
        renderer.paint(graphics, rectangle, extent);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, imageWidth-1, imageHeight-1);
        graphics.setFont(this.textFont);
        graphics.setColor(this.textColor);

        this.drawText(graphics, String.format("%s N", (int)extent.getMaxY()),
                imageWidth / 2, 10, true);
        this.drawText(graphics, String.format("%s N", (int)extent.getMinY()),
                imageWidth / 2, imageHeight - 3, true);
        AffineTransform originalTransform = graphics.getTransform();
        graphics.rotate(-Math.PI / 2, 10, imageHeight / 2);
        this.drawText(graphics, String.format("%s E", (int)extent.getMinX()),
                10, imageHeight / 2, false);
        graphics.setTransform(originalTransform);
        graphics.rotate(Math.PI / 2, imageWidth - 10, imageHeight / 2);
        this.drawText(graphics, String.format("%s E", (int)extent.getMaxX()),
                imageWidth - 100, imageHeight / 2, false);
        graphics.setTransform(originalTransform);

        return bi;
    }

    private void drawText(Graphics2D graphics, String txt, int x, int y, boolean center) {
        if (center) {
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int txtWidth = fontMetrics.stringWidth(txt);
            x = x - txtWidth / 2;
        }
        graphics.drawString(txt, x, y);
    }
}
