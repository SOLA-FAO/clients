/*
 * Copyright 2014 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sola.clients.swing.common.utils;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.util.Locale;

/**
 * Contains different localization support functions
 */
public class LocalizationTools {
    /**
     * Sets orientation for provided component and its child components. 
     * Orientation is set depending on default locale.
     * @param c Component to assign orientation
     */
    public static void setOrientation(Component c){
        //c.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        c.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        // Scroll through the child controls and assign their orientation
//        if(Container.class.isAssignableFrom(c.getClass())){
//            Component[] children = ((Container)c).getComponents();
//            for (Component child : children) {
//                child.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
//                setOrientation(child);
//            }
//        }
    }
}
