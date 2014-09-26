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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Renders table cell with application services list to display in the form 
 * of the list.
 */
public class CellDelimitedListRenderer extends JTextArea implements TableCellRenderer {

    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
    private String delimiter;
    private boolean showDash;

    /** 
     * Creates new table cell renderer with text area, breaking string into items. 
     * @param delimiter String delimiter used to break string.
     */
    public CellDelimitedListRenderer(String delimiter) {
        this(delimiter, true);
    }
    
    /** 
     * Creates new table cell renderer with text area, breaking string into items. 
     * Default "," comma delimiter will be applied.
     */
    public CellDelimitedListRenderer() {
        this(",");
    }
    
    /** 
     * Creates new table cell renderer with text area, breaking string into items. 
     * @param delimiter String delimiter used to break string.
     * @param showDash Boolean value indicating whether "-" dash symbol should be added in front of each item.
     */
    public CellDelimitedListRenderer(String delimiter, boolean showDash) {
        if(delimiter == null || delimiter.length() < 1){
            this.delimiter = ",";
        } else {
            this.delimiter = delimiter;
        }
        this.showDash = showDash;
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());

        try {
            if (value != null) {
                Object[] arr = value.toString().split(delimiter);
                String out = "";
                String prefix = "- ";
                
                if(!showDash){
                    prefix = "";
                }
                
                for (int i = 0; i < arr.length; i++) {
                    if (out.length() > 0) {
                        out += "\n";
                    }
                    if(arr.length == 1){
                        out += String.format("%s%s", prefix, arr[i]);
                    } else {
                        out += String.format("%s%s;", prefix, arr[i]);
                    }
                }

                setText(out);
                setToolTipText(out);

            }

            TableColumnModel columnModel = table.getColumnModel();
            setSize(columnModel.getColumn(column).getWidth(), 100000);

            int height_wanted = (int) getPreferredSize().getHeight();

            if (height_wanted > table.getRowHeight(row)) {
                table.setRowHeight(row, height_wanted);
            }

        } catch (Exception e) {
            setText("");
            setToolTipText("");
        }

        return this;
    }
}
