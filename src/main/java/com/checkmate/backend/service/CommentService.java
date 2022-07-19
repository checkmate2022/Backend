package com.checkmate.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.comment.Comment;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.response.CommentResponse;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	private final BoardRepository boardRepository;

	//게시글 별 댓글 조회
	public List<CommentResponse> findAllByBoard(long boardSeq) {
		Board board = boardRepository.findById(boardSeq).orElseThrow(
			() -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
		);

		List<Comment> commentList = commentRepository.findAllByBoard(board);

		List<CommentResponse> collect =
			commentList.stream().map(comment -> CommentResponse.builder()
				.commentSeq(comment.getCommentSeq())
				.content(comment.getContent())
				.modifiedDate(comment.getModifiedAt())
				.userImage(comment.getUser().getUserImage())
				.username(comment.getUser().getUsername())
				.build()).collect(Collectors.toList());

		return collect;
	}

	//댓글 생성
	public CommentResponse create(String content, long boardSeq, User user) {
		Board board = boardRepository.findById(boardSeq).orElseThrow();
		Comment comment = new Comment(content, board, user);

		commentRepository.save(comment);

		CommentResponse commentResponse = CommentResponse.builder()
			.commentSeq(comment.getCommentSeq())
			.content(comment.getContent())
			.modifiedDate(comment.getModifiedAt())
			.userImage(comment.getUser().getUserImage())
			.username(comment.getUser().getUsername())
			.build();

		return commentResponse;
	}

	//댓글 수정
	public CommentResponse update(String content, long commentSeq) {
		Comment comment = commentRepository.findById(commentSeq).orElseThrow();

		comment.update(content);

		CommentResponse commentResponse = CommentResponse.builder()
			.commentSeq(comment.getCommentSeq())
			.content(comment.getContent())
			.modifiedDate(comment.getModifiedAt())
			.userImage(comment.getUser().getUserImage())
			.username(comment.getUser().getUsername())
			.build();

		return commentResponse;
	}

	//댓글 삭제
	public void delete(long commentSeq) {
		Comment comment = commentRepository.findById(commentSeq).orElseThrow();

		comment.delete();
	}

}