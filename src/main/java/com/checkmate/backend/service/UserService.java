package com.checkmate.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.advice.exception.TokenValidFailedException;
import com.checkmate.backend.advice.exception.UserNotFoundException;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.UserDto;
import com.checkmate.backend.repo.UserRepository;

import antlr.Token;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public User getUser(String userId) {
		return userRepository.findUserByUserId(userId).orElseThrow(
			() -> new UserNotFoundException("사용자를 찾지 못했습니다.(토큰 확인)")
		);
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

	public List<User> searchUsers(String query) {
		return userRepository.searchUsers(query);
	}

	public void deleteUser(User user) {
		userRepository.delete(user);
	}

	public User modifyUser(User user, String username, String password) {
		user.update(username, password);
		return user;
	}

	public boolean checkPassword(User user, String password) {
		if (passwordEncoder.matches(password, user.getPassword()))
			return true;
		else
			return false;
	}
}