package com.odeon.odeon_airlines.controller;

import com.odeon.odeon_airlines.dto.LoginRequest;
import com.odeon.odeon_airlines.exception.UsernameAlreadyExistsException;
import com.odeon.odeon_airlines.model.AppUser;
import com.odeon.odeon_airlines.model.Flight;
import com.odeon.odeon_airlines.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth") // Base URL for authentication endpoints
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register") // Endpoint for user registration
    public ResponseEntity<String> register(@RequestBody AppUser user) {
        try {
            AppUser newUser = authService.register(user); // Register user
            return ResponseEntity.ok("{\"message\": \"User registered successfully\", \"user\": \"" + newUser.getUsername() + "\", \"role\": \"" + newUser.getRole() + "\"}");
        } catch (UsernameAlreadyExistsException ex) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        AppUser user = authService.login(username, password);
        if (user != null) {
            String jsonResponse = String.format("{\"message\": \"Login successful\", \"user\": \"%s\", \"role\": \"%s\", \"location\": \"%s\"}",
                    user.getUsername(), user.getRole(), user.getLocation());

            return ResponseEntity.ok(jsonResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid username or password\"}");
        }
    }

}
