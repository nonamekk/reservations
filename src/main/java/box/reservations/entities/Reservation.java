package box.reservations.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Short reservationNumber;

    @ManyToOne
    @JoinColumn(name = "registered_under_subcategory_id")
    private SubCategory registeredUnderSubCategory;

    @ManyToOne
    @JoinColumn(name = "assigned_to_room_id")
    private Room assignedToRoom;

    private String userReservationOwner;

    @ManyToOne
    @JoinColumn(name = "user_reservation_server_user_id")
    private User userReservationServer;

    private Status reservationStatus;

    public Reservation(
            Short reservationNumber,
            String ownerUuid,
            SubCategory subCategory) {
        this.reservationNumber = reservationNumber;
        this.userReservationOwner = ownerUuid;
        this.registeredUnderSubCategory = subCategory;
        this.reservationStatus = Status.INACTIVE;
    }
}
