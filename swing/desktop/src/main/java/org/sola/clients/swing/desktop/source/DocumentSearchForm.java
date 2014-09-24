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
package org.sola.clients.swing.desktop.source;

import org.sola.clients.swing.ui.ContentPanel;
import org.sola.common.StringUtility;

/**
 * Allows to search documents and print out digital copies.
 */
public class DocumentSearchForm extends ContentPanel {

    /**
     * Default constructor.
     */
    public DocumentSearchForm() {
        initComponents();
    }

    public org.sola.clients.swing.desktop.source.DocumentSearchPanel getDocumentSearchPanel() {
        return documentSearchPanel;
    }

    public void clickFind() {
        documentSearchPanel.searchDocuments();
    }

    @Override
    public void setBreadCrumbTitle(String breadCrumbPath, String panelTitle) {
        // Ignore the BreadCrumbPath
        if (StringUtility.isEmpty(panelTitle)) {
            panelTitle = getBreadCrumbTitle();
        }
        if (getHeaderPanel() != null) {
            getHeaderPanel().setTitleText(panelTitle);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        documentSearchPanel = new org.sola.clients.swing.desktop.source.DocumentSearchPanel();

        setHeaderPanel(headerPanel1);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/source/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("DocumentSearchForm.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        headerPanel1.setName("headerPanel1"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("DocumentSearchForm.headerPanel1.titleText")); // NOI18N

        documentSearchPanel.setName(bundle.getString("DocumentSearchForm.documentSearchPanel.name")); // NOI18N
        documentSearchPanel.setShowAttachButton(false);
        documentSearchPanel.setShowSelectButton(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentSearchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(documentSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.desktop.source.DocumentSearchPanel documentSearchPanel;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    // End of variables declaration//GEN-END:variables
}
