package box.reservations.services.client;

import box.reservations.entities.Reservation;
import box.reservations.entities.Status;
import box.reservations.entities.SubCategory;
import box.reservations.repositories.ReservationRepository;
import box.reservations.repositories.SubCategoryRepository;
import box.reservations.services.client.returns.AllReservations;
import box.reservations.services.client.returns.AllReservationsClientViewAll;
import box.reservations.services.client.returns.ReservationSimplified;
import box.reservations.services.client.returns.ReservationSimplifiedWithSubCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ClientReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    /**
     * A method to generate a session cookie.
     *
     * @return         	the generated session cookie
     */
    public ResponseCookie generateSessionCookie() {
        return createCookie(UUID.randomUUID().toString());
    }

    /**
     * Generates a session cookie for the specified owner UUID.
     *
     * @param  ownerUuid  the UUID of the owner
     * @return            the generated session cookie
     */
    public ResponseCookie generateSessionCookie(String ownerUuid) {
        return createCookie(ownerUuid);
    }

    /**
     * Create a reservation for a specified owner UUID and subcategory ID.
     *
     * @param  ownerUuid      the UUID of the owner
     * @param  subCategoryId  the ID of the subcategory
     * @return                the reservation number
     */
    public Short createReservation(String ownerUuid, Long subCategoryId) throws Exception {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(() ->
                new Exception("No subcategory found with specified id"));
        // need to check whether this ownerUuid have ACTIVE or INACTIVE reservations on this subcategory
        if (reservationRepository.findReservationsBySubcategoryIdAndUserReservationOwner(
                subCategoryId, ownerUuid
        ).orElse(null) == null) {
            Reservation newReservation = new Reservation(
                    generateNextReservationNumber(subCategoryId),
                    ownerUuid,
                    subCategory
            );
            reservationRepository.save(newReservation);
            log.info("New reservation is created by user");
            return newReservation.getReservationNumber();
        } else {
            throw new Exception("Reservation already made, await your call");
        }

    }

    /**
     * A method to list all reservations that belong to a specific owner for a given subcategory.
     * Provides a reservation that belongs to the user and all active, inactive reservations.
     *
     * @param  ownerUuid      the UUID of the owner
     * @param  subCategoryId  the ID of the sub-category
     * @return                an object containing all reservations
     */
    public AllReservations listAllReservations(String ownerUuid, Long subCategoryId) {
        AllReservations returning = new AllReservations(
                findAllThatBelongToUser(ownerUuid, subCategoryId),
                findAllActive(subCategoryId),
                findAllInactive(subCategoryId)
        );
        log.info("List of all reservations + one belonging to user is found");
        return returning;
    }

    /**
     * Finds all reservations that belong to a specific owner for a given subcategory.
     *
     * @param  ownerUuid     the UUID of the user
     * @param  subCategoryId the ID of the sub-category
     * @return               an optional containing the reservation simplified with sub-category, or empty if not found
     */
    private Optional<ReservationSimplifiedWithSubCategory> findAllThatBelongToUser(String ownerUuid, Long subCategoryId) {
        Reservation reservation = reservationRepository.findReservationsBySubcategoryIdAndUserReservationOwner(
                subCategoryId,
                ownerUuid
        ).orElse(null);
        if (reservation == null) {
            return Optional.empty();
        }
        ReservationSimplifiedWithSubCategory reservationSimplified = new ReservationSimplifiedWithSubCategory(
                reservation
        );
        return Optional.of(reservationSimplified);
    }

    /**
     * Simplifies a list of reservations to a list of simplified reservations.
     *
     * @param  reservationList   the list of reservations to be simplified
     * @return                  the list of simplified reservations
     */
    private List<ReservationSimplified> simplifyList(List<Reservation> reservationList) {
        List<ReservationSimplified> reservationSimplifiedList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            reservationSimplifiedList.add(new ReservationSimplified(reservation));
        }
        return reservationSimplifiedList;
    }

    /**
     * Find all active reservations for a given subcategory.
     *
     * @param  subCategoryId  the ID of the subcategory
     * @return                a list of simplified active reservations
     */
    private List<ReservationSimplified> findAllActive(Long subCategoryId) {
        List<Reservation> reservationList = reservationRepository.findByRegisteredUnderSubCategoryIdAndReservationStatus(
                subCategoryId, Status.ACTIVE
        );

        return simplifyList(reservationList);
    }

    /**
     * Finds all inactive reservations for a given subcategory ID.
     *
     * @param  subCategoryId  the ID of the subcategory
     * @return                a list of simplified reservations
     */
    private List<ReservationSimplified> findAllInactive(Long subCategoryId) {
        List<Reservation> reservationList = reservationRepository.findByRegisteredUnderSubCategoryIdAndReservationStatus(
                subCategoryId, Status.INACTIVE
        );
        return simplifyList(reservationList);
    }


    /**
     * Lists all reservations of a specific owner.
     *
     * @param  ownerUuid  the UUID of the owner
     * @return            all reservations for the owner
     */
    public AllReservationsClientViewAll listAllReservations(String ownerUuid) {
        AllReservationsClientViewAll returning = new AllReservationsClientViewAll(
                findAllCompleted(ownerUuid),
                findAllActive(ownerUuid),
                findAllInactive(ownerUuid)
        );
        log.info("Personal view of reservations for specific user is found");
        return returning;
    }

    /**
     * Simplifies a list of reservations with subcategories.
     *
     * @param  reservationList  the list of reservations to be simplified
     * @return                  the simplified list of reservations with subcategories
     */
    private List<ReservationSimplifiedWithSubCategory> simplifyListWithSubcategories(List<Reservation> reservationList) {
        List<ReservationSimplifiedWithSubCategory> reservationSimplifiedList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            reservationSimplifiedList.add(new ReservationSimplifiedWithSubCategory(reservation));
        }
        return reservationSimplifiedList;
    }

    /**
     * Find all completed reservations of a given owner.
     *
     * @param  ownerUuid  the UUID of the owner
     * @return            a list of simplified reservations with subcategories
     */
    private List<ReservationSimplifiedWithSubCategory> findAllCompleted(String ownerUuid) {
        List<Reservation> reservationList = reservationRepository.findByUserReservationOwnerAndReservationStatus(
                ownerUuid, Status.COMPLETED
        );
        return simplifyListWithSubcategories(reservationList);
    }

    /**
     * Find all active reservations of a given owner.
     *
     * @param  ownerUuid  the UUID of the owner
     * @return            a list of simplified reservations with subcategories
     */
    private List<ReservationSimplifiedWithSubCategory> findAllActive(String ownerUuid) {
        List<Reservation> reservationList = reservationRepository.findByUserReservationOwnerAndReservationStatus(
                ownerUuid, Status.ACTIVE
        );
        return simplifyListWithSubcategories(reservationList);
    }

    /**
     * Finds all inactive reservations of a given owner.
     *
     * @param  ownerUuid  the UUID of the owner
     * @return            a list of simplified reservations with subcategories
     */
    private List<ReservationSimplifiedWithSubCategory> findAllInactive(String ownerUuid) {
        List<Reservation> reservationList = reservationRepository.findByUserReservationOwnerAndReservationStatus(
                ownerUuid, Status.INACTIVE
        );
        return simplifyListWithSubcategories(reservationList);
    }

    /**
     * Generates the next reservation number based on the given subcategory ID.
     *
     * @param  subCategoryId  the ID of the subcategory for which the reservation number is generated
     * @return                the next reservation number
     */
    public Short generateNextReservationNumber(Long subCategoryId) {
        List<Reservation> reservationList = reservationRepository.findByRegisteredUnderSubCategoryId(subCategoryId);
        if (reservationList.isEmpty()) {
            // it is first reservation today
            return 1;
        }
        if (reservationList.getLast().getReservationNumber() == 999) {
            return 1;
        }
        return (short) (reservationList.getLast().getReservationNumber() + 1);
    }

    /**
     * Creates a response cookie for the specified owner UUID.
     *
     * @param  ownerUuid  the owner's UUID
     * @return            the newly created response cookie
     */
    private ResponseCookie createCookie(String ownerUuid) {
        ResponseCookie returning = ResponseCookie
                .from("__Host-session-token", ownerUuid)
                .path("/")
                .domain(null)
                .secure(true)
                .httpOnly(true)
                .maxAge(24 * 60 * 60)
                .build();
        log.info("New cookie for client is created");
        return returning;
    }
}

