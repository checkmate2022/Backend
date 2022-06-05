package com.checkmate.backend.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.UserRepository;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.model.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User getUser(String userId) {
		return userRepository.findByUserId(userId);
	}

	public User signUpUser(UserDto user) {
		LocalDateTime now = LocalDateTime.now();
		User u = new User(
			user.getUserId(),
			user.getUsername(),
			passwordEncoder.encode(user.getPassword()),
			ProviderType.LOCAL,
			RoleType.USER,
			now,
			now
		);
		return userRepository.save(u);
	}

	public int checkUsername(String username) {
		return userRepository.countByUsername(username);
	}

	public int checkId(String userId) {
		return userRepository.countByUserId(userId);
	}

}