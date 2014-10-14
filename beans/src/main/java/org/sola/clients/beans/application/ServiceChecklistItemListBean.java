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
package org.sola.clients.beans.application;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.ChecklistGroupBean;
import org.sola.clients.beans.referencedata.ChecklistItemBean;
import org.sola.common.StringUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.ServiceChecklistItemTO;

/**
 *
 * @author soladev
 */
public class ServiceChecklistItemListBean extends AbstractBindingListBean {

    public static final String SELECTED_SERVICE_CHECKLIST_ITEM = "selectedServiceChecklistItem";
    private SolaList<ServiceChecklistItemBean> serviceChecklistItemList;
    private ServiceChecklistItemBean selectedServiceChecklistItem;
    private String serviceId;

    public ServiceChecklistItemListBean() {
        super();
    }

    public ObservableList<ServiceChecklistItemBean> getServiceChecklistItemList() {
        if (serviceChecklistItemList == null) {
            serviceChecklistItemList = new SolaList<ServiceChecklistItemBean>();
        }
        return serviceChecklistItemList.getFilteredList();
    }

    public ServiceChecklistItemBean getSelectedServiceChecklistItem() {
        return selectedServiceChecklistItem;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setSelectedServiceChecklistItem(ServiceChecklistItemBean selectedItem) {
        ServiceChecklistItemBean oldValue = this.selectedServiceChecklistItem;
        this.selectedServiceChecklistItem = selectedItem;
        propertySupport.firePropertyChange(SELECTED_SERVICE_CHECKLIST_ITEM, oldValue, this.selectedServiceChecklistItem);
    }

    public void saveList(String checklistGroupCode) {
        List<ServiceChecklistItemTO> toList = new ArrayList<ServiceChecklistItemTO>();
        // Translate list of beans to a list of TO's 
        TypeConverters.BeanListToTransferObjectList((List) serviceChecklistItemList, toList, ServiceChecklistItemTO.class);
        serviceChecklistItemList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().saveServiceChecklistItem(checklistGroupCode, toList),
                ServiceChecklistItemBean.class, (List) serviceChecklistItemList);
    }

    public void addItem(ServiceChecklistItemBean item) {
        serviceChecklistItemList.addAsNew(item);
    }

    public void removeItem(ServiceChecklistItemBean item) {
        serviceChecklistItemList.safeRemove(item, EntityAction.DELETE);
    }

    /**
     * Adds any new checklist items from the selected Checklist Group to the
     * list of service checklist items. Also removes any service checklist items
     * that do not relate to an item in the selected Checklist Group. Items from
     * the group that are already in the service checklist remain unchanged.
     *
     * @param checklistGroupBean The selected Checklist Group.
     */
    public void loadList(ChecklistGroupBean checklistGroupBean) {

        // Remove any service checklist items that are not in the selected Checklist Group. 
        ListIterator<ServiceChecklistItemBean> it = serviceChecklistItemList.listIterator();
        boolean found;
        while (it.hasNext()) {
            ServiceChecklistItemBean serviceItem = it.next();
            // Check if this is a custom checklist item. If so, skip it.
            if (!StringUtility.isEmpty(serviceItem.getItemCode())) {
                found = false;
                if (checklistGroupBean != null) {
                    for (ChecklistItemBean item : checklistGroupBean.getChecklistItemList()) {
                        if (serviceItem.getItemCode().equalsIgnoreCase(item.getCode())) {
                            // Service Checklist item is also an item in the Checklist Group. 
                            // Do not delete this item from the list. 
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    // Use safeRemove to delete items from the list that have not yet
                    // been saved to the database. 
                    serviceChecklistItemList.safeRemove(serviceItem, EntityAction.DELETE);
                }
            }
        }

        if (checklistGroupBean != null && checklistGroupBean.getChecklistItemList().size() > 0) {
            // Add any checklist items in the Checklist Group that are not in the list 
            // of Serivce Checklist items. 
            for (ChecklistItemBean item : checklistGroupBean.getChecklistItemList()) {
                found = false;
                for (ServiceChecklistItemBean serviceItem : serviceChecklistItemList) {
                    if (!StringUtility.isEmpty(serviceItem.getItemCode())
                            && serviceItem.getItemCode().equalsIgnoreCase(item.getCode())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Add the new ServiceChecklistItem. 
                    ServiceChecklistItemBean bean = new ServiceChecklistItemBean();
                    bean.setItemCode(item.getCode());
                    bean.setServiceId(serviceId);
                    bean.setItemDisplayValue(item.getDisplayValue());
                    bean.setItemDescription(item.getDescription());
                    bean.setItemDisplayOrder(item.getDisplayOrder());
                    bean.setComplies(false);
                    serviceChecklistItemList.addAsNew(bean);
                }
            }
        }
    }

    /**
     * Retrieves the list of service checklist items from the database using the
     * service id.
     *
     * @param serviceId
     */
    public void loadList(String serviceId) {
        setServiceId(serviceId);
        serviceChecklistItemList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getServiceChecklistItem(serviceId),
                ServiceChecklistItemBean.class, (List) serviceChecklistItemList);
    }

    /**
     * Overrides the default validate method to ensure all ServiceChecklistItems
     * are validated as well
     *
     * @param <T>
     * @param showMessage
     * @param group
     * @return List of warning messages to display to the user
     */
    @Override
    public <T extends AbstractBindingBean> Set<ConstraintViolation<T>> validate(boolean showMessage, Class<?>... group) {

        Set<ConstraintViolation<T>> warningsList = super.validate(false, group);
        for (ServiceChecklistItemBean bean : serviceChecklistItemList) {
            Set<ConstraintViolation<T>> beanWarningsList = bean.validate(false, group);
            warningsList.addAll(beanWarningsList);
        }

        if (showMessage) {
            showMessage(warningsList);
        }
        return warningsList;
    }
}
