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
