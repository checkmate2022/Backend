package com.checkmate.backend.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.user.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void 회원가입() {
		//given
		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo", "repo", "repo", ProviderType.LOCAL, RoleType.USER, now, now);

		//when
		User savedUser = userRepository.save(user);

		//then
		assertEquals(savedUser.getUserId(), user.getUserId());
	}

	@Test
	@DisplayName("회원ID를 이용하여 회원 찾기")
	void 회원id로_회원찾기() {
		//given
		String userId = "repo";

		//when
		User user = userRepository.findByUserId(userId);

		//then
		assertEquals(user.getUserId(), userId);
	}

	@Test
	@DisplayName("같은 닉네임을 가진 회원수 반환(닉네임 중복)")
	void countByUsername() {
		//given
		String userId = "repo";

		//when
		int userCnt = userRepository.countByUsername(userId);

		//then
		assertEquals(userCnt, 1);
	}

	@Test
	@DisplayName("같은 Id을 가진 회원수 반환(ID 중복)")
	void countByUserId() {
		//given
		String userId = "repo";

		//when
		int userCnt = userRepository.countByUserId(userId);

		//then
		assertEquals(userCnt, 1);
	}

	@Test
	@DisplayName("사용자 검색")
	void searchUsers() {
		//given
		String query = "repo";

		//when
		List<User> users = userRepository.searchUsers(query);

		//then
		assertEquals(users.size(), 1);
		assertEquals(users.get(0).getUsername(), "repo");
	}

	@AfterEach
	void delete() {
		userRepository.deleteAll();
	}
}