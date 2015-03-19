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
import org.sola.webservices.transferobjects.casemanagement.ObjectionTO;

/**
 *
 * @author soladev
 */
public class ObjectionListBean extends AbstractBindingListBean {

    public static final String SELECTED_OBJECTION = "selectedObjection";
    private SolaList<ObjectionBean> objectionList;
    private ObjectionBean selectedObjection;
    private String serviceId;

    public ObjectionListBean() {
        super();
        objectionList = new SolaList<ObjectionBean>();
    }

    public ObservableList<ObjectionBean> getFilteredObjectionList() {
        return objectionList.getFilteredList();
    }

    public ObjectionBean getSelectedObjection() {
        return selectedObjection;
    }

    public void setSelectedObjection(ObjectionBean selected) {
        ObjectionBean oldValue = this.selectedObjection;
        this.selectedObjection = selected;
        propertySupport.firePropertyChange(SELECTED_OBJECTION, oldValue, this.selectedObjection);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void addItem(ObjectionBean item) {
        objectionList.addAsNew(item);
    }

    public void removeItem(ObjectionBean item) {
        objectionList.safeRemove(item, EntityAction.DELETE);
    }

    /**
     * Retrieves the list of objections from the database using the service id.
     *
     * @param serviceId
     */
    public void loadList(String serviceId) {
        setServiceId(serviceId);
        objectionList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getObjections(serviceId),
                ObjectionBean.class, (List) objectionList);
    }

    /**
     * Saves the list of Public Display Items
     */
    public void saveList() {
        List<ObjectionTO> toList = new ArrayList<ObjectionTO>();
        // Translate list of beans to a list of TO's 
        TypeConverters.BeanListToTransferObjectList((List) objectionList, toList, ObjectionTO.class);
        objectionList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().saveObjections(toList),
                ObjectionBean.class, (List) objectionList);
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
        for (ObjectionBean bean : objectionList) {
            Set<ConstraintViolation<T>> beanWarningsList = bean.validate(false, group);
            warningsList.addAll(beanWarningsList);
        }

        if (showMessage) {
            showMessage(warningsList);
        }
        return warningsList;
    }

    /**
     * Determines whether a objection should be added to the list of items or
     * replace an existing item in the list.
     *
     * @param item The item to add or update.
     */
    public void addOrUpdateItem(ObjectionBean item) {
        if (item != null && objectionList != null) {
            if (objectionList.contains(item)) {
                objectionList.set(objectionList.indexOf(item), item);
            } else {
                objectionList.addAsNew(item);
            }
        }
    }
}
