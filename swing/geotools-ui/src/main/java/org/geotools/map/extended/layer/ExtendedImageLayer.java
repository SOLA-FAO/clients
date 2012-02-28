/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.map.extended.layer;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.DirectLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

/**
 *
 * @author Elton Manoku
 */
public class ExtendedImageLayer extends ExtendedLayer {
    
    public ExtendedImageLayer(String name, String rasterFile)throws Exception{
         DirectImageLayer rasterLayer = new DirectImageLayer();
         rasterLayer.setRasterFile(rasterFile);
         this.getMapLayers().add(rasterLayer);
    }
}
