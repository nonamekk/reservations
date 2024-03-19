package box.reservations.services.client.returns;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
import java.util.List;

@Data
@AllArgsConstructor
public class AllReservations {
    Optional<ReservationSimplifiedWithSubCategory> reservation_of_user;
    List<ReservationSimplified> reservations_active;
    List<ReservationSimplified> reservations_inactive;
}
