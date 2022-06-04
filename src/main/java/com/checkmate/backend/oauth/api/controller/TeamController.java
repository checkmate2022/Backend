package com.checkmate.backend.oauth.api.controller;

import java.util.Optional;

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
import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.Team;
import com.checkmate.backend.oauth.api.entity.User;
import com.checkmate.backend.oauth.api.repo.ScheduleRepository;
import com.checkmate.backend.oauth.model.ScheduleRequest;
import com.checkmate.backend.oauth.model.ScheduleResponse;
import com.checkmate.backend.oauth.model.TeamRequest;
import com.checkmate.backend.oauth.model.TeamResponse;
import com.checkmate.backend.oauth.service.ScheduleService;
import com.checkmate.backend.oauth.service.TeamService;
import com.checkmate.backend.oauth.service.UserService;
import com.checkmate.backend.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Team", description = "팀 API")
@RequestMapping(value = "/api/v1/team")
@Slf4j
@RequiredArgsConstructor
@RestController
public class TeamController {

	private final ScheduleService scheduleService;
	private final TeamService teamService;
	private final ScheduleRepository scheduleRepository;
	private final ResponseService responseService;
	private final UserService userService;

	@Operation(summary = "전체 팀 조회",description = "전체팀조회")
	@GetMapping
	public ListResult<Team> getTeams() {
		return responseService.getListResult(teamService.findTeams());
	}

	@Operation(summary = "단건 팀 조회",description = "단건팀조회")
	@GetMapping("/{teamId}")
	public SingleResult<Optional<Team>> getTeam(
		@Parameter(description = "팀id", required = true, example = "3") @PathVariable Long teamId) {
		return responseService.getSingleResult(teamService.findOne(teamId));
	}

	@Operation(summary = "사용자별 팀 조회",description = "사용자별 팀 가져오기", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/user")
	public ListResult<TeamResponse> getTeamByUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		return responseService.getListResult(teamService.findTeamByUser(user));
	}


	@Operation(summary = "팀 등록",description = "팀등록", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	public SingleResult<Team> createTeam(@RequestBody @Parameter TeamRequest teamdto) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		Team result = teamService.make(teamdto, user);
		return responseService.getSingleResult(result);
	}

	@Operation(summary = "팀 수정",description = "팀수정")
	@PutMapping("/{teamId}")
	public SingleResult<Team> updateTeam(@Parameter @PathVariable Long teamId,
		@Parameter @RequestBody TeamRequest teamRequest) {
		return responseService.getSingleResult(teamService.update(teamId, teamRequest));
	}

	@Operation(summary = "팀 삭제",description = "팀삭제")
	@DeleteMapping("/{teamId}")
	public CommonResult deleteTeam(@Parameter @PathVariable Long teamId) {
		teamService.delete(teamId);
		return responseService.getSuccessResult();
	}
}