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
package org.sola.clients.beans.referencedata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaCodeList;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.security.UserGroupBean;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Holds list of {@link SourceTypeHelperBean} objects for binding on the form.
 */
public class SourceTypeHelperListBean extends AbstractBindingListBean {

    private class SourceTypeHelperListener implements PropertyChangeListener, Serializable {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(SourceTypeHelperBean.IS_IN_LIST_PROPERTY)
                    && evt.getNewValue() != evt.getOldValue() && triggerSourceTypeUpdates) {

                if (sourceTypeCodes != null) {
                    SourceTypeHelperBean sourceTypeHelper = (SourceTypeHelperBean) evt.getSource();
                    boolean isInList = false;

                    for (RequestTypeSourceTypeBean requestTypeSourceType : sourceTypeCodes) {
                        if (requestTypeSourceType.getSourceTypeCode().equals(sourceTypeHelper.getSourceType().getCode())) {
                            isInList = true;
                            if (!sourceTypeHelper.isInList()) {
                                sourceTypeCodes.safeRemove(requestTypeSourceType, EntityAction.DELETE);
                            }
                            break;
                        }
                    }

                    if (!isInList && sourceTypeHelper.isInList()) {
                        RequestTypeSourceTypeBean requestTypeSourceType = new RequestTypeSourceTypeBean(sourceTypeHelper.getSourceType().getCode());
                        sourceTypeCodes.add(requestTypeSourceType);
                    }
                }
            }
        }
    }
    private SolaObservableList<SourceTypeHelperBean> sourceTypeHelpers;
    private SolaList<RequestTypeSourceTypeBean> sourceTypeCodes;
    private boolean triggerSourceTypeUpdates = true;
    private SourceTypeHelperListener listener;

    public SourceTypeHelperListBean() {
        super();
        sourceTypeHelpers = new SolaObservableList<SourceTypeHelperBean>();
        listener = new SourceTypeHelperListener();
        loadList();
    }

    private void loadList() {
        sourceTypeHelpers.clear();
        String[] excludedCodes = null;

        if (sourceTypeCodes != null && sourceTypeCodes.size() > 0) {
            excludedCodes = new String[sourceTypeCodes.size()];
            int i = 0;
            for (RequestTypeSourceTypeBean requestType : sourceTypeCodes) {
                excludedCodes[i] = requestType.getSourceTypeCode();
                i += 1;
            }
        }

        SolaCodeList<SourceTypeBean> sourceTypes = new SolaCodeList<SourceTypeBean>(excludedCodes);

        for (SourceTypeBean sourceType : CacheManager.getSourceTypes()) {
            sourceTypes.add(sourceType);
        }

        for (SourceTypeBean sourceType : sourceTypes) {
            SourceTypeHelperBean sourceTypeHelper = new SourceTypeHelperBean(false, sourceType);
            sourceTypeHelper.addPropertyChangeListener(listener);
            sourceTypeHelpers.add(sourceTypeHelper);
        }
    }

    /**
     * Returns list of {@link RequestTypeSourceTypeBean}, if it is set.
     */
    public SolaList<RequestTypeSourceTypeBean> getSourceTypeCodes() {
        return sourceTypeCodes;
    }

    /**
     * Sets list of {@link RequestTypeSourceTypeBean}, which is managed, based
     * on checks of the source types in the list of {@link SourceTypeHelperBean}.
     */
    public void setSourceTypeCodes(SolaList<RequestTypeSourceTypeBean> sourceTypeCodes) {
        this.sourceTypeCodes = sourceTypeCodes;
        loadList();
        setChecks(this.sourceTypeCodes);
    }

    public ObservableList<SourceTypeHelperBean> getSourceTypeHelpers() {
        return sourceTypeHelpers;
    }

    /**
     * Sets or removes checks from the groups, based on provided {@link UserGroupBean}
     * object.
     */
    public void setChecks(SolaList<RequestTypeSourceTypeBean> sourceTypeCodes) {
        triggerSourceTypeUpdates = false;
        for (SourceTypeHelperBean sourceTypeHelper : sourceTypeHelpers) {

            sourceTypeHelper.setInList(false);

            if (sourceTypeCodes != null) {
                for (RequestTypeSourceTypeBean requestTypeSourceType : sourceTypeCodes) {
                    if (sourceTypeHelper.getSourceType().getCode().equals(requestTypeSourceType.getSourceTypeCode())) {
                        sourceTypeHelper.setInList(true);
                    }
                }
            }
        }
        triggerSourceTypeUpdates = true;
    }
}
