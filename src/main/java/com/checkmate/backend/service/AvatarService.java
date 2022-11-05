package com.checkmate.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.advice.exception.DuplicateException;
import com.checkmate.backend.advice.exception.MaximumException;
import com.checkmate.backend.advice.exception.ResourceNotExistException;
import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.avatar.AvatarType;
import com.checkmate.backend.entity.avatar.Emoticon;
import com.checkmate.backend.entity.avatar.EmoticonType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.AvatarRepository;
import com.checkmate.backend.repo.EmoticonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AvatarService {

	private final AvatarRepository avatarRepository;
	private final EmoticonRepository emoticonRepository;

	// 전체 캐릭터 조회
	@Transactional(readOnly = true)
	public List<Avatar> findAvatars() {
		List<Avatar> avatars = avatarRepository.findAll();
		return avatars;
	}

	// 단건 캐릭터 조회
	@Transactional(readOnly = true)
	public Avatar findOne(Long avatarId) {
		Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(
			() -> new ResourceNotExistException("해당 캐릭터는 존재하지 않습니다.")
		);
		return avatar;
	}

	//기본캐릭터 조회
	@Transactional(readOnly = true)
	public Avatar findBasicAvatar(User user) {
		Avatar avatar = avatarRepository.findAvatarByUserAndIsBasicTrue(user).orElseThrow(
			() -> new ResourceNotExistException("해당 캐릭터는 존재하지 않습니다.")
		);
		return avatar;
	}

	// 사용자별 캐릭터 조회
	@Transactional(readOnly = true)
	public List<Avatar> findAvatarByUser(User user) {
		List<Avatar> avatars = avatarRepository.findAllByUser(user);
		return avatars;
	}
	//
	// // 사용자별 ㅇㅣ모티콘 조회
	// @Transactional(readOnly = true)
	// public List<Emoticon> findEmoticonsByUser(User user) {
	// 	List<Emoticon> emoticons = emoticonRepository.findAllByUser(user);
	// 	return emoticons;
	// }

	// 기본 아바타 ㅇㅣ모티콘 조회
	@Transactional(readOnly = true)
	public List<Emoticon> findEmoticonsByBasicAvatar(User user) {
		Avatar avatar = avatarRepository.findAvatarByUserAndIsBasicTrue(user).orElseThrow(
			() -> new ResourceNotExistException("해당 캐릭터는 존재하지 않습니다.")
		);
		List<Emoticon> emoticons = emoticonRepository.findEmoticonsByAvatar(avatar);
		return emoticons;
	}

	//  캐릭터 등록
	public Avatar make(Avatar avatar, User user, String sadEmoticon, String happyEmoticon, String winkEmoticon,
		String angryEmoticon) {
		if (user.getAvatar().size() > 3) {
			throw new MaximumException("아바타 개수 초과");
		}
		avatar.setUser(user);
		Avatar save = avatarRepository.save(avatar);

		Emoticon emoticon = new Emoticon(sadEmoticon, EmoticonType.SAD);
		emoticon.setAvatar(save);
		emoticon = emoticonRepository.save(emoticon);
		save.setEmoticon(emoticon);

		emoticon = new Emoticon(happyEmoticon, EmoticonType.HAPPY);
		emoticon.setAvatar(save);
		emoticon = emoticonRepository.save(emoticon);
		save.setEmoticon(emoticon);

		emoticon = new Emoticon(winkEmoticon, EmoticonType.WINK);
		emoticon.setAvatar(save);
		emoticon = emoticonRepository.save(emoticon);
		save.setEmoticon(emoticon);

		emoticon = new Emoticon(angryEmoticon, EmoticonType.ANGRY);
		emoticon.setAvatar(save);
		emoticon = emoticonRepository.save(emoticon);
		save.setEmoticon(emoticon);
		return save;
	}

	//  캐릭터 수정
	public Avatar update(Long avatarId, String avatarName, String avatarDescription, AvatarType style, Long styleId,
		String OriginFileUrl, String CreatedFileUrl, LocalDateTime dateTime) {
		Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(
			() -> new ResourceNotExistException("해당 캐릭터는 존재하지 않습니다.")
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
			() -> new ResourceNotExistException("해당 캐릭터는 존재하지 않습니다.")
		);
		//기본 아바타 설정
		List<Avatar> userAvatar = user.getAvatar();
		for (Avatar a : userAvatar) {
			a.setIsBasicFalse();
			if (a.getAvatarSeq() == avatar.getAvatarSeq()) {
				avatar.setIsBasic();
			}
		}
		//프로필 사진변경
		user.setUserImage(avatar.getAvatarCreatedUrl());
	}

	// 아바타 이름 중복 조회
	public void validateNameDuplicateException(String name) {
		int cntAvatarName = avatarRepository.countByAvatarName(name);
		if (cntAvatarName > 0) {
			throw new DuplicateException("이미 존재하는 name 입니다.");
		}
	}
}
