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
package org.sola.clients.beans.source;

import java.util.Date;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.SourceTypeBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.casemanagement.SourceSummaryTO;

/** 
 * Represents summary version of the <b>source</b> object in the domain model. 
 * Could be populated from the {@link SourceSummaryTO} object.<br />
 * For more information see data dictionary <b>Source</b> schema.
 */
public class SourceSummaryBean extends AbstractTransactionedBean {

    public static final String ACCEPTANCE_PROPERTY = "acceptance";
    public static final String ARCHIVE_ID_PROPERTY = "archiveId";
    public static final String ARCHIVE_DOCUMENT_ID_PROPERTY = "archiveDocumentId";
    public static final String LA_NR_PROPERTY = "laNr";
    public static final String RECORDATION_PROPERTY = "recordation";
    public static final String REFERENCE_NR_PROPERTY = "referenceNr";
    public static final String SUBMISSION_PROPERTY = "submission";
    public static final String SOURCE_TYPE_CODE_PROPERTY = "typeCode";
    public static final String SOURCE_TYPE_PROPERTY = "sourceType";
    public static final String OWNER_NAME_PROPERTY = "ownerName";
    public static final String VERSION_PROPERTY = "version";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String TRANSACTION_ID_PROPERTY = "transactionId";
    
    private Date acceptance;
    private String archiveId;
    private String archiveDocumentId;
    private String laNr;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_RECORDATION, payload=Localized.class)
    private Date recordation;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_REFERENCENR, payload=Localized.class)
    private String referenceNr;
    private Date submission;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_SOURCETYPE, payload=Localized.class)
    private SourceTypeBean sourceType;
    private String ownerName;
    private String version;
    private String description;
    private String transactionId;
    
    public SourceSummaryBean() {
        super();
        sourceType = new SourceTypeBean();
    }

    public void clean() {
        this.setId(UUID.randomUUID().toString());
        this.setAcceptance(null);
        this.setArchiveDocumentId(null);
        this.setEntityAction(null);
        this.setLaNr(null);
        this.setRecordation(null);
        this.setReferenceNr(null);
        this.setRowId(null);
        this.setRowVersion(0);
        this.setSubmission(null);
        this.setDescription(null);
        this.setVersion(null);
        this.setOwnerName(null);
        this.setTransactionId(null);
    }

    public String getTypeCode() {
        return sourceType.getCode();
    }

    /** 
     * Sets source type code and retrieves {@link SourceTypeBean} 
     * from the cache. 
     * @param value Source type code.
     */
    public void setTypeCode(String typeCode) {
        String old = sourceType.getCode();
        setSourceType(CacheManager.getBeanByCode(
                CacheManager.getSourceTypes(), typeCode));
        propertySupport.firePropertyChange(SOURCE_TYPE_CODE_PROPERTY, old, typeCode);
    }

    public SourceTypeBean getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceTypeBean sourceType) {
        if (this.sourceType == null) {
            this.sourceType = new SourceTypeBean();
        }
        this.setJointRefDataBean(this.sourceType, sourceType, SOURCE_TYPE_PROPERTY);
    }

    public Date getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(Date value) {
        Date old = acceptance;
        acceptance = value;
        propertySupport.firePropertyChange(ACCEPTANCE_PROPERTY, old, value);
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String value) {
        String old = archiveId;
        archiveId = value;
        propertySupport.firePropertyChange(ARCHIVE_ID_PROPERTY, old, value);
    }

    public String getArchiveDocumentId() {
        return archiveDocumentId;
    }

    public void setArchiveDocumentId(String value) {
        String old = archiveDocumentId;
        archiveDocumentId = value;
        propertySupport.firePropertyChange(ARCHIVE_DOCUMENT_ID_PROPERTY, old, value);
    }

    public String getLaNr() {
        return laNr;
    }

    public void setLaNr(String value) {
        String old = laNr;
        laNr = value;
        propertySupport.firePropertyChange(LA_NR_PROPERTY, old, value);
    }

    public Date getRecordation() {
        return recordation;
    }

    public void setRecordation(Date value) {
        Date old = recordation;
        recordation = value;
        propertySupport.firePropertyChange(RECORDATION_PROPERTY, old, value);
    }

    public String getReferenceNr() {
        return referenceNr;
    }

    public void setReferenceNr(String value) {
        String old = referenceNr;
        referenceNr = value;
        propertySupport.firePropertyChange(REFERENCE_NR_PROPERTY, old, value);
    }

    public Date getSubmission() {
        return submission;
    }

    public void setSubmission(Date value) {
        Date old = submission;
        submission = value;
        propertySupport.firePropertyChange(SUBMISSION_PROPERTY, old, value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        propertySupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, this.description);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        String oldValue = this.ownerName;
        this.ownerName = ownerName;
        propertySupport.firePropertyChange(OWNER_NAME_PROPERTY, oldValue, this.ownerName);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        String oldValue = this.version;
        this.version = version;
        propertySupport.firePropertyChange(VERSION_PROPERTY, oldValue, this.version);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        String oldValue = this.transactionId;
        this.transactionId = transactionId;
        propertySupport.firePropertyChange(TRANSACTION_ID_PROPERTY, oldValue, this.transactionId);
    }
}
