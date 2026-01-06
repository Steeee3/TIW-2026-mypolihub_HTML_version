package it.polimi.mypolihub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/student/**").hasRole("STUDENT")
            .requestMatchers("/professor/**").hasRole("PROFESSOR")
            .anyRequest().authenticated()
        )
        .formLogin(withDefaults())
        .logout(withDefaults());

        return http.build();
    }
}
