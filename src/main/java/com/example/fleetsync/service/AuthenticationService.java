package com.example.fleetsync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.fleetsync.model.AuthenticationResponse;
import com.example.fleetsync.model.Role;
import com.example.fleetsync.model.User;
import com.example.fleetsync.model.UserDto;
import com.example.fleetsync.model.UserInfo;
import com.example.fleetsync.repository.*;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthenticationResponse registerUser(UserDto req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (req.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Oops, username taken. Enter a unique username.");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(req.getEmail());
        userInfo.setFullname(req.getFullname());

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);
        user.setUserinfo(userInfo);

        userRepository.save(user);
        userInfoRepository.save(userInfo);

        emailSender.sendRegistrationEmail(req.getEmail(), req.getUsername(), req.getPassword(), req.getFullname());


        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(token, user.getRole().name(), refreshToken);
    }

    public AuthenticationResponse authenticate(User req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(token, user.getRole().name(), refreshToken);
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isValid(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        return new AuthenticationResponse(newAccessToken, user.getRole().name(), refreshToken);
    }
}
