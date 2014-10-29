package org.sola.clients.web.admin.beans.security;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.sola.clients.web.admin.beans.AbstractBackingBean;
import org.sola.clients.web.admin.beans.helpers.ErrorKeys;
import org.sola.clients.web.admin.beans.helpers.MessageProvider;
import org.sola.clients.web.admin.beans.language.LanguageBean;
import org.sola.common.StringUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejbs.admin.businesslogic.AdminEJBLocal;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Group;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.GroupRole;
import org.sola.services.ejb.refdata.businesslogic.RefDataEJBLocal;
import org.sola.services.ejb.refdata.entities.Role;

/**
 * Contains methods and properties to manage {@link Group}
 */
@Named
@ViewScoped
public class GroupPageBean extends AbstractBackingBean {
    private Group group;
    private List<Group> groups;
    private Role[] roles;
    private String[] selectedRoleCodes;
    
    @Inject
    MessageProvider msgProvider;

    @Inject
    private LanguageBean languageBean;
    
    @EJB
    RefDataEJBLocal refEjb;
    
    @EJB
    SystemEJBLocal systemEjb;
    
    @EJB
    AdminEJBLocal adminEjb;

    public Group getGroup() {
        return group;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public GroupRole[] getGroupRoles(Group g){
        if(g == null || g.getGroupRoles() == null){
            return new GroupRole[]{};
        }
        return g.getGroupRoles().toArray(new GroupRole[g.getGroupRoles().size()]);
    }
    
    public String[] getSelectedRoleCodes() {
        return selectedRoleCodes;
    }

    public void setSelectedRoleCodes(String[] selectedRoleCodes) {
        this.selectedRoleCodes = selectedRoleCodes;
    }

    public Role[] getRoles() {
        return roles;
    }
    
    @PostConstruct
    private void init() {
        loadList();
        List<Role> rolesList = systemEjb.getCodeEntityList(Role.class, languageBean.getLocale());
        if(rolesList != null){
            roles = rolesList.toArray(new Role[rolesList.size()]);
        }
    }
    
    private void loadList(){
        groups = adminEjb.getGroups();
    }
    
    public String getRoleName(String code) {
        if (code != null && roles != null) {
            for (Role item : roles) {
                if (item.getCode().equalsIgnoreCase(code)) {
                    return item.getDisplayValue();
                }
            }
        }
        return "";
    }
    
    public void loadEntity(String id) {
        if (StringUtility.isEmpty(id)) {
            group = new Group();
            group.setId(UUID.randomUUID().toString());
            group.setGroupRoles(new ArrayList<GroupRole>());
        } else {
            group = adminEjb.getGroup(id);
        }

        // Select/unselect roles
        List<Role> rolesToSelect = new ArrayList<>();
        if (group.getGroupRoles() != null) {
            for (GroupRole groupRole : group.getGroupRoles()) {
                if (roles != null) {
                    for (Role role : roles) {
                        if (groupRole.getRoleCode().equalsIgnoreCase(role.getCode())) {
                            rolesToSelect.add(role);
                            break;
                        }
                    }
                }
            }
        }
        selectedRoleCodes = new String[rolesToSelect.size()];
        int i=0;
        for(Role role : rolesToSelect){
            selectedRoleCodes[i] = role.getCode();
            i+=1;
        }
    }

    public void deleteEntity(Group group) {
        group.setEntityAction(EntityAction.DELETE);
        adminEjb.saveGroup(group);
        loadList();
    }

    public void saveEntity() throws Exception {
        if (group != null) {
            // Validate
            String errors = "";
            if (StringUtility.isEmpty(group.getName())) {
                errors += msgProvider.getErrorMessage(ErrorKeys.GROUP_PAGE_FILL_IN_NAME) + "\r\n";
            }
            if (selectedRoleCodes == null || selectedRoleCodes.length < 1) {
                errors += msgProvider.getErrorMessage(ErrorKeys.GROUP_PAGE_SELECT_ROLE) + "\r\n";
            }
            
            if (!errors.equals("")) {
                throw new Exception(errors);
            }

            // Prepare roles related to the group
            // Delete
            if (group.getGroupRoles()!= null) {
                for (GroupRole groupRole : group.getGroupRoles()) {
                    boolean found = false;
                    if (selectedRoleCodes != null) {
                        for (String roleCode : selectedRoleCodes) {
                            if (groupRole.getRoleCode().equalsIgnoreCase(roleCode)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        groupRole.setEntityAction(EntityAction.DELETE);
                    }
                }
            }
            
            // Add
            if (selectedRoleCodes != null) {
                for (String roleCode : selectedRoleCodes) {
                    boolean found = false;
                    if (group.getGroupRoles() != null) {
                        for (GroupRole groupRole : group.getGroupRoles()) {
                            if (groupRole.getRoleCode().equalsIgnoreCase(roleCode)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if(!found){
                        GroupRole groupRole = new GroupRole();
                        groupRole.setGroupId(group.getId());
                        groupRole.setRoleCode(roleCode);
                        group.getGroupRoles().add(groupRole);
                    }
                }
            }

            adminEjb.saveGroup(group);
            loadList();
        }
    }
}
