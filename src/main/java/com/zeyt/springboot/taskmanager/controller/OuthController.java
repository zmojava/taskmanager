package com.zeyt.springboot.taskmanager.controller;


import com.zeyt.springboot.taskmanager.dto.AuthenticationRequest;
import com.zeyt.springboot.taskmanager.dto.AuthenticationResponse;
import com.zeyt.springboot.taskmanager.model.User;
import com.zeyt.springboot.taskmanager.repository.UserRepository;
import com.zeyt.springboot.taskmanager.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outh")
public class OuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public OuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
    @GetMapping("/today")
    public String getToday(){
        return "maha scbka kajckja kjckjbc";
    }
}

