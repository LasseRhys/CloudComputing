package API.EventTom.controller.users;

import API.EventTom.dto.RoleDTO;
import API.EventTom.services.users.interfaces.IRoleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles/query")
@RequiredArgsConstructor
public class RoleQueryController {

    private final IRoleQueryService roleQueryService;


    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleQueryService.getAll());
    }
}
