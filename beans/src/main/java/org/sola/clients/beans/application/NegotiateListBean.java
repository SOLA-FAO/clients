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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.administrative.BaUnitSummaryBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.NegotiateTO;

/**
 *
 * @author soladev
 */
public class NegotiateListBean extends AbstractBindingListBean {

    public static final String SELECTED_NEGOTIATE = "selectedNegotiate";
    public static final String TOTAL_BEAN_ID = "totalId";
    private SolaList<NegotiateBean> negotiateList;
    private NegotiateBean selectedNegotiate;
    private String serviceId;

    public NegotiateListBean() {
        super();
        negotiateList = new SolaList<NegotiateBean>();
    }

    public ObservableList<NegotiateBean> getFilteredNegotiationList() {
        return negotiateList.getFilteredList();
    }

    public NegotiateBean getSelectedNegotiation() {
        return selectedNegotiate;
    }

    public void setSelectedNegotiation(NegotiateBean selected) {
        NegotiateBean oldValue = this.selectedNegotiate;
        this.selectedNegotiate = isTotalBean(selected) ? null : selected; // Don't allow the user to select the TotalBean. 
        propertySupport.firePropertyChange(SELECTED_NEGOTIATE, oldValue, this.selectedNegotiate);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void addItem(NegotiateBean item) {
        negotiateList.addAsNew(item);
    }

    public void removeItem(NegotiateBean item) {
        negotiateList.safeRemove(item, EntityAction.DELETE);
    }

    public void addUpdateTotalBean() {
        if (negotiateList.size() > 0) {
            NegotiateBean total = new NegotiateBean();
            total.setId(TOTAL_BEAN_ID);
            total.setInitialAmount(BigDecimal.ZERO);
            total.setFinalAmount(BigDecimal.ZERO);
            total.setBaUnit(new BaUnitSummaryBean());
            total.getBaUnit().setArea(BigDecimal.ZERO);
            total.getBaUnit().setTypeCode(BaUnitTypeBean.CODE_STATE_LAND);
            total.getBaUnit().setNameFirstpart("    " + MessageUtility.getLocalizedMessageText(ClientMessage.NEGOTIATIONS_TOTALS));
            ListIterator<NegotiateBean> it = negotiateList.listIterator();
            while (it.hasNext()) {
                NegotiateBean bean = it.next();
                if (!isTotalBean(bean)) {
                    total.setInitialAmount(total.getInitialAmount().add(bean.getInitialAmount() == null
                            ? BigDecimal.ZERO : bean.getInitialAmount()));
                    total.setFinalAmount(total.getFinalAmount().add(bean.getFinalAmount() == null
                            ? BigDecimal.ZERO : bean.getFinalAmount()));
                    total.getBaUnit().setArea(total.getBaUnit().getArea().add(bean.getBaUnit().getArea() == null
                            ? BigDecimal.ZERO : bean.getBaUnit().getArea()));
                }
            }
            addOrUpdateItem(total);
        }
    }

    /**
     * Retrieves the list of notify parties from the database using the service
     * id.
     *
     * @param serviceId
     */
    public void loadList(String serviceId) {
        setServiceId(serviceId);
        negotiateList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getNegotiations(serviceId),
                NegotiateBean.class, (List) negotiateList);
        addUpdateTotalBean();
    }

    /**
     * Saves the list of Notify Parties
     */
    public void saveList() {
        // Remove the TotalBean first
        ListIterator<NegotiateBean> it = negotiateList.listIterator();
        while (it.hasNext()) {
            NegotiateBean bean = it.next();
            if (isTotalBean(bean)) {
                it.remove();
                break;
            }
        }

        List<NegotiateTO> toList = new ArrayList<NegotiateTO>();
        // Translate list of beans to a list of TO's 
        TypeConverters.BeanListToTransferObjectList((List) negotiateList, toList, NegotiateTO.class);
        negotiateList.clear();
        // Translate the TO's with the saved data to Beans and replace the original bean list
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().saveNegotiations(toList),
                NegotiateBean.class, (List) negotiateList);
        addUpdateTotalBean();
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
        for (NegotiateBean bean : negotiateList) {
            Set<ConstraintViolation<T>> beanWarningsList = bean.validate(false, group);
            warningsList.addAll(beanWarningsList);
        }

        if (showMessage) {
            showMessage(warningsList);
        }
        return warningsList;
    }

    /**
     * Determines whether a negotiation should be added to the list of items or
     * replace an existing item in the list.
     *
     * @param item The item to add or update.
     */
    public void addOrUpdateItem(NegotiateBean item) {
        if (item != null && negotiateList != null) {
            if (negotiateList.contains(item)) {
                negotiateList.set(negotiateList.indexOf(item), item);
            } else {
                negotiateList.addAsNew(item);
            }
        }
    }

    /**
     * Determines if the bean is the total/summary bean or not.
     *
     * @param item
     * @return
     */
    public boolean isTotalBean(NegotiateBean item) {
        boolean result = false;
        if (item != null) {
            result = TOTAL_BEAN_ID.equals(item.getId());
        }
        return result;
    }
}
