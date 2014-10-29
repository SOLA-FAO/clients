package org.sola.clients.web.admin.beans.security;

import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.inject.Inject;
import javax.inject.Named;
import org.sola.clients.web.admin.beans.helpers.ErrorKeys;
import org.sola.clients.web.admin.beans.helpers.MessageProvider;
import org.sola.services.ejbs.admin.businesslogic.AdminEJBLocal;

@Named
@RequestScoped
public class LoginBean implements Serializable {

    @Inject
    MessageProvider msgProvider;
    
    @Inject
    ActiveUserBean activeUserBean;
    
    @EJB
    AdminEJBLocal admin;
       
    public ActiveUserBean getActiveUserBean(){
        return activeUserBean;
    }
     
    public void setActiveUserBean(ActiveUserBean activeUser){
        this.activeUserBean = activeUser;
    }
     
    public void login() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        if(!validate(context, true)){
            return;
        }
        
        try {
            request.login(activeUserBean.getUserName(), activeUserBean.getPassword());
            // Check user is active
            if(!admin.isUserActive(activeUserBean.getUserName())){
                // Log out user
                request.logout();
                context.addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.LOGIN_ACCOUNT_BLOCKED)));
                return;
            }
            // Check user has administrative rights
            if(!admin.isUserAdmin()){
                // Log out user
                request.logout();
                context.addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.LOGIN_NO_ADMIN_RIGHTS)));
                return;
            }
             
            // Redirect
            context.getExternalContext().redirect(request.getContextPath() + "/index.xhtml");
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.LOGIN_FAILED)));
        }
    }
    
    private boolean validate(FacesContext context, boolean showMessage){
        boolean isValid = true;
        
        // TODO: ADD USER NAME AND PASSWORD VALIDATION
        
        return isValid;
    }
    
    public void logout() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
            // Redirect
            context.getExternalContext().redirect(request.getContextPath() + "/login.xhtml");
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.LOGIN_LOGOUT_FAILED)));
        }
    }
}
