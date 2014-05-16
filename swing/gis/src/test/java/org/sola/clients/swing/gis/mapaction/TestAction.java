/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.mapaction;

import java.io.IOException;
import org.geotools.swing.extended.Map;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.swing.gis.imagegenerator.MapImageGeneratorForSelectedParcel;

/**
 *
 * @author Elton Manoku
 */
public class TestAction extends ExtendedAction{

    public TestAction(Map map) {
        super(map, "Test", "Used to test", "test");
    }
    
    @Override
    public void onClick() {
    }
    
}
