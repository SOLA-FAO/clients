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
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.sola.clients.swing.common.laf.LafManager;

/**
 * {@link JTable} component with predefined styles
 */
public class JTableWithDefaultStyles extends JTable {

    private Color scrollPaneBackgroundColor;
    private Color defaultBackground;
    private Color oddRowColor;
    private Color selectedColor;
    TableCellRenderer headerRenderer;

    /**
     * Class constructor. Initializes default values
     */
    public JTableWithDefaultStyles() {
        this.setAutoCreateRowSorter(true);

        Object newFirstRow = "Table.alternateRowColor";
        Color newFRColor = UIManager.getColor(newFirstRow);
        defaultBackground = newFRColor;

        Object newSecondRow = "Table.alternateRowColor";
        Color newSRColor = UIManager.getColor(newSecondRow);
        oddRowColor = newSRColor;

        Object newSelectedRow = "paleSolaGrey";
        Color newSelColor = UIManager.getColor(newSelectedRow);
        this.setSelectionBackground(newSelColor);
        selectedColor = newSelColor;
        Object newSelForecolor = "List.foreground";
        Color newSelFore = UIManager.getColor(newSelForecolor);
        this.setSelectionForeground(newSelFore);
        this.setGridColor(newSelFore);
        this.tableHeader.setForeground(UIManager.getColor(newSecondRow));
        Object newGrid = "Table.dropLineColor";
        Color newGridColor = UIManager.getColor(newGrid);
        this.setGridColor(newSelFore);

        scrollPaneBackgroundColor = Color.WHITE;
        super.setBackground(defaultBackground);
        this.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        this.setShowGrid(true);
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        this.addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorAdded(AncestorEvent e) {
                setScrollPaneColor();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        // Remove the input mapping for the Enter key so that it can be used to fire the default button on the form instead. 
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
    }
    
    /**
     * Used to color alternative(even) rows.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
        Component comp;
        try {
            comp = super.prepareRenderer(renderer, Index_row, Index_col);
            if (comp != null) {
                if (Index_row % 2 != 0 && !isCellSelected(Index_row, Index_col)) {
                    comp.setBackground(oddRowColor);
                } else {
                    if (!isCellSelected(Index_row, Index_col)) {
                        comp.setBackground(oddRowColor);
                    }
                }

            }
            return comp;
        } catch (Exception e) {
            return getDefaultRenderer(renderer.getClass()).getTableCellRendererComponent(this, null, false, false, 0, 0);
        }
    }

    private void setScrollPaneColor() {
        ((JViewport) this.getParent()).setBackground(scrollPaneBackgroundColor);
    }

    public Color getScrollPaneBackgroundColor() {
        return scrollPaneBackgroundColor;
    }

    public void setScrollPaneBackgroundColor(Color scrollPaneBackgroundColor) {
        this.scrollPaneBackgroundColor = scrollPaneBackgroundColor;
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        defaultBackground = color;
    }

    public Color getOddRowColor() {
        return oddRowColor;
    }

    public void setOddRowColor(Color oddRowColor) {
        this.oddRowColor = oddRowColor;
    }

    public void enableSorting() {
        this.setRowSorter(new TableRowSorter<TableModel>(getModel()));
    }

    public void disableSorting() {
        this.setRowSorter(null);
    }
}
