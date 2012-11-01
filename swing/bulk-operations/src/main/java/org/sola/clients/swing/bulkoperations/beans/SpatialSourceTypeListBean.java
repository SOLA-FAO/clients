/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractListBean;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 * The list bean is used to encapsulate the list initialization and managing of the import source
 * type list.
 *
 * @author Elton Manoku
 */
public class SpatialSourceTypeListBean extends AbstractListBean {

    private static java.util.ResourceBundle bundleForTypes =
            java.util.ResourceBundle.getBundle(
            "org/sola/clients/swing/bulkoperations/spatialobjects/source_types");

    public SpatialSourceTypeListBean() {
        super();
        if (getBeanList().size()>0){
            setSelectedBean((SpatialSourceTypeBean)getBeanList().get(0));
        }
    }

    @Override
    protected SolaObservableList initializeBeanList() {
        SolaObservableList<SpatialSourceTypeBean> list =
                new SolaObservableList<SpatialSourceTypeBean>();
        for (String code : bundleForTypes.keySet()) {
            SpatialSourceTypeBean bean = new SpatialSourceTypeBean();
            bean.setCode(code);
            bean.setDisplayValue(bundleForTypes.getString(code));
            list.add(bean);
        }
        return list;
    }

    @Override
    public SpatialSourceTypeBean getSelectedBean() {
        return (SpatialSourceTypeBean)super.getSelectedBean();
    }
    
    
}
