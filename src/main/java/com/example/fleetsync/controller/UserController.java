package com.example.fleetsync.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleetsync.model.ApiResponse;
import com.example.fleetsync.model.Role;
import com.example.fleetsync.model.User;
import com.example.fleetsync.model.UserDto;
import com.example.fleetsync.service.JwtService;
import com.example.fleetsync.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(@PathVariable Role role) {
        try {
            List<UserDto> users = userService.getUsersByRole(role);
            String user = (role == Role.USER) ? "Users" : "Admins";
            return ResponseEntity.ok(new ApiResponse<>(user + " retrieved successfully", true, users));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable int id) {
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(new ApiResponse<>("User retrieved successfully", true, user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("User not found with ID: " + id, false, null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable int id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok(new ApiResponse<>("User deleted successfully.", true, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("User not found with ID: " + id, false, null));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(HttpServletRequest request, @RequestBody UserDto userDto) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);

            UserDto updatedUser = userService.updateUser(user, userDto);
            return ResponseEntity.ok(new ApiResponse<>("User updated successfully", true, updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getUserProfile(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);

            if (user != null) {
            	UserDto userDto = new UserDto(
            			user.getUserId(),
            			user.getUserinfo().getFullname(),
            			user.getUserinfo().getEmail(),
            			user.getUserinfo().getProfilePicture(),
            			user.getUserinfo().getPhonenumber()
            			);
            	userDto.setUsername(user.getUsername());
            	userDto.setPassword(user.getPassword());
                return ResponseEntity.ok(new ApiResponse<>("User profile retrieved successfully", true, userDto));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("User not found", false, null));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}

