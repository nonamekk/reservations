package box.reservations.services.client.returns;

import box.reservations.entities.Reservation;
import lombok.Data;

@Data
public class ReservationSimplified {
    private Short reservationNumber;
    private String roomName;

    public ReservationSimplified(Reservation reservation) {
        this.reservationNumber = reservation.getReservationNumber();
        this.roomName = (reservation.getAssignedToRoom() == null ? "" : reservation.getAssignedToRoom().getName());
    }
}


