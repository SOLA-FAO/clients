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
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.CadastreClient;

/**
 *
 * @author Manoku
 */
public class CadastreObjectSearch extends FreeTextSearch {

    private SolaTask searchTask = null;

    public CadastreObjectSearch() {
        super();
        this.setMinimalSearchStringLength(3);
    }
    
    @Override
    public void onNewSearchString(final String searchString, final DefaultListModel listModel) {

    // Check if a search is currently running and if so, cancel it
    if (searchTask != null && TaskManager.getInstance().isTaskRunning(searchTask.getId())) {
        TaskManager.getInstance().removeTask(searchTask);
    }

    // Use a SolaTask to make the search much smoother. 
    final List<CadastreObjectBean> searchResult = new LinkedList<CadastreObjectBean>();
    listModel.clear();
    searchTask = new SolaTask<Void, Void>() {

        @Override
        public Void doTask() {
            // Perform the search on a background thread
            setMessage(MessageUtility.getLocalizedMessage(
                    ClientMessage.PROGRESS_MSG_MAP_SEARCHING,
                    new String[]{""}).getMessage());
            try {
                // Allow a small delay on the background thread so that the thread can be cancelled
                // before executing the search if the user is still typing. 
                Thread.sleep(500);
                TypeConverters.TransferObjectListToBeanList(
                        WSManager.getInstance().getCadastreService().getCadastreObjectByParts(searchString),
                        CadastreObjectBean.class, (List) searchResult);
            } catch (InterruptedException ex) {
            }
            return null;
        }

        @Override
        public void taskDone() {
            // Update the GUI using the primary EDT thread
            if (searchResult.size() > 0) {
                for (CadastreObjectBean cadastreObject : searchResult) {
                    listModel.addElement(cadastreObject);
                }
            }
        }
    };
    TaskManager.getInstance().runTask(searchTask);
}
}
