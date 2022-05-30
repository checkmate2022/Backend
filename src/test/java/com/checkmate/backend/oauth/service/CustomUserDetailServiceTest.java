package com.checkmate.backend.oauth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.checkmate.backend.oauth.api.entity.User;
import com.checkmate.backend.oauth.api.repo.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

	@Mock
	private UserRepository userRepository;

	private CustomUserDetailService customUserDetailService;

	@BeforeEach
	void setUp() {
		customUserDetailService = new CustomUserDetailService(userRepository);
	}

	@Test
	@DisplayName("username을 통해 User 반환")
	public void loadUserByUsername() {
		String userId = "test";
		User user = mock(User.class);

		when(userRepository.findByUserId(userId)).thenReturn(user);

		final UserDetails userDetails = customUserDetailService.loadUserByUsername(userId);

		assertNotNull(userDetails);
		assertEquals(user.getUserId(), userDetails.getUsername());
	}

}