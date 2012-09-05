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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.geotools.swing.extended.exception.DirectImageNotValidFileException;
import org.geotools.swing.extended.util.Messaging;

/**
 * This form is used during the adding of the image in the map.
 * In this form is also calculated the left-bottom and right-top corner in the map where the 
 * image will be located.
 * <br/>
 * After an image is loaded, two orientation points in the image are defined. In the form
 * there are given as properties the orientation points in the map.
 * 
 * @author Elton Manoku
 */
public class DirectImageForm extends javax.swing.JDialog {

    private boolean success = false;
    private Double firstPointInMapX;
    private Double firstPointInMapY;
    private Double secondPointInMapX;
    private Double secondPointInMapY;

    /** 
     * Creates new form DirectImageForm 
     */
    public DirectImageForm() {
        initComponents();
        this.lblAction.setText("TEST");
        this.setAlwaysOnTop(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.pnlImage.setLabelImageAction(this.lblAction);
    }

    /**
     * Sets the image file.
     * 
     * @param file
     * @throws DirectImageNotValidFileException
     * @throws IOException 
     */
    public void setImage(File file) throws DirectImageNotValidFileException, IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new DirectImageNotValidFileException();
        }
        this.pnlImage.setImage(image);
    }

    /**
     * Gets if the definition of the location of the image in the map is successful
     * 
     * @return 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the X coordinate of the first point in the map
     * 
     * @param firstPointInMapX 
     */
    public void setFirstPointInMapX(Double firstPointInMapX) {
        this.firstPointInMapX = firstPointInMapX;
    }

    /**
     * Sets the Y coordinate of the first point in the map
     * 
     * @param firstPointInMapY 
     */
    public void setFirstPointInMapY(Double firstPointInMapY) {
        this.firstPointInMapY = firstPointInMapY;
    }

    /**
     * Sets the X coordinate of the second point in the map
     * 
     * @param secondPointInMapX 
     */
    public void setSecondPointInMapX(Double secondPointInMapX) {
        this.secondPointInMapX = secondPointInMapX;
    }

    /**
     * Sets the Y coordinate of the second point in the map
     * 
     * @param secondPointInMapY 
     */
    public void setSecondPointInMapY(Double secondPointInMapY) {
        this.secondPointInMapY = secondPointInMapY;
    }

    /**
     * Gets the left bottom X map coordinate of the image
     * @return 
     */
    public Double getLeftBottomImageCornerInMapX() {
        return this.firstPointInMapX - (this.getImageResolution() * this.pnlImage.getFirstPointX());
    }

    /**
     * Gets the left bottom Y map coordinate of the image
     * @return 
     */
    public Double getLeftBottomImageCornerInMapY() {
        return this.firstPointInMapY - (this.getImageResolution()
                * (this.pnlImage.getImageHeight() - this.pnlImage.getFirstPointY()));
    }

    /**
     * Gets the top right X map coordinate of the image
     * @return 
     */
    public Double getRightTopImageCornerInTheMapX() {
        return this.secondPointInMapX
                + (this.getImageResolution()
                * (this.pnlImage.getImageWidth() - this.pnlImage.getSecondPointX()));
    }

    /**
     * Gets the top right Y map coordinate of the image
     * @return 
     */
    public Double getRightTopImageCornerInTheMapY() {
        return this.secondPointInMapY
                + (this.getImageResolution() * this.pnlImage.getSecondPointY());
    }

    private Double getImageResolution() {
        return Math.abs(this.secondPointInMapX - this.firstPointInMapX)
                / Math.abs(this.pnlImage.getSecondPointX() - this.pnlImage.getFirstPointX());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlImage = new org.geotools.swing.tool.extended.ui.ImagePanel();
        lblAction = new javax.swing.JLabel();
        cmdOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(463, 286));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        javax.swing.GroupLayout pnlImageLayout = new javax.swing.GroupLayout(pnlImage);
        pnlImage.setLayout(pnlImageLayout);
        pnlImageLayout.setHorizontalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );
        pnlImageLayout.setVerticalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );

        lblAction.setText("Image action");

        cmdOk.setText("Ok");
        cmdOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAction, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 326, Short.MAX_VALUE)
                .addComponent(cmdOk))
            .addComponent(pnlImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdOk, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblAction, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    this.pnlImage.resetFirstPoint();
    this.pnlImage.resetSecondPoint();
    this.lblAction.setText(Messaging.getInstance().getMessageText(
            Messaging.Ids.ADD_DIRECT_IMAGE_DEFINE_ORIENTATION_POINT_1_IN_IMAGE.toString()));
    this.success = false;
    this.pnlImage.repaint();
}//GEN-LAST:event_formComponentShown

private void cmdOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOkActionPerformed
    this.success = (this.pnlImage.getFirstPointX() != null
            && this.pnlImage.getSecondPointX() != null);
    if (this.success) {
        this.success = this.success && ((this.firstPointInMapX < this.secondPointInMapX)
                ? this.pnlImage.getFirstPointX() < this.pnlImage.getSecondPointX()
                : this.pnlImage.getFirstPointX() > this.pnlImage.getSecondPointX());
        this.success = this.success && ((this.firstPointInMapY < this.secondPointInMapY)
                ? this.pnlImage.getFirstPointY() > this.pnlImage.getSecondPointY()
                : this.pnlImage.getFirstPointY() < this.pnlImage.getSecondPointY());
    }
    if (success) {
        this.setVisible(false);
    } else {
        Messaging.getInstance().show(
                Messaging.Ids.ADD_DIRECT_IMAGE_DEFINE_POINT_IN_IMAGE_ERROR.toString());
    }
}//GEN-LAST:event_cmdOkActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdOk;
    private javax.swing.JLabel lblAction;
    private org.geotools.swing.tool.extended.ui.ImagePanel pnlImage;
    // End of variables declaration//GEN-END:variables
}
