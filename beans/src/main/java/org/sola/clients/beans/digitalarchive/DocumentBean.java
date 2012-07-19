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
package org.sola.clients.beans.digitalarchive;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.common.FileUtility;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.digitalarchive.DocumentBinaryTO;
import org.sola.webservices.transferobjects.digitalarchive.DocumentTO;

/**
 * Represents digital archive document, excluding binary content. Could be populated from the {@link DocumentTO}
 * object.<br /> For more information see data dictionary <b>d=Document</b> schema.
 */
public class DocumentBean extends AbstractIdBean {

    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String EXTENSION_PROPERTY = "extension";
    public static final String NR_PROPERTY = "nr";
    public static final String NAME_PROPERTY = "name";
    private String description;
    private String extension;
    private String nr;

    public DocumentBean() {
        super();
        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(AbstractVersionedBean.ENTITY_ACTION_PROPERTY)) {
                    fireNameChange((EntityAction) evt.getNewValue());
                }
            }
        });
    }

    public void fireNameChange(EntityAction action) {
        if (action != EntityAction.DISASSOCIATE && action != EntityAction.DELETE) {
            propertySupport.firePropertyChange(NAME_PROPERTY, null, description);
        } else {
            propertySupport.firePropertyChange(NAME_PROPERTY, description, null);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        String old = description;
        description = value;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, old, value);
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String value) {
        String old = extension;
        extension = value;
        propertySupport.firePropertyChange(EXTENSION_PROPERTY, old, value);
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String value) {
        String old = nr;
        nr = value;
        propertySupport.firePropertyChange(NR_PROPERTY, old, value);
    }

    /**
     * Returns description property only if document is not deleted or disassociated
     */
    public String getName() {
        if (this.getEntityAction() != EntityAction.DISASSOCIATE
                && this.getEntityAction() != EntityAction.DELETE) {
            return description;
        } else {
            return null;
        }
    }

    /**
     * Returns a versioned file name for the document.
     *
     * @see #getFileName(java.lang.String, int, java.lang.String)
     */
    public String getFileName() {
        return FileUtility.generateFileName(getNr(), getRowVersion(), getExtension());
    }

    /**
     * Creates digital archive document from the file in the remote folder.
     *
     * @param fileName The name of the file in the remote folder.
     */
    public static DocumentBean createDocumentFromServerFile(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            DocumentTO document = new DocumentTO();
            document.setDescription("");
            document = WSManager.getInstance().getDigitalArchive().createDocumentFromServer(document, fileName);
            DocumentBean documentBean = TypeConverters.TransferObjectToBean(document,
                    DocumentBean.class, null);
            return documentBean;
        }
        return null;
    }

    /**
     * Creates digital archive document from the file in the local folder.
     *
     * @param file File object in the local folder.
     */
    public static DocumentBean createDocumentFromLocalFile(File file) {
        if (file != null && file.getName().contains(".")) {
            DocumentBinaryTO documentBinary = new DocumentBinaryTO();
            documentBinary.setDescription(file.getName());
            documentBinary.setFileName(file.getAbsolutePath());
//            documentBinary.setBody(FileUtility.getFileBinary(file.getAbsolutePath()));
            documentBinary.setExtension(FileUtility.getFileExtension(file.getName()));
            DocumentTO document = WSManager.getInstance().getDigitalArchive().createDocument(documentBinary);
            DocumentBean documentBean = TypeConverters.TransferObjectToBean(document,
                    DocumentBean.class, null);
            return documentBean;
        }
        return null;
    }

    /**
     * Opens document from the digital archive. This method will not check the local documents cache
     * before retrieving the document.
     *
     * @param Id The ID of the document to open.
     * @see #openDocument(java.lang.String, java.lang.String)
     */
    public static void openDocument(String Id) {
        if (Id != null) {
            DocumentBinaryTO documentBinary = WSManager.getInstance().getDigitalArchive().getDocument(Id);
            if (documentBinary != null) {
                FileUtility.openFile(documentBinary.getFileName());
            } else {
                throw new SOLAException(ClientMessage.SOURCE_NO_DOCUMENT);
            }
        }
    }

    /**
     * Checks if a document exists in the local documents cache and if so, opens the document from
     * the cache, otherwise loads the document from the DigitalArchive service.
     *
     * @param Id The id of the document to open
     * @param fileName The name of the file. The file name should be consistent with
     * {@linkplain #getFileName(java.lang.String, int, java.lang.String) getFileName}
     * @see #openDocument(java.lang.String)
     */
    public static void openDocument(String Id, String fileName) {
        if (fileName == null || !FileUtility.isCached(fileName)) {
            openDocument(Id);
        } else {
            FileUtility.openFile(fileName);
        }
    }
}
