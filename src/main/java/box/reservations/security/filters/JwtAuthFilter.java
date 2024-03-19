package box.reservations.security.filters;

import box.reservations.security.utils.JwtUtils;
import box.reservations.services.auth.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtUtils jwtUtils;


    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/")
                || path.startsWith("/api/v1/categories")
                || path.startsWith("/api/v1/subcategories")
                || path.startsWith("/api/v1/reservations")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs");
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        final String userEmail;
        String jwtToken = null;

        log.info("JWT filter starts");

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("__Host-auth-token")) {
                log.info("JWT filter found cookie");
                jwtToken = cookie.getValue();
            }
        }
        if (jwtToken == null) {
            log.info("JWT filter haven't found cookie");
            filterChain.doFilter(request, response);
            return;
        }

        userEmail = jwtUtils.extractSubject(jwtToken);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtUtils.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("JWT filter success");
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
