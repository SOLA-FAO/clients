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
package org.sola.clients.beans.referencedata;

import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaCodeList;
import org.sola.common.StringUtility;

/**
 * Holds list of {@link RrrSubTypeBean} objects.
 */
public class RrrSubTypeListBean extends AbstractBindingListBean {

    public static final String SELECTED_RRR_SUB_TYPE_PROPERTY = "selectedRrrSubType";
    private SolaCodeList<RrrSubTypeBean> rrrSubTypes;
    private RrrSubTypeBean selectedRrrSubType;
    private boolean createDummy = false;
    private boolean listFiltered = false;

    public RrrSubTypeListBean() {
        this(false);
    }

    /**
     * Creates object instance.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public RrrSubTypeListBean(boolean createDummy) {
        this(createDummy, (String) null);
    }

    /**
     * Creates object instance.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public RrrSubTypeListBean(boolean createDummy, String... excludedCodes) {
        super();
        this.createDummy = createDummy;
        rrrSubTypes = new SolaCodeList<RrrSubTypeBean>(excludedCodes);
        loadList(createDummy);
    }

    /**
     * Loads list of {@link RrrSubTypeBean}.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public final void loadList(boolean createDummy) {
        loadCodeList(RrrSubTypeBean.class, rrrSubTypes,
                CacheManager.getRrrSubTypes(), createDummy);
    }

    public ObservableList<RrrSubTypeBean> getRrrSubTypes() {
        return rrrSubTypes.getFilteredList();
    }

    public void setExcludedCodes(String... codes) {
        rrrSubTypes.setExcludedCodes(codes);
    }

    public RrrSubTypeBean getSelectedRrrSubType() {
        return selectedRrrSubType;
    }

    public void setSelectedRrrSubType(RrrSubTypeBean selectedRrrSubType) {
        RrrSubTypeBean oldValue = this.selectedRrrSubType;
        this.selectedRrrSubType = selectedRrrSubType;
        propertySupport.firePropertyChange(SELECTED_RRR_SUB_TYPE_PROPERTY, oldValue, this.selectedRrrSubType);
    }

    /**
     * Sets the codes that can be displayed to the user
     *
     * @param codes
     */
    public void setAllowedCodes(String... codes) {
        rrrSubTypes.setAllowedCodes(codes);
    }

    /**
     * Filters the list of RrrSubTypes so that only those applicable to the
     * RrrType are displayed. Can also optionally include the currentRrrSubType
     * to ensure that is displayed regardless of whether it is still current or
     * not.
     *
     * @param rrrTypeCode
     * @param defaultRrrSubType
     */
    public void setRrrTypeFilter(String rrrTypeCode, String defaultRrrSubType) {
        if (StringUtility.isEmpty(rrrTypeCode)) {
            clearAllCodes();
            return;
        }
        List<String> codes = new ArrayList<String>();
        if (!StringUtility.isEmpty(defaultRrrSubType)) {
            codes.add(defaultRrrSubType);
        }
        for (RrrSubTypeBean bean : rrrSubTypes) {
            if (rrrTypeCode.equals(bean.getRrrTypeCode())) {
                codes.add(bean.getCode());
            }
        }
        setAllowedCodes(codes.toArray(new String[codes.size()]));;
    }

    /**
     * Indicates if there are any RrrSubTypes for display for this Rrr Type.
     * Requires setRrrTypeFilter to be called prior to calling this method.
     */
    public boolean hasRrrSubTypes() {
        // Determine if the list should contain a dummy entry to not
        return this.createDummy ? getRrrSubTypes().size() > 1
                : getRrrSubTypes().size() > 0;
    }

    public void clearAllCodes() {
        rrrSubTypes.setAllowedCodes("");
    }

}
