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
import org.sola.services.ejb.refdata.entities.ConfigPanelLauncher;
import org.sola.services.ejb.refdata.entities.PanelLauncherGroup;

/**
 * Contains methods and properties to manage {@link ConfigPanelLauncher}
 */
@Named
@ViewScoped
public class PanelLauncherPageBean extends AbstractBackingBean {
    private ConfigPanelLauncher panelLauncher;
    private List<ConfigPanelLauncher> panelLauncherList;
    private PanelLauncherGroup[] panelLauncherGroups;
    
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

    public ConfigPanelLauncher getPanelLauncher() {
        return panelLauncher;
    }

    public void setPanelLauncher(ConfigPanelLauncher panelLauncher) {
        this.panelLauncher = panelLauncher;
    }

    public List<ConfigPanelLauncher> getPanelLauncherList() {
        return panelLauncherList;
    }

    public PanelLauncherGroup[] getPanelLauncherGroups() {
        return panelLauncherGroups;
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
        // Load panel launcher groups
        List<PanelLauncherGroup> panleLauncherGroupsList = refEjb.getCodeEntityList(PanelLauncherGroup.class, languageBean.getLocale());
        if(panleLauncherGroupsList != null){
            // Add dummy
            PanelLauncherGroup dummy = new PanelLauncherGroup();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            panleLauncherGroupsList.add(0, dummy);
            panelLauncherGroups = panleLauncherGroupsList.toArray(new PanelLauncherGroup[panleLauncherGroupsList.size()]);
        }
    }
    
    public String getGroupName(String code){
        if(code != null && panelLauncherGroups != null){
            for(PanelLauncherGroup status : panelLauncherGroups){
                if(status.getCode().equalsIgnoreCase(code)){
                    return status.getDisplayValue();
                }
            }
        }
        return "";
    }
    
    private void loadList() {
        panelLauncherList = refEjb.getCodeEntityList(ConfigPanelLauncher.class, languageBean.getLocale());
    }
    
    public void loadEntity(String code) {
        if (StringUtility.isEmpty(code)) {
            try {
                panelLauncher = new ConfigPanelLauncher();
                panelLauncher.setCode("");
            } catch (Exception ex) {
                LogUtility.log("Failed to instantiate reference data class", ex);
            }
        } else {
            panelLauncher = refEjb.getCodeEntity(ConfigPanelLauncher.class, code, null);
        }

        localizedDisplayValues = new LocalizedValuesListBean(languageBean);
        localizedDescriptionValues = new LocalizedValuesListBean(languageBean);
        
        localizedDisplayValues.loadLocalizedValues(panelLauncher.getDisplayValue());
        localizedDescriptionValues.loadLocalizedValues(panelLauncher.getDescription());
    }

    public void deleteEntity(AbstractCodeEntity entity) {
        entity.setEntityAction(EntityAction.DELETE);
        refEjb.saveCode(entity);
        loadList();
    }

    public void saveEntity() throws Exception {
        if (panelLauncher != null) {
            // Validate
            String errors = "";
            if (StringUtility.isEmpty(panelLauncher.getCode())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_CODE) + "\r\n";
            }
            if (StringUtility.isEmpty(panelLauncher.getStatus())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_SELECT_STATUS) + "\r\n";
            }
            if (localizedDisplayValues.getLocalizedValues() == null || localizedDisplayValues.getLocalizedValues().size() < 1
                    || StringUtility.isEmpty(localizedDisplayValues.getLocalizedValues().get(0).getLocalizedValue())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_FILL_DISPLAY_VALUE) + "\r\n";
            }
            if (StringUtility.isEmpty(panelLauncher.getLaunchGroupCode())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.REFDATA_PAGE_SELECT_PANEL_LAUNCHER_GROUP) + "\r\n";
            }
            
            if (!errors.equals("")) {
                throw new Exception(errors);
            }

            panelLauncher.setDisplayValue(localizedDisplayValues.buildMultilingualString());
            panelLauncher.setDescription(localizedDescriptionValues.buildMultilingualString());
            refEjb.saveCode(panelLauncher);
            loadList();
        }
    }
}