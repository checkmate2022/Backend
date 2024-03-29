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
public class CommentResponse {

	private Long commentSeq;

	private String content;

	private String username;

	private String emoticon;

	private String userImage;

	private LocalDateTime modifiedDate;

}
