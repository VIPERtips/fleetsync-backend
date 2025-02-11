package com.example.fleetsync.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleetsync.model.Role;
import com.example.fleetsync.model.User;

public interface UserRepository  extends JpaRepository<User, Integer>{

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	List<User> findByRole(Role role);

	User findByUserinfo_Email(String email);

	User findByResetToken(String token);

}
