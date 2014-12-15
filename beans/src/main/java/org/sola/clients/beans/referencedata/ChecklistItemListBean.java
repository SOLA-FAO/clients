/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.referencedata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaCodeList;
import org.sola.common.mapping.MappingManager;
import org.sola.common.mapping.MappingUtility;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * CodeList class for ChecklistItemBean. Supports selection of multiple
 * checklist items using the checked property of the bean
 *
 * @author soladev
 */
public class ChecklistItemListBean extends AbstractBindingListBean {

    private class CheckedListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(AbstractCodeBean.CHECKED_PROPERTY)) {
                ChecklistItemBean bean = (ChecklistItemBean) evt.getSource();
                EntityAction action = null;
                if (!(Boolean) evt.getNewValue()) {
                    // The checked property is deselected, so ensure the bean will
                    // be disassociated when saved. 
                    action = EntityAction.DISASSOCIATE;
                }
                bean.setEntityAction(action);
                if (selectedItems.contains(bean)) {
                    selectedItems.get(selectedItems.indexOf(bean)).setEntityAction(action);
                } else {
                    selectedItems.add(bean);
                }
            }
        }
    }

    public static final String SELECTED_CHECKLIST_ITEM = "selectedChecklistItem";
    private SolaCodeList<ChecklistItemBean> checklistItemList;
    private ChecklistItemBean selectedChecklistItem;
    private List<ChecklistItemBean> selectedItems;
    private CheckedListener listener = new CheckedListener();

    // UI Designer constructor
    public ChecklistItemListBean() {
        checklistItemList = new SolaCodeList<ChecklistItemBean>();
    }

    /**
     * General constructor to load the list bean.
     *
     * @param selectedItems The checklist items that have already been selected
     * for the checklist group.
     */
    public ChecklistItemListBean(List<ChecklistItemBean> selectedItems) {
        if (selectedItems != null) {
            this.selectedItems = selectedItems;
        } else {
            this.selectedItems = new ArrayList<ChecklistItemBean>();
        }
        // Setup an Observable List Listener to update the selectedItems list if the
        // user sets or unsets the checked property. 
        checklistItemList = new SolaCodeList<ChecklistItemBean>();
        checklistItemList.addObservableListListener(new ObservableListListener() {
            @Override
            public void listElementsAdded(ObservableList list, int index, int length) {
                for (int i = index; i < length + index; i++) {
                    ((ChecklistItemBean) list.get(i)).addPropertyChangeListener(listener);
                }
            }

            @Override
            public void listElementsRemoved(ObservableList list, int index, List oldElements) {
                for (Object app : oldElements) {
                    ((ChecklistItemBean) app).removePropertyChangeListener(listener);
                }
            }

            @Override
            public void listElementReplaced(ObservableList list, int index, Object oldElement) {
                ((ChecklistItemBean) oldElement).removePropertyChangeListener(listener);
            }

            @Override
            public void listElementPropertyChanged(ObservableList list, int index) {
            }
        });
        loadList(false);
        setChecked(this.selectedItems);
    }

    public ObservableList<ChecklistItemBean> getChecklistItemList() {
        return checklistItemList.getFilteredList();
    }

    public final void loadList(boolean createDummy) {
        // Use the Mapping Manager to clone the list from CacheManager so that
        // the CacheManager list is not modified by the user. 
        List<ChecklistItemBean> tmpList = new ArrayList<ChecklistItemBean>();
        MappingUtility.translateList(CacheManager.getChecklistItems(), tmpList,
                ChecklistItemBean.class, MappingManager.getMapper());
        loadCodeList(ChecklistItemBean.class, checklistItemList,
                tmpList, createDummy);
    }

    public ChecklistItemBean getSelectedChecklistItem() {
        return selectedChecklistItem;
    }

    public void setSelectedChecklistItem(ChecklistItemBean value) {
        this.selectedChecklistItem = value;
        propertySupport.firePropertyChange(SELECTED_CHECKLIST_ITEM,
                null, selectedChecklistItem);
    }

    /**
     * Uses the selectedItems list to set the checked field the master (a.k.a.
     * checklistItemList) list
     *
     * @param selectedItems
     */
    private void setChecked(List<ChecklistItemBean> selectedItems) {
        if (selectedItems != null && selectedItems.size() > 0) {
            for (ChecklistItemBean bean : selectedItems) {
                if (checklistItemList.contains(bean)) {
                    checklistItemList.get(checklistItemList.indexOf(bean)).setChecked(true);
                }
            }
        }
    }
}
