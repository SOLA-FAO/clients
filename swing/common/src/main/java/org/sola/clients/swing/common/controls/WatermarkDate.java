/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
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
        String dateTemplate = ((SimpleDateFormat) formatter).toLocalizedPattern();
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

