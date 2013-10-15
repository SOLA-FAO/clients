/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.ui.cadastre;

import org.sola.clients.swing.common.controls.FreeTextSearch;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.cadastre.LocationBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.CadastreClient;

/**
 *
 * @author RizzoM
 */
public class LocationSearch extends FreeTextSearch {

    private SolaTask searchTask = null;

    public LocationSearch() {
        super();
        this.setMinimalSearchStringLength(3);
        setRefreshTextInSelection(true);
    }

    @Override
    public void onNewSearchString(final String searchString, final DefaultListModel listModel) {

        // Check if a search is currently running and if so, cancel it
        if (searchTask != null && TaskManager.getInstance().isTaskRunning(searchTask.getId())) {
            TaskManager.getInstance().removeTask(searchTask);
        }

        // Use a SolaTask to make the search much smoother. 
        final List<LocationBean> searchResult = new LinkedList<LocationBean>();
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
                            LocationBean.class, (List) searchResult);
                } catch (InterruptedException ex) {
                }
                return null;
            }

            @Override
            public void taskDone() {
                // Update the GUI using the primary EDT thread
                String oldValue = "begin";
                String newValue;
                String subStringLastpart;
                if (searchResult.size() > 0) {
                    for (LocationBean cadastreObject : searchResult) {
                        newValue = cadastreObject.getNameLastpart();
//                        subStringLastpart = cadastreObject.getNameLastpart().substring(cadastreObject.getNameLastpart().indexOf(" ")).trim();
//                        newValue = cadastreObject.getNameLastpart().substring(0, (subStringLastpart.indexOf(" ") + cadastreObject.getNameLastpart().indexOf(" ") + 1));
//                        if (!newValue.toUpperCase().trim().contains(oldValue.toUpperCase().trim())) {

                        if (!newValue.contains(oldValue)) {
                            listModel.addElement(cadastreObject);
                        }

//                        subStringLastpart = cadastreObject.getNameLastpart().substring(cadastreObject.getNameLastpart().indexOf(" ")).trim();
//                        oldValue = cadastreObject.getNameLastpart().substring(0, (subStringLastpart.indexOf(" ") + cadastreObject.getNameLastpart().indexOf(" ") + 1));

                        oldValue = cadastreObject.getNameLastpart();

                    }
                }
            }
        };
        TaskManager.getInstance().runTask(searchTask);
    }
}
