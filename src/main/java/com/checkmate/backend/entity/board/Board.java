package com.checkmate.backend.entity.board;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BOARD")
public class Board {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "AVATAR_ID")
	private Long baordSeq;
}
