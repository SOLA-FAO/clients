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
package org.sola.clients.swing.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import org.sola.common.FileUtility;

/**
 * This component provides preview capability for the {@link JFileChooser}
 * component.
 */
public class ImagePreview extends JComponent implements PropertyChangeListener {

    Image thumbnail = null;
    File file = null;

    public ImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(450, 400));
        fc.addPropertyChangeListener(this);
    }

    /**
     * Creates thumbnail of the selected file.
     */
    public void loadImage() {
        if (file == null) {
            thumbnail = null;
            return;
        }
        BufferedImage image = FileUtility.createImageThumbnail(file.getPath(), 450, -1);
        if (image == null) {
            thumbnail = null;
        } else {
            thumbnail = Toolkit.getDefaultToolkit().createImage(image.getSource());
        }
    }

    /**
     * Traps property change events from the {@link JFileChooser} component. If
     * new file is selected, calls methods to create a thumbnail and paint it on
     * this control.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        boolean update = false;
        String prop = e.getPropertyName();

        //If the directory changed, don't show an image.
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
            update = true;

            //If a file became selected, find out which one.
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            update = true;
        }

        //Update the preview accordingly.
        if (update) {
            thumbnail = null;
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    /**
     * Paints the preview icon.
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {

            // Scale the icon to fit the display area based on its longest dimension
            Double scaledHeight = new Double(getHeight());
            Double scaledWidth = new Double(getWidth());
            if (thumbnail.getWidth(null) > thumbnail.getHeight(null)) {
                // The width is the longest dimension so scale the height
                Double scaleFactor = scaledWidth / new Double(thumbnail.getWidth(null));
                scaledHeight = scaleFactor * new Double(thumbnail.getHeight(null));
            } else {
                // The height is the longest dimension so scale the width
                Double scaleFactor = scaledHeight / new Double(thumbnail.getHeight(null));
                scaledWidth = scaleFactor * new Double(thumbnail.getWidth(null));
                if (scaledWidth > getWidth()) {
                    // The width of the Preview is fixed at 400 so check if the scaled
                    // width is too big, and if so, scale the height instead. 
                    scaledWidth = new Double(getWidth());
                    scaleFactor = scaledWidth / new Double(thumbnail.getWidth(null));
                    scaledHeight = scaleFactor * new Double(thumbnail.getHeight(null));
                }
            }

            Image display = thumbnail.getScaledInstance(scaledWidth.intValue(),
                    scaledHeight.intValue(), Image.SCALE_SMOOTH);

            int x = getWidth() / 2 - display.getWidth(null) / 2;
            int y = getHeight() / 2 - display.getHeight(null) / 2;

            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }
            g.drawImage(display, x, y, null);
        }
    }
}