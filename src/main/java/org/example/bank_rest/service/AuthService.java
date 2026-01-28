package org.example.bank_rest.service;

import lombok.RequiredArgsConstructor;
import org.example.bank_rest.dto.auth.AuthRequest;
import org.example.bank_rest.dto.auth.AuthResponse;
import org.example.bank_rest.entity.Role;
import org.example.bank_rest.entity.User;
import org.example.bank_rest.exception.UserAlreadyExistsException;
import org.example.bank_rest.repository.UserRepository;
import org.example.bank_rest.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User " + request.getUsername() + " already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .get()
                .getAuthority()
                .replace("ROLE_", "");

        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        return  AuthResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }
}
