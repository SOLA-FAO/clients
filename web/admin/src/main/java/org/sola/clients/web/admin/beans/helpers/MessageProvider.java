package org.sola.clients.web.admin.beans.helpers;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sola.clients.web.admin.beans.language.LanguageBean;

/**
 * Provides methods to extract messages from the bundle files
 */
@Named
@SessionScoped
public class MessageProvider implements Serializable {

    private ResourceBundle msgBundle;
    private ResourceBundle errBundle;
    private final String MESSAGE_BUNDLE = "messages";
    private final String ERROR_BUNDLE = "errors";
    private String locale;
    
    @Inject
    LanguageBean langBean;
    
    public MessageProvider(){
    }
    
    /** 
     * Returns bundle with errors messages.
     * @return  
     */
    public ResourceBundle getErrorsBundle() {
        if (errBundle == null || locale == null || !locale.equalsIgnoreCase(langBean.getLocale())) {
            errBundle = getBundle(ERROR_BUNDLE);
         }
        return errBundle;
    }
    
    /** 
     * Returns bundle with general messages.
     * @return  
     */
    public ResourceBundle getMessagesBundle() {
        if (msgBundle == null || locale == null || !locale.equalsIgnoreCase(langBean.getLocale())) {
            msgBundle = getBundle(MESSAGE_BUNDLE);
        }
        return msgBundle;
    }

    /** 
     * Returns bundle by provided name.
     * @param name Bundle name
     * @return  
     */
    public ResourceBundle getBundle(String name) {
        locale = langBean.getLocale();
        String langCode = locale;
        String countryCode = locale;
        if (locale.indexOf("-") > -1){
            langCode = locale.substring(0, locale.indexOf("-"));
            countryCode = locale.substring(locale.lastIndexOf("-") + 1, locale.length());
        }
        return java.util.ResourceBundle.getBundle("org/sola/clients/web/admin/" + name, new Locale(langCode, countryCode)); 
    }
    
    /** 
     * Returns error message value of the provided bundle key.
     * @param key Bundle key
     * @return  
     */
    public String getErrorMessage(String key) {
        String result = null;
        try {
            result = getErrorsBundle().getString(key);
        } catch (MissingResourceException e) {
            result = "???" + key + "??? not found";
        }
        return result;
    }
    
    /** 
     * Returns message value of the provided bundle key.
     * @param key Bundle key
     * @return  
     */
    public String getMessage(String key) {
        String result = null;
        try {
            result = getMessagesBundle().getString(key);
        } catch (MissingResourceException e) {
            result = "???" + key + "??? not found";
        }
        return result;
    }
}
