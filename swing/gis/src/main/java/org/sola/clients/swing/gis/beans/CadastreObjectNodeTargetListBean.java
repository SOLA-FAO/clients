/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import org.sola.clients.beans.controls.SolaObservableList;

/**
 * The list bean is used to encapsulate the list initialization and managing of the 
 * cadastre object node target lists.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectNodeTargetListBean extends AbstractListSpatialBean{

    public CadastreObjectNodeTargetListBean() {
        super();
    }

    @Override
    protected SolaObservableList initializeBeanList() {
        return new SolaObservableList<CadastreObjectNodeTargetBean>();
    }
}
