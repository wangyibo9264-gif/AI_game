package com.rumortown.user;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public GuestPlayerDto createGuest() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.guest("guest-" + System.currentTimeMillis(), now);
        return GuestPlayerDto.from(userRepository.save(user));
    }
}
