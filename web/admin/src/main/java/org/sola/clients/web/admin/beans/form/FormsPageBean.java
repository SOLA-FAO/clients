package org.sola.clients.web.admin.beans.form;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sola.clients.web.admin.beans.AbstractBackingBean;
import org.sola.clients.web.admin.beans.helpers.ErrorKeys;
import org.sola.clients.web.admin.beans.helpers.MessageProvider;
import org.sola.clients.web.admin.beans.helpers.MessagesKeys;
import org.sola.clients.web.admin.beans.language.LanguageBean;
import org.sola.common.StringUtility;
import org.sola.common.mapping.MappingManager;
import org.sola.opentenure.services.ejbs.claim.businesslogic.ClaimEJBLocal;
import org.sola.opentenure.services.ejbs.claim.entities.FieldConstraint;
import org.sola.opentenure.services.ejbs.claim.entities.FieldConstraintOption;
import org.sola.opentenure.services.ejbs.claim.entities.FieldTemplate;
import org.sola.opentenure.services.ejbs.claim.entities.FormTemplate;
import org.sola.opentenure.services.ejbs.claim.entities.SectionTemplate;
import org.sola.services.common.EntityAction;
import org.sola.services.common.logging.LogUtility;
import org.sola.services.ejb.refdata.businesslogic.RefDataEJBLocal;
import org.sola.services.ejb.refdata.entities.FieldConstraintType;
import org.sola.services.ejb.refdata.entities.FieldType;

/**
 * Contains methods to manage dynamic forms
 */
@Named
@ViewScoped
public class FormsPageBean extends AbstractBackingBean {

    @Inject
    LanguageBean langBean;

    @Inject
    MessageProvider msgProvider;

    @EJB
    RefDataEJBLocal refEjb;
    
    @EJB
    ClaimEJBLocal claimEjb;

    private FormTemplate formTemplate;
    private SectionTemplate tmpSection;
    private FieldTemplate tmpField;
    private FieldConstraint tmpConstraint;
    private FieldConstraintOption tmpOption;
    private FieldType[] fieldTypes;
    private FieldConstraintType[] constraintTypes;
    private String message;
    
    public FormTemplate getFormTemplate() {
        return formTemplate;
    }

    public SectionTemplate getTmpSection() {
        return tmpSection;
    }

    public FieldTemplate getTmpField() {
        return tmpField;
    }

    public FieldConstraint getTmpConstraint() {
        return tmpConstraint;
    }

