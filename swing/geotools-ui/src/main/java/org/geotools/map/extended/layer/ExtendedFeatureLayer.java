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
package org.geotools.map.extended.layer;

import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.geotools.swing.control.extended.TocSymbol;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.Messaging;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 * This layer is used as a base feature layer for other feature based layers.
 * @author Elton Manoku
 */
public class ExtendedFeatureLayer extends ExtendedLayer {

    /**
     * The String with the path to the resources for layers. If there is more than one path,
     * concatenate using comma ,.
     */
    private static String sldResources = "/org/geotools/map/extended/layer/resources/";
    private static StyleFactory styleFactory = new StyleFactoryImpl();
    private static FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
    private SimpleFeatureSource featureSource;
    private Style style;
    private FeatureLayer featureLayer;
    private String filterExpressionForSnapping = null;
    /**
     * It is used to read WKB and convert it to geometry
     */
    protected final static WKBReader wkbReader = new WKBReader();
    /**
     * It is used to convert a geometry to WKB
     */
    protected final static WKBWriter wkbWriter = new WKBWriter();

    /**
     * It initializes the layer.
     * @param name Name of the layer. Has to be unique.
     * @param featureSource The source of features.
     * @param styleResource The style resource name. With this name, it is searched in the paths 
     * provided in sldResources in the order of appearance.
     * @throws InitializeLayerException 
     */
    protected void initialize(String name, SimpleFeatureSource featureSource, String styleResource)
            throws InitializeLayerException {
        Style styleTmp = this.getStyleFromSLD(styleResource);
        this.initialize(name, featureSource, styleTmp);
    }

    /**
     * Initializer but the style is already resolved.
     * @param name
     * @param featureSource
     * @param style
     * @throws InitializeLayerException 
     */
    protected void initialize(String name, SimpleFeatureSource featureSource, Style style)
            throws InitializeLayerException {
        if (style == null) {
            throw new InitializeLayerException("Style is missing.", null);
        }
        this.setLayerName(name);
        this.featureSource = featureSource;
        this.style = style;
        this.featureLayer = new FeatureLayer(this.featureSource, this.style, this.getLayerName());
        this.getMapLayers().add(this.featureLayer);
    }

    /**
     * It adds extra sld resources to the existing one. The last one added gets priority.
     */
    public static void setExtraSldResources(String sldExtraResources){
        sldResources = String.format("%s,%s",sldExtraResources, sldResources); 
    }
    /**
     * Gets the feature source
     * @return 
     */
    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    /**
     * Sets the feature source
     * @param featureSource 
     */
    public void setFeatureSource(SimpleFeatureSource featureSource) {
        this.featureSource = featureSource;
    }

    /**
     * Gets the style of the layer
     * @return 
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Gets a string that represents a filter expressions if the feature layer will be used
     * as a target for snapping during editing.
     * @return 
     */
    public String getFilterExpressionForSnapping() {
        return filterExpressionForSnapping;
    }

    /**
     * Sets the filter for the features in the layer that can be used as a target for snapping.
     * @param filterExpressionForSnapping It is a attribute based filter as in sql.
     */
    public void setFilterExpressionForSnapping(String filterExpressionForSnapping) {
        this.filterExpressionForSnapping = filterExpressionForSnapping;
    }

    /**
     * It retrieves the list of symbols that represent the symbology. For each rule in the style
     * a symbol is created.
     * @return 
     */
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
        } else if (geomType.getBinding().toString().contains("LineString")) {
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

    /**
     * It gets the first style found in the resource.
     * @param sldResource The resource name.
     * @return The first style if styles found or null
     * @throws InitializeLayerException 
     */
    private Style getStyleFromSLD(String sldResource) throws InitializeLayerException {
        Style[] styles = this.getStylesFromSLD(sldResource);
        Style styleTmp = null;
        if (styles != null && styles.length > 0) {
            styleTmp = styles[0];
        }
        return styleTmp;
    }

    /**
     * Gets the list of styles found in the resource. The location of the resource is taken 
     * from @see sldResources.
     * @param sldResource
     * @return
     * @throws InitializeLayerException 
     */
    private Style[] getStylesFromSLD(String sldResource) throws InitializeLayerException {
        Style[] styles = null;
        try {

            URL sldURL = null;
            String[] resourcesArray = sldResources.split(",");
            for (String resourcePath : resourcesArray) {
                sldURL = ExtendedFeatureLayer.class.getResource(resourcePath + sldResource);
                if (sldURL != null) {
                    break;
                }
            }
            if (sldURL == null) {
                throw new InitializeLayerException(
                        Messaging.Ids.UTILITIES_SLD_DOESNOT_EXIST_ERROR.toString(), null);
            }
            SLDParser stylereader = new SLDParser(styleFactory, sldURL);
            styles = stylereader.readXML();
            return styles;
        } catch (IOException ex) {
            throw new InitializeLayerException(
                    Messaging.Ids.UTILITIES_SLD_LOADING_ERROR.toString(), ex);
        }
    }

    /**
     * It searches for features within the bbox. Additionally, the extra attribute based filter 
     * can be used as well.
     * @param bbox The bounding box to search in
     * @param whereAttributeFilter Extra attribute based filter. It can be null
     * @return A FeatureCollection if something found or nothing found 
     * or null if some error occurred
     */
    public FeatureCollection getFeaturesInRange(
            ReferencedEnvelope bbox, String whereAttributeFilter) {

        FeatureType schema = this.getFeatureSource().getSchema();
        String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
        Filter filterBbox = filterFactory.bbox(filterFactory.property(geometryPropertyName), bbox);
        Filter filter;
        if (whereAttributeFilter != null) {
            String[] filterSides = whereAttributeFilter.split("=");
            Filter filterExtra = filterFactory.equals(
                    filterFactory.property(filterSides[0]),
                    filterFactory.literal(filterSides[1]));
            filter = filterFactory.and(filterBbox, filterExtra);
        } else {
            filter = filterBbox;
        }
        try {
            return this.getFeatureSource().getFeatures().subCollection(filter);
        } catch (IOException ex) {
            Messaging.getInstance().show(
                    Messaging.Ids.GEOTOOL_GET_FEATURE_IN_RANGLE_ERROR.toString(), ex.getMessage());
        }
        return null;
    }
    
    /**
     * Gets first feature within bounding box.
     * @param bbox
     * @return first feature found or null.
     */
    public SimpleFeature getFirstFeatureInRange(ReferencedEnvelope bbox){
        SimpleFeature feature = null;
        FeatureCollection featureCollection = this.getFeaturesInRange(bbox, null);
        if (featureCollection != null) {
            SimpleFeatureIterator featureIterator =
                    (SimpleFeatureIterator) featureCollection.features();
            while (featureIterator.hasNext()) {
                feature = featureIterator.next();
                break;
            }
            featureIterator.close();
        }
        return feature;
        
    }
}
