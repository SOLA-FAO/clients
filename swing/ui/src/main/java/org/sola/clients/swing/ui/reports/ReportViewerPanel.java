/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.ui.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.view.save.JRCsvSaveContributor;
import net.sf.jasperreports.view.save.JRDocxSaveContributor;
import net.sf.jasperreports.view.save.JRHtmlSaveContributor;
import net.sf.jasperreports.view.save.JROdtSaveContributor;
import net.sf.jasperreports.view.save.JRPdfSaveContributor;
import net.sf.jasperreports.view.save.JRRtfSaveContributor;
import net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor;
import net.sf.jasperreports.view.save.JRXmlSaveContributor;
import static org.sola.clients.swing.ui.reports.SaveFormat.Docx;
import static org.sola.clients.swing.ui.reports.SaveFormat.Odt;
import static org.sola.clients.swing.ui.reports.SaveFormat.Pdf;
import static org.sola.clients.swing.ui.reports.SaveFormat.Xls;

/**
 * Extends the JasperViewer panel to allow customization of the JRViewer
 * component (viewer field)
 *
 * @author soladev
 */
public class ReportViewerPanel extends JasperViewer {

    private JasperViewer jasperViewer;

    public ReportViewerPanel() {
        this(null);
    }

    public ReportViewerPanel(JasperPrint jasperPrint) {
        super(jasperPrint);

        // Set the save contributors to make available from the report with 
        // PDF the default selection. 
        setSaveFormats(SaveFormat.Pdf, SaveFormat.Csv, 
                SaveFormat.Docx, SaveFormat.Odt, SaveFormat.Rtf, SaveFormat.Xls);
    }

    /**
     * Sets the output formats that can be used when saving the report to disk.
     * @param formats 
     */
    public final void setSaveFormats(SaveFormat... formats) {
        if (formats != null) {
            List<JRSaveContributor> saveContributors = new ArrayList<JRSaveContributor>();
            for (SaveFormat format : formats) {
                switch (format) {
                    case Pdf:
                        saveContributors.add(new JRPdfSaveContributor(Locale.getDefault(), null));
                        break;
                    case Docx:
                        saveContributors.add(new JRDocxSaveContributor(Locale.getDefault(), null));
                        break;
                    case Odt:
                        saveContributors.add(new JROdtSaveContributor(Locale.getDefault(), null));
                        break;
                    case Xls:
                        saveContributors.add(new JRSingleSheetXlsSaveContributor(Locale.getDefault(), null));
                        break;
                    case Csv:
                        saveContributors.add(new JRCsvSaveContributor(Locale.getDefault(), null));
                        break;
                    case Html:
                        saveContributors.add(new JRHtmlSaveContributor(Locale.getDefault(), null));
                        break;
                    case Xml:
                        saveContributors.add(new JRXmlSaveContributor(Locale.getDefault(), null));
                        break;
                    case Rtf:
                        saveContributors.add(new JRRtfSaveContributor(Locale.getDefault(), null));
                        break;
                    default:
                        break;
                }
            }
            this.viewer.setSaveContributors(saveContributors.toArray(
                    new JRSaveContributor[saveContributors.size()]));
        }
    }
}