    public FieldConstraintOption getTmpOption() {
        return tmpOption;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FieldType[] getFieldTypes() {
        return fieldTypes;
    }

    public FieldConstraintType[] getConstraintTypes() {
        return constraintTypes;
    }

    public FormsPageBean() {
    }

    @PostConstruct
    private void init() {
        // Load lists
        List<FieldType> fieldTypesList = refEjb.getCodeEntityList(FieldType.class, langBean.getLocale());
        List<FieldConstraintType> constraintTypesList = refEjb.getCodeEntityList(FieldConstraintType.class, langBean.getLocale());
        
        if(fieldTypesList != null){
            FieldType dummy = new FieldType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            fieldTypesList.add(0, dummy);
            fieldTypes = fieldTypesList.toArray(new FieldType[fieldTypesList.size()]);
        }
        
        if(constraintTypesList != null){
            FieldConstraintType dummy = new FieldConstraintType();
            dummy.setCode("");
            dummy.setDisplayValue(" ");
            constraintTypesList.add(0, dummy);
            constraintTypes = constraintTypesList.toArray(new FieldConstraintType[constraintTypesList.size()]);
        }
        
        // Check action code
        String action = getRequestParam("action");
        if (!StringUtility.isEmpty(action) && action.equalsIgnoreCase("saved")) {
            message = msgProvider.getMessage(MessagesKeys.FORMS_PAGE_FORM_SAVED);
        }

        // Init form template
        String id = getRequestParam("id");
        if (!StringUtility.isEmpty(id)) {
            formTemplate = claimEjb.getFormTemplate(id, null);
        }
        if (formTemplate == null) {
            formTemplate = new FormTemplate();
        }
        
        // Init tmp variables
        tmpSection = new SectionTemplate();
        tmpField = new FieldTemplate();
        tmpConstraint = new FieldConstraint();
        tmpOption = new FieldConstraintOption();
    }

    public SectionTemplate[] getSections() {
        if (formTemplate == null) {
            return null;
        }
        if (formTemplate.getSectionTemplateList() == null) {
            formTemplate.setSectionTemplateList(new ArrayList<SectionTemplate>());
        }

        List<SectionTemplate> sections = new ArrayList<>();
        for (SectionTemplate sec : formTemplate.getSectionTemplateList()) {
            if (sec.getEntityAction() == null || sec.getEntityAction() != EntityAction.DELETE) {
                sections.add(sec);
            }
        }
        return sections.toArray(new SectionTemplate[sections.size()]);
    }

    public void saveFormHeader() throws Exception {
        // Check for errors
        String errors = "";
        if (StringUtility.isEmpty(formTemplate.getName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(formTemplate.getDisplayName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_DISPLAY_NAME);
        }
        if (!errors.equals("")) {
            throw new Exception(errors);
        }
    }

    public void editSection(SectionTemplate sec) {
        if (sec == null) {
            tmpSection = new SectionTemplate();
            tmpSection.setId(UUID.randomUUID().toString());
            tmpSection.setFormTemplateName(formTemplate.getName());
            tmpSection.setFieldTemplateList(new ArrayList<FieldTemplate>());
        } else {
            tmpSection = MappingManager.getMapper().map(sec, SectionTemplate.class);
        }
    }

    public void deleteSection(SectionTemplate sec) {
        if (sec != null) {
            if (sec.isNew()) {
                // Just delete from the list
                int i = 0;
                for (SectionTemplate secTmpl : formTemplate.getSectionTemplateList()) {
                    if (sec.getId().equals(secTmpl.getId())) {
                        formTemplate.getSectionTemplateList().remove(i);
                        return;
                    }
                    i += 1;
                }
            } else {
                // Set entity action
                sec.setEntityAction(EntityAction.DELETE);
            }
        }
    }

    public void saveSection() throws Exception {
        if (tmpSection == null) {
            return;
        }

        // Check for errors
        String errors = "";
        if (StringUtility.isEmpty(tmpSection.getName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpSection.getDisplayName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_DISPLAY_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpSection.getElementName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_ELEMENT_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpSection.getElementDisplayName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_ELEMENT_DISPLAY_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpSection.getErrorMsg())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_ERROR_MESSAGE) + "\r\n";
        }
        if (tmpSection.getMinOccurrences() > tmpSection.getMaxOccurrences()) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_MIN_OCCUR_GRATER_MAX_OCCUR) + "\r\n";
        }
        if (!errors.equals("")) {
            throw new Exception(errors);
        }

