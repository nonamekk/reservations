package box.reservations.services.auth;

import box.reservations.entities.Ticket;
import box.reservations.entities.User;
import box.reservations.repositories.UserRepository;
import box.reservations.payload.requests.auth.RegistrationRequest;
import box.reservations.services.admin.AdminControlsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AdminControlsService adminControlsService;

    public User addUser(@NotNull RegistrationRequest registrationRequest) throws Exception {
        User newUser = new User();
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPassword(encoder.encode(registrationRequest.getPassword()));

        Ticket ticket = adminControlsService.getTicket(registrationRequest.getKey());

        newUser.setRole(ticket.getRole());
        User user = userRepository.save(newUser);
        adminControlsService.voidTicket(ticket);
        return user;
    }
}
