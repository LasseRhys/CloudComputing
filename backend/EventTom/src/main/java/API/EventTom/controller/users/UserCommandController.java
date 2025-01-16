package API.EventTom.controller.users;


import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.dto.request.UserEditProfileRequestDTO;
import API.EventTom.dto.response.UserDTO;
import API.EventTom.services.users.interfaces.IUserCommandService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user/command")
@AllArgsConstructor
public class UserCommandController {

    private final IUserCommandService userCommandService;

    @PutMapping("/edit")
    public ResponseEntity<UserDTO> editUser(@Valid @RequestBody UserEditProfileRequestDTO userDTO, @AuthenticatedUserId Long userId) {
        return ResponseEntity.ok(userCommandService.editProfile(userDTO, userId));
    }
}
