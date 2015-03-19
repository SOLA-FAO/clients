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
package org.sola.clients.swing.desktop.application;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.swing.common.controls.TreeTableRowData;

/**
 * Custom TreeTableModel used to create the tree for displaying request types.
 * Creates a two level tree with request types grouped by the display_group_name
 * field
 *
 * @author soladev
 */
public class RequestTypeTreeTableModel extends AbstractTreeTableModel {

    private static String[] COLUMN_NAMES = {"Task", "Description"};

    public RequestTypeTreeTableModel(List<RequestTypeBean> requestTypes) {
        super(new DefaultMutableTreeNode());
        String group = "";
        DefaultMutableTreeNode parent = null;
        // Group the requst types by display group name.
        for (RequestTypeBean bean : requestTypes) {
            if (!group.equals(bean.getCategoryDisplayValue())) {
                parent = new DefaultMutableTreeNode(new TreeTableRowData(true,
                        bean.getRequestCategory(), bean.getCategoryDisplayValue(), null));
                ((DefaultMutableTreeNode) this.getRoot()).add(parent);
                group = bean.getCategoryDisplayValue();
            }
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new TreeTableRowData(false,
                    bean, bean.getDisplayValue(), bean.getDescription()));
            parent.add(child);
        }
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) parent;
            return nodes.getChildCount();
        }
        return 0;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) parent;
            return nodes.getChildAt(index);
        }
        return null;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) parent;
            return dataNode.getIndex((DefaultMutableTreeNode) child);
        }
        return 0;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof TreeTableRowData) {
            TreeTableRowData data = (TreeTableRowData) node;
            return data.getColumnData()[column];
        }
        if (node instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;
            TreeTableRowData data = (TreeTableRowData) dataNode.getUserObject();
            if (data != null) {
                return data.getColumnData()[column];
            }
        }
        return null;
    }

    /**
     * Identifies the label to use for the Columns displayed in the Add Service
     * dialog.
     *
     * @param column1Lbl
     * @param column2Lbl
     */
    public void setColumnLabels(String column1Lbl, String column2Lbl) {
        COLUMN_NAMES[0] = column1Lbl;
        if (COLUMN_NAMES.length > 1) {
            COLUMN_NAMES[1] = column2Lbl;
        }

    }

    /**
     * Prevents the second column from displaying in the Tree table. 
     * Must be set before the model is added to the treetable control. 
     */
    public void hideColumn2() {
        COLUMN_NAMES = new String[]{COLUMN_NAMES[0]};
    }

}
