package com.checkmate.backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.model.dto.ScheduleGetDto;
import com.checkmate.backend.service.ChannelService;
import com.checkmate.backend.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Channel", description = "채널 API")
@RequestMapping(value = "/api/v1/channel")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ChannelController {

	private final ResponseService responseService;
	private final ChannelService channelService;

	@Operation(summary = "팀별 채널 조회")
	@GetMapping("/team/{teamId}")
	public ListResult<Channel> getSchedulesByTeam(@Parameter @PathVariable Long teamId) {
		return responseService.getListResult(channelService.findAllByTeam(teamId));
	}

	@Operation(summary = "채널 등록", description = "채널 등록")
	@PostMapping
	public SingleResult<Channel> create(@Parameter @RequestParam long teamId,
		@Parameter @RequestParam String channelName) {
		return responseService.getSingleResult(channelService.create(teamId, channelName));
	}

	@Operation(summary = "채널 삭제", description = "채널삭제")
	@DeleteMapping("/{channelId}")
	public CommonResult deleteChannel(@Parameter @PathVariable Long channelId) {
		channelService.delete(channelId);
		return responseService.getSuccessResult();
	}

	@Operation(summary = "채널 수정", description = "채널수정")
	@PutMapping("/{channelId}")
	public SingleResult<Channel> updateChannel(@Parameter @PathVariable Long channelId,
		@Parameter @RequestParam String channelName) {
		return responseService.getSingleResult(channelService.modify(channelId, channelName));
	}
}
