/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.NotifyTO;

/**
 *
 * @author soladev
 */
public class NotifyListBean extends AbstractBindingListBean {

    public static final String SELECTED_NOTIFY = "selectedNotify";
    private SolaList<NotifyBean> notifyList;
    private NotifyBean selectedNotify;
    private String serviceId;

    public NotifyListBean() {
        super();
        notifyList = new SolaList<NotifyBean>();
    }

    public ObservableList<NotifyBean> getFilteredNotifyList() {
        return notifyList.getFilteredList();
    }

    public NotifyBean getSelectedNotify() {
        return selectedNotify;
    }

    public void setSelectedNotify(NotifyBean selected) {
        NotifyBean oldValue = this.selectedNotify;
        this.selectedNotify = selected;
        propertySupport.firePropertyChange(SELECTED_NOTIFY, oldValue, this.selectedNotify);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void addItem(NotifyBean item) {
        notifyList.addAsNew(item);
    }

    public void removeItem(NotifyBean item) {
        notifyList.safeRemove(item, EntityAction.DELETE);
    }

    /**
     * Retrieves the list of notify parties from the database using the service
     * id.
     *
     * @param serviceId
     */
    public void loadList(String serviceId) {
        setServiceId(serviceId);
        notifyList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getNotifyParties(serviceId),
                NotifyBean.class, (List) notifyList);
    }

    /**
     * Saves the list of Notify Parties
     */
    public void saveList() {
        List<NotifyTO> toList = new ArrayList<NotifyTO>();
        // Translate list of beans to a list of TO's 
        TypeConverters.BeanListToTransferObjectList((List) notifyList, toList, NotifyTO.class);
        notifyList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().saveNotifyParties(toList),
                NotifyBean.class, (List) notifyList);
    }

    /**
     * Overrides the default validate method to ensure all
     * PublicDisplayItemBeans are validated as well
     *
     * @param <T>
     * @param showMessage
     * @param group
     * @return List of warning messages to display to the user
     */
    @Override
    public <T extends AbstractBindingBean> Set<ConstraintViolation<T>> validate(boolean showMessage, Class<?>... group) {

        Set<ConstraintViolation<T>> warningsList = super.validate(false, group);
        for (NotifyBean bean : notifyList) {
            Set<ConstraintViolation<T>> beanWarningsList = bean.validate(false, group);
            warningsList.addAll(beanWarningsList);
        }

        if (showMessage) {
            showMessage(warningsList);
        }
        return warningsList;
    }

    /**
     * Determines whether a notify party should be added to the list of items or
     * replace an existing item in the list.
     *
     * @param item The item to add or update.
     */
    public void addOrUpdateItem(NotifyBean item) {
        if (item != null && notifyList != null) {
            if (notifyList.contains(item)) {
                notifyList.set(notifyList.indexOf(item), item);
            } else {
                notifyList.addAsNew(item);
            }
        }
    }

    /**
     * Returns list of checked notify parties.
     *
     * @param includeSelected Indicates whether to include in the list selected
     * notify party if there no notify parties checked.
     */
    public List<NotifyBean> getChecked(boolean includeSelected) {
        List<NotifyBean> checked = new ArrayList<NotifyBean>();
        for (NotifyBean bean : getFilteredNotifyList()) {
            if (bean.isChecked()) {
                checked.add(bean);
            }
        }
        if (includeSelected && checked.size() < 1 && getSelectedNotify() != null) {
            checked.add(getSelectedNotify());
        }
        return checked;
    }
}
