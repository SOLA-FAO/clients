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
package org.sola.clients.swing.gis.beans;

import com.vividsolutions.jts.geom.Geometry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.common.messaging.GisMessage;

/**
 * This is an abstract bean from which the beans that involve a geometry inherit from.
 * The extra property is featureGeom which holds the geometry of feature corresponding to the bean.
 * This bean, is used in a layer of type {@see AbstractSpatialObjectLayer}.
 * 
 * @author Elton Manoku
 */
public abstract class SpatialBean extends AbstractVersionedBean{

    private Geometry featureGeom;

    /**
     * Gets the geometry of the bean
     * @return 
     */
    public Geometry getFeatureGeom(){
        return featureGeom;
    }
    
    /**
     * Sets the geometry of the bean. For different kinds of beans this method can be overridden.
     * 
     * @param geometryValue 
     */
    public void setFeatureGeom(Geometry geometryValue){
        featureGeom = geometryValue;
    }
    
    /**
     * Gets the values of the bean for properties found in the argument.
     * It requires getters and setters in the bean corresponding to the property names.
     * If a getter/setter is not found the error is logged and the process goes on.
     * 
     * @param fromFieldsOnly The array of properties in the bean for which to get the values
     * @return 
     */
    public HashMap<String, Object> getValues(String[] fromFieldsOnly){
        HashMap<String, Object> values = new HashMap<String, Object>();
        for(String fieldName:fromFieldsOnly){
            try{
                Method method = this.getClass().getDeclaredMethod(
                        this.getMethodName(fieldName, true));
                Object fieldValue = method.invoke(this);
                values.put(fieldName, fieldValue);
            }catch(NoSuchMethodException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_GET_BEAN_VALUES, ex);
            }catch(SecurityException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_GET_BEAN_VALUES, ex);
            }catch(IllegalAccessException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_GET_BEAN_VALUES, ex);
            }catch(IllegalArgumentException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_GET_BEAN_VALUES, ex);
            }catch(InvocationTargetException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_GET_BEAN_VALUES, ex);
            }
        }
        return values;
    }

    /**
     * It sets the values in the bean properties that are found in values argument.
     * The property must have getter and setter in order to make it work.
     * If the method fails to execute, it is logged an exception but the execution of the code
     * goes on.
     * @param values 
     */
    public void setValues(HashMap<String, Object> values){
        for(String fieldName:values.keySet()){
            try{
                Method getMethod = this.getClass().getDeclaredMethod(
                        this.getMethodName(fieldName, true));
                Method method = this.getClass().getDeclaredMethod(
                        this.getMethodName(fieldName, false),
                        getMethod.getReturnType());
                method.invoke(this, values.get(fieldName));
            }catch(NoSuchMethodException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_SET_BEAN_VALUES, ex);
            }catch(IllegalAccessException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_SET_BEAN_VALUES, ex);
            }catch(InvocationTargetException ex){
                org.sola.common.logging.LogUtility.log(
                        GisMessage.CADASTRE_CHANGE_ERROR_SET_BEAN_VALUES, ex);
            }
        }
        
    }

    /**
     * It gets the method name
     * @param fieldName For the field
     * @param isGet If the method is get
     * @return THe name of the method
     */
    private String getMethodName(String fieldName, boolean isGet){
        return (isGet?"get":"set") 
                + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }
}
