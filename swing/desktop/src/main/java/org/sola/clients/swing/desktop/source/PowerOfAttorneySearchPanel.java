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
import org.sola.clients.beans.source.PowerOfAttorneyBean;
import org.sola.clients.swing.desktop.MainForm;

/**
 * This panel extends {@link org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel} 
 * to handle events and open appropriate forms.
 */
public class PowerOfAttorneySearchPanel extends org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel {
    public PowerOfAttorneySearchPanel(){
        super();
        init();
    }
    
    private void init() {
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel.OPEN_APPLICATION)) {
                    MainForm.getInstance().openApplicationForm((ApplicationBean)evt.getNewValue());
                } else if (evt.getPropertyName().equals(org.sola.clients.swing.ui.source.PowerOfAttorneySearchPanel.VIEW_POWER_OF_ATTORNEY)) {
                    MainForm.getInstance().openDocumentViewForm((PowerOfAttorneyBean)evt.getNewValue());
                }
            }
        });
    }
}
