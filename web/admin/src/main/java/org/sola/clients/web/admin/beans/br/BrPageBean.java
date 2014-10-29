package org.sola.clients.web.admin.beans.br;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
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
import org.sola.clients.web.admin.beans.localization.LocalizedValuesListBean;
import org.sola.common.StringUtility;
import org.sola.common.mapping.MappingManager;
import org.sola.services.common.EntityAction;
import org.sola.services.common.repository.entities.AbstractCodeEntity;
import org.sola.services.ejb.refdata.businesslogic.RefDataEJBLocal;
import org.sola.services.ejb.refdata.entities.ApplicationActionType;
import org.sola.services.ejb.refdata.entities.BrSeverityType;
import org.sola.services.ejb.refdata.entities.BrTechnicalType;
import org.sola.services.ejb.refdata.entities.BrValidationTargetType;
import org.sola.services.ejb.refdata.entities.RegistrationStatusType;
import org.sola.services.ejb.refdata.entities.RequestType;
import org.sola.services.ejb.refdata.entities.RrrType;
import org.sola.services.ejb.refdata.entities.ServiceActionType;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.Br;
import org.sola.services.ejb.system.repository.entities.BrDefinition;
import org.sola.services.ejb.system.repository.entities.BrValidation;

/**
 * Contains methods and properties to manage {@link Br}
 */
@Named
@ViewScoped
public class BrPageBean extends AbstractBackingBean {

    private Br br;
    private BrTechnicalType[] technicalTypes;
    private BrSeverityType[] severityTypes;
    private BrValidationTargetType[] targetTypes;
    private ApplicationActionType[] applicationMoments;
    private ServiceActionType[] serviceMoments;
    private RequestType[] requestTypes;
    private RrrType[] rrrTypes;
    private RegistrationStatusType[] regMoments;
    private BrValidation brValidation;
    private BrDefinition brDefinition;
    LocalizedValuesListBean localizedFeedback;

    @EJB
    private SystemEJBLocal systemEjb;

    @EJB
    private RefDataEJBLocal refEjb;

    @Inject
    private LanguageBean langBean;

    @Inject
    MessageProvider msgProvider;

    @Inject
    MessageBean msgBean;

    public Br getBr() {
        return br;
    }

    public LocalizedValuesListBean getLocalizedFeedback() {
        return localizedFeedback;
    }

    public BrTechnicalType[] getTechnicalTypes() {
        return technicalTypes;
    }

    public BrSeverityType[] getSeverityTypes() {
        return severityTypes;
    }

    public BrValidationTargetType[] getTargetTypes() {
        return targetTypes;
    }

    public ApplicationActionType[] getApplicationMoments() {
        return applicationMoments;
    }

    public ServiceActionType[] getServiceMoments() {
        return serviceMoments;
    }

    public RequestType[] getRequestTypes() {
        return requestTypes;
    }

    public RrrType[] getRrrTypes() {
        return rrrTypes;
    }

    public RegistrationStatusType[] getRegMoments() {
        return regMoments;
    }

    public BrValidation getBrValidation() {
        return brValidation;
    }

    public BrDefinition getBrDefinition() {
        return brDefinition;
    }

    public BrPageBean() {
        super();
    }

