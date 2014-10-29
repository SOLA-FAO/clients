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
import org.sola.services.ejb.refdata.businesslogic.RefDataEJBLocal;
import org.sola.services.ejb.refdata.entities.ConfigPanelLauncher;
import org.sola.services.ejb.refdata.entities.RrrGroupType;
import org.sola.services.ejb.refdata.entities.RrrType;

/**
 * Contains methods and properties to manage {@link RrrType}
 */
@Named
@ViewScoped
public class RrrTypePageBean extends AbstractBackingBean {

    private RrrType rrrType;
    private List<RrrType> rrrTypes;
    private ConfigPanelLauncher[] configPanelLaunchers;
    private RrrGroupType[] rrrGroupTypes;
    
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

    public List<RrrType> getRrrTypes() {
        return rrrTypes;
    }

    public ConfigPanelLauncher[] getConfigPanelLaunchers() {
        return configPanelLaunchers;
    }

    public RrrGroupType[] getRrrGroupTypes() {
        return rrrGroupTypes;
    }

    public RrrType getRrrType() {
        return rrrType;
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
        // Load supporting lists
        List<ConfigPanelLauncher> configPanelLauncherList = refEjb.getCodeEntityList(ConfigPanelLauncher.class, languageBean.getLocale());
        List<RrrGroupType> rrrGroupTypesList = refEjb.getCodeEntityList(RrrGroupType.class, languageBean.getLocale());
        
        if (configPanelLauncherList != null) {
            // Add dummy
            ConfigPanelLauncher dummy = new ConfigPanelLauncher();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            configPanelLauncherList.add(0, dummy);
            configPanelLaunchers = configPanelLauncherList.toArray(new ConfigPanelLauncher[configPanelLauncherList.size()]);
        }
        
        if (rrrGroupTypesList != null) {
            // Add dummy
            RrrGroupType dummy = new RrrGroupType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            rrrGroupTypesList.add(0, dummy);
            rrrGroupTypes = rrrGroupTypesList.toArray(new RrrGroupType[rrrGroupTypesList.size()]);
        }
    }
    
    public String getPanelLauncherName(String code) {
        if (code != null && configPanelLaunchers != null) {
            for (ConfigPanelLauncher item : configPanelLaunchers) {
                if (item.getCode().equalsIgnoreCase(code)) {
                    return item.getDisplayValue();
                }
            }
        }
        return "";
    }
    
    public String getRrrGroupName(String code) {
        if (code != null && rrrGroupTypes != null) {
            for (RrrGroupType item : rrrGroupTypes) {
                if (item.getCode().equalsIgnoreCase(code)) {
                    return item.getDisplayValue();
                }
            }
        }
        return "";
    }
    
    private void loadList() {
        rrrTypes = refEjb.getCodeEntityList(RrrType.class, languageBean.getLocale());
    }
    
    public void loadEntity(String code) {
        if (StringUtility.isEmpty(code)) {
            try {
                rrrType = new RrrType();
                rrrType.setCode("");
            } catch (Exception ex) {
                LogUtility.log("Failed to instantiate reference data class", ex);
            }
        } else {
            rrrType = refEjb.getCodeEntity(RrrType.class, code, null);
        }

        localizedDisplayValues = new LocalizedValuesListBean(languageBean);
        localizedDescriptionValues = new LocalizedValuesListBean(languageBean);
        
        localizedDisplayValues.loadLocalizedValues(rrrType.getDisplayValue());
        localizedDescriptionValues.loadLocalizedValues(rrrType.getDescription());
    }

    public void deleteEntity(RrrType entity) {
        entity.setEntityAction(EntityAction.DELETE);
        refEjb.saveCode(entity);
        loadList();
    }

    public void saveEntity() throws Exception {
        if (rrrType != null) {
            // Validate
            String errors = "";
            if (StringUtility.isEmpty(rrrType.getCode())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_CODE) + "\r\n";
            }
            if (StringUtility.isEmpty(rrrType.getStatus())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_SELECT_STATUS) + "\r\n";
            }
            if (localizedDisplayValues.getLocalizedValues() == null || localizedDisplayValues.getLocalizedValues().size() < 1
                    || StringUtility.isEmpty(localizedDisplayValues.getLocalizedValues().get(0).getLocalizedValue())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_DISPLAY_VALUE) + "\r\n";
            }
            if (StringUtility.isEmpty(rrrType.getRrrGroupTypeCode())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_SELECT_RRR_GROUP) + "\r\n";
            }

            if (!errors.equals("")) {
                throw new Exception(errors);
            }

            rrrType.setDisplayValue(localizedDisplayValues.buildMultilingualString());
            rrrType.setDescription(localizedDescriptionValues.buildMultilingualString());

            refEjb.saveCode(rrrType);
            loadList();
        }
    }
}
