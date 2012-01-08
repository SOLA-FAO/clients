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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.sola.clients.swing.ui.cadastre;

import org.sola.clients.swing.common.controls.FreeTextSearch;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.CadastreClient;

/**
 *
 * @author Manoku
 */
public class CadastreObjectSearch extends FreeTextSearch {

    private CadastreClient dataSource;

    @Override
    public void onNewSearchString(String searchString, DefaultListModel listModel) {
        if (this.dataSource == null) {
            this.dataSource = WSManager.getInstance().getCadastreService();
        }

        List<CadastreObjectBean> searchResult = new LinkedList<CadastreObjectBean>();
        TypeConverters.TransferObjectListToBeanList(this.dataSource.getCadastreObjectByParts(searchString), 
                CadastreObjectBean.class, (List)searchResult);
        
        listModel.clear();
        
        for (CadastreObjectBean cadastreObject : searchResult) {
            listModel.addElement(cadastreObject);
        }
    }
}
