package box.reservations.services.client.returns;

import box.reservations.entities.Reservation;
import box.reservations.entities.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSimplifiedWithSubCategory {
    private Short reservationNumber;
    private String roomName;

    private SubCategory subCategory;

    public ReservationSimplifiedWithSubCategory(Reservation reservation) {
        this.reservationNumber = reservation.getReservationNumber();
        this.roomName = (reservation.getAssignedToRoom() == null ? "" : reservation.getAssignedToRoom().getName());
        this.subCategory = reservation.getRegisteredUnderSubCategory();
    }

}
