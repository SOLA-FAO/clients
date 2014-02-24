/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.common.utils;

import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;

/**
 * Contains helping method to manage beans binding aspects.
 */
public class BindingTools {

    /** 
     * Updates binding with new source. 
     * @param bindingGroup Binding group holding bindings.
     * @param oldSourceObject Currently bound source object.
     * @param newSourceObject New source object to bind.
     */
    public static void updateSource(BindingGroup bindingGroup, Object oldSourceObject, Object newSourceObject) {
        if (bindingGroup != null && oldSourceObject != null && newSourceObject != null) {
            for (Binding binding : bindingGroup.getBindings()) {
                if (binding.getSourceObject() != null && binding.getSourceObject() == oldSourceObject) {
                    binding.unbind();
                    binding.setSourceObject(newSourceObject);
                    binding.bind();
                }
            }
            //bindingGroup.bind();
        }
    }

    /** 
     * Resets binding. 
     * @param bindingGroup Binding group holding bindings.
     */
    public static void resetBinding(BindingGroup bindingGroup) {
        if (bindingGroup != null) {
            bindingGroup.unbind();
            bindingGroup.bind();
        }
    }
    
    /** 
     * Resets binding by name. 
     * @param bindingGroup Binding group holding bindings.
     * @param groupName Name of the group to reset.
     */
    public static void resetBinding(BindingGroup bindingGroup, String groupName) {
        if (bindingGroup != null) {
            Binding binding = bindingGroup.getBinding(groupName);
            binding.unbind();
            binding.bind();
        }
    }

    /** 
     * Refreshes all bindings in the given binding group. 
     * @param bindingGroup Binding group holding bindings.
     */
    public static void refreshBinding(BindingGroup bindingGroup) {
        if (bindingGroup != null) {
            for (Binding binding : bindingGroup.getBindings()) {
                if (binding.isBound()) {
                    binding.refreshAndNotify();
                }
            }
        }
    }

    /** 
     * Refreshes bindings by the name of the binding group. 
     * @param bindingGroup Binding group holding bindings.
     * @param groupName Name of the group to refresh.
     */
    public static void refreshBinding(BindingGroup bindingGroup, String groupName) {
        if (bindingGroup != null) {
            Binding binding = bindingGroup.getBinding(groupName);
            if (binding != null && binding.isBound()) {
                binding.refreshAndNotify();
            }
        }
    }
}
