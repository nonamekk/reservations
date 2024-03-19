package box.reservations.controllers.client;

import box.reservations.payload.requests.reservations.ReservationSubCategoryId;
import box.reservations.payload.responses.reservation.ReservationNumberResponse;
import box.reservations.services.client.ClientReservationService;
import box.reservations.services.client.returns.AllReservations;
import box.reservations.services.client.returns.AllReservationsClientViewAll;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/reservations")
public class ReservationController {

    @Autowired
    ClientReservationService clientReservationService;

    @GetMapping("/subcategories")
    public ResponseEntity<AllReservations> getAllReservations(
            @RequestParam Long subcategory_id,
            @CookieValue(name = "__Host-session-token", required = false) String cookie
    ) {
        String ownerUuid = "";
        if (cookie != null) {
            ownerUuid = cookie;
        }
        return ResponseEntity.ok().body(clientReservationService.listAllReservations(
                ownerUuid, subcategory_id)
        );
    }

    @GetMapping
    public ResponseEntity<AllReservationsClientViewAll> getAllReservations(
            @CookieValue(name = "__Host-session-token", required = false) String cookie
    ) {
        String ownerUuid = "";
        if (cookie != null) {
            ownerUuid = cookie;
        }

        return ResponseEntity.ok().body(clientReservationService.listAllReservations(ownerUuid));
    }

    @PostMapping
    public ResponseEntity<ReservationNumberResponse> makeReservation(
            @RequestBody @Validated @NotNull ReservationSubCategoryId requestBody,
            @CookieValue(name = "__Host-session-token", required = false) String cookie) throws Exception {

        ResponseCookie newCookie;
        if (cookie == null) {
            newCookie = clientReservationService.generateSessionCookie();
        } else {
            newCookie = clientReservationService.generateSessionCookie(cookie);
        }

        ReservationNumberResponse responseBody = new ReservationNumberResponse(
                clientReservationService.createReservation(
                newCookie.getValue(), requestBody.getSubcategory_id())
        );

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newCookie.toString()).body(responseBody);
    }

}
