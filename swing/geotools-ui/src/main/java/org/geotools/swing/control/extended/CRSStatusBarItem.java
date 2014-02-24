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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.geotools.swing.control.StatusBarItem;
import org.geotools.swing.extended.Map;

/**
 *
 * @author Elton Manoku
 */
public class CRSStatusBarItem extends StatusBarItem {

    private static final String COMPONENT_NAME = "CRSStatusbarItem";
    private Map map;

    public CRSStatusBarItem(Map map) {
        super(COMPONENT_NAME, false);
        this.map = map;
        addVisualControl();
    }

    protected void addVisualControl() {
        if (map.getCRSList().size() == 1){
            JLabel lblSrid = new JLabel();
            lblSrid.setText(map.getCRSList().get(0).toString());
            add(lblSrid);
            return;
        }
        JComboBox cmbSrid = new JComboBox();
        for (CRSItem item : map.getCRSList()) {
            cmbSrid.addItem(item);
            if (item.getSrid() == map.getSrid()){
                cmbSrid.setSelectedItem(item);
            }
        }
        add(cmbSrid);
        cmbSrid.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cmb = (JComboBox) e.getSource();
                onCRSChange((CRSItem) cmb.getSelectedItem());
            }
        });
    }
    
    private void onCRSChange(CRSItem selectedCRSItem){
        map.setCRS(selectedCRSItem.getCrs());
    }
}
