package box.reservations.services.employee.returns;

import box.reservations.entities.Reservation;
import box.reservations.entities.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsEmployeeSimplified {
    Short reservation_number;
    SubCategory subcategory;

    public ReservationsEmployeeSimplified(Reservation reservation) {
        this.reservation_number = reservation.getReservationNumber();
        this.subcategory = reservation.getRegisteredUnderSubCategory();
    }
}
