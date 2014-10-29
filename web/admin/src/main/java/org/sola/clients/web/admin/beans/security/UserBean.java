package org.sola.clients.web.admin.beans.security;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.User;

/**
 * Wrapper user bean, exposing properties of
 * {@link org.sola.services.ejbs.admin.businesslogic.repository.entities.User} 
 * and some other supplementary properties 
 */
@Named
@RequestScoped
public class UserBean implements Serializable {

    private User user;
    private String passwordRepeat;
    private String oldPassword;

    /** returns userName property name */
    public String getPropertyUserName(){
        return "userName";
    }
    
    /** returns firstName property name */
    public String getPropertyFirstName(){
        return "firstName";
    }
    
    /** returns lastName property name */
    public String getPropertyLastName(){
        return "lastName";
    }
    
    /** returns mobileNumber property name */
    public String getPropertyMobilNumber(){
        return "mobileNumber";
    }
    
    /** returns password property name */
    public String getPropertyPassword(){
        return "password";
    }
    
    /** returns passwordRepeat property name */
    public String getPropertyPasswordRepeat(){
        return "passwordRepeat";
    }
    
    /** returns oldPassword property name */
    public String getPropertyOldPassword(){
        return "oldPassword";
    }
    
    /** returns email property name */
    public String getPropertyEmail(){
        return "email";
    }
    
    /** returns description property name */
    public String getPropertyDescription(){
        return "description";
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getUserName() {
        return user.getUserName();
    }

    public void setUserName(String userName){
        user.setUserName(userName);
    }
    
    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(String password){
        user.setPassword(password);
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    public String getFirstName() {
        return user.getFirstName();
    }

    public void setFirstName(String firstName){
        user.setFirstName(firstName);
    }
    
    public String getLastName() {
        return user.getLastName();
    }

    public void setLastName(String lastName){
        user.setLastName(lastName);
    }
    
    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email){
        user.setEmail(email);
    }
    
    public String getActivationCode() {
        return user.getActivationCode();
    }

    public void setActivationCode(String code){
        user.setActivationCode(code);
    }
    
    public String getMobileNumber(){
        return user.getMobileNumber();
    }
    
    public void setMobileNumber(String number){
        user.setMobileNumber(number);
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }
    
    public String getDescription() {
        return user.getDescription();
    }

    public void setDescription(String description) {
        user.setDescription(description);
    }
    
    public UserBean() {
        user = new User();
    }
    
    public UserBean(User user) {
        this.user = user;
    }
}
