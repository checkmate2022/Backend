package com.checkmate.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.BoardDto;
import com.checkmate.backend.model.response.BoardResponse;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

	private final BoardRepository boardRepository;
	private final ChannelRepository channelRepository;
	private final TeamRepository teamRepository;

	//ㄱㅔ시판 생성 (채널마다)
	public Board create(long channelId, BoardDto boardDto, User user) {
		LocalDateTime now = LocalDateTime.now();
		System.out.println("this");
		Channel channel = channelRepository.findById(channelId).orElseThrow();
		System.out.println("this");
		Board board = new Board(boardDto.getTitle(), boardDto.getContent(), user, channel, channel.getTeam(), now, now);
		return boardRepository.save(board);
	}

	//게시판 수정
	public BoardResponse modify(long boardSeq, BoardDto boardDto) {
		Board board = boardRepository.findById(boardSeq).orElseThrow();
		board.update(boardDto.getTitle(), boardDto.getContent());
		BoardResponse boardResponse = BoardResponse.builder()
			.baordSeq(boardSeq)
			.content(board.getContent())
			.title(board.getTitle())
			.username(board.getUser().getUsername())
			.userImage(board.getUser().getUserImage())
			.createDate(board.getCreatedAt())
			.build();
		return boardResponse;
	}

	//게시판 삭제
	public void delete(long boardSeq) {
		boardRepository.deleteById(boardSeq);
	}

	//게시판 채널 별 조회
	public List<BoardResponse> getBoards(long channelId) {
		Channel channel = channelRepository.findById(channelId).orElseThrow();
		List<Board> boards = boardRepository.findAllByChannel(channel);
		List<BoardResponse> collect =
			boards.stream().map(p -> BoardResponse.builder()
				.baordSeq(p.getBoardSeq())
				.content(p.getContent())
				.title(p.getTitle())
				.username(p.getUser().getUsername())
				.userImage(p.getUser().getUserImage())
				.createDate(p.getCreatedAt())
				.build()).collect(Collectors.toList());
		return collect;
	}

	//게시판 팀 별 조회
	public List<BoardResponse> getBoardsByTeam(long teamId) {
		Team team = teamRepository.findById(teamId).orElseThrow();
		List<Board> boards = boardRepository.findAllByTeam(team);
		List<BoardResponse> collect =
			boards.stream().map(p -> BoardResponse.builder()
				.baordSeq(p.getBoardSeq())
				.content(p.getContent())
				.title(p.getTitle())
				.username(p.getUser().getUsername())
				.userImage(p.getUser().getUserImage())
				.createDate(p.getCreatedAt())
				.build()).collect(Collectors.toList());
		return collect;
	}
}
