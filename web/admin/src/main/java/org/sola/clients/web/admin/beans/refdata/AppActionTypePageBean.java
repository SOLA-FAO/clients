package org.sola.clients.web.admin.beans.refdata;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sola.clients.web.admin.beans.AbstractBackingBean;
import org.sola.clients.web.admin.beans.helpers.ErrorKeys;
import org.sola.clients.web.admin.beans.helpers.MessageBean;
import org.sola.clients.web.admin.beans.helpers.MessageProvider;
import org.sola.clients.web.admin.beans.language.LanguageBean;
import org.sola.clients.web.admin.beans.localization.LocalizedValuesListBean;
import org.sola.common.StringUtility;
import org.sola.common.logging.LogUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.common.repository.entities.AbstractCodeEntity;
import org.sola.services.ejb.refdata.businesslogic.RefDataEJBLocal;
import org.sola.services.ejb.refdata.entities.ApplicationActionType;
import org.sola.services.ejb.refdata.entities.ApplicationStatusType;

/**
 * Contains methods and properties to manage {@link ApplicationActionType}
 */
@Named
@ViewScoped
public class AppActionTypePageBean extends AbstractBackingBean {
    private ApplicationActionType appActionType;
    private List<ApplicationActionType> appActionTypeList;
    private ApplicationStatusType[] appStatuses;
    
    @Inject
    MessageProvider msgProvider;

    @Inject
    private LanguageBean languageBean;
    
    LocalizedValuesListBean localizedDisplayValues;
    LocalizedValuesListBean localizedDescriptionValues;

    @EJB
    RefDataEJBLocal refEjb;

    public ApplicationActionType getAppActionType() {
        return appActionType;
    }

    public void setAppActionType(ApplicationActionType appActionType) {
        this.appActionType = appActionType;
    }

    public List<ApplicationActionType> getAppActionTypeList() {
        return appActionTypeList;
    }

    public ApplicationStatusType[] getAppStatuses() {
        return appStatuses;
    }

    public LocalizedValuesListBean getLocalizedDisplayValues() {
        return localizedDisplayValues;
    }

    public LocalizedValuesListBean getLocalizedDescriptionValues() {
        return localizedDescriptionValues;
    }
    
    @PostConstruct
    private void init() {
        loadList();
        // Load application statuses
        List<ApplicationStatusType> appStatusesList = refEjb.getCodeEntityList(ApplicationStatusType.class, languageBean.getLocale());
        if(appStatusesList != null){
            // Add dummy
            ApplicationStatusType dummy = new ApplicationStatusType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            appStatusesList.add(0, dummy);
            appStatuses = appStatusesList.toArray(new ApplicationStatusType[appStatusesList.size()]);
        }
    }
    
    public String getAppStatus(String code){
        if(code != null && appStatuses != null){
            for(ApplicationStatusType status : appStatuses){
                if(status.getCode().equalsIgnoreCase(code)){
                    return status.getDisplayValue();
                }
            }
        }
        return "";
    }
    
    private void loadList() {
        appActionTypeList = refEjb.getCodeEntityList(ApplicationActionType.class, languageBean.getLocale());
    }
    
    public void loadEntity(String code) {
        if (StringUtility.isEmpty(code)) {
            try {
                appActionType = new ApplicationActionType();
                appActionType.setCode("");
            } catch (Exception ex) {
                LogUtility.log("Failed to instantiate reference data class", ex);
            }
        } else {
            appActionType = refEjb.getCodeEntity(ApplicationActionType.class, code, null);
        }

        localizedDisplayValues = new LocalizedValuesListBean(languageBean);
        localizedDescriptionValues = new LocalizedValuesListBean(languageBean);
        
        localizedDisplayValues.loadLocalizedValues(appActionType.getDisplayValue());
        localizedDescriptionValues.loadLocalizedValues(appActionType.getDescription());
    }

    public void deleteEntity(AbstractCodeEntity entity) {
        entity.setEntityAction(EntityAction.DELETE);
        refEjb.saveCode(entity);
        loadList();
    }

    public void saveEntity() throws Exception {
        if (appActionType != null) {
            // Validate
            String errors = "";
            if (StringUtility.isEmpty(appActionType.getCode())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_CODE) + "\r\n";
            }
            if (StringUtility.isEmpty(appActionType.getStatus())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_SELECT_STATUS) + "\r\n";
            }
            if (localizedDisplayValues.getLocalizedValues() == null || localizedDisplayValues.getLocalizedValues().size() < 1
                    || StringUtility.isEmpty(localizedDisplayValues.getLocalizedValues().get(0).getLocalizedValue())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_DISPLAY_VALUE) + "\r\n";
            }
            if (StringUtility.isEmpty(appActionType.getStatusToSet())) {
                appActionType.setStatusToSet(null);
            }
            
            if (!errors.equals("")) {
                throw new Exception(errors);
            }

            appActionType.setDisplayValue(localizedDisplayValues.buildMultilingualString());
            appActionType.setDescription(localizedDescriptionValues.buildMultilingualString());
            refEjb.saveCode(appActionType);
            loadList();
        }
    }
}
