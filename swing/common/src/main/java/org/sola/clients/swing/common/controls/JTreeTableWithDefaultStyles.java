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
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.sola.clients.swing.common.laf.LafManager;

/**
 * Customized SwingX JXTreeTable intended to look much like the SOLA
 * JTableWithDefaultStyles.
 *
 * @author solaDev
 */
public class JTreeTableWithDefaultStyles extends JXTreeTable {

    private Color scrollPaneBackgroundColor;
    private Color defaultBackground;
    private Color oddRowColor;
    private Color selectBackground;
    private Color selectForeground;
    private KeyListener keyboardNavigation;

    /**
     * Class constructor. Initializes default values
     */
    public JTreeTableWithDefaultStyles() {
        super();

        // Use the default Nimbus Icons because for the leaf and folder
        // nodes because they look better. 
        this.setOpenIcon(null);
        this.setClosedIcon(null);
        this.setLeafIcon(null);

        Object newFirstRow = "Table.alternateRowColor";
        Color newFRColor = UIManager.getColor(newFirstRow);
        defaultBackground = newFRColor;

        Object newSecondRow = "Table.alternateRowColor";
        Color newSRColor = UIManager.getColor(newSecondRow);
        oddRowColor = newSRColor;

        Object newSelectedRow = "paleSolaGrey";
        selectBackground = UIManager.getColor(newSelectedRow);
        this.setSelectionBackground(selectBackground);

        Object newSelForecolor = "List.foreground";
        selectForeground = UIManager.getColor(newSelForecolor);
        this.setSelectionForeground(selectForeground);
        this.setGridColor(selectForeground);
        
        scrollPaneBackgroundColor = Color.WHITE;
        super.setBackground(scrollPaneBackgroundColor);
        this.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        this.setShowGrid(true);
        this.setRowSelectionAllowed(true);
        this.setColumnSelectionAllowed(false);
        // Displays a dropdown control in the top right of the tree table allowing 
        // the user to hide or display columns in the tree table.  
        this.setColumnControlVisible(false);
        this.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Create the alternating strip pattern using the HighligtherFactory
        CompoundHighlighter compHigh = (CompoundHighlighter) HighlighterFactory.createAlternateStriping(defaultBackground, oddRowColor);
        this.addHighlighter(compHigh);

        configureKeyboardNavigation();
    }

    /**
     * Sets up a key listener that allows the user expand and collapse the tree
     * using the LEFT and RIGHT keys. Can also be used to expand or collapse all
     * nodes in the tree if the CTRL key is pressed as well. The key strokes
     * have no effect unless the selected cell is a parent node with the
     * appropriate expanded or collapsed state. This ensures the LEFT and RIGHT
     * keys can also be used to navigate around the TreeTable.
     *
     * To navigate out of the TreeTable, use CTRL + TAB or CTRL + SHIFT + TAB
     */
    private void configureKeyboardNavigation() {
        final JXTreeTable treeTable = this;
        if (keyboardNavigation == null) {
            keyboardNavigation = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                        if (treeTable.getSelectedRow() >= 0
                                && treeTable.isExpanded(treeTable.getSelectedRow())
                                && treeTable.getSelectedColumn() == treeTable.getHierarchicalColumn()
                                && getRowData(treeTable.getSelectedRow()).isParent()) {
                            // This row contains a parent node, so proceed to collapse the 
                            // row and then consume the event so no other actions occur
                            if (evt.isControlDown()) {
                                treeTable.collapseAll();
                            } else {
                                treeTable.collapseRow(treeTable.getSelectedRow());
                            }
                            evt.consume();
                        }
                    } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if (treeTable.getSelectedRow() >= 0
                                && !treeTable.isExpanded(treeTable.getSelectedRow())
                                && treeTable.getSelectedColumn() == treeTable.getHierarchicalColumn()
                                && getRowData(treeTable.getSelectedRow()).isParent()) {
                            // This row contains a parent node, so proceed to collapse the 
                            // row and then consume the event so no other actions occur
                            if (evt.isControlDown()) {
                                treeTable.expandAll();
                            } else {
                                treeTable.expandRow(treeTable.getSelectedRow());
                                evt.consume();
                            }
                        }
                    }
                }
            };
        } else {
            this.removeKeyListener(keyboardNavigation);
        }
        this.addKeyListener(keyboardNavigation);

        // Remove the input mapping for the Enter key so that it can be used to fire the default button on the form instead. 
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
    }

    /**
     * Retrieves all selected rows from the TreeTable. If no rows are selected,
     * the size of the returned list will be 0.
     */
    public List<TreeTableRowData> getSelectedDataRows() {
        List<TreeTableRowData> result = new ArrayList<TreeTableRowData>();
        TreePath[] selectedNodes = this.getTreeSelectionModel().getSelectionPaths();
        for (TreePath tp : selectedNodes) {
            if (tp.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                Object row = ((DefaultMutableTreeNode) tp.getLastPathComponent()).getUserObject();
                result.add((TreeTableRowData) row);
            }
        }
        return result;
    }

    /**
     * Retrieves the TreeTableRowData record at the specified row number.
     *
     * @param rowNum
     * @return
     */
    public TreeTableRowData getRowData(int rowNum) {
        TreeTableRowData result = null;
        TreePath tp = this.getPathForRow(rowNum);
        if (tp != null) {
            Object row = ((DefaultMutableTreeNode) tp.getLastPathComponent()).getUserObject();
            result = ((TreeTableRowData) row);
        }
        return result;

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

    @Override
    public void setTreeTableModel(TreeTableModel treeModel) {
        super.setTreeTableModel(treeModel);

        // Hack that ensures the background selection colour of the JXTree column 
        // (i.e. Hierarchical Column) matches the background selection colour set
        // during the constructor above. Without this hack, the background selection
        // colour of the JXTree column is based on the nimbusSelectionBackground
        // Nimbus property (i.e. ugly blue color). It seems setting the
        // InheritDefaults value to false fixes the issue, however this setting is
        // ignored unless the Overrides setting has a non null value as well. 
        JXTree tree = getTreeComponent();
        if (tree != null) {
            UIDefaults treeTableUI = new UIDefaults();
            tree.putClientProperty("Nimbus.Overrides", treeTableUI);
            tree.putClientProperty("Nimbus.Overrides.InheritDefaults", false);
        }
    }

    /**
     * @return Returns a handle to the JXTree for this TreeTable or null if no
     * tree component exists
     */
    protected JXTree getTreeComponent() {
        JXTree tree = null;
        if (getTreeTableModel() != null && getTreeTableModel().getHierarchicalColumn() >= 0) {
            tree = ((JXTree) getCellRenderer(0, getTreeTableModel().getHierarchicalColumn()));
        }
        return tree;
    }

}
