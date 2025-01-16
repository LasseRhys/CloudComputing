package API.EventTom.controller.users;

import API.EventTom.models.user.Roles;
import API.EventTom.services.users.interfaces.IRoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleManagementController {
    private final IRoleManagementService roleManagementService;



    @PostMapping("/assign/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> assignRole(
            @PathVariable Long userId,
            @RequestBody Roles role) {
        roleManagementService.assignRole(userId, role);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-multiple/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> assignRoles(
            @PathVariable Long userId,
            @RequestBody Set<Roles> roles) {
        roleManagementService.assignRoles(userId, roles);
        return ResponseEntity.ok().build();
    }
}