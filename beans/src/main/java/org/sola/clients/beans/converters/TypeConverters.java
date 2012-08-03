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
package org.sola.clients.beans.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.dozer.Mapper;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.common.mapping.MappingManager;

/**
 * Provides static methods to convert various data types.
 */
public final class TypeConverters {

    /** Converts {@link XMLGregorianCalendar} to {@link Date}*/
    public static Date XMLDateToDate(XMLGregorianCalendar xmlDate) {
        if (xmlDate == null) {
            return null;
        }
        return xmlDate.toGregorianCalendar().getTime();
    }

    /** Converts {@link Date} to {@link XMLGregorianCalendar}*/
    public static XMLGregorianCalendar DateToXMLDate(Date date) {
        if (date == null) {
            return null;
        }

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException ex) {
            return null;
        }
    }

    /** Converts {@link String} to {@link Date}*/
    public static Date StringToShortDate(String string) {
        Date result;
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        format.setLenient(false);

        try {
            result = format.parse(string);
        } catch (ParseException ex) {
            return null;
        }
        return result;
    }

    /**
     * Obtains an instance of the Mapper and sets the GenericTranslatorListener. 
     * @return 
     */
    private static Mapper getMapper() {
        return MappingManager.getMapper(new GenericTranslatorListener());        
    }
    
    /** 
     * Converts transfer object (TO) to bean.
     * @param <S> The type of transfer object.
     * @param <T> The type of the bean.
     * @param bean Bean object to populate with TO data.
     * @param to Transfer object.
     */
    public static <T extends AbstractBindingBean, S> T TransferObjectToBean(S to,
            Class<T> beanClass, AbstractBindingBean bean) {
        
        T resultEntity = null;
        if (to != null) {
            if (bean == null) {
                resultEntity = getMapper().map(to, beanClass);
            } else {
                getMapper().map(to, bean);
                resultEntity = (T) bean;
            }
        }
        return resultEntity;
    }

    /**
     * Generically translates a list of TO objects into a list of bean objects.
     * <p> This methods will copy TO data directly onto the bean with the matching Id / Code 
     * if one exists. If a matching bean does not exist, a new bean will be created and added
     * to the bean list. 
     * </p>
     * <p>
     * This method will not remove an bean from the bean list if it does not exist in the 
     * toList as the absence of a TO in the toList is more likely because no changes are required to
     * the matching bean. To delete and bean from the list, set its Delete flag on the matching
     * TO. 
     * </p> 
     * @param <T> The generic type of the bean class. Must extend AbstractBindingBean. 
     * @param <S> The generic type of the TO class. Must extend AbstractTO
     * @param toList The list of TO objects to translate from. If the list is null or empty, 
     * no translation occurs. 
     * @param beanClass The concrete bean class to use for the translation. e.g. Party.class.
     * @param beanList The bean list to translate the TO objects into. 
     * @return An beanList updated with the data from the TO objects or a new list if beanList 
     * was null. 
     */
    public static <T extends AbstractBindingBean, S> List<T> TransferObjectListToBeanList(
            List<S> toList, Class<T> beanClass, List<AbstractBindingBean> beanList) {
        // Default the return list to the list of entities passed in
        List<T> resultList = (List<T>) beanList;
        if (resultList == null) {
            resultList = new ArrayList<T>();
        }

        if (toList != null && toList.size() > 0) {
            resultList.clear();
            for (Object to : toList) {
                resultList.add(TransferObjectToBean(to, beanClass, null));
            }
        }
        return resultList;
    }

    /**
     * Generically translates from an bean object tree to a TO object tree.
     * @param <T> The type of TO class to translate to. Must extend AbstractTO. 
     * @param bean The bean object to translate from.
     * @param toClass The concrete TO class to translate to. e.g. ApplicationTO.class
     * @return The translated TO object or null
     */
    public static <T> T BeanToTrasferObject(AbstractBindingBean bean, Class<T> toClass) {
        T resultTO = null;
        if (bean != null) {
            resultTO = getMapper().map(bean, toClass);
        }
        return resultTO;
    }

    /**
     * Translates a list of bean objects to a list of TO objects. 
     * @param beanList The list of bean objects to translate from.  
     * @param toList The list of TO objects to translate to.  
     * @param toClass The concrete class of the TO to translate to. e.g. PartyTO.class 
     */
    public static void BeanListToTransferObjectList(
            List<AbstractBindingBean> beanList, List toList, Class<?> toClass) {
        if (beanList != null && beanList.size() > 0 && toList != null) {
            for (AbstractBindingBean bean : beanList) {
                toList.add(BeanToTrasferObject(bean, toClass));
            }
        }
    }
}