package box.reservations.controllers.employee;

import box.reservations.security.utils.JwtUtils;
import box.reservations.services.employee.EmployeeControlsReservationsService;
import box.reservations.services.employee.returns.ReservationsEmployeeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee/controls/reservation")
public class ControlsReservationController {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private EmployeeControlsReservationsService employeeControlsReservationsService;

    @GetMapping
    public ResponseEntity<ReservationsEmployeeAll> getReservations(
            @CookieValue("__Host-auth-token") String cookie) throws Exception {
        return ResponseEntity.ok().body(employeeControlsReservationsService.getAllReservations(
                jwtUtils.extractSubject(cookie))
        );
    }

    @PostMapping ResponseEntity<ReservationsEmployeeAll> requestNextReservation(
            @CookieValue("__Host-auth-token") String cookie) throws Exception {
        return ResponseEntity.ok().body(employeeControlsReservationsService.requestNextReservation(
                jwtUtils.extractSubject(cookie))
        );
    }

}
