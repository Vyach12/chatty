package ru.gusarov.messenger.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.dto.UserDTO;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.repositories.UserRepository;
import ru.gusarov.messenger.utils.UserException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(
                        ()-> new UserException("User this name: " + username + " does not exist")
                );
    }

    public UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setRole(user.getRole().getName());
        return userDTO;
    }
}
