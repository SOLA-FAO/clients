/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.digitalarchive;

import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;

/** 
 * Holds the list of {@link FileInfoBean} objects, representing the list of 
 * scanned images in the remote folder. 
 */
public class FileInfoListBean extends AbstractBindingBean {
    
    /** Property name for the selected file in the remote folder files list. */
    public static final String SELECTED_FILE_INFO_BEAN_PROPERTY = "selectedFileInfoBean";
    
    private SolaObservableList<FileInfoBean> lstFileInfo;
    private FileInfoBean selectedFileInfoBean;
    
    /** 
     * Creates object's instance and initializes {@link ObservableList}
     * &lt;{@link FileInfoBean}&gt; object.
     */
    public FileInfoListBean() {
        lstFileInfo = new SolaObservableList<FileInfoBean>();
    }
    
    /** 
     * Populates {@link ObservableList}&lt;{@link FileInfoBean}&gt; with data 
     * reflecting remote folder content.
     */
    public void loadServerFileInfoList(){
        lstFileInfo.clear();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getDigitalArchive().getAllFiles(), 
                FileInfoBean.class, (List)lstFileInfo);
    }

    public ObservableList<FileInfoBean> getFileInfoList() {
        return lstFileInfo;
    }

    public FileInfoBean getSelectedFileInfoBean() {
        return selectedFileInfoBean;
    }

    public void setSelectedFileInfoBean(FileInfoBean value) {
        selectedFileInfoBean = value;
        propertySupport.firePropertyChange(SELECTED_FILE_INFO_BEAN_PROPERTY, null, value);
    }
    
}
