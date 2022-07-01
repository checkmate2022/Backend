package com.checkmate.backend.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponse {

	private Long baordSeq;

	private String title;

	private String content;

	private String username;

	private String userImage;

	private LocalDateTime createDate;

}
