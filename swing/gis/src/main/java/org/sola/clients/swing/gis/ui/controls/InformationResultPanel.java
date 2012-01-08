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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * InformationResultPanel.java
 *
 * Created on Jul 1, 2011, 2:23:36 PM
 */
package org.sola.clients.swing.gis.ui.controls;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.sola.webservices.search.GenericResult;
import net.java.dev.jaxb.array.StringArray;
/**
 *
 * @author Manoku
 */
public class InformationResultPanel extends javax.swing.JPanel {
    
    private ArrayList<String> fieldsNotToDisplay = new ArrayList<String>();

    /** Creates new form InformationResultPanel */
    public InformationResultPanel() {
        initComponents();
    }
    
    public InformationResultPanel(GenericResult result) {
        this();
        fieldsNotToDisplay.add("id");
        fieldsNotToDisplay.add("the_geom");
        this.tblResult.removeAll();
        DefaultTableModel tableModel = new DefaultTableModel();
        List<Integer> indexListToShow = new ArrayList<Integer>();

        //Add columns
        for (Integer fieldInd = 0; fieldInd < result.getFieldNames().size(); fieldInd++) {
            String fieldName = result.getFieldNames().get(fieldInd);
            if (fieldsNotToDisplay.contains(fieldName)) {
                continue;
            }
            tableModel.addColumn(fieldName);
            indexListToShow.add(fieldInd);
        }

        //Add values
        for (Object row : result.getValues()) {
            StringArray rowAsArray = (StringArray) row;
            String[] rowToAdd = new String[indexListToShow.size()];
            for (Integer fieldIndIndex = 0; 
                    fieldIndIndex < indexListToShow.size(); fieldIndIndex++) {
                rowToAdd[fieldIndIndex] = 
                        rowAsArray.getItem().get(indexListToShow.get(fieldIndIndex));
            }
            tableModel.addRow(rowToAdd);
        }
        this.tblResult.setModel(tableModel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblResult = new javax.swing.JTable();

        tblResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblResult);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblResult;
    // End of variables declaration//GEN-END:variables
}