    public void init() {
        String id = getRequestParam("id");
        String action = getRequestParam("action");
        if (action.equalsIgnoreCase("saved")) {
            msgBean.setSuccessMessage(msgProvider.getMessage(MessagesKeys.BR_PAGE_BR_SAVED));
        }

        if (!StringUtility.isEmpty(id)) {
            br = systemEjb.getBr(id, null);
        }

        if (br == null) {
            br = new Br();
            br.setId(UUID.randomUUID().toString());
            br.setBrDefinitionList(new ArrayList<BrDefinition>());
            br.setBrValidationList(new ArrayList<BrValidation>());
        }

        brDefinition = new BrDefinition();
        brValidation = new BrValidation();

        localizedFeedback = new LocalizedValuesListBean(langBean);
        localizedFeedback.loadLocalizedValues(br.getFeedback());

        // Load lists
        List<BrTechnicalType> technicalTypesList = refEjb.getCodeEntityList(BrTechnicalType.class, langBean.getLocale());
        List<BrSeverityType> severityTypesList = refEjb.getCodeEntityList(BrSeverityType.class, langBean.getLocale());
        List<BrValidationTargetType> targetTypesList = refEjb.getCodeEntityList(BrValidationTargetType.class, langBean.getLocale());
        List<ApplicationActionType> applicationMomentsList = refEjb.getCodeEntityList(ApplicationActionType.class, langBean.getLocale());
        List<ServiceActionType> serviceMomentsList = refEjb.getCodeEntityList(ServiceActionType.class, langBean.getLocale());
        List<RequestType> requestTypesList = refEjb.getCodeEntityList(RequestType.class, langBean.getLocale());
        List<RrrType> rrrTypesList = refEjb.getCodeEntityList(RrrType.class, langBean.getLocale());
        List<RegistrationStatusType> regMomentsList = refEjb.getCodeEntityList(RegistrationStatusType.class, langBean.getLocale());

        // Load lists
        if (technicalTypesList != null) {
            BrTechnicalType dummy = new BrTechnicalType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            technicalTypesList.add(0, dummy);
            technicalTypes = technicalTypesList.toArray(new BrTechnicalType[technicalTypesList.size()]);
        }
        if (severityTypesList != null) {
            BrSeverityType dummy = new BrSeverityType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            severityTypesList.add(0, dummy);
            severityTypes = severityTypesList.toArray(new BrSeverityType[severityTypesList.size()]);
        }
        if (targetTypesList != null) {
            BrValidationTargetType dummy = new BrValidationTargetType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            targetTypesList.add(0, dummy);
            targetTypes = targetTypesList.toArray(new BrValidationTargetType[targetTypesList.size()]);
        }
        if (applicationMomentsList != null) {
            ApplicationActionType dummy = new ApplicationActionType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            applicationMomentsList.add(0, dummy);
            applicationMoments = applicationMomentsList.toArray(new ApplicationActionType[applicationMomentsList.size()]);
        }
        if (serviceMomentsList != null) {
            ServiceActionType dummy = new ServiceActionType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            serviceMomentsList.add(0, dummy);
            serviceMoments = serviceMomentsList.toArray(new ServiceActionType[serviceMomentsList.size()]);
        }
        if (requestTypesList != null) {
            RequestType dummy = new RequestType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            requestTypesList.add(0, dummy);
            requestTypes = requestTypesList.toArray(new RequestType[requestTypesList.size()]);
        }
        if (rrrTypesList != null) {
            RrrType dummy = new RrrType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            rrrTypesList.add(0, dummy);
            rrrTypes = rrrTypesList.toArray(new RrrType[rrrTypesList.size()]);
        }
        if (regMomentsList != null) {
            RegistrationStatusType dummy = new RegistrationStatusType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            regMomentsList.add(0, dummy);
            regMoments = regMomentsList.toArray(new RegistrationStatusType[regMomentsList.size()]);
        }
    }

    public String getTechnicalTypeName(String code) {
        return getCodeDisplayValue(code, technicalTypes);
    }

    public String getSeverityTypeName(String code) {
        return getCodeDisplayValue(code, severityTypes);
    }

    public String getTargetTypeName(String code) {
        return getCodeDisplayValue(code, targetTypes);
    }

    public String getApplicationMomentName(String code) {
        return getCodeDisplayValue(code, applicationMoments);
    }

    public String getServiceMomentName(String code) {
        return getCodeDisplayValue(code, serviceMoments);
    }

    public String getRegMomentName(String code) {
        return getCodeDisplayValue(code, regMoments);
    }

    public String getRequestTypeName(String code) {
        return getCodeDisplayValue(code, requestTypes);
    }

    public String getRrrTypeName(String code) {
        return getCodeDisplayValue(code, rrrTypes);
    }

    private <T extends AbstractCodeEntity> String getCodeDisplayValue(String code, T[] array) {
        if (array != null) {
            for (T codeEntity : array) {
                if (codeEntity.getCode().equalsIgnoreCase(code)) {
                    return codeEntity.getDisplayValue();
                }
            }
        }
        return "";
    }

    public BrValidation[] getBrValidations() {
        if (br != null && br.getBrValidationList() != null) {
            List<BrValidation> brValidations = new ArrayList<>();
            for (BrValidation validation : br.getBrValidationList()) {
                if (validation.getEntityAction() == null || (!validation.getEntityAction().equals(EntityAction.DELETE))) {
                    brValidations.add(validation);
                }
            }
            return brValidations.toArray(new BrValidation[brValidations.size()]);
        }
        return null;
    }

    public void loadBrValidation(String id) {
        if (!StringUtility.isEmpty(id)) {
            if (br != null && br.getBrValidationList() != null) {
                for (BrValidation validation : br.getBrValidationList()) {
                    if (validation.getId().equalsIgnoreCase(id)) {
                        MappingManager.getMapper().map(validation, brValidation);
                        return;
                    }
                }
            }
        }
        brValidation = new BrValidation();
        brValidation.setId(UUID.randomUUID().toString());
        brValidation.setBrId(br.getId());
    }

    public void deleteBrValidation(String id) {
        if (brValidation == null || br == null) {
            return;
        }
        for (BrValidation validation : br.getBrValidationList()) {
            if (validation.getId().equals(id)) {
                validation.setEntityAction(EntityAction.DELETE);
                return;
            }
        }
    }

