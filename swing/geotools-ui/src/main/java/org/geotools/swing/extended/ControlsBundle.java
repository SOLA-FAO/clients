/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */

/*
 * ControlsBundle.java
 *
 * Created on Apr 18, 2011, 5:41:25 PM
 */
package org.geotools.swing.extended;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import org.geotools.swing.control.JCoordsStatusBarItem;
import org.geotools.swing.control.JMapStatusBar;
import org.geotools.swing.control.JRendererStatusBarItem;
import org.geotools.swing.control.extended.MapScaleStatusBarItem;
import org.geotools.swing.control.extended.ScaleBarStatusBarItem;
import org.geotools.swing.control.extended.Toc;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.mapaction.extended.FullExtent;
import org.geotools.swing.tool.extended.ExtendedPan;
import org.geotools.swing.tool.extended.ExtendedZoominTool;

/**
 * This is a bundle of controls that form a single control with:<br/> a map control, <br/> toolbar
 * for adding tools and commands for the map, <br/> a statusbar, <br/> a table of layers <br/> This
 * is used to integrate in other forms or controls easily. The control is layouted in design mode.
 *
 * @author Elton Manoku
 */
public class ControlsBundle extends javax.swing.JPanel {

    private Map map;
    private JTabbedPane leftPanel;
    private Toc toc;
    JMapStatusBar statusBar;

    /**
     * Creates new ControlsBundle
     */
    public ControlsBundle() {
        initComponents();
    }

    /**
     * It sets up the control with a table of contents/ layers.
     *
     * @param srid The srid
     * @param wktOfReferenceSystem the WKT definition of Reference system if not found in the
     * srid.properties resource file. If found there there is not need to specify.
     * @param withToc
     * @throws InitializeMapException
     */
    public void Setup(int srid, String wktOfReferenceSystem, boolean withToc)
            throws InitializeMapException {
        this.initialize(srid, wktOfReferenceSystem);
        //Set default tool
        this.getMap().getToolItemByName(ExtendedPan.NAME).setSelected(true);
        this.setupLeftPanel();
    }

    /**
     * Internal method to initialize the control. It handles also the layouting.
     *
     * @param srid
     * @param wktOfReferenceSystem
     * @throws InitializeMapException
     */
    private void initialize(int srid, String wktOfReferenceSystem) throws InitializeMapException {
        if (wktOfReferenceSystem == null) {
            this.map = new Map(srid);
        } else {
            this.map = new Map(srid, wktOfReferenceSystem);
        }
        this.pnlMap.setLayout(new BorderLayout());
        this.pnlMap.add(this.map, BorderLayout.CENTER);

        this.setupToolbar();
        this.setupStatusBar();
    }

    /**
     * It adds a left panel to the bundle. In the left panel can be added different kinds of panels.
     * <br/> Table of Contents is added to this panel. It links also the TOC with the Map.
     */
    protected void setupLeftPanel() {
        this.leftPanel = new JTabbedPane();
        this.pnlMap.add(leftPanel, BorderLayout.WEST);
        this.toc = new Toc();
        this.toc.setMap(this.map);
        this.map.setToc(this.toc);
        this.addInLeftPanel(Messaging.getInstance().getMessageText(
                Messaging.Ids.LEFT_PANEL_TAB_LAYERS_TITLE.toString()),
                this.toc);
    }

    /**
     * It adds in the left panel a tab with the panel provided.
     *
     * @param title The title to appear in the ne tab
     * @param panel The panel to add
     */
    protected void addInLeftPanel(String title, JPanel panel) {
        this.leftPanel.insertTab(title, null, panel, null, this.leftPanel.getTabCount());
    }

    /**
     * It starts up the statusbar. It adds the basic navigation commands and tools.
     */
    protected void setupToolbar() {
        this.getMap().addMapAction(new FullExtent(this.getMap()), this.mapToolbar, true);
        this.getMap().addMapAction(
                new org.geotools.swing.mapaction.extended.ZoomOutAction(this.getMap()),
                this.mapToolbar, true);
        this.getMap().addTool(new ExtendedZoominTool(), this.mapToolbar, true);
        this.getMap().addTool(new ExtendedPan(), this.mapToolbar, true);
    }

    /**
     * It starts up the Statusbar.
     */
    private void setupStatusBar() {
        this.pnlStatusbar.setLayout(new BorderLayout());
        statusBar = new JMapStatusBar();
        statusBar.addItem(new JRendererStatusBarItem(this.getMap()), false, true);
        JCoordsStatusBarItem coordStatusItem = new JCoordsStatusBarItem(this.getMap());
        statusBar.addItem(coordStatusItem);
        // Adds the Scale Bar and Map Scale to the status bar of the map. Uses MigLayout constraints
        // to position the items on the status bar. 
        statusBar.addItem(new ScaleBarStatusBarItem(this.getMap()), false, true, "push, align center");
        statusBar.addItem(new MapScaleStatusBarItem(this.getMap()), false, true, "align right");
        this.pnlStatusbar.add(statusBar, BorderLayout.CENTER);
        //statusBar.addItem(null);
    }

    /**
     * Returns the status bar for the control bundle. 
     */
    protected JMapStatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * Gets the map control
     *
     * @return
     */
    public Map getMap() {
        return this.map;
    }

    /**
     * Get the toolbar
     *
     * @return
     */
    public JToolBar getToolbar() {
        return this.mapToolbar;
    }

    /**
     * Gets the Table of Contents
     *
     * @return
     */
    public Toc getToc() {
        return toc;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMap = new javax.swing.JPanel();
        pnlStatusbar = new javax.swing.JPanel();
        mapToolbar = new javax.swing.JToolBar();

        setBackground(new java.awt.Color(255, 255, 255));
        setName("Form");

        pnlMap.setName("pnlMap");

        javax.swing.GroupLayout pnlMapLayout = new javax.swing.GroupLayout(pnlMap);
        pnlMap.setLayout(pnlMapLayout);
        pnlMapLayout.setHorizontalGroup(
            pnlMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 584, Short.MAX_VALUE)
        );
        pnlMapLayout.setVerticalGroup(
            pnlMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 492, Short.MAX_VALUE)
        );

        pnlStatusbar.setName("pnlStatusbar");

        javax.swing.GroupLayout pnlStatusbarLayout = new javax.swing.GroupLayout(pnlStatusbar);
        pnlStatusbar.setLayout(pnlStatusbarLayout);
        pnlStatusbarLayout.setHorizontalGroup(
            pnlStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 584, Short.MAX_VALUE)
        );
        pnlStatusbarLayout.setVerticalGroup(
            pnlStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        mapToolbar.setFloatable(false);
        mapToolbar.setRollover(true);
        mapToolbar.setName("mapToolbar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlStatusbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mapToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
            .addComponent(pnlMap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(mapToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar mapToolbar;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JPanel pnlStatusbar;
    // End of variables declaration//GEN-END:variables
}
