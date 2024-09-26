package com.odeon.odeon_airlines.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig   {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Şifreleme için BCrypt kullan
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF korumasını devre dışı bırak
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll() // Auth endpointlerine herkes erişebilir
                        .requestMatchers("/api/admin/**").permitAll()
                        .anyRequest().authenticated() // Diğer tüm istekler kimlik doğrulaması gerektirir
                )
                .formLogin(withDefaults()) // Form tabanlı oturum açmaya izin ver
                .logout(withDefaults()); // Çıkış yapmaya izin ver

        return http.build();
    }
}