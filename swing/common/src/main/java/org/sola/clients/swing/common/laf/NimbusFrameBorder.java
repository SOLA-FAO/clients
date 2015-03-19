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
package org.sola.clients.swing.common.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Window;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/**
 * Border for Nimbus Frame
 * 
 */
/**
* Sourced from https://weblogs.java.net/blogs/Anthra
* @author Brennenraedts Benjamin
*/

public class NimbusFrameBorder extends AbstractBorder implements UIResource {

    private static final Insets defaultInsets = new Insets(2, 5, 5, 5);

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y,
            int w, int h) {
        Dimension titlePaneDimension = UIManager.getDimension("FrameTitlePane.dimension");
        int height = titlePaneDimension.height;

        Color background;
        Color shadow, inactiveShadow;
        
//        background = UIManager.getColor("Frame.background");
//        shadow = UIManager.getColor("Frame.foreground");
//        inactiveShadow = UIManager.getColor("nimbusBorder").darker();
        background = new Color (33,124,149);
        shadow = new Color (33,124,149);
        inactiveShadow = new Color (33,124,149);

        Color outerBorder;
        Color innerBorder;
        Color innerBorderShadow;
        Paint gradient = new LinearGradientPaint(0.0f, defaultInsets.top, 0.0f, height+defaultInsets.top, new float[]{0.0f,1.0f}, new Color[]{background.brighter(),background.darker()});

        Window window = SwingUtilities.getWindowAncestor(c);
        if (window != null && window.isActive()) {
            outerBorder = shadow;
            
            innerBorder = new Color (33,124,149);
            innerBorderShadow = new Color (33,124,149);
        
//            innerBorder = new Color(shadow.getRed(),shadow.getGreen(),shadow.getBlue(),150);
//            innerBorderShadow = new Color(innerBorder.getRed(),innerBorder.getGreen(),innerBorder.getBlue(),75);
        } else {
            outerBorder = inactiveShadow;
            innerBorder = new Color (33,124,149);
            innerBorderShadow = new Color (33,124,149);
//            innerBorder = new Color(shadow.getRed(),shadow.getGreen(),shadow.getBlue(),50);
//            innerBorderShadow = new Color(innerBorder.getRed(),innerBorder.getGreen(),innerBorder.getBlue(),0);
        }
        
        //<editor-fold defaultstate="collapsed" desc="Background">
        g.setColor(background);
//        g.setColor(new Color (33,124,149));
        
        if(g instanceof Graphics2D)
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(gradient);
        }
        
        // Draw the bulk of the border
        for (int i = 1; i < defaultInsets.left; i++) {
            g.drawRect(x + i, y + i, w - (i * 2) - 1, h - (i * 2) - 1);
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Shadowed inner border">
        g.setColor(innerBorder);
        
        g.drawRect(x + defaultInsets.left -1, y + defaultInsets.top + height -1, w - (defaultInsets.left+defaultInsets.right) +1, h - (defaultInsets.top+defaultInsets.bottom) - height +1);

        g.setColor(innerBorderShadow);
        g.drawRect(x + defaultInsets.left -2, defaultInsets.top + height -2, w - (defaultInsets.left+defaultInsets.right) +3, h - (defaultInsets.top+defaultInsets.bottom) - height +3);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Outer border">
        
        g.setColor(outerBorder);

        g.drawRoundRect(x, y, w-1, h-1, 5, 5);
        //</editor-fold>
        
    }

    @Override
    public Insets getBorderInsets(Component c, Insets newInsets) {
        newInsets.set(defaultInsets.top, defaultInsets.left, defaultInsets.bottom, defaultInsets.right);
        return newInsets;
    }
}
