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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.mapaction.extended.print;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.util.MapImageGenerator;
import org.geotools.swing.extended.util.ScalebarGenerator;

/**
 * A Print generator. It is used to generate a PDF of the map following a print layout.
 * 
 * @author Elton Manoku
 */
public class PrintoutGenerator {

    private final static String TEMPORARY_PRINT_FILE_LOCATION =
            System.getProperty("user.home") + File.separator + "sola";
    private final static String TEMPORARY_PRINT_FILE = "print.pdf";
    private ScalebarGenerator scalebar = new ScalebarGenerator();
    private MapImageGenerator mapImageGenerator;
    private HashMap<String, BaseFont> fonts = new HashMap<String, BaseFont>();

    /**
     * Constructor of the utility.
     * @param map The map control to use for the print
     */
    public PrintoutGenerator(Map map) {
        this.mapImageGenerator = new MapImageGenerator(map);
    }

    /**
     * Generates the printout.
     * @param layout The layout to use for the printout
     * @param scale The scale of the map to be used.
     * @return The location of the pdf file generated
     */
    public String generate(PrintLayout layout, double scale) {
        String pathToResult;
        BufferedImage mapImage = this.getMapImage(layout.getMap(), scale);
        BufferedImage scalebarImage = this.getScalebarImage(layout.getScalebar(), scale);
        Rectangle pageSize = new Rectangle(
                this.getDimesionInPoints(layout.getPageWidth()),
                this.getDimesionInPoints(layout.getPageHeight()));
        Document document = new Document(pageSize);
        try {
            File location = new File(TEMPORARY_PRINT_FILE_LOCATION);
            if (!location.exists()) {
                location.mkdirs();
            }
            pathToResult = TEMPORARY_PRINT_FILE_LOCATION + File.separator + TEMPORARY_PRINT_FILE;
            // creation of the different writers
            PdfWriter writer = PdfWriter.getInstance(document,
                    new FileOutputStream(pathToResult));
            document.open();
            this.addImage(document, mapImage,
                    layout.getMap().getX(), layout.getMap().getX(), layout.getMap().getWidth());
            this.addImage(document, scalebarImage,
                    layout.getScalebar().getX(), layout.getScalebar().getY(),
                    layout.getScalebar().getWidth());
            for (ImageLayout imageLayout : layout.getImageLayouts()) {
                URL imageUrl = this.getClass().getResource(imageLayout.getResourceLocation());
                this.addImage(document, imageUrl,
                        imageLayout.getX(), imageLayout.getY(), imageLayout.getWidth());
            }

            PdfContentByte docContent = writer.getDirectContent();
            for (TextLayout textLayout : layout.getTextLayouts()) {
                this.addText(docContent, textLayout.getX(), textLayout.getY(),
                        textLayout.getValue(), textLayout.getFontName(), textLayout.getSize());
            }

        } catch (DocumentException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            document.close();
        }
        return pathToResult;
    }

    private BufferedImage getMapImage(ImageLayout mapImageLayout, double scale) {
        BufferedImage result = null;
        if (mapImageLayout != null) {
            int mapWidth = (int) this.getDimesionInPoints(mapImageLayout.getWidth());
            int mapHeight = (int) this.getDimesionInPoints(mapImageLayout.getHeight());
            result = this.mapImageGenerator.getImage(
                    mapWidth, mapHeight, scale, this.getDpi());
        }
        return result;
    }

    private BufferedImage getScalebarImage(ImageLayout imageLayout, double scale) {
        BufferedImage result = null;
        if (imageLayout != null) {
            double width = this.getDimesionInPoints(imageLayout.getWidth());
            result = scalebar.getImage(scale, width, this.getDpi());
            imageLayout.setWidth(Math.round(Utilities.pointsToMillimeters(result.getWidth())));
        }
        return result;
    }

    private void addText(PdfContentByte docContent, int x, int y, String text,
            String fontName, int fontSize) throws DocumentException, IOException {
        // add text at an absolute position
        docContent.beginText();
        docContent.setFontAndSize(this.getFont(fontName), fontSize);
        docContent.setTextMatrix(
                this.getDimesionInPoints(x), this.getDimesionInPoints(y));
        docContent.showText(text);
        docContent.endText();
    }

    private BaseFont getFont(String fontName) throws DocumentException, IOException {
        BaseFont bFont = null;
        if (this.fonts.containsKey(fontName)) {
            bFont = this.fonts.get(fontName);
        } else {
            bFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
        }
        return bFont;
    }

    private float getDimesionInPoints(int mm) {
        return com.lowagie.text.Utilities.millimetersToPoints(mm);
    }

    private int getDpi() {
        return (int) com.lowagie.text.Utilities.inchesToPoints(1);
    }

    private void addImage(Document document, URL url, int x, int y, int width)
            throws BadElementException, IOException, DocumentException {
        Image img = Image.getInstance(url);
        this.addImage(document, img, x, y, width);
    }

    private void addImage(Document document, BufferedImage bImage, int x, int y, int width)
            throws BadElementException, IOException, DocumentException {
        Image img = Image.getInstance(bImage, null, false);
        this.addImage(document, img, x, y, width);
    }

    private void addImage(Document document, Image img, int x, int y, int width)
            throws BadElementException, IOException, DocumentException {
        img.setAbsolutePosition(this.getDimesionInPoints(x), this.getDimesionInPoints(y));
        float widthInPoints = this.getDimesionInPoints(width);
        img.scalePercent((widthInPoints / img.getWidth()) * 100);
        document.add(img);
    }
}
