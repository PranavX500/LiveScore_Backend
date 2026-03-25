package com.example.livescore.config;

import com.example.livescore.security.FirebaseAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/auth/signup").permitAll()
                        .requestMatchers("/auth/send-email-otp").permitAll()
                        .requestMatchers("/auth/verify-email-otp").permitAll()
                        .requestMatchers("/auth/verify-signup").permitAll()
                        .requestMatchers("/api/team/teams").permitAll()
                        .requestMatchers("/api/team/get/**").permitAll()
                        .requestMatchers("/api/tournament/**").permitAll()
                        .requestMatchers("/score/**").permitAll()
                        .requestMatchers("/live/**").permitAll()
                        .requestMatchers("/get/tournament").permitAll()
                        .requestMatchers("/auth/resend-signup-otp").permitAll()
                        .requestMatchers("/auth/player/**").hasRole("PLAYER")
                        .requestMatchers("/PointTable/**").permitAll()
                        .requestMatchers("/auth/count/players").permitAll()

                        .requestMatchers("/tournaments/**").hasRole("ADMIN")
                        .requestMatchers("/live/upcoming").permitAll()
                        .requestMatchers("/auth/me/notifications/**").permitAll()


                        .requestMatchers("/auth/make-admin/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(firebaseFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
