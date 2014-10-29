package org.sola.clients.web.admin.beans.setting;

import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sola.clients.web.admin.beans.AbstractBackingBean;
import org.sola.clients.web.admin.beans.helpers.ErrorKeys;
import org.sola.clients.web.admin.beans.helpers.MessageBean;
import org.sola.clients.web.admin.beans.helpers.MessageProvider;
import org.sola.clients.web.admin.beans.helpers.MessagesKeys;
import org.sola.clients.web.admin.beans.language.LanguageBean;
import org.sola.common.ConfigConstants;
import org.sola.common.StringUtility;
import org.sola.services.ejb.search.businesslogic.SearchEJBLocal;
import org.sola.services.ejb.search.repository.entities.ConfigMapLayer;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.Setting;

/**
 * Contains methods and properties to manage {@link Setting}
 */
@Named
@ViewScoped
public class CommunityAreaPageBean extends AbstractBackingBean {

    @EJB
    SearchEJBLocal searchEjb;

    @Inject
    LanguageBean langBean;

    @Inject
    MessageProvider msgProvider;

    @EJB
    SystemEJBLocal systemEjb;
    
    @Inject
    MessageBean msgBean;

    private List<ConfigMapLayer> layers;
    private Setting communityArea;

    public Setting getCommunityArea() {
        return communityArea;
    }

    public List<ConfigMapLayer> getLayers() {
        return layers;
    }

    public ConfigMapLayer[] getLayersArray() {
        if (layers == null) {
            return null;
        }
        return layers.toArray(new ConfigMapLayer[layers.size()]);
    }

    public void init() {
        String action = getRequestParam("action");
        if (action.equalsIgnoreCase("saved")) {
            msgBean.setSuccessMessage(msgProvider.getMessage(MessagesKeys.COMMUNITY_AREA_PAGE_SAVED_SUCCESS));
        }
        layers = searchEjb.getConfigMapLayerList(langBean.getLocale());
        communityArea = systemEjb.getSetting(ConfigConstants.OT_COMMUNITY_AREA);
    }

    public void save() {
        if (communityArea == null || StringUtility.isEmpty(communityArea.getValue())) {
            getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.MAP_CONTROL_PROVIDE_COMMUNITY_AREA)));
            return;
        }
        systemEjb.saveSetting(communityArea);
        try {
            getContext().getExternalContext().redirect(getRequest().getContextPath() + "/settings/CommunityArea.xhtml?action=saved");
        } catch (Exception ex) {
            getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.GENERAL_REDIRECT_FAILED)));
        }
    }
}
