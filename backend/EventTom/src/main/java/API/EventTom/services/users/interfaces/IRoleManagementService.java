package API.EventTom.services.users.interfaces;

import API.EventTom.models.user.Role;
import API.EventTom.models.user.Roles;
import API.EventTom.models.user.User;

import java.util.Set;

public interface IRoleManagementService {
    void assignRole(Long userId, Roles roleName);
    void assignRoles(Long userId, Set<Roles> roles);
    void removeRole(Long userId, Roles role);
    Set<Role> getDefaultRoles();
    Set<Role> getRolesByNames(Set<Roles> roleNames);
    void validateUserRoles(User user);
    Set<Role> getUserRoles(Long userId);
}