    public void saveBrValidation() throws Exception {
        if (brValidation == null || br == null) {
            return;
        }
        if (br.getBrValidationList() == null) {
            br.setBrValidationList(new ArrayList());
        }
        // Validate
        String errors = "";
        if (StringUtility.isEmpty(brValidation.getSeverityCode())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_SELECT_SEVERITY) + "\r\n";
        }
        if (StringUtility.isEmpty(brValidation.getTargetCode())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_SELECT_TARGET) + "\r\n";
        }

        if (!errors.equals("")) {
            throw new Exception(errors);
        }

        // Search for Br validation
        for (BrValidation validation : br.getBrValidationList()) {
            if (validation.getId().equals(brValidation.getId())) {
                MappingManager.getMapper().map(brValidation, validation);
                return;
            }
        }
        br.getBrValidationList().add(MappingManager.getMapper().map(brValidation, BrValidation.class));
    }

    public BrDefinition[] getBrDefinitions() {
        if (br != null && br.getBrDefinitionList() != null) {
            List<BrDefinition> brDefinitions = new ArrayList<>();
            for (BrDefinition definition : br.getBrDefinitionList()) {
                if (definition.getEntityAction() == null || (!definition.getEntityAction().equals(EntityAction.DELETE))) {
                    brDefinitions.add(definition);
                }
            }
            return brDefinitions.toArray(new BrDefinition[brDefinitions.size()]);
        }
        return null;
    }

    public void loadBrDefinition(BrDefinition definition) {
        if (definition != null) {
            MappingManager.getMapper().map(definition, brDefinition);
            if (brDefinition.getActiveUntil() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(brDefinition.getActiveUntil());
                if (cal.get(Calendar.YEAR) > 2100) {
                    cal.set(2099, 11, 31, 23, 59, 59);
                    brDefinition.setActiveUntil(cal.getTime());
                }
            }
        } else {
            brDefinition = new BrDefinition();
            brDefinition.setActiveFrom(Calendar.getInstance().getTime());
            brDefinition.setBrId(br.getId());
        }
    }

    public void deleteBrDefinition(BrDefinition definition) {
        if (definition == null) {
            return;
        }
        definition.setEntityAction(EntityAction.DELETE);
    }

    public void saveBrDefinition() throws Exception {
        if (brDefinition == null || br == null) {
            return;
        }
        if (br.getBrDefinitionList() == null) {
            br.setBrDefinitionList(new ArrayList());
        }
        // Validate
        String errors = "";
        if (brDefinition.getActiveFrom() == null) {
            errors += msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_FILL_IN_ACTIVE_FROM) + "\r\n";
        }
        if (brDefinition.getActiveUntil() == null) {
            errors += msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_FILL_IN_ACTIVE_UNTIL) + "\r\n";
        }
        if (brDefinition.getActiveFrom() != null && brDefinition.getActiveUntil() != null
                && brDefinition.getActiveFrom().after(brDefinition.getActiveUntil())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_ACTIVE_LESS_THAN_UNTIL) + "\r\n";
        }
        if (StringUtility.isEmpty(brDefinition.getBody())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_FILL_IN_BODY) + "\r\n";
        }

        if (!errors.equals("")) {
            throw new Exception(errors);
        }

        // Search for Br validation
        for (BrDefinition definition : br.getBrDefinitionList()) {
            //if (definition.getObjectId().equals(brDefinition.getObjectId())) {
            if (definition.getActiveFrom().equals(brDefinition.getActiveFrom())) {
                MappingManager.getMapper().map(brDefinition, definition);
                return;
            }
        }
        br.getBrDefinitionList().add(MappingManager.getMapper().map(brDefinition, BrDefinition.class));
    }

    public void save() throws Exception {
        // Validate
        boolean isValid = true;

        if (StringUtility.isEmpty(br.getDisplayName())) {
            getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_FILL_IN_DISPLAY_NAME)));
            isValid = false;
        }
        if (StringUtility.isEmpty(br.getTechnicalTypeCode())) {
            getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_SELECT_TECHNICAL_TYPE)));
            isValid = false;
        }
        if (getBrDefinitions() == null || getBrDefinitions().length < 1) {
            getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_PROVIDE_DEFINITION)));
            isValid = false;
        }
//        if (getBrValidations() == null || getBrValidations().length < 1) {
//            getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.BR_PAGE_PROVIDE_VALIDATION)));
//            isValid = false;
//        }

        if (!isValid) {
            return;
        }

        br.setFeedback(localizedFeedback.buildMultilingualString());

        try {
            runUpdate(new Runnable() {
                @Override
                public void run() {
                    systemEjb.saveBr(br);
                    try {
                        getContext().getExternalContext().redirect(getRequest().getContextPath() + "/br/Br.xhtml?id=" + br.getId() + "&action=saved");
                    } catch (IOException ex) {
                        getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.GENERAL_REDIRECT_FAILED)));
                    }
                }
            });
        } catch (Exception e) {
            getContext().addMessage(null, new FacesMessage(processException(e, langBean.getLocale()).getMessage()));
        }
    }
}
