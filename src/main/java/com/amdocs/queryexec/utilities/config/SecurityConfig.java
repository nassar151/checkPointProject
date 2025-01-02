package com.amdocs.queryexec.utilities.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/auth/**").hasRole("STUDENT") // Require STUDENT role for /auth/** endpoints
                        .requestMatchers("/student/**").hasRole("STUDENT") // Require STUDENT role for /student/** endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Require ADMIN role for /admin/** endpoints
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection for simplicity
                .httpBasic(); // Enable HTTP Basic authentication
        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").password("{noop}admin").roles("ADMIN").build());
        manager.createUser(User.withUsername("student").password("{noop}student").roles("STUDENT").build());
        return manager;
    }
}