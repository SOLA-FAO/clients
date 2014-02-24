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

import java.util.List;
import javax.swing.JCheckBox;
import org.geotools.map.extended.layer.ExtendedLayer;

/**
 * The TocLayerNode is a checkbox which can optionally have TocSymbology nodes.
 * @author Elton Manoku
 */
public class TocLayerNode extends AbstractTocNode {

    /**
     * It initializes a Node in the Table of Contents of the type layer.
     * @param layer The map layer which is associated with the node
     */
    public TocLayerNode(ExtendedLayer layer) {
        super(layer);
        this.initialize();
    }

    /**
     * internal initializer of the node.
     */
    private void initialize() {
        this.visualisationComponent = new JCheckBox();
        this.getVisualisationComponent().setText(this.getLayer().getTitle());
        this.getVisualisationComponent().setSelected(this.getLayer().isVisible());
        this.addSymbology();
    }

    @Override
    public JCheckBox getVisualisationComponent() {
        return (JCheckBox) this.visualisationComponent;
    }

    /**
     * It changes the visibility of the map layer. If it is visible is made non-visible
     * and the other way around.
     */
    public void changeVisibility() {
        boolean currentVisibility = this.getLayer().isVisible();
        this.getLayer().setVisible(!currentVisibility);
        this.getVisualisationComponent().setSelected(this.getLayer().isVisible());
    }

    /**
     * It is called in Node Click. Here the Node Clicked event is catched.
     */
    public void OnNodeClicked() {
        this.changeVisibility();
    }

    /**
     * It retrieves the symbology from the layer that is associated with the node
     * and adds the TocSymbology Nodes as child nodes.
     */
    private void addSymbology() {
        List<TocSymbol> tocSymbolList = this.getLayer().getLegend();
        if (tocSymbolList == null || tocSymbolList.isEmpty()) {
            return;
        }
        for (TocSymbol tocSymbol : tocSymbolList) {
            TocSymbologyNode symbologyNode = 
                    new TocSymbologyNode(tocSymbol.getTitle(), tocSymbol.getImage());
            this.add(symbologyNode);
        }
    }
}
