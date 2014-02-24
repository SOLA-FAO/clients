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
package org.geotools.swing.tool.extended;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.extended.layer.ExtendedImageLayer;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.extended.exception.DirectImageNotValidFileException;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.tool.extended.ui.DirectImageForm;

/**
 * A tool used to start the process of adding an image in the map.
 * The tool allows the adding of 2 orientation points in the map and image to define location
 * of the image in the map.
 * <br/>
 * It supports adding of images of types: jpg, tif, png.
 * 
 * @author Elton Manoku
 */
public class AddDirectImageTool extends ExtendedTool {

    public final static String NAME = "image-add";
    private String toolTip = Messaging.getInstance().getMessageText(
            Messaging.Ids.ADD_DIRECT_IMAGE_TOOLTIP.toString());
    private ExtendedImageLayer imageLayer;
    private boolean firstPointMode = true;
    private DirectPosition2D firstPoint = null;
    private DirectPosition2D secondPoint = null;
    private JFileChooser fileChooser = new JFileChooser();
    private DirectImageForm imageForm = new DirectImageForm();

    /**
     * Constructor of the tool.
     * @param imageLayer The Layer where the image will be added
     */
    public AddDirectImageTool(ExtendedImageLayer imageLayer) {
        this.setToolName(NAME);
        this.setToolTip(toolTip);
        this.imageLayer = imageLayer;
        this.fileChooser.setMultiSelectionEnabled(false);
        this.fileChooser.removeChoosableFileFilter(this.fileChooser.getAcceptAllFileFilter());
        String[] extensions = new String[]{"jpg", "jpeg", "png", "tif", "tiff"};
        this.fileChooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Images (*.jpg, *.jpeg, *.png, *.tif, *.tiff)",
                extensions));
    }

    /**
     * For each click in the map the first orientation point or the second orientation point is 
     * defined.
     * <br/>
     * After the second point is added, the loading of the image starts.
     * @param ev 
     */
    @Override
    public void onMouseClicked(MapMouseEvent ev) {
        DirectPosition2D pos = ev.getWorldPos();
        if (this.firstPointMode) {
            this.reset();
            this.imageLayer.setFirstPoint(pos.getX(), pos.getY());
            this.firstPoint = pos.clone();
            this.secondPoint = null;
            Messaging.getInstance().show(
                    Messaging.Ids.ADD_DIRECT_IMAGE_ADD_SECOND_POINT.toString());
        } else {
            this.imageLayer.setSecondPoint(pos.getX(), pos.getY());
            this.secondPoint = pos.clone();
        }
        this.firstPointMode = !this.firstPointMode;
        if (this.firstPoint != null && this.secondPoint != null) {
            this.loadImage();
        }
    }

    @Override
    public void onSelectionChanged(boolean selected) {
        super.onSelectionChanged(selected);
        if (selected) {
            if (this.firstPointMode) {
                Messaging.getInstance().show(
                        Messaging.Ids.ADD_DIRECT_IMAGE_ADD_FIRST_POINT.toString());
            } else {
                Messaging.getInstance().show(
                        Messaging.Ids.ADD_DIRECT_IMAGE_ADD_SECOND_POINT.toString());
            }
        }
    }

    /**
     * It resets the process.
     */
    public void reset() {
        this.firstPointMode = true;
        try {
            this.imageLayer.setRasterFile(null);
            this.getMapControl().refresh();
        } catch (IOException ex) {
            //Not relevant while resetting
        } catch (DirectImageNotValidFileException ex) {
            //Not relevant while resetting            
        }
    }

    private void loadImage() {
        if (this.fileChooser.showOpenDialog(this.getMapControl()) == JFileChooser.APPROVE_OPTION) {
            File file = this.fileChooser.getSelectedFile();
            try {
                this.imageForm.setImage(file);
                this.imageForm.setFirstPointInMapX(this.firstPoint.getX());
                this.imageForm.setFirstPointInMapY(this.firstPoint.getY());
                this.imageForm.setSecondPointInMapX(this.secondPoint.getX());
                this.imageForm.setSecondPointInMapY(this.secondPoint.getY());
                this.imageForm.setVisible(true);
                if (this.imageForm.isSuccess()) {
                    this.imageLayer.setRasterFile(file);
                    this.imageLayer.setMinX(this.imageForm.getLeftBottomImageCornerInMapX());
                    this.imageLayer.setMinY(this.imageForm.getLeftBottomImageCornerInMapY());
                    this.imageLayer.setMaxX(this.imageForm.getRightTopImageCornerInTheMapX());
                    this.imageLayer.setMaxY(this.imageForm.getRightTopImageCornerInTheMapY());
                    this.getMapControl().refresh();
                } else {
                    this.reset();
                }
            } catch (IOException ex) {
                Messaging.getInstance().show(
                        Messaging.Ids.ADD_DIRECT_IMAGE_LOAD_IMAGE_ERROR.toString(),
                        ex.getMessage());
                this.reset();
            } catch (DirectImageNotValidFileException ex) {
                Messaging.getInstance().show(
                        Messaging.Ids.ADD_DIRECT_IMAGE_LOAD_IMAGE_ERROR.toString(),
                        ex.getMessage());
                this.reset();
            }
        }
    }
}
