/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.ui.cadastre;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.common.controls.TextSearch;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Extends {@link TextSearch} components to search for cadastre objects
 */
public class CadastreObjectSearch2 extends TextSearch {

    public CadastreObjectSearch2() {
        super();
    }

    @Override
    public void search(String searchText) {
        super.search(searchText);
        List<CadastreObjectBean> searchResult = new LinkedList<CadastreObjectBean>();
        
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCadastreService().getCadastreObjectByParts(searchText),
                CadastreObjectBean.class, (List) searchResult);
        setDataList(searchResult);
    }
}
