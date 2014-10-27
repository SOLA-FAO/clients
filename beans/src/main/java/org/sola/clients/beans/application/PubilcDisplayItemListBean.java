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
public class PubilcDisplayItemListBean extends AbstractBindingListBean {

    public static final String SELECTED_PUBLIC_DISPLAY_ITEM = "selectedPublicDisplayItem";
    private SolaList<PublicDisplayItemBean> publicDisplayItemList;
    private PublicDisplayItemBean selectedPublicDisplayItem;
    private String serviceId;

    public PubilcDisplayItemListBean() {
        super();
        publicDisplayItemList = new SolaList<PublicDisplayItemBean>();
    }

    public ObservableList<PublicDisplayItemBean> getPublicDisplayItemList() {
        return publicDisplayItemList.getFilteredList();
    }

    public PublicDisplayItemBean getSelectedPublicDisplayItem() {
        return selectedPublicDisplayItem;
    }

    public void setSelectedPublicDisplayItem(PublicDisplayItemBean selectedItem) {
        PublicDisplayItemBean oldValue = this.selectedPublicDisplayItem;
        this.selectedPublicDisplayItem = selectedItem;
        propertySupport.firePropertyChange(SELECTED_PUBLIC_DISPLAY_ITEM, oldValue, this.selectedPublicDisplayItem);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void addItem(PublicDisplayItemBean item) {
        publicDisplayItemList.addAsNew(item);
    }

    public void removeItem(PublicDisplayItemBean item) {
        publicDisplayItemList.safeRemove(item, EntityAction.DELETE);
    }

    /**
     * Retrieves the list of public display items from the database using the
     * service id.
     *
     * @param serviceId
     */
    public void loadList(String serviceId) {
        setServiceId(serviceId);
        publicDisplayItemList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getPublicDisplayItems(serviceId),
                PublicDisplayItemBean.class, (List) publicDisplayItemList);
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
        for (PublicDisplayItemBean bean : publicDisplayItemList) {
            Set<ConstraintViolation<T>> beanWarningsList = bean.validate(false, group);
            warningsList.addAll(beanWarningsList);
        }

        if (showMessage) {
            showMessage(warningsList);
        }
        return warningsList;
    }
}
