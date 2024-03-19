package box.reservations.services.employee.returns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationsEmployeeAll {
    List<ReservationsEmployeeSimplified> active_reservations;
    List<ReservationsEmployeeSimplified> inactive_reservations;
}