        if (formTemplate.getSectionTemplateList() != null) {
            // Check if section already exists
            for (SectionTemplate templ : formTemplate.getSectionTemplateList()) {
                if (templ.getId().equals(tmpSection.getId())) {
                    MappingManager.getMapper().map(tmpSection, templ);
                    return;
                }
            }
            // Otherwise add it
            formTemplate.getSectionTemplateList().add(tmpSection);
        }
    }

    public FieldTemplate[] getFields(SectionTemplate sec) {
        if (sec == null) {
            return null;
        }
        if (sec.getFieldTemplateList() == null) {
            sec.setFieldTemplateList(new ArrayList<FieldTemplate>());
        }

        List<FieldTemplate> fields = new ArrayList<>();
        for (FieldTemplate fTempl : sec.getFieldTemplateList()) {
            if (fTempl.getEntityAction() == null || fTempl.getEntityAction() != EntityAction.DELETE) {
                fields.add(fTempl);
            }
        }
        return fields.toArray(new FieldTemplate[fields.size()]);
    }

    public void editField(FieldTemplate fTmpl, String sectionId) {
        if (fTmpl == null) {
            tmpField = new FieldTemplate();
            tmpField.setId(UUID.randomUUID().toString());
            tmpField.setSectionTemplateId(sectionId);
            tmpField.setFieldConstraintList(new ArrayList<FieldConstraint>());
        } else {
            tmpField = MappingManager.getMapper().map(fTmpl, FieldTemplate.class);
        }
    }

    public void deleteField(FieldTemplate fTmpl, SectionTemplate sec) {
        if (fTmpl != null && sec != null) {
            if (fTmpl.isNew()) {
                // Just delete from the list
                int i = 0;
                for (FieldTemplate fTmpl2 : sec.getFieldTemplateList()) {
                    if (fTmpl2.getId().equals(fTmpl.getId())) {
                        sec.getFieldTemplateList().remove(i);
                        return;
                    }
                    i += 1;
                }
            } else {
                // Set entity action
                fTmpl.setEntityAction(EntityAction.DELETE);
            }
        }
    }

    public void saveField() throws Exception {
        if (tmpField == null) {
            return;
        }

        // Check for errors
        String errors = "";
        if (StringUtility.isEmpty(tmpField.getName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpField.getDisplayName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_DISPLAY_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpField.getHint())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_HINT) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpField.getFieldType())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_SELECT_FIELD_TYPE) + "\r\n";
        }

        if (!errors.equals("")) {
            throw new Exception(errors);
        }

        if (formTemplate.getSectionTemplateList() != null) {
            // Look for section 
            for (SectionTemplate secTmpl : formTemplate.getSectionTemplateList()) {
                if (secTmpl.getId().equals(tmpField.getSectionTemplateId())) {
                    // Check if field already exists
                    for (FieldTemplate fTmpl : secTmpl.getFieldTemplateList()) {
                        if (fTmpl.getId().equals(tmpField.getId())) {
                            MappingManager.getMapper().map(tmpField, fTmpl);
                            return;
                        }
                    }
                    // Otherwise add it
                    secTmpl.getFieldTemplateList().add(tmpField);
                    return;
                }
            }
        }
    }

    public FieldConstraint[] getConstraints(FieldTemplate fTmpl) {
        if (fTmpl == null) {
            return null;
        }
        if (fTmpl.getFieldConstraintList() == null) {
            fTmpl.setFieldConstraintList(new ArrayList<FieldConstraint>());
        }

        List<FieldConstraint> constraints = new ArrayList<>();
        for (FieldConstraint fContsr : fTmpl.getFieldConstraintList()) {
            if (fContsr.getEntityAction() == null || fContsr.getEntityAction() != EntityAction.DELETE) {
                constraints.add(fContsr);
            }
        }
        return constraints.toArray(new FieldConstraint[constraints.size()]);
    }

    public void editConstraint(FieldConstraint fConstr, String fieldId) {
        if (fConstr == null) {
            tmpConstraint = new FieldConstraint();
            tmpConstraint.setId(UUID.randomUUID().toString());
            tmpConstraint.setFieldTemplateId(fieldId);
            tmpConstraint.setFieldConstraintOptionList(new ArrayList<FieldConstraintOption>());
        } else {
            tmpConstraint = MappingManager.getMapper().map(fConstr, FieldConstraint.class);
        }
    }

    public void deleteConstraint(FieldConstraint fConstr, FieldTemplate fTmpl) {
        if (fConstr != null && fTmpl != null) {
            if (fConstr.isNew()) {
                // Just delete from the list
                int i = 0;
                for (FieldConstraint fConstr2 : fTmpl.getFieldConstraintList()) {
                    if (fConstr2.getId().equals(fConstr.getId())) {
                        fTmpl.getFieldConstraintList().remove(i);
                        return;
                    }
                    i += 1;
                }
            } else {
                // Set entity action
                fConstr.setEntityAction(EntityAction.DELETE);
            }
        }
    }

    public void saveConstraint() throws Exception {
        if (tmpConstraint == null) {
            return;
        }

        // Check for errors
        String errors = "";
        if (StringUtility.isEmpty(tmpConstraint.getName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpConstraint.getDisplayName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_DISPLAY_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpConstraint.getErrorMsg())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_ERROR_MESSAGE) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpConstraint.getFieldConstraintType())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_SELECT_FIELD_CONSTRAINT_TYPE) + "\r\n";
        }

        if (!errors.equals("")) {
            throw new Exception(errors);
        }

        if (formTemplate.getSectionTemplateList() != null) {
            // Loop through sections 
            for (SectionTemplate secTmpl : formTemplate.getSectionTemplateList()) {
                if (secTmpl.getFieldTemplateList() != null) {
                    // Look for field 
                    for (FieldTemplate fTmpl : secTmpl.getFieldTemplateList()) {
                        if (fTmpl.getId().equals(tmpConstraint.getFieldTemplateId())) {
                            // Found field, look for constraint
                            if (fTmpl.getFieldConstraintList() != null) {
                                for (FieldConstraint fConstr : fTmpl.getFieldConstraintList()) {
                                    if (fConstr.getId().equals(tmpConstraint.getId())) {
                                        // Found constraint
                                        MappingManager.getMapper().map(tmpConstraint, fConstr);
                                        return;
                                    }
                                }
                                // Otherwise add it
                                fTmpl.getFieldConstraintList().add(tmpConstraint);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public FieldConstraintOption[] getOptions(FieldConstraint fConstr) {
        if (fConstr == null) {
            return null;
        }
        if (fConstr.getFieldConstraintOptionList() == null) {
            fConstr.setFieldConstraintOptionList(new ArrayList<FieldConstraintOption>());
        }

        List<FieldConstraintOption> options = new ArrayList<>();
        for (FieldConstraintOption option : fConstr.getFieldConstraintOptionList()) {
            if (option.getEntityAction() == null || option.getEntityAction() != EntityAction.DELETE) {
                options.add(option);
            }
        }
        return options.toArray(new FieldConstraintOption[options.size()]);
    }

    public void editOption(FieldConstraintOption option, String constrId) {
        if (option == null) {
            tmpOption = new FieldConstraintOption();
            tmpOption.setId(UUID.randomUUID().toString());
            tmpOption.setFieldConstraintId(constrId);
        } else {
            tmpOption = MappingManager.getMapper().map(option, FieldConstraintOption.class);
        }
    }

    public void deleteOption(FieldConstraintOption option, FieldConstraint fConstr) {
        if (option != null && fConstr != null) {
            if (option.isNew()) {
                // Just delete from the list
                int i = 0;
                for (FieldConstraintOption option2 : fConstr.getFieldConstraintOptionList()) {
                    if (option2.getId().equals(option.getId())) {
                        fConstr.getFieldConstraintOptionList().remove(i);
                        return;
                    }
                    i += 1;
                }
            } else {
                // Set entity action
                option.setEntityAction(EntityAction.DELETE);
            }
        }
    }

    public void saveOption() throws Exception {
        if (tmpOption == null) {
            return;
        }

        // Check for errors
        String errors = "";
        if (StringUtility.isEmpty(tmpOption.getName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(tmpOption.getDisplayName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_DISPLAY_NAME) + "\r\n";
        }

        if (!errors.equals("")) {
            throw new Exception(errors);
        }

        if (formTemplate.getSectionTemplateList() == null) {
            return;
        }

        // Loop through sections 
        for (SectionTemplate secTmpl : formTemplate.getSectionTemplateList()) {
            if (secTmpl.getFieldTemplateList() != null) {
                // Loop through fields 
                for (FieldTemplate fTmpl : secTmpl.getFieldTemplateList()) {
                    if (fTmpl.getFieldConstraintList() != null) {
                        // Loop through constraints
                        for (FieldConstraint fConstr : fTmpl.getFieldConstraintList()) {
                            if (fConstr.getId().equals(tmpOption.getFieldConstraintId())) {
                                // Found constaint, loop through options
                                if (fConstr.getFieldConstraintOptionList() != null) {
                                    for (FieldConstraintOption option : fConstr.getFieldConstraintOptionList()) {
                                        if (option.getId().equals(tmpOption.getId())) {
                                            // Found option
                                            MappingManager.getMapper().map(tmpOption, option);
                                            return;
                                        }
                                    }
                                    // Otherwise add it
                                    fConstr.getFieldConstraintOptionList().add(tmpOption);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void saveForm() {
        try {
            // Check form
            if (StringUtility.isEmpty(formTemplate.getName()) || StringUtility.isEmpty(formTemplate.getDisplayName())) {
                getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_FILL_FORM_NAME_AND_DISPLAY_NAME)));
                return;
            }

            if (formTemplate.getSectionTemplateList() == null || getSections().length < 1) {
                getContext().addMessage(null, new FacesMessage(msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_ADD_1_SECTION)));
                return;
            }

            // Loop though sections
            for (SectionTemplate sec : formTemplate.getSectionTemplateList()) {
                // Assign form name to the section to make sure it's the same
                sec.setFormTemplateName(formTemplate.getName());
                // Check fields
                if (sec.getEntityAction() == null && getFields(sec).length < 1) {
                    getContext().addMessage(null, new FacesMessage(String.format(msgProvider.getErrorMessage(ErrorKeys.FORMS_PAGE_ADD_1_FIELD), sec.getName())));
                    return;
                }
            }

            // Do save
            runUpdate(new Runnable() {
                @Override
                public void run() {
                    formTemplate = claimEjb.saveFormTemplate(formTemplate);
                }
            });

            try {
                getContext().getExternalContext().redirect(getRequest().getContextPath() + "/forms/Form.xhtml?id=" + formTemplate.getName() + "&action=saved");
            } catch (Exception e) {
                LogUtility.log(msgProvider.getMessage(ErrorKeys.GENERAL_REDIRECT_FAILED), e);
            }
        } catch (Exception e) {
            getContext().addMessage(null, new FacesMessage(processException(e, langBean.getLocale()).getMessage()));
        }
    }
}
