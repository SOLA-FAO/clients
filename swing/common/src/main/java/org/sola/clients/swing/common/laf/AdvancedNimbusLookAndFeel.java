/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.common.laf;

import org.sola.clients.swing.common.laf.NimbusFrameBorder;
import org.sola.clients.swing.common.laf.NimbusRootPaneUI;
import java.awt.Dimension;
import javax.swing.UIDefaults;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
* Sourced from https://weblogs.java.net/blogs/Anthra
* @author Brennenraedts Benjamin
*/

public class AdvancedNimbusLookAndFeel extends NimbusLookAndFeel{
     
    private UIDefaults uiDefaults;

    /**
     * @inheritDoc
     */
    @Override
    public UIDefaults getDefaults() {
        if(uiDefaults==null)
        {
            uiDefaults = super.getDefaults();
            uiDefaults.put("RootPaneUI", NimbusRootPaneUI.class.getName());
            uiDefaults.put("RootPane.frameBorder", new NimbusFrameBorder());
            uiDefaults.put("FrameTitlePane.dimension",new Dimension(50, 24));
            populateWithStandard(uiDefaults, "Frame");
        }
        return uiDefaults;
    }
    
    protected void populateWithStandard(UIDefaults defaults, String componentKey)
    {
        String key = componentKey+".foreground";
        if (!uiDefaults.containsKey(key)){
            uiDefaults.put(key,
                    new NimbusProperty(componentKey,"textForeground"));
        }
        key = componentKey+".background";
        if (!uiDefaults.containsKey(key)){
            uiDefaults.put(key,
                    new NimbusProperty(componentKey,"background"));
        }
        key = componentKey+".font";
        if (!uiDefaults.containsKey(key)){
            uiDefaults.put(key,
                    new NimbusProperty(componentKey,"font"));
        }
        key = componentKey+".disabledText";
        if (!uiDefaults.containsKey(key)){
            uiDefaults.put(key,
                    new NimbusProperty(componentKey,"Disabled",
                           "textForeground"));
        }
        key = componentKey+".disabled";
        if (!uiDefaults.containsKey(key)){
            uiDefaults.put(key,
                    new NimbusProperty(componentKey,"Disabled",
                            "background"));
        }
    }
    
    @Override
    public boolean getSupportsWindowDecorations() {
        return true;
    }
    
    /**
     * Nimbus Property that looks up Nimbus keys for standard key names. For
     * example "Button.background" --> "Button[Enabled].backgound"
     */
    private class NimbusProperty implements UIDefaults.ActiveValue, UIResource {
        private String prefix;
        private String state = null;
        private String suffix;
        private boolean isFont;

        private NimbusProperty(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
            isFont = "font".equals(suffix);
        }

        private NimbusProperty(String prefix, String state, String suffix) {
            this(prefix,suffix);
            this.state = state;
        }

        /**
         * Creates the value retrieved from the <code>UIDefaults</code> table.
         * The object is created each time it is accessed.
         *
         * @param table a <code>UIDefaults</code> table
         * @return the created <code>Object</code>
         */
        @Override
        public Object createValue(UIDefaults table) {
            Object obj = null;
            // check specified state
            if (state!=null){
                obj = uiDefaults.get(prefix+"["+state+"]."+suffix);
            }
            // check enabled state
            if (obj==null){
                obj = uiDefaults.get(prefix+"[Enabled]."+suffix);
            }
            // check for defaults
            if (obj==null){
                if (isFont) {
                    obj = uiDefaults.get("defaultFont");
                } else {
                    obj = uiDefaults.get(suffix);
                }
            }
            return obj;
        }
    }
    
    //    @Override public void initialize() {
//        super.initialize();
//        // create synth style factory
//        setStyleFactory(new ThreadStyleFactory(getStyleFactory()));
//    }
//    
//    private class ThreadStyleFactory extends SynthStyleFactory{
//
//        private final SynthStyleFactory embedded;
//        
//        public ThreadStyleFactory(SynthStyleFactory synthStyleFactory) {
//            this.embedded = synthStyleFactory;
//        }
//        
//        @Override
//        public synchronized SynthStyle getStyle(JComponent c, Region id) {
//            return this.embedded.getStyle(c, id);
//        }
//    
//    }
}
