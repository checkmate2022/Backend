package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByBoard(Board board);

}
