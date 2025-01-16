package API.EventTom.services.users;

import API.EventTom.exceptions.notFoundExceptions.RoleNotFoundException;
import API.EventTom.exceptions.notFoundExceptions.UserNotFoundException;
import API.EventTom.models.user.Role;
import API.EventTom.models.user.Roles;
import API.EventTom.models.user.User;
import API.EventTom.models.user.UserType;
import API.EventTom.repositories.RoleRepository;
import API.EventTom.repositories.UserRepository;
import API.EventTom.services.users.interfaces.IRoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RoleManagementServiceImpl implements IRoleManagementService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void assignRole(Long userId, Roles roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        validateUserRoles(user);
        user.addRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, Set<Roles> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        validateUserRoles(user);

        Set<Role> roleEntities = getRolesByNames(roles);
        user.setRoles(roleEntities);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRole(Long userId, Roles roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Role roleToRemove = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        if (user.getRoles().size() <= 1 && user.getRoles().contains(roleToRemove)) {
            Role defaultRole = getRoleByName(Roles.NONE);
            user.getRoles().add(defaultRole);
        }

        user.getRoles().remove(roleToRemove);
        userRepository.save(user);
    }

    @Override
    public Set<Role> getDefaultRoles() {
        Role defaultRole = getRoleByName(Roles.NONE);
        return Set.of(defaultRole);
    }

    @Override
    public Set<Role> getRolesByNames(Set<Roles> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return getDefaultRoles();
        }

        return roleNames.stream()
                .map(this::getRoleByName)
                .collect(Collectors.toSet());
    }

    private Role getRoleByName(Roles roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
    }

    public void validateUserRoles(User user) {
        if (user.getUserType() == UserType.CUSTOMER) {
            Set<Role> invalidRoles = user.getRoles().stream()
                    .filter(role -> Roles.isEmployeeOnly(role.getName()))
                    .collect(Collectors.toSet());

            if (!invalidRoles.isEmpty()) {
                throw new IllegalStateException("Customer has invalid employee roles: " +
                        invalidRoles.stream()
                                .map(role -> role.getName().getDisplayName())
                                .collect(Collectors.joining(", ")));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId))
                .getRoles();
    }

}