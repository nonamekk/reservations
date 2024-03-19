package box.reservations.components;

import box.reservations.services.employee.EmployeeControlsReservationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationCleanup {

    @Autowired
    EmployeeControlsReservationsService controlsReservationsService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteReservationsAtMidnight() {
        controlsReservationsService.deleteAll();
    }
}
