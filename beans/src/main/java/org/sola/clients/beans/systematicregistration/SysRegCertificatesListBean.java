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
package org.sola.clients.beans.systematicregistration;
import java.util.List;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.SysRegCertificatesTO;

/**
 *
 * @author RizzoM
 */
public class SysRegCertificatesListBean extends AbstractBindingBean {

    public static final String SELECTED_CERTIFICATE_PROPERTY = "selectedParcels";
    private SolaObservableList<SysRegCertificatesBean> certificates;
    private SysRegCertificatesBean selectedCertificates;

    public SysRegCertificatesListBean() {
        super();
        certificates = new SolaObservableList<SysRegCertificatesBean>();
    }

    public SolaObservableList<SysRegCertificatesBean> getSysRegCertificates() {
        return certificates;
    }

    public SysRegCertificatesBean getSelectedParcelCertificates() {
        return selectedCertificates;
    }

    public void setSelectedCertificates(SysRegCertificatesBean selectedCertificates) {
        SysRegCertificatesBean oldValue = this.selectedCertificates;
        this.selectedCertificates = selectedCertificates;
        propertySupport.firePropertyChange(SELECTED_CERTIFICATE_PROPERTY, oldValue, this.selectedCertificates);
    }

    /**
     * Returns collection of {@link ParcelNumberListingListBean} objects. This
     * method is used by Jasper report designer to extract properties of
     * application bean to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        SysRegCertificatesListBean bean = new SysRegCertificatesListBean();
        collection.add(bean);
        return collection;
    }

    //      /** Passes from date and to date search criteria. */
    public void passParameter(String params) {

        List<SysRegCertificatesTO> sysRegCertificatesTO =
                WSManager.getInstance().getCaseManagementService().getSysRegCertificatesByLocation(params);

        TypeConverters.TransferObjectListToBeanList(sysRegCertificatesTO,
                SysRegCertificatesBean.class, (List) certificates);
    }
    
     //      /** Passes from date and to date search criteria. */
    public void passParameterApp(String params, String nr) {

        List<SysRegCertificatesTO> sysRegCertificatesTO =
                WSManager.getInstance().getCaseManagementService().getSysRegCertificatesByApplication(params, nr);

        TypeConverters.TransferObjectListToBeanList(sysRegCertificatesTO,
                SysRegCertificatesBean.class, (List) certificates);
    }
}
