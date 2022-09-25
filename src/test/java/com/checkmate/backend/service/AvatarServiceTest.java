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

import com.checkmate.backend.advice.exception.DuplicateException;
import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.avatar.Emoticon;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.AvatarRepository;
import com.checkmate.backend.repo.EmoticonRepository;
import com.checkmate.backend.repo.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvatarService 테스트")
class AvatarServiceTest {

	AvatarService avatarService;
	@Mock
	AvatarRepository avatarRepository;
	@Mock
	EmoticonRepository emoticonRepository;
	@Mock
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
		this.avatarService = new AvatarService(this.avatarRepository,this.emoticonRepository);
	}

	@Test
	@DisplayName("전체 캐릭터 조회")
	void findAvatars() {
		Avatar mockAvatar = Avatar.builder().avatarName("test").build();
		List<Avatar> mockAvatars = new ArrayList<>();
		mockAvatars.add(mockAvatar);
		given(avatarRepository.findAll()).willReturn(mockAvatars);
		List<Avatar> avatars = avatarService.findAvatars();
		assertEquals(mockAvatars, avatars);
	}

	@Test
	@DisplayName("단건 캐릭터 조회")
	void findOne() {
		Long avatarId = 1L;
		Avatar mockAvatar = Avatar.builder().avatarName("test").build();
		given(avatarRepository.findById(avatarId)).willReturn(java.util.Optional.ofNullable(mockAvatar));

		Avatar findOne = avatarService.findOne(avatarId);

		assertEquals(mockAvatar, findOne);
	}

	@Test
	@DisplayName("사용자별 캐릭터 조회")
	void findAvatarByUser() {
		User mockUser = User.builder().userId("repo1").build();
		Avatar mockAvatar = Avatar.builder().avatarName("test").build();
		List<Avatar> mockAvatars = new ArrayList<>();
		mockAvatars.add(mockAvatar);
		given(avatarRepository.findAllByUser(mockUser)).willReturn(mockAvatars);
		List<Avatar> avatars = avatarService.findAvatarByUser(mockUser);
		assertEquals(mockAvatars, avatars);

	}

	@Test
	@DisplayName("캐릭터 등록")
	void make() {
		LocalDateTime now = LocalDateTime.now();
		User mockUser = User.builder()
			.userId("repo1")
			.password("repo1")
			.build();
		Avatar mockAvatar = Avatar.builder()
			.avatarName("test")
			.avatarDescription("아바타설명")
			.avatarCreatedUrl("캐릭터")
			.avatarOriginUrl("원본")
			.avatarDate(now)
			.emoticons(new ArrayList<>())
			.build();
		Emoticon mockEmoticons= Emoticon.builder().build();

		List<Avatar> avatars = new ArrayList<>();
		mockUser.setAvatar(avatars);

		given(avatarRepository.save(any())).willReturn(mockAvatar);
		given(emoticonRepository.save(any())).willReturn(mockEmoticons);

		Avatar avatar = avatarService.make(mockAvatar, mockUser,"sad","happy","wink","angry");
		assertEquals(mockAvatar, avatar);
		assertEquals(mockEmoticons,avatar.getEmoticons().get(0));
	}

	@Test
	@DisplayName("아바타 기본설정")
	void setIsBasic() {
		LocalDateTime now = LocalDateTime.now();
		User mockUser = User.builder()
			.userId("repo1")
			.password("repo1")
			.build();
		Avatar mockAvatar = Avatar.builder()
			.avatarSeq(1L)
			.avatarName("test")
			.avatarDescription("아바타설명")
			.avatarCreatedUrl("캐릭터")
			.avatarOriginUrl("원본")
			.avatarDate(now)
			.build();

		List<Avatar> avatars = new ArrayList<>();
		avatars.add(mockAvatar);
		mockUser.setAvatar(avatars);

		given(avatarRepository.findById(1L)).willReturn(java.util.Optional.ofNullable(mockAvatar));

		avatarService.setIsBasic(1L, mockUser);
		assertEquals(mockUser.getAvatar().get(0).getIsBasic(), true);
	}

	@Test
	@DisplayName("아바타 이름 중복시 (DuplicateException 반환)")
	void validateNameDuplicateException() {
		String name = "test";

		given(avatarRepository.countByAvatarName(name)).willReturn(1);
		assertThrows(DuplicateException.class, () -> avatarService.validateNameDuplicateException(name));

	}
}