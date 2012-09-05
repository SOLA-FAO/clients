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

package org.geotools.swing.tool.extended.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import org.geotools.swing.extended.util.Messaging;

/**
 * This is an Image panel that allows adding of an image and defining of two orientation points
 * on it. Those points are always scaled.
 * 
 * @author Elton Manoku
 */
public class ImagePanel extends javax.swing.JPanel {

    private boolean firstPointMode = true;
    private BufferedImage image;
    private Integer firstPointX;
    private Integer firstPointY;
    private Integer secondPointX;
    private Integer secondPointY;
    private Integer firstPointMomentImageWidth;
    private Integer firstPointMomentImageHeight;
    private Integer secondPointMomentImageWidth;
    private Integer secondPointMomentImageHeight;
    private Integer pointGraphicDimesion = 10;
    private Color pointGraphicColor = Color.GREEN;
    private Integer pointGraphicStrokeWidth = 3;
    private JLabel labelImageAction;

    /** Creates new form ImagePanel */
    public ImagePanel() {
        initComponents();
    }

    /**
     * Sets the image
     * @param image 
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Gets X of the first point
     * @return 
     */
    public Integer getFirstPointX() {
        if (firstPointX == null || image == null){
            return null;
        }
        return firstPointX * image.getWidth() / this.firstPointMomentImageWidth;
    }

    /**
     * Gets Y of the first point
     * @return 
     */

    public Integer getFirstPointY() {
        if (firstPointY == null || image == null){
            return null;
        }
        return firstPointY * image.getHeight() / this.firstPointMomentImageHeight;
    }

    /**
     * Gets X of the second point
     * @return 
     */

    public Integer getSecondPointX() {
        if (secondPointX == null || image == null){
            return null;
        }
        return secondPointX * image.getWidth() / this.secondPointMomentImageWidth;
    }

    /**
     * Gets Y of the second point
     * @return 
     */
    public Integer getSecondPointY() {
        if (secondPointY == null || image == null){
            return null;
        }
        return secondPointY * image.getHeight() / this.secondPointMomentImageHeight;
    }

    /**
     * Gets the original width of the image
     * @return 
     */
    public Integer getImageWidth(){
        if (image == null){
            return null;
        }
        return image.getWidth();
    }

    /**
     * Gets the original height of the image
     * @return 
     */
    public Integer getImageHeight(){
        if (image == null){
            return null;
        }
        return image.getHeight();
    }

    /**
     * Removes the first point
     */
    public void resetFirstPoint(){
        this.firstPointX = null;
        this.firstPointY = null;
        this.firstPointMomentImageWidth = null;
        this.firstPointMomentImageHeight = null;
    }

    /**
     * Removes the second point
     */
    public void resetSecondPoint(){
        this.secondPointX = null;
        this.secondPointY = null;
        this.secondPointMomentImageWidth = null;
        this.secondPointMomentImageHeight = null;
        
    }

    /**
     * Sets a label where instructions for operating can be written.
     * 
     * @param labelImageAction 
     */
    public void setLabelImageAction(JLabel labelImageAction) {
        this.labelImageAction = labelImageAction;
    }

    /**
     * It draws the image in the component graphics and also the orientation points
     * if they are inserted.
     * During drawing points are also scaled.
     * 
     * @param g 
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image == null) {
            return;
        }
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        if (firstPointX != null && firstPointY != null) {
            Integer xToDraw = firstPointX * this.getWidth() / this.firstPointMomentImageWidth;
            Integer yToDraw = firstPointY * this.getHeight() / this.firstPointMomentImageHeight;
            this.drawPoint(xToDraw, yToDraw, pointGraphicDimesion, g);
        }
        if (secondPointX != null && secondPointY != null) {
            Integer xToDraw =
                    secondPointX * this.getWidth() / this.secondPointMomentImageWidth;
            Integer yToDraw =
                    secondPointY * this.getHeight() / this.secondPointMomentImageHeight;
            this.drawPoint(xToDraw, yToDraw, pointGraphicDimesion, g);
        }
    }

    /**
     * It draws an orientation point.
     * <br/>
     * It draws an 'X' where the point has to be located. If another shape has to be drawn then
     * this method has to be overridden.
     * @param x
     * @param y
     * @param graphicDimension
     * @param g 
     */
    protected void drawPoint(Integer x, Integer y, Integer graphicDimension, Graphics g) {
        g.setColor(this.pointGraphicColor);
        ((Graphics2D) g).setStroke(new BasicStroke(this.pointGraphicStrokeWidth));
        Integer halfDimesion = graphicDimension / 2;
        g.drawLine(x - halfDimesion, y - halfDimesion, x + halfDimesion, y + halfDimesion);
        g.drawLine(x - halfDimesion, y + halfDimesion, x + halfDimesion, y - halfDimesion);
    }

    private void setImageAction(String messageId){
        if (this.labelImageAction != null){
            this.labelImageAction.setText(Messaging.getInstance().getMessageText(messageId));
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    if (this.firstPointMode) {
        this.firstPointX = evt.getX();
        this.firstPointY = evt.getY();
        this.firstPointMomentImageWidth = this.getWidth();
        this.firstPointMomentImageHeight = this.getHeight();
        this.resetSecondPoint();
        this.setImageAction(
                Messaging.Ids.ADD_DIRECT_IMAGE_DEFINE_ORIENTATION_POINT_2_IN_IMAGE.toString());
    } else {
        this.secondPointX = evt.getX();
        this.secondPointY = evt.getY();
        this.secondPointMomentImageWidth = this.getWidth();
        this.secondPointMomentImageHeight = this.getHeight();
        this.setImageAction(Messaging.Ids.ADD_DIRECT_IMAGE_LOAD_IMAGE.toString());        
    }
    this.repaint();
    this.firstPointMode = !this.firstPointMode;
}//GEN-LAST:event_formMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
