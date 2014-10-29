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
import org.sola.services.ejb.refdata.entities.SourceType;

/**
 * Contains methods and properties to manage {@link SourceType}
 */
@Named
@ViewScoped
public class SourceTypePageBean extends AbstractBackingBean {
    private SourceType sourceType;
    private List<SourceType> sourceTypeList;
    
    @Inject
    MessageBean msg;

    @Inject
    MessageProvider msgProvider;

    @Inject
    private LanguageBean languageBean;
    
    LocalizedValuesListBean localizedDisplayValues;
    LocalizedValuesListBean localizedDescriptionValues;

    @EJB
    RefDataEJBLocal refEjb;

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public List<SourceType> getSourceTypeList() {
        return sourceTypeList;
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
    }
    
    private void loadList() {
        sourceTypeList = refEjb.getCodeEntityList(SourceType.class, languageBean.getLocale());
    }
    
    public void loadEntity(String code) {
        if (StringUtility.isEmpty(code)) {
            try {
                sourceType = new SourceType();
                sourceType.setCode("");
            } catch (Exception ex) {
                LogUtility.log("Failed to instantiate reference data class", ex);
            }
        } else {
            sourceType = refEjb.getCodeEntity(SourceType.class, code, null);
        }

        localizedDisplayValues = new LocalizedValuesListBean(languageBean);
        localizedDescriptionValues = new LocalizedValuesListBean(languageBean);
        
        localizedDisplayValues.loadLocalizedValues(sourceType.getDisplayValue());
        localizedDescriptionValues.loadLocalizedValues(sourceType.getDescription());
    }

    public void deleteEntity(AbstractCodeEntity entity) {
        entity.setEntityAction(EntityAction.DELETE);
        refEjb.saveCode(entity);
        loadList();
    }

    public void saveEntity() throws Exception {
        if (sourceType != null) {
            // Validate
            String errors = "";
            if (StringUtility.isEmpty(sourceType.getCode())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_CODE) + "\r\n";
            }
            if (StringUtility.isEmpty(sourceType.getStatus())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_SELECT_STATUS) + "\r\n";
            }
            if (localizedDisplayValues.getLocalizedValues() == null || localizedDisplayValues.getLocalizedValues().size() < 1
                    || StringUtility.isEmpty(localizedDisplayValues.getLocalizedValues().get(0).getLocalizedValue())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_DISPLAY_VALUE) + "\r\n";
            }
                        
            if (!errors.equals("")) {
                throw new Exception(errors);
            }

            sourceType.setDisplayValue(localizedDisplayValues.buildMultilingualString());
            sourceType.setDescription(localizedDescriptionValues.buildMultilingualString());
            refEjb.saveCode(sourceType);
            loadList();
        }
    }
}
