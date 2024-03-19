package box.reservations.controllers;

import box.reservations.entities.Ticket;
import box.reservations.entities.User;
import box.reservations.payload.requests.ByIdRequest;
import box.reservations.payload.requests.auth.ticket.NewTicketCreationRequest;
import box.reservations.payload.responses.auth.TicketResponse;
import box.reservations.payload.responses.auth.UsersResponse;
import box.reservations.security.utils.JwtUtils;
import box.reservations.services.admin.AdminControlsService;
import box.reservations.services.auth.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin/controls")
public class AdminControlsController {

    @Autowired
    AdminControlsService adminControlsService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/create_ticket")
    public ResponseEntity<?> createTicket(@Validated @RequestBody NewTicketCreationRequest ticketCreationRequest) throws Exception {
        Ticket ticket = adminControlsService.createTicket(ticketCreationRequest);
        log.info("Ticket was created");
        return ResponseEntity.ok().body(new TicketResponse(ticket));
    }

    @GetMapping("/tickets")
    public ResponseEntity<?> getAllTickets() {
        List<Ticket> tickets = adminControlsService.getAll();
        log.info("Tickets was obtained");
        return ResponseEntity.ok().body(Map.of("tickets", tickets));
    }

    @GetMapping("/users")
    public ResponseEntity<UsersResponse> getAllUsers() {
        List<User> users = userDetailsService.getAll();
        log.info("Users were obtained");
        return ResponseEntity.ok().body(new UsersResponse(users));
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(
            @Validated @RequestBody @NotNull ByIdRequest deleteUserRequest,
            @CookieValue("__Host-auth-token") String cookie
    ) throws Exception {
        userDetailsService.deleteUser(
                deleteUserRequest.getId(),
                jwtUtils.extractSubject(cookie)
        );
        log.info("User was deleted");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ticket")
    public ResponseEntity<?> deleteTicket(@Validated @RequestBody @NotNull ByIdRequest deleteTicketRequest) throws Exception {
        adminControlsService.deleteTicket(deleteTicketRequest.getId());
        log.info("Ticket was deleted");
        return ResponseEntity.noContent().build();
    }
}
