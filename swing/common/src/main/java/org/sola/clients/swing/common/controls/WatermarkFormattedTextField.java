/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
