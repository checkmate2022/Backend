package com.checkmate.backend.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.response.CommentResponse;
import com.checkmate.backend.service.CommentService;
import com.checkmate.backend.service.ResponseService;
import com.checkmate.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "comment", description = "댓글 API")
@RequestMapping(value = "/api/v1/comment")
@RequiredArgsConstructor
@RestController
public class CommentController {

	private final CommentService commentService;
	private final UserService userService;
	private final ResponseService responseService;

	@Operation(summary = "게시글 별 댓글 조회")
	@GetMapping("/{boardId}")
	public ListResult<CommentResponse> comments(@PathVariable long boardId) {
		return responseService.getListResult(commentService.findAllByBoard(boardId));
	}

	@Operation(summary = "댓글 생성", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping("")
	public SingleResult<CommentResponse> create(long boardId, String content, String emoticon) throws IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();

		User user = userService.getUser(name);

		return responseService.getSingleResult(commentService.create(content, boardId, emoticon, user));
	}

	@Operation(summary = "댓글 수정", security = {@SecurityRequirement(name = "bearer-key")})
	@PutMapping("")
	public SingleResult<CommentResponse> update(long commentSeq, String content, String emoticon) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();

		User user = userService.getUser(name);

		return responseService.getSingleResult(commentService.update(content, commentSeq, emoticon, user));
	}

	@Operation(summary = "댓글 삭제", security = {
		@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("")
	public CommonResult delete(long commentSeq) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();

		User user = userService.getUser(name);
		commentService.delete(commentSeq, user);
		return responseService.getSuccessResult();
	}

}
