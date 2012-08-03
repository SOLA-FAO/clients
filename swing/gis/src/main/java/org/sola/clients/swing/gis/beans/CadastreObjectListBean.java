/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import org.sola.clients.beans.controls.SolaObservableList;

/**
 * The list bean is used to encapsulate the list initialization and managing of the 
 * cadastre object lists.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectListBean extends AbstractListSpatialBean{

    public CadastreObjectListBean() {
        super();
    }

    @Override
    protected SolaObservableList initializeBeanList() {
        return new SolaObservableList<CadastreObjectBean>();
    }
}
