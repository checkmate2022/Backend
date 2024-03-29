package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;

public interface BoardRepository extends JpaRepository<Board, Long> {

	List<Board> findAllByChannel(Channel channel);

	List<Board> findAllByTeamId(Long teamId);

}
