/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.data;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.swing.extended.util.Messaging;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.beans.SurveyPointBean;
import org.sola.common.messaging.GisMessage;

/**
 * External file importer that imports points from an CSV file.
 * The importer can be configured to define the column of id, x and y.
 * It can also be configured the line from where the points are found.
 * The format of the point is <id>,<x>, <y>.
 * 
 * @author Elton Manoku
 */
public class ExternalFileImporterSurveyPointBeans extends ExternalFileImporter {

    private int columnIndexForIdValue = 0;
    private int columnIndexForXValue = 1;
    private int columnIndexForYValue = 2;
    private int lineIndexToStartFrom = 1;
    
    private static ExternalFileImporterSurveyPointBeans instanceImporter =
            new ExternalFileImporterSurveyPointBeans();

    /**
     * Gets a singletone instance of the importer
     * 
     * @return 
     */
    public static ExternalFileImporterSurveyPointBeans getInstance() {
        return instanceImporter;
    }

    /**
     * Sets the index of the column that will be used for the ID of the point
     * @param columnIndexForIdValue 
     */
    public void setColumnIndexForIdValue(int columnIndexForIdValue) {
        this.columnIndexForIdValue = columnIndexForIdValue;
    }

    /**
     * Sets the index of the column that will be used for the X coordinate of the point
     * @param columnIndexForXValue 
     */
    public void setColumnIndexForXValue(int columnIndexForXValue) {
        this.columnIndexForXValue = columnIndexForXValue;
    }

    /**
     * Sets the index of the column that will be used for the Y coordinate of the point
     * @param columnIndexForYValue 
     */
    public void setColumnIndexForYValue(int columnIndexForYValue) {
        this.columnIndexForYValue = columnIndexForYValue;
    }

    /**
     * Sets the index of the line from which the lines will be considered as points
     * @param lineIndexToStartFrom 
     */
    public void setLineIndexToStartFrom(int lineIndexToStartFrom) {
        this.lineIndexToStartFrom = lineIndexToStartFrom;
    }

    @Override
    public <T extends SpatialBean> List<T> getBeans(String filePath) {
        List<T> result = new ArrayList<T>();
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));
            int lineIndex = 0;
            String[] line;
            while ((line = reader.readNext()) != null) {
                lineIndex++;
                if (lineIndex <= this.lineIndexToStartFrom) {
                    continue;
                }
                SurveyPointBean bean = new SurveyPointBean();
                bean.setId(line[this.columnIndexForIdValue]);
                bean.setX(Double.parseDouble(line[this.columnIndexForXValue]));
                bean.setY(Double.parseDouble(line[this.columnIndexForYValue]));
                result.add((T) bean);
            }
        } catch (IOException ex) {
            Messaging.getInstance().show(GisMessage.IMPORT_FILE_DOCUMENT_DOES_NOT_HAVE_ATTACHMENT);
            org.sola.common.logging.LogUtility.log(
                    GisMessage.IMPORT_FILE_DOCUMENT_DOES_NOT_HAVE_ATTACHMENT, ex);

        }
        return result;
    }
}
