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
package org.sola.clients.beans.converters;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.common.logging.LogUtility;

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
        Date result = null;
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
     * Converts transfer object (TO) to bean.
     * @param <S> The type of transfer object.
     * @param <T> The type of the bean.
     * @param bean Bean object to populate with TO data.
     * @param to Transfer object.
     */
    public static <T extends AbstractBindingBean, S> T TransferObjectToBean(S to,
            Class<T> beanClass, AbstractBindingBean bean) {

        T resultBean = null;
        if (to != null) {
            try {
                if (bean == null) {
                    // No bean resultTO merge with, so create a new instance of T
                    resultBean = beanClass.newInstance();
                    LogUtility.log("Translating TO: " + to.toString()
                            + " to a new bean.", Level.FINE);
                } else {
                    // Use bean as the base object for the resultbean and copy data from the TO
                    // onto it. 
                    resultBean = (T) bean;
                    LogUtility.log("Translating TO: " + to.toString()
                            + " to existing bean " + bean.toString(), Level.FINE);
                }

                // Translate the id or code first as this will be useful reference information 
                // during translation of child objects
                if (resultBean instanceof AbstractIdBean) {
                    Method getIdMethod = to.getClass().getMethod("getId");
                    setBeanProperty(resultBean, getIdMethod.getName(), getIdMethod.invoke(to), getIdMethod.getReturnType());
                } else if (resultBean instanceof AbstractCodeBean) {
                    Method getCodeMethod = to.getClass().getMethod("getCode");
                    setBeanProperty(resultBean, getCodeMethod.getName(), getCodeMethod.invoke(to), getCodeMethod.getReturnType());
                }

                // Use the TO resultTO identify the fields resultTO translate based on the TO's public 
                // getter methods. Filter out any get methods with return type VOID and 
                // the getClass method which is on every object. 
                Method[] methods = to.getClass().getMethods();
                for (Method toMethod : methods) {
                    if ((toMethod.getName().startsWith("get")
                            || toMethod.getName().startsWith("is"))
                            && !toMethod.getReturnType().equals(Void.TYPE)
                            && !toMethod.getName().equals("getClass")
                            && !toMethod.getName().equals("getId")) {

                        // Use the return type of the get method resultTO determine how resultTO process it
                        Class<?> toReturnClass = toMethod.getReturnType();

                        if (isSimpleType(toReturnClass)) {
                            // Simple getter. Obtain the value from the TO get method and assign
                            // it resultTO the bean. 
                            setBeanProperty(resultBean, toMethod.getName(), toMethod.invoke(to), toMethod.getReturnType());

                        } else if (checkAbstractTOClass(toReturnClass)) {
                            // This is a child TO so it needs resultTO be translated. Invoke the matching
                            // getter on the bean resultTO determine the appropriate child bean 
                            // class type. If the child bean is null, create a new instance and
                            // attach it resultTO the parent before doing the translation. This should add
                            // the child into the persistence context. Newing the child before it 
                            // is translated will also mean any child entities of this child will 
                            // also be added resultTO the persistence context correctly. 
                            try {
                                Object toObject = toMethod.invoke(to);
                                if (toObject != null) {
                                    Method childBeanGetter = resultBean.getClass().getMethod(toMethod.getName());
                                    AbstractBindingBean childBean = (AbstractBindingBean) childBeanGetter.invoke(resultBean);
                                    if (childBean == null) {
                                        // Create a blank child bean and set it on the parent. 
                                        Class<?> childBeanClass = childBeanGetter.getReturnType();
                                        childBean = (AbstractBindingBean) childBeanClass.newInstance();
                                        setBeanProperty(resultBean, toMethod.getName(), childBean, childBean.getClass());
                                    }
                                    // Execute the translation on the child TO object directly into the
                                    // child bean attached resultTO the parent bean currently being translated. 
                                    TransferObjectToBean(toObject, childBean.getClass(), childBean);
                                }

                            } catch (NoSuchMethodException e) {
                                // Could find the bean type for the beanSetter
                                LogUtility.log("Could not translate child TO for " + toMethod.getName()
                                        + " with return type " + toReturnClass.getSimpleName()
                                        + " on to " + to.getClass().getSimpleName(), Level.WARNING);
                            }
                        } else if (Iterable.class.isAssignableFrom(toReturnClass)) {
                            // This is an iterable class, so it likely contains a list of child
                            // objects resultTO translate. Assume it is a parameterized generic list for
                            // simplicity. 
                            try {
                                Method beanListGetter = resultBean.getClass().getMethod(toMethod.getName());
                                ParameterizedType beanListReturnType =
                                        (ParameterizedType) beanListGetter.getGenericReturnType();
                                Class<?> beanChildClass =
                                        ((Class<?>) beanListReturnType.getActualTypeArguments()[0]);

                                if (isSimpleType(beanChildClass)) {
                                    // This is a list of simple types. Reassign the bean with 
                                    // the list from the TO list getter
                                    setBeanProperty(resultBean, toMethod.getName(), toMethod.invoke(to), toMethod.getReturnType());

                                } else if (AbstractBindingBean.class.isAssignableFrom(beanChildClass)) {
                                    // This is a list of beans. Check if there is anything resultTO 
                                    // translate from the TO. 
                                    List<Object> toChildList = (List<Object>) toMethod.invoke(to);
                                    if (toChildList != null && !toChildList.isEmpty()) {
                                        // Get the list of child entities from the matching getter
                                        // on the bean. 
                                        List<AbstractBindingBean> beanChildList =
                                                (List<AbstractBindingBean>) beanListGetter.invoke(resultBean);
                                        if (beanChildList == null) {
                                            // The list on the bean is null. Create a list and 
                                            // set it on the bean before performing the translation
                                            beanChildList = (List<AbstractBindingBean>) createList(
                                                    ((AbstractBindingBean) beanChildClass.newInstance()).getClass());
                                            setBeanProperty(resultBean, toMethod.getName(), beanChildList, beanChildList.getClass());
                                        }
                                        // Translate the list of TO objects onto the beanChildList
                                        TransferObjectListToBeanList(toChildList,
                                                ((AbstractBindingBean) beanChildClass.newInstance()).getClass(),
                                                beanChildList);
                                    }
                                }
                            } catch (NoSuchMethodException e) {
                                // Could find the bean type for the beanSetter
                                LogUtility.log("Could not translate child TO for " + toMethod.getName()
                                        + " with return type " + toReturnClass.getSimpleName()
                                        + " on to " + to.getClass().getSimpleName(), Level.WARNING);
                            }
                        }
                    }
                }
                LogUtility.log("Finished translating TO: " + to.toString(), Level.FINE);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to translate fromTO " + to.toString(), ex);
            }
        }
        return resultBean;
    }

    /**
     * Uses java generics to create a new ArrayList that can contain elements of the
     * specified elementClass. Calling a method appears resultTO be the easiest way resultTO achieve this. 
     * @param <T> Generic type parameter
     * @param elementClass The class of T
     * @return A new ArrayList that can contain elements of class elementClass. 
     */
    private static <T> List<T> createList(Class<T> elementClass) {
        return new ArrayList<T>();
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
            resultList = createList(beanClass);
        }

        if (toList != null && toList.size() > 0) {

            // Clear bean list
            resultList.clear();

            for (Object to : toList) {
                // Add the object to the resultList
                resultList.add(TransferObjectToBean(to, beanClass, null));
            }
        }
        return resultList;
    }

    /** Checks if the given class is inheriting from the AbstractTO */
    private static boolean checkAbstractTOClass(Class<?> toClass) {
        if (org.sola.webservices.transferobjects.AbstractTO.class.isAssignableFrom(toClass)) {
            return true;
        }
        return false;
    }

    /** Checks if the given class is inheriting from the AbstractIdTO */
    private static boolean checkAbstractIdTOClass(Class<?> toClass) {
        if (org.sola.webservices.transferobjects.AbstractIdTO.class.isAssignableFrom(toClass)) {
            return true;
        }
        return false;
    }

    /** Checks if the given class is inheriting from the AbstractCodeTO */
    private static boolean checkAbstractCodeTOClass(Class<?> toClass) {
        if (org.sola.webservices.transferobjects.AbstractCodeTO.class.isAssignableFrom(toClass)) {
            return true;
        }
        return false;
    }

    /** Returns <code>Id</code> from the AbstractIdTO object. */
    private static String getIdFromTO(Object to) {
        if (checkAbstractIdTOClass(to.getClass())) {
            return invokeStringGetter(to, "getId");
        }
        return null;
    }

    /** Returns <code>Code</code> from the AbstractCodeTO object. */
    private static String getCodeFromTO(Object to) {
        if (checkAbstractCodeTOClass(to.getClass())) {
            return invokeStringGetter(to, "getCode");
        }
        return null;
    }

    /** Invokes getter on the given object, which returns String type.*/
    private static String invokeStringGetter(Object to, String methodName) {
        try {
            Method getMethodForIdCode = to.getClass().getMethod(methodName);

            if (getMethodForIdCode == null) {
                return null;
            }

            return getMethodForIdCode.invoke(to).toString();

        } catch (Exception ex) {
            LogUtility.log("Unable to get method \"" + methodName + "\" for object " + to.toString() + ", Exception: " + ex.getMessage(), Level.FINE);
            return null;
            //throw new RuntimeException("Unable to get method \"" + methodName + "\" for object " + to.toString(), ex);
        }
    }

    /** 
     * Sets bean's property.
     * @param <T> The type of the bean.
     * @param bean Bean object to set value to.
     * @param toGetterName The name of the getter method.
     * @param value The value to set.
     * @param valueClass The class of the value.
     */
    private static <T extends AbstractBindingBean> void setBeanProperty(T bean,
            String toGetterName, Object value, Class<?> valueClass) {

        Method beanSetter = null;
        Method beanGetter = null;
        Class<?> beanSetterParamClass = null;
        String beanSetterName = null;
        Object beanGetterValue = null;
        boolean areEqual = false;

        // Before setting the value, check resultTO make sure it is not the same as the current 
        // value on the bean. This will avoid triggering any events that may be tied resultTO
        // the beanSetter

        try {
            beanGetter = bean.getClass().getMethod(toGetterName);
        } catch (NoSuchMethodException e) {
            LogUtility.log("No getter called " + toGetterName
                    + " on bean " + bean.toString(), Level.WARNING);
            return;
        }

        try {
            beanGetterValue = beanGetter.invoke(bean);
        } catch (Exception e) {
            LogUtility.log("Unable to invoke getter " + beanGetter.getName()
                    + " on bean " + bean.toString()
                    + ". Exception: " + e.getMessage(),
                    Level.WARNING);
        }

        // Trick for XML dates
        if (XMLGregorianCalendar.class.isAssignableFrom(valueClass)) {
            valueClass = Date.class;
            if (value != null) {
                value = XMLDateToDate((XMLGregorianCalendar) value);
            }
        }

        // Uses nested short hand if expression resultTO determine the equality of the two Getter 
        // values as follows:
        // 1. If both values are null, then they are equal. 
        // 2. If one of the values are null (given the first check), the values cannot be equal
        // 3. If neither value is null, perform the equals check
        areEqual = beanGetterValue == null && value == null ? true
                : beanGetterValue == null || value == null ? false
                : beanGetterValue.equals(value);

        beanSetterParamClass = beanGetter.getReturnType();

        if (!areEqual && beanSetterParamClass.isAssignableFrom(valueClass)) {
            // Determine the name of the beanSetter method by replacing get with set on the 
            // beanGetter. Also obtain the beanSetterParamClass from the return type of the
            // beanGetter. 
            if (beanGetter.getName().startsWith("is")) {
                beanSetterName = "set" + beanGetter.getName().substring(2);
            } else {
                beanSetterName = "set" + beanGetter.getName().substring(3);
            }

            try {
                beanSetter = bean.getClass().getMethod(beanSetterName, beanSetterParamClass);
            } catch (NoSuchMethodException e) {
                // Could be a one way property, so just log a warning
                LogUtility.log("No setter called " + beanSetterName
                        + " with parameter " + beanSetterParamClass.getSimpleName()
                        + " for " + toGetterName + " on bean "
                        + bean.toString(), Level.WARNING);
            }
            if (beanSetter != null) {
                try {
                    // Invoke the beanSetter with the value obtained from the resultTO getter.
                    beanSetter.invoke(bean, value);
                } catch (Exception e) {
                    // Attempt resultTO invoke the beanSetter method failed. Log a warning
                    LogUtility.log("Unable to invoke setter " + beanSetter.getName()
                            + " with parameter " + beanSetterParamClass.getSimpleName()
                            + " on bean " + bean.toString()
                            + " with value "
                            + ". Exception: " + e.getMessage(),
                            Level.WARNING);
                }
            }
        } else {
            LogUtility.log("Values are equal so did not use " + toGetterName
                    + " to set value "
                    + " on bean " + bean.toString(), Level.FINEST);
        }
    }

    /** 
     * Checks if the type of the given class is simple type. 
     * @param checkClass The class to check.
     */
    private static boolean isSimpleType(Class<?> checkClass) {

        return checkClass.isPrimitive()
                || checkClass.isArray()
                || String.class.isAssignableFrom(checkClass)
                || Date.class.isAssignableFrom(checkClass)
                || BigDecimal.class.isAssignableFrom(checkClass)
                || XMLGregorianCalendar.class.isAssignableFrom(checkClass)
                || checkClass.isEnum()
                || Short.class.isAssignableFrom(checkClass)
                || Boolean.class.isAssignableFrom(checkClass);
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
        if (bean != null && checkAbstractTOClass(toClass)) {
            try {

                LogUtility.log("Translating bean: " + bean.toString(), Level.FINE);
                resultTO = toClass.newInstance();

                // Use the TO resultTO identify the fields resultTO translate based on the TO set methods
                Method[] methods = resultTO.getClass().getMethods();

                for (Method toMethod : methods) {

                    if ((toMethod.getName().substring(0, 3).equalsIgnoreCase("set")
                            && toMethod.getParameterTypes().length == 1)
                            || Iterable.class.isAssignableFrom(toMethod.getReturnType())) {

                        // set Method, so get the parameter type and the value from the bean getter
                        Class<?> toParameterType;

                        if (Iterable.class.isAssignableFrom(toMethod.getReturnType())) {
                            toParameterType = toMethod.getReturnType();
                        } else {
                            toParameterType = toMethod.getParameterTypes()[0];
                        }

                        String beanGetterName;

                        if (boolean.class.isAssignableFrom(toParameterType)) {
                            beanGetterName = "is" + toMethod.getName().substring(3);
                        } else {
                            beanGetterName = "get" + toMethod.getName().substring(3);
                        }

                        Method beanGetter = null;
                        Object beanGetterValue = null;

                        try {
                            beanGetter = bean.getClass().getMethod(beanGetterName);
                        } catch (NoSuchMethodException e) {
                            // Could be a one way property, so just log a warning
                            LogUtility.log("No getter called " + beanGetterName
                                    + " for " + toMethod.getName() + " on bean "
                                    + bean.toString(), Level.WARNING);
                            continue;
                        }

                        if (beanGetter != null) {

                            // Get the value from the bean getter
                            beanGetterValue = beanGetter.invoke(bean);

                            if (isSimpleType(toParameterType)) {
                                // Simple type, so copy the value from the bean onto the
                                // TO object
                                LogUtility.log("Setting " + toMethod.getName()
                                        + " with value " + beanGetterValue
                                        + " from bean " + bean.toString(), Level.FINEST);

                                // Trick for XML dates
                                if (XMLGregorianCalendar.class.isAssignableFrom(toParameterType)) {
                                    if (beanGetterValue != null) {
                                        beanGetterValue = DateToXMLDate((Date) beanGetterValue);
                                    }
                                }

                                toMethod.invoke(resultTO, beanGetterValue);

                            } else if (checkAbstractTOClass(toParameterType)) {

                                // A child TO, so translate the child bean resultTO the appropriate
                                // TO and set it on the parent TO. 
                                Object childTO = null;
                                childTO = BeanToTrasferObject((AbstractBindingBean) beanGetterValue,
                                        (toParameterType.newInstance()).getClass());

                                if (childTO != null) {
                                    LogUtility.log("Setting " + toMethod.getName()
                                            + " with childTO " + childTO.toString()
                                            + " from bean " + bean.toString(), Level.FINEST);
                                    toMethod.invoke(resultTO, childTO);
                                }

                            } else if (Iterable.class.isAssignableFrom(toParameterType)) {
                                // A list object. Get the generic type of the list

                                ParameterizedType toListParameterType = (ParameterizedType) toMethod.getGenericReturnType();

                                //ParameterizedType toListParameterType =
                                //        (ParameterizedType) toMethod.getReturnType().get;
                                Class<?> toChildClass =
                                        ((Class<?>) toListParameterType.getActualTypeArguments()[0]);

                                if (isSimpleType(toChildClass)) {
                                    // List of simple types, just assign te list across
                                    LogUtility.log("Setting " + toMethod.getName()
                                            + " with list value " + beanGetterValue
                                            + " from bean " + bean.toString(), Level.FINEST);
                                    //toMethod.invoke(resultTO, beanGetterValue);

                                    List beanLst = (List) beanGetterValue;
                                    List toLst = (List) toMethod.invoke(resultTO);

                                    if (beanLst != null && beanLst.size() > 0 && toLst != null) {
                                        for (Object obj : beanLst) {
                                            toLst.add(obj);
                                        }
                                    }

                                } else if (checkAbstractTOClass(toChildClass)) {
                                    // List of AbstractTOs. Need to translate each bean to a 
                                    // TO object.
                                    BeanListToTransferObjectList(
                                            ((List<AbstractBindingBean>) beanGetterValue),
                                            (List) toMethod.invoke(resultTO), toChildClass);
                                    //
                                    // This doen't work for TOs from boudary
                                    //toMethod.invoke(resultTO, childTOList);
                                }
                            }
                        }
                    }
                }
                LogUtility.log("Finished translating bean: " + bean.toString(), Level.FINE);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to translate bean to TO " + bean.toString(), ex);
            }
        }
        return resultTO;
    }

    /**
     * Translates a list of bean objects to a list of TO objects. 
     * @param <T> The type of TO class to translate to. Must extend AbstractTO. 
     * @param <S> The type of bean class to translate from. Must extend AbstractBindingBean 
     * @param beanList The list of bean objects to translate from.  
     * @param toClass The concrete class of the TO to translate to. e.g. PartyTO.class 
     * @return A list of TO objects or null. 
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