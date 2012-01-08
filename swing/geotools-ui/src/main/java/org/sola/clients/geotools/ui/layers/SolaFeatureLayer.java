/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.geotools.ui.layers;

import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryType;
import org.sola.clients.geotools.ui.TocSymbol;
import org.sola.clients.geotools.util.Messaging;

/**
 * This layer is used as a base feature layer for other feature based layers.
 * @author Manoku
 */
public class SolaFeatureLayer extends SolaLayer {

    public static String SLD_RESOURCES = "/org/sola/clients/geotools/ui/layers/resources/";
    private static StyleFactory styleFactory = new StyleFactoryImpl();
    protected static WKBReader wkbReader = new WKBReader();
    protected static WKBWriter wkbWriter = new WKBWriter();
    private SimpleFeatureSource featureSource;
    private Style style;
    private FeatureLayer featureLayer;
    private String filterExpressionForSnapping = null;

    public SolaFeatureLayer() throws Exception {
    }

    protected void initialize(String name, SimpleFeatureSource featureSource, String styleResource)
            throws Exception {
        Style styleTmp = this.getStyleFromSLD(styleResource);
        this.initialize(name, featureSource, styleTmp);
    }

    protected void initialize(String name, SimpleFeatureSource featureSource, Style style)
            throws Exception {
        if (style == null) {
            throw new Exception("Style is null...");
        }
        this.setLayerName(name);
        this.featureSource = featureSource;
        this.style = style;
        this.featureLayer = new FeatureLayer(this.featureSource, this.style, this.getLayerName());
        this.getMapLayers().add(this.featureLayer);
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    public void setFeatureSource(SimpleFeatureSource featureSource) {
        this.featureSource = featureSource;
    }

    public Style getStyle() {
        return style;
    }

    public String getFilterExpressionForSnapping() {
        return filterExpressionForSnapping;
    }

    public void setFilterExpressionForSnapping(String filterExpressionForSnapping) {
        this.filterExpressionForSnapping = filterExpressionForSnapping;
    }

    @Override
    public List<TocSymbol> getLegend() {
        org.geotools.legend.Drawer legendDrawer = org.geotools.legend.Drawer.create();
        SimpleFeatureType simpleFeatureType =
                this.featureLayer.getSimpleFeatureSource().getSchema();
        SimpleFeature feature = null;
        GeometryType geomType = simpleFeatureType.getGeometryDescriptor().getType();
        if (geomType.getBinding().toString().contains("Polygon")) {
            int[] xy = new int[10];
            xy[0] = 2;
            xy[1] = 2;
            xy[2] = 20;
            xy[3] = 2;
            xy[4] = 20;
            xy[5] = 10;
            xy[6] = 2;
            xy[7] = 10;
            xy[8] = 2;
            xy[9] = 2;
            feature = legendDrawer.feature(legendDrawer.polygon(xy));
        } else if (geomType.getBinding().toString().contains("Polyline")) {
            int[] xy = new int[4];
            xy[0] = 0;
            xy[1] = 5;
            xy[2] = 20;
            xy[3] = 5;
            feature = legendDrawer.feature(legendDrawer.line(xy));
        } else if (geomType.getBinding().toString().contains("Point")) {
            int x = 10, y = 5;
            feature = legendDrawer.feature(legendDrawer.point(x, y));
        }
        List<TocSymbol> legendImageList = null;
        if (feature != null) {
            legendImageList = new ArrayList<TocSymbol>();
            for (FeatureTypeStyle featureTypeStyle : this.getStyle().featureTypeStyles()) {
                for (Rule rule : featureTypeStyle.rules()) {
                    BufferedImage bi = new BufferedImage(22, 12, BufferedImage.TYPE_INT_ARGB);
                    bi.getGraphics().setColor(Color.white);
                    bi.getGraphics().fillRect(0, 0, 22, 12);
                    legendDrawer.drawDirect(bi, feature, rule);
                    TocSymbol tocSymbol = new TocSymbol();
                    tocSymbol.setImage(bi);
                    tocSymbol.setTitle(rule.getName());
                    legendImageList.add(tocSymbol);
                }
            }
        }
        return legendImageList;
    }

    private Style getStyleFromSLD(String sldResource) throws Exception {
        Style[] styles = this.getStylesFromSLD(sldResource);
        Style styleTmp = null;
        if (styles != null && styles.length > 0) {
            styleTmp = styles[0];
        }
        return styleTmp;
    }

    private Style[] getStylesFromSLD(String sldResource) throws Exception {
        Style[] styles = null;
        try {

            URL sldURL = null;
            String[] resourcesArray = SLD_RESOURCES.split(",");
            for (String resourcePath : resourcesArray) {
                sldURL = SolaFeatureLayer.class.getResource(resourcePath + sldResource);
                if (sldURL != null) {
                    break;
                }
            }
            if (sldURL == null) {
                throw new Exception(
                        Messaging.Ids.UTILITIES_SLD_DOESNOT_EXIST_ERROR.toString());
            }
            // StyleFactory styleFactory = new StyleFactoryImpl();
            SLDParser stylereader = new SLDParser(styleFactory, sldURL);
            styles = stylereader.readXML();
            return styles;
        } catch (Exception ex) {
            throw new Exception(
                    Messaging.Ids.UTILITIES_SLD_LOADING_ERROR.toString());
        }
    }
}
