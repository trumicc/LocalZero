package com.localzero.localzero.service;

import com.localzero.localzero.model.User;
import com.localzero.localzero.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User register(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

            String passwordHash = passwordEncoder.encode(user.getPasswordHash());
            user.setPasswordHash(passwordHash);

            if ("community_organizer".equals(user.getRoles())) {
                user.setRoles("community_organizer");
            } else {
                user.setRoles("resident");
            }
            return userRepository.save(user);

        }

        public Optional<User> findByEmail(String email){
            return userRepository.findByEmail(email.toLowerCase());
        }

        public boolean checkPassword(String rawPassword, String hashedPassword) {
            return passwordEncoder.matches(rawPassword, hashedPassword);
        }

        public Optional<User> findById(Long id) {
            return userRepository.findById(id);
        }

}