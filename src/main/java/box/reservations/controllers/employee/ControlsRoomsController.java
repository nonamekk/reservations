package box.reservations.controllers.employee;

import box.reservations.payload.responses.room.AllRoomsResponse;
import box.reservations.payload.responses.room.RoomSimplified;
import box.reservations.security.utils.JwtUtils;
import box.reservations.services.employee.EmployeeRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/employee/rooms")
public class ControlsRoomsController {

    @Autowired
    EmployeeRoomService employeeRoomService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<AllRoomsResponse> getAllRooms(
            @CookieValue("__Host-auth-token") String cookie
    ) throws Exception {
        Object[] roomsIncludeUserAssigned = employeeRoomService.getAllRoomsIncludeUserAssigned(
                jwtUtils.extractSubject(cookie)
        );
        AllRoomsResponse response = new AllRoomsResponse(
                (RoomSimplified) roomsIncludeUserAssigned[0],
                (List<RoomSimplified>) roomsIncludeUserAssigned[1]
        );
        return ResponseEntity.ok().body(response);
    }
}
