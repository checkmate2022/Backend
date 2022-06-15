package com.checkmate.backend.oauth.api.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.oauth.api.entity.Avatar;
import com.checkmate.backend.oauth.api.entity.User;
import com.checkmate.backend.oauth.service.AvatarService;
import com.checkmate.backend.oauth.service.FileService;
import com.checkmate.backend.oauth.service.UserService;
import com.checkmate.backend.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Avatar")
@RequestMapping(value = "/api/v1/avatar")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AvatarController {

	private final AvatarService avatarService;
	private final ResponseService responseService;
	private final FileService fileService;
	private final UserService userService;

	@Operation(summary = "전체 캐릭터 조회",description = "전체캐릭터조회")
	@GetMapping
	public ListResult<Avatar> getAvatars() {
		return responseService.getListResult(avatarService.findAvatars());
	}

	@Operation(summary = "단건 캐릭터 조회",description = "단건캐릭터조회")
	@GetMapping("/{avatarId}")
	public SingleResult<Optional<Avatar>> getAvatar(
		@Parameter(description = "캐릭터id") @PathVariable Long avatarId) {
		return responseService.getSingleResult(avatarService.findOne(avatarId));
	}

	@Operation(summary = "사용자별 캐릭터 조회",description = "사용자별 캐릭터 조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/user")
	public ListResult<Avatar> getAvatarByUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		return responseService.getListResult(avatarService.findAvatarByUser(user));
	}

	//postman 으로 테스트 해야함
	@Operation(summary = "캐릭터 등록: postman 사용",description = "캐릭터등록", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	public SingleResult<Avatar> createAvatar(MultipartFile originfile, MultipartFile createdfile, String avatarName,
		String avatarDescription, String avatarStyle, Long avatarStyleId) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());
		LocalDateTime now = LocalDateTime.now();

		File OriginFile = fileService.saveOriginFile(originfile, avatarName + "_" + user.getUserId());
		File CreatedFile = fileService.saveCreatedFile(createdfile, avatarName + "_" + user.getUserId());
		Avatar avatar = new Avatar(user, avatarName, avatarDescription, avatarStyle, avatarStyleId,
			OriginFile.getPath(), CreatedFile.getPath(), now);

		Avatar result = avatarService.make(avatar, user);
		return responseService.getSingleResult(result);
	}

	@Operation(summary = "캐릭터 수정: postman 사용",description = "캐릭터수정", security = {@SecurityRequirement(name = "bearer-key")})
	@PutMapping("/{avatarId}")
	public SingleResult<Avatar> updateAvatar(@PathVariable Long avatarId, MultipartFile originfile,
		MultipartFile createdfile, String avatarName, String avatarDescription, String avatarStyle,
		Long avatarStyleId) {

		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());
		LocalDateTime now = LocalDateTime.now();

		Optional<Avatar> findAvtar = avatarService.findOne(avatarId);

		File OriginFile = fileService.updateOriginFile(originfile,
			findAvtar.get().getAvatarName() + "_" + user.getUserId(),
			avatarName + "_" + user.getUserId());
		File CreatedFile = fileService.updateCreatedFile(createdfile,
			findAvtar.get().getAvatarName() + "_" + user.getUserId(),
			avatarName + "_" + user.getUserId());

		Avatar result = avatarService.update(avatarId, avatarName, avatarDescription,
			avatarStyle, avatarStyleId, OriginFile.getPath(), CreatedFile.getPath(), now);
		return responseService.getSingleResult(result);
	}

	@Operation(summary = "캐릭터 삭제",description = "캐릭터삭제", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/{avatarId}")
	public CommonResult deleteAvatar(@Parameter @PathVariable Long avatarId) {
		avatarService.delete(avatarId);
		return responseService.getSuccessResult();
	}

	@Operation(summary = "캐릭터 기본설정",description = "캐릭터기본설정")
	@PostMapping("/isBasic/{avatarId}")
	public CommonResult setBasic(@Parameter @PathVariable Long avatarId) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());
		avatarService.setIsBasic(avatarId, user);

		return responseService.getSuccessResult();
	}

	@Operation(summary = "캐릭터이름 중복조회",description = "캐릭터이름 중복조회")
	@GetMapping("/checkName/{avatarName}")
	public CommonResult getAvatarByName(@PathVariable String avatarName) {
		avatarService.validatenameDuplicateException(avatarName);
		return responseService.getSuccessResult();
	}
}
