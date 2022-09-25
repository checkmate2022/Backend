package com.checkmate.backend.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.entity.schedule.Notification;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.FcmDto;
import com.checkmate.backend.service.FCMService;
import com.checkmate.backend.service.ResponseService;
import com.checkmate.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "FCM", description = "알림 API")
@RequestMapping(value = "/api/v1/fcm")
@Slf4j
@RestController
@RequiredArgsConstructor
public class FCMController {

	private final FCMService fcmService;
	private final UserService userService;
	private final ResponseService responseService;

	@Operation(summary = "디바이스 토큰 등록", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping("/register")
	public CommonResult postDeviceTokenInfo(@RequestParam String deviceToken) throws Throwable {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		fcmService.registerDeviceToken(deviceToken, user.getUserId());

		return responseService.getSuccessResult();
	}

	@Operation(summary = "알림보내기(token)")
	@PostMapping
	public CommonResult pushMessage(@RequestBody FcmDto requestDTO) throws IOException {
		System.out.println(requestDTO.getUserId() + " "
			+ requestDTO.getTitle() + " " + requestDTO.getBody());

		fcmService.sendMessageTo(
			requestDTO.getUserId(),
			requestDTO.getTitle(),
			requestDTO.getBody());

		return responseService.getSuccessResult();
	}

	//팀별 조회
	@Operation(summary = "회원 알림 조회", description = "회원 알림 가져오기(알림 IsNotice True)", security = {
		@SecurityRequirement(name = "bearer-key")})
	@GetMapping("")
	public ListResult<Notification> getNotifications() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		return responseService.getListResult(fcmService.findNotificationByUser(name));
	}
}
