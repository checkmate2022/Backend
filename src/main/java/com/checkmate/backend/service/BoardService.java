package com.checkmate.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.advice.exception.UserNotFoundException;
import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.comment.Comment;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.BoardDto;
import com.checkmate.backend.model.response.BoardResponse;
import com.checkmate.backend.model.response.CommentResponse;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

	private final BoardRepository boardRepository;
	private final ChannelRepository channelRepository;
	private final TeamRepository teamRepository;
	private final UserRepository userRepository;

	//ㄱㅔ시판 생성 (채널마다)
	public Board create(long channelId, BoardDto boardDto, User user) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(
			() -> new IllegalArgumentException("채널이 존재하지 않습니다.")
		);
		Board board = new Board(boardDto.getTitle(), boardDto.getContent(), channel, channel.getTeam().getTeamSeq(),
			user.getUserSeq());
		return boardRepository.save(board);
	}

	//게시판 수정
	public BoardResponse modify(long boardSeq, BoardDto boardDto, User user) {
		Board board = boardRepository.findById(boardSeq).orElseThrow(
			() -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
		);

		if (board.getUserId() != user.getUserSeq()) {
			throw new UserNotFoundException("작성자가 일치하지 않습니다");
		}
		board.update(boardDto.getTitle(), boardDto.getContent());

		List<Comment> commentList = board.getComments();

		List<CommentResponse> collect = getCommentResponses(commentList);

		BoardResponse boardResponse = BoardResponse.builder()
			.boardSeq(boardSeq)
			.content(board.getContent())
			.title(board.getTitle())
			.username(user.getUsername())
			.userImage(user.getUserImage())
			.createDate(board.getCreatedAt())
			.comments(collect)
			.build();
		return boardResponse;
	}

	//게시판 삭제
	public void delete(long boardSeq, User user) {
		Board board = boardRepository.findById(boardSeq).orElseThrow(
			() -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
		);

		if (board.getUserId() != user.getUserSeq()) {
			throw new UserNotFoundException("작성자가 일치하지 않습니다");
		}
		boardRepository.delete(board);
	}

	//게시판 채널 별 조회
	public List<BoardResponse> getBoards(long channelId) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(
			() -> new IllegalArgumentException("채널이 존재하지 않습니다.")
		);
		List<Board> boards = boardRepository.findAllByChannel(channel);
		List<BoardResponse> collect = getBoardResponses(boards);
		return collect;
	}

	@NotNull
	private List<BoardResponse> getBoardResponses(List<Board> boards) {
		List<BoardResponse> collect = new ArrayList<>();
		for (Board p : boards) {
			User user = userRepository.findById(p.getUserId()).orElseThrow();
			BoardResponse boardResponse = BoardResponse.builder()
				.boardSeq(p.getBoardSeq())
				.content(p.getContent())
				.title(p.getTitle())
				.username(user.getUsername())
				.userImage(user.getUserImage())
				.createDate(p.getCreatedAt())
				.build();
			collect.add(boardResponse);
		}
		return collect;
	}

	//게시판 팀 별 조회
	public List<BoardResponse> getBoardsByTeam(long teamId) {
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("팀이 존재하지 않습니다.")
		);
		List<Board> boards = boardRepository.findAllByTeamId(teamId);
		List<BoardResponse> collect = getBoardResponses(boards);
		return collect;
	}

	//단건 조회
	public BoardResponse findById(long boardId) {
		Board board = boardRepository.findById(boardId).orElseThrow(
			() -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
		);
		User user = userRepository.findById(board.getUserId()).orElseThrow(
			() -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
		);
		BoardResponse boardResponse = BoardResponse.builder()
			.boardSeq(board.getBoardSeq())
			.content(board.getContent())
			.title(board.getTitle())
			.username(user.getUsername())
			.userImage(user.getUserImage())
			.createDate(board.getCreatedAt())
			.build();

		List<Comment> commentList = board.getComments();

		List<CommentResponse> collect = getCommentResponses(commentList);

		boardResponse.setComments(collect);

		return boardResponse;
	}

	@NotNull
	private List<CommentResponse> getCommentResponses(List<Comment> commentList) {
		List<CommentResponse> collect = new ArrayList<>();
		for (Comment comment : commentList) {
			User commentUser = userRepository.findById(comment.getUserId()).orElseThrow();
			CommentResponse commentResponse = CommentResponse.builder()
				.commentSeq(comment.getCommentSeq())
				.content(comment.getContent())
				.modifiedDate(comment.getModifiedAt())
				.userImage(commentUser.getUserImage())
				.username(commentUser.getUsername())
				.emoticon(comment.getEmoticonUrl())
				.build();
			collect.add(commentResponse);
		}
		return collect;
	}
}


