package com.checkmate.backend.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.avatar.AvatarType;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.user.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AvatarRepositoryTest {

	User savedUser;
	Avatar savedAvatar;
	@Autowired
	private AvatarRepository avatarRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void 아바타생성() {
		//given
		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo", "repo", "repo", ProviderType.LOCAL, RoleType.USER, now, now);
		savedUser = userRepository.save(user);

		Avatar avatar = new Avatar(savedUser, "avatar", "테스트 아바타입니다.", AvatarType.anime, 1L, "원본", "캐릭터", now);
		savedAvatar = avatarRepository.save(avatar);

		//then
		assertEquals(avatar.getAvatarName(), savedAvatar.getAvatarName());
	}

	@Test
	@DisplayName("아바타이름 중복 조회")
	void countByAvatarName() {
		String name = "avatar";

		int cnt = avatarRepository.countByAvatarName(name);

		assertEquals(1, cnt);
	}

	@Test
	@DisplayName("사용자별 캐릭터 조회")
	void findAllByUser() {
		List<Avatar> avatars = avatarRepository.findAllByUser(savedUser);

		assertEquals(avatars.get(0), savedAvatar);
	}
}