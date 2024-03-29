package com.checkmate.backend.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.avatar.AvatarType;
import com.checkmate.backend.entity.avatar.Emoticon;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.service.AvatarService;
import com.checkmate.backend.service.FileService;
import com.checkmate.backend.service.ResponseService;
import com.checkmate.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Avatar")
@RequestMapping(value = "/api/v1/avatar")
@RequiredArgsConstructor
@RestController
public class AvatarController {

	private final static String AVATAR_FOLDER_DIRECTORY = "/avatar";
	private final static String AVATAR_USER_IMAGE_FILES_POSTFIX = "_origin";
	private final static String AVATAR_CREATED_IMAGE_FILES_POSTFIX = "_created";
	private final AvatarService avatarService;
	private final ResponseService responseService;
	private final FileService fileService;
	private final UserService userService;

	@Operation(summary = "전체 캐릭터 조회", description = "전체캐릭터조회")
	@GetMapping
	public ListResult<Avatar> getAvatars() {
		return responseService.getListResult(avatarService.findAvatars());
	}

	// @Operation(summary = "전체 이모티콘 조회", security = {@SecurityRequirement(name = "bearer-key")})
	// @GetMapping("/emoticons")
	// public ListResult<Emoticon> getEmoticons() {
	// 	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	// 	String name = authentication.getName();
	// 	User user = userService.getUser(name);
	// 	return responseService.getListResult(avatarService.findEmoticonsByUser(user));
	// }

	@Operation(summary = "기본 캐릭터 이모티콘 조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/basic/emoticons")
	public ListResult<Emoticon> getEmoticonsByAvatar() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		User user = userService.getUser(name);
		return responseService.getListResult(avatarService.findEmoticonsByBasicAvatar(user));
	}

	@Operation(summary = "단건 캐릭터 조회", description = "단건캐릭터조회")
	@GetMapping("/{avatarId}")
	public SingleResult<Avatar> getAvatar(
		@Parameter(description = "캐릭터id") @PathVariable Long avatarId) {
		return responseService.getSingleResult(avatarService.findOne(avatarId));
	}

	@Operation(summary = "기본 캐릭터 조회", description = "해당 사용자의 기본 캐릭터 조회", security = {
		@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/basic")
	public SingleResult<Avatar> getBasicAvatar() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		User user = userService.getUser(name);
		return responseService.getSingleResult(avatarService.findBasicAvatar(user));
	}

	@Operation(summary = "사용자별 캐릭터 조회", description = "사용자별 캐릭터 조회", security = {
		@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/user")
	public ListResult<Avatar> getAvatarByUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		User user = userService.getUser(name);

		return responseService.getListResult(avatarService.findAvatarByUser(user));
	}

	//postman 으로 테스트 해야함
	@Operation(summary = "캐릭터 등록: postman 사용", description = "캐릭터등록", security = {
		@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	@Transactional
	public SingleResult<Avatar> createAvatar(MultipartFile originfile, MultipartFile createdfile, String avatarName,
		String avatarDescription, String avatarStyle, Long avatarStyleId, String sadEmoticon, String happyEmoticon,
		String winkEmoticon, String angryEmoticon) throws IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();

		User user = userService.getUser(name);

		LocalDateTime now = LocalDateTime.now();
		avatarName = avatarName.replace("\"", "");

		String OriginFile = fileService.saveFile(originfile,
			avatarName + "_" + user.getUserId() + AVATAR_USER_IMAGE_FILES_POSTFIX, AVATAR_FOLDER_DIRECTORY);
		String CreatedFile = fileService.saveFile(createdfile,
			avatarName + "_" + user.getUserId() + AVATAR_CREATED_IMAGE_FILES_POSTFIX, AVATAR_FOLDER_DIRECTORY);
		Avatar avatar = new Avatar(user, avatarName, avatarDescription, AvatarType.valueOf(avatarStyle), avatarStyleId,
			OriginFile, CreatedFile, now);

		Avatar result = avatarService.make(avatar, user, sadEmoticon, happyEmoticon, winkEmoticon, angryEmoticon);

		return responseService.getSingleResult(result);
	}

	// @Operation(summary = "캐릭터 수정: postman 사용", description = "캐릭터수정", security = {
	// 	@SecurityRequirement(name = "bearer-key")})
	// @PutMapping("/{avatarId}")
	// @Transactional
	// public SingleResult<Avatar> updateAvatar(@PathVariable Long avatarId, MultipartFile originfile,
	// 	MultipartFile createdfile, String avatarName, String avatarDescription, AvatarType avatarStyle,
	// 	Long avatarStyleId) {
	//
	// 	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	// 	String name = authentication.getName();
	// 	User user = userService.getUser(name);
	// 	LocalDateTime now = LocalDateTime.now();
	//
	// 	Avatar findAvtar = avatarService.findOne(avatarId);
	//
	// 	File OriginFile = fileService.updateFile(originfile,
	// 		findAvtar.getAvatarOriginUrl(),
	// 		avatarName + "_" + user.getUserId() + "_origin");
	// 	File CreatedFile = fileService.updateFile(createdfile,
	// 		findAvtar.getAvatarCreatedUrl(),
	// 		avatarName + "_" + user.getUserId() + "_created");
	//
	// 	Avatar result = avatarService.update(avatarId, avatarName, avatarDescription,
	// 		avatarStyle, avatarStyleId, OriginFile.getPath(), CreatedFile.getPath(), now);
	// 	return responseService.getSingleResult(result);
	// }

	@Operation(summary = "캐릭터 삭제", description = "캐릭터삭제", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/{avatarId}")
	public CommonResult deleteAvatar(@Parameter @PathVariable Long avatarId) {
		avatarService.delete(avatarId);
		return responseService.getSuccessResult();
	}

	@Operation(summary = "캐릭터 기본설정", description = "캐릭터기본설정")
	@PostMapping("/isBasic/{avatarId}")
	public CommonResult setBasic(@Parameter @PathVariable Long avatarId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		User user = userService.getUser(name);
		avatarService.setIsBasic(avatarId, user);

		return responseService.getSuccessResult();
	}

	@Operation(summary = "캐릭터이름 중복조회", description = "캐릭터이름 중복조회")
	@GetMapping("/checkName/{avatarName}")
	public CommonResult getAvatarByName(@PathVariable String avatarName) {
		avatarService.validateNameDuplicateException(avatarName);
		return responseService.getSuccessResult();
	}
}
