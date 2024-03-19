package box.reservations.controllers;

import box.reservations.payload.requests.auth.LoginRequest;
import box.reservations.security.utils.JwtUtils;
import box.reservations.payload.requests.auth.RegistrationRequest;
import box.reservations.services.auth.RegistrationService;
import box.reservations.services.auth.UserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
                );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        ResponseCookie cookie = jwtUtils.generateCookie(userDetails);
        log.info("Cookie was created. User logged in.");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@Validated @RequestBody RegistrationRequest registrationRequest) throws Exception {
        registrationService.addUser(registrationRequest);
        log.info("User was registered");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/signout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        log.info("User deleted his access cookie. Signing out.");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

}
