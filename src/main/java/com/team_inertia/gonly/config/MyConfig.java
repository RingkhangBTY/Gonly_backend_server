package com.team_inertia.gonly.config;

import com.team_inertia.gonly.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class MyConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC — Auth endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // PUBLIC — GET requests for gems
                        .requestMatchers(HttpMethod.GET, "/api/gems").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gems/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gems/category").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gems/state").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gems/nearby").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gems/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gems/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gems/{gemId}/reviews").permitAll()

                        // PUBLIC — GET requests for events
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()

                        // PROTECTED — Everything else needs JWT
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}