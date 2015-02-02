/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.common.laf;

import javax.swing.Painter;
import sun.swing.plaf.synth.SynthIcon;

import javax.swing.plaf.synth.SynthContext;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.nimbus.NimbusStyle;
import javax.swing.plaf.synth.SynthConstants;

/**
 * An icon that delegates to a painter.
 *
 * Extracted from nimbus look and feel then transformed to load InternalFrame 
 * icon's with partial Synth Context
 */

/**
* Sourced from https://weblogs.java.net/blogs/Anthra
* @author Brennenraedts Benjamin
*/

public class NimbusTitlePaneIcon extends SynthIcon {
    private final int width;
    private final int height;
    private final String prefix;
    private final String key;
    private final NimbusTitlePane pane;

    NimbusTitlePaneIcon(String prefix, String key, NimbusTitlePane pane, int w, int h) {
        this.width = w;
        this.height = h;
        this.prefix = prefix;
        this.key = key;
        this.pane = pane;
    }

    @Override
    public void paintIcon(SynthContext context, Graphics g, int x, int y,
                          int w, int h) {
        Painter painter = null;
        if (context != null) {
            Object oPainter = UIManager.get(getKey(context, pane, prefix, key));
            if(oPainter instanceof Painter)
            {
                painter = (Painter) oPainter;
            }
        }
        if (painter == null){
            painter = (Painter) UIManager.get(prefix + "[Enabled]." + key);
        }

        if (painter != null && context != null) {
            if (g instanceof Graphics2D){
                Graphics2D gfx = (Graphics2D)g;
                painter.paint(gfx, context.getComponent(), w, h);
            } else {
                // use image if we are printing to a Java 1.1 PrintGraphics as
                // it is not a instance of Graphics2D
                BufferedImage img = new BufferedImage(w,h,
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D gfx = img.createGraphics();
                painter.paint(gfx, context.getComponent(), w, h);
                gfx.dispose();
                g.drawImage(img,x,y,null);
                img = null;
            }
        }
    }

    private static String getKey(SynthContext context, NimbusTitlePane pane, String prefix, String key){
        
        boolean windowNotFocused = false;
        boolean windowMaximized = false;
        Window window = pane.getWindow();
        if (window != null) {
            if (!window.isFocused()) {
                windowNotFocused = true;
            }
            if (prefix.contains("maximizeButton") && window instanceof Frame) {
                Frame f = (Frame) pane.getWindow();
                windowMaximized = (f.getExtendedState() == Frame.MAXIMIZED_BOTH);
            }
        }

        String state = "Enabled";

        if(context!=null)
        {
            switch (context.getComponentState()) {
                case SynthConstants.ENABLED:
                    state = "Enabled";
                    break;
                case SynthConstants.MOUSE_OVER:
                case (SynthConstants.MOUSE_OVER + SynthConstants.ENABLED):
                    state = "MouseOver";
                    break;
                case SynthConstants.MOUSE_OVER + SynthConstants.PRESSED:
                case SynthConstants.PRESSED:
                    state = "Pressed";
                    break;
                case SynthConstants.DISABLED:
                    state = "Disabled";
                    break;
                default:
                    state = "Enabled";
                    break;
            }
        }
        
        StringBuilder sbKey = new StringBuilder(prefix);
        sbKey.append("[").append(state);
        if(windowMaximized)sbKey.append("+WindowMaximized");
        if(windowNotFocused)sbKey.append("+WindowNotFocused");
        sbKey.append("].").append(key);
        return sbKey.toString();
    }
    
    /**
     * Implements the standard Icon interface's paintIcon method as the standard
     * synth stub passes null for the context and this will cause us to not
     * paint any thing, so we override here so that we can paint the enabled
     * state if no synth context is available
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Painter painter = (Painter)UIManager.get(prefix + "[Enabled]." + key);
        if (painter != null){
            JComponent jc = (c instanceof JComponent) ? (JComponent)c : null;
            Graphics2D gfx = (Graphics2D)g;
            gfx.translate(x, y);
            painter.paint(gfx, jc , width, height);
            gfx.translate(-x, -y);
        }
    }

    @Override
    public int getIconWidth(SynthContext context) {
        if (context == null) {
            return width;
        }
        JComponent c = context.getComponent();
        if (c instanceof JToolBar && ((JToolBar)c).getOrientation() == JToolBar.VERTICAL) {
            //we only do the -1 hack for UIResource borders, assuming
            //that the border is probably going to be our border
            if (c.getBorder() instanceof UIResource) {
                return c.getWidth() - 1;
            } else {
                return c.getWidth();
            }
        } else {
            return scale(context, width);
        }
    }

    @Override
    public int getIconHeight(SynthContext context) {
        if (context == null) {
            return height;
        }
        JComponent c = context.getComponent();
        if (c instanceof JToolBar){
            JToolBar toolbar = (JToolBar)c;
            if (toolbar.getOrientation() == JToolBar.HORIZONTAL) {
                //we only do the -1 hack for UIResource borders, assuming
                //that the border is probably going to be our border
                if (toolbar.getBorder() instanceof UIResource) {
                    return c.getHeight() - 1;
                } else {
                    return c.getHeight();
                }
            } else {
                return scale(context, width);
            }
        } else {
            return scale(context, height);
        }
    }

    /**
     * Scale a size based on the "JComponent.sizeVariant" client property of the
     * component that is using this icon
     *
     * @param context The synthContext to get the component from
     * @param size The size to scale
     * @return The scaled size or original if "JComponent.sizeVariant" is not
     *          set
     */
    private int scale(SynthContext context, int size) {
        if (context == null || context.getComponent() == null){
            return size;
        }
        // The key "JComponent.sizeVariant" is used to match Apple's LAF
        String scaleKey = (String) context.getComponent().getClientProperty(
                "JComponent.sizeVariant");
        if (scaleKey != null) {
            if (NimbusStyle.LARGE_KEY.equals(scaleKey)) {
                size *= NimbusStyle.LARGE_SCALE;
            } else if (NimbusStyle.SMALL_KEY.equals(scaleKey)) {
                size *= NimbusStyle.SMALL_SCALE;
            } else if (NimbusStyle.MINI_KEY.equals(scaleKey)) {
                // mini is not quite as small for icons as full mini is
                // just too tiny
                size *= NimbusStyle.MINI_SCALE + 0.07;
            }
        }
        return size;
    }
}
