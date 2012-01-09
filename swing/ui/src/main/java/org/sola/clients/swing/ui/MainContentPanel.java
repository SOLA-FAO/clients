/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Displays different panels on the Main form.
 */
public class MainContentPanel extends javax.swing.JPanel {

    public final String CARD_DASHBOARD = "dashboard";
    public final String CARD_PERSONS = "persons";
    public final String CARD_APPSEARCH = "appsearch";
    public final String CARD_BAUNIT_SEARCH = "baunitsearch";
    public final String CARD_DOCUMENT_SEARCH = "documentsearch";
    public final String CARD_APPASSIGNMENT = "appassignment";
    public final String CARD_MAP = "map";
    private HashMap<String, Component> cards;

    /** Default constructor. */
    public MainContentPanel() {
        cards = new HashMap<String, Component>();
        initComponents();
    }

    /** 
     * Checks if panel already opened. 
     * @param panelClass Class of the panel to search for.
     */
    public boolean isPanelOpened(String cardName) {
        return cards.containsKey(cardName);
    }

    /** 
     * Adds panel into cards panels collection. 
     * @param panel Panel object to add into the cards collection.
     * @param cardName Name of the card to assign to the added panel.
     */
    public void addPanel(Component panel, String cardName) {
        cards.put(cardName, panel);
        pnlContent.add(panel, cardName);
    }

    /** 
     * Returns card/panel by the given card name. 
     * @param cardName Name of the card to search by.
     */
    public Component getPanel(String cardName) {
        return cards.get(cardName);
    }
    
    /** 
     * Closes panel by component object. 
     * @param panel Panel object to remove from the cards collection.
     */
    public void closePanel(Component panel) {
        if (cards.containsValue(panel)) {
            Set<Entry<String, Component>> tab = cards.entrySet();
            for (Iterator<Entry<String, Component>> it = tab.iterator(); it.hasNext();) {
                Entry<String, Component> entry = it.next();
                if (entry.getValue().equals(panel)) {
                    cards.remove(entry.getKey());
                    break;
                }
            }
            pnlContent.remove(panel);
        }
    }

    /** 
     * Closes panel by card name. 
     * @param cardName Name of the card to close.
     */
    public void closePanel(String cardName) {
        if(cards.containsKey(cardName)){
            pnlContent.remove(cards.get(cardName));
            cards.remove(cardName);
        }
    }
    
    /** 
     * Shows panel by the given card name. 
     * @param cardName Name of the card to search by.
     */
    public void showPanel(String cardName) {
        ((CardLayout) pnlContent.getLayout()).show(pnlContent, cardName);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContent = new javax.swing.JPanel();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(MainContentPanel.class);
        pnlContent.setBackground(resourceMap.getColor("pnlContent.background")); // NOI18N
        pnlContent.setName("pnlContent"); // NOI18N
        pnlContent.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlContent;
    // End of variables declaration//GEN-END:variables
}
