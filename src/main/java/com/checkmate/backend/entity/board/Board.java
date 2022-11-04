package com.checkmate.backend.entity.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.comment.Comment;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BOARD")
public class Board {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "BOARD_SEQ")
	private Long boardSeq;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "CONTENT")
	private String content;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	@Column(name = "MODIFIED_AT")
	private LocalDateTime modifiedAt;

	@Column(name = "USER")
	private long userId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHANNEL_ID")
	private Channel channel;

	@Column(name = "TEAM")
	private Long teamId;

	// @JsonIgnore
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "TEAM_ID")
	// private Team team;

	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Comment> comments = new ArrayList<>();

	public Board(
		String title,
		String content,
		Channel channel,
		Long teamId,
		Long userId

	) {
		this.title = title;
		this.content = content;
		this.channel = channel;
		this.teamId = teamId;
		this.userId = userId;
		this.createdAt = LocalDateTime.now();
		this.modifiedAt = LocalDateTime.now();
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
		this.modifiedAt = LocalDateTime.now();
	}

}
