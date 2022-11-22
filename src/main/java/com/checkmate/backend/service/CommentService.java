package com.checkmate.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.advice.exception.ResourceNotExistException;
import com.checkmate.backend.advice.exception.UserNotFoundException;
import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.comment.Comment;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.response.CommentResponse;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.CommentRepository;
import com.checkmate.backend.repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final FCMService fcmService;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	//게시글 별 댓글 조회
	public List<CommentResponse> findAllByBoard(long boardSeq) {
		Board board = boardRepository.findById(boardSeq).orElseThrow(
			() -> new ResourceNotExistException("게시글이 존재하지 않습니다.")
		);

		List<Comment> commentList = commentRepository.findAllByBoard(board);
		List<CommentResponse> collect = new ArrayList<>();
		for (Comment comment : commentList) {
			User user = userRepository.findById(comment.getUserId()).orElseThrow();
			CommentResponse commentResponse = CommentResponse.builder()
				.commentSeq(comment.getCommentSeq())
				.content(comment.getContent())
				.modifiedDate(comment.getModifiedAt())
				.userImage(user.getUserImage())
				.username(user.getUsername())
				.emoticon(comment.getEmoticonUrl())
				.build();
			collect.add(commentResponse);
		}

		return collect;
	}

	//댓글 생성
	public CommentResponse create(String content, long boardSeq, String emoticon, User user) throws IOException {
		Board board = boardRepository.findById(boardSeq).orElseThrow(
			() -> new ResourceNotExistException("게시글이 존재하지 않습니다.")
		);
		User boardUser = userRepository.findById(board.getUserId()).orElseThrow();
		Comment comment = new Comment(content, board, user.getUserSeq(), emoticon);

		commentRepository.save(comment);

		CommentResponse commentResponse = CommentResponse.builder()
			.commentSeq(comment.getCommentSeq())
			.content(comment.getContent())
			.modifiedDate(comment.getModifiedAt())
			.userImage(user.getUserImage())
			.username(user.getUsername())
			.emoticon(emoticon)
			.build();
		try {
			fcmService.sendMessageTo(
				boardUser.getUserId(),
				user.getUserId() + "이 댓글을 달았습니다.",
				comment.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return commentResponse;
	}

	//댓글 수정
	public CommentResponse update(String content, long commentSeq, String emoticon, User user) {
		Comment comment = commentRepository.findById(commentSeq).orElseThrow(
			() -> new ResourceNotExistException("댓글이 존재하지 않습니다.")
		);

		if (!comment.getUserId().equals(user.getUserSeq())) {
			throw new UserNotFoundException("댓글 작성자가 아닙니다.");
		}

		comment.update(content, emoticon);

		CommentResponse commentResponse = CommentResponse.builder()
			.commentSeq(comment.getCommentSeq())
			.content(comment.getContent())
			.emoticon(emoticon)
			.modifiedDate(comment.getModifiedAt())
			.userImage(user.getUserImage())
			.username(user.getUsername())
			.build();

		return commentResponse;
	}

	//댓글 삭제
	public void delete(long commentSeq, User user) {
		Comment comment = commentRepository.findById(commentSeq).orElseThrow(
			() -> new ResourceNotExistException("댓글이 존재하지 않습니다.")
		);

		if (!comment.getUserId().equals(user.getUserSeq())) {
			throw new UserNotFoundException("댓글 작성자가 아닙니다.");
		}

		commentRepository.delete(comment);
	}

}
