/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.map.extended.layer;

import java.io.File;
import java.io.IOException;
import org.geotools.swing.extended.exception.DirectImageNotValidFileException;

/**
 *
 * @author Elton Manoku
 */
public class ExtendedImageLayer extends ExtendedLayer {
    
    DirectImageLayer rasterLayer;
    public ExtendedImageLayer(String name, String title)throws Exception{
        this.setLayerName(name);
        this.setTitle(title);
         this.rasterLayer = new DirectImageLayer();
         this.getMapLayers().add(this.rasterLayer);
    }
    
    public void setRasterFile(File rasterFile) 
            throws IOException, DirectImageNotValidFileException{
        this.rasterLayer.setRasterFile(rasterFile);
    }
    public void setMaxX(double maxX) {
        this.rasterLayer.setMaxX(maxX);
    }

    public void setMaxY(double maxY) {
        this.rasterLayer.setMaxY(maxY);
    }

    public void setMinX(double minX) {
        this.rasterLayer.setMinX(minX);
    }

    public void setMinY(double minY) {
        this.rasterLayer.setMinY(minY);
    } 

}
