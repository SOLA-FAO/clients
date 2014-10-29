package org.sola.clients.web.admin.beans.language;

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
import org.sola.clients.web.admin.beans.localization.LocalizedValuesListBean;
import org.sola.common.StringUtility;
import org.sola.common.logging.LogUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.ejb.refdata.businesslogic.RefDataEJBLocal;
import org.sola.services.ejb.refdata.entities.Language;

/**
 * Contains methods and properties to manage {@link Language}
 */
@Named
@ViewScoped
public class LanguagePageBean extends AbstractBackingBean {
    private Language language;
    private List<Language> languages;
    
    @Inject
    MessageBean msg;

    @Inject
    MessageProvider msgProvider;

    @Inject
    private LanguageBean languageBean;
    
    LocalizedValuesListBean localizedDisplayValues;

    @EJB
    RefDataEJBLocal refEjb;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public LocalizedValuesListBean getLocalizedDisplayValues() {
        return localizedDisplayValues;
    }

    public void setLocalizedDisplayValues(LocalizedValuesListBean localizedDisplayValues) {
        this.localizedDisplayValues = localizedDisplayValues;
    }
    
    @PostConstruct
    private void init() {
        loadList();
    }
    
    private void loadList() {
        languages = refEjb.getLanguages(languageBean.getLocale());
    }
    
    public void loadEntity(String code) {
        if (StringUtility.isEmpty(code)) {
            try {
                language = new Language();
                language.setCode("");
            } catch (Exception ex) {
                LogUtility.log("Failed to instantiate language class", ex);
            }
        } else {
            language = refEjb.getLanguage(code, null);
        }
        localizedDisplayValues = new LocalizedValuesListBean(languageBean);
        localizedDisplayValues.loadLocalizedValues(language.getDisplayValue());
    }

    public void deleteEntity(Language entity) {
        entity.setEntityAction(EntityAction.DELETE);
        refEjb.saveLanguage(entity);
        languageBean.loadLanguages();
        loadList();
    }

    public void saveEntity() throws Exception {
        if (language != null) {
            // Validate
            String errors = "";
            if (StringUtility.isEmpty(language.getCode())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_CODE) + "\r\n";
            }
            if (localizedDisplayValues.getLocalizedValues() == null || localizedDisplayValues.getLocalizedValues().size() < 1
                    || StringUtility.isEmpty(localizedDisplayValues.getLocalizedValues().get(0).getLocalizedValue())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_DISPLAY_VALUE) + "\r\n";
            }
            
            if (!errors.equals("")) {
                throw new Exception(errors);
            }

            language.setDisplayValue(localizedDisplayValues.buildMultilingualString());
            refEjb.saveLanguage(language);
            languageBean.loadLanguages();
            loadList();
        }
    }
}
