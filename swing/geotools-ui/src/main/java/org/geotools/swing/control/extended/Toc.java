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
package org.geotools.swing.control.extended;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.geotools.swing.extended.Map;

/**
 * This is the table of contents / layers which can be associated with the map control. It is
 * implemented through a JTree. The root node is hidden from the GUI. Each layer is implemented as a
 * Node with a checkbox. Additionally, the layer node can have one or more Symbology Nodes {
 *
 * @see TocSymbologyNode.java} where the symbology is displayed.
 *
 * @author Elton Manoku
 */
public class Toc extends JPanel {

    private JTree tree;
    DefaultMutableTreeNode top;
    private Map map;
    DefaultTreeModel treeModel;

    /**
     * It initializes the TOC.
     */
    public Toc() {
        top = new DefaultMutableTreeNode("Layers");
        treeModel = new DefaultTreeModel(top);
        tree = new JTree(treeModel);
        this.setLayout(new BorderLayout());
        JScrollPane scroller = new JScrollPane(tree);
        this.add(scroller, BorderLayout.CENTER);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        TocCellRenderer renderer = new TocCellRenderer();
        tree.setCellRenderer(renderer);
        this.setPreferredSize(new Dimension(250, 200));
        this.addListners();
    }

    /**
     * It adds different listeners to the objects in the TOC.
     */
    private void addListners() {
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                doMouseClicked(me);
            }
        });

    }

    /**
     * It handles the click on the Node itself.
     *
     * @param me
     */
    private void doMouseClicked(MouseEvent me) {
        TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
        if (tp == null) {
            return;
        }
        Object clickedNode = tp.getLastPathComponent();
        if (clickedNode instanceof TocLayerNode) {
            TocLayerNode clickedTocNode = (TocLayerNode) tp.getLastPathComponent();
            changeNodeSwitch(clickedTocNode);
        }
    }

    /**
     * Sets the map. With this, the linking with the map is established.
     *
     * @param map
     */
    public void setMap(Map map) {
        this.map = map;
        for (ExtendedLayer solaLayer : this.map.getSolaLayers().values()) {
            if (!solaLayer.isShowInToc()) {
                continue;
            }
            this.addLayer(solaLayer);
        }
    }

    /**
     * It adds a layer in the TOC. The layer is added in the top.
     *
     * @param layer
     */
    public void addLayer(ExtendedLayer layer) {
        TocLayerNode tocNode = new TocLayerNode(layer);
        this.treeModel.insertNodeInto(tocNode, top, 0);
        tree.scrollPathToVisible(new TreePath(tocNode.getPath()));
    }

    /**
     * Gets a list of TOC Nodes
     *
     * @return
     */
    public TocLayerNode getTocLayerNode(String layerName) {
        TocLayerNode tocNode;
        for (int nodeIndex = 0; nodeIndex < top.getChildCount(); nodeIndex++) {
            if (top.getChildAt(nodeIndex) instanceof TocLayerNode) {
                tocNode = (TocLayerNode) top.getChildAt(nodeIndex);
                if (tocNode.getLayer().getLayerName().equals(layerName)) {
                    return tocNode;
                }
            }
        }
        return null;
    }

    /**
     * It forces the node to change its status (from checked to unchecked and otherway around).
     * <br/>
     * If node is not found nothing happen.
     * 
     * @param layerName The name of the layer
     */
    public void changeNodeSwitch(String layerName) {
        TocLayerNode tocNode = this.getTocLayerNode(layerName);
        if (tocNode != null) {
            tocNode.OnNodeClicked();
            this.treeModel.nodeChanged(tocNode);
        }
    }

    /**
     * It forces the node to change its status (from checked to unchecked and otherway around).
     * <br/>
     * If node is not found nothing happen.
     * @param tocNode The node to change the switch. If the node is null nothing will happen
     */
    public void changeNodeSwitch(TocLayerNode tocNode) {
        if (tocNode != null) {
            tocNode.OnNodeClicked();
            this.treeModel.nodeChanged(tocNode);
        }
    }
}
