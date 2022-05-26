package com.checkmate.backend.oauth.api.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
import com.checkmate.backend.oauth.api.repo.AvatarRepository;
import com.checkmate.backend.oauth.service.AvatarService;
import com.checkmate.backend.oauth.service.FileService;
import com.checkmate.backend.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "AvatarController")
@RequestMapping(value = "/api/avatar")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AvatarController {

	private final AvatarService avatarService;
	private final AvatarRepository avatarRepository;
	private final ResponseService responseService;
	private final FileService fileService;

	@Operation(description = "전체캐릭터조회")
	@GetMapping
	public ListResult<Avatar> getAvatars() {
		return responseService.getListResult(avatarService.findAvatars());
	}

	@Operation(description = "단건캐릭터조회")
	@GetMapping("/{avatarId}")
	public SingleResult<Optional<Avatar>> getAvatar(@Parameter(description = "캐릭터id", required = true, example = "3")@PathVariable Long avatarId) {
		return responseService.getSingleResult(avatarService.findOne(avatarId));
	}

//postman 으로 테스트 해야함(localdate안됨), 아바타 이름 중복확인
	@Operation(description = "캐릭터등록")
	@PostMapping
	public CommonResult createAvatar(MultipartFile originfile,MultipartFile createdfile,
		String user_id,String avatar_name,String avatar_description,String avatar_date) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(avatar_date,formatter);

		File OriginFile = fileService.saveOriginFile(originfile,avatar_name+"_"+user_id);
		File CreatedFile = fileService.saveCreatedFile(createdfile,avatar_name+"_"+user_id);
		Avatar avatar = new Avatar(user_id, avatar_name, avatar_description,
			OriginFile.getPath(), CreatedFile.getPath(),dateTime);
		avatarService.make(avatar);
		return responseService.getSuccessResult();
	}


	@Operation(description = "캐릭터수정")
	@PutMapping("/{avatarId}")
	public SingleResult<Avatar> updateAvatar(@PathVariable Long avatarId,MultipartFile originfile,MultipartFile createdfile,
		String user_id,String avatar_name,String avatar_description,String avatar_date) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(avatar_date,formatter);

		Optional<Avatar> findAvtar = avatarService.findOne(avatarId);

		File OriginFile=fileService.updateOriginFile(originfile,findAvtar.get().getAvatar_name()+"_"+user_id,avatar_name+"_"+user_id);
		File CreatedFile =fileService.updateCreatedFile(createdfile,findAvtar.get().getAvatar_name()+"_"+user_id,avatar_name+"_"+user_id);
		Avatar result= avatarService.update(avatarId,user_id,avatar_name, avatar_description,
			OriginFile.getPath(), CreatedFile.getPath(),dateTime);
		return responseService.getSingleResult(result);
	}

	@Operation(description = "캐릭터삭제")
	@DeleteMapping("/{avatarId}")
	public CommonResult deleteAvatar(@Parameter @PathVariable Long avatarId) {
		avatarService.delete(avatarId);
		return responseService.getSuccessResult();
	}

	@Operation(description = "캐릭터기본설정")
	@PostMapping("/isBasic")
	public CommonResult setBasic(@Parameter @PathVariable Long avatarId,String user_id){
		avatarService.setIsBasic(avatarId,user_id);
		return responseService.getSuccessResult();
	}
/*
	@Operation(description = "캐릭터이름 중복조회")
	@GetMapping("/{avatar_name}")
	public CommonResult getAvatarByName(@PathVariable String avatar_name) {
		avatarService.validatenameDuplicateException(avatar_name);
		return responseService.getSuccessResult();
	}*/
}
