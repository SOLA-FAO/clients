/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sola.clients.swing.common.utils.FormattersFactory;

/** Shows date template as a watermark for formatted text field. */
public class WatermarkDate extends WatermarkFormattedTextField {

    public WatermarkDate() {
        super();
        setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        setDateWatermarkText();
    }

    private String getDateSeparator() {
        String dateString = DateFormat.getInstance().format(new java.util.Date());
        Matcher matcher = Pattern.compile("[^\\w]").matcher(dateString);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(0);
    }

    private void setDateWatermarkText() {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        String dateTemplate = ((SimpleDateFormat) formatter).toPattern();
        dateTemplate = dateTemplate.toUpperCase();

        // Normalize date template
        String dateSeparator = getDateSeparator();
        String[] dateParts = dateTemplate.split(dateSeparator);
        for (int i = 0; i < dateParts.length; i++) {
            if (dateParts[i].length() == 1) {
                dateParts[i] = dateParts[i] + dateParts[i];
            }
        }

        // Assemble date template
        if (dateParts.length > 1) {
            dateTemplate = "";
            for (int i = 0; i < dateParts.length; i++) {
                dateTemplate = dateTemplate + dateParts[i];
                if(i < dateParts.length - 1){
                    dateTemplate = dateTemplate + dateSeparator;
                }
            }
        }
        
        super.setWatermarkText(dateTemplate);
    }

    @Override
    public void setWatermarkText(String watermakrText) {
        // Do nothing
    }
}

