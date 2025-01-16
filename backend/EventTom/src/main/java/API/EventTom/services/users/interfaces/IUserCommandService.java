package API.EventTom.services.users.interfaces;

import API.EventTom.dto.request.UserEditProfileRequestDTO;
import API.EventTom.dto.response.UserDTO;

public interface IUserCommandService {
    UserDTO editProfile(UserEditProfileRequestDTO userDTO, Long userId);
}
