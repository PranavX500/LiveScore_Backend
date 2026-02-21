package com.example.livescore.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/auth/signup").permitAll()


                        // ADMIN APIs
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/auth/make-admin/**").hasRole("ADMIN")

                        // AUTHENTICATED
                        .requestMatchers("/auth/**").authenticated()

                        // EVERYTHING ELSE
                        .anyRequest().authenticated()
                )
                .addFilterBefore(firebaseFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}