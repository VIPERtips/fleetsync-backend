package com.example.fleetsync.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.fleetsync.model.Role;
import com.example.fleetsync.model.User;
import com.example.fleetsync.model.UserDto;
import com.example.fleetsync.model.UserInfo;
import com.example.fleetsync.repository.UserInfoRepository;
import com.example.fleetsync.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void generateResetToken(User user) {
        String token = UUID.randomUUID().toString(); 
        user.setResetToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(30)); 
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByUserinfo_Email(email);
    }

    public User findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); 
        user.setTokenExpiration(null);
        userRepository.save(user);
    }
    
    public List<UserDto> getUsersByRole(Role role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(user -> new UserDto(
                        user.getUserId(),
                        user.getUserinfo().getFullname(),
                        user.getUserinfo().getEmail(),
                        user.getUsername()
                ))
                .collect(Collectors.toList());
    }

    public UserDto getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return new UserDto(
                user.getUserId(),
                user.getUserinfo().getFullname(),
                user.getUserinfo().getEmail(),
                user.getUsername()
        );
    }

    public void deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public UserDto updateUser(User user, UserDto req) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + user.getUsername()));

        UserInfo userInfo = existingUser.getUserinfo();
        userInfo.setFullname(req.getFullname());
        userInfo.setEmail(req.getEmail());
        ///set logic of upadting profile image here?
        userInfo.setPhonenumber(req.getPhonenumber());

        userInfoRepository.save(userInfo);

        return new UserDto(existingUser.getUserId(), userInfo.getFullname(), userInfo.getEmail(), existingUser.getUsername());
    }


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username " + username));
    }
}
