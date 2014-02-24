/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.controls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Extends {@link ExtendedList} to implement filtering of binding beans, 
 * inherited from {@link AbstractBindingBean}, by status and 
 * {@link EntityAction} properties.
 */
public class SolaList<E extends AbstractBindingBean> extends ExtendedList<E> {

    /**
     * Implementation of {@link ExtendedListFilter} to filter elements by status 
     * field and {@link EntityAction}.
     */
    private class StatusAndActionFilter implements ExtendedListFilter, Serializable {

        public StatusAndActionFilter() {
        }

        @Override
        public boolean isAllowedByFilter(Object element) {
            if (element == null) {
                return true;
            }

            AbstractBindingBean bindingBean;

            if (AbstractBindingBean.class.isAssignableFrom(element.getClass())) {
                bindingBean = (AbstractBindingBean) element;
            } else {
                return true;
            }

            if (bindingBean.getEntityAction() == EntityAction.DISASSOCIATE
                    || bindingBean.getEntityAction() == EntityAction.DELETE) {
                return false;
            }

            if (excludedStatuses != null && excludedStatuses.length > 0) {
                for (String status : excludedStatuses) {
                    if (status != null && status.length() > 0) {
                        if (AbstractTransactionedBean.class.isAssignableFrom(bindingBean.getClass())) {
                            AbstractTransactionedBean bean = (AbstractTransactionedBean) bindingBean;
                            if (bean.getStatusCode() != null &&
                                    bean.getStatusCode().equalsIgnoreCase(status)) {
                                return false;
                            }
                        } else if (AbstractCodeBean.class.isAssignableFrom(bindingBean.getClass())) {
                            AbstractCodeBean bean = (AbstractCodeBean) bindingBean;
                            if (bean.getStatus() != null &&
                                    bean.getStatus().equalsIgnoreCase(status)) {
                                return false;
                            }
                        }
                    }
                }
            }

            if (includedStatuses != null && includedStatuses.length > 0) {
                for (String status : includedStatuses) {
                    if (AbstractTransactionedBean.class.isAssignableFrom(bindingBean.getClass())) {
                        AbstractTransactionedBean bean = (AbstractTransactionedBean) bindingBean;
                        if ((bean.getStatusCode() != null && bean.getStatusCode().equalsIgnoreCase(status))
                                || (status == null && bean.getStatus() == null)) {
                            return true;
                        }
                    } else if (AbstractCodeBean.class.isAssignableFrom(bindingBean.getClass())) {
                        AbstractCodeBean bean = (AbstractCodeBean) bindingBean;
                        if (status != null && bean.getStatus().equals(status)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            return true;
        }
    }
    private String[] excludedStatuses;
    private String[] includedStatuses;

    /** Default class constructor. */
    public SolaList() {
        this(new ArrayList<E>(), null, null);
    }

    /**
     * Class constructor.
     * @param list Initial unfiltered list
     */
    public SolaList(List<E> list) {
        this(list, null, null);
    }

    /** 
     * Class constructor with excluded and included statuses 
     * @param excludedStatuses Array of statuses used to filter out parent list
     * @param includedStatuses Array of statuses used to include items from parent list
     */
    public SolaList(String[] excludedStatuses, String[] includedStatuses) {
        this(new ArrayList<E>(), excludedStatuses, includedStatuses);
    }

    /** 
     * Class constructor with initial list, excluded and included statuses 
     * @param list Initial unfiltered list
     * @param excludedStatuses Array of statuses used to filter out parent list
     * @param includedStatuses Array of statuses used to include items from parent list
     */
    public SolaList(List<E> list, String[] excludedStatuses, String[] includedStatuses) { //NOSONAR
        super(list);
        StatusAndActionFilter filter = new StatusAndActionFilter();
        this.includedStatuses = includedStatuses; //NOSONAR
        this.excludedStatuses = excludedStatuses; //NOSONAR
        setFilter(filter);
    }

    /** Returns a copy of excluded statuses array. */
    public String[] getExcludedStatuses() {
        return excludedStatuses.clone();
    }

    /** Sets excluded statuses array. */
    public void setExcludedStatuses(String[] excludedStatuses) { //NOSONAR
        this.excludedStatuses = excludedStatuses; //NOSONAR
        super.filter();
    }

    /** Returns a copy of included statuses array. */
    public String[] getIncludedStatuses() {
        return includedStatuses.clone();
    }

    /** Sets included statuses array. */
    public void setIncludedStatuses(String[] includedStatuses) { //NOSONAR
        this.includedStatuses = includedStatuses; //NOSONAR
        super.filter();
    }

    /** 
     * Safe way to remove element from the list. Removes from the initial list 
     * only if element is new or newly added into the list. Sets {@link EntityAction} 
     * and checks element against the filter to remove or add into filtered list.
     * @param element Element to remove.
     * @param action Type of {@link EntityAction} to set on the element.
     * @see ExtendedList#addAsNew(java.lang.Object) 
     */
    public boolean safeRemove(E element, EntityAction action) {
        int index = getRealIndex(element);
        boolean result = false;
        
        if (index > -1) {
            element.setEntityAction(action);
            
            if (isNewlyAdded(element)) {
                remove(index);
                result = true;
            } else if(AbstractVersionedBean.class.isAssignableFrom(element.getClass())){
                if(((AbstractVersionedBean) element).isNew()){
                    remove(index);
                    result = true;
                }
            } 
            
            if(!result){
                filterElement(index);
            }
        }
        return result;
    }
}
