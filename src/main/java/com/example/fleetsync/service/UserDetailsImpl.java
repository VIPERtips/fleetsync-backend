package com.example.fleetsync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fleetsync.repository.UserRepository;

@Service
public class UserDetailsImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    System.err.println("Attempting to load user by username: {}"+ username);
	    return userRepository.findByUsername(username)
	            .orElseThrow(() -> {
	               
	                return new UsernameNotFoundException("User not found: " + username);
	            });
	}

}

