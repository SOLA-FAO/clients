/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.data;

import java.util.ArrayList;
import java.util.List;
import org.sola.clients.swing.gis.beans.SpatialBean;

/**
 * This is an abstract class that encapsulates common functionality for file importers.
 * The file importers are utilities that parse external files and extract beans used in the 
 * GIS Component.
 * 
 * @author Elton Manoku
 */
public abstract class ExternalFileImporter {

    /**
     * Gets the beans extracted from the file.
     * @param <T> The Type of the bean. It has to be a subclass of SpatialBean.
     * @param filePath The file location.
     * @return The list of extracted beans or an empty list if nothing can be extracted.
     */
    public <T extends SpatialBean> List<T> getBeans(String filePath){
        return new ArrayList<T>();
    }
}
