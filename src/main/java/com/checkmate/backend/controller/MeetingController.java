package com.checkmate.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.model.response.MeetingResponse;
import com.checkmate.backend.service.MeetingService;
import com.checkmate.backend.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Meeting", description = "회의 API")
@RequestMapping(value = "/api/v1/meeting")
@Slf4j
@RequiredArgsConstructor
@RestController
public class MeetingController {

	private final MeetingService meetingService;
	private final ResponseService responseService;

	//단건 조회
	@Operation(summary = "단건 회의 조회")
	@GetMapping("/{meetingId}")
	public SingleResult<MeetingResponse> getMeeting(
		@Parameter(description = "회의id", required = true) @PathVariable String meetingId) {
		return responseService.getSingleResult(meetingService.findById(meetingId));
	}

	//팀별 조회
	@Operation(summary = "팀별 회의 조회", description = "팀별 회의 가져오기")
	@GetMapping("/team/{teamId}")
	public ListResult<MeetingResponse> getSchedulesByTeam(@Parameter @PathVariable Long teamId) {
		return responseService.getListResult(meetingService.findMeetingsByTeam(teamId));
	}

}
