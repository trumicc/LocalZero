package com.localzero.localzero.controller;

import com.localzero.localzero.model.User;
import com.localzero.localzero.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

// request passes Security filter and then reaches here
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // FR100 + FR103: Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, HttpSession session) {

        if (user.getName() == null || user.getEmail() == null ||
                user.getPasswordHash() == null || user.getLocation() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "All fields are required"));
        }


        try {
            User saved = userService.register(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                        "message", "Registration successful", "userId", saved.getId(), "name", saved.getName()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // FR101: Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
        }
        User user = userOptional.get();
        if (!userService.checkPassword(password, user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid password"));
        }
        session.setAttribute("userId", user.getId());
        return ResponseEntity.ok(Map.of("message", "logged in succesfully"));

    }

    // FR102: Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged ot succesfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not logged in"));
        }
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "location", user.getLocation(),
                "roles", user.getRoles()
        ));
    }
}