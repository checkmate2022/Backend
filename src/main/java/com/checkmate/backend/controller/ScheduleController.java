package com.checkmate.backend.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.ScheduleRepository;
import com.checkmate.backend.model.dto.ScheduleGetDto;
import com.checkmate.backend.model.request.ScheduleRequest;
import com.checkmate.backend.service.ScheduleService;
import com.checkmate.backend.service.UserService;
import com.checkmate.backend.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Schdule", description = "일정 API")
@RequestMapping(value = "/api/v1/schedule")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleController {

	private final ScheduleService scheduleService;
	private final ScheduleRepository scheduleRepository;
	private final ResponseService responseService;
	private final UserService userService;

	/*
	@Operation(summary = "전체 일정 조회",description = "전체일정조회")
	@GetMapping
	public ListResult<Schedule> getSchedules() {
		return responseService.getListResult(scheduleService.findSchedules());
	}*/

	@Operation(summary = "팀별 일정 조회",description = "팀별 일정 가져오기")
	@GetMapping("/team/{teamId}")
	public ListResult<ScheduleGetDto> getSchedulesByTeam(@Parameter @PathVariable Long teamId) {
		return responseService.getListResult(scheduleService.findScheduleByTeam(teamId));
	}

	@Operation(summary = "단건 일정 조회",description = "단건일정조회")
	@GetMapping("/{scheduleId}")
	public SingleResult<ScheduleGetDto> getSchedule(
		@Parameter(description = "일정id", required = true) @PathVariable Long scheduleId) {
		return responseService.getSingleResult(scheduleService.findOne(scheduleId));
	}

	/*
	@Operation(description = "사용자별 일정 가져오기", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/user")
	public ListResult<ScheduleResponse> getSchedulesByUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		return responseService.getListResult(scheduleService.findScheduleByUser(user));
	}*/


	@Operation(summary = "일정 등록",description = "일정등록", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	public SingleResult<Schedule> createSchedule(@RequestBody @Parameter ScheduleRequest scheduledto) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		//Schedule result = scheduleService.make(schedule, user,participants);
		Schedule result = scheduleService.make(scheduledto, user);
		return responseService.getSingleResult(result);
	}

	@Operation(summary = "일정 수정",description = "일정수정")
	@PutMapping("/{scheduleId}")
	public SingleResult<Schedule> updateSchdule(@Parameter @PathVariable Long scheduleId,
		@Parameter @RequestBody ScheduleRequest scheduleRequest) {
		return responseService.getSingleResult(scheduleService.update(scheduleId, scheduleRequest));
	}

	@Operation(summary = "일정 삭제",description = "일정삭제")
	@DeleteMapping("/{scheduleId}")
	public CommonResult deleteSchdule(@Parameter @PathVariable Long scheduleId) {
		scheduleService.delete(scheduleId);
		return responseService.getSuccessResult();
	}
}
