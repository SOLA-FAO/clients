/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.layer;

import java.util.List;
import org.opengis.feature.simple.SimpleFeature;

/**
 * The interface has to be implemented by layers that will be used in the functionality of
 * irregular boundary change.
 * 
 * @author Elton Manoku
 */
public interface TargetBoundaryLayer {
    
    /**
     * This is the distance that a point must have from a boundary to be considered as being
     * on the boundary.
     */
    static double FILTER_PRECISION = 0.01;
    
    /**
     * Gets the feature that corresponds to the cadastre object.
     * @param id The id of the cadastre object
     * @return The feature or null if nothing is found
     */
    SimpleFeature getFeatureByCadastreObjectId(String id);
    
    /**
     * It notifies the change events for the feature corresponding to the cadastre object
     * 
     * @param forFeatureOfCadastreObjectId The id of the cadastre object
     */
    void notifyEventChanges(String forFeatureOfCadastreObjectId);
    
    /**
     * Gets a list of ids for those cadastre objects where a node is involved
     * 
     * @param nodeFeature
     * @return 
     */
    List<String> getCadastreObjectTargetIdsFromNodeFeature(SimpleFeature nodeFeature);
    
}
