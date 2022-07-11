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
import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.BoardDto;
import com.checkmate.backend.model.response.BoardResponse;
import com.checkmate.backend.service.BoardService;
import com.checkmate.backend.service.ResponseService;
import com.checkmate.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Board")
@RequestMapping(value = "/api/v1/board")
@RequiredArgsConstructor
@RestController
public class BoardController {

	private final ResponseService responseService;
	private final BoardService boardService;
	private final UserService userService;

	@Operation(summary = "게시판 단건 조회")
	@GetMapping("/board/{boardId}")
	public SingleResult<BoardResponse> findById(
		@Parameter(description = "게시글 id") @PathVariable long boardId) {
		return responseService.getSingleResult(boardService.findById(boardId));
	}

	@Operation(summary = "채널별 게시판 조회", description = "채널별 게시판 전체 조회")
	@GetMapping("/channel/{channelId}")
	public ListResult<BoardResponse> getBoardsByChannel(
		@Parameter(description = "채널 id") @PathVariable long channelId) {
		return responseService.getListResult(boardService.getBoards(channelId));
	}

	@Operation(summary = "팀별 게시판 조회", description = "팀별 게시판 전체 조회")
	@GetMapping("")
	public ListResult<BoardResponse> getBoardsByTeam(
		@Parameter(description = "팀 id") long teamId) {
		return responseService.getListResult(boardService.getBoardsByTeam(teamId));
	}

	@Operation(summary = "게시판 생성", description = "게시판을 생성한다(title, content, Channel)", security = {
		@SecurityRequirement(name = "bearer-key")})
	@PostMapping("")
	public SingleResult<Board> create(@RequestBody BoardDto boardDto, long channelId) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		return responseService.getSingleResult(boardService.create(channelId, boardDto, user));
	}

	@Operation(summary = "게시판 수정", description = "게시판을 수정한다", security = {
		@SecurityRequirement(name = "bearer-key")})
	@PutMapping("/{boardId}")
	public SingleResult<BoardResponse> modify(@PathVariable long boardId, @RequestBody BoardDto boardDto) {
		return responseService.getSingleResult(boardService.modify(boardId, boardDto));
	}

	@Operation(summary = "게시판 삭제", description = "게시판을 삭제한다.", security = {
		@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/{boardId}")
	public CommonResult modify(@PathVariable long boardId) {
		boardService.delete(boardId);
		return responseService.getSuccessResult();
	}

}
