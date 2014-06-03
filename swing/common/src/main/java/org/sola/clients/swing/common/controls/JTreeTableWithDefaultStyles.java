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
package org.sola.clients.swing.common.controls;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * Customized SwingX JXTreeTable intended to look much like the SOLA
 * JTableWithDefaultStyles.
 *
 * @author solaDev
 */
public class JTreeTableWithDefaultStyles extends org.jdesktop.swingx.JXTreeTable {

    private Color scrollPaneBackgroundColor;
    private Color defaultBackground;
    private Color oddRowColor;
    private Color selectBackground;
    private Color selectForeground;

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

        Object newSecondRow = "PasswordField.background";
        Color newSRColor = UIManager.getColor(newSecondRow);
        oddRowColor = newSRColor;

        Object newSelectedRow = "List.background";
        selectBackground = UIManager.getColor(newSelectedRow);
        this.setSelectionBackground(selectBackground);

        Object newSelForecolor = "List.foreground";
        selectForeground = UIManager.getColor(newSelForecolor);
        this.setSelectionForeground(selectForeground);
        this.setGridColor(selectForeground);

        scrollPaneBackgroundColor = Color.WHITE;
        super.setBackground(scrollPaneBackgroundColor);
        this.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        Object tableFont = "Table.font";
        Font newTableFont = UIManager.getFont(tableFont);
        this.setFont(newTableFont);
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
