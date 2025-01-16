package API.EventTom.services.users;


import API.EventTom.dto.request.UserEditProfileRequestDTO;
import API.EventTom.dto.response.UserDTO;
import API.EventTom.exceptions.notFoundExceptions.UserNotFoundException;
import API.EventTom.exceptions.userExceptions.EmailAlreadyExistsException;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.user.User;
import API.EventTom.repositories.UserRepository;
import API.EventTom.services.users.interfaces.IUserCommandService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class UserCommandServiceImpl implements IUserCommandService {
    private final UserRepository userRepository;
    private final StandardDTOMapper standardDTOMapper;

    @Override
    public UserDTO editProfile(UserEditProfileRequestDTO userEditProfileRequestDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + userId + " could not be found" ));
        if (!user.getEmail().equals(userEditProfileRequestDTO.email())) {
            if (userRepository.existsByEmail(userEditProfileRequestDTO.email())) {
                throw new EmailAlreadyExistsException("Email " + userEditProfileRequestDTO.email() + " is already in use");
            }
        }
        user.setEmail(userEditProfileRequestDTO.email());
        user.setFirstName(userEditProfileRequestDTO.firstName());
        user.setLastName(userEditProfileRequestDTO.lastName());

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        return standardDTOMapper.mapUserToUserDTO(user);
    }
}

