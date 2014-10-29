package org.sola.clients.web.admin.beans.security;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.sola.services.ejbs.admin.businesslogic.AdminEJBLocal;

/**
 * Stores user information
 */
@SessionScoped
@Named
public class ActiveUserBean implements Serializable {

    @EJB
    AdminEJBLocal adminEjb;
    
    private String username;
    private String password;
    
    public String getUserName() {
        return this.username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isAuthenticated(){
        String userName = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
        return userName != null && !userName.equals("");
    }
 
    /**
     * Creates a new instance of ActiveUser bean
     */
    public ActiveUserBean() {
    }

    public boolean getHasAdminRights(){
        return adminEjb.isUserAdmin();
    }
}
