package org.sola.clients.web.admin.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.EJBAccessException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.sola.common.DynamicFormException;
import org.sola.common.SOLAException;
import org.sola.common.SOLANoDataException;
import org.sola.common.StringUtility;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.MessageUtility;
import org.sola.common.messaging.ServiceMessage;
import org.sola.services.common.EntityAction;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.faults.FaultUtility;
import static org.sola.services.common.faults.FaultUtility.getCause;
import org.sola.services.common.faults.OptimisticLockingFault;
import org.sola.services.common.faults.SOLAAccessFault;
import org.sola.services.common.faults.SOLAFault;
import org.sola.services.common.faults.SOLAObjectExistsException;
import org.sola.services.common.faults.SOLAValidationException;
import org.sola.services.common.faults.SOLAValidationFault;
import org.sola.services.common.repository.entities.AbstractEntity;

/**
 * Abstract class for backing beans
 */
public abstract class AbstractBackingBean implements Serializable {
    /**
     * Returns {@link FacesContext} object
     */
    public FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

    /**
     * Returns faces external context.
     */
    public ExternalContext getExtContext() {
        return getContext().getExternalContext();
    }

    /**
     * Returns JSF implementation version.
     */
    public String getJsfVersion() {
        return FacesContext.class.getPackage().getImplementationVersion();
    }

    /**
     * Returns current user name.
     */
    public String getUserName() {
        return getExtContext().getRemoteUser();
    }

    /**
     * Checks if user belongs to the role
     *
     * @param roleName Role name to check
     */
    public boolean isInRole(String roleName) {
        return getExtContext().isUserInRole(roleName);
    }

