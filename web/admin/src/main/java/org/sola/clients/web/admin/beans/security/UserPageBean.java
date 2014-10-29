package org.sola.clients.web.admin.beans.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sola.clients.web.admin.beans.AbstractBackingBean;
import org.sola.clients.web.admin.beans.helpers.ErrorKeys;
import org.sola.clients.web.admin.beans.helpers.MessageProvider;
import org.sola.common.StringUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.ejb.search.businesslogic.SearchEJBLocal;
import org.sola.services.ejb.search.repository.entities.UserSearchParams;
import org.sola.services.ejb.search.repository.entities.UserSearchResult;
import org.sola.services.ejbs.admin.businesslogic.AdminEJBLocal;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Group;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.GroupRole;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.User;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.UserGroup;

/**
 * Contains methods and properties to manage {@link Group}
 */
@Named
@ViewScoped
public class UserPageBean extends AbstractBackingBean {

    private User user;
    private List<UserSearchResult> searchResults;
    private Group[] groups;
    private Map<String, String> mapGroups;
    private String[] selectedGroupCodes;
    private UserSearchParams searchParams;
    private String passwordConfirmation;
    private String oldPassword;

    @Inject
    MessageProvider msgProvider;

    @EJB
    AdminEJBLocal adminEjb;

    @EJB
    SearchEJBLocal searchEjb;

    public User getUser() {
        return user;
    }

    public List<UserSearchResult> getSearchResults() {
        return searchResults;
    }

    public Group[] getGroups() {
        return groups;
    }

    public Map<String, String> getMapGroups() {
        return mapGroups;
    }

    public String[] getSelectedGroupCodes() {
        return selectedGroupCodes;
    }

    public void setSelectedGroupCodes(String[] selectedGroupCodes) {
        this.selectedGroupCodes = selectedGroupCodes;
    }

    public UserSearchParams getSearchParams() {
        if (searchParams == null) {
            searchParams = new UserSearchParams();
        }
        return searchParams;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getGroupName(String id) {
        if (id != null && groups != null) {
            for (Group item : groups) {
                if (item.getId().equalsIgnoreCase(id)) {
                    return item.getName();
                }
            }
        }
        return "";
    }

    @PostConstruct
    private void init() {
        List<Group> groupsList = adminEjb.getGroups();
        mapGroups = new HashMap<>();
        if (groupsList != null) {
            groups = groupsList.toArray(new Group[groupsList.size()]);
            for (Group g : groups) {
                mapGroups.put(g.getName(), g.getId());
            }
        }
        mapGroups.put("", "");
        searchParams = new UserSearchParams();
        search();
    }

    public void search() {
        searchResults = searchEjb.searchUsers(searchParams);
    }

    public void loadUser(String userName) {
        if (StringUtility.isEmpty(userName)) {
            user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setUserGroups(new ArrayList<UserGroup>());
        } else {
            user = adminEjb.getUser(userName);
        }

        passwordConfirmation = user.getPassword();
        oldPassword = user.getPassword();

        // Select/unselect groups
        List<Group> groupsToSelect = new ArrayList<>();
        if (user.getUserGroups() != null) {
            for (UserGroup userGroup : user.getUserGroups()) {
                if (groups != null) {
                    for (Group group : groups) {
                        if (userGroup.getGroupId().equalsIgnoreCase(group.getId())) {
                            groupsToSelect.add(group);
                            break;
                        }
                    }
                }
            }
        }
        selectedGroupCodes = new String[groupsToSelect.size()];
        int i = 0;
        for (Group group : groupsToSelect) {
            selectedGroupCodes[i] = group.getId();
            i += 1;
        }
    }

    public void deleteUser(String userName) {
        if (!StringUtility.isEmpty(userName)) {
            User usr = adminEjb.getUser(userName);
            usr.setEntityAction(EntityAction.DELETE);
            adminEjb.saveUser(usr);
            search();
        }
    }

    public void saveUser() throws Exception {
        if (user == null) {
            return;
        }

        // Validate
        String errors = "";
        if (StringUtility.isEmpty(user.getUserName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.USER_PAGE_FILL_IN_USER_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(user.getFirstName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.USER_PAGE_FILL_IN_FIRST_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(user.getLastName())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.USER_PAGE_FILL_IN_LAST_NAME) + "\r\n";
        }
        if (StringUtility.isEmpty(user.getPassword())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.USER_PAGE_FILL_IN_PASSWORD) + "\r\n";
        }
        if (StringUtility.isEmpty(passwordConfirmation)) {
            errors += msgProvider.getErrorMessage(ErrorKeys.USER_PAGE_FILL_IN_PASSWORD_CONFIRMATION) + "\r\n";
        }
        if (!StringUtility.empty(passwordConfirmation).equals(user.getPassword())) {
            errors += msgProvider.getErrorMessage(ErrorKeys.USER_PAGE_PASSWORD_NOT_MATCH_CONFIRMATION) + "\r\n";
        }
        if (selectedGroupCodes == null || selectedGroupCodes.length < 1) {
            errors += msgProvider.getErrorMessage(ErrorKeys.USER_PAGE_SELECT_GROUP) + "\r\n";
        }

        if (!errors.equals("")) {
            throw new Exception(errors);
        }

        // Prepare groups related to the user
        // Delete
        if (user.getUserGroups() != null) {
            for (UserGroup userGroup : user.getUserGroups()) {
                boolean found = false;
                if (selectedGroupCodes != null) {
                    for (String groupId : selectedGroupCodes) {
                        if (userGroup.getGroupId().equalsIgnoreCase(groupId)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    userGroup.setEntityAction(EntityAction.DELETE);
                }
            }
        }

        // Add
        if (selectedGroupCodes != null) {
            for (String groupId : selectedGroupCodes) {
                boolean found = false;
                if (user.getUserGroups() != null) {
                    for (UserGroup userGroup : user.getUserGroups()) {
                        if (userGroup.getGroupId().equalsIgnoreCase(groupId)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    UserGroup userGroup = new UserGroup();
                    userGroup.setGroupId(groupId);
                    userGroup.setUserId(user.getId());
                    user.getUserGroups().add(userGroup);
                }
            }
        }
        
        if(StringUtility.isEmpty(user.getEmail())){
            user.setEmail(null);
        }
        if(StringUtility.isEmpty(user.getMobileNumber())){
            user.setMobileNumber(null);
        }
        if(StringUtility.isEmpty(user.getDescription())){
            user.setDescription(null);
        }
        if(StringUtility.isEmpty(user.getActivationCode())){
            user.setActivationCode(null);
        }

        String passwd = user.getPassword();
        
        adminEjb.saveUser(user);
        if (!StringUtility.empty(oldPassword).equals(passwd)) {
            adminEjb.changePassword(user.getUserName(), passwd);
        }
        search();
    }
}
