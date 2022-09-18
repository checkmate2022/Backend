package com.checkmate.backend.entity.comment;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COMMENT")
public class Comment {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "COMMENT_SEQ")
	private Long commentSeq;

	@Column(name = "CONTENT")
	private String content;

	@Column(name = "EMOTICON")
	private String emoticonUrl;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOARD_SEQ")
	private Board board;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	@Column(name = "CREATED_AT")
	@NotNull
	private LocalDateTime createdAt;

	@Column(name = "MODIFIED_AT")
	@NotNull
	private LocalDateTime modifiedAt;

	public Comment(String content, Board board, User user, String emoticonUrl) {
		this.content = content;
		this.board = board;
		this.user = user;
		this.emoticonUrl = emoticonUrl;
		this.createdAt = LocalDateTime.now();
		this.modifiedAt = LocalDateTime.now();
	}

	public void update(String content) {
		this.content = content;
		this.modifiedAt = LocalDateTime.now();
	}

}
