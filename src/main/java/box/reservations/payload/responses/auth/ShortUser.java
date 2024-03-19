package box.reservations.payload.responses.auth;

import box.reservations.entities.Role;
import box.reservations.entities.User;
import lombok.Getter;

@Getter
public class ShortUser {
    private final Long id;
    private final String email;
    private final Role role;

    ShortUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

}
