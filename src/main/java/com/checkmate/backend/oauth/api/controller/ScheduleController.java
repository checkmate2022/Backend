package com.checkmate.backend.oauth.api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.User;
import com.checkmate.backend.oauth.api.repo.ScheduleRepository;
import com.checkmate.backend.oauth.model.ScheduleDto;
import com.checkmate.backend.oauth.service.ScheduleService;
import com.checkmate.backend.oauth.service.UserService;
import com.checkmate.backend.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Schdule")
@RequestMapping(value = "/api/v1/schedule")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleController {

	private final ScheduleService scheduleService;
	private final ScheduleRepository scheduleRepository;
	private final ResponseService responseService;
	private final UserService userService;

	@Operation(description = "전체일정조회")
	@GetMapping
	public ListResult<Schedule> getSchedules() {
		return responseService.getListResult(scheduleService.findSchedules());
	}

	@Operation(description = "단건일정조회")
	@GetMapping("/{scheduleId}")
	public SingleResult<Optional<Schedule>> getSchedule(
		@Parameter(description = "일정id", required = true, example = "3") @PathVariable Long scheduleId) {
		return responseService.getSingleResult(scheduleService.findOne(scheduleId));
	}

	@Operation(description = "일정등록", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	public SingleResult<Schedule> createSchedule(@RequestBody @Parameter ScheduleDto scheduledto) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		Schedule schedule = new Schedule(scheduledto);
		//Schedule result = scheduleService.make(schedule, user,participants);
		Schedule result = scheduleService.make(schedule, user);
		return responseService.getSingleResult(result);
	}

	@Operation(description = "일정수정")
	@PutMapping("/{scheduleId}")
	public SingleResult<Schedule> updateSchdule(@Parameter @PathVariable Long scheduleId,
		@Parameter @RequestBody ScheduleDto scheduleDto) {
		return responseService.getSingleResult(scheduleService.update(scheduleId, scheduleDto));
	}

	@Operation(description = "일정삭제")
	@DeleteMapping("/{scheduleId}")
	public CommonResult deleteSchdule(@Parameter @PathVariable Long scheduleId) {
		scheduleService.delete(scheduleId);
		return responseService.getSuccessResult();
	}
}
