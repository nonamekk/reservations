package box.reservations.services.employee;

import box.reservations.entities.*;
import box.reservations.repositories.ReservationRepository;
import box.reservations.repositories.RoomRepository;
import box.reservations.repositories.UserRepository;
import box.reservations.services.employee.returns.ReservationsEmployeeAll;
import box.reservations.services.employee.returns.ReservationsEmployeeSimplified;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class EmployeeControlsReservationsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    ReservationRepository reservationRepository;

    /**
     * Deletes all reservations and logs the action.
     *
     */
    public void deleteAll() {
        reservationRepository.deleteAll();
        log.info("All reservations were deleted");
    }

    /**
     * Retrieves all reservations. Active reservations are associated with the employee email.
     *
     * @param  email the email of the user
     * @return       active and inactive list of reservations for employee requesting
     */
    public ReservationsEmployeeAll getAllReservations(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new Exception("No user found with specified email"));
        Room room = roomRepository.findByUserId(user.getId()).orElseThrow(()->
                new Exception("No room found with specified user. Assign to room first"));
        List<SubCategory> subCategoryList = room.getSubcategoriesAssigned();

        return getAllReservations(subCategoryList, user);
    }

    /**
     * Request the next reservation for the specified employee email.
     *
     * @param  email	email of the user
     * @return         	updated reservations list of active and inactive reservations
     */
    public ReservationsEmployeeAll requestNextReservation(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new Exception("No user found with specified email"));
        Room room = roomRepository.findByUserId(user.getId()).orElseThrow(()->
                new Exception("No room found with specified user. Assign to room first"));
        List<SubCategory> subCategoryList = room.getSubcategoriesAssigned();
        Reservation activeReservation = reservationRepository
                .findByUserReservationServerAndReservationStatus(
                user, Status.ACTIVE
        ).orElse(null);
        // complete reservation if was active
        if (activeReservation != null) {
            activeReservation.setReservationStatus(Status.COMPLETED);
            reservationRepository.save(activeReservation);
            log.info("Previously active reservation is now completed");
        }

        // take new reservations from inactive
        List<Reservation> inactiveReservations = reservationRepository
                .findByRegisteredUnderSubCategoryInAndReservationStatus(
                subCategoryList, Status.INACTIVE
        );

        // check if there are any
        if (inactiveReservations.isEmpty()) {
            return getAllReservations(subCategoryList, user);
        }

        // set first reservation as active
        Reservation nextReservation = inactiveReservations.getFirst();
        nextReservation.setReservationStatus(Status.ACTIVE);
        nextReservation.setUserReservationServer(user);
        nextReservation.setAssignedToRoom(room);
        reservationRepository.save(nextReservation);
        log.info("Next reservation is now active");
        // calling to database for additional obtaining lists is bad. We already have them,
        //  but we need to update them.

        inactiveReservations.removeFirst();
        List<Reservation> activeReservations = new ArrayList<>();
        activeReservations.add(nextReservation);

        List<ReservationsEmployeeSimplified> inactiveReservationsSimplifiedList = prepareReservationsList(inactiveReservations);
        List<ReservationsEmployeeSimplified> activeReservationsSimplifiedList = prepareReservationsList(activeReservations);
        log.info("Reservations on employee side were prepared");

        return new ReservationsEmployeeAll(
                activeReservationsSimplifiedList,
                inactiveReservationsSimplifiedList
        );
    }

    /**
     * Retrieves active and inactive reservations for a given list of subcategories and employee server.
     *
     * @param  subcategories   the list of subcategories
     * @param  employeeServer  the employee server user
     * @return                 the reservations for the employee
     */
    private ReservationsEmployeeAll getAllReservations(List<SubCategory> subcategories, User employeeServer) {
        ReservationsEmployeeAll returning = new ReservationsEmployeeAll(
                getActiveReservations(subcategories, employeeServer),
                getInactiveReservations(subcategories)
        );
        log.info("Reservations on employee side were obtained by user");
        return returning;
    }

    /**
     * Retrieves a list of inactive reservations for the given subcategories.
     *
     * @param  subcategories   the list of subcategories to filter the reservations
     * @return                 a list of simplified reservations for employee
     */
    private List<ReservationsEmployeeSimplified> getInactiveReservations(List<SubCategory> subcategories) {
        List<Reservation> reservations = reservationRepository
                .findByRegisteredUnderSubCategoryInAndReservationStatus(
                subcategories, Status.INACTIVE
        );

        return prepareReservationsList(reservations);
    }

    /**
     * Retrieves active reservations for a given list of subcategories and employee server.
     *
     * @param  subcategories    the list of subcategories
     * @param  employeeServer   the employee server
     * @return                  a list of simplified reservations for the given employee server
     */
    private List<ReservationsEmployeeSimplified> getActiveReservations(List<SubCategory> subcategories, User employeeServer) {
        Optional<Reservation> optionalReservation = reservationRepository
                .findByUserReservationServerAndReservationStatus(
                employeeServer, Status.ACTIVE
        );

        List<Reservation> listToReturn = new ArrayList<>();
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            listToReturn.add(reservation);
        }

        return prepareReservationsList(listToReturn);
    }

    /**
     * Prepare a simplified list of reservations from the given list of reservations.
     *
     * @param  reservations  the list of reservations to be simplified
     * @return              the simplified list of reservations suited for employee
     */
    private List<ReservationsEmployeeSimplified> prepareReservationsList(List<Reservation> reservations) {
        List<ReservationsEmployeeSimplified> reservationsSimplifiedList = new ArrayList<>();
        for (Reservation reservation : reservations) {
            reservationsSimplifiedList.add(new ReservationsEmployeeSimplified(
                    reservation.getReservationNumber(),
                    reservation.getRegisteredUnderSubCategory()
            ));
        }
        return reservationsSimplifiedList;
    }
}
