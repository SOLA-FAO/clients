/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.common.controls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.JFormattedTextField;

/** Displays formatted field with provided watermark text. */
public class WatermarkFormattedTextField extends JFormattedTextField {

    private String watermarkText = "";
    private Color watermarkColor;

    /** Class constructor initiating default settings. */
    public WatermarkFormattedTextField() {
        super();
        setText("");
        watermarkColor = new java.awt.Color(100, 100, 100);
    }

    /** Returns watermark text. */
    public String getWatermarkText() {
        return watermarkText;
    }

    /** Sets watermark text. */
    public void setWatermarkText(String watermark) {
        if(watermark == null){
            this.watermarkText = "";
        } else {
            this.watermarkText = watermark;
        }
        this.repaint();
    }

    /** Returns watermark text color. */
    public Color getWatermarkColor() {
        return watermarkColor;
    }

    /** Sets watermark text color. */
    public void setWatermarkColor(Color watermarkColor) {
        this.watermarkColor = watermarkColor;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        String stringToDraw = getWatermarkText();
        int fontHeight = getFontMetrics(getFont()).getHeight();
        int y = (this.getHeight() / 2) + (fontHeight / 2) - getFontMetrics(getFont()).getDescent();

        if (getValue() != null || (getText() != null && !getText().equals(""))) {
            stringToDraw = "";
        }

        if (!stringToDraw.equals("")) {
            AttributedString as = new AttributedString(stringToDraw);
            as.addAttribute(TextAttribute.FONT, this.getFont());
            as.addAttribute(TextAttribute.FOREGROUND, watermarkColor);
            g2.drawString(as.getIterator(), getInsets().left, y);
        }
    }
}
