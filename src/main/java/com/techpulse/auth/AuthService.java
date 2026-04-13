package com.techpulse.auth;

import com.techpulse.user.User;
import com.techpulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return Map.of(
                    "token", token,
                    "user", user
            );
        }
        throw new RuntimeException("Invalid email or password");
    }

    public User register(String email, String password, String fullName, User.Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .avatarInitials(getInitials(fullName))
                .build();

        return userRepository.save(user);
    }

    public String generateDevToken(String email, User.Role role) {
        return jwtUtil.generateToken(email, role.name());
    }

    public List<Map<String, Object>> getDemoProfiles() {
        return List.of(
                buildDemoProfile("anna.smirnova@company.dev", User.Role.HR),
                buildDemoProfile("oleg.ivanov@company.dev", User.Role.MANAGER),
                buildDemoProfile("ivan.petrov@company.dev", User.Role.EMPLOYEE)
        );
    }

    private Map<String, Object> buildDemoProfile(String email, User.Role expectedRole) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Demo profile not found in DB: " + email));
        if (user.getRole() != expectedRole) {
            throw new RuntimeException("Demo profile role mismatch for " + email);
        }
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole().name(),
                "avatarInitials", user.getAvatarInitials()
        );
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "";
        String[] parts = fullName.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }
}
