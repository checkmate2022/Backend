package com.checkmate.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.UserDto;
import com.checkmate.backend.repo.UserRepository;

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
		given(userRepository.findUserByUserId(userId)).willReturn(java.util.Optional.ofNullable(mockUser));

		User getUser = userService.getUser(userId);

		assertEquals(getUser, mockUser);
	}

	@Test
	@DisplayName("닉네임 증복체크")
	void checkUsername() {
		String username = "repo1";
		given(userRepository.countByUsername(username)).willReturn(1);
		int cnt = userService.validateUserNameDuplicate(username);
		assertEquals(1, cnt);
	}

	@Test
	@DisplayName("아이디 증복체크")
	void checkId() {
		String userId = "repo1";
		given(userRepository.countByUserId(userId)).willReturn(1);
		int cnt = userService.validateUserIdDuplicate(userId);
		assertEquals(1, cnt);
	}

	@Test
	@DisplayName("비밀번호 중복 확인")
	void checkPassword() {
		User mockUser = User.builder()
			.userId("repo1")
			.password(passwordEncoder.encode("repo1"))
			.build();
		boolean isTrue = userService.checkPassword(mockUser, "repo1");
		boolean isFalse = userService.checkPassword(mockUser, "repo");
		assertEquals(true, isTrue);
		assertEquals(false, isFalse);
	}

	@Test
	@DisplayName("회원 검색")
	void searchUsers() {
		String userId = "repo1";
		User mockUser = User.builder().userId("repo1").build();
		List<User> mockUsers = new ArrayList<>();
		mockUsers.add(mockUser);
		given(userRepository.searchUsers(userId)).willReturn(mockUsers);
		List<User> users = userService.searchUsers(userId);
		assertEquals(mockUsers, users);
	}

	@Test
	@DisplayName("사용자 정보 수정")
	void modifyUser() {
		User mockUser = User.builder()
			.userId("repo1")
			.password(passwordEncoder.encode("repo1"))
			.build();
		User modifiedUser = userService.modifyUser(mockUser, "repo", "repo");
		assertEquals("repo", modifiedUser.getUsername());
	}

}