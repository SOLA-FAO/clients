/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.control;

import java.awt.Dimension;
import javax.swing.JComboBox;
import org.sola.clients.beans.referencedata.HierarchyLevelListBean;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupBean;
import org.sola.clients.swing.gis.beans.SpatialUnitGroupListBean;

/**
 *
 * @author Elton Manoku
 */
public class SpatialUnitGroupOptionControl extends JComboBox {
    
    private HierarchyLevelListBean beanList = new HierarchyLevelListBean();
    public SpatialUnitGroupOptionControl(){
        super();
        this.setMinimumSize(new Dimension(100, 20));
        this.setMaximumSize(new Dimension(100, 20));
        initializeOptions();
    }

    private void initializeOptions() {
        for(Object bean: beanList.getSpatialUnitGroupHierarchyList()){
            this.addItem(bean);
        }
    }
    
}
