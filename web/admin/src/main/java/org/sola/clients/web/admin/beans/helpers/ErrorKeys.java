package org.sola.clients.web.admin.beans.helpers;

/**
 * Holds list of error keys, used to extract messages from errors bundle
 */
public class ErrorKeys {

// General errors
    /**
     * Form Submission was not successful. Please review and correct listed
     * errors:
     */
    public static final String GENERAL_ERROR_LIST_HEADER = "GENERAL_ERROR_LIST_HEADER";

    /**
     * Unexpected errors have occured while executing requested action :
     */
    public static final String GENERAL_UNEXPECTED_ERROR = "GENERAL_UNEXPECTED_ERROR";

    /** Failed to redirect */
    public static final String GENERAL_REDIRECT_FAILED = "GENERAL_REDIRECT_FAILED";
    
// Login erros
    /**
     * Provide user name
     */
    public static final String LOGIN_USERNAME_REQUIRED = "LOGIN_USERNAME_REQUIRED";

    /**
     * Provide password
     */
    public static final String LOGIN_PASSWORD_REQUIRED = "LOGIN_PASSWORD_REQUIRED";

    /**
     * Login failed
     */
    public static final String LOGIN_FAILED = "LOGIN_FAILED";

    /**
     * Logout failed
     */
    public static final String LOGIN_LOGOUT_FAILED = "LOGIN_LOGOUT_FAILED";

    /**
     * Your account is not active. If you just registered, you need to activate
     * it first.
     */
    public static final String LOGIN_ACCOUNT_BLOCKED = "LOGIN_ACCOUNT_BLOCKED";
    /**
     * You don't have administration access rights
     */
    public static final String LOGIN_NO_ADMIN_RIGHTS = "LOGIN_NO_ADMIN_RIGHTS";

    // Dynamic forms
    /**
     * Form template cannot be deleted because it has related claim records.
     */
    public static final String FORMS_PAGE_FORM_HAS_PAYLOAD = "FORMS_PAGE_FORM_HAS_PAYLOAD";
    /**
     * - Fill in name
     */
    public static final String FORMS_PAGE_FILL_NAME = "FORMS_PAGE_FILL_NAME";
    /**
     * - Fill in display name
     */
    public static final String FORMS_PAGE_FILL_DISPLAY_NAME = "FORMS_PAGE_FILL_DISPLAY_NAME";
    /**
     * - Fill in element name
     */
    public static final String FORMS_PAGE_FILL_ELEMENT_NAME = "FORMS_PAGE_FILL_ELEMENT_NAME";
    /**
     * - Fill in element display name
     */
    public static final String FORMS_PAGE_FILL_ELEMENT_DISPLAY_NAME = "FORMS_PAGE_FILL_ELEMENT_DISPLAY_NAME";
    /**
     * - Fill in error message
     */
    public static final String FORMS_PAGE_FILL_ERROR_MESSAGE = "FORMS_PAGE_FILL_ERROR_MESSAGE";
    /**
     * - Minimum occurrence should not be grater than maximum occurrence
     */
    public static final String FORMS_PAGE_MIN_OCCUR_GRATER_MAX_OCCUR = "FORMS_PAGE_MIN_OCCUR_GRATER_MAX_OCCUR";
    /**
     * - Fill in hint
     */
    public static final String FORMS_PAGE_FILL_HINT = "FORMS_PAGE_FILL_HINT";
    /**
     * - Select field type
     */
    public static final String FORMS_PAGE_SELECT_FIELD_TYPE = "FORMS_PAGE_SELECT_FIELD_TYPE";
    /**
     * - Select field constraint type
     */
    public static final String FORMS_PAGE_SELECT_FIELD_CONSTRAINT_TYPE = "FORMS_PAGE_SELECT_FIELD_CONSTRAINT_TYPE";
    /**
     * Fill in form name and display name
     */
    public static final String FORMS_PAGE_FILL_FORM_NAME_AND_DISPLAY_NAME = "FORMS_PAGE_FILL_FORM_NAME_AND_DISPLAY_NAME";
    /**
     * Add at least 1 section
     */
    public static final String FORMS_PAGE_ADD_1_SECTION = "FORMS_PAGE_ADD_1_SECTION";
    /**
     * Add at least 1 field into section %s
     */
    public static final String FORMS_PAGE_ADD_1_FIELD = "FORMS_PAGE_ADD_1_FIELD";
    
