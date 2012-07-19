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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.mapaction.extended;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.MapScaleException;
import org.geotools.swing.extended.exception.PrintLayoutException;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.geotools.swing.mapaction.extended.print.PrintoutGenerator;
import org.geotools.swing.mapaction.extended.print.TextLayout;
import org.geotools.swing.mapaction.extended.ui.IPrintUi;
import org.geotools.swing.mapaction.extended.ui.PrintForm;

/**
 * Map action used to start up a the printing process of the map. A form starts up where the user
 * can chose a layout and a scale to print in. <br/> This class can be extended to modify the
 * loading of layouts, or calling another print generator.
 *
 *
 * @author Elton Manoku
 */
public class Print extends ExtendedAction {

    private IPrintUi printForm;
    protected String layoutLocation = "resources/print/layouts.properties";

    public Print(Map mapControl) {
        super(mapControl, "print",
                Messaging.getInstance().getMessageText(Messaging.Ids.PRINT.toString()),
                "resources/print.png");
    }

    /**
     * It starts up the printing process. Then after it collects the parameters to print, it uses
     * the print generator to generate a pdf according to a predefined layout.
     */
    @Override
    public void onClick() {
        if (this.printForm == null) {
            this.printForm = this.getPrintForm();
        }
        try {
            this.printForm.setPrintLayoutList(this.getPrintLayouts());
        } catch (PrintLayoutException ex) {
            Messaging.getInstance().show(Messaging.Ids.PRINT_LAYOUT_GENERATION_ERROR.toString());
        }

        try {
            this.printForm.setScale(this.getMapControl().getScale().intValue());
            this.printForm.setVisibility(true);
            if (this.printForm.getPrintLayout() == null) {
                return;
            }


            String printLocation = this.print(
                    this.printForm.getPrintLayout(),
                    this.printForm.getScale(),
                    this.printForm.getExtraFields());
            this.showPrintableDocument(printLocation);

        } catch (MapScaleException ex) {
            Messaging.getInstance().show(Messaging.Ids.PRINT_LAYOUT_GENERATION_ERROR.toString());
        }
    }

    /**
     * Gets the form that is open when the map action is clicked. To get another interface, override
     * this form.
     *
     * @return
     */
    protected IPrintUi getPrintForm() {
        return new PrintForm();
    }

    /**
     * Gets the list of available print layouts. The print layouts are defined in
     * resources/print/layouts.properties. <br/> If another source of layout has to be defined, this
     * method has to be overridden.
     *
     * @return
     */
    protected List<PrintLayout> getPrintLayouts() throws PrintLayoutException {
        Properties propertyLayouts = new Properties();
        String resourceLocation = String.format("/%s/%s",
                this.getClass().getPackage().getName().replace('.', '/'),
                this.layoutLocation);
        List<PrintLayout> layoutList = new ArrayList<PrintLayout>();

        try {
            propertyLayouts.load(this.getClass().getResourceAsStream(resourceLocation));
            for (String layoutId : propertyLayouts.stringPropertyNames()) {
                PrintLayout layout = new PrintLayout(
                        layoutId, propertyLayouts.getProperty(layoutId));
                layoutList.add(layout);
            }
        } catch (IOException ex) {
            //Not important to catch
        }
        return layoutList;
    }

    /**
     * It generates the pdf and gives back the path of the pdf file.
     *
     * @param layout The layout
     * @param scale The scale
     * @return The path where generated PDF is found
     */
    protected String print(
            PrintLayout layout, double scale, java.util.Map<String, Object> extraFields) {
        if (extraFields == null) {
            extraFields = new HashMap<String, Object>();
        }
        for (TextLayout textLayout : layout.getTextLayouts()) {
            if (extraFields.containsKey(textLayout.getValue())) {
                textLayout.setValue(extraFields.get(textLayout.getValue()).toString());
            }
        }

        PrintoutGenerator printoutGenerator = new PrintoutGenerator(this.getMapControl());
        return printoutGenerator.generate(layout, scale);
    }

    /**
     * Used to show a pdf file.
     *
     * @param location The location file
     */
    protected void showPrintableDocument(String location) {
        boolean fileOpened = false;
        if (Desktop.isDesktopSupported()) {
            Desktop dt = Desktop.getDesktop();
            if (dt.isSupported(Desktop.Action.OPEN)) {
                try {
                    dt.open(new File(location));
                    fileOpened = true;
                } catch (Exception ex) {
                    // The file could not be opened. The most likely cause is there is no editor
                    // installed for the file extension, but it may be due to file security 
                    // restrictions. Either way, inform the user they should open the file manually. 
                    fileOpened = false;
                }
            }
        }
        if (!fileOpened) {
            // The Java Desktop is not supported on this platform. Raise a mesage to 
            // tell the user they must manually open the document. 
            Messaging.getInstance().show(Messaging.Ids.FAILED_OPEN_FILE.toString(), location);
        }
    }
}
