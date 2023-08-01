package com.chatty.authentication.security;

import com.chatty.authentication.util.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class ApplicationConfig {
    private final WebClient.Builder webClientBuilder;
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            log.info("sent request for getting user from userDetails");
            UserDTO userDTO = webClientBuilder.build().get()
                    .uri("http://user-management-service/api/v1/users/{username}", username)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
            if(userDTO == null) {
                throw new UsernameNotFoundException("User not found");
            }
            log.info(userDTO.getUsername() + " " + userDTO.getAuthorities());
            return new User(userDTO.getUsername(), "", userDTO.getAuthorities());
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
