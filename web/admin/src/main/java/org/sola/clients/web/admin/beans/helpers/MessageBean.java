package org.sola.clients.web.admin.beans.helpers;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Used to display messages
 */
@Named
@RequestScoped
public class MessageBean{

    public static enum MESSAGE_TYPE{ERROR, WARNING, SUCCESS}

    private String message = "";
    private String m;
    private MESSAGE_TYPE type = MESSAGE_TYPE.SUCCESS;

    public String getMessage() {
        return message;
    }
    
    public boolean isError(){
        return type == MESSAGE_TYPE.ERROR;
    }

    public boolean isSuccess(){
        return type == MESSAGE_TYPE.SUCCESS;
    }
    
    public boolean isWarning(){
        return type == MESSAGE_TYPE.WARNING;
    }
    
    public void setMessage(String message, MESSAGE_TYPE type) {
        this.message = message;
        this.type = type;
    }
    
    public void setSuccessMessage(String message) {
        setMessage(message, MESSAGE_TYPE.SUCCESS);
    }
    
    public void setErrorMessage(String message) {
        setMessage(message, MESSAGE_TYPE.ERROR);
    }
    
    public void setWarningMessage(String message) {
        setMessage(message, MESSAGE_TYPE.WARNING);
    }
}
