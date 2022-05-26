package com.checkmate.backend.oauth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.oauth.api.entity.Avatar;
import com.checkmate.backend.oauth.api.repo.AvatarRepository;

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
	public Optional<Avatar> findOne(Long avatarId) {
		Optional<Avatar> avatar = avatarRepository.findById(avatarId);
		return avatar;
	}

	//  캐릭터 등록
	public Avatar make(Avatar avatar) {
		Avatar save = avatarRepository.save(avatar);
		return save;
	}

	//  캐릭터 수정
	public Avatar update(Long avatarId, String avatar_name, String avatar_description,
		String OriginFileUrl, String CreatedFileUrl, LocalDateTime dateTime) {
		Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(
			() -> new IllegalArgumentException("해당 캐릭터는 존재하지 않습니다.")
		);
		avatar.update(avatar_name, avatar_description, OriginFileUrl, CreatedFileUrl, dateTime);
		return avatar;
	}

	//  캐릭터 삭제
	public void delete(Long avatarId) {
		avatarRepository.deleteById(avatarId);
	}

	//아바타 기본설정
	public void setIsBasic(Long avatarId, String user_id) {
		Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(
			() -> new IllegalArgumentException("해당 캐릭터는 존재하지 않습니다.")
		);
		avatar.setIsBasic();
		//user_id에 해당하는 user에 캐릭터 리스트 가져와서 basic 바꾸기
		//프로필 사진변경
	}
/*
	// 아바타 이름 중복 조회
	public void validatenameDuplicateException(String name) {
		List<Avatar> findAvatar = avatarRepository.findByAvatar_name(name);
		if (!findAvatar.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 name 입니다.");
		}
	}*/
}
