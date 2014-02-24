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
package org.sola.clients.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.sola.clients.beans.controls.ExtendedList;
import org.sola.clients.beans.validation.ValidatorFactory;
import org.sola.common.mapping.MappingManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Abstract bean, which is used as a basic class for all beans supporting
 * binding.
 */
public abstract class AbstractBindingBean implements Serializable {

    public final static String ENTITY_ACTION_PROPERTY = "entityAction";
    protected transient final PropertyChangeSupport propertySupport;
    private EntityAction entityAction;
    private transient String stateHash;

    public AbstractBindingBean() {
        propertySupport = new PropertyChangeSupport(this);
    }

    public EntityAction getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(EntityAction entityAction) {
        EntityAction oldValue = this.entityAction;
        this.entityAction = entityAction;
        propertySupport.firePropertyChange(ENTITY_ACTION_PROPERTY, oldValue, entityAction);
    }

    /**
     * Registers property change listener on the bean.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes property change listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * Validates current object instance.
     *
     * @param showMessage Boolean value indicating whether to display message in
     * case of validation violations.
     * @param group List of validation groups.
     */
    public <T extends AbstractBindingBean> Set<ConstraintViolation<T>> validate(boolean showMessage, Class<?>... group) {
        // Don't call validation for deleted entity
//        if(this.getEntityAction()!=null && this.getEntityAction() == EntityAction.DELETE){
//            return new HashSet<ConstraintViolation<T>>();
//        }
        
        T bean = (T) this;
        Set<ConstraintViolation<T>> warningsList = ValidatorFactory.getInstance().getValidator().validate(bean, group);

        if (showMessage) {
            showMessage(warningsList);
        }
        return warningsList;
    }

    /**
     * Updates bean in the list if it already exists or adds it to the list if
     * it is not.
     *
     * @param bean Bean to update/add
     * @param list List holding beans
     * @param addIfNotExists Boolean flag indicating whether to add bean into
     * the list if it doesn't exist.
     */
    public <T extends AbstractBindingBean> boolean updateListItem(
            T bean, List<T> list, boolean addIfNotExists) {

        int i = list.indexOf(bean);

        if (i > -1) {
            list.set(i, bean);
            return true;
        }

        // If bean is new
        if (addIfNotExists) {
            if (ExtendedList.class.isAssignableFrom(list.getClass())) {
                ((ExtendedList) list).addAsNew(bean);
            } else {
                list.add(bean);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Makes a copy of current object instance.
     */
    public <T extends AbstractBindingBean> T copy() {
        return (T) MappingManager.getMapper().map(this, this.getClass());
    }

    /**
     * Copies provided bean over the current object instance.
     *
     * @param sourceObject Object to copy from.
     */
    public <T extends AbstractBindingBean> void copyFromObject(T sourceObject) {
        if (sourceObject != null) {
            MappingManager.getMapper().map(sourceObject, this);
        }
    }

    /**
     * Shows validation message.
     *
     * @param warningsList The list of validation violations to show on the
     * message.
     */
    public <T extends AbstractBindingBean> void showMessage(Set<ConstraintViolation<T>> warningsList) {
        if (warningsList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (Iterator<ConstraintViolation<T>> it = warningsList.iterator(); it.hasNext();) {
                ConstraintViolation<T> constraintViolation = it.next();
                stringBuilder.append(String.format("- %s\n", constraintViolation.getMessage()));
            }
            MessageUtility.displayMessage(ClientMessage.GENERAL_BEAN_VALIDATION,
                    new Object[]{stringBuilder.toString()});
        }
    }

    /**
     * Updates reference data bean by copying from provided object.
     *
     * @param refDataBean Bean object to update.
     * @param newRefDataBean Provided bean object to update from.
     * @param property Name of the property to use in the property change event.
     */
    protected <T extends AbstractCodeBean> void setJointRefDataBean(
            T refDataBean, T newRefDataBean, String property) {

        if (newRefDataBean == null) {
            refDataBean.setCode(null);
            refDataBean.setDescription(null);
            refDataBean.setDisplayValue(null);
            refDataBean.setEntityAction(null);
            refDataBean.setStatus("");
        } else {
            refDataBean.copyFromObject(newRefDataBean);
        }
        propertySupport.firePropertyChange(property, null, refDataBean);
    }

    /** 
     * Compares current object state with saved state. If they are different, returns true. 
     * @see AbstractBindingBean#saveStateHash() 
     */
    public boolean hasChanges() throws IOException, NoSuchAlgorithmException{
        String newState = getCheckSum();
        if(stateHash == null || !stateHash.equals(newState)){
            return true;
        }else{
            return false;
        }
    }
    
    /** 
     * Generates hash of the object and saves it locally. It might be used to 
     * compare this value, later after doing changes on the object.
     * @see AbstractBindingBean#getCheckSum()  
     * @see AbstractBindingBean#hasChanges()  
     */
    public void saveStateHash() throws IOException, NoSuchAlgorithmException{
        stateHash = getCheckSum();
    }
    
    /** 
     * Returns previously saved object state hash. 
     * @see AbstractBindingBean#saveStateHash() 
     */
    public String getSavedHash(){
        return stateHash;
    }
    
    /** Calculates hash for this object and child objects. */
    public String getCheckSum() throws IOException, NoSuchAlgorithmException {
        String hashString;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;

        oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();

        MessageDigest m;
        m = MessageDigest.getInstance("SHA1");
        byte[] bytes = baos.toByteArray();
        m.update(bytes);
        hashString = (new BigInteger(1, m.digest())).toString(16);
        System.out.println("Bean="+ bytes.toString()); 
        return hashString;
    }
    
}
