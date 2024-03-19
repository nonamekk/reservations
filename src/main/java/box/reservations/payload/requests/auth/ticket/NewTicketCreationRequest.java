package box.reservations.payload.requests.auth.ticket;

import box.reservations.entities.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewTicketCreationRequest {

    @NotNull(message = "role must no be null")
    private Role role;

    @NotNull(message = "uses must not be null")
    private Integer uses;
}
