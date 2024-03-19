package box.reservations.services.client.returns;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllReservationsClientViewAll {
    List<ReservationSimplifiedWithSubCategory> completed_reservations;
    List<ReservationSimplifiedWithSubCategory> active_reservations;
    List<ReservationSimplifiedWithSubCategory> inactive_reservations;
}
