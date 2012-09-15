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
package org.sola.clients.swing.desktop.source;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This panel extends {@link org.sola.clients.swing.ui.source.DocumentSearchPanel} 
 * to handle events and open appropriate forms.
 */
public class DocumentSearchPanel extends org.sola.clients.swing.ui.source.DocumentSearchPanel {
    public DocumentSearchPanel(){
        super();
        init();
    }
    
    private void init() {
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(org.sola.clients.swing.ui.source.DocumentSearchPanel.EDIT_SOURCE)) {
                    final SourceBean source = (SourceBean) evt.getNewValue();

                    SolaTask t = new SolaTask<Void, Void>() {

                        @Override
                        public Void doTask() {
                            setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_FORM_OPENING));
                            DocumentForm form = new DocumentForm(source);
                            form.addPropertyChangeListener(new PropertyChangeListener() {

                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getPropertyName().equals(DocumentForm.DOCUMENT_SAVED)) {
                                        searchDocuments();
                                    }
                                }
                            });
                            MainForm.getInstance().getMainContentPanel().addPanel(form, MainContentPanel.CARD_SOURCE, true);
                            return null;
                        }
                    };
                    TaskManager.getInstance().runTask(t);
                } else if (evt.getPropertyName().equals(org.sola.clients.swing.ui.source.DocumentSearchPanel.OPEN_APPLICATION)) {
                    MainForm.getInstance().openApplicationForm((ApplicationBean)evt.getNewValue());
                } else if (evt.getPropertyName().equals(org.sola.clients.swing.ui.source.DocumentSearchPanel.VIEW_SOURCE)) {
                    MainForm.getInstance().openDocumentViewForm((SourceBean)evt.getNewValue());
                }
            }
        });
    }
}
