package com.prashant.ems.controller;

import com.prashant.ems.dto.AuthRequest;
import com.prashant.ems.dto.AuthResponse;
import com.prashant.ems.dto.RegisterRequest;
import com.prashant.ems.entity.Role;
import com.prashant.ems.entity.User;
import com.prashant.ems.repository.RoleRepository;
import com.prashant.ems.repository.UserRepository;
import com.prashant.ems.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already taken");
        }

        String roleName = "ROLE_" + request.getRole().toUpperCase();

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(roleName)
                                .build()
                ));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

