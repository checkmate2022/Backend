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
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

	@Column(name = "USER")
	private Long userId;

	@Column(name = "CREATED_AT")
	@NotNull
	private LocalDateTime createdAt;

	@Column(name = "MODIFIED_AT")
	@NotNull
	private LocalDateTime modifiedAt;

	public Comment(String content, Board board, long userId, String emoticonUrl) {
		this.content = content;
		this.board = board;
		this.userId = userId;
		this.emoticonUrl = emoticonUrl;
		this.createdAt = LocalDateTime.now();
		this.modifiedAt = LocalDateTime.now();
	}

	public void update(String content, String emoticonUrl) {
		this.content = content;
		this.emoticonUrl = emoticonUrl;
		this.modifiedAt = LocalDateTime.now();
	}

}
