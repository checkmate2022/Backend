package com.checkmate.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.avatar.AvatarType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.AvatarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AvatarService {

	private final AvatarRepository avatarRepository;
	//private final PersonalRepository personalRepository;

	// 전체 캐릭터 조회
	@Transactional(readOnly = true)
	public List<Avatar> findAvatars() {
		List<Avatar> avatars = avatarRepository.findAll();

		return avatars;
	}

	// 단건 캐릭터 조회
	public Avatar findOne(Long avatarId) {
		Avatar avatar = avatarRepository.findById(avatarId).orElseThrow();
		return avatar;
	}

	// 사용자별 캐릭터 조회
	public List<Avatar> findAvatarByUser(User user) {
		List<Avatar> avatars = avatarRepository.findAllByUser(user);
		return avatars;
	}

	//  캐릭터 등록
	public Avatar make(Avatar avatar, User user) {
		avatar.setUser(user);
		Avatar save = avatarRepository.save(avatar);
		return save;
	}

	//  캐릭터 수정
	public Avatar update(Long avatarId, String avatarName, String avatarDescription, AvatarType style, Long styleId,
		String OriginFileUrl, String CreatedFileUrl, LocalDateTime dateTime) {
		Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(
			() -> new IllegalArgumentException("해당 캐릭터는 존재하지 않습니다.")
		);
		avatar.update(avatarName, avatarDescription, style, styleId, OriginFileUrl, CreatedFileUrl, dateTime);
		return avatar;
	}

	//  캐릭터 삭제
	public void delete(Long avatarId) {
		avatarRepository.deleteById(avatarId);
	}

	//아바타 기본설정
	public void setIsBasic(Long avatarId, User user) {
		Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(
			() -> new IllegalArgumentException("해당 캐릭터는 존재하지 않습니다.")
		);

		List<Avatar> userAvatar = user.getAvatar();
		for (Avatar ar : userAvatar) {
			if (ar.getAvatarSeq() == avatar.getAvatarSeq()) {
				avatar.setIsBasic();
			} else {
				ar.setIsBasicFalse();
			}
		}

		//프로필 사진변경
		user.setUserImage(avatar.getAvatarCreatedUrl());
	}

	// 아바타 이름 중복 조회
	public void validatenameDuplicateException(String name) {
		List<Avatar> findAvatar = avatarRepository.findByAvatarName(name);
		if (!findAvatar.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 name 입니다.");
		}
	}
}
