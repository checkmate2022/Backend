package com.checkmate.backend.controller.oauth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.UserDto;
import com.checkmate.backend.service.ResponseService;
import com.checkmate.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @package : com.checkmate.backend.oauth.api.controller
 * @name: UserController.java
 * @date : 2022/05/23 2:15 오전
 * @author : jifrozen
 * @version : 1.0.0
 * @description : 사용자 관리 Controller
 * @modified :
 **/
@Tag(name = "user", description = "유저관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;
	private final ResponseService responseService;

	@Operation(summary = "유저 정보 조회", description = "로그인한 유저의 정보를 조회합니다", security = {
		@SecurityRequirement(name = "bearer-key")})
	@GetMapping
	public SingleResult<User> getUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		return responseService.getSingleResult(user);
	}

	@Operation(summary = "회원가입", description = "회원가입 요청")
	@PostMapping("/join")
	public SingleResult<User> join(@RequestBody @Parameter(description = "회원가입 정보", required = true) UserDto user) {
		log.info("회원가입 - {}", user);
		User signedUser = userService.signUpUser(user);
		return responseService.getSingleResult(signedUser);
	}

	@Operation(summary = "name 중복 확인", description = "닉네임 증복 확인")
	@PostMapping("/check/name")
	public SingleResult<Integer> checkName(@Parameter @RequestParam String username) {
		return responseService.getSingleResult(userService.checkUsername(username));
	}

	@Operation(summary = "ID 중복 확인", description = "닉네임 증복 확인")
	@PostMapping("/check/userId")
	public SingleResult<Integer> checkId(@Parameter @RequestParam String userId) {
		return responseService.getSingleResult(userService.checkId(userId));
	}
}
