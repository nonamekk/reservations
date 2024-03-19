package box.reservations.services;

import box.reservations.entities.Reservation;
import box.reservations.entities.User;
import box.reservations.repositories.ReservationRepository;
import box.reservations.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserControlService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    ReservationRepository reservationRepository;

    /**
     * Deletes a user and their reservations based on the specified email.
     *
     * @param  email   the email of the user to be deleted
     * @throws Exception   if the user with the specified email is not found
     */
    public void selfDeleteUser(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new Exception("User with specified email not found"));

        List<Reservation> reservationList = reservationRepository.findByUserReservationServer(user);
        for (Reservation reservation : reservationList) {
            reservationRepository.delete(reservation);
        }

        userRepository.delete(user);
    }

    /**
     * Updates the password for the user with the specified email.
     *
     * @param  email     the email of the user
     * @param  password  the new password
     * @throws Exception if the user with the specified email is not found
     */
    public void selfChangePassword(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new Exception("User with specified email not found"));
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }
}
