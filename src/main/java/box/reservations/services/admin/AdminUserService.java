package box.reservations.services.admin;

import box.reservations.entities.Role;
import box.reservations.entities.User;
import box.reservations.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    /**
     * Creates an admin user with email "admin@admin.com" and password "admin".
     */
    public void createAdmin() {
        User user = new User();
        user.setEmail("admin@admin.com");

        String password = "admin";
        user.setPassword(encoder.encode(password));

        user.setRole(Role.ADMIN);

        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            userRepository.save(user);
        }
        log.info("User admin is created. Email: " + user.getEmail() + " password: " + password);
    }
}
