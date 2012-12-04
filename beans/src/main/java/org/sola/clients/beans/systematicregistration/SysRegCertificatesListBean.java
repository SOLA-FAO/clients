/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
