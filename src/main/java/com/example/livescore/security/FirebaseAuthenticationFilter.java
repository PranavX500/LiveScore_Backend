package com.example.livescore.security;

import com.example.livescore.Model.Role;
import com.example.livescore.Model.User;
import com.example.livescore.Service.FirebaseAuthService;
import com.example.livescore.Service.UserService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseAuthService firebaseAuthService;
    private final UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean skip = path.startsWith("/public") || path.startsWith("/auth/signup");
        log.info("FILTER CHECK → {} | skip={}", path, skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        log.info("FILTER HIT → {}", path);

        String header = request.getHeader("Authorization");

        if (header == null) {
            log.warn("NO AUTH HEADER");
        }

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                log.info("TOKEN PRESENT");

                FirebaseToken decodedToken =
                        firebaseAuthService.verifyToken(token);

                String uid = decodedToken.getUid();
                log.info("TOKEN UID → {}", uid);

                User user = userService.getUser(uid);
                log.info("DB USER → {}", user);

                if (user == null) {
                    log.warn("USER NOT FOUND IN DB");
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                Role roleEnum = user.getRoleEnum();
                log.info("ROLE ENUM → {}", roleEnum);

                if (roleEnum == null) {
                    log.warn("ROLE NULL");
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                String role = roleEnum.name();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                uid,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("AUTH SET → ROLE_{}", role);

            } catch (Exception e) {
                log.error("AUTH FAILED", e);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}