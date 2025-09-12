package spring.beautiq.domain.user.service;


import java.util.UUID;
import org.springframework.stereotype.Service;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.repository.UserRepository;

@Service

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //회원 존재하는지 검증
    public void JoinProcess(UserRequest userRequest) {
        String name = userRequest.getName();

        Boolean isExist = userRepository.existsById(UUID.fromString(name));

        if (isExist) {

            return;
        }


    }
}
