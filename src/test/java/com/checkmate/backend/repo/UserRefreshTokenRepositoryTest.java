package com.checkmate.backend.repo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checkmate.backend.entity.user.UserRefreshToken;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRefreshTokenRepositoryTest {
	@Autowired
	private UserRefreshTokenRepository userRefreshTokenRepository;

	@BeforeEach
	void 토큰생성() {
		//given
		UserRefreshToken refreshToken = new UserRefreshToken("test", "refreshToken");

		//when
		UserRefreshToken savedRefreshToken = userRefreshTokenRepository.save(refreshToken);

		//then
		assertEquals("test", savedRefreshToken.getUserId());
	}

	@Test
	@DisplayName("회원 Id 이용해 refreshToken 찾기")
	void findByUserId() {
		//given
		String userId = "test";

		//when
		UserRefreshToken refreshToken = userRefreshTokenRepository.findByUserId(userId);

		//then
		assertEquals(refreshToken.getUserId(), userId);
	}

	@Test
	void findByUserIdAndRefreshToken() {
		//given
		String userId = "test";
		String refreshToken = "refreshToken";

		//when
		UserRefreshToken token = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);

		//then
		assertEquals(token.getUserId(), userId);
		assertEquals(token.getRefreshToken(), refreshToken);

	}
}