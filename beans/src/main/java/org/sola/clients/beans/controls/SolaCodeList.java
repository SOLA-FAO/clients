/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.beans.controls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Extends {@link ExtendedList} to implement filtering of code beans, inherited
 * from {@link AbstractCodeBean}. Filters by status,
 * {@link EntityAction} and
 * <code>code</code> properties.
 */
public class SolaCodeList<E extends AbstractCodeBean> extends ExtendedList<E> {

    /**
     * Implementation of {@link ExtendedListFilter} to filter elements by status
     * field and {@link EntityAction}.
     */
    private class CodeBeanFilter implements ExtendedListFilter, Serializable {

        public CodeBeanFilter() {
        }

        @Override
        public boolean isAllowedByFilter(Object element) {
            if (element == null && showAll) {
                return true;
            }

            AbstractCodeBean codeBean;

            if (AbstractBindingBean.class.isAssignableFrom(element.getClass())) {
                codeBean = (AbstractCodeBean) element;
            } else {
                return true;
            }
            
            // Don't filter dummy items
            if (codeBean.getCode() == null && codeBean.getEntityAction() == EntityAction.DISASSOCIATE) {
                return true;
            }
            
            // Checks excluded codes
            if (excludedCodes != null) {
                for (String code : excludedCodes) {
                    if (code != null && code.equalsIgnoreCase(codeBean.getCode())) {
                        return true;
                    }
                }
            }
            
            // Check state.
            if (codeBean.getEntityAction() == EntityAction.DISASSOCIATE
                    || codeBean.getEntityAction() == EntityAction.DELETE) {
                return false;
            }

            // Check status
            if (codeBean.getStatus() != null && !codeBean.getStatus().equalsIgnoreCase("c")) {
                return false;
            }

            return true;
        }
    }
    private boolean showAll = false;
    private String[] excludedCodes;

    /**
     * Default class constructor.
     */
    public SolaCodeList() {
        this(false);
    }
    
    /**
     * Class constructor.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public SolaCodeList(String ... excludedCodes) {
        this(new ArrayList<E>(), false, excludedCodes);
    }

    /**
     * Class constructor.
     * @param showAll If true, all codes will be shown, without filtering.
     */
    public SolaCodeList(boolean showAll) {
        this(new ArrayList<E>(), showAll);
    }

    /**
     * Class constructor with initial list.
     * @param list Initial unfiltered list
     */
    public SolaCodeList(List<E> list) {
        this(list, false);
    }

    /**
     * Class constructor with initial list.
     * @param list Initial unfiltered list
     * @param showAll If true, all codes will be shown, without filtering.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public SolaCodeList(List<E> list, boolean showAll, String ... excludedCodes) {
        super(list);
        this.showAll = showAll;
        this.excludedCodes = excludedCodes;
        CodeBeanFilter filter = new CodeBeanFilter();
        setFilter(filter);
    }

    /** Returns read-only collection of excluded code. Codes, which are skipped while filtering. */
    public String[] getExcludedCodes() {
        if(excludedCodes!=null){
            return excludedCodes.clone();
        }else{
            return null;
        }
    }

    /** Sets codes, which should be skipped while filtering. */
    public void setExcludedCodes(String ... excludedCodes) {
        this.excludedCodes = excludedCodes;
        super.filter();
    }

    /** Returns true if no filtering required. Default is false.*/
    public boolean isShowAll() {
        return showAll;
    }

    /** If set to true, all codes will be shown, without filtering. */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
        super.filter();
    }
    
}
