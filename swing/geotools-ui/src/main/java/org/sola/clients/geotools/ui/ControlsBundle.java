/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
 * ControlsBundle.java
 *
 * Created on Apr 18, 2011, 5:41:25 PM
 */
package org.sola.clients.geotools.ui;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import org.sola.clients.geotools.ui.mapactions.FullExtent;
import org.sola.clients.geotools.ui.maptools.SolaPan;
import org.sola.clients.geotools.ui.maptools.SolaZoominTool;

/**
 * This is a bundle of controls that form a single control with a map control, toolbar for adding 
 * tools and commands for the map and a statusbar. This is used to integrate in other forms or 
 * controls easily.
 * The control is layouted in design mode.
 *
 * @author Elton Manoku (date July 2011)
 */
public class ControlsBundle extends javax.swing.JPanel {

    private Map map;
    private Toc toc;

    /** Creates new ControlsBundle */
    public ControlsBundle() {
        initComponents();
    }

    /**
     * This sets up the control. It initializes child controls. It must be called directly after
     * the initialization of the control and before being displayed.
     * @param srid The srid that is used for initializing the map control.
     * @throws Exception 
     */
    public void Setup(int srid) throws Exception {
        this.initialize(srid);
    }
    
    public void Setup(int srid, boolean withToc) throws Exception {
        this.initialize(srid);
        this.setupToc();
    }

    private void initialize(int srid) throws Exception {
        this.map = new Map(srid);
        this.pnlMap.setLayout(new BorderLayout());
        this.pnlMap.add(this.map, BorderLayout.CENTER);
        this.setupToolbar();
        this.setupStatusBar();
    }

    /**
     * It starts up the statusbar. It adds the basic navigation commands and tools.
     */
    private void setupToolbar() {
        this.getMap().addMapAction(new FullExtent(this.getMap()), this.mapToolbar);
        this.getMap().addMapAction(
                new org.sola.clients.geotools.ui.mapactions.ZoomOutAction(this.getMap()),
                this.mapToolbar);
        this.getMap().addTool(new SolaZoominTool(), this.mapToolbar);
        this.getMap().addTool(new SolaPan(), this.mapToolbar);
    }

    /**
     * This is not yet used.
     */
    private void setupToc() {
        this.toc = new Toc();
        //JScrollPane tocScrollPane = new JScrollPane(this.toc);
        this.pnlMap.add(this.toc, BorderLayout.WEST);
        this.toc.setMap(this.map);
        this.map.setToc(this.toc);
    }

    private void setupStatusBar() {
        SolaStatusbar mapStatusbar = new SolaStatusbar(this.map);
        this.pnlStatusbar.setLayout(new BorderLayout());
        this.pnlStatusbar.add(mapStatusbar, BorderLayout.CENTER);
    }

    
    /**
     * This is not yet used.
     */
    public void switchTocVisibility() {
//        if (this.pnlToc != null) {
//            this.pnlToc.setVisible(!this.pnlToc.isVisible());
//            this.pnlToc.repaint();
//        }
    }

    /**
     * Gets the map control
     * @return 
     */
    public Map getMap() {
        return this.map;
    }
    

    /**
     * Get the toolbar
     * @return 
     */
    public JToolBar getToolbar(){
        return this.mapToolbar;
    }

    public Toc getToc() {
        return toc;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMap = new javax.swing.JPanel();
        pnlToc = new javax.swing.JPanel();
        pnlStatusbar = new javax.swing.JPanel();
        mapToolbar = new javax.swing.JToolBar();

        setBackground(new java.awt.Color(255, 255, 255));
        setName("Form"); // NOI18N

        pnlMap.setBackground(new java.awt.Color(255, 102, 102));
        pnlMap.setName("pnlMap"); // NOI18N

        pnlToc.setName("pnlToc"); // NOI18N

        javax.swing.GroupLayout pnlTocLayout = new javax.swing.GroupLayout(pnlToc);
        pnlToc.setLayout(pnlTocLayout);
        pnlTocLayout.setHorizontalGroup(
            pnlTocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 186, Short.MAX_VALUE)
        );
        pnlTocLayout.setVerticalGroup(
            pnlTocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 282, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlMapLayout = new javax.swing.GroupLayout(pnlMap);
        pnlMap.setLayout(pnlMapLayout);
        pnlMapLayout.setHorizontalGroup(
            pnlMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlToc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(388, Short.MAX_VALUE))
        );
        pnlMapLayout.setVerticalGroup(
            pnlMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapLayout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addComponent(pnlToc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );

        pnlToc.getAccessibleContext().setAccessibleParent(this);

        pnlStatusbar.setName("pnlStatusbar"); // NOI18N

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
        mapToolbar.setName("mapToolbar"); // NOI18N

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
    private javax.swing.JPanel pnlToc;
    // End of variables declaration//GEN-END:variables
}
