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

import java.util.Set;
import org.reflections.Reflections;
import org.sola.clients.beans.AbstractListBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.common.logging.LogUtility;

/**
 * The list bean is used to encapsulate the list initialization and managing of the import source
 * type list.
 *
 * @author Elton Manoku
 */
public class SpatialDestinationTypeListBean extends AbstractListBean {

    public SpatialDestinationTypeListBean() {
        super();
        if (getBeanList().size()>0){
            setSelectedBean((SpatialDestinationTypeBean)getBeanList().get(0));
        }
    }

    @Override
    protected SolaObservableList initializeBeanList() {
        SolaObservableList<SpatialDestinationTypeBean> list =
                new SolaObservableList<SpatialDestinationTypeBean>();
        String namespaceToScan = SpatialDestinationTypeBean.class.getPackage().getName();
        Reflections reflections = new Reflections(namespaceToScan);
        Set<Class<? extends SpatialDestinationTypeBean>> subTypes =
                reflections.getSubTypesOf(SpatialDestinationTypeBean.class);
        try {
            for (Class<? extends SpatialDestinationTypeBean> foundClass : subTypes) {
                list.add(foundClass.newInstance());
            }
        } catch (InstantiationException ex) {
            LogUtility.log("Error initializing Application Action Panels", ex);
        } catch (IllegalAccessException ex) {
            LogUtility.log("Error initializing Application Action Panels", ex);
        }
        return list;
    }

    @Override
    public SpatialDestinationTypeBean getSelectedBean() {
        return (SpatialDestinationTypeBean)super.getSelectedBean();
    }
    
    
}
