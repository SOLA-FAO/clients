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
