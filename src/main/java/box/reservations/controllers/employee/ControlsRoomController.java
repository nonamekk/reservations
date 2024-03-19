package box.reservations.controllers.employee;

import box.reservations.entities.Room;
import box.reservations.payload.requests.ByIdRequest;
import box.reservations.payload.requests.room.RoomIdNameRequest;
import box.reservations.payload.requests.room.RoomIdRequest;
import box.reservations.payload.requests.room.RoomIdSubCategoryIdRequest;
import box.reservations.payload.requests.room.RoomNameSubCategoryIdRequest;
import box.reservations.security.utils.JwtUtils;
import box.reservations.services.employee.EmployeeRoomService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/employee/controls/room")
public class ControlsRoomController {

    @Autowired
    EmployeeRoomService employeeRoomService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping()
    public ResponseEntity<?> createRoom(
            @RequestBody @Validated @NotNull RoomNameSubCategoryIdRequest requestBody) throws Exception {
        employeeRoomService.createRoom(
                requestBody.getName(),
                requestBody.getSubcategory_id()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<?> removeRoom(
            @RequestBody @Validated @NotNull ByIdRequest requestBody) throws Exception {
        employeeRoomService.deleteRoom(requestBody.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<?> updateRoom(
            @RequestBody @Validated @NotNull RoomIdNameRequest requestBody) throws Exception {
        employeeRoomService.updateRoom(requestBody.getRoom_id(), requestBody.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subcategory")
    public ResponseEntity<Room> assignAnotherSubCategory(
            @RequestBody @Validated @NotNull RoomIdSubCategoryIdRequest requestBody) throws Exception {
        employeeRoomService.assignAnotherSubCategoryToRoom(
                requestBody.getRoom_id(),
                requestBody.getSubcategory_id()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user")
    public ResponseEntity<?> assignSelfToRoom(
            @RequestBody @Validated @NotNull RoomIdRequest requestBody,
            @CookieValue("__Host-auth-token") String cookie
            ) throws Exception {

        employeeRoomService.assignAnotherUserToRoom(
                requestBody.getRoom_id(),
                jwtUtils.extractSubject(cookie)
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/subcategory")
    public ResponseEntity<?> deleteSubCategory(
            @RequestBody @Validated @NotNull RoomIdSubCategoryIdRequest requestBody) throws Exception {
        employeeRoomService.removeSubCategoryFromRoom(
                requestBody.getRoom_id(),
                requestBody.getSubcategory_id()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteSelfFromRoom(
            @CookieValue("__Host-auth-token") String cookie
            ) throws Exception {
        employeeRoomService.removeUserFromRoom(
                jwtUtils.extractSubject(cookie)
        );

        return ResponseEntity.ok().build();
    }


}
