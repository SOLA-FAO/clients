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
package org.sola.clients.swing.common.controls;

import com.toedter.calendar.JCalendar;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFormattedTextField;

/**
 * Dialog form to display pop-up calendar
 */
public class CalendarForm extends javax.swing.JDialog {

    private JCalendar calendarPanel;
    private JFormattedTextField dateField;

    /**
     * Creates new instance of CalendarForm.
     *
     * @param dateField Instance of {@link JFormattedTextField} to bind the
     * calendar to.
     */
    public CalendarForm(java.awt.Frame parent, boolean modal, JFormattedTextField dateField) {
        super(parent, modal);
        this.dateField = dateField;
        initForm();
    }

    private void initForm() {
        this.getContentPane().setLayout(new BorderLayout());
        calendarPanel = new JCalendar();
        calendarPanel.getDayChooser().setAlwaysFireDayProperty(false);

        calendarPanel.getDayChooser().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("day")) {
                    calendarHandler();
                }
            }
        });
        
        this.getContentPane().add(calendarPanel, BorderLayout.CENTER);
        this.pack();
        setPositionAndDate();
        calendarPanel.getDayChooser().setAlwaysFireDayProperty(true);
    }

    private void setPositionAndDate() {
        if (dateField != null) {
            this.setLocation(dateField.getLocationOnScreen().x,
                    dateField.getLocationOnScreen().y + dateField.getHeight() + 2);
            if (dateField.getValue() != null) {
                calendarPanel.setDate((Date) dateField.getValue());
            } else {
                calendarPanel.setDate(Calendar.getInstance().getTime());
            }
        }
    }

    private void calendarHandler() {
        if (dateField != null) {
            dateField.setValue(calendarPanel.getDate());
        }
        this.dispose();
    }

    public JFormattedTextField getDateField() {
        return dateField;
    }

    public void setDateField(JFormattedTextField dateField) {
        this.dateField = dateField;
        setPositionAndDate();
    }
}