    // Reference data
    /**
     * - Fill in code
     */
    public static final String REFDATA_PAGE_FILL_CODE = "REFDATA_PAGE_FILL_CODE";
    /**
     * - Fill in display value
     */
    public static final String REFDATA_PAGE_FILL_DISPLAY_VALUE = "REFDATA_PAGE_FILL_DISPLAY_VALUE";
    /**
     * - Select status
     */
    public static final String REFDATA_PAGE_SELECT_STATUS = "REFDATA_PAGE_SELECT_STATUS";
    /**
     * - Select panel launcher group
     */
    public static final String REFDATA_PAGE_SELECT_PANEL_LAUNCHER_GROUP = "REFDATA_PAGE_SELECT_PANEL_LAUNCHER_GROUP";
    /**
     * - Select request category
     */
    public static final String REFDATA_PAGE_SELECT_REQUEST_CATEGORY = "REFDATA_PAGE_SELECT_REQUEST_CATEGORY";
    /**
     * - Fill in days to complete
     */
    public static final String REFDATA_PAGE_FILL_DAYS_TO_COMPLETE = "REFDATA_PAGE_FILL_DAYS_TO_COMPLETE";
    /**
     * - Fill in base fee
     */
    public static final String REFDATA_PAGE_FILL_BASE_FEE = "REFDATA_PAGE_FILL_BASE_FEE";
    /**
     * - Fill in area base fee
     */
    public static final String REFDATA_PAGE_FILL_AREA_BASE_FEE = "REFDATA_PAGE_FILL_AREA_BASE_FEE";
    /**
     * - Fill in value base fee
     */
    public static final String REFDATA_PAGE_FILL_VALUE_BASE_FEE = "REFDATA_PAGE_FILL_VALUE_BASE_FEE";
    /**
     * - Fill in number of required properties
     */
    public static final String REFDATA_PAGE_FILL_REQ_PROP_NUMBER = "REFDATA_PAGE_FILL_REQ_PROP_NUMBER";
    /**
     * - Select RRR group
     */
    public static final String REFDATA_PAGE_SELECT_RRR_GROUP = "REFDATA_PAGE_SELECT_RRR_GROUP";
    
    // Settings
    /**
     * - Fill in name
     */
    public static final String SETTINGS_PAGE_FILL_IN_NAME = "SETTINGS_PAGE_FILL_IN_NAME";
    /**
     * - Fill in value
     */
    public static final String SETTINGS_PAGE_FILL_IN_VALUE = "SETTINGS_PAGE_FILL_IN_VALUE";
    
    // Community area
    /**
     * Provide community area
     */
    public static final String MAP_CONTROL_PROVIDE_COMMUNITY_AREA = "MAP_CONTROL_PROVIDE_COMMUNITY_AREA";
    
    // Group
    
    /** - Fill in name */
    public static final String GROUP_PAGE_FILL_IN_NAME = "GROUP_PAGE_FILL_IN_NAME";
    /** - Select at least 1 role */
    public static final String GROUP_PAGE_SELECT_ROLE = "GROUP_PAGE_SELECT_ROLE";
    
    // User
    
    /** - Fill in user name */
    public static final String USER_PAGE_FILL_IN_USER_NAME = "USER_PAGE_FILL_IN_USER_NAME";
    /** - Fill in first name */
    public static final String USER_PAGE_FILL_IN_FIRST_NAME = "USER_PAGE_FILL_IN_FIRST_NAME";
    /** - Fill in last name */
    public static final String USER_PAGE_FILL_IN_LAST_NAME = "USER_PAGE_FILL_IN_LAST_NAME";
    /** - Fill in password */
    public static final String USER_PAGE_FILL_IN_PASSWORD = "USER_PAGE_FILL_IN_PASSWORD";
    /** - Fill in password confirmation */
    public static final String USER_PAGE_FILL_IN_PASSWORD_CONFIRMATION = "USER_PAGE_FILL_IN_PASSWORD_CONFIRMATION";
    /** - Provided password and password confirmation don't match */
    public static final String USER_PAGE_PASSWORD_NOT_MATCH_CONFIRMATION = "USER_PAGE_PASSWORD_NOT_MATCH_CONFIRMATION";
    /** - Select at least 1 group */
    public static final String USER_PAGE_SELECT_GROUP = "USER_PAGE_SELECT_GROUP";
    
    // Business rules
    /** Fill in display name */
    public static final String BR_PAGE_FILL_IN_DISPLAY_NAME = "BR_PAGE_FILL_IN_DISPLAY_NAME";
    /** Select technical type */
    public static final String BR_PAGE_SELECT_TECHNICAL_TYPE = "BR_PAGE_SELECT_TECHNICAL_TYPE";
    /** Provide at least 1 definition */
    public static final String BR_PAGE_PROVIDE_DEFINITION = "BR_PAGE_PROVIDE_DEFINITION";
    /** Provide at least 1 validation */
    public static final String BR_PAGE_PROVIDE_VALIDATION = "BR_PAGE_PROVIDE_VALIDATION";
    /** - Fill in active from field */
    public static final String BR_PAGE_FILL_IN_ACTIVE_FROM = "BR_PAGE_FILL_IN_ACTIVE_FROM";
    /** - Fill in active until field */
    public static final String BR_PAGE_FILL_IN_ACTIVE_UNTIL = "BR_PAGE_FILL_IN_ACTIVE_UNTIL";
    /** - Fill in rule body */
    public static final String BR_PAGE_FILL_IN_BODY = "BR_PAGE_FILL_IN_BODY";
    /** - Select validation target */
    public static final String BR_PAGE_SELECT_TARGET = "BR_PAGE_SELECT_TARGET";
    /** - Select severity type */
    public static final String BR_PAGE_SELECT_SEVERITY = "BR_PAGE_SELECT_SEVERITY";
    /** - Fill in execution order */
    public static final String BR_PAGE_FILL_IN_ORDER = "BR_PAGE_FILL_IN_ORDER";
    /** - Active from date should be less than active until date */
    public static final String BR_PAGE_ACTIVE_LESS_THAN_UNTIL = "BR_PAGE_ACTIVE_LESS_THAN_UNTIL";
}