    /**
     * Returns {@link HttpServletRequest} object object
     */
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) getContext().getExternalContext().getRequest();
    }

    /**
     * Returns request parameter value
     *
     * @param paramName Parameter name
     * @return
     */
    public String getRequestParam(String paramName) {
        Map<String, String> parameterMap = (Map<String, String>) getExtContext().getRequestParameterMap();
        if (!StringUtility.isEmpty(parameterMap.get(paramName))) {
            return parameterMap.get(paramName);
        }
        return "";
    }

    /**
     * Returns application URL
     */
    public String getApplicationUrl() {
        HttpServletRequest r = getRequest();
        return r.getRequestURL().substring(0, r.getRequestURL().length() - r.getRequestURI().length());
    }

    /**
     * Returns {@link HttpSession} object object
     */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * Holds a reference to the UserTransction. Injected using @Resource
     */
    @Resource
    private UserTransaction tx;

    public AbstractBackingBean() {

    }

    /**
     * Starts a transaction.
     *
     * @throws Exception
     */
    protected void beginTransaction() throws Exception {
        tx.begin();
    }

    /**
     * Commits a transaction as long as the transaction is not in the
     * NO_TRANSACTION state.
     *
     * @throws Exception
     */
    protected void commitTransaction() throws Exception {
        if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
            tx.commit();
        }
    }

    /**
     * Rolls back the transaction as long as the transaction is not in the
     * NO_TRANSACTION state. This method should be called in the finally clause
     * wherever a transaction is started.
     *
     * @throws Exception
     */
    protected void rollbackTransaction() throws Exception {
        if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
            tx.rollback();
        }
    }

    /**
     * Provides common fault handling and transaction functionality for secured
     * web methods that do not require access privileges.
     *
     * @param runnable Anonymous inner class that implements the
     * {@linkplain java.lang.Runnable Runnable} interface
     * @throws Exception
     */
    protected void runOpenQuery(Runnable runnable) throws Exception {
        try {
            LocalInfo.setUserName(getUserName());
            beginTransaction();
            runnable.run();
            commitTransaction();
        } finally {
            rollbackTransaction();
            cleanUp();
        }
    }

    /**
     * Provides common fault handling and transaction functionality for secured
     * web methods that do not perform data updates but require access
     * privileges.
     *
     * @param runnable Anonymous inner class that implements the
     * {@linkplain java.lang.Runnable Runnable} interface
     * @throws Exception
     */
    protected void runGeneralQuery(Runnable runnable) throws Exception {
        try {
            LocalInfo.setUserName(getUserName());
            beginTransaction();
            runnable.run();
            commitTransaction();
        } finally {
            rollbackTransaction();
            cleanUp();
        }
    }

    /**
     * Provides common fault handling and transaction functionality for secured
     * web methods that perform data updates but do not perform validation.
     *
     * @param runnable Anonymous inner class that implements the
     * {@linkplain java.lang.Runnable Runnable} interface
     * @throws Exception
     */
    protected void runUpdate(Runnable runnable) throws Exception {
        try {
            LocalInfo.setUserName(getUserName());
            beginTransaction();
            runnable.run();
            commitTransaction();
        } finally {
            rollbackTransaction();
            cleanUp();
        }
    }

    /**
     * Provides common fault handling and transaction functionality for secured
     * web methods that perform data updates as well as data validation.
     *
     * @param runnable Anonymous inner class that implements the
     * {@linkplain java.lang.Runnable Runnable} interface
     * @throws Exception
     */
    protected void runUpdateValidation(Runnable runnable) throws Exception {
        try {
            LocalInfo.setUserName(getUserName());
            beginTransaction();
            runnable.run();
            commitTransaction();
        } finally {
            rollbackTransaction();
            cleanUp();
        }
    }

    public boolean isPostback() {
        return getContext().isPostback();
    }

    /**
     * Returns entity list size excluding deleted or disassociated
     */
    public <T extends AbstractEntity> int getEntityListSize(List<T> entityList) {
        int cnt = 0;
        for (AbstractEntity entity : entityList) {
            if (entity.getEntityAction() == null || (!entity.getEntityAction().equals(EntityAction.DELETE) && !entity.getEntityAction().equals(EntityAction.DISASSOCIATE))) {
                cnt += 1;
            }
        }
        return cnt;
    }

    /**
     * Performs clean up actions after the web method logic has been executed.
     */
    protected void cleanUp() {
        LocalInfo.remove();
    }

    /**
     * Process exception and returns back in appropriate format.
     *
     * @param t Exception to handle
     * @return
     */
    protected Exception processException(Exception t, String localeCode) {
        String stackTraceAsStr = FaultUtility.getStackTraceAsString(t);

        try {
            String msg = "SOLA OT FaultId = "
                    + FaultUtility.createFaultId(getRequest().getRemoteUser())
                    + System.getProperty("line.separator")
                    + stackTraceAsStr;
            LogUtility.log(msg, Level.SEVERE);
        } catch (Exception logEx) {
            return new Exception(MessageUtility.getLocalizedMessageText(ServiceMessage.EXCEPTION_FAILED_LOGGING, localeCode));
        }

        try {
            // Identify the type of exception and raise the appropriate Service Fault
            if (FaultUtility.hasCause(t, SOLAObjectExistsException.class)) {
                return new Exception(MessageUtility.getLocalizedMessageText(
                        FaultUtility.getCause(t, SOLAObjectExistsException.class).getMessage(), localeCode));
            } else if (FaultUtility.hasCause(t, SOLANoDataException.class)) {
                SOLANoDataException ex = FaultUtility.getCause(t, SOLANoDataException.class);
                return new Exception(MessageUtility.getLocalizedMessageText(ex.getMessage(), localeCode));
            } else if (FaultUtility.hasCause(t, DynamicFormException.class)) {
                return getCause(t, DynamicFormException.class);
            } else if (FaultUtility.hasCause(t, SOLAValidationException.class) || t.getClass() == SOLAValidationFault.class) {
                // TODO: Improve handling of validation
                return new Exception(MessageUtility.getLocalizedMessageText(ServiceMessage.RULE_VALIDATION_FAILED, localeCode));
            } else if (FaultUtility.hasCause(t, EJBAccessException.class) || t.getClass() == SOLAAccessFault.class) {
                return new Exception(MessageUtility.getLocalizedMessageText(ServiceMessage.EXCEPTION_INSUFFICIENT_RIGHTS, localeCode));
            } else if (FaultUtility.isOptimisticLocking(t, stackTraceAsStr) || t.getClass() == OptimisticLockingFault.class) {
                // Optimistic locking exception
                return new Exception(MessageUtility.getLocalizedMessageText(ServiceMessage.GENERAL_OPTIMISTIC_LOCK, localeCode));
            } else if (FaultUtility.hasCause(t, SOLAException.class)) {
                SOLAException ex = getCause(t, SOLAException.class);
                Object[] msgParms = ex.getMessageParameters();
                return new Exception(MessageUtility.getLocalizedMessageText(ex.getMessage(), localeCode, msgParms));
            } else if (t.getClass() == SOLAFault.class) {
                SOLAFault f = (SOLAFault) t;
                Object[] msgParms = null;
                if (f.getFaultInfo().getMessageParameters() != null) {
                    msgParms = f.getFaultInfo().getMessageParameters().toArray();
                }
                return new Exception(MessageUtility.getLocalizedMessageText(f.getFaultInfo().getMessageCode(), localeCode, msgParms));
            } else {
                // Unhandled Exception.
                return (Exception)getInitialException(t);
            }
        } catch (Exception formatEx) {
            // Catch all in case the format throws an exception. Note that the
            // exception details in the log will not match the details of the
            // format exception (i.e. the one in the log will be the original
            // exception).      
            return new Exception(MessageUtility.getLocalizedMessageText(ServiceMessage.EXCEPTION_FAILED_FORMATTING, localeCode));
        }
    }
    
    public static Throwable getInitialException(Throwable t) {
        if(t.getCause() != null){
            return getInitialException(t.getCause());
        } else {
            return t;
        }
    }
}
