package com.checkmate.backend.oauth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.checkmate.backend.oauth.api.entity.User;
import com.checkmate.backend.oauth.api.repo.UserRepository;
import com.checkmate.backend.oauth.entity.ProviderType;
import com.checkmate.backend.oauth.entity.RoleType;
import com.checkmate.backend.oauth.model.UserDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

	UserService userService;
	@Mock
	UserRepository userRepository;

	BCryptPasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		this.passwordEncoder = new BCryptPasswordEncoder();
		this.userService = new UserService(this.userRepository, this.passwordEncoder);
	}

	@Test
	@DisplayName("회원가입 테스트")
	void signedUser() {
		//given
		UserDto userDto = UserDto.builder().userId("repo1").username("repo1").password("repo1").build();
		//given
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
			.userSeq(1L)
			.password(passwordEncoder.encode("repo1"))
			.userId("repo1")
			.username("repo1")
			.providerType(ProviderType.LOCAL)
			.roleType(RoleType.USER)
			.createdAt(now)
			.modifiedAt(now)
			.build();

		given(userRepository.save(any())).willReturn(user);
		//when

		User result = userService.signUpUser(userDto);

		assertEquals(1L, result.getUserSeq());
	}

	@Test
	@DisplayName("userId로 User 가져오기")
	void getUser() {
		String userId = "repo1";
		User mockUser = User.builder().userId("repo1").build();
		given(userRepository.findByUserId(userId)).willReturn(mockUser);

		User getUser = userService.getUser(userId);

		assertEquals(getUser, mockUser);
	}

	@Test
	@DisplayName("닉네임 증복체크")
	void checkUsername() {
		String username = "repo1";
		given(userRepository.countByUsername(username)).willReturn(1);
		int cnt = userService.checkUsername(username);
		assertEquals(1, cnt);
	}

	@Test
	@DisplayName("아이디 증복체크")
	void checkId() {
		String userId = "repo1";
		given(userRepository.countByUserId(userId)).willReturn(1);
		int cnt = userService.checkId(userId);
		assertEquals(1, cnt);
	}
}