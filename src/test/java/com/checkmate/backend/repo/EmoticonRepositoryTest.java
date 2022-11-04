package com.checkmate.backend.repo;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
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
class EmoticonRepositoryTest {
	User savedUser;
	Avatar savedAvatar;
	@Autowired
	private AvatarRepository avatarRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void 이모티콘생성() {
		//given
		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo", "repo", "repo", ProviderType.LOCAL, RoleType.USER, now, now);
		savedUser = userRepository.save(user);

		Avatar avatar = new Avatar(savedUser, "avatar", "테스트 아바타입니다.", AvatarType.anime, 1L, "원본", "캐릭터", now);
		savedAvatar = avatarRepository.save(avatar);

	}

	@Test
	void findEmoticonsByAvatar() {
	}
}