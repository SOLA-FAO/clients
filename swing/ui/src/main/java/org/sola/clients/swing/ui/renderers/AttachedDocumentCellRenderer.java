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
package org.sola.clients.swing.ui.renderers;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.webservices.transferobjects.EntityAction;

/** Displays attachment image in the table row populated with {@link SourceBean} 
 * object, if it has an attached digital copy. */
public class AttachedDocumentCellRenderer extends DefaultTableCellRenderer {

    private ImageIcon image;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        if (value != null) {
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.CENTER);

            try {
                boolean drawImage = false;

                if (String.class.isAssignableFrom(value.getClass())) {
                    // For the fields with archiveId
                    drawImage = true;
                } else if(DocumentBean.class.isAssignableFrom(value.getClass())) {
                    // For the fields with DocumentBean
                    DocumentBean document = (DocumentBean) value;
                    if (document.getEntityAction() != EntityAction.DELETE
                            && document.getEntityAction() != EntityAction.DISASSOCIATE) {
                        drawImage = true;
                    }
                }
                
                if (drawImage) {
                    if (image != null) {
                        label.setText(null);
                        label.setIcon(image);
                    } else {
                        image = new ImageIcon(AttachedDocumentCellRenderer.class.getResource(
                                "/images/common/attachment.png"));
                        label.setIcon(image);
                        label.setText(null);
                    }

                    int height = table.getRowHeight(row);

                    if (height < image.getIconHeight() + 5) {
                        table.setRowHeight(row, image.getIconHeight() + 5);
                    }
                } else {
                    label.setIcon(null);
                    label.setText(null);
                }
            } catch (Exception ex) {
                label.setIcon(null);
                label.setText("+");
            }
        } else {
            label.setIcon(null);
            label.setText(null);
        }

        return this;
    }
}
