package box.reservations.repositories;
import box.reservations.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRegisteredUnderSubCategoryId(Long subcategoryId);

    List<Reservation> findByUserReservationOwner(String userReservationOwner);

    List<Reservation> findByRegisteredUnderSubCategoryInAndReservationStatus(List<SubCategory> subcategories, Status status);

    List<Reservation> findByRegisteredUnderSubCategoryInAndUserReservationServerAndReservationStatus(List<SubCategory> subcategories, User userReservationServer, Status status);
    Optional<Reservation> findByUserReservationServerAndReservationStatus(User userReservationServer, Status status);
    List<Reservation> findByUserReservationOwnerAndReservationStatus(String userReservationOwner, Status status);

    List<Reservation> findByRegisteredUnderSubCategoryIdAndReservationStatus(Long subcategoryId, Status status);

    List<Reservation> findByAssignedToRoom(Room room);

    List<Reservation> findByUserReservationServer(User userReservationServer);
    @Query("SELECT r FROM Reservation r WHERE r.registeredUnderSubCategory.id = :subcategoryId AND r.userReservationOwner = :userReservationOwner AND r.reservationStatus = 0 OR r.reservationStatus = 1")
        // Please note that this Query uses numeric values for Status: 0 = INACTIVE, 1 = ACTIVE
    Optional<Reservation> findReservationsBySubcategoryIdAndUserReservationOwner(Long subcategoryId, String userReservationOwner);

//    void deleteAll();
}
