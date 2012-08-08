/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

/**
 * This interface contains methods that must be implemented by tools that target cadastre objects.
 * 
 * @author Eltoon Manoku
 */
public interface TargetCadastreObjectTool {
    
    /**
     * Sets the type of cadastre object that will be treated as a target.
     * @param cadastreObjectType 
     */
    public void setCadastreObjectType(String cadastreObjectType);

}